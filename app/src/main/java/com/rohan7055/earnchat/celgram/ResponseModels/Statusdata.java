package com.rohan7055.earnchat.celgram.ResponseModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 4/15/2017.
 */
public class Statusdata {
    @SerializedName("celgram_status")
    String celgram_status;

    public String getCelgram_status() {
        return celgram_status;
    }

    public void setCelgram_status(String celgram_status) {
        this.celgram_status = celgram_status;
    }
}
