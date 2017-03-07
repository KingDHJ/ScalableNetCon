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
public class HttpTask<T> implements Runnable {
    private IHttpService httpService;
    public HttpTask(RequestHodler<T> requestHodler)
    {
        httpService=requestHodler.getHttpService();
        httpService.setHttpListener(requestHodler.getHttpListener());
        httpService.setUrl(requestHodler.getUrl());
        T request=requestHodler.getRequestInfo();
        String requestInfo= JSON.toJSONString(request);

        try {
            httpService.setRequestData(requestInfo.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        httpService.excute();
    }
}
