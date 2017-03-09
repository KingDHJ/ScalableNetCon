package com.dhj.scalablenetcon.http.fileNet.interfaces;

/**
 * Created by duanhuangjun on 17/3/9.
 */

import com.dhj.scalablenetcon.http.fileNet.bean.DownloadItemInfo;

/**
 * 通过接口的方式,把下载的情况返回给应用层
 * */
public interface IDownloadServiceCallable {

    void onDownloadStatusChanged(DownloadItemInfo downloadItemInfo);

    void onTotalLengthReceived(DownloadItemInfo downloadItemInfo);

    void onCurrentSizeChanged(DownloadItemInfo downloadItemInfo, double downLenth, long speed);

    void onDownloadSuccess(DownloadItemInfo downloadItemInfo);

    void onDownloadPause(DownloadItemInfo downloadItemInfo);

    void onDownloadError(DownloadItemInfo downloadItemInfo, int var2, String var3);
}
