package com.multi_thread.download;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {

	private String URL = "http://imps.tcl-ta.com/cailiang/media/lq/3gp/h264_aac_320_240_v50_10_a16_22_m.mp4";
	private Button btn_download;
	private EditText mEditText;
	private ProgressBar progressBar;
	private MyHandler myHandler;

	private final int BEGIN_DOWNLOAD = 1;
	private final int SUCCESS = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mEditText = (EditText) findViewById(R.id.video_url);
		btn_download = (Button) findViewById(R.id.button1);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setProgress(0);

		btn_download.setOnClickListener(this);
		myHandler = new MyHandler();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button1) {
			String url=mEditText.getText().toString();
			if(!url.trim().equals("")){
				URL=url;
			}
			MyThread thread = new MyThread();
			thread.start();
		}
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String mess = "";
			switch (msg.what) {
			case BEGIN_DOWNLOAD:
				mess = "begin download";
				break;
			case SUCCESS:
				mess = "download success";
				Intent intent = new Intent(MainActivity.this,PlayActivity.class);
				intent.putExtra(PlayActivity.VIDEO_URL, (String)msg.obj);
				MainActivity.this.startActivity(intent);
				break;
			}
			Toast.makeText(MainActivity.this, mess, Toast.LENGTH_SHORT).show();

		}

	}

	class MyThread extends Thread {
		@Override
		public void run() {
			myHandler.sendEmptyMessage(BEGIN_DOWNLOAD);
			progressBar.setProgress(0);
			try {
				URL url = new URL(URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				int filesize = connection.getContentLength();
				progressBar.setMax(filesize);
				String fileUrl = Environment.getExternalStorageDirectory()
						+ "/aaa";
				File fileDir = new File(fileUrl);
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				String fileName = URL.substring(URL.lastIndexOf("/") + 1);

				File file = new File(fileDir + fileName);
				file.createNewFile();

				int fenzise = filesize / 2;
				int sizemore = filesize % 2;

				MultiThread[] threads = new MultiThread[2];
				for (int i = 0; i < threads.length; i++) {
					MultiThread thread = new MultiThread(url, file,
							i * fenzise, fenzise * (i + 1) - 1);
					thread.start();
					threads[i] = thread;
				}
				int count = 0;
				boolean isfinished = true;
				while (isfinished) {
					// 先把整除的余数搞定
					count = sizemore;
					isfinished = false;
					for (int i = 0; i < threads.length; i++) {
						count += threads[i].getDownloadsize();
						if (!threads[i].isOk()) {
							isfinished = true;
						}
					}
					progressBar.setProgress(count);
					if (count == filesize) {
						myHandler.sendMessage(myHandler.obtainMessage(SUCCESS,
								file.getAbsolutePath()));
					}
					sleep(1000);
				}

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
