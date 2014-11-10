package com.xiexin.ces.entry;

import java.io.Serializable;

public class ZhangTao implements Serializable
{

    private String realName;
    private String showName;

    public String getRealName()
    {
	return realName;
    }

    public void setRealName( String realName )
    {
	this.realName = realName;
    }

    public String getShowName()
    {
	return showName;
    }

    public void setShowName( String showName )
    {
	this.showName = showName;
    }

}
