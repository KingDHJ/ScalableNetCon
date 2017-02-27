package com.dhj.scalablenetcon.http;

/**
 * Created by duanhuangjun on 17/2/27.
 */

import com.dhj.scalablenetcon.http.interfaces.IHttpListener;
import com.dhj.scalablenetcon.http.interfaces.IHttpService;

/**
 * 请求任务所需要的参数的封装
 * */
public class RequseHold<T> {
    /**
     * 具体执行下载的类
     * */
    private IHttpService iHttpService;

    /**
     * 把下载获得response的数据解析处理
     * */
    private IHttpListener iHttpListener;

    /**
     * 请求的url
     * */
    private String url;

    /**
     * 应用层调用时传入的请求参数实体类
     * */
    private T paramInfo;

    public IHttpService getiHttpService() {
        return iHttpService;
    }

    public void setiHttpService(IHttpService iHttpService) {
        this.iHttpService = iHttpService;
    }

    public IHttpListener getiHttpListener() {
        return iHttpListener;
    }

    public void setiHttpListener(IHttpListener iHttpListener) {
        this.iHttpListener = iHttpListener;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public T getParamInfo() {
        return paramInfo;
    }

    public void setParamInfo(T paramInfo) {
        this.paramInfo = paramInfo;
    }
}
