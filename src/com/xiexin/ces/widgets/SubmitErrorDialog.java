package com.xiexin.ces.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.xiexin.ces.R;

public class SubmitErrorDialog extends Dialog {

	public SubmitErrorDialog(Context context) {
		super(context, R.style.MyDialog);
	}

	public SubmitErrorDialog(Context context, Handler handler) {
		this(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_submit_error);

		setCanceledOnTouchOutside(true);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		dismiss();
	}

}
