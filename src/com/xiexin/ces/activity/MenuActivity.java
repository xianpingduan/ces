package com.xiexin.ces.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.xiexin.ces.R;
import com.xiexin.ces.fragment.CalendarFragment;
import com.xiexin.ces.fragment.HomeFragment;
import com.xiexin.ces.fragment.ProfileFragment;
import com.xiexin.ces.fragment.SettingsFragment;
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
	//valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip. 
	resideMenu.setScaleValue( 0.6f );
	
	//右边菜单不显示
	resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

	// create menu items;
	itemPendApproval = new ResideMenuItem( this , R.drawable.icon_home , getString(R.string.menu_pend_approval) );
	itemSendItem = new ResideMenuItem( this , R.drawable.icon_profile , getString(R.string.menu_sent_item) );
	itemScratchUpcome = new ResideMenuItem( this , R.drawable.icon_calendar , getString(R.string.menu_scratch_upcome) );
	itemApproved = new ResideMenuItem( this , R.drawable.icon_settings ,getString(R.string.menu_approved) );

	itemPendApproval.setOnClickListener( this );
	itemSendItem.setOnClickListener( this );
	itemScratchUpcome.setOnClickListener( this );
	itemApproved.setOnClickListener( this );
	
	itemMessage = new ResideMenuItem( this , R.drawable.icon_home , getString(R.string.menu_message) );
	itemAds = new ResideMenuItem( this , R.drawable.icon_profile , getString(R.string.menu_ads) );
	
	itemMessage.setOnClickListener( this );
	itemAds.setOnClickListener( this );
	
	itemInvoice = new ResideMenuItem( this , R.drawable.icon_calendar , getString(R.string.menu_invoice) );
	itemRecord = new ResideMenuItem( this , R.drawable.icon_settings ,getString(R.string.menu_record) );


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
	    changeFragment( new HomeFragment( ) );
	}
	else if( view == itemSendItem )
	{
	    changeFragment( new ProfileFragment( ) );
	}
	else if( view == itemScratchUpcome )
	{
	    changeFragment( new CalendarFragment( ) );
	}
	else if( view == itemApproved )
	{
	    changeFragment( new SettingsFragment( ) );
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
}
