package com.dhj.scalablenetcon.http.fileNet.interfaces;

/**
 * Created by Administrator on 2017/1/17 0017.
 */


import com.dhj.scalablenetcon.http.fileNet.enums.DownloadStatus;

/**
 * 断点续传监听接口
 * Created by duanhuangjun on 17/2/27.
 */
public interface IDownloadCallable
{

    /**
     * 新增下载任务的监听
     *
     * @param downloadId
     *            下载id
     */
    void onDownloadInfoAdd(int downloadId);

    /**
     * 删除下载任务的监听
     *
     * @param downloadId
     *            下载id
     */
    void onDownloadInfoRemove(int downloadId);

    /**
     * 下载状态变化
     *
     * @param downloadId
     *            下载id
     * @param status
     *            下载状态
     */

    void onDownloadStatusChanged(int downloadId, DownloadStatus status);

    /**
     * 获取了下载文件总的长度
     *
     * @param downloadId
     *            下载id
     * @param totalLength
     *            下载文件总的长度
     */
    void onTotalLengthReceived(int downloadId, long totalLength);

    /**
     * 下载进度
     *
     * @param downloadId
     *            下载id
     * @param downloadpercent
     *            下载的百分比
     * @param speed
     *            下载速度
     */
    void onCurrentSizeChanged(int downloadId, double downloadpercent, long speed);

    /**
     * 下载成功
     *
     * @param downloadId
     *            下载id
     */
    void onDownloadSuccess(int downloadId);

    /**
     * 下载失败监听
     *
     * @param downloadId
     *            下载id
     * @param errorCode
     *            下载错误码
     * @param errorMsg
     *            下载错误信息
     */
    void onDownloadError(int downloadId, int errorCode, String errorMsg);
}