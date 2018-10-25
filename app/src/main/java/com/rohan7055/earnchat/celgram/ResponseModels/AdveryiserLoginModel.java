package com.rohan7055.earnchat.celgram.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rohan on 8/13/2017.
 */

public class AdveryiserLoginModel {
    @SerializedName("mobile")
    @Expose
    String mobile;

    @SerializedName("password")
    @Expose
    String password;

    public AdveryiserLoginModel(String mobile, String password) {
        this.mobile = mobile;
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
