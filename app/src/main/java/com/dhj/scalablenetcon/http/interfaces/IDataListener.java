package com.dhj.scalablenetcon.http.interfaces;

/**
 * Created by duanhuangjun on 17/2/27.
 */

/**
 * 把请求框架层的数据传给我们的应用调用层
 * */
public interface IDataListener<M> {
    /**
     * IHttpListener处理数据完成后得到调用层所需要的数据,通过该接口返回给调用层
     * */
    void onSuccess(M m);

    /**
     * 给调用层返回请求框架层请求失败的情况
     * */
    void onFail(int code);
}
