package com.rohan7055.earnchat.celgram.DataBase_Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.rohan7055.earnchat.celgram.CelgramUtils;
import com.rohan7055.earnchat.celgram.ContactsModel;
import com.rohan7055.earnchat.celgram.GroupsModel;
import com.rohan7055.earnchat.celgram.ResponseModels.Result8Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by manu on 9/19/2016.
 */
public class ContactSQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION =21;
    private static final String DATABASE_NAME = "contacts_store";

    private static final String TABLE_CONTACTS = "contacts";
    private static final String KEY_ID="id";            //mobile number or group id
    private static final String KEY_UNAME = "username";      //the display name(either the contact name or group name)
    private static final String KEY_CHIKOOP_NAME="chikoop_name";        //The name of the person or group as registered on chikoop
    private static final String KEY_TYPE="contact_type";    //1=single contact,2=group member but not in my contact
    private static final String KEY_DP_UPDATED="dp_update_time";   //time at which latest dp was updated by peer
    private static final String KEY_DISPLAY_NAME="display_name";        //the name of user or group as seen by self

    //Rohan
    private static final String TABLE_MUTE ="muteusers";
    private static final String TABLE_BLOCKED="blockeduser";
    private static final String MUTE_STATUS="mutestatus";//byDefault user is mute
    private static final String BlOCKED_STATUS="blockstatus";//byDefault user is BlOCKED
    private static final String TABLE_GROUPS="groups";
    private static final String KEY_CREATED_BY="created_by";
    private static final String KEY_MEMBERS = "members";
    private static final String KEY_ADMINS="admins";
    private static final String KEY_DP_STATUS="db_update_status";           //0=not updated,1=up to date
    private static final String KEY_GROUP_CREATED="group_created";          //0=creation pending,1=created,2=Exit,3=not created
    private static final String KEY_DP_URL="dp_url";
    private static final String KEY_DATE_CREATED="date_created";
    private static final String KEY_IMAGENAME="imgname";

    private static  final String TABLE_USER_MAP ="usernumberMap";
    private static final String USER_NUM = "number";
    private static final  String USER_ID = "userid";

    private static final String TABLE_LOCAL_PROF ="localprofimage";


    private static final String TABLE_MAGIC_CHAT="magicChattable";
    private static final String USER_MAGIC_NUM = "number";
    private static final  String USER_MAGIC_ID = "userid";






    public ContactSQLiteHelper(Context context) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath()+"/EarnChat/"+DATABASE_NAME, null, DATABASE_VERSION);
        //super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_UNAME + " TEXT,"
                +KEY_CHIKOOP_NAME + " TEXT,"+KEY_TYPE+ " INTEGER,"+ KEY_DP_UPDATED + " TEXT,"+KEY_DISPLAY_NAME+" TEXT,"+KEY_IMAGENAME+" TEXT DEFAULT '')";

        String CREATE_GROUPS_TABLE= " CREATE TABLE "+ TABLE_GROUPS + "("
                + KEY_ID + " TEXT," + KEY_DISPLAY_NAME + " TEXT,"
                +KEY_CREATED_BY + " TEXT,"+KEY_MEMBERS+ " TEXT,"+KEY_ADMINS+" TEXT,"
                +KEY_GROUP_CREATED+" INTEGER,"+KEY_DP_STATUS+" INTEGER,"+KEY_DP_URL +" TEXT,"+KEY_DATE_CREATED+" TEXT)";
        String CREATE_CONTACTS_MUTE="CREATE TABLE "+TABLE_MUTE+"("+KEY_ID+" TEXT PRIMARY KEY, "+MUTE_STATUS+" INTEGER)";
        String CREATE_CONTACTS_BLOCKED="CREATE TABLE "+TABLE_BLOCKED+" ("+KEY_ID+" TEXT PRIMARY KEY, "+BlOCKED_STATUS+" INTEGER )";
        String CREATE_USERID_NUMBER="CREATE TABLE "+TABLE_USER_MAP+"("+USER_NUM+" TEXT PRIMARY KEY,"+USER_ID+" TEXT)";
        String CREATE_LOCAL_PROFILE_IMAGE = "CREATE TABLE "+TABLE_LOCAL_PROF+"("+USER_ID+" TEXT PRIMARY KEY ,"+KEY_IMAGENAME+" TEXT)";
        String CREATE_MAGIC_CONTACTS = "CREATE TABLE "+TABLE_MAGIC_CHAT+"("+USER_MAGIC_NUM+" TEXT PRIMARY KEY)";


        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_GROUPS_TABLE);
        db.execSQL(CREATE_CONTACTS_MUTE);
        db.execSQL(CREATE_CONTACTS_BLOCKED);
        db.execSQL(CREATE_USERID_NUMBER);
        db.execSQL(CREATE_LOCAL_PROFILE_IMAGE);
        db.execSQL(CREATE_MAGIC_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCKED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_MAP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL_PROF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAGIC_CHAT);
        onCreate(db);
    }
    //Rohan Thakur

    public void addUserBlocked(String id)
    {
        Log.i("ContactsBlocked","add userBlocked is called");
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(KEY_ID,id);
        values.put(BlOCKED_STATUS,1);
        db.insert(TABLE_BLOCKED,null,values);
        db.close();
    }


    public void updateMagicContactList(String number)
    {    SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(USER_MAGIC_NUM,number);

        if(!checkuserMagic(number))
        {
            values.put(USER_MAGIC_NUM,number);
            db.insert(TABLE_MAGIC_CHAT,null,values);
            db.close();
        }
        else
        {
            Log.i("magic","User Already Exist");
        }
    }

    public void addUseridMap(HashMap<String,String> useridmap)
    {
        ArrayList<String> userNumber = new ArrayList<>(useridmap.keySet());
        for(int i = 0;i<userNumber.size();i++)
        {
            Log.i("addUserid",userNumber.get(i));
        }
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();
        for(String s :userNumber)
        {
            if(!checkuserMapid(s)) {
                values.put(USER_NUM, s);
                values.put(USER_ID, useridmap.get(s));
                db.insert(TABLE_USER_MAP, null, values);
            }
        }
    }

    public String getUseridMap(String number)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+USER_ID+" FROM "+TABLE_USER_MAP+" WHERE "+USER_NUM+" = '"+number+"'";
        Cursor cursor =db.rawQuery(query,null);
        cursor.moveToFirst();
        String userid = cursor.getString(0);
        if(userid!=null)
        {
            return userid;
        }
        else
            return "";
    }
    public String getimagename(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+KEY_IMAGENAME+" FROM TABLE CONTACT WHERE USERID = '"+id+"'";
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        String imgname = cursor.getString(0);
        if(imgname!=null)
        {
            return imgname;
        }
        else
            return "";
    }
    public boolean checkuserMagic(String number)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query ="SELECT count(*) FROM "+TABLE_MAGIC_CHAT+" WHERE "+USER_MAGIC_NUM+" = '"+number+"'";
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if(count>0)
        {
            return true;
        }
        else return false;
    }
    public boolean checkuserMapid(String number)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT count(*) FROM "+TABLE_USER_MAP+" WHERE "+USER_NUM+"='"+number+"'";
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0)
        {
            return true;
        }
        else return  false;
    }


    public String getUserid(String number)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query ="SELECT "+USER_ID+" FROM "+TABLE_USER_MAP+" WHERE "+USER_NUM+" = '"+number+"'";
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        String userid=null;
        try {
            userid = cursor.getString(0);
        }
        catch (Exception e)
        {
            Log.i("userid get Exception",e.getMessage().toString());
        }
        if(userid!=null)
        {
            Log.i("getUserid",userid);
            return userid;

        }
        return "";
    }



    public void addUserMute(String id)
    {   Log.i("ContactsMute","add userMute is called");
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(KEY_ID,id);
        values.put(MUTE_STATUS,1);
        db.insert(TABLE_MUTE,null,values);
        db.close();
    }

    //group mute

    public void addGroupMute(String id)
    {   Log.i("ContactsMute","add userMute is called");
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(KEY_ID,id);
        values.put(MUTE_STATUS,1);
        db.insert(TABLE_MUTE,null,values);
        db.close();
    }

    public boolean tableEmptyBlocked()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String count ="SELECT count(*) FROM "+TABLE_BLOCKED+"";
        Cursor cursor = db.rawQuery(count,null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0)
        {
            return false;
        }
        else return  true;
    }
//group

    public boolean tableEmpty()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String count ="SELECT count(*) FROM "+TABLE_MUTE+"";
        Cursor cursor = db.rawQuery(count,null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0)
        {
            return false;
        }
        else return  true;
    }

    public boolean checkLocalprofImageEmpty()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM "+TABLE_LOCAL_PROF+"";
        Cursor cursor = db.rawQuery(count,null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0)
        {
            return false;
        }
        else
            return true;
    }
    public void addLocalImage(String userid,String imgname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_ID,userid);
        values.put(KEY_IMAGENAME,imgname);
        db.insert(TABLE_LOCAL_PROF,null,values);
        db.close();

    }

    public void setLocalImage(String userid,String imgname)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        String updatequery = "UPDATE "+TABLE_LOCAL_PROF+" SET "+KEY_IMAGENAME+" = '"+imgname+"' WHERE "+USER_ID+" = '"+userid+"'";
        Cursor cursor = db.rawQuery(updatequery,null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }

    public String getLocalImage(String userid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+KEY_IMAGENAME+" FROM "+TABLE_LOCAL_PROF+" WHERE "+USER_ID+" = '"+userid+"'";
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        String imgname = cursor.getString(0);
        if(imgname!=null)
        {
            return  imgname;
        }
        else  return "";
    }

    public boolean checkLocalUseridExist(String userid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT count(*) FROM "+TABLE_LOCAL_PROF+" WHERE "+USER_ID+" = '"+userid+"'";
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if(count>0)
        {
            cursor.close();
            db.close();
            return true;

        }
        cursor.close();
        db.close();
        return false;



    }

    //group table Empty
    public boolean tableGroupEmpty()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String count ="SELECT count(*) FROM "+TABLE_MUTE+"";
        Cursor cursor = db.rawQuery(count,null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0)
        {
            return false;
        }
        else return  true;
    }
    //Block contacts .Rohan
    public boolean checkBlockedStatus(ContactsModel contactsModel)
    {
        int blockstatus;
        SQLiteDatabase db =this.getWritableDatabase();
        String query = "SELECT "+BlOCKED_STATUS+" FROM "+TABLE_BLOCKED+" WHERE "+KEY_ID+" ='"+contactsModel.getId()+"'";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            Log.i("ContactsBLocked","Check mute query success");
            blockstatus = cursor.getInt(0);
            if (blockstatus==1)
            {Log.i("ContactsBlocked","inner else status 1");
                return true;
            }
            else
            {
                Log.i("ContactsBlocked","inner else status 0");
                return false;
            }
        }
        Log.i("ContactsBlocked","outer false");
        return false;
    }
    //Mute Contact .Rohan
    public boolean checkmuteStatus(ContactsModel contactsModel)
    {
        int mutestatus;
        SQLiteDatabase db =this.getWritableDatabase();
        String query = "SELECT "+MUTE_STATUS+" FROM "+TABLE_MUTE+" WHERE "+KEY_ID+" ='"+contactsModel.getId()+"'";
        Cursor cursor = db.rawQuery(query,null);
        try{
        if(cursor.moveToFirst())
        {
            Log.i("ContactsMute","Check mute query success");
            mutestatus = cursor.getInt(0);
            if (mutestatus==1)
            {Log.i("ContactsMute","inner else status 1");
                return true;
            }
            else
            {
                Log.i("ContactsMute","inner else status 0");
                return false;
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.i("ContactsMute","outer false");
        return false;

    }
    //Group Mute status
    public boolean checkGroupmuteStatus(GroupsModel groupsModel)
    {
        int mutestatus;
        SQLiteDatabase db =this.getWritableDatabase();
        String query = "SELECT "+MUTE_STATUS+" FROM "+TABLE_MUTE+" WHERE "+KEY_ID+" ='"+groupsModel.getId()+"'";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            Log.i("GroupsMute","Check mute query success");
            mutestatus = cursor.getInt(0);
            if (mutestatus==1)
            {Log.i("GroupsMute","inner else status 1");
                return true;
            }
            else
            {
                Log.i("GroupsMute","inner else status 0");
                return false;
            }
        }
        Log.i("GroupsMute","outer false");
        return false;

    }

    public boolean checkBlockedUserExist(String id)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        String query ="SELECT * FROM "+TABLE_BLOCKED+" WHERE "+KEY_ID+" = "+id+"";
        Cursor cursor =db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            Log.i("ContactsBlocked","user exist table true");
            return true;
        }
        else {
            Log.i("ContactBlocked","user donot exist table false");
            return false;
        }
    }

    public boolean checkMuteUserExist(String id)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        String query ="SELECT * FROM "+TABLE_MUTE+" WHERE "+KEY_ID+" = "+id+"";
        Cursor cursor =db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            Log.i("ContactMute","user exist table true");
            return true;
        }
        else {
            Log.i("ContactMute","user donot exist table false");
            return false;
        }
    }

    //group checkMuteuserExist
    public boolean checkMuteGroupExist(String id)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        String query ="SELECT * FROM "+TABLE_MUTE+" WHERE "+KEY_ID+" = '"+id+"'";
        Cursor cursor =db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            Log.i("GroupMute","user exist table true");
            return true;
        }
        else {
            Log.i("GroupMute","user donot exist table false");
            return false;
        }
    }

    public boolean contactBlock(ContactsModel contactsModel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if(tableEmpty())
        {
            addUserBlocked(contactsModel.getId());
        }

        else
        {   Log.i("Contacts Blocked","Block query is called");
            if(checkBlockedUserExist(contactsModel.getId())) {
                String mutequery = "UPDATE " + TABLE_BLOCKED + " SET " + BlOCKED_STATUS + " = 1 WHERE " + KEY_ID + " = '" + contactsModel.getId() + "'";
                Cursor cursor = db.rawQuery(mutequery, null);
                cursor.moveToFirst();
                cursor.close();
                db.close();
                Log.i("ContactBlocked","update Blocked user sucess");
                return true;
            }
            else
            {   Log.i("ContactBlocked","add block user success table not empty");
                addUserBlocked(contactsModel.getId());
                db.close();
                return true;
            }
        }
        return false;
    }

    public boolean contactMute(ContactsModel contactsModel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if(tableEmpty())
        {
            addUserMute(contactsModel.getId());
        }

        else
        {   Log.i("Contacts mute","Mute query is called");
            if(checkMuteUserExist(contactsModel.getId())) {
                String mutequery = "UPDATE " + TABLE_MUTE + " SET " + MUTE_STATUS + " = 1 WHERE " + KEY_ID + " = '" + contactsModel.getId() + "'";
                Cursor cursor = db.rawQuery(mutequery, null);
                cursor.moveToFirst();
                cursor.close();
                db.close();
                Log.i("ContactMute","update mute user sucess");
                return true;
            }
            else
            {   Log.i("ContactMute","add mute user success table not empty");
                addUserMute(contactsModel.getId());
                db.close();
                return true;
            }
        }
        return false;
    }

    //groupmute
    public boolean groupMute(GroupsModel groupsModel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if(tableGroupEmpty())
        {
            addGroupMute(groupsModel.getId());
        }

        else
        {   Log.i("Group mute","Mute query is called");
            if(checkMuteGroupExist(groupsModel.getId())) {
                String mutequery = "UPDATE " + TABLE_MUTE + " SET " + MUTE_STATUS + " = 1 WHERE " + KEY_ID + " = '" + groupsModel.getId() + "'";
                Cursor cursor = db.rawQuery(mutequery, null);
                cursor.moveToFirst();
                cursor.close();
                db.close();
                Log.i("GroupMute","update mute group sucess");
                return true;
            }
            else
            {   Log.i("GroupMute","add mute group success table not empty");
                addGroupMute(groupsModel.getId());
                db.close();
                return true;
            }
        }
        return false;
    }

    public boolean unblockContact(ContactsModel contactsModel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String unMuteQuery ="UPDATE "+TABLE_BLOCKED+" SET "+BlOCKED_STATUS+" = 0 WHERE "+KEY_ID+" = '"+contactsModel.getId()+"'";
        Cursor cursor=db.rawQuery(unMuteQuery,null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
        return false;
    }

    public boolean unmuteContact(ContactsModel contactsModel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String unMuteQuery ="UPDATE "+TABLE_MUTE+" SET "+MUTE_STATUS+" = 0 WHERE "+KEY_ID+" = '"+contactsModel.getId()+"'";
        Cursor cursor=db.rawQuery(unMuteQuery,null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
        return false;
    }
    //group
    public boolean unmuteGroup(GroupsModel groupsModel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String unMuteQuery ="UPDATE "+TABLE_MUTE+" SET "+MUTE_STATUS+" = 0 WHERE "+KEY_ID+" = '"+groupsModel.getId()+"'";
        Cursor cursor=db.rawQuery(unMuteQuery,null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
        return false;
    }




    public void addContact(ContactsModel contactsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("CELGRAM_ADDCONTA","true");
        ContentValues values = new ContentValues();
        values.put(KEY_ID, contactsModel.getId());
        values.put(KEY_UNAME, contactsModel.getUname());
        values.put(KEY_TYPE, contactsModel.getContact_type());
        values.put(KEY_CHIKOOP_NAME,contactsModel.getChikoop_name());
        values.put(KEY_DISPLAY_NAME,contactsModel.getDisplay_name());
        values.put(KEY_DP_UPDATED,contactsModel.getDp_update_time());
        values.put(KEY_IMAGENAME,contactsModel.getImgname());

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    //numberversion
    public void addContactNum(ContactsModel contactsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("CELGRAM_ADDCONTA","true");
        ContentValues values = new ContentValues();
        values.put(KEY_ID, contactsModel.getId());
        values.put(KEY_UNAME, contactsModel.getUname());
        values.put(KEY_TYPE, contactsModel.getContact_type());
        values.put(KEY_CHIKOOP_NAME,contactsModel.getChikoop_name());
        values.put(KEY_DISPLAY_NAME,contactsModel.getDisplay_name());
        values.put(KEY_DP_UPDATED,contactsModel.getDp_update_time());
        values.put(KEY_IMAGENAME,contactsModel.getImgname());

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }


    public void addGroup(GroupsModel groupsModel){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder members=new StringBuilder();
        for(int i=0;i<groupsModel.getMembers().size();i++){
            if(i==groupsModel.getMembers().size()-1){
                members.append(groupsModel.getMembers().get(i));
            }
            else{
                members.append(groupsModel.getMembers().get(i)+",");
            }
        }
        StringBuilder admins=new StringBuilder();
        for(int i=0;i<groupsModel.getAdmins().size();i++){
            if(i==groupsModel.getAdmins().size()-1){
                admins.append(groupsModel.getAdmins().get(i));
            }
            else{
                admins.append(groupsModel.getAdmins().get(i)+",");
            }
        }
        ContentValues values = new ContentValues();
        values.put(KEY_ID, groupsModel.getId());
        Log.i("GROUP_GETID",""+groupsModel.getId());
        values.put(KEY_CREATED_BY, groupsModel.getCreated_by());
        values.put(KEY_DISPLAY_NAME, groupsModel.getDisplay_name());
        values.put(KEY_MEMBERS,String.valueOf(members));
        values.put(KEY_ADMINS,String.valueOf(admins));
        values.put(KEY_GROUP_CREATED,String.valueOf(groupsModel.getCreation_status()));
        values.put(KEY_DP_STATUS,String.valueOf(groupsModel.getDp_status()));
        values.put(KEY_DP_URL,groupsModel.getDp_url());
        values.put(KEY_DATE_CREATED,groupsModel.getCreated_on());
        db.insert(TABLE_GROUPS, null, values);
        db.close();
    }
    public List<ContactsModel> getAllMagicContacts()
    {
        List<ContactsModel> contactList = new ArrayList<ContactsModel>();
        String query ="SELECT * FROM "+TABLE_MAGIC_CHAT+"";
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do
            {
                ContactsModel contactsModel = new ContactsModel();
                contactsModel.setId(cursor.getString(0));
                contactsModel.setDisplay_name(cursor.getString(0));
                contactsModel.setUname("magicUser");
                contactList.add(contactsModel);

            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return contactList;
    }

    public boolean checkmagicContact(String id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String query="SELECT * FROM "+TABLE_CONTACTS+" WHERE "+KEY_ID+"='"+id+"'";
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            Log.i("CHECKMAGIC","USER EXIST");

            return false;
        }
        else
            Log.i("CHECKMAGIC","USER DONOT EXIST");
           /*cursor.close();
        db.close();*/
        return true;

    }


    public void mergeMagicListContact(List<String> listdata)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        for(String id:listdata) {
            if (checkmagicContact(id)) {
                Log.i("merge", "user does exit in table");
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_ID, id);
                contentValues.put(KEY_UNAME, "@magicuser" + id.substring(7, 9));
                contentValues.put(KEY_CHIKOOP_NAME, "chikoopuser" + id);
                contentValues.put(KEY_TYPE, 4);
                db.insert(TABLE_CONTACTS, null, contentValues);
            }
        }

    }
    public List<ContactsModel> getAllContacts(int type) {
        List<ContactsModel> contactList = new ArrayList<ContactsModel>();

        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS +" WHERE "+KEY_TYPE+"="+String.valueOf(type) ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ContactsModel contactsModel=new ContactsModel();
                contactsModel.setId(cursor.getString(0));
                contactsModel.setUname(cursor.getString(1));
                contactsModel.setChikoop_name(cursor.getString(2));
                contactsModel.setContact_type(Integer.parseInt(cursor.getString(3)));
                contactsModel.setDp_update_time(cursor.getString(4));
                contactsModel.setDisplay_name(cursor.getString(5));
                contactsModel.setGroup_member(false);
                String imgname ="";
                try
                {
                    imgname =cursor.getString(6);
                    Log.i("Image Name",imgname);
                }
                catch (Exception e){Log.i("Error setImage",e.getMessage().toString());}
                if(imgname!=null)

                { contactsModel.setImgname(imgname);
                    Log.i("Image Name","setImage is called");
                }
                else
                { contactsModel.setImgname("");}
                contactList.add(contactsModel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return contactList;
    }

    public List<GroupsModel> getAllGroups() {
        List<GroupsModel> groupsList = new ArrayList<GroupsModel>();

        String selectQuery = "SELECT * FROM " + TABLE_GROUPS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Log.i("GROUP_ID",""+cursor.getString(0));

                String[] admins=cursor.getString(4).split(",");
                String[] members=cursor.getString(3).split(",");
                Log.i("GROUP_ID","member "+members[0]);
                GroupsModel groupsModel=new GroupsModel();
                for(String member:members){
                    groupsModel.addMember(member);
                }
                for(String admin:admins){
                    groupsModel.addAdmin(admin);
                }
                groupsModel.setId(cursor.getString(0));
                groupsModel.setDisplay_name(cursor.getString(1));
                groupsModel.setCreated_by(cursor.getString(2));
                groupsModel.setCreation_status(Integer.parseInt(cursor.getString(5)));
                groupsModel.setDp_status(Integer.parseInt(cursor.getString(6)));
                groupsModel.setDp_url(cursor.getString(7));
                groupsModel.setCreated_on(cursor.getString(8));

                groupsList.add(groupsModel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Log.d("Anuraj", groupsList.toString());

        return groupsList;
    }

    public ContactsModel getContact(String id){

        ContactsModel contactsModel=new ContactsModel();

        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS +" WHERE "+KEY_ID+"='"+id+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                contactsModel.setId(cursor.getString(0));
                contactsModel.setUname(cursor.getString(1));
                contactsModel.setChikoop_name(cursor.getString(2));
                contactsModel.setContact_type(Integer.parseInt(cursor.getString(3)));
                contactsModel.setDp_update_time(cursor.getString(4));
                contactsModel.setDisplay_name(cursor.getString(5));
                contactsModel.setImgname(cursor.getString(6));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactsModel;
    }
    public Integer getContactType(String id){


        String selectQuery = "SELECT "+KEY_TYPE+" FROM " + TABLE_CONTACTS +" WHERE "+KEY_ID+"='"+id+"'";
        Integer contact_type=null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                contact_type= Integer.parseInt(cursor.getString(0));


            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contact_type;
    }

    // to check group exist or not
    public boolean checkGroupExist(String id)
    {
        String selectQuery = "SELECT * FROM " + TABLE_GROUPS +" WHERE "+KEY_ID+"='"+id+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

    // to check group exist or not
    public boolean checkContact(String id)
    {
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS +" WHERE "+KEY_ID+"='"+id+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }
    public GroupsModel getGroup(String id){

        GroupsModel groupsModel=new GroupsModel();

        String selectQuery = "SELECT * FROM " + TABLE_GROUPS +" WHERE "+KEY_ID+"='"+id+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String[] admins=cursor.getString(4).split(",");
                String[] members=cursor.getString(3).split(",");
                for(String member:members){
                    groupsModel.addMember(member);
                }
                for(String admin:admins){
                    groupsModel.addAdmin(admin);
                }
                groupsModel.setId(cursor.getString(0));
                groupsModel.setDisplay_name(cursor.getString(1));
                groupsModel.setCreated_by(cursor.getString(2));
                groupsModel.setCreation_status(Integer.parseInt(cursor.getString(5)));
                groupsModel.setDp_status(Integer.parseInt(cursor.getString(6)));
                groupsModel.setDp_url(cursor.getString(7));
                groupsModel.setCreated_on(cursor.getString(8));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return groupsModel;
    }

    public List<String> getIndirectContacts(){
        List<String> contacts=new ArrayList<>();
        String selectQuery="SELECT "+KEY_ADMINS+" FROM "+TABLE_GROUPS;

        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            do {
                String[] members=cursor.getString(0).split(",");
                for(String member:members){
                    contacts.add(member);
                }


            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return contacts;
    }

    public List<String> getMyGroups(){
        List<String> all_groups=new ArrayList<>();
        String selectQuery="SELECT "+KEY_ID+" FROM "+TABLE_GROUPS;

        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            do {

                all_groups.add(cursor.getString(0));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return all_groups;

    }

    public void updateDpTime(String id,String new_time){
        String updateQuery = "UPDATE " + TABLE_CONTACTS +" SET "+KEY_DP_UPDATED+"='"+new_time+"' WHERE "+KEY_ID+"='"+id+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(updateQuery, null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }


    public void updateContactList(HashMap<String,String> number_name){

        String selectQuery= "SELECT "+KEY_ID+" FROM "+TABLE_CONTACTS+" WHERE "+KEY_TYPE+"="+String.valueOf(1);

        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        try {

            if (cursor.moveToFirst()) {
                do {
                    String number = cursor.getString(0);
                    String name = number_name.get(number);
                    if (name == null) {
                        db.delete(TABLE_CONTACTS, KEY_ID + " = ? ",
                                new String[]{number});
                    } else {
                        String checkQuery = "SELECT " + KEY_DISPLAY_NAME + " FROM " + TABLE_CONTACTS + " WHERE " + KEY_ID + "='" + number + "'";
                        Cursor mCursor = db.rawQuery(checkQuery, null);

                        if (mCursor.moveToFirst()) {
                            do {
                                if (number_name.get(number).equals(mCursor.getString(0))) {

                                } else {
                                    String updateQuery = "UPDATE " + TABLE_CONTACTS + " SET " + KEY_DISPLAY_NAME + "='" + number_name.get(number)
                                            + "' WHERE " + KEY_ID + "='" + number + "'";
                                    Cursor m_cursor = db.rawQuery(updateQuery, null);
                                    m_cursor.moveToFirst();
                                    m_cursor.close();
                                }

                            } while (mCursor.moveToNext());
                        }

                        mCursor.close();

                    }

                } while (cursor.moveToNext());
            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }



        db.close();
        cursor.close();


    }


    public boolean updateContact(ContactsModel newContactsModel) {
        boolean need_dp_update = false;
        ContactsModel oldContactModel = getContact(newContactsModel.getId());
        SQLiteDatabase db = this.getWritableDatabase();
        if(oldContactModel.getDp_update_time()!=null)
        {
            if (!newContactsModel.equals(oldContactModel)) {
                if (!newContactsModel.getImgname().equals(oldContactModel.getImgname())) {
                    need_dp_update = true;
                    Log.i("dp","need_dp_update");
                }

                db.delete(TABLE_CONTACTS, KEY_ID + " = ? ",//todo error
                        new String[]{newContactsModel.getId()});
                db.close();

                addContact(newContactsModel);


            } else {
                Log.i("CELGRAM_ELSE","true");
                need_dp_update = false;
            }

        }
        else{

            addContact(newContactsModel);
        }

        return need_dp_update;

    }

    public void addIndirectContacts(List<String> grp_contacts){
        List<String> existing_contacts=new ArrayList<>();

        String selectQuery="SELECT "+KEY_ID+" FROM "+TABLE_CONTACTS;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            do {

                existing_contacts.add(cursor.getString(0));

            } while (cursor.moveToNext());
        }
        cursor.close();

        grp_contacts.removeAll(existing_contacts);

        for(int i=0;i<grp_contacts.size();i++){
            ContentValues values = new ContentValues();
            values.put(KEY_ID, grp_contacts.get(i));
            values.put(KEY_TYPE, 2);

            db.insert(TABLE_CONTACTS, null, values);

        }

        db.close();

    }

    public List<String> getAllNumbers(){
        List<String> numbers=new ArrayList<>();
        String selectQuery="SELECT "+KEY_ID+" FROM "+TABLE_CONTACTS+" WHERE "+KEY_TYPE+"='"+1+"'";

        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            do {

                numbers.add(cursor.getString(0));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();


        return numbers;
    }
    public String getContactName(String id){
        String name=null;
        String getName="SELECT "+KEY_UNAME+" FROM "+TABLE_CONTACTS+" WHERE "+KEY_ID+"='"+id+"'";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(getName,null);
        if(cursor.moveToFirst()){
            do{
                name=cursor.getString(0);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return name;
    }

    public String getChikoopName(String id){
        String name=null;
        String getName="SELECT "+KEY_CHIKOOP_NAME+" FROM "+TABLE_CONTACTS+" WHERE "+KEY_ID+"='"+id+"'";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(getName,null);
        if(cursor.moveToFirst()){
            do{
                name=cursor.getString(0);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return name;
    }

    public List<ContactsModel> getGroupMembers(String group_id){
        List<ContactsModel> group_members=new ArrayList<>();
        String members="";

        String selectQuery="SELECT "+KEY_MEMBERS+" FROM "+TABLE_GROUPS+" WHERE "+KEY_ID+"='"+group_id+"'";

        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                members=cursor.getString(0);
            }while(cursor.moveToNext());
        }
        cursor.close();

        String selectQuery2="SELECT "+KEY_ID+" FROM "+TABLE_CONTACTS+" WHERE "+KEY_ID+" IN("+String.valueOf(members)+")";
        Cursor mCursor=db.rawQuery(selectQuery2,null);

        if(mCursor.moveToFirst()){
            do{
                ContactsModel contactsModel=getContact(mCursor.getString(0));
                group_members.add(contactsModel);
            }while(mCursor.moveToNext());
        }

        return group_members;
    }

    public void groupCreated(String old_id,String new_id){

        Log.i("DATE_CREATED",""+ CelgramUtils.getCurrentTime());
        String updateQuery="UPDATE "+TABLE_GROUPS+" SET "+KEY_ID+"='"+new_id+"',"+KEY_DATE_CREATED+"='"
                + CelgramUtils.getCurrentTime()+"' WHERE "+KEY_ID+"='"+old_id+"'";
        Log.i("UPDATE query",""+updateQuery);
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(updateQuery,null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }

    public void setGroupCreationStatus(String group_id,int status) {
        String updateQuery = "UPDATE " + TABLE_GROUPS+" SET "+KEY_GROUP_CREATED+"="+String.valueOf(status)+" WHERE "+KEY_ID+"='"+group_id+"'";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(updateQuery,null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }

    public void updateGroupAdmin(String groupid,String admin)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery ="UPDATE "+TABLE_GROUPS+" SET "+KEY_ADMINS+" = '"+admin+"' WHERE "+KEY_ID+" = '"+groupid+"'";
        Cursor cursor =db.rawQuery(updateQuery,null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }

    public void updateGroupDp(String groupid,String imagename)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery ="UPDATE "+TABLE_GROUPS+" SET "+KEY_DP_URL+" = '"+imagename+"' WHERE "+KEY_ID+" = '"+groupid+"'";
        Cursor cursor =db.rawQuery(updateQuery,null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }
//update group name


    public void updateGroupName(String groupid,String groupName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery ="UPDATE "+TABLE_GROUPS+" SET "+KEY_DISPLAY_NAME+" = '"+groupName+"' WHERE "+KEY_ID+" = '"+groupid+"'";
        Cursor cursor =db.rawQuery(updateQuery,null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }
    //update group members
    public void updateGroupMembers(String groupid,String members)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery ="UPDATE "+TABLE_GROUPS+" SET "+KEY_MEMBERS+" = '"+members+"' WHERE "+KEY_ID+" = '"+groupid+"'";
        Cursor cursor =db.rawQuery(updateQuery,null);
        cursor.moveToFirst();
        cursor.close();
        db.close();

    }


    public String getgroupdpName(String groupid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query ="SELECT "+KEY_DP_URL+" FROM "+TABLE_GROUPS+" WHERE "+KEY_ID+" = '"+groupid+"'";
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        String imagename = cursor.getString(0);
        if(imagename!=null)
        {
            return  imagename;
        }

        else return "";

    }

    public void newMemberAdded(String group_id,String member){
        String members="";
        String getMembers="SELECT "+KEY_MEMBERS+" FROM "+TABLE_GROUPS+" WHERE "+KEY_ID+"='"+group_id+"'";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(getMembers,null);
        if(cursor.moveToFirst()){
            do{
                members=cursor.getString(0);

            }while(cursor.moveToNext());
        }
        cursor.close();
        members=members+","+member;
        String updateQuery="UPDATE "+TABLE_GROUPS+" SET "+KEY_MEMBERS+"='"+members+"' WHERE "+KEY_ID+"='"+group_id+"'";
        Cursor mCursor=db.rawQuery(updateQuery,null);
        mCursor.moveToFirst();
        mCursor.close();
        db.close();
    }

    public void memberRemoved(String group_id,String member){
        String members="";
        List<String> members_list=new ArrayList<>();
        String getMembers="SELECT "+KEY_MEMBERS+" FROM "+TABLE_GROUPS+" WHERE "+KEY_ID+"='"+group_id+"'";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(getMembers,null);
        if(cursor.moveToFirst()){
            do{
                members=cursor.getString(0);

            }while(cursor.moveToNext());
        }
        cursor.close();

        String[] membersList=members.split(",");
        for(int i=0;i<membersList.length;i++){
            if(!member.equals(membersList[i])){
                members_list.add(membersList[i]);}
        }
        members="";
        for(int i=0;i<members_list.size();i++){
            if(i!=members_list.size()-1){
                members=members+members_list.get(i)+",";
            }
            else{
                members=members+members_list.get(i);
            }
        }

        String updateQuery="UPDATE "+TABLE_GROUPS+" SET "+KEY_MEMBERS+"='"+members+"' WHERE "+KEY_ID+"='"+group_id+"'";
        Cursor mCursor=db.rawQuery(updateQuery,null);
        mCursor.moveToFirst();
        mCursor.close();
        db.close();
    }

}
