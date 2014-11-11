package com.xiexin.ces.entry;

import java.io.Serializable;

public class BaseResult implements Serializable
{
    private int Success;
    private String Msg;

    public int getSuccess()
    {
	return Success;
    }

    public void setSuccess( int success )
    {
	Success = success;
    }

    public String getMsg()
    {
	return Msg;
    }

    public void setMsg( String msg )
    {
	Msg = msg;
    }
}
