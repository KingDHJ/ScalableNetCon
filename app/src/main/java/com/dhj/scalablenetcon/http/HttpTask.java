package com.dhj.scalablenetcon.http;

/**
 * Created by duanhuangjun on 17/2/27.
 */

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.dhj.scalablenetcon.http.interfaces.IHttpService;

import java.io.UnsupportedEncodingException;

/**
 *一次请求的封装,交给线程池去处理,每做一次网络请求,就去创建一个请求的封装对象,再交给线程池去处理
 * */
public class HttpTask<T> implements Runnable{

    private static final String TAG = "KING_DHJ";
    private IHttpService iHttpService;

    public HttpTask(RequseHold<T> requseHold) {
        this.iHttpService = requseHold.getiHttpService();
        iHttpService.setIHttpListener(requseHold.getiHttpListener());
        //应用层调用接口传入的请求参数的实体对象,转成json
        T paramInfo = requseHold.getParamInfo();
        String paramInfoString = JSON.toJSONString(paramInfo);
        //把参数设置给具体做请求的IhttpService
        try {
            iHttpService.setRequest(paramInfoString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        iHttpService.excute();
        Log.i(TAG,"HTTP_TASK开始执行");
    }
}
