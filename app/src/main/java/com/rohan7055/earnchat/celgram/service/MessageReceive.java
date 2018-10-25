package com.rohan7055.earnchat.celgram.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.ConnectivityReceiver;
import com.rohan7055.earnchat.Foreground;
import com.rohan7055.earnchat.Model.StatusModel;
import com.rohan7055.earnchat.MyApplication;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.UserHelper;
import com.rohan7055.earnchat.celgram.CelgramApi.RestClientS3Group;
import com.rohan7055.earnchat.celgram.CelgramUtils;
import com.rohan7055.earnchat.celgram.ContactsModel;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.rohan7055.earnchat.celgram.DataBase_Helper.MessageSQLiteHelper;
import com.rohan7055.earnchat.celgram.GroupsModel;
import com.rohan7055.earnchat.celgram.Message;
import com.rohan7055.earnchat.celgram.Utils;
import com.rohan7055.earnchat.celgram.events.AddMemExitEvent;
import com.rohan7055.earnchat.celgram.events.AddSingleMemberEvent;
import com.rohan7055.earnchat.celgram.events.AddSingleMemberResponse;
import com.rohan7055.earnchat.celgram.events.AlertUser;
import com.rohan7055.earnchat.celgram.events.AvailableEvent;
import com.rohan7055.earnchat.celgram.events.ChangeAvailability;
import com.rohan7055.earnchat.celgram.events.ChangeGroupDpEvent;
import com.rohan7055.earnchat.celgram.events.ChangeGroupSubEvent;
import com.rohan7055.earnchat.celgram.events.ChangeLastSeenAvail;
import com.rohan7055.earnchat.celgram.events.ConnectivityEvent;
import com.rohan7055.earnchat.celgram.events.CreateGroupEvent;
import com.rohan7055.earnchat.celgram.events.DialogdismissEvent;
import com.rohan7055.earnchat.celgram.events.DisconnectEvent;
import com.rohan7055.earnchat.celgram.events.GroupCreationEvent;
import com.rohan7055.earnchat.celgram.events.GroupCreationNotify;
import com.rohan7055.earnchat.celgram.events.GroupExitEvent;
import com.rohan7055.earnchat.celgram.events.GroupMessageEvent;
import com.rohan7055.earnchat.celgram.events.GroupNameChangeResponse;
import com.rohan7055.earnchat.celgram.events.GroupTypingEvent;
import com.rohan7055.earnchat.celgram.events.MessageAckEvent;
import com.rohan7055.earnchat.celgram.events.MessageEvent;
import com.rohan7055.earnchat.celgram.events.MessageStatusEvent;
import com.rohan7055.earnchat.celgram.events.NotifyEvent;
import com.rohan7055.earnchat.celgram.events.ParticipantRefreshEvent;
import com.rohan7055.earnchat.celgram.events.RemRecvResponseEvent;
import com.rohan7055.earnchat.celgram.events.RemoveMemResponse;
import com.rohan7055.earnchat.celgram.events.RemoveMemberEvent;
import com.rohan7055.earnchat.celgram.events.SendTypingEvent;
import com.rohan7055.earnchat.celgram.events.StickerEvent;
import com.rohan7055.earnchat.celgram.events.StopServiceEvent;
import com.rohan7055.earnchat.celgram.events.UnsubscribeEvent;
import com.rohan7055.earnchat.celgram.events.UnsubscribeResSelf;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageReceive extends Service implements ConnectivityReceiver.ConnectivityReceiverListener {

    private Socket mSocket;
    private final static String TOKEN = "ChikoopApp@chikoop.com";
    EventBus eventBus;
    Handler handler;
    SharedPreferences preferences;
    MessageSQLiteHelper db_helper;
    ContactSQLiteHelper contact_db;
    Boolean retry;
    static String socket_id;
    String session;
    List<String> all_groups=new ArrayList<>();
    List<Message> msg_not=new ArrayList<>();
    List<String> unique_conversation=new ArrayList<>();
    boolean notification_running=false,show_notification=true;
    int notification_id=111;
    NotificationManager notificationManager;
    // Uri ringtone_uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    Uri ringtone_uri;
    ProgressDialog dialog;

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public int counter=0;
    public static boolean restart=true;

    Foreground.Listener mlistener = new Foreground.Listener(){
        public void onBecameForeground(){
            // ... whatever you want to do
            Log.i("LASTSEEN","onBecameForeground");
            //eventBus.post(new ChangeLastSeenAvail(true));
            setAvailable(true);
        }
        public void onBecameBackground(){
            // ... whatever you want to do
            Log.i("LASTSEEN","onBecameBackground");
            //eventBus.post(new ChangeLastSeenAvail(false));
            setAvailable(false);
        }
    };


    @Override
    public void onCreate() {

        Log.d("RingTone",""+ringtone_uri);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        handler = new Handler();
        retry=true;
        Foreground.get(this).addListener(mlistener);



        try {
            // mSocket = IO.socket("http://104.238.72.167:3000/");
            //mSocket = IO.socket("http://45.55.213.207:3000/");
            mSocket = IO.socket("http://myrhband.tech:3000/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        super.onCreate();
        MyApplication.getInstance().setConnectivityListener(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        preferences = getSharedPreferences("USER", MODE_PRIVATE);
        UserHelper.loadUser(this);
        db_helper = new MessageSQLiteHelper(this);
        contact_db = new ContactSQLiteHelper(this);
        socket_id=preferences.getString("id","");
        //socket_id=UserHelper.getId();
        session= Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        all_groups=contact_db.getMyGroups();
        startTimer();

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("authenticate", OnAuthentication);
        mSocket.on("msg", onMessage);//this listener handles all the messages
        mSocket.on("typing", onTyping);
        mSocket.on("stoptyping", onStopTyping);
        mSocket.on("onstatus",onStatus);
        mSocket.on("broadcastmsgtogrp",Onbroadcastmsg);//this is for the group chat feature ignore fothsi time
        mSocket.connect();//this connect to the server

        return START_STICKY;
    }


    private void sendToken() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("username",socket_id);//socket_id is here your mobile number
        Log.i("yoloCelgram_uname", "=" +socket_id);
        object.put("token", Utils.md5(TOKEN));//token should be same in the app and server
        object.put("session",session);//to uniquely identify the mobile
        mSocket.emit("authenticate", object);//this the event emitted for the  server
    }

    private Emitter.Listener OnAuthentication = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Boolean isAuthenticated = (Boolean) args[0];
            if (isAuthenticated) {
                Log.i("yolocelgram", "Socket is authenticated");
                sendPending();//this method send pending messages

            }
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("yolocelgram", "connected");
            try {
                if(!socket_id.equals("")&&socket_id!=null){
                sendToken();
                }else{
                    socket_id=preferences.getString("id","");
                    Log.i("yolocelgram",socket_id+"");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("yolocelgram", "disconnected");
            Log.i("yolocelgram",socket_id+"");


            if(retry){
                mSocket.connect();
            }

        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("yolocelgram",socket_id+"");

            if(retry){
                mSocket.connect();
            }
        }
    };



    private Emitter.Listener onMessage=new Emitter.Listener() {
        @Override
        public void call(Object... args) {


            JSONObject object= (JSONObject) args[0];
            Random r = new Random ();
            try {
                Log.i("Success", object.getString("content"));
                JSONObject mObject = object.getJSONObject("content");

                if (!mObject.getString("sender").equals(mObject.getString("receiver"))) {



                Message message = new Message(mObject.getString("sender"), false, mObject.getInt("msg_type"), "", mObject.getString("timestamp"),
                        0, mObject.getString("data"), "", "");
                ContactsModel contactsBlocked = contact_db.getContact(message.getConvo_partner());
                if (!contact_db.checkBlockedStatus(contactsBlocked)) {
                    message.setId(mObject.getString("msgid"));
                    message.setMsg_status(0);
                    msg_not.add(message);
                    if (!unique_conversation.contains(message.getConvo_partner())) {
                        unique_conversation.add(message.getConvo_partner());
                    }

                /*if(show_notification) {
                    updateNotification();

                }*/
                    ContactsModel contactsModel = contact_db.getContact(message.getConvo_partner());
                    if (!contact_db.checkmuteStatus(contactsModel) && isAppIsInBackground()) {
                        updateNotification();
                    }

                    if (mObject.getInt("msg_type") == 0 || mObject.getInt("msg_type") == 5 || mObject.getInt("msg_type") == 6 || mObject.getInt("msg_type") == 13) {
                        db_helper.addMessage(message);
                        db_helper.addToChatList(mObject.getString("sender"), mObject.getString("msgid"));
                        eventBus.post(message);
                    } else if (mObject.getInt("msg_type") == 403) {
                        //
                        String data = mObject.getString("data");
                        AlertUser alertUser = new AlertUser(data);
                        eventBus.postSticky(alertUser);
                    }
                    //for dp changed
                    else if (mObject.getInt("msg_type") == 6) {

                        Log.i("success", "Group dp changed by " + mObject.getString("sender") + " to " + mObject.getString("nurl"));
                        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
                        String mobile = prefs.getString("mobile", "");
                        Message msg = new Message(mObject.getString("uniquegroupid"), mObject.getString("sender"),
                                (mObject.getString("sender").equals(socket_id)), 10, "", mObject.getString("timestamp"), 0, mObject.getString("nurl"), "", "");
                        msg.setId(Long.toString(r.nextLong(), 36));
                        db_helper.addMessage(msg);
                        db_helper.addToChatList(mObject.getString("uniquegroupid"), msg.getId());

                        String sender = mObject.getString("sender");
                        if (!sender.equals(mobile)) {
                            contact_db.updateGroupDp(mObject.getString("uniquegroupid"), mObject.getString("nurl"));
                            NotifyEvent notifyEvent = new NotifyEvent(mObject.getString("uniquegroupid"));
                            eventBus.post(notifyEvent);
                            eventBus.post(msg);

                        } else {
                            //  Toast.makeText(getApplicationContext(),"OWN sender",Toast.LENGTH_LONG).show();
                            Log.i("Message receive", "OWN SENDER");
                        }
                    } else if (mObject.getInt("msg_type") == 1 || mObject.getInt("msg_type") == 2 || mObject.getInt("msg_type") == 3 || mObject.getInt("msg_type") == 4) {
                        if (mObject.getInt("msg_type") == 1 || mObject.getInt("msg_type") == 2) {
                            message.setMsg_status(-1);
                        } else {
                            message.setMsg_status(0);
                        }

                        message.setMsg_url(mObject.getString("msg_url"));
                        message.setMime_type(mObject.getString("mime_type"));
                        message.setMedia_size(mObject.getInt("media_size"));
                        if (mObject.getInt("msg_type") == 2 || mObject.getInt("msg_type") == 3) {
                            message.setDuration(mObject.getString("duration"));
                        }
                        db_helper.addMessage(message);
                        db_helper.addToChatList(mObject.getString("sender"), mObject.getString("msgid"));
                        if (mObject.getInt("msg_type") == 1 || mObject.getInt("msg_type") == 2) {

                            Log.i("Message Img/vid", message.getMsg_url());
                            startDownload(message);
                        } else {
                            eventBus.post(message);//this is called to refresh the view on the app
                            //like Message is an event so there will be subcriber in the Celgram main and Chat window
                        }
                    }

                }
            }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            Ack ack = (Ack) args[args.length - 1];
            ack.call(true);

        }
    };

    private Emitter.Listener onStatus=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject object= (JSONObject) args[0];
            try {
                String messageID=object.getString("msgid");
                int statustype=object.getInt("status");
                if (statustype==2){
                    Log.i ("success"," status is delievred with messageid "+messageID);
                    db_helper.setMessageStatus(messageID,2);
                    MessageStatusEvent event=new MessageStatusEvent(messageID,2);
                    eventBus.post(event);
                }else {
                    Log.i("success","status is read with messageid "+messageID);
                    db_helper.setMessageStatus(messageID,3);
                    MessageStatusEvent event=new MessageStatusEvent(messageID,3);
                    eventBus.post(event);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Ack ack = (Ack) args[args.length - 1];
            ack.call(true);
        }
    };


    private Emitter.Listener onTyping=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject object= (JSONObject) args[0];
            try {
                if (object.getBoolean("group")){
                    Log.i("success","Got typing message for group");
                    Log.i("TYPING_RECEIVE_SENDER",""+object.getString("sender"));

                    Log.i("TYPING_RECEIVE_GRP",""+object.getString("uniquegroupid"));
                    GroupTypingEvent event=new GroupTypingEvent(object.getString("uniquegroupid"),object.getString("sender"),true);
                    eventBus.post(event);
                }else{
                    Log.i("success","got typing message for member");
                    eventBus.post(object.getString("sender"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private Emitter.Listener onStopTyping=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject object= (JSONObject) args[0];
            try {
                if (object.getBoolean("group")){
                    Log.i("success","Got stop typing message for group");
                    GroupTypingEvent event=new GroupTypingEvent(object.getString("uniquegroupid"),object.getString("sender"),false);
                    eventBus.post(event);
                }else{
                    Log.i("success","Got stop typing message for member");
                    eventBus.post(object.getString("sender"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private Emitter.Listener Onbroadcastmsg=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject object= (JSONObject) args[0];
            try {
                Log.i ("Success",object.getString("content"));
                Log.i("Success Redelievery", String.valueOf(object.getBoolean("redelievery")));
                JSONObject message=object.getJSONObject("content");
                int messagetype=message.getInt("msgtype");
                Message msg;
                Random r = new Random ();

                if(!all_groups.contains(message.getString("uniquegroupid"))){
                    newGroup(message.getString("uniquegroupid"));
                }

                switch (messagetype){
                    case 1:
                        Log.i("success","group member named "+message.getString("receiver")+" added by "+message.getString("sender"));

                        msg=new Message(message.getString("uniquegroupid"),message.getString("sender"),
                                (message.getString("sender").equals(socket_id)),7,"",message.getString("timestamp"),0,message.getString("receiver"),"","");
                        msg.setId(Long.toString (r.nextLong (), 36));
                        db_helper.addMessage(msg);
                        db_helper.addToChatList(message.getString("uniquegroupid"),msg.getId());
                        Log.i("Group recv",message.getString("receiver").equals(socket_id)+"");
                        ParticipantRefreshEvent refreshEvent=new ParticipantRefreshEvent(true);
                        if(message.getString("receiver").equals(socket_id)) {
                            AddMemExitEvent addMemExitEvent = new AddMemExitEvent(true, message.getString("receiver"),message.getString("uniquegroupid"),msg);
                            GroupCreationEvent groupCreationEvent = new GroupCreationEvent(1,message.getString("uniquegroupid"));
                            eventBus.post(addMemExitEvent);
                            eventBus.post(groupCreationEvent);
                        }

                        eventBus.post(msg);
                        eventBus.post(refreshEvent);
                        break;

                    case 2:
                        Log.i("success","Message received :"+message.getString("message")+" by "+message.getString("sender"));
                        if(!message.getString("sender").equals(socket_id)) {
                            msg = new Message(message.getString("uniquegroupid"), message.getString("sender"),
                                    false, message.getInt("subtype"), "", message.getString("timestamp"), 0, message.getString("message"), "", "");
                            msg.setId(message.getString("msgid"));
                            GroupsModel groupsModel = contact_db.getGroup(message.getString("uniquegroupid"));
                            if(!contact_db.checkGroupmuteStatus(groupsModel)&&Foreground.get().isBackground())
                            {
                                updateNotification();
                            }
                            if (msg.getMsg_type() == 0||msg.getMsg_type()==5||msg.getMsg_type()==6||msg.getMsg_type()==13) {
                                db_helper.addMessage(msg);
                                db_helper.addToChatList(message.getString("uniquegroupid"), msg.getId());
                                eventBus.post(msg);
                            } else if(msg.getMsg_type()==1||msg.getMsg_type()==2||msg.getMsg_type()==3||msg.getMsg_type()==4||msg.getMsg_type()==14){
                                //Handle media
                                if(msg.getMsg_type()==1||msg.getMsg_type()==2)
                                {
                                    msg.setMsg_status(-1);
                                }
                                else {
                                    msg.setMsg_status(0);
                                }
                                msg.setMsg_url(message.getString("msg_url"));
                                msg.setMime_type(message.getString("mime_type"));
                                msg.setMedia_size(message.getInt("media_size"));
                                if(msg.getMsg_type()==2||msg.getMsg_type()==3||msg.getMsg_type()==14)
                                {
                                    msg.setDuration(message.getString("duration"));
                                }
                                db_helper.addMessage(msg);
                                db_helper.addToChatList(message.getString("sender"),message.getString("msgid"));
                                if(msg.getMsg_type()==1||msg.getMsg_type()==2)
                                {
                                    startDownload(msg);
                                }
                                else
                                {
                                    eventBus.post(msg);
                                }
                            }
                        }

                        break;
                    case 3:
                        Log.i("success","removed member named :"+message.getString("user")+" by " +message.getString("sender"));

                        msg=new Message(message.getString("uniquegroupid"),message.getString("sender"),
                                (message.getString("sender").equals(socket_id)),8,"",message.getString("timestamp"),0,message.getString("user"),"","");
                        msg.setId(Long.toString (r.nextLong (), 36));
                        db_helper.addMessage(msg);
                        db_helper.addToChatList(message.getString("uniquegroupid"),msg.getId());
                        ParticipantRefreshEvent refreshEvent2=new ParticipantRefreshEvent(true);

                        if(message.getString("user").equals(socket_id))
                        {
                            RemRecvResponseEvent responseEvent = new RemRecvResponseEvent(true,msg,message.getString("uniquegroupid"));
                            GroupCreationEvent groupCreationEvent =new GroupCreationEvent(2,message.getString("uniquegroupid"));
                            eventBus.post(responseEvent);
                            eventBus.post(groupCreationEvent);
                        }
                        eventBus.post(msg);
                        eventBus.post(refreshEvent2);
                        break;
                    case 4:
                        Log.i("success","delete group called");

                        break;
                    case 5:
                        Log.i("success","group name changed to "+message.getString("nname")+" by "+message.getString("sender"));
                        msg=new Message(message.getString("uniquegroupid"),message.getString("sender"),
                                (message.getString("sender").equals(socket_id)),9,"",message.getString("timestamp"),0,message.getString("nname"),"","");
                        msg.setId(Long.toString (r.nextLong (), 36));
                        db_helper.addMessage(msg);
                        db_helper.addToChatList(message.getString("uniquegroupid"),msg.getId());
                        if(!message.getString("sender").equals(UserHelper.getId()))
                        {
                            GroupNameChangeResponse response = new GroupNameChangeResponse(true,message.getString("nname"),message.getString("uniquegroupid"));
                            eventBus.post(response);
                        }
                        eventBus.post(msg);
                        break;
                    case 6:
                        Log.i("success","Group dp changed by "+message.getString("sender")+" to "+message.getString("nurl"));
                        SharedPreferences prefs= getSharedPreferences("USER",MODE_PRIVATE);
                        String mobile = prefs.getString("mobile","");
                        msg=new Message(message.getString("uniquegroupid"),message.getString("sender"),
                                (message.getString("sender").equals(socket_id)),10,"",message.getString("timestamp"),0,message.getString("nurl"),"","");
                        msg.setId(Long.toString (r.nextLong (), 36));
                        db_helper.addMessage(msg);
                        db_helper.addToChatList(message.getString("uniquegroupid"),msg.getId());

                        String sender=message.getString("sender");
                        if(!sender.equals(mobile))
                        {
                            contact_db.updateGroupDp(message.getString("uniquegroupid"),message.getString("nurl"));
                            NotifyEvent notifyEvent = new NotifyEvent(message.getString("uniquegroupid"));
                            eventBus.post(notifyEvent);
                            eventBus.post(msg);

                        }

                        else
                        {
                            //  Toast.makeText(getApplicationContext(),"OWN sender",Toast.LENGTH_LONG).show();
                            Log.i("Message receive","OWN SENDER");
                        }

                        break;
                    case 7:
                        Log.i("success","Group left by "+message.getString("user"));

                        msg=new Message(message.getString("uniquegroupid"),message.getString("user"),
                                (message.getString("user").equals(socket_id)),11,"",message.getString("timestamp"),0,"","","");
                        msg.setId(Long.toString (r.nextLong (), 36));
                        db_helper.addMessage(msg);
                        db_helper.addToChatList(message.getString("uniquegroupid"),msg.getId());
                        SharedPreferences prefs2= getSharedPreferences("USER",MODE_PRIVATE);
                        String mobile2 = prefs2.getString("mobile","");
                        String sender2=message.getString("user");
                        ParticipantRefreshEvent refreshEvent3=new ParticipantRefreshEvent(true);
                        eventBus.post(refreshEvent3);

                        if(!sender2.equals(mobile2))
                        {
                            GroupExitEvent groupExitEvent=new GroupExitEvent(message.getString("uniquegroupid"),message.getString("user"),message.getString("timestamp"));
                            eventBus.post(groupExitEvent);
                            eventBus.post(msg);
                        }

                        else
                        {
                            //  Toast.makeText(getApplicationContext(),"OWN sender",Toast.LENGTH_LONG).show();
                            Log.i("Message receive ","OWN SENDER EXIT");
                        }

                        break;

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            Ack ack = (Ack) args[args.length - 1];
            ack.call(true);
        }
    };

    public void setAvailable(Boolean available){
        JSONObject object=new JSONObject();
        try {

            object.put("sender",socket_id);
            object.put("setavail",available);
            //last seen
            object.put("lastseen",System.currentTimeMillis());
            //Decides if msg need to be sent to a group or single person

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("setavailable", object, new Ack() {
            @Override
            public void call(Object... args) {
                Boolean Messagesent=(Boolean)args[0];
                Log.i ("Success","Set Available is" +Messagesent);
            }
        });
    }



    private void getAvailable(String id){

        JSONObject object=new JSONObject();
        try {

            object.put("username",id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("getavailable", object, new Ack() {
            @Override
            public void call(Object... args) {
                Boolean Messagesent=(Boolean)args[0];
                //last seen
                //
                String lastseen = (String) args[1];
                Log.i ("Success","Available is " +Messagesent);
                ChangeAvailability changeAvailability=new ChangeAvailability(Messagesent,lastseen);
                eventBus.post(changeAvailability);


            }
        });
    }

    private void sendPending(){
        List <Message> messages=db_helper.getPendingMessages();

        Log.i("TEST_MARKERRR","pendingMsg="+String.valueOf(messages.size()));

        for(int i=0;i<messages.size();i++) {

            final Message message=messages.get(i);
            JSONObject object = new JSONObject();
            try {
                object.put("sender",socket_id);
                object.put("receiver",message.getConvo_partner());
                object.put("data",message.getData());
                object.put("isfirstmsg",true);
                object.put("needack",true);
                object.put("msg_type",message.getMsg_type());

                if(message.getMsg_type()==1){
                    object.put("mime_type",message.getMime_type());
                    object.put("media_size",message.getMedia_size());
                    object.put("msg_url",message.getMsg_url());
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            if(mSocket.connected()) {
                mSocket.emit("sendpmmsg", object, new Ack() {
                    @Override
                    public void call(Object... args) {
                        String message_id= (String) args[0];
                        Log.i("Success", "Message sent" + message_id);

                        if (message_id!=null) {
                            Log.i("Success", "Message sent is" + message.getData());
                            Log.i("TEST_MARKER","MessageIdBefore "+message.getId());
                            db_helper.msgSentToSever(message.getId(),message_id);
                            Log.i("TEST_MARKER","MessageIdAfter "+message.getId());
                        }
                    }
                });
            }
        }

    }

    private void createGroup(final String group_name, final String id,final List<String> members){

        JSONObject grobject=new JSONObject();

        try {
            grobject.put("admin",socket_id);
            grobject.put("groupname",group_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(mSocket.connected()) {
            mSocket.emit("creategroup", grobject, new Ack() {
                @Override
                public void call(Object... args) {
                    String UniqueGroupId = (String) args[0];
                    Log.i("success", "Unique id generated is " + UniqueGroupId);
                    if(UniqueGroupId!=null){
                        contact_db.groupCreated(id,UniqueGroupId);
                        Log.i("ID@MESSAGERECEIVE",""+id+"/"+UniqueGroupId);
                        all_groups.add(UniqueGroupId);
                        Message message=new Message(UniqueGroupId,socket_id,true,12,"",0,group_name,"","");
                        Log.i("TIMESTAMP_CHECK",""+message.getTimestamp());
                        db_helper.addMessage(message);
                        db_helper.addToChatList(UniqueGroupId,message.getId());
                        addMemberMod(id,UniqueGroupId,members);}

                    else{
                        contact_db.setGroupCreationStatus(id,3);
                        GroupCreationNotify event=new GroupCreationNotify(id,id,3);
                        DialogdismissEvent dialogdismissEvent=new DialogdismissEvent(false);
                        eventBus.post(event);
                        //notify admemberactivity group created failed
                        eventBus.post(dialogdismissEvent);
                    }

                }
            });

        }
        else{
            contact_db.setGroupCreationStatus(id,3);
            GroupCreationNotify event=new GroupCreationNotify(id,id,3);
            DialogdismissEvent dialogdismissEvent=new DialogdismissEvent(false);
            eventBus.post(event);
            //notify admemberactivity group created  failed
            eventBus.post(dialogdismissEvent);
        }

    }

    private void addGroupMember(String group_id, final String member){


        JSONObject memberObject =new JSONObject();
        try {
            memberObject.put("uniquegroupid",group_id);
            memberObject.put("sender",socket_id);
            memberObject.put("receiver",member);
            memberObject.put("msgtype",1);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(mSocket.connected()) {
            mSocket.emit("addgroupmember", memberObject, new Ack() {
                @Override
                public void call(Object... args) {
                    Boolean Groupmemberadded = (Boolean) args[0];
                    Log.i("Success", "Added member :" + Groupmemberadded);
                }
            });
        }

    }


    private void subscribegroupfortyping(String group_id) throws JSONException {
        JSONObject object=new JSONObject();
        object.put("uniquegroupid",group_id);
        mSocket.emit("subscribetyping", object, new Ack() {
            @Override
            public void call(Object... args) {
                Log.i("success","group subscribed");

            }
        });
    }

    private void changegroupdp(String group_id,String dp_url) throws JSONException {
        //Toast.makeText(this, "changegroupdpiscalled"+dp_url, Toast.LENGTH_SHORT).show();
        JSONObject object=new JSONObject();
        Log.i("GroupInfo",dp_url);
        GroupsModel groupsModel = contact_db.getGroup(group_id);
        object.put("uniquegroupid",group_id);
        object.put("sender",socket_id);
        object.put("nurl",dp_url);
        object.put("msgtype",6);

        if(mSocket.connected()) {
            mSocket.emit("chgrdp", object, new Ack() {
                @Override
                public void call(Object... args) {
                    Boolean dp = (Boolean) args[0];
                    Log.i("Success", "Group dp changed :" + dp);
                }
            });
        }
    }

    private void broadcastMessageToGroup(String group_id,Message message){
        final Message messageGroup = message;

        JSONObject groupBroadcast=new JSONObject();
        try {
            groupBroadcast.put("uniquegroupid",group_id);
            groupBroadcast.put("sender",socket_id);
            groupBroadcast.put("msgtype",2);
            groupBroadcast.put("message",message.getData());
            groupBroadcast.put("subtype",message.getMsg_type());

            if(message.getMsg_type()!=0){
                groupBroadcast.put("mime_type", message.getMime_type());
                groupBroadcast.put("media_size", message.getMedia_size());
                groupBroadcast.put("msg_url", message.getMsg_url());

                if(message.getMsg_type()==2||message.getMsg_type()==3||message.getMsg_type()==14){
                    groupBroadcast.put("media_duration", message.getDuration());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(mSocket.connected()) {

            mSocket.emit("broadcastmsgtogrp", groupBroadcast, new Ack() {
                @Override
                public void call(Object... args) {
                    String message_id = (String) args[0];
                    Log.i("Success", "Msg Sent:" + message_id);
                    if (message_id != null) {
                        db_helper.msgSentToSever(messageGroup.getId(), message_id);
                        MessageStatusEvent event=new MessageStatusEvent(messageGroup.getId(),message_id);
                        eventBus.post(event);

                    }
                }
            });

        }
    }

    @Subscribe
    public void sendMessageGroup(GroupMessageEvent event)
    {    //Toast.makeText(this,"sendMessageGroupIscalled",Toast.LENGTH_LONG).show();
        GroupsModel groupsModel = event.getGroupsModel();
        String id = groupsModel.getId();
        Message message = event.getMessage();

        broadcastMessageToGroup(id,message);
    }

    @Subscribe
    public void removeGroupMemberReq(RemoveMemberEvent removeMemberEvent)
    {
        try {
            removegroupmember(removeMemberEvent.getGroup_id(),removeMemberEvent.getMember());
        }catch (JSONException e)
        {
            Log.i("RemGrpMem",e.getMessage());
        }
    }

    public void removegroupmember(final String group_id, final String member) throws JSONException {

        JSONObject object=new JSONObject();
        object.put("uniquegroupid",group_id);
        object.put("sender",socket_id);
        object.put("user",member);
        object.put("msgtype",3);

        if(mSocket.connected()) {
            mSocket.emit("removegroupmember", object, new Ack() {
                @Override
                public void call(Object... args) {
                    Boolean Groupmemberremoved = (Boolean) args[0];
                    Log.i("Success", "Removed member :" + Groupmemberremoved);
                    RemoveMemResponse response = new RemoveMemResponse(true,member,group_id);
                    eventBus.post(response);
                }
            });
        }else{
            RemoveMemResponse response = new RemoveMemResponse(false,member,group_id);
            eventBus.post(response);
        }
    }


    private void getgroupmembersinfo(String group_id) throws JSONException {
        JSONObject object=new JSONObject();
        object.put("uniquegroupid",group_id);
        if(mSocket.connected()) {
            mSocket.emit("getgroupmemberinfo", object, new Ack() {
                @Override
                public void call(Object... args) {
                    JSONArray members = (JSONArray) args[0];
                    Log.i("success", "Length of members is" + members.length());
                    /**
                     * Keys for each member object :
                     * user_id,user_name,status,user_dp_url,groups_sub,created_at
                     *
                     */
                    for (int i = 0; i < members.length(); i++) {
                        try {
                            JSONObject member = members.getJSONObject(i);
                            Log.i("success", "id is " + member.getString("user_id") + " name is " + member.getString("user_name") + " created at " + member.getString("created_at"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        }
    }

    private void unsubscribegroup(final String group_id) throws JSONException {

        JSONObject object=new JSONObject();
        object.put("uniquegroupid",group_id);
        object.put("user",socket_id);    //Self removal of user
        object.put("msgtype",7);

        if(mSocket.connected()) {

            mSocket.emit("unsubgr", object, new Ack() {
                @Override
                public void call(Object... args) {
                    Boolean Groupunsub = (Boolean) args[0];
                    if(Groupunsub){
                        contact_db.setGroupCreationStatus(group_id,2);
                        UnsubscribeResSelf event=new UnsubscribeResSelf(true);
                        eventBus.post(event);
                    }
                    Log.i("Success", "Unsubscribed :" + Groupunsub);
                }
            });

        }else{
            UnsubscribeResSelf event=new UnsubscribeResSelf(false);
            eventBus.post(event);
        }
    }

    private void deletegroup(String group_id,String sender) throws JSONException {
        JSONObject object=new JSONObject();
        object.put("uniquegroupid",group_id);
        object.put("sender",sender);
        object.put("msgtype",4);

        if(mSocket.connected()) {

            mSocket.emit("deletegroup", object, new Ack() {
                @Override
                public void call(Object... args) {
                    Boolean Groupdeleted = (Boolean) args[0];
                    Log.i("Success", "Deleted group :" + Groupdeleted);
                }
            });
        }else{

        }
    }

    @Subscribe
    public void ChangeSubject(ChangeGroupSubEvent event)
    {
        try {
            changegroupname(event.getGroupid(),event.getGroupSub());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void changegroupname(final String group_id, final String new_name) throws JSONException {
        JSONObject object=new JSONObject();
        object.put("uniquegroupid",group_id);
        object.put("sender",socket_id);
        object.put("nname",new_name);
        object.put("msgtype",5);
        if(mSocket.connected()) {
            mSocket.emit("chgrname", object, new Ack() {
                @Override
                public void call(Object... args) {
                    Boolean Groupnamechanged = (Boolean) args[0];
                    Log.i("Success", "Group name changed :" + Groupnamechanged);
                    GroupNameChangeResponse groupNameChangeResponse = new GroupNameChangeResponse(Groupnamechanged,new_name,group_id);
                    eventBus.post(groupNameChangeResponse);
                }
            });
        }else{
            GroupNameChangeResponse groupNameChangeResponse = new GroupNameChangeResponse(false);
            eventBus.post(groupNameChangeResponse);

        }

    }


    private void getusergroupsinfo(final String group_id, String user_id) throws JSONException {
        JSONObject object=new JSONObject();
        object.put("username",user_id);
        mSocket.emit("getusergroupsinfo", object, new Ack() {
            @Override
            public void call(Object... args) {
                JSONArray groups= (JSONArray) args[0];
                Log.i("success","Length of groups is"+groups.length());

                /**
                 * Keys for each group object :
                 *
                 * group_id,group_name,gr_dp_url,group_members,admin,created_at
                 */

                for (int i=0;i<groups.length();i++){
                    try {
                        JSONObject group=groups.getJSONObject(i);

                        if(group_id.equals(group.getString("group_id"))){
                            Log.i("success","json is "+group.toString());
                            String members[]=group.getString("group_members").split(",");
                            String admins[]=group.getString("admin").split(",");
                            List<String> memberList=new ArrayList<String>(),adminList=new ArrayList<String>();
                            for(String member:members){
                                memberList.add(member);
                            }
                            for (String admin:adminList){
                                adminList.add(admin);
                            }
                            GroupsModel groupsModel=new GroupsModel(group.getString("admin"),group.getString("group_name"),memberList
                                    ,adminList, group.getString("gr_dp_url"),1);

                            groupsModel.setId(group.getString("group_id"));
                            groupsModel.setCreation_status(1);
                            contact_db.addGroup(groupsModel);
                            eventBus.post(groupsModel);

                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

//    private void getGroupInfo(String group_id) throws JSONException {
//        JSONObject object=new JSONObject();
//        object.put("uniquegroupid",group_id);
//        if(mSocket.connected()) {
//            mSocket.emit("getgroupinfo", object, new Ack() {
//                @Override
//                public void call(Object... args) {
//                    /**
//                     * Keys for group object :
//                     *
//                     * group_id,group_name,gr_dp_url,group_members,admin,created_at
//                     */
//
//                    try {
//                        JSONObject group = (JSONObject) args[0];
//                        Log.i("success", "json is " + group.toString());
//
//                        String members[] = group.getString("group_members").split(",");
//                        String admins[] = group.getString("admin").split(",");
//                        List<String> memberList = new ArrayList<String>(), adminList = new ArrayList<String>();
//                        for (String member : members) {
//                            memberList.add(member);
//                        }
//                        for (String admin : admins) {
//                            adminList.add(admin);
//                        }
//                        GroupsModel groupsModel = new GroupsModel(group.getString("group_id"), group.getString("admin")
//                                , group.getString("group_name"), memberList, adminList,
//                                group.getString("gr_dp_url"), 1);
//
//                        groupsModel.setCreation_status(1);
//                        contact_db.addGroup(groupsModel);
//                        eventBus.post(groupsModel);
//
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//
//        }
//    }

    private void newGroup(final String group_id){

        JSONObject object=new JSONObject();
        try {
            object.put("uniquegroupid",group_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(mSocket.connected()) {
            mSocket.emit("getgroupinfo", object, new Ack() {
                @Override
                public void call(Object... args) {
                    /**
                     * Keys for group object :
                     *
                     * group_id,group_name,gr_dp_url,group_members,admin,created_at
                     */

                    try {
                        JSONObject group = (JSONObject) args[0];
                        Log.i("success", "json is " + group.toString());
                        if(group!=null) {
                            String members[] = group.getString("groups_members").split(",");
                            String admins[] = group.getString("admin").split(",");
                            List<String> memberList = new ArrayList<String>(), adminList = new ArrayList<String>();
                            for (String member : members) {
                                if(!member.equals(socket_id)){
                                    memberList.add(member);}
                            }
                            for (String admin : admins) {
                                adminList.add(admin);
                            }
                            GroupsModel groupsModel = new GroupsModel(group.getString("admin")
                                    , group.getString("group_name"), memberList, adminList,
                                    group.getString("gr_dp_url"), 1);

                            groupsModel.setId(group.getString("group_id"));
                            groupsModel.setCreation_status(1);
                            groupsModel.setCreated_on(group.getString("created_at"));
                            contact_db.addGroup(groupsModel);
                            all_groups.add(groupsModel.getId());
                            subscribegroupfortyping(groupsModel.getId());
                            Message message=new Message(groupsModel.getId(),groupsModel.getCreated_by(),false,12,"",groupsModel.getCreated_on()
                                    ,0,groupsModel.getDisplay_name(),"","");
                            db_helper.addMessage(message);
                            contact_db.addIndirectContacts(memberList);
                            eventBus.post(groupsModel);
                        }

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

        }

    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        Foreground.get(this).removeListener(mlistener);
        eventBus.unregister(this);
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("RestartReceiver");
        broadcastIntent.putExtra("RESTART",true);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }



   //this the subscriber method for the message event
    @Subscribe
    public void sendMessage(MessageEvent messageEvent) {
        //Toast.makeText(this,"if statement individ message is called",Toast.LENGTH_LONG).show();
        final Message message=messageEvent.getMessage();
        if(message.getConvo_type()==0) {

            JSONObject object = new JSONObject();
            try {
                object.put("sender", socket_id);
                object.put("receiver", message.getConvo_partner());
                object.put("data", message.getData());
                object.put("isfirstmsg", true);
                object.put("needack", true);
                object.put("msg_type", message.getMsg_type());

                if (message.getMsg_type()==1||message.getMsg_type()==2||message.getMsg_type()==3||message.getMsg_type()==4) {

                    object.put("mime_type", message.getMime_type());
                    object.put("media_size", message.getMedia_size());
                    object.put("msg_url", message.getMsg_url());
                    if(message.getMsg_type()==2||message.getMsg_type()==3){
                        object.put("duration",message.getDuration());
                    }
                }

                else if (message.getMsg_type() == 14){
                    object.put("mime_type", message.getMime_type());
                    object.put("media_size", message.getMedia_size());
                    object.put("msg_url", message.getMsg_url());
                    object.put("duration",message.getDuration());
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"Exception sendMessage",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            if (mSocket.connected()) {
                //Toast.makeText(getApplicationContext(),"socketConnected",Toast.LENGTH_LONG).show();

                mSocket.emit("sendpmmsg", object, new Ack() {
                    @Override
                    public void call(Object... args) {
                        // Toast.makeText(getApplicationContext(),"call method is called",Toast.LENGTH_LONG).show();
                        Log.i("Success","call is invoked");


                        String message_id = (String) args[0];
                        Log.i("Success", "Message sent" + message_id);

                        if (message_id != null) {
                            db_helper.msgSentToSever(message.getId(), message_id);
                            MessageStatusEvent event=new MessageStatusEvent(message.getId(),message_id);
                            eventBus.post(event);

                        }
                        else
                        {
                            Log.i("Success","msgidnull");

                        }
                    }
                });
            }
        }
        else{
            // Toast.makeText(this,"else statement group message is called",Toast.LENGTH_LONG).show();
            JSONObject groupBroadcast=new JSONObject();
            try {
                groupBroadcast.put("uniquegroupid",message.getConvo_partner());
                groupBroadcast.put("sender",socket_id);
                groupBroadcast.put("msgtype",2);
                groupBroadcast.put("message",message.getData());
                groupBroadcast.put("subtype",message.getMsg_type());

                   /* if(message.getMsg_type()!=0){
                        groupBroadcast.put("mime_type", message.getMime_type());
                        groupBroadcast.put("media_size", message.getMedia_size());
                        groupBroadcast.put("msg_url", message.getMsg_url());

                        if(message.getMsg_type()==2||message.getMsg_type()==3){
                            groupBroadcast.put("media_duration", message.getDuration());
                        }
                    }*/

                if (message.getMsg_type()==1||message.getMsg_type()==2||message.getMsg_type()==3||message.getMsg_type()==4) {

                    groupBroadcast.put("mime_type", message.getMime_type());
                    groupBroadcast.put("media_size", message.getMedia_size());
                    groupBroadcast.put("msg_url", message.getMsg_url());
                    if(message.getMsg_type()==2||message.getMsg_type()==3){
                        groupBroadcast.put("duration",message.getDuration());
                    }
                }

                else if (message.getMsg_type() == 14){
                    groupBroadcast.put("mime_type", message.getMime_type());
                    groupBroadcast.put("media_size", message.getMedia_size());
                    groupBroadcast.put("msg_url", message.getMsg_url());
                    groupBroadcast.put("duration",message.getDuration());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(mSocket.connected()) {

                mSocket.emit("broadcastmsgtogrp", groupBroadcast, new Ack() {
                    @Override
                    public void call(Object... args) {
                        String message_id = (String) args[0];
                        Log.i("Success", "Msg Sent:" + message_id);
                        if (message_id != null) {
                            db_helper.msgSentToSever(message.getId(), message_id);
                            MessageStatusEvent event=new MessageStatusEvent(message.getId(),message_id);
                            eventBus.post(event);

                        }
                    }
                });

            }

        }

    }

    @Subscribe
    public void sendTyping(SendTypingEvent event){
        if(mSocket.connected()) {
            if (event.getIsTyping()) {
                mSocket.emit("typing", event.getObject());
            } else {
                mSocket.emit("stoptyping", event.getObject());
            }
        }
    }

    @Subscribe
    public void onUnsubscibe(UnsubscribeEvent event){
        try {
            unsubscribegroup(event.getGroup_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Subscribe
    public void getAvailable(AvailableEvent event){
        getAvailable(event.getUname());
    }
    @Subscribe
    public void changeAvailability(ChangeLastSeenAvail event){
        setAvailable(event.getAvailable());
        if(event.getAvailable()){
            if(notificationManager!=null){
                notificationManager.cancelAll();
            }
            show_notification=false;
            unique_conversation.clear();
            msg_not.clear();
            for(int i=0;i<all_groups.size();i++){
                try {
                    subscribegroupfortyping(all_groups.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            show_notification=true;
            unique_conversation.clear();
            msg_not.clear();
        }

    }


    @Subscribe
    public void createGroupEvent(CreateGroupEvent event){
        Log.i("CreateGroupEvent","called");
        GroupsModel groupsModel=event.getGroupsModel();
        createGroup(groupsModel.getDisplay_name(),groupsModel.getId(),groupsModel.getMembers());

    }


    @Subscribe
    public void changedpEvent(ChangeGroupDpEvent event)
    {
        Log.i("ChangeDpEvent","called");
        GroupsModel groupsModel=event.getGroupsModel();
        try {
            changegroupdp(groupsModel.getId(),contact_db.getgroupdpName(groupsModel.getId()));
        } catch (JSONException e) {
            Log.e("ChangeDpEvent",e.toString());
        }
    }

    /* @Subscribe
     public void onUnsubscribeEvent(UnsubscribeEvent event){
         try {
             unsubscribegroup(event.getGroup_id());
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }
 */
    @Subscribe
    public void AddSingleMember(final AddSingleMemberEvent event){
        JSONObject memberObject =new JSONObject();
        try {
            memberObject.put("uniquegroupid",event.getGroup_id());
            memberObject.put("sender",socket_id);
            memberObject.put("receiver",event.getMember());
            memberObject.put("msgtype",1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(mSocket.connected()) {
            mSocket.emit("addgroupmembermod", memberObject, new Ack() {
                @Override
                public void call(Object... args) {
                    Boolean Groupmemberadded = (Boolean) args[0];
                    Log.i("Success", "Added member :" + Groupmemberadded);
                    if(Groupmemberadded){
                        contact_db.newMemberAdded(event.getGroup_id(),event.getMember());
                        AddSingleMemberResponse mEvent=new AddSingleMemberResponse(true,event.getMember(),event.getGroup_id());
                        eventBus.post(mEvent);
//                        Message msg=new Message(event.getGroup_id(),socket_id, true,
//                                7,"",new CelgramUtils().getCurrentTime(),0,event.getMember(),"","");
//                        msg.setId(Long.toString (r.nextLong (), 36));
//                        db_helper.addMessage(msg);
//                        db_helper.addToChatList(message.getString("uniquegroupid"),msg.getId());
                    }
                }
            });
        }
        else{
            AddSingleMemberResponse mEvent=new AddSingleMemberResponse(false,event.getMember(),event.getGroup_id());
            eventBus.post(mEvent);
        }
    }



    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if(isConnected){
            retry=true;
            if(!mSocket.connected()){
                mSocket.connect();
                eventBus.post(new ConnectivityEvent(true));}
        }
        else{
            retry=false;

        }
    }
    public void addMemberMod(final String old_id,final String group_id,final List<String> members)
    {
        StringBuilder users=new StringBuilder();
        Log.i("MemberListSize",""+members.size());
        for(String member:members)
        {
            users.append(member + ",");
        }
        users.deleteCharAt(users.length() - 1);
        String.valueOf(users);
        RestClientS3Group.ApiGroupInterface service = RestClientS3Group.getClient();
        Call<StatusModel> call = service.addMembers(group_id, String.valueOf(users));
        call.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                StatusModel model=response.body();
                if(model.getStatus())
                {

                    addMembers(old_id,group_id,members);
                }else
                {

                    DialogdismissEvent dialogdismissEvent=new DialogdismissEvent(false);
                    //notify admemberactivity group created failed
                    eventBus.post(dialogdismissEvent);
                }
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                DialogdismissEvent dialogdismissEvent=new DialogdismissEvent(false);
                //notify admemberactivity group created failed
                eventBus.post(dialogdismissEvent);
            }
        });

    }

    public void addMembers(final String old_id,final String group_id, final List<String> members){
        Log.i("MemberListSize",""+members.size());
        for(final String member:members){
            JSONObject memberObject =new JSONObject();
            try {
                memberObject.put("uniquegroupid",group_id);
                memberObject.put("sender",socket_id);
                memberObject.put("receiver",member);
                memberObject.put("msgtype",1);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(mSocket.connected()) {
                mSocket.emit("addgroupmember", memberObject, new Ack() {
                    @Override
                    public void call(Object... args) {
                        Boolean Groupmemberadded = (Boolean) args[0];
                        Log.i("Success", "Added member :" + Groupmemberadded);
                        if(!Groupmemberadded){
                            contact_db.setGroupCreationStatus(group_id,3);
                            GroupCreationNotify event=new GroupCreationNotify(old_id,group_id,3);
                            eventBus.post(event);
                            //notify addmemberactivity
                            eventBus.post(false);
                        }
                    }
                });
            }
            else{
                contact_db.setGroupCreationStatus(group_id,3);
                GroupCreationNotify event=new GroupCreationNotify(old_id,group_id,3);
                DialogdismissEvent dialogdismissEvent=new DialogdismissEvent(false);
                eventBus.post(event);
                //notify admemberactivity group created success
                eventBus.post(dialogdismissEvent);

            }

        }
        Log.i("FinalCreation","true");
        try {
            subscribegroupfortyping(group_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        contact_db.setGroupCreationStatus(group_id,1);
        GroupCreationNotify event=new GroupCreationNotify(old_id,group_id,1);
        DialogdismissEvent dialogdismissEvent=new DialogdismissEvent(true,group_id);
        eventBus.post(event);
        //notify admemberactivity group created success
        eventBus.post(dialogdismissEvent);

    }

    @Subscribe
    public void sendSticker(StickerEvent stickerEvent){

    }

    @Subscribe
    public void sendMessageAck(final MessageAckEvent event){
        JSONObject object=new JSONObject();


        try {
            object.put("msgid",event.getMessage_id());
            object.put("sender",socket_id);
            object.put("receiver",event.getReceiver());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(mSocket.connected()) {
            mSocket.emit("sendreadack", object, new Ack() {
                @Override
                public void call(Object... args) {
                    Boolean ack = (Boolean) args[0];
                    Log.i("success", " send read ack sent " + ack);
                    if(ack){
                        db_helper.updateSingleMessageStatus(event.getMessage_id(),1);
                    }
                }
            });
        }
    }

    private void updateNotification(){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext());
        notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String not_title="",not_body="";
        if(unique_conversation.size()==1){
            String temp=contact_db.getContactName(msg_not.get(0).getConvo_partner());
            not_title=(temp!=null?temp:msg_not.get(0).getConvo_partner());
            if(msg_not.size()==1){
                not_body= CelgramUtils.getMessageBrief(msg_not.get(0));
            }
            else {
                not_body=String.valueOf(msg_not.size())+" messages";
            }
        }
        else if(unique_conversation.size()>1){
            not_title="Celgram";
            not_body=String.valueOf(msg_not.size())+" messages from "+String.valueOf(unique_conversation.size())+" conversations";
        }

        //modified by rohan thakur 16 feb 2017
        SharedPreferences PSharedPreferences = getSharedPreferences("MYDATA", MODE_PRIVATE);
        ringtone_uri = Uri.parse(PSharedPreferences.getString("uri", ""));
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.earnchat_logo);
        builder.setLargeIcon(bm);
        builder.setContentText(not_body)
                .setContentTitle(not_title)
                .setSmallIcon(R.drawable.earnchat_logo)
                .setLargeIcon(bm)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)

                .setVibrate(new long[] {1000, 1000})

                //New Addition
                .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE)
                .setContentIntent(
                        PendingIntent.getActivity(getApplicationContext(), 10,
                                new Intent(getApplicationContext(), CelgramMain.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                                0)
                );
        notificationManager.notify(notification_id, builder.build());

        builder.setContentTitle(not_title);
        builder.setContentText(not_body);

        MediaPlayer mp= MediaPlayer.create(getApplicationContext(), R.raw.onetoone_and_group);
        mp.start();

        notificationManager.notify(notification_id, builder.build());

        notification_running=true;
    }

    private boolean isAppIsInBackground() {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(getApplicationContext().ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(getApplicationContext().getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(getApplicationContext().getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    private void startDownload(Message message){
        Intent downloadService=new Intent(getApplicationContext(), AwsDownloadService.class);
        downloadService.putExtra("message",message);

        getApplicationContext().startService(downloadService);
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Subscribe
    public void disconnectEvent(DisconnectEvent event){
        mSocket.disconnect();
    }


}