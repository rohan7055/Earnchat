package com.rohan7055.earnchat.celgram.events;


import com.rohan7055.earnchat.celgram.Message;

/**
 * Created by rohan on 1/5/2018.
 */

public class RemRecvResponseEvent
{
    private boolean isRemoved;
    private Message message;
    private String groupId;

    public RemRecvResponseEvent(boolean isRemoved, Message message, String groupId) {
        this.isRemoved = isRemoved;
        this.message = message;
        this.groupId = groupId;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public Message getMessage() {
        return message;
    }

    public String getGroupId() {
        return groupId;
    }
}
