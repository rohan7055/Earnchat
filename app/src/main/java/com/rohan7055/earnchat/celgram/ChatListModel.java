package com.rohan7055.earnchat.celgram;

/**
 * Created by manu on 10/8/2016.
 */
public class ChatListModel {
    private String convo_partner,last_msg;

    public ChatListModel(String convo_partner,String last_msg){
        this.convo_partner=convo_partner;
        this.last_msg=last_msg;
    }

    public ChatListModel(){}

    public String getConvo_partner(){return convo_partner;}

    public void setConvo_partner(String convo_partner){this.convo_partner=convo_partner;}

    public String getLast_msg(){return last_msg;}

    public void setLast_msg(String last_msg){this.last_msg=last_msg;}
}
