package com.xiexin.ces.update;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 有标题的弹出提示框
 * allenduan
 * 
 * 提示消息通过构造方法设置
*/

public class SelfUpdateCommonDialog extends Dialog
{

    private Context mContext;
    private TextView mTipTextView;
    private TextView mTitleTextView;

    //设置title
    private String mTitle;
    private String mTip;
    private Button mLeftBtn;
    private String mLeftBtnTitle;

    private Button mRightBtn;
    private String mRightBtnTitle;

    private View.OnClickListener mLeftBtnListener;
    private View.OnClickListener mRightBtnListener;

    private String sure = "sure";
    private String cancel = "cancel";

    private SelfUpdateCommonDialog( Context context )
    {
	super( context );
    }

    /**
     * param context 上下文
     * param tip 提示信息
     * param title 标题
     */

    public SelfUpdateCommonDialog( Context context , String title , String tip )
    {
	super( context , ResourceUtil.getStyleId( context , "SelfUpdateDialog" ) );
	this.mContext = context;
	this.mTip = tip;
	this.mTitle = title;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	setContentView( ResourceUtil.getLayoutId( mContext , "selfupdate_common_dialog" ) );

	mTipTextView = (TextView)findViewById( ResourceUtil.getId( mContext , "tip" ) );
	if( mTip == null )
	{
	    mTip = "";
	}
	mTipTextView.setText( mTip );

	mTitleTextView = (TextView)findViewById( ResourceUtil.getId( mContext , "title" ) );
	if( mTitle == null )
	{
	    mTitle = "";
	}
	mTitleTextView.setText( mTitle );

	mLeftBtn = (Button)findViewById( ResourceUtil.getId( mContext , "left" ) );
	mLeftBtn.setText( mLeftBtnTitle == null ? cancel : mLeftBtnTitle );
	mRightBtn = (Button)findViewById( ResourceUtil.getId( mContext , "right" ) );
	mRightBtn.setText( mRightBtnTitle == null ? sure : mRightBtnTitle );
	mLeftBtn.setOnClickListener( mLeftBtnListener == null ? mLeftBtnDefautListener : mLeftBtnListener );
	mRightBtn.setOnClickListener( mRightBtnListener == null ? mRightBtnDefautListener : mRightBtnListener );

    }

    private View.OnClickListener mLeftBtnDefautListener = new View.OnClickListener( )
    {

	@Override
	public void onClick( View v )
	{
	    dismiss( );
	}
    };

    private View.OnClickListener mRightBtnDefautListener = new View.OnClickListener( )
    {

	@Override
	public void onClick( View v )
	{
	    dismiss( );
	}
    };

    public void setTitle( String title )
    {
	mTitle = title;
    }

    public void addLeftBtnListener( View.OnClickListener listener )
    {
	this.mLeftBtnListener = listener;
    }

    public void addRightBtnListener( View.OnClickListener listener )
    {
	this.mRightBtnListener = listener;
    }

    public void setmLeftBtnTitle( String mLeftBtnTitle )
    {
	this.mLeftBtnTitle = mLeftBtnTitle;
    }

    public void setmRightBtnTitle( String mRightBtnTitle )
    {
	this.mRightBtnTitle = mRightBtnTitle;
    }

    @Override
    public void onBackPressed()
    {
	super.onBackPressed( );
    }
}
