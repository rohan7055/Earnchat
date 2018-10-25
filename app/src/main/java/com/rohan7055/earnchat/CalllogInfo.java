package com.rohan7055.earnchat;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Taranjyot on 8/10/2016.
 */
public class CalllogInfo {

    private Long id;
    private String name;
    private String number;
    private String calltype;
    private String callDate;
    private Date callDayTime;
    private String duration;
    private String image;
    private ArrayList<CalllogInfo> callLog;


    public CalllogInfo(){
        callLog = new ArrayList<>();
    }

    public ArrayList<CalllogInfo> getCallLog() {
        return callLog;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCallLog(CalllogInfo callLog1) {
        callLog.add(callLog1);
    }

    public int getCallLogSize(){
        return callLog.size();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public Date getCallDayTime() {
        return callDayTime;
    }

    public void setCallDayTime(Date callDayTime) {
        this.callDayTime = callDayTime;
    }
}
