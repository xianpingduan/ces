package com.xiexin.ces.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.xiexin.ces.R;

public class SubmitSuccessDialog extends Dialog {

	public SubmitSuccessDialog(Context context) {
		super(context, R.style.MyDialog);
	}

	public SubmitSuccessDialog(Context context, Handler handler) {
		this(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_submit_success);

		setCanceledOnTouchOutside(true);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		dismiss();
	}

}
