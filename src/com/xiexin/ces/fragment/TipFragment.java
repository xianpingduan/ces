package com.xiexin.ces.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiexin.ces.R;

/**
 * User: special
 * Date: 13-12-22
 * Time: 下午3:28
 * Mail: specialcyci@gmail.com
 */
public class TipFragment extends Fragment
{

    @Override
    public View onCreateView( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState )
    {
	return inflater.inflate( R.layout.fragment_tip , container , false );
    }

}
