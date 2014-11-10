package com.xiexin.ces.widgets;

public interface ILoadingViewListener
{

    /*
     * 
     */
    public abstract void onInitRequestData();

    /*
     * 在网络错误提示下，点击重试按钮后调用
     */
    public abstract void onRetryRequestData();

}
