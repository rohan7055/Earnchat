package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 12/5/2016.
 */

public class MessageAckEvent {
    private String message_id,receiver;
    public MessageAckEvent(String message_id,String receiver){
        this.message_id=message_id;
        this.receiver=receiver;
    }

    public String getMessage_id() {
        return message_id;
    }

    public String getReceiver() {
        return receiver;
    }
}
