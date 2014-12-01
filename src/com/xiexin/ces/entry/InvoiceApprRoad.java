package com.xiexin.ces.entry;

import java.io.Serializable;

public class InvoiceApprRoad implements Serializable
{

    private int ID;
    private String PragID;
    private String DataNbr;
    private String Category;
    private String Kind;
    private String ApprObj;
    private String Title;
    private String ApprDate;
    private String ProcessMode;
    private String ApprMemo;
    private String Status;
    private String CrtUser;
    private int InxNbr;
    private String ApprObjName;

    public int getID()
    {
	return ID;
    }

    public void setID( int iD )
    {
	ID = iD;
    }

    public String getPragID()
    {
	return PragID;
    }

    public void setPragID( String pragID )
    {
	PragID = pragID;
    }

    public String getDataNbr()
    {
	return DataNbr;
    }

    public void setDataNbr( String dataNbr )
    {
	DataNbr = dataNbr;
    }

    public String getCategory()
    {
	return Category;
    }

    public void setCategory( String category )
    {
	Category = category;
    }

    public String getKind()
    {
	return Kind;
    }

    public void setKind( String kind )
    {
	Kind = kind;
    }

    public String getApprObj()
    {
	return ApprObj;
    }

    public void setApprObj( String apprObj )
    {
	ApprObj = apprObj;
    }

    public String getTitle()
    {
	return Title;
    }

    public void setTitle( String title )
    {
	Title = title;
    }

    public String getApprDate()
    {
	return ApprDate;
    }

    public void setApprDate( String apprDate )
    {
	ApprDate = apprDate;
    }

    public String getProcessMode()
    {
	return ProcessMode;
    }

    public void setProcessMode( String processMode )
    {
	ProcessMode = processMode;
    }

    public String getApprMemo()
    {
	return ApprMemo;
    }

    public void setApprMemo( String apprMemo )
    {
	ApprMemo = apprMemo;
    }

    public String getStatus()
    {
	return Status;
    }

    public void setStatus( String status )
    {
	Status = status;
    }

    public String getCrtUser()
    {
	return CrtUser;
    }

    public void setCrtUser( String crtUser )
    {
	CrtUser = crtUser;
    }

    public int getInxNbr()
    {
	return InxNbr;
    }

    public void setInxNbr( int inxNbr )
    {
	InxNbr = inxNbr;
    }

    public String getApprObjName()
    {
	return ApprObjName;
    }

    public void setApprObjName( String apprObjName )
    {
	ApprObjName = apprObjName;
    }

}
