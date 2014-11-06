package com.ivali.gamebox.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ivali.gamebox.MainActivity;
import com.ivali.gamebox.R;


/***
 * 更新版本
 * 
 * @author zhangjia
 * 
 */
public class UpdateService extends Service {
	private static final int TIMEOUT = 10 * 1000;// 超时
	//private static final String down_url = "http://1.kaiyuanxiangmu.sinaapp.com/healthworld/data/download/healthworld_v1.0_beta_official.apk";
	private static final int DOWN_OK = 1;
	private static final int DOWN_ERROR = 0;

	public String  app_name;
	public String  down_url;
	public String  notiIds;
	public String  filepaths;
	public NotificationManager notificationManager;
	private Intent updateIntent;
	private PendingIntent pendingIntent;


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			if(intent.getStringExtra("app_name")!=null){
				app_name = intent.getStringExtra("app_name");
			}
			if(intent.getStringExtra("url")!=null){
				down_url= intent.getStringExtra("url");
			}
			if(intent.getStringExtra("noti")!=null){
				notiIds= intent.getStringExtra("noti");
			}
			if(intent.getStringExtra("filepath")!=null){
				filepaths= intent.getStringExtra("filepath");
			}
			// 一个应用下载		
			Notification notification=createNotification(app_name);
			
			notificationManager.notify(Integer.parseInt(notiIds), 
					notification);
			createThread(down_url,
					filepaths,
					notification,
					Integer.parseInt(notiIds));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return super.onStartCommand(intent, flags, startId);

	}
	  
	  @Override
	public void onCreate() {
		// TODO Auto-generated method stu
		super.onCreate();
		notificationManager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	/***
	 * 开线程下载
	 */
	public void createThread(final String url,final String filepath,final Notification noti,final Integer notiId) {
		/***
		 * 更新UI
		 */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DOWN_OK:
					// 下载完成，点击安装
					notificationManager.cancel(notiId);
					stopSelf();
						//下载完成
						Intent in=new Intent();
						in.setAction("com.ivali.gamebox.download");
						in.putExtra("flag", true);
						in.putExtra("filepath", filepath);
						in.putExtra("notiId", notiId+"");
						UpdateService.this.sendBroadcast(in);				
					break;
				case DOWN_ERROR:
					notificationManager.cancel(notiId);
					stopSelf();
					break;

				default:
					notificationManager.cancel(notiId);
					stopSelf();
					break;
				}

			}

		};

		final Message message = new Message();

		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					long downloadSize = downloadUpdateFile(url,
							filepath,noti,notiId);
					if (downloadSize > 0) {
						// 下载成功
						message.what = DOWN_OK;
						handler.sendMessage(message);
					}

				} catch (Exception e) {
					e.printStackTrace();
					message.what = DOWN_ERROR;
					handler.sendMessage(message);
				}

			}
		}).start();
	}

	/***
	 * 创建通知栏
	 */
	

	public Notification createNotification(String appname) {
		Notification notification = new Notification();
		/***
		 * 在这里我们用自定的view来显示Notification
		 */
		
		notification.contentView = getRemoteViews(appname);
		//notification.icon = R.drawable.ic_launcher;
		notification.icon = android.R.drawable.stat_sys_download;
		updateIntent = new Intent(this, MainActivity.class);
		updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);

		notification.contentIntent = pendingIntent;
		return notification;
		

	}
	public RemoteViews getRemoteViews(String appname){
		RemoteViews contentView;
		contentView = new RemoteViews(getPackageName(),
				R.layout.notification_item);
		contentView.setTextViewText(R.id.notificationTitle, appname);
		contentView.setTextViewText(R.id.notificationPercent, "0%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		return contentView;
	}
	/***
	 * 下载文件
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public long downloadUpdateFile(String down_url, String file,Notification noti,Integer notiId)
			throws Exception {
		int down_step = 5;// 提示step
		int totalSize;// 文件总大小
		int downloadCount = 0;// 已经下载好的大小
		int updateCount = 0;// 已经上传的文件大小
		InputStream inputStream;
		OutputStream outputStream;

		URL url = new URL(down_url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		// 获取下载文件的size
		totalSize = httpURLConnection.getContentLength();
		if (httpURLConnection.getResponseCode() == 404) {
			throw new Exception("fail!");
		}
		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉
		byte buffer[] = new byte[1024*512];
		int readsize = 0;
		while ((readsize = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;// 时时获取下载到的大小
			/**
			 * 每次增张5%
			 */
			if (updateCount == 0
					|| (downloadCount * 100 / totalSize - down_step) >= updateCount) {
				updateCount += down_step;
				// 改变通知栏
				// notification.setLatestEventInfo(this, "正在下载...", updateCount
				// + "%" + "", pendingIntent);
				
				noti.contentView.setTextViewText(R.id.notificationPercent,
						updateCount + "%");
				noti.contentView.setProgressBar(R.id.notificationProgress, 100,
						updateCount, false);
				// show_view
				notificationManager.notify(notiId, noti);
			}

		}
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();

		return downloadCount;

	}

}
