package com.xiexin.ces.activity;

import android.app.Activity;
import android.os.Bundle;

import com.xiexin.ces.R;

public class SplashActivity extends Activity
{

    public final static String TAG = "SplashActivity";

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	// TODO Auto-generated method stub
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_login );
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
}
