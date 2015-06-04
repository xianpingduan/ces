package com.xiexin.ces.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.xiexin.ces.activity.InvoiceInfoActivity;
import com.xiexin.ces.activity.MenuActivity;
import com.xiexin.ces.activity.SearchPendApprovalActivity;
import com.xiexin.ces.entry.Invoice;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.utils.StringUtils;
import com.xiexin.ces.widgets.LoadingDialog;
import com.xiexin.ces.widgets.LoadingUIListView;
import com.xiexin.ces.widgets.PullListView.IListViewListener;

/**
 * User: special Date: 13-12-22 Time: 下午3:26 Mail: specialcyci@gmail.com
 * 
 */
public class PendApprovalFragment extends Fragment implements OnClickListener {

	private final static String TAG = "PendApprovalFragment";

	private View parentView;
	private LoadingUIListView mListView;
	private InvoiceAdapter mInvoiceAdapter;

	// // header start
	// private LinearLayout mReturnLl;
	// private FrameLayout mReturnIv;
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
	
	private boolean isShowDialog = true;
	
	public void setIsShowDialog(boolean b){
		isShowDialog = b;
	}

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
			mLoadingDialog = new LoadingDialog(getActivity());
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(isShowDialog){
					mLoadingDialog.show();
				}
			}
		}, 200);

	}

	private View generateListHeaderView() {
		LayoutInflater inflater = App.getLayoutInflater();
		View view = inflater.inflate(
				R.layout.fragment_pend_approval_list_header, null);
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), SearchPendApprovalActivity.class);
				Logger.d(TAG, "kind="+mKind);
				intent.putExtra("kind", mKind);
				startActivityForResult(intent, 1);
			}
		});
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Logger.d(TAG, "onAttach");

		mQueue = Volley.newRequestQueue(App.getAppContext());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d(TAG, "onCreate");
	}

	public void setKind(int kind, boolean b) {
		Logger.d(TAG, "mKind=" + mKind + ",kind=" + kind);
		if (mKind != kind || b) {
			mKind = kind;
			mCurrentPage = 1;
			mUiHandler.sendEmptyMessage(MSG_KIND_CHANGE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Logger.d(TAG, "onCreateView");

		parentView = inflater.inflate(R.layout.fragment_pend_approval,
				container, false);
		mListView = (LoadingUIListView) parentView
				.findViewById(R.id.pend_approval_list);
		mListView.setHeaderPullEnable(true);
		mListView.setFooterPullEnable(true);
		mListView.setListViewListener(mListViewListener);

		mListView.addHeaderView(generateListHeaderView());

		// // header start
		// mReturnLl = (LinearLayout) parentView.findViewById(R.id.return_ll);
		// mReturnIv = (FrameLayout) parentView.findViewById(R.id.return_iv);
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

		initView();

		initData();
		return parentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.d(TAG, "onResume");
	}

	private final IListViewListener mListViewListener = new IListViewListener() {
		@Override
		public void onRefresh() {
			mCurrentPage = 1;
			requestPendApproval(LOAD_LIST_REFRESH);
		}

		@Override
		public void onLoadMore() {
			requestPendApproval(LOAD_LIST_LOADMORE);
		}
	};

	private void initData() {

		if (mInvoiceAdapter == null)
			mInvoiceAdapter = new InvoiceAdapter();

		mListView.setAdapter(mInvoiceAdapter);

		// 请求数据
		requestPendApproval(LOAD_LIST_NORMAL);

	}

	private static final int MSG_GET_INVOICE_LIST_SUCCESS = 1;

	private static final int MSG_GET_INVOICE_LIST_ERROR = 2;

	private static final int MSG_LAST_PAGE = 3;

	private static final int MSG_NO_DATA = 4;

	private static final int MSG_KIND_CHANGE = 5;

	private final Handler mUiHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			//提醒
			MenuActivity menuActivity=null;
			if(getActivity()!=null){
				menuActivity = (MenuActivity) getActivity();
			}
			
			switch (msg.what) {
			case MSG_GET_INVOICE_LIST_SUCCESS:

				mListView.setFooterPullEnable(true);
				String data = (String) msg.obj;
				ArrayList<Invoice> invoices = getInvoiceList(data);
				if (invoices.size() > 0) {
					if (mCurrentPage == 1) {
						mInvoiceAdapter.addData(invoices,
								Constants.TYPE_LIST_ADD_COVER);
					} else {
						mInvoiceAdapter.addData(invoices,
								Constants.TYPE_LIST_ADD_APPEND);
					}
					mInvoiceAdapter.notifyDataSetChanged();
					mListView.stopHeaderRefresh();
					mListView.stopFooterRefresh();
					mCurrentPage++;

					if (invoices.size() < Constants.PAGE_SIZE) {
						mListView.setFooterPullEnable(false);
					}
					
					//提醒
					if(menuActivity!=null&& mKind==Constants.TYPE_PEND_APPROVAL_TASKS){
						menuActivity.showOrNoApprovalTip(true,invoices.size());
					}

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
				mListView.setFooterPullEnable(false);
				Toast.makeText(
						App.getAppContext(),
						App.getAppContext().getString(
								R.string.data_laoding_failed),
						Toast.LENGTH_SHORT).show();
				break;

			case MSG_LAST_PAGE:
				if (mListView != null) {
					mListView.setFooterPullEnable(false);
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
				//提醒
				if(menuActivity!=null&& mKind==Constants.TYPE_PEND_APPROVAL_TASKS){
					menuActivity.showOrNoApprovalTip(false,0);
				}
				
				break;

			case MSG_KIND_CHANGE:
				mListView.setFooterPullEnable(true);
				requestPendApproval(LOAD_LIST_NORMAL);
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
				invoice.setAccount(obj.getString("account").equals("null") ? ""
						: obj.getString("account"));
				invoice.setPrgid(obj.getString("prgid"));
				invoice.setPrgname(obj.getString("prgname"));
				invoice.setDatanbr(obj.getString("datanbr"));
				invoice.setApprobj(obj.getString("approbj"));
				invoice.setApprname(obj.getString("apprname"));
				invoice.setDepart(obj.getString("depart"));
				invoice.setChannel(obj.getString("channel"));
				invoice.setAccname(obj.getString("accname"));
				invoice.setVertype(obj.getString("vertype").equals("null") ? ""
						: obj.getString("vertype"));
				invoice.setTotalcost(obj.getDouble("totalcost"));
				invoice.setApprdate(obj.getString("apprdate"));
				invoice.setProcessmode(obj.getString("processmode").equals(
						"null") ? "" : obj.getString("processmode"));
				invoice.setStatus(obj.getString("status").equals("null") ? ""
						: obj.getString("status"));
				invoice.setReason(obj.getString("reason").equals("null") ? ""
						: obj.getString("reason"));
				invoice.setAccountname(obj.getString("accountname").equals("null") ? ""
                        : obj.getString("accountname"));
				invoiceList.add(invoice);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return invoiceList;

	}

	private void requestPendApproval(final int type) {

		if (type == LOAD_LIST_NORMAL) {
			showDialog();
		}

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
		urlSbf.append("&filter=").append("");

		JsonObjectRequest json = new JsonObjectRequest(Method.GET,
				urlSbf.toString(), null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Logger.d(TAG, "----response----" + response.toString());
						try {
							int resCode = response.getInt("success");
							Message msg = Message.obtain();
							if (resCode == 0) {
								msg.what = MSG_GET_INVOICE_LIST_SUCCESS;
								msg.obj = response.getString("data");
							} else {
								msg.what = MSG_GET_INVOICE_LIST_ERROR;
								msg.obj = response.get("msg");
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

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int i, long l) {
				Toast.makeText(getActivity(), "Clicked item!",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	private class InvoiceAdapter extends BaseAdapter {

		private final ArrayList<Invoice> list = new ArrayList<Invoice>();

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
				holder.invoiceFrameRl = (RelativeLayout) convertView.findViewById(R.id.invoice_frame);
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
				
				holder.accountNameTv =(TextView) convertView.findViewById(R.id.account_name_tv);

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
					intent.setClass(getActivity(), InvoiceInfoActivity.class);
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

			String apprDate = invoice.getApprdate();
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
			holder.invoiceIdTv.setText(invoice.getDatanbr());
			holder.invoiceDateTv.setText(apprDate);
			
			String account=invoice.getAccount();
			if(account==null||account.isEmpty()){
			    String accountName = App.getSharedPreference().getString(Constants.ZHANG_TAO_ACCINFO, "");
			    //holder.accountNameTv.setText(accountName);
			    holder.departUserNameTv.setText(invoice.getDepart() + " " + invoice.getApprname() +"  "+accountName);
			    
			}else{
			    holder.accountNameTv.setText(invoice.getAccountname()); 
			    holder.departUserNameTv.setText(invoice.getDepart() + " " + invoice.getApprname() +"  " +invoice.getAccountname());
			}
			
			if(invoice.getStatus().equals("待审")){
				holder.invoiceFrameRl.setBackgroundResource(R.drawable.listview_item_bg_color_pend);
			}else{
				holder.invoiceFrameRl.setBackgroundResource(R.drawable.listview_item_bg_color);
			}
			
			Logger.d(TAG, "Totalcost="+invoice.getTotalcost());
			holder.moneyTv
					.setText("￥" + StringUtils.priceDecimal(invoice.getTotalcost())+"");
			holder.prgIdTv.setText(invoice.getPrgid());
			

			
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
		RelativeLayout invoiceFrameRl;
		TextView invoiceIdTv;
		TextView invoiceDateTv;
		TextView departUserNameTv;
		TextView moneyTv;
		TextView invoiceDescTv;
		ImageView indicateIv;
		TextView accountNameTv;

		// 存数据
		TextView prgIdTv;
		TextView accountTv;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.return_iv:

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
		// int resultFrom = data.getIntExtra(Constants.APPR_LIST_RESULT_FROM,0);
		switch (resultCode) {
		case Constants.APPR_LIST_RESULT_FROM_RETURN:
			Logger.d(TAG, "no approval");
			break;
		case Constants.APPR_LIST_RESULT_FROM_APPRSUC:
			Logger.d(TAG, "approval success!");
			refreshList();
			break;
		default:
			break;
		}

	}

	private void refreshList() {
		mCurrentPage = 1;
		requestPendApproval(LOAD_LIST_REFRESH);
	}
}
