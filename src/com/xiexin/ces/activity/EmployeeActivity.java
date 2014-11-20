package com.xiexin.ces.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.xiexin.ces.entry.Employee;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.LoadingDialog;
import com.xiexin.ces.widgets.LoadingUIListView;

public class EmployeeActivity extends Activity implements OnClickListener
{

    private final static String TAG = "EmployeeActivity";

    // header start
    private LinearLayout mReturnLl;
    private ImageView mReturnIv;
    private TextView mReturnTv;
    private TextView mTitle;
    private Button mBtn1;
    private Button mBtn2;

    // header end

    private LoadingUIListView mListView;
    private EmployeeAdapter mEmployeeAdapter;

    private LoadingDialog mLoadingDialog;

    private String mConnName;// 账套信息

    private RequestQueue mQueue;

    private String mEmployeeCheckId;

    private EditText mSearchEt;
    private Button mSearchBtn;

    private int mFromHanler;

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
	setContentView( R.layout.activity_employee );

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

	// mInvoiceType = getIntent( ).getIntExtra( Constants.INVOICE_TYPE , 0
	// );
	mReturnTv.setText( getString( R.string.invoice_info ) );
	mTitle.setText( getString( R.string.please_select_employee ) );

	mBtn1.setVisibility( View.VISIBLE );
	mBtn1.setText( getString( R.string.finish_to ) );
	mBtn2.setVisibility( View.GONE );
	mReturnLl.setVisibility( View.VISIBLE );

	mBtn1.setOnClickListener( this );
	// mBtn2.setOnClickListener( this );
	mReturnLl.setOnClickListener( this );

	mListView = (LoadingUIListView)findViewById( R.id.employee_list );
	mListView.setFooterPullEnable( false );
	mListView.setHeaderPullEnable( false );

	mSearchEt = (EditText)findViewById( R.id.search_et );

	mSearchEt.addTextChangedListener( new TextWatcher( )
	{
	    @Override
	    public void onTextChanged( CharSequence s , int start , int before , int count )
	    {
		Logger.d( TAG , "Text [" + s + "]" );
		String filter = s.toString( );
		if( filter == null || filter.isEmpty( ) )
		{
		    filter = filter.trim( );
		    mEmployeeAdapter.getFilter( ).filter( filter );
		}
	    }

	    @Override
	    public void beforeTextChanged( CharSequence s , int start , int count , int after )
	    {

	    }

	    @Override
	    public void afterTextChanged( Editable s )
	    {}
	} );
	mSearchBtn = (Button)findViewById( R.id.search_btn );
	mSearchBtn.setVisibility( View.GONE );
	mSearchBtn.setOnClickListener( this );

    }

    private void initData()
    {

	Intent intent = getIntent( );
	mConnName = intent.getStringExtra( Constants.ZHANG_TAO_CONN_NAME );
	mFromHanler = intent.getIntExtra( Constants.CHECK_EMPLOYEE_FROM , 0 );
	if( mConnName == null || mConnName.isEmpty( ) )
	{
	    mConnName = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );
	}

	if( mEmployeeAdapter == null )
	    mEmployeeAdapter = new EmployeeAdapter( );
	mListView.setAdapter( mEmployeeAdapter );

	requestEmployees( "" );
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
	    case R.id.btn1 :
		setResult( );
		break;
	    case R.id.search_btn :
		doSearch( );
		break;
	    default :
		break;
	}

    }

    //
    private void setResult()
    {
	Intent in = new Intent( );
	// in.putExtra(Constants.ZHANG_TAO_CONN_NAME, mCheckConnName);
	// in.putExtra(Constants.ZHANG_TAO_ACCINFO, mCheckAccInfo);
	setResult( RESULT_OK , in );
	finish( );
    }

    private void doSearch()
    {

	String filter = mSearchEt.getText( ).toString( );

	if( filter == null || filter.isEmpty( ) )
	{
	    Toast.makeText( App.getAppContext( ) , App.getAppContext( ).getString( R.string.please_enter_employee_name ) , Toast.LENGTH_SHORT ).show( );
	    return;
	}
	else
	{
	    requestEmployees( filter );
	}

    }

    private ArrayList< Employee > getEmployeeList( String jsonStr )
    {

	ArrayList< Employee > invoiceList = new ArrayList< Employee >( );
	try
	{
	    JSONArray arrays = new JSONArray( jsonStr );
	    for( int i = 0 ; i < arrays.length( ) ; i++ )
	    {

		JSONObject obj = arrays.getJSONObject( i );
		Employee employee = new Employee( );
		employee.setEmployeeID( obj.getString( "EmployeeID" ) );
		employee.setDescr( obj.getString( "Descr" ) );
		employee.setDepart( obj.getString( "Depart" ) );
		employee.setJob( obj.getString( "Job" ) );
		invoiceList.add( employee );
	    }
	}
	catch ( JSONException e )
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace( );
	}
	return invoiceList;

    }

    private void requestEmployees( String filter )
    {

	showDialog( );
	StringBuffer urlSbf = new StringBuffer( Constants.ROOT_URL + Constants.GET_EMPLOYEE_LIST + "?" );
	urlSbf.append( "account=" ).append( mConnName );
	urlSbf.append( "&filter=" ).append( filter );

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
			msg.what = MSG_GET_EMPLOYEE_LIST_SUCCESS;
			msg.obj = response.getString( "Data" );
		    }
		    else
		    {
			msg.what = MSG_GET_EMPLOYEE_LIST_ERROR;
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
		Logger.d( TAG , "----e----" + error.toString( ) );
		mUiHandler.sendEmptyMessage( MSG_GET_EMPLOYEE_LIST_ERROR );
	    }
	} );
	mQueue.add( json );
	mQueue.start( );

    }

    private static final int MSG_GET_EMPLOYEE_LIST_SUCCESS = 1;
    private static final int MSG_GET_EMPLOYEE_LIST_ERROR = 2;

    private Handler mUiHandler = new Handler( )
    {

	@Override
	public void handleMessage( Message msg )
	{
	    super.handleMessage( msg );

	    switch ( msg.what )
	    {
		case MSG_GET_EMPLOYEE_LIST_SUCCESS :
		    dismissDialog( );

		    String dataStr = (String)msg.obj;
		    ArrayList< Employee > employees = getEmployeeList( dataStr );
		    if( employees.size( ) > 0 )
		    {
			mEmployeeAdapter.addData( employees );
		    }
		    mEmployeeAdapter.notifyDataSetChanged( );
		    break;

		case MSG_GET_EMPLOYEE_LIST_ERROR :
		    dismissDialog( );
		    Toast.makeText( App.getAppContext( ) , App.getAppContext( ).getString( R.string.request_appr_road_list_error ) , Toast.LENGTH_SHORT ).show( );
		    break;

		default :
		    break;
	    }
	}

    };

    private class EmployeeAdapter extends BaseAdapter implements Filterable
    {

	private ArrayList< Employee > list = new ArrayList< Employee >( );

	private HashMap< String , Boolean > mMap = new HashMap< String , Boolean >( );

	public void addData( ArrayList< Employee > data )
	{
	    list.clear( );
	    list.addAll( data );

	    // 初始化
	    for( Employee e : list )
	    {
		mMap.put( e.getEmployeeID( ) , false );
	    }
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
		convertView = App.getLayoutInflater( ).inflate( R.layout.activity_employee_list_item , null );
		holder = new ViewHolder( );
		holder.employeeFrameRl = (RelativeLayout)findViewById( R.id.employee_frame );
		holder.employeeNameTv = (TextView)convertView.findViewById( R.id.employee_name_tv );
		holder.employeeDepartTv = (TextView)convertView.findViewById( R.id.employee_depart_tv );
		holder.employeeJobTv = (TextView)convertView.findViewById( R.id.employee_job_tv );
		holder.employeeCheckCb = (CheckBox)convertView.findViewById( R.id.employee_check_cb );

		convertView.setTag( holder );
	    }
	    else
	    {

		holder = (ViewHolder)convertView.getTag( );
	    }

	    bindData( holder , list.get( position ) );

	    return convertView;
	}

	private void bindData( final ViewHolder holder , final Employee employee )
	{

	    holder.employeeNameTv.setText( employee.getDescr( ) );
	    holder.employeeDepartTv.setText( employee.getDepart( ) );
	    holder.employeeJobTv.setTag( employee.getJob( ) );
	    holder.employeeCheckCb.setTag( employee.getEmployeeID( ) );

	    if( mEmployeeCheckId != null && mEmployeeCheckId.equals( employee.getEmployeeID( ) ) )
	    {
		holder.employeeCheckCb.setChecked( true );
		mMap.put( employee.getEmployeeID( ) , true );
	    }
	    else
	    {
		holder.employeeCheckCb.setChecked( false );
		mMap.put( employee.getEmployeeID( ) , false );
	    }
	    holder.employeeCheckCb.setOnClickListener( new View.OnClickListener( )
	    {
		@Override
		public void onClick( View v )
		{
		    if( !holder.employeeCheckCb.isChecked( ) )
		    {
			holder.employeeCheckCb.setChecked( false );
			mMap.put( holder.employeeCheckCb.getTag( ).toString( ) , false );
		    }
		    else
		    {
			holder.employeeCheckCb.setChecked( true );
			mMap.put( holder.employeeCheckCb.getTag( ).toString( ) , true );
		    }

		    // mUiHandler.sendEmptyMessage(MSG_REFRESH_ZT_LIST);
		}
	    } );
	    // 同步checkBox事件
	    holder.employeeFrameRl.setOnClickListener( new OnClickListener( )
	    {
		@Override
		public void onClick( View v )
		{
		    ( (ViewHolder)v.getTag( ) ).employeeCheckCb.performClick( );
		}
	    } );
	}

	@Override
	public Filter getFilter()
	{
	    return new Filter( )
	    {

		@Override
		protected FilterResults performFiltering( CharSequence constraint )
		{
		    FilterResults results = new FilterResults( );
		    // We implement here the filter logic
		    if( constraint == null || constraint.length( ) == 0 )
		    {
			// No filter implemented we return all the list
			results.values = list;
			results.count = list.size( );
		    }
		    else
		    {
			// We perform filtering operation
			ArrayList< Employee > nEmployeeList = new ArrayList< Employee >( );

			for( Employee p : list )
			{
			    if( p.getDescr( ).contains( constraint.toString( ).toUpperCase( ) ) )
				nEmployeeList.add( p );
			}

			results.values = nEmployeeList;
			results.count = nEmployeeList.size( );

		    }
		    return results;
		}

		@Override
		protected void publishResults( CharSequence constraint , FilterResults results )
		{
		    // Now we have to inform the adapter about the new list filtered
		    if( results.count == 0 )
			notifyDataSetInvalidated( );
		    else
		    {
			list = (ArrayList< Employee >)results.values;
			notifyDataSetChanged( );
		    }
		}

	    };
	}
    }

    class ViewHolder
    {
	RelativeLayout employeeFrameRl;
	TextView employeeNameTv;
	TextView employeeDepartTv;
	TextView employeeJobTv;
	CheckBox employeeCheckCb;
    }

    @Override
    public void onBackPressed()
    {
	super.onBackPressed( );
	finish( );
    }

}
