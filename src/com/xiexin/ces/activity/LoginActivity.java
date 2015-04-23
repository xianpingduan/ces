package com.xiexin.ces.activity;

import org.json.JSONArray;
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
import com.xiexin.ces.update.SelfUpgrade;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.LoadingDialog;

public class LoginActivity extends Activity implements OnClickListener
{

    public final static String TAG = "LoginActivity";
    private Context mContext;

    private ImageView mLogo;
    private EditText mLoginAccEt;
    private EditText mLoginPwdEt;
    private Button mLoginBtn;
    private RelativeLayout mSelectZtRl;
    private ImageView mZtSelectIv;
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

    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	// TODO Auto-generated method stub
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_login_xiexin );
	mContext = this;
	initView( );

	mQueue = Volley.newRequestQueue( App.getAppContext( ) );

	//初始化数据
	//修复bug(勾选记住密码后换个登陆人选择帐套没有读取到帐套信息，而且从选择帐套页面回到登录页面后登陆人也变成了记住的用户)
	initData( );

    }

    private void initData()
    {
	boolean remeberPwd = App.getSharedPreference( ).getBoolean( Constants.REMEBER_PWD , false );
	if( remeberPwd )
	{
	    String account = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_ACCINFO , "" );
	    // String account = "web_001";
	    String userid = App.getSharedPreference( ).getString( Constants.USER_ID , "" );
	    String pwd = App.getSharedPreference( ).getString( Constants.PWD , "" );
	    String connName = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );

	    boolean rememberPwd = App.getSharedPreference( ).getBoolean( Constants.REMEBER_PWD , false );
	    boolean autoLogin = App.getSharedPreference( ).getBoolean( Constants.AUTO_LOGIN , false );

	    mZtTv.setTag( connName );
	    mZtTv.setText( account );
	    mLoginAccEt.setText( userid );
	    mLoginPwdEt.setText( pwd );

	    mRemmenberPwdCb.setChecked( rememberPwd );
	    mAutoLoginCb.setChecked( autoLogin );
	}
	
	//检测更新
	checkUpdate(Constants.CHECK_UPDATE_AUTO);
	
    }

    private void initView()
    {
	mLogo = (ImageView)findViewById( R.id.logo );
	mLoginAccEt = (EditText)findViewById( R.id.login_account_et );
	mLoginPwdEt = (EditText)findViewById( R.id.login_pwd_et );
	mLoginBtn = (Button)findViewById( R.id.login_btn );
	mSelectZtRl = (RelativeLayout)findViewById( R.id.zt_select_rl );
	mZtSelectIv = (ImageView)findViewById( R.id.zt_select_iv );
	mRemmenberPwdCb = (CheckBox)findViewById( R.id.remmenber_pwd_cb );
	mAutoLoginCb = (CheckBox)findViewById( R.id.auto_login_cb );
	mRemmenberPwdTv = (TextView)findViewById( R.id.remmenber_pwd_tv );
	mAutoLoginTv = (TextView)findViewById( R.id.auto_login_tv );

	mZtTv = (TextView)findViewById( R.id.zt_tv );

	mShowCb = (CheckBox)findViewById( R.id.show_cb );
	mShowCb.setTag( true );

	mLogo.setOnClickListener( this );
	mLoginBtn.setOnClickListener( this );
	mZtSelectIv.setOnClickListener( this );
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
	//	 初始化数据
	//	initData( );
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
	showDialog( getString( R.string.logining ) );

	String account = mZtTv.getTag( ).toString( );
	// String account = "web_001";
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

	Logger.d( TAG , "App.getRootUrl( )=" + App.getRootUrl( ) );
	Logger.d( TAG , "account=" + account );

	StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) + Constants.LOGIN_URL + "?" );
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
		    int resCode = response.getInt( "success" );
		    Message msg = Message.obtain( );
		    if( resCode == 0 )
		    {
			msg.what = MSG_LOGIN_SUCCESS;
			msg.obj = response.getString( "data" );
		    }
		    else
		    {
			msg.what = MSG_LOGIN_ERROR;
			msg.obj = response.get( "msg" );
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
		mUiHandler.sendEmptyMessage( MSG_LOGIN_NET_ERROR );
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

	showDialog( getString( R.string.loading_tip_text ) );

	String userid = mLoginAccEt.getText( ).toString( );
	StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) + Constants.ZHANG_TAO_URL + "?" );
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
		    int resCode = response.getInt( "success" );
		    if( resCode == 0 )
		    {
			String rspData = response.getString( "data" );
			boolean b = validateRequestZt( rspData );
			if( b )
			{
			    App.getSharedPreference( ).edit( ).putString( Constants.ZHANG_TAO_LIST , response.getString( "data" ) ).commit( );
			    mUiHandler.sendEmptyMessage( MSG_REQUEST_ZT_SUCCESS );
			}
			else
			{
			    mUiHandler.sendEmptyMessage( MSG_REQUEST_ZT_NULL );
			}
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

    private boolean validateRequestZt( String requestZt )
    {

	try
	{
	    JSONArray array = new JSONArray( requestZt );
	    if( array.length( ) > 0 )
	    {
		return true;
	    }
	}
	catch ( JSONException e )
	{
	    e.printStackTrace( );
	    return false;
	}
	return false;

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
	boolean rememberPwd = App.getSharedPreference( ).getBoolean( Constants.REMEBER_PWD , false );

	if( rememberPwd )
	{
	    String userId = App.getSharedPreference( ).getString( Constants.USER_ID , "" );
	    if( !account.equals( userId ) )
	    {
		getAccount( );
	    }
	    else
	    {
		Intent intent = new Intent( );
		intent.setClass( LoginActivity.this , ZhangTaoActivity.class );
		// intent.putExtra(Constants.SERVER_CONFIG_SET_FROM,
		// Constants.SERVER_CONFIG_SET_FROM_LOGIN);
		startActivityForResult( intent , 1 );
	    }
	}
	else
	{
	    getAccount( );
	}

    }

    private final static int MSG_GET_ZT = 1;
    private final static int MSG_REQUEST_ZT_SUCCESS = 2;
    private final static int MSG_REQUEST_ZT_ERROR = 3;
    private final static int MSG_REQUEST_ZT_NULL = 7;

    private static final int MSG_LOGIN_SUCCESS = 4;
    private static final int MSG_LOGIN_ERROR = 5;

    private static final int MSG_LOGIN_NET_ERROR = 6;

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
		    dismissDialog( );
		    Intent intent = new Intent( );
		    intent.setClass( LoginActivity.this , ZhangTaoActivity.class );
		    startActivityForResult( intent , 1 );
		    break;
		case MSG_REQUEST_ZT_ERROR :
		    dismissDialog( );
		    Toast.makeText( LoginActivity.this , getString( R.string.get_accout_error ) , Toast.LENGTH_SHORT ).show( );
		    break;
		case MSG_LOGIN_ERROR :
		    dismissDialog( );
		    Toast.makeText( LoginActivity.this , (String)msg.obj , Toast.LENGTH_SHORT ).show( );
		    break;
		case MSG_LOGIN_SUCCESS :
		    dismissDialog( );
		    String loginInfo = (String)msg.obj;
		    saveLoginInfo( loginInfo );
		    intentToMain( );
		    break;
		case MSG_LOGIN_NET_ERROR :
		    dismissDialog( );
		    Toast.makeText( LoginActivity.this , getString( R.string.please_check_net ) , Toast.LENGTH_SHORT ).show( );
		    break;
		case MSG_REQUEST_ZT_NULL :
		    dismissDialog( );
		    Toast.makeText( LoginActivity.this , getString( R.string.request_zhangtao_null ) , Toast.LENGTH_SHORT ).show( );
		    break;
		default :
		    break;
	    }
	}

    };

    private void dismissDialog()
    {
	if( mLoadingDialog != null && mLoadingDialog.isShowing( ) )
	{
	    mLoadingDialog.dismiss( );
	}
    }

    private void showDialog( String tip )
    {
	if( mLoadingDialog == null )
	{
	    mLoadingDialog = new LoadingDialog( mContext );
	}
	mLoadingDialog.setTitle( tip );
	mLoadingDialog.show( );
    }

    private boolean mConnIsChanged = false;

    private void saveLoginInfo( String loginInfo )
    {
    	
	try
	{
	    JSONObject object = new JSONObject( loginInfo );
	    mUserId = object.getString( "userid" );
	    mPwd = mLoginPwdEt.getText( ).toString( );

	    String mPwdMd5 = object.getString( "password" );
	    String depart = object.getString( "depart" );
	    if( "null".equals( depart ) )
	    {
		depart = "";
	    }
	    String title = object.getString( "title" );
	    String job = object.getString( "job" );
	    if( "null".equals( job ) )
	    {
		job = "";
	    }
	    String userName = object.getString( "username" );
	    boolean locked = object.getBoolean( "locked" );

	    Logger.d( TAG , "userName=" + userName + ",depart=" + depart );

	    String cacheConn = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );
	    String currConn = mZtTv.getTag( ).toString( );

	    if( !currConn.equals( cacheConn ) )
	    {
		mConnIsChanged = true;
	    }
	    // 保存数据
	    App.getSharedPreference( ).edit( ).putString( Constants.USER_ID , mUserId ).commit( );
	    App.getSharedPreference( ).edit( ).putString( Constants.USER_NAME , userName == null ? "" : userName ).commit( );
	    App.getSharedPreference( ).edit( ).putString( Constants.PWD , mPwd ).commit( );
	    App.getSharedPreference( ).edit( ).putString( Constants.PWD_MD5 , mPwdMd5 ).commit( );
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
	Logger.d( TAG , "mConnIsChanged=" + mConnIsChanged );
	intent.putExtra( Constants.CONN_CHANGED , mConnIsChanged );
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
	    case R.id.zt_select_iv :
		selectZt( );
		break;
	    case R.id.auto_login_cb :
		autoLogin( );
		break;
	    case R.id.auto_login_tv :
		autoLogin( );
		break;
	    case R.id.remmenber_pwd_cb :
		rememberPwd( );
		break;
	    case R.id.remmenber_pwd_tv :
		rememberPwd( );
		break;
	    case R.id.logo :
		setServerConfig( );
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
	    mRemmenberPwdCb.setChecked( false );
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
	Intent intent = new Intent( );
	intent.setClass( LoginActivity.this , ServerConfigActivity.class );
	intent.putExtra( Constants.SERVER_CONFIG_REQ , 1 );
	startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult( int requestCode , int resultCode , Intent data )
    {
	super.onActivityResult( requestCode , resultCode , data );

	Logger.d(TAG, "onActivityResult ,reqeustCode="+requestCode + ",resultCode="+resultCode);
	
	if( resultCode == RESULT_OK && requestCode==1)
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
	}else if (resultCode == RESULT_OK && requestCode==2){
		boolean serverConfigChanged = data.getBooleanExtra(Constants.SERVER_CONFIG_CHANGED, false);
		Logger.d(TAG, "serverConfigChanged="+serverConfigChanged);
		if(serverConfigChanged){
			checkUpdate(Constants.CHECK_UPDATE_AUTO);
		}
	}
    }
    
    
	private void checkUpdate(final int type) {
		
		mUiHandler.postDelayed(new Runnable() {

			@Override
			public void run() {

				SelfUpgrade.getInstance(LoginActivity.this).startUpgrade(type);

			}
		}, 500);
	}

}
