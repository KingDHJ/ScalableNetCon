package com.dhj.scalablenetcon;


import com.dhj.scalablenetcon.db.annotion.DbTable;

/**
 * Created by duanhuangjun on 17/2/27.
 */
@DbTable("tb_photo")
public class Photo {
    public String time;

    public String path;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
