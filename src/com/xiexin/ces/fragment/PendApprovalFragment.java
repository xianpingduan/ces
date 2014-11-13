package com.xiexin.ces.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiexin.ces.R;

/**
 * User: special
 * Date: 13-12-22
 * Time: 下午3:26
 * Mail: specialcyci@gmail.com
 */
public class PendApprovalFragment extends Fragment implements OnClickListener
{

    private View parentView;
    private ListView listView;

    //header start
    private LinearLayout mReturnLl;
    private ImageView mReturnIv;
    private TextView mReturnTv;
    private TextView mTitle;
    private Button mBtn1;
    private Button mBtn2;

    //header end

    @Override
    public View onCreateView( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState )
    {
	parentView = inflater.inflate( R.layout.fragment_pend_approval , container , false );
	listView = (ListView)parentView.findViewById( R.id.listView );

	//header start
	mReturnLl = (LinearLayout)parentView.findViewById( R.id.return_ll );
	mReturnIv = (ImageView)parentView.findViewById( R.id.return_iv );
	mReturnTv = (TextView)parentView.findViewById( R.id.return_tv );
	mTitle = (TextView)parentView.findViewById( R.id.title );
	mBtn1 = (Button)parentView.findViewById( R.id.btn1 );
	mBtn2 = (Button)parentView.findViewById( R.id.btn2 );
	///header end

	mReturnTv.setVisibility( View.GONE );
	mTitle.setText( getString( R.string.server_config ) );
	//	mBtn1.setText( getString( R.string.finish_to ) );
	//	mBtn1.setOnClickListener( this );

	mReturnIv.setOnClickListener( this );

	initView( );
	return parentView;
    }

    private void initView()
    {
	ArrayAdapter< String > arrayAdapter = new ArrayAdapter< String >( getActivity( ) , android.R.layout.simple_list_item_1 , getCalendarData( ) );
	listView.setAdapter( arrayAdapter );
	listView.setOnItemClickListener( new AdapterView.OnItemClickListener( )
	{
	    @Override
	    public void onItemClick( AdapterView< ? > adapterView , View view , int i , long l )
	    {
		Toast.makeText( getActivity( ) , "Clicked item!" , Toast.LENGTH_LONG ).show( );
	    }
	} );
    }

    private ArrayList< String > getCalendarData()
    {
	ArrayList< String > calendarList = new ArrayList< String >( );
	calendarList.add( "New Year's Day" );
	calendarList.add( "St. Valentine's Day" );
	calendarList.add( "Easter Day" );
	calendarList.add( "April Fool's Day" );
	calendarList.add( "Mother's Day" );
	calendarList.add( "Memorial Day" );
	calendarList.add( "National Flag Day" );
	calendarList.add( "Father's Day" );
	calendarList.add( "Independence Day" );
	calendarList.add( "Labor Day" );
	calendarList.add( "Columbus Day" );
	calendarList.add( "Halloween" );
	calendarList.add( "All Soul's Day" );
	calendarList.add( "Veterans Day" );
	calendarList.add( "Thanksgiving Day" );
	calendarList.add( "Election Day" );
	calendarList.add( "Forefather's Day" );
	calendarList.add( "Christmas Day" );
	return calendarList;
    }

    @Override
    public void onClick( View v )
    {
	switch ( v.getId( ) )
	{
	    case R.id.return_iv :

		break;

	    default :
		break;
	}

    }
}
