package com.xiexin.ces.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pada.juidownloader.util.LogUtils;
import pada.juidownloadmanager.ApkDownloadManager;
import pada.juidownloadmanager.DownloadTask;
import pada.juidownloadmanager.DownloadTask.TaskState;
import pada.juidownloadmanager.DownloadTaskStateListener;
import pada.juidownloadmanager.IDownloadTaskStateListener;
import pada.juidownloadmanager.JuiDownloadService;
import pada.juidownloadmanager.entry.DownloadInfo;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.entry.AttachMent;
import com.xiexin.ces.utils.APNUtil;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.utils.Md5Util;
import com.xiexin.ces.utils.OpenFiles;
import com.xiexin.ces.widgets.DotProgressBar;
import com.xiexin.ces.widgets.HorizonScrollLayout;
import com.xiexin.ces.widgets.HorizonScrollLayout.OnTouchScrollListener;
import com.xiexin.ces.widgets.IUpdateSingleView;
import com.xiexin.ces.widgets.LoadingUIListView;

public class AttachmentActivity extends Activity implements OnClickListener {

	private static final String TAG = "AttachmentActivity";
	private DotProgressBar mDotProgressBar;
	private HorizonScrollLayout mHorizonScrollLayout;
	private Context mContext;
	private boolean isCircule = true;

	private int infoSize = 0;

	private static final int MSG_CIRCULE = 0;
	private static final int TIME_CIRCULE_START = 4 * 1000;

	private static final int MSG_LOADING_IMAGE = 1;

	public static DisplayImageOptions optionsIcon = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_air)
			.showImageForEmptyUri(R.drawable.icon_air)
			.showImageOnFail(R.drawable.icon_air).cacheInMemory(true)
			.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();

	// header start
	private LinearLayout mReturnLl;
	private FrameLayout mReturnIv;
	private TextView mReturnTv;
	private TextView mTitle;
	private Button mBtn1;
	private Button mBtn2;

	// private int mRequestFrom;

	// header end

	private LoadingUIListView mListView;
	private AttachmentAdapter mAttachmentAdapter;

	private ApkDownloadManager mApkDownloadManager;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invoice_attachment);
		mContext = this;
		initView();
		initData();

		LogUtils.open();

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mNetChangeReceiver, mFilter);
	}

	private void initView() {
		mHorizonScrollLayout = (HorizonScrollLayout) findViewById(R.id.image_attachment_sl);
		mHorizonScrollLayout.setEnableOverScroll(false);
		mHorizonScrollLayout.setLockAllWhenTouch(true);
		mHorizonScrollLayout.setScrollSlop(1.75f);
		mHorizonScrollLayout.setCircle(true);

		mDotProgressBar = (DotProgressBar) findViewById(R.id.image_attachment_dot);
		// mDotProgressBar.setTotalNum( infos.size( ) );
		mDotProgressBar.setDotbarIconResource(R.drawable.guide_dot_white,
				R.drawable.guide_dot_black);
		mDotProgressBar.setVisibility(View.VISIBLE);

		mHorizonScrollLayout
				.setOnTouchScrollListener(new OnTouchScrollListener() {

					@Override
					public void onScrollStateChanged(int scrollState,
							int currentScreem) {
						// 当手滚动广告位是, 不播放
						if (OnTouchScrollListener.SCROLL_STATE_TOUCH_SCROLL == scrollState) {
							stopCricule();
						} else if (OnTouchScrollListener.SCROLL_STATE_IDLE == scrollState) {
							startCricle();
						}
					}

					@Override
					public void onScroll(View view, float leftX,
							float screemWidth) {
					}

					@Override
					public void onScreenChange(int displayScreem) {
						mDotProgressBar.setCurProgress(displayScreem);
					}
				});

		// header start
		mReturnLl = (LinearLayout) findViewById(R.id.return_ll);
		mReturnIv = (FrameLayout) findViewById(R.id.return_iv);
		mReturnTv = (TextView) findViewById(R.id.return_tv);
		mTitle = (TextView) findViewById(R.id.title);
		mBtn1 = (Button) findViewById(R.id.btn1);
		mBtn2 = (Button) findViewById(R.id.btn2);
		// /header end
		mReturnLl.setVisibility(View.VISIBLE);
		mTitle.setText(getString(R.string.attachment));

		mReturnLl.setOnClickListener(this);

		mListView = (LoadingUIListView) findViewById(R.id.attachment_list);
		mListView.setFooterPullEnable(false);
		mListView.setHeaderPullEnable(false);
		// mListView.setListViewListener(mListViewListener);
		// mListView.setPadaLoadingViewListener(mLoadingViewListener);
		if (mAttachmentAdapter == null)
			mAttachmentAdapter = new AttachmentAdapter();

		mListView.setAdapter(mAttachmentAdapter);

		// mHorizonScrollLayout.setOnClickListener(this);

	}

	private String mFilePathStr;

	private ArrayList<String> mImageList = new ArrayList<String>();
	private ArrayList<AttachMent> mDocList = new ArrayList<AttachMent>();

	private void initData() {
		mImageList.clear();
		mDocList.clear();

		Intent intent = getIntent();
		mFilePathStr = intent.getStringExtra(Constants.FILES_PATH);
		Logger.d(TAG, "mFilePathStr=" + mFilePathStr);
		if (mFilePathStr != null && !mFilePathStr.isEmpty()) {
			try {
				JSONArray jsonArray = new JSONArray(mFilePathStr);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					String attachMentName = object.getString("attchname");
					if (attachMentName.contains(".jpg")
							|| attachMentName.contains(".png")
							|| attachMentName.contains(".jpeg")||attachMentName.contains(".JPEG")||attachMentName.contains(".JPG")||attachMentName.contains(".PNG")) {
						mImageList.add(object.getString("filepath"));
					} else {
						AttachMent attachMent = new AttachMent();
						attachMent.setAttchname(attachMentName);
						attachMent.setFilepath(object.getString("filepath"));
						mDocList.add(attachMent);
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		//图片数量为零时处理
		if(mImageList.size()>0){
			scrollViewAddData(mImageList);
			mHorizonScrollLayout.setVisibility(View.VISIBLE);
			mDotProgressBar.setVisibility(View.VISIBLE);
		}else{
			mHorizonScrollLayout.setVisibility(View.GONE);
			mDotProgressBar.setVisibility(View.GONE);
		}
		
		setListAdapter();

		mApkDownloadManager = JuiDownloadService.getDownloadManager(App
				.getAppContext());
		mApkDownloadManager.setAutoInstall(false);// 不自动安装
		mApkDownloadManager.setIsNotApk(true);

	}

	private void setListAdapter() {
		mAttachmentAdapter.addData(mDocList);
		mAttachmentAdapter.notifyDataSetChanged();
	}

	private DownloadTaskStateListener mDownloadTaskStateListener = new DownloadTaskStateListener() {

		@Override
		public void onUpdateTaskState(DownloadTask arg0) {

			if (mAttachmentAdapter != null)
				mAttachmentAdapter.onUpdateTaskState(arg0);
		}

		@Override
		public void onUpdateTaskProgress(DownloadTask arg0) {

			if (mAttachmentAdapter != null)
				mAttachmentAdapter.onUpdateTaskProgress(arg0);
		}

		@Override
		public void onUpdateTaskList(Object arg0) {
			if (mAttachmentAdapter != null)
				mAttachmentAdapter.onUpdateTaskList(arg0);
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mApkDownloadManager.registerListener(mDownloadTaskStateListener);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mApkDownloadManager.unregisterListener(mDownloadTaskStateListener);
		unregisterReceiver(mNetChangeReceiver);
	}

	private void circule() {
		if (infoSize > 0) {
			mHorizonScrollLayout.displayNextScreen();
			mDotProgressBar.setCurProgress(getCurScreen());
		}
	}

	public void scrollViewAddData(ArrayList<String> images) {

		Logger.d(TAG, "===images.size=" + images.size());
		this.infoSize = images.size();
		mHorizonScrollLayout.removeAllViews();

		mDotProgressBar.setTotalNum(images.size());
		mDotProgressBar.setDotbarNum(images.size());

		if (APNUtil.isWifiDataEnable(mContext)) {
			for (String image : images) {
				LayoutInflater inflater = App.getLayoutInflater();
				ViewGroup view = (ViewGroup) inflater.inflate(
						R.layout.attachment_child, null);
				mHorizonScrollLayout.addView(view);
				ImageView imageView = (ImageView) view
						.findViewById(R.id.attachment_child_iv);

				imageView.setTag(changeToUrl(image));

				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String url = (String) view.getTag();
						intentToImageGallery(url);
					}
				});

				Logger.d(TAG, "url=" + changeToUrl(image));
				ImageLoader.getInstance().displayImage(changeToUrl(image),
						imageView, optionsIcon);
			}
		} else {
			Toast.makeText(AttachmentActivity.this,
					getString(R.string.please_download_at_wifi),
					Toast.LENGTH_SHORT).show();
		}

	}

	public void setCurScreen(int position) {
		mHorizonScrollLayout.setDefaultScreem(position);
		mDotProgressBar.setCurProgress(position);
	}

	/**
	 * 得到当前是第几个屏幕
	 * 
	 * @return
	 */
	public int getCurScreen() {
		return mHorizonScrollLayout.getCurScreen();
	}

	private void startCricle() {
		isCircule = true;
		if (mScrollHandler != null) {
			mScrollHandler.removeMessages(MSG_CIRCULE);
			mScrollHandler.sendEmptyMessageDelayed(MSG_CIRCULE,
					TIME_CIRCULE_START);
		}
	}

	private Handler mScrollHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 暂停消息发送的时候，不需处理
			switch (msg.what) {
			case MSG_CIRCULE:
				if (isCircule && infoSize > 0) {
					if (mScrollHandler != null) {
						// TODO 循环下个列表
						circule();
						mScrollHandler.sendEmptyMessageDelayed(MSG_CIRCULE,
								TIME_CIRCULE_START);
					}
				}
				break;

			default:
				break;
			}
		};
	};

	private Handler mUiHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 暂停消息发送的时候，不需处理
			switch (msg.what) {
			case MSG_LOADING_IMAGE:
				if (mImageList.size() > 0) {
					Logger.d(TAG, "loading image...");
					Toast.makeText(AttachmentActivity.this,
							getString(R.string.download_at_wifi),
							Toast.LENGTH_SHORT).show();
					scrollViewAddData(mImageList);
				}
				break;
			default:
				break;
			}
		};
	};

	public void stopCricule() {
		isCircule = false;
		if (mScrollHandler != null) {
			mScrollHandler.removeMessages(MSG_CIRCULE);
		}
	}

	private class AttachmentAdapter extends BaseAdapter implements
			IUpdateSingleView<String>, IDownloadTaskStateListener {

		private ArrayList<AttachMent> list = new ArrayList<AttachMent>();

		protected HashMap<String, View> mViewMap = new HashMap<String, View>();

		public void addData(ArrayList<AttachMent> data) {
			list.clear();
			list.addAll(data);
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
						R.layout.activity_attachment_list_item, null);
				holder = new ViewHolder();
				holder.downloadRl = (RelativeLayout) convertView
						.findViewById(R.id.attachment_frame);
				holder.fileNameTv = (TextView) convertView
						.findViewById(R.id.attachment_name_tv);
				holder.handleBtn = (Button) convertView
						.findViewById(R.id.handle_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			bindData(holder, list.get(position));

			String url = list.get(position).getFilepath();
			String signCode = Md5Util.stringToMD5(changeToUrl(url));

			setViewForSingleUpdate(signCode, convertView);
			return convertView;
		}

		private void bindData(final ViewHolder holder, final AttachMent am) {

			String url = am.getFilepath();
			String signCode = Md5Util.stringToMD5(changeToUrl(url));
			Logger.d(TAG, "bindData,signCode=" + signCode);
			final DownloadTask task = mApkDownloadManager
					.getDownloadTaskBySignCode(signCode);
			Logger.d(TAG, "bindData,task=" + task);
			// if (task != null && task.getState() == TaskState.SUCCEEDED) {
			// holder.handleBtn.setText(getString(R.string.open_file));
			// holder.handleBtn.setTag(task);
			// } else {
			// holder.handleBtn.setTag(am);
			// holder.handleBtn.setText(getString(R.string.download));
			// }

			if (task != null) {
				holder.handleBtn.setTag(task);
				setState(holder.handleBtn, task);
			} else {
				holder.handleBtn.setTag(am);
				holder.handleBtn.setText(getString(R.string.download));
			}

			holder.fileNameTv.setText(am.getAttchname());
			holder.handleBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Object obj = v.getTag();
					if (obj instanceof DownloadTask) {
						DownloadTask dinfo = (DownloadTask) obj;
							switch (dinfo.getState()) {
							case PREPARING:
							case WAITING:
							case STARTED:
							case LOADING:
								mApkDownloadManager.stopDownload(dinfo);
								break;
							case SUCCEEDED:
								openFile(dinfo.getFileSavePath());
								break;
							case STOPPED:
							case FAILED_NETWORK:
							case FAILED_SERVER:
							case FAILED_NOFREESPACE:
								if (APNUtil.isWifiDataEnable(mContext)) {
									mApkDownloadManager.resumeDownload(dinfo);
								} else {

									Toast.makeText(
											AttachmentActivity.this,
											getString(R.string.please_download_at_wifi),
											Toast.LENGTH_SHORT).show();
								}

								break;
							case FAILED_BROKEN:
							case DELETED:
								if (APNUtil.isWifiDataEnable(mContext)) {
									mApkDownloadManager.restartDownload(dinfo);
								} else {

									Toast.makeText(
											AttachmentActivity.this,
											getString(R.string.please_download_at_wifi),
											Toast.LENGTH_SHORT).show();
								}

								break;
							case FAILED_NOEXIST:
								mApkDownloadManager.removeDownload(dinfo);
								break;
							}

					} else if (obj instanceof AttachMent) {
						AttachMent attachMent = (AttachMent) v.getTag();
						startDownload(attachMent);
					}
				}
			});
			// Log.d(TAG,
			// "connName="+zt.getConnName()+"checked="+mMap.get(zt.getConnName()));
		}

		private void setState(Button button, DownloadTask task) {

			switch (task.getState()) {
			case PREPARING:
				button.setText(getString(R.string.app_pause));
				break;
			case WAITING:
				button.setText(getString(R.string.app_pause));
				break;
			case STARTED:
			case LOADING:
				button.setText(getString(R.string.app_pause));
				break;
			case STOPPED:
				button.setText(getString(R.string.app_resume));
				break;
			case SUCCEEDED:
				button.setText(getString(R.string.open_file));
				break;
			case DELETED:
				button.setText(getString(R.string.app_redownload));
				break;
			case FAILED_NETWORK:
				button.setText(getString(R.string.app_retry));
				break;
			case FAILED_BROKEN:
				button.setText(getString(R.string.app_retry));
				break;
			case FAILED_NOEXIST:
				button.setText(getString(R.string.app_delete));
				break;
			case FAILED_SERVER:
				button.setText(getString(R.string.app_retry));
				break;
			case FAILED_NOFREESPACE:
				button.setText(getString(R.string.app_retry));
				break;
			default:
				break;
			}
		}

		private void startDownload(AttachMent attachMent) {

			String url = attachMent.getFilepath();
			String signCode = Md5Util.stringToMD5(changeToUrl(url));

			Logger.d(TAG, "startDownload,signCode=" + signCode);

			DownloadInfo downloadInfo = new DownloadInfo();
			downloadInfo.appDownloadURL = changeToUrl(url);
			Log.d(TAG, "downloadInfo.appDownloadURL="
					+ downloadInfo.appDownloadURL);
			downloadInfo.appIconURL = "";

			downloadInfo.appName = attachMent.getAttchname();

			downloadInfo.nFromPos = 0;
			downloadInfo.packageName = "com.xiexin.ces";
			downloadInfo.packId = 0;
			downloadInfo.signCode = signCode;

			Logger.d(TAG, "startDownload,downloadInfo.signCode=" + signCode);

			mApkDownloadManager.startDownload(downloadInfo);
		}

		@Override
		public View getViewByKey(String key) {
			View view = mViewMap.get(key);
			return view;
		}

		@Override
		public void setViewForSingleUpdate(String key, View view) {
			if (view == null) {
				return;
			}
			mViewMap.put(key, view);
		}

		@Override
		public void removeViewForSingleUpdate(View view) {

		}

		@Override
		public void removeViewForSingleUpdate(String key) {
			mViewMap.remove(key);
		}

		@Override
		public void onUpdateTaskList(Object task) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onUpdateTaskProgress(DownloadTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUpdateTaskState(DownloadTask task) {

			Log.d(TAG, "onUpdateTaskState,signCode=" + task.signCode);
			View view = getViewByKey(task.signCode);
			Log.d(TAG, "view=" + view);
			if (view == null) {
				return;
			}
			ViewHolder holder = (ViewHolder) view.getTag();
			Log.d(TAG, "onUpdateTaskState,arg0.getState()=" + task.getState());
			if (task.getState() == TaskState.SUCCEEDED) {
				holder.handleBtn.setText(getString(R.string.open_file));
			} else {
				holder.handleBtn.setText(getString(R.string.download));
			}
			
			notifyDataSetChanged();
		}
	}

	class ViewHolder {
		RelativeLayout downloadRl;
		TextView fileNameTv;
		Button handleBtn;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.return_ll:
			onBackPressed();
			break;
		case R.id.image_attachment_sl:

			break;
		default:
			break;
		}
	}

	private void intentToImageGallery(String url) {

		Intent intent = new Intent();
		intent.setClass(AttachmentActivity.this, ImageGalleryActivity.class);
		Logger.d(TAG, "ImageGalleryActivity.URL=" + ImageGalleryActivity.URL);
		intent.putExtra(ImageGalleryActivity.URL, url);
		intent.putExtra(ImageGalleryActivity.URL_DATA, mImageList);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	private void openFile(String filePath) {

		File currentPath = new File(filePath);

		if (currentPath != null && currentPath.isFile()) {
//			String fileName = currentPath.toString();
//			Intent intent;
//			if (checkEndsWithInStringArray(fileName, getResources()
//					.getStringArray(R.array.fileEndingImage))) {
//				intent = OpenFiles.getImageFileIntent(currentPath);
//				startActivity(intent);
//			} else if (checkEndsWithInStringArray(fileName, getResources()
//					.getStringArray(R.array.fileEndingWebText))) {
//				intent = OpenFiles.getHtmlFileIntent(currentPath);
//				startActivity(intent);
//			} else if (checkEndsWithInStringArray(fileName, getResources()
//					.getStringArray(R.array.fileEndingPackage))) {
//				intent = OpenFiles.getApkFileIntent(currentPath);
//				startActivity(intent);
//			} else if (checkEndsWithInStringArray(fileName, getResources()
//					.getStringArray(R.array.fileEndingAudio))) {
//				intent = OpenFiles.getAudioFileIntent(currentPath);
//				startActivity(intent);
//			} else if (checkEndsWithInStringArray(fileName, getResources()
//					.getStringArray(R.array.fileEndingVideo))) {
//				intent = OpenFiles.getVideoFileIntent(currentPath);
//				startActivity(intent);
//			} else if (checkEndsWithInStringArray(fileName, getResources()
//					.getStringArray(R.array.fileEndingText))) {
//				intent = OpenFiles.getTextFileIntent(currentPath);
//				startActivity(intent);
//			} else if (checkEndsWithInStringArray(fileName, getResources()
//					.getStringArray(R.array.fileEndingPdf))) {
//				intent = OpenFiles.getPdfFileIntent(currentPath);
//				startActivity(intent);
//			} else if (checkEndsWithInStringArray(fileName, getResources()
//					.getStringArray(R.array.fileEndingWord))) {
//				intent = OpenFiles.getWordFileIntent(currentPath);
//				startActivity(intent);
//			} else if (checkEndsWithInStringArray(fileName, getResources()
//					.getStringArray(R.array.fileEndingExcel))) {
//				intent = OpenFiles.getExcelFileIntent(currentPath);
//				startActivity(intent);
//			} else if (checkEndsWithInStringArray(fileName, getResources()
//					.getStringArray(R.array.fileEndingPPT))) {
//				intent = OpenFiles.getPPTFileIntent(currentPath);
//				startActivity(intent);
//			} else {
//				Toast.makeText(AttachmentActivity.this, "无法打开，请安装相应的第三方软件！",
//						Toast.LENGTH_SHORT).show();
//			}
			Intent intent = OpenFiles.openFile(currentPath);
			if(intent!=null){
				startActivity(intent);
			}else{
				Toast.makeText(AttachmentActivity.this, "无法打开，请安装相应的软件！",Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(AttachmentActivity.this, "对不起，这不是文件！",
					Toast.LENGTH_SHORT).show();
		}
	}

	private String changeToUrl(String filePath) {
		String downLoadUrl = filePath;
		String url1 = downLoadUrl
				.substring(0, downLoadUrl.lastIndexOf("/") + 1);
		String url2 = downLoadUrl.substring(downLoadUrl.lastIndexOf("/") + 1,
				downLoadUrl.length());
		try {
			return url1 + URLEncoder.encode(url2, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return filePath;
	}

	// if(mImageList.size()>0&&APNUtil.isWifiDataEnable(mContext)){
	// scrollViewAddData(mImageList);
	// }

	private BroadcastReceiver mNetChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Logger.d(TAG, "net changed!");
				if (APNUtil.isWifiDataEnable(mContext)) {
					Logger.d(TAG, "net is wifi!");
					mUiHandler.sendEmptyMessage(MSG_LOADING_IMAGE);
				}
			}
		}
	};

}
