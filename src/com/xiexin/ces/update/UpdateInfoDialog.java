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

public class UpdateInfoDialog extends Dialog
{

    private Context mContext;
    private TextView mTipTextView;
    private TextView mTitleTextView;

    //设置title
    private String mTitle;
    private String mTip;
    private Button mSureBtn;
    private String mSureBtnTitle;

    private View.OnClickListener mSureBtnListener;

    private String cancel = "确定";

    private UpdateInfoDialog( Context context )
    {
	super( context );
    }

    /**
     * param context 上下文
     * param tip 提示信息
     * param title 标题
     */

    public UpdateInfoDialog( Context context , String title , String tip )
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
	setContentView( ResourceUtil.getLayoutId( mContext , "update_info_dialog" ) );

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

	mSureBtn = (Button)findViewById( ResourceUtil.getId( mContext , "dialog_sure" ) );
	mSureBtn.setText( mSureBtnTitle == null ? cancel : mSureBtnTitle );
	mSureBtn.setOnClickListener( mSureBtnListener == null ? mSureBtnDefautListener : mSureBtnListener );

    }

    private View.OnClickListener mSureBtnDefautListener = new View.OnClickListener( )
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

    public void addSureBtnListener( View.OnClickListener listener )
    {
	this.mSureBtnListener = listener;
    }


    public void setmSureBtnTitle( String mSureBtnTitle )
    {
	this.mSureBtnTitle = mSureBtnTitle;
    }


    @Override
    public void onBackPressed()
    {
	super.onBackPressed( );
	dismiss();
    }
}
