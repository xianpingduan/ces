package com.xiexin.ces.entry;

public class PushMessage
{
    private String Account;
    private String FromUser;
    private String Title;
    private String Content;
    private String ToUser;
    private int bRead;
    private int MsgType;
    private String MsgID;
    private String CrtDate;
    private int ApprID;
    private String FilesPath;

    public String getAccount()
    {
	return Account;
    }

    public void setAccount( String account )
    {
	Account = account;
    }

    public String getFromUser()
    {
	return FromUser;
    }

    public void setFromUser( String fromUser )
    {
	FromUser = fromUser;
    }

    public String getTitle()
    {
	return Title;
    }

    public void setTitle( String title )
    {
	Title = title;
    }

    public String getContent()
    {
	return Content;
    }

    public void setContent( String content )
    {
	Content = content;
    }

    public String getToUser()
    {
	return ToUser;
    }

    public void setToUser( String toUser )
    {
	ToUser = toUser;
    }

    public int getbRead()
    {
	return bRead;
    }

    public void setbRead( int bRead )
    {
	this.bRead = bRead;
    }

    public int getMsgType()
    {
	return MsgType;
    }

    public void setMsgType( int msgType )
    {
	MsgType = msgType;
    }

    public String getMsgID()
    {
	return MsgID;
    }

    public void setMsgID( String msgID )
    {
	MsgID = msgID;
    }

    public String getCrtDate()
    {
	return CrtDate;
    }

    public void setCrtDate( String crtDate )
    {
	CrtDate = crtDate;
    }

    public int getApprID()
    {
	return ApprID;
    }

    public void setApprID( int apprID )
    {
	ApprID = apprID;
    }

    public String getFilesPath()
    {
	return FilesPath;
    }

    public void setFilesPath( String filesPath )
    {
	FilesPath = filesPath;
    }

}
