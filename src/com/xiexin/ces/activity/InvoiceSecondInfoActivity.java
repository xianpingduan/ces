package com.xiexin.ces.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.LoadingUIListView;

public class InvoiceSecondInfoActivity extends Activity implements
		OnClickListener {

	public final static String TAG = "InvoiceSecondInfoActivity";

	private LoadingUIListView mListView;
	private InvoiceSecondInfoAdapter mInvoiceSecondInfoAdapter;

	// header start
	private LinearLayout mReturnLl;
	private ImageView mReturnIv;
	private TextView mReturnTv;
	private TextView mTitle;
	private Button mBtn1;
	private Button mBtn2;

	private String mDetConfigStr;
	private String mDetContentStr;
	private String mDetHeaderStr;
	private String mPrgid;

	private String[] mDetConfig;
	private JSONObject mDetContent;
	private JSONObject mDetHeader;// 表头

	private LayoutInflater mInflater;

	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_invoice_second_info);

		// mRequestFrom =
		// getIntent().getIntExtra(Constants.SERVER_CONFIG_SET_FROM, 0);

		initView();

		initData();
	}

	private void initView() {
		mListView = (LoadingUIListView) findViewById(R.id.invoice_second_info_list);
		mListView.setFooterPullEnable(false);
		mListView.setHeaderPullEnable(false);
		// mListView.setListViewListener(mListViewListener);
		// mListView.setPadaLoadingViewListener(mLoadingViewListener);
		if (mInvoiceSecondInfoAdapter == null)
			mInvoiceSecondInfoAdapter = new InvoiceSecondInfoAdapter();

		mListView.setAdapter(mInvoiceSecondInfoAdapter);

		// header start
		mReturnLl = (LinearLayout) findViewById(R.id.return_ll);
		mReturnIv = (ImageView) findViewById(R.id.return_iv);
		mReturnTv = (TextView) findViewById(R.id.return_tv);
		mTitle = (TextView) findViewById(R.id.title);
		mBtn1 = (Button) findViewById(R.id.btn1);
		mBtn2 = (Button) findViewById(R.id.btn2);
		// /header end
		mReturnLl.setVisibility(View.VISIBLE);
		mTitle.setText(getString(R.string.invoice_info));

		mReturnLl.setOnClickListener(this);

		mInflater = App.getLayoutInflater();
	}

	private void initData() {

		Intent intent = getIntent();
		mDetConfigStr = intent.getStringExtra(Constants.DET_CONFIG);
		mDetContentStr = intent.getStringExtra(Constants.DET_INFO);
		mDetHeaderStr = intent.getStringExtra(Constants.DET_HEAD_CONFIG);
		mPrgid = intent.getStringExtra(Constants.PRGID);
		
		Logger.d(TAG, "mDetConfigStr="+mDetConfigStr);
		Logger.d(TAG, "mDetContentStr="+mDetContentStr);
		Logger.d(TAG, "mDetHeaderStr="+mDetHeaderStr);

		if (mDetConfigStr == null || mDetConfigStr.isEmpty()) {
			mDetConfig = Constants.getDetDefaultConfig(mPrgid);
		} else {
			mDetConfig = mDetConfigStr.split(",");
		}
		// mDetContent = new JSONObject(mDetContentStr);
		if (mDetHeaderStr == null || mDetHeaderStr.isEmpty()) {
			mDetHeader = Constants.getDet(mPrgid);
		} else {
			try {
				mDetHeader = new JSONObject(mDetHeaderStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		Logger.d(TAG, "mDetContentStr=" + mDetContentStr + ",mPrgid=" + mPrgid);

		try {
			JSONArray array = new JSONArray(mDetContentStr);
			mInvoiceSecondInfoAdapter.addData(array);
			mInvoiceSecondInfoAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// private final static int REFRESH = 1;
	// private final static int APPEND = 2;

	private class InvoiceSecondInfoAdapter extends BaseAdapter {

		private JSONArray array = new JSONArray();

		public void addData(JSONArray array) {
			this.array = array;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return array.length();
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

			LinearLayout ll = new LinearLayout(InvoiceSecondInfoActivity.this);
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			ll.setLayoutParams(lp);
			ll.setOrientation(LinearLayout.VERTICAL);
			for (int i = 0; i < mDetConfig.length; i++) {

				RelativeLayout view = null;
				if (i == 0) {
					view = (RelativeLayout) mInflater
							.inflate(
									R.layout.activity_invoice_second_info_tv_item,
									null);
					
				}else{
					view = (RelativeLayout) mInflater
							.inflate(
									R.layout.activity_invoice_second_info_tv_item_2,
									null);
				}
				TextView headerTv = (TextView) view
						.findViewById(R.id.table_header_name);
				TextView contentTv = (TextView) view
						.findViewById(R.id.table_content);
				try {
					
					if(i == 0){
						headerTv.setText(mDetHeader.getString(mDetConfig[i].toLowerCase()) );
					}else{
						headerTv.setText(mDetHeader.getString(mDetConfig[i].toLowerCase()) + ":");
					}
					JSONObject jsonObject = array.getJSONObject(position);
					String content = jsonObject.getString(mDetConfig[i].toLowerCase());
					if (content == null || content.equals("null")) {
						content = "";
					}

					if (content.contains("T00:00:00")) {
						Date date = new Date();
						try {
							date = sdf.parse(content);
							content = sdf.format(date);
						} catch (ParseException e) {
							e.printStackTrace();
							Logger.d(TAG, "date format error");
						}
					}
					contentTv.setText(content);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (i == 0) {
					headerTv.setTextColor(InvoiceSecondInfoActivity.this
							.getResources().getColor(
									R.color.header_bar_btn_txt_press_color));
					contentTv.setTextColor(InvoiceSecondInfoActivity.this
							.getResources().getColor(
									R.color.header_bar_btn_txt_press_color));
					view.setBackgroundColor(InvoiceSecondInfoActivity.this
							.getResources().getColor(
									R.color.approval_list_item_bg_color));
				}

				ll.addView(view);
			}

			convertView = ll;

			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
