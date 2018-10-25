package com.rohan7055.earnchat.celgram.events;

/**
 * Created by rohan on 1/5/2018.
 */

public class RemoveMemberEvent {
    String group_id,member;

    public RemoveMemberEvent(String group_id, String member) {
        this.group_id = group_id;
        this.member = member;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}
