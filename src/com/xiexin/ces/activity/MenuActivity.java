package com.xiexin.ces.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.fragment.HomeFragment;
import com.xiexin.ces.fragment.PendApprovalFragment;
import com.xiexin.ces.menu.ResideMenu;
import com.xiexin.ces.menu.ResideMenuItem;

public class MenuActivity extends FragmentActivity implements View.OnClickListener
{

    private ResideMenu resideMenu;
    private MenuActivity mContext;
    private ResideMenuItem itemPendApproval;
    private ResideMenuItem itemSendItem;
    private ResideMenuItem itemScratchUpcome;
    private ResideMenuItem itemApproved;
    private ResideMenuItem itemMessage;
    private ResideMenuItem itemAds;
    private ResideMenuItem itemInvoice;
    private ResideMenuItem itemRecord;

    // Fragment
    private Fragment mPendApprovalFragment;
    private Fragment mMessageFragment;
    private Fragment mTipFragment;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	setContentView( R.layout.main );
	mContext = this;
	setUpMenu( );
	if( savedInstanceState == null )
	    changeFragment( new HomeFragment( ) );
    }

    private void setUpMenu()
    {

	// attach to current activity;
	resideMenu = new ResideMenu( this );
	resideMenu.setBackground( R.drawable.menu_background );
	resideMenu.attachToActivity( this );
	resideMenu.setMenuListener( menuListener );
	// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
	// 150dip.
	resideMenu.setScaleValue( 0.6f );

	// 右边菜单不显示
	resideMenu.setSwipeDirectionDisable( ResideMenu.DIRECTION_RIGHT );

	// create menu items;
	itemPendApproval = new ResideMenuItem( this , R.drawable.icon_home , getString( R.string.menu_pend_approval ) );
	itemSendItem = new ResideMenuItem( this , R.drawable.icon_profile , getString( R.string.menu_sent_item ) );
	itemScratchUpcome = new ResideMenuItem( this , R.drawable.icon_calendar , getString( R.string.menu_scratch_upcome ) );
	itemApproved = new ResideMenuItem( this , R.drawable.icon_settings , getString( R.string.menu_approved ) );

	itemPendApproval.setOnClickListener( this );
	itemSendItem.setOnClickListener( this );
	itemScratchUpcome.setOnClickListener( this );
	itemApproved.setOnClickListener( this );

	itemMessage = new ResideMenuItem( this , R.drawable.icon_home , getString( R.string.menu_message ) );
	itemAds = new ResideMenuItem( this , R.drawable.icon_profile , getString( R.string.menu_ads ) );

	itemMessage.setOnClickListener( this );
	itemAds.setOnClickListener( this );

	itemInvoice = new ResideMenuItem( this , R.drawable.icon_calendar , getString( R.string.menu_invoice ) );
	itemRecord = new ResideMenuItem( this , R.drawable.icon_settings , getString( R.string.menu_record ) );

	itemInvoice.setOnClickListener( this );
	itemRecord.setOnClickListener( this );

	resideMenu.addMenuItem( itemPendApproval , ResideMenu.DIRECTION_LEFT );
	resideMenu.addMenuItem( itemSendItem , ResideMenu.DIRECTION_LEFT );
	resideMenu.addMenuItem( itemScratchUpcome , ResideMenu.DIRECTION_LEFT );
	resideMenu.addMenuItem( itemApproved , ResideMenu.DIRECTION_LEFT );

	resideMenu.addMenuItem( itemMessage , ResideMenu.DIRECTION_LEFT );
	resideMenu.addMenuItem( itemAds , ResideMenu.DIRECTION_LEFT );

	resideMenu.addMenuItem( itemInvoice , ResideMenu.DIRECTION_LEFT );
	resideMenu.addMenuItem( itemRecord , ResideMenu.DIRECTION_LEFT );

	// You can disable a direction by setting ->
	// resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

	findViewById( R.id.title_bar_left_menu ).setOnClickListener( new View.OnClickListener( )
	{
	    @Override
	    public void onClick( View view )
	    {
		resideMenu.openMenu( ResideMenu.DIRECTION_LEFT );
	    }
	} );
	findViewById( R.id.title_bar_right_menu ).setOnClickListener( new View.OnClickListener( )
	{
	    @Override
	    public void onClick( View view )
	    {
		resideMenu.openMenu( ResideMenu.DIRECTION_RIGHT );
	    }
	} );
    }

    @Override
    public boolean dispatchTouchEvent( MotionEvent ev )
    {
	return resideMenu.dispatchTouchEvent( ev );
    }

    @Override
    public void onClick( View view )
    {

	if( view == itemPendApproval )
	{
	    if( mPendApprovalFragment == null )
	    {
		mPendApprovalFragment = new PendApprovalFragment( );
		// ((PendApprovalFragment)
		// mPendApprovalFragment).setKind(Constants.TYPE_PEND_APPROVAL_TASKS);
	    }
	    changeFragment( mPendApprovalFragment );
	}
	else if( view == itemSendItem )
	{
	    if( mPendApprovalFragment == null )
	    {
		mPendApprovalFragment = new PendApprovalFragment( );
		// ((PendApprovalFragment)
		// mPendApprovalFragment).setKind(Constants.TYPE_SEND_ITEM_TASKS);
	    }
	    changeFragment( mPendApprovalFragment );
	}
	else if( view == itemScratchUpcome )
	{
	    if( mPendApprovalFragment == null )
	    {
		mPendApprovalFragment = new PendApprovalFragment( );
		// ((PendApprovalFragment)
		// mPendApprovalFragment).setKind(Constants.TYPE_SCRATCH_UPCOME_TASKS);
	    }
	    changeFragment( mPendApprovalFragment );
	}
	else if( view == itemApproved )
	{
	    if( mPendApprovalFragment == null )
	    {
		mPendApprovalFragment = new PendApprovalFragment( );
		// ((PendApprovalFragment)
		// mPendApprovalFragment).setKind(Constants.TYPE_APPROVED_TASKS);
	    }
	    changeFragment( mPendApprovalFragment );
	}
	else if( view == itemMessage )
	{

	    if( mMessageFragment == null )
	    {
		mMessageFragment = new PendApprovalFragment( );
	    }
	    changeFragment( mMessageFragment );
	}
	else if( view == itemAds )
	{
	    if( mTipFragment == null )
	    {
		mTipFragment = new PendApprovalFragment( );
	    }
	    changeFragment( mTipFragment );
	}
	else if( view == itemInvoice )
	{
	    if( mTipFragment == null )
	    {
		mTipFragment = new PendApprovalFragment( );
	    }
	    changeFragment( mTipFragment );
	}
	else if( view == itemRecord )
	{
	    if( mTipFragment == null )
	    {
		mTipFragment = new PendApprovalFragment( );
	    }
	    changeFragment( mTipFragment );
	}

	resideMenu.closeMenu( );
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener( )
    {
	@Override
	public void openMenu()
	{
	    Toast.makeText( mContext , "Menu is opened!" , Toast.LENGTH_SHORT ).show( );
	}

	@Override
	public void closeMenu()
	{
	    Toast.makeText( mContext , "Menu is closed!" , Toast.LENGTH_SHORT ).show( );
	}
    };

    private void changeFragment( Fragment targetFragment )
    {
	resideMenu.clearIgnoredViewList( );
	getSupportFragmentManager( ).beginTransaction( ).replace( R.id.main_fragment , targetFragment , "fragment" ).setTransitionStyle( FragmentTransaction.TRANSIT_FRAGMENT_FADE ).commit( );
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu()
    {
	return resideMenu;
    }

    private long lastClickKeyBackTime;

    //    private boolean isStopping = false;

    @Override
    public void onBackPressed()
    {
	// TODO Auto-generated method stub
	super.onBackPressed( );

	long now = System.currentTimeMillis( );
	if( now - lastClickKeyBackTime > 2000 )
	{
	    Toast.makeText( MenuActivity.this , getString( R.string.click_exit_label ) , Toast.LENGTH_SHORT ).show( );
	    lastClickKeyBackTime = now;
	}
	else
	{
	    //	    if( !isStopping )
	    //	    {
	    //		isStopping = true;
	    //	    }
	    finish( );
	    boolean autoLogin = App.getSharedPreference( ).getBoolean( Constants.AUTO_LOGIN , false );
	    boolean remeberPwd = App.getSharedPreference( ).getBoolean( Constants.REMEBER_PWD , false );

	    if( autoLogin == false && remeberPwd == false )
	    {
		App.clear( );
	    }
	}

    }
}
