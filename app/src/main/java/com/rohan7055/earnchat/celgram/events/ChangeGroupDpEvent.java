package com.rohan7055.earnchat.celgram.events;


import com.rohan7055.earnchat.celgram.GroupsModel;

/**
 * Created by ron on 2/18/2017.
 */

public class ChangeGroupDpEvent {
    GroupsModel groupsModel;

    public ChangeGroupDpEvent(GroupsModel groupsModel){
        this.groupsModel=groupsModel;
    }

    public GroupsModel getGroupsModel(){return groupsModel;}
}
