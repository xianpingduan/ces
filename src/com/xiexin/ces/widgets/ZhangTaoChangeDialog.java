package com.xiexin.ces.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiexin.ces.update.ResourceUtil;

/**
 * 有标题的弹出提示框
 * allenduan
 * 
 * 提示消息通过构造方法设置
*/

public class ZhangTaoChangeDialog extends Dialog
{

    private Context mContext;
    private TextView mTipTextView;

    private String mTip;
    private Button mSureBtn;
    private Button mCancelBtn;
    private String mSureBtnTitle;
    private String mCancelBtnTitle;

    private View.OnClickListener mSureBtnListener;
    private View.OnClickListener mCancelBtnListener;
    
    private String sure = "确定";
    private String cancel = "取消";

    private ZhangTaoChangeDialog( Context context )
    {
	super( context );
    }

    /**
     * param context 上下文
     * param tip 提示信息
     * param title 标题
     */

    public ZhangTaoChangeDialog( Context context , String tip )
    {
	super( context , ResourceUtil.getStyleId( context , "SelfUpdateDialog" ) );
	this.mContext = context;
	this.mTip = tip;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	setContentView( ResourceUtil.getLayoutId( mContext , "zhangtao_change_dialog" ) );

	mTipTextView = (TextView)findViewById( ResourceUtil.getId( mContext , "tip" ) );
	if( mTip == null )
	{
	    mTip = "";
	}
	mSureBtn = (Button)findViewById( ResourceUtil.getId( mContext , "dialog_sure" ) );
	mCancelBtn = (Button)findViewById( ResourceUtil.getId( mContext , "dialog_cancel" ) );
	mTipTextView.setText( mTip );
	mSureBtn.setText( mSureBtnTitle == null ? sure : mSureBtnTitle );
	mCancelBtn.setText( mCancelBtnTitle == null ? cancel : mCancelBtnTitle );
	mSureBtn.setOnClickListener( mSureBtnListener == null ? mSureBtnDefautListener : mSureBtnListener );
	mCancelBtn.setOnClickListener( mSureBtnListener == null ? mCancelBtnDefautListener : mCancelBtnListener );
    }

    private View.OnClickListener mSureBtnDefautListener = new View.OnClickListener( )
    {

	@Override
	public void onClick( View v )
	{
	    dismiss( );
	}
    };
    

    private View.OnClickListener mCancelBtnDefautListener = new View.OnClickListener( )
    {

	@Override
	public void onClick( View v )
	{
	    dismiss( );
	}
    };

    public void addSureBtnListener( View.OnClickListener listener )
    {
	this.mSureBtnListener = listener;
    }
    public void addCancelBtnListener( View.OnClickListener listener )
    {
	this.mCancelBtnListener = listener;
    }


    public void setmSureBtnTitle( String mSureBtnTitle )
    {
	this.mSureBtnTitle = mSureBtnTitle;
    }
    
    public void setmCancelBtnTitle( String mCancelBtnTitle )
    {
	this.mCancelBtnTitle = mCancelBtnTitle;
    }


    @Override
    public void onBackPressed()
    {
	super.onBackPressed( );
	dismiss();
    }
}
