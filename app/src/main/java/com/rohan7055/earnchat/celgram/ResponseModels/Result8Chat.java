package com.rohan7055.earnchat.celgram.ResponseModels;

import com.rohan7055.earnchat.celgram.Message;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rohan on 8/13/2017.
 */

public class Result8Chat {
    @SerializedName("id")
    @Expose
    private String userid;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("lname")
    @Expose
    private String lname;

    @SerializedName("fname")
    @Expose
    private String fname;

    @SerializedName("first_name")
    @Expose
    private String firstname;

    @SerializedName("last_name")
    @Expose
    private String lastname;
    @SerializedName("brand_name")
    @Expose
    private String brandname;

    @SerializedName("company_name")
    @Expose
    private String companyname;

    @SerializedName("mobile_no")
    @Expose
    private String number;

    @SerializedName("cover_image_name")
    @Expose
    private String coverimagename;

    @SerializedName("logo_image_name")
    @Expose
    private String logoImagename;

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("date_time")
    @Expose
    private String datetime;

    private Boolean checked=false,isTyping=false,group_member;
    private int contact_type,unseen_msg;
    private Message latest_msg;

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getTyping() {
        return isTyping;
    }

    public void setTyping(Boolean typing) {
        isTyping = typing;
    }

    public Boolean getGroup_member() {
        return group_member;
    }

    public void setGroup_member(Boolean group_member) {
        this.group_member = group_member;
    }

    public int getContact_type() {
        return contact_type;
    }

    public void setContact_type(int contact_type) {
        this.contact_type = contact_type;
    }

    public int getUnseen_msg() {
        return unseen_msg;
    }

    public void setUnseen_msg(int unseen_msg) {
        this.unseen_msg = unseen_msg;
    }

    public Message getLatest_msg() {
        return latest_msg;
    }

    public void setLatest_msg(Message latest_msg) {
        this.latest_msg = latest_msg;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    private Boolean status=true;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCoverimagename() {
        return coverimagename;
    }

    public void setCoverimagename(String coverimagename) {
        this.coverimagename = coverimagename;
    }

    public String getLogoImagename() {
        return logoImagename;
    }

    public void setLogoImagename(String logoImagename) {
        this.logoImagename = logoImagename;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }
}
