package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 1/6/2017.
 */

public class ProgressEvent {
    private String msg_id;
    private int progress;

    public ProgressEvent(String msg_id,int progress){
        this.msg_id=msg_id;
        this.progress=progress;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public int getProgress() {
        return progress;
    }
}
