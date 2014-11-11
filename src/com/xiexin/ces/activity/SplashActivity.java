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
	
	String url = App.getSharedPreference().getString(Constants.SERVER_CONFIG_URL, "");
	String port = App.getSharedPreference().getString(Constants.SERVER_CONFIG_PORT, "");
	
	Constants.ROOT=url+":"+port;
	Logger.d(TAG,"server url="+ url+":"+port);
	
	new Handler().postDelayed(new Runnable() {
		
		@Override
		public void run() {
			// TODO 
			
			
			
			intentTo();
		}
	}, 1000);
	
	
    }
    
    private void doRequestServerConfigInfo(){
    	
    	
    }
    
    
    private void intentTo(){
		Intent intent = new Intent();
		intent.setClass(SplashActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
    }

    @Override
    protected void onResume()
    {
	// TODO Auto-generated method stub
	super.onResume( );
    }

    @Override
    protected void onDestroy()
    {
	// TODO Auto-generated method stub
	super.onDestroy( );
    }
    
    @Override
    public void onBackPressed() {
    	return ;
    }
}
