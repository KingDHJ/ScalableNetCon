package com.dhj.scalablenetcon.http.interfaces;

/**
 * Created by duanhuangjun on 17/2/27.
 */

import java.util.Map;

/**
 *获取网络
 */
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

    void pause();

    /**
     *
     * 以下的方法是 额外添加的
     * 获取请求头的map
     * @return
     */
     Map<String,String> getHttpHeadMap();

     boolean cancle();

     boolean isCancle();

     boolean isPause();


}
