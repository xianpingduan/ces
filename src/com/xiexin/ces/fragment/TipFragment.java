package com.xiexin.ces.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiexin.ces.R;
import com.xiexin.ces.utils.Logger;

/**
 * User: special Date: 13-12-22 Time: 下午3:28 Mail: specialcyci@gmail.com
 */
public class TipFragment extends Fragment {

	private static final String TAG = "TipFragment";

	private Handler mMainUIHandler;
	public void setMainUIHandler(Handler handler){
		mMainUIHandler = handler;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Logger.d(TAG, "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d(TAG, "onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_tip, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.d(TAG, "onResume");
	}

}
