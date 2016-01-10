package com.xiexin.ces.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.update.ResourceUtil;
import com.xiexin.ces.update.SelfUpdateCommonDialog;
import com.xiexin.ces.utils.Logger;

//服务器配置信息请求转到此

public class ServerConfigActivity extends Activity implements OnClickListener
{

    public final static String TAG = "ServerConfigActivity";
    private EditText mServerConfigUrlEt;
    private EditText mServerConfigPortEt;
    private Context mContext;

    private boolean isFirstIn = true;

    // header start
    private LinearLayout mReturnLl;
    private FrameLayout mReturnIv;
    private ImageView mReturnIv2;
    private TextView mReturnTv;
    private TextView mTitle;
    private Button mBtn1;
    private Button mBtn2;
    
    private  boolean serverConfigChanged =false;

    // header end

    private int mRequest = 0;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	// TODO Auto-generated method stub
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_server_config );
	mContext = this;
	initView( );

	isFirstIn = App.getSharedPreference( ).getBoolean( Constants.SERVER_CONFIG_FIRST_IN , true );

	Logger.d( TAG , "isFirstIn=" + isFirstIn );

	if( !isFirstIn )
	{
	    mRequest = getIntent( ).getIntExtra( Constants.SERVER_CONFIG_REQ , 0 );
	    Logger.d( TAG , "mRequest=" + mRequest );
	    if( mRequest == 1 )
	    {
		initData( );
	    }
	}

    }

    private void initView()
    {
	mServerConfigUrlEt = (EditText)findViewById( R.id.server_config_url_et );
	mServerConfigPortEt = (EditText)findViewById( R.id.server_config_port_et );

	// header start
	mReturnLl = (LinearLayout)findViewById( R.id.return_ll );
	mReturnIv = (FrameLayout)findViewById( R.id.return_iv );
	mReturnIv2 = (ImageView) findViewById(R.id.return_iv_2);
	mReturnTv = (TextView)findViewById( R.id.return_tv );
	mTitle = (TextView)findViewById( R.id.title );
	mBtn1 = (Button)findViewById( R.id.btn1 );
	mBtn2 = (Button)findViewById( R.id.btn2 );
	// /header end

	if( isFirstIn )
	{
		mReturnIv.setVisibility(View.VISIBLE);
		mReturnTv.setVisibility(View.GONE);
		mReturnIv.setClickable(false);
		mReturnIv2.setImageResource(R.drawable.title_bar_menu);
	}
	else
	{
		mReturnTv.setVisibility(View.VISIBLE);
	    mReturnIv.setVisibility( View.VISIBLE );
		mReturnIv.setClickable(true);
		mReturnIv2.setImageResource(R.drawable.ab_btn_return);

	}
	

	mTitle.setText( getString( R.string.server_config ) );
	mBtn1.setVisibility( View.VISIBLE );
	mBtn1.setText( getString( R.string.finish_to ) );

	mBtn1.setOnClickListener( this );

	//		mServerConfigUrlEt.setText("http://");
	mServerConfigUrlEt.setSelection( mServerConfigUrlEt.getText( ).toString( ).length( ) );
	mServerConfigPortEt.setSelection( mServerConfigPortEt.getText( ).toString( ).length( ) );

    }

    private void initData()
    {
	String url = App.getSharedPreference( ).getString( Constants.SERVER_CONFIG_URL , "" );
	String port = App.getSharedPreference( ).getString( Constants.SERVER_CONFIG_PORT , "" );

	mServerConfigUrlEt.setText( url );
	mServerConfigUrlEt.setSelection( mServerConfigUrlEt.getText( ).toString( ).length( ) );
	mServerConfigPortEt.setText( port );
    }

    private void saveServerConfig()
    {
	Log.d( TAG , "saveServerConfig,url=" + url + ",port=" + port );
	App.getSharedPreference( ).edit( ).putString( Constants.SERVER_CONFIG_URL , url ).commit( );
	App.getSharedPreference( ).edit( ).putString( Constants.SERVER_CONFIG_PORT , port ).commit( );
	App.getSharedPreference( ).edit( ).putBoolean( Constants.SERVER_CONFIG_FIRST_IN , false ).commit( );

	//设置服务器路径
	//Constants.setRootPath( );
    }

    private String url = null;
    private String port = null;

    private boolean validate()
    {

	url = mServerConfigUrlEt.getText( ).toString( );
	if( !url.startsWith( "http://" ) && !url.startsWith( "https://" ) )
	{
	    url = "http://" + url;
	}
	port = mServerConfigPortEt.getText( ).toString( );
	Log.d( TAG , "url=" + url );
	if( url == null || url.equals( "http://" ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_enter_server_config_url_hint ) , Toast.LENGTH_SHORT ).show( );
	    return false;
	}
	else if( port == null || port.isEmpty( ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_enter_server_config_port_hint ) , Toast.LENGTH_SHORT ).show( );
	    return false;
	}

	return true;
    }

    private void showConfirmDialog()
    {
        final SelfUpdateCommonDialog mUpgradeDialog = new SelfUpdateCommonDialog(mContext, "", getString(R.string.server_config_change_msg));
        mUpgradeDialog.setmRightBtnTitle(mContext.getString(ResourceUtil.getStringId(mContext, "sure")));
        mUpgradeDialog.setmLeftBtnTitle(mContext.getString(ResourceUtil.getStringId(mContext, "cancel")));
        mUpgradeDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); // 全局dialog
        mUpgradeDialog.addRightBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpgradeDialog.dismiss();
                serverConfigChanged=true;
                doRequestServerConfigInfo( );
            }
        });
        mUpgradeDialog.addLeftBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUpgradeDialog != null)
                    mUpgradeDialog.dismiss();
            }
        });
        mUpgradeDialog.show();
        mUpgradeDialog.setTipTextViewGrivity();

//	AlertDialog.Builder builder = new Builder( ServerConfigActivity.this );
//	builder.setMessage( getString( R.string.server_config_change_msg ) );
//	builder.setTitle( null );
//	builder.setPositiveButton( getString( R.string.sure ) , new DialogInterface.OnClickListener( )
//	{
//	    @Override
//	    public void onClick( DialogInterface dialog , int which )
//	    {
//		dialog.dismiss( );
//		serverConfigChanged=true;
//		doRequestServerConfigInfo( );
//	    }
//	} );
//	builder.setNegativeButton( getString( R.string.cancel ) , new DialogInterface.OnClickListener( )
//	{
//	    @Override
//	    public void onClick( DialogInterface dialog , int which )
//	    {
//		dialog.dismiss( );
//	    }
//	} );
//	builder.create( ).show( );
    }

    // 跳转到首页
    private void intentTo()
    {
	if( mRequest == 1 )
	{
		Logger.d(TAG, "setResult,serverConfigChanged="+serverConfigChanged);
	    Intent intent = new Intent( );
//	    intent.setClass( ServerConfigActivity.this , LoginActivity.class );
	    intent.putExtra(Constants.SERVER_CONFIG_CHANGED, serverConfigChanged);
	    setResult(RESULT_OK,intent);
	}
	else
	{
//		Logger.d(TAG, "setResult,serverConfigChanged="+serverConfigChanged);
	    Intent intent = new Intent( );
	    intent.setClass( ServerConfigActivity.this , LoginActivity.class );
//	    intent.putExtra(Constants.SERVER_CONFIG_CHANGED, serverConfigChanged);
//	    setResult(RESULT_OK,intent);
	    startActivity( intent );
	}
	finish( );
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
    public void onClick( View v )
    {
	switch ( v.getId( ) )
	{
	    case R.id.btn1 :

		boolean validate = validate( );
		Logger.d( TAG , "validate=" + validate + ",isFirstIn=" + isFirstIn );
		if( validate && !isFirstIn )
		{
		    showConfirmDialog( );
		    
		}
		else if( validate && isFirstIn )
		{
		    doRequestServerConfigInfo( );
		    // TODO
		}
		break;
	    default :
		break;
	}
    }

    // 加载服务器信息
    private void doRequestServerConfigInfo()
    {
	mUiHandler.sendEmptyMessage( MSG_GET_SERVER_CONFOG_SUCCESS );
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
		    saveServerConfig( );
		    if( mRequest == 1 )
		    {
			App.clear( );

		    }
		    intentTo( );
		    break;
		case MSG_GET_SERVER_CONFOG_ERROR :

		    break;
		default :
		    break;
	    }
	}

    };

    @Override
    public void onBackPressed()
    {
	super.onBackPressed( );
	finish( );
    }
}
