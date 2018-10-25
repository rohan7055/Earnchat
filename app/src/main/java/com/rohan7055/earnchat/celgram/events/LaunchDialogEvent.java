package com.rohan7055.earnchat.celgram.events;

/**
 * Created by rohan on 1/2/2018.
 */

public class LaunchDialogEvent {
    private Boolean isDismiss;

    public LaunchDialogEvent(Boolean isDismiss) {
        this.isDismiss = isDismiss;
    }

    public Boolean getDismiss() {
        return isDismiss;
    }

    public void setDismiss(Boolean dismiss) {
        isDismiss = dismiss;
    }
}
