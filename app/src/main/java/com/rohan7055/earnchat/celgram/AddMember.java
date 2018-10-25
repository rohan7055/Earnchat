package com.rohan7055.earnchat.celgram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rohan7055.earnchat.Adapter.AddMemberAdapter;
import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.UserHelper;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.rohan7055.earnchat.celgram.events.CreateGroupEvent;
import com.rohan7055.earnchat.celgram.events.DialogdismissEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddMember extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static RecyclerView add_member_view,added_member_view,search_view;
    private static List<ContactsModel> add_member,added_member,search_list;
    private static AddMemberAdapter add_adapter,added_adapter,search_adapter;
    private static SearchView searchView;
    private static int name_size;
    private static ActionBar actionBar;
    private String group_subject,file_path;
    public  ProgressDialog dialog;

    private EventBus eventBus;
    ContactSQLiteHelper contact_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        group_subject=getIntent().getStringExtra("group_subject");
        file_path=getIntent().getStringExtra("file_path");

        Log.d("Anuraj_filePath", file_path);

        eventBus=EventBus.getDefault();
        eventBus.register(this);
        View view= getLayoutInflater().inflate(R.layout.custom_actionbar_addmember,null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);

        actionBar=getSupportActionBar();
        contact_db=new ContactSQLiteHelper(this);

        add_member_view=(RecyclerView)findViewById(R.id.add_member_view);
        added_member_view=(RecyclerView)findViewById(R.id.added_member_view);
        search_view=(RecyclerView)findViewById(R.id.search_view);

        RecyclerView.LayoutManager LayoutManager = new LinearLayoutManager(AddMember.this);
        add_member_view.setLayoutManager(LayoutManager);
        add_member_view.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AddMember.this,LinearLayoutManager.HORIZONTAL,false);
        added_member_view.setLayoutManager(mLayoutManager);
        added_member_view.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(AddMember.this);
        search_view.setLayoutManager(nLayoutManager);
        search_view.setItemAnimator(new DefaultItemAnimator());

        add_member=new ArrayList<>();
        added_member=new ArrayList<>();
        search_list=new ArrayList<>();

        add_member=contact_db.getAllContacts(1);

        Collections.sort(add_member,new Comparator<ContactsModel>()

        {
            public int compare (ContactsModel ob1, ContactsModel ob2){
                return ob1.getDisplay_name().compareToIgnoreCase(ob2.getDisplay_name());
            }
        });

        added_adapter=new AddMemberAdapter(added_member,this,0);
        add_adapter=new AddMemberAdapter(add_member,this,1);
        search_adapter=new AddMemberAdapter(search_list,this,2);

        add_member_view.setAdapter(add_adapter);
        search_view.setAdapter(search_adapter);
        added_member_view.setAdapter(added_adapter);
    }

    public void addMember(String id){

       try {
           if (!searchView.isIconified()) {
               searchView.setIconified(true);
           }
       }catch(NullPointerException e)
       {
          // Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
       }
        setVisibility(false);
        int position=getPosition(id,1);
        if(position!=-1){
            add_member.get(position).setChecked(true);
            add_adapter.notifyItemChanged(position);
            added_member.add(add_member.get(position));
           // actionBar.setTitle("Participants : "+added_member.size()+" of 100");
            added_adapter.notifyItemInserted(added_member.size()-1);
        }
    }



    public void deleteMember(String id){
        int position=getPosition(id,2);
        int pos=getPosition(id,1);
        setVisibility(false);
        if(position!=-1){
            added_member.remove(position);
            //actionBar.setTitle("Participants : "+added_member.size()+" of 100");
            added_adapter.notifyItemRemoved(position);
            if(added_member.size()==0){
                added_member_view.setVisibility(View.GONE);
            }
            if(pos!=-1){
                add_member.get(pos).setChecked(false);
                add_adapter.notifyItemChanged(pos);
            }
        }

    }

    private int getPosition(String id,int list){
        if(list==1) {
            for (int i = 0; i < add_member.size(); i++) {
                if (id.equals(add_member.get(i).getId())) {
                    return i;
                }
            }
            return -1;
        }
        else{
            for (int i = 0; i < added_member.size(); i++) {
                if (id.equals(added_member.get(i).getId())) {
                    return i;
                }
            }
            return -1;
        }
    }

    public int getParticipantsCount(){
        return added_member.size();
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
            for (int i = 0; i < add_member.size(); i++) {

                if (add_member.get(i).getDisplay_name().length() > name.length()) {
                    if (name.equalsIgnoreCase((add_member.get(i).getDisplay_name().toString().substring(0,name.length())))) {
                        search_list.add(add_member.get(i));

                    }
                } else if (add_member.get(i).getDisplay_name().length() == name.length()) {
                    if (add_member.get(i).getDisplay_name().equalsIgnoreCase(name)) {
                        search_list.add(add_member.get(i));
                    }

                }

            }

            if (search_list.size() != 0) {
                setVisibility(true);
                search_adapter.notifyDataSetChanged();
            }


        }

        else{
            setVisibility(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (search_view != null) {
            if (!searchView.isIconified()) {
                searchView.setIconified(true);
                setVisibility(false);
            }
        }
        else {
            super.onBackPressed();
        }
    }



    private void setVisibility(boolean search_view_visible){
        if(search_view_visible){
            added_member_view.setVisibility(View.GONE);
            add_member_view.setVisibility(View.GONE);
            search_view.setVisibility(View.VISIBLE);
        }
        else{
            added_member_view.setVisibility(View.VISIBLE);
            add_member_view.setVisibility(View.VISIBLE);
            search_view.setVisibility(View.GONE);
        }
    }

    public int getNameSize(){
        return name_size;
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public void done(View v){
        CreateGroup.activity.finish();

        Log.i("PARTICIPANT_SIZE","="+String.valueOf(added_member.size()));

        if(added_member.size()!=0) {

            List<String> participants = new ArrayList<>();
            for (int i = 0; i < added_member.size(); i++) {
                participants.add(added_member.get(i).getId());
                Log.d("Anuraj_participant", added_member.get(i).getId());
            }
            //This is here to get admin id the group creater
            List<String> admins = new ArrayList<>();
            admins.add(UserHelper.getId());

            GroupsModel groupsModel = new GroupsModel(UserHelper.getId(), group_subject, participants, admins, file_path, 0);
            groupsModel.setCreation_status(0);
            Log.d("Anuraj_display_name",groupsModel.getDisplay_name());
            contact_db.addGroup(groupsModel);
            CreateGroupEvent event = new CreateGroupEvent(groupsModel);
            //rohan changed :no need to to this
            //ChangeGroupDpEvent eventDp = new ChangeGroupDpEvent(groupsModel);
            eventBus.post(event);
           dialog = ProgressDialog.show(AddMember.this, "", "Creating Group Please wait", true);


        }
        else{
            Toast.makeText(getApplicationContext(),"Add at least one participant",Toast.LENGTH_SHORT).show();
        }
    }
    //rohan mod 8chat
    @Subscribe
    public void notifyGroupCreated(DialogdismissEvent event)
    {
        Log.i("Created Group dia",event.getGroupId());


        if(!event.getResult())
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();

                    Toast.makeText(AddMember.this,"Failed to create group",Toast.LENGTH_LONG).show();

                }
            });
        }
        else {
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      dialog.dismiss();
                      Toast.makeText(AddMember.this, "Group Created", Toast.LENGTH_LONG).show();
                      Intent i2 = new Intent(AddMember.this, CelgramMain.class);
                      startActivity(i2);
                      finish();
                  }
              });





        }
    }

}
