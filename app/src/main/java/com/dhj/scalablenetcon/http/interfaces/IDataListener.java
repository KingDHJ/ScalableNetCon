package com.dhj.scalablenetcon.http.interfaces;

/**
 * Created by duanhuangjun on 17/2/27.
 */

public interface IDataListener<M> {
    /**
     * 回调结果给调用层
     * @param m
     */
     void onSuccess(M m);


      void onErro();
}
