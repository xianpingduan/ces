package com.xiexin.ces.utils;

public class StringUtils {

	public static String nullStringHandle(String str) {
		if (str == null || str.equals("null") || str.isEmpty()) {
			return "";
		} else {
			return str;
		}
	}
}
