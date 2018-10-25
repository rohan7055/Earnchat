package com.rohan7055.earnchat.celgram;

import android.graphics.Bitmap;

/**
 * Created by manu on 7/24/2016.
 */
public class Media {
    private String fromName, message,time_stamp;
    private boolean isSelf;
    private Bitmap bitmap;
    private String file_path;
    private int media_type;  //0->mp3,1->image,2->video,3->location.

    public Media() {
    }

    public Media(String fromName, String message, Bitmap bitmap,String file_path,int media_type, boolean isSelf) {
        this.fromName = fromName;
        this.message = message;
        this.bitmap=bitmap;
        this.file_path=file_path;
        this.media_type=media_type;
        this.isSelf = isSelf;
    }

    public String getFromName() {
        return fromName;
    }

    public String getTime_stamp(){return this.time_stamp;}

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public int getMedia_type() {
        return media_type;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

}