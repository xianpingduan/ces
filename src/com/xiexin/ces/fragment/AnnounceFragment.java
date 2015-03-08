package com.xiexin.ces.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
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
import com.xiexin.ces.R;
import com.xiexin.ces.activity.AnnounceInfoActivity;
import com.xiexin.ces.entry.PushAnnounce;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.LoadingDialog;
import com.xiexin.ces.widgets.LoadingUIListView;
import com.xiexin.ces.widgets.PullListView.IListViewListener;

public class AnnounceFragment extends Fragment implements OnClickListener {

	private final static String TAG = "AnnounceFragment";
	private View parentView;
	private LoadingUIListView mListView;
	private AnnounceAdapter mAnnounceAdapter;

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

	private CheckBox mReadedCb;
	private CheckBox mNoReadCb;

	private static final int LOAD_LIST_NORMAL = 0;
	private static final int LOAD_LIST_REFRESH = 1;
	private static final int LOAD_LIST_LOADMORE = 2;

	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private Handler mMainUIHandler;

	public void setMainUIHandler(Handler handler) {
		mMainUIHandler = handler;
	}

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

		parentView = inflater.inflate(R.layout.fragment_announce, container,
				false);
		mListView = (LoadingUIListView) parentView
				.findViewById(R.id.announce_list);
		mListView.setHeaderPullEnable(true);
		mListView.setFooterPullEnable(true);
		mListView.setListViewListener(mListViewListener);

		mReadedCb = (CheckBox) parentView.findViewById(R.id.readed_cb);
		mNoReadCb = (CheckBox) parentView.findViewById(R.id.noread_cb);

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
			requestMsg(LOAD_LIST_REFRESH);
		}

		@Override
		public void onLoadMore() {
			requestMsg(LOAD_LIST_LOADMORE);
		}
	};

	private void initData() {

		if (mAnnounceAdapter == null)
			mAnnounceAdapter = new AnnounceAdapter();

		mListView.setAdapter(mAnnounceAdapter);

		// 请求数据
		requestMsg(LOAD_LIST_NORMAL);
		mReadedCb.setChecked(false);
		mNoReadCb.setChecked(true);

	}

	private void showConfirmDialog(final int msgId, final int msgType) {

		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setMessage(getString(R.string.sure_to_del_the_msg));
		builder.setTitle(null);
		builder.setPositiveButton(getString(R.string.sure),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// doDelMsg(msgId, msgType);
					}
				});
		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private static final int MSG_GET_ANNOUNCE_LIST_SUCCESS = 1;

	private static final int MSG_GET_ANNOUNCE_LIST_ERROR = 2;

	private static final int MSG_LAST_PAGE = 3;

	private static final int MSG_NO_DATA = 4;

	private static final int MSG_DEL_MSG_SUCCESS = 5;

	private static final int MSG_DEL_MSG_ERROR = 6;

	private Handler mUiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_GET_ANNOUNCE_LIST_SUCCESS:
				mListView.setFooterPullEnable(true);
				String data = (String) msg.obj;
				ArrayList<PushAnnounce> invoices = getAnnounceList(data);
				Logger.d(TAG, "invoices.size=" + invoices.size());
				if (invoices.size() > 0) {
					if (mCurrentPage == 1) {
						mAnnounceAdapter.addData(invoices,
								Constants.TYPE_LIST_ADD_COVER);
					} else {
						mAnnounceAdapter.addData(invoices,
								Constants.TYPE_LIST_ADD_APPEND);
					}
					mAnnounceAdapter.notifyDataSetChanged();
					mListView.stopHeaderRefresh();
					mListView.stopFooterRefresh();
					mCurrentPage++;

					if (invoices.size() <= Constants.PAGE_SIZE) {
						mListView.setFooterPullEnable(false);
					}

				} else if (invoices.size() == 0 && mCurrentPage == 1) {
					mAnnounceAdapter.addData(invoices,
							Constants.TYPE_LIST_ADD_COVER);
					mAnnounceAdapter.notifyDataSetChanged();
					mUiHandler.sendEmptyMessage(MSG_NO_DATA);
				} else {
					mUiHandler.sendEmptyMessage(MSG_LAST_PAGE);
				}
				dismissDialog();
				break;

			case MSG_GET_ANNOUNCE_LIST_ERROR:
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
						App.getAppContext().getString(R.string.no_announce),
						Toast.LENGTH_SHORT).show();
				break;

			case MSG_DEL_MSG_SUCCESS:
				Toast.makeText(
						App.getAppContext(),
						App.getAppContext().getString(R.string.del_msg_success),
						Toast.LENGTH_SHORT).show();

				// 刷新列表
				mCurrentPage = 1;
				requestMsg(LOAD_LIST_REFRESH);
				break;
			case MSG_DEL_MSG_ERROR:
				Toast.makeText(App.getAppContext(), (String) msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}

	};

	private ArrayList<PushAnnounce> getAnnounceList(String msgStr) {

		ArrayList<PushAnnounce> announceList = new ArrayList<PushAnnounce>();

		try {
			JSONArray arrays = new JSONArray(msgStr);
			for (int i = 0; i < arrays.length(); i++) {
				JSONObject jsonObject = arrays.getJSONObject(i);
				PushAnnounce announce = new PushAnnounce();

				announce.setContent(jsonObject.getString("content"));
				announce.setNoticeid(jsonObject.getString("noticeid"));
				announce.setFromuser(jsonObject.getString("fromuser"));
				announce.setTitle(jsonObject.getString("title"));
				
				String files = jsonObject.getString("filespath");
				if(files==null || files.equals("null")){
					files="";
				}
				announce.setFilespath(files);
				announceList.add(announce);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return announceList;

	}

	// account=web_101&amp;userid=600012&amp;bread=0&amp;page=1&amp;size=100

	private int mBread = 0;

	public void requestMsg(final int type) {

		if (type == LOAD_LIST_NORMAL) {
			showDialog();
		}

		String account = App.getSharedPreference().getString(
				Constants.ZHANG_TAO_CONN_NAME, "");

		StringBuffer urlSbf = new StringBuffer(App.getRootUrl()
				+ Constants.GET_ANNOUNCE_URL + "?");
		urlSbf.append("&account=").append(account);
		urlSbf.append("&page=").append(mCurrentPage);
		urlSbf.append("&size=").append(Constants.PAGE_SIZE);

		Logger.d(TAG, "urlSbf=" + urlSbf.toString());
		JsonObjectRequest json = new JsonObjectRequest(Method.GET,
				urlSbf.toString(), null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO
						try {
							int resCode = response.getInt("success");
							Message msg = Message.obtain();
							if (resCode == 0) {
								msg.what = MSG_GET_ANNOUNCE_LIST_SUCCESS;
								msg.obj = response.getString("data");
								Logger.d(TAG, "data=" + msg.obj);
							} else {
								msg.what = MSG_GET_ANNOUNCE_LIST_ERROR;
								msg.obj = response.get("msg");
							}
							mUiHandler.sendMessage(msg);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						mUiHandler
								.sendEmptyMessage(MSG_GET_ANNOUNCE_LIST_ERROR);
					}
				});

		mQueue.add(json);
		mQueue.start();
	}

	private void initView() {

		mReadedCb.setOnClickListener(this);
		mNoReadCb.setOnClickListener(this);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int i, long l) {
				// Toast.makeText(getActivity(), "Clicked item!",
				// Toast.LENGTH_LONG).show();
			}
		});
	}

	private class AnnounceAdapter extends BaseAdapter {

		private ArrayList<PushAnnounce> list = new ArrayList<PushAnnounce>();

		public void addData(ArrayList<PushAnnounce> data, int type) {

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
						R.layout.fragment_announce_list_item, null);
				holder = new ViewHolder();
				holder.idTv = (TextView) convertView.findViewById(R.id.id_tv);
				holder.titleTv = (TextView) convertView
						.findViewById(R.id.title_tv);
				holder.contentTv = (TextView) convertView
						.findViewById(R.id.content_tv);
				holder.indicateIv = (ImageView) convertView
						.findViewById(R.id.indicate_iv);
				holder.filesPathTv = (TextView) convertView.findViewById(R.id.filespath_tv);
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
					String filespath = holder.filesPathTv.getText().toString();
					Intent intent = new Intent();
					intent.setClass(getActivity(), AnnounceInfoActivity.class);
					intent.putExtra("id", id);
					intent.putExtra("title", title);
					intent.putExtra("content", content);
					intent.putExtra("filespath", filespath);
					startActivityForResult(intent, 1);

				}
			});

			holder.indicateIv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					PushAnnounce pushAnnounce = (PushAnnounce) v.getTag();
					String title = pushAnnounce.getTitle();
					String content = pushAnnounce.getContent();
					String announceId = pushAnnounce.getNoticeid();
					String filespath =  pushAnnounce.getFilespath();
					Intent intent = new Intent();
					intent.setClass(getActivity(), AnnounceInfoActivity.class);
					intent.putExtra("id", announceId);
					intent.putExtra("title", title);
					intent.putExtra("content", content);
					intent.putExtra("filespath", filespath);
					startActivityForResult(intent, 1);
				}
			});

			// convertView.setOnLongClickListener(new View.OnLongClickListener()
			// {
			//
			// @Override
			// public boolean onLongClick(View v) {
			// TextView idTv = (TextView) v.findViewById(R.id.id_tv);
			// TextView msgTypeTv = (TextView) v
			// .findViewById(R.id.msg_type_tv);
			// String idStr = idTv.getText().toString();
			// String msgType = msgTypeTv.getText().toString();
			// Logger.d(TAG, "idStr=" + idStr + ",msgType=" + msgType);
			// showConfirmDialog(Integer.parseInt(idStr),
			// Integer.parseInt(msgType));
			//
			// return false;
			// }
			// });

			return convertView;
		}

		private void bindData(final ViewHolder holder,
				final PushAnnounce pushAnnounce) {
			// Logger.d(TAG, "---prgName="+invoice.getPrgName());
			holder.titleTv.setText(pushAnnounce.getTitle());
			holder.contentTv.setText(pushAnnounce.getContent());
			holder.indicateIv.setTag(pushAnnounce);
			holder.idTv.setText(pushAnnounce.getNoticeid());
			String filespath = pushAnnounce.getFilespath().toString();
			holder.filesPathTv.setText(filespath.toString());

		}

	}

	class ViewHolder {
		TextView titleTv;
		TextView contentTv;
		ImageView indicateIv;
		// 存数据
		TextView idTv;
		TextView filesPathTv;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.return_iv:

			break;
		case R.id.readed_cb:
			if (!mReadedCb.isChecked()) {
				mReadedCb.setChecked(true);
			}
			mNoReadCb.setChecked(false);
			mBread = 1;
			mCurrentPage = 1;
			requestMsg(LOAD_LIST_NORMAL);
			break;
		case R.id.noread_cb:
			if (!mNoReadCb.isChecked()) {
				mNoReadCb.setChecked(true);
			}
			mReadedCb.setChecked(false);
			mBread = 0;
			mCurrentPage = 1;
			requestMsg(LOAD_LIST_NORMAL);
			break;
		default:
			break;
		}

	}

	// // del msg
	// private void doDelMsg( int msgId , int msgType )
	// {
	// StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) +
	// Constants.DEL_MSG + "?" );
	// String account = App.getSharedPreference( ).getString(
	// Constants.ZHANG_TAO_CONN_NAME , "" );
	// String userid = App.getSharedPreference( ).getString( Constants.USER_ID ,
	// "" );
	// urlSbf.append( "account=" ).append( account );
	// urlSbf.append( "&id=" ).append( msgId );
	// urlSbf.append( "&userid=" ).append( userid );
	// urlSbf.append( "&msgtype=" ).append( msgType );
	// Logger.d( TAG , "urlSbf=" + urlSbf.toString( ) );
	// JsonObjectRequest json = new JsonObjectRequest( Method.GET ,
	// urlSbf.toString( ) , null , new Listener< JSONObject >( )
	// {
	// @Override
	// public void onResponse( JSONObject response )
	// {
	// try
	// {
	// int resCode = response.getInt( "Success" );
	// Message msg = Message.obtain( );
	// if( resCode == 0 )
	// {
	// Logger.d( TAG , response.getString( "Data" ) );
	// Logger.d( TAG , "resCode=" + resCode );
	// mUiHandler.sendEmptyMessage( MSG_DEL_MSG_SUCCESS );
	// }
	// else
	// {
	// Logger.d( TAG , response.getString( "Msg" ) );
	// msg.what = MSG_DEL_MSG_ERROR;
	// msg.obj = response.getString( "Msg" );
	// mUiHandler.sendMessage( msg );
	// }
	// }
	// catch ( JSONException e )
	// {
	// e.printStackTrace( );
	// }
	// }
	// } , new ErrorListener( )
	// {
	// @Override
	// public void onErrorResponse( VolleyError error )
	// {
	// Logger.d( TAG , "onErrorResponse:" + error.getMessage( ) );
	// }
	// } );
	// mQueue.add( json );
	// mQueue.start( );
	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mCurrentPage = 1;
		requestMsg(LOAD_LIST_REFRESH);
		Logger.d(TAG, "AnnounceFragment,onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
	}
}
