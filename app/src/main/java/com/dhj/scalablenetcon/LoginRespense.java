package com.dhj.scalablenetcon;

/**
 * Created by duanhuangjun on 17/2/27.
 */
class LoginRespense
{
    private int code;
    private String user_id;
    private String  time;
    private String name;
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "LoginRespense{" +
                "code=" + code +
                ", user_id='" + user_id + '\'' +
                ", time='" + time + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

