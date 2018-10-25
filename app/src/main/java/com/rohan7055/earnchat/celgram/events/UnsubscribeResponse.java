package com.rohan7055.earnchat.celgram.events;


import com.rohan7055.earnchat.celgram.Message;

/**
 * Created by manu on 10/23/2016.
 */
public class UnsubscribeResponse {
   private boolean isUnsubscibed;
    private Message message;

    public UnsubscribeResponse(boolean isUnsubscibed, Message message) {
        this.isUnsubscibed = isUnsubscibed;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public UnsubscribeResponse(boolean isUnsubscibed){
        this.isUnsubscibed=isUnsubscibed;
    }

    public boolean isUnsubscibed() {
        return isUnsubscibed;
    }

}
