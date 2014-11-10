package com.xiexin.ces.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiexin.ces.R;

public class LoginActivity extends Activity implements OnClickListener
{

    public final static String TAG = "LoginActivity";
    private Context mContext;

    private ImageView mLogo;
    private EditText mLoginAccEt;
    private EditText mLoginPwdEt;
    private Button mLoginBtn;
    private RelativeLayout mSelectZtRl;
    private CheckBox mRemmenberPwdCb;
    private CheckBox mAutoLoginCb;
    private TextView mRemmenberPwdTv;
    private TextView mAutoLoginTv;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	// TODO Auto-generated method stub
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_login );
	mContext = this;
	initView( );

    }

    private void initView()
    {
	mLogo = (ImageView)findViewById( R.id.logo );
	mLoginAccEt = (EditText)findViewById( R.id.login_account_et );
	mLoginPwdEt = (EditText)findViewById( R.id.login_pwd_et );
	mLoginBtn = (Button)findViewById( R.id.login_btn );
	mSelectZtRl = (RelativeLayout)findViewById( R.id.zt_select_rl );
	mRemmenberPwdCb = (CheckBox)findViewById( R.id.remmenber_pwd_cb );
	mAutoLoginCb = (CheckBox)findViewById( R.id.auto_login_cb );
	mRemmenberPwdTv = (TextView)findViewById( R.id.remmenber_pwd_tv );
	mAutoLoginTv = (TextView)findViewById( R.id.auto_login_tv );

	mLogo.setOnClickListener( this );
	mLoginBtn.setOnClickListener( this );
	mSelectZtRl.setOnClickListener( this );
	mRemmenberPwdCb.setOnClickListener( this );
	mAutoLoginCb.setOnClickListener( this );
	mRemmenberPwdTv.setOnClickListener( this );
	mAutoLoginTv.setOnClickListener( this );

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

    private void login()
    {

    }

    private void validate()
    {

    }

    private void selectZt()
    {
	Intent intent = new Intent( );
	intent.setClass( LoginActivity.this , ZhangTaoActivity.class );
	startActivityForResult( intent , 1 );
    }

    @Override
    public void onClick( View view )
    {
	switch ( view.getId( ) )
	{
	    case R.id.login_btn :

		break;
	    case R.id.zt_select_rl :
		selectZt( );
		break;

	    case R.id.auto_login_cb :

		break;
	    case R.id.auto_login_tv :

		break;
	    case R.id.remmenber_pwd_cb :

		break;
	    case R.id.remmenber_pwd_tv :

		break;
	    case R.id.logo :

		break;
	    default :
		break;
	}

    }
}
