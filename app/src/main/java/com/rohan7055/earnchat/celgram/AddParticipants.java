package com.rohan7055.earnchat.celgram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rohan7055.earnchat.Adapter.GetContactAdapter;
import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.UserHelper;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.rohan7055.earnchat.celgram.events.CreateGroupEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddParticipants extends AppCompatActivity {

    private static TextView participant_count;
    private static TextView activity_name;
    private static EditText contact_name;
    private static EditText multi_contact_name;
    private RecyclerView participant_recycler_view, contact_recycler_view,multi_contact_list;
    private static GetContactAdapter participant_adapter;
    private static GetContactAdapter search_list_adapter;
    private static GetContactAdapter multi_search_adapter;
    private static List<ContactsModel> search_list;
    private static List<ContactsModel> multi_search_list;
    private static List<ContactsModel> participant_list;
    private static List<ContactsModel> all_contact_list;
    private static List<ContactsModel> temp_list;
    private RelativeLayout rr,multi_selection;
    Button create_group,button_done,button_cancel;
    private ContactsModelWrapper cmw;
    private ContactSQLiteHelper contact_db;
    private static boolean visible=false;
    private String group_subject,file_path;
    EventBus eventBus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participants);
        group_subject=getIntent().getStringExtra("group_subject");
        file_path=getIntent().getStringExtra("file_path");
        Log.i("Group+File","="+group_subject+"/"+file_path);


        eventBus=EventBus.getDefault();
        contact_db=new ContactSQLiteHelper(this);

        activity_name=(TextView) findViewById(R.id.heading_txt);
        participant_count = (TextView) findViewById(R.id.participant_count);
        contact_name = (EditText) findViewById(R.id.contact_name3);
        participant_recycler_view = (RecyclerView) findViewById(R.id.participants_recycler_view);
        contact_recycler_view = (RecyclerView) findViewById(R.id.contact_recycler_view);
        multi_contact_list =(RecyclerView) findViewById(R.id.multi_contact_list);
        multi_contact_name = (EditText) findViewById(R.id.multi_contact_name);
        multi_selection= (RelativeLayout) findViewById(R.id.multi_selection);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_done = (Button)  findViewById(R.id.button_done);
        search_list = new ArrayList<>();
        participant_list = new ArrayList<>();
        multi_search_list= new ArrayList<>();
        all_contact_list = new ArrayList<>();
        temp_list = new ArrayList<>();
        all_contact_list.addAll(contact_db.getAllContacts(1));

        activity_name.setText("Add Participants");

        Collections.sort(all_contact_list,new Comparator<ContactsModel>()

        {
            public int compare (ContactsModel ob1, ContactsModel ob2){
                return ob1.getDisplay_name().compareToIgnoreCase(ob2.getDisplay_name());
            }
        });

        temp_list=all_contact_list;
        multi_search_list=all_contact_list;

        search_list_adapter=new GetContactAdapter(getApplicationContext(), search_list, AddParticipants.this, 1);
        participant_adapter=new GetContactAdapter(getApplicationContext(), participant_list, AddParticipants.this, 2);
        multi_search_adapter=new GetContactAdapter(getApplicationContext(), multi_search_list, AddParticipants.this, 3);


        rr = (RelativeLayout) findViewById(R.id.rr);
        create_group = (Button) findViewById(R.id.button_create_group);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AddParticipants.this);
        RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(AddParticipants.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddParticipants.this);


        participant_recycler_view.setLayoutManager(mLayoutManager);
        participant_recycler_view.setItemAnimator(new DefaultItemAnimator());

        contact_recycler_view.setLayoutManager(nLayoutManager);
        contact_recycler_view.setItemAnimator(new DefaultItemAnimator());

        multi_contact_list.setLayoutManager(layoutManager);
        multi_contact_list.setItemAnimator(new DefaultItemAnimator());

        contact_recycler_view.setAdapter(search_list_adapter);
        participant_recycler_view.setAdapter(participant_adapter);
        multi_contact_list.setAdapter(multi_search_adapter);



        contact_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!contact_name.getText().toString().trim().equalsIgnoreCase("")) {


                    search_list.clear();
                    contact_recycler_view.removeAllViews();

                    for (int i = 0; i < temp_list.size(); i++) {

                        if (temp_list.get(i).getDisplay_name().length() > contact_name.getText().toString().trim().length()) {
                            if (contact_name.getText().toString().trim().equalsIgnoreCase((temp_list.get(i).getDisplay_name().toString().substring(0, contact_name.getText().toString().trim().length())))) {
                                search_list.add(temp_list.get(i));

                            }
                        } else if (temp_list.get(i).getDisplay_name().length() == contact_name.getText().toString().trim().length()) {
                            if (temp_list.get(i).getDisplay_name().equalsIgnoreCase(contact_name.getText().toString().trim())) {
                                search_list.add(temp_list.get(i));
                            }

                        } else {


                        }


                    }

                    if (search_list.size() == 0) {
                        rr.setVisibility(View.GONE);
                    } else {
                        rr.setVisibility(View.VISIBLE);
                        search_list_adapter.notifyDataSetChanged();
                    }


                } else {
                    rr.setVisibility(View.GONE);

                }
            }
        });


        multi_contact_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name=multi_contact_name.getText().toString().trim();
                if (!multi_contact_name.getText().toString().trim().equalsIgnoreCase("")) {

                    multi_search_list.clear();
                    for (int i = 0; i < all_contact_list.size(); i++) {

                        if (all_contact_list.get(i).getDisplay_name().length() > multi_contact_name.getText().toString().trim().length()) {
                            if (multi_contact_name.getText().toString().trim().equalsIgnoreCase((all_contact_list.get(i).getDisplay_name().toString().substring(0,multi_contact_name.getText().toString().trim().length())))) {
                                multi_search_list.add(all_contact_list.get(i));

                            }
                        } else if (all_contact_list.get(i).getDisplay_name().length() == multi_contact_name.getText().toString().trim().length()) {
                            if (all_contact_list.get(i).getDisplay_name().equalsIgnoreCase(multi_contact_name.getText().toString().trim())) {
                                multi_search_list.add(all_contact_list.get(i));
                            }

                        } else {


                        }


                    }

                    if (multi_search_list.size() != 0) {
                        multi_search_adapter.notifyDataSetChanged();
                    }


                } else {
                    multi_search_list=all_contact_list;
                    multi_search_adapter.notifyDataSetChanged();

                }
            }
        });


    }

    @Override
    public void onBackPressed()
    {
        if(visible){
            visible=false;
            multi_selection.setVisibility(View.GONE);
            activity_name.setText("Add Participants");
            multi_search_list=all_contact_list;

        }
        else{
            super.onBackPressed();
        }

    }

    public void addParticipant(View v) {
//        visible=true;
//        multi_selection.setVisibility(View.VISIBLE);
//        search_list.clear();
//        search_list_adapter.notifyDataSetChanged();
//        rr.setVisibility(View.GONE);
//        activity_name.setText(String.valueOf(participant_list.size())+" Selected");
//        multi_search_adapter.notifyDataSetChanged();

    }


    public void createGroup(View v) {
        CreateGroup.activity.finish();
        //TODO api calls
        Log.i("ALL_SIZE","="+String.valueOf(all_contact_list.size()));
        Log.i("TEMP_SIZE","="+String.valueOf(temp_list.size()));
        Log.i("PARTICIPANT_SIZE","="+String.valueOf(participant_list.size()));



        List<String> participants=new ArrayList<>();
        for(int i=0;i<participant_list.size();i++){
            participants.add(participant_list.get(i).getId());
        }

        List<String> admins=new ArrayList<>();
        Log.i("USER_Mobile",""+UserHelper.getId());
        GroupsModel groupsModel=new GroupsModel(UserHelper.getId(),group_subject,participants,admins,"",0);
        groupsModel.setCreation_status(0);
        contact_db.addGroup(groupsModel);
        CreateGroupEvent event=new CreateGroupEvent(groupsModel);
        Log.i("ID@ADDPARTI",""+groupsModel.getId());
        eventBus.post(event);


        Intent i=new Intent(this, CelgramMain.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();

    }


    public void onDone(View v){
        visible=false;
        multi_selection.setVisibility(View.GONE);
        activity_name.setText("Add Participants");
        multi_search_list=all_contact_list;

    }

    public static void participantAdded(ContactsModel contactsModel){
//        int pos;
//        if(visible){
//            //pos=getPosition(contactsModel,)
//            pos=getPosition(contactsModel,4);
//            if(pos!=-1){
//
//
//            }
//
//        }
//        else{
//            pos=getPosition(contactsModel,3);
//            if(pos!=-1){
//                search_list.remove(pos);
//                search_list_adapter.notifyDataSetChanged();
//
//            }
//
//        }
////        if(pos!=-1){
////            search_list.remove(pos);
////            search_list_adapter.notifyItemRemoved(pos);
////        }
//        int position=getPosition(contactsModel,2);
//        if(position!=-1){
//            temp_list.remove(getPosition(contactsModel,4));
//            contactsModel.setChecked(true);
//            all_contact_list.remove(position);
//            all_contact_list.add(position,contactsModel);
//
//        }
//        participant_list.add(contactsModel);
//        participant_adapter.notifyItemInserted(participant_list.size());
//        participant_count.setText(String.valueOf(participant_list.size())+"/100");
//        if(visible) {
//            activity_name.setText(String.valueOf(participant_list.size()) + " Selected");
//        }
        if(!visible){
            search_list.remove(contactsModel);
            search_list_adapter.notifyDataSetChanged();
        }
        temp_list.remove(contactsModel);
        participant_list.add(contactsModel);
        participant_adapter.notifyDataSetChanged();

            int pos=getPosition(contactsModel,2);
            if(pos!=-1){
                all_contact_list.remove(contactsModel);
                contactsModel.setChecked(true);
                all_contact_list.add(pos,contactsModel);
            }
        if(visible){
            activity_name.setText(String.valueOf(participant_list.size())+" Selected");
        }
       participant_count.setText(String.valueOf(participant_list.size())+"/100");

    }

    public static void participantRemoved(ContactsModel contactModel){
//        Log.i("Removed_name","="+contactModel.getDisplay_name());
//        int position=getPosition(contactModel,1);
//        if(position!=-1){
//        participant_list.remove(position);
//           int pos=getPosition(contactModel,2);
//            if(pos!=-1){
//                all_contact_list.remove(pos);
//                contactModel.setChecked(false);
//                all_contact_list.add(pos,contactModel);
//
//            }
//        participant_count.setText(String.valueOf(participant_list.size())+"/100");
//            if(visible) {
//                activity_name.setText(String.valueOf(participant_list.size()) + " Selected");
//            }
//        participant_adapter.notifyItemRemoved(position);
//            temp_list.add(contactModel);
//            Collections.sort(temp_list,new Comparator<ContactsModel>()
//
//            {
//                public int compare (ContactsModel ob1, ContactsModel ob2){
//                    return ob1.getDisplay_name().compareToIgnoreCase(ob2.getDisplay_name());
//                }
//            });
//
//
//        }
        int pos=getPosition(contactModel,2);
        if(pos!=-1){
            all_contact_list.remove(pos);
            contactModel.setChecked(false);
            all_contact_list.add(pos,contactModel);
        }
        temp_list.add(contactModel);
        Collections.sort(temp_list,new Comparator<ContactsModel>()

            {
                public int compare (ContactsModel ob1, ContactsModel ob2){
                    return ob1.getDisplay_name().compareToIgnoreCase(ob2.getDisplay_name());
                }
            });
        participant_list.remove(contactModel);

        participant_adapter.notifyDataSetChanged();

        if(visible){
            activity_name.setText(String.valueOf(participant_list.size())+" Selected");
        }
        participant_count.setText(String.valueOf(participant_list.size())+"/100");

    }

    private static int getPosition(ContactsModel contactsModel,int list){
        if(list==1){
        for(int i=0;i<participant_list.size();i++){
            if(contactsModel.getId().equals(participant_list.get(i).getId())){
                return i;
            }
        }}
        else if(list==2){
            for(int i=0;i<all_contact_list.size();i++){
                if(contactsModel.getId().equals(all_contact_list.get(i).getId())){
                    return i;
                }
            }

        }
        else if(list==3){
            for(int i=0;i<search_list.size();i++){
                if(contactsModel.getId().equals(search_list.get(i).getId())){
                    return i;
                }
            }
        }
        else{
            for(int i=0;i<temp_list.size();i++){
                if(contactsModel.getId().equals(temp_list.get(i).getId())){
                    return i;
                }
            }
        }
        return -1;
    }

    public static int getNameSize(){
        if(visible){
            return multi_contact_name.length();
        }
        else{
            return contact_name.length();
        }
    }

    public static int getListSize(){
        return participant_list.size();
    }

}
