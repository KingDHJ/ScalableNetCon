package com.dhj.scalablenetcon.http.interfaces;

/**
 * Created by duanhuangjun on 17/2/27.
 */

import org.apache.http.HttpEntity;

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
}
