package com.xiexin.ces.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.entry.AttachMent;
import com.xiexin.ces.widgets.DotProgressBar;
import com.xiexin.ces.widgets.HorizonScrollLayout;
import com.xiexin.ces.widgets.HorizonScrollLayout.OnTouchScrollListener;

public class AttachmentActivity extends Activity
{
    private DotProgressBar mDotProgressBar;
    private HorizonScrollLayout mHorizonScrollLayout;
    private Context mContext;
    private boolean isCircule = true;

    private int infoSize = 0;

    private static final int MSG_CIRCULE = 0;
    private static final int TIME_CIRCULE_START = 4 * 1000;

    public static DisplayImageOptions optionsIcon = new DisplayImageOptions.Builder( ).showImageOnLoading( R.drawable.app_icon ).showImageForEmptyUri( R.drawable.app_icon )
	    .showImageOnFail( R.drawable.app_icon ).cacheInMemory( true ).cacheOnDisc( true ).bitmapConfig( Bitmap.Config.RGB_565 ).build( );

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_invoice_attachment );
	mContext = this;
	initView( );
	initData( );
    }

    private void initView()
    {
	mHorizonScrollLayout = (HorizonScrollLayout)findViewById( R.id.image_attachment_sl );
	mHorizonScrollLayout.setEnableOverScroll( false );
	mHorizonScrollLayout.setLockAllWhenTouch( true );
	mHorizonScrollLayout.setScrollSlop( 1.75f );
	mHorizonScrollLayout.setCircle( true );

	mDotProgressBar = (DotProgressBar)findViewById( R.id.image_attachment_dot );
	//	mDotProgressBar.setTotalNum( infos.size( ) );
	mDotProgressBar.setDotbarIconResource( R.drawable.dot_white , R.drawable.dot_black );
	mDotProgressBar.setVisibility( View.VISIBLE );

	mHorizonScrollLayout.setOnTouchScrollListener( new OnTouchScrollListener( )
	{

	    @Override
	    public void onScrollStateChanged( int scrollState , int currentScreem )
	    {
		// 当手滚动广告位是, 不播放
		if( OnTouchScrollListener.SCROLL_STATE_TOUCH_SCROLL == scrollState )
		{
		    stopCricule( );
		}
		else if( OnTouchScrollListener.SCROLL_STATE_IDLE == scrollState )
		{
		    startCricle( );
		}
	    }

	    @Override
	    public void onScroll( View view , float leftX , float screemWidth )
	    {}

	    @Override
	    public void onScreenChange( int displayScreem )
	    {
		mDotProgressBar.setCurProgress( displayScreem );
	    }
	} );
    }

    private String mFilePathStr;

    private ArrayList< String > mImageList = new ArrayList< String >( );
    private ArrayList< AttachMent > mDocList = new ArrayList< AttachMent >( );

    private void initData()
    {
	mImageList.clear( );
	mDocList.clear( );

	Intent intent = getIntent( );
	mFilePathStr = intent.getStringExtra( Constants.FILES_PATH );
	if( mFilePathStr != null && !mFilePathStr.isEmpty( ) )
	{
	    try
	    {
		JSONArray jsonArray = new JSONArray( mFilePathStr );
		for( int i = 0 ; i < jsonArray.length( ) ; i++ )
		{
		    JSONObject object = jsonArray.getJSONObject( i );
		    String attachMentName = object.getString( "AttchName" );
		    if( attachMentName.contains( ".jpg" ) )
		    {
			mImageList.add( object.getString( "FilePath" ) );
		    }
		    else
		    {
			AttachMent attachMent = new AttachMent( );
			attachMent.setAttchName( attachMentName );
			attachMent.setFilePath( object.getString( "FilePath" ) );
			mDocList.add( attachMent );
		    }
		}

	    }
	    catch ( JSONException e )
	    {
		e.printStackTrace( );
	    }
	}
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

    private void circule()
    {
	if( infoSize > 0 )
	{
	    mHorizonScrollLayout.displayNextScreen( );
	    mDotProgressBar.setCurProgress( getCurScreen( ) );
	}
    }

    public void scrollViewAddData( String [] images )
    {
	this.infoSize = images.length;

	mDotProgressBar.setTotalNum( images.length );
	mDotProgressBar.setDotbarNum( images.length );

	for( int i = 0 ; i < images.length ; i++ )
	{
	    ImageView imageView = new ImageView( mContext );
	    mHorizonScrollLayout.addView( imageView );
	    ImageLoader.getInstance( ).displayImage( images[i] , imageView , optionsIcon );
	}
    }

    public void setCurScreen( int position )
    {
	mHorizonScrollLayout.setDefaultScreem( position );
	mDotProgressBar.setCurProgress( position );
    }

    /**
     * 得到当前是第几个屏幕
     * @return
     */
    public int getCurScreen()
    {
	return mHorizonScrollLayout.getCurScreen( );
    }

    private void startCricle()
    {
	isCircule = true;
	if( mScrollHandler != null )
	{
	    mScrollHandler.removeMessages( MSG_CIRCULE );
	    mScrollHandler.sendEmptyMessageDelayed( MSG_CIRCULE , TIME_CIRCULE_START );
	}
    }

    private Handler mScrollHandler = new Handler( )
    {
	@Override
	public void handleMessage( android.os.Message msg )
	{
	    //暂停消息发送的时候，不需处理
	    switch ( msg.what )
	    {
		case MSG_CIRCULE :
		    if( isCircule && infoSize > 0 )
		    {
			if( mScrollHandler != null )
			{
			    //TODO 循环下个列表
			    circule( );
			    mScrollHandler.sendEmptyMessageDelayed( MSG_CIRCULE , TIME_CIRCULE_START );
			}
		    }
		    break;

		default :
		    break;
	    }
	};
    };

    public void stopCricule()
    {
	isCircule = false;
	if( mScrollHandler != null )
	{
	    mScrollHandler.removeMessages( MSG_CIRCULE );
	}
    }

}
