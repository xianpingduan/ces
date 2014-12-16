package com.xiexin.ces.db;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pada.juidownloadmanager.utils.ThreadTask;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.xiexin.ces.db.EmployeeDbAdapter.EmployeeInfoColumns;
import com.xiexin.ces.entry.Employee;

public class EmployeeManager
{

    private static EmployeeManager mEmployeeManager;

    private EmployeeDbAdapter mEmployeeDbAdapter;

    public static EmployeeManager getInstance( Context context )
    {
	if( mEmployeeManager == null )
	{
	    mEmployeeManager = new EmployeeManager( context );
	}
	return mEmployeeManager;
    }

    private EmployeeManager( Context context )
    {
	mEmployeeDbAdapter = new EmployeeDbAdapter( context );
    }

    private ContentValues createContentValues( Employee employee )
    {
	ContentValues values = new ContentValues( );
	values.put( EmployeeInfoColumns.EMPLOYEE_ID , employee.getEmployeeID( ) );
	values.put( EmployeeInfoColumns.SEX , employee.getSex( ) );
	values.put( EmployeeInfoColumns.DESCR , employee.getDescr( ) );
	values.put( EmployeeInfoColumns.DEPART , employee.getDepart( ) );
	values.put( EmployeeInfoColumns.JOB , employee.getJob( ) );
	values.put( EmployeeInfoColumns.MOBILE , employee.getMobile( ) );
	values.put( EmployeeInfoColumns.TELNBR , employee.getTelNbr( ) );
	values.put( EmployeeInfoColumns.EMIAL , employee.getEmail( ) );
	return values;
    }

    private Employee createTaskFromCursor( Cursor cursor )
    {
	Employee employee = new Employee( );

	employee.setEmployeeID( cursor.getString( 1 ) );
	employee.setDescr( cursor.getString( 2 ) );
	employee.setSex( cursor.getString( 3 ) );
	employee.setDepart( cursor.getString( 4 ) );
	employee.setJob( cursor.getString( 5 ) );
	employee.setMobile( cursor.getString( 6 ) );
	employee.setTelNbr( cursor.getString( 7 ) );
	employee.setEmail( cursor.getString( 8 ) );

	return employee;
    }

    public synchronized void saveEmpToDB( final Employee employee )
    {
	ThreadTask.postTask( new Runnable( )
	{
	    @Override
	    public void run()
	    {
		mEmployeeDbAdapter.insert( createContentValues( employee ) );
	    }
	} );

    }

    public synchronized void updateEmpToDB( final Employee employee )
    {
	ThreadTask.postTask( new Runnable( )
	{
	    @Override
	    public void run()
	    {
		mEmployeeDbAdapter.update( employee.getEmployeeID( ) , createContentValues( employee ) );
	    }
	} );

    }

    public Employee findEmployeeById( String eId )
    {
	Cursor cursor = mEmployeeDbAdapter.queryById( eId );

	if( cursor != null && cursor.getCount( ) > 0 )
	{
	    return createTaskFromCursor( cursor );
	}
	return null;
    }

    public void saveResult( String jsonStr )
    {
	ArrayList< Employee > employeeList = new ArrayList< Employee >( );
	try
	{
	    JSONArray arrays = new JSONArray( jsonStr );
	    for( int i = 0 ; i < arrays.length( ) ; i++ )
	    {
		JSONObject obj = arrays.getJSONObject( i );
		Employee employee = new Employee( );
		employee.setEmployeeID( obj.getString( "EmployeeID" ) );
		employee.setDescr( obj.getString( "Descr" ) );
		employee.setDepart( obj.getString( "Depart" ) );
		employee.setJob( obj.getString( "Job" ) );
		employee.setSex( obj.getString( "Sex" ) );
		employee.setEmail( obj.getString( "Email" ) );
		employee.setMobile( obj.getString( "Mobile" ) );
		employee.setTelNbr( obj.getString( "TelNbr" ) );
		employeeList.add( employee );
	    }
	}
	catch ( JSONException e )
	{
	    e.printStackTrace( );
	}
	new Thread( new SaveDataToDBRunnable( employeeList ) ).start( );
    }

    //clear
    public void delAll()
    {
	mEmployeeDbAdapter.delAll( );
    }

    private class SaveDataToDBRunnable implements Runnable
    {

	private final List< Employee > data;

	private SaveDataToDBRunnable( List< Employee > data )
	{
	    this.data = data;
	}

	@Override
	public void run()
	{

	    for( Employee employee : data )
	    {
		insertRecord( employee );
	    }
	}

	private void insertRecord( Employee employee )
	{
	    mEmployeeDbAdapter.insert( createContentValues( employee ) );
	}
    }

}
