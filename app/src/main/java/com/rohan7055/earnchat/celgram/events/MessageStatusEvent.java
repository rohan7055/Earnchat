package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 12/4/2016.
 */

public class MessageStatusEvent {
    private String old_id = null, new_id;
    private int status;

    public MessageStatusEvent(String old_id, String new_id) {
        this.old_id = old_id;
        this.new_id = new_id;

    }

    public MessageStatusEvent(String new_id, int status) {
        this.new_id = new_id;
        this.status = status;
    }

    public String getOld_id() {
        return old_id;
    }

    public String getNew_id() {
        return new_id;
    }

    public int getStatus() {
        return status;
    }
}
