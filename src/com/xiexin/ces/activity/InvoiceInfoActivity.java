package com.xiexin.ces.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.LoadingDialog;

public class InvoiceInfoActivity extends Activity implements OnClickListener {

	
	private final static String TAG ="InvoiceInfoActivity";
	
	// header start
	private LinearLayout mReturnLl;
	private ImageView mReturnIv;
	private TextView mReturnTv;
	private TextView mTitle;
	private Button mBtn1;
	private Button mBtn2;

	// header end

	private LinearLayout mInfoContentLl;
	private LoadingDialog mLoadingDialog;

	private int mInvoiceType;

	private RequestQueue mQueue;

	private String mConnName;// 账套信息
	private String mPrgid; // 业务类型
	private String mDatanbr;// 单据编号
	
	
	private String mDetConfig;//详情要显示的字段，以","隔开
	private String mDetContent;//详情内容
	private String mFilesPath; //附件详情
	
	private RadioButton mApprovalRbtn;
	private RadioButton mNotifyRbtn;
	private RadioButton mPlusSignRbtn;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invoice_info);
		mQueue = Volley.newRequestQueue(App.getAppContext());
		initView();
		initData();
	}

	private void dismissDialog() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}

	private void showDialog() {
		if (mLoadingDialog == null) {
			mLoadingDialog = new LoadingDialog(this);
		}
		mLoadingDialog.show();
	}

	private void initView() {

		mInfoContentLl = (LinearLayout) findViewById(R.id.info_content_ll);

		// header start
		mReturnLl = (LinearLayout) findViewById(R.id.return_ll);
		mReturnIv = (ImageView) findViewById(R.id.return_iv);
		mReturnTv = (TextView) findViewById(R.id.return_tv);
		mTitle = (TextView) findViewById(R.id.title);
		mBtn1 = (Button) findViewById(R.id.btn1);
		mBtn2 = (Button) findViewById(R.id.btn2);
		// /header end

		mInvoiceType = getIntent().getIntExtra(Constants.INVOICE_TYPE, 0);

		mReturnTv.setText(getReturnStr(mInvoiceType));
		mTitle.setText(getString(R.string.invoice_info));
		mBtn1.setText(getString(R.string.invoice_approval_road));
		mBtn2.setText(getString(R.string.invoice_attachment));

		mBtn1.setVisibility(View.VISIBLE);
		mBtn2.setVisibility(View.VISIBLE);
		mReturnLl.setVisibility(View.VISIBLE);

		mBtn1.setOnClickListener(this);
		mBtn2.setOnClickListener(this);
		mReturnLl.setOnClickListener(this);
		
		
		mApprovalRbtn = (RadioButton) findViewById(R.id.approval_rb);
		mNotifyRbtn = (RadioButton) findViewById(R.id.notify_rb);
		mPlusSignRbtn = (RadioButton) findViewById(R.id.plus_sign_rb);
		
		mApprovalRbtn.setOnClickListener(this);
		mNotifyRbtn.setOnClickListener(this);
		mPlusSignRbtn.setOnClickListener(this);
	}

	private void initData() {
		Intent intent = getIntent();
		mPrgid = intent.getStringExtra(Constants.PRGID);
		mConnName = intent.getStringExtra(Constants.ZHANG_TAO_CONN_NAME);
		if (mConnName == null || mConnName.isEmpty()) {
			mConnName = App.getSharedPreference().getString(
					Constants.ZHANG_TAO_CONN_NAME, "");
		}
		mDatanbr = intent.getStringExtra(Constants.DATANBR);
	}
	
	
	

	private String getReturnStr(int type) {
		switch (type) {
		case Constants.TYPE_PEND_APPROVAL_TASKS:
			return getString(R.string.menu_pend_approval);
		case Constants.TYPE_SCRATCH_UPCOME_TASKS:
			return getString(R.string.menu_scratch_upcome);
		case Constants.TYPE_SEND_ITEM_TASKS:
			return getString(R.string.menu_sent_item);
		case Constants.TYPE_APPROVED_TASKS:
			return getString(R.string.menu_approved);
		default:
			break;
		}
		return getString(R.string.return_text);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn1:
			intentToApprovalRoad();
			break;
		case R.id.btn2:
			intentToAttachMent();
			break;
		case R.id.return_ll:
			onBackPressed();
			break;
		case R.id.approval_rb:
			approval();
			break;
		case R.id.notify_rb:
			notifyRb();
			break;
		case R.id.plus_sign_rb:
			plusSign();
			break;
		default:
			break;
		}

	}
	
	private void approval(){
		
		
	}
	private void plusSign(){
		
		
	}
	private void notifyRb(){
		
		
	}

	private void intentToApprovalRoad() {
		Intent intent = new Intent();
		intent.setClass(InvoiceInfoActivity.this, InvoiceApprRoadActivity.class);
		intent.putExtra(Constants.ZHANG_TAO_CONN_NAME, mConnName);
		intent.putExtra(Constants.PRGID, mPrgid);
		intent.putExtra(Constants.DATANBR, mDatanbr);
		startActivity(intent);
	}

	private void intentToAttachMent() {

		Intent intent = new Intent();
//		intent.setClass(InvoiceInfoActivity.class, cls);
		intent.putExtra(Constants.FILES_PATH, mFilesPath);
		startActivity(intent);
	}
	
	private void intentToInfo(){
		
		Intent intent = new Intent();
//		intent.setClass(InvoiceInfoActivity.class, cls);
		intent.putExtra(Constants.DET_CONFIG, mDetConfig);
		intent.putExtra(Constants.DET_INFO, mDetContent);
		startActivity(intent);
		
	}
	
	private void intentToEmployee(){
		
		Intent intent = new Intent();
//		intent.setClass(InvoiceInfoActivity.class, cls);
		intent.putExtra(Constants.ZHANG_TAO_CONN_NAME, mConnName);
		intent.putExtra(Constants.CHECK_EMPLOYEE_FROM, Constants.CHECK_EMPLOYEE_FROM_NOTIFY);
		startActivityForResult(intent, -1);
	}

	private void doRequestMobileCfg() {
		showDialog();
		StringBuffer urlSbf = new StringBuffer(Constants.ROOT_URL
				+ Constants.GET_MOBILE_CFG + "?");
		urlSbf.append("account=").append(mConnName);
		urlSbf.append("&prgid=").append(mPrgid);

		JsonObjectRequest json = new JsonObjectRequest(Method.GET,
				urlSbf.toString(), null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Logger.d(TAG, "----response----" + response.toString());
						try {
							int resCode = response.getInt("Success");
							Message msg = Message.obtain();
							if (resCode == 0) {
								msg.what = MSG_GET_INFO_SUCCESS;
								msg.obj = response.getString("Data");
							} else {
								msg.what = MSG_GET_INFO_ERROR;
								msg.obj = response.get("Msg");
							}
							mUiHandler.sendMessage(msg);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.d(TAG, "----e----" + error.toString());
						mUiHandler
								.sendEmptyMessage(MSG_GET_INFO_ERROR);
					}
				});
		mQueue.add(json);
		mQueue.start();
	}
	
	
	private final static int MSG_GET_CONFIG_SUCCESS =1;
	private final static int MSG_GET_CONFIG_ERROR =2;
	private final static int MSG_GET_INFO_SUCCESS =3;
	private final static int MSG_GET_INFO_ERROR =4;
	
	private final static int MSG_SET_PLUS_SIGN_SUCCESS=5;
	private final static int MSG_SET_PLUS_SIGN_ERROR=6;
	
	private final static int MSG_SET_APPR_ATTENTION_SUCCESS=7;
	private final static int MSG_SET_APPR_ATTENTION_ERROR=8;
	
	private final static int MSG_SET_SUBMIT_WORK_FLOW_SUCCESS=9;
	private final static int MSG_SET_SUBMIT_WORK_FLOW_ERROR=10;
	
	private Handler  mUiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_GET_CONFIG_SUCCESS:
				
				break;
			case MSG_GET_CONFIG_ERROR:
				
				break;
			case MSG_GET_INFO_SUCCESS:
				
				break;
			case MSG_GET_INFO_ERROR:
				
				break;
				
			case MSG_SET_PLUS_SIGN_SUCCESS:
				
				break;
			case MSG_SET_PLUS_SIGN_ERROR:
				
				break;
				
			case MSG_SET_APPR_ATTENTION_SUCCESS:
				
				break;
			case MSG_SET_APPR_ATTENTION_ERROR:
				
				break;

			case MSG_SET_SUBMIT_WORK_FLOW_SUCCESS:
				
				break;
			case MSG_SET_SUBMIT_WORK_FLOW_ERROR:
				
				break;
			default:
				break;
			}
		}
		
	};
	
	private void doRequestInfo(){
		
		StringBuffer urlSbf = new StringBuffer(Constants.ROOT_URL
				+ Constants.GET_DOC_INFORMATION + "?");
		urlSbf.append("account=").append(mConnName);
		urlSbf.append("&prgid=").append(mPrgid);
		urlSbf.append("&datanbr=").append(mDatanbr);

		JsonObjectRequest json = new JsonObjectRequest(Method.GET,
				urlSbf.toString(), null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Logger.d(TAG, "----response----" + response.toString());
						try {
							int resCode = response.getInt("Success");
							Message msg = Message.obtain();
							if (resCode == 0) {
								msg.what = MSG_GET_INFO_SUCCESS;
								msg.obj = response.getString("Data");
							} else {
								msg.what = MSG_GET_INFO_ERROR;
								msg.obj = response.get("Msg");
							}
							mUiHandler.sendMessage(msg);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.d(TAG, "----e----" + error.toString());
						mUiHandler
								.sendEmptyMessage(MSG_GET_INFO_ERROR);
					}
				});
		mQueue.add(json);
		mQueue.start();
	}
	
	
	//加签
	private int mPosition=-1;
	private String mUserId;
	private String mUpdateUser;
	private int inxnbr;
	
	private void doSetPlusSign(){
		
		StringBuffer urlSbf = new StringBuffer(Constants.ROOT_URL
				+ Constants.SET_PLUS_SIGN + "?");
		urlSbf.append("account=").append(mConnName);
		urlSbf.append("&prgid=").append(mPrgid);
		urlSbf.append("&datanbr=").append(mDatanbr);
		urlSbf.append("&position=").append(mPosition );
		urlSbf.append("&userid=").append(mUserId );
		urlSbf.append("&updateuser=").append(mUpdateUser );
		urlSbf.append("&inxnbr=").append(inxnbr);

		JsonObjectRequest json = new JsonObjectRequest(Method.GET,
				urlSbf.toString(), null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Logger.d(TAG, "----response----" + response.toString());
						try {
							int resCode = response.getInt("Success");
							Message msg = Message.obtain();
							if (resCode == 0) {
								msg.what = MSG_SET_PLUS_SIGN_SUCCESS;
								msg.obj = response.getString("Data");
							} else {
								msg.what = MSG_SET_PLUS_SIGN_ERROR;
								msg.obj = response.get("Msg");
							}
							mUiHandler.sendMessage(msg);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.d(TAG, "----e----" + error.toString());
						mUiHandler
								.sendEmptyMessage(MSG_SET_PLUS_SIGN_ERROR);
					}
				});
		mQueue.add(json);
		mQueue.start();
	}
	
	//审批
	private String mApprovalMemo;
	private void doSubmitWorkFlow(){
		
		StringBuffer urlSbf = new StringBuffer(Constants.ROOT_URL
				+ Constants.SUBMIT_WORK_FLOW + "?");
		urlSbf.append("account=").append(mConnName);
		urlSbf.append("&prgid=").append(mPrgid);
		urlSbf.append("&datanbr=").append(mDatanbr);
		urlSbf.append("&duty=").append(mUpdateUser );
		urlSbf.append("&inxnbr=").append(inxnbr);
		urlSbf.append("&memo=").append(mApprovalMemo);

		JsonObjectRequest json = new JsonObjectRequest(Method.GET,
				urlSbf.toString(), null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Logger.d(TAG, "----response----" + response.toString());
						try {
							int resCode = response.getInt("Success");
							Message msg = Message.obtain();
							if (resCode == 0) {
								msg.what = MSG_SET_SUBMIT_WORK_FLOW_SUCCESS;
								msg.obj = response.getString("Data");
							} else {
								msg.what = MSG_SET_SUBMIT_WORK_FLOW_ERROR;
								msg.obj = response.get("Msg");
							}
							mUiHandler.sendMessage(msg);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.d(TAG, "----e----" + error.toString());
						mUiHandler
								.sendEmptyMessage(MSG_SET_SUBMIT_WORK_FLOW_ERROR);
					}
				});
		mQueue.add(json);
		mQueue.start();
	}
	
	
	
	//知会
	private void doSetApprAttention(){
		
		StringBuffer urlSbf = new StringBuffer(Constants.ROOT_URL
				+ Constants.SET_APPR_ATTENTION + "?");
		urlSbf.append("account=").append(mConnName);
		urlSbf.append("&prgid=").append(mPrgid);
		urlSbf.append("&datanbr=").append(mDatanbr);
		urlSbf.append("&userid=").append(mUserId );
		urlSbf.append("&updateuser=").append(mUpdateUser );

		JsonObjectRequest json = new JsonObjectRequest(Method.GET,
				urlSbf.toString(), null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Logger.d(TAG, "----response----" + response.toString());
						try {
							int resCode = response.getInt("Success");
							Message msg = Message.obtain();
							if (resCode == 0) {
								msg.what = MSG_SET_APPR_ATTENTION_SUCCESS;
								msg.obj = response.getString("Data");
							} else {
								msg.what = MSG_SET_APPR_ATTENTION_ERROR;
								msg.obj = response.get("Msg");
							}
							mUiHandler.sendMessage(msg);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.d(TAG, "----e----" + error.toString());
						mUiHandler
								.sendEmptyMessage(MSG_SET_APPR_ATTENTION_ERROR);
					}
				});
		mQueue.add(json);
		mQueue.start();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode ==RESULT_OK){
			mUserId = data.getStringExtra("userid");
		}
		
	}

}
