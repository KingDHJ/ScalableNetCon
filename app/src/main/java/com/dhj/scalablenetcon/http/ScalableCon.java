package com.dhj.scalablenetcon.http;

/**
 * Created by duanhuangjun on 17/2/27.
 */

import android.util.Log;

import com.dhj.scalablenetcon.http.interfaces.IDataListener;
import com.dhj.scalablenetcon.http.jsonNet.JsonDealListener;
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
    public static <T,M> void sendRequest(T requestInfo, String url , Class<M> reseponseClass, IDataListener<M> iDataListener){
        JsonDealListener mJsonDealListener = new JsonDealListener<>(reseponseClass, iDataListener);
        JsonHttpService mJsonHttpService = new JsonHttpService();
        //构建创建一个任务所需要的参数,requestHold
        RequseHold<T> mRequseHold = new RequseHold<>();
        mRequseHold.setiHttpListener(mJsonDealListener);
        mRequseHold.setiHttpService(mJsonHttpService);
        mRequseHold.setUrl(url);
        HttpTask<T> tHttpTask = new HttpTask<>(mRequseHold);
        //把任务交给线程池去执行
        try {
            ThreadPoolManager.getInstance().excute(new FutureTask<Object>(tHttpTask,null));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i(TAG,e.toString());
        }
    }
}
