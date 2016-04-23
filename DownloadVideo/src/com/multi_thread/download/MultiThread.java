package com.multi_thread.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

public class MultiThread  extends Thread{
	
	private URL url;
	private File file;
	private int startPsittion;
	private int endPosition;
	private boolean isOk=false;
	
	private int downloadsize;
	
	public MultiThread(URL url,File file,int startPsittion,int endPosition) {
		this.url=url;
		this.file=file;
		this.startPsittion=startPsittion;
		this.endPosition=endPosition;
	}
	
	@Override
	public void run() {
		try {
			BufferedInputStream input=null;
			RandomAccessFile output=null;
			byte[] b=new byte[1024];
			URLConnection conn=url.openConnection();
			conn.setAllowUserInteraction(true);

			// 设置当前线程下载的起点，终点
			conn.setRequestProperty("Range","bytes="+startPsittion+"-"+endPosition);
			// 使用java中的RandomAccessFile 对文件进行随机读写操作
			output=new RandomAccessFile(file, "rw");
			// 设置开始写文件的位置
			output.seek(startPsittion);
			
			input=new BufferedInputStream(conn.getInputStream());
			int count=0;
			// 开始循环以流的形式读写文件
			while(count<endPosition){
				int length=input.read(b, 0, 1024);
				if(length==-1)
				break;
				
				output.write(b,0,length);//错误代码：output.write(b,0,1024);
				count+=length;
				
				if(count>endPosition){
					downloadsize+=length-(count-endPosition)+1;
				}else{
					downloadsize+=length;
				}
			}
			isOk=true;// 下载完成设为true
			input.close();
			output.close();						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isOk() {
		return isOk;
	}
	
	public int getDownloadsize() {
		return downloadsize;
	}
	
	
}
