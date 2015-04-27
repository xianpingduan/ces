package com.xiexin.ces.update;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pada.juidownloader.HttpUtils;
import pada.juidownloader.exception.HttpException;
import pada.juidownloader.http.HttpHandler;
import pada.juidownloader.http.ResponseInfo;
import pada.juidownloader.http.callback.RequestCallBack;
import pada.juidownloader.util.LogUtils;
import pada.juidownloadmanager.utils.PackageUtils;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
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
import com.xiexin.ces.utils.Logger;

/**
 * 自更新下载 有无通知栏通知由配置项决定
 */
public class SelfUpgrade {

	public final static String TAG = "SelfUpgrade";

	private static SelfUpgrade mInstance = null;
	private SelfUpdateListener mSelfUpdateListener;
	private final SelfUpateNotificationCenter mNotificationCenter;
	private final Context mContext;
	private PackageInfo info = null;
	private boolean isDownloading = false;

	private final static int MSG_HAVE_UPGRADE_TO_INSTALL = 0;
	private final static int MSG_NO_HAVE_UPGRADE_TO_INSTALL = 1; // 表示无更新
	private final static int MSG_UPDATE_ERROR = 2;

	public static final int DEFAULT_REQ_GAP_TIME = 60 * 60 * 1000;

	// 网络请求

	private final RequestQueue mQueue;

	private JSONObject mUpdateInfo = null;
	private String packUrl;

	// 下载包保存的路径
	public final static String UPDATE_APK_SAVE_PATH = "/ces/apk/";

	private HttpUtils http = null;

	public void setmUpdateListener(SelfUpdateListener mSelfUpdateListener) {
		this.mSelfUpdateListener = mSelfUpdateListener;
	}

	public void setmSmallUpdateNotifyIcon(int icon) {
		mNotificationCenter.setUpdateNotifyIcon(icon);
	}

	private SelfUpgrade(Context context) {
		mContext = context;
		mNotificationCenter = SelfUpateNotificationCenter.getInstance(context);

		mQueue = Volley.newRequestQueue(App.getAppContext());
	}

	public static SelfUpgrade getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new SelfUpgrade(context);
		}

		return mInstance;
	}

	public interface SelfUpdateListener {
		public void checkSuccessed(String info);

		public void checkUnUpdate(int errorCode);

		public void checkError(int errorCode, String msg);

	}

	public interface UpdateListener {
		public void onSuccess(ResponseInfo<File> responseInfo);

		public void onStart();

		public void onStopped();

		public void onLoading(long total, long current, boolean isUploading);

		public void onFailure(HttpException error, String msg);

	}

	private int checkType;
	public void startUpgrade(int checkType) {
		this.checkType = checkType;
		checkUpdate();
	}

	// apk save file name
	private String getSaveFileName(final Context context) {
		try {
			info = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String packName = context.getPackageName() + "." + info.versionName;
		return packName.replace(".", "_") + ".apk";
	}

	/**
	 * 获取当前有效的存储路径, 以"/"结尾
	 * 
	 * @param path
	 * @return
	 */
	private static String getStorePath(final Context context, String path) {
		// 获取SdCard状态
		String state = android.os.Environment.getExternalStorageState();
		// 判断SdCard是否存在并且是可用的
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
				File file = new File(android.os.Environment
						.getExternalStorageDirectory().getPath() + path);
				if (!file.exists()) {
					file.mkdirs();
				}
				String absolutePath = file.getAbsolutePath();
				if (!absolutePath.endsWith("/")) {// 保证以"/"结尾
					absolutePath += "/";
				}
				return absolutePath;
			}
		}
		String absolutePath = context.getFilesDir().getAbsolutePath();
		if (!absolutePath.endsWith("/")) {// 保证以"/"结尾
			absolutePath += "/";
		}
		return absolutePath;
	}

	private void startDonwloadApk() {
		if (mUpdateInfo == null) {
			LogUtils.e("update is null,please check!");
			return;
		}
		// 下载apk存放的路径
		String filePath = getStorePath(mContext, UPDATE_APK_SAVE_PATH);
		File apkFile = new File(filePath + getSaveFileName(mContext));
		if (apkFile.exists()) {
			LogUtils.d("apkFile.exists=" + apkFile.exists());
			apkFile.delete();
		}
		try {
			apkFile.createNewFile();
		} catch (Exception e) {
			LogUtils.e("create file error!");
		}
		PowerManager pm = (PowerManager) mContext
				.getSystemService(Context.POWER_SERVICE);
		if (http == null)
			http = new HttpUtils(pm);
		http.configRequestThreadPoolSize(1);

		try {
			packUrl = mUpdateInfo.getString("packurl");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		addDownloadNotification();

		HttpHandler<File> mHandler = http.download(packUrl, filePath
				+ getSaveFileName(mContext), true, false,
				new RequestCallBack<File>() {
					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						isDownloading = false;
						LogUtils.d("文件" + responseInfo.result.getPath()
								+ "下载完成，开始安装!");
						finishDownloadNotification(responseInfo.result);
						// 安装
						PackageUtils.installNormal(mContext,
								responseInfo.result.getPath());
						// 统计
					}

					@Override
					public void onStart() {
						// if( mUpdateListener != null )
						// mUpdateListener.onStart( );
					}

					@Override
					public void onStopped() {
						isDownloading = false;
						// if( mUpdateListener != null )
						// mUpdateListener.onStopped( );
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						isDownloading = true;
						updateDownloadNotification((int) current, (int) total);
						// if( mUpdateListener != null )
						// mUpdateListener.onLoading( total , current ,
						// isUploading );
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						isDownloading = false;
						updateDownloadNotificationError();
						// if( mUpdateListener != null )
						// mUpdateListener.onFailure( error , msg );
					}

				});
		
		

	}

	private void doRequestUpdate() {

		// JSONObject obj = new JSONObject( );
		// try
		// {
		// obj.put( "account" , account );
		// obj.put( "userid" , userid );
		// obj.put( "pwd" , pwd );
		// }
		// catch ( JSONException e )
		// {
		// // TODO
		// e.printStackTrace( );
		// }
		// http://core130.com:8081/api/CESApp/GetWorkMessage?account=web_Group&userid=000018&kind=1&filter=%20&size=10&page=1

		Logger.d(TAG, "App.getRootUrl( )=" + App.getRootUrl());
		StringBuffer urlSbf = new StringBuffer(App.getRootUrl()
				+ Constants.UPDATE + "?");
		urlSbf.append("packname=").append(App.PackageName());
		urlSbf.append("&vercode=").append(App.VersionCode());
		urlSbf.append("&vername=").append(App.VersionName());
		urlSbf.append("&from=").append("apk");

		JsonObjectRequest json = new JsonObjectRequest(Method.GET,
				urlSbf.toString(), null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Logger.d(TAG, "----response----" + response.toString());
						try {
							int resCode = response.getInt("success");
							Message msg = Message.obtain();
							if (resCode == 0) {
								msg.what = MSG_HAVE_UPGRADE_TO_INSTALL;
								msg.obj = response.getString("data");
							} else {
								msg.what = MSG_NO_HAVE_UPGRADE_TO_INSTALL;
								msg.obj = response.get("msg");
							}

							mHandler.sendMessage(msg);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						mHandler.sendEmptyMessage(MSG_NO_HAVE_UPGRADE_TO_INSTALL);
					}
				});
		mQueue.add(json);
		mQueue.start();
	}

	// 通知栏相关
	private void addDownloadNotification() {
		mNotificationCenter.addSelfDownloadStartNotification();
	}

	private void finishDownloadNotification(File file) {
		mNotificationCenter.addSelfDownloadFinishNotification(file);
	}

	private void updateDownloadNotification(int dlsize, int totalsize) {
		mNotificationCenter.addSelfDownloadProgressNotification(dlsize,
				totalsize);
	}

	private void updateDownloadNotificationError() {
		mNotificationCenter.addSelfDownloadErrorNotification();
	}

	private void checkUpdate() {
		doRequestUpdate();
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_HAVE_UPGRADE_TO_INSTALL:
				String info = (String) msg.obj;
				try {
					JSONArray array = new JSONArray(info);
					mUpdateInfo = array.getJSONObject(0);
					Logger.d(TAG, "newvercode="+mUpdateInfo.getInt("newvercode"));
					Logger.d(TAG, "app versioncode="+App.VersionCode());
					if(mUpdateInfo.getInt("newvercode")>App.VersionCode()){
						showUpgradeSpecApkDialog();
					}else{
						Logger.d(TAG, "已经是最新版本"+App.VersionCode());
						if(checkType==Constants.CHECK_UPDATE_NOAUTO&&mContext!=null){
							//Toast.makeText(mContext, "当前软件版本是"+App.VersionName()+",已经是最新版本!", Toast.LENGTH_SHORT).show();
							if(mUpdateInfoDialog==null){
								mUpdateInfoDialog = new UpdateInfoDialog(mContext, "", "当前软件版本是"+App.VersionName()+",已经是最新版本");
							}
							mUpdateInfoDialog.getWindow().setType(
									WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); // 全局dialog
							mUpdateInfoDialog.show();
							
						}
					}
					if (mSelfUpdateListener != null) {
						mSelfUpdateListener.checkSuccessed(mUpdateInfo.getInt("newvercode")+"");
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				//long next_req_time = System.currentTimeMillis()+ DEFAULT_REQ_GAP_TIME;
				break;
			case MSG_NO_HAVE_UPGRADE_TO_INSTALL:
				LogUtils.e("当前是最新版本!");
				if (mSelfUpdateListener != null) {
					mSelfUpdateListener
							.checkUnUpdate(MSG_NO_HAVE_UPGRADE_TO_INSTALL);
				}
				break;
			case MSG_UPDATE_ERROR:
				if (mSelfUpdateListener != null) {
					mSelfUpdateListener.checkError(msg.arg1, (String) msg.obj);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private SelfUpdateCommonDialog mUpgradeDialog;
	private String mUpdatePrompt;
	private String mUpdateDesc;
	
	private UpdateInfoDialog mUpdateInfoDialog;

	// 上报最近任务弹出框
	private void showUpgradeSpecApkDialog() {
		try {
			mUpdatePrompt = mUpdateInfo.getString("updateprompt");
			mUpdateDesc = mUpdateInfo.getString("updatedesc");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (mUpgradeDialog == null)
			mUpgradeDialog = new SelfUpdateCommonDialog(mContext,
					mUpdatePrompt == null ? "" : mUpdatePrompt,
					mUpdateDesc == null ? "" : mUpdateDesc);
		mUpgradeDialog.setmRightBtnTitle(mContext.getString(ResourceUtil
				.getStringId(mContext, "self_update_dialog_right_title")));
		mUpgradeDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); // 全局dialog
		mUpgradeDialog.addRightBtnListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isDownloading) {
					startDonwloadApk();
				}
				mUpgradeDialog.dismiss();
			}
		});
		mUpgradeDialog.addLeftBtnListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mUpgradeDialog != null)
					mUpgradeDialog.dismiss();
			}
		});
		mUpgradeDialog.show();
	}

}
