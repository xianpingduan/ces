package com.xiexin.ces.update;

import java.io.File;

import pada.juidownloader.util.LogUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class SelfUpateNotificationCenter
{

    private static SelfUpateNotificationCenter mInstance;
    private final NotificationManager mNotificationManager;
    private static final int UPDATE_NOTIFY_ID = 100001;
    private static final int SELF_DOWNLOAD_NOTIFY_ID = 100003;

    private Context mContext;

    private int mUpdateNotifyIcon;

    public void setUpdateNotifyIcon( int icon )
    {
	mUpdateNotifyIcon = icon;
    }

    private SelfUpateNotificationCenter( Context context )
    {
	mContext = context;
	mUpdateNotifyIcon = ResourceUtil.getDrawableId( mContext , "app_icon" );
	mNotificationManager = (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
    }

    public static SelfUpateNotificationCenter getInstance( Context context )
    {
	if( mInstance == null )
	{
	    mInstance = new SelfUpateNotificationCenter( context );
	}
	return mInstance;
    }

    private Bitmap getBitmap()
    {
	try
	{
	    PackageInfo info = mContext.getPackageManager( ).getPackageInfo( mContext.getPackageName( ) , PackageManager.GET_SIGNATURES );
	    Drawable icon = info.applicationInfo.loadIcon( mContext.getPackageManager( ) );
	    LogUtils.e( "icon=" + icon.toString( ) );
	    return drawableToBitmap( icon );
	}
	catch ( NameNotFoundException e )
	{
	    LogUtils.e( "startUpgrade," + e.getMessage( ) );
	    return null;
	}
    }

    private Bitmap drawableToBitmap( Drawable drawable )
    {

	// 取 drawable 的长宽  
	int w = drawable.getIntrinsicWidth( );
	int h = drawable.getIntrinsicHeight( );
	// 取 drawable 的颜色格式  
	Bitmap.Config config = drawable.getOpacity( ) != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
	Bitmap bitmap = Bitmap.createBitmap( w , h , config );
	Canvas canvas = new Canvas( bitmap );
	//canvas.setBitmap(bitmap);  
	drawable.setBounds( 0 , 0 , w , h );
	drawable.draw( canvas );
	return bitmap;
    }

    /** 开始自更新下载通知
     * 
     * */
    public void addSelfDownloadStartNotification()
    {
	Notification.Builder nb = new Notification.Builder( mContext );
	nb.setContentTitle( mContext.getString( ResourceUtil.getStringId( mContext , "self_update_notify_start" ) ) );
	nb.setProgress( 100 , 0 , true );
	nb.setSmallIcon( mUpdateNotifyIcon );
	if( getBitmap( ) != null )
	{
	    nb.setLargeIcon( getBitmap( ) );
	}
	nb.setWhen( System.currentTimeMillis( ) );
	nb.setOngoing( true );

	mNotificationManager.notify( SELF_DOWNLOAD_NOTIFY_ID , nb.getNotification( ) );
    }

    /** 自更新下载完成通知
     * 
     * */
    public void addSelfDownloadFinishNotification( File file )
    {
	Intent intent = new Intent( );
	intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
	intent.setAction( android.content.Intent.ACTION_VIEW );
	Uri uri = Uri.fromFile( file );
	intent.setDataAndType( uri , "application/vnd.android.package-archive" );
	PendingIntent pendingIntent = PendingIntent.getActivity( mContext , 0 , intent , 0 );
	Notification.Builder nb = new Notification.Builder( mContext );
	nb.setContentTitle( mContext.getString( ResourceUtil.getStringId( mContext , "self_update_notify_end" ) ) );
	nb.setContentText( mContext.getString( ResourceUtil.getStringId( mContext , "self_update_notify_end_text" ) ) );
	nb.setSmallIcon( mUpdateNotifyIcon );
	if( getBitmap( ) != null )
	{
	    nb.setLargeIcon( getBitmap( ) );
	}
	nb.setWhen( System.currentTimeMillis( ) );
	nb.setContentIntent( pendingIntent );
	nb.setOngoing( false );
	mNotificationManager.notify( SELF_DOWNLOAD_NOTIFY_ID , nb.getNotification( ) );
    }

    /**自更新下载进度通知
     * 
     * */
    public void addSelfDownloadProgressNotification( int dlsize , int totalsize )
    {
	NotificationManager mNotificationManager = (NotificationManager)mContext.getSystemService( Context.NOTIFICATION_SERVICE );
	Notification.Builder nb = new Notification.Builder( mContext );
	nb.setContentTitle( mContext.getString( ResourceUtil.getStringId( mContext , "self_update_notify_start" ) ) );
	int progress = dlsize / totalsize * 100;
	nb.setProgress( 100 , progress , true );
	nb.setSmallIcon( mUpdateNotifyIcon );
	if( getBitmap( ) != null )
	{
	    nb.setLargeIcon( getBitmap( ) );
	}
	nb.setWhen( System.currentTimeMillis( ) );
	nb.setOngoing( true );
	mNotificationManager.notify( SELF_DOWNLOAD_NOTIFY_ID , nb.getNotification( ) );
    }

    /**自更新下载失败通知
     * 
     * */
    public void addSelfDownloadErrorNotification()
    {
	NotificationManager mNotificationManager = (NotificationManager)mContext.getSystemService( Context.NOTIFICATION_SERVICE );
	Notification.Builder nb = new Notification.Builder( mContext );
	nb.setContentTitle( mContext.getString( ResourceUtil.getStringId( mContext , "self_update_notify_error" ) ) );
	nb.setContentText( mContext.getString( ResourceUtil.getStringId( mContext , "self_update_notify_error_text" ) ) );
	nb.setSmallIcon( mUpdateNotifyIcon );
	if( getBitmap( ) != null )
	{
	    nb.setLargeIcon( getBitmap( ) );
	}
	nb.setWhen( System.currentTimeMillis( ) );
	nb.setOngoing( false );
	mNotificationManager.notify( SELF_DOWNLOAD_NOTIFY_ID , nb.getNotification( ) );
    }

    public void cancelNotify()
    {
	mNotificationManager.cancel( UPDATE_NOTIFY_ID );
    }

}
