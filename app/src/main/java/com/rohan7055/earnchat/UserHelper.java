package com.rohan7055.earnchat;

import com.rohan7055.earnchat.Model.UserModel;
import com.rohan7055.earnchat.Model.UserModel8Chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

public class UserHelper {

    private static String username;
    private static String id;
    private static String firstname;
    private static String lastname;
    private static String session;
    private static String imgname;
    private static boolean once;
    private static String status;


    public static void saveUser(UserModel user, Context ctx){
        ctx.getSharedPreferences("USER", ctx.MODE_PRIVATE).edit()
                .putString("id", user.getUserid())
                .putString("firstname", user.getFirstname())
                .putString("lastname", user.getLastname())
                .putString("username", user.getUsername())
                .putString("session", user.getSession())
                .putString("imgname", user.getImgname())
                .putString("status",user.getstatus())
                .putBoolean("once",false).apply();

    }
    public static void saveUser8chat(UserModel8Chat user, Context ctx, String type){

        /*Log.d("id", user.getMobile());
        Log.d("mobile", user.getMobile());
        Log.d("firstname", user.getFirstname());
        Log.d("lastname", user.getLastname());
        Log.d("username", user.getUsername());
        Log.d("imagename", user.getImgname());*/

        ctx.getSharedPreferences("USER", ctx.MODE_PRIVATE).edit()
                .putString("id", user.getMobile())
                .putString("email", user.getEmail())
                .putString("mobile", user.getMobile())
                .putString("firstname", user.getFirstname())
                .putString("lastname", user.getLastname())
                .putString("username", type+" (" + user.getCompany() + ")")
                .putString("session", user.getSession())
                .putString("brand", user.getCompany())
                .putBoolean("once", true)
                .putString("imgname", user.getImgname()).apply();
    }

    public static void loadUser(Context ctx){
        SharedPreferences preferences= ctx.getSharedPreferences("USER", ctx.MODE_PRIVATE);
        UserHelper.id = preferences
                .getString("id", "");
        UserHelper.firstname = preferences
                .getString("firstname", "");
        UserHelper.lastname = preferences
                .getString("lastname", "");
        UserHelper.username = preferences
                .getString("username", "");
        UserHelper.session = preferences
                .getString("session", "");
        UserHelper.imgname = preferences
                .getString("imgname", "");
        UserHelper.once=preferences
                .getBoolean("once",false);

        UserHelper.status=preferences
                .getString("status","Hey I am Using EarnChat!!!");
    }

    public static void logout(Context ctx){
        UserModel userModel = new UserModel();
        saveUser(userModel, ctx);

        SharedPreferences preferences= ctx.getSharedPreferences("requests", ctx.MODE_PRIVATE);
        preferences.edit().clear().apply();

        SharedPreferences preferences2= ctx.getSharedPreferences("pending", ctx.MODE_PRIVATE);
        preferences2.edit().clear().apply();

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EarnChat/contacts_store";
        SQLiteDatabase.deleteDatabase(new File(path));

        String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EarnChat/message_store";
        SQLiteDatabase.deleteDatabase(new File(path2));
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        UserHelper.status = status;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserHelper.username = username;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        UserHelper.id = id;
    }


    public static String getFirstName() {
        return firstname;
    }

    public static void setFirstname(String firstname) {
        UserHelper.firstname = firstname;
    }

    public static String getLastname() {
        return lastname;
    }

    public static void setLastname(String lastname) {
        UserHelper.lastname = lastname;
    }

    public static String getSession() {
        return session;
    }

    public static void setSession(String session) {
        UserHelper.session = session;
    }

    public static String getImgname() {
        return imgname;
    }

    public static String getFirstname() {
        return firstname;
    }

    public static void setImgname(String imgname) {
        UserHelper.imgname = imgname;
    }

    public static boolean isOnce() {
        return once;
    }

    public static void setOnce(boolean once) {
        UserHelper.once = once;
    }
}
