package com.dhj.scalablenetcon.http.jsonNet;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.dhj.scalablenetcon.http.constants.Constants;
import com.dhj.scalablenetcon.http.interfaces.IDataListener;
import com.dhj.scalablenetcon.http.interfaces.IHttpListener;

import org.apache.http.HttpEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by duanhuangjun on 17/2/27.
 */

/**
 * 策略模式
 * 支持json请求的策略,处理json请求的,请求数据处理类
 * */
public class JsonDealListener<M> implements IHttpListener{

    private Class<M> responese;   //调用层希望返回的请求结果对象

    IDataListener<M> dataListener;   //框架层把数据返回给调用层的接口

    Handler handler = new Handler(Looper.getMainLooper());

    public JsonDealListener(Class<M> responese, IDataListener<M> dataListener) {
        this.responese = responese;
        this.dataListener = dataListener;
    }

    @Override
    public void onSuccess(HttpEntity httpEntity) {
        InputStream inputStream=null;
        try {
            inputStream = httpEntity.getContent();
            String content = getContent(inputStream);
            //使用fastJson把数据解析成应用层希望的获取数据
            final M m =JSON.parseObject(content,responese);
            /*
            * 以上的所有操作都是在子线程中,需要把数据发到主线程,给应用层使用
            * */
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dataListener.onSuccess(m);
                }
            });
        } catch (IOException e) {
            dataListener.onFail(Constants.Analysy_Data);
        }
    }

    private String getContent(InputStream inputStream) {
        String content=null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();

            String line = null;

            try {

                while ((line = reader.readLine()) != null) {

                    sb.append(line + "\n");

                }

            } catch (IOException e) {
                dataListener.onFail(Constants.Analysy_Data);
                System.out.println("Error=" + e.toString());

            } finally {

                try {

                    inputStream.close();

                } catch (IOException e) {

                    System.out.println("Error=" + e.toString());

                }

            }
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            dataListener.onFail(Constants.Analysy_Data);
        }
        return content;
    }


    @Override
    public void onFail(int code) {
        dataListener.onFail(code);
    }
}
