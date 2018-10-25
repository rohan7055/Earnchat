package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 10/28/2016.
 */
public class AddSingleMemberResponse {
    private boolean success;
    private String contact_id,group_id;


    public AddSingleMemberResponse(boolean success, String contact_id,String group_id){
        this.success=success;
        this.contact_id=contact_id;
        this.group_id=group_id;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getContact_id() {
        return contact_id;
    }

    public String getGroup_id() {
        return group_id;
    }
}
