package com.xiexin.ces.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.service.MessageService;
import com.xiexin.ces.utils.Logger;

public class PushScreenOnReceiver extends BroadcastReceiver {

	private static final String TAG = PushScreenOnReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {

		Logger.d(TAG, "onReceive =" + intent.getAction().toString());

		long currentTime = System.currentTimeMillis();
		long nextReqTime = App.getSharedPreference().getLong(
				Constants.THE_LAST_REQUEST_MSG_TIME, 0);
		long nextReqApprovalTime = App.getSharedPreference().getLong(
				Constants.THE_LAST_REQUEST_APPROVAL_TIME, 0);

		if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			if (currentTime >= nextReqTime) {
				Logger.d(TAG, "onReceive,requestMsg");
				requestMsg(context);
			}

					if (currentTime >= nextReqApprovalTime) {
						Logger.d(TAG, "onReceive,requestApproval");
						requestApproval(context);
					}

		} else if (intent.getAction().equals(
				ConnectivityManager.CONNECTIVITY_ACTION)) {
			if (currentTime >= nextReqTime) {
				Logger.d(TAG, "onReceive,requestMsg");
				requestMsg(context);
			}

            if (currentTime >= nextReqApprovalTime)
            {
                Logger.d(TAG, "onReceive,requestApproval");
                requestApproval(context);
            }
		}
	}

	private void requestMsg(Context context) {

		Intent in = new Intent();
		in.putExtra("type", 0);
		in.setClass(context, MessageService.class);
		context.startService(in);

	}

	//取消此功能
	private void requestApproval(Context context) {
		Intent in = new Intent();
		in.putExtra("type", 1);
		in.setClass(context, MessageService.class);
		context.startService(in);
	}

	// private void generateService( Context context , int command , int trigger
	// )
	// {
	// Intent intent = new Intent( );
	// intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
	// context.startService( intent );
	// }
}
