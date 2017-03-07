package com.dhj.scalablenetcon.http.interfaces;

/**
 * Created by duanhuangjun on 17/2/27.
 */

/**
 * 把请求框架层的数据传给我们的应用调用层
 * */
public interface IDataListener<M> {
    /**
     * 回调结果给调用层
     * @param m
     */
    void onSuccess(M m);


    void onFail();
}