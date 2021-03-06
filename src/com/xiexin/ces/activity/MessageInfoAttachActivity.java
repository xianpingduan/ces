package com.xiexin.ces.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiexin.ces.App;
import com.xiexin.ces.R;
import com.xiexin.ces.download.DownloadInfo;
import com.xiexin.ces.download.DownloadManager;
import com.xiexin.ces.download.DownloadState;
import com.xiexin.ces.download.DownloadViewHolder;
import com.xiexin.ces.entry.AttachMent;
import com.xiexin.ces.utils.APNUtil;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.utils.OpenFiles;
import com.xiexin.ces.widgets.DotProgressBar;
import com.xiexin.ces.widgets.HorizonScrollLayout;
import com.xiexin.ces.widgets.HorizonScrollLayout.OnTouchScrollListener;
import com.xiexin.ces.widgets.LoadingUIListView;

public class MessageInfoAttachActivity extends Activity implements OnClickListener {

	private static final String TAG = "MessageInfoAttachActivity";
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
	private AttachmentAdapter mAnnounceAttachmentAdapter;

	// new download
	private DownloadManager downloadManager;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messageinfo_attachment);
		mContext = this;
		initView();
		initData();

		downloadManager = DownloadManager.getInstance();

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
		if (mAnnounceAttachmentAdapter == null)
			mAnnounceAttachmentAdapter = new AttachmentAdapter(this);

		mListView.setAdapter(mAnnounceAttachmentAdapter);

		// mHorizonScrollLayout.setOnClickListener(this);

	}

	private String mFilePathStr;

	private ArrayList<String> mImageList = new ArrayList<String>();
	private ArrayList<AttachMent> mDocList = new ArrayList<AttachMent>();

	private void initData() {
		mImageList.clear();
		mDocList.clear();

		Intent intent = getIntent();
		mFilePathStr = intent.getStringExtra("filespath");
		Logger.d(TAG, "mFilePathStr=" + mFilePathStr);
		if (mFilePathStr != null && !mFilePathStr.isEmpty()) {
			try {
				JSONArray jsonArray = new JSONArray(mFilePathStr);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					String attachMentName = object.getString("attchname");
					if (attachMentName.contains(".jpg")
							|| attachMentName.contains(".png")
							|| attachMentName.contains(".jpeg")) {
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


	}

	private void setListAdapter() {
		mAnnounceAttachmentAdapter.addData(mDocList);
		mAnnounceAttachmentAdapter.notifyDataSetChanged();
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
			Toast.makeText(MessageInfoAttachActivity.this,
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
					Toast.makeText(MessageInfoAttachActivity.this,
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

	private class AttachmentAdapter extends BaseAdapter {

		private ArrayList<AttachMent> list = new ArrayList<AttachMent>();
		private LayoutInflater inflater;
		private Context context;

		public AttachmentAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			this.context = context;
		}

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
			AttachMent attachMent = list.get(position);
			DownloadItemViewHolder holder = null;
			DownloadInfo downloadInfo = downloadManager.getDownloadInfo(context,attachMent);
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.activity_attachment_list_item, null);
				holder = new DownloadItemViewHolder(convertView, downloadInfo);
				convertView.setTag(holder);
				holder.refresh();
			} else {
				holder = (DownloadItemViewHolder) convertView.getTag();
				holder.update(downloadInfo);
			}
			return convertView;
		}
	}

	public class DownloadItemViewHolder extends DownloadViewHolder {
		@ViewInject(R.id.attachment_name_tv)
		TextView label;
		@ViewInject(R.id.handle_btn)
		Button stopBtn;

		public DownloadItemViewHolder(View view, DownloadInfo downloadInfo) {
			super(view, downloadInfo);
			refresh();
		}

		@Event(R.id.handle_btn)
		private void toggleEvent(View view) {
			DownloadState state = downloadInfo.getState();
			switch (state) {
			case WAITING:
			case STARTED:
				downloadManager.stopDownload(downloadInfo);
				break;
			case ERROR:
			case STOPPED:
				try {
					downloadManager.startDownload(downloadInfo.getUrl(),
							downloadInfo.getLabel(),
							downloadInfo.getFileSavePath(),
							downloadInfo.isAutoResume(),
							downloadInfo.isAutoRename(), this);
				} catch (DbException ex) {
					Toast.makeText(x.app(), "添加下载失败", Toast.LENGTH_LONG).show();
				}
				break;
			case FINISHED:
				openFile(downloadInfo.getFileSavePath());
//				Toast.makeText(x.app(), "已经下载完成", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}

		// @Event(R.id.download_remove_btn)
		// private void removeEvent(View view) {
		// try {
		// downloadManager.removeDownload(downloadInfo);
		// downloadListAdapter.notifyDataSetChanged();
		// } catch (DbException e) {
		// Toast.makeText(x.app(), "移除任务失败", Toast.LENGTH_LONG).show();
		// }
		// }

		@Override
		public void update(DownloadInfo downloadInfo) {
			super.update(downloadInfo);
			refresh();
		}

		@Override
		public void onWaiting() {
			refresh();
		}

		@Override
		public void onStarted() {
			refresh();
		}

		@Override
		public void onLoading(long total, long current) {
			refresh();
		}

		@Override
		public void onSuccess(File result) {
			refresh();
		}

		@Override
		public void onError(Throwable ex, boolean isOnCallback) {
			refresh();
		}

		@Override
		public void onCancelled(Callback.CancelledException cex) {
			refresh();
		}

		public void refresh() {
			label.setText(downloadInfo.getLabel());
			// state.setText(downloadInfo.getState().toString());
			// progressBar.setProgress(downloadInfo.getProgress());

			stopBtn.setVisibility(View.VISIBLE);
			stopBtn.setText(x.app().getString(R.string.app_pause));
			DownloadState state = downloadInfo.getState();
			switch (state) {
			case WAITING:
			case STARTED:
				stopBtn.setText(x.app().getString(R.string.app_pause));
				break;
			case ERROR:
			case STOPPED:
				stopBtn.setText(x.app().getString(R.string.download));
				break;
			case FINISHED:
				stopBtn.setText(x.app().getString(R.string.open_file));
				break;
			default:
				stopBtn.setText(x.app().getString(R.string.download));
				break;
			}
		}
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
		intent.setClass(MessageInfoAttachActivity.this, ImageGalleryActivity.class);
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
			String fileName = currentPath.toString();
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
//				Toast.makeText(MessageInfoAttachActivity.this, "无法打开，请安装相应的软件！",
//						Toast.LENGTH_SHORT).show();
//			}
			
			Intent intent = OpenFiles.openFile(currentPath);
			if(intent!=null){
				startActivity(intent);
			}else{
				Toast.makeText(MessageInfoAttachActivity.this, "无法打开，请安装相应的软件！",Toast.LENGTH_SHORT).show();
			}
			
		} else {
			Toast.makeText(MessageInfoAttachActivity.this, "对不起，这不是文件！",
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
