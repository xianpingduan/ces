package com.xiexin.ces.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

import com.xiexin.ces.utils.Logger;

public class OverScrollGallery extends Gallery
{
    private static final int MAX_SCROLL_X = 200;
    private static final float SCROLL_RATIO = 0.5f;// 阻尼系数  
    
    public OverScrollGallery( Context context )
    {
	super( context ,null);
    }

    public OverScrollGallery( Context context , AttributeSet attrs )
    {
	this( context , attrs ,0);
    }

    public OverScrollGallery( Context context , AttributeSet attrs , int defStyle )
    {
	super( context , attrs , defStyle );
    }

    @Override
    public boolean onFling( MotionEvent e1 , MotionEvent e2 , float velocityX , float velocityY )
    {
	return super.onFling( e1 , e2 , velocityX / (float)1.5 , velocityY / (float)1.5 );
    }

    @Override
    public void setOverScrollMode( int mode )
    {
	super.setOverScrollMode( View.OVER_SCROLL_ALWAYS );
    }

    @Override
    protected boolean overScrollBy( int deltaX , int deltaY , int scrollX , int scrollY , int scrollRangeX , int scrollRangeY , int maxOverScrollX , int maxOverScrollY , boolean isTouchEvent )
    {
	// TODO Auto-generated method stub
	Logger.d( "OverScrollGallery" , "overScrollBy" );
	int newDeltaX = deltaX;
	int delta = (int) ( deltaX * SCROLL_RATIO );
	if( delta != 0 )
	    newDeltaX = delta;
	return super.overScrollBy( newDeltaX , deltaY , scrollX , scrollY , scrollRangeX , scrollRangeY , MAX_SCROLL_X , maxOverScrollY , isTouchEvent );
    }
}
