package com.xiexin.ces.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiexin.ces.R;
import com.xiexin.ces.widgets.HorizonScrollLayout.OnTouchScrollListener;

/**
 * @author allenduan
 */

public class CirculeView
{
    private static final String TAG = "HomeCirculeAdvView";

    private static final int MSG_HOME_CIRCULE = 0;

    private static final int TIME_CIRCULE_START = 4 * 1000;

    /**
     * 从第几个位置开始循环标志
     */

    private DotProgressBar mDotProgressBar;
    private HorizonScrollLayout mHorizonScrollLayout;
    private Context context;

    private boolean isCircule = true;

    private int infoSize = 0;

    public static DisplayImageOptions optionsIcon = new DisplayImageOptions.Builder( ).showImageOnLoading( R.drawable.app_icon ).showImageForEmptyUri( R.drawable.app_icon )
	    .showImageOnFail( R.drawable.app_icon ).cacheInMemory( true ).cacheOnDisc( true ).bitmapConfig( Bitmap.Config.RGB_565 ).build( );

    @SuppressLint( "HandlerLeak" )
    private Handler mScrollHandler = new Handler( )
    {
	@Override
	public void handleMessage( android.os.Message msg )
	{
	    //暂停消息发送的时候，不需处理
	    switch ( msg.what )
	    {
		case MSG_HOME_CIRCULE :
		    if( isCircule && infoSize > 0 )
		    {
			if( mScrollHandler != null )
			{
			    //TODO 循环下个列表
			    circuleAdv( );
			    mScrollHandler.sendEmptyMessageDelayed( MSG_HOME_CIRCULE , TIME_CIRCULE_START );
			}
		    }
		    break;

		default :
		    break;
	    }
	};
    };

    public CirculeView( View view , Context context )
    {
	//	infoSize = infos.size( );
	this.context = context;

	mHorizonScrollLayout = (HorizonScrollLayout)view.findViewById( R.id.image_attachment_sl );
	mHorizonScrollLayout.setEnableOverScroll( false );
	mHorizonScrollLayout.setLockAllWhenTouch( true );
	mHorizonScrollLayout.setScrollSlop( 1.75f );
	mHorizonScrollLayout.setCircle( true );

	mDotProgressBar = (DotProgressBar)view.findViewById( R.id.image_attachment_dot );
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

    private void circuleAdv()
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
	    ImageView imageView = new ImageView( context );
	    mHorizonScrollLayout.addView( imageView );
	    ImageLoader.getInstance( ).displayImage( images[i] , imageView , optionsIcon );
	}
    }

    public void stopCricule()
    {
	isCircule = false;
	if( mScrollHandler != null )
	{
	    mScrollHandler.removeMessages( MSG_HOME_CIRCULE );
	}
    }

    public boolean isCricle()
    {
	return isCircule;
    }

    private void startCricle()
    {
	isCircule = true;
	if( mScrollHandler != null )
	{
	    mScrollHandler.removeMessages( MSG_HOME_CIRCULE );
	    mScrollHandler.sendEmptyMessageDelayed( MSG_HOME_CIRCULE , TIME_CIRCULE_START );
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

    /**
     * 回复首页第一个广告位的循环
     */
    public void onReply()
    {
	startCricle( );
    }

    /**
     * 停止广告位的循环动作
     */
    public void destoryCirclue()
    {
	mScrollHandler = null;
    }

}
