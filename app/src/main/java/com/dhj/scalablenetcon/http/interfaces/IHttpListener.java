package com.dhj.scalablenetcon.http.interfaces;

/**
 * Created by duanhuangjun on 17/2/27.
 */

import org.apache.http.HttpEntity;

import java.util.Map;

/**
 * 负责处理网络请求的到的数据,把response中的数据转化成需要的数据
 *
 * */
public interface IHttpListener {
    /**
     * 网络访问
     * 处理结果  回调
     * @param httpEntity
     */
    void onSuccess(HttpEntity httpEntity);

    void onFail();
    //接受应用层传入的头信息,再给Iservice传入,添加头信息去具体请求数据
    void addHttpHeader(Map<String,String> headerMap);
}
