package com.xiexin.ces.entry;

import java.io.Serializable;

public class InvoiceApprRoad implements Serializable {

	private int id;
	private String pragid;
	private String datanbr;
	private String category;
	private String kind;
	private String approbj;
	private String title;
	private String apprdate;
	private String processmode;
	private String apprmemo;
	private String status;
	private String crtuser;
	private int inxnbr;
	private String approbjname;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPragid() {
		return pragid;
	}

	public void setPragid(String pragid) {
		this.pragid = pragid;
	}

	public String getDatanbr() {
		return datanbr;
	}

	public void setDatanbr(String datanbr) {
		this.datanbr = datanbr;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getApprobj() {
		return approbj;
	}

	public void setApprobj(String approbj) {
		this.approbj = approbj;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getApprdate() {
		return apprdate;
	}

	public void setApprdate(String apprdate) {
		this.apprdate = apprdate;
	}

	public String getProcessmode() {
		return processmode;
	}

	public void setProcessmode(String processmode) {
		this.processmode = processmode;
	}

	public String getApprmemo() {
		return apprmemo;
	}

	public void setApprmemo(String apprmemo) {
		this.apprmemo = apprmemo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCrtuser() {
		return crtuser;
	}

	public void setCrtuser(String crtuser) {
		this.crtuser = crtuser;
	}

	public int getInxnbr() {
		return inxnbr;
	}

	public void setInxnbr(int inxnbr) {
		this.inxnbr = inxnbr;
	}

	public String getApprobjname() {
		return approbjname;
	}

	public void setApprobjname(String approbjname) {
		this.approbjname = approbjname;
	}

}
