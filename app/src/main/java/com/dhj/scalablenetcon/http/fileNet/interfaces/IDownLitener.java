package com.dhj.scalablenetcon.http.fileNet.interfaces;

import com.dhj.scalablenetcon.http.interfaces.IHttpListener;
import com.dhj.scalablenetcon.http.interfaces.IHttpService;

/**
 * Created by duanhuangjun on 17/3/9.
 */

/**
 * 下载文件的处理数据的接口,除了请求成功、失败回调的处理,还增加了
 * */
public interface IDownLitener extends IHttpListener{
    //在IDownLitener应该拥有IHttpService的应用,去把应用层设置的head信息传给IHttpService去做具体的请求
    void setHttpServive(IHttpService httpServive);

    //取消下载
    void  setCancleCalle();

    //暂停下载
    void  setPuaseCallble();


}
