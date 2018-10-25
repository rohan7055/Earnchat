package com.rohan7055.earnchat.celgram.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rohan7055.earnchat.Adapter.GetContactAdapter;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.ContactsModel;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Anuraj Jain on 8/25/2017.
 */

public class advertiser_newChat extends Fragment{

    RecyclerView recyclerView;
    GetContactAdapter contactAdapter;
    ContactSQLiteHelper contact_db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.advertiser_new_chat, container, false);

        contact_db=new ContactSQLiteHelper(getActivity());

        List<ContactsModel> cList=new ArrayList<>();
        cList= contact_db.getAllContacts(1);

        List<ContactsModel> contactList = new ArrayList<>();
        //cList.addAll(contactList);

        for(int i=0; i<cList.size(); i++){
            Log.d("Anuraj_index", cList.get(i).getDisplay_name());
            if(cList.get(i).getUname().startsWith("Advertiser") || cList.get(i).getUname().startsWith("customer_care")){
                Log.d("Anuraj_toRemove", cList.get(i).getDisplay_name());
                contactList.add(cList.get(i));
            }
        }

        contactAdapter=new GetContactAdapter(getActivity(),contactList,getActivity(),0);

        Collections.sort(contactList,new Comparator<ContactsModel>()
        {
            public int compare (ContactsModel ob1, ContactsModel ob2){
                return ob1.getDisplay_name().compareToIgnoreCase(ob2.getDisplay_name());
            }
        });

        recyclerView=(RecyclerView)rootView.findViewById(R.id.create_chat_view);
        RecyclerView.LayoutManager LayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(LayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(contactAdapter);

        //Anuraj jain
        /*Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d("Anuraj_position", String.valueOf(position));

                if(position == 0){
                    //Toast.makeText(NewChat.this, "postion 0", Toast.LENGTH_SHORT).show();
                    List<ContactsModel> cList = new ArrayList<>();
                    cList = contact_db.getAllContacts(1);
                    clear();
                    for(int i=0; i<cList.size(); i++){
                        Log.d("Anuraj_index", cList.get(i).getDisplay_name());
                        if(cList.get(i).getUname().startsWith("Advertiser") || cList.get(i).getUname().startsWith("customer_care")){
                            Log.d("Anuraj_toRemove", cList.get(i).getDisplay_name());
                            contactList.add(cList.get(i));
                        }
                    }
                    contactAdapter.notifyItemRangeInserted(0, contactList.size());
                }
                else {
                    //Toast.makeText(NewChat.this, "postion 0", Toast.LENGTH_SHORT).show();
                    List<ContactsModel> cList = new ArrayList<>();
                    cList = contact_db.getAllContacts(1);
                    clear();
                    for(int i=0; i<cList.size(); i++){
                        Log.d("Anuraj_index", cList.get(i).getDisplay_name());
                        if(cList.get(i).getUname().startsWith("Media") || cList.get(i).getUname().startsWith("customer_care")){
                            Log.d("Anuraj_toRemove", cList.get(i).getDisplay_name());
                            contactList.add(cList.get(i));
                        }
                    }
                    contactAdapter.notifyItemRangeInserted(0, contactList.size());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        return rootView;
    }
}