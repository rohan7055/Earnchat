package com.rohan7055.earnchat.celgram;

import java.io.Serializable;

/**
 * Created by manu on 6/26/2016.
 */
public class ContactsModel implements Serializable {
    private String uname,id="",chikoop_name,display_name,dp_update_time;
    private Boolean checked=false,isTyping=false,group_member;
    private int contact_type,unseen_msg;
    private Message latest_msg;
    private String  imgname;
    private boolean isImageLoadedOnce;


//Constructor for a normal contact
    public ContactsModel(String id,String uname,String chikoop_name,String display_name,String dp_update_time){
        this.id=id;
        this.uname=uname;
        this.chikoop_name=chikoop_name;
        this.display_name=display_name;
        this.dp_update_time=dp_update_time;
        if(display_name==null){
            this.contact_type=2;
        }
        else {
            this.contact_type = 1;
            this.group_member=false;
        }
        this.isImageLoadedOnce=false;


    }

    public boolean getImageLoadedOnce() {
        return isImageLoadedOnce;
    }

    public void setImageLoadedOnce(boolean imageLoadedOnce) {
        isImageLoadedOnce = imageLoadedOnce;
    }

    public ContactsModel(String id, String uname, String chikoop_name, String display_name, String dp_update_time, String imgname){
        this.id=id;
        this.uname=uname;
        this.chikoop_name=chikoop_name;
        this.display_name=display_name;
        this.dp_update_time=dp_update_time;
        this.imgname=imgname;
        this.isImageLoadedOnce=false;
        if(display_name==null){
            this.contact_type=2;
        }
        else {
            this.contact_type = 1;
            this.group_member=false;
        }
    }

    //Constructor for anonymous sender
    public ContactsModel(String id){
        this.id=id;
        this.display_name=id;
    }


    public ContactsModel(){}

    public String getDisplay_name(){return display_name;}

    public void setDisplay_name(String display_name){this.display_name=display_name;}

    public String getUname(){return this.uname;}

    public void setUname(String uname){
        this.uname=uname;
    }

    public Boolean getChecked(){return this.checked;}

    public void setChecked(Boolean checked){
        this.checked=checked;
    }

    public String getId(){return this.id;}

    public void setId(String id){this.id=id;}

    public void setIsTyping(Boolean isTyping){
        this.isTyping=isTyping;
    }

    public Boolean getIsTyping(){
        return this.isTyping;
    }

    public int getContact_type(){
        return this.contact_type;
    }

    public void setContact_type(int contact_type){
        this.contact_type=contact_type;
    }

    public void setChikoop_name(String chikoop_name){
        this.chikoop_name=chikoop_name;
    }

    public String getChikoop_name(){
        return this.chikoop_name;
    }

    public String getImageUrl(){
        String url="http://128.199.198.1/old/20.png";
        return url;
    }

    public Message getLatest_msg(){return latest_msg;}

    public void setLatest_msg(Message latest_msg){this.latest_msg=latest_msg;}

    public String getDp_update_time(){return dp_update_time;}

    public void setImgname (String imgname){this.imgname=imgname;}

    public String getImgname(){return this.imgname;}

    public void setDp_update_time(String dp_update_time){this.dp_update_time=dp_update_time;}

    public int getUnseen_msg() {
        return unseen_msg;
    }

    public void setUnseen_msg(int unseen_msg) {
        this.unseen_msg = unseen_msg;
    }

    public Boolean getGroup_member() {
        return group_member;
    }

    public void setGroup_member(Boolean group_member) {
        this.group_member = group_member;
    }
}

