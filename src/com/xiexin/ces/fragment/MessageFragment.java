package com.xiexin.ces.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.xiexin.ces.activity.MenuActivity;
import com.xiexin.ces.activity.MessageInfoActivity;
import com.xiexin.ces.entry.PushMessage;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.LoadingDialog;
import com.xiexin.ces.widgets.LoadingUIListView;
import com.xiexin.ces.widgets.PullListView.IListViewListener;

/**
 * User: special Date: 13-12-22 Time: 下午3:26 Mail: specialcyci@gmail.com
 */
public class MessageFragment extends Fragment implements OnClickListener
{

    private final static String TAG = "MessageFragment";

    private View parentView;
    private LoadingUIListView mListView;
    private MessageAdapter mMessageAdapter;

    // // header start
    // private LinearLayout mReturnLl;
    // private ImageView mReturnIv;
    // private TextView mReturnTv;
    // private TextView mTitle;
    // private Button mBtn1;
    // private Button mBtn2;
    //
    // // header end

    private LoadingDialog mLoadingDialog;

    private RequestQueue mQueue;
    private int mKind = 1;
    private int mCurrentPage = 1;

    private CheckBox mReadedCb;
    private CheckBox mNoReadCb;

    private static final int LOAD_LIST_NORMAL = 0;
    private static final int LOAD_LIST_REFRESH = 1;
    private static final int LOAD_LIST_LOADMORE = 2;

    public SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
    
	private Handler mMainUIHandler;
	public void setMainUIHandler(Handler handler){
		mMainUIHandler = handler;
	}

    private void dismissDialog()
    {
	new Handler( ).postDelayed( new Runnable( )
	{

	    @Override
	    public void run()
	    {
		if( mLoadingDialog != null && mLoadingDialog.isShowing( ) )
		{
		    mLoadingDialog.dismiss( );
		}
	    }
	} , 500 );

    }

    private void showDialog()
    {
	if( mLoadingDialog == null )
	{
	    mLoadingDialog = new LoadingDialog( getActivity( ) );
	}
	new Handler( ).postDelayed( new Runnable( )
	{
	    @Override
	    public void run()
	    {
		mLoadingDialog.show( );
	    }
	} , 200 );

    }

    @Override
    public void onAttach( Activity activity )
    {
	super.onAttach( activity );

	Logger.d( TAG , "onAttach" );

	mQueue = Volley.newRequestQueue( App.getAppContext( ) );
    }

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	Logger.d( TAG , "onCreate" );
    }

    @Override
    public View onCreateView( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState )
    {

	Logger.d( TAG , "onCreateView" );

	parentView = inflater.inflate( R.layout.fragment_message , container , false );
	mListView = (LoadingUIListView)parentView.findViewById( R.id.message_list );
	mListView.setHeaderPullEnable( true );
	mListView.setFooterPullEnable( true );
	mListView.setListViewListener( mListViewListener );

	mReadedCb = (CheckBox)parentView.findViewById( R.id.readed_cb );
	mNoReadCb = (CheckBox)parentView.findViewById( R.id.noread_cb );

	// // header start
	// mReturnLl = (LinearLayout) parentView.findViewById(R.id.return_ll);
	// mReturnIv = (ImageView) parentView.findViewById(R.id.return_iv);
	// mReturnTv = (TextView) parentView.findViewById(R.id.return_tv);
	// mTitle = (TextView) parentView.findViewById(R.id.title);
	// mBtn1 = (Button) parentView.findViewById(R.id.btn1);
	// mBtn2 = (Button) parentView.findViewById(R.id.btn2);
	// // /header end
	//
	// mReturnTv.setVisibility(View.GONE);

	// String title = "";
	// switch (mKind) {
	// case Constants.TYPE_PEND_APPROVAL_TASKS:
	// title = getString(R.string.menu_pend_approval);
	// break;
	// case Constants.TYPE_SCRATCH_UPCOME_TASKS:
	// title = getString(R.string.menu_scratch_upcome);
	// break;
	// case Constants.TYPE_APPROVED_TASKS:
	// title = getString(R.string.menu_approved);
	// break;
	// case Constants.TYPE_SEND_ITEM_TASKS:
	// title = getString(R.string.menu_sent_item);
	// break;
	// default:
	// break;
	// }
	// mTitle.setText(title);
	// mBtn1.setText( getString( R.string.finish_to ) );
	// mBtn1.setOnClickListener( this );
	// mReturnIv.setOnClickListener(this);

	initView( );

	initData( );
	return parentView;
    }

    @Override
    public void onResume()
    {
	super.onResume( );
	Logger.d( TAG , "onResume" );
    }

    private final IListViewListener mListViewListener = new IListViewListener( )
    {
	@Override
	public void onRefresh()
	{
	    mCurrentPage = 1;
	    requestMsg( LOAD_LIST_REFRESH );
	}

	@Override
	public void onLoadMore()
	{
	    requestMsg( LOAD_LIST_LOADMORE );
	}
    };

    private void initData()
    {

	if( mMessageAdapter == null )
	    mMessageAdapter = new MessageAdapter( );

	mListView.setAdapter( mMessageAdapter );

	// 请求数据
	requestMsg( LOAD_LIST_NORMAL );
	mReadedCb.setChecked( false );
	mNoReadCb.setChecked( true );

    }

    private void showConfirmDialog( final int msgId , final int msgType )
    {

	AlertDialog.Builder builder = new Builder( getActivity( ) );
	builder.setMessage( getString( R.string.sure_to_del_the_msg ) );
	builder.setTitle( null );
	builder.setPositiveButton( getString( R.string.sure ) , new DialogInterface.OnClickListener( )
	{
	    @Override
	    public void onClick( DialogInterface dialog , int which )
	    {
		dialog.dismiss( );
		doDelMsg( msgId , msgType );
	    }
	} );
	builder.setNegativeButton( getString( R.string.cancel ) , new DialogInterface.OnClickListener( )
	{
	    @Override
	    public void onClick( DialogInterface dialog , int which )
	    {
		dialog.dismiss( );
	    }
	} );
	builder.create( ).show( );
    }

    private static final int MSG_GET_MESSAGE_LIST_SUCCESS = 1;

    private static final int MSG_GET_MESSAGE_LIST_ERROR = 2;

    private static final int MSG_LAST_PAGE = 3;

    private static final int MSG_NO_DATA = 4;

    private static final int MSG_DEL_MSG_SUCCESS = 5;

    private static final int MSG_DEL_MSG_ERROR = 6;

    private Handler mUiHandler = new Handler( )
    {

	@Override
	public void handleMessage( Message msg )
	{
	    // TODO Auto-generated method stub
	    super.handleMessage( msg );
	    
		//提醒
		MenuActivity menuActivity=null;
		if(getActivity()!=null){
			menuActivity = (MenuActivity) getActivity();
		}
	    
	    switch ( msg.what )
	    {
		case MSG_GET_MESSAGE_LIST_SUCCESS :
		    mListView.setFooterPullEnable( true );
		    String data = (String)msg.obj;
		    ArrayList< PushMessage > invoices = getMessageList( data );
		    Logger.d( TAG , "invoices.size=" + invoices.size( ) );
		    if( invoices.size( ) > 0 )
		    {
			if( mCurrentPage == 1 )
			{
			    mMessageAdapter.addData( invoices , Constants.TYPE_LIST_ADD_COVER );
			}
			else
			{
			    mMessageAdapter.addData( invoices , Constants.TYPE_LIST_ADD_APPEND );
			}
			mMessageAdapter.notifyDataSetChanged( );
			mListView.stopHeaderRefresh( );
			mListView.stopFooterRefresh( );
			mCurrentPage++;

			if( invoices.size( ) <= Constants.PAGE_SIZE )
			{
			    mListView.setFooterPullEnable( false );
			}
			
			//提醒
			if(menuActivity!=null && mBread==0){
				menuActivity.showOrNoMsgTip(true);
			}

		    }
		    else if( invoices.size( ) == 0 && mCurrentPage == 1 )
		    {
			mMessageAdapter.addData( invoices , Constants.TYPE_LIST_ADD_COVER );
			mMessageAdapter.notifyDataSetChanged( );
			mUiHandler.sendEmptyMessage( MSG_NO_DATA );
		    }
		    else
		    {
			mUiHandler.sendEmptyMessage( MSG_LAST_PAGE );
		    }
		    dismissDialog( );
		    break;

		case MSG_GET_MESSAGE_LIST_ERROR :
		    dismissDialog( );
		    mListView.stopFooterRefresh( );
		    mListView.setFooterPullEnable( false );
		    Toast.makeText( App.getAppContext( ) , App.getAppContext( ).getString( R.string.data_laoding_failed ) , Toast.LENGTH_SHORT ).show( );
		    break;

		case MSG_LAST_PAGE :
		    if( mListView != null )
		    {
			mListView.setFooterPullEnable( false );
			mListView.stopFooterRefresh( );
			Toast.makeText( App.getAppContext( ) , App.getAppContext( ).getString( R.string.tip_last_page ) , Toast.LENGTH_SHORT ).show( );
		    }
		    break;
		case MSG_NO_DATA :
		    mListView.setFooterPullEnable( false );
		    Toast.makeText( App.getAppContext( ) , App.getAppContext( ).getString( R.string.no_msg ) , Toast.LENGTH_SHORT ).show( );
			//提醒
			if(menuActivity!=null&& mBread==0){
				menuActivity.showOrNoMsgTip(false);
			}
		    break;

		case MSG_DEL_MSG_SUCCESS :
		    Toast.makeText( App.getAppContext( ) , App.getAppContext( ).getString( R.string.del_msg_success ) , Toast.LENGTH_SHORT ).show( );

		    // 刷新列表
		    mCurrentPage = 1;
		    requestMsg( LOAD_LIST_REFRESH );
		    break;
		case MSG_DEL_MSG_ERROR :
		    Toast.makeText( App.getAppContext( ) , (String)msg.obj , Toast.LENGTH_SHORT ).show( );
		    break;
		default :
		    break;
	    }
	}

    };

    private ArrayList< PushMessage > getMessageList( String msgStr )
    {

	ArrayList< PushMessage > messageList = new ArrayList< PushMessage >( );

	try
	{
	    JSONArray arrays = new JSONArray( msgStr );
	    for( int i = 0 ; i < arrays.length( ) ; i++ )
	    {
		JSONObject jsonObject = arrays.getJSONObject( i );
		PushMessage msg = new PushMessage( );

		msg.setAccount( jsonObject.getString( "account" ) );
		msg.setApprid( jsonObject.getInt( "apprid" ) );
		msg.setBread( jsonObject.getInt( "bread" ) );
		msg.setContent( jsonObject.getString( "content" ) );
		msg.setCrtdate( jsonObject.getString( "crtdate" ) );
		msg.setFilespath( jsonObject.getString( "filespath" ) );
		msg.setFromuser( jsonObject.getString( "fromuser" ) );
		msg.setMsgid( jsonObject.getString( "msgid" ) );
		msg.setMsgtype( jsonObject.getInt( "msgtype" ) );
		msg.setTitle( jsonObject.getString( "title" ) );
		msg.setTouser( jsonObject.getString( "touser" ) );
		messageList.add( msg );
	    }
	}
	catch ( JSONException e )
	{
	    e.printStackTrace( );
	}

	return messageList;

    }

    // account=web_101&amp;userid=600012&amp;bread=0&amp;page=1&amp;size=100

    private int mBread = 0;

    public void requestMsg( final int type )
    {

	if( type == LOAD_LIST_NORMAL )
	{
	    showDialog( );
	}

	String account = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );
	String userid = App.getSharedPreference( ).getString( Constants.USER_ID , "" );

	StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) + Constants.GET_MSG_URL + "?" );
	urlSbf.append( "userid=" ).append( userid );
	urlSbf.append( "&account=" ).append( account );
	urlSbf.append( "&bread=" ).append( mBread );
	urlSbf.append( "&page=" ).append( mCurrentPage );
	urlSbf.append( "&size=" ).append( Constants.PAGE_SIZE );

	Logger.d( TAG , "urlSbf=" + urlSbf.toString( ) );
	JsonObjectRequest json = new JsonObjectRequest( Method.GET , urlSbf.toString( ) , null , new Listener< JSONObject >( )
	{
	    @Override
	    public void onResponse( JSONObject response )
	    {
		// TODO
		try
		{
		    int resCode = response.getInt( "success" );
		    Message msg = Message.obtain( );
		    if( resCode == 0 )
		    {
			msg.what = MSG_GET_MESSAGE_LIST_SUCCESS;
			msg.obj = response.getString( "data" );
			Logger.d( TAG , "data=" + msg.obj );
		    }
		    else
		    {
			msg.what = MSG_GET_MESSAGE_LIST_ERROR;
			msg.obj = response.get( "msg" );
		    }
		    mUiHandler.sendMessage( msg );
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
		mUiHandler.sendEmptyMessage( MSG_GET_MESSAGE_LIST_ERROR );
	    }
	} );

	mQueue.add( json );
	mQueue.start( );
    }

    private void initView()
    {

	mReadedCb.setOnClickListener( this );
	mNoReadCb.setOnClickListener( this );

	mListView.setOnItemClickListener( new AdapterView.OnItemClickListener( )
	{
	    @Override
	    public void onItemClick( AdapterView< ? > adapterView , View view , int i , long l )
	    {
		// Toast.makeText(getActivity(), "Clicked item!",
		// Toast.LENGTH_LONG).show();
	    }
	} );
    }

    private class MessageAdapter extends BaseAdapter
    {

	private ArrayList< PushMessage > list = new ArrayList< PushMessage >( );

	public void addData( ArrayList< PushMessage > data , int type )
	{

	    switch ( type )
	    {
		case Constants.TYPE_LIST_ADD_APPEND :
		    list.addAll( data );
		    break;
		case Constants.TYPE_LIST_ADD_COVER :
		    list.clear( );
		    list.addAll( data );
		    break;
		default :
		    break;
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
		convertView = App.getLayoutInflater( ).inflate( R.layout.fragment_message_list_item , null );
		holder = new ViewHolder( );
		holder.idTv = (TextView)convertView.findViewById( R.id.id_tv );
		holder.titleTv = (TextView)convertView.findViewById( R.id.title_tv );
		holder.contentTv = (TextView)convertView.findViewById( R.id.content_tv );
		holder.indicateIv = (ImageView)convertView.findViewById( R.id.indicate_iv );
		holder.msgTypeTv = (TextView)convertView.findViewById( R.id.msg_type_tv );
		holder.attachmentTv = (TextView) convertView.findViewById(R.id.attachment_tv);
		convertView.setTag( holder );
	    }
	    else
	    {

		holder = (ViewHolder)convertView.getTag( );
	    }

	    bindData( holder , list.get( position ) );

	    convertView.setOnClickListener( new View.OnClickListener( )
	    {

		@Override
		public void onClick( View v )
		{
		    ViewHolder holder = (ViewHolder)v.getTag( );
		    String id = holder.idTv.getText( ).toString( );
		    String title = holder.titleTv.getText( ).toString( );
		    String content = holder.contentTv.getText( ).toString( );
		    String msgType = holder.msgTypeTv.getText( ).toString( );
		    String filespath = holder.attachmentTv.getText().toString();
		    Intent intent = new Intent( );
		    intent.setClass( getActivity( ) , MessageInfoActivity.class );
		    intent.putExtra( "id" , Integer.parseInt( id ) );
		    intent.putExtra( "title" , title );
		    intent.putExtra( "content" , content );
		    intent.putExtra( "msgtype" , Integer.parseInt( msgType ) );
		    intent.putExtra( "filespath" , filespath);
		    startActivityForResult( intent , 1 );

		}
	    } );

	    holder.indicateIv.setOnClickListener( new View.OnClickListener( )
	    {
		@Override
		public void onClick( View v )
		{
		    PushMessage pushMessage = (PushMessage)v.getTag( );
		    String title = pushMessage.getTitle( );
		    String content = pushMessage.getContent( );
		    String filespath = pushMessage.getFilespath();
		    String id = "";
		    int msgType = pushMessage.getMsgtype( );
		    switch ( msgType )
		    {
			case 0 :
			    id = pushMessage.getMsgid( );
			    break;
			case 1 :
			    id = pushMessage.getApprid( ) + "";
			    break;
			default :
			    break;
		    }
		    Intent intent = new Intent( );
		    intent.setClass( getActivity( ) , MessageInfoActivity.class );
		    intent.putExtra( "id" , Integer.parseInt( id ) );
		    intent.putExtra( "title" , title );
		    intent.putExtra( "content" , content );
		    intent.putExtra( "msgtype" , msgType );
		    intent.putExtra( "filespath" , filespath);
		    startActivityForResult( intent , 1 );
		}
	    } );

	    convertView.setOnLongClickListener( new View.OnLongClickListener( )
	    {

		@Override
		public boolean onLongClick( View v )
		{
		    TextView idTv = (TextView)v.findViewById( R.id.id_tv );
		    TextView msgTypeTv = (TextView)v.findViewById( R.id.msg_type_tv );
		    String idStr = idTv.getText( ).toString( );
		    String msgType = msgTypeTv.getText( ).toString( );
		    Logger.d( TAG , "idStr=" + idStr + ",msgType=" + msgType );
		    showConfirmDialog( Integer.parseInt( idStr ) , Integer.parseInt( msgType ) );

		    return false;
		}
	    } );

	    return convertView;
	}

	private void bindData( final ViewHolder holder , final PushMessage pushMessage )
	{
	    // Logger.d(TAG, "---prgName="+invoice.getPrgName());
	    holder.titleTv.setText( pushMessage.getTitle( ) );
	    holder.contentTv.setText( pushMessage.getContent( ) );
	    holder.indicateIv.setTag( pushMessage );
	    holder.attachmentTv.setText(pushMessage.getFilespath());

	    int msgType = pushMessage.getMsgtype( );
	    switch ( msgType )
	    {
		case 0 :
		    holder.idTv.setText( pushMessage.getMsgid( ) + "" );
		    break;
		case 1 :
		    holder.idTv.setText( pushMessage.getApprid( ) + "" );
		    break;

		default :
		    break;
	    }
	    holder.msgTypeTv.setText( pushMessage.getMsgtype( ) + "" );

	}

	// 跳转到详情界面
	private void intentToMessageDesc()
	{
	    // TODO
	    Intent intent = new Intent( );
	    return;
	}
    }

    class ViewHolder
    {
	TextView titleTv;
	TextView contentTv;
	TextView msgTypeTv;
	ImageView indicateIv;

	// 存数据
	TextView idTv;
	TextView attachmentTv;
    }

    @Override
    public void onClick( View v )
    {
	switch ( v.getId( ) )
	{
	    case R.id.return_iv :

		break;
	    case R.id.readed_cb :
		if( !mReadedCb.isChecked( ) )
		{
		    mReadedCb.setChecked( true );
		}
		mNoReadCb.setChecked( false );
		mBread = 1;
		mCurrentPage = 1;
		requestMsg( LOAD_LIST_NORMAL );
		break;
	    case R.id.noread_cb :
		if( !mNoReadCb.isChecked( ) )
		{
		    mNoReadCb.setChecked( true );
		}
		mReadedCb.setChecked( false );
		mBread = 0;
		mCurrentPage = 1;
		requestMsg( LOAD_LIST_NORMAL );
		break;
	    default :
		break;
	}

    }

    // del msg
    private void doDelMsg( int msgId , int msgType )
    {
	StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) + Constants.DEL_MSG + "?" );
	String account = App.getSharedPreference( ).getString( Constants.ZHANG_TAO_CONN_NAME , "" );
	String userid = App.getSharedPreference( ).getString( Constants.USER_ID , "" );
	urlSbf.append( "account=" ).append( account );
	urlSbf.append( "&id=" ).append( msgId );
	urlSbf.append( "&userid=" ).append( userid );
	urlSbf.append( "&msgtype=" ).append( msgType );
	Logger.d( TAG , "urlSbf=" + urlSbf.toString( ) );
	JsonObjectRequest json = new JsonObjectRequest( Method.GET , urlSbf.toString( ) , null , new Listener< JSONObject >( )
	{
	    @Override
	    public void onResponse( JSONObject response )
	    {
		try
		{
		    int resCode = response.getInt( "Success" );
		    Message msg = Message.obtain( );
		    if( resCode == 0 )
		    {
			Logger.d( TAG , response.getString( "Data" ) );
			Logger.d( TAG , "resCode=" + resCode );
			mUiHandler.sendEmptyMessage( MSG_DEL_MSG_SUCCESS );
		    }
		    else
		    {
			Logger.d( TAG , response.getString( "Msg" ) );
			msg.what = MSG_DEL_MSG_ERROR;
			msg.obj = response.getString( "Msg" );
			mUiHandler.sendMessage( msg );
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
    public void onActivityResult( int requestCode , int resultCode , Intent data )
    {
	mCurrentPage = 1;
	requestMsg( LOAD_LIST_REFRESH );
	Logger.d( TAG , "MessageFragment,onActivityResult" );
	super.onActivityResult( requestCode , resultCode , data );
    }
}
