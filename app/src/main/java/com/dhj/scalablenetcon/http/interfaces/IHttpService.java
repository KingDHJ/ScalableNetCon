package com.dhj.scalablenetcon.http.interfaces;

/**
 * Created by duanhuangjun on 17/2/27.
 */

/**
 * 负责处理网络请求的操作
 * */
public interface IHttpService {
    /**
     * 执行网络操作
     */
    void excute();

    /**
     * 设置url
     * */
    void setUrl(String url);

    /**
     * 设置数据处理接口
     * */
    void setIHttpListener(IHttpListener iHttpListener);

    /**
     * 设置请求参数
     * */
    void setRequest(byte[] request);
}
