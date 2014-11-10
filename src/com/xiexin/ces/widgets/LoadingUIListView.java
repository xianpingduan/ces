package com.xiexin.ces.widgets;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.xiexin.ces.widgets.LoadingView.IListViewLoadingRetry;

public class LoadingUIListView extends PullListView implements IListViewLoadingRetry
{
    private final String tag = "PadaLoadingUIListView";

    private LoadingView mLoadingView;
    private ILoadingViewListener mListener;

    private final int LLVS_LOADING = 0x01;
    private final int LLVS_NONETWORK = 0x02;
    private final int LLVS_NORMAL = 0x03;

    public LoadingUIListView( Context context )
    {
	super( context );
	initView( context );
    }

    public LoadingUIListView( Context context , AttributeSet attrs )
    {
	super( context , attrs );
	initView( context );
    }

    public LoadingUIListView( Context context , AttributeSet attrs , int defStyle )
    {
	super( context , attrs , defStyle );
	initView( context );
    }

    public void setPadaLoadingViewListener( ILoadingViewListener listener )
    {
	mListener = listener;
    }

    public void setLoadingTipText( String tip )
    {
	if( mLoadingView != null )
	{
	    mLoadingView.setLoadingTipText( tip );
	}
    }

    public void hideLoadingUI()
    {
	if( mLoadingView != null )
	{
	    mLoadingView.hide( );
	}
    }

    public void showLoadingUI()
    {
	if( mLoadingView != null )
	{
	    mLoadingView.showLoading( );
	}
    }

    private void initView( Context context )
    {
	mLoadingView = new LoadingView( context );
	mLoadingView.setOnRetryListener( this );
    }

    public void initRequestData()
    {
	if( mListener == null )
	    return;

	setViewStatus( LLVS_LOADING );
	mListener.onInitRequestData( );
    }

    private void setViewStatus( int nStatus )
    {
	switch ( nStatus )
	{
	    case LLVS_LOADING :
		//setVisibility(View.GONE);
		mLoadingView.showLoading( );
		break;
	    case LLVS_NONETWORK :
		//setVisibility(View.GONE);
		mLoadingView.showNoNetwork( );
		break;
	    case LLVS_NORMAL :
		//setVisibility(View.VISIBLE);
		mLoadingView.hide( );
		break;

	    default :
		break;
	}
    }

    @Override
    public void setAdapter( ListAdapter adapter )
    {
	super.setAdapter( adapter );

	adapter.registerDataSetObserver( mDataSetObserver );

	ViewGroup viewGroup = (ViewGroup)getParent( );
	if( viewGroup != null )
	{
	    viewGroup.addView( mLoadingView );
	    setEmptyView( mLoadingView );
	}
    }

    private final DataSetObserver mDataSetObserver = new DataSetObserver( )
    {

	@Override
	public void onChanged()
	{
	    ListAdapter adapter = getAdapter( );

	    int nListCount = adapter.getCount( );
	    if( isAddedFooter( ) )
		nListCount--;
	    if( isAddedHeader( ) )
		nListCount--;

	    Log.d( tag , "onChanged nListCount=" + nListCount );
	    if( nListCount > 0 )
	    {
		setViewStatus( LLVS_NORMAL );
	    }
	}

	@Override
	public void onInvalidated()
	{
	    //			ListAdapter adapter = getAdapter();
	    //
	    //			int nListCount = adapter.getCount();
	    //			if (isAddedFooter())
	    //				nListCount--;
	    //			if (isAddedHeader())
	    //				nListCount--;
	    //
	    //			if (nListCount <= 0) {
	    //				setViewStatus(LLVS_NONETWORK);
	    //			}
	    //getAdapter().isEmpty() ;

	    Log.d( tag , "onInvalidated" );
	    setViewStatus( LLVS_NONETWORK );
	}
    };

    @Override
    public void onRetry()
    {
	setViewStatus( LLVS_LOADING );
	if( mListener == null )
	    return;
	mListener.onRetryRequestData( );
    }

}
