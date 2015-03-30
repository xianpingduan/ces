package com.xiexin.ces.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.xiexin.ces.App;
import com.xiexin.ces.Constants;

public class PushScreenOnReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive( Context context , Intent intent )
    {

	long currentTime = System.currentTimeMillis( );
	long nextReqTime = App.getSharedPreference( ).getLong( Constants.THE_LAST_REQUEST_MSG_TIME , 0 );
	long nextReqApprovalTime = App.getSharedPreference().getLong(Constants.THE_LAST_REQUEST_APPROVAL_TIME, 0);

	if( intent.getAction( ).equals( Intent.ACTION_SCREEN_ON ) )
	{
	    if( currentTime >= nextReqTime )
	    {
	    	requestMsg( context );
	    }
	    
	    if(currentTime>=nextReqApprovalTime){
	    	
	    	requestApproval(context);
	    }

	}
	else if( intent.getAction( ).equals( ConnectivityManager.CONNECTIVITY_ACTION ) )
	{
	    if( currentTime >= nextReqTime )
	    {
	    	requestMsg( context );
	    }
	    
	    if(currentTime>=nextReqApprovalTime){
	    	
	    	requestApproval(context);
	    }
	}
    }

    private void requestMsg( Context context )
    {
		Intent in = new Intent( );
		in.putExtra("type", 0);
		in.setAction( "MessageService.action.command" );
		context.startService( in );
    }
    
    private void requestApproval( Context context )
    {
		Intent in = new Intent( );
		in.putExtra("type", 1);
		in.setAction( "MessageService.action.command" );
		context.startService( in );
    }

    //    private void generateService( Context context , int command , int trigger )
    //    {
    //	Intent intent = new Intent( );
    //	intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
    //	context.startService( intent );
    //    }
}
