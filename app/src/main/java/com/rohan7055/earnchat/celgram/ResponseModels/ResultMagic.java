package com.rohan7055.earnchat.celgram.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 4/28/2017.
 */
public class ResultMagic
{
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("mobile")
    @Expose
    private String mobile;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
