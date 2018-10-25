package com.rohan7055.earnchat.celgram;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.celgram.events.ChangeGroupSubEvent;
import com.rohan7055.earnchat.celgram.events.GroupExitEvent;
import com.rohan7055.earnchat.celgram.events.GroupNameChangeResponse;
import com.rohan7055.earnchat.celgram.events.ParticipantRefreshEvent;
import com.rohan7055.earnchat.celgram.events.RemoveMemResponse;
import com.rohan7055.earnchat.celgram.events.RemoveMemberEvent;
import com.rohan7055.earnchat.celgram.events.UnsubscribeResSelf;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rohan7055.earnchat.Adapter.ParticipantsAdapter;
import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.AppConstants;
import com.rohan7055.earnchat.UserHelper;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.CelgramApi.RestClientGroup;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.rohan7055.earnchat.celgram.collapsingToolbar.HeaderView;
import com.rohan7055.earnchat.celgram.events.AddSingleMemberResponse;
import com.rohan7055.earnchat.celgram.events.ChangeGroupDpEvent;
import com.rohan7055.earnchat.celgram.events.NotifyEvent;
import com.rohan7055.earnchat.celgram.events.UnsubscribeEvent;
import com.rohan7055.earnchat.celgram.events.UnsubscribeResponse;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupInfo extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,ParticipantsAdapter.OnMemberItemClickListener {

    private ImageView edit_group, group_icon , editgroup;
    private LinearLayout exit_group, add_participant, delete_group;
    private GroupsModel group;
    private RecyclerView recyclerView;
    private List<ContactsModel> participants, type_one, type_two, type_one1, type_two2;
    private String creation_details;
    private RelativeLayout participant_rr;
    private ProgressBar progressBar;
    private ContactSQLiteHelper contact_db;
    private ParticipantsAdapter participantsAdapter;
    private TextView participant_count;
    EventBus eventBus;
    private boolean self_admin = true;
    public File filePath;
    ProgressDialog dialog;
    private TextView tvGroupSub;
    @Nullable
    @BindView(R.id.toolbar_header_view)
    protected HeaderView toolbarHeaderView;
    String file_path;
    @Nullable
    @BindView(R.id.float_header_view)
    protected HeaderView floatHeaderView;

    @Nullable
    @BindView(R.id.appbar)
    protected AppBarLayout appBarLayout;

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    String new_filepath = "";

    private boolean isHideToolbarView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_info);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        group = (GroupsModel) getIntent().getSerializableExtra("group");
        //Rohan Thakur modified
        if (group.getCreated_by() == null) {
            group.setCreated_by(UserHelper.getId());
        }
        self_admin = (group.getCreated_by().equals(UserHelper.getId())) && (group.getCreation_status() != 2);

        creation_details = "Created by " + (CelgramMain.number_name.get(group.getCreated_by()) != null
                ? CelgramMain.number_name.get(group.getCreated_by()) : group.getCreated_by()) + " on " + group.getCreated_on();


        edit_group = (ImageView) findViewById(R.id.edit_group);
        editgroup = (ImageView) findViewById(R.id.editgroup);
        group_icon = (ImageView) findViewById(R.id.group_icon);
        exit_group = (LinearLayout) findViewById(R.id.exit_group);
        add_participant = (LinearLayout) findViewById(R.id.add_participant);
        recyclerView = (RecyclerView) findViewById(R.id.participant_recyclerView);
        participant_rr = (RelativeLayout) findViewById(R.id.participants_rr);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        participant_count = (TextView) findViewById(R.id.participant_count);
        delete_group = (LinearLayout) findViewById(R.id.delete_group);
        progressBar.setVisibility(View.VISIBLE);
        tvGroupSub=(TextView)findViewById(R.id.contact_group_name);

        RecyclerView.LayoutManager LayoutManager = new LinearLayoutManager(GroupInfo.this);
        recyclerView.setLayoutManager(LayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        participants = new ArrayList<>();
        type_one = new ArrayList<>();
        type_two = new ArrayList<>();
        type_one1 = new ArrayList<>();
        type_two2 = new ArrayList<>();
        contact_db = new ContactSQLiteHelper(this);

        for (int i = 0; i < group.getMembers().size(); i++) {
            if (!group.getMembers().get(i).equals(UserHelper.getId())) {
                ContactsModel contactsModel = contact_db.getContact(group.getMembers().get(i));
                if (contactsModel.getDisplay_name() == null || contactsModel.getDisplay_name().isEmpty()) {
                    contactsModel = new ContactsModel(group.getMembers().get(i));
                }
                if (contactsModel.getContact_type() == 1) {
                    type_one.add(contactsModel);
                } else {
                    type_two.add(contactsModel);
                }
            }else{
                SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
                String fname = preferences.getString("firstname", "");
                String lname = preferences.getString("lastname", "");
                String uname = preferences.getString("username", "");
                String brand = preferences.getString("brand", "");
                String img = preferences.getString("imgname", "");
                String id = preferences.getString("id", "");
                String email = preferences.getString("email", "");

                ContactsModel contactsModel = new ContactsModel(id, uname, email, "You", brand, img);
                if (contactsModel.getContact_type() == 1) {
                    type_one.add(contactsModel);
                } else {
                    type_two.add(contactsModel);
                }
            }
        }

        for (int i = 0; i < group.getAdmins().size(); i++) {
            if (!group.getAdmins().get(i).equals(UserHelper.getId())) {
                ContactsModel contactsModel = contact_db.getContact(group.getAdmins().get(i));
                if (contactsModel.getDisplay_name() == null || contactsModel.getDisplay_name().isEmpty()) {
                    contactsModel = new ContactsModel(group.getAdmins().get(i));
                }
                if (contactsModel.getContact_type() == 1) {
                    type_one1.add(contactsModel);
                } else {
                    type_two2.add(contactsModel);
                }
            }else{
                SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
                String fname = preferences.getString("firstname", "");
                String lname = preferences.getString("lastname", "");
                String uname = preferences.getString("username", "");
                String brand = preferences.getString("brand", "");
                String img = preferences.getString("imgname", "");
                String id = preferences.getString("id", "");
                String email = preferences.getString("email", "");

                ContactsModel contactsModel = new ContactsModel(id, uname, email, "You", brand, img);
                if (contactsModel.getContact_type() == 1) {
                    type_one1.add(contactsModel);
                } else {
                    type_two2.add(contactsModel);
                }
            }
        }

        List<ContactsModel> temp = new ArrayList<>();
        temp.addAll(type_one);

        for (ContactsModel c1 : type_one1){
            for (ContactsModel c2 : temp){
                if (c1.getId().equals(c2.getId())){
                    type_one.remove(c2);
                }
            }
        }

        temp.clear();
        temp.addAll(type_two);

        for (ContactsModel c1 : type_two2){
            for (ContactsModel c2 : temp){
                if (c1.getId().equals(c2.getId())){
                    type_two.remove(c2);
                }
            }
        }

        /*type_one.removeAll(type_one1);
        type_two.removeAll(type_two2);*/
        type_one.addAll(type_one1);
        type_two.addAll(type_two2);

        Collections.sort(type_one, new Comparator<ContactsModel>() {
            public int compare(ContactsModel ob1, ContactsModel ob2) {
                return (ob1.getDisplay_name().compareToIgnoreCase(ob2.getDisplay_name()));
            }
        });

        Collections.sort(type_two, new Comparator<ContactsModel>() {
            public int compare(ContactsModel ob1, ContactsModel ob2) {
                return (ob1.getDisplay_name().compareToIgnoreCase(ob2.getDisplay_name()));
            }
        });

        participants.addAll(type_one);
        participants.addAll(type_two);

        participantsAdapter = new ParticipantsAdapter(getApplicationContext(), this, participants, self_admin,UserHelper.getId(),group.getId());
        participantsAdapter.setOnMemberItemClickListener(GroupInfo.this);
        recyclerView.setAdapter(participantsAdapter);

        participant_count.setText(String.valueOf(participants.size()) + " of 100");

        if (self_admin) {
            add_participant.setVisibility(View.VISIBLE);
        } else {
            add_participant.setVisibility(View.GONE);
        }

        progressBar.setVisibility(View.GONE);
        participant_rr.setVisibility(View.VISIBLE);

        if (group.getCreation_status() != 2) {
            edit_group.setOnClickListener(editGroup);
            exit_group.setOnClickListener(exitGroup2);
            editgroup.setOnClickListener(editGroup);
            add_participant.setOnClickListener(addParticipant);
            group_icon.setOnClickListener(change_grp_icon);
        } else if (group.getCreation_status() == 2) {
            edit_group.setVisibility(View.GONE);
            editgroup.setVisibility(View.GONE);
            exit_group.setVisibility(View.GONE);
            delete_group.setVisibility(View.VISIBLE);
            delete_group.setOnClickListener(deleteGroup);
        }

        Log.d("Anuraj_dp", AppConstants.UPLOADS_URL_GROUP + "uploads/groups/"+group.getDp_url());
        //Picasso.with(this).load(AppConstants.UPLOADS_URL_ADVERTISERS + "groups/" + group.getId() + "/" + group.getDp_url()).error(R.drawable.ic_group).fit().centerCrop().into(group_icon);
        Picasso.with(this).load(AppConstants.UPLOADS_URL_GROUP + "uploads/groups/"+group.getDp_url()).error(R.drawable.ic_group).fit().centerCrop().into(group_icon);

        initUi();
    }

    private void initUi() {
        appBarLayout.addOnOffsetChangedListener(this);
        toolbarHeaderView.bindTo(group.getDisplay_name(), creation_details);
        floatHeaderView.bindTo(group.getDisplay_name(), creation_details);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }


    View.OnClickListener editGroup = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), EditGroup.class);
            i.putExtra("getgroup", group);
            startActivityForResult(i, 1234);
        }
    };

    View.OnClickListener exitGroup2=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(GroupInfo.this);

            // Setting Dialog Title
            alertDialog.setTitle("Exit \""+group.getDisplay_name()+"\" group?");




            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {

                    // Write your code here to invoke YES event
                    UnsubscribeEvent event=new UnsubscribeEvent(group.getId());
                    eventBus.post(event);
                    launchDialog();
                }
            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnsubscribeResSelf(final UnsubscribeResSelf event){
        Log.i("EXIT Self",""+event.isSuccess());
        if(event.isSuccess()){
            try {

                SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
                final String selfId = preferences.getString("mobile", "");

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                final Message msg=new Message(group.getId(),selfId,
                        true,11,"",timestamp,0,"","","");
                group.setCreation_status(2);
                group.setLatest_msg(msg);
                final GroupExitEvent groupExitEvent = new GroupExitEvent(group.getId(),selfId,timestamp);
                final int pos=getItemPosition(selfId);
                if(pos!=-1) {
                    participants.remove(pos);
                }
                eventBus.post(groupExitEvent);
                eventBus.post(msg);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //update ui on group exit
                        dialog.dismiss();
                        exit_group.setVisibility(View.INVISIBLE);
                        add_participant.setVisibility(View.INVISIBLE);
                        edit_group.setVisibility(View.INVISIBLE);
                        editgroup.setVisibility(View.INVISIBLE);
                        delete_group.setVisibility(View.INVISIBLE);
                        delete_group.setOnClickListener(deleteGroup);
                        participantsAdapter.notifyItemRemoved(pos);


                    }
                });
            }catch (Exception e)
            {
                Log.i("UNSUBSRESPONSE",e.getMessage());
            }

        }
        else{
            dialog.dismiss();
            showToast("Check internet connection and try again");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnsubscribeResponse(final UnsubscribeResponse event){
        Log.i("EXIT",event.getMessage().getConvo_partner()+""+event.isUnsubscibed());
        if(event.isUnsubscibed()){
            try {

                SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
                final String selfId = preferences.getString("mobile", "");

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                final Message msg=new Message(group.getId(),selfId,
                        true,11,"",timestamp,0,"","","");
                group.setCreation_status(2);
                group.setLatest_msg(msg);
                final GroupExitEvent groupExitEvent = new GroupExitEvent(group.getId(),selfId,timestamp);
                final int pos=getItemPosition(selfId);
                if(pos!=-1) {
                    participants.remove(pos);
                }
                eventBus.post(groupExitEvent);
                eventBus.post(msg);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //update ui on group exit
                        // dialog.dismiss();
                        exit_group.setVisibility(View.INVISIBLE);
                        add_participant.setVisibility(View.INVISIBLE);
                        edit_group.setVisibility(View.INVISIBLE);
                        editgroup.setVisibility(View.INVISIBLE);
                        delete_group.setVisibility(View.INVISIBLE);
                        delete_group.setOnClickListener(deleteGroup);
                        participantsAdapter.notifyItemRemoved(pos);


                    }
                });
            }catch (Exception e)
            {
                Log.i("UNSUBSRESPONSE",e.getMessage());
            }

        }
        else{
            //dialog.dismiss();
            showToast("Check internet connection and try again");
        }
    }


    private int getItemPosition(String member) {
        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i) instanceof ContactsModel) {
                if (member.equals(((ContactsModel) participants.get(i)).getId())) {
                    return i;
                }

            }
        }

        return -1;
    }

    private int getItemPositionOne(String member) {
        for (int i = 0; i < type_one.size(); i++) {
            if (type_one.get(i) instanceof ContactsModel) {
                if (member.equals(((ContactsModel) type_one.get(i)).getId())) {
                    return i;
                }

            }
        }

        return -1;
    }

    private void launchDialog()
    {
        dialog = ProgressDialog.show(GroupInfo.this, "",
                "Exiting. Please wait...", true);


    }
    private void dismissdialog()
    {
        dialog.dismiss();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!eventBus.isRegistered(this))
        {
            eventBus.register(this);
        }
        Picasso.with(this).load(AppConstants.UPLOADS_URL_GROUP + "uploads/groups/"+group.getDp_url()).error(R.drawable.ic_group).fit().centerCrop().into(group_icon);
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    View.OnClickListener exitGroup = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            new MaterialDialog.Builder(GroupInfo.this)
                    .content("Do you really want to exit?")
                    .positiveText("Yes")
                    .negativeText("No")
                    .show();
            new MaterialDialog.Builder(GroupInfo.this)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            UnsubscribeEvent event = new UnsubscribeEvent(group.getId());
                            eventBus.post(event);
                            Toast.makeText(GroupInfo.this, "You successfully exited the group", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // TODO
                        }
                    });
        }
    };

    View.OnClickListener addParticipant = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ArrayList<String> group_members = new ArrayList<>();
            for (ContactsModel cm : type_one) {
                group_members.add(cm.getId());
            }

            Intent intent = new Intent(GroupInfo.this, AddSingleMember.class);
            intent.putExtra("group_id", group.getId());
            intent.putStringArrayListExtra("added_members", group_members);
            startActivity(intent);
        }
    };

    View.OnClickListener change_grp_icon = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(GroupInfo.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(GroupInfo.this);
            }
            builder.setTitle("8Chat")
                    .setMessage("Do you want to change the group icon?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            final int ACTIVITY_SELECT_IMAGE = 12345;
                            startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    };

    View.OnClickListener deleteGroup = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1234:

                    if(resultCode==RESULT_OK){
                        String subject=data.getStringExtra("new_subject");

                        if(!subject.equals("")&&subject!=null)
                        {
                            ChangeGroupSubEvent changeGroupSubEvent=new ChangeGroupSubEvent(group.getId(),subject);
                            eventBus.post(changeGroupSubEvent);
                        }

                    }
                    break;


            case 12345:
                if(resultCode==RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    new_filepath = cursor.getString(columnIndex);
                    cursor.close();
                    Picasso.with(getApplicationContext()).load(new File(new_filepath)).error(R.drawable.ic_group).fit().centerCrop().into(group_icon);
                    UploadDpPost(new_filepath);
                }
                break;
            default:
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GroupSubChange(final GroupNameChangeResponse response)
    {
        if(response.isSucces())
        {
            contact_db.updateGroupName(group.getId(),response.getGroupname());
            group.setDisplay_name(response.getGroupname());
            toolbarHeaderView.bindTo(group.getDisplay_name(), creation_details);
            floatHeaderView.bindTo(group.getDisplay_name(), creation_details);

        }
    }



    public static RequestBody createRequestBody(String field) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), field);
    }



    @Subscribe
    public void onListRefresh(ParticipantRefreshEvent event)
    {
        if(event.isrefresh())
        {
           // GetGroupInfo();
        }
    }
    public void UploadDpPost(String filePath) {
        File f = new File(filePath);
        Bitmap bm = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        final String title = f.getName();

        RestClientGroup.ApiGroupInterface service = RestClientGroup.getClient();
        Call<group_dp_model> call = service.uploadDP(title, encodedImage);
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Uploading!. Please wait...", true);
        call.enqueue(new Callback<group_dp_model>() {
            @Override
            public void onResponse(Call<group_dp_model> call, Response<group_dp_model> response) {
                group_dp_model groupDpUpload = response.body();
                try {
                    if (groupDpUpload.isStatus()) {
                        //Log.d("Anuraj_upload", file_path);
                        dialog.dismiss();
                        group.setDp_url(title);
                        contact_db.updateGroupDp(group.getId(), title);
                        final SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
                        String mobile = preferences.getString("mobile", "");
                        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                        Message msg = new Message(group.getId(), mobile,
                                true, 10, "", timestamp, 0, groupDpUpload.getImageName(), "", "");
                        group.setLatest_msg(msg);
                        ChangeGroupDpEvent event = new ChangeGroupDpEvent(group);
                        NotifyEvent notifyEvent = new NotifyEvent(group.getId());
                        eventBus.post(notifyEvent);
                        eventBus.post(event);
                        eventBus.post(msg);
                        Toast.makeText(getApplicationContext(), "uploaded successfully", Toast.LENGTH_LONG).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<group_dp_model> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }


    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifyEvent(NotifyEvent event) throws InterruptedException {
        Thread.sleep(500);
        Picasso.with(this).load(AppConstants.UPLOADS_URL_GROUP + "uploads/groups/"+group.getDp_url()).error(R.drawable.ic_group).fit().centerCrop().into(group_icon);

    }


    @Subscribe
    public void memberAddNotify(AddSingleMemberResponse event) {
        if (event.isSuccess()) {
            ContactsModel contactsModel = contact_db.getContact(event.getContact_id());
            participants.clear();
            Log.i("Participant_size", "" + participants.size());
            type_one.add(contactsModel);
            Collections.sort(type_one, new Comparator<ContactsModel>() {
                public int compare(ContactsModel ob1, ContactsModel ob2) {
                    return (ob1.getDisplay_name().compareToIgnoreCase(ob2.getDisplay_name()));
                }
            });
            participants.addAll(type_one);
            participants.addAll(type_two);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    participantsAdapter.notifyDataSetChanged();

                }
            });

        } else {
            showToast("Check internet connection and try again");
        }
    }

    private void showToast(String content) {
        Toast toast = Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Subscribe
    public void onMemberRemoved(RemoveMemResponse response)
    {
        Log.i("GroupInfoREM",response.getMember());
        dialog.dismiss();
        if(response.isRemoved())
        {
            final int pos = getItemPosition(response.getMember());
            final int postypeone=getItemPositionOne(response.getMember());
            if(pos!=-1)
            {
                participants.remove(pos);
                type_one.remove(postypeone);
                for(int i =0;i<group.getMembers().size();i++)
                {
                    if(response.getMember().equals(group.getMembers().get(i)))
                    {
                        group.getMembers().remove(i);
                    }
                }
                StringBuilder numbers = new StringBuilder();
                for (String number : group.getMembers()) {
                    numbers.append(number + ",");
                }
                numbers.deleteCharAt(numbers.length() - 1);
                //local db update required
                contact_db.updateGroupMembers(group.getId(),String.valueOf(numbers));

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    participantsAdapter.notifyItemRemoved(pos);
                }
            });
        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GroupInfo.this,"Retry check internet",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    @Override
    public void onRemoveItem(String member, String groupId) {
        Log.i("GroupInfoREM","Interface Method called");
        RemoveMemberEvent removeMemberEvent = new RemoveMemberEvent(groupId,member);
        eventBus.post(removeMemberEvent);
        dialog = ProgressDialog.show(GroupInfo.this, "",
                "Removing. Please wait...", true);

    }
}