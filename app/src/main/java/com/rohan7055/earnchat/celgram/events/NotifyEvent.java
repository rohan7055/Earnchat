package com.rohan7055.earnchat.celgram.events;

/**
 * Created by ron on 4/9/2017.
 */

public class NotifyEvent {
     String groupid;
    public NotifyEvent(String groupid)
    {
        this.groupid=groupid;
    }
    public String getGroupid(){return this.groupid;}

}
