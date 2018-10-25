package com.rohan7055.earnchat.celgram.events;

/**
 * Created by rohan on 1/12/2018.
 */

public class ChangeGroupSubEvent {
    private String groupid,groupSub;

    public ChangeGroupSubEvent(String groupid, String groupSub) {
        this.groupid = groupid;
        this.groupSub = groupSub;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupSub() {
        return groupSub;
    }

    public void setGroupSub(String groupSub) {
        this.groupSub = groupSub;
    }
}
