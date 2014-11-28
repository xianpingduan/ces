package com.xiexin.ces.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 类似桌面的workspace控件
 * 
 * @author allenduan
 */
public class HorizonScrollLayout extends ViewGroup
{

    private static final String TAG = "HorizonScrollLayout";
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private int mCurScreen = 0;
    private int mDefaultScreen = 0;
    private int mChildScreenWidth = 0;
    private int mScreenWidth = 0;

    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private static final int TOUCH_STATE_LOCK = 2;

    private static final int SNAP_VELOCITY = 600;

    private int mTouchState = TOUCH_STATE_REST;
    private int mTouchSlop = 24;
    private float mAngleSlop = 0.577f; // tan(a) = x/y , 滑动的角度30
    private float mLastMotionX;
    private float mLastMotionY;

    private float mMinScrollX = 0;
    private float mMaxScrollX = 0;

    private float mPreScrollX = 0;

    private boolean mEnableOverScroll = true;
    //	private float mDurationCorr = (float) (Math.PI/1000.0f); // 时间的调和数
    // 屏幕状态
    private int mScrollState = OnTouchScrollListener.SCROLL_STATE_IDLE;

    // 是否禁止滚动标记
    private boolean mEnableScroll = true;

    // 
    private boolean mLockAllWhenTouch = false;

    // 状态回调
    private OnTouchScrollListener mTouchScrollListener = null;

    private static final int INVALID_SCREEN = -999;
    private int mNextScreen = INVALID_SCREEN;

    int mScrollX;
    int mScrollY;
    boolean mIsCircle = false;
    boolean mChangeCoordinate = true;

    /**
     * 屏幕滚动回调
     * 
     * @author allenduan
     * 
     */
    public interface OnTouchScrollListener
    {
	// scroll state
	public static int SCROLL_STATE_IDLE = 0;
	public static int SCROLL_STATE_TOUCH_SCROLL = 1;
	public static int SCROLL_STATE_FLING = 2;

	/**
	 * 当前显示的屏幕改变
	 * 
	 * @param displayScreem
	 *            :显示的屏幕
	 */
	public void onScreenChange( int displayScreem );

	/**
	 * 屏幕当前滚动的位置
	 * 
	 * @param leftX
	 *            :当前屏左边的位置
	 * @param screemWidth
	 *            :屏幕的总宽度
	 */
	public void onScroll( View view , float leftX , float screemWidth );

	/**
	 * 滚动状态
	 * 
	 * @param scrollState
	 * @param currentScreem
	 */
	public void onScrollStateChanged( int scrollState , int currentScreem );
    }

    public HorizonScrollLayout( Context context , AttributeSet attrs )
    {
	this( context , attrs , 0 );
    }

    public HorizonScrollLayout( Context context , AttributeSet attrs , int defStyle )
    {
	super( context , attrs , defStyle );
	mScroller = new Scroller( context );

	mCurScreen = mDefaultScreen;
	mTouchSlop = ViewConfiguration.get( getContext( ) ).getScaledTouchSlop( );
    }

    /**
     * 设置是否可以滑动出边界
     * @param enable
     */
    public void setEnableOverScroll( boolean enable )
    {
	mEnableOverScroll = enable;
    }

    /**
     * 设置水平滑动的响应角度的水平值:tan(30);
     * @param slop
     */
    public void setScrollSlop( float slop )
    {
	mAngleSlop = slop;
    }

    @Override
    protected void onLayout( boolean changed , int l , int t , int r , int b )
    {
	int childLeft = 0;
	final int childCount = getChildCount( );
	for( int i = 0 ; i < childCount ; i++ )
	{
	    final View childView = getChildAt( i );
	    if( childView.getVisibility( ) != View.GONE )
	    {
		final int childWidth = childView.getMeasuredWidth( );

		// 如果屏幕大小末发生变化, 只对当前屏幕重新布局, 否则全部布局
		if( changed == false )
		{
		    if( i == mCurScreen )
		    {
			childView.layout( childLeft , 0 , childLeft + childWidth , childView.getMeasuredHeight( ) );
			childView.postInvalidate( );
			break;
		    }
		}
		else
		{
		    childView.layout( childLeft , 0 , childLeft + childWidth , childView.getMeasuredHeight( ) );
		    childView.postInvalidate( );
		}
		childLeft += childWidth;
	    }
	}

	// 重新计算时间参数
	//		if( changed){
	//			mDurationCorr = (float) (Math.PI / ((getChildCount()-1) * getWidth()));
	//		}

	// 限制滑动范围
	mChildScreenWidth = getWidth( ); // 子屏幕宽度
	mScreenWidth = mChildScreenWidth * childCount; // 屏幕总宽度
	if( mEnableOverScroll )
	{
	    mMinScrollX = - ( mChildScreenWidth >> 2 ); // 向左移动范围
	    mMaxScrollX = mScreenWidth - mChildScreenWidth - mMinScrollX; // 向右移动范围
	}
	else
	{
	    mMinScrollX = 0;
	    mMaxScrollX = mScreenWidth - mChildScreenWidth;
	}

	// 区域改变后,重新移动
	if( changed == true )
	{
	    // 解决横竖屏切换位置不正确问题
	    if( !mScroller.isFinished( ) )
	    {
		mScroller.abortAnimation( );
	    }
	    scrollTo( mCurScreen * getWidth( ) , 0 );
	}
    }

    /**
     * 设置到第几个
     * @param child
     */
    public void layoutChild( int child )
    {
	final View childView = getChildAt( child );
	if( childView != null )
	{
	    childView.requestLayout( );
	}
    }

    @Override
    protected void onMeasure( int widthMeasureSpec , int heightMeasureSpec )
    {
	super.onMeasure( widthMeasureSpec , heightMeasureSpec );

	final int width = MeasureSpec.getSize( widthMeasureSpec );
	int height = width / 3;

	final int widthMode = MeasureSpec.getMode( widthMeasureSpec );
	if( widthMode != MeasureSpec.EXACTLY )
	{
	    // throw new
	    // IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!");
	}

	final int heightMode = MeasureSpec.getMode( heightMeasureSpec );
	if( heightMode != MeasureSpec.EXACTLY )
	{
	    // throw new
	    // IllegalStateException("ScrollLayout only can run at EXACTLY mode!");
	}

	// The children are given the same width and height as the scrollLayout
	final int count = getChildCount( );
	for( int i = 0 ; i < count ; i++ )
	{
	    getChildAt( i ).measure( widthMeasureSpec , heightMeasureSpec );
	}

	setMeasuredDimension( width , height );
    }

    /**
     * According to the position of current layout scroll to the destination
     * page.
     */
    protected void snapToDestination()
    {
	final int screenWidth = getWidth( );
	final int destScreen = ( mScrollX + screenWidth / 2 ) / screenWidth;
	snapToScreen( destScreen , true );
    }

    protected void snapToScreen( int whichScreen , boolean isCallback )
    {
	// get the valid layout page
	whichScreen = Math.max( 0 , Math.min( whichScreen , getChildCount( ) - 1 ) );
	mNextScreen = whichScreen;
	if( mScrollX != ( whichScreen * getWidth( ) ) )
	{
	    if( isCallback == true && mTouchScrollListener != null && mCurScreen != whichScreen )
	    {
		mTouchScrollListener.onScreenChange( whichScreen );
	    }

	    final int delta = whichScreen * getWidth( ) - mScrollX;
	    //			int duration = (int) (1000 * Math.atan(Math.abs(mDurationCorr * delta))); // 0ms-1500ms
	    int duration = (int) ( 1000 * Math.atan( Math.abs( Math.PI * delta / 1000.0f ) ) ); // 0ms-1500ms
	    //			TLog.v(TAG, "new:" + duration + ", old:" + Math.abs(delta) * 2 + ", duration1:" + duration1);
	    mScroller.startScroll( mScrollX , 0 , delta , 0 , duration/*Math.abs(delta) * 2*/);

	    mCurScreen = whichScreen;

	    layoutChild( mCurScreen );
	    invalidate( ); // Redraw the layout
	}
    }

    protected void snapToLastScreen()
    {
	// get the valid layout page
	int childCount = getChildCount( );
	mNextScreen = childCount - 1;
	if( mScrollX != ( childCount - 1 ) * getWidth( ) )
	{
	    if( mTouchScrollListener != null )
	    {
		mTouchScrollListener.onScreenChange( mNextScreen );
	    }
	    int delta = 0;
	    if( mScrollX < 0 && mScrollX > 0 - getWidth( ) / 2 )
	    {
		mScrollX = childCount * getWidth( ) + mScrollX;
		mChangeCoordinate = false;
		scrollTo( mScrollX , mScrollY );
		mChangeCoordinate = true;
	    }
	    delta = 0 - ( mScrollX - getWidth( ) * ( childCount - 1 ) );
	    //			int duration = (int) (1000 * Math.atan(Math.abs(mDurationCorr * delta))); // 0ms-1500ms
	    int duration = (int) ( 1000 * Math.atan( Math.abs( Math.PI * delta / 1000.0f ) ) ); // 0ms-1500ms
	    //			TLog.v(TAG, "new:" + duration + ", old:" + Math.abs(delta) * 2 + ", duration1:" + duration1);
	    mScroller.startScroll( mScrollX , 0 , delta , 0 , duration/*Math.abs(delta) * 2*/);

	    mCurScreen = mNextScreen;
	    layoutChild( mCurScreen );
	    invalidate( ); // Redraw the layout
	}
    }

    protected void snapToFirstScreen()
    {
	// get the valid layout page
	mNextScreen = 0;
	if( mScrollX != 0 )
	{
	    if( mTouchScrollListener != null )
	    {
		mTouchScrollListener.onScreenChange( mNextScreen );
	    }
	    if( mScrollX > ( getChildCount( ) - 1 ) * getWidth( ) - 1 && mScrollX < getChildCount( ) * getWidth( ) - getWidth( ) / 2 )
	    {
		mScrollX = mScrollX - getChildCount( ) * getWidth( );
		mChangeCoordinate = false;
		scrollTo( mScrollX , mScrollY );
		mChangeCoordinate = true;
	    }
	    final int delta = 0 - mScrollX;
	    // int duration = (int) (1000 * Math.atan(Math.abs(mDurationCorr * delta))); // 0ms-1500ms
	    int duration = (int) ( 1000 * Math.atan( Math.abs( Math.PI * delta / 1000.0f ) ) ); // 0ms-1500ms
	    // TLog.v(TAG, "new:" + duration + ", old:" + Math.abs(delta) * 2 + ", duration1:" + duration1);
	    mScroller.startScroll( mScrollX , 0 , delta , 0 , duration/* Math.abs(delta) * 2 */);

	    mCurScreen = mNextScreen;
	    layoutChild( mCurScreen );
	    invalidate( ); // Redraw the layout
	}
    }

    /**
     * 显示下一屏幕
     */
    public void displayNextScreen()
    {
	if( mCurScreen < getChildCount( ) - 1 )
	{
	    setDisplayedChild( mCurScreen + 1 , true );
	}
	else
	{
	    snapToFirstScreen( );
	}
    }

    /**
     * 显示上一屏幕
     */
    public void displayPreScreen()
    {
	if( mCurScreen > 0 )
	{
	    setDisplayedChild( mCurScreen - 1 , true );
	}
	else
	{
	    snapToLastScreen( );
	}
    }

    public int getCurScreen()
    {
	return mCurScreen;
    }

    @Override
    public void computeScroll()
    {
	if( mScroller.computeScrollOffset( ) )
	{
	    scrollTo( mScroller.getCurrX( ) , mScroller.getCurrY( ) );
	    postInvalidate( );

	    // 状态回调
	    if( mTouchScrollListener != null )
	    {
		// 开始
		if( mScroller.isFinished( ) == false )
		{
		    if( mScrollState != OnTouchScrollListener.SCROLL_STATE_FLING )
		    {
			mTouchScrollListener.onScrollStateChanged( OnTouchScrollListener.SCROLL_STATE_FLING , mCurScreen );
			mScrollState = OnTouchScrollListener.SCROLL_STATE_FLING;
		    }
		}
		else
		{
		    // 结束
		    if( mScrollState != OnTouchScrollListener.SCROLL_STATE_IDLE )
		    {
			mTouchScrollListener.onScrollStateChanged( OnTouchScrollListener.SCROLL_STATE_IDLE , mCurScreen );
			mScrollState = OnTouchScrollListener.SCROLL_STATE_IDLE;
		    }
		}
	    }
	}
	else
	{
	    if( mTouchScrollListener != null )
	    {
		// 因为自由滚动结束后,还会调用一次computeScroll, 所以需要加入mTouchState的判断
		if( mScrollState != OnTouchScrollListener.SCROLL_STATE_TOUCH_SCROLL && mTouchState == TOUCH_STATE_SCROLLING )
		{
		    mTouchScrollListener.onScrollStateChanged( OnTouchScrollListener.SCROLL_STATE_TOUCH_SCROLL , mCurScreen );
		    mScrollState = OnTouchScrollListener.SCROLL_STATE_TOUCH_SCROLL;
		}
	    }
	}

	// 滚动回调, 类似 listView的onScroll
	if( mPreScrollX != getScrollX( ) )
	{
	    mPreScrollX = getScrollX( );
	    if( mTouchScrollListener != null )
	    {
		mTouchScrollListener.onScroll( this , getScrollX( ) , mScreenWidth );
	    }
	}
    }

    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
	if( !mEnableScroll )
	{
	    return true;
	}

	if( mVelocityTracker == null )
	{
	    mVelocityTracker = VelocityTracker.obtain( );
	}
	mVelocityTracker.addMovement( event );

	final int action = event.getAction( );
	final float x = event.getRawX( );
	final float y = event.getRawY( );

	switch ( action )
	{
	    case MotionEvent.ACTION_DOWN :
		if( !mScroller.isFinished( ) )
		{
		    mScroller.abortAnimation( );
		}
		mLastMotionX = x;
		break;

	    case MotionEvent.ACTION_MOVE :
		// 锁定,当手势向下滑动后,就不进行左右滑动
		if( mTouchState == TOUCH_STATE_LOCK )
		{
		    break;
		}
		if( getParent( ) != null )
		{
		    getParent( ).requestDisallowInterceptTouchEvent( true );
		}
		int deltaX = (int) ( mLastMotionX - x );
		mLastMotionX = x;
		int beScrollTo = getScrollX( ) + deltaX;
		if( mIsCircle || ( beScrollTo > mMinScrollX && beScrollTo < mMaxScrollX ) )
		{
		    scrollBy( deltaX , 0 );
		}
		break;
	    case MotionEvent.ACTION_UP :
		// if (mTouchState == TOUCH_STATE_SCROLLING) {
		final VelocityTracker velocityTracker = mVelocityTracker;
		velocityTracker.computeCurrentVelocity( 1000 );
		int velocityX = (int)velocityTracker.getXVelocity( );

		if( velocityX > SNAP_VELOCITY && mCurScreen >= 0 )
		{
		    // Fling enough to move left
		    int left = mCurScreen - 1;
		    if( mIsCircle && left < 0 )
		    {
			left += getChildCount( );
			snapToLastScreen( );
		    }
		    else
		    {
			snapToScreen( left , true );
		    }
		}
		else if( velocityX < -SNAP_VELOCITY && mCurScreen <= getChildCount( ) - 1 )
		{
		    // Fling enough to move right
		    int right = ( mCurScreen + 1 );
		    if( mIsCircle && right > getChildCount( ) - 1 )
		    {
			right -= getChildCount( );
			snapToFirstScreen( );
		    }
		    else
		    {
			snapToScreen( right , true );
		    }
		}
		else
		{
		    snapToDestination( );
		}

		if( mVelocityTracker != null )
		{
		    mVelocityTracker.recycle( );
		    mVelocityTracker = null;
		}
		// }
		mTouchState = TOUCH_STATE_REST;
		break;
	    case MotionEvent.ACTION_CANCEL :
		mTouchState = TOUCH_STATE_REST;
		break;
	}
	return true;
    }

    @Override
    public boolean onInterceptTouchEvent( MotionEvent ev )
    {
	if( mLockAllWhenTouch )
	{
	    getParent( ).requestDisallowInterceptTouchEvent( true );
	}
	if( !mEnableScroll )
	{
	    return false;
	}

	final int action = ev.getAction( );

	// 移动过程中,锁定
	if( ( action == MotionEvent.ACTION_MOVE ) && ( mTouchState == TOUCH_STATE_SCROLLING ) )
	{
	    return true;
	}

	final float x = ev.getRawX( );
	final float y = ev.getRawY( );

	switch ( action )
	{
	    case MotionEvent.ACTION_MOVE :
		if( mTouchState == TOUCH_STATE_LOCK )
		{
		    break;
		}
		final float xDiff = Math.abs( mLastMotionX - x );
		final float yDiff = Math.abs( mLastMotionY - y );

		if( xDiff > mTouchSlop )
		{
		    float tan = yDiff / xDiff;
		    if( tan < mAngleSlop )
		    {
			mTouchState = TOUCH_STATE_SCROLLING;
		    }
		    else
		    {
			mTouchState = TOUCH_STATE_LOCK;
		    }
		}
		break;

	    case MotionEvent.ACTION_DOWN :
		mLastMotionX = x;
		mLastMotionY = y;
		mTouchState = mScroller.isFinished( ) ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
		break;

	    case MotionEvent.ACTION_CANCEL :
	    case MotionEvent.ACTION_UP :
		mTouchState = TOUCH_STATE_REST;
		break;
	}

	return mTouchState == TOUCH_STATE_SCROLLING;
    }

    public void setDisplayedChild( int i )
    {
	// 有动画
	snapToScreen( i , false );
	// 无动画
	// setToScreen(i);
    }

    public void setDisplayedChild( int i , boolean callback )
    {
	// 有动画
	snapToScreen( i , callback );
	// 无动画
	// setToScreen(i);
    }

    // 无过度动画
    public void setDisplayedChildNoAmin( int whichScreen )
    {
	if( mCurScreen == whichScreen )
	{
	    return;
	}

	whichScreen = Math.max( 0 , Math.min( whichScreen , getChildCount( ) - 1 ) );
	mCurScreen = whichScreen;
	scrollTo( whichScreen * getWidth( ) , 0 );
	if( mTouchScrollListener != null )
	{
	    mTouchScrollListener.onScreenChange( whichScreen );
	}
	layoutChild( mCurScreen );
	invalidate( ); // Redraw the layout
    }

    public int getDisplayedChild()
    {
	return mCurScreen;
    }

    public void setOnTouchScrollListener( OnTouchScrollListener listener )
    {
	mTouchScrollListener = listener;
    }

    public OnTouchScrollListener getOnTouchScrollListener()
    {
	return mTouchScrollListener;
    }

    public void setTouchScrollEnable( boolean enable )
    {
	mEnableScroll = enable;
    }

    public boolean getEnableScroll()
    {
	return mEnableScroll;
    }

    public void setLockAllWhenTouch( boolean lock )
    {
	mLockAllWhenTouch = lock;
    }

    public void setDefaultScreem( int screem )
    {
	mDefaultScreen = screem;
	mCurScreen = mDefaultScreen;
    }

    public int getDefaultScreem()
    {
	return mDefaultScreen;
    }

    public void destroy()
    {
	mScroller = null;
	mVelocityTracker = null;
	mTouchScrollListener = null;
    }

    @Override
    public void scrollTo( int x , int y )
    {
	if( mIsCircle && mChangeCoordinate )
	{
	    final int width = getWidth( );
	    final int count = getChildCount( );
	    if( x <= -width / 2 )
	    {
		x = width * count + x;
	    }
	    else if( x >= width * count - width / 2 )
	    {
		x = x - width * count;
	    }
	}
	mScrollX = x;
	mScrollY = y;
	super.scrollTo( x , y );
    }

    public void setCircle( boolean circle )
    {
	mIsCircle = circle;
    }

    @Override
    protected void dispatchDraw( Canvas canvas )
    {
	if( !mIsCircle || mCurScreen >= getChildCount( ) || mCurScreen < 0 )
	{
	    super.dispatchDraw( canvas );
	    return;
	}

	// 循环滑动的绘制
	boolean restore = false;
	int restoreCount = 0;
	// ViewGroup.dispatchDraw() supports many features we don't need:
	// clip to padding, layout animation, animation listener, disappearing
	// children, etc. The following implementation attempts to fast-track
	// the drawing dispatch by drawing only what we know needs to be drawn.
	// cycleSlide modified:
	boolean fastDraw = mTouchState != TOUCH_STATE_SCROLLING && mNextScreen == INVALID_SCREEN;
	// If we are not scrolling or flinging, draw only the current screen
	if( fastDraw )
	{
	    drawChild( canvas , getChildAt( mCurScreen ) , getDrawingTime( ) );
	}
	else
	{
	    /*
	     * final long drawingTime = getDrawingTime(); final float scrollPos = (float) mScrollX / getWidth(); final int leftScreen =
	     * (int) scrollPos; final int rightScreen = leftScreen + 1; if (leftScreen >= 0) { drawChild(canvas, getChildAt(leftScreen),
	     * drawingTime); } if (scrollPos != leftScreen && rightScreen < getChildCount()) { drawChild(canvas, getChildAt(rightScreen),
	     * drawingTime); }
	     */
	    /*
	     * cycleSlide modified: 1、 slide is complete: draw CurScreen 2、 slide is running： draw leftScreen and rightScreen 3、 slide is
	     * cycle running: do translate
	     */
	    long drawingTime = getDrawingTime( );
	    int width = getWidth( );
	    float scrollPos = (float)getScrollX( ) / width;
	    boolean endlessScrolling = true;

	    int leftScreen;
	    int rightScreen;
	    boolean isScrollToRight = false;
	    int childCount = getChildCount( );
	    if( scrollPos < 0 && endlessScrolling )
	    {
		leftScreen = childCount - 1;
		rightScreen = 0;
	    }
	    else
	    {
		leftScreen = Math.min( (int)scrollPos , childCount - 1 );
		rightScreen = leftScreen + 1;
		if( endlessScrolling )
		{
		    rightScreen = rightScreen % childCount;
		    isScrollToRight = true;
		}
	    }
	    if( isScreenNoValid( leftScreen ) )
	    {
		if( rightScreen == 0 && !isScrollToRight )
		{ // 向左滑动，如果rightScreen是0
		    int offset = childCount * width;
		    canvas.translate( -offset , 0 );
		    drawChild( canvas , getChildAt( leftScreen ) , drawingTime );
		    canvas.translate( +offset , 0 );
		}
		else
		{
		    drawChild( canvas , getChildAt( leftScreen ) , drawingTime );
		}
	    }
	    if( scrollPos != leftScreen && isScreenNoValid( rightScreen ) )
	    {
		if( endlessScrolling && rightScreen == 0 && isScrollToRight )
		{
		    int offset = childCount * width;
		    canvas.translate( +offset , 0 );
		    drawChild( canvas , getChildAt( rightScreen ) , drawingTime );
		    canvas.translate( -offset , 0 );
		}
		else
		{
		    drawChild( canvas , getChildAt( rightScreen ) , drawingTime );
		}
	    }
	}
	if( restore )
	{
	    canvas.restoreToCount( restoreCount );
	}
    }

    private boolean isScreenNoValid( int screen )
    {
	return screen >= 0 && screen < getChildCount( );
    }
}
