package com.rohan7055.earnchat.celgram;

import android.app.Activity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rohan7055.earnchat.Adapter.AddMemberAdapter;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.rohan7055.earnchat.celgram.events.AddSingleMemberEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AddSingleMember extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static List<ContactsModel> contactList,search_list;
    private AddMemberAdapter addMemberAdapter,searchAdapter;
    private ContactSQLiteHelper contact_db;
    private static EventBus eventBus;
    private static String group_id;
    private SearchView searchView;
    private static int name_size;
    private RecyclerView contact_view,search_view;
    private ArrayList<String> added_members=new ArrayList<>();
    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_single_member);

        context = AddSingleMember.this;

        group_id=getIntent().getStringExtra("group_id");
        added_members=getIntent().getStringArrayListExtra("added_members");

        eventBus=EventBus.getDefault();
        contact_db=new ContactSQLiteHelper(this);

        contactList=new ArrayList<>();
        search_list=new ArrayList<>();

        contact_view=(RecyclerView)findViewById(R.id.contact_view);
        search_view=(RecyclerView)findViewById(R.id.search_view);

        contactList=contact_db.getAllContacts(1);
        for(int i=0;i<contactList.size();i++){
            if(added_members.contains(contactList.get(i).getId())){
                contactList.get(i).setGroup_member(true);
            }
        }

        addMemberAdapter=new AddMemberAdapter(contactList,this,3);
        searchAdapter=new AddMemberAdapter(search_list,this,4);

        RecyclerView.LayoutManager LayoutManager=new LinearLayoutManager(AddSingleMember.this);
        RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(AddSingleMember.this);

        contact_view.setLayoutManager(LayoutManager);
        contact_view.setItemAnimator(new DefaultItemAnimator());
        search_view.setLayoutManager(mLayoutManager);
        search_view.setItemAnimator(new DefaultItemAnimator());

        contact_view.setAdapter(addMemberAdapter);
        search_view.setAdapter(searchAdapter);


    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_participant_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String name=newText.trim();
        name_size=name.length();
        if (!name.equalsIgnoreCase("")) {

            search_list.clear();
            for (int i = 0; i < contactList.size(); i++) {

                if (contactList.get(i).getDisplay_name().length() > name.length()) {
                    if (name.equalsIgnoreCase((contactList.get(i).getDisplay_name().toString().substring(0,name.length())))) {
                        search_list.add(contactList.get(i));

                    }
                } else if (contactList.get(i).getDisplay_name().length() == name.length()) {
                    if (contactList.get(i).getDisplay_name().equalsIgnoreCase(name)) {
                        search_list.add(contactList.get(i));
                    }

                }

            }

                setVisibility(true);
                searchAdapter.notifyDataSetChanged();


        }

        else{
            setVisibility(false);
        }
        return true;
    }

    private void setVisibility(boolean search_view_visibility){
        if(search_view_visibility){
        contact_view.setVisibility(View.GONE);
        search_view.setVisibility(View.VISIBLE);
        }
        else{
            contact_view.setVisibility(View.VISIBLE);
            search_view.setVisibility(View.GONE);
        }
    }

    public void addMember(Activity activity, String id){
        //Toast.makeText(AddSingleMember.this, "Group member added", Toast.LENGTH_SHORT).show();
        AddSingleMemberEvent event=new AddSingleMemberEvent(group_id,id);
        eventBus.post(event);
        //super.onBackPressed();
        Toast.makeText(activity, "Group member will be added", Toast.LENGTH_SHORT).show();
        activity.finish();
    }

    public int getName_size(){
        return name_size;
    }
}
