package com.xiexin.ces.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.entry.Invoice;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.LoadingDialog;
import com.xiexin.ces.widgets.LoadingUIListView;
import com.xiexin.ces.widgets.PullListView.IListViewListener;

/**
 * User: special Date: 13-12-22 Time: 下午3:26 Mail: specialcyci@gmail.com
 */
public class SearchPendApprovalActivity extends Activity implements
		OnClickListener {

	private final static String TAG = "SearchPendApprovalFragment";

	private LoadingUIListView mListView;
	private InvoiceAdapter mInvoiceAdapter;
	private EditText mFilterEt;
	private Button mCancelBtn;

	private String filter;

	// // header start
	// private LinearLayout mReturnLl;
	// private ImageView mReturnIv;
	// private TextView mReturnTv;
	// private TextView mTitle;
	// private Button mBtn1;
	// private Button mBtn2;
	//
	// // header end

	private LoadingDialog mLoadingDialog;

	private RequestQueue mQueue;
	private int mKind = 1;
	private int mCurrentPage = 1;

	private Handler mMainUIHandler;

	public void setMainUIHandler(Handler handler) {
		mMainUIHandler = handler;
	}

	private static final int LOAD_LIST_NORMAL = 0;
	private static final int LOAD_LIST_REFRESH = 1;
	private static final int LOAD_LIST_LOADMORE = 2;

	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private void dismissDialog() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
			}
		}, 500);

	}

	private void showDialog() {
		if (mLoadingDialog == null) {
			mLoadingDialog = new LoadingDialog(SearchPendApprovalActivity.this);
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mLoadingDialog.show();
			}
		}, 200);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d(TAG, "onCreate");
		mQueue = Volley.newRequestQueue(App.getAppContext());

		setContentView(R.layout.activity_search_pend_approval);
		mListView = (LoadingUIListView) findViewById(R.id.search_pend_approval_list);
		mListView.setHeaderPullEnable(false);
		mListView.setFooterPullEnable(false);

		mFilterEt = (EditText) findViewById(R.id.search_et);
		mCancelBtn = (Button) findViewById(R.id.cancel_btn);

		mFilterEt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// 先隐藏键盘
					((InputMethodManager) mFilterEt.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(getCurrentFocus()
									.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

					filter = mFilterEt.getText().toString();

					if (!filter.isEmpty()) {
						try {
							filter = URLEncoder.encode(filter, "utf-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						requestPendApproval(filter);
					}
				}
				return false;
			}
		});

		// mFilterEt.requestFocus();

		// // header start
		// mReturnLl = (LinearLayout) parentView.findViewById(R.id.return_ll);
		// mReturnIv = (ImageView) parentView.findViewById(R.id.return_iv);
		// mReturnTv = (TextView) parentView.findViewById(R.id.return_tv);
		// mTitle = (TextView) parentView.findViewById(R.id.title);
		// mBtn1 = (Button) parentView.findViewById(R.id.btn1);
		// mBtn2 = (Button) parentView.findViewById(R.id.btn2);
		// // /header end
		//
		// mReturnTv.setVisibility(View.GONE);

		// String title = "";
		// switch (mKind) {
		// case Constants.TYPE_PEND_APPROVAL_TASKS:
		// title = getString(R.string.menu_pend_approval);
		// break;
		// case Constants.TYPE_SCRATCH_UPCOME_TASKS:
		// title = getString(R.string.menu_scratch_upcome);
		// break;
		// case Constants.TYPE_APPROVED_TASKS:
		// title = getString(R.string.menu_approved);
		// break;
		// case Constants.TYPE_SEND_ITEM_TASKS:
		// title = getString(R.string.menu_sent_item);
		// break;
		// default:
		// break;
		// }
		// mTitle.setText(title);
		// mBtn1.setText( getString( R.string.finish_to ) );
		// mBtn1.setOnClickListener( this );
		// mReturnIv.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		initView();

		initData();
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.d(TAG, "onResume");
	}

	private void initData() {

		if (mInvoiceAdapter == null)
			mInvoiceAdapter = new InvoiceAdapter();

		mListView.setAdapter(mInvoiceAdapter);
		mKind = getIntent().getIntExtra("kind", 1);

		// 请求数据
		// requestPendApproval(LOAD_LIST_NORMAL);

	}

	private static final int MSG_GET_INVOICE_LIST_SUCCESS = 1;

	private static final int MSG_GET_INVOICE_LIST_ERROR = 2;

	private static final int MSG_LAST_PAGE = 3;

	private static final int MSG_NO_DATA = 4;

	private static final int MSG_KIND_CHANGE = 5;

	private Handler mUiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_GET_INVOICE_LIST_SUCCESS:
				String data = (String) msg.obj;
				ArrayList<Invoice> invoices = getInvoiceList(data);
				if (invoices.size() > 0) {
					mInvoiceAdapter.addData(invoices,
							Constants.TYPE_LIST_ADD_COVER);
					mInvoiceAdapter.notifyDataSetChanged();
					mCurrentPage++;
				} else if (invoices.size() == 0 && mCurrentPage == 1) {
					mInvoiceAdapter.addData(invoices,
							Constants.TYPE_LIST_ADD_COVER);
					mInvoiceAdapter.notifyDataSetChanged();
					mUiHandler.sendEmptyMessage(MSG_NO_DATA);
				} else {
					mUiHandler.sendEmptyMessage(MSG_LAST_PAGE);
				}
				dismissDialog();
				break;

			case MSG_GET_INVOICE_LIST_ERROR:
				dismissDialog();
				mListView.stopFooterRefresh();
				Toast.makeText(
						App.getAppContext(),
						App.getAppContext().getString(
								R.string.data_laoding_failed),
						Toast.LENGTH_SHORT).show();
				break;

			case MSG_LAST_PAGE:
				if (mListView != null) {
					mListView.stopFooterRefresh();
					Toast.makeText(
							App.getAppContext(),
							App.getAppContext().getString(
									R.string.tip_last_page), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case MSG_NO_DATA:
				mListView.setFooterPullEnable(false);
				Toast.makeText(App.getAppContext(),
						App.getAppContext().getString(R.string.no_invoice),
						Toast.LENGTH_SHORT).show();
				break;

			case MSG_KIND_CHANGE:
				// mListView.setFooterPullEnable(true);
				// requestPendApproval(LOAD_LIST_NORMAL);
				break;
			default:
				break;
			}
		}

	};

	private ArrayList<Invoice> getInvoiceList(String jsonStr) {

		ArrayList<Invoice> invoiceList = new ArrayList<Invoice>();
		try {
			JSONArray arrays = new JSONArray(jsonStr);
			for (int i = 0; i < arrays.length(); i++) {
				JSONObject obj = arrays.getJSONObject(i);
				Invoice invoice = new Invoice();
				invoice.setAccount(obj.getString("Account").equals("null") ? ""
						: obj.getString("Account"));
				invoice.setPrgID(obj.getString("PrgID"));
				invoice.setPrgName(obj.getString("PrgName"));
				invoice.setDataNbr(obj.getString("DataNbr"));
				invoice.setApprObj(obj.getString("ApprObj"));
				invoice.setApprName(obj.getString("ApprName"));
				invoice.setDepart(obj.getString("Depart"));
				invoice.setChannel(obj.getString("Channel"));
				invoice.setAccName(obj.getString("AccName"));
				invoice.setVerType(obj.getString("VerType").equals("null") ? ""
						: obj.getString("VerType"));
				invoice.setTotalCost(obj.getInt("TotalCost"));
				invoice.setApprDate(obj.getString("ApprDate"));
				invoice.setProcessMode(obj.getString("ProcessMode").equals(
						"null") ? "" : obj.getString("ProcessMode"));
				invoice.setStatus(obj.getString("Status").equals("null") ? ""
						: obj.getString("Status"));
				invoice.setReason(obj.getString("Reason").equals("null") ? ""
						: obj.getString("Reason"));
				invoiceList.add(invoice);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return invoiceList;

	}

	private void requestPendApproval(final String filter) {

		Logger.d(TAG, "filter =" + filter);
		showDialog();

		String account = App.getSharedPreference().getString(
				Constants.ZHANG_TAO_CONN_NAME, "");
		String userId = App.getSharedPreference().getString(Constants.USER_ID,
				"");

		StringBuffer urlSbf = new StringBuffer(App.getRootUrl()
				+ Constants.GET_WORK_MESSAGE_URL + "?");
		urlSbf.append("account=").append(account);
		urlSbf.append("&userid=").append(userId);
		urlSbf.append("&kind=").append(mKind);
		urlSbf.append("&size=").append(Constants.PAGE_SIZE);
		urlSbf.append("&page=").append(mCurrentPage);
		urlSbf.append("&filter=").append(filter);

		JsonObjectRequest json = new JsonObjectRequest(Method.GET,
				urlSbf.toString(), null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Logger.d(TAG, "----response----" + response.toString());
						try {
							int resCode = response.getInt("Success");
							Message msg = Message.obtain();
							if (resCode == 0) {
								msg.what = MSG_GET_INVOICE_LIST_SUCCESS;
								msg.obj = response.getString("Data");
							} else {
								msg.what = MSG_GET_INVOICE_LIST_ERROR;
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
						mUiHandler.sendEmptyMessage(MSG_GET_INVOICE_LIST_ERROR);
					}
				});
		mQueue.add(json);
		mQueue.start();

	}

	private void initView() {

		// mListView.setOnItemClickListener(new
		// AdapterView.OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> adapterView, View view,
		// int i, long l) {
		// Toast.makeText(SearchPendApprovalActivity.this,
		// "Clicked item!", Toast.LENGTH_LONG).show();
		// }
		// });
	}

	private class InvoiceAdapter extends BaseAdapter {

		private ArrayList<Invoice> list = new ArrayList<Invoice>();

		public void addData(ArrayList<Invoice> data, int type) {

			switch (type) {
			case Constants.TYPE_LIST_ADD_APPEND:
				list.addAll(data);
				break;
			case Constants.TYPE_LIST_ADD_COVER:
				list.clear();
				list.addAll(data);
				break;
			default:
				break;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				convertView = App.getLayoutInflater().inflate(
						R.layout.fragment_pend_approval_list_item, null);
				holder = new ViewHolder();
				holder.invoiceIdTv = (TextView) convertView
						.findViewById(R.id.invoice_id_tv);
				holder.invoiceDateTv = (TextView) convertView
						.findViewById(R.id.invoice_date_tv);
				holder.departUserNameTv = (TextView) convertView
						.findViewById(R.id.depart_username_tv);
				holder.moneyTv = (TextView) convertView
						.findViewById(R.id.money_tv);
				holder.invoiceDescTv = (TextView) convertView
						.findViewById(R.id.invoice_desc_tv);
				holder.indicateIv = (ImageView) convertView
						.findViewById(R.id.indicate_iv);

				// 存数据
				holder.accountTv = (TextView) convertView
						.findViewById(R.id.account_tv);
				holder.prgIdTv = (TextView) convertView
						.findViewById(R.id.prgid_tv);

				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			bindData(holder, list.get(position));

			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ViewHolder holder = (ViewHolder) v.getTag();
					String connName = holder.accountTv.getText().toString();
					String id = holder.invoiceIdTv.getText().toString();
					String prgId = holder.prgIdTv.getText().toString();
					Logger.d(TAG, "connName=" + connName + ",id=" + id
							+ ",prgId=" + prgId);
					Intent intent = new Intent();
					intent.setClass(SearchPendApprovalActivity.this,
							InvoiceInfoActivity.class);
					intent.putExtra(Constants.ZHANG_TAO_CONN_NAME, connName);
					intent.putExtra(Constants.PRGID, prgId);
					intent.putExtra(Constants.DATANBR, id);
					intent.putExtra(Constants.INVOICE_TYPE, mKind);
					startActivityForResult(intent, 1);

				}
			});

			return convertView;
		}

		private void bindData(final ViewHolder holder, final Invoice invoice) {

			String apprDate = invoice.getApprDate();
			Date date = new Date();
			try {
				if (apprDate != null && !apprDate.equals("null")) {
					date = sdf.parse(apprDate);
					apprDate = sdf.format(date);
				} else {
					apprDate = "";
				}
			} catch (ParseException e) {
				e.printStackTrace();
				Logger.d(TAG, "date format error");
			}
			// Logger.d(TAG, "---prgName="+invoice.getPrgName());
			holder.invoiceIdTv.setText(invoice.getDataNbr());
			holder.invoiceDateTv.setText(apprDate);
			holder.departUserNameTv.setText(invoice.getDepart() + " "
					+ invoice.getApprName());
			holder.moneyTv
					.setText("￥" + String.valueOf(invoice.getTotalCost()));
			holder.prgIdTv.setText(invoice.getPrgID());
			holder.accountTv.setText(invoice.getAccount());
			holder.invoiceDescTv.setText(invoice.getReason());
			holder.indicateIv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					intentToInvoiceDesc();
				}
			});

		}

		// 跳转到详情界面
		private void intentToInvoiceDesc() {
			// TODO
			Intent intent = new Intent();
			return;
		}
	}

	class ViewHolder {
		TextView invoiceIdTv;
		TextView invoiceDateTv;
		TextView departUserNameTv;
		TextView moneyTv;
		TextView invoiceDescTv;
		ImageView indicateIv;
		// 存数据
		TextView prgIdTv;
		TextView accountTv;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.return_iv:
			break;
		case R.id.cancel_btn:
			Intent in = new Intent();
			setResult(Constants.APPR_LIST_RESULT_FROM_RETURN, in);
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		Logger.d(TAG, "requestCode=" + requestCode + ",resultCode="
				+ resultCode);
		setResult(resultCode, data);
		finish();
	}
	//
	// private void refreshList() {
	// mCurrentPage = 1;
	// requestPendApproval(LOAD_LIST_REFRESH);
	// }
}
