package com.rohan7055.earnchat.celgram.events;


import com.rohan7055.earnchat.celgram.GroupsModel;

/**
 * Created by manu on 10/13/2016.
 */
public class CreateGroupEvent {
    GroupsModel groupsModel;

    public CreateGroupEvent(GroupsModel groupsModel){
        this.groupsModel=groupsModel;
    }

   public GroupsModel getGroupsModel(){return groupsModel;}
}
