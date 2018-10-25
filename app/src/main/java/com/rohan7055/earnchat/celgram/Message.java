package com.rohan7055.earnchat.celgram;

import android.util.Log;
import com.rohan7055.earnchat.CelgramMain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by manu on 7/16/2016.
 */
public class Message implements Serializable {
    private String id,convo_partner,sender_id,mime_type,msg_url,timestamp,data,file_path,thumb_path
            ,server_receipt,device_receipt,device_seen,duration;
    private int msg_status,convo_type,msg_type,media_size,aws_id=-1,progress;
    private boolean isSelf;
    private Date date=new Date();
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Message() {
    }

    //for one to one message
    public Message(String convo_partner,boolean isSelf, int msg_type,String mime_type,
                   String timestamp,int media_size,String data,String file_path,String duration) {


        this.id=String.valueOf(new Random().nextInt(9999)-1)+System.currentTimeMillis();
        this.convo_partner=convo_partner;
        this.convo_type=0;
        this.sender_id=convo_partner;
        this.isSelf=isSelf;
        this.msg_type=msg_type;
        this.mime_type=mime_type;
        this.timestamp=timestamp;
        this.media_size=media_size;
        this.data=data;
        this.file_path=file_path;
        this.duration=duration;
        this.msg_status=0;
    }

    public Message(String convo_partner,boolean isSelf, int msg_type,String mime_type,
                   int media_size,String data,String file_path,String duration) {



        this.id=String.valueOf(new Random().nextInt(9999)-1)+System.currentTimeMillis();
        this.convo_partner=convo_partner;
        this.convo_type=0;
        this.sender_id=convo_partner;
        this.isSelf=isSelf;
        this.msg_type=msg_type;
        this.mime_type=mime_type;
        this.timestamp=df.format(c.getTime());
        this.media_size=media_size;
        this.data=data;
        this.file_path=file_path;
        this.duration=duration;
        this.msg_status=0;
    }

    //for group message
    public Message(String convo_partner, String sender_id,boolean isSelf, int msg_type,String mime_type,
                   String timestamp,int media_size,String data,String file_path,String duration) {

        this.id=String.valueOf(new Random().nextInt(9999)-1)+System.currentTimeMillis();
        this.convo_partner=convo_partner;
        this.convo_type=1;
        this.sender_id=sender_id;
        this.isSelf=isSelf;
        this.msg_type=msg_type;
        this.mime_type=mime_type;
        this.timestamp=timestamp;
        this.media_size=media_size;
        this.data=data;
        this.file_path=file_path;
        this.msg_status=0;
        this.duration=duration;
    }

    public Message(String convo_partner, String sender_id,boolean isSelf, int msg_type,String mime_type,
                   int media_size,String data,String file_path,String duration) {

        this.id=String.valueOf(new Random().nextInt(9999)-1)+System.currentTimeMillis();
        this.convo_partner=convo_partner;
        this.convo_type=1;
        this.sender_id=sender_id;
        this.isSelf=isSelf;
        this.msg_type=msg_type;
        this.mime_type=mime_type;
        this.timestamp=df.format(c.getTime());
        Log.i("TIMESTAMP",""+this.timestamp);
        this.media_size=media_size;
        this.data=data;
        this.file_path=file_path;
        this.msg_status=0;
        this.duration=duration;
    }


    public String getId(){return id;}

    public void setId(String id){this.id=id;}

    public String getConvo_partner() {
        return this.convo_partner;
    }

    public void setConvo_partner(String convo_partner) {
        this.convo_partner = convo_partner;
    }

    public String getSender_id() {
        return this.sender_id;
    }

    public void setSender_id(String sender_id){this.sender_id=sender_id;}

    public String getMime_type(){return this.mime_type;}

    public void setMime_type(String mime_type){this.mime_type=mime_type;}

    public void setMsg_url(String msg_url) {
        this.msg_url = msg_url;
    }

    public String getMsg_url() {
        return this.msg_url;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        if(timestamp!=null) {
            return timestamp;
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.toString();
    }

    public String getData(){return data;}

    public void setData(String data){this.data=data;}

    public String getFile_path(){
        return this.file_path;
    }

    public void setFile_path(String file_path){
        this.file_path=file_path;
    }

    public String getThumb_path(){
        return this.thumb_path;
    }

    public void setThumb_path(String thumb_path){
        this.thumb_path=thumb_path;
    }

    public String getServer_receipt(){return this.server_receipt;}

    public void setServer_receipt(String server_receipt){this.server_receipt=server_receipt;}

    public String getDevice_receipt(){return this.device_receipt;}

    public void setDevice_receipt(String device_receipt){this.device_receipt=device_receipt;}

    public String getDevice_seen(){return this.device_seen;}

    public void setDevice_seen(String device_seen){this.device_seen=device_seen;}

    public int getMsg_status(){return this.msg_status;}

    public void setMsg_status(int msg_status) {
        this.msg_status = msg_status;
    }

    public int getConvo_type(){return convo_type;}

    public void setConvo_type(int convo_type){this.convo_type=convo_type;}

    public int getMsg_type(){return msg_type;}

    public void setMsg_type(int msg_type){this.msg_type=msg_type;}

    public int getMedia_size(){return media_size;}

    public void setMedia_size(int media_size){this.media_size=media_size;}

    public boolean isSelf(){return isSelf;}

    public void setSelf(boolean isSelf){this.isSelf=isSelf;}

    public String getDuration(){return duration;}

    public void setDuration(String duration){this.duration=duration;}

    public String getDate(){
        Calendar c= Calendar.getInstance();
        Date date=new Date(),msg_date=new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy"),format=new SimpleDateFormat("yyyy-MM-dd");;
        try {
            date =df.parse(df.format(c.getTime()));
            Log.i("MARKER>>","date="+date.toString());
            if(getTimestamp()!=null){
            msg_date=format.parse(getTimestamp().substring(0,10));}
            else{
                return "";
            }
            Log.i("MARKER>>","msg_date="+msg_date.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

       if(date.equals(msg_date)){
           return getTimestamp().substring(11,16);
       }
        else{
           c.add(Calendar.DATE,-1);
           try {
               date =df.parse(df.format(c.getTime()));
           } catch (ParseException e) {
               e.printStackTrace();
           }
           if(date.equals(msg_date)){
               return "yesterday";
           }
           else{
               return df.format(msg_date);
           }

       }
    }


    public String getNotificationBody(){
        String sender,receiver,body;
        sender=(CelgramMain.number_name.get(sender_id)!=null? CelgramMain.number_name.get(sender_id):sender_id);
        receiver=(CelgramMain.number_name.get(data)!=null? CelgramMain.number_name.get(data):data);

        Log.i("CHECK_DATA",data);
        Log.i("CHECK_RECEIVER",receiver);

        switch(getMsg_type()){
            case 7:body=sender+" added "+receiver;
                break;
            case 8:body=sender+" removed "+receiver;
                break;
            case 9:body=sender+" changed the group subject to '"+data+"'";
                break;
            case 10:body=sender+" changed the group icon";
                break;
            case 11:body=sender+" left the group";
                break;
            case 12:body=sender+" created group '"+data+"'";
                break;
            default:body="";
                break;

        }

        return body;
    }


    public int getAws_id() {
        return aws_id;
    }

    public void setAws_id(int aws_id) {
        this.aws_id = aws_id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

}