package com.xiexin.ces;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public class PushNotificationCenter
{

    private static PushNotificationCenter mInstance;
    private final NotificationManager mNotificationManager;
    private static final int MESSAGE_NOTIFY_ID = 100001;
    private Context mContext;

    private PushNotificationCenter( Context context )
    {
	mContext = context;
	mNotificationManager = (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
    }

    public static PushNotificationCenter getInstance( Context context )
    {
	if( mInstance == null )
	{
	    mInstance = new PushNotificationCenter( context );
	}
	return mInstance;
    }

    public void cancelNotify()
    {
	mNotificationManager.cancel( MESSAGE_NOTIFY_ID );
    }

    /**
     * 消息通知
     * */
    public void addMessageNotification( String title , String msg )
    {
	NotificationManager mNotificationManager = (NotificationManager)mContext.getSystemService( Context.NOTIFICATION_SERVICE );
	Notification.Builder nb = new Notification.Builder( mContext );
	nb.setContentTitle( title );
	nb.setContentText( msg );
	nb.setSmallIcon( R.drawable.logo );
	nb.setWhen( System.currentTimeMillis( ) );
	nb.setOngoing( false );

	mNotificationManager.notify( MESSAGE_NOTIFY_ID , nb.getNotification( ) );
    }

}
