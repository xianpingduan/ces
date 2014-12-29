package com.xiexin.sortlistview;

public class SortModel {

	private String name; // 显示的数据
	private String sortLetters; // 显示数据拼音的首字母
	private String usrId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getUsrId() {
		return usrId;
	}

	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}
}
