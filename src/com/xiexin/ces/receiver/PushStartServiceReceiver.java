package com.xiexin.ces.receiver;

import java.util.List;

import pada.juidownloader.util.LogUtils;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiexin.ces.service.MessageService;

/**
 * 启动service入口
*/

public class PushStartServiceReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive( Context context , Intent intent )
    {
	//判断系统启动、service是否在运行
	if( !isServiceRunning( context , "com.xiexin.ces.service.MessageService" ) )
	{
	    Intent intentToService = new Intent( );
	    intentToService.setClass( context , MessageService.class );
	    context.startService( intentToService );
	}
    }

    //判断进程是否在运行
    public boolean isServiceRunning( Context context , String serviceClassName )
    {
	final ActivityManager activityManager = (ActivityManager)context.getSystemService( Context.ACTIVITY_SERVICE );
	final List< RunningServiceInfo > services = activityManager.getRunningServices( Integer.MAX_VALUE );

	for( RunningServiceInfo runningServiceInfo : services )
	{
	    if( runningServiceInfo.service.getClassName( ).equals( serviceClassName ) )
	    {
		LogUtils.d( serviceClassName + " ,服务已经启动! " );
		return true;
	    }
	}
	return false;
    }

}
