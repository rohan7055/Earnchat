package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 11/30/2016.
 */

public class StickerEvent {
    private String sticker_name,convo_partner;

    public StickerEvent(String sticker_name,String convo_partner){
        this.sticker_name=sticker_name;
        this.convo_partner=convo_partner;
    }

    public String getSticker_name() {
        return sticker_name;
    }

    public String getConvo_partner() {
        return convo_partner;
    }
}
