package com.xiexin.ces.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiexin.ces.App;
import com.xiexin.ces.R;
import com.xiexin.ces.entry.ZhangTao;
import com.xiexin.ces.widgets.ILoadingViewListener;
import com.xiexin.ces.widgets.LoadingUIListView;
import com.xiexin.ces.widgets.PullListView.IListViewListener;

public class ZhangTaoActivity extends Activity
{

    public final static String TAG = "ZhangTaoActivity";

    private LoadingUIListView mListView;

    //header start
    private LinearLayout mReturnLl;
    private ImageView mReturnIv;
    private TextView mReturnTv;
    private TextView mTitle;
    private Button mBtn1;
    private Button mBtn2;

    //header end

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	// TODO Auto-generated method stub
	super.onCreate( savedInstanceState );

	setContentView( R.layout.activity_zhangtao );

	initView( );
    }

    private void initView()
    {
	mListView = (LoadingUIListView)findViewById( R.id.zhangtao_list );
	mListView.setFooterPullEnable( true );
	mListView.setHeaderPullEnable( true );

	mListView.setPadaListViewListener( mListViewListener );
	mListView.setPadaLoadingViewListener( mLoadingViewListener );

	//header start
	mReturnLl = (LinearLayout)findViewById( R.id.return_ll );
	mReturnIv = (ImageView)findViewById( R.id.return_iv );
	mReturnTv = (TextView)findViewById( R.id.return_tv );
	mTitle = (TextView)findViewById( R.id.title );
	mBtn1 = (Button)findViewById( R.id.btn1 );
	mBtn2 = (Button)findViewById( R.id.btn2 );
	///header end
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

    private final static int MSG_REQUEST_DATA = 1;

    private Handler mUiHandler = new Handler( )
    {

	@Override
	public void handleMessage( Message msg )
	{
	    // TODO Auto-generated method stub
	    super.handleMessage( msg );

	    switch ( msg.what )
	    {
		case MSG_REQUEST_DATA :

		    break;

		default :
		    break;
	    }
	}

    };

    private final ILoadingViewListener mLoadingViewListener = new ILoadingViewListener( )
    {

	@Override
	public void onRetryRequestData()
	{
	    mUiHandler.sendEmptyMessage( MSG_REQUEST_DATA );
	}

	@Override
	public void onInitRequestData()
	{
	    mUiHandler.sendEmptyMessage( MSG_REQUEST_DATA );
	}
    };

    private final IListViewListener mListViewListener = new IListViewListener( )
    {
	@Override
	public void onRefresh()
	{}

	@Override
	public void onLoadMore()
	{
	    reqZhangTaoList( );
	}
    };

    private void reqZhangTaoList()
    {

    }

    //    private final static int REFRESH = 1;
    //    private final static int APPEND = 2;

    private class ZhangTaoAdapter extends BaseAdapter
    {

	private ArrayList< ZhangTao > list = new ArrayList< ZhangTao >( );

	public void addData( ArrayList< ZhangTao > data )
	{
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
		convertView = App.getLayoutInflater( ).inflate( R.layout.activity_zhangtao_list_item , null );
		holder = new ViewHolder( );
		holder.ztFrameRl = (RelativeLayout)convertView.findViewById( R.id.zt_frame );
		holder.nameTv = (TextView)convertView.findViewById( R.id.zt_name );
		holder.showNameTv = (TextView)convertView.findViewById( R.id.zt_show_name );
		holder.ckBox = (CheckBox)convertView.findViewById( R.id.zt_check );

		convertView.setTag( holder );
	    }
	    else
	    {

		holder = (ViewHolder)convertView.getTag( );
	    }

	    bindData( holder , list.get( position ) );

	    return convertView;
	}

	private void bindData( final ViewHolder holder , final ZhangTao zt )
	{
	    holder.nameTv.setText( zt.getRealName( ) );
	    holder.showNameTv.setText( zt.getShowName( ) );
	    holder.ckBox.setTag( zt.getRealName( ) );
	    holder.ckBox.setOnClickListener( new View.OnClickListener( )
	    {
		@Override
		public void onClick( View v )
		{
		    if( !holder.ckBox.isChecked( ) )
		    {
			holder.ckBox.setChecked( false );
		    }
		    else
		    {
			holder.ckBox.setChecked( true );
		    }
		}
	    } );
	    //同步checkBox事件
	    holder.ztFrameRl.setOnClickListener( new OnClickListener( )
	    {
		@Override
		public void onClick( View v )
		{
		    ( (ViewHolder)v.getTag( ) ).ckBox.performClick( );
		}
	    } );
	}
    }

    class ViewHolder
    {
	RelativeLayout ztFrameRl;
	TextView showNameTv;
	TextView nameTv;
	CheckBox ckBox;
    }
}
