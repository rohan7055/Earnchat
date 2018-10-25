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
 * Created by lenovo on 8/25/2017.
 */

public class media_owner_new_chat extends Fragment{

    RecyclerView recyclerView;
    GetContactAdapter contactAdapter;
    ContactSQLiteHelper contact_db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.media_owner_new_chat, container, false);

        contact_db=new ContactSQLiteHelper(getActivity());

        List<ContactsModel> cList=new ArrayList<>();
        cList= contact_db.getAllContacts(1);

        List<ContactsModel> contactList = new ArrayList<>();
        //cList.addAll(contactList);

        for(int i=0; i<cList.size(); i++){
            Log.d("Anuraj_index", cList.get(i).getDisplay_name());
            if(cList.get(i).getUname().startsWith("Media") || cList.get(i).getUname().startsWith("customer_care")){
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

        return rootView;
    }

}
