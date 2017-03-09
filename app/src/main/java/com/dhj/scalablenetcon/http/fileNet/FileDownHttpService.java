package com.dhj.scalablenetcon.http.fileNet;

import android.util.Log;

import com.dhj.scalablenetcon.http.interfaces.IHttpListener;
import com.dhj.scalablenetcon.http.interfaces.IHttpService;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by duanhuangjun on 17/3/9.
 */

public class FileDownHttpService implements IHttpService{
    private static final String TAG = "king_dhj";
    /**
     * 即将添加到请求头的信息,使用线程安全的集合
     */
    private Map<String ,String> headerMap= Collections.synchronizedMap(new HashMap<String ,String>());
    /**
     * 含有请求处理的 接口
     */
    private IHttpListener httpListener;

    private HttpGet httpPost;
    private HttpClient httpClient=new DefaultHttpClient();
    private String url;
    /**
     * httpClient获取网络的回调
     */
    private  HttpRespnceHandler httpRespnceHandler=new HttpRespnceHandler();

    private byte[] requestDate;

    @Override
    public void setUrl(String url) {
        this.url=url;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    @Override
    public void setHttpListener(IHttpListener httpListener) {
        this.httpListener=httpListener;
    }

    @Override
    public void setRequestData(byte[] requestData) {
        this.requestDate=requestData;
    }

    @Override
    public void excute() {
        httpPost =new HttpGet(url);
        constrcutHeader();
//        ByteArrayEntity byteArrayEntity=new ByteArrayEntity(requestDate);
//        httpPost.setEntity(byteArrayEntity);
        try {
            httpClient.execute(httpPost,httpRespnceHandler);
        } catch (IOException e) {
            httpListener.onFail();
        }
    }

    /**
     * 把map集合放入到底层请求的的head中
     * */
    private void constrcutHeader() {
        Iterator iterator=headerMap.keySet().iterator();
        while (iterator.hasNext())
        {
            String key= (String) iterator.next();
            String value=headerMap.get(key);
            Log.i(TAG," 请求头信息  "+key+"  value "+value);
            httpPost.addHeader(key,value);
        }
    }

    @Override
    public Map<String, String> getHttpHeadMap() {
        return null;
    }

    @Override
    public boolean cancle() {
        return false;
    }

    @Override
    public boolean isCancle() {
        return false;
    }

    @Override
    public boolean isPause() {
        return false;
    }

    @Override
    public void pause() {

    }



    private class HttpRespnceHandler extends BasicResponseHandler
    {
        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException {
            //响应吗
            int code=response.getStatusLine().getStatusCode();
            if(code==200)
            {
                httpListener.onSuccess(response.getEntity());
            }else
            {
                httpListener.onFail();
            }


            return null;
        }
    }

}
