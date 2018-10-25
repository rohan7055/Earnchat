package com.rohan7055.earnchat.celgram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by manu on 10/13/2016.
 */
public class GroupsModel implements Serializable {

    private String id,created_by,display_name,typing=null,dp_url,created_on;
    private List<String> members=new ArrayList<>(),admins=new ArrayList<>();
    private int dp_status,creation_status,unseen_msg;
    private Message latest_msg;
    private boolean isTyping=false;


    public GroupsModel(String created_by,String display_name,List<String> members,List<String> admins,String dp_url,int dp_status){
        this.id=String.valueOf(new Random().nextInt(9999)-1)+System.currentTimeMillis();
        this.created_by=created_by;
        this.display_name=display_name;
        this.members=members;
        this.admins=admins;
        this.dp_url=dp_url;
        this.dp_status=dp_status;

    }

    public GroupsModel(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }

    public int getDp_status() {
        return dp_status;
    }

    public void setDp_status(int dp_status) {
        this.dp_status = dp_status;
    }

    public Message getLatest_msg() {
        return latest_msg;
    }

    public void setLatest_msg(Message latest_msg) {
        this.latest_msg = latest_msg;
    }


    public int getCreation_status() {
        return creation_status;
    }

    public void setCreation_status(int creation_status) {
        this.creation_status = creation_status;
    }

    public void addMember(String member){this.members.add(member);}

    public void addAdmin(String admin){this.admins.add(admin);}

    public int getUnseen_msg() {
        return unseen_msg;
    }

    public void setUnseen_msg(int unseen_msg) {
        this.unseen_msg = unseen_msg;
    }

    public String getTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }

    public void setIsTyping(boolean isTyping){
        this.isTyping=isTyping;
    }

    public boolean getIsTyping(){
        return isTyping;
    }

    public String getDp_url() {
        //String dp_url="http://128.199.198.1/old/20.png";

        return dp_url;
    }

    public void setDp_url(String dp_url) {
        this.dp_url = dp_url;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }
}
