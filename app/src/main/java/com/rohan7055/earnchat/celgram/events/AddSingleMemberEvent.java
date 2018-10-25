package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 10/28/2016.
 */
public class AddSingleMemberEvent {
    private String group_id,member;

    public AddSingleMemberEvent(String group_id, String member){
        this.group_id=group_id;
        this.member=member;
    }

    public String getGroup_id() {
        return group_id;
    }

    public String getMember() {
        return member;
    }
}
