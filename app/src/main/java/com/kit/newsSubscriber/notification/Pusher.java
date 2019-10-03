package com.kit.newsSubscriber.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.newsSubscriber.R;
import com.kit.newsSubscriber.news.NewsDisplayActivity;

public class Pusher {

	@SuppressLint("StaticFieldLeak")
	private static volatile Pusher instance = null;
	private Context context;
	private NotificationManager notificationManager;

	private Pusher(Context context) {
		this.context = context;
		this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public static Pusher getInstance(Context context) {
		if (instance == null) {
			synchronized (Pusher.class) {
				if (instance == null) {
					instance = new Pusher(context);
				}
			}
		}
		return instance;
	}

	public void notification(String tag, String url, String title, String digest) {
		Intent intent = new Intent(context, NewsDisplayActivity.class);
		intent.putExtra("news_url", url);
		intent.setData(Uri.parse(url));
		//8.0 以后需要加上channelId 才能正常显示
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			String channelId = "default";
			String channelName = "默认通知";
			notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
		}

		//设置TaskStackBuilder
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(NewsDisplayActivity.class);
		stackBuilder.addNextIntent(intent);

		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(context, "default")
				.setSmallIcon(R.mipmap.notification)
				.setContentTitle(title)
				.setContentText(digest)
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pendingIntent)
				.build();

		notificationManager.notify(tag, 0, notification);
	}
}
