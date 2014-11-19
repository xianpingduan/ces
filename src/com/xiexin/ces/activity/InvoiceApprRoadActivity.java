package com.xiexin.ces.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.entry.ZhangTao;

public class InvoiceApprRoadActivity extends Activity implements OnClickListener
{

    // header start
    private LinearLayout mReturnLl;
    private ImageView mReturnIv;
    private TextView mReturnTv;
    private TextView mTitle;
    private Button mBtn1;
    private Button mBtn2;

    // header end

    private String mConnName;//账套信息
    private String mPrgid; //业务类型
    private String mDatanbr;//单据编号

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_invoice_info );
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

	//	mInvoiceType = getIntent( ).getIntExtra( Constants.INVOICE_TYPE , 0 );
	mReturnTv.setText( getString( R.string.invoice_info ) );
	mTitle.setText( getString( R.string.invoice_approval_road ) );

	mBtn1.setVisibility( View.GONE );
	mBtn2.setVisibility( View.GONE );
	mReturnLl.setVisibility( View.VISIBLE );

	//	mBtn1.setOnClickListener( this );
	//	mBtn2.setOnClickListener( this );
	mReturnLl.setOnClickListener( this );
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

    private class InvoiceApprRoadAdapter extends BaseAdapter
    {

	private ArrayList< ZhangTao > list = new ArrayList< ZhangTao >( );

	private HashMap< String , Boolean > mMap = new HashMap< String , Boolean >( );

	public void addData( ArrayList< ZhangTao > data )
	{
	    list.clear( );
	    list.addAll( data );

	    // 初始化
	    for( ZhangTao zt : list )
	    {
		mMap.put( zt.getConnName( ) , false );
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
		convertView = App.getLayoutInflater( ).inflate( R.layout.activity_zhangtao_list_item , null );
		holder = new ViewHolder( );
		holder.ztFrameRl = (RelativeLayout)convertView.findViewById( R.id.zt_frame );
		holder.connNameTv = (TextView)convertView.findViewById( R.id.zt_name );
		holder.accInfoTv = (TextView)convertView.findViewById( R.id.zt_show_name );
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
	    holder.connNameTv.setText( zt.getConnName( ) );
	    holder.accInfoTv.setText( zt.getAccInfo( ) );
	    holder.ckBox.setTag( zt.getConnName( ) );
	    holder.accInfoTv.setTag( zt.getAccInfo( ) );
	    // Log.d(TAG,
	    // "connName="+zt.getConnName()+"checked="+mMap.get(zt.getConnName()));
	    if( mCheckConnName != null && mCheckConnName.equals( zt.getConnName( ) ) )
	    {
		holder.ckBox.setChecked( true );
		mMap.put( zt.getConnName( ) , true );
	    }
	    else
	    {
		holder.ckBox.setChecked( false );
		mMap.put( zt.getConnName( ) , false );
	    }
	    holder.ckBox.setOnClickListener( new View.OnClickListener( )
	    {
		@Override
		public void onClick( View v )
		{
		    if( !holder.ckBox.isChecked( ) )
		    {
			holder.ckBox.setChecked( false );
			mMap.put( holder.ckBox.getTag( ).toString( ) , false );
			mCheckConnName = null;
			mCheckAccInfo = null;
		    }
		    else
		    {
			holder.ckBox.setChecked( true );
			mMap.put( holder.ckBox.getTag( ).toString( ) , true );
			mCheckConnName = holder.ckBox.getTag( ).toString( );
			mCheckAccInfo = holder.accInfoTv.getTag( ).toString( );
		    }
		    Log.d( TAG , "mCheckConnName=" + mCheckConnName );

		    mUiHandler.sendEmptyMessage( MSG_REFRESH_ZT_LIST );
		}
	    } );
	    // 同步checkBox事件
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
	TextView accInfoTv;
	TextView connNameTv;
	CheckBox ckBox;
    }

    @Override
    public void onBackPressed()
    {
	super.onBackPressed( );
	finish( );
    }

}
