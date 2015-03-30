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
import android.text.Html;

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
import com.xiexin.ces.R;
import com.xiexin.ces.entry.PushMessage;
import com.xiexin.ces.receiver.PushScreenOnReceiver;
import com.xiexin.ces.utils.Logger;

public class MessageService extends Service
{

    private final static String TAG = "MessageService";

    private PushScreenOnReceiver mPushScreenOnReceiver;

    private final static int MSG_REQUEST_LAST_MSG_SUCCESS = 1;
    private final static int MSG_REQUEST_LAST_MSG_ERROR = 2;
    private final static int MSG_REQUEST_LAST_APPROVAL_SUCCESS = 3;
    private final static int MSG_REQUEST_LAST_APPROVAL_ERROR = 4;

    private RequestQueue mQueue;

    private String mMsgStr;
    private String mApprovalStr;

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
		case MSG_REQUEST_LAST_APPROVAL_SUCCESS :
			String data = (String) msg.obj;
			if(data!=null){
				int size = size(data);
				if(size>0){
					sendHaveAppMsg();
				}
			}
	
			break;
		case MSG_REQUEST_LAST_APPROVAL_ERROR :
			
			break;

		default :
		    break;
	    }
	};
    };
    
    private void sendHaveAppMsg(){
    	 PushNotificationCenter.getInstance( mContext ).
    	 addMessageNotification( mContext.getString(R.string.task_note) ,mContext.getString(R.string.have_approval_to_handle) );
    }

    private void setMessage( JSONObject jsonObject )
    {

	PushMessage msg = new PushMessage( );
	try
	{
	    msg.setAccount( jsonObject.getString( "account" ) );
	    msg.setApprid( jsonObject.getInt( "apprid" ) );
	    msg.setBread( jsonObject.getInt( "bread" ) );
	    msg.setContent( jsonObject.getString( "content" ) );
	    msg.setCrtdate( jsonObject.getString( "crtdate" ) );
	    msg.setFilespath( jsonObject.getString( "filespath" ) );
	    msg.setFromuser( jsonObject.getString( "fromuser" ) );
	    msg.setMsgid( jsonObject.getString( "msgid" ) );
	    msg.setMsgtype( jsonObject.getInt( "msgtype" ) );
	    msg.setTitle( jsonObject.getString( "title" ) );
	    msg.setTouser( jsonObject.getString( "touser" ) );

	    PushNotificationCenter.getInstance( mContext ).addMessageNotification( msg.getTitle( ) ,Html.fromHtml( msg.getContent( )).toString() );

	}
	catch ( JSONException e )
	{
	    e.printStackTrace( );
	}

    }
    
    
	private int size(String jsonStr) {
			JSONArray arrays;
			try {
				arrays = new JSONArray(jsonStr);
				return arrays.length();
			} catch (JSONException e) {
				return  0;
			}
		

	}

    // account=web_101&amp;userid=600012&amp;bread=0&amp;page=1&amp;size=100
    public void startGetMsg()
    {
	String account = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );
	String userid = App.getSharedPreference( ).getString( Constants.USER_ID , "" );

	StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) + Constants.GET_MSG_URL + "?" );
	urlSbf.append( "userid=" ).append( userid );
	urlSbf.append( "&account=" ).append( account );
	urlSbf.append( "&bread=" ).append( 0 );
	urlSbf.append( "&page=" ).append( 1 );
	urlSbf.append( "&size=" ).append( 1 );
	JsonObjectRequest json = new JsonObjectRequest( Method.GET , urlSbf.toString( ) , null , new Listener< JSONObject >( )
	{
	    @Override
	    public void onResponse( JSONObject response )
	    {
		// TODO
		try
		{
		    int resCode = response.getInt( "success" );
		    if( resCode == 0 )
		    {
			mMsgStr = response.getString( "data" );
			long next_req_time = System.currentTimeMillis( ) + Constants.DEFAULT_GAP_TIME;
			Logger.d( TAG , "next_req_time =" + next_req_time );
			App.getSharedPreference( ).edit( ).putLong( Constants.THE_LAST_REQUEST_MSG_TIME , next_req_time ).commit( );
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

	if( !account.isEmpty( ) && !userid.isEmpty( ) )
	{
	    mQueue.add( json );
	    mQueue.start( );
	}

    }
    
    
    public void startGetApproval()
    {
    	
    Logger.d(TAG, "MessageService,startGetApproval");
	String account = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );
	String userid = App.getSharedPreference( ).getString( Constants.USER_ID , "" );

	StringBuffer urlSbf = new StringBuffer(App.getRootUrl()
			+ Constants.GET_WORK_MESSAGE_URL + "?");
	urlSbf.append("account=").append(account);
	urlSbf.append("&userid=").append(userid);
	urlSbf.append("&kind=").append(Constants.TYPE_PEND_APPROVAL_TASKS);
	urlSbf.append("&size=").append(Constants.PAGE_SIZE);
	urlSbf.append("&page=").append(1);
	urlSbf.append("&filter=").append("");
	JsonObjectRequest json = new JsonObjectRequest( Method.GET , urlSbf.toString( ) , null , new Listener< JSONObject >( )
	{
	    @Override
	    public void onResponse( JSONObject response )
	    {
		// TODO
		try
		{
		    int resCode = response.getInt( "success" );
		    if( resCode == 0 )
		    {
		    mApprovalStr = response.getString( "data" );
			long next_req_time = System.currentTimeMillis( ) + Constants.DEFAULT_GAP_TIME;
			Logger.d( TAG , "next_req_time =" + next_req_time );
			App.getSharedPreference( ).edit( ).putLong( Constants.THE_LAST_REQUEST_APPROVAL_TIME , next_req_time ).commit( );
			mHandler.sendEmptyMessage( MSG_REQUEST_LAST_APPROVAL_SUCCESS );
		    }
		    else
		    {
			mHandler.sendEmptyMessage( MSG_REQUEST_LAST_APPROVAL_ERROR );
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
		mHandler.sendEmptyMessage( MSG_REQUEST_LAST_APPROVAL_ERROR );
	    }
	} );

	if( !account.isEmpty( ) && !userid.isEmpty( ) )
	{
	    mQueue.add( json );
	    mQueue.start( );
	}

    }

    @Override
    public int onStartCommand( Intent intent , int flags , int startId )
    {
    	
    	int type  = 0;
	    if(intent!=null){
	    	type = intent.getIntExtra("type", 0);
	    }
	    switch (type) {
		case 0:
			startGetMsg( );
			break;
		case 1:
			startGetApproval( );
			break;
		default:
			break;
		}
		return super.onStartCommand( intent , flags , startId );

    }

}
