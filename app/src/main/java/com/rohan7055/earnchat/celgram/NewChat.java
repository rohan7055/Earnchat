package com.rohan7055.earnchat.celgram;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.rohan7055.earnchat.Adapter.GetContactAdapter;
import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.UserHelper;
import com.rohan7055.earnchat.celgram.CelgramApi.RestClientS3Group;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.rohan7055.earnchat.celgram.ResponseModels.CheckContacts;
import com.rohan7055.earnchat.celgram.ResponseModels.Result;
import com.rohan7055.earnchat.celgram.fragments.TabPagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewChat extends AppCompatActivity {

    RecyclerView recyclerView;
    private List<ContactsModel> contactList=new ArrayList<>();
    GetContactAdapter contactAdapter;
    ContactSQLiteHelper contact_db;
    EventBus eventBus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

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
        contactAdapter=new GetContactAdapter(this,contactList,NewChat.this,0);

        recyclerView=(RecyclerView)findViewById(R.id.create_chat_view);
        RecyclerView.LayoutManager LayoutManager = new LinearLayoutManager(NewChat.this);
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
