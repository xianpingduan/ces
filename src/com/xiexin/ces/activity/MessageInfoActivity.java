package com.xiexin.ces.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MessageInfoActivity extends Activity
{

    private String mMessageContent;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );

	initView( );

	initData( );
    }

    private void initView()
    {

    }

    private void initData()
    {
	Intent intent = getIntent( );
	mMessageContent = intent.getStringExtra( "" );
    }

    @Override
    protected void onResume()
    {
	super.onResume( );
    }

    @Override
    protected void onDestroy()
    {
	super.onDestroy( );
    }
}
