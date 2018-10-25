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
import android.widget.Toast;

import com.rohan7055.earnchat.Adapter.GetContactAdapter;
import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MGCChat extends AppCompatActivity {
    RecyclerView recyclerView;
    private List<ContactsModel> contactList=new ArrayList<>();
    GetContactAdapter contactAdapter;
    ContactSQLiteHelper contact_db;
    EventBus eventBus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mgcchat);
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

        contactList= contact_db.getAllMagicContacts();
        Collections.sort(contactList,new Comparator<ContactsModel>()
        {
            public int compare (ContactsModel ob1, ContactsModel ob2){
                return ob1.getDisplay_name().compareToIgnoreCase(ob2.getDisplay_name());
            }
        });
        contactAdapter=new GetContactAdapter(this,contactList,MGCChat.this,0);
        Toast.makeText(this,"MGCCHAT IS CALLED",Toast.LENGTH_LONG).show();
        for(ContactsModel contactsModel:contactList)
        {
            Log.i("chatMagic",contactsModel.getDisplay_name()+"id:"+contactsModel.getId());
        }

        recyclerView=(RecyclerView)findViewById(R.id.create_chat_view_magic2);
        RecyclerView.LayoutManager LayoutManager = new LinearLayoutManager(MGCChat.this);
        recyclerView.setLayoutManager(LayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(contactAdapter);
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
