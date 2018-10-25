package com.rohan7055.earnchat.celgram.events;


import com.rohan7055.earnchat.celgram.Message;

/**
 * Created by rohan on 1/5/2018.
 */

public class AddMemExitEvent {
    private boolean success;
    private String contact_id,group_id;
    private Message message;

    public AddMemExitEvent(boolean success, String contact_id, String group_id, Message message) {
        this.success = success;
        this.contact_id = contact_id;
        this.group_id = group_id;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public AddMemExitEvent(boolean success, String contact_id, String group_id) {
        this.success = success;
        this.contact_id = contact_id;
        this.group_id = group_id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }
}
