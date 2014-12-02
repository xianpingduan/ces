package com.xiexin.ces.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.receiver.PushScreenOnReceiver;

public class MessageService extends Service
{

    private PushScreenOnReceiver mPushScreenOnReceiver;

    private final static int MSG_REQUEST_LAST_MSG_SUCCESS = 1;
    private final static int MSG_REQUEST_LAST_MSG_ERROR = 2;

    private RequestQueue mQueue;

    @Override
    public IBinder onBind( Intent intent )
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void onCreate()
    {
	// TODO Auto-generated method stub
	super.onCreate( );

	mQueue = Volley.newRequestQueue( App.getAppContext( ) );

	mPushScreenOnReceiver = new PushScreenOnReceiver( );
	IntentFilter intentFilter = new IntentFilter( );
	intentFilter.addAction( Intent.ACTION_SCREEN_ON );
	intentFilter.addAction( ConnectivityManager.CONNECTIVITY_ACTION );
	registerReceiver( mPushScreenOnReceiver , intentFilter );
    }

    private Handler mHandler = new Handler( )
    {
	@Override
	public void handleMessage( android.os.Message msg )
	{
	    switch ( msg.what )
	    {
		case MSG_REQUEST_LAST_MSG_SUCCESS :

		    break;
		case MSG_REQUEST_LAST_MSG_ERROR :

		    break;

		default :
		    break;
	    }
	};
    };

    public void startGetMsg()
    {
	StringBuffer urlSbf = new StringBuffer( Constants.ROOT_URL + Constants.LAST_MSG_URL + "?" );
	urlSbf.append( "userid=" ).append( "" );
	JsonObjectRequest json = new JsonObjectRequest( Method.GET , urlSbf.toString( ) , null , new Listener< JSONObject >( )
	{
	    @Override
	    public void onResponse( JSONObject response )
	    {
		// TODO
		try
		{
		    int resCode = response.getInt( "Success" );
		    if( resCode == 0 )
		    {
			String rspData = response.getString( "Data" );
			mHandler.sendEmptyMessage( MSG_REQUEST_LAST_MSG_SUCCESS );
		    }
		    else
		    {
			mHandler.sendEmptyMessage( MSG_REQUEST_LAST_MSG_ERROR );
		    }
		}
		catch ( JSONException e )
		{
		    e.printStackTrace( );
		}
	    }
	} , new ErrorListener( )
	{
	    @Override
	    public void onErrorResponse( VolleyError error )
	    {
		mHandler.sendEmptyMessage( MSG_REQUEST_LAST_MSG_ERROR );
	    }
	} );

	mQueue.add( json );
	mQueue.start( );
    }

    @Override
    public int onStartCommand( Intent intent , int flags , int startId )
    {
	startGetMsg( );
	return super.onStartCommand( intent , flags , startId );

    }

}
