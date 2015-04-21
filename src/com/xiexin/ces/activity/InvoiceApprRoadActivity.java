package com.xiexin.ces.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.xiexin.ces.entry.InvoiceApprRoad;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.LoadingDialog;
import com.xiexin.ces.widgets.LoadingUIListView;

public class InvoiceApprRoadActivity extends Activity implements OnClickListener
{

    private final static String TAG = "InvoiceApprRoadActivity";

    // header start
    private LinearLayout mReturnLl;
    private FrameLayout mReturnIv;
    private TextView mReturnTv;
    private TextView mTitle;
    private Button mBtn1;
    private Button mBtn2;

    // header end

    private LoadingUIListView mListView;
    private InvoiceApprRoadAdapter mInvoiceApprRoadAdapter;

    private LoadingDialog mLoadingDialog;

    private String mConnName;// 账套信息
    private String mPrgid; // 业务类型
    private String mDatanbr;// 单据编号
    private String mApprList;

    private RequestQueue mQueue;

    public SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );

    private void dismissDialog()
    {

	if( mLoadingDialog != null && mLoadingDialog.isShowing( ) )
	{
	    mLoadingDialog.dismiss( );
	}

    }

    private void showDialog()
    {
	if( mLoadingDialog == null )
	{
	    mLoadingDialog = new LoadingDialog( this );
	}

	mLoadingDialog.show( );

    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_invoice_road );

	mQueue = Volley.newRequestQueue( App.getAppContext( ) );
	initView( );

	initData( );

    }

    private void initView()
    {

	// header start
	mReturnLl = (LinearLayout)findViewById( R.id.return_ll );
	mReturnIv = (FrameLayout)findViewById( R.id.return_iv );
	mReturnTv = (TextView)findViewById( R.id.return_tv );
	mTitle = (TextView)findViewById( R.id.title );
	mBtn1 = (Button)findViewById( R.id.btn1 );
	mBtn2 = (Button)findViewById( R.id.btn2 );
	// /header end

	// mInvoiceType = getIntent( ).getIntExtra( Constants.INVOICE_TYPE , 0
	// );
	mReturnTv.setText( getString( R.string.invoice_info ) );
	mTitle.setText( getString( R.string.invoice_approval_road ) );

	mBtn1.setVisibility( View.GONE );
	mBtn2.setVisibility( View.GONE );
	mReturnLl.setVisibility( View.VISIBLE );

	// mBtn1.setOnClickListener( this );
	// mBtn2.setOnClickListener( this );
	mReturnLl.setOnClickListener( this );

	mListView = (LoadingUIListView)findViewById( R.id.invoice_road_list );
	mListView.setFooterPullEnable( false );
	mListView.setHeaderPullEnable( false );

    }

    private void initData()
    {

	Intent intent = getIntent( );
	mPrgid = intent.getStringExtra( Constants.PRGID );
	mConnName = intent.getStringExtra( Constants.ZHANG_TAO_CONN_NAME );
	if( mConnName == null || mConnName.isEmpty( ) )
	{
	    mConnName = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );
	}
	mDatanbr = intent.getStringExtra( Constants.DATANBR );
	mApprList = intent.getStringExtra( Constants.APPR_LIST );

	if( mInvoiceApprRoadAdapter == null )
	    mInvoiceApprRoadAdapter = new InvoiceApprRoadAdapter( );
	mListView.setAdapter( mInvoiceApprRoadAdapter );

	if( mApprList == null || mApprList.isEmpty( ) )
	{
	    Logger.d( TAG , "requestInvoiceApprRoad" );
	    requestInvoiceApprRoad( );
	}
	else
	{
	    Logger.d( TAG , "setListAdapter" );
	    setListAdapter( );
	}
    }

    private void setListAdapter()
    {

	Message msg = Message.obtain( );
	msg.what = MSG_GET_INVOICE_ROAD_LIST_SUCCESS;
	msg.obj = mApprList;
	mUiHandler.sendMessage( msg );
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

    private ArrayList< InvoiceApprRoad > getInvoiceList( String jsonStr )
    {

	ArrayList< InvoiceApprRoad > invoiceList = new ArrayList< InvoiceApprRoad >( );
	try
	{
	    JSONArray arrays = new JSONArray( jsonStr );
	    for( int i = 0 ; i < arrays.length( ) ; i++ )
	    {

		JSONObject obj = arrays.getJSONObject( i );
		InvoiceApprRoad invoiceApprRoad = new InvoiceApprRoad( );
		invoiceApprRoad.setApprdate( obj.getString( "apprdate" ) );
		invoiceApprRoad.setApprmemo( obj.getString( "apprmemo" ) );
		invoiceApprRoad.setApprobj( obj.getString( "approbj" ) );
		invoiceApprRoad.setCategory( obj.getString( "category" ) );
		invoiceApprRoad.setCrtuser( obj.getString( "crtuser" ) );
		invoiceApprRoad.setDatanbr( obj.getString( "datanbr" ) );
		invoiceApprRoad.setId( obj.getInt( "id" ) );
		invoiceApprRoad.setInxnbr( obj.getInt( "inxnbr" ) );
		invoiceApprRoad.setKind( obj.getString( "kind" ) );
		invoiceApprRoad.setPragid( obj.getString( "pragid" ) );
		invoiceApprRoad.setProcessmode( obj.getString( "processmode" ) );
		invoiceApprRoad.setTitle( obj.getString( "title" ) );
		invoiceApprRoad.setApprobjname( obj.getString( "approbjname" ) );

		invoiceList.add( invoiceApprRoad );
	    }
	}
	catch ( JSONException e )
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace( );
	}
	return invoiceList;

    }

    private void requestInvoiceApprRoad()
    {

	showDialog( );
	StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) + Constants.GET_APPROVAL_ROAD_LIST + "?" );
	urlSbf.append( "account=" ).append( mConnName );
	urlSbf.append( "&prgid=" ).append( mPrgid );
	urlSbf.append( "&datanbr=" ).append( mDatanbr );

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
			msg.what = MSG_GET_INVOICE_ROAD_LIST_SUCCESS;
			msg.obj = response.getString( "data" );
		    }
		    else
		    {
			msg.what = MSG_GET_INVOICE_ROAD_LIST_ERROR;
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
		Logger.d( TAG , "----e----" + error.toString( ) );
		mUiHandler.sendEmptyMessage( MSG_GET_INVOICE_ROAD_LIST_ERROR );
	    }
	} );
	mQueue.add( json );
	mQueue.start( );

    }

    private static final int MSG_GET_INVOICE_ROAD_LIST_SUCCESS = 1;
    private static final int MSG_GET_INVOICE_ROAD_LIST_ERROR = 2;

    private Handler mUiHandler = new Handler( )
    {

	@Override
	public void handleMessage( Message msg )
	{
	    super.handleMessage( msg );

	    switch ( msg.what )
	    {
		case MSG_GET_INVOICE_ROAD_LIST_SUCCESS :
		    dismissDialog( );

		    String dataStr = (String)msg.obj;
		    ArrayList< InvoiceApprRoad > roads = getInvoiceList( dataStr );
		    if( roads.size( ) > 0 )
		    {
			mInvoiceApprRoadAdapter.addData( roads );
		    }
		    mInvoiceApprRoadAdapter.notifyDataSetChanged( );
		    break;

		case MSG_GET_INVOICE_ROAD_LIST_ERROR :
		    dismissDialog( );
		    Toast.makeText( App.getAppContext( ) , App.getAppContext( ).getString( R.string.request_appr_road_list_error ) , Toast.LENGTH_SHORT ).show( );
		    break;

		default :
		    break;
	    }
	}

    };

    private class InvoiceApprRoadAdapter extends BaseAdapter
    {

	private ArrayList< InvoiceApprRoad > list = new ArrayList< InvoiceApprRoad >( );

	private HashMap< String , Boolean > mMap = new HashMap< String , Boolean >( );

	public void addData( ArrayList< InvoiceApprRoad > data )
	{
	    list.clear( );
	    list.addAll( data );
	}

	@Override
	public int getCount()
	{
	    // TODO Auto-generated method stub
	    return list.size( );
	}

	@Override
	public Object getItem( int arg0 )
	{
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public long getItemId( int position )
	{
	    // TODO Auto-generated method stub
	    return position;
	}

	@Override
	public View getView( int position , View convertView , ViewGroup parent )
	{

	    ViewHolder holder;
	    if( convertView == null )
	    {
		convertView = App.getLayoutInflater( ).inflate( R.layout.activity_invoice_road_item , null );
		holder = new ViewHolder( );
		holder.apprDateTv = (TextView)convertView.findViewById( R.id.node_date_tv );
		holder.apprTimeTv = (TextView)convertView.findViewById( R.id.node_time_tv );
		holder.processModeTv = (TextView)convertView.findViewById( R.id.node_name_tv );
		holder.apprMemoTv = (TextView)convertView.findViewById( R.id.node_content_tv );
		holder.isFinishedCb = (CheckBox)convertView.findViewById( R.id.is_finished_cb );

		convertView.setTag( holder );
	    }
	    else
	    {

		holder = (ViewHolder)convertView.getTag( );
	    }

	    bindData( holder , list.get( position ) );

	    return convertView;
	}

	private void bindData( final ViewHolder holder , final InvoiceApprRoad iar )
	{

	    String apprDate = iar.getApprdate( );
	    String apprTime = "";
	    
	    if(!apprDate.isEmpty()){
	    	apprTime=apprDate.substring(apprDate.lastIndexOf("T")+1);
	    	if(apprTime.contains(".")){
		    	apprTime=apprTime.substring(0,apprTime.lastIndexOf("."));
	    	}
	    }
	    if( apprTime.equals( "00:00:00" ) )
	    {
		apprTime = "";
	    }
	    
	    Date date = new Date( );
	    try
	    {
		date = sdf.parse( apprDate );
		apprDate = sdf.format( date );
	    }
	    catch ( ParseException e )
	    {
		e.printStackTrace( );
		Logger.d( TAG , "date format error" );
		apprDate = "";
	    }
	    if( apprDate.equals( "0001-01-01" ) )
	    {
		apprDate = "";
	    }
	    
	  

	    Logger.d( TAG , "bindData,date =" + apprDate );
	    Logger.d( TAG , "bindData,apprTime =" + apprTime );

	    String apprObjName = iar.getApprobjname( );
	    if( apprObjName == null || apprObjName.equals( "null" ) )
	    {
		apprObjName = "";
	    }
	    holder.processModeTv.setText( apprObjName );
	    holder.apprDateTv.setText( apprDate );
	    holder.apprTimeTv.setText( apprTime );
	    String apprMemo = iar.getApprmemo( );
	    if( apprMemo == null || apprMemo.equals( "null" ) )
	    {
		apprMemo = "";
	    }
	    
	    Logger.d(TAG, apprMemo);
	    
	    holder.apprMemoTv.setTag( apprMemo );
	    holder.apprMemoTv.setText(apprMemo);
	    Logger.d( TAG , "apprDate=" + apprDate );
	    // String isFinished = iar.getStatus();
	    // Log.d(TAG,
	    // "connName="+zt.getConnName()+"checked="+mMap.get(zt.getConnName()));
	    if( !apprDate.isEmpty( ) )
	    {
		holder.isFinishedCb.setChecked( true );
		holder.processModeTv.setTextColor( Color.parseColor( "#0160fe" ) );
		holder.apprDateTv.setTextColor( Color.parseColor( "#0160fe" ) );
		holder.apprMemoTv.setTextColor( Color.parseColor( "#0160fe" ) );
		holder.apprTimeTv.setTextColor( Color.parseColor( "#0160fe" ) );
	    }
	    else
	    {
		holder.isFinishedCb.setChecked( false );
		holder.processModeTv.setTextColor( Color.parseColor( "#575757" ) );
		holder.apprDateTv.setTextColor( Color.parseColor( "#ababab" ) );
		holder.apprMemoTv.setTextColor( Color.parseColor( "#ababab" ) );
		holder.apprTimeTv.setTextColor( Color.parseColor( "#ababab" ) );
	    }
	}
    }

    class ViewHolder
    {
	TextView apprDateTv;
	TextView apprTimeTv;
	TextView processModeTv;
	TextView apprMemoTv;
	CheckBox isFinishedCb;
    }

    @Override
    public void onBackPressed()
    {
	super.onBackPressed( );
	finish( );
    }

}
