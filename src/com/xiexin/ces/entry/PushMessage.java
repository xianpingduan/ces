package com.xiexin.ces.entry;

public class PushMessage {
	private String account;
	private String fromuser;
	private String title;
	private String content;
	private String touser;
	private int bread;
	private int msgtype;
	private String msgid;
	private String crtdate;
	private int apprid;
	private String filespath;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getFromuser() {
		return fromuser;
	}

	public void setFromuser(String fromuser) {
		this.fromuser = fromuser;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public int getBread() {
		return bread;
	}

	public void setBread(int bread) {
		this.bread = bread;
	}

	public int getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(int msgtype) {
		this.msgtype = msgtype;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getCrtdate() {
		return crtdate;
	}

	public void setCrtdate(String crtdate) {
		this.crtdate = crtdate;
	}

	public int getApprid() {
		return apprid;
	}

	public void setApprid(int apprid) {
		this.apprid = apprid;
	}

	public String getFilespath() {
		return filespath;
	}

	public void setFilespath(String filespath) {
		this.filespath = filespath;
	}

}
