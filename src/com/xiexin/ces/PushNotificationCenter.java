package com.xiexin.ces;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.xiexin.ces.activity.MenuActivity;
import com.xiexin.ces.utils.Logger;

public class PushNotificationCenter {

	private static final String TAG = PushNotificationCenter.class.getSimpleName();
	private static PushNotificationCenter mInstance;
	private final NotificationManager mNotificationManager;
	private static final int MESSAGE_NOTIFY_ID = 100001;
	private static final int APPROVAL_NOTIFY_ID = 100002;
	private Context mContext;

	private PushNotificationCenter(Context context) {
		mContext = context;
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public static PushNotificationCenter getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new PushNotificationCenter(context);
		}
		return mInstance;
	}

	public void cancelMessageNotify() {
		mNotificationManager.cancel(MESSAGE_NOTIFY_ID);
	}
	
	public void cancelApprovalNotify() {
		mNotificationManager.cancel(APPROVAL_NOTIFY_ID);
	}

	/**
	 * 消息通知
	 * */
	public void addMessageNotification(String title, String msg) {

		Intent intent = new Intent(mContext, MenuActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.MENU_HANDLE, Constants.TYPE_MENU_HANDLE_MSG);
		intent.putExtra(Constants.MENU_HANDLE_BUNDLE, bundle);
		intent.setData(Uri.parse("custom://"+System.currentTimeMillis()));
		PendingIntent pendingIntent = PendingIntent.getActivity(App.getAppContext(), 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder nb = new Notification.Builder(mContext);
		nb.setContentIntent(pendingIntent);
		nb.setContentTitle(title);
		nb.setContentText(msg);
		nb.setSmallIcon(R.drawable.logo);
		nb.setWhen(System.currentTimeMillis());
		nb.setOngoing(false);
		nb.getNotification().defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.FLAG_AUTO_CANCEL;
		//nb.getNotification().flags = Notification.FLAG_ONGOING_EVENT; // 设置常驻 Flag  
		mNotificationManager.notify(MESSAGE_NOTIFY_ID, nb.getNotification());
		
	}
	
	/**
	 * 任务通知
	 * */
	public void addApprovalNotification(String title, String msg) {

		Intent intent = new Intent(mContext, MenuActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.MENU_HANDLE, Constants.TYPE_MENU_HANDLE_APPROVAL);
		intent.putExtra(Constants.MENU_HANDLE_BUNDLE,bundle);
		intent.setData(Uri.parse("custom://"+System.currentTimeMillis()));
		PendingIntent pendingIntent = PendingIntent.getActivity(App.getAppContext(), 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder nb = new Notification.Builder(mContext);
		nb.setContentIntent(pendingIntent);
		nb.setContentTitle(title);
		nb.setContentText(msg);
		nb.setSmallIcon(R.drawable.logo);
		Logger.d(TAG, "addApprovalNotification,"+System.currentTimeMillis());
		nb.setWhen(System.currentTimeMillis());
		nb.setOngoing(false);
		nb.getNotification().defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.FLAG_AUTO_CANCEL;
		//nb.getNotification().flags = Notification.FLAG_ONGOING_EVENT; // 设置常驻 Flag  
		mNotificationManager.notify(APPROVAL_NOTIFY_ID, nb.getNotification());
		
	}
	

}
