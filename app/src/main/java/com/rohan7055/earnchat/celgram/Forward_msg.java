package com.rohan7055.earnchat.celgram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Forward_msg extends AppCompatActivity {

    RecyclerView recyclerView;
    private List<ContactsModel> contactList=new ArrayList<>();
    forward_msg_adapter contactAdapter;
    ContactSQLiteHelper contact_db;
    EventBus eventBus;

    public static ArrayList<Message> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_msg);

        eventBus=EventBus.getDefault();
        eventBus.register(this);
        contact_db=new ContactSQLiteHelper(this);

        ImageView back = (ImageView) findViewById(R.id.back_img_single);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        contactList= contact_db.getAllContacts(1);
        Collections.sort(contactList,new Comparator<ContactsModel>()
        {
            public int compare (ContactsModel ob1, ContactsModel ob2){
                return ob1.getDisplay_name().compareToIgnoreCase(ob2.getDisplay_name());
            }
        });
        contactAdapter=new forward_msg_adapter(this,contactList,Forward_msg.this,0);

        recyclerView=(RecyclerView)findViewById(R.id.create_chat_view);
        RecyclerView.LayoutManager LayoutManager = new LinearLayoutManager(Forward_msg.this);
        recyclerView.setLayoutManager(LayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(contactAdapter);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        list = (ArrayList<Message>) args.getSerializable("ARRAYLIST");
        String temp = null;
        for(Message msg : list){
            temp = temp + msg.getData();
        }
        Log.d("Anuraj_list", temp);
    }




    //Anuraj
    public ArrayList<Message> addMessages(ContactsModel model){

        ArrayList<Message> msgs = new ArrayList<>();

        for(Message msg : list){
            msg.setConvo_partner(model.getId());
            msg.setSelf(true);
            //ChatWindow obj = new ChatWindow();
            //obj.forward_msg(msg);
            msgs.add(msg);
        }

        Log.d("anuraj_outside", "yes");
        /*db_helper.addMessage(message);
        db_helper.addToChatList(message.getConvo_partner(),message.getId());
        appendMessage(message);
        eventBus.post(message);
        MessageEvent messageEvent=new MessageEvent(message);
        eventBus.post(messageEvent);*/
        return msgs;
    }

    @Subscribe
    public void onTypingNotification(String id) {
        Log.i ("yoloCelgramMain","got your typing :"+id);
        final int position=getItemPosition(id);

        if(position!=-1) {
            if (!contactList.get(position).getIsTyping()) {
                contactList.get(position).setIsTyping(true);

            } else {
                contactList.get(position).setIsTyping(false);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    contactAdapter.notifyItemChanged(position);
                }
            });
        }

    }

    private int getItemPosition(String convo_partner){
        for(int i=0;i<contactList.size();i++){
            if(convo_partner.equals(contactList.get(i).getId())){
                return i;
            }
        }

        return -1;
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CelgramMain.class));
        finish();
    }
}
