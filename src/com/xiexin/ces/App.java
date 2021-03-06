package com.xiexin.ces;

import org.xutils.x;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.xiexin.ces.db.EmployeeManager;
import com.xiexin.ces.utils.Logger;

public class App extends Application
{

    private static final String TAG = "App";

    private static App mAppContext = null;
    private static LayoutInflater mLayoutInflater = null;
    private static int mVerCode;
    private static String mVerName;
    private static String mPackageName;
    private static SharedPreferences mSharePrefences;
    // 常量
    private final static String SHARED_PERFERENCE_NAME = "ces.conf";

    public static App getAppContext()
    {
	return mAppContext;
    }

    public static LayoutInflater getLayoutInflater()
    {
	return mLayoutInflater;
    }

    public static SharedPreferences getSharedPreference()
    {
	return mSharePrefences;
    }

    public static String getRootUrl()
    {
	String rootUrl = App.getSharedPreference( ).getString( Constants.SERVER_CONFIG_URL , "http://210.75.12.178" );
	String port = App.getSharedPreference( ).getString( Constants.SERVER_CONFIG_PORT , "8091" );

	Logger.d( TAG , "App.getRootUrl=" + rootUrl + ":" + port + "/api/CESApp/" );
	return rootUrl + ":" + port + "/api/CESApp/";

    }

    @Override
    public void onCreate()
    {
	super.onCreate( );

	x.Ext.init(this);
	x.Ext.setDebug(true);
	
	mAppContext = (App)getApplicationContext( );
	mLayoutInflater = (LayoutInflater)getSystemService( Context.LAYOUT_INFLATER_SERVICE );

	try
	{
	    mVerName = getPackageManager( ).getPackageInfo( App.getAppContext( ).getPackageName( ) , 0 ).versionName;
	    mVerCode = getPackageManager( ).getPackageInfo( App.getAppContext( ).getPackageName( ) , 0 ).versionCode;
	    mPackageName = getPackageManager( ).getPackageInfo( App.getAppContext( ).getPackageName( ) , 0 ).packageName;
	}
	catch ( NameNotFoundException e )
	{
	    e.printStackTrace( );
	}

	ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder( this ).threadPriority( Thread.NORM_PRIORITY - 2 ).discCacheFileNameGenerator( new Md5FileNameGenerator( ) )
		.tasksProcessingOrder( QueueProcessingType.LIFO ).discCacheSize( 50 * 1024 * 1024 ).threadPoolSize( 4 )
		.memoryCache(new UsingFreqLimitedMemoryCache(2000000))
		// .writeDebugLogs() // Remove
		// for
		// release
		// app
		.build( );
	// Initialize ImageLoader with configuration.
	ImageLoader.getInstance( ).init( config );

	mSharePrefences = getSharedPreferences( SHARED_PERFERENCE_NAME , 0 );
	
    }

    @Override
    public void onTerminate()
    {

	mLayoutInflater = null;
	mSharePrefences = null;

	super.onTerminate( );
    }

    public static String PackageName()
    {
	return mPackageName;
    }

    public static int VersionCode()
    {
	return mVerCode;
    }

    public static String VersionName() 
    {
	return mVerName;
    }

    public static void clear()
    {
	//	mSharePrefences.edit( ).putBoolean( key , value )
	//保存数据
	mSharePrefences.edit( ).putString( Constants.USER_ID , "" ).commit( );
	mSharePrefences.edit( ).putString( Constants.PWD , "" ).commit( );
	mSharePrefences.edit( ).putString( Constants.PWD_MD5 , "" ).commit( );
	mSharePrefences.edit( ).putString( Constants.DEPART , "" ).commit( );
	mSharePrefences.edit( ).putString( Constants.TITLE , "" ).commit( );
	mSharePrefences.edit( ).putString( Constants.JOB , "" ).commit( );
	mSharePrefences.edit( ).putBoolean( Constants.LOCKED , false ).commit( );
	mSharePrefences.edit( ).putString( Constants.ZHANG_TAO_ACCINFO , "" ).commit( );
	mSharePrefences.edit( ).putString( Constants.ZHANG_TAO_CONN_NAME , "" ).commit( );
	mSharePrefences.edit( ).putString( Constants.ZHANG_TAO_LIST , "" ).commit( );

	mSharePrefences.edit( ).putBoolean( Constants.REMEBER_PWD , false ).commit( );
	mSharePrefences.edit( ).putBoolean( Constants.AUTO_LOGIN , false ).commit( );

	EmployeeManager.getInstance( mAppContext ).delAll( );

	Logger.d( TAG , "clear login info" );
    }

}
