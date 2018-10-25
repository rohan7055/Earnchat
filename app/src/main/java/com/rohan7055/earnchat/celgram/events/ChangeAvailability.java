package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 9/27/2016.
 */
public class ChangeAvailability {

    private Boolean available;
    private String lastseen;

    public ChangeAvailability(Boolean available){
        this.available=available;
    }
    //last seen api mod
    public ChangeAvailability(Boolean available,String lastseen){
        this.available=available;
        this.lastseen=lastseen;
    }

    public Boolean getAvailable(){
        return this.available;
    }

    public String getLastseen() {
        return lastseen;
    }
}

