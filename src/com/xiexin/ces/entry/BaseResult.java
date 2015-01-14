package com.xiexin.ces.entry;

import java.io.Serializable;

public class BaseResult implements Serializable {
	private int success;
	private String msg;
	private String data;

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
