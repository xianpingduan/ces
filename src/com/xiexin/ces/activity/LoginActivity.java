package com.xiexin.ces.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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

    private TextView mZtTv;

    //网络请求

    private RequestQueue mQueue;

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
	//	if( validate( ) )
	//	{
	doRequestLogin( );
	//	}
    }

    private void doRequestLogin()
    {
    	
    	JSONObject obj = new JSONObject();
    	try {
			obj.put("account", "web_Group");
			obj.put("userid", "000018");
			obj.put("kind", 1);
			obj.put("filter", "");
			obj.put("size", 1);
			obj.put("page", 1);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//    	http://core130.com:8081/api/CESApp/GetWorkMessage?account=web_Group&userid=000018&kind=1&filter=%20&size=10&page=1
	JsonObjectRequest json = new JsonObjectRequest( Method.POST , "http://core130.com:8081/api/CESApp/GetWorkMessage" , obj ,
		new Listener< JSONObject >( )
		{

		    @Override
		    public void onResponse( JSONObject response )
		    {
			Logger.d( TAG , "----response----" + response.toString( ) );

		    }
		} , new ErrorListener( )
		{

		    @Override
		    public void onErrorResponse( VolleyError error )
		    {
			Logger.d( TAG , "----e----" + error.getMessage( ) );
			Logger.d( TAG , "----e----" + error.getLocalizedMessage());

		    }
		} );
	mQueue.add( json );
	mQueue.start( );
    }
    
    
    private void getAccount(){

    	
//    	Map<String, String> map = new HashMap<String, String>();    
//    	map.put("userid", "000018");    
//    	JSONObject obj = new JSONObject(map);  
//    	JSONObject obj = new JSONObject();
//    	try {
//			obj.put("userid", "000018");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	http://core130.com:8081/api/CESApp/GetWorkMessage?account=web_Group&userid=000018&kind=1&filter=%20&size=10&page=1
	StringRequest json = new StringRequest( Method.POST , Constants.ROOT_URL+Constants.ZHANG_TAO_URL ,
		new Listener< String >( )
		{

		    @Override
		    public void onResponse( String response )
		    {
			Logger.d( TAG , "----response----" + response.toString( ) );
			
			Message msg = Message.obtain();
			msg.what= MSG_REQUEST_ZT_SUCCESS;
			msg.obj = response.toString();
			mUiHandler.sendMessage(msg);
			
		    }
		} , new ErrorListener( )
		{

		    @Override
		    public void onErrorResponse( VolleyError error )
		    {
			Logger.d( TAG , "----e----" + error.getMessage( ) );
		    }
		})
		{
//		@Override
//		public Map<String, String> getHeaders() throws AuthFailureError {
//            HashMap<String, String> headers = new HashMap<String, String>();
//            headers.put("Accept", "application/json");
//            headers.put("Content-Type","application/json; charset=UTF-8");
//            return headers;
//		}
		
		 @Override
		    protected Map<String, String> getParams() 
		    {  
		            Map<String, String>  params = new HashMap<String, String> ();  
		            params.put("userid", "000018");  
		            return params;  
		    }
				
		};
	mQueue.add( json );
	mQueue.start( );
    }
    

    private boolean validate()
    {
	String account = mLoginAccEt.getText( ).toString( );
	String pwd = mLoginPwdEt.getText( ).toString( );
	String zt = mZtTv.getText( ).toString( );
	if(account ==null || account.isEmpty( ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_enter_account ) , Toast.LENGTH_SHORT ).show( );
	    return false;
	}
	else if(pwd ==null || pwd.isEmpty( ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_enter_pwd ) , Toast.LENGTH_SHORT ).show( );
	    return false;
	}
	else if(zt==null|| zt.isEmpty( ) || zt.equals( getString( R.string.p_select_zt_text ) ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_select_zt ) , Toast.LENGTH_SHORT ).show( );
	    return false;
	}
	return true;
    }

    private void selectZt()
    {
	String account = mLoginAccEt.getText( ).toString( );
	if( account ==null || account.isEmpty( ) )
	{
	    Toast.makeText( mContext , getString( R.string.please_enter_account ) , Toast.LENGTH_SHORT ).show( );
	    return;
	}
	getAccount();
    }

    private final static int MSG_GET_ZT = 1;
    private final static int MSG_REQUEST_ZT_SUCCESS=2;

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
		case MSG_REQUEST_ZT_SUCCESS:
			Intent intent = new Intent( );
			intent.setClass( LoginActivity.this , ZhangTaoActivity.class );
			intent.putExtra( Constants.ZHANG_TAO_LIST , (String)msg.obj );
			startActivityForResult( intent , 1 );
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
		msg.what = MSG_GET_ZT;
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
