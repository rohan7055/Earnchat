package com.rohan7055.earnchat.celgram.ResponseModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 4/15/2017.
 */
public class GetStatus {

    @SerializedName("status")
    boolean status;

    @SerializedName("message")
    String message;


    @SerializedName("data")
    Statusdata data;

    public Statusdata getData() {
        return data;
    }

    public void setData(Statusdata data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}
