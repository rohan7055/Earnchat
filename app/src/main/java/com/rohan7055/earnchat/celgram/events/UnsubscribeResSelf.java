package com.rohan7055.earnchat.celgram.events;

/**
 * Created by rohan on 1/13/2018.
 */

public class UnsubscribeResSelf {
    private boolean isSuccess;

    public UnsubscribeResSelf(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
