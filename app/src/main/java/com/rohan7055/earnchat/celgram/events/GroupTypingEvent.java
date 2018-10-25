package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 10/21/2016.
 */
public class GroupTypingEvent {
   private String group_id,sender;
    private boolean isTyping;

    public GroupTypingEvent(String group_id,String sender,boolean isTyping){
        this.group_id=group_id;
        this.sender=sender;
        this.isTyping=isTyping;

    }


    public String getGroup_id() {
        return group_id;
    }

    public String getSender() {
        return sender;
    }

    public boolean isTyping() {
        return isTyping;
    }
}
