package com.rohan7055.earnchat.celgram.DataBase_Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.rohan7055.earnchat.celgram.ChatListModel;
import com.rohan7055.earnchat.celgram.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manu on 9/12/2016.
 */
public class MessageSQLiteHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 26;


    private static final String DATABASE_NAME = "message_store";
    private static final String TABLE_MESSAGE = "messages";
    private static final String TABLE_CHATS = "chat_list";

    //Columns for table_message
    private static final String KEY_ID="_id";
    private static final String KEY_CONVERSATION_PARTNER = "conversation_partner";      //the id of group or user with whom message is exchanged
    private static final String KEY_CONVERSATION_TYPE="conversation_type";      //0=individual,1=group
    private static final String KEY_SENDER="sender";                            //same as convo partner for individual&id of sender in case of group
    private static final String KEY_ROLE = "role";        //0=outgoing message,1=incoming message
    private static final String KEY_MSG_TYPE="msg_type";  //0=text,1=image,2=video,3=audio,4=pdf,.doc,5=contact card,6=sticker,7=group notification(added)
    //8=grp not(removed),9=grp not(name changed),10=grp not(dp changed),11=grp not(left),12=You created,13=location
    private static final String KEY_MIME_TYPE="mime_type"; //mime type for msg_type=1,2,3,4
    private static final String KEY_MSG_URL="media_url";      //url of the file on the server
    private static final String KEY_TIMESTAMP="timestamp";    //local time of device
    private static final String KEY_SIZE="media_size";
    private static final String KEY_DATA="data";           //text data for msg type=0,else captions for other msg_type
    private static final String KEY_STATUS = "msg_status";     //FOR outgoing,0=pending,1=sent to server,2=delivered,3=seen,-2=thumbnail being uploaded(only for image and video),
    //-1=media being uploaded
    //FOR incoming -2=media being downloaded,-1=thumbnail being downloaded,0=received,1=seen and ack sent,2=seen but ack not sent
    //COMMON 4=aws transaction stopped

    private static final String KEY_AWS_ID = "aws_id";      //transaction id for aws upload or download
    private static final String KEY_FILE="file_path";     //path to file if msg_type=1,2,3,4
    private static final String KEY_THUMB="thumbnail_path"; //path to media thumbnail
    private static final String KEY_SERVER_RECEIPT="server_receipt";    //time at which msg reached server
    private static final String KEY_DEVICE_RECEIPT="device_receipt";    //time at which msg reached recipient's device
    private static final String KEY_DEVICE_SEEN="device_seen";          //time at which msg seen by recipient
    private static final String KEY_DURATION="media_duration";          //duration of media
    private static final String KEY_COLOR="color";                      //color theme of chat
    private static final String KEY_LAST_MSG="last_msg_id";
    private static final String KEY_PROGRESS="progress";


    public MessageSQLiteHelper(Context context) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath()+"/EarnChat/"+DATABASE_NAME, null, DATABASE_VERSION);
        // super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MESSAGE_TABLE = "CREATE TABLE " + TABLE_MESSAGE + "("+KEY_ID+" TEXT,"
                + KEY_CONVERSATION_PARTNER + " TEXT," + KEY_CONVERSATION_TYPE + " INTEGER,"+KEY_SENDER
                + " TEXT," + KEY_ROLE + " INTEGER," + KEY_MSG_TYPE + " INTEGER,"+ KEY_MIME_TYPE + " TEXT,"
                + KEY_MSG_URL + " TEXT," +KEY_TIMESTAMP + " TEXT,"+KEY_SIZE + " TEXT,"+KEY_DATA+" TEXT,"
                +KEY_STATUS+" INTEGER," +KEY_FILE+" TEXT,"+KEY_THUMB+" TEXT,"+KEY_SERVER_RECEIPT+" TEXT,"
                +KEY_DEVICE_RECEIPT+" TEXT,"+KEY_DEVICE_SEEN+" TEXT,"+KEY_DURATION+" TEXT,"+ KEY_AWS_ID + " INTEGER,"+ KEY_PROGRESS + " INTEGER)";

        String CREATE_CHAT_TABLE="CREATE TABLE " + TABLE_CHATS + "("+KEY_CONVERSATION_PARTNER+" TEXT,"+KEY_LAST_MSG+" TEXT,"+KEY_COLOR + " TEXT)";

        db.execSQL(CREATE_MESSAGE_TABLE);
        db.execSQL(CREATE_CHAT_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATS);
        onCreate(db);
    }


    public void addMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, message.getId());
        values.put(KEY_CONVERSATION_PARTNER, message.getConvo_partner());
        values.put(KEY_CONVERSATION_TYPE, message.getConvo_type());
        values.put(KEY_SENDER, message.getSender_id());
        values.put(KEY_ROLE, message.isSelf()?0:1);
        values.put(KEY_MSG_TYPE, message.getMsg_type());
        values.put(KEY_MIME_TYPE, message.getMime_type());
        values.put(KEY_MSG_URL, message.getMsg_url());
        values.put(KEY_TIMESTAMP, message.getTimestamp());
        values.put(KEY_SIZE, message.getMedia_size());
        values.put(KEY_DATA, message.getData());
        values.put(KEY_STATUS, message.getMsg_status());
        values.put(KEY_FILE, message.getFile_path());
        values.put(KEY_DURATION,message.getDuration());

        db.insert(TABLE_MESSAGE, null, values);
        db.close();
    }

    public List<Message> getAllMessages(String convo_partner) {
        List<Message> messageList = new ArrayList<Message>();
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGE + " WHERE "+KEY_CONVERSATION_PARTNER+"='"+convo_partner
                +"' AND "+KEY_STATUS+" NOT IN("+String.valueOf(4)+")";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(cursor.getString(0));
                message.setConvo_partner(cursor.getString(1));
                message.setConvo_type(Integer.parseInt(cursor.getString(2)));
                message.setSender_id(cursor.getString(3));
                message.setSelf(Integer.parseInt(cursor.getString(4))==0);
                message.setMsg_type(Integer.parseInt(cursor.getString(5)));
                message.setMime_type(cursor.getString(6));
                message.setMsg_url(cursor.getString(7));
                message.setTimestamp(cursor.getString(8));
                message.setMedia_size(Integer.parseInt(cursor.getString(9)));
                message.setData(cursor.getString(10));
                message.setMsg_status(Integer.parseInt(cursor.getString(11)));
                message.setFile_path(cursor.getString(12));
                message.setThumb_path(cursor.getString(13));
                message.setServer_receipt(cursor.getString(14));
                message.setDevice_receipt(cursor.getString(15));
                message.setDevice_seen(cursor.getString(16));
                message.setDuration(cursor.getString(17));
                if(cursor.getString(18)!=null){
                    message.setAws_id(Integer.parseInt(cursor.getString(18)));
                }
                if(cursor.getString(19)!=null){
                    message.setProgress(Integer.parseInt(cursor.getString(19)));
                }


                messageList.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return messageList;
    }

    public Message getMessage(String id){
        Message message = new Message();
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGE + " WHERE "+KEY_ID+"='"+id
                +"' AND "+KEY_STATUS+" NOT IN("+String.valueOf(4)+")";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                message.setId(cursor.getString(0));
                message.setConvo_partner(cursor.getString(1));
                message.setConvo_type(Integer.parseInt(cursor.getString(2)));
                message.setSender_id(cursor.getString(3));
                message.setSelf(Integer.parseInt(cursor.getString(4))==0);
                message.setMsg_type(Integer.parseInt(cursor.getString(5)));
                message.setMime_type(cursor.getString(6));
                message.setMsg_url(cursor.getString(7));
                message.setTimestamp(cursor.getString(8));
                message.setMedia_size(Integer.parseInt(cursor.getString(9)));
                message.setData(cursor.getString(10));
                message.setMsg_status(Integer.parseInt(cursor.getString(11)));
                message.setFile_path(cursor.getString(12));
                message.setThumb_path(cursor.getString(13));
                message.setServer_receipt(cursor.getString(14));
                message.setDevice_receipt(cursor.getString(15));
                message.setDevice_seen(cursor.getString(16));
                message.setDuration(cursor.getString(17));
                if(cursor.getString(18)!=null){
                    message.setAws_id(Integer.parseInt(cursor.getString(18)));}
                if(cursor.getString(19)!=null){
                    message.setProgress(Integer.parseInt(cursor.getString(19)));
                }


            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return message;

    }

    public void deleteMessage(String id) {

        String deleteQuery="DELETE FROM "+TABLE_MESSAGE+" WHERE "+KEY_ID+"='"+id+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=db.rawQuery(deleteQuery,null);
        cursor.moveToFirst();
        cursor.close();
        db.close();

    }

    public void deleteChatUser(String id)
    {
        String deleteQuery = "DELETE FROM "+TABLE_CHATS+" WHERE "+KEY_CONVERSATION_PARTNER+"='"+id+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(deleteQuery,null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }

    public void clearChat(String convo_partner){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGE, KEY_CONVERSATION_PARTNER + " = ? ",
                new String[] {convo_partner});
        db.close();
    }

    public void updateMessageStatus(String convo_partner,int status){
        Log.i("Success","updateMsgStatus"+convo_partner);
        String updateQuery = "UPDATE " + TABLE_MESSAGE +" SET "+KEY_STATUS+"="+String.valueOf(status)
                +" WHERE "+KEY_CONVERSATION_PARTNER+"='"+convo_partner+"' AND "+KEY_ROLE+"="
                +String.valueOf(1) +" AND "+KEY_STATUS+" IN("+String.valueOf(0)+","+String.valueOf(2)+")";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(updateQuery, null);
        cursor.moveToFirst();
        cursor.close();
        db.close();

    }

    public void updateSingleMessageStatus(String message_id,int status){
        String updateQuery = "UPDATE " + TABLE_MESSAGE +" SET "+KEY_STATUS+"="+String.valueOf(status)
                +" WHERE "+KEY_ID+"='"+message_id+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(updateQuery, null);
        cursor.moveToFirst();
        cursor.close();
        db.close();

    }

    public void updateThumbPath(String message_id,String thumb_path) {
        String updateQuery = "UPDATE " + TABLE_MESSAGE + " SET " + KEY_THUMB + "='" + thumb_path
                + "' WHERE " + KEY_ID + "='" + message_id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(updateQuery, null);
        cursor.moveToFirst();
        cursor.close();
        db.close();

    }

    public void updateFilePath(String message_id,String file_path) {
        String updateQuery = "UPDATE " + TABLE_MESSAGE + " SET " + KEY_FILE + "='" + file_path
                + "' WHERE " + KEY_ID + "='" + message_id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(updateQuery, null);
        cursor.moveToFirst();
        cursor.close();
        db.close();

    }

    public void updateAwsId(String message_id,int aws_id){
        String updateQuery = "UPDATE " + TABLE_MESSAGE +" SET "+KEY_AWS_ID+"="+String.valueOf(aws_id)
                +" WHERE "+KEY_ID+"='"+message_id+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(updateQuery, null);
        cursor.moveToFirst();
        cursor.close();


        String selectQuery = "SELECT "+KEY_AWS_ID+" FROM " + TABLE_MESSAGE + " WHERE "+KEY_ID+"='"+message_id
                +"'";

        Cursor cursor_ = db.rawQuery(selectQuery, null);

        if (cursor_.moveToFirst()) {
            do{
                Log.i("AWS_ID",""+cursor_.getString(0));
            }while(cursor_.moveToNext());
        }

        cursor_.close();
        db.close();

    }

    public Integer getUnreadMessageCount(String convo_partner) {
        String countQuery = "SELECT * FROM " + TABLE_MESSAGE +" WHERE "+KEY_CONVERSATION_PARTNER+"='"+convo_partner
                +"' AND "+KEY_ROLE+"="+String.valueOf(1)+" AND "+KEY_STATUS+"="+String.valueOf(0)+" AND "+KEY_MSG_TYPE+" IN('0','1','2','3','4','5','6')";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        Integer size=cursor.getCount();
        cursor.close();
        db.close();

        return size;
    }

    public void setMessageStatus(String id,int status) {


        String setStatusQuery = "UPDATE " + TABLE_MESSAGE + " SET " + KEY_STATUS + "=" + String.valueOf(status) + " WHERE " + KEY_ID + "='" + id + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(setStatusQuery, null);
        cursor.moveToFirst();
        cursor.close();
        db.close();


    }

    public List<Message> getPendingMessages(){
        List<Message> messageList = new ArrayList<Message>();
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGE + " WHERE "+KEY_STATUS+"="+String.valueOf(0)
                +" AND "+KEY_ROLE+"="+String.valueOf(0)+" AND "+KEY_MSG_TYPE+" IN('0','1','2','3','4','5','6')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(cursor.getString(0));
                message.setConvo_partner(cursor.getString(1));
                message.setConvo_type(Integer.parseInt(cursor.getString(2)));
                message.setSender_id(cursor.getString(3));
                message.setSelf(Integer.parseInt(cursor.getString(4))==0);
                message.setMsg_type(Integer.parseInt(cursor.getString(5)));
                message.setMime_type(cursor.getString(6));
                message.setMsg_url(cursor.getString(7));
                message.setTimestamp(cursor.getString(8));
                message.setMedia_size(Integer.parseInt(cursor.getString(9)));
                message.setData(cursor.getString(10));
                message.setMsg_status(Integer.parseInt(cursor.getString(11)));
                message.setFile_path(cursor.getString(12));
                message.setThumb_path(cursor.getString(13));
                message.setServer_receipt(cursor.getString(14));
                message.setDevice_receipt(cursor.getString(15));
                message.setDevice_seen(cursor.getString(16));
                message.setDuration(cursor.getString(17));
                if(cursor.getString(18)!=null){
                    message.setAws_id(Integer.parseInt(cursor.getString(18)));}
                if(cursor.getString(19)!=null){
                    message.setProgress(Integer.parseInt(cursor.getString(19)));
                }

                messageList.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return messageList;

    }

    public void msgSentToSever(String old_id,String new_id){

        Log.i("TEST_MARKER","old id="+old_id);
        Log.i("TEST_MARKER","new id="+new_id);
        String updateQuery1="UPDATE "+TABLE_MESSAGE+" SET "+KEY_ID+"='"+new_id+"',"+KEY_STATUS+"="+String.valueOf(1)+
                " WHERE "+KEY_ID+"='"+old_id+"'";
        String updateQuery2="UPDATE "+TABLE_CHATS+" SET "+KEY_LAST_MSG+"='"+new_id+"' WHERE "+KEY_LAST_MSG+"='"+old_id+"'";


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(updateQuery1, null);
        cursor.moveToFirst();
        cursor.close();
        Cursor mCursor= db.rawQuery(updateQuery2, null);
        mCursor.moveToFirst();
        mCursor.close();
        db.close();


    }

    public void setMsgUrl(String id,String url){
        String updateQuery="UPDATE "+TABLE_MESSAGE+" SET "+KEY_MSG_URL+"='"+url+"' WHERE "+KEY_ID+"='"+id+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(updateQuery, null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }

    public void setProgress(String id,int progress){
        String updateQuery="UPDATE "+TABLE_MESSAGE+" SET "+KEY_PROGRESS+"='"+progress+"' WHERE "+KEY_ID+"='"+id+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(updateQuery, null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }

    public void addToChatList(String convo_partner,String last_msg){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery="SELECT * FROM "+TABLE_CHATS+" WHERE "+KEY_CONVERSATION_PARTNER+"='"+convo_partner+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        Integer size=cursor.getCount();
        cursor.close();

        if(size!=0){
            String updateQuery="UPDATE "+TABLE_CHATS+" SET "+KEY_LAST_MSG+"='"+last_msg+"' WHERE "
                    +KEY_CONVERSATION_PARTNER+"='"+convo_partner+"'";
            Cursor cursor1= db.rawQuery(updateQuery,null);
            cursor1.moveToFirst();
            cursor1.close();
        }
        else{
            ContentValues values = new ContentValues();
            values.put(KEY_CONVERSATION_PARTNER, convo_partner);
            values.put(KEY_LAST_MSG, last_msg);
            values.put(KEY_COLOR, "#FFDEAF");               //initial chat color   //prachie
            db.insert(TABLE_CHATS, null, values);
        }

        db.close();
    }

    public List<ChatListModel> getChatList(){

        List<ChatListModel> chatList = new ArrayList<ChatListModel>();
        String selectQuery = "SELECT * FROM " + TABLE_CHATS ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ChatListModel chat=new ChatListModel();
                chat.setConvo_partner(cursor.getString(0));
                chat.setLast_msg(cursor.getString(1));

                chatList.add(chat);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return chatList;

    }

    public String getLastMsgId(String id){
        String selectQuery="SELECT "+KEY_LAST_MSG+" FROM "+TABLE_CHATS+" WHERE "+KEY_CONVERSATION_PARTNER+"='"+id+"'";
        String lastMsgId="";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                lastMsgId=cursor.getString(0);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lastMsgId;
    }

    public String getMsgUrl(String id){
        String selectQuery="SELECT "+KEY_MSG_URL+" FROM "+TABLE_MESSAGE+" WHERE "+KEY_ID+"='"+id+"'";
        String url=null;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                url=cursor.getString(0);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return url;
    }

    //prachie
    public void updatecolor(String color,String user)
    {
        String updateQuery = "UPDATE " + TABLE_CHATS +" SET "+KEY_COLOR+"= '"+color+"' WHERE "+KEY_CONVERSATION_PARTNER+"='"+user+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(updateQuery, null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }
    public String getcolor(String id)
    {
        String updateQuery = "SELECT "+KEY_COLOR+" FROM "+TABLE_CHATS+" WHERE "+KEY_CONVERSATION_PARTNER+"='"+id+"'";
        String url=null;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(updateQuery,null);
        if(cursor.moveToFirst()){
            do{
                url=cursor.getString(0);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return url;

    }
}
