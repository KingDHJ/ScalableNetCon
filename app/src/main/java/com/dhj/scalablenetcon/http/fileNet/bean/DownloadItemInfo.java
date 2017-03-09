package com.dhj.scalablenetcon.http.fileNet.bean;

/**
 * Created by duanhuangjun on 17/3/9.
 */

import com.dhj.scalablenetcon.http.HttpTask;
import com.dhj.scalablenetcon.http.fileNet.enums.DownloadStatus;

/**
 * 保存每个任务下载数据相关信息的对象
 * */
public class DownloadItemInfo extends  BaseEntity<DownloadItemInfo>{
    //下载的目前长度
    private long currentLength;
    //下载的总长度
    private long totalLength;
    //下载的url
    private String url ;
    //下载文件保存的文件夹路径
    private String filePath;
    //下载的任务线程
    private  transient HttpTask httpTask;
    //下载的状态
    private DownloadStatus status;

    public DownloadItemInfo(String url, String filePath) {
        this.url = url;
        this.filePath = filePath;
    }

    public DownloadItemInfo( ) {
    }


    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public HttpTask getHttpTask() {
        return httpTask;
    }

    public void setHttpTask(HttpTask httpTask) {
        this.httpTask = httpTask;
    }
}
