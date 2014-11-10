package com.xiexin.ces.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiexin.ces.R;

public class ServerConfigActivity extends Activity
{

    public final static String TAG = "ServerConfigActivity";
    private EditText mServerConfigUrlEt;
    private EditText mServerConfigPortEt;
    private Context mContext;

    //header start
    private LinearLayout mReturnLl;
    private ImageView mReturnIv;
    private TextView mReturnTv;
    private TextView mTitle;
    private Button mBtn1;
    private Button mBtn2;

    //header end

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	// TODO Auto-generated method stub
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_server_config );
	mContext = this;
	initView( );

    }

    private void initView()
    {
	mServerConfigUrlEt = (EditText)findViewById( R.id.server_config_url_et );
	mServerConfigPortEt = (EditText)findViewById( R.id.server_config_port_et );

	//header start
	mReturnLl = (LinearLayout)findViewById( R.id.return_ll );
	mReturnIv = (ImageView)findViewById( R.id.return_iv );
	mReturnTv = (TextView)findViewById( R.id.return_tv );
	mTitle = (TextView)findViewById( R.id.title );
	mBtn1 = (Button)findViewById( R.id.btn1 );
	mBtn2 = (Button)findViewById( R.id.btn2 );
	///header end
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
