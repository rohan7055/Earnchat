package com.rohan7055.earnchat.celgram.events;


import com.rohan7055.earnchat.celgram.GroupsModel;
import com.rohan7055.earnchat.celgram.Message;

/**
 * Created by manu on 10/19/2016.
 */
public class GroupMessageEvent {
    private Message message;
    //Rohan Thakur
    private GroupsModel groupsModel;
    public GroupMessageEvent(Message message,GroupsModel groupsModel){
        this.message=message;
        this.groupsModel=groupsModel;
    }

    public Message getMessage() {
        return message;
    }
    //Rohan Thakur
    public GroupsModel getGroupsModel(){return  groupsModel;}
}

