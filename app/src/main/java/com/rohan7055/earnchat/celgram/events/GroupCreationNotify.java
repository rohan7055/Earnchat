package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 10/17/2016.
 */
public class GroupCreationNotify {
    private String old_id,new_id;
    private int status;

    public GroupCreationNotify(String old_id,String new_id,int status){
        this.old_id=old_id;
        this.new_id=new_id;
        this.status=status;
    }

    public String getOld_id() {
        return old_id;
    }

    public String getNew_id() {
        return new_id;
    }

    public int getStatus() {
        return status;
    }
}
