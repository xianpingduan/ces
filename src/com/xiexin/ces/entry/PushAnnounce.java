package com.xiexin.ces.entry;

public class PushAnnounce
{
    private String fromuser;
    private String title;
    private String content;
    private String noticeid;
    private String filespath;
    private String crtdate;

    public String getFilespath()
    {
        return filespath;
    }

    public void setFilespath(String filespath)
    {
        this.filespath = filespath;
    }

    public String getFromuser()
    {
        return fromuser;
    }

    public void setFromuser(String fromuser)
    {
        this.fromuser = fromuser;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getNoticeid()
    {
        return noticeid;
    }

    public void setNoticeid(String noticeid)
    {
        this.noticeid = noticeid;
    }

    public String getCrtdate()
    {
        return crtdate;
    }

    public void setCrtdate(String crtdate)
    {
        this.crtdate = crtdate;
    }
}
