package com.rohan7055.earnchat.celgram.events;

/**
 * Created by rohan on 1/5/2018.
 */

public class RemoveMemResponse {
    private boolean isRemoved;
    private String member,groupId;

    public RemoveMemResponse(Boolean isRemoved, String member, String groupId) {
        this.isRemoved = isRemoved;
        this.member = member;
        this.groupId = groupId;
    }

    public String getMember() {
        return member;
    }

    public String getGroupId() {
        return groupId;
    }

    public boolean isRemoved() {
        return isRemoved;
    }
}
