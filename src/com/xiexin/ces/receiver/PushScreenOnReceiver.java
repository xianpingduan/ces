package com.xiexin.ces.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class PushScreenOnReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive( Context context , Intent intent )
    {

	if( intent.getAction( ).equals( Intent.ACTION_SCREEN_ON ) )
	{

	}
	else if( intent.getAction( ).equals( ConnectivityManager.CONNECTIVITY_ACTION ) )
	{

	}
    }

    //    private void generateService( Context context , int command , int trigger )
    //    {
    //	Intent intent = new Intent( );
    //	intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
    //	context.startService( intent );
    //    }
}
