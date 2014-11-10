package com.xiexin.ces.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.utils.Logger;

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

    private TextView mZtTv;

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

	mZtTv = (TextView)findViewById( R.id.zt_tv );

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
	if( validate( ) )
	{
	    doRequestLogin( );
	}
    }

    private void doRequestLogin()
    {

    }

    private boolean validate()
    {
	String account = mLoginAccEt.getText( ).toString( );
	String pwd = mLoginPwdEt.getText( ).toString( );
	String zt = mZtTv.getText( ).toString( );
	if( account.isEmpty( ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_enter_account ) , Toast.LENGTH_SHORT ).show( );
	    return false;
	}
	else if( pwd.isEmpty( ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_enter_pwd ) , Toast.LENGTH_SHORT ).show( );
	    return false;
	}
	else if( zt.isEmpty( ) || zt.equals( getString( R.string.p_select_zt_text ) ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_select_zt ) , Toast.LENGTH_SHORT ).show( );
	    return false;
	}
	return true;
    }

    private void selectZt()
    {
	String account = mLoginAccEt.getText( ).toString( );
	if( account.isEmpty( ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_enter_account ) , Toast.LENGTH_SHORT ).show( );
	    return;
	}
	Intent intent = new Intent( );
	intent.setClass( LoginActivity.this , ZhangTaoActivity.class );
	intent.putExtra( Constants.USER_NAME , account );
	startActivityForResult( intent , 1 );
    }

    private final static int GET_ZT = 1;

    private Handler mUiHandler = new Handler( )
    {

	@Override
	public void handleMessage( Message msg )
	{
	    // TODO Auto-generated method stub
	    super.handleMessage( msg );

	    switch ( msg.what )
	    {
		case GET_ZT :
		    mZtTv.setText( (String)msg.obj );
		    break;

		default :
		    break;
	    }
	}

    };

    @Override
    public void onClick( View view )
    {
	switch ( view.getId( ) )
	{
	    case R.id.login_btn :
		login( );
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

    @Override
    protected void onActivityResult( int requestCode , int resultCode , Intent data )
    {
	super.onActivityResult( requestCode , resultCode , data );

	if( resultCode == RESULT_OK )
	{
	    String zt = data.getStringExtra( Constants.ZHANG_TAO );
	    if( !zt.isEmpty( ) )
	    {
		Message msg = Message.obtain( );
		msg.what = GET_ZT;
		msg.obj = zt;
		mUiHandler.sendMessage( msg );
	    }
	    else
	    {
		Logger.e( TAG , "账套为空" );
	    }

	}
    }

}
