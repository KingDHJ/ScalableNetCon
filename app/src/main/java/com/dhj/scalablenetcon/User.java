package com.dhj.scalablenetcon;


import com.dhj.scalablenetcon.db.annotion.DbTable;

/**
 * Created by duanhuangjun on 17/2/27.
 */
@DbTable("tb_user")
public class User {

    public String name;

    public String password;

    public String user_id;
    public  Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
