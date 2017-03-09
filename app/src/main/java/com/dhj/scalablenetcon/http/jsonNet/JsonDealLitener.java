package com.dhj.scalablenetcon.http.jsonNet;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.dhj.scalablenetcon.http.interfaces.IDataListener;
import com.dhj.scalablenetcon.http.interfaces.IHttpListener;

import org.apache.http.HttpEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by duanhuangjun on 17/2/27.
 */

public class JsonDealLitener<M> implements IHttpListener {
    private Class<M> responese;
    /**
     * 回调调用层 的接口
     */
    private IDataListener<M> dataListener;

    Handler handler=new Handler(Looper.getMainLooper());
    public JsonDealLitener(Class<M> responese, IDataListener<M> dataListener) {
        this.responese = responese;
        this.dataListener = dataListener;
    }

    @Override
    public void onSuccess(HttpEntity httpEntity) {
        InputStream inputStream=null;
        try {
            inputStream=httpEntity.getContent();
            /*
            得到网络返回的数据
            子线程
             */
            String content=getContent(inputStream);
            final M m= JSON.parseObject(content,responese);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    dataListener.onSuccess(m);
                }
            });

        } catch (IOException e) {
            dataListener.onErro();
        }

    }

    @Override
    public void onFail() {
        dataListener.onErro();
    }

    @Override
    public void addHttpHeader(Map<String, String> headerMap) {

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
                dataListener.onErro();
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
            dataListener.onErro();
        }
        return content;
    }
}
