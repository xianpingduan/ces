package com.xiexin.ces.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;

public class InvoiceInfoActivity extends Activity implements OnClickListener
{

    // header start
    private LinearLayout mReturnLl;
    private ImageView mReturnIv;
    private TextView mReturnTv;
    private TextView mTitle;
    private Button mBtn1;
    private Button mBtn2;

    // header end

    private int mInvoiceType;
    
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_invoice_info );
	mRequestQueue=Volley.newRequestQueue(App.getAppContext());
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
    }

    private void initData()
    {
	mInvoiceType = getIntent( ).getIntExtra( Constants.INVOICE_TYPE , 0 );

	mReturnTv.setText( getReturnStr( mInvoiceType ) );
	mTitle.setText( getString( R.string.invoice_info ) );

	mBtn1.setVisibility( View.VISIBLE );
	mBtn2.setVisibility( View.VISIBLE );
	mReturnLl.setVisibility( View.VISIBLE );

	mBtn1.setOnClickListener( this );
	mBtn2.setOnClickListener( this );
	mReturnLl.setOnClickListener( this );

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
	    case R.id.btn1 :
		intentToApprovalRoad( );
		break;
	    case R.id.btn2 :
		intentToAttachMent( );
		break;
	    case R.id.return_ll :
		onBackPressed( );
		break;
	    default :
		break;
	}

    }

    private void intentToApprovalRoad()
    {

    }

    private void intentToAttachMent()
    {

    }
    
    private void doRequestMobileCfg(){
    	
    }

    @Override
    public void onBackPressed()
    {
	super.onBackPressed( );
	finish( );
    }

}
