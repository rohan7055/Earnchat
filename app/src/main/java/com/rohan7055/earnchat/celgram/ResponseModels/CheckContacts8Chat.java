package com.rohan7055.earnchat.celgram.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohan on 8/13/2017.
 */

public class CheckContacts8Chat {

    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("message")
    @Expose
    private Boolean message;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getMessage() {
        return message;
    }

    public void setMessage(Boolean message) {
        this.message = message;
    }

    public List<Result8Chat> getResult() {
        return result;
    }

    public void setResult(List<Result8Chat> result) {
        this.result = result;
    }

    @SerializedName("data")
    @Expose
    private List<Result8Chat> result = new ArrayList<Result8Chat>();
}
