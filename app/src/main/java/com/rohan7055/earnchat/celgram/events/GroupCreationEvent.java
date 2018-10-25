package com.rohan7055.earnchat.celgram.events;

/**
 * Created by rohan on 1/5/2018.
 */

public class GroupCreationEvent {
    private int status;
    private String groupId;

    public GroupCreationEvent(int status, String groupId) {
        this.status = status;
        this.groupId = groupId;
    }

    public int getStatus() {
        return status;
    }

    public String getGroupId() {
        return groupId;
    }
}
