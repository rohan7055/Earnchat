package com.rohan7055.earnchat.celgram.events;


import com.rohan7055.earnchat.celgram.Message;

/**
 * Created by manu on 9/25/2016.
 */
public class MessageEvent {
    Message message;

    public MessageEvent(Message message){
        this.message=message;
    }

    public Message getMessage(){
        return this.message;
    }
}
