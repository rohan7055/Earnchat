package com.rohan7055.earnchat.celgram.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by manu on 8/28/2016.
 *
 *
 *
 */

/*{
        "status": true,
        "result": {
        "user_id": "7055743",
        "username": "rohan7055",
        "firstname": "Rohan",
        "lastname": "Thakur",
        "status": null,
        "imgname": "70557433KE5vqb0X.jpg",
        "groups_sub": null,
        "created_at": "2018-07-14T00:50:32.000Z",
        "lastseen": "2018-07-13T19:20:32.000Z",
        "session": "xavsgdahuysas",
        "db_update_time": null
        }
        }*/
public class Result {

    @SerializedName("user_id")
    @Expose
    private String userid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("lastname")
    @Expose
    private String lastname;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("imgname")
    @Expose
    private String imgname;


    @SerializedName("groups_sub")
    @Expose
    private String groups_sub;

    @SerializedName("session")
    @Expose
    private String session;

    @SerializedName("db_update_time")
    @Expose
    private String db_update_time;


    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The imgname
     */
    public String getImgname() {
        return imgname;
    }

    /**
     *
     * @param imgname
     * The imgname
     */
    public void setImgname(String imgname) {
        this.imgname = imgname;
    }

    /**
     *
     * @return
     * The firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     *
     * @param firstname
     * The firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }


    /**
     *
     * @return
     * The lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     *
     * @param lastname
     * The lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     *
     * @return
     * The db_update_time
     */
    public String getDb_update_time() {
        return db_update_time;
    }

    /**
     *
     * @param db_update_time
     * The db_update_time
     */
    public void setDb_update_time(String db_update_time) {
        this.db_update_time = db_update_time;
    }

    public String getChikoopName(){
        return firstname+" "+lastname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }



}
