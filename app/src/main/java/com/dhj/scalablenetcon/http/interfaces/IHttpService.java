package com.dhj.scalablenetcon.http.interfaces;

/**
 * Created by duanhuangjun on 17/2/27.
 */

import java.util.Map;

/**
 * 负责处理网络请求的操作
 * */
public interface IHttpService {
    /**
     * 设置url
     * @param url
     */
    void setUrl(String url);

    /**
     * 执行获取网络
     */
    void excute();

    /**
     * 设置处理接口
     * @param httpListener
     */
    void setHttpListener(IHttpListener httpListener);

    /**
     * 设置请求参数
     * String  1
     * byte[]  2
     *
     */
    void setRequestData(byte[] requestData);

    /**
     * 文件下载额外添加的方法
     * */
    Map<String,String> getHttpHeadMap();

    boolean cancle();

    boolean isCancle();

    boolean isPause();

    void pause();
}
