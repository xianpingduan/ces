package com.xiexin.ces.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.expandablelist.PinnedHeaderExpandableListView;
import com.xiexin.ces.expandablelist.PinnedHeaderExpandableListView.OnHeaderUpdateListener;
import com.xiexin.ces.expandablelist.StickyLayout;
import com.xiexin.ces.expandablelist.StickyLayout.OnGiveUpTouchEventListener;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.utils.StringUtils;

public class InvoiceSecondInfoActivity extends Activity implements
		OnClickListener, ExpandableListView.OnChildClickListener,
		ExpandableListView.OnGroupClickListener, OnHeaderUpdateListener,
		OnGiveUpTouchEventListener {

	public final static String TAG = "InvoiceSecondInfoActivity";

	// private LoadingUIListView mListView;
	// private InvoiceSecondInfoAdapter mInvoiceSecondInfoAdapter;

	// allenduan add at 20150121 start
	private PinnedHeaderExpandableListView expandableListView;
	private StickyLayout stickyLayout;
	private final LinkedList<String> groupList = new LinkedList<String>();
	// private LinkedList<JSONArray> childList;

	private MyexpandableListAdapter adapter;
	// allenduan add at 20150121 end

	// header start
	private LinearLayout mReturnLl;
	private FrameLayout mReturnIv;
	private TextView mReturnTv;
	private TextView mTitle;
	private Button mBtn1;
	private Button mBtn2;

	private String mDetConfigStr;
	private String mDetContentStr;
	private String[] mDetContentStrs;
	private String mDetHeaderStr;
	private String mPrgid;

	// 需显示的字段 数组
	private final LinkedList<String[]> mDetConfigList = new LinkedList<String[]>();
	// 需显示内容 child
	private final LinkedList<JSONArray> mDetContentList = new LinkedList<JSONArray>();
	// 需显示头部
	private final LinkedList<JSONObject> mDetHeaderList = new LinkedList<JSONObject>();

	private String[] mDetConfig;
	// private JSONObject mDetContent;
	private JSONObject mDetHeader;// 表头

	private LayoutInflater mInflater;

	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private String mDetNameStr = "";
	private String[] mDetNames;
	private int mDetCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_invoice_second_info);

		// mRequestFrom =
		// getIntent().getIntExtra(Constants.SERVER_CONFIG_SET_FROM, 0);

		initView();

		// initData();
	}

	private void initView() {
		// mListView = (LoadingUIListView)
		// findViewById(R.id.invoice_second_info_list);
		// mListView.setFooterPullEnable(false);
		// mListView.setHeaderPullEnable(false);
		// // mListView.setListViewListener(mListViewListener);
		// // mListView.setPadaLoadingViewListener(mLoadingViewListener);
		// if (mInvoiceSecondInfoAdapter == null)
		// mInvoiceSecondInfoAdapter = new InvoiceSecondInfoAdapter();
		//
		// mListView.setAdapter(mInvoiceSecondInfoAdapter);

		// header start
		mReturnLl = (LinearLayout) findViewById(R.id.return_ll);
		mReturnIv = (FrameLayout) findViewById(R.id.return_iv);
		mReturnTv = (TextView) findViewById(R.id.return_tv);
		mTitle = (TextView) findViewById(R.id.title);
		mBtn1 = (Button) findViewById(R.id.btn1);
		mBtn2 = (Button) findViewById(R.id.btn2);
		// /header end
		mReturnLl.setVisibility(View.VISIBLE);
		mTitle.setText(getString(R.string.invoice_info));

		mReturnLl.setOnClickListener(this);

		mInflater = App.getLayoutInflater();

		expandableListView = (PinnedHeaderExpandableListView) findViewById(R.id.expandablelist);
		stickyLayout = (StickyLayout) findViewById(R.id.sticky_layout);

		initData();

		adapter = new MyexpandableListAdapter(this);
		expandableListView.setAdapter(adapter);

		// 展开所有group
		for (int i = 0, count = expandableListView.getCount(); i < count; i++) {
			expandableListView.expandGroup(i);
		}

		expandableListView.setOnHeaderUpdateListener(this);
		expandableListView.setOnChildClickListener(this);
		expandableListView.setOnGroupClickListener(this);
		stickyLayout.setOnGiveUpTouchEventListener(this);
	}

	private void generateDetNames(String detNames) {

		if (detNames != null && !detNames.isEmpty()) {
			mDetNames = detNames.split(",");
		}
	}

	private void generateGroupList() {
		groupList.clear();
		for (String s : mDetNames) {
			Logger.d(TAG, "generateGroupList,s=" + s);
			groupList.add(s.trim());
		}
	}

	private void generateDetConfig(String detConfig) {
		mDetConfigList.clear();
		if(detConfig!=null&&!detConfig.isEmpty()){
			String tempArray[] = detConfig.split("\\|");
			// Log.d(TAG, "tempArray.length="+tempArray.length);
			for (String a : tempArray) {
				Logger.d(TAG, "generateDetConfig,a=" + a);
				mDetConfigList.add(a.split(","));
			}
		}
		// Log.d(TAG, "detConfig="+detConfig);
	}

	private void generateDetContent(String[] detContentStrs) {
		mDetContentList.clear();
		for (String s : detContentStrs) {
			try {
				JSONArray array = new JSONArray(s);
				Logger.d(TAG, "generateDetContent,array=" + array.toString());
				mDetContentList.add(array);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void initData() {

		Intent intent = getIntent();
		mDetConfigStr = intent.getStringExtra(Constants.DET_CONFIG);
		generateDetConfig(mDetConfigStr);
		// mDetContentStr = intent.getStringExtra(Constants.DET_INFO);

		Bundle bundle = intent.getBundleExtra(Constants.DET_INFO);
		if (bundle != null) {
			mDetContentStrs = bundle.getStringArray(Constants.DET_INFOS);
			generateDetContent(mDetContentStrs);
		}

		mDetNameStr = intent.getStringExtra(Constants.DET_NAMES);
		mDetCount = intent.getIntExtra(Constants.DET_COUNT, 0);
		generateDetNames(mDetNameStr);
		generateGroupList();

		mDetHeaderStr = intent.getStringExtra(Constants.DET_HEAD_CONFIG);
		mPrgid = intent.getStringExtra(Constants.PRGID);

		Logger.d(TAG, "mDetConfigStr=" + mDetConfigStr);
		Logger.d(TAG, "mDetContentStr=" + mDetContentStr);
		Logger.d(TAG, "mDetHeaderStr=" + mDetHeaderStr);
		Logger.d(TAG, "mDetNameStr=" + mDetNameStr);
		Logger.d(TAG, "mDetCount=" + mDetCount);

		// if (mDetConfigStr == null || mDetConfigStr.isEmpty()) {
		// mDetConfig = Constants.getDetDefaultConfig(mPrgid);
		// } else {
		// mDetConfig = mDetConfigStr.split(",");
		// }
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

		// try {
		// JSONArray array = new JSONArray(mDetContentStr);
		// mInvoiceSecondInfoAdapter.addData(array);
		// mInvoiceSecondInfoAdapter.notifyDataSetChanged();
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }

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

				} else {
					view = (RelativeLayout) mInflater.inflate(
							R.layout.activity_invoice_second_info_tv_item_2,
							null);
				}
				TextView headerTv = (TextView) view
						.findViewById(R.id.table_header_name);
				TextView contentTv = (TextView) view
						.findViewById(R.id.table_content);
				try {

					if (i == 0) {
						headerTv.setText(mDetHeader.getString(mDetConfig[i]
								.toLowerCase()));
					} else {
						headerTv.setText(mDetHeader.getString(mDetConfig[i]
								.toLowerCase()) + ":");
					}
					JSONObject jsonObject = array.getJSONObject(position);
					String content = jsonObject.getString(mDetConfig[i]
							.toLowerCase());
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
					headerTv.setTextColor(InvoiceSecondInfoActivity.this.getResources().getColor(R.color.header_bar_btn_txt_press_color));
					contentTv.setTextColor(InvoiceSecondInfoActivity.this.getResources().getColor(R.color.header_bar_btn_txt_press_color));
					view.setBackgroundColor(InvoiceSecondInfoActivity.this.getResources().getColor(R.color.approval_list_item_bg_color));
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

	/***
	 * 数据源
	 * 
	 * @author Administrator
	 * 
	 */
	class MyexpandableListAdapter extends BaseExpandableListAdapter {
		private final Context context;
		private final LayoutInflater inflater;

		public MyexpandableListAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		// 返回父列表个数
		@Override
		public int getGroupCount() {
			return groupList.size();
		}

		// 返回子列表个数
		@Override
		public int getChildrenCount(int groupPosition) {
			return mDetContentList.get(groupPosition).length();
		}

		@Override
		public Object getGroup(int groupPosition) {

			return groupList.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			try {
				return mDetContentList.get(groupPosition).get(childPosition);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {

			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHolder groupHolder = null;
			if (convertView == null) {
				groupHolder = new GroupHolder();
				convertView = inflater.inflate(R.layout.group, null);
				groupHolder.textView = (TextView) convertView
						.findViewById(R.id.group);
				groupHolder.imageView = (ImageView) convertView
						.findViewById(R.id.image);
				convertView.setTag(groupHolder);
			} else {
				groupHolder = (GroupHolder) convertView.getTag();
			}

			groupHolder.textView.setText(((String) getGroup(groupPosition)));

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			LinearLayout ll = new LinearLayout(InvoiceSecondInfoActivity.this);
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			ll.setLayoutParams(lp);
			ll.setOrientation(LinearLayout.VERTICAL);

			String[] tempDetConfig = mDetConfigList.get(groupPosition);

			for (int i = 0; i < tempDetConfig.length; i++) {

				RelativeLayout view = null;
				if (i == 0) {
					view = (RelativeLayout) mInflater
							.inflate(
									R.layout.activity_invoice_second_info_tv_item,
									null);

				} else {
					view = (RelativeLayout) mInflater.inflate(
							R.layout.activity_invoice_second_info_tv_item_2,
							null);
				}
				TextView headerTv = (TextView) view
						.findViewById(R.id.table_header_name);
				TextView contentTv = (TextView) view
						.findViewById(R.id.table_content);
				
				
				try {

					if (i == 0) {
						headerTv.setText(mDetHeader.getString(tempDetConfig[i]
								.trim().toLowerCase()));
					} else {
						headerTv.setText(mDetHeader.getString(tempDetConfig[i]
								.trim().toLowerCase()) + ":");
					}
					JSONArray array = mDetContentList.get(groupPosition);
					JSONObject jsonObject = array.getJSONObject(childPosition);
					String content = jsonObject.getString(tempDetConfig[i]
							.trim().toLowerCase());
					if (content == null || content.equals("null")) {
						content = "";
					}

					if (content.contains("T")) {
						Date date = new Date();
						try {
							date = sdf.parse(content);
							content = sdf.format(date);
						} catch (ParseException e) {
							e.printStackTrace();
							Logger.d(TAG, "date format error");
						}
					}
					
					//金额处理
					if (!content.isEmpty() && (tempDetConfig[i].toLowerCase().equals("totalcost")||tempDetConfig[i].toLowerCase().equals("actcost"))) {
						content = "￥" + StringUtils.priceDecimal(Double.parseDouble(content));
					}
					
					
					if (tempDetConfig[i].toLowerCase().equals("totalcost")||tempDetConfig[i].toLowerCase().equals("actcost")) {
						contentTv.setTextColor(getResources().getColor(
								R.color.pl_main_red_color));
					} else {
						contentTv.setTextColor(getResources().getColor(
								R.color.pl_main_text_color));
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

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	class GroupHolder {
		TextView textView;
		ImageView imageView;
	}

	@Override
	public boolean onGroupClick(final ExpandableListView parent, final View v,
			int groupPosition, final long id) {

		return false;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// Toast.makeText(InvoiceSecondInfoActivity.this,
		// mDetContentList.get(groupPosition).get(childPosition)., 1)
		// .show();
		return false;
	}

	@Override
	public View getPinnedHeader() {
		View headerView = getLayoutInflater().inflate(
				R.layout.group, null);
		headerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		return headerView;
	}

	@Override
	public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
		String firstVisibleGroup = (String) adapter
				.getGroup(firstVisibleGroupPos);
		TextView textView = (TextView) headerView.findViewById(R.id.group);
		textView.setText(firstVisibleGroup);
	}

	@Override
	public boolean giveUpTouchEvent(MotionEvent event) {
		if (expandableListView.getFirstVisiblePosition() == 0) {
			View view = expandableListView.getChildAt(0);
			if (view != null && view.getTop() >= 0) {
				return true;
			}
		}
		return false;
	}

}
