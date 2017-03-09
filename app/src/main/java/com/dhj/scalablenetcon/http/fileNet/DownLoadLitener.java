package com.dhj.scalablenetcon.http.fileNet;

import android.os.Handler;
import android.os.Looper;

import com.dhj.scalablenetcon.http.fileNet.bean.DownloadItemInfo;
import com.dhj.scalablenetcon.http.fileNet.enums.DownloadStatus;
import com.dhj.scalablenetcon.http.fileNet.interfaces.IDownLitener;
import com.dhj.scalablenetcon.http.fileNet.interfaces.IDownloadServiceCallable;
import com.dhj.scalablenetcon.http.interfaces.IDataListener;
import com.dhj.scalablenetcon.http.interfaces.IHttpService;

import org.apache.http.HttpEntity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by duanhuangjun on 17/3/9.
 */

/**
 * 下载有关成功后,对http Response的数据进行处理
 * */
public class DownLoadLitener implements IDownLitener{

    //下载有关信息的封装
    private  DownloadItemInfo downloadItemInfo;
    private File file;
    protected  String url;
    //已经下载的长度
    private long breakPoint;
    //给应用层的回调
    private IDownloadServiceCallable downloadServiceCallable;
    //具体做下载
    private IHttpService httpService;

    private Handler handler=new Handler(Looper.getMainLooper());

    public DownLoadLitener(DownloadItemInfo downloadItemInfo,
                           IDownloadServiceCallable downloadServiceCallable,
                           IHttpService httpService) {
        this.downloadItemInfo = downloadItemInfo;
        this.downloadServiceCallable = downloadServiceCallable;
        this.httpService = httpService;
        this.file=new File(downloadItemInfo.getFilePath());
        /**
         * 得到已经下载的长度
         */
        this.breakPoint=file.length();
    }

    @Override
    public void setHttpServive(IHttpService httpServive) {
        this.httpService=httpServive;
    }

    @Override
    public void setCancleCalle() {

    }

    @Override
    public void setPuaseCallble() {

    }

    /**
     * 请求成功后,IHttpLitener处理下载的数据,
     * */
    @Override
    public void onSuccess(HttpEntity httpEntity) {
        InputStream inputStream = null;
        try {
            inputStream = httpEntity.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long startTime = System.currentTimeMillis();
        //用于计算每秒多少k
        long speed = 0L;
        //花费时间
        long useTime = 0L;
        //下载的长度
        long getLen = 0L;
        //接受的长度
        long receiveLen = 0L;
        boolean bufferLen = false;
        //得到下载的长度
        long dataLength = httpEntity.getContentLength();
        //单位时间下载的字节数
        long calcSpeedLen = 0L;
        //总数
        long totalLength = this.breakPoint + dataLength;

        //更新数量
        this.receviceTotalLength(totalLength);
        //更新状态
        this.downloadStatusChange(DownloadStatus.downloading);
        byte[] buffer = new byte[1024];
        int count = 0;
        long currentTime = System.currentTimeMillis();
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;

        try {
            if (!makeDir(this.getFile().getParentFile())) {
                downloadServiceCallable.onDownloadError(downloadItemInfo,1,"创建文件夹失败");
            } else {
                fos = new FileOutputStream(this.getFile(), true);
                bos = new BufferedOutputStream(fos);
                int length = 1;
                while ((length = inputStream.read(buffer)) != -1) {
                    if (this.getHttpService().isCancle()) {
                        downloadServiceCallable.onDownloadError(downloadItemInfo, 1, "用户取消了");
                        return;
                    }

                    if (this.getHttpService().isPause()) {
                        downloadServiceCallable.onDownloadError(downloadItemInfo, 2, "用户暂停了");
                        return;
                    }

                    bos.write(buffer, 0, length);
                    getLen += (long) length;
                    receiveLen += (long) length;
                    calcSpeedLen += (long) length;
                    ++count;
                    if (receiveLen * 10L / totalLength >= 1L || count >= 5000) {
                        currentTime = System.currentTimeMillis();
                        useTime = currentTime - startTime;
                        startTime = currentTime;
                        speed = 1000L * calcSpeedLen / useTime;
                        count = 0;
                        calcSpeedLen = 0L;
                        receiveLen = 0L;
                        this.downloadLengthChange(this.breakPoint + getLen, totalLength, speed);
                    }
                }
                bos.close();
                inputStream.close();
                if (dataLength != getLen) {
                    downloadServiceCallable.onDownloadError(downloadItemInfo, 3, "下载长度不相等");
                } else {
                    this.downloadLengthChange(this.breakPoint + getLen, totalLength, speed);
                    this.downloadServiceCallable.onDownloadSuccess(downloadItemInfo.copy());
                }
            }
        } catch (IOException ioException) {
            if (this.getHttpService() != null) {
//                this.getHttpService().abortRequest();
            }
            return;
        } catch (Exception e) {
            if (this.getHttpService() != null) {
//                this.getHttpService().abortRequest();
            }
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }

                if (httpEntity != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 回调  长度的变化
     * @param totalLength
     */
    private void receviceTotalLength(long totalLength) {
        downloadItemInfo.setCurrentLength(totalLength);
        final DownloadItemInfo copyDownloadItemInfo=downloadItemInfo.copy();
        if(downloadServiceCallable!=null)
        {
            synchronized (this.downloadServiceCallable)
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadServiceCallable.onTotalLengthReceived(copyDownloadItemInfo);
                    }
                });
            }
        }

    }

    /**
     * 回调下载进度的改变
     * */
    private void downloadLengthChange(final long downlength, final long totalLength, final long speed) {

        downloadItemInfo.setCurrentLength(downlength);
        if(downloadServiceCallable!=null)
        {
            DownloadItemInfo copyDownItenIfo=downloadItemInfo.copy();
            synchronized (this.downloadServiceCallable)
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadServiceCallable.onCurrentSizeChanged(downloadItemInfo,downlength/totalLength,speed);
                    }
                });
            }
        }
    }


    /**
     * 更改下载时的状态
     * @param downloading
     */
    private void downloadStatusChange(DownloadStatus downloading) {
        downloadItemInfo.setStatus(downloading);
        final DownloadItemInfo copyDownloadItemInfo=downloadItemInfo.copy();
        if(downloadServiceCallable!=null)
        {
            synchronized (this.downloadServiceCallable)
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadServiceCallable.onDownloadStatusChanged(copyDownloadItemInfo);
                    }
                });
            }
        }
    }

    /**
     * 创建文件夹的操作
     * @param parentFile
     * @return
     */
    private boolean makeDir(File parentFile) {
        return parentFile.exists()&&!parentFile.isFile()
                ?parentFile.exists()&&parentFile.isDirectory():
                parentFile.mkdirs();
    }


    public File getFile() {
        return file;
    }


    public IHttpService getHttpService() {
        return httpService;
    }



    @Override
    public void onFail() {

    }

    @Override
    public void addHttpHeader(Map<String, String> headerMap) {

    }
}
