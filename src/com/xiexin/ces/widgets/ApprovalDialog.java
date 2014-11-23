package com.xiexin.ces.widgets;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.activity.InvoiceInfoActivity;

public class ApprovalDialog extends Dialog {

	private Spinner mSpinner;
	private EditText mApprovalContentEt;
	private Button mSubmit;
	private Handler mHandler;

	private int mSpinnerVal;
	private ApprovalSpinnerAdapter mApprovalSpinnerAdapter;

	public ApprovalDialog(Context context) {
		super(context, R.style.MyDialog);
	}

	public ApprovalDialog(Context context, Handler handler) {
		this(context);
		mHandler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_approval);
		setCanceledOnTouchOutside(false);
		generateList();

		mSpinner = (Spinner) findViewById(R.id.approval_spinner);

		mApprovalSpinnerAdapter = new ApprovalSpinnerAdapter();

		mSpinner.setAdapter(mApprovalSpinnerAdapter);

		mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int arg2, long arg3) {
				TextView idTv = (TextView) view
						.findViewById(R.id.spinner_id_tv);
				Log.d("ApprovalDialog", "idTv=" + idTv.getText().toString());
				mSpinnerVal = Integer.parseInt(idTv.getText().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

		mApprovalContentEt = (EditText) findViewById(R.id.approval_content_et);
		mSubmit = (Button) findViewById(R.id.submit_btn);
		mSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getApprovalContent().isEmpty()&&(mSpinnerVal==4||mSpinnerVal==5||mSpinnerVal==7)){
					Toast.makeText(App.getAppContext(), App.getAppContext().getString(R.string.please_enter_approval_content), Toast.LENGTH_SHORT).show();
					return ;
				}
				Message msg = Message.obtain();
				msg.what = InvoiceInfoActivity.MSG_APPROVAL_SUBMIT;
				mHandler.sendMessage(msg);

			}
		});
	}

	public int getSpinnerValue() {
		return mSpinnerVal;
	}

	public String getApprovalContent() {
		String etVal = mApprovalContentEt.getText().toString();
		if (etVal != null && !etVal.isEmpty()) {
			return etVal;
		}
		return "";
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		dismiss();
	}

	class ApprovalType {

		private int id;
		private String showName;
	}

	private ArrayList<ApprovalType> mList = new ArrayList<ApprovalDialog.ApprovalType>();

	public void generateList() {
		mList.clear();
		for (int i = 1; i <= 10; i++) {
			ApprovalType at = new ApprovalType();
			at.id = i;
			at.showName = Constants.getType(i);
			mList.add(at);
		}
	}

	private class ApprovalSpinnerAdapter extends BaseAdapter {

		// 1.提交、2.同意、3.已阅、4.拒绝、5.中止、6.还原、7.回退、8.回收、9.暂存、10.撤回

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = App.getLayoutInflater().inflate(
						R.layout.spinner_list_item, null);
				holder = new ViewHolder();
				holder.idTv = (TextView) convertView
						.findViewById(R.id.spinner_id_tv);
				holder.showTv = (TextView) convertView
						.findViewById(R.id.spinner_show_name_tv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			bindData(holder, mList.get(position));
			return convertView;
		}

		private void bindData(ViewHolder holder, ApprovalType aType) {
			Log.d("ApprovalDialog", "id="+aType.id);
			holder.idTv.setText(aType.id+"");
			holder.showTv.setText(aType.showName);
		}

	}

	class ViewHolder {
		TextView idTv;
		TextView showTv;
	}
}
