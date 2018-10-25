package com.rohan7055.earnchat.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//This is just dummy
public class UserModel {

    @SerializedName("user_id")
    @Expose
    private String userid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("lastname")
    @Expose
    private String lastname;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("imgname")
    @Expose
    private String imgname;



    @SerializedName("groups_sub")
    @Expose
    private String groups_sub;

    @SerializedName("session")
    @Expose
    private String session;

    @SerializedName("db_update_time")
    @Expose
    private String db_update_time;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public String getImgname() {
        return imgname;
    }

    public void setImgname(String imgname) {
        this.imgname = imgname;
    }

    public String getGroups_sub() {
        return groups_sub;
    }

    public void setGroups_sub(String groups_sub) {
        this.groups_sub = groups_sub;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getDb_update_time() {
        return db_update_time;
    }

    public void setDb_update_time(String db_update_time) {
        this.db_update_time = db_update_time;
    }
}
