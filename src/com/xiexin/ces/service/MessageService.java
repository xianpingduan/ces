package com.xiexin.ces.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.PushNotificationCenter;
import com.xiexin.ces.entry.PushMessage;
import com.xiexin.ces.receiver.PushScreenOnReceiver;

public class MessageService extends Service
{

    private final static String TAG = "MessageService";

    private PushScreenOnReceiver mPushScreenOnReceiver;

    private final static int MSG_REQUEST_LAST_MSG_SUCCESS = 1;
    private final static int MSG_REQUEST_LAST_MSG_ERROR = 2;

    private RequestQueue mQueue;

    private String mMsgStr;

    private Context mContext;

    @Override
    public IBinder onBind( Intent intent )
    {
	return null;
    }

    @Override
    public void onCreate()
    {
	super.onCreate( );

	mContext = this;
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

		    if( mMsgStr != null && !mMsgStr.isEmpty( ) )
		    {

			try
			{
			    JSONArray jsonArray = new JSONArray( mMsgStr );
			    if( jsonArray.length( ) > 0 )
			    {
				JSONObject jsonObject = jsonArray.getJSONObject( 0 );
				setMessage( jsonObject );

			    }
			}
			catch ( JSONException e )
			{
			    // TODO Auto-generated catch block
			    e.printStackTrace( );
			}

		    }

		    break;
		case MSG_REQUEST_LAST_MSG_ERROR :

		    break;

		default :
		    break;
	    }
	};
    };

    private void setMessage( JSONObject jsonObject )
    {

	PushMessage msg = new PushMessage( );
	try
	{
	    msg.setAccount( jsonObject.getString( "Account" ) );
	    msg.setApprID( jsonObject.getInt( "ApprID" ) );
	    msg.setbRead( jsonObject.getInt( "bRead" ) );
	    msg.setContent( jsonObject.getString( "Content" ) );
	    msg.setCrtDate( jsonObject.getString( "CrtDate" ) );
	    msg.setFilesPath( jsonObject.getString( "FilesPath" ) );
	    msg.setFromUser( jsonObject.getString( "FromUser" ) );
	    msg.setMsgID( jsonObject.getString( "MsgID" ) );
	    msg.setMsgType( jsonObject.getInt( "MsgType" ) );
	    msg.setTitle( jsonObject.getString( "Title" ) );
	    msg.setToUser( jsonObject.getString( "ToUser" ) );

	    PushNotificationCenter.getInstance( mContext ).addMessageNotification( msg.getTitle( ) , msg.getContent( ) );

	}
	catch ( JSONException e )
	{
	    e.printStackTrace( );
	}

    }

    // account=web_101&amp;userid=600012&amp;bread=0&amp;page=1&amp;size=100
    public void startGetMsg()
    {
	String account = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );
	String userid = App.getSharedPreference( ).getString( Constants.USER_ID , "" );

	StringBuffer urlSbf = new StringBuffer( Constants.ROOT_URL + Constants.GET_MSG_URL + "?" );
	urlSbf.append( "userid=" ).append( userid );
	urlSbf.append( "account=" ).append( account );
	urlSbf.append( "bread=" ).append( 0 );
	urlSbf.append( "page=" ).append( 1 );
	urlSbf.append( "size=" ).append( 1 );
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
			mMsgStr = response.getString( "Data" );
			long next_req_time = System.currentTimeMillis( ) + Constants.DEFAULT_GAP_TIME;
			Log.d( TAG , "next_req_time =" + next_req_time );
			App.getSharedPreference( ).edit( ).putLong( Constants.THE_LAST_REQUEST_MSG_TIME , next_req_time );
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
