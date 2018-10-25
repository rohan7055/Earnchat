package com.rohan7055.earnchat.celgram.events;

/**
 * Created by rohan on 12/31/2017.
 */

public class DialogdismissEvent {
    private boolean result;
    private String groupId;

    public DialogdismissEvent(boolean result, String groupId) {
        this.result = result;
        this.groupId = groupId;
    }

    public DialogdismissEvent(boolean result) {
        this.result = result;
    }

    public String getGroupId() {
        return groupId;
    }

    public boolean getResult() {
        return result;
    }
}
