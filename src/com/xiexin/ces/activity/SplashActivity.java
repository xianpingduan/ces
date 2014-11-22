package com.xiexin.ces.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.utils.Logger;

public class SplashActivity extends Activity {

	public final static String TAG = "SplashActivity";

	private boolean mIsSetSerVerConfig = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		//		test();

		String url = App.getSharedPreference().getString(
				Constants.SERVER_CONFIG_URL, "");
		String port = App.getSharedPreference().getString(
				Constants.SERVER_CONFIG_PORT, "");
		Constants.ROOT = url + ":" + port;
		Logger.d(TAG, "server url=" + url + ":" + port);
		if (url.isEmpty()) {
			mIsSetSerVerConfig = true;
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 加载数据，跳转到登录界面
				// TODO
				if (mIsSetSerVerConfig) {
					intentToServerConfig();
				} else {
					doRequestServerConfigInfo();
				}
			}
		}, 1000);
	}

	private final static int MSG_GET_SERVER_CONFOG_SUCCESS = 1;
	private final static int MSG_GET_SERVER_CONFOG_ERROR = 2;
	private Handler mUiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			switch (msg.what) {
			case MSG_GET_SERVER_CONFOG_SUCCESS:
				final boolean autoLogin = App.getSharedPreference().getBoolean(
						Constants.AUTO_LOGIN, false);
						if (autoLogin) {
							intentToMain();
						} else {
							intentToLogin();
						}
						finish();
				break;
			case MSG_GET_SERVER_CONFOG_ERROR:
				
				break;
			default:
				break;
			}
		}

	};

	// 加载服务器信息
	private void doRequestServerConfigInfo() {
		mUiHandler.sendEmptyMessage(MSG_GET_SERVER_CONFOG_SUCCESS);
	}

	private void intentToLogin() {
		Intent intent = new Intent();
		intent.setClass(SplashActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private void intentToServerConfig() {
		Intent intent = new Intent();
		intent.setClass(SplashActivity.this, ServerConfigActivity.class);
		startActivity(intent);
		finish();
	}

	private void intentToMain() {
		Intent intent = new Intent();
		intent.setClass(SplashActivity.this, MenuActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		return;
	}
	
	private void test(){
		
		try {
			JSONObject  obj=new JSONObject(Constants.RQ001);
			Log.d(TAG, obj.get("Data").toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
