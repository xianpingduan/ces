package com.xiexin.ces.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.xiexin.ces.R;

public class LoadingDialog extends Dialog
{
    private String tiptext;

    public LoadingDialog( Context context )
    {
	super( context , R.style.MyDialog );
    }

    public LoadingDialog( Context context , String tip )
    {
	this( context );
	tiptext = tip;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	setContentView( R.layout.loading );
	TextView tipTv = (TextView)findViewById( R.id.tip );
	if( !TextUtils.isEmpty( tiptext ) )
	{
	    tipTv.setText( tiptext );
	}
    }

}
