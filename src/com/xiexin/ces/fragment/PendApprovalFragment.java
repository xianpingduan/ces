package com.xiexin.ces.fragment;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.xiexin.ces.entry.Invoice;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.LoadingUIListView;

/**
 * User: special Date: 13-12-22 Time: 下午3:26 Mail: specialcyci@gmail.com
 */
public class PendApprovalFragment extends Fragment implements OnClickListener {
	
	private final static String TAG="PendApprovalFragment";

	private View parentView;
	private LoadingUIListView mListView;
	private InvoiceAdapter mInvoiceAdapter;
	
	

	// header start
	private LinearLayout mReturnLl;
	private ImageView mReturnIv;
	private TextView mReturnTv;
	private TextView mTitle;
	private Button mBtn1;
	private Button mBtn2;

	// header end
	
	private RequestQueue mQueue;
	private int mKind = 3;
	private int mCurrentPage=1;
	
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		mQueue = Volley.newRequestQueue(App.getAppContext());
	}
	
	public void setKind(int kind){
		
		mKind = kind;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.fragment_pend_approval,
				container, false);
		mListView = (LoadingUIListView) parentView.findViewById(R.id.pend_approval_list);

		// header start
		mReturnLl = (LinearLayout) parentView.findViewById(R.id.return_ll);
		mReturnIv = (ImageView) parentView.findViewById(R.id.return_iv);
		mReturnTv = (TextView) parentView.findViewById(R.id.return_tv);
		mTitle = (TextView) parentView.findViewById(R.id.title);
		mBtn1 = (Button) parentView.findViewById(R.id.btn1);
		mBtn2 = (Button) parentView.findViewById(R.id.btn2);
		// /header end

		mReturnTv.setVisibility(View.GONE);
		
		
		String title="";
		switch (mKind) {
		case Constants.TYPE_PEND_APPROVAL_TASKS:
			title = getString(R.string.menu_pend_approval);
			break;
		case Constants.TYPE_SCRATCH_UPCOME_TASKS:
			title = getString(R.string.menu_scratch_upcome);
			break;
		case Constants.TYPE_APPROVED_TASKS:
			title = getString(R.string.menu_approved);
			break;
		case Constants.TYPE_SEND_ITEM_TASKS:
			title = getString(R.string.menu_sent_item);
			break;
		default:
			break;
		}
		mTitle.setText(title);
		// mBtn1.setText( getString( R.string.finish_to ) );
		// mBtn1.setOnClickListener( this );

		mReturnIv.setOnClickListener(this);

		initView();
		
		initData();
		return parentView;
	}
	
	
	private void initData(){
		
		if (mInvoiceAdapter == null)
			mInvoiceAdapter = new InvoiceAdapter();

		mListView.setAdapter(mInvoiceAdapter);
		
		//请求数据
		requestPendApproval();
		
	}
	
	private static final int MSG_GET_INVOICE_LIST_SUCCESS =1;
	
	private static final int MSG_GET_INVOICE_LIST_ERROR =2;
	
	private Handler mUiHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_GET_INVOICE_LIST_SUCCESS:
				
				String data = (String)msg.obj;
				mInvoiceAdapter.addData(getInvoiceList(data), Constants.TYPE_LIST_ADD_APPEND);
				mInvoiceAdapter.notifyDataSetChanged();
				
				break;
				
			case MSG_GET_INVOICE_LIST_ERROR:
				
				break;

			default:
				break;
			}
		}
		
	};
	
	private ArrayList<Invoice> getInvoiceList(String jsonStr ){
		
		ArrayList<Invoice> invoiceList = new ArrayList<Invoice>();
		try {
			JSONArray arrays = new JSONArray(jsonStr);
			for (int i = 0; i < arrays.length(); i++) {
				JSONObject obj = arrays.getJSONObject(i);
				Invoice invoice = new Invoice();
				invoice.setAccount(obj.getString("Account")==null ?"":obj.getString("Account"));
				invoice.setPrgID(obj.getString("PrgID"));
				invoice.setPrgName(obj.getString("PrgName"));
				invoice.setDataNbr(obj.getString("DataNbr"));
				invoice.setApprObj(obj.getString("ApprObj"));
				invoice.setApprName(obj.getString("ApprName"));
				invoice.setDepart(obj.getString("Depart"));
				invoice.setChannel(obj.getString("Channel"));
				invoice.setAccName(obj.getString("AccName"));
				invoice.setVerType(obj.getString("VerType")==null?"":obj.getString("VerType"));
				invoice.setTotalCost(obj.getDouble("TotalCost"));
				invoice.setApprDate(obj.getString("ApprDate"));
				invoice.setProcessMode(obj.getString("ProcessMode")==null?"":obj.getString("ProcessMode"));
				invoice.setStatus(obj.getString("Status")==null?"":obj.getString("Status"));
				invoiceList.add(invoice);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return invoiceList;
		
	}

	private void requestPendApproval() {
		
		String account = App.getSharedPreference().getString(Constants.ZHANG_TAO_CONN_NAME, "");
		String userId = App.getSharedPreference().getString(Constants.USER_ID, "");
		
		StringBuffer urlSbf = new StringBuffer(Constants.ROOT_URL
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
						Logger.d(TAG, "----e----" + error.getMessage());
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
				convertView = App.getLayoutInflater().inflate(R.layout.fragment_pend_approval_list_item, null);
				holder = new ViewHolder();
				holder.invoiceIdTv = (TextView) convertView.findViewById(R.id.invoice_id_tv);
				holder.invoiceDateTv = (TextView) convertView.findViewById(R.id.invoice_date_tv);
				holder.departUserNameTv = (TextView) convertView.findViewById(R.id.depart_username_tv);
				holder.moneyTv = (TextView) convertView.findViewById(R.id.money_tv);
				holder.invoiceDescTv =(TextView) convertView.findViewById(R.id.invoice_desc_tv);
				holder.indicateIv =(ImageView) convertView.findViewById(R.id.indicate_iv);
				
				//存数据
								holder.accountTv= (TextView)convertView.findViewById(R.id.account_tv);
								holder.prgIdTv = (TextView) convertView.findViewById(R.id.prgid_tv);

				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			bindData(holder, list.get(position));

			return convertView;
		}

		private void bindData(final ViewHolder holder, final Invoice invoice) {
			
//			String apprDate = sdf.format(new Date(invoice.getApprDate()));
			holder.invoiceIdTv.setText(invoice.getPrgName() +" "+invoice.getDataNbr());
			holder.invoiceDateTv.setText(invoice.getApprDate());
			holder.departUserNameTv.setText(invoice.getDepart()+" "+invoice.getApprName());
			holder.moneyTv.setText(invoice.getTotalCost()+"");
			holder.prgIdTv.setText(invoice.getPrgID());
			holder.accountTv.setText(invoice.getAccount());
			
			holder.indicateIv.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					intentToInvoiceDesc();
					
				}
			});
			
			
		}
		
		private void intentToInvoiceDesc(){
			//TODO
			Intent intent = new Intent();
			return ;
		}
	}

	class ViewHolder {
		TextView invoiceIdTv;
		TextView invoiceDateTv;
		TextView departUserNameTv;
		TextView moneyTv;
		TextView invoiceDescTv;
		ImageView indicateIv;
		
		//存数据
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
}
