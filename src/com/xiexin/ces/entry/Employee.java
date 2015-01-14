package com.xiexin.ces.entry;

import java.io.Serializable;

public class Employee implements Serializable
{

    private String employeeid;
    private String descr;
    private String sex;
    private String condition;
    private String credit;
    private String title;
    private String depart;
    private String job;
    private String reportto;
    private String ename;
    private String telnbr;
    private String mobile;
    private String email;
    private String effdate;
    private String faildate;
    private String bank;
    private String account;
    private String channel;
    private String area;

    private boolean bdupborrow;

    private String updateuser;
    private String updatedt;
    private String remark;
    private boolean btelass;
    
    private String sortLetters; // 显示数据拼音的首字母
    public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getEmployeeid() {
		return employeeid;
	}
	public void setEmployeeid(String employeeid) {
		this.employeeid = employeeid;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDepart() {
		return depart;
	}
	public void setDepart(String depart) {
		this.depart = depart;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getReportto() {
		return reportto;
	}
	public void setReportto(String reportto) {
		this.reportto = reportto;
	}
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public String getTelnbr() {
		return telnbr;
	}
	public void setTelnbr(String telnbr) {
		this.telnbr = telnbr;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEffdate() {
		return effdate;
	}
	public void setEffdate(String effdate) {
		this.effdate = effdate;
	}
	public String getFaildate() {
		return faildate;
	}
	public void setFaildate(String faildate) {
		this.faildate = faildate;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public boolean isBdupborrow() {
		return bdupborrow;
	}
	public void setBdupborrow(boolean bdupborrow) {
		this.bdupborrow = bdupborrow;
	}
	public String getUpdateuser() {
		return updateuser;
	}
	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}
	public String getUpdatedt() {
		return updatedt;
	}
	public void setUpdatedt(String updatedt) {
		this.updatedt = updatedt;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public boolean isBtelass() {
		return btelass;
	}
	public void setBtelass(boolean btelass) {
		this.btelass = btelass;
	}

	

}
