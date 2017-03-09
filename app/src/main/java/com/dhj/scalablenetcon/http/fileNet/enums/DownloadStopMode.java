package com.dhj.scalablenetcon.http.fileNet.enums;
/**
 * Created by duanhuangjun on 17/2/27.
 */
public enum DownloadStopMode
{
    /**
     * 后台根据下载优先级调度自动停止下载任务
     */
    auto(0),

    /**
     * 手动停止下载任务
     */
    hand(1);
    DownloadStopMode(Integer value)
    {
        this.value = value;
    }

    /**
     * 值
     */
    private Integer value;

    public Integer getValue()
    {
        return value;
    }

    public void setValue(Integer value)
    {
        this.value = value;
    }

    public static DownloadStopMode getInstance(int value)
    {
        for (DownloadStopMode mode : DownloadStopMode.values())
        {
            if (mode.getValue() == value)
            {
                return mode;
            }
        }
        return DownloadStopMode.auto;
    }
}
