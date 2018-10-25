package com.rohan7055.earnchat.celgram.events;


import com.rohan7055.earnchat.celgram.Message;

/**
 * Created by manu on 10/14/2016.
 */
public class GroupMessage {
    private Message message;

    public GroupMessage(Message message){
        this.message=message;
    }

    public Message getMessage() {
        return message;
    }
}
