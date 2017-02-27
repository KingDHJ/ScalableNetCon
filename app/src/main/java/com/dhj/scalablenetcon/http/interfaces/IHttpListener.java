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
     * 对IHttpService中的请求成功数据,自己根据自己的业务需求去处理返回的数据
     * */
    void onSuccess(HttpEntity httpEntity);

    /**
     *当IHttpService请求失败时,子类实现该方法去处理
     * */
    void onFail(int code);
}
