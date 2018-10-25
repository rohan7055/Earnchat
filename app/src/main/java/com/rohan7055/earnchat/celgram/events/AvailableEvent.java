package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 9/22/2016.
 */
public class AvailableEvent {

   // private Boolean to_service;
    private String uname;

    public AvailableEvent(String uname){

        this.uname=uname;
    }

//    public Boolean getTo_service(){
//        return this.to_service;
//    }

    public String getUname(){
        return this.uname;
    }
}
