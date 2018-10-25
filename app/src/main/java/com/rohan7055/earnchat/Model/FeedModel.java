package com.rohan7055.earnchat.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public  class FeedModel {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("uploader_id")
    @Expose
    private String uploader_id;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("tags")
    @Expose
    private String tags;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("color")
    @Expose
    private String color;

    @SerializedName("datetime")
    @Expose
    private String datetime;

    @SerializedName("modified_on")
    @Expose
    private String modified_on;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("file_name")
    @Expose
    private String file_name;

    @SerializedName("file_type")
    @Expose
    private String file_type;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("lastname")
    @Expose
    private String lastname;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("imgname")
    @Expose
    private String imgname;


    @SerializedName("likescount")
    @Expose
    public int likesCount;

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLiked = false;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

//    public FeedModel()
//    {
//        System.out.println("Name: " + firstname + " " + lastname);
//    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The uploader_id
     */
    public String getUploader_id() {
        return uploader_id;
    }

    /**
     * @param uploader_id The id
     */
    public void setUploader_id(String uploader_id) {
        this.uploader_id = uploader_id;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The id
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * @param tags The id
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The id
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color The id
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return The datetime
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime The id
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * @return The modified_on
     */
    public String getModified_on() {
        return modified_on;
    }

    /**
     * @param modified_on The id
     */
    public void setModified_on(String modified_on) {
        this.modified_on = modified_on;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
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

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImgname() {
        return imgname;
    }

    public void setImgname(String imgname) {
        this.imgname = imgname;
    }
}
