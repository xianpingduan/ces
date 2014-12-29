package com.xiexin.ces.entry;

import java.io.Serializable;

public class Employee implements Serializable
{

    private String EmployeeID;
    private String Descr;
    private String Sex;
    private String Condition;
    private String Credit;
    private String Title;
    private String Depart;
    private String Job;
    private String ReportTo;
    private String EName;
    private String TelNbr;
    private String Mobile;
    private String Email;
    private String EffDate;
    private String FailDate;
    private String Bank;
    private String Account;
    private String Channel;
    private String Area;

    private boolean bDupBorrow;

    private String UpdateUser;
    private String UpdateDT;
    private String Remark;
    private boolean bTelAss;
    
    private String sortLetters; // 显示数据拼音的首字母
    public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getEmployeeID()
    {
	return EmployeeID;
    }

    public void setEmployeeID( String employeeID )
    {
	EmployeeID = employeeID;
    }

    public String getDescr()
    {
	return Descr;
    }

    public void setDescr( String descr )
    {
	Descr = descr;
    }

    public String getSex()
    {
	return Sex;
    }

    public void setSex( String sex )
    {
	Sex = sex;
    }

    public String getCondition()
    {
	return Condition;
    }

    public void setCondition( String condition )
    {
	Condition = condition;
    }

    public String getCredit()
    {
	return Credit;
    }

    public void setCredit( String credit )
    {
	Credit = credit;
    }

    public String getTitle()
    {
	return Title;
    }

    public void setTitle( String title )
    {
	Title = title;
    }

    public String getDepart()
    {
	return Depart;
    }

    public void setDepart( String depart )
    {
	Depart = depart;
    }

    public String getJob()
    {
	return Job;
    }

    public void setJob( String job )
    {
	Job = job;
    }

    public String getReportTo()
    {
	return ReportTo;
    }

    public void setReportTo( String reportTo )
    {
	ReportTo = reportTo;
    }

    public String getEName()
    {
	return EName;
    }

    public void setEName( String eName )
    {
	EName = eName;
    }

    public String getTelNbr()
    {
	return TelNbr;
    }

    public void setTelNbr( String telNbr )
    {
	TelNbr = telNbr;
    }

    public String getMobile()
    {
	return Mobile;
    }

    public void setMobile( String mobile )
    {
	Mobile = mobile;
    }

    public String getEmail()
    {
	return Email;
    }

    public void setEmail( String email )
    {
	Email = email;
    }

    public String getEffDate()
    {
	return EffDate;
    }

    public void setEffDate( String effDate )
    {
	EffDate = effDate;
    }

    public String getFailDate()
    {
	return FailDate;
    }

    public void setFailDate( String failDate )
    {
	FailDate = failDate;
    }

    public String getBank()
    {
	return Bank;
    }

    public void setBank( String bank )
    {
	Bank = bank;
    }

    public String getAccount()
    {
	return Account;
    }

    public void setAccount( String account )
    {
	Account = account;
    }

    public String getChannel()
    {
	return Channel;
    }

    public void setChannel( String channel )
    {
	Channel = channel;
    }

    public String getArea()
    {
	return Area;
    }

    public void setArea( String area )
    {
	Area = area;
    }

    public boolean getbDupBorrow()
    {
	return bDupBorrow;
    }

    public void setbDupBorrow( boolean bDupBorrow )
    {
	this.bDupBorrow = bDupBorrow;
    }

    public String getUpdateUser()
    {
	return UpdateUser;
    }

    public void setUpdateUser( String updateUser )
    {
	UpdateUser = updateUser;
    }

    public String getUpdateDT()
    {
	return UpdateDT;
    }

    public void setUpdateDT( String updateDT )
    {
	UpdateDT = updateDT;
    }

    public String getRemark()
    {
	return Remark;
    }

    public void setRemark( String remark )
    {
	Remark = remark;
    }

    public boolean getbTelAss()
    {
	return bTelAss;
    }

    public void setbTelAss( boolean bTelAss )
    {
	this.bTelAss = bTelAss;
    }

}
