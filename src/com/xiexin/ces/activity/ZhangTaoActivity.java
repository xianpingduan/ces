package com.xiexin.ces.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.entry.ZhangTao;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.ILoadingViewListener;
import com.xiexin.ces.widgets.LoadingUIListView;
import com.xiexin.ces.widgets.PullListView.IListViewListener;

public class ZhangTaoActivity extends Activity implements OnClickListener {

	public final static String TAG = "ZhangTaoActivity";

	private LoadingUIListView mListView;
	private ZhangTaoAdapter mZhangTaoAdapter;

	// header start
	private LinearLayout mReturnLl;
	private ImageView mReturnIv;
	private TextView mReturnTv;
	private TextView mTitle;
	private Button mBtn1;
	private Button mBtn2;

	// private int mRequestFrom;

	// header end

	private String mCheckAccInfo;
	private String mCheckConnName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_zhangtao);

		// mRequestFrom =
		// getIntent().getIntExtra(Constants.SERVER_CONFIG_SET_FROM, 0);

		initView();

		initData();
	}

	private void initView() {
		mListView = (LoadingUIListView) findViewById(R.id.zhangtao_list);
		mListView.setFooterPullEnable(false);
		mListView.setHeaderPullEnable(false);
		// mListView.setListViewListener(mListViewListener);
		// mListView.setPadaLoadingViewListener(mLoadingViewListener);
		if (mZhangTaoAdapter == null)
			mZhangTaoAdapter = new ZhangTaoAdapter();

		mListView.setAdapter(mZhangTaoAdapter);

		// header start
		mReturnLl = (LinearLayout) findViewById(R.id.return_ll);
		mReturnIv = (ImageView) findViewById(R.id.return_iv);
		mReturnTv = (TextView) findViewById(R.id.return_tv);
		mTitle = (TextView) findViewById(R.id.title);
		mBtn1 = (Button) findViewById(R.id.btn1);
		mBtn2 = (Button) findViewById(R.id.btn2);
		// /header end
		mReturnLl.setVisibility(View.GONE);
		mTitle.setText(getString(R.string.select_zt));
		mBtn1.setVisibility(View.VISIBLE);
		mBtn1.setText(getString(R.string.finish_to));

		mBtn1.setOnClickListener(this);
		mReturnLl.setOnClickListener(this);
	}

	private void initData() {
		String ztListStr = App.getSharedPreference().getString(
				Constants.ZHANG_TAO_LIST, "");
		Logger.d(TAG, "zhangtao info is " + ztListStr);
		if (!ztListStr.isEmpty()) {

			mZhangTaoAdapter.addData(getZhangTaoList(ztListStr));
			mZhangTaoAdapter.notifyDataSetChanged();

		}

	}

	private ArrayList<ZhangTao> getZhangTaoList(String ztListStr) {
		ArrayList<ZhangTao> ztList = new ArrayList<ZhangTao>();
		try {
			JSONArray arrays = new JSONArray(ztListStr);
			for (int i = 0; i < arrays.length(); i++) {
				JSONObject obj = arrays.getJSONObject(i);
				ZhangTao zt = new ZhangTao();
				zt.setAccInfo(obj.getString("AccInfo"));
				zt.setConnName(obj.getString("ConnName"));
				zt.setUserID(obj.getString("UserID"));
				zt.setUserName(obj.getString("UserName"));
				ztList.add(zt);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ztList;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private final static int MSG_REQUEST_DATA = 1;
	private static final int MSG_REFRESH_ZT_LIST = 2;

	private Handler mUiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			switch (msg.what) {
			case MSG_REQUEST_DATA:

				break;
			case MSG_REFRESH_ZT_LIST:
				if (mZhangTaoAdapter != null) {
					Logger.d(TAG, "mZhangTaoAdapter.notifyDataSetChanged()");
					mZhangTaoAdapter.notifyDataSetChanged();
				}

				break;

			default:
				break;
			}
		}

	};

	private final ILoadingViewListener mLoadingViewListener = new ILoadingViewListener() {

		@Override
		public void onRetryRequestData() {
			mUiHandler.sendEmptyMessage(MSG_REQUEST_DATA);
		}

		@Override
		public void onInitRequestData() {
			mUiHandler.sendEmptyMessage(MSG_REQUEST_DATA);
		}
	};

	private final IListViewListener mListViewListener = new IListViewListener() {
		@Override
		public void onRefresh() {
		}

		@Override
		public void onLoadMore() {
			reqZhangTaoList();
		}
	};

	private void reqZhangTaoList() {

	}

	// private final static int REFRESH = 1;
	// private final static int APPEND = 2;

	private class ZhangTaoAdapter extends BaseAdapter {

		private ArrayList<ZhangTao> list = new ArrayList<ZhangTao>();

		private HashMap<String, Boolean> mMap = new HashMap<String, Boolean>();

		public void addData(ArrayList<ZhangTao> data) {
			list.clear();
			list.addAll(data);

			// 初始化
			for (ZhangTao zt : list) {
				mMap.put(zt.getConnName(), false);
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
						R.layout.activity_zhangtao_list_item, null);
				holder = new ViewHolder();
				holder.ztFrameRl = (RelativeLayout) convertView
						.findViewById(R.id.zt_frame);
				holder.connNameTv = (TextView) convertView
						.findViewById(R.id.zt_name);
				holder.accInfoTv = (TextView) convertView
						.findViewById(R.id.zt_show_name);
				holder.ckBox = (CheckBox) convertView
						.findViewById(R.id.zt_check);

				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			bindData(holder, list.get(position));

			return convertView;
		}

		private void bindData(final ViewHolder holder, final ZhangTao zt) {
			holder.connNameTv.setText(zt.getConnName());
			holder.accInfoTv.setText(zt.getAccInfo());
			holder.ckBox.setTag(zt.getConnName());
			holder.accInfoTv.setTag(zt.getAccInfo());
			// Log.d(TAG,
			// "connName="+zt.getConnName()+"checked="+mMap.get(zt.getConnName()));
			if (mCheckConnName != null
					&& mCheckConnName.equals(zt.getConnName())) {
				holder.ckBox.setChecked(true);
				mMap.put(zt.getConnName(), true);
			} else {
				holder.ckBox.setChecked(false);
				mMap.put(zt.getConnName(), false);
			}
			holder.ckBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!holder.ckBox.isChecked()) {
						holder.ckBox.setChecked(false);
						mMap.put(holder.ckBox.getTag().toString(), false);
						mCheckConnName = null;
						mCheckAccInfo = null;
					} else {
						holder.ckBox.setChecked(true);
						mMap.put(holder.ckBox.getTag().toString(), true);
						mCheckConnName = holder.ckBox.getTag().toString();
						mCheckAccInfo = holder.accInfoTv.getTag().toString();
					}
					Logger.d(TAG, "mCheckConnName=" + mCheckConnName);

					mUiHandler.sendEmptyMessage(MSG_REFRESH_ZT_LIST);
				}
			});
			// 同步checkBox事件
			holder.ztFrameRl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((ViewHolder) v.getTag()).ckBox.performClick();
				}
			});
		}
	}

	class ViewHolder {
		RelativeLayout ztFrameRl;
		TextView accInfoTv;
		TextView connNameTv;
		CheckBox ckBox;
	}

	private void setResult() {
		Intent in = new Intent();
		in.putExtra(Constants.ZHANG_TAO_CONN_NAME, mCheckConnName);
		in.putExtra(Constants.ZHANG_TAO_ACCINFO, mCheckAccInfo);
		setResult(RESULT_OK, in);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn1:
			Logger.d(TAG, "选择的帐套是:" + mCheckConnName);
			if (mCheckConnName == null || mCheckConnName.isEmpty()) {
				Toast.makeText(ZhangTaoActivity.this,
						getString(R.string.please_select_zt),
						Toast.LENGTH_SHORT).show();
			} else {
				setResult();
			}
			break;
		case R.id.return_ll:
			onBackPressed();
			break;
		default:
			break;
		}

	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		finish();
	}
}
