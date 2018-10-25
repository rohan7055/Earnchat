package com.rohan7055.earnchat.celgram.events;

/**
 * Created by rohan on 1/2/2018.
 */

public class GroupExitEvent {
    private String groupId,sender,timestamp;

    public GroupExitEvent(String groupId, String sender,String timestamp) {
        this.groupId = groupId;
        this.sender = sender;
        this.timestamp=timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
