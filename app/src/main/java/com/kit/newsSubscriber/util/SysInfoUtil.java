package com.kit.newsSubscriber.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;

public class SysInfoUtil {

	/**
	 * 服务器地址
	 */
	public static final String HOST = "";

	/**
	 * 判断网络连接是否可用
	 *
	 * @param context 上下文
	 * @return boolean
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (context != null) {
			// 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (manager != null) {
				// 获取NetworkInfo对象
				NetworkInfo networkInfo = manager.getActiveNetworkInfo();
				//判断NetworkInfo对象是否为空
				return networkInfo != null && networkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断通知是否开启
	 * @param context 上下文
	 * @return boolean
	 */
	public static boolean isNotificationEnable(Context context) {
		NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
		return notificationManagerCompat.areNotificationsEnabled();
	}

	/**
	 * 从系统信息中组合出mqtt的client id
	 * @return client id
	 */
	public static String getClientID() {
		return  "" +
				Build.BOARD.length()%10 +
				Build.BRAND.length()%10 +
				Build.SUPPORTED_ABIS[0].length()%10 +
				Build.DEVICE.length()%10 +
				Build.DISPLAY.length()%10 +
				Build.HOST.length()%10 +
				Build.ID.length()%10 +
				Build.MANUFACTURER.length()%10 +
				Build.MODEL.length()%10 +
				Build.PRODUCT.length()%10 +
				Build.TAGS.length()%10 +
				Build.TYPE.length()%10 +
				Build.USER.length()%10 ; //13 digits
	}
}
