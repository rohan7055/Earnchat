package com.rohan7055.earnchat.celgram.events;


import com.rohan7055.earnchat.celgram.Message;

/**
 * Created by manu on 10/7/2016.
 */
public class DownloadEvent {

    Message message;
    private boolean error;

    public DownloadEvent(Message message,boolean error){
        this.message=message;
        this.error=error;
    }

    public Message getMessage(){
        return message;
    }

    public boolean isError() {
        return error;
    }
}
