package com.xiexin.ces.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.xiexin.ces.db.EmployeeManager;
import com.xiexin.ces.entry.Employee;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.ApprovalDialog;
import com.xiexin.ces.widgets.LoadingDialog;
import com.xiexin.ces.widgets.NotifyDialog;
import com.xiexin.ces.widgets.PlusSignDialog;
import com.xiexin.ces.widgets.SubmitErrorDialog;
import com.xiexin.ces.widgets.SubmitSuccessDialog;

public class InvoiceInfoActivity extends Activity implements OnClickListener
{

    private final static String TAG = "InvoiceInfoActivity";

    // header start
    private LinearLayout mReturnLl;
    private ImageView mReturnIv;
    private TextView mReturnTv;
    private TextView mTitle;
    private Button mBtn1;
    private Button mBtn2;

    // header end

    private LinearLayout mInfoContentLl;
    private LoadingDialog mLoadingDialog;

    private int mInvoiceType;

    private RequestQueue mQueue;

    private String mConnName;// 账套信息
    private String mPrgid; // 业务类型
    private String mDatanbr;// 单据编号

    private String mDataConfigStr;
    private String [] mDataConfig; // data要显示的字段
    private JSONObject mDataContent;
    private JSONObject mDataHeader;// 表头
    private String mDataHeaderStr;

    private String mDetConfigStr;// 详情要显示的字段，以","隔开
    private String mDetContentStr;// 详情内容
    private String mDetHeaderStr;

    private String mFilesPathStr; // 附件详情

    private String mApprListStr;// 审批路线

    private CheckBox mApprovalRbtn;
    private CheckBox mNotifyRbtn;
    private CheckBox mPlusSignRbtn;
    private Button mSecondInfoBtn;

    private int mCurrentUi = NORMAL;
    private final static int NORMAL = 0;
    private final static int NOTIFY = 1;
    private final static int APROVAL = 2;
    private final static int PLUS_SIGN = 3;

    public SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );

    private SubmitSuccessDialog mSubmitSuccessDialog;
    private SubmitErrorDialog mSubmitErrorDialog;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_invoice_info );
	mQueue = Volley.newRequestQueue( App.getAppContext( ) );
	initView( );
	initData( );
    }

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

    private void dismissSubmitSuccessDialog()
    {
	if( mSubmitSuccessDialog != null && mSubmitSuccessDialog.isShowing( ) )
	{
	    mSubmitSuccessDialog.dismiss( );
	}
    }

    private void showSubmitSucessDialog()
    {
	if( mSubmitSuccessDialog == null )
	{
	    mSubmitSuccessDialog = new SubmitSuccessDialog( this );
	}
	mSubmitSuccessDialog.show( );
    }

    private void dismissSubmitErrorDialog()
    {
	if( mSubmitErrorDialog != null && mSubmitErrorDialog.isShowing( ) )
	{
	    mSubmitErrorDialog.dismiss( );
	}
    }

    private void showSubmitErrorDialog()
    {
	if( mSubmitErrorDialog == null )
	{
	    mSubmitErrorDialog = new SubmitErrorDialog( this );
	}
	mSubmitErrorDialog.show( );
    }

    private void initView()
    {

	mInfoContentLl = (LinearLayout)findViewById( R.id.info_content_ll );

	// header start
	mReturnLl = (LinearLayout)findViewById( R.id.return_ll );
	mReturnIv = (ImageView)findViewById( R.id.return_iv );
	mReturnTv = (TextView)findViewById( R.id.return_tv );
	mTitle = (TextView)findViewById( R.id.title );
	mBtn1 = (Button)findViewById( R.id.btn1 );
	mBtn2 = (Button)findViewById( R.id.btn2 );
	// /header end

	mInvoiceType = getIntent( ).getIntExtra( Constants.INVOICE_TYPE , 0 );

	mReturnTv.setText( getReturnStr( mInvoiceType ) );
	mTitle.setText( getString( R.string.invoice_info ) );
	mBtn1.setText( getString( R.string.invoice_approval_road ) );
	mBtn2.setText( getString( R.string.invoice_attachment ) );

	mBtn1.setVisibility( View.VISIBLE );
	mBtn2.setVisibility( View.VISIBLE );
	mReturnLl.setVisibility( View.VISIBLE );

	mBtn1.setOnClickListener( this );
	mBtn2.setOnClickListener( this );
	mReturnLl.setOnClickListener( this );
	mApprovalRbtn = (CheckBox)findViewById( R.id.approval_rb );
	mNotifyRbtn = (CheckBox)findViewById( R.id.notify_rb );
	mPlusSignRbtn = (CheckBox)findViewById( R.id.plus_sign_rb );

	mApprovalRbtn.setOnClickListener( this );
	mNotifyRbtn.setOnClickListener( this );
	mPlusSignRbtn.setOnClickListener( this );

	mSecondInfoBtn = (Button)findViewById( R.id.second_info_btn );
	mSecondInfoBtn.setOnClickListener( this );
    }

    private void initData()
    {
	Intent intent = getIntent( );
	mPrgid = intent.getStringExtra( Constants.PRGID );
	mConnName = intent.getStringExtra( Constants.ZHANG_TAO_CONN_NAME );
	if( mConnName == null || mConnName.isEmpty( ) || mConnName.equals( "null" ) )
	{
	    mConnName = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );
	}

	mDatanbr = intent.getStringExtra( Constants.DATANBR );

	Logger.d( TAG , "mConnName=" + mConnName + ",mPrgId=" + mPrgid + ",mDatanbr=" + mDatanbr );

	// 当前登录人
	mUpdateUser = App.getSharedPreference( ).getString( Constants.USER_ID , "" );

	// 初始化header信息
	mDataHeader = Constants.getData( mPrgid );

	// 获取配置
	doRequestMobileCfg( );
	// doRequestInfo( );
    }

    private void setListRefreshResult( int resultFrom )
    {
	Logger.d( TAG , "setListRefreshResult" );
	Intent in = new Intent( );
	// in.putExtra(Constants.APPR_LIST_RESULT_FROM, resultFrom);
	setResult( resultFrom , in );
	finish( );
    }

    private String getReturnStr( int type )
    {
	switch ( type )
	{
	    case Constants.TYPE_PEND_APPROVAL_TASKS :
		return getString( R.string.menu_pend_approval );
	    case Constants.TYPE_SCRATCH_UPCOME_TASKS :
		return getString( R.string.menu_scratch_upcome );
	    case Constants.TYPE_SEND_ITEM_TASKS :
		return getString( R.string.menu_sent_item );
	    case Constants.TYPE_APPROVED_TASKS :
		return getString( R.string.menu_approved );
	    default :
		break;
	}
	return getString( R.string.return_text );
    }

    @Override
    protected void onResume()
    {
	Logger.d( TAG , "onResume" );
	super.onResume( );
    }

    @Override
    protected void onDestroy()
    {
	dismissSubmitErrorDialog( );
	dismissSubmitSuccessDialog( );
	super.onDestroy( );
    }

    @Override
    public void onClick( View v )
    {
	switch ( v.getId( ) )
	{
	    case R.id.btn1 :
		intentToApprovalRoad( );
		break;
	    case R.id.btn2 :
		intentToAttachMent( );
		break;
	    case R.id.return_ll :
		onBackPressed( );
		break;
	    case R.id.second_info_btn :
		intentToInfo( );
		break;
	    case R.id.approval_rb :
		approval( );
		mNotifyRbtn.setChecked( false );
		mPlusSignRbtn.setChecked( false );
		mApprovalRbtn.setChecked( true );
		mCurrentUi = APROVAL;
		break;
	    case R.id.plus_sign_rb :
		mNotifyRbtn.setChecked( false );
		mPlusSignRbtn.setChecked( true );
		mApprovalRbtn.setChecked( false );
		if( mInvoiceType == Constants.TYPE_APPROVED_TASKS || mInvoiceType == Constants.TYPE_SEND_ITEM_TASKS )
		{
		    Toast.makeText( InvoiceInfoActivity.this , "已审批事项或已发事项不能加签!" , Toast.LENGTH_SHORT ).show( );
		}
		else
		{
		    plusSign( );
		    mCurrentUi = PLUS_SIGN;
		}
		break;
	    case R.id.notify_rb :
		mNotifyRbtn.setChecked( true );
		mPlusSignRbtn.setChecked( false );
		mApprovalRbtn.setChecked( false );
		if( mInvoiceType == Constants.TYPE_APPROVED_TASKS || mInvoiceType == Constants.TYPE_SEND_ITEM_TASKS )
		{
		    Toast.makeText( InvoiceInfoActivity.this , "已审批事项或已发事项不能知会!" , Toast.LENGTH_SHORT ).show( );
		}
		else
		{
		    notifyRb( );
		    mCurrentUi = NOTIFY;
		}
		break;
	    default :
		break;
	}
    }

    private void cancelChecked()
    {
	mApprovalRbtn.setChecked( false );
	mPlusSignRbtn.setChecked( false );
	mNotifyRbtn.setChecked( false );
    }

    public final static int MSG_APPROVAL_SUBMIT = 1000;
    public final static int MSG_PLUS_SIGN_SUBMIT = 1001;
    public final static int MSG_NOTIFY_SUBMIT = 1002;
    public final static int MSG_PLUS_SIGN_TO_EMPLOYEE = 1003;
    public final static int MSG_NOTIFY_TO_EMPLOYEE = 1004;
    public final static int MSG_CLEAR_RADIOGROUP_CHECK = 1005;
    private Handler mLogicHandler = new Handler( )
    {

	@Override
	public void handleMessage( Message msg )
	{
	    // TODO Auto-generated method stub
	    super.handleMessage( msg );
	    switch ( msg.what )
	    {
		case MSG_APPROVAL_SUBMIT :
		    doSubmitWorkFlow( );
		    break;
		case MSG_PLUS_SIGN_SUBMIT :
		    doSetPlusSign( );
		    break;
		case MSG_NOTIFY_SUBMIT :
		    doSetApprAttention( );
		    break;
		case MSG_PLUS_SIGN_TO_EMPLOYEE :
		    intentToEmployee( Constants.CHECK_EMPLOYEE_FROM_SETPLUGIN );
		    break;
		case MSG_NOTIFY_TO_EMPLOYEE :
		    intentToEmployee( Constants.CHECK_EMPLOYEE_FROM_NOTIFY );
		    break;
		case MSG_CLEAR_RADIOGROUP_CHECK :
		    // mApprovalHandleRgp.clearCheck();
		    break;
		default :
		    break;
	    }
	}

    };

    private ApprovalDialog mApprovalDialog;

    private void approval()
    {
	if( mApprovalDialog == null )
	{
	    mApprovalDialog = new ApprovalDialog( InvoiceInfoActivity.this , mLogicHandler , mInvoiceType );
	}
	mApprovalDialog.show( );
    }

    private void dismissApprovalDialog()
    {
	if( mApprovalDialog != null )
	{
	    mApprovalDialog.dismiss( );
	    mApprovalDialog = null;
	}
	mCurrentUi = APROVAL;
    }

    private PlusSignDialog mPlusSignDialog;

    private void plusSign()
    {
	if( mPlusSignDialog == null )
	{
	    mPlusSignDialog = new PlusSignDialog( InvoiceInfoActivity.this , mLogicHandler );
	}
	mPlusSignDialog.show( );
    }

    private void dismissPlusSignDialog()
    {
	if( mPlusSignDialog != null )
	{
	    mPlusSignDialog.dismiss( );
	    mPlusSignDialog = null;
	}

	mCurrentUi = APROVAL;
	mCheckUserId = null;
    }

    private NotifyDialog mNotifyDialog;

    private void notifyRb()
    {
	if( mNotifyDialog == null )
	{
	    mNotifyDialog = new NotifyDialog( InvoiceInfoActivity.this , mLogicHandler );
	}
	mNotifyDialog.show( );
    }

    private void dismissNotifyDialog()
    {
	if( mNotifyDialog != null )
	{
	    mNotifyDialog.dismiss( );
	    mNotifyDialog = null;
	}
	mCurrentUi = APROVAL;
	mCheckUserId = null;
    }

    private void intentToApprovalRoad()
    {
	Intent intent = new Intent( );
	intent.setClass( InvoiceInfoActivity.this , InvoiceApprRoadActivity.class );
	intent.putExtra( Constants.ZHANG_TAO_CONN_NAME , mConnName );
	intent.putExtra( Constants.PRGID , mPrgid );
	intent.putExtra( Constants.DATANBR , mDatanbr );
	intent.putExtra( Constants.APPR_LIST , mApprListStr );
	startActivity( intent );
    }

    // TODO 第三阶段
    // 附件
    private void intentToAttachMent()
    {
	Intent intent = new Intent( );
	intent.setClass( InvoiceInfoActivity.this , AttachmentActivity.class );
	intent.putExtra( Constants.FILES_PATH , mFilesPathStr );
	startActivity( intent );
    }

    private void intentToInfo()
    {
	Intent intent = new Intent( );
	intent.setClass( InvoiceInfoActivity.this , InvoiceSecondInfoActivity.class );
	intent.putExtra( Constants.PRGID , mPrgid );
	intent.putExtra( Constants.DET_CONFIG , mDetConfigStr );
	intent.putExtra( Constants.DET_INFO , mDetContentStr );
	intent.putExtra( Constants.DET_HEAD_CONFIG , mDetHeaderStr );
	startActivity( intent );

    }

    private void intentToEmployee( int from )
    {
	// switch (from) {
	// case Constants.CHECK_EMPLOYEE_FROM_NOTIFY:
	// dismissNotifyDialog();
	// break;
	// case Constants.CHECK_EMPLOYEE_FROM_SETPLUGIN:
	// dismissPlusSignDialog();
	// break;
	// default:
	// break;
	// }

	Intent intent = new Intent( );
	intent.setClass( InvoiceInfoActivity.this , EmployeeActivity.class );
	intent.putExtra( Constants.ZHANG_TAO_CONN_NAME , mConnName );
	intent.putExtra( Constants.CHECK_EMPLOYEE_FROM , from );
	startActivityForResult( intent , 1 );
    }

    private void doRequestMobileCfg()
    {
	showDialog( );
	StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) + Constants.GET_MOBILE_CFG + "?" );
	urlSbf.append( "account=" ).append( mConnName );
	urlSbf.append( "&prgid=" ).append( mPrgid );

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
			msg.what = MSG_GET_CONFIG_SUCCESS;
			msg.obj = response.getString( "Data" );
		    }
		    else
		    {
			msg.what = MSG_GET_CONFIG_ERROR;
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
		mUiHandler.sendEmptyMessage( MSG_GET_INFO_ERROR );
	    }
	} );
	mQueue.add( json );
	mQueue.start( );
    }

    private final static int MSG_GET_CONFIG_SUCCESS = 1;
    private final static int MSG_GET_CONFIG_ERROR = 2;
    private final static int MSG_GET_INFO_SUCCESS = 3;
    private final static int MSG_GET_INFO_ERROR = 4;

    private final static int MSG_SET_PLUS_SIGN_SUCCESS = 5;
    private final static int MSG_SET_PLUS_SIGN_ERROR = 6;

    private final static int MSG_SET_APPR_ATTENTION_SUCCESS = 7;
    private final static int MSG_SET_APPR_ATTENTION_ERROR = 8;

    private final static int MSG_SET_SUBMIT_WORK_FLOW_SUCCESS = 9;
    private final static int MSG_SET_SUBMIT_WORK_FLOW_ERROR = 10;

    private Handler mUiHandler = new Handler( )
    {
	@Override
	public void handleMessage( Message msg )
	{
	    super.handleMessage( msg );
	    switch ( msg.what )
	    {
		case MSG_GET_CONFIG_SUCCESS :

		    String jsonStr = (String)msg.obj;
		    handleConfigResult( jsonStr );
		    doRequestInfo( );
		    break;
		case MSG_GET_CONFIG_ERROR :
		    doRequestInfo( );
		    break;
		case MSG_GET_INFO_SUCCESS :
		    mDataContent = (JSONObject)msg.obj;
		    try
		    {
			mDetContentStr = mDataContent.getString( "Det" );
			mFilesPathStr = mDataContent.getString( "FilesPath" );
			mApprListStr = mDataContent.getString( "ApprList" );
		    }
		    catch ( JSONException e )
		    {
			e.printStackTrace( );
		    }
		    Logger.d( TAG , "inxNbr=" + getInxNbr( ) );
		    createDataView( );
		    mSecondInfoBtn.setEnabled( true );
		    dismissDialog( );

		    break;
		case MSG_GET_INFO_ERROR :
		    Toast.makeText( InvoiceInfoActivity.this , (String)msg.obj , Toast.LENGTH_SHORT ).show( );
		    mSecondInfoBtn.setEnabled( false );
		    dismissDialog( );
		    break;

		case MSG_SET_PLUS_SIGN_SUCCESS :
		    dismissDialog( );
		    dismissPlusSignDialog( );

		    showSubmitSucessDialog( );
		    Toast.makeText( InvoiceInfoActivity.this , getString( R.string.plus_sign_success , mCheckUserName ) , Toast.LENGTH_SHORT ).show( );
		    setListRefreshResult( Constants.APPR_LIST_RESULT_FROM_APPRSUC );
		    break;
		case MSG_SET_PLUS_SIGN_ERROR :
		    dismissDialog( );
		    showSubmitErrorDialog( );
		    Toast.makeText( InvoiceInfoActivity.this , (String)msg.obj , Toast.LENGTH_SHORT ).show( );
		    break;

		case MSG_SET_APPR_ATTENTION_SUCCESS :
		    dismissDialog( );
		    dismissNotifyDialog( );
		    showSubmitSucessDialog( );
		    Toast.makeText( InvoiceInfoActivity.this , getString( R.string.notify_success , mCheckUserName ) , Toast.LENGTH_SHORT ).show( );
		    setListRefreshResult( Constants.APPR_LIST_RESULT_FROM_APPRSUC );
		    break;
		case MSG_SET_APPR_ATTENTION_ERROR :
		    dismissDialog( );
		    showSubmitErrorDialog( );
		    Toast.makeText( InvoiceInfoActivity.this , (String)msg.obj , Toast.LENGTH_SHORT ).show( );
		    break;
		case MSG_SET_SUBMIT_WORK_FLOW_SUCCESS :
		    dismissDialog( );
		    dismissApprovalDialog( );
		    showSubmitSucessDialog( );
		    Toast.makeText( InvoiceInfoActivity.this , getString( R.string.approval_success ) , Toast.LENGTH_SHORT ).show( );
		    setListRefreshResult( Constants.APPR_LIST_RESULT_FROM_APPRSUC );
		    break;
		case MSG_SET_SUBMIT_WORK_FLOW_ERROR :

		    dismissDialog( );
		    showSubmitErrorDialog( );
		    Toast.makeText( InvoiceInfoActivity.this , (String)msg.obj , Toast.LENGTH_SHORT ).show( );

		    break;
		default :
		    break;
	    }
	}

    };

    private void handleConfigResult( String result )
    {

	try
	{
	    JSONArray jsonArray = new JSONArray( result );
	    for( int i = 0 ; i < jsonArray.length( ) ; i++ )
	    {
		JSONObject jsonObject = jsonArray.getJSONObject( i );
		String prgId = jsonObject.getString( "PrgID" );
		if( prgId.equals( mPrgid ) )
		{
		    mDataConfigStr = jsonObject.getString( "Doc" );
		    mDetConfigStr = jsonObject.getString( "Det" );
		    mDataHeaderStr = jsonObject.getString( "CDoc" );
		    mDetHeaderStr = jsonObject.getString( "CDet" );
		}
	    }

	    if( jsonArray.length( ) > 0 )
	    {
		generateDataConfig( mDataConfigStr , mDataHeaderStr );

	    }

	}
	catch ( JSONException e )
	{
	    e.printStackTrace( );
	}

    }

    private void generateDataConfig( String mDataConfigStr , String mDataHeaderStr )
    {
	mDataConfig = mDataConfigStr.split( "," );
	try
	{
	    mDataHeader = new JSONObject( mDataHeaderStr );
	}
	catch ( JSONException e )
	{
	    e.printStackTrace( );
	}
    }

    private int getInxNbr()
    {
	Logger.d( TAG , "updateUser=" + mUpdateUser );
	int inxNbr = 0;
	try
	{
	    JSONArray array = new JSONArray( mApprListStr );
	    for( int i = 0 ; i < array.length( ) ; i++ )
	    {
		JSONObject jsonObject = array.getJSONObject( i );
		String apprObj = jsonObject.getString( "ApprObj" );
		if( apprObj.equals( mUpdateUser ) )
		{
		    inxNbr = jsonObject.getInt( "InxNbr" );
		    break;
		}
	    }
	}
	catch ( JSONException e )
	{
	    e.printStackTrace( );
	}
	if( inxNbr == 0 )
	{
	    Logger.d( TAG , "get appro person " + mUpdateUser + " inxNbr error!" );
	}
	return inxNbr;
    }

    private void createDataView()
    {
	// mInfoContentLl
	LayoutInflater inflater = App.getLayoutInflater( );
	Logger.d( TAG , "createDataView,mPrgid=" + mPrgid );
	Logger.d( TAG , "createDataView,mDataHeader=" + mDataHeader.toString( ) );
	if( mDataConfig == null || mDataConfig.length == 0 )
	{
	    Logger.d( TAG , "get doc config is error ,use default" );
	    mDataConfig = Constants.getDataDefaultConfig( mPrgid );
	}
	for( int i = 0 ; i < mDataConfig.length ; i++ )
	{
	    RelativeLayout view = (RelativeLayout)inflater.inflate( R.layout.activity_invoice_info_tv_item , null );
	    TextView headerTv = (TextView)view.findViewById( R.id.table_header_name );
	    TextView contentTv = (TextView)view.findViewById( R.id.table_content );
	    ImageView msgIv = (ImageView)view.findViewById( R.id.message );
	    ImageView phoneIv = (ImageView)view.findViewById( R.id.phone );
	    contentTv.setTextColor( getResources( ).getColor( R.color.info_content_text_color ) );

	    if( mDataConfig[i].equals( "TotalCost" ) )
	    {
		contentTv.setTextColor( getResources( ).getColor( R.color.pl_main_red_color ) );
	    }
	    else
	    {
		contentTv.setTextColor( getResources( ).getColor( R.color.info_content_text_color ) );
	    }

	    msgIv.setOnClickListener( new View.OnClickListener( )
	    {

		@Override
		public void onClick( View v )
		{
		    Object obj = v.getTag( );
		    if( obj != null && !((String)obj).isEmpty())
		    {
			Logger.d( TAG , "mobile =" + (String)obj );
			sendSMS( ( (String)obj ) );
		    }
		    else
		    {
			Toast.makeText( InvoiceInfoActivity.this , getString( R.string.no_phone_info ) , Toast.LENGTH_SHORT ).show( );
		    }

		}
	    } );

	    phoneIv.setOnClickListener( new View.OnClickListener( )
	    {

		@Override
		public void onClick( View v )
		{
		    Object obj = v.getTag( );
		    if( obj != null && !((String)obj).isEmpty() )
		    {
			Logger.d( TAG , "mobile =" + (String)obj );
			callPhone( ( (String)obj ) );
		    }
		    else
		    {
			Toast.makeText( InvoiceInfoActivity.this , getString( R.string.no_phone_info ) , Toast.LENGTH_SHORT ).show( );
		    }
		}
	    } );

	    try
	    {
		headerTv.setText( mDataHeader.getString( mDataConfig[i] ) + ":" );
		String content = mDataContent.getString( mDataConfig[i] );

		if( mDataConfig[i].equals( "Duty" ) )
		{
		    msgIv.setVisibility( View.VISIBLE );
		    phoneIv.setVisibility( View.VISIBLE );

		    Logger.d( TAG , "before,content =" + content );
		    Employee employee = EmployeeManager.getInstance( App.getAppContext( ) ).findEmployeeById( mDataContent.getString( mDataConfig[i] ) );

		    if( employee != null )
		    {
			content = employee.getDescr( );
			msgIv.setTag( employee.getMobile( ) );
			phoneIv.setTag( employee.getMobile( ) );
		    }

		    Logger.d( TAG , "last,content =" + content );

		}
		else
		{
		    msgIv.setVisibility( View.GONE );
		    phoneIv.setVisibility( View.GONE );
		}

		if( content == null || content.equals( "null" ) )
		{
		    content = "";
		}
		if( content.contains( "T00:00:00" ) )
		{
		    Date date = new Date( );
		    try
		    {
			date = sdf.parse( content );
			content = sdf.format( date );
		    }
		    catch ( ParseException e )
		    {
			e.printStackTrace( );
			Logger.d( TAG , "date format error" );
		    }
		}

		if( !content.isEmpty( ) && mDataConfig[i].equals( "TotalCost" ) )
		{
		    content = "￥" + content;
		}
		contentTv.setText( content );
	    }
	    catch ( JSONException e )
	    {
		e.printStackTrace( );
	    }
	    mInfoContentLl.addView( view );
	}

    }

    // 整个单据详情
    private void doRequestInfo()
    {
	// showDialog( );
	StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) + Constants.GET_DOC_INFORMATION + "?" );
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
		    int resCode = response.getInt( "Success" );
		    Message msg = Message.obtain( );
		    if( resCode == 0 )
		    {
			msg.what = MSG_GET_INFO_SUCCESS;
			msg.obj = response.getJSONObject( "Data" );
		    }
		    else
		    {
			msg.what = MSG_GET_INFO_ERROR;
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
		mUiHandler.sendEmptyMessage( MSG_GET_INFO_ERROR );
	    }
	} );
	mQueue.add( json );
	mQueue.start( );
    }

    // 加签
    private int mPosition = -1;
    private String mCheckUserId = null;
    private String mUpdateUser;

    // private int inxnbr = 0;

    private void doSetPlusSign()
    {

	mPosition = mPlusSignDialog.getType( );
	if( mCheckUserId == null || mCheckUserId.isEmpty( ) )
	{
	    Toast.makeText( InvoiceInfoActivity.this , getString( R.string.please_select_employee ) , Toast.LENGTH_SHORT ).show( );
	    return;
	}
	if( mPosition == -1 )
	{
	    Toast.makeText( InvoiceInfoActivity.this , getString( R.string.please_check_qianhou ) , Toast.LENGTH_SHORT ).show( );
	    return;
	}
	StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) + Constants.SET_PLUS_SIGN + "?" );
	urlSbf.append( "account=" ).append( mConnName );
	urlSbf.append( "&prgid=" ).append( mPrgid );
	urlSbf.append( "&datanbr=" ).append( mDatanbr );
	urlSbf.append( "&position=" ).append( mPosition );
	urlSbf.append( "&userid=" ).append( mCheckUserId );
	urlSbf.append( "&updateuser=" ).append( mUpdateUser );
	urlSbf.append( "&inxnbr=" ).append( getInxNbr( ) );

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
			msg.what = MSG_SET_PLUS_SIGN_SUCCESS;
			msg.obj = response.getString( "Data" );
		    }
		    else
		    {
			msg.what = MSG_SET_PLUS_SIGN_ERROR;
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
		mUiHandler.sendEmptyMessage( MSG_SET_PLUS_SIGN_ERROR );
	    }
	} );
	mQueue.add( json );
	mQueue.start( );
    }

    // 审批
    private String mApprovalMemo;
    private int mApprovalKind;

    private void doSubmitWorkFlow()
    {
	showDialog( );
	mApprovalMemo = mApprovalDialog.getApprovalContent( );
	if( !mApprovalMemo.isEmpty( ) )
	{
	    //try
	    //{
	    //	mApprovalMemo = new String( mApprovalMemo.getBytes( "GBK2312" ) );
	    //	Logger.d( TAG , "mApprovalMemo =" + mApprovalMemo );
	    //}
	    //catch ( UnsupportedEncodingException e )
	    //{
	    //	e.printStackTrace( );
	    //}
	    //	    mApprovalMemo.
	    try
	    {
		mApprovalMemo = URLEncoder.encode( mApprovalMemo , "utf-8" );
	    }
	    catch ( UnsupportedEncodingException e )
	    {
		e.printStackTrace( );
	    }
	}
	Log.d( TAG , "mApprovalMemo=" + mApprovalMemo );
	mApprovalKind = mApprovalDialog.getSpinnerValue( );
	StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) + Constants.SUBMIT_WORK_FLOW + "?" );
	urlSbf.append( "account=" ).append( mConnName );
	urlSbf.append( "&datanbr=" ).append( mDatanbr );
	urlSbf.append( "&prgid=" ).append( mPrgid );
	urlSbf.append( "&duty=" ).append( mUpdateUser );
	urlSbf.append( "&memo=" ).append( mApprovalMemo );
	urlSbf.append( "&inxnbr=" ).append( getInxNbr( ) );
	urlSbf.append( "&kind=" ).append( mApprovalKind );

	Logger.d( TAG , "urlSbf=" + urlSbf.toString( ) );

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
			msg.what = MSG_SET_SUBMIT_WORK_FLOW_SUCCESS;
			msg.obj = response.getString( "Data" );
		    }
		    else
		    {
			msg.what = MSG_SET_SUBMIT_WORK_FLOW_ERROR;
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
		mUiHandler.sendEmptyMessage( MSG_SET_SUBMIT_WORK_FLOW_ERROR );
	    }
	} );
	mQueue.add( json );
	mQueue.start( );
    }

    // 知会
    private void doSetApprAttention()
    {

	if( mCheckUserId == null || mCheckUserId.isEmpty( ) )
	{
	    Toast.makeText( InvoiceInfoActivity.this , getString( R.string.please_select_employee ) , Toast.LENGTH_SHORT ).show( );
	    return;
	}

	StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) + Constants.SET_APPR_ATTENTION + "?" );
	urlSbf.append( "account=" ).append( mConnName );
	urlSbf.append( "&prgid=" ).append( mPrgid );
	urlSbf.append( "&datanbr=" ).append( mDatanbr );
	urlSbf.append( "&userid=" ).append( mCheckUserId );
	urlSbf.append( "&updateuser=" ).append( mUpdateUser );

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
			msg.what = MSG_SET_APPR_ATTENTION_SUCCESS;
			msg.obj = response.getString( "Data" );
		    }
		    else
		    {
			msg.what = MSG_SET_APPR_ATTENTION_ERROR;
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
		mUiHandler.sendEmptyMessage( MSG_SET_APPR_ATTENTION_ERROR );
	    }
	} );
	mQueue.add( json );
	mQueue.start( );
    }

    @Override
    public void onBackPressed()
    {
	super.onBackPressed( );
	setListRefreshResult( Constants.APPR_LIST_RESULT_FROM_RETURN );
	cancelChecked( );
	dismissSubmitErrorDialog( );
	dismissSubmitSuccessDialog( );
    }

    private String mCheckUserName;

    @Override
    protected void onActivityResult( int requestCode , int resultCode , Intent data )
    {
	// TODO Auto-generated method stub
	super.onActivityResult( requestCode , resultCode , data );
	Logger.d( TAG , "onActivityResult" );
	if( resultCode == RESULT_OK )
	{
	    mCheckUserId = data.getStringExtra( "userid" );
	    mCheckUserName = data.getStringExtra( "userName" );
	    String mCheckUserName = data.getStringExtra( "userName" );
	    Logger.d( TAG , "mCurrentUi=" + mCurrentUi + ",mCheckUserId=" + mCheckUserId + ",mCheckUserName=" + mCheckUserName );
	    switch ( mCurrentUi )
	    {
		case NOTIFY :
		    if( mNotifyDialog != null && mNotifyDialog.isShowing( ) )
			mNotifyDialog.setEmployee( mCheckUserName );
		    break;
		case PLUS_SIGN :
		    if( mPlusSignDialog != null && mPlusSignDialog.isShowing( ) )
			mPlusSignDialog.setEmployee( mCheckUserName );
		    break;
		default :
		    break;
	    }
	}

    }

    //发短信
    public void sendSMS( String phoneNum )
    {
	Intent intent = new Intent( );
	intent.setAction( Intent.ACTION_SENDTO );
	intent.setData( Uri.parse( "smsto:" + phoneNum ) );
	startActivity( intent );
    }

    //拨打电话
    public void callPhone( String phoneNum )
    {
	Intent intent = new Intent( );
	intent.setAction( Intent.ACTION_CALL );
	intent.setData( Uri.parse( "tel:" + phoneNum ) );
	startActivity( intent );
    }

}
