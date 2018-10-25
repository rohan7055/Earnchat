package com.rohan7055.earnchat.celgram;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lenovo on 9/15/2017.
 */

public class group_dp_model {

    @SerializedName("title")
    String title;

    @SerializedName("image")
    String image;

    @SerializedName("imgname")
     String imageName;

    @SerializedName("status")
    boolean status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
