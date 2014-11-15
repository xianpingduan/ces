package com.xiexin.ces.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.utils.Logger;

public class SplashActivity extends Activity
{

    public final static String TAG = "SplashActivity";

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	// TODO Auto-generated method stub
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_splash );

	String url = App.getSharedPreference( ).getString( Constants.SERVER_CONFIG_URL , "" );
	String port = App.getSharedPreference( ).getString( Constants.SERVER_CONFIG_PORT , "" );

	Constants.ROOT = url + ":" + port;
	Logger.d( TAG , "server url=" + url + ":" + port );

	if( url.isEmpty( ) )
	{
	    intentToServerConfig( );
	    return;
	}

    }

    //加载服务器信息
    private void doRequestServerConfigInfo()
    {

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
    }

    private void intentToMain()
    {
	Intent intent = new Intent( );
	intent.setClass( SplashActivity.this , MenuActivity.class );
	startActivity( intent );
    }

    @Override
    protected void onResume()
    {

	//加载数据，跳转到登录界面
	//TODO
	final boolean autoLogin = App.getSharedPreference( ).getBoolean( Constants.AUTO_LOGIN , false );
	new Handler( ).postDelayed( new Runnable( )
	{
	    @Override
	    public void run()
	    {
		// TODO 
		if( autoLogin )
		{
		    intentToMain( );
		}
		else
		{
		    intentToLogin( );
		}

		SplashActivity.this.finish( );

	    }
	} , 1000 );
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
}
