package com.xiexin.ces.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;

import com.xiexin.ces.R;
import com.xiexin.ces.widgets.DotProgressBar;
import com.xiexin.ces.widgets.ImageGalleryAdapter;
import com.xiexin.ces.widgets.OverScrollGallery;

public class ImageGalleryActivity extends Activity implements OnItemSelectedListener
{

    private static final String TAG = "ImageGalleryActivity";

    //    private ImageManager mIconManager;
    private ImageView mImageView;
    //    private ImageSlideGallery mGallery;
    private OverScrollGallery mGallery;
    private DotProgressBar mDotProgressBar;
    private ImageGalleryAdapter mAdapter;
    private ArrayList< String > urls;
    private String url = "";
    private int urlPosition = 0;
    public static final String URL_DATA = "urls";
    public static final String ORIENTATION = "orientation";
    public static final String URL = "url";

    //    private ImageLoader imageLoader;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	setContentView( R.layout.activity_image_gallery );
	//	imageLoader = ImageLoader.getInstance( );
	//	mIconManager = ImageManager.getInstance( );
	urls = getIntent( ).getStringArrayListExtra( URL_DATA );
	url = getIntent( ).getStringExtra( URL );
	for( int i = 0 ; i < urls.size( ) ; i++ )
	{
	    if( urls.get( i ).equals( url ) )
	    {
		urlPosition = i;
		break;
	    }
	}
	initialViews( );
    }

    private void initialViews()
    {
	//	getImgs( );

	mAdapter = new ImageGalleryAdapter( this );
	mAdapter.setData( urls );
	//	mAdapter.setOrientation( getIntent( ).getIntExtra( ImageGalleryActivity.ORIENTATION , ImageGalleryAdapter.LANDSCAPE ) );
	mGallery = (OverScrollGallery)findViewById( R.id.img_gallery );
	mGallery.setAdapter( mAdapter );
	mGallery.setSelection( urlPosition );
	mGallery.setUnselectedAlpha( 1f );
	mGallery.setOnItemSelectedListener( this );

	mImageView = (ImageView)findViewById( R.id.close_image );
	mImageView.setOnClickListener( new OnClickListener( )
	{
	    @Override
	    public void onClick( View v )
	    {
		finish( );
	    }
	} );

	mDotProgressBar = (DotProgressBar)findViewById( R.id.dot );
	mDotProgressBar.setDotbarIconResource( R.drawable.guide_dot_white , R.drawable.guide_dot_black );
	mDotProgressBar.setVisibility( View.VISIBLE );
	mDotProgressBar.setCurProgress( urlPosition );
	mDotProgressBar.setTotalNum( urls.size( ) );
    }

    @Override
    protected void onResume()
    {
	super.onResume( );
    }

    @Override
    protected void onPause()
    {
	super.onPause( );
    }

    @Override
    public void onItemSelected( AdapterView< ? > parent , View view , int position , long id )
    {
	mDotProgressBar.setCurProgress( position );
    }

    @Override
    public void onNothingSelected( AdapterView< ? > parent )
    {

    }

    @Override
    public void onConfigurationChanged( Configuration newConfig )
    {

	int position = mGallery.getSelectedItemPosition( );
	mAdapter.notifyDataSetChanged( );
	mGallery.setSelection( position );
	mDotProgressBar.setCurProgress( position );
	super.onConfigurationChanged( newConfig );
    }

}
