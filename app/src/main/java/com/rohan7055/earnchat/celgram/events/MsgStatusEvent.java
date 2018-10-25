package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 10/6/2016.
 */
public class MsgStatusEvent {

    private String convo_partner;
    private int status;

    public MsgStatusEvent(String convo_partner,int status){
        this.convo_partner=convo_partner;
        this.status=status;
    }

    public String getConvo_partner(){
        return convo_partner;
    }

    public int getStatus(){
        return status;
    }
}
