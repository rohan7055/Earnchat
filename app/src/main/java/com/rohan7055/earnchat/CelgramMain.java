package com.rohan7055.earnchat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.Adapter.ActiveChatAdapter;
import com.rohan7055.earnchat.Adapter.GetContactAdapter;
import com.rohan7055.earnchat.Adapter.chatListAdapter;
import com.rohan7055.earnchat.Model.StatusModel;
import com.rohan7055.earnchat.celgram.AlertScreenActivity;
import com.rohan7055.earnchat.celgram.CelgramApi.RestClientS3Group;
import com.rohan7055.earnchat.celgram.CreateGroup;
import com.rohan7055.earnchat.celgram.ResponseModels.CheckContacts;
import com.rohan7055.earnchat.celgram.ResponseModels.CheckContacts8Chat;
import com.rohan7055.earnchat.celgram.ResponseModels.Result;
import com.rohan7055.earnchat.celgram.ResponseModels.Result8Chat;
import com.rohan7055.earnchat.celgram.RoundedCornersTransform;
import com.rohan7055.earnchat.celgram.SettingsActivity;
import com.rohan7055.earnchat.celgram.events.AddMemExitEvent;
import com.rohan7055.earnchat.celgram.events.AlertUser;
import com.rohan7055.earnchat.celgram.events.ChangeLastSeenAvail;
import com.rohan7055.earnchat.celgram.events.ConnectivityEvent;
import com.rohan7055.earnchat.celgram.events.DisconnectEvent;
import com.rohan7055.earnchat.celgram.events.GroupExitEvent;
import com.rohan7055.earnchat.celgram.events.GroupNameChangeResponse;
import com.rohan7055.earnchat.celgram.events.OnDestroyEvent;
import com.rohan7055.earnchat.celgram.events.RemRecvResponseEvent;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.rohan7055.earnchat.celgram.CelgramApi.RestClient;
import com.rohan7055.earnchat.celgram.CelgramApi.RestClientUser;
import com.rohan7055.earnchat.celgram.ChatListModel;
import com.rohan7055.earnchat.celgram.ContactsModel;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.rohan7055.earnchat.celgram.DataBase_Helper.MessageSQLiteHelper;
import com.rohan7055.earnchat.celgram.GroupsModel;
import com.rohan7055.earnchat.celgram.Message;
import com.rohan7055.earnchat.celgram.NewChat;
import com.rohan7055.earnchat.celgram.ResponseModels.CheckMagicContacts;
import com.rohan7055.earnchat.celgram.ResponseModels.ResultMagic;
import com.rohan7055.earnchat.celgram.events.AddSingleMemberResponse;
import com.rohan7055.earnchat.celgram.events.ChangeAvailability;
import com.rohan7055.earnchat.celgram.events.GroupCreationNotify;
import com.rohan7055.earnchat.celgram.events.GroupMessage;
import com.rohan7055.earnchat.celgram.events.GroupTypingEvent;
import com.rohan7055.earnchat.celgram.events.NotifyEvent;
import com.rohan7055.earnchat.celgram.events.StringEvent;
import com.rohan7055.earnchat.celgram.events.UnsubscribeEvent;
import com.rohan7055.earnchat.celgram.fragments.ActiveChatFragment;
import com.rohan7055.earnchat.celgram.fragments.SettingsFragment;
import com.rohan7055.earnchat.celgram.service.MessageReceive;
import com.rohan7055.earnchat.intro.intro;
import com.rohan7055.earnchat.utils.MultiChoiceHelper;
import com.squareup.picasso.Picasso;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class CelgramMain extends AppCompatActivity implements ActiveChatAdapter.InteractionListener{

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final long RIPPLE_DURATION = 150;
    public static List<ContactsModel> contactList = new ArrayList<>();
    public static List<GroupsModel> groupList = new ArrayList<>();
    public static HashMap<String, String> number_name = new HashMap<>();
    public static HashMap<String, String> magicnumber_userid = new HashMap<>();
    public static HashMap<String, String> number_cname = new HashMap<>();
    public static HashMap<String, String> userid_cnum = new HashMap<>();
    //Rohan change ActiveChatadpater to public access
    public ActiveChatAdapter activeChatAdapter;
    public static String chat_color;
    public static String current_chat = "";
    private static String UniqueUsername;
    public List<Object> activeChatList = new ArrayList<>();
    public List<ChatListModel> chatList = new ArrayList<>();
    public Boolean flag = false;
    TextView fullname, userName;
    List<String> magicnumbers = new ArrayList<String>();
    List<String> numbers = new ArrayList<String>();
    Handler h = new Handler();
    Runnable runnable;
    MessageSQLiteHelper db_helper;
    ContactSQLiteHelper contact_db;
    String csn = "";
    int num;
    ImageView iv_start_chat;
    TextView tv_start;
    TextView tv_click_here;
    EventBus eventBus;
    @BindView(R.id.toolbar_celegram)
    Toolbar toolbar;
    @BindView(R.id.root_celgram)
    FrameLayout root;
    @BindView(R.id.rl_cel)
    RelativeLayout rl_cel;
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.content_hamburger_celgram)
    View contentHamburger;
    GuillotineAnimation animation;
    String filename;
    private ProgressWheel progressWheel;
    private RecyclerView recyclerView;
    private GetContactAdapter mAdapter;
    private boolean listUpdated = false;
    private Boolean isFabOpen = false;
    private CircleImageView profil;
    public static boolean active = false;
    private BottomNavigationView bottomNavigationView;
    private TextView tvEditChat,tvHeadingText;
    private ImageView ivNewChat,ivEditBack;
    private MultiChoiceHelper multiChoiceHelper;
    private Button btnDeletechats;
    private RelativeLayout rl_deleteLayout;
    Intent mServiceIntent;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celgram_main);
        filename = getIntent().getStringExtra("filename");
        iv_start_chat = (ImageView) findViewById(R.id.iv_start_chat);
        tv_start = (TextView) findViewById(R.id.tv_start);
        tv_click_here = (TextView) findViewById(R.id.tv_click_here);
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottom_navigation);
        tvEditChat=(TextView)findViewById(R.id.tv_edit);
        ivNewChat=(ImageView)findViewById(R.id.iv_new_chat);
        tvHeadingText=(TextView)findViewById(R.id.heading_txt);
        ivEditBack=(ImageView)findViewById(R.id.iv_edit_back);
        btnDeletechats=(Button)findViewById(R.id.btn_delete_chats);
        rl_deleteLayout=(RelativeLayout)findViewById(R.id.rl_deletechats);

        tvEditChat.setVisibility(View.INVISIBLE);




        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        progressWheel.setVisibility(View.VISIBLE);

        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        UniqueUsername = preferences.getString("username", "");
        UserHelper.loadUser(CelgramMain.this);

        eventBus = EventBus.getDefault();
        eventBus.register(this);


        mServiceIntent = new Intent(this, MessageReceive.class);
        if (isMyServiceRunning(MessageReceive.class)) {
            Log.i("yolocelgram", "service is already running");
        } else {
            startService(mServiceIntent);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.action_settings:
                        Intent i=new Intent(CelgramMain.this, SettingsActivity.class);
                        startActivity(i);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.action_chats:
                        break;
                }


                return true;
            }
        });

        db_helper = new MessageSQLiteHelper(this);
        contact_db = new ContactSQLiteHelper(this);

        ContactsModel contactsModel = contact_db.getContact("9898989899");
        Log.i("CELGRAM_CONTACT", "" + contactsModel.getChikoop_name());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.GONE);

        activeChatAdapter = new ActiveChatAdapter(activeChatList, CelgramMain.this, true, this);
        //activeChatAdapter.setmListener(CelgramMain.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CelgramMain.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        multiChoiceHelper=new MultiChoiceHelper(CelgramMain.this,activeChatAdapter);
        multiChoiceHelper.setMultiChoiceModeListener(new MultiChoiceHelper.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        recyclerView.setAdapter(activeChatAdapter);

        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);

        tv_click_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//
//
                final Dialog dialog = new Dialog(CelgramMain.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.welcome_dialog);
                try {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                    wmlp.gravity = Gravity.NO_GRAVITY;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                LinearLayout ll_final_dialog1 = (LinearLayout) dialog.findViewById(R.id.ll_first_dialog);
                LinearLayout ll_final_dialog2 = (LinearLayout) dialog.findViewById(R.id.ll_first_dialog2);
                ll_final_dialog1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //fetchContactsUpdate();
                        dialog.dismiss();
                        eventBus.post(new DisconnectEvent());
                        Intent i = new Intent(getApplicationContext(), NewChat.class);
                        startActivity(i);
                        finish();
                    }
                });
                ll_final_dialog2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), CreateGroup.class);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        fetchContacts();
        num = activeChatList.size();
        if (num > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            tv_start.setVisibility(View.GONE);
            tv_click_here.setVisibility(View.GONE);
            iv_start_chat.setVisibility(View.GONE);
            ivNewChat.setVisibility(View.VISIBLE);
        }

        if(tv_start.getVisibility()==View.VISIBLE&&tv_click_here.getVisibility()==View.VISIBLE&&iv_start_chat.getVisibility()==View.VISIBLE)
        {
            ivNewChat.setVisibility(View.GONE);
        }else if(ivNewChat.getVisibility()==View.VISIBLE)
        {
            tv_start.setVisibility(View.GONE);
            tv_click_here.setVisibility(View.GONE);
            iv_start_chat.setVisibility(View.GONE);
        }
        //magic chat Api call
        // fetchMagicContacts();
        contact_db.mergeMagicListContact(magicnumbers);
/*
        recyclerViewlerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (num > 0) {
                    if (dy > 0 || dy < 0 && bottomNavigationView.isShown())
                        bottomNavigationView.setVisibility(View.GONE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (num > 0) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                } else {
                    bottomNavigationView.setVisibility(View.GONE);
                }


                super.onScrollStateChanged(recyclerView, newState);
            }
        });
*/



        ButterKnife.bind(this);







        NotificationManager notificationManager;
        notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(notificationManager!=null){
            notificationManager.cancelAll();
        }


        tvEditChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  if(ivEditBack.getVisibility()==View.INVISIBLE&&tvHeadingText.getText().toString().equals("Chats")&&activeChatList.size()>0){
                      tvEditChat.setVisibility(View.INVISIBLE);
                      tvHeadingText.setText("Edit chats");
                      ivEditBack.setVisibility(View.VISIBLE);
                      ivNewChat.setVisibility(View.INVISIBLE);
                      rl_deleteLayout.setVisibility(View.VISIBLE);
                      bottomNavigationView.setVisibility(View.GONE);
                      activeChatAdapter.showcheckbox(true);

                  }
                  else
                  {
                      Toast.makeText(CelgramMain.this, "Its Look Like you new here.Click above to Start", Toast.LENGTH_SHORT).show();
                  }
            }
        });
        ivEditBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvHeadingText.getText().toString().equals("Edit chats")&&tvEditChat.getVisibility()==View.INVISIBLE){
                   tvEditChat.setVisibility(View.VISIBLE);
                    tvHeadingText.setText("Chats");
                    ivEditBack.setVisibility(View.INVISIBLE);
                    ivNewChat.setVisibility(View.VISIBLE);
                    rl_deleteLayout.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    activeChatAdapter.showcheckbox(false);

                }
            }
        });

        btnDeletechats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeChatAdapter.deletecheckedItem();


            }
        });

        ivNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(CelgramMain.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.welcome_dialog);
                try {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                    wmlp.gravity = Gravity.NO_GRAVITY;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                LinearLayout ll_final_dialog1 = (LinearLayout) dialog.findViewById(R.id.ll_first_dialog);
                LinearLayout ll_final_dialog2 = (LinearLayout) dialog.findViewById(R.id.ll_first_dialog2);
                ll_final_dialog1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), NewChat.class);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });
                ll_final_dialog2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Intent i = new Intent(getApplicationContext(), CreateGroup.class);
                        startActivity(i);*/
                        dialog.dismiss();
                        Toast.makeText(CelgramMain.this, "Coming Soon in Next Update", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();

            }
        });
    }


    View.OnClickListener startnewChat=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(CelgramMain.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.welcome_dialog);
            try {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.NO_GRAVITY;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            LinearLayout ll_final_dialog1 = (LinearLayout) dialog.findViewById(R.id.ll_first_dialog);
            LinearLayout ll_final_dialog2 = (LinearLayout) dialog.findViewById(R.id.ll_first_dialog2);
            ll_final_dialog1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), NewChat.class);
                    startActivity(i);
                    dialog.dismiss();
                }
            });
            ll_final_dialog2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), CreateGroup.class);
                    startActivity(i);
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


    public void changeHeaderData() {
        profil = (CircleImageView) findViewById(R.id.profile_image);
        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        String fname = preferences.getString("firstname", "");
        String lname = preferences.getString("lastname", "");
        String uname = preferences.getString("username", "");
        String img = preferences.getString("imgname", "");
        String id = preferences.getString("id", "");
        fullname = (TextView) findViewById(R.id.profileName);
        userName = (TextView) findViewById(R.id.profileUserName);
        String post_image = "http://chikoop.com/api/uploads/" + id + "/" + img;
        try {
            if (!(img.equals("")))
                Picasso.with(this).load(post_image).into(profil);
            else
                Picasso.with(this).load(String.valueOf(getResources().getDrawable(R.drawable.earnchat_logo))).error(R.drawable.earnchat_logo).into(profil);
        } catch (Exception e) {
            Log.i("ERROR", e.toString());
        }
        fullname.setText(fname + " " + lname);
        userName.setText("@" + uname);




    }

    @Override
    public void onBackPressed() {

      super.onBackPressed();
    }




    public void fetchContacts() {

        List<String> contacts;
        //final List<ContactsModel> contactList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            contacts = getContacts();
            List<String> numbers = contact_db.getAllNumbers();

            numbers.removeAll(contacts);
            contacts.addAll(numbers);

            for (int i = 0; i < contacts.size(); i++) {

                if (i == contacts.size() - 1) {
                    csn += contacts.get(i);
                } else {
                    csn += contacts.get(i) + ",";
                }
            }


        }

        loadOfflineData();
        updateList(csn);


    }
    public void fetchContactsUpdate() {

        List<String> contacts;
        //final List<ContactsModel> contactList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            contacts = getContacts();
            List<String> numbers = contact_db.getAllNumbers();

            numbers.removeAll(contacts);
            contacts.addAll(numbers);

            for (int i = 0; i < contacts.size(); i++) {

                if (i == contacts.size() - 1) {
                    csn += contacts.get(i);
                } else {
                    csn += contacts.get(i) + ",";
                }
            }


        }

        updateListV2(csn);


    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_LONG).show();
            }
        }
    }

    private List<String> getContacts() {
        List<String> contacts = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        String mobile = preferences.getString("mobile", "");

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                number_name.clear();
                do {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    number = number.replace("+", "");
                    number = number.replace(" ", "");
                    number = number.replace("-", "");
                    number = number.replace("(", "");
                    number = number.replace(")", "");
                    if (!number.equals(mobile)) {
                        if (number.length() >= 10) {
                            number = number.substring(number.length() - 10);
                            contacts.add(number);
                            number_name.put(number, name);
                        }
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
        }
        contact_db.updateContactList(number_name);

        HashSet<String> uniqueContacts = new HashSet<>(contacts);
//                for(int i=0;i<contacts.size()-1;i++){
//                        for(int j=i+1;j<contacts.size();j++){
//                                if(contacts.get(i).equals(contacts.get(j))){
//                                        contacts.remove(j);
//                                }
//                        }
//
//                }
        return new ArrayList<>(uniqueContacts);
    }

    private void saveList(List<ContactsModel> contactsModelList) {
        contactList.clear();
        contactList.addAll(contactsModelList);

    }

    @Override
    protected void onPause() {
        Log.i("Pause", "-Called/" + contactList.size());
        // h.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        //current_chat = "";
        //loadOfflineData();
        UserHelper.loadUser(getApplicationContext());

        if (isMyServiceRunning(MessageReceive.class)) {
            Log.i("yolocelgram", "service is already running");
        } else {
            startService(mServiceIntent);
        }
        if(activeChatList.size()>0)
        {
            recyclerView.setVisibility(View.VISIBLE);
            ivNewChat.setVisibility(View.VISIBLE);
            tv_start.setVisibility(View.GONE);
            tv_click_here.setVisibility(View.GONE);
            iv_start_chat.setVisibility(View.GONE);

        }


        if(tv_start.getVisibility()==View.VISIBLE&&tv_click_here.getVisibility()==View.VISIBLE&&iv_start_chat.getVisibility()==View.VISIBLE)
        {
            ivNewChat.setVisibility(View.GONE);
        }else if(ivNewChat.getVisibility()==View.VISIBLE)
        {
            tv_start.setVisibility(View.GONE);
            tv_click_here.setVisibility(View.GONE);
            iv_start_chat.setVisibility(View.GONE);
        }
        Log.i("Resume", "-Called/" + contactList.size());
        // setlastmsg();
        activeChatAdapter.notifyDataSetChanged();
        eventBus.post(new ChangeLastSeenAvail(true));

        super.onResume();
    }

    private void setlastmsg() {
        contactList.clear();
        // groupList.clear();
        chatList.clear();
        contactList = contact_db.getAllContacts(1);
        groupList = contact_db.getAllGroups();
        chatList = db_helper.getChatList();

        for (ChatListModel chatListModel : chatList) {
            Log.i("ChatList", chatListModel.getConvo_partner());
        }

        for (GroupsModel groupsModel : groupList) {
            Log.i("ListGroup", groupsModel.getDisplay_name());
        }
        for (int i = 0; i < chatList.size(); i++) {
            Message latest_msg = db_helper.getMessage(chatList.get(i).getLast_msg());
            int unread = db_helper.getUnreadMessageCount(chatList.get(i).getConvo_partner());
            if (chatList.get(i).getConvo_partner().length() == 10) {
                ContactsModel contactsModel = getActiveContact(chatList.get(i).getConvo_partner());
                if (contactsModel != null) {

                    contactsModel.setLatest_msg(latest_msg);
                    contactsModel.setUnseen_msg(unread);
                    activeChatList.remove(i);
                    activeChatList.add(i, contactsModel);
                } else {
                    contactsModel = new ContactsModel(chatList.get(i).getConvo_partner());
                    contactsModel.setLatest_msg(latest_msg);
                    contactsModel.setUnseen_msg(unread);
                    activeChatList.remove(i);
                    activeChatList.add(i, contactsModel);
                }
            } else {
                Log.i("GROUP_ID_CONVO", "" + chatList.get(i).getConvo_partner());
                GroupsModel groupsModel = getActiveGroup(chatList.get(i).getConvo_partner());
                if (groupsModel != null) {
                    groupsModel.setLatest_msg(latest_msg);
                    groupsModel.setUnseen_msg(unread);
                    activeChatList.remove(i);
                    activeChatList.add(i, groupsModel);

                }

            }
        }

        Collections.sort(activeChatList, new Comparator<Object>()

        {
            public int compare(Object ob1, Object ob2) {

                return (ob2 instanceof ContactsModel ? ((ContactsModel) ob2).getLatest_msg().getTimestamp() : ((GroupsModel) ob2).getLatest_msg().getTimestamp())
                        .compareToIgnoreCase(ob1 instanceof ContactsModel ? ((ContactsModel) ob1).getLatest_msg().getTimestamp() : ((GroupsModel) ob1).getLatest_msg().getTimestamp());

            }
        });
        activeChatAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        eventBus.post(new ChangeLastSeenAvail(false));
        Log.i("CelgramMain", "EventBUSunregister");
        eventBus.unregister(this);
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }


    private void loadOfflineData() {
        contactList.clear();
        groupList.clear();
        contactList = contact_db.getAllContacts(1);
        groupList = contact_db.getAllGroups();

        chatList = db_helper.getChatList();

        for (ChatListModel chatListModel : chatList) {
            Log.i("ChatList", chatListModel.getConvo_partner());
        }
        Log.i("Chat_list_size", "" + chatList.size());
        for (int i = 0; i < chatList.size(); i++) {
            Message latest_msg = db_helper.getMessage(chatList.get(i).getLast_msg());
            int unread = db_helper.getUnreadMessageCount(chatList.get(i).getConvo_partner());
            if (chatList.get(i).getConvo_partner().length() == 10) {
                ContactsModel contactsModel = getActiveContact(chatList.get(i).getConvo_partner());
                if (contactsModel != null) {
                    contactsModel.setLatest_msg(latest_msg);
                    contactsModel.setUnseen_msg(unread);
                    activeChatList.add(contactsModel);
                } else {
                    contactsModel = new ContactsModel(chatList.get(i).getConvo_partner());
                    contactsModel.setLatest_msg(latest_msg);
                    contactsModel.setUnseen_msg(unread);
                    activeChatList.add(contactsModel);
                }
            } else {
                Log.i("GROUP_ID_CONVO", "" + chatList.get(i).getConvo_partner());
                GroupsModel groupsModel = getActiveGroup(chatList.get(i).getConvo_partner());
                if (groupsModel != null) {
                    groupsModel.setLatest_msg(latest_msg);
                    groupsModel.setUnseen_msg(unread);
                    activeChatList.add(groupsModel);
                }
            }
        }
        Collections.sort(activeChatList, new Comparator<Object>() {
            public int compare(Object ob1, Object ob2) {
                return (ob2 instanceof ContactsModel ? ((ContactsModel) ob2).getLatest_msg().getTimestamp() : ((GroupsModel) ob2).getLatest_msg().getTimestamp())
                        .compareToIgnoreCase(ob1 instanceof ContactsModel ? ((ContactsModel) ob1).getLatest_msg().getTimestamp() : ((GroupsModel) ob1).getLatest_msg().getTimestamp());
            }
        });
        activeChatAdapter.notifyDataSetChanged();
        progressWheel.setVisibility(View.GONE);
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Subscribe
    public void onNotifyEvent(StringEvent event) {
        int position = getItemPosition(event.getStr());
        if (position != -1) {
            if (activeChatList.get(position) instanceof ContactsModel) {
                ((ContactsModel) activeChatList.get(position)).setUnseen_msg(0);
            } else {
                ((GroupsModel) activeChatList.get(position)).setUnseen_msg(0);
            }
           activeChatAdapter.notifyItemChanged(position);

        }

    }


    @Subscribe
    public void onMessageReceived(Message event) {
        String convo_partner = event.getConvo_partner();
        final int position = getItemPosition(convo_partner);
        if (convo_partner.length() == 10) {
            if (position != -1) {

                ((ContactsModel) activeChatList.get(position)).setLatest_msg(event);

                if (!(convo_partner.equals(current_chat) || event.isSelf())) {

                    ((ContactsModel) activeChatList.get(position)).setUnseen_msg(((ContactsModel) activeChatList.get(position)).getUnseen_msg() + 1);

                }

                Log.i("TempTestPosition", "" + ((ContactsModel) activeChatList.get(position)).getDisplay_name() + "/" + String.valueOf(((ContactsModel) activeChatList.get(position)).getUnseen_msg()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activeChatAdapter.notifyItemChanged(position);//to refresh the recyler view

                    }
                });
            } else {
                ContactsModel temp = getActiveContact(convo_partner);
                if (temp != null) {
                    temp.setLatest_msg(event);
                    temp.setIsTyping(false);
                    if (!event.isSelf()) {
                        temp.setUnseen_msg(1);
                    }

                    Log.i("TempTest", "" + temp.getDisplay_name() + "/" + String.valueOf(temp.getUnseen_msg()));
                    activeChatList.add(0, temp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activeChatAdapter.notifyItemInserted(0);

                        }
                    });

                } else {
                    Log.i("TESTT_else", "true");
                    temp = new ContactsModel(event.getConvo_partner());
                    temp.setLatest_msg(event);
                    temp.setIsTyping(false);
                    temp.setUnseen_msg(1);
                    activeChatList.add(0, temp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activeChatAdapter.notifyItemInserted(0);

                        }
                    });
                }
            }

        } else {
            if (position != -1) {
                ((GroupsModel) activeChatList.get(position)).setLatest_msg(event);

                if (!(convo_partner.equals(current_chat) || event.isSelf())) {

                    if (String.valueOf(event.getMsg_type()).matches("0|1|2|3|4|5|6|13|14")) {
                        ((GroupsModel) activeChatList.get(position)).setUnseen_msg(((GroupsModel) activeChatList.get(position)).getUnseen_msg() + 1);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //only refresh the item meant
                        // activeChatAdapter.notifyDataSetChanged();
                        activeChatAdapter.notifyItemChanged(position);
                    }
                });

            } else {
                ChatListModel chatListModel = new ChatListModel(event.getConvo_partner(), event.getId());
                chatList.add(chatListModel);
            }
        }
    }

    @Subscribe
    public void onGroupMessageReceived(GroupMessage event) {

        Message message = event.getMessage();
        String group_id = message.getConvo_partner();
        String sender = message.getSender_id();
        final int position = getItemPosition(group_id);
        if (position != -1) {

            GroupsModel temp = (GroupsModel) activeChatList.get(position);

            if (temp.getCreation_status() == 1) {
                temp.setLatest_msg(event.getMessage());

                if (!(group_id.equals(current_chat) || message.isSelf())) {
                    if (String.valueOf(message.getMsg_type()).matches("0|1|2|3|4|5|6|13|14")) {
                        temp.setUnseen_msg(temp.getUnseen_msg() + 1);
                    }

                    activeChatList.remove(position);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        activeChatAdapter.notifyItemRemoved(position);
//                    }
//                });
                    activeChatList.add(0, temp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activeChatAdapter.notifyDataSetChanged();

                        }
                    });

                } else {
                    activeChatList.remove(position);
                    activeChatList.add(0, temp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activeChatAdapter.notifyDataSetChanged();

                        }
                    });

                }
            } else if (temp.getCreation_status() == 2) {
                UnsubscribeEvent unsubscribeEvent = new UnsubscribeEvent(group_id);
                eventBus.post(unsubscribeEvent);
            }

        }

//        else{
//            GroupsModel temp=getActiveGroup(group_id);
//            if(temp!=null){
//                if(temp.getCreation_status()==1) {
//                    temp.setLatest_msg(message);
//                    temp.setIsTyping(false);
//                    if(!message.isSelf()){
//                        temp.setUnseen_msg(1);;
//                    }
//
//                    activeChatList.add(0,temp);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            activeChatAdapter.notifyItemInserted(0);
//                        }
//                    });
//
//                }
//
//                else{
//
//                }
//                temp.setLatest_msg(message);
//                temp.setIsTyping(false);
//                if(!event.isSelf()){
//                    temp.setUnseen_msg(1);;
//                }
//
//                activeChatList.add(0,temp);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        activeChatAdapter.notifyItemInserted(0);
//                    }
//                });
//
//            }
//            else{
//                temp=new ContactsModel(event.getConvo_partner());
//                temp.setLatest_msg(event);
//                temp.setIsTyping(false);
//                temp.setUnseen_msg(1);
//                activeChatList.add(0,temp);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        activeChatAdapter.notifyItemInserted(0);
//                    }
//                });
//            }
//
//        }

    }


    @Subscribe
    public void onTypingNotification(String id) {
        Log.i("yoloCelgramMain", "got your typing :" + id);
        final int position = getItemPosition(id);
        if (!(id.equals(current_chat))) {
            if (position != -1) {
                if (activeChatList.get(position) instanceof ContactsModel) {
                    ((ContactsModel) activeChatList.get(position)).setIsTyping(!((ContactsModel) activeChatList.get(position)).getIsTyping());
                } else {
                    ((GroupsModel) activeChatList.get(position)).setIsTyping(!((GroupsModel) activeChatList.get(position)).getIsTyping());
                    ((GroupsModel) activeChatList.get(position)).setTyping(id);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activeChatAdapter.notifyItemChanged(position);

                    }
                });
            }
        }

    }

    @Subscribe
    public void onGroupTyping(GroupTypingEvent event) {
        final int pos = getItemPosition(event.getGroup_id());
        if (pos != -1) {
            GroupsModel groupsModel = (GroupsModel) activeChatList.get(pos);
            if (event.isTyping()) {
                groupsModel.setIsTyping(true);
                groupsModel.setTyping((number_name.get(event.getSender()) != null
                        ? number_name.get(event.getSender()) : event.getSender()));
            } else {
                groupsModel.setIsTyping(false);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activeChatAdapter.notifyItemChanged(pos);

                }
            });

        }
    }

    @Subscribe
    public void onGroupCreate(GroupsModel event) {
        Log.i("GROUP_CREATED", "=true");
        Message message = new Message();
        for (int i = 0; i < chatList.size(); i++) {
            if (chatList.get(i).getConvo_partner().equals(event.getId())) {
                message = db_helper.getMessage(chatList.get(i).getLast_msg());
                break;
            }
        }
        event.setLatest_msg(message);
        groupList.add(event);
        activeChatList.add(0, event);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activeChatAdapter.notifyDataSetChanged();

            }
        });

        List<String> new_numbers = event.getMembers();
        new_numbers.removeAll(contact_db.getAllNumbers());

        StringBuilder numbers = new StringBuilder();
        for (String number : new_numbers) {
            numbers.append(number + ",");
        }
        numbers.deleteCharAt(numbers.length() - 1);

        updateList(String.valueOf(numbers));
    }


    private ContactsModel getActiveContact(String convo_partner) {
        Log.i("TESTT_convo_p", "" + convo_partner);
        Log.i("onSize", "" + contactList.size());
        for (int i = 0; i < contactList.size(); i++) {
            Log.i("TESTT_contact_ID", "" + contactList.get(i).getId());
            if (convo_partner.equals(contactList.get(i).getId())) {
                return contactList.get(i);
            }
        }

        return null;
    }

    private GroupsModel getActiveGroup(String group_id) {
        for (int i = 0; i < groupList.size(); i++) {
            if (group_id.equals(groupList.get(i).getId())) {
                return groupList.get(i);
            }
        }
        //Toast.makeText(this, "null is returned", Toast.LENGTH_LONG).show();
        return null;
    }

  /*  @Override
    protected void onStart() {
       *//* h.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateList(csn);
                runnable = this;
                h.postDelayed(runnable, 60000);
            }
        }, 20000);*//*
        super.onStart();
        active = true;
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Subscribe
    public void onConnectivityEvent(ConnectivityEvent event) {
        boolean connected = event.isConnected();
        if (connected) {
            if (!listUpdated) {
                updateList(csn);
            }
        }
    }

    private int getItemPosition(String convo_partner) {
        for (int i = 0; i < activeChatList.size(); i++) {
            if (activeChatList.get(i) instanceof ContactsModel) {
                if (convo_partner.equals(((ContactsModel) activeChatList.get(i)).getId())) {
                    return i;
                }
            } else {
                if (convo_partner.equals(((GroupsModel) activeChatList.get(i)).getId())) {
                    return i;
                }
            }
        }

        return -1;
    }

   /* @Subscribe
    public void onCreateNewGroup(CreateGroupEvent event) {
        Log.i("CreateNotify", "GrouponCreate");
        GroupsModel groupsModel = event.getGroupsModel();
        activeChatList.add(0, groupsModel);
        activeChatAdapter.notifyDataSetChanged();

    }*/

    @Subscribe
    public void onGroupCreated(GroupCreationNotify event) {
        Log.i("CreateNotify", "GroupCreationNotify");
        String old_id = event.getOld_id();
        String new_id = event.getNew_id();
        int status = event.getStatus();
        int position = getItemPosition(old_id);

        if (position != -1) {
            GroupsModel groupsModel;
            if (status == 1) {
                Log.i("FinalCreation", "notified");
                groupsModel = (GroupsModel) activeChatList.get(position);
                groupsModel.setId(new_id);
                groupsModel.setLatest_msg(db_helper.getMessage(db_helper.getLastMsgId(new_id)));
                groupsModel.setCreation_status(1);
                activeChatList.remove(position);
                activeChatList.add(position, groupsModel);

            } else if (status == 3 && !old_id.equals(new_id)) {
                groupsModel = (GroupsModel) activeChatList.get(position);
                groupsModel.setId(new_id);
                groupsModel.setCreation_status(3);
                activeChatList.remove(position);
                activeChatList.add(position, groupsModel);


            } else {
                groupsModel = (GroupsModel) activeChatList.get(position);
                groupsModel.setCreation_status(3);
                activeChatList.remove(position);
                activeChatList.add(position, groupsModel);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activeChatAdapter.notifyDataSetChanged();

                }
            });
        }
    }

    private void  updateList(String csn) {
        RestClientS3Group.ApiGroupInterface service = RestClientS3Group.getClient();
        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        String sessionId = preferences.getString("session", "");
        Log.i("CelSes", sessionId);
        String seId = "f1b6cdf3b1148e3fe3b63f8c4aec1381";
        Call<CheckContacts> call = service.checkContacts(csn);
        Log.i("CSN_Check", csn);
        call.enqueue(new Callback<CheckContacts>() {
            @Override
            public void onResponse(Call<CheckContacts> call, Response<CheckContacts> response) {
                CheckContacts checkContacts = response.body();
                List<ContactsModel> contactsModelList = new ArrayList<>();
                listUpdated = true;
                Log.i("CelStat", checkContacts.getStatus().toString());
                if (checkContacts.getStatus()) {
                    List<Result> checkContactsList;
                    Result result;
                    ContactsModel contactsModel;
                    checkContactsList = checkContacts.getResult();
                    for (int i = 0; i < checkContactsList.size(); i++) {
                        result = checkContactsList.get(i);
                       // Log.i("CelStat", result.ge().toString());
                        if (true) {
                            if (number_name.get(result.getUserid()) == null) {
                                number_cname.put(result.getUserid(), result.getUsername());
                            }
                            userid_cnum.put(result.getUserid(), result.getUserid());

                            contactsModel = new ContactsModel(result.getUserid(), result.getUsername(), result.getUsername()
                                    , number_name.get(result.getUserid()), result.getDb_update_time(), result.getImgname());
                            Log.i("CELGRAM_API", "" + contactsModel.getChikoop_name());
                            try {
                                boolean need_dp_update = contact_db.updateContact(contactsModel);
                                if (need_dp_update) {
                                    //todo change dp
                                    contactsModel.setImgname(result.getImgname());
                                    int pos = getItemPosition(contactsModel.getId());
                                    activeChatList.remove(pos);
                                    activeChatList.add(pos, contactsModel);
                                    activeChatAdapter.notifyItemChanged(pos);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            contactsModelList.add(contactsModel);
                        }
                    }
                    Log.i("TEST_CONTACTLISTSIZE", "" + contactsModelList.size());
                    contact_db.addUseridMap(userid_cnum);

                    saveList(contactsModelList);

                } else {
                    //Toast.makeText(getApplicationContext(), checkContacts.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CheckContacts> call, Throwable t) {
                //  Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_SHORT).show();
                listUpdated = false;
            }

        });

        number_name.put(UserHelper.getId(), "You");

    }
    private void  updateListV2(String csn) {
        RestClientS3Group.ApiGroupInterface service = RestClientS3Group.getClient();
        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        String sessionId = preferences.getString("session", "");
        Log.i("CelSes", sessionId);
        String seId = "f1b6cdf3b1148e3fe3b63f8c4aec1381";
        Call<CheckContacts> call = service.checkContacts(csn);
        Log.i("CSN_Check", csn);
        dialog = ProgressDialog.show(this, "",
                "Processing!. Please wait...", true);
        call.enqueue(new Callback<CheckContacts>() {
            @Override
            public void onResponse(Call<CheckContacts> call, Response<CheckContacts> response) {
                CheckContacts checkContacts = response.body();
                List<ContactsModel> contactsModelList = new ArrayList<>();
                listUpdated = true;
                Log.i("CelStat", checkContacts.getStatus().toString());
                if (checkContacts.getStatus()) {
                    List<Result> checkContactsList;
                    Result result;
                    ContactsModel contactsModel;
                    checkContactsList = checkContacts.getResult();
                    for (int i = 0; i < checkContactsList.size(); i++) {
                        result = checkContactsList.get(i);
                       // Log.i("CelStat", result.ge().toString());
                        if (true) {
                            if (number_name.get(result.getUserid()) == null) {
                                number_cname.put(result.getUserid(), result.getUsername());
                            }
                            userid_cnum.put(result.getUserid(), result.getUserid());

                            contactsModel = new ContactsModel(result.getUserid(), result.getUsername(), result.getUsername()
                                    , number_name.get(result.getUserid()), result.getDb_update_time(), result.getImgname());
                            Log.i("CELGRAM_API", "" + contactsModel.getChikoop_name());
                            try {

                                boolean need_dp_update = contact_db.updateContact(contactsModel);
                                if (need_dp_update) {
                                    //todo change dp
                                    contactsModel.setImgname(result.getImgname());
                                    int pos = getItemPosition(contactsModel.getId());
                                    activeChatList.remove(pos);
                                    activeChatList.add(pos, contactsModel);
                                    activeChatAdapter.notifyItemChanged(pos);


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            contactsModelList.add(contactsModel);
                        }
                    }
                    Log.i("TEST_CONTACTLISTSIZE", "" + contactsModelList.size());
                    contact_db.addUseridMap(userid_cnum);

                    saveList(contactsModelList);
                    dialog.dismiss();
                    number_name.put(UserHelper.getId(), "You");
                    Intent intent=new Intent(CelgramMain.this,NewChat.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);

                } else {
                    //Toast.makeText(getApplicationContext(), checkContacts.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<CheckContacts> call, Throwable t) {
                //  Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_SHORT).show();
                listUpdated = false;
                dialog.dismiss();
            }

        });


    }

    @Subscribe
    public void addSingleMemberResponse(AddSingleMemberResponse event) {
        int position = getItemPosition(event.getGroup_id());
        if (position != -1) {
            ((GroupsModel) activeChatList.get(position)).addMember(event.getContact_id());
        }
    }

    @Subscribe
    public void onBroadcastAddmember(AddMemExitEvent event) {
        Log.i("GroupAddMemmExit", event.getGroup_id());
        if (event.isSuccess()) {
            final int pos = getItemPosition(event.getGroup_id());

            if (pos != -1) {
                GroupsModel groupsModel = contact_db.getGroup(event.getGroup_id());
                contact_db.setGroupCreationStatus(event.getGroup_id(), 1);
                groupsModel.setCreation_status(1);
                groupsModel.setLatest_msg(event.getMessage());
                activeChatList.remove(pos);
                activeChatList.add(pos, groupsModel);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activeChatAdapter.notifyItemChanged(pos);

                    }
                });

            } else {
                GroupsModel groupsModel = contact_db.getGroup(event.getGroup_id());
                contact_db.setGroupCreationStatus(event.getGroup_id(), 1);
                groupsModel.setCreation_status(1);
                groupsModel.setLatest_msg(event.getMessage());
                activeChatList.add(groupsModel);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activeChatAdapter.notifyDataSetChanged();

                    }
                });
            }


        }
    }

    // UI updates must run on MainThread
    @Subscribe
    public void OnExitGroupNotify(GroupExitEvent event) {
        final int pos = getItemPosition(event.getGroupId());
        Log.i("GroupExitcalled", "pos before if " + pos);

        if (pos != -1) {
            Log.i("GroupExitcalled", "pos " + pos);
            SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
            final String socket_id = preferences.getString("mobile", "");
            GroupsModel groupsModel = contact_db.getGroup(event.getGroupId());
            Message msg = new Message(event.getGroupId(), event.getSender(),
                    (event.getSender().equals(socket_id)), 11, "", event.getTimestamp(), 0, "", "", "");
            groupsModel.setLatest_msg(msg);
            if (msg.isSelf()) {
                groupsModel.setCreation_status(2);
            }

            activeChatList.remove(pos);
            activeChatList.add(pos, groupsModel);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activeChatAdapter.notifyItemChanged(pos);

                }
            });
        }
    }

    @Subscribe
    public void OnRemoveMemeberNotify(RemRecvResponseEvent event) {
        final int pos = getItemPosition(event.getGroupId());
        Log.i("MemRemove", "pos before if " + pos);

        if (pos != -1) {
            Log.i("MemRemove", "pos " + pos);
            SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
            GroupsModel groupsModel = contact_db.getGroup(event.getGroupId());
            contact_db.setGroupCreationStatus(event.getGroupId(), 2);
            groupsModel.setLatest_msg(event.getMessage());
            groupsModel.setCreation_status(2);
            activeChatList.remove(pos);
            activeChatList.add(pos, groupsModel);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activeChatAdapter.notifyItemChanged(pos);

                }
            });
        }
    }

    @Subscribe
    public void OnSubjectChange(GroupNameChangeResponse response) {
        Log.i("GroupNAme", response.getGroupId());
        final int position = getItemPosition(response.getGroupId());
        try {
            if (position != 1) {
                GroupsModel group = contact_db.getGroup(response.getGroupId());
                group.setDisplay_name(response.getGroupname());
                //update local database and check first it exist
                if (contact_db.checkGroupExist(response.getGroupId())) {
                    contact_db.updateGroupName(response.getGroupId(), response.getGroupname());
                }
                activeChatList.remove(position);
                activeChatList.add(position, group);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activeChatAdapter.notifyItemChanged(position);

                    }
                });
            }
        } catch (Exception e) {
            Log.i("GroupNAme", e.getMessage());
        }

    }

    @Subscribe
    public void ChangeGroupDp(NotifyEvent event) {
        Log.i("GroupDP", event.getGroupid());
        final int pos = getItemPosition(event.getGroupid());
        if (pos != -1) {
            GroupsModel groupsModel = contact_db.getGroup(event.getGroupid());
            groupsModel.setDp_url(contact_db.getgroupdpName(event.getGroupid()));
            //rohan mod 25/12/2017
            activeChatList.remove(pos);
            activeChatList.add(pos, groupsModel);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // activeChatAdapter.notifyDataSetChanged();
                    activeChatAdapter.notifyItemChanged(pos);



                }
            });

        }
    }

    // UI updates must run on MainThread
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAlertEvent(AlertUser event) {
        Toast.makeText(this, event.getMessage(), Toast.LENGTH_SHORT).show();
        eventBus.removeStickyEvent(event);
        Intent intent=new Intent(CelgramMain.this, AlertScreenActivity.class);
        UserHelper.logout(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void onShowNewChats() {

            if(tvHeadingText.getText().toString().equals("Edit chats")&&tvEditChat.getVisibility()==View.INVISIBLE){
                tvEditChat.setVisibility(View.VISIBLE);
                tvHeadingText.setText("Chats");
                ivEditBack.setVisibility(View.INVISIBLE);
                rl_deleteLayout.setVisibility(View.GONE);
                ivNewChat.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);


            }


    }

    private boolean checkChatFragment()
    {
        ActiveChatFragment myFragment = (ActiveChatFragment) getSupportFragmentManager().findFragmentByTag("CHAT");
        if (myFragment != null && myFragment.isVisible()) {
            // add your code here
            return true;
        }
        else {
            return false;
        }
    }


}