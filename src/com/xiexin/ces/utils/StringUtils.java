package com.xiexin.ces.utils;

import java.text.DecimalFormat;

public class StringUtils {

	public static String nullStringHandle(String str) {
		if (str == null || str.equals("null") || str.isEmpty()) {
			return "";
		} else {
			return str;
		}
	}

	public static String numberToStr(String money) {

		return "";
	}

	public static String priceDecimal(double price) {
		String toShow = String.valueOf(price);
		if (toShow.equals("0.0")) {
			return "0.0";
		} else {
			DecimalFormat formatter = new DecimalFormat("###,###,###.##");
			return formatter.format(price);
		}

	}
}
