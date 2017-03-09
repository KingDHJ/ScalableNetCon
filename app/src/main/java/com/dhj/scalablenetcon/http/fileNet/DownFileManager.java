package com.dhj.scalablenetcon.http.fileNet;

/**
 * Created by duanhuangjun on 17/3/9.
 */

import android.os.Environment;
import android.util.Log;

import com.dhj.scalablenetcon.http.HttpTask;
import com.dhj.scalablenetcon.http.RequestHodler;
import com.dhj.scalablenetcon.http.ThreadPoolManager;
import com.dhj.scalablenetcon.http.fileNet.bean.DownloadItemInfo;
import com.dhj.scalablenetcon.http.fileNet.interfaces.IDownloadServiceCallable;
import com.dhj.scalablenetcon.http.interfaces.IHttpListener;
import com.dhj.scalablenetcon.http.interfaces.IHttpService;

import java.io.File;
import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * 与应用层进行交互的类,将应用层和框架层进行隔离
 * */

public class DownFileManager implements IDownloadServiceCallable {
    private static final String TAG = "king_dhj";
    private byte[] lock=new byte[0];
    /**
     * 下载
     * @param url
     */
    public void down(String url){
        synchronized(lock){
            //创建文件夹
            String[] preFixs=url.split("/");
            String afterFix=preFixs[preFixs.length-1];
            File file=new File(Environment.getExternalStorageDirectory(),afterFix);
            //实例化DownloadItem
            DownloadItemInfo downloadItemInfo=new DownloadItemInfo(url,file.getAbsolutePath());
            //请求参数的封装对象
            RequestHodler requestHodler=new RequestHodler();
            //设置请求下载的策略
            IHttpService httpService= new FileDownHttpService();
            //得到请求头的参数 map
            Map<String,String> map=httpService.getHttpHeadMap();
            /**
             * 处理结果的策略
             */
            IHttpListener httpListener=new DownLoadLitener(downloadItemInfo,this,httpService);
            requestHodler.setHttpListener(httpListener);
            requestHodler.setHttpService(httpService);
            requestHodler.setUrl(url);
            HttpTask httpTask=new HttpTask(requestHodler);
            try {
                ThreadPoolManager.getInstance().execte(new FutureTask<Object>(httpTask,null));
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public void onDownloadStatusChanged(DownloadItemInfo downloadItemInfo) {

    }

    @Override
    public void onTotalLengthReceived(DownloadItemInfo downloadItemInfo) {

    }

    @Override
    public void onCurrentSizeChanged(DownloadItemInfo downloadItemInfo, double downLenth, long speed) {
        Log.i(TAG,"下载速度："+ speed/1000 +"k/s");
        Log.i(TAG,"-----路径  "+ downloadItemInfo.getFilePath()+"  下载长度  "+downLenth+"   速度  "+speed);
    }

    @Override
    public void onDownloadSuccess(DownloadItemInfo downloadItemInfo) {
        Log.i(TAG,"下载成功    路劲  "+ downloadItemInfo.getFilePath()+"  url "+ downloadItemInfo.getUrl());
    }

    @Override
    public void onDownloadPause(DownloadItemInfo downloadItemInfo) {

    }

    @Override
    public void onDownloadError(DownloadItemInfo downloadItemInfo, int var2, String var3) {

    }
}
