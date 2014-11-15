package com.xiexin.ces;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;

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

    @Override
    public void onCreate()
    {
	super.onCreate( );

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
	mSharePrefences.edit( ).putString( Constants.DEPART , "" ).commit( );
	mSharePrefences.edit( ).putString( Constants.TITLE , "" ).commit( );
	mSharePrefences.edit( ).putString( Constants.JOB , "" ).commit( );
	mSharePrefences.edit( ).putBoolean( Constants.LOCKED , false ).commit( );
	mSharePrefences.edit( ).putString( Constants.ZHANG_TAO_ACCINFO , "" ).commit( );
	mSharePrefences.edit( ).putString( Constants.ZHANG_TAO_CONN_NAME , "" ).commit( );
	mSharePrefences.edit( ).putString( Constants.ZHANG_TAO_LIST , "" ).commit( );

	mSharePrefences.edit( ).putBoolean( Constants.REMEBER_PWD , false ).commit( );
	mSharePrefences.edit( ).putBoolean( Constants.AUTO_LOGIN , false ).commit( );
    }

}
