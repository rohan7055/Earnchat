package com.rohan7055.earnchat.celgram.events;

/**
 * Created by rohan on 1/12/2018.
 */

public class GroupNameChangeResponse {
    private boolean isSucces;
    private String groupname;
    private String groupId;


    public GroupNameChangeResponse(boolean isSucces, String groupname, String groupId) {
        this.isSucces = isSucces;
        this.groupname = groupname;
        this.groupId = groupId;
    }

    public GroupNameChangeResponse(boolean isSucces) {
        this.isSucces = isSucces;
    }

    public GroupNameChangeResponse(boolean isSucces, String groupname) {
        this.isSucces = isSucces;
        this.groupname = groupname;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isSucces() {
        return isSucces;
    }

    public void setSucces(boolean succes) {
        isSucces = succes;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }
}
