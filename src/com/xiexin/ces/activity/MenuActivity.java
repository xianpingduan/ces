package com.xiexin.ces.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.xiexin.ces.db.EmployeeManager;
import com.xiexin.ces.fragment.AnnounceFragment;
import com.xiexin.ces.fragment.MessageFragment;
import com.xiexin.ces.fragment.PendApprovalFragment;
import com.xiexin.ces.fragment.TipFragment;
import com.xiexin.ces.menu.ResideMenu;
import com.xiexin.ces.menu.ResideMenuItem;
import com.xiexin.ces.update.SelfUpgrade;
import com.xiexin.ces.update.SelfUpgrade.SelfUpdateListener;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.LoadingDialog;

public class MenuActivity extends FragmentActivity implements
		View.OnClickListener,SelfUpdateListener {

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

	private ResideMenuItem itemUpdate;

	// Fragment
	private PendApprovalFragment mPendApprovalFragment;
	private MessageFragment mMessageFragment;
	private TipFragment mTipFragment;
	private AnnounceFragment mAnnounceFragment;

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
	private LinearLayout mLoginOutLl;

	// user info end

	private boolean mAccountChanged = false;

	// 网络请求

	private RequestQueue mQueue;

	private EmployeeManager mEmployeeManager;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = this;

		mQueue = Volley.newRequestQueue(App.getAppContext());

		mEmployeeManager = EmployeeManager.getInstance(App.getAppContext());

		mEmployeeManager.setHandler(mUiHandler);

		setUpMenu();

		// 置位
		mAccountChanged = false;

		initUserInfo();

		initSwitchAccoutLl();

		initLoginOutLl();

		boolean isConnChanged = getIntent().getBooleanExtra(
				Constants.CONN_CHANGED, false);
		// request
		long currentTime = System.currentTimeMillis();
		long nextReqTime = App.getSharedPreference().getLong(
				Constants.THE_SYNC_EMPLOYEE_TIME, 0);

		Logger.d(TAG, "isConnChange=" + isConnChanged);
		Logger.d(TAG, "currentTime > nextReqTime="
				+ (currentTime > nextReqTime));
		if (isConnChanged || (currentTime > nextReqTime)) {
			requestEmployees("");
		}

		boolean b = getIntent().getBooleanExtra(Constants.MENU_HANDLE, false);
		if (b) {
			if (mMessageFragment == null) {
				mMessageFragment = new MessageFragment();
			}
			mMessageFragment.setMainUIHandler(mUiHandler);
			changeFragment(mMessageFragment);
			mTitleView.setText(getString(R.string.menu_message));
		} else {
			if (mPendApprovalFragment == null)
				mPendApprovalFragment = new PendApprovalFragment();
			// mPendApprovalFragment.setArguments(args);
			mPendApprovalFragment.setKind(Constants.TYPE_PEND_APPROVAL_TASKS,
					mAccountChanged);
			changeFragment(mPendApprovalFragment);
			mTitleView.setText(getString(R.string.menu_pend_approval));
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {

		boolean b = intent.getBooleanExtra(Constants.MENU_HANDLE, false);
		Logger.d(TAG, "b=" + b);
		if (b) {
			if (mMessageFragment == null) {
				mMessageFragment = new MessageFragment();
			}
			mMessageFragment.setMainUIHandler(mUiHandler);
			changeFragment(mMessageFragment);
			mTitleView.setText(getString(R.string.menu_message));
		}
		super.onNewIntent(intent);
	}

	private void initLoginOutLl() {

		mLoginOutLl = (LinearLayout) findViewById(R.id.login_out_ll);
		mLoginOutLl.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		Logger.d(TAG, "onResume");
		super.onResume();

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

	private void sureToLoginOut() {

		AlertDialog.Builder builder = new Builder(MenuActivity.this);
		builder.setMessage(getString(R.string.sure_to_login_out));
		builder.setTitle(null);
		builder.setPositiveButton(getString(R.string.sure),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						loginout();
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

	private void loginout() {
		App.clear();

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(MenuActivity.this, LoginActivity.class);
		startActivity(intent);

		finish();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Logger.d(TAG, "requestCode=" + requestCode + ",resultCode="+
		// resultCode + ",data=" + data.toString());
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String ztConnName = data
					.getStringExtra(Constants.ZHANG_TAO_CONN_NAME);
			String ztAccInfo = data.getStringExtra(Constants.ZHANG_TAO_ACCINFO);
			if (!ztConnName.isEmpty()) {
				App.getSharedPreference().edit()
						.putString(Constants.ZHANG_TAO_CONN_NAME, ztConnName)
						.commit();
				App.getSharedPreference().edit()
						.putString(Constants.ZHANG_TAO_ACCINFO, ztAccInfo)
						.commit();
				Message msg = Message.obtain();
				msg.what = MSG_GET_ZT;
				msg.obj = ztAccInfo;
				mUiHandler.sendMessage(msg);

			} else {
				Logger.e(TAG, "账套为空");
			}

		}

	}
	
	
	public void showOrNoApprovalTip(final boolean b){
		Logger.d(TAG, "showOrNoApprovalTip,b="+b);
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				itemPendApproval.getIv_badge().setVisibility(b?View.VISIBLE:View.GONE);
			}
		});

	}
	
	public void showOrNoMsgTip(final boolean b){
		Logger.d(TAG, "showOrNoMsgTip,b="+b);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				itemMessage.getIv_badge().setVisibility(b?View.VISIBLE:View.GONE);
			}
		});
	}
	

	private void setUpMenu() {

		// attach to current activity;
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.color.menu_bg_color);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(menuListener);
		// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
		// 150dip.
		resideMenu.setScaleValue(0.6f);

		// 右边菜单不显示
		resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

		// create menu items;
		itemPendApproval = new ResideMenuItem(this,
				R.drawable.icon_pend_approval,
				getString(R.string.menu_pend_approval));
		itemSendItem = new ResideMenuItem(this, R.drawable.icon_send_item,
				getString(R.string.menu_sent_item));
		itemScratchUpcome = new ResideMenuItem(this,
				R.drawable.icon_scratch_upcome,
				getString(R.string.menu_scratch_upcome));
		itemApproved = new ResideMenuItem(this, R.drawable.icon_approved,
				getString(R.string.menu_approved));

		itemPendApproval.setOnClickListener(this);
		itemSendItem.setOnClickListener(this);
		itemScratchUpcome.setOnClickListener(this);
		itemApproved.setOnClickListener(this);

		itemMessage = new ResideMenuItem(this, R.drawable.icon_message,
				getString(R.string.menu_message));
		itemAds = new ResideMenuItem(this, R.drawable.icon_ad,
				getString(R.string.menu_ads));

		itemMessage.setOnClickListener(this);
		itemAds.setOnClickListener(this);
		
		
		itemUpdate = new ResideMenuItem(this, R.drawable.icon_scratch_upcome,
				getString(R.string.menu_update));
		
		itemInvoice = new ResideMenuItem(this, R.drawable.fapiao_check,
				getString(R.string.menu_invoice));
		itemRecord = new ResideMenuItem(this, R.drawable.icon_record,
				getString(R.string.menu_record));

		itemInvoice.setOnClickListener(this);
		itemRecord.setOnClickListener(this);
		
		itemUpdate.setOnClickListener(this);

		List<ResideMenuItem> oneList = new ArrayList<ResideMenuItem>();
		oneList.clear();
		oneList.add(itemPendApproval);
		oneList.add(itemSendItem);
		oneList.add(itemScratchUpcome);
		oneList.add(itemApproved);

		List<ResideMenuItem> twoList = new ArrayList<ResideMenuItem>();
		twoList.clear();
		twoList.add(itemMessage);
		twoList.add(itemAds);

		List<ResideMenuItem> thirdList = new ArrayList<ResideMenuItem>();
		thirdList.clear();
		thirdList.add(itemInvoice);
		thirdList.add(itemRecord);

		List<ResideMenuItem> fourList = new ArrayList<ResideMenuItem>();
		fourList.clear();
		fourList.add(itemUpdate);

		// resideMenu.addMenuItem( itemPendApproval ,
		// ResideMenu.DIRECTION_LEFT);
		// resideMenu.addMenuItem( itemSendItem , ResideMenu.DIRECTION_LEFT );
		// resideMenu.addMenuItem( itemScratchUpcome ,
		// ResideMenu.DIRECTION_LEFT);
		// resideMenu.addMenuItem( itemApproved , ResideMenu.DIRECTION_LEFT );

		resideMenu.addMenuItem(oneList, ResideMenu.DIRECTION_LEFT);

		// resideMenu.addMenuItem( itemMessage , ResideMenu.DIRECTION_LEFT );
		// resideMenu.addMenuItem( itemAds , ResideMenu.DIRECTION_LEFT );

		resideMenu.addMenuItem(twoList, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(thirdList, ResideMenu.DIRECTION_LEFT);
		
		resideMenu.addMenuItem(itemUpdate, ResideMenu.DIRECTION_LEFT);

		// resideMenu.addMenuItem(itemInvoice, ResideMenu.DIRECTION_LEFT);
		// resideMenu.addMenuItem(itemRecord, ResideMenu.DIRECTION_LEFT);

		// resideMenu.addMenuItem( thirdList , ResideMenu.DIRECTION_LEFT );

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
				String account = (String) msg.obj;
				if (!account.equals(mUserAccountTv.getText().toString())) {
					mAccountChanged = true;
					if (mPendApprovalFragment == null)
						mPendApprovalFragment = new PendApprovalFragment();
					// mPendApprovalFragment.setArguments(args);
					mPendApprovalFragment
							.setKind(Constants.TYPE_PEND_APPROVAL_TASKS,
									mAccountChanged);
					changeFragment(mPendApprovalFragment);
					mTitleView.setText(getString(R.string.menu_pend_approval));
					// 同步人员数据
					requestEmployees("");
				}
				mUserAccountTv.setText((String) msg.obj);
				break;

			case MSG_GET_EMPLOYEE_LIST_SUCCESS:
				String data = (String) msg.obj;
				Logger.d(TAG, "data=" + data);
				EmployeeManager.getInstance(mContext).saveResult(data);
				break;
			case MSG_GET_EMPLOYEE_LIST_ERROR:
				dismissDialog();
				break;
			case MSG_SAVE_EMPLOYEE_LIST_SUCCESS:
				Logger.d(TAG, "save data success!");
				// resideMenu.closeMenu();
				dismissDialog();
				long next_req_time = System.currentTimeMillis()
						+ Constants.SYNC_EMPLOYEE_GAP_TIME;
				Logger.d(TAG, "next_req_time =" + next_req_time);
				App.getSharedPreference()
						.edit()
						.putLong(Constants.THE_SYNC_EMPLOYEE_TIME,
								next_req_time).commit();

				// checkUpdate
				// checkUpdate(Constants.CHECK_UPDATE_AUTO);

				break;
			case MSG_FROM_FRAGMENT_CLOSE_MENU:
				resideMenu.closeMenu();
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
			mPendApprovalFragment.setMainUIHandler(mUiHandler);
			mTitleView.setText(getString(R.string.menu_pend_approval));
			changeFragment(mPendApprovalFragment);
		} else if (view == itemSendItem) {
			if (mPendApprovalFragment == null) {
				mPendApprovalFragment = new PendApprovalFragment();
			}
			mPendApprovalFragment.setKind(Constants.TYPE_SEND_ITEM_TASKS,
					mAccountChanged);
			mPendApprovalFragment.setMainUIHandler(mUiHandler);
			mTitleView.setText(getString(R.string.menu_sent_item));
			changeFragment(mPendApprovalFragment);
		} else if (view == itemScratchUpcome) {
			if (mPendApprovalFragment == null) {
				mPendApprovalFragment = new PendApprovalFragment();
			}
			mPendApprovalFragment.setKind(Constants.TYPE_SCRATCH_UPCOME_TASKS,
					mAccountChanged);
			mPendApprovalFragment.setMainUIHandler(mUiHandler);
			mTitleView.setText(getString(R.string.menu_scratch_upcome));
			changeFragment(mPendApprovalFragment);
		} else if (view == itemApproved) {
			if (mPendApprovalFragment == null) {
				mPendApprovalFragment = new PendApprovalFragment();
			}
			mPendApprovalFragment.setKind(Constants.TYPE_APPROVED_TASKS,
					mAccountChanged);
			mPendApprovalFragment.setMainUIHandler(mUiHandler);
			mTitleView.setText(getString(R.string.menu_approved));
			changeFragment(mPendApprovalFragment);
		} else if (view == itemMessage) {
			if (mMessageFragment == null) {
				mMessageFragment = new MessageFragment();
			}
			mMessageFragment.setMainUIHandler(mUiHandler);
			mTitleView.setText(getString(R.string.menu_message));
			changeFragment(mMessageFragment);
		} else if (view == itemAds) {
			if (mAnnounceFragment == null) {
				mAnnounceFragment = new AnnounceFragment();
			}
			mAnnounceFragment.setMainUIHandler(mUiHandler);
			mTitleView.setText(getString(R.string.menu_ads));
			changeFragment(mAnnounceFragment);
		} else if (view == itemInvoice) {
			if (mTipFragment == null) {
				mTipFragment = new TipFragment();
			}
			mTipFragment.setMainUIHandler(mUiHandler);
			mTitleView.setText(getString(R.string.menu_invoice));
			changeFragment(mTipFragment);
		} else if (view == itemRecord) {
			if (mTipFragment == null) {
				mTipFragment = new TipFragment();
			}
			mTipFragment.setMainUIHandler(mUiHandler);
			mTitleView.setText(getString(R.string.menu_record));
			changeFragment(mTipFragment);
		} else if (view == mUserHeaderIv) {

		} else if (view == mSwitchAccountLl) {
			intentToZhangTao();
		} else if (view == mLoginOutLl) {
			sureToLoginOut();
		}else if(view == itemUpdate){
			if(isFastDoubleClick()){
				Toast.makeText(MenuActivity.this, "正在检查更新...", Toast.LENGTH_SHORT).show();
			}else{
				checkUpdate(Constants.CHECK_UPDATE_NOAUTO);
			}
		}

		if (view != mSwitchAccountLl && view != mLoginOutLl&& view != itemUpdate) {
			mUiHandler.sendEmptyMessageDelayed(MSG_FROM_FRAGMENT_CLOSE_MENU, 0);
		}

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

	private static final int MSG_GET_EMPLOYEE_LIST_ERROR = 4;
	private static final int MSG_GET_EMPLOYEE_LIST_SUCCESS = 5;
	public static final int MSG_SAVE_EMPLOYEE_LIST_SUCCESS = 6;
	public static final int MSG_FROM_FRAGMENT_CLOSE_MENU = 7;

	private void requestEmployees(String filter) {

		showDialog();
		String commName = App.getSharedPreference().getString(
				Constants.ZHANG_TAO_CONN_NAME, "");
		StringBuffer urlSbf = new StringBuffer(App.getRootUrl()
				+ Constants.GET_EMPLOYEE_LIST + "?");
		urlSbf.append("account=").append(commName);
		urlSbf.append("&filter=").append(filter);

		JsonObjectRequest json = new JsonObjectRequest(Method.GET,
				urlSbf.toString(), null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Logger.d(TAG, "----response----" + response.toString());
						try {
							int resCode = response.getInt("success");
							Message msg = Message.obtain();
							if (resCode == 0) {
								msg.what = MSG_GET_EMPLOYEE_LIST_SUCCESS;
								msg.obj = response.getString("data");
							} else {
								msg.what = MSG_GET_EMPLOYEE_LIST_ERROR;
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
						mUiHandler
								.sendEmptyMessage(MSG_GET_EMPLOYEE_LIST_ERROR);
					}
				});
		json.setShouldCache(true);
		mQueue.add(json);
		mQueue.start();

	}

	private LoadingDialog mLoadingDialog;

	private void dismissDialog() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}

	private void showDialog() {
		if (mLoadingDialog == null) {
			mLoadingDialog = new LoadingDialog(this, "正在同步人员档案信息");
		}
		mLoadingDialog.show();
	}

	private void checkUpdate(final int type) {
		

		mUiHandler.postDelayed(new Runnable() {

			@Override
			public void run() {

				SelfUpgrade.getInstance(MenuActivity.this).startUpgrade(type);

			}
		}, 500);
	}
	
	 private static long lastClickTime; 
	 public static boolean isFastDoubleClick() { 
	        long time = System.currentTimeMillis(); 
	        long timeD = time - lastClickTime; 
	        if ( 0 < timeD && timeD < 2000) {    
	            return true;    
	        }    
	        lastClickTime = time;    
	        return false;    
	    }

	@Override
	public void checkSuccessed(String info) {
		final int verCode = Integer.parseInt(info);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(verCode>App.VersionCode()){
					itemUpdate.getIv_badge().setVisibility(View.VISIBLE);
				}else{
					itemUpdate.getIv_badge().setVisibility(View.GONE);
				}
				
			}
		});
		
	}

	@Override
	public void checkUnUpdate(int errorCode) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				itemUpdate.getIv_badge().setVisibility(View.GONE);
			}
		});
		
	}

	@Override
	public void checkError(int errorCode, String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				itemUpdate.getIv_badge().setVisibility(View.GONE);
			}
		});
		
	} 

}
