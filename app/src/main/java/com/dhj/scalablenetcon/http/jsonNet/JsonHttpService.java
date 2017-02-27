package com.dhj.scalablenetcon.http.jsonNet;

import android.util.Log;

import com.dhj.scalablenetcon.http.constants.Constants;
import com.dhj.scalablenetcon.http.interfaces.IHttpListener;
import com.dhj.scalablenetcon.http.interfaces.IHttpService;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;

/**
 * Created by duanhuangjun on 17/2/27.
 */

public class JsonHttpService implements IHttpService{
    private static final String TAG = "KING_DHJ";
    private byte[] requestData;
    private IHttpListener iHttpListener;
    private String url;
    private HttpPost httpPost;
    private HttpClient httpClient=new DefaultHttpClient();
    private HttpResponseHandler httpResponseHandler = new HttpResponseHandler();
    @Override
    public void excute() {
        /**
         * 第一版只支持post请求,下一版添加(get请求方法,以及添加head);
         * */
        httpPost =  new HttpPost(url);
        ByteArrayEntity entity = new ByteArrayEntity(requestData);
        httpPost.setEntity(entity);
        Log.i(TAG,requestData.toString());
        try {
            httpClient.execute(httpPost,httpResponseHandler);
        } catch (IOException e) {
            iHttpListener.onFail(Constants.Conn_Erro);
        }
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setIHttpListener(IHttpListener iHttpListener) {
        this.iHttpListener=iHttpListener;
    }

    @Override
    public void setRequest(byte[] request) {
        this.requestData = request;
    }

    /**
     * HttpClient请求结果回调处理
     * */
    private class HttpResponseHandler extends BasicResponseHandler
    {
        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException {
            int statusCode = response.getStatusLine().getStatusCode();
            Log.i("TAG",statusCode+"");
            if(statusCode==200){
                iHttpListener.onSuccess(response.getEntity());
            }else {
                iHttpListener.onFail(Constants.Conn_Erro);
            }
            return null;
        }
    }
}
