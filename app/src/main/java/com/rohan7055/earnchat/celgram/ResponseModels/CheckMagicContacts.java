package com.rohan7055.earnchat.celgram.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ron on 4/28/2017.
 */
public class CheckMagicContacts
{
    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("result")
    @Expose
    private List<ResultMagic> result = new ArrayList<ResultMagic>();


    private String message;
    /**
     *
     * @return
     * The status
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The result
     */
    public List<ResultMagic> getResultMagic() {
        return result;
    }

    /**
     *
     * @param result
     * The result
     */
    public void setResultMagic(List<ResultMagic> result) {
        this.result = result;
    }
}
