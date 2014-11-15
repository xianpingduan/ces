package com.xiexin.ces.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private CheckBox mShowCb;

    private TextView mZtTv;

    // 网络请求

    private RequestQueue mQueue;

    private String mUserId;
    private String mPwd;
    private String mConnName;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	// TODO Auto-generated method stub
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_login );
	mContext = this;
	initView( );

	mQueue = Volley.newRequestQueue( App.getAppContext( ) );

    }

    private void initData()
    {
	boolean remeberPwd = App.getSharedPreference( ).getBoolean( Constants.REMEBER_PWD , false );
	if( remeberPwd )
	{
	    String account = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );
	    //		String account = "web_001";
	    String userid = App.getSharedPreference( ).getString( Constants.USER_ID , "" );
	    String pwd = App.getSharedPreference( ).getString( Constants.PWD , "" );
	    String connName = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );

	    mZtTv.setTag( connName );
	    mZtTv.setText( account );
	    mLoginAccEt.setText( userid );
	    mLoginPwdEt.setText( pwd );
	}
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

	mShowCb = (CheckBox)findViewById( R.id.show_cb );
	mShowCb.setTag( true );

	mLogo.setOnClickListener( this );
	mLoginBtn.setOnClickListener( this );
	mSelectZtRl.setOnClickListener( this );
	mRemmenberPwdCb.setOnClickListener( this );
	mAutoLoginCb.setOnClickListener( this );
	mRemmenberPwdTv.setOnClickListener( this );
	mAutoLoginTv.setOnClickListener( this );
	mShowCb.setOnClickListener( this );

    }

    @Override
    protected void onResume()
    {
	super.onResume( );
	//初始化数据
	initData( );
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

	// account=web_001&userid=000018&password=123

	String account = mZtTv.getTag( ).toString( );
	//		String account = "web_001";
	String userid = mLoginAccEt.getText( ).toString( );
	String pwd = mLoginPwdEt.getText( ).toString( );

	// JSONObject obj = new JSONObject( );
	// try
	// {
	// obj.put( "account" , account );
	// obj.put( "userid" , userid );
	// obj.put( "pwd" , pwd );
	// }
	// catch ( JSONException e )
	// {
	// // TODO
	// e.printStackTrace( );
	// }
	// http://core130.com:8081/api/CESApp/GetWorkMessage?account=web_Group&userid=000018&kind=1&filter=%20&size=10&page=1

	StringBuffer urlSbf = new StringBuffer( Constants.ROOT_URL + Constants.LOGIN_URL + "?" );
	urlSbf.append( "account=" ).append( account );
	urlSbf.append( "&userid=" ).append( userid );
	urlSbf.append( "&password=" ).append( pwd );

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
			msg.what = MSG_LOGIN_SUCCESS;
			msg.obj = response.getString( "Data" );
		    }
		    else
		    {

			msg.what = MSG_LOGIN_ERROR;
			msg.obj = response.get( "Msg" );
		    }

		    mUiHandler.sendMessage( msg );
		}
		catch ( JSONException e )
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace( );
		}

	    }
	} , new ErrorListener( )
	{
	    @Override
	    public void onErrorResponse( VolleyError error )
	    {
		Logger.d( TAG , "----e----" + error.getMessage( ) );
	    }
	} );
	mQueue.add( json );
	mQueue.start( );
    }

    private void getAccount()
    {

	// Map<String, String> map = new HashMap<String, String>();
	// map.put("userid", "000018");
	// JSONObject obj = new JSONObject(map);
	// JSONObject obj = new JSONObject();
	// try {
	// obj.put("userid", "000018");
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// http://core130.com:8081/api/CESApp/GetWorkMessage?account=web_Group&userid=000018&kind=1&filter=%20&size=10&page=1

	String userid = mLoginAccEt.getText( ).toString( );
	StringBuffer urlSbf = new StringBuffer( Constants.ROOT_URL + Constants.ZHANG_TAO_URL + "?" );
	urlSbf.append( "userid=" ).append( userid );
	JsonObjectRequest json = new JsonObjectRequest( Method.GET , urlSbf.toString( ) , null , new Listener< JSONObject >( )
	{
	    @Override
	    public void onResponse( JSONObject response )
	    {
		Logger.d( TAG , "----response----" + response.toString( ) );

		// TODO
		try
		{
		    int resCode = response.getInt( "Success" );
		    if( resCode == 0 )
		    {
			App.getSharedPreference( ).edit( ).putString( Constants.ZHANG_TAO_LIST , response.getString( "Data" ) ).commit( );
			mUiHandler.sendEmptyMessage( MSG_REQUEST_ZT_SUCCESS );
		    }
		    else
		    {
			mUiHandler.sendEmptyMessage( MSG_REQUEST_ZT_ERROR );
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
		Logger.d( TAG , "----e----" + error.getMessage( ) );
		mUiHandler.sendEmptyMessage( MSG_REQUEST_ZT_ERROR );
	    }
	} );

	// { @Override
	// public Map<String, String> getHeaders() throws AuthFailureError {
	// HashMap<String, String> headers = new HashMap<String, String>();
	// headers.put("Accept", "application/json");
	// headers.put("Content-Type","application/json; charset=UTF-8");
	// return headers;
	// }
	// @Override
	// protected Map< String , String > getParams()
	// {
	// Map< String , String > params = new HashMap< String , String >( );
	// params.put( "userid" , "000018" );
	// return params;
	// }}

	mQueue.add( json );
	mQueue.start( );
    }

    private boolean validate()
    {
	String account = mLoginAccEt.getText( ).toString( );
	String pwd = mLoginPwdEt.getText( ).toString( );
	String zt = mZtTv.getText( ).toString( );
	if( account == null || account.isEmpty( ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_enter_account ) , Toast.LENGTH_SHORT ).show( );
	    return false;
	}
	else if( pwd == null || pwd.isEmpty( ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_enter_pwd ) , Toast.LENGTH_SHORT ).show( );
	    return false;
	}
	else if( zt == null || zt.isEmpty( ) || zt.equals( getString( R.string.p_select_zt_text ) ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_select_zt ) , Toast.LENGTH_SHORT ).show( );
	    return false;
	}
	return true;
    }

    private void selectZt()
    {
	String account = mLoginAccEt.getText( ).toString( );
	if( account == null || account.isEmpty( ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_enter_account ) , Toast.LENGTH_SHORT ).show( );
	    return;
	}
	getAccount( );
    }

    private final static int MSG_GET_ZT = 1;
    private final static int MSG_REQUEST_ZT_SUCCESS = 2;
    private final static int MSG_REQUEST_ZT_ERROR = 3;

    private static final int MSG_LOGIN_SUCCESS = 4;
    private static final int MSG_LOGIN_ERROR = 5;

    private Handler mUiHandler = new Handler( )
    {

	@Override
	public void handleMessage( Message msg )
	{
	    // TODO Auto-generated method stub
	    super.handleMessage( msg );

	    switch ( msg.what )
	    {
		case MSG_GET_ZT :
		    mZtTv.setText( (String)msg.obj );
		    break;
		case MSG_REQUEST_ZT_SUCCESS :
		    Intent intent = new Intent( );
		    intent.setClass( LoginActivity.this , ZhangTaoActivity.class );
		    startActivityForResult( intent , 1 );
		    break;
		case MSG_REQUEST_ZT_ERROR :
		    Toast.makeText( LoginActivity.this , getString( R.string.get_accout_error ) , Toast.LENGTH_SHORT ).show( );
		    break;
		case MSG_LOGIN_ERROR :
		    Toast.makeText( LoginActivity.this , (String)msg.obj , Toast.LENGTH_SHORT ).show( );
		    break;
		case MSG_LOGIN_SUCCESS :

		    String loginInfo = (String)msg.obj;
		    saveLoginInfo( loginInfo );

		    intentToMain( );

		    break;
		default :
		    break;
	    }
	}

    };

    private void saveLoginInfo( String loginInfo )
    {

	try
	{
	    JSONObject object = new JSONObject( loginInfo );
	    mUserId = object.getString( "UserID" );
	    mPwd = object.getString( "Password" );

	    String depart = object.getString( "Depart" );
	    String title = object.getString( "Title" );
	    String job = object.getString( "Job" );
	    boolean locked = object.getBoolean( "Locked" );

	    //保存数据
	    App.getSharedPreference( ).edit( ).putString( Constants.USER_ID , mUserId ).commit( );
	    App.getSharedPreference( ).edit( ).putString( Constants.PWD , mPwd ).commit( );
	    App.getSharedPreference( ).edit( ).putString( Constants.DEPART , depart ).commit( );
	    App.getSharedPreference( ).edit( ).putString( Constants.TITLE , title ).commit( );
	    App.getSharedPreference( ).edit( ).putString( Constants.JOB , job ).commit( );
	    App.getSharedPreference( ).edit( ).putBoolean( Constants.LOCKED , locked ).commit( );
	    App.getSharedPreference( ).edit( ).putString( Constants.ZHANG_TAO_ACCINFO , mZtTv.getText( ).toString( ) ).commit( );
	    App.getSharedPreference( ).edit( ).putString( Constants.ZHANG_TAO_CONN_NAME , mZtTv.getTag( ).toString( ) ).commit( );

	    if( mRemmenberPwdCb.isChecked( ) )
	    {
		App.getSharedPreference( ).edit( ).putBoolean( Constants.REMEBER_PWD , true ).commit( );
	    }
	    if( mAutoLoginCb.isChecked( ) )
	    {
		App.getSharedPreference( ).edit( ).putBoolean( Constants.AUTO_LOGIN , true ).commit( );
	    }

	}
	catch ( JSONException e )
	{
	    e.printStackTrace( );
	}
    }

    private void intentToMain()
    {

	Intent intent = new Intent( );
	intent.setClass( LoginActivity.this , MenuActivity.class );
	startActivity( intent );
	finish( );
    }

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
		rememberPwd( );
		break;
	    case R.id.auto_login_tv :
		rememberPwd( );
		break;
	    case R.id.remmenber_pwd_cb :
		autoLogin( );
		break;
	    case R.id.remmenber_pwd_tv :
		autoLogin( );
		break;
	    case R.id.logo :

		break;
	    case R.id.show_cb :
		showPwd( view );
		break;
	    default :
		break;
	}
    }

    private void rememberPwd()
    {
	if( !mRemmenberPwdCb.isChecked( ) )
	{
	    mRemmenberPwdCb.setChecked( false );
	}
	else
	{
	    mRemmenberPwdCb.setChecked( true );
	}
    }

    private void autoLogin()
    {
	if( !mAutoLoginCb.isChecked( ) )
	{
	    mAutoLoginCb.setChecked( false );
	    mRemmenberPwdCb.setClickable( true );
	    mRemmenberPwdTv.setClickable( true );
	}
	else
	{
	    mAutoLoginCb.setChecked( true );
	    mRemmenberPwdCb.setChecked( true );
	    mRemmenberPwdCb.setClickable( false );
	    mRemmenberPwdTv.setClickable( false );
	}
    }

    private void showPwd( View v )
    {
	boolean hidePwd = (Boolean)v.getTag( );
	if( hidePwd )
	{
	    mLoginPwdEt.setTransformationMethod( HideReturnsTransformationMethod.getInstance( ) );
	    mShowCb.setChecked( true );
	}
	else
	{
	    mLoginPwdEt.setTransformationMethod( PasswordTransformationMethod.getInstance( ) );
	    mShowCb.setChecked( false );
	}
	mLoginPwdEt.setSelection( mLoginPwdEt.getText( ).toString( ).length( ) );
	v.setTag( !hidePwd );
    }

    private void setServerConfig()
    {

    }

    @Override
    protected void onActivityResult( int requestCode , int resultCode , Intent data )
    {
	super.onActivityResult( requestCode , resultCode , data );

	if( resultCode == RESULT_OK )
	{
	    String ztConnName = data.getStringExtra( Constants.ZHANG_TAO_CONN_NAME );
	    String ztAccInfo = data.getStringExtra( Constants.ZHANG_TAO_ACCINFO );
	    if( !ztConnName.isEmpty( ) )
	    {
		mZtTv.setTag( ztConnName );

		Message msg = Message.obtain( );
		msg.what = MSG_GET_ZT;
		msg.obj = ztAccInfo;
		mUiHandler.sendMessage( msg );

	    }
	    else
	    {
		Logger.e( TAG , "账套为空" );
	    }

	}
    }

}
