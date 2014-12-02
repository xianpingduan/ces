package com.xiexin.ces.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pada.juidownloadmanager.utils.PackageUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.xiexin.ces.PushNotificationCenter;
import com.xiexin.ces.R;
import com.xiexin.ces.activity.InvoiceInfoActivity;
import com.xiexin.ces.activity.MessageInfoActivity;
import com.xiexin.ces.entry.Invoice;
import com.xiexin.ces.entry.PushMessage;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.LoadingDialog;
import com.xiexin.ces.widgets.LoadingUIListView;
import com.xiexin.ces.widgets.PullListView.IListViewListener;

/**
 * User: special Date: 13-12-22 Time: 下午3:26 Mail: specialcyci@gmail.com
 */
public class MessageFragment extends Fragment implements OnClickListener {

	private final static String TAG = "MessageFragment";

	private View parentView;
	private LoadingUIListView mListView;
	private MessageAdapter mMessageAdapter;

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
				mLoadingDialog.show();
			}
		}, 200);

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Logger.d(TAG, "onCreateView");

		parentView = inflater.inflate(R.layout.fragment_message, container,
				false);
		mListView = (LoadingUIListView) parentView
				.findViewById(R.id.message_list);
		mListView.setHeaderPullEnable(true);
		mListView.setFooterPullEnable(true);
		mListView.setListViewListener(mListViewListener);

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
			startGetMsg();
		}

		@Override
		public void onLoadMore() {
			startGetMsg();
		}
	};

	private void initData() {

		if (mMessageAdapter == null)
			mMessageAdapter = new MessageAdapter();

		mListView.setAdapter(mMessageAdapter);

	}

	private static final int MSG_GET_MESSAGE_LIST_SUCCESS = 1;

	private static final int MSG_GET_MESSAGE_LIST_ERROR = 2;

	private static final int MSG_LAST_PAGE = 3;

	private static final int MSG_NO_DATA = 4;

	private Handler mUiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_GET_MESSAGE_LIST_SUCCESS:
				mListView.setFooterPullEnable(true);
				String data = (String) msg.obj;
				ArrayList<PushMessage> invoices = getMessageList(data);
				if (invoices.size() > 0) {
					if (mCurrentPage == 1) {
						mMessageAdapter.addData(invoices,
								Constants.TYPE_LIST_ADD_COVER);
					} else {
						mMessageAdapter.addData(invoices,
								Constants.TYPE_LIST_ADD_APPEND);
					}
					mMessageAdapter.notifyDataSetChanged();
					mListView.stopHeaderRefresh();
					mListView.stopFooterRefresh();
					mCurrentPage++;

					if (invoices.size() <= Constants.PAGE_SIZE) {
						mListView.setFooterPullEnable(false);
					}

				} else if (invoices.size() == 0 && mCurrentPage == 1) {
					mMessageAdapter.addData(invoices,
							Constants.TYPE_LIST_ADD_COVER);
					mMessageAdapter.notifyDataSetChanged();
					mUiHandler.sendEmptyMessage(MSG_NO_DATA);
				} else {
					mUiHandler.sendEmptyMessage(MSG_LAST_PAGE);
				}
				dismissDialog();
				break;

			case MSG_GET_MESSAGE_LIST_ERROR:
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
				break;

			default:
				break;
			}
		}

	};

	private ArrayList<PushMessage> getMessageList(String msgStr) {

		ArrayList<PushMessage> messageList = new ArrayList<PushMessage>();

		try {
			JSONArray arrays = new JSONArray(msgStr);
			for (int i = 0; i < arrays.length(); i++) {
				JSONObject jsonObject = arrays.getJSONObject(i);
				PushMessage msg = new PushMessage();

				msg.setAccount(jsonObject.getString("Account"));
				msg.setApprID(jsonObject.getInt("ApprID"));
				msg.setbRead(jsonObject.getInt("bRead"));
				msg.setContent(jsonObject.getString("Content"));
				msg.setCrtDate(jsonObject.getString("CrtDate"));
				msg.setFilesPath(jsonObject.getString("FilesPath"));
				msg.setFromUser(jsonObject.getString("FromUser"));
				msg.setMsgID(jsonObject.getString("MsgID"));
				msg.setMsgType(jsonObject.getInt("MsgType"));
				msg.setTitle(jsonObject.getString("Title"));
				msg.setToUser(jsonObject.getString("ToUser"));
				messageList.add(msg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return messageList;

	}

	// account=web_101&amp;userid=600012&amp;bread=0&amp;page=1&amp;size=100

	public void startGetMsg() {

		String account = App.getSharedPreference().getString(
				Constants.ZHANG_TAO_CONN_NAME, "");
		String userid = App.getSharedPreference().getString(Constants.USER_ID,
				"");

		StringBuffer urlSbf = new StringBuffer(Constants.ROOT_URL
				+ Constants.GET_MSG_URL + "?");
		urlSbf.append("userid=").append(userid);
		urlSbf.append("account=").append(account);
		urlSbf.append("bread=").append(0);
		urlSbf.append("page=").append(mCurrentPage);
		urlSbf.append("size=").append(Constants.PAGE_SIZE);
		JsonObjectRequest json = new JsonObjectRequest(Method.GET,
				urlSbf.toString(), null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO
						try {
							int resCode = response.getInt("Success");
							Message msg = Message.obtain();
							if (resCode == 0) {
								msg.what = MSG_GET_MESSAGE_LIST_SUCCESS;
								msg.obj = response.getString("Data");
							} else {
								msg.what = MSG_GET_MESSAGE_LIST_ERROR;
								msg.obj = response.get("Msg");
							}
							mUiHandler.sendMessage(msg);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						mUiHandler.sendEmptyMessage(MSG_GET_MESSAGE_LIST_ERROR);
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
				// Toast.makeText(getActivity(), "Clicked item!",
				// Toast.LENGTH_LONG).show();
			}
		});
	}

	private class MessageAdapter extends BaseAdapter {

		private ArrayList<PushMessage> list = new ArrayList<PushMessage>();

		public void addData(ArrayList<PushMessage> data, int type) {

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
						R.layout.fragment_message_list_item, null);
				holder = new ViewHolder();
				holder.idTv = (TextView) convertView
						.findViewById(R.id.id_tv);
				holder.titleTv = (TextView) convertView
						.findViewById(R.id.title_tv);
				holder.contentTv = (TextView) convertView
						.findViewById(R.id.content_tv);

				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			bindData(holder, list.get(position));

			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ViewHolder holder = (ViewHolder) v.getTag();
					String id = holder.idTv.getText().toString();
					String title = holder.titleTv.getText().toString();
					String content = holder.contentTv.getText().toString();
					Intent intent = new Intent();
					intent.setClass(getActivity(), MessageInfoActivity.class);
					intent.putExtra("id", id);
					intent.putExtra("title", title);
					intent.putExtra("content", content);
					startActivity(intent);

				}
			});

			return convertView;
		}

		private void bindData(final ViewHolder holder,
				final PushMessage pushMessage) {
			// Logger.d(TAG, "---prgName="+invoice.getPrgName());
			holder.titleTv.setText(pushMessage.getTitle());
			holder.contentTv.setText(pushMessage.getContent());
			holder.idTv.setText(pushMessage.getMsgID());

			holder.indicateIv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					intentToMessageDesc();

				}
			});

		}

		// 跳转到详情界面
		private void intentToMessageDesc() {
			// TODO
			Intent intent = new Intent();
			return;
		}
	}

	class ViewHolder {
		TextView titleTv;
		TextView contentTv;
		ImageView indicateIv;

		// 存数据
		TextView idTv;
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
