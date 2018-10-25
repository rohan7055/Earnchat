package com.rohan7055.earnchat.celgram.ResponseModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 4/8/2017.
 */

public class GroupUpdation {

    @SerializedName("status")
    String status;

    @SerializedName("message")
    String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
