package com.rohan7055.earnchat.celgram.events;

import org.json.JSONObject;

/**
 * Created by manu on 9/22/2016.
 */
public class SendTypingEvent {

    private Boolean isTyping=false;
    private JSONObject object;

    public SendTypingEvent(Boolean isTyping, JSONObject object){
        this.isTyping=isTyping;
        this.object=object;
    }



    public Boolean getIsTyping(){
        return this.isTyping;
    }

    public JSONObject getObject(){
        return this.object;
    }

}
