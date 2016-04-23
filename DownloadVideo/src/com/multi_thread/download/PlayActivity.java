package com.multi_thread.download;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.multi_thread.download.R;

public class PlayActivity extends Activity implements SurfaceHolder.Callback {

	public static final String VIDEO_URL = "video_url";
	private MediaPlayer mMediaPlayer;
	private SurfaceView mSurfaceView;

	private String videoUrl = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_play);

		Intent intent = getIntent();
		videoUrl = intent.getStringExtra(VIDEO_URL);
		if (videoUrl == null || "".equals(videoUrl.trim())) {
			this.finish();
			return;
		}
		mSurfaceView = (SurfaceView) findViewById(R.id.play_view);
		mSurfaceView.getHolder().addCallback(this);

	}

	private void play() {
		File file = new File(videoUrl);
		if (file == null) {
			return;
		}

		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
		mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
		mMediaPlayer.setDisplay(mSurfaceView.getHolder());
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mMediaPlayer.setDataSource(file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		mMediaPlayer.prepareAsync();
	}
	
	private OnPreparedListener mOnPreparedListener=new OnPreparedListener(){

		@Override
		public void onPrepared(MediaPlayer mp) {
			// TODO Auto-generated method stub
			mp.start();
		}
		
	};
	
	private OnCompletionListener mOnCompletionListener=new OnCompletionListener(){

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			PlayActivity.this.finish();
		}
		
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		play();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}
	
	private void scanFile(String url){
		MediaScannerConnection.scanFile(this, new String[] { url}, null, null);
	}

}
