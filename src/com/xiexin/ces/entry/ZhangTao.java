package com.xiexin.ces.entry;

import java.io.Serializable;

public class ZhangTao implements Serializable {

	private String userid;
	private String username;
	private String accinfo;
	private String connname;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAccinfo() {
		return accinfo;
	}

	public void setAccinfo(String accinfo) {
		this.accinfo = accinfo;
	}

	public String getConnname() {
		return connname;
	}

	public void setConnname(String connname) {
		this.connname = connname;
	}

}
