package com.xiexin.ces.widgets;

import java.util.List;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiexin.ces.App;
import com.xiexin.ces.R;
import com.xiexin.ces.utils.APNUtil;

public class ImageGalleryAdapter extends BaseAdapter
{

    private final Context mContext;

    private List< String > mImgPathList;

    public static final int PORTRAIT = 1;

    public static final int LANDSCAPE = 2;

    private ImageLoader imageLoader;

    public ImageGalleryAdapter( Context context )
    {

	mContext = context;
	imageLoader = ImageLoader.getInstance( );

    }

    public void setData( List< String > imgList )
    {
	mImgPathList = imgList;
    }
    
    
    public static int getWindowsWidthPixel()
    {
	DisplayMetrics dm = App.getAppContext( ).getResources( ).getDisplayMetrics( );
	return dm.widthPixels;
    }

    public static int getWindowsHeightPixel()
    {
	DisplayMetrics dm = App.getAppContext( ).getResources( ).getDisplayMetrics( );
	return dm.heightPixels;
    }


    @Override
    public View getView( int position , View convertView , ViewGroup parent )
    {
	ImageView imgView;
	if( convertView == null )
	{
	    convertView = LayoutInflater.from( mContext ).inflate( R.layout.image_gallery_item , null );

	    imgView = (ImageView)convertView.findViewById( R.id.img_item );
	    android.view.ViewGroup.LayoutParams ps = imgView.getLayoutParams( );

//	    if( UITools.isPortrait( ) )
//	    {
//		ps.width = getWindowsHeightPixel / 16 * 8;
//	    }
//	    else
//	    {
//		ps.width = UITools.getWindowsWidthPixel( ) / 5 * 3;
//	    }
//	    ps.width = getWindowsHeightPixel() / 16 * 8;

	    imgView.setLayoutParams( ps );
	    convertView.setTag( imgView );

	}
	else
	{
	    imgView = (ImageView)convertView.getTag( );
	}
	
	if(APNUtil.isWifiDataEnable(App.getAppContext())){
		imageLoader.displayImage( mImgPathList.get( position ) , imgView );
	}

	//	ImageView imgView = (ImageView)convertView.findViewById( R.id.img_item );
	//	ProgressBar progressBar = (ProgressBar)convertView.findViewById( R.id.loading_progress );
	//
	//	Bitmap bitmap = mIconManager.getImage( mImgPathList.get( position ) , null , true );
	//	if( bitmap != null )
	//	{
	//	    imgView.setImageBitmap( bitmap );
	//	    imgView.setVisibility( View.VISIBLE );
	//	}
	return convertView;
    }

    @Override
    public int getCount()
    {
	if( mImgPathList != null )
	{
	    return mImgPathList.size( );
	}
	else
	{
	    return 0;
	}
    }

    @Override
    public Object getItem( int position )
    {
	if( position >= 0 && mImgPathList != null && position < mImgPathList.size( ) )
	{
	    return mImgPathList.get( position );
	}
	else
	{
	    return null;
	}
    }

    @Override
    public long getItemId( int position )
    {
	return position;
    }

}
