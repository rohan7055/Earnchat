package com.rohan7055.earnchat.celgram.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetGroupWrapper {

    @SerializedName("session")
    @Expose
    private String session;

    @SerializedName("groupId")
    @Expose
    private String groupId;

    public GetGroupWrapper(String session, String groupId) {
        this.session = session;
        this.groupId = groupId;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}

