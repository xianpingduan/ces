package com.xiexin.ces.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiexin.ces.R;

/**
 * User: special
 * Date: 13-12-10
 * Time: 下午11:05
 * Mail: specialcyci@gmail.com
 */
public class ResideMenuItem extends LinearLayout
{

    /** menu item  icon  */
    private ImageView iv_icon;
    /** menu item  title */
    private TextView tv_title;
    private FrameLayout fl_badge;
    private TextView tv_badge_num;

    public TextView getTv_badge_num() {
		return tv_badge_num;
	}

	public FrameLayout getFl_badge() {
		return fl_badge;
	}

	public ResideMenuItem( Context context )
    {
	super( context );
	initViews( context );
    }

    public ResideMenuItem( Context context , int icon , int title )
    {
	super( context );
	initViews( context );
	iv_icon.setImageResource( icon );
	tv_title.setText( title );
    }

    public ResideMenuItem( Context context , int icon , String title )
    {
	super( context );
	initViews( context );
	iv_icon.setImageResource( icon );
	tv_title.setText( title );
    }

    private void initViews( Context context )
    {
	LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	inflater.inflate( R.layout.residemenu_item , this );
	iv_icon = (ImageView)findViewById( R.id.iv_icon );
	tv_title = (TextView)findViewById( R.id.tv_title );
	fl_badge = (FrameLayout) findViewById(R.id.fl_badge);
	tv_badge_num = (TextView) findViewById(R.id.tv_badge_num);
    }

    /**
     * set the icon color;
     *
     * @param icon
     */
    public void setIcon( int icon )
    {
	iv_icon.setImageResource( icon );
    }

    /**
     * set the title with resource
     * ;
     * @param title
     */
    public void setTitle( int title )
    {
	tv_title.setText( title );
    }

    /**
     * set the title with string;
     *
     * @param title
     */
    public void setTitle( String title )
    {
	tv_title.setText( title );
    }
}
