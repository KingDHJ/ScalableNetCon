package com.dhj.scalablenetcon.http;

/**
 * Created by duanhuangjun on 17/2/27.
 */

import android.util.Log;

import com.dhj.scalablenetcon.http.interfaces.IDataListener;
import com.dhj.scalablenetcon.http.interfaces.IHttpListener;
import com.dhj.scalablenetcon.http.interfaces.IHttpService;
import com.dhj.scalablenetcon.http.jsonNet.JsonDealLitener;
import com.dhj.scalablenetcon.http.jsonNet.JsonHttpService;

import java.util.concurrent.FutureTask;

/**
 * 应用层调用网络请求框架层的入口
 * */

public class ScalableCon {
    private static final String TAG = "KING_DHJ";

    /**
     * T  应用层的请求参数对象
     * M  应用层传入的希望返回的结果对象
     *
     * */
    public static <T,M> void sendRequest(T  requestInfo, String url,
                                         Class<M> response, IDataListener dataListener)
    {
        RequestHodler<T> requestHodler=new RequestHodler<>();
        requestHodler.setUrl(url);
        IHttpService httpService=new JsonHttpService();
        IHttpListener httpListener=new JsonDealLitener<>(response,dataListener);
        requestHodler.setHttpService(httpService);
        requestHodler.setHttpListener(httpListener);
        HttpTask<T> httpTask=new HttpTask<>(requestHodler);
        try {
            ThreadPoolManager.getInstance().execte(new FutureTask<Object>(httpTask,null));
        } catch (InterruptedException e) {
            dataListener.onFail();
        }
    }
}
