package com.xiexin.ces.entry;

import java.io.Serializable;

public class ZhangTao implements Serializable
{

    private String UserID;
    private String UserName;
    private String AccInfo;
	private String ConnName;
	
    public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getAccInfo() {
		return AccInfo;
	}
	public void setAccInfo(String accInfo) {
		AccInfo = accInfo;
	}
	public String getConnName() {
		return ConnName;
	}
	public void setConnName(String connName) {
		ConnName = connName;
	}

 

}
