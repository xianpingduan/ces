package com.xiexin.ces.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class MessageInfoActivity extends Activity implements OnClickListener
{

    private final static String TAG = "MessageInfoActivity";

    private String mMegContent;
    private int mMsgId;
    private String mMsgTitle;
    private int mMsgType;

    // header start
    private LinearLayout mReturnLl;
    private ImageView mReturnIv;
    private TextView mReturnTv;
    private TextView mTitle;
    private Button mBtn1;
    private Button mBtn2;

    // header end

    private TextView mMsgTitleTv;
    private TextView mMsgContentTv;

    private RequestQueue mQueue;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );

	setContentView( R.layout.activity_message );
	mQueue = Volley.newRequestQueue( App.getAppContext( ) );
	initView( );

	initData( );
    }

    private void initView()
    {
	// header start
	mReturnLl = (LinearLayout)findViewById( R.id.return_ll );
	mReturnIv = (ImageView)findViewById( R.id.return_iv );
	mReturnTv = (TextView)findViewById( R.id.return_tv );
	mTitle = (TextView)findViewById( R.id.title );
	mBtn1 = (Button)findViewById( R.id.btn1 );
	mBtn2 = (Button)findViewById( R.id.btn2 );
	// /header end

	mReturnIv.setVisibility( View.VISIBLE );

	mTitle.setText( getString( R.string.msg_center ) );
	mBtn1.setVisibility( View.GONE );

	mReturnLl.setOnClickListener( this );

	mMsgTitleTv = (TextView)findViewById( R.id.msg_title_tv );
	mMsgContentTv = (TextView)findViewById( R.id.msg_content_tv );

    }

    private void initData()
    {
	Intent intent = getIntent( );
	mMegContent = intent.getStringExtra( "content" );
	mMsgId = intent.getIntExtra( "id" , 0 );
	mMsgTitle = intent.getStringExtra( "title" );
	mMsgType =intent.getIntExtra("msgtype", 0);

	mMsgTitleTv.setText( mMsgTitle );
	mMsgContentTv.setText( mMegContent );

	doMsgRead( );

    }

    private void doMsgRead()
    {
	StringBuffer urlSbf = new StringBuffer( Constants.ROOT_URL + Constants.SET_MESSAGE_READ + "?" );

	String account = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );
	String userid = App.getSharedPreference( ).getString( Constants.USER_ID , "" );
	urlSbf.append( "account=" ).append( account );
	urlSbf.append( "&userid=" ).append( userid );
	urlSbf.append( "&id=" ).append( mMsgId );
	urlSbf.append( "&msgtype=" ).append( mMsgType );
	JsonObjectRequest json = new JsonObjectRequest( Method.GET , urlSbf.toString( ) , null , new Listener< JSONObject >( )
	{
	    @Override
	    public void onResponse( JSONObject response )
	    {
		try
		{
		    int resCode = response.getInt( "Success" );
		    if( resCode == 0 )
		    {
			Logger.d( TAG , response.getString( "Data" ) );
		    }
		    else
		    {
			Logger.d( TAG , response.getString( "Msg" ) );
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
		Logger.d( TAG , "onErrorResponse:" + error.getMessage( ) );
	    }
	} );
	mQueue.add( json );
	mQueue.start( );
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

    @Override
    public void onClick( View v )
    {
	switch ( v.getId( ) )
	{
	    case R.id.return_ll :
		onBackPressed( );
		break;
	    default :
		break;
	}
    }

    @Override
    public void onBackPressed()
    {
	super.onBackPressed( );
	finish( );
    }
}
