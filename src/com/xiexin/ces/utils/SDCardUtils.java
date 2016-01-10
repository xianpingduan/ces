package com.xiexin.ces.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class SDCardUtils {

	public final static String DOWNLOAD ="ces/download";
	
	public static String getDiskCacheDir(Context context){
		
		String cachePath = null;
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				&&!Environment.isExternalStorageRemovable()
				&& context.getExternalCacheDir()!=null){
			cachePath = context.getExternalCacheDir().getPath();
		}else{
			cachePath = context.getCacheDir().getPath();
		}
		
		return cachePath;
	}
	
	public static String getDownloadDiskCacheDir(Context context){
		return getDiskCacheDir(context)+File.separator+DOWNLOAD;
	}
	
	public static String getDownloadDiskCacheFilePath(Context context,String name){
		return getDownloadDiskCacheDir(context)+File.separator+name;
	}
	
}
