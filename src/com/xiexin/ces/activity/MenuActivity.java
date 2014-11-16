package com.xiexin.ces.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.fragment.PendApprovalFragment;
import com.xiexin.ces.fragment.TipFragment;
import com.xiexin.ces.menu.ResideMenu;
import com.xiexin.ces.menu.ResideMenuItem;
import com.xiexin.ces.utils.Logger;

public class MenuActivity extends FragmentActivity implements
		View.OnClickListener {

	private final static String TAG = "MenuActivity";

	private ResideMenu resideMenu;
	private MenuActivity mContext;
	private ResideMenuItem itemPendApproval;
	private ResideMenuItem itemSendItem;
	private ResideMenuItem itemScratchUpcome;
	private ResideMenuItem itemApproved;
	private ResideMenuItem itemMessage;
	private ResideMenuItem itemAds;
	private ResideMenuItem itemInvoice;
	private ResideMenuItem itemRecord;

	// Fragment
	private PendApprovalFragment mPendApprovalFragment;
	private TipFragment mMessageFragment;
	private TipFragment mTipFragment;

	// this
	private TextView mTitleView;
	private Button mLeftMenuBtn;
	private Button mRightMenuBtn;

	// user info start
	private TextView mUserNameTv;
	private TextView mUserDeptTv;
	private TextView mUserAccountTv;
	private ImageView mUserHeaderIv;
	private LinearLayout mSwitchAccountLl;

	// user info end

	private boolean mAccountChanged = false;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = this;
		setUpMenu();
		if (savedInstanceState == null)
			if (mPendApprovalFragment == null)
				mPendApprovalFragment = new PendApprovalFragment();
		// mPendApprovalFragment.setArguments(args);
		mPendApprovalFragment.setKind(Constants.TYPE_PEND_APPROVAL_TASKS,
				mAccountChanged);
		changeFragment(mPendApprovalFragment);

		// 置位
		mAccountChanged = false;

		mTitleView.setText(getString(R.string.menu_pend_approval));

		initUserInfo();

		initSwitchAccoutLl();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();

		if (mAccountChanged) {
			if (mPendApprovalFragment == null)
				mPendApprovalFragment = new PendApprovalFragment();
			// mPendApprovalFragment.setArguments(args);
			mPendApprovalFragment.setKind(Constants.TYPE_PEND_APPROVAL_TASKS,
					mAccountChanged);
			changeFragment(mPendApprovalFragment);
		}
	}

	private void initUserInfo() {

		mUserNameTv = (TextView) findViewById(R.id.user_name_tv);
		mUserDeptTv = (TextView) findViewById(R.id.user_dept_tv);
		mUserAccountTv = (TextView) findViewById(R.id.user_account_tv);
		mUserHeaderIv = (ImageView) findViewById(R.id.user_head_img);

		String userName = App.getSharedPreference().getString(
				Constants.USER_NAME, "");
		String userDept = App.getSharedPreference().getString(Constants.DEPART,
				"");
		String userAccount = App.getSharedPreference().getString(
				Constants.ZHANG_TAO_ACCINFO, "");

		mUserNameTv.setText(userName);
		mUserDeptTv.setText(userDept);
		mUserAccountTv.setText(userAccount);

		mUserHeaderIv.setOnClickListener(this);
	}

	private void initSwitchAccoutLl() {

		mSwitchAccountLl = (LinearLayout) findViewById(R.id.switch_accout_ll);
		mSwitchAccountLl.setOnClickListener(this);
	}

	private void intentToZhangTao() {

		Intent intent = new Intent();
		intent.setClass(MenuActivity.this, ZhangTaoActivity.class);
		// intent.putExtra(Constants.SERVER_CONFIG_SET_FROM,
		// Constants.SERVER_CONFIG_SET_FROM_MENU);
		startActivityForResult(intent, 1);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			String ztConnName = data
					.getStringExtra(Constants.ZHANG_TAO_CONN_NAME);
			String ztAccInfo = data.getStringExtra(Constants.ZHANG_TAO_ACCINFO);
			if (!ztConnName.isEmpty()) {

				String oldConnName = App.getSharedPreference().getString(
						Constants.ZHANG_TAO_CONN_NAME, "");
				Log.d(TAG, "oldConnName=" + oldConnName + ",ztConnName="
						+ ztConnName);
				if (!oldConnName.isEmpty() && !ztConnName.equals(oldConnName)) {
					mAccountChanged = true;
				}
				Message msg = Message.obtain();
				msg.what = MSG_GET_ZT;
				msg.obj = ztAccInfo;
				mUiHandler.sendMessage(msg);
				saveAccoutInfo(ztConnName, ztAccInfo);

			} else {
				Logger.e(TAG, "账套为空");
			}

		}
	}

	private void saveAccoutInfo(String ztConnName, String ztAccInfo) {

		App.getSharedPreference().edit()
				.putString(Constants.ZHANG_TAO_ACCINFO, ztAccInfo).commit();
		App.getSharedPreference().edit()
				.putString(Constants.ZHANG_TAO_CONN_NAME, ztConnName).commit();

		Logger.d(TAG, "ztConnName=" + ztConnName + ",ztAccInfo=" + ztAccInfo);
	}

	private void setUpMenu() {

		// attach to current activity;
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(menuListener);
		// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
		// 150dip.
		resideMenu.setScaleValue(0.6f);

		// 右边菜单不显示
		resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

		// create menu items;
		itemPendApproval = new ResideMenuItem(this, R.drawable.icon_home,
				getString(R.string.menu_pend_approval));
		itemSendItem = new ResideMenuItem(this, R.drawable.icon_profile,
				getString(R.string.menu_sent_item));
		itemScratchUpcome = new ResideMenuItem(this, R.drawable.icon_calendar,
				getString(R.string.menu_scratch_upcome));
		itemApproved = new ResideMenuItem(this, R.drawable.icon_settings,
				getString(R.string.menu_approved));

		itemPendApproval.setOnClickListener(this);
		itemSendItem.setOnClickListener(this);
		itemScratchUpcome.setOnClickListener(this);
		itemApproved.setOnClickListener(this);

		itemMessage = new ResideMenuItem(this, R.drawable.icon_home,
				getString(R.string.menu_message));
		itemAds = new ResideMenuItem(this, R.drawable.icon_profile,
				getString(R.string.menu_ads));

		itemMessage.setOnClickListener(this);
		itemAds.setOnClickListener(this);

		itemInvoice = new ResideMenuItem(this, R.drawable.icon_calendar,
				getString(R.string.menu_invoice));
		itemRecord = new ResideMenuItem(this, R.drawable.icon_settings,
				getString(R.string.menu_record));

		itemInvoice.setOnClickListener(this);
		itemRecord.setOnClickListener(this);

		resideMenu.addMenuItem(itemPendApproval, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemSendItem, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemScratchUpcome, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemApproved, ResideMenu.DIRECTION_LEFT);

		resideMenu.addMenuItem(itemMessage, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemAds, ResideMenu.DIRECTION_LEFT);

		resideMenu.addMenuItem(itemInvoice, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemRecord, ResideMenu.DIRECTION_LEFT);

		// You can disable a direction by setting ->
		// resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

		mTitleView = (TextView) findViewById(R.id.title_bar_title);
		mLeftMenuBtn = (Button) findViewById(R.id.title_bar_left_menu);
		mRightMenuBtn = (Button) findViewById(R.id.title_bar_right_menu);
		mRightMenuBtn.setVisibility(View.GONE);

		mLeftMenuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
			}
		});
		// mRightMenuBtn.setOnClickListener(
		// new View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		// resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
		// }
		// });
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return resideMenu.dispatchTouchEvent(ev);
	}

	private static final int MSG_OPEN_MENU = 1;
	private static final int MSG_CLOSE_MENU = 2;
	private final static int MSG_GET_ZT = 3;

	private Handler mUiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case MSG_OPEN_MENU:

				break;

			case MSG_CLOSE_MENU:

				break;

			case MSG_GET_ZT:

				mUserAccountTv.setText((String) msg.obj);

				break;

			default:
				break;
			}

		}

	};

	@Override
	public void onClick(View view) {

		if (view == itemPendApproval) {
			if (mPendApprovalFragment == null) {
				mPendApprovalFragment = new PendApprovalFragment();
			}
			mPendApprovalFragment.setKind(Constants.TYPE_PEND_APPROVAL_TASKS,
					mAccountChanged);
			mTitleView.setText(getString(R.string.menu_pend_approval));
			changeFragment(mPendApprovalFragment);
		} else if (view == itemSendItem) {
			if (mPendApprovalFragment == null) {
				mPendApprovalFragment = new PendApprovalFragment();
			}
			mPendApprovalFragment.setKind(Constants.TYPE_SEND_ITEM_TASKS,
					mAccountChanged);
			mTitleView.setText(getString(R.string.menu_sent_item));
			changeFragment(mPendApprovalFragment);
		} else if (view == itemScratchUpcome) {
			if (mPendApprovalFragment == null) {
				mPendApprovalFragment = new PendApprovalFragment();

			}
			mPendApprovalFragment.setKind(Constants.TYPE_SCRATCH_UPCOME_TASKS,
					mAccountChanged);
			mTitleView.setText(getString(R.string.menu_scratch_upcome));
			changeFragment(mPendApprovalFragment);
		} else if (view == itemApproved) {
			if (mPendApprovalFragment == null) {
				mPendApprovalFragment = new PendApprovalFragment();
			}
			mPendApprovalFragment.setKind(Constants.TYPE_APPROVED_TASKS,
					mAccountChanged);
			mTitleView.setText(getString(R.string.menu_approved));
			changeFragment(mPendApprovalFragment);
		} else if (view == itemMessage) {

			if (mMessageFragment == null) {
				mMessageFragment = new TipFragment();
			}
			mTitleView.setText(getString(R.string.menu_message));
			changeFragment(mMessageFragment);
		} else if (view == itemAds) {
			if (mTipFragment == null) {
				mTipFragment = new TipFragment();
			}
			mTitleView.setText(getString(R.string.menu_ads));
			changeFragment(mTipFragment);
		} else if (view == itemInvoice) {
			if (mTipFragment == null) {
				mTipFragment = new TipFragment();
			}
			mTitleView.setText(getString(R.string.menu_invoice));
			changeFragment(mTipFragment);
		} else if (view == itemRecord) {
			if (mTipFragment == null) {
				mTipFragment = new TipFragment();
			}
			mTitleView.setText(getString(R.string.menu_record));
			changeFragment(mTipFragment);
		} else if (view == mUserHeaderIv) {

		} else if (view == mSwitchAccountLl) {

			intentToZhangTao();
		}

		resideMenu.closeMenu();
	}

	// 菜单打开和关闭监听
	private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
		@Override
		public void openMenu() {
			mUiHandler.sendEmptyMessage(MSG_OPEN_MENU);
			// Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT)
			// .show();
		}

		@Override
		public void closeMenu() {
			mUiHandler.sendEmptyMessage(MSG_CLOSE_MENU);
			// Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT)
			// .show();
		}
	};

	private void changeFragment(Fragment targetFragment) {
		resideMenu.clearIgnoredViewList();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_fragment, targetFragment, "fragment")
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();
	}

	// What good method is to access resideMenu？
	public ResideMenu getResideMenu() {
		return resideMenu;
	}

	private long lastClickKeyBackTime;

	// 退出
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed( );
		long now = System.currentTimeMillis();
		if (now - lastClickKeyBackTime > 2000) {
			Toast.makeText(MenuActivity.this,
					getString(R.string.click_exit_label), Toast.LENGTH_SHORT)
					.show();
			lastClickKeyBackTime = now;
		} else {
			boolean autoLogin = App.getSharedPreference().getBoolean(
					Constants.AUTO_LOGIN, false);
			boolean remeberPwd = App.getSharedPreference().getBoolean(
					Constants.REMEBER_PWD, false);

			if (autoLogin == false && remeberPwd == false) {
				App.clear();
			}
			finish();
		}

	}
}
