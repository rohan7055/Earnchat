package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 10/14/2016.
 */
public class UnsubscribeEvent {
    private String group_id;

    public UnsubscribeEvent(String group_id){
        this.group_id=group_id;

    }

    public String getGroup_id() {
        return group_id;
    }

}
