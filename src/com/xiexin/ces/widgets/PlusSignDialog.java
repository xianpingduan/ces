package com.xiexin.ces.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiexin.ces.R;
import com.xiexin.ces.activity.InvoiceInfoActivity;

public class PlusSignDialog extends Dialog {

	private ImageView mEmployeeSelectIv;
	private TextView mEmployeeTv;
	private LinearLayout mXiangqianLl;
	private CheckBox mXiangqianCb;
	private LinearLayout mXianghouLl;
	private CheckBox mXianghouCb;
	private Button mSubmit;

	private int mType = -1;

	private Handler mHandler;
	
	private RelativeLayout mZtSelectRl;

	public PlusSignDialog(Context context) {
		super(context, R.style.MyDialog);
	}

	public PlusSignDialog(Context context, Handler handler) {
		this(context);
		mHandler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_plus_sign);
		
		setCanceledOnTouchOutside(false);

		mEmployeeSelectIv = (ImageView) findViewById(R.id.employee_select_iv);
		mEmployeeTv = (TextView) findViewById(R.id.employee_tv);

		mXiangqianLl = (LinearLayout) findViewById(R.id.xiangqian_ll);
		mXiangqianCb = (CheckBox) findViewById(R.id.xiangqian_cb);

		mXianghouLl = (LinearLayout) findViewById(R.id.xianghou_ll);
		mXianghouCb = (CheckBox) findViewById(R.id.xianghou_cb);

		mXiangqianCb.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mXiangqianCb.isChecked()) {
					mXiangqianCb.setChecked(false);
					mType = -1;
				} else {
					mType = 0;
					mXiangqianCb.setChecked(true);
					mXianghouCb.setChecked(false);
				}

			}
		});

		mXianghouCb.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mXianghouCb.isChecked()) {
					mXianghouCb.setChecked(false);
					mType = -1;
				} else {
					mType = 1;
					mXianghouCb.setChecked(true);
					mXiangqianCb.setChecked(false);

				}
			}
		});

		mZtSelectRl = (RelativeLayout) findViewById(R.id.zt_select_rl);

		mZtSelectRl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// intent to employee activity
				Message msg = Message.obtain();
				msg.what = InvoiceInfoActivity.MSG_PLUS_SIGN_TO_EMPLOYEE;
				mHandler.sendMessage(msg);

			}
		});
		
		mEmployeeSelectIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// intent to employee activity
				Message msg = Message.obtain();
				msg.what = InvoiceInfoActivity.MSG_PLUS_SIGN_TO_EMPLOYEE;
				mHandler.sendMessage(msg);

			}
		});

		mSubmit = (Button) findViewById(R.id.submit_btn);

		mSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.what = InvoiceInfoActivity.MSG_PLUS_SIGN_SUBMIT;
				mHandler.sendMessage(msg);
			}
		});

	}

	public int getType() {
		return mType;
	}

	public void setEmployee(String selectEmployee) {
		if (mEmployeeTv != null)
			mEmployeeTv.setText(selectEmployee);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.dismiss();
		mHandler.sendEmptyMessage(InvoiceInfoActivity.MSG_CLEAR_RADIOGROUP_CHECK);
	}

}
