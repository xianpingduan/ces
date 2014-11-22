package com.xiexin.ces.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiexin.ces.R;
import com.xiexin.ces.activity.InvoiceInfoActivity;

public class NotifyDialog extends Dialog {

	private ImageView mEmployeeSelectIv;
	private TextView mEmployeeTv;
	private Button mSubmit;

	private Handler mHandler;

	public NotifyDialog(Context context) {
		super(context, R.style.MyDialog);
	}

	public NotifyDialog(Context context, Handler handler) {
		this(context);
		mHandler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_notify);
		
		setCanceledOnTouchOutside(false);

		mEmployeeSelectIv = (ImageView) findViewById(R.id.employee_select_iv);
		mEmployeeTv = (TextView) findViewById(R.id.employee_tv);

		mEmployeeSelectIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// intent to employee activity
				Message msg = Message.obtain();
				msg.what = InvoiceInfoActivity.MSG_NOTIFY_TO_EMPLOYEE;
				mHandler.sendMessage(msg);

			}
		});

		mSubmit = (Button) findViewById(R.id.submit_btn);

		mSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.what = InvoiceInfoActivity.MSG_NOTIFY_SUBMIT;
				mHandler.sendMessage(msg);
			}
		});

	}

	public void setEmployee(String selectEmployee) {
		if (mEmployeeTv != null)
			mEmployeeTv.setText(selectEmployee);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		dismiss();
	}

}
