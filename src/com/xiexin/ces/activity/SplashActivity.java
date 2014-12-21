package com.xiexin.ces.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.utils.Logger;

public class SplashActivity extends Activity
{

    public final static String TAG = "SplashActivity";

    private boolean mIsSetSerVerConfig = false;

    private TextView mPercentTv;

    private RequestQueue mQueue;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	// TODO Auto-generated method stub
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_splash );

	mQueue = Volley.newRequestQueue( App.getAppContext( ) );

	initView( );

	// 启动拉取服务
	startPushService( );

	// test();

	String url = App.getSharedPreference( ).getString( Constants.SERVER_CONFIG_URL , "" );
	String port = App.getSharedPreference( ).getString( Constants.SERVER_CONFIG_PORT , "" );
	//	Constants.ROOT = url + ":" + port;
	Logger.d( TAG , "server url=" + url + ":" + port );
	if( url.isEmpty( ) )
	{
	    mIsSetSerVerConfig = true;
	}

	new Handler( ).postDelayed( new Runnable( )
	{
	    @Override
	    public void run()
	    {
		// 加载数据，跳转到登录界面
		// TODO
		if( mIsSetSerVerConfig )
		{
		    intentToServerConfig( );
		}
		else
		{
		    doRequestServerConfigInfo( );
		}
	    }
	} , 1000 );
    }

    private final static int MSG_GET_SERVER_CONFOG_SUCCESS = 1;
    private final static int MSG_GET_SERVER_CONFOG_ERROR = 2;
    private Handler mUiHandler = new Handler( )
    {

	@Override
	public void handleMessage( Message msg )
	{
	    // TODO Auto-generated method stub
	    super.handleMessage( msg );

	    switch ( msg.what )
	    {
		case MSG_GET_SERVER_CONFOG_SUCCESS :
		    final boolean autoLogin = App.getSharedPreference( ).getBoolean( Constants.AUTO_LOGIN , false );
		    if( autoLogin )
		    {
			intentToMain( );
		    }
		    else
		    {
			intentToLogin( );
		    }
		    finish( );
		    break;
		case MSG_GET_SERVER_CONFOG_ERROR :

		    break;
		default :
		    break;
	    }
	}

    };

    private int counter = 0;
    private Timer timer;

    private void initView()
    {
	mPercentTv = (TextView)findViewById( R.id.percent );
	counter = 0;
	timer = new Timer( );
	timer.schedule( new TimerTask( )
	{
	    @Override
	    public void run()
	    {
		runOnUiThread( new Runnable( )
		{
		    @Override
		    public void run()
		    {
			mPercentTv.setText( counter + "%" );
			counter = counter + 10;
			if( counter > 100 )
			{
			    mPercentTv.setText( "100%" );
			}
		    }
		} );
	    }
	} , 100 , 100 );
    }

    // 加载服务器信息
    private void doRequestServerConfigInfo()
    {
	StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) + Constants.GET_SERVER_CFG );
	JsonObjectRequest json = new JsonObjectRequest( Method.GET , urlSbf.toString( ) , null , new Listener< JSONObject >( )
	{
	    @Override
	    public void onResponse( JSONObject response )
	    {
		Logger.d( TAG , "----response----" + response.toString( ) );
		try
		{
		    int resCode = response.getInt( "Success" );
		    Message msg = Message.obtain( );
		    if( resCode == 0 )
		    {
			msg.what = MSG_GET_SERVER_CONFOG_SUCCESS;
			msg.obj = response.getString( "Data" );
		    }
		    else
		    {

			msg.what = MSG_GET_SERVER_CONFOG_ERROR;
			msg.obj = response.get( "Msg" );
		    }

		    mUiHandler.sendMessage( msg );
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
		mUiHandler.sendEmptyMessage( MSG_GET_SERVER_CONFOG_ERROR );
	    }
	} );
	mQueue.add( json );
	mQueue.start( );
	// mUiHandler.sendEmptyMessage( MSG_GET_SERVER_CONFOG_SUCCESS );
    }

    private void intentToLogin()
    {
	Intent intent = new Intent( );
	intent.setClass( SplashActivity.this , LoginActivity.class );
	startActivity( intent );
	finish( );
    }

    private void intentToServerConfig()
    {
	Intent intent = new Intent( );
	intent.setClass( SplashActivity.this , ServerConfigActivity.class );
	startActivity( intent );
	finish( );
    }

    private void intentToMain()
    {
	Intent intent = new Intent( );
	intent.setClass( SplashActivity.this , MenuActivity.class );
	intent.putExtra( Constants.CONN_CHANGED , false );
	startActivity( intent );
	finish( );
    }

    @Override
    protected void onResume()
    {
	super.onResume( );
    }

    @Override
    protected void onDestroy()
    {
	// TODO Auto-generated method stub
	super.onDestroy( );
    }

    @Override
    public void onBackPressed()
    {
	return;
    }

    private void test()
    {

	try
	{
	    JSONObject obj = new JSONObject( Constants.RQ001 );
	    Logger.d( TAG , obj.get( "Data" ).toString( ) );
	}
	catch ( JSONException e )
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace( );
	}
    }

    private void startPushService()
    {
	Intent intent = new Intent( );
	intent.setAction( "com.xiexin.ces.receiver.PushStartServiceReceiver" );
	sendStickyBroadcast( intent );
    }
}
