package com.rohan7055.earnchat.celgram;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.AppConstants;
import com.rohan7055.earnchat.UserHelper;
import com.rohan7055.earnchat.celgram.events.ChangeAvailability;
import com.rohan7055.earnchat.celgram.events.ChangeLastSeenAvail;
import com.rohan7055.earnchat.celgram.events.GroupCreationEvent;
import com.rohan7055.earnchat.celgram.events.GroupMessageEvent;
import com.rohan7055.earnchat.celgram.events.GroupNameChangeResponse;
import com.rohan7055.earnchat.utils.ImageHelper;
import com.rohan7055.earnchat.utils.RoundedCornersTransformation;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.dd.CircularProgressButton;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.rohan7055.earnchat.Adapter.SetMessageAdapter;
import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.rohan7055.earnchat.celgram.DataBase_Helper.MessageSQLiteHelper;
import com.rohan7055.earnchat.celgram.celme.SendMessageService;
import com.rohan7055.earnchat.celgram.celme.WiFiDirectBroadcastReceiver;
import com.rohan7055.earnchat.celgram.events.AddSingleMemberResponse;
import com.rohan7055.earnchat.celgram.events.AvailableEvent;
import com.rohan7055.earnchat.celgram.events.AwsEvent;
import com.rohan7055.earnchat.celgram.events.DownloadEvent;
import com.rohan7055.earnchat.celgram.events.GroupTypingEvent;
import com.rohan7055.earnchat.celgram.events.MessageEvent;
import com.rohan7055.earnchat.celgram.events.MessageStatusEvent;
import com.rohan7055.earnchat.celgram.events.ProgressEvent;
import com.rohan7055.earnchat.celgram.events.SendTypingEvent;
import com.rohan7055.earnchat.celgram.events.StickerEvent;
import com.rohan7055.earnchat.celgram.events.StringEvent;
import com.rohan7055.earnchat.celgram.events.UnsubscribeResponse;
import com.rohan7055.earnchat.celgram.service.AwsUploadService;
import com.rohan7055.earnchat.celgram.service.GPSTracker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.codetail.widget.RevealFrameLayout;

public class ChatWindow extends AppCompatActivity implements WifiP2pManager.PeerListListener,WifiP2pManager.ConnectionInfoListener {


    private static final int SELECT_PHOTO = 30;
    /*public static final int MEDIA_TYPE_IMAGE = 1;
        public static final int MEDIA_TYPE_VIDEO = 2;
    */
    //public CircularProgressButton circularProgressButton;
    private ImageView action_overflow;
    private TextView heading_txt, connect_dots, online_status;
    private RelativeLayout frameLayout;
    private RelativeLayout condensedContainer;
    private ImageView cancel_connect, chatEmoticon;
    private EmojiEditText message_body;
    private ImageView sendmessage;
    private ImageView sendAudio;
    private ImageView bigmicrophone;
    private RelativeLayout mssgLayout;
    private static String audiorecordPath;
    private Intent i;
    private Boolean isWifiP2pEnabled = false;
    private LinearLayout exitGroupLayout;
    public static final String TAG = "ChatWindow";
    private WifiP2pManager manager;
    private WifiP2pManager.Channel mchannel, channel;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver receiver = null;
    private ProgressDialog progressDialog;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    WifiP2pManager.PeerListListener peerListListener;
    private Boolean flag = false, eflag = false, cflag = false;
    private WifiP2pDevice device;
    private WifiP2pConfig config;
    private static SetMessageAdapter setMessageAdapter;
    private static SetMessageAdapter setCondensedMessageAdapter;
    private static List<Object> messageList;
    private List<Object> messageCondensedList;
    private static RecyclerView recyclerView;
    private RecyclerView condensedrecylerview;
    private boolean isCondensed=false;
    private static Message message;
    private static Media media;
    private static String name;
    private static String rmsg = "";
    public static Boolean retry = false, availability = false;
    private static ServerSocket message_socket = null, image_socket = null, video_socket = null;
    public static int IMAGE_PORT = 8080;
    public static int VIDEO_PORT = 8880;
    public static int MESSAGE_PORT = 8998;
    public static boolean server_running = false;
    public static boolean mserver_running = false;
    public static boolean vserver_running = false;
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private static final int RESULT_PICK_CONTACT = 19891;
    private static final int RESULT_PICK_DOC = 200;
    private WifiP2pInfo info;
    String perm;
    private LinearLayout contact_info, person_info;
    //    private RelativeLayout message_layout;
    private TextView left_grp_message;

    private LinearLayout replyLay;
    private RelativeLayout editLayout;
    private boolean replyMessage = false;

    TextView replyTo;
    TextView replyToMessage;
    ImageView replyToImage;

    LinearLayout callsim;
    public static String clientIp = "";
    public static Boolean ip_sent = false;
    private Boolean mTyping = false;
    private Boolean uTyping = false;

    private Boolean tut_seen = false;
    static ReceiveMessageAsyncTask message_task = null;
    static ReceiveImageAsyncTask image_task = null;
    static ReceiveVideoAsyncTask video_task = null;
    GroupsModel groupsModel;
    FrameLayout celme_connect_layout;

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.NORMAL);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    private  PopupWindow popupWindow;
    private boolean isPopUpShown=false;
    private int KEY_BACKPRESSED=0;



    //Contextual Action Bar
    private ActionMode mActionMode;
    //celgram toolbar related stuff//
    Toolbar toolbar;
    boolean hidden = true;
    LinearLayout mRevealView;
    ImageButton ib_gallery, ib_contacts, ib_location;
    ImageButton ib_doc, ib_audio, ib_camera;
    ImageButton ib_images, ib_call;

    //pop
    ImageButton ib_gallery_pop, ib_contacts_pop, ib_location_pop;
    ImageButton ib_doc_pop, ib_audio_pop, ib_camera_pop;
    ImageButton ib_images_pop, ib_call_pop;

    Uri fileUri;
    //end//
    ArrayList<String> mute_id;

    private static String sender;

    private Handler mHandler;

    private Handler mTypingHandler = new Handler();
    private static final int TYPING_TIMER_LENGTH = 600;
    private Boolean isOnline = false;
    public static boolean isContact;

    private boolean isKeyBoardHidden=true;

    MessageSQLiteHelper db_helper;
    ContactSQLiteHelper db_helper_contact;
    private Boolean isMute = false;
    private Boolean isGroupMute = false;
    private Boolean isBlock = false;

    EventBus eventBus;
    public static GroupsModel group;

    String color;

    Document document;
    String FILE;
    StringBuilder allmsg;

    public static ContactsModel cmw;

    private Button send;
    LinearLayout message_layout;

    TextView rl_click;
    //Buckets
    EmojiPopup emojiPopup;
    public static String defaultbucket = "chikoopbucket";
    public static String imagesbucket = "chikoopimages";
    public static String videosbucket = "chikoopvideos";
    public static String documentsbucket = "chikoopdocuments";

    //Prefix Urls

    public static String PrefixDefault = "https://s3.ap-south-1.amazonaws.com/chikoopbucket/";
    public static String PrefixImages = "https://s3.ap-south-1.amazonaws.com/chikoopimages";
    public static String PrefixVideos = "https://s3.ap-south-1.amazonaws.com/chikoopvideos";
    public static String PrefixDocuments = "https://s3.ap-south-1.amazonaws.com/chikoopdocuments";
    int count = 0;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static Activity activity;
    //Audio Recorder
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "Chikoop/Media/audios";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP};

    ImageButton c0, c1, c2, c3, c4;
    public static boolean isActive = false;
    private RevealFrameLayout revealFrameLayout;
    private  RelativeLayout rootView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_toolbar);
        if (savedInstanceState != null) {
            isMute = savedInstanceState.getBoolean("isMute");
            isGroupMute = savedInstanceState.getBoolean("isGroupMute");
            isBlock = savedInstanceState.getBoolean("isBlock");
            count = savedInstanceState.getInt("count");
        }
        getWindow().setBackgroundDrawableResource(R.drawable.cpb_background);
        rootView=(RelativeLayout)findViewById(R.id.rootView);



        initPopup();

        popupWindow.getContentView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("POPCLICK",""+v.getId());

                switch (v.getId())
                {
                    case R.id.call_pop:
                        Toast.makeText(ChatWindow.this, "Call button is clicked", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }

            }
        });

        //to adjust keyboard

        //setputEmoticons();
        setupUI(rootView);
        frameLayout=(RelativeLayout)findViewById(R.id.frame);
        condensedContainer=(RelativeLayout)findViewById(R.id.rlcondensedcontainer);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        mute_id = new ArrayList<String>();
        sendmessage = (ImageView) findViewById(R.id.imageView26);
        sendAudio = (ImageView) findViewById(R.id.microPhone);
        bigmicrophone = (ImageView) findViewById(R.id.bigmircophone);
        //exitgroup
        exitGroupLayout=(LinearLayout)findViewById(R.id.exitgroup);
        mssgLayout=(RelativeLayout)findViewById(R.id.relativeLayout3);
        revealFrameLayout=(RevealFrameLayout)findViewById(R.id.rv_framelayout);

        editLayout = (RelativeLayout) findViewById(R.id.relativeLayout3);
        replyLay = (LinearLayout) findViewById(R.id.replyLay);
        replyLay.setVisibility(View.GONE);
        replyTo = (TextView) findViewById(R.id.reply_sender);
        replyToMessage = (TextView) findViewById(R.id.replyToText);
        replyToImage = (ImageView) findViewById(R.id.reply_image);


        // rl_click=(TextView) findViewById(R.id.rl_click);
        // groupsModel =(GroupsModel) getIntent().getSerializableExtra("groupmodel");
        group = (GroupsModel) getIntent().getSerializableExtra("group");

        //keyboard listeners
        initKeyBoardListener();


       // setupUI(findViewById(R.id.rootView));
        person_info = (LinearLayout) findViewById(R.id.contact_info);
        person_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isContact) {
                    String contact_name = cmw.getDisplay_name();
                    String contact_id = cmw.getId();
                    String user_id = db_helper_contact.getUserid(cmw.getId());
                    //Log.d("Anuraj_", "cmw "+cmw.getId());
                    String contact_uname = cmw.getUname();
                    String email = cmw.getChikoop_name();
                    Toast.makeText(ChatWindow.this, contact_name, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ChatWindow.this, ContactDetails.class);
                    i.putExtra("CONTACT_NAME", contact_name);
                    i.putExtra("ID", contact_id);
                    i.putExtra("userid", user_id);
                    i.putExtra("UNAME", contact_uname);
                    i.putExtra("image", cmw.getImgname());
                    i.putExtra("MAIL", email);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "GROUP INFO", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), GroupInfo.class);
                    i.putExtra("group", group);
                    //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }
            }
        });

      /*  filename =  getIntent().getStringExtra("filepath");
        group.setDp_url(filename);*/
        //celgram toolbar stuff//
//        iv_phone_reciever=(ImageView)findViewById(R.id.iv_phone_reciever);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        ib_audio = (ImageButton) findViewById(R.id.audio);
        ib_camera = (ImageButton) findViewById(R.id.camera);
        ib_contacts = (ImageButton) findViewById(R.id.contacts);
        ib_gallery = (ImageButton) findViewById(R.id.gallery);
        ib_location = (ImageButton) findViewById(R.id.location);
        ib_doc = (ImageButton) findViewById(R.id.document);
        ib_images = (ImageButton) findViewById(R.id.images);
        ib_call = (ImageButton) findViewById(R.id.call);

        ib_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*final Dialog dialog1 = new Dialog(activity);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                dialog1.setContentView(R.layout.call_popup);
                callsim = (LinearLayout) dialog1.findViewById(R.id.callsim1);
                LinearLayout callChikoop = (LinearLayout) dialog1.findViewById(R.id.chikoop_layout);
                dialog1.show();

                callsim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                        String contact_id = ChatWindow.cmw.getId();
                        perm = "android.permission.CALL_PHONE";
                        int permission = PermissionChecker.checkSelfPermission(activity, perm);

                        if (permission == PermissionChecker.PERMISSION_GRANTED) {
//                            Use Intent.ACTION_CALL to directly call without giving the user the choice to select a different app
//                            for calling like TrueCaller etc.
                            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact_id));
                            activity.startActivity(i);
                        } else {
//                             permission not granted, you decide what to do
                        }
                    }
                });

                callChikoop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                        String contact_id = ChatWindow.cmw.getId();
                        perm = "android.permission.CALL_PHONE";
                        int permission = PermissionChecker.checkSelfPermission(activity, perm);

                        if (permission == PermissionChecker.PERMISSION_GRANTED) {
                            if (!contact_id.isEmpty()) {
                                Intent intent = new Intent(ChatWindow.this, CallingScreen.class);
                                intent.putExtra("phoneNumber", contact_id);
                                ContactInfo temp = CiaoFunction.fetchContactsDetailsFromNo1(ChatWindow.this, contact_id);
                                if (temp != null) {
                                    intent.putExtra("flag", "1");
                                    intent.putExtra("nameOfCaller", temp.getName() + "");
                                    intent.putExtra("contactid", temp.get_id());
                                    intent.putExtra("calltype", "OUTGOING");
                                    intent.putExtra("imgUrl", temp.getPhone());
                                } else {
                                    intent.putExtra("nameOfCaller", "");
                                    intent.putExtra("calltype", "OUTGOING");
                                }
                                startActivity(intent);
                            } else {
                                Toast.makeText(ChatWindow.this, "Please Enter a Mobile Number", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ChatWindow.this, "Calling permission is needed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
                String contact_id = ChatWindow.cmw.getId();
                perm = "android.permission.CALL_PHONE";
                int permission = PermissionChecker.checkSelfPermission(activity, perm);

                if (permission == PermissionChecker.PERMISSION_GRANTED) {
//                            Use Intent.ACTION_CALL to directly call without giving the user the choice to select a different app
//                            for calling like TrueCaller etc.
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact_id));
                    activity.startActivity(i);
                } else {
                    Toast.makeText(ChatWindow.this, "Provide permission to phone to proceed calling", Toast.LENGTH_SHORT).show();
                }
            }
        });
//
        ib_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("audio/*");
                startActivityForResult(i, 4);
            }
        });
        ib_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               /* long index= System.currentTimeMillis();
                String fileName = "Chikoop_Image"+index+".jpg";

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by Chikoop");*/

               /* imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);*/


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                Log.i("ChatWindow", "" + fileUri);

                List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    activity.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                //intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, 11);


            }
        });
        ib_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }
        });
        ib_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("video/*");
                startActivityForResult(i, 1);
            }
        });
        ib_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GPSTracker gps = new GPSTracker(ChatWindow.this);

                if (gps.canGetLocation()) {

                    //double latitude = gps.getLatitude();
                    //double longitude = gps.getLongitude();
                    MyParams3 pass = new MyParams3(gps);

                    new getLocationNow().execute(pass);

                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });
        ib_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                String[] mimetypes = {"text/*", "application/pdf"};
                // DEFINE MORE MIMETYPES IN THE ABOVE STRING ARRAY TO PICK PPT/ XLS FILES ETC.
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

                startActivityForResult(intent, 200);
            }
        });

        ib_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //images pick.
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
            }
        });


        sendAudio.setOnTouchListener(new View.OnTouchListener() {

            private int CLICK_ACTION_THRESHOLD = 200;
            private float startX = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // start recording.
                    bigmicrophone.setVisibility(View.VISIBLE);
                    sendAudio.setVisibility(View.GONE);
                    message_body.setText("Tap & Hold to Record Audio");
                    sendmessage.setVisibility(View.GONE);
                    startRecording();
                    startX = event.getEventTime();
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float endX = event.getEventTime();

                    Log.d("startX", String.valueOf(startX));
                    Log.d("endX", String.valueOf(endX));

                    if (isAClick(startX, endX)) {
                        // Stop recording and save file
                        sendAudio.setVisibility(View.VISIBLE);
                        bigmicrophone.setVisibility(View.GONE);
                        message_body.setText("");
                        stopRecording();
                        sendaudioMessage(audiorecordPath);
                    } else {
                        sendAudio.setVisibility(View.VISIBLE);
                        bigmicrophone.setVisibility(View.GONE);
                        message_body.setText("");
                        if (null != recorder) {
                            //recorder.stop();
                            //recorder.reset();
                            recorder.release();

                            recorder = null;
                        }
                        Toast.makeText(ChatWindow.this, "Touch & Hold to record audio", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
                v.getParent().requestDisallowInterceptTouchEvent(true); //specific to my project
                return false; //specific to my project
            }

            private boolean isAClick(float startX, float endX) {
                float differenceX = Math.abs(endX - startX);
                return (differenceX > CLICK_ACTION_THRESHOLD);
            }

        });

        setSupportActionBar(toolbar);
        mRevealView.setVisibility(View.INVISIBLE);
        //end//

        ImageView back = (ImageView) findViewById(R.id.back_imgx);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CelgramMain.class));
                supportFinishAfterTransition();
                finish();
            }
        });
        db_helper = new MessageSQLiteHelper(this);
        db_helper_contact = new ContactSQLiteHelper(this);
        mHandler = new Handler();

        activity = this;

        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        //sender = preferences.getString("mobile", "");
        sender=UserHelper.getId();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        action_overflow = (ImageView) findViewById(R.id.action_overflow);
        heading_txt = (TextView) findViewById(R.id.heading_txt);
        online_status = (TextView) findViewById(R.id.online_status);
        online_status.setVisibility(View.GONE);
        message_body = (EmojiEditText) findViewById(R.id.message_body);
        chatEmoticon = (ImageView) findViewById(R.id.imageView24);
        if (message_body.length() == 0) {
            sendAudio.setVisibility(View.VISIBLE);
            sendmessage.setVisibility(View.GONE);
        }


        if (getIntent().getBooleanExtra("isContact", false)) {
            isContact = true;
            mssgLayout.setVisibility(View.VISIBLE);
            if ((ContactsModel) getIntent().getSerializableExtra("contact") == null) {
                byte[] contactbyte = getIntent().getByteArrayExtra("contactSerial");
                cmw = (ContactsModel) deserializeObject(contactbyte);
            } else {
                cmw = (ContactsModel) getIntent().getSerializableExtra("contact");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                heading_txt.setTransitionName("displayname");
            }

            heading_txt.setText(cmw.getDisplay_name());

            Log.d("anuraj_cmw_displayname", cmw.getDisplay_name());
            Log.d("anuraj_cmw_ID", cmw.getId());

            db_helper.updateMessageStatus(cmw.getId(), 2);
            StringEvent stringEvent = new StringEvent();
            stringEvent.setStr(cmw.getId());
            eventBus.post(stringEvent);
           /* person_info=(LinearLayout)findViewById(R.id.contact_info);
            person_info.setOnClickListener(showPersonInfo);*/
            isMute = db_helper_contact.checkmuteStatus(cmw);
            isBlock = db_helper_contact.checkBlockedStatus(cmw);
            if (savedInstanceState != null) {
                isMute = savedInstanceState.getBoolean("isMute");
                isBlock = savedInstanceState.getBoolean("isBlock");
            }
        } else {
            isContact = false;
            if (getIntent().getSerializableExtra("group") == null) {
                //byte [] groupByte = getIntent().getByteArrayExtra("groupSerial");
                //group=(GroupsModel)deserializeObject(groupByte);
                group = (GroupsModel) getIntent().getSerializableExtra("group");
            } else {
                group = (GroupsModel) getIntent().getSerializableExtra("group");
            }

            // getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                heading_txt.setTransitionName("displayname");
            }
            heading_txt.setText(group.getDisplay_name());
            db_helper.updateMessageStatus(group.getId(), 2);
            StringEvent stringEvent = new StringEvent();
            stringEvent.setStr(group.getId());
            eventBus.post(stringEvent);
           /* contact_info=(LinearLayout)findViewById(R.id.contact_info);
            contact_info.setOnClickListener(showContactInfo);*/
           /* if (group.getCreation_status() == 2) {
                message_layout.setVisibility(View.GONE);
                left_grp_message.setVisibility(View.VISIBLE);
            }*/
            isGroupMute = db_helper_contact.checkGroupmuteStatus(group);
            if (savedInstanceState != null) {
                isGroupMute = savedInstanceState.getBoolean("isGroupMute");
            }
            //check creation status if =2 no longer a participant
            checkGroupCreation();
        }

        //set chat color
        //setchatcolor(db_helper.getcolor(cmw.getId()));
        mStatusChecker.run();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        celme_connect_layout = (FrameLayout) findViewById(R.id.celme_connect_layout);
        connect_dots = (TextView) findViewById(R.id.connect_dots);
        cancel_connect = (ImageView) findViewById(R.id.cancel_connect);
        recyclerView = (RecyclerView) findViewById(R.id.message_recycler_view);
        condensedrecylerview=(RecyclerView)findViewById(R.id.condensed_recycler_view);
        RecyclerView.LayoutManager LayoutManager = new LinearLayoutManager(ChatWindow.this);
        RecyclerView.LayoutManager LayoutManager1 = new LinearLayoutManager(ChatWindow.this);

        if(isCondensed)
        {
         condensedrecylerview.setVisibility(View.VISIBLE);
         recyclerView.setVisibility(View.GONE);
        }else{
            condensedrecylerview.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }

        recyclerView.setLayoutManager(LayoutManager);
        condensedrecylerview.setLayoutManager(LayoutManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        messageList = new ArrayList<>();
        messageCondensedList=new ArrayList<>();
        messageList.addAll(db_helper.getAllMessages(isContact ? cmw.getId() : group.getId()));
        updatecondensedMessageList();

        setMessageAdapter = new SetMessageAdapter(messageList, ChatWindow.this, isContact);
        setCondensedMessageAdapter=new SetMessageAdapter(messageCondensedList,ChatWindow.this,isContact);
        recyclerView.setAdapter(setMessageAdapter);
        condensedrecylerview.setAdapter(setCondensedMessageAdapter);


        // USE THE CODE TO LOAD MORE MESSAGES USING THE onLoadMore to get next batch of messages.
        /*
        recyclerView.setOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager)LayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //Toast.makeText(ChatWindow.this,"udi babaaa",Toast.LENGTH_SHORT).show();
            }
        });
        */


        //COMMENT THE BELOW LINE IF USING PAGINATION.
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        condensedrecylerview.scrollToPosition(condensedrecylerview.getAdapter().getItemCount()-1);

        //RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rootView);


        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                if (emojiPopup.isShowing()) {
                    emojiPopup.dismiss();
                }
            }
        }).build(message_body);



        ImageView emo_icon = (ImageView) findViewById(R.id.emo);
        emo_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
            }
        });


/* chatEmoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout emotwrapper = (RelativeLayout) findViewById(R.id.emolayout);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(message_body.getWindowToken(), 0);
                ViewGroup.LayoutParams params = emotwrapper.getLayoutParams();
                if (params.height > 0) {
                    params.height = 0;
                } else {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, displaymetrics);
                    params.height = 600;
                }
                emotwrapper.setLayoutParams(params);
                message_body.clearFocus();
            }
        });*/

chatEmoticon.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        // finding X and Y co-ordinates
        Log.d("POP","Popup show:"+isPopUpShown);
        Log.d("POP","isKeyboardHidden"+isKeyBoardHidden);
        if(isPopUpShown)
        {
            isPopUpShown=false;
            popupWindow.dismiss();
        }else
        {
            if(isKeyBoardHidden) {

                int cx = (chatEmoticon.getLeft() + chatEmoticon.getRight()) / 2;
                int cy = (chatEmoticon.getTop());

                // to find  radius when icon is tapped for showing layout
                int startradius = 0;
                int endradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

                // performing circular reveal when icon will be tapped
                Animator animator = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    animator = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, startradius, endradius);
                }
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(300);

                //reverse animation
                // to find radius when icon is tapped again for hiding layout
                //  starting radius will be the radius or the extent to which circular reveal animation is to be shown

                int reverse_startradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());


                //endradius will be zero
                int reverse_endradius = 0;

                // performing circular reveal for reverse animation
                Animator animate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                    animate = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, reverse_startradius, reverse_endradius);
                }
                final Animator finalAnimate = animate;


                if (hidden) {

                    // to show the layout when icon is tapped
                    revealFrameLayout.setVisibility(View.VISIBLE);
                    mRevealView.setVisibility(View.VISIBLE);
                    animator.start();
                    hidden = false;
                } else {

                    // mRevealView.setVisibility(View.VISIBLE);
                    //revealFrameLayout.setVisibility(View.VISIBLE);


                    // to hide layout on animation end
                    try {
                        animate.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                // mRevealView.setVisibility(View.INVISIBLE);
                                revealFrameLayout.setVisibility(View.GONE);
                                hidden = true;
                            }
                        });
                        animate.start();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                //keyboard is showing show on top of the keyboard
                // final RelativeLayout rootView=(RelativeLayout)findViewById(R.id.rootViewSecondary);
                //rootView.removeAllViews();


                final Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);

                int heightDifference = getUsableScreenHeight() - (rect.bottom - rect.top);
                if(condensedrecylerview.getVisibility()==View.VISIBLE)
                {
                    RelativeLayout.LayoutParams recylerviewParams
                            = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    recylerviewParams.topMargin=(int)heightDifference/2;
                    condensedContainer.setLayoutParams(recylerviewParams);
                }

                final Resources resources = getResources();
                final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");

                if (resourceId > 0) {
                    heightDifference -= resources.getDimensionPixelSize(resourceId);
                }
                popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                popupWindow.setHeight(heightDifference);

                if(!isKeyBoardHidden)
                {
                    Log.d("KEyboard",""+isKeyBoardHidden);
                    //showAtBottom();
                    popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                    isPopUpShown=true;
                    isKeyBoardHidden=true;
                    hideSoftKeyboard(message_body);

                }
            }
        }

    }
});


    action_overflow.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //this is to toggle recylerview to condensed recyclerview and vice versa
            if(isCondensed)
            {
                isCondensed=false;
                recyclerView.setVisibility(View.VISIBLE);
                condensedrecylerview.setVisibility(View.GONE);
                recyclerView.scrollToPosition(messageList.size()-1);
            }else
            {
                isCondensed=true;
                recyclerView.setVisibility(View.GONE);
                condensedrecylerview.setVisibility(View.VISIBLE);
                if(!isKeyBoardHidden||isPopUpShown)
                {
                    final Rect rect = new Rect();
                    rootView.getWindowVisibleDisplayFrame(rect);

                    int heightDifference = getUsableScreenHeight() - (rect.bottom - rect.top);
                    RelativeLayout.LayoutParams recylerviewParams
                            = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    recylerviewParams.topMargin = (int) heightDifference / 3;
                    condensedContainer.setLayoutParams(recylerviewParams);

                }else {
                    RelativeLayout.LayoutParams recylerviewParams
                            = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    recylerviewParams.topMargin = (int) getScreenHeight() / 2;
                    condensedContainer.setLayoutParams(recylerviewParams);
                }
                updatecondensedMessageList();
            }
        }
    });

        message_body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(messageList.size()>0) {
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }

                if (emojiPopup.isShowing()) {
                    emojiPopup.dismiss();
                }
                if(popupWindow!=null) {
                    if(isPopUpShown) {
                        isPopUpShown=false;
                        popupWindow.dismiss();
                    }
                }
            }
        });
        message_body.setOnFocusChangeListener(new View.OnFocusChangeListener() {


            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("Focus","Called : "+hasFocus);
                if (hasFocus) {
                    RelativeLayout emotwrapper = (RelativeLayout) findViewById(R.id.emolayout);
                    ViewGroup.LayoutParams params = emotwrapper.getLayoutParams();
                    params.height = 0;
                    emotwrapper.setLayoutParams(params);

                }
            }
        });
        message_body.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mTyping) {
                    mTyping = true;

                    JSONObject typingobject = new JSONObject();
                    try {

                        typingobject.put("sender", sender);
                        Log.i("TYPING_SENDER_START", "" + sender);
                        typingobject.put("group", !isContact);   //Decides if msg need to be sent to a group or single person
                        typingobject.put("receiver", (isContact ? cmw.getId() : group.getId()));
                        typingobject.put("uniquegroupid", (!isContact ? group.getId() : ""));
                        eventBus.post(new SendTypingEvent(true, typingobject));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (message_body.length() == 0) {
                    //sendmessage.setEnabled(false);
                    sendmessage.setVisibility(View.GONE);
                    sendAudio.setVisibility(View.VISIBLE);
                } else {
                    // sendmessage.setEnabled(true);
                    sendmessage.setVisibility(View.VISIBLE);
                    sendAudio.setVisibility(View.GONE);
                }
            }
        });


        implementRecyclerViewClickListeners();

        if (getIntent().getBooleanExtra("frwrd", false)) {

            Bundle args = getIntent().getBundleExtra("BUNDLE");
            ArrayList<Message> list = (ArrayList<Message>) args.getSerializable("ARRAYLIST");

            for (Message msg : list) {
                forward_msg(msg);
                Log.d("Anuraj_specl", "yes");
            }
        }

        if (isContact) {
            final CircleImageView profileImage = (CircleImageView) findViewById(R.id.contact_image155);
            String userid = db_helper_contact.getUserid(cmw.getId());
            // getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                profileImage.setTransitionName("profile");
            }
            Picasso.with(this).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/EarnChat/" + userid + "/" + cmw.getImgname())).error(R.drawable.default_user).fit().into(profileImage, new Callback() {
                @Override
                public void onSuccess() {
                   // Toast.makeText(ChatWindow.this, "Success Loading Image", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError() {
                     Picasso.with(ChatWindow.this).load(AppConstants.AWS_PROFILEIMAGE+cmw.getImgname()).error(R.drawable.default_user).transform(new RoundedCornersTransform()).into(profileImage);
                }
            });
        }
        //TODO:group chat dp update
        //rohan 30/12/2017
        else
        {
            CircleImageView profileImage = (CircleImageView) findViewById(R.id.contact_image155);
            //getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                profileImage.setTransitionName("profile");
            }
            Picasso.with(this).load(AppConstants.AWS_IMAGEBUCKET_GROUP+group.getDp_url()).error(R.drawable.default_user).fit().into(profileImage);

        }


       frameLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.i("TOUCH","WORKING");
               if(revealFrameLayout.getVisibility()==View.VISIBLE)
               {
                   revealFrameLayout.setVisibility(View.GONE);

               }
           }
       });

    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */

    private void initKeyBoardListener() {
        // Минимальное значение клавиатуры. Threshold for minimal keyboard height.
        final int MIN_KEYBOARD_HEIGHT_PX = 150;
        // Окно верхнего уровня view. Top-level window decor view.
        final View decorView = getWindow().getDecorView();
        // Регистрируем глобальный слушатель. Register global layout listener.
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            // Видимый прямоугольник внутри окна. Retrieve visible rectangle inside window.
            private final Rect windowVisibleDisplayFrame = new Rect();
            private int lastVisibleDecorViewHeight;

            @Override
            public void onGlobalLayout() {
                Log.d("POP","Global Layout is called");
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();
                final Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);

                int heightDifference = getUsableScreenHeight() - (rect.bottom - rect.top);

                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        RelativeLayout.LayoutParams recylerviewParams
                                = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        recylerviewParams.topMargin=(int)heightDifference/3;
                        condensedContainer.setLayoutParams(recylerviewParams);
                        if(isPopUpShown)
                        {
                            isKeyBoardHidden=true;
                        }else {
                            isKeyBoardHidden = false;
                        }
                        KEY_BACKPRESSED=0;
                        Log.d("Pasha", "SHOW");
                        Log.d("Pasha", "Keyboard is hidden"+isKeyBoardHidden);
                        if(!isCondensed&&messageList.size()>0)
                        {
                            recyclerView.scrollToPosition(messageList.size()-1);
                        }


                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        if(isPopUpShown) {
                            isPopUpShown = false;
                            KEY_BACKPRESSED=1;
                            popupWindow.dismiss();
                        }
                        isKeyBoardHidden=true;
                        Log.d("Pasha", "HIDE");
                        RelativeLayout.LayoutParams recylerviewParams
                                = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        recylerviewParams.topMargin=(int)getScreenHeight()/2;
                        condensedContainer.setLayoutParams(recylerviewParams);

                    }
                }
                // Сохраняем текущую высоту view до следующего вызова.
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight;
            }
        });
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putBoolean("isMute", isMute);
        outState.putBoolean("isGroupMute", isGroupMute);
        outState.putBoolean("isBlock", isBlock);
        outState.putInt("count", count);
        outState.putParcelable("file_uri", fileUri);
    }

    /*
     * Here we restore the fileUri again
     */

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        isMute = savedInstanceState.getBoolean("isMute");
        isGroupMute = savedInstanceState.getBoolean("isGroupMute");
        isBlock = savedInstanceState.getBoolean("isBlock");
        count = savedInstanceState.getInt("count");
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputMediaFileUri(int type) {

        /*Uri photoURI = Uri.fromFile(createImageFile())
Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile());*/

        //return Uri.fromFile(getOutputMediaFile(type));
        //Log.d("package", getApplicationContext().getPackageName() + ".com.release.chikoopapp.celgram.provider");
        return FileProvider.getUriForFile(this, "com.rohan7055.earnchat.provider", getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "EarnChat_Images");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof RevealFrameLayout)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if(revealFrameLayout.getVisibility()==View.VISIBLE)
                    {
                        revealFrameLayout.setVisibility(View.GONE);
                    }
                    return false;
                }
            });

        }


        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    //Implement item click and long click over recycler view
    private void implementRecyclerViewClickListeners() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, int position) {
                //If ActionMode not null select item
                if (mActionMode != null)
                    onListItemSelect(position);

            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                onListItemSelect(position);
            }
        }));
    }

    //List item select method
    private void onListItemSelect(int position) {
        /*setMessageAdapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = setMessageAdapter.getSelectedCount() > 0;//Check if any items are already selected or not
        if(setMessageAdapter.getSelectedCount()>1)
        {
            mActionMode.invalidate();
        }


        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = startSupportActionMode(new Toolbar_ActionMode_Callback(this,setMessageAdapter,(ArrayList) messageList,db_helper));
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null)
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(setMessageAdapter
                    .getSelectedCount()) + " selected");*/

        setMessageAdapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = setMessageAdapter.getSelectedCount() > 0;//Check if any items are already selected or not
        if (setMessageAdapter.getSelectedCount() > 1) {
            mActionMode.invalidate();
        }

        if (hasCheckedItems && mActionMode == null) {
            // there are some selected items, start the actionMode
            SetMessageAdapter.notselected = false;
            mActionMode = startSupportActionMode(new Toolbar_ActionMode_Callback(this, setMessageAdapter, (ArrayList) messageList, db_helper));
        } else if (!hasCheckedItems && mActionMode != null) {
            // there no selected items, finish the actionMode
            mActionMode.finish();
        } else if (setMessageAdapter.getSelectedCount() == 1 && mActionMode != null) {
            mActionMode.invalidate();
        }

        if (mActionMode != null)
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(setMessageAdapter
                    .getSelectedCount()) + " selected");


    }

    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    //Delete selected rows
    public void deleteRows() {
        SparseBooleanArray selected = setMessageAdapter
                .getSelectedIds();//Get selected ids

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {

                //If current id is selected remove the item via key
                messageList.remove(selected.keyAt(i));
                updatecondensedMessageList();
                setMessageAdapter.notifyDataSetChanged();//notify adapter

            }
        }
        //Toast.makeText(getApplicationContext(), selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();//Show Toast
        mActionMode.finish();//Finish action mode after use

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isContact) {
            getMenuInflater().inflate(R.menu.chat_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.group_chat_menu, menu);
        }
        return true;
    }


    @Subscribe
    public void OnGroupCreationChange(GroupCreationEvent event)
    {
        if(event.getGroupId().equals(group.getId()))
        {
            group.setCreation_status(event.getStatus());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkGroupCreation();
                }
            });

        }
    }

    @Override

    public boolean onPrepareOptionsMenu(Menu menu)

    {  //Toast.makeText(this,"OnprepareoptionMenuiscalled",Toast.LENGTH_LONG).show();
        super.onPrepareOptionsMenu(menu);

        if (isContact) {

            MenuItem menuItem = menu.findItem(R.id.mute);
            MenuItem menuItemBlock = menu.findItem(R.id.block);
            if (!isMute) {
                menuItem.setTitle("Mute");

            } else {
                menuItem.setTitle("Unmute");
            }
            if (!isBlock) {
                menuItemBlock.setTitle("Block");
            } else {
                menuItemBlock.setTitle("UnBlock");
            }
        } else {
            MenuItem menuItem = menu.findItem(R.id.group_mute);
            if (!isGroupMute) {
                menuItem.setTitle("Mute");

            } else {
                menuItem.setTitle("Unmute");

            }
        }
        return true;
    }

    /*public void onClick(View v) {
        switch (v.getId()) {
            case R.id.audio:
                Snackbar.make(v, "Audio Clicked", Snackbar.LENGTH_SHORT).show();
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;
            case R.id.camera:
                Snackbar.make(v, "Camera Clicked", Snackbar.LENGTH_SHORT).show();
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;
            case R.id.location:
                Snackbar.make(v, "Location Clicked", Snackbar.LENGTH_SHORT).show();
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;
            case R.id.contacts:
                Snackbar.make(v, "Contacts Clicked", Snackbar.LENGTH_SHORT).show();
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;
            case R.id.video:
                Snackbar.make(v, "Video Clicked", Snackbar.LENGTH_SHORT).show();
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;
            case R.id.gallery:
                Snackbar.make(v, "Gallery Clicked", Snackbar.LENGTH_SHORT).show();
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;
        }
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (isContact) {
            if (id == R.id.mute) {


                if (db_helper_contact.checkmuteStatus(cmw)) {

                    item.setTitle("UnMute");
                    db_helper_contact.unmuteContact(cmw);
                    isMute = false;

                } else {
                    item.setTitle("Mute");
                    db_helper_contact.contactMute(cmw);
                    isMute = true;
                }
            } else if (id == R.id.block) {
                if (isContact) {
                    if (id == R.id.block) {
                        if (db_helper_contact.checkBlockedStatus(cmw)) {

                            item.setTitle("UnBlock");
                            db_helper_contact.unblockContact(cmw);
                            isBlock = false;
                            // Toast.makeText(getApplicationContext(), "UnMute", Toast.LENGTH_SHORT).show();
                        } else {
                            item.setTitle("Block");
                            db_helper_contact.contactBlock(cmw);
                            isBlock = true;
                            // count = count + 1;
                            //Toast.makeText(getApplicationContext(), "Mute", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //Toast.makeText(getApplicationContext(), "Block", Toast.LENGTH_SHORT).show();
                }
            } else if (id == R.id.clear_chat) {

                db_helper.clearChat(cmw.getId());
                if (messageList != null) {
                    messageList.clear();
                    messageCondensedList.clear();
                }
                setMessageAdapter.notifyDataSetChanged();
                updatecondensedMessageList();
                Toast.makeText(ChatWindow.this, "Chat Cleared Succesfully ", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.email_chat) {
                SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);


//            Email chat.
//
                List<Message> msglist;
                msglist = db_helper.getAllMessages(cmw.getId());
                allmsg = new StringBuilder();
                for (Message s : msglist) {
                    if (s.isSelf()) {
                        String x = preferences.getString("firstname", "") + " " + preferences.getString("lastname", "");
                        String y = s.getData();
                        allmsg.append(x);
                        allmsg.append(" : ");
                        allmsg.append(y);
                        allmsg.append("   :   ");
                        allmsg.append(s.getTimestamp());
                        allmsg.append("\n");
                    } else {
                        String x = s.getConvo_partner();
                        String x3 = db_helper_contact.getChikoopName(x);
                        String y = s.getData();
                        allmsg.append(x3);
                        allmsg.append("  :  ");
                        allmsg.append(y);
                        allmsg.append("   :   ");
                        allmsg.append(s.getTimestamp());
                        allmsg.append("\n");
                    }


                    FILE = Environment.getExternalStorageDirectory().toString()
                            + "/EarnChat/" + "Mailchat.pdf";

                    document = new Document(PageSize.A4);
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/EarnChat");
                    myDir.mkdirs();

                    try {
                        PdfWriter.getInstance(document, new FileOutputStream(FILE));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    document.open();


                    addMetaData(document);


                    try {
                        addTitlePage(document);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    document.close();
                }
                sendMail(FILE);
            }
            //Adding chat shortcut
            if (id == R.id.add_shortcut) {
                Intent intent = new Intent(this, ChatWindow.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("isContact", true);
                byte[] contactSerial = serializeObject(cmw);
                intent.putExtra("contactSerial", contactSerial);
                Intent addIntent = new Intent();
                addIntent
                        .putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, cmw.getDisplay_name());
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                        Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                                R.drawable.default_user));

                addIntent
                        .setAction("com.android.launcher.action.INSTALL_SHORTCUT");

                // addIntent.putExtra("duplicate", false);  //may it's already there so don't duplicate
                getApplicationContext().sendBroadcast(addIntent);

            }
            if (id == R.id.theme) {

                final Dialog dialog = new Dialog(ChatWindow.this);
                dialog.setContentView(R.layout.theme_layout);
                //Button dialogButton = (Button) dialog.findViewById(R.id.done);

                // if button is clicked, close the custom dialog
                c0 = (ImageButton) dialog.findViewById(R.id.c0);
                c1 = (ImageButton) dialog.findViewById(R.id.c1);
                c2 = (ImageButton) dialog.findViewById(R.id.c2);
                c3 = (ImageButton) dialog.findViewById(R.id.c3);
                c4 = (ImageButton) dialog.findViewById(R.id.c4);
                c0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        color = "#AEAEAE";
                        db_helper.updatecolor(color, cmw.getId());
                        setchatcolor(color);
                        //finish();
                        dialog.dismiss();
                        recreate();
                    }
                });
                c1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        color = "#33B5E5";
                        db_helper.updatecolor(color, cmw.getId());
                        setchatcolor(color);
                        //finish();
                        dialog.dismiss();
                        recreate();
                    }
                });
                c2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        color = "#D72D69";
                        db_helper.updatecolor(color, cmw.getId());
                        setchatcolor(color);
                        //finish();
                        dialog.dismiss();
                        recreate();
                    }
                });
                c3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        color = "#72C1BE";
                        db_helper.updatecolor(color, cmw.getId());
                        setchatcolor(color);
                        //finish();
                        dialog.dismiss();
                        recreate();
                    }
                });
                c4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        color = "#E5AF5E";
                        db_helper.updatecolor(color, cmw.getId());
                        setchatcolor(color);
                        //finish();
                        dialog.dismiss();
                        recreate();
                    }
                });
                /*dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db_helper.updatecolor(color,cmw.getId());
                        setchatcolor(color);
                        //finish();
                        dialog.dismiss();
                        recreate();
                    }
                });*/

                dialog.show();
            }
          /*  if (id == R.id.wallpaper) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                //    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                //    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, SELECT_PHOTO);

            }*/

        } else {

            if (id == R.id.group_edit) {
                Toast.makeText(getApplicationContext(), "EDIT GROUP", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), GroupInfo.class);
                i.putExtra("group", group);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else if (id == R.id.group_mute) {
                if (db_helper_contact.checkGroupmuteStatus(group)) {

                    item.setTitle("Unmute");
                    db_helper_contact.unmuteGroup(group);
                    isGroupMute = false;
                    Toast.makeText(getApplicationContext(), "Mute", Toast.LENGTH_SHORT).show();

                } else {
                    item.setTitle("Mute");
                    db_helper_contact.groupMute(group);
                    isGroupMute = true;
                    Toast.makeText(getApplicationContext(), "UnMute", Toast.LENGTH_SHORT).show();

                }
            } else if (id == R.id.group_clear) {
                db_helper.clearChat(group.getId());
                if (messageList != null) {
                    messageList.clear();
                }
                setMessageAdapter.notifyDataSetChanged();
                updatecondensedMessageList();
                Toast.makeText(ChatWindow.this, "Chat Cleared Succesfully ", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Clear Chat", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.group_shortcut) {
                Intent intent = new Intent(this, ChatWindow.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("isContact", false);
                byte[] groupSerial = serializeObject(group);
                intent.putExtra("groupSerial", groupSerial);
                Intent addIntent = new Intent();
                addIntent
                        .putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, group.getDisplay_name());
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                        Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                                R.drawable.default_user));

                addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

                // addIntent.putExtra("duplicate", false);  //may it's already there so don't duplicate
                getApplicationContext().sendBroadcast(addIntent);
            } else if (id == R.id.group_email)
                Toast.makeText(getApplicationContext(), "Email Chat", Toast.LENGTH_SHORT).show();

          /*  else if (id == R.id.groupd)
                Toast.makeText(getApplicationContext(), "Delete Group", Toast.LENGTH_SHORT).show();*/
        }

        if (id == R.id.action_attachment) {// attachment icon click event


            // finding X and Y co-ordinates
            int cx = (mRevealView.getLeft() + mRevealView.getRight());
            int cy = (mRevealView.getTop());

            // to find  radius when icon is tapped for showing layout
            int startradius = 0;
            int endradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

            // performing circular reveal when icon will be tapped
            Animator animator = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                animator = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, startradius, endradius);
            }
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(300);

            //reverse animation
            // to find radius when icon is tapped again for hiding layout
            //  starting radius will be the radius or the extent to which circular reveal animation is to be shown

            int reverse_startradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());


            //endradius will be zero
            int reverse_endradius = 0;

            // performing circular reveal for reverse animation
            Animator animate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                animate = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, reverse_startradius, reverse_endradius);
            }
            final Animator finalAnimate = animate;


            if (hidden) {

                // to show the layout when icon is tapped
                mRevealView.setVisibility(View.VISIBLE);
                animator.start();
                hidden = false;
            } else {

                mRevealView.setVisibility(View.VISIBLE);

                // to hide layout on animation end
                try {
                    animate.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;
                        }
                    });
                    animate.start();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

//        if(id== R.id.chat_menu_two){
//            / bundling up the contact details and sending to populate the contacts details page.
//
//            String contact_name=cmw.getDisplay_name();
//            String contact_id=cmw.getId();
//            String contact_uname=cmw.getUname();
//            String email=cmw.getChikoop_name();
//            Toast.makeText(ChatWindow.this,contact_name,Toast.LENGTH_SHORT).show();
//            Intent i = new Intent(ChatWindow.this, ContactDetails.class);
//            i.putExtra("CONTACT_NAME", contact_name);
//            i.putExtra("ID",contact_id);
//            i.putExtra("UNAME",contact_uname);
//            i.putExtra("MAIL",email);
//            startActivity(i);
//        }

        return super.onOptionsItemSelected(item);
    }

    private void setchatcolor(String color) {
        // color="#ff9902";
        Drawable background = getApplicationContext().getResources().getDrawable(R.drawable.round_background8);
        try {
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable) background).getPaint().setColor(Color.parseColor(color));
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(Color.parseColor(color));
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable) background).setColor(Color.parseColor(color));
            }

        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }


        Drawable background2 = getApplicationContext().getResources().getDrawable(R.drawable.media_corner);
        try {
            if (background2 instanceof ShapeDrawable) {
                ((ShapeDrawable) background2).getPaint().setColor(Color.parseColor(color));
            } else if (background2 instanceof GradientDrawable) {
                ((GradientDrawable) background2).setColor(Color.parseColor(color));
            } else if (background2 instanceof ColorDrawable) {
                ((ColorDrawable) background2).setColor(Color.parseColor(color));
            }

        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        Drawable background3 = getApplicationContext().getResources().getDrawable(R.drawable.text_round);
        try {
            if (background3 instanceof ShapeDrawable) {
                ((ShapeDrawable) background3).getPaint().setColor(Color.parseColor(color));
            } else if (background3 instanceof GradientDrawable) {
                ((GradientDrawable) background3).setColor(Color.parseColor(color));
            } else if (background3 instanceof ColorDrawable) {
                ((ColorDrawable) background3).setColor(Color.parseColor(color));
            }

        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void addMetaData(Document document) {
        document.addTitle("Celgram Chat History");
        document.addSubject("OUR CHAT");

    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void addTitlePage(Document document) throws DocumentException {
        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        String chikoopname = preferences.getString("firstname", "") + preferences.getString("lastname", "");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        Paragraph para = new Paragraph();
        //we add One Empty Line
        addEmptyLine(para, 1);
        //Lets Write A bigger Header
        para.add(new Paragraph("Celgram Chat History", catFont));
        //add one Empty Line
        addEmptyLine(para, 1);
        para.add(new Paragraph("Chat History generated by : " + chikoopname + "  ,  " + formattedDate, smallBold));
        addEmptyLine(para, 3);
        para.add(new Paragraph(allmsg.toString(), subFont));
        document.add(para);


        //  Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD );
        // Paragraph prHead = new Paragraph();
        // prHead.setFont(titleFont);
        // prHead.setAlignment(Element.ALIGN_CENTER);
        /// prHead.add("OUR CHAT \n \n \n");
        // document.add(prHead);



/*
        Font paraFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD );
        para.setFont(paraFont);
        para.add(allmsg.toString());
        document.add(para);*/

        /*
        for(int i=0;i<finres.length;i++){
            para.add(i+1 + ")" + " ");
            para.add(finres[i]);
            para.add("\n");
        */
    }

    public void startPreviewActivity(String filepath, Uri uri) {
        /*ArrayList<String> images = new ArrayList<>();
        images.add(filepath);
        // ADD MORE images if multiple images is used.

        Intent intent = new Intent(ChatWindow.this, ImagePreviewActivity.class);
        intent.putStringArrayListExtra(ImagePreviewActivity.EXTRA_NAME, images);
        String urisend=uri.toString();
        intent.putExtra("uri",urisend);
        startActivityForResult(intent,10);*/

        Intent intent = new Intent(ChatWindow.this, ImageSendIntermediateActivity.class);
        intent.putExtra("path", filepath);
        String urisend = uri.toString();
        intent.putExtra("uri", urisend);
        startActivityForResult(intent, 10);
    }


    public void sendMail(String filename) {
        String mailto = "";
        Uri uri = Uri.fromFile(new File(filename));
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "EARNCHAT!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, " OUR CHAT");
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Send email using:"));




            /*
            String filename="Mailchat.pdf";
            File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
            Uri path = Uri.fromFile(filelocation);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // set the type to 'email'
            emailIntent .setType("vnd.android.cursor.dir/email");
            String to[] = {"shubhang.periwal@gmail.com"};
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent .putExtra(Intent.EXTRA_STREAM, path);
            // the mail subject
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "OUR CHAT");
            startActivity(Intent.createChooser(emailIntent , "Send email..."));
            */


    }

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public void sendMessage(View v) {

        String msg = message_body.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            message_body.requestFocus();
            return;
        }
        message_body.setText("");

        if (replyMessage) {

            if (isContact) {
                //Message is class to identify what type messages are being sent,you can see it in free time
                //type of message and parameters
                //cmw.getId() fetch the mobile number of the user to whom you are sending the message
                message = new Message(cmw.getId(), true, 14, ReplyToMsgId, ReplyToMsgType, msg, "", ReplyData);
                message.setMsg_url(ReplyTo);
            } else {
                message = new Message(group.getId(), sender, true, 14, ReplyToMsgId, ReplyToMsgType, msg, "", ReplyData);
                message.setMsg_url(ReplyTo);
            }

            db_helper.addMessage(message);
            db_helper.addToChatList(message.getConvo_partner(), message.getId());
            appendMessage(message);
            eventBus.post(message);
            MessageEvent messageEvent = new MessageEvent(message);//This is the meesage event and subscriber is in the service class
            eventBus.post(messageEvent);//this emits the event

            editLayout.setBackgroundResource(R.drawable.round_background9);
            replyLay.setVisibility(View.GONE);
            replyMessage = false;
        } else {
            if (isContact) {
                message = new Message(cmw.getId(), true, 0, "", 0, msg, "", "");
                Log.d("msg_sent_cmwId", cmw.getId());
            } else {
                message = new Message(group.getId(), sender, true, 0, "", 0, msg, "", "");
            }

            db_helper.addMessage(message);
            db_helper.addToChatList(message.getConvo_partner(), message.getId());
            appendMessage(message);
            eventBus.post(message);
            MessageEvent messageEvent = new MessageEvent(message);
            eventBus.post(messageEvent);
        }

        /*if(isContact){
            message=new Message(cmw.getId(),true,0,"",0,msg,"","");
            Log.d("msg_sent_cmwId", cmw.getId());
        }
        else{
            message=new Message(group.getId(),sender,true,0,"",0,msg,"","");
        }

        db_helper.addMessage(message);
        db_helper.addToChatList(message.getConvo_partner(),message.getId());
        appendMessage(message);
        eventBus.post(message);
        MessageEvent messageEvent=new MessageEvent(message);
        eventBus.post(messageEvent); */

        /*Intent serviceIntent = new Intent(getApplicationContext(), SendMessageService.class);
        serviceIntent.setAction(SendMessageService.ACTION_SEND_FILE);
        serviceIntent.putExtra(SendMessageService.EXTRAS_ADDRESS, clientIp);
        serviceIntent.putExtra(SendMessageService.EXTRAS_PORT, MESSAGE_PORT);
        serviceIntent.putExtra(SendMessageService.MESSAGE,msg);
        getApplicationContext().startService(serviceIntent);*/

    }

    public void sendDocument(Intent data) throws NullPointerException {

        Uri uri = data.getData();
        String filepath = AwsUtils.getPath(getApplicationContext(), data.getData());
        File doc = new File(filepath);
        Message message;
        if (isContact) {

            message = new Message(cmw.getId(), true, 4, getMimeType(this, uri), (int) doc.length(), doc.getName(), filepath, "");
        } else {
            message = new Message(group.getId(), sender, true, 4, getMimeType(this, uri), (int) doc.length(), doc.getName(), filepath, "");
        }

        message.setMsg_status(-1);
        db_helper.addMessage(message);
        db_helper.addToChatList(message.getConvo_partner(), message.getId());
        appendMessage(message);
        startUpload(message);

    }

    public void sendContact(Intent data) {
        Cursor cursor = null;
        try {
            String Name, Number;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            Number = cursor.getString(phoneIndex);
            Name = cursor.getString(nameIndex);

            Intent intent = new Intent(ChatWindow.this, Contact_View.class);
            intent.putExtra("NAME", Name);
            intent.putExtra("NUMBER", Number);
            startActivityForResult(intent, 609);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public void ConfirmSendContact(Intent data) {
        if (data.getStringExtra("ACTION").equals("CANCEL"))
            return;
        try {
            String Number = data.getStringExtra("NUMBER");
            String Name = data.getStringExtra("NAME");

            String contact = Name + "/" + Number;


            if (isContact) {

                message = new Message(cmw.getId(), true, 5, "", 0, contact, "", "");
                db_helper.addMessage(message);
                db_helper.addToChatList(message.getConvo_partner(), message.getId());

            } else {

                message = new Message(group.getId(), sender, true, 5, "", 0, contact, "", "");
                db_helper.addMessage(message);
                db_helper.addToChatList(message.getConvo_partner(), message.getId());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        appendMessage(message);
        eventBus.post(message);
        MessageEvent messageEvent = new MessageEvent(message);
        eventBus.post(messageEvent);
    }

    public void sendLocation(String msg, double latitude, double longitude) {


        if (TextUtils.isEmpty(msg)) {
            message_body.requestFocus();
            return;
        }
        message_body.setText("");

        msg = msg + "/" + latitude + "|" + longitude;
        // | is used to separate lat long values from the actual location.

        if (isContact) {

            // 13 for location message.

            message = new Message(cmw.getId(), true, 13, "", 0, msg, "", "");
            db_helper.addMessage(message);
            db_helper.addToChatList(message.getConvo_partner(), message.getId());

        } else {
            // 13 for location message.
            message = new Message(group.getId(), sender, true, 13, "", 0, msg, "", "");
            db_helper.addMessage(message);
            db_helper.addToChatList(message.getConvo_partner(), message.getId());

        }

        appendMessage(message);
        eventBus.post(message);
        MessageEvent messageEvent = new MessageEvent(message);
        eventBus.post(messageEvent);
    }

    //Anuraj
    public void forward_msg(Message message) {
        Log.d("msg_frwrder", message.getData());
        Log.d("msg_contact_id", message.getConvo_partner());
        Log.d("msg_isSelf", String.valueOf(message.isSelf()));
        Log.d("msg_msgType", String.valueOf(message.getMsg_type()));
        Log.d("msg_mimeType", message.getMime_type());
        Log.d("msg_mediaSize", String.valueOf(message.getMedia_size()));
        Log.d("msg_filePath", message.getFile_path());
        Log.d("msg_Duration", message.getDuration());

        /*Message finalMsg = new Message(message.getConvo_partner(),true,0,"",0,message.getData(),"","");

        //Log.d("msg_contact_id", message.getConvo_partner());
        MessageSQLiteHelper db_helper=new MessageSQLiteHelper(ChatWindow.this);
        db_helper.updateMessageStatus(finalMsg.getConvo_partner(), 2);
        db_helper.addMessage(finalMsg);
        db_helper.addToChatList(finalMsg.getConvo_partner(),String.valueOf(new Random().nextInt(9999)-1)+System.currentTimeMillis());
        appendMessage(finalMsg);

        EventBus eventBus=EventBus.getDefault();
        eventBus.register(this);
        StringEvent stringEvent = new StringEvent();
        stringEvent.setStr(finalMsg.getConvo_partner());
        eventBus.post(stringEvent);

        eventBus.post(finalMsg);
        MessageEvent messageEvent=new MessageEvent(finalMsg);
        eventBus.post(messageEvent);*/

        MessageEvent messageEvent = null;

        switch (message.getMsg_type()) {

            case 0:
                message = new Message(message.getConvo_partner(), true, 0, "", 0, message.getData(), "", "");
                db_helper.addMessage(message);
                db_helper.addToChatList(message.getConvo_partner(), message.getId());
                appendMessage(message);
                eventBus.post(message);
                messageEvent = new MessageEvent(message);
                eventBus.post(messageEvent);
                return;
            case 5:
                db_helper.addMessage(message);
                db_helper.addToChatList(message.getConvo_partner(), message.getId());
                appendMessage(message);
                eventBus.post(message);
                messageEvent = new MessageEvent(message);
                eventBus.post(messageEvent);
                return;
            case 13:
                db_helper.addMessage(message);
                db_helper.addToChatList(message.getConvo_partner(), message.getId());
                appendMessage(message);
                eventBus.post(message);
                messageEvent = new MessageEvent(message);
                eventBus.post(messageEvent);
                return;
            case 4:
                message.setMsg_status(-1);
                db_helper.addMessage(message);
                db_helper.addToChatList(message.getConvo_partner(), message.getId());
                appendMessage(message);
                startUpload(message);
                return;
            case 3:
                message.setMsg_status(-1);
                appendMessage(message);
                db_helper.addMessage(message);
                db_helper.addToChatList(message.getConvo_partner(), message.getId());
                startUpload(message);
                return;
            case 1:
                message.setMsg_status(-2);
                appendMessage(message);
                db_helper.addMessage(message);
                db_helper.addToChatList(message.getConvo_partner(), message.getId());
                startUpload(message);
                return;
            case 2:
                message.setMsg_status(-2);
                appendMessage(message);
                db_helper.addMessage(message);
                db_helper.addToChatList(message.getConvo_partner(), message.getId());
                startUpload(message);
                return;
            case 14:
                message = new Message(message.getConvo_partner(), true, 0, "", 0, message.getData(), "", "");
                db_helper.addMessage(message);
                db_helper.addToChatList(message.getConvo_partner(), message.getId());
                appendMessage(message);
                eventBus.post(message);
                messageEvent = new MessageEvent(message);
                eventBus.post(messageEvent);
                return;
            case 6:
                Message sticker_msg;
                sticker_msg = new Message(message.getConvo_partner(), true, 6, "", 0, message.getData(), "", "");
                db_helper.addMessage(sticker_msg);
                db_helper.addToChatList(sticker_msg.getConvo_partner(), sticker_msg.getId());
                appendMessage(sticker_msg);
                eventBus.post(sticker_msg);
                messageEvent = new MessageEvent(sticker_msg);
                eventBus.post(messageEvent);
                return;
            default:
                Toast.makeText(this, "Some error in forwarding the message", Toast.LENGTH_SHORT).show();

        }

    }

    public void giveIntent(SparseBooleanArray selected, ArrayList<Object> message_models) {

        Message model2 = null;
        ArrayList<Message> msg_list = new ArrayList<>();

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                model2 = (Message) message_models.get(selected.keyAt(i));

                msg_list.add(model2);

                /*db_helper.addMessage(message);
                db_helper.addToChatList(message.getConvo_partner(),message.getId());
                appendMessage(message);
                eventBus.post(message);
                MessageEvent messageEvent=new MessageEvent(message);
                eventBus.post(messageEvent);*/
            }
        }

        Intent intent = new Intent(ChatWindow.this, Forward_msg.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST", (Serializable) msg_list);
        intent.putExtra("BUNDLE", args);
        startActivity(intent);
        finish();
    }


    public void initiateCelme() {
        if (!isWifiP2pEnabled) {
            Toast.makeText(getApplicationContext(), "Enable your wifi for this feature", Toast.LENGTH_LONG).show();
        } else {
            connecting(1);

            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int reasonCode) {
                }
            });

        }


    }


    public void notifyDiscovery() {

        if (flag) {
            if (eflag) {
                Toast.makeText(getApplicationContext(), "Ensure your friend is within 100metres radius", Toast.LENGTH_LONG).show();
                eflag = false;
            } else {
                Toast.makeText(getApplicationContext(), "Connection aborted", Toast.LENGTH_LONG).show();
            }

            flag = false;
        }

    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());

        if (peers.size() == 0) {
            Log.d(ChatWindow.TAG, "No devices found");
            return;
        }

        for (int i = 0; i < peers.size(); i++) {
            if (peers.get(i).deviceName.equals("TEST_NAME")) {
                cflag = true;
                device = peers.get(i);
                config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                manager.connect(channel, config, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int reason) {

                    }
                });

                break;
            }

        }

        if (!cflag) {
            Toast.makeText(getApplicationContext(), "(Onpeersavailable)Ensure your friend is within 100metres radius", Toast.LENGTH_LONG).show();
            cflag = false;
        }

    }

    private void checkGroupCreation()
    {
        if(group.getCreation_status()==2)
        {
            sendAudio.setVisibility(View.INVISIBLE);
            sendmessage.setVisibility(View.INVISIBLE);
            bigmicrophone.setVisibility(View.INVISIBLE);
            message_body.setVisibility(View.INVISIBLE);
            exitGroupLayout.setVisibility(View.VISIBLE);
            mssgLayout.setVisibility(View.INVISIBLE);
        }
        else
        {   exitGroupLayout.setVisibility(View.GONE);
            sendmessage.setVisibility(View.GONE);
            bigmicrophone.setVisibility(View.GONE);
            sendAudio.setVisibility(View.VISIBLE);
            message_body.setVisibility(View.VISIBLE);
            mssgLayout.setVisibility(View.VISIBLE);


        }

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;

        if (info.isGroupOwner) {
            showToast("OWNER", false);
            connecting(2);
            ip_sent = true;


        } else {
            showToast("CLIENT", false);
            connecting(2);
            clientIp = info.groupOwnerAddress.getHostAddress();

            if (!ip_sent) {
                ping();
            }
        }

        if (!mserver_running) {
            message_task = new ReceiveMessageAsyncTask(getApplicationContext());
            message_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            mserver_running = true;
        }

        if (!server_running) {
            image_task = new ReceiveImageAsyncTask(getApplicationContext());
            image_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            server_running = true;
        }

        if (!vserver_running) {
            video_task = new ReceiveVideoAsyncTask(getApplicationContext());
            video_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            vserver_running = true;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            return cursor.getString(idx);
        }

    }

    @Override


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
//prachie
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {

                    Uri uri = data.getData();
                    File filepath = new File(getRealPathFromURI(uri));
                    Drawable d = Drawable.createFromPath(filepath.getAbsolutePath());
                    RelativeLayout r = (RelativeLayout) findViewById(R.id.rootView);
                    r.setBackground(d);


                }
                break;

            case 11:
                if (resultCode == RESULT_OK) {
                    if (fileUri.toString() != null) {
                        //Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                        Uri uri = fileUri;
                        if (fileUri == null) {
                            Toast.makeText(this, "fileUri is NULL", Toast.LENGTH_LONG).show();
                        } else {
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Uri finuri = getImageUri(ChatWindow.this, bitmap);
                            String filepath = AwsUtils.getPath(getApplicationContext(), finuri);
                            startPreviewActivity(filepath, finuri);
                        }


                    } else {
                        Toast.makeText(this, "Intent Data is null", Toast.LENGTH_LONG).show();
                    }
                }
                break;


            case CHOOSE_FILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {

                    Uri uri = data.getData();
                    String filepath = AwsUtils.getPath(getApplicationContext(), data.getData());
                    startPreviewActivity(filepath, uri);

                }
                break;


            case RESULT_PICK_CONTACT:
                if (resultCode == RESULT_OK) {
                    sendContact(data);
                } else
                    Log.e("MainActivity", "Failed to pick contact");
                break;


            case 200:
                if (resultCode == RESULT_OK) {
                    sendDocument(data);
                }
                break;

            case 609:
                if (resultCode == RESULT_OK) {
                    ConfirmSendContact(data);
                }
                break;

            case 10:
                if (resultCode == RESULT_OK) {

                    String filepath = data.getStringExtra("path");
                    String uri_rec = data.getStringExtra("uri");
                    String caption = data.getStringExtra("caption");
                    Uri uri = Uri.parse(uri_rec);
                    File media = new File(filepath);
                    Message message;

                    if (isContact) {

                        message = new Message(cmw.getId(), true, 1, getMimeType(this, uri), (int) media.length(), caption, filepath, "");
                    } else {
                        message = new Message(group.getId(), sender, true, 1, getMimeType(this, uri), (int) media.length(), caption, filepath, "");
                    }

                    message.setMsg_status(-2);
                    appendMessage(message);
                    db_helper.addMessage(message);
                    db_helper.addToChatList(message.getConvo_partner(), message.getId());
                    startUpload(message);

                }
                break;


            case 1:
                if (resultCode == RESULT_OK) {
                    //video

                    Uri uri = data.getData();
                    String filepath = AwsUtils.getPath(getApplicationContext(), data.getData());
                    Log.i(getApplication().toString(), filepath.toString());
                    Message message;
                    File media = new File(filepath);
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(filepath);
                    String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    mmr.release();

                    Integer dur = Integer.parseInt(duration);
                    duration = String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes((long) dur),
                            TimeUnit.MILLISECONDS.toSeconds((long) dur) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) dur)));

                    if (isContact) {

                        message = new Message(cmw.getId(), true, 2, getMimeType(this, uri), (int) media.length(), "", filepath, duration);
                    } else {
                        message = new Message(group.getId(), sender, true, 2, getMimeType(this, uri), (int) media.length(), "", filepath, duration);
                    }
                    message.setMsg_status(-2);
                    appendMessage(message);
                    db_helper.addMessage(message);
                    db_helper.addToChatList(message.getConvo_partner(), message.getId());
                    startUpload(message);

                }
                break;
            //audio message
            case 21:
                if (resultCode == RESULT_OK) {

                }
                break;
            //Rohan

            case 4:

                if (resultCode == RESULT_OK) {
                    //audio.

                    Uri uri = data.getData();
                    String filepath = AwsUtils.getPath(getApplicationContext(), data.getData());
                    Log.i("audio path", filepath);
                    File media = new File(filepath);
                    Message message;

                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(filepath);
                    String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    mmr.release();

                    Integer dur = Integer.parseInt(duration);
                    duration = String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes((long) dur),
                            TimeUnit.MILLISECONDS.toSeconds((long) dur) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) dur)));


                    if (isContact) {
                        message = new Message(cmw.getId(), true, 3, getMimeType(this, uri), (int) media.length(), "", filepath, duration);
                    } else {
                        message = new Message(group.getId(), sender, true, 3, getMimeType(this, uri), (int) media.length(), "", filepath, duration);
                    }

                    message.setMsg_status(-1);
                    appendMessage(message);
                    db_helper.addMessage(message);
                    db_helper.addToChatList(message.getConvo_partner(), message.getId());
                    startUpload(message);
                }
                break;
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public class MyParams2 {
        double lat;
        double longi;
        String fin_loc;

        public MyParams2(double lat, double longi, String fin_loc) {
            this.lat = lat;
            this.longi = longi;
            this.fin_loc = fin_loc;
        }

    }

    public class MyParams3 {
        GPSTracker gps;

        public MyParams3(GPSTracker gps) {
            this.gps = gps;

        }

    }

    public class getLocationNow extends AsyncTask<MyParams3, Void, MyParams2> {

        @Override
        protected MyParams2 doInBackground(MyParams3... params) {

            String location1 = "";
            String location2 = "";
            String location3 = "";
            String location4 = "";


            MyParams2 params2;

            double latitude = params[0].gps.getLatitude();
            double longitude = params[0].gps.getLongitude();


            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (null != listAddresses && listAddresses.size() > 0) {

                    location1 = listAddresses.get(0).getAddressLine(0);
                    if (location1 == null)
                        location1 = listAddresses.get(0).getAddressLine(0);
                    location2 = listAddresses.get(0).getAddressLine(1);
                    if (location2 == null)
                        location2 = listAddresses.get(0).getAddressLine(1);

                    location3 = listAddresses.get(0).getAddressLine(2);
                    if (location3 == null)
                        location3 = listAddresses.get(0).getAddressLine(2);

                    location4 = listAddresses.get(0).getAddressLine(3);
                    if (location4 == null)
                        location4 = listAddresses.get(0).getAddressLine(3);


                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String fin_loc = location1 + " " + location2 + " " + location3 + " " + location4;
            params2 = new MyParams2(latitude, longitude, fin_loc);


            return params2;
        }


        @Override
        protected void onPostExecute(MyParams2 params2) {
            super.onPostExecute(params2);

            sendLocation(params2.fin_loc, params2.lat, params2.longi);
        }
    }


    public static class ReceiveImageAsyncTask extends AsyncTask<Void, Void, String> {

        private final Context context;

        public ReceiveImageAsyncTask(Context context) {
            this.context = context;

        }

        @Override
        protected String doInBackground(Void... params) {
            Socket client = null;
            try {
                image_socket = new ServerSocket();
                image_socket.setReuseAddress(true);
                image_socket.bind(new InetSocketAddress(IMAGE_PORT));
                Log.d(ChatWindow.TAG, "Server: Socket opened");
                client = image_socket.accept();
                Log.d(ChatWindow.TAG, "Server: connection done");
                Log.d("INET", client.getInetAddress().toString());
                Log.d("LOCAL", client.getLocalAddress().toString());

                String Path = Environment.getExternalStorageDirectory().getPath().toString() + "/Chikoop/" + context.getPackageName() + "/Images";
                File FPath = new File(Path);
                if (!FPath.exists())
                    FPath.mkdirs();

                String fname = "image" + System.currentTimeMillis() + ".jpg";
                File f = new File(FPath, fname);
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    Log.e("FileErrror", e.getMessage());
                }

                Log.d(ChatWindow.TAG, "server: copying files " + f.toString());
                InputStream inputstream = client.getInputStream();
                OutputStream outputStream = new FileOutputStream(f);
                copyFile(inputstream, outputStream);
                image_socket.close();
                client.close();
                server_running = false;

                Log.d("Done", "Done in background");
                return f.getAbsolutePath();

            } catch (IOException e) {
                Log.e(ChatWindow.TAG, e.getMessage());
                return null;
            } finally {
                if (image_socket != null) {
                    try {
                        image_socket.close();
                    } catch (IOException e) {
                        Log.e("file Socket", e.getMessage());
                    }

                }

                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        Log.e("file Socket", e.getMessage());
                    }

                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (!server_running) {
                image_task = new ReceiveImageAsyncTask(context);
                image_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                server_running = true;
            }
            if (result != null) {
                Log.d(ChatWindow.TAG, "File Received");
             /*   Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);*/
                //displayImage("caption",result);
            }

        }

        @Override
        protected void onPreExecute() {

        }

    }

    public static class ReceiveVideoAsyncTask extends AsyncTask<Void, Void, String> {

        private final Context context;


        public ReceiveVideoAsyncTask(Context context) {
            this.context = context;

        }

        @Override
        protected String doInBackground(Void... params) {
            Socket client = null;
            try {
                video_socket = new ServerSocket();
                video_socket.setReuseAddress(true);
                video_socket.bind(new InetSocketAddress(VIDEO_PORT));
                Log.d(ChatWindow.TAG, "Server: Socket opened");
                client = video_socket.accept();
                Log.d(ChatWindow.TAG, "Server: connection done");
                Log.d("INET", client.getInetAddress().toString());
                Log.d("LOCAL", client.getLocalAddress().toString());

                String Path = Environment.getExternalStorageDirectory().getPath().toString() + "/Chikoop/" + context.getPackageName() + "/Videos";
                File FPath = new File(Path);
                if (!FPath.exists())
                    FPath.mkdirs();

                String fname = "video" + System.currentTimeMillis() + ".mp4";
                File f = new File(FPath, fname);
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    Log.e("FileErrror", e.getMessage());
                }
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    Log.e("FileErrror", e.getMessage());
                }
                Log.d(ChatWindow.TAG, "server: copying files " + f.toString());
                InputStream inputstream = client.getInputStream();
                OutputStream outputStream = new FileOutputStream(f);
                copyFile(inputstream, outputStream);
                video_socket.close();
                client.close();
                vserver_running = false;

                Log.d("Done", "Done in background");
                return f.getAbsolutePath();

            } catch (IOException e) {
                Log.e(ChatWindow.TAG, e.getMessage());
                return null;
            } finally {
                if (video_socket != null) {
                    try {
                        video_socket.close();
                    } catch (IOException e) {
                        Log.e("file Socket", e.getMessage());
                    }

                }

                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        Log.e("file Socket", e.getMessage());
                    }

                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (!vserver_running) {
                video_task = new ReceiveVideoAsyncTask(context);
                video_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                vserver_running = true;
            }
            if (result != null) {
                Log.d(ChatWindow.TAG, "File Received");
             /*   Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);*/
                //displayVideo("caption",result);
            }

        }

        @Override
        protected void onPreExecute() {

        }

    }


    private static class ReceiveMessageAsyncTask extends AsyncTask<Void, Void, String> {

        final Context context;

        private ReceiveMessageAsyncTask(Context context) {
            this.context = context;

        }

        @Override
        protected String doInBackground(Void... params) {
            rmsg = "";

            Socket client = null;
            DataInputStream inputStream = null;
            try {

                message_socket = new ServerSocket();
                message_socket.setReuseAddress(true);
                message_socket.bind(new InetSocketAddress(MESSAGE_PORT));
                Log.d("Server Socket for ip", "Open");
                client = message_socket.accept();
                Log.d("Server Socket for ip", "Connected");
                inputStream = new DataInputStream(client.getInputStream());
                if (clientIp.equals("")) {
                    clientIp = client.getInetAddress().getHostAddress();
                } else {
                    rmsg = inputStream.readUTF();

                }
                mserver_running = false;
                inputStream.close();
                message_socket.close();
                client.close();
                return clientIp;
            } catch (IOException e) {
                Log.e("ReceiveIpAsync", e.getMessage());
                return null;
            } finally {
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        Log.e("ReceiveIpAsync", e.getMessage());
                    }
                }
                if (message_socket != null) {
                    try {
                        message_socket.close();
                    } catch (IOException e) {
                        Log.e("ReceiveIpAsync", e.getMessage());
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e("ReceiveIpAsync", e.getMessage());
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (!mserver_running) {
                message_task = new ReceiveMessageAsyncTask(context);
                message_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                mserver_running = true;
            }


            if (rmsg != "") {
                //  displayMessage(rmsg,"sender","timestamp");
            } else {
                Log.d("IP RECEIVED=", clientIp);
            }

        }

        @Override
        protected void onPreExecute() {

        }

    }

    private void ping() {
        Intent serviceIntent = new Intent(getApplicationContext(), SendMessageService.class);
        serviceIntent.setAction(SendMessageService.ACTION_SEND_FILE);
        serviceIntent.putExtra(SendMessageService.EXTRAS_ADDRESS, clientIp);
        // showToast("sending message to="+clientIp,false);
        serviceIntent.putExtra(SendMessageService.EXTRAS_PORT, MESSAGE_PORT);
        serviceIntent.putExtra(SendMessageService.MESSAGE, "ping");
        getApplicationContext().startService(serviceIntent);
    }


    @Override
    protected void onResume() {
        // Toast.makeText(this,"onResume is called",Toast.LENGTH_LONG).show();
        mserver_running = false;
        server_running = false;
        vserver_running = false;

        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
        Log.d("VERIFY", "resume");
        eventBus.post(new ChangeLastSeenAvail(true));

           //mStatusChecker.run();
        super.onResume();
    }


    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        Log.d("VERIFY", "pause");
        //  mHandler.removeCallbacks(mStatusChecker);
        super.onPause();

    }


    @Override
    protected void onStop() {
        super.onStop();

        isActive = false;

        if (image_socket != null) {
            try {
                image_socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (message_socket != null) {
            try {
                message_socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (video_socket != null) {
            try {
                video_socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (message_task != null) {
            if (!message_task.isCancelled()) {
                message_task.cancel(true);
            }
        }
        if (image_task != null) {
            if (!image_task.isCancelled()) {
                image_task.cancel(true);
            }
        }
        if (video_task != null) {
            if (!video_task.isCancelled()) {
                video_task.cancel(true);
            }
        }
    }


    public void showToast(String message, Boolean length_long) {
        if (length_long) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

        }
    }


    public void changeDeviceName() {

        // SharedPreferences preferences= getSharedPreferences("USER", MODE_PRIVATE);
        // String uname =  preferences.getString("username","");
        String uname = "TEST_NAME";
        try {

            mchannel = manager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
                @Override
                public void onChannelDisconnected() {
                    manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
                }
            });
            Class[] paramTypes = new Class[3];
            paramTypes[0] = WifiP2pManager.Channel.class;
            paramTypes[1] = String.class;
            paramTypes[2] = WifiP2pManager.ActionListener.class;

            Method setDeviceName = manager.getClass().getMethod(
                    "setDeviceName", paramTypes);
            setDeviceName.setAccessible(true);

            Object arglist[] = new Object[3];
            arglist[0] = mchannel;
            arglist[1] = uname;
            arglist[2] = new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d("setDeviceName succeeded", "true");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("setDeviceName failed", "true");
                }
            };
            setDeviceName.invoke(manager, arglist);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }


    public void appendMessage(final Message m) {

        messageList.add(m);
        updatecondensedMessageList();
        setMessageAdapter.notifyItemInserted(messageList.size() - 1);

        recyclerView.smoothScrollToPosition(messageList.size() - 1);

    }


    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(ChatWindow.TAG, e.toString());
            return false;
        }
        return true;
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }
        return extension;
    }

    private void connecting(int i) {
        Timer t = new Timer();
        if (i == 1) {
            celme_connect_layout.setVisibility(View.VISIBLE);

            t.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    String dots = connect_dots.getText().toString();
                    switch (dots.length()) {
                        case 0:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    connect_dots.setText(". ");
                                }
                            });
                            break;
                        case 2:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    connect_dots.setText(". . ");
                                }
                            });
                            break;
                        case 4:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    connect_dots.setText(". . .");
                                }
                            });
                            break;
                        case 5:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    connect_dots.setText("");
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }

            }, 0, 700);

        }
        if (i == 2) {
            celme_connect_layout.setVisibility(View.GONE);
            connect_dots.setText("");
            t.cancel();
        }
    }



    @Override
    public void onBackPressed() {
        Log.d("KEy","ONBACKPRESSED");
        CelgramMain.current_chat = "";
        db_helper.updateMessageStatus(isContact ? cmw.getId() : group.getId(), 2);
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rootView);

            if(isPopUpShown) {
                isPopUpShown=false;
                popupWindow.dismiss();
            }

        RelativeLayout emotwrapper = (RelativeLayout) findViewById(R.id.emolayout);
        ViewGroup.LayoutParams params = emotwrapper.getLayoutParams();
        if (params.height > 0) {
            params.height = 0;
            emotwrapper.setLayoutParams(params);
        } else {
            super.onBackPressed();
        }
        startActivity(new Intent(ChatWindow.this, CelgramMain.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        CelgramMain.current_chat = "";
        Log.i("ChatWindow", "EventBUSunregister");
        mHandler.removeCallbacks(mStatusChecker);
        super.onDestroy();
    }

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;
            mTyping = false;
            JSONObject typingobject = new JSONObject();
            try {

                typingobject.put("sender", sender);
                Log.i("TYPING_SENDER_STOP", "" + sender);
                typingobject.put("group", !isContact);   //Decides if msg need to be sent to a group or single person
                typingobject.put("receiver", (isContact ? cmw.getId() : group.getId()));
                typingobject.put("uniquegroupid", (!isContact ? group.getId() : ""));

                eventBus.post(new SendTypingEvent(false, typingobject));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Subscribe
    public void onMessageReceived(final Message event) {

        Log.i("UNIQUE_ID", "" + event.getConvo_partner());
        if (!event.isSelf() && event.getConvo_partner().equals(isContact ? cmw.getId() : group.getId())) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    appendMessage(event);
                }
            });
        }


    }

    @Subscribe
    public void onTypingNotification(String id) {
        Log.i("yoloChatWindow", "got your typing :" + id);
        if (isContact) {
            if (cmw.getId().equals(id)) {
                if (!uTyping) {
                    uTyping = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            online_status.setVisibility(View.VISIBLE);
                            online_status.setText("typing...");
                        }
                    });

                } else {
                    uTyping = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isOnline) {
                                online_status.setVisibility(View.VISIBLE);
                                online_status.setText("online");
                            } else {
                                online_status.setText("");
                                online_status.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        }
    }

    @Subscribe
    public void getAvailable(final ChangeAvailability event) {

        if (event.getAvailable()) {
            isOnline = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    online_status.setVisibility(View.VISIBLE);
                    online_status.setText("online");
                }
            });
        } else {
            isOnline = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    online_status.setText("last seen:" +
                            " "+event.getLastseen());
                    online_status.setVisibility(View.VISIBLE);
                }
            });

        }
    }
    @Subscribe
    public void getAvailable(Boolean result) {

        if (result) {
            isOnline = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    online_status.setVisibility(View.VISIBLE);
                    online_status.setText("online");
                }
            });
        } else {
            isOnline = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    online_status.setText("");
                    online_status.setVisibility(View.GONE);
                }
            });

        }
    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                Log.i("StatusChecker", "Running");
                if (isContact) {
                    AvailableEvent availableEvent = new AvailableEvent(cmw.getId());
                    eventBus.post(availableEvent);
                }


            } finally {
                mHandler.postDelayed(mStatusChecker, 5000);
            }
        }
    };


    public void smiley(View v) {
        // emojiPopup.toggle(); // Toggles visibility of the Popup
        //emojiPopup.dismiss(); // Dismisses the Popup
        //emojiPopup.isShowing(); // Returns true when Popup is showing
        //
    }

  /*  public void setputEmoticons() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new recentFragment(), "");
        adapter.addFragment(new emoFragrment(), "");
        adapter.addFragment(new emo1Fragment(), "");
        adapter.addFragment(new emo2Fragment(), "");
        adapter.addFragment(new emo3Fragment(), "");
        adapter.addFragment(new emo4Fragment(), "");
        adapter.addFragment(new emo5Fragment(), "");
        adapter.addFragment(new emo6Fragment(), "");
        adapter.addFragment(new emo7Fragment(), "");
        viewPager.setAdapter(adapter);
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        Drawable d1 = getResources().getDrawable(R.drawable.tab_icon1);
        Drawable d2 = getResources().getDrawable(R.drawable.tab_icon2);
        Drawable d3 = getResources().getDrawable(R.drawable.tab_icon3);
        Drawable d4 = getResources().getDrawable(R.drawable.tab_icon4);
        Drawable d5 = getResources().getDrawable(R.drawable.tab_icon5);
        Drawable d6 = getResources().getDrawable(R.drawable.tab_icon6);
        Drawable d7 = getResources().getDrawable(R.drawable.tab_icon2);
        Drawable d8 = getResources().getDrawable(R.drawable.tab_icon3);
        Drawable d9 = getResources().getDrawable(R.drawable.tab_icon4);


        tabLayout.getTabAt(0).setIcon(d1);
        tabLayout.getTabAt(1).setIcon(d2);
        tabLayout.getTabAt(2).setIcon(d3);
        tabLayout.getTabAt(3).setIcon(d4);
        tabLayout.getTabAt(4).setIcon(d5);
        tabLayout.getTabAt(5).setIcon(d6);
        tabLayout.getTabAt(6).setIcon(d7);
        tabLayout.getTabAt(7).setIcon(d8);
        tabLayout.getTabAt(8).setIcon(d9);


    }*/


    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Subscribe
    public void onAwsEvent(AwsEvent awsEvent) {
        Log.i("AWS_EVENT_CALLED", "" + awsEvent.getId() + "," + awsEvent.getAws_id() + "," + awsEvent.getAws_id());
        final ArrayList<TransferState> paused = new ArrayList<>();

        paused.add(TransferState.CANCELED);
        paused.add(TransferState.FAILED);
        paused.add(TransferState.PAUSED);

        String id = awsEvent.getId();
        final int aws_id = awsEvent.getAws_id();

        final int pos = setMessageAdapter.getPosition(id);
        if (pos != -1) {

            final View root_view = recyclerView.getLayoutManager().findViewByPosition(pos);
            final CircularProgressButton cpb = (CircularProgressButton) root_view.findViewById(R.id.btnWithText);
            final ImageView cancel = (ImageView) root_view.findViewById(R.id.cancel);
            final RelativeLayout head_layout = (RelativeLayout) root_view.findViewById(R.id.head_layout);
            final ImageView doc_img = (ImageView) root_view.findViewById(R.id.doc_img);
            if (awsEvent.isError()) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (root_view != null) {
                            cancel.setVisibility(View.GONE);
                            cancel.setClickable(false);
                            cpb.setVisibility(View.VISIBLE);
                            cpb.setAlpha(0.5f);
                            cpb.setProgress(0);
                            cpb.setClickable(true);
                            if (!((Message) messageList.get(pos)).isSelf()) {
                                cpb.setText("\u25BD  " + Utils.getMediaSize(((Message) messageList.get(pos)).getMedia_size()));
                            }
                            if (((Message) messageList.get(pos)).getMsg_type() == 3) {
                                head_layout.setVisibility(View.GONE);
                            } else if (((Message) messageList.get(pos)).getMsg_type() == 4) {
                                doc_img.setVisibility(View.GONE);
                            }

                        }
                    }
                });

            } else {
                if (awsEvent.getStatus() == -2) {
                    ((Message) messageList.get(pos)).setAws_id(aws_id);

                } else {
                    ((Message) messageList.get(pos)).setAws_id(aws_id);
                    ((Message) messageList.get(pos)).setMsg_status(-1);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (root_view != null) {
                                cpb.setClickable(false);
                                cpb.setAlpha(0.7f);
                                cancel.setVisibility(View.VISIBLE);
                                cancel.setClickable(true);
                                if (((Message) messageList.get(pos)).getMsg_type() == 3) {
                                    head_layout.setVisibility(View.GONE);
                                } else if (((Message) messageList.get(pos)).getMsg_type() == 4) {
                                    doc_img.setVisibility(View.GONE);
                                }

                                // cancel.setTag(pos);
                                // cancel.setOnClickListener(setMessageAdapter.cancelTransfer);
//                            final TransferObserver observer = AwsUtils.getObserver(aws_id, getApplicationContext());
//                            observer.setTransferListener(new TransferListener() {
//                                @Override
//                                public void onStateChanged(int id, TransferState transferState) {
//                                    if(transferState.equals(TransferState.PENDING_PAUSE)){
//                                        cpb.setIndeterminateProgressMode(true);
//                                        cpb.setProgress(50);
//                                        cpb.setClickable(false);
//                                    }
//                                    else if(transferState.equals(TransferState.PAUSED)) {
//                                        cpb.setIndeterminateProgressMode(false);
//                                        cpb.setProgress(0);
//                                        cpb.setAlpha(0.5f);
//                                        cpb.setClickable(true);
//                                        if (!(((Message) messageList.get(pos)).isSelf())) {
//                                            cpb.setText("\u25BD  " + Utils.getMediaSize(((Message) messageList.get(pos)).getMedia_size()));
//                                        }
//                                    }
//
//                                }
//
//                                @Override
//                                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                                    Log.i("observer_of_chatwindow","true");
//                                    if (bytesTotal > 0) {
//                                        if (!paused.contains(observer.getState())) {
//                                            int percentage = (int) (bytesCurrent * 100 / bytesTotal);
//                                            if (percentage > 0) {
//                                                cpb.setIndeterminateProgressMode(false);
//                                                cpb.setProgress(percentage);
//                                            }
//
//                                            if (percentage == 100) {
//                                                cpb.setVisibility(View.GONE);
//                                                cancel.setVisibility(View.GONE);
//                                            }
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onError(int id, Exception e) {
//
//                                }
//                            });
                            } else {
                                setMessageAdapter.notifyItemChanged(pos);
                                updatecondensedMessageList();
                            }
                        }
                    });

                }
            }
        }
    }

    @Subscribe
    public void onGroupTyping(final GroupTypingEvent event) {
        if (!isContact) {
            if (group.getId().equals(event.getGroup_id())) {
                if (event.isTyping()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
                            String mobile = preferences.getString("mobile", "");
                            Log.d("Anuraj_typing", event.getSender()+" "+mobile);
                            if (!event.getSender().equals(mobile)) {
                                online_status.setVisibility(View.VISIBLE);
                                online_status.setText((CelgramMain.number_name.get(event.getSender()) != null
                                        ? CelgramMain.number_name.get(event.getSender()) : event.getSender()) + " typing");
                            }
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            online_status.setText("");
                            online_status.setVisibility(View.GONE);
                        }
                    });

                }
            }
        }
    }

    View.OnClickListener showPersonInfo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "CONTACT INFO" + cmw.getDisplay_name(), Toast.LENGTH_SHORT).show();
            String contact_name = cmw.getDisplay_name();
            String contact_id = cmw.getId();
            String contact_uname = cmw.getUname();
            String email = cmw.getChikoop_name();
            Toast.makeText(getApplicationContext(), contact_name, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), ContactDetails.class);
            i.putExtra("CONTACT_NAME", contact_name);
            i.putExtra("ID", contact_id);
            i.putExtra("UNAME", contact_uname);
            i.putExtra("MAIL", email);
            startActivity(i);


        }
    };
    View.OnClickListener showContactInfo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "GROUP INFO", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), GroupInfo.class);

            i.putExtra("group", group);
            startActivity(i);

        }
    };

    public static byte[] serializeObject(Object o) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.close();

            // Get the bytes of the serialized object
            byte[] buf = bos.toByteArray();

            return buf;
        } catch (IOException ioe) {
            Log.e("serializeObject", "error", ioe);

            return null;
        }
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        audiorecordPath = getFilename();
        recorder.setOutputFile(audiorecordPath);
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
        }
    };

    private void stopRecording() {
        if (null != recorder) {
            recorder.stop();
            recorder.reset();
            recorder.release();

            recorder = null;
        }
    }

    public static Object deserializeObject(byte[] b) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
            Object object = in.readObject();
            in.close();

            return object;
        } catch (ClassNotFoundException cnfe) {
            Log.e("deserializeObject", "class not found error", cnfe);

            return null;
        } catch (IOException ioe) {
            Log.e("deserializeObject", "io error", ioe);

            return null;
        }
    }

    public void sendaudioMessage(String filepath) {
        final String path = filepath;
        Log.i("record audio", path);
        File media = new File(path);
        Message message;

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mmr.release();

        Integer dur = Integer.parseInt(duration);
        duration = String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) dur),
                TimeUnit.MILLISECONDS.toSeconds((long) dur) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) dur)));


        if (isContact) {
            message = new Message(cmw.getId(), true, 3, ".3gp", (int) media.length(), "", path, duration);
        } else {
            message = new Message(group.getId(), sender, true, 3, ".3gp", (int) media.length(), "", path, duration);
        }

        message.setMsg_status(-1);
        appendMessage(message);
        db_helper.addMessage(message);
        db_helper.addToChatList(message.getConvo_partner(), message.getId());
        startUpload(message);
    }

    @Subscribe
    public void onUnsubscribeResponse(UnsubscribeResponse event) {
        if (event.isUnsubscibed()) {
            group=db_helper_contact.getGroup(group.getId());
            if(group.getCreation_status()==2)
            {
                exitGroupLayout.setVisibility(View.VISIBLE);
                sendAudio.setVisibility(View.GONE);
                sendmessage.setVisibility(View.GONE);
                bigmicrophone.setVisibility(View.GONE);

            }
            else
            {
                exitGroupLayout.setVisibility(View.GONE);
                sendAudio.setVisibility(View.VISIBLE);
                sendmessage.setVisibility(View.GONE);
                bigmicrophone.setVisibility(View.GONE);
            }

        }
    }

    @Subscribe
    public void GroupSubChange(final GroupNameChangeResponse response)
    {
        if(response.isSucces()&&response.getGroupId().equals(group.getId()))
        {
            group.setDisplay_name(response.getGroupname());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    heading_txt.setText(response.getGroupname());
                }
            });
        }
    }

    @Subscribe
    public void AddMemberNotify(AddSingleMemberResponse event) {
        if (!isContact) {
            group.addMember(event.getContact_id());
        }
    }

    @Subscribe
    public void sendSticker(StickerEvent stickerEvent) {
        Message sticker_msg;
        if (isContact) {
            sticker_msg = new Message(cmw.getId(), true, 6, "", 0, stickerEvent.getSticker_name(), "", "");
        } else {
            sticker_msg = new Message(group.getId(), sender, true, 6, "", 0, stickerEvent.getSticker_name(), "", "");
        }
        db_helper.addMessage(sticker_msg);
        db_helper.addToChatList(sticker_msg.getConvo_partner(), sticker_msg.getId());

        appendMessage(sticker_msg);
        eventBus.post(sticker_msg);

        MessageEvent messageEvent = new MessageEvent(sticker_msg);
        eventBus.post(messageEvent);

    }

    @Subscribe
    public void onMessageStatus(final MessageStatusEvent event) {
        if (event.getOld_id() == null) {
            final int position = setMessageAdapter.getPosition(event.getNew_id());

            if (position != -1) {
                View root_view = recyclerView.getLayoutManager().findViewByPosition(position);

                final ImageView msg_status = (ImageView) root_view.findViewById(R.id.msg_status);
                ((Message) messageList.get(position)).setMsg_status(event.getStatus());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (event.getStatus()) {
                            case -2:
                                msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                                break;
                            case -1:
                                msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                                break;
                            case 0:
                                msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                                break;
                            case 1:
                                msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));
                                break;
                            case 2:
                                msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delivered));
                                break;
                            case 3:
                                msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_read));
                                break;
                            default:
                                msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                                break;
                        }

                    }
                });

            }
        } else {
            final int position = setMessageAdapter.getPosition(event.getOld_id());
            final int position2 = setCondensedMessageAdapter.getPosition(event.getOld_id());
            if (position != -1) {
                Log.i("position_", "" + position);
                ((Message) messageList.get(position)).setId(event.getNew_id());
                View root_view = recyclerView.getLayoutManager().findViewByPosition(position);
                final ImageView msg_status = (ImageView) root_view.findViewById(R.id.msg_status);
                ((Message) messageList.get(position)).setMsg_status(1);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));

                    }
                });
            } else {
                Log.i("position not positive", "true");
            }
        }
    }

    @Subscribe
    public void onMessageStatusCondensed(final MessageStatusEvent event) {
        if(isCondensed) {
            if (event.getOld_id() == null) {
                final int position = setCondensedMessageAdapter.getPosition(event.getNew_id());

                if (position != -1) {
                    View root_view = condensedrecylerview.getLayoutManager().findViewByPosition(position);

                    final ImageView msg_status = (ImageView) root_view.findViewById(R.id.msg_status);
                    ((Message) messageCondensedList.get(position)).setMsg_status(event.getStatus());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (event.getStatus()) {
                                case -2:
                                    msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                                    break;
                                case -1:
                                    msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                                    break;
                                case 0:
                                    msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                                    break;
                                case 1:
                                    msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));
                                    break;
                                case 2:
                                    msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delivered));
                                    break;
                                case 3:
                                    msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_read));
                                    break;
                                default:
                                    msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                                    break;
                            }

                        }
                    });

                }
            } else {
                final int position = setCondensedMessageAdapter.getPosition(event.getOld_id());
                if (position != -1) {
                    Log.i("position_", "" + position);
                    ((Message) messageCondensedList.get(position)).setId(event.getNew_id());
                    View root_view = condensedrecylerview.getLayoutManager().findViewByPosition(position);
                    final ImageView msg_status = (ImageView) root_view.findViewById(R.id.msg_status);
                    ((Message) messageCondensedList.get(position)).setMsg_status(1);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msg_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));

                        }
                    });
                } else {
                    Log.i("position not positive", "true");
                }
            }
        }
    }

    @Subscribe
    public void onDownloadEvent(DownloadEvent event) {
        final Message message = event.getMessage();
        final int pos = setMessageAdapter.getPosition(message.getId());

        if (pos != -1) {
            final View root_view = recyclerView.getLayoutManager().findViewByPosition(pos);
            final CircularProgressButton cpb = (CircularProgressButton) root_view.findViewById(R.id.btnWithText);
            final ImageView cancel = (ImageView) root_view.findViewById(R.id.cancel);
            final ImageView sticker = (ImageView) root_view.findViewById(R.id.sticker);
            final RelativeLayout pp_relative_layout = (RelativeLayout) root_view.findViewById(R.id.pp_relative_layout);
            final TextView doc_name = (TextView) root_view.findViewById(R.id.doc_name);
            final ImageView play_video = (ImageView) root_view.findViewById(R.id.play_video);

            if (event.isError()) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (root_view != null) {
                            cancel.setVisibility(View.GONE);
                            cpb.setVisibility(View.VISIBLE);
                            cpb.setIndeterminateProgressMode(false);
                            cpb.setAlpha(0.5f);
                            cpb.setProgress(0);
                            cpb.setClickable(true);
                            if (((Message) messageList.get(pos)).getMsg_type() == 3) {
                                pp_relative_layout.setVisibility(View.GONE);
                            } else if (((Message) messageList.get(pos)).getMsg_type() == 4) {
                                doc_name.setVisibility(View.GONE);
                            }
                        }
                    }
                });


            } else {

                if (message.getMsg_status() == -2) {
                    //downloaded
                    ((Message) messageList.get(pos)).setMsg_status(1);
                    ((Message) messageList.get(pos)).setFile_path(message.getFile_path());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (root_view != null) {
                                cpb.setVisibility(View.GONE);
                                cancel.setVisibility(View.GONE);
                                if (((Message) messageList.get(pos)).getMsg_type() == 1) {
                                    Picasso.with(getApplicationContext()).load(new File(message.getFile_path())).fit().centerCrop().into(sticker);
                                } else if (((Message) messageList.get(pos)).getMsg_type() == 2) {
                                    play_video.setVisibility(View.VISIBLE);
                                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(message.getFile_path(), MediaStore.Video.Thumbnails.MICRO_KIND);
                                    sticker.setImageBitmap(thumb);
                                } else if (((Message) messageList.get(pos)).getMsg_type() == 3) {
                                    pp_relative_layout.setVisibility(View.VISIBLE);
                                } else if (((Message) messageList.get(pos)).getMsg_type() == 4) {
                                    doc_name.setVisibility(View.GONE);
                                }
                            } else {
                                setMessageAdapter.notifyItemChanged(pos);
                                updatecondensedMessageList();


                            }
                        }
                    });

                } else {

                    ((Message) messageList.get(pos)).setMsg_status(-2);
                    ((Message) messageList.get(pos)).setAws_id(message.getAws_id());
                    ((Message) messageList.get(pos)).setFile_path(message.getFile_path());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (root_view != null) {
                                cancel.setVisibility(View.VISIBLE);
                                cancel.setClickable(true);
                                cpb.setVisibility(View.VISIBLE);
                                cpb.setAlpha(0.7f);
                                cpb.setIndeterminateProgressMode(true);
                                cpb.setProgress(50);
                                if (((Message) messageList.get(pos)).getMsg_type() == 3) {
                                    pp_relative_layout.setVisibility(View.GONE);
                                } else if (((Message) messageList.get(pos)).getMsg_type() == 4) {
                                    doc_name.setVisibility(View.GONE);
                                }
                            } else {
                                setMessageAdapter.notifyItemChanged(pos);
                                updatecondensedMessageList();

                            }
                        }
                    });

                }
            }
        }
    }

    private void startUpload(Message message) {
        Log.i("check1-filepath", "" + message.getFile_path());
        Intent uploadService = new Intent(getApplicationContext(), AwsUploadService.class);
        uploadService.putExtra("message", message);
        getApplicationContext().startService(uploadService);

    }

    public static Message getMessage(int id) {
        return (Message) messageList.get(id);
    }

    @Subscribe
    public void onProgressChanged(final ProgressEvent event) {
        int pos = setMessageAdapter.getSetPosition(event.getMsg_id(), event.getProgress());
        if (pos != -1) {
            db_helper.setProgress(event.getMsg_id(), event.getProgress());
            final Message message = ((Message) messageList.get(pos));
            View view = recyclerView.getLayoutManager().findViewByPosition(pos);
            final CircularProgressButton cpb = (CircularProgressButton) view.findViewById(R.id.btnWithText);
            final ImageView cancel = (ImageView) view.findViewById(R.id.cancel);
            final RelativeLayout head_layout = (RelativeLayout) view.findViewById(R.id.head_layout);
            final RelativeLayout pp_relative_layout = (RelativeLayout) view.findViewById(R.id.pp_relative_layout);
            final ImageView doc_img = (ImageView) view.findViewById(R.id.doc_img);
            final TextView doc_name = (TextView) view.findViewById(R.id.doc_name);
            final ImageView play_video = (ImageView) view.findViewById(R.id.play_video);

            if (event.getProgress() != 100) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cpb.setIndeterminateProgressMode(false);
                        cpb.setProgress(event.getProgress());
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cpb.setVisibility(View.GONE);
                        cancel.setVisibility(View.GONE);
                        if (message.getMsg_type() == 2) {
                            play_video.setVisibility(View.VISIBLE);
                        } else if (message.getMsg_type() == 3) {
                            if (message.isSelf()) {
                                head_layout.setVisibility(View.VISIBLE);
                            } else {
                                pp_relative_layout.setVisibility(View.VISIBLE);
                            }
                        } else if (message.getMsg_type() == 4) {
                            if (message.isSelf()) {
                                doc_img.setVisibility(View.VISIBLE);
                            } else {
                                doc_name.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }
    }

    //Anuraj
    String ReplyData;
    String ReplyTo;
    int ReplyToMsgType;
    String ReplyToMsgId;

    public void replyTo(Message message) {
        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        String mobile = preferences.getString("mobile", "");

        List<ContactsModel> cList = new ArrayList<>();
        cList = db_helper_contact.getAllContacts(1);

        //according to message type do things...
        int msg_type = message.getMsg_type();

        if (msg_type == 1) {
            File imgFile = null;
            if (!message.isSelf()) {
                imgFile = new File(message.getFile_path());
                Log.i("SetMessageAdapter", imgFile.toString());
                if (!imgFile.exists()) {
                    imgFile = new File(message.getThumb_path());
                    Log.i("SetMessagegetThumb", imgFile.toString());
                }
            } else {
                if (message.getFile_path().equals("")) {
                    if (message.getThumb_path() != null) {
                        imgFile = new File(message.getThumb_path());
                    }
                } else {
                    imgFile = new File(message.getFile_path());
                    if (!imgFile.exists()) {
                        imgFile = new File(message.getThumb_path());
                    }
                }
            }
            if (imgFile != null) {
                Picasso.with(this).load(imgFile).error(R.drawable.images).transform(new RoundedCornersTransformation(10, 0)).fit().centerCrop().into(replyToImage);
            }

            if (message.getData().isEmpty()) {
                ReplyData = "\uD83D\uDCF7" + " Image";
            } else {
                ReplyData = "\uD83D\uDCF7" + " " + message.getData();
            }

        } else if (msg_type == 2) {

            Bitmap thumb;
            if (message.isSelf()) {
                thumb = ThumbnailUtils.createVideoThumbnail(message.getFile_path(), MediaStore.Video.Thumbnails.MICRO_KIND);
            } else {
                if (message.getFile_path().equals("")) {
                    thumb = BitmapFactory.decodeFile(message.getThumb_path());
                } else {
                    thumb = ThumbnailUtils.createVideoThumbnail(message.getFile_path(), MediaStore.Video.Thumbnails.MICRO_KIND);
                }
            }
            replyToImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap(thumb, 10));

            if (message.getDuration().isEmpty()) {
                ReplyData = "\uD83C\uDFA5" + " Video";
            } else {
                ReplyData = "\uD83D\uDCF7" + " Video " + message.getDuration();
            }
        } else if (msg_type == 13) {
            replyToImage.setImageResource(R.drawable.location);
            ReplyData = "\uD83D\uDCCC" + " Location";
        } else if (msg_type == 5) {
            replyToImage.setImageResource(R.drawable.contact_as_att);
            if (message.getData().isEmpty()) {
                ReplyData = Html.fromHtml("&#9742;") + " Contact";
            } else {
                String ms = message.getData();
                final String x[] = ms.split("/");
                ReplyData = Html.fromHtml("&#9742;") + " Contact: " + x[0];
            }
        } else if (msg_type == 3) {
            replyToImage.setImageResource(R.drawable.audio);
            if (message.getDuration().isEmpty()) {
                ReplyData = "\uD83D\uDD08" + " Audio message";
            } else {
                ReplyData = "\uD83D\uDD08" + " Audio message " + message.getDuration();
            }
        } else if (msg_type == 4) {
            replyToImage.setImageResource(R.drawable.doc);
            if (message.getData().isEmpty()) {
                ReplyData = Html.fromHtml("&#128195;") + " Document";
            } else {
                ReplyData = Html.fromHtml("&#128195;") + " " + message.getData();
            }
        } else if (msg_type == 6) {
            String sticker_name = message.getData();
            if (sticker_name != null) {
                Picasso.with(activity)
                        .load(activity.getResources().getIdentifier(sticker_name, "drawable", activity.getPackageName()))
                        .fit()
                        .centerCrop()
                        .into(replyToImage);
            }
            //ReplyData = "\uD83D\uDCF7"+" STICKER";
            ReplyData = sticker_name;
        } else {
            replyToImage.setVisibility(View.GONE);
            ReplyData = message.getData();
        }

        /*if (model != null) {
            if (message.getConvo_partner().equals(id)) {
                replyTo.setText("You");
            } else {
                replyTo.setText(model.getDisplay_name());
            }
        } else {
            replyTo.setText(message.getConvo_partner());
        }*/


        if (message.isSelf()) {
            ReplyTo = mobile;
            replyTo.setText("You");
        } else {
            if (isContact) {
                ReplyTo = cmw.getId();
                replyTo.setText(cmw.getDisplay_name());
            } else {
                ContactsModel model = getActiveContact(cList, message.getSender_id());
                if (model != null) {
                    ReplyTo = message.getSender_id();
                    replyTo.setText(model.getDisplay_name());
                } else {
                    ReplyTo = message.getSender_id();
                    replyTo.setText(ReplyTo);
                }
            }
        }

        //ReplyTo = message.getConvo_partner();
        ReplyToMsgType = message.getMsg_type();
        ReplyToMsgId = message.getId();
        if (msg_type == 6) {
            replyToMessage.setText("\uD83D\uDCF7" + " STICKER");
        } else {
            replyToMessage.setText(ReplyData);
        }

        editLayout.setBackgroundResource(R.drawable.rounded_back_edittext);
        replyLay.setVisibility(View.VISIBLE);

        message_body.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(message_body, InputMethodManager.SHOW_IMPLICIT);
        //message_body.setActivated(true);
        replyMessage = true;
    }

    public static ContactsModel getActiveContact(List<ContactsModel> contactList, String convo_partner) {
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

    public static void smoothScroll(final int position) {
        recyclerView.scrollToPosition(position);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.findViewHolderForItemId(setMessageAdapter.getItemId(position)).itemView.setBackgroundColor(Color.parseColor("#64EE7E1A"));
                //recyclerView.findViewHolderForItemId(setMessageAdapter.getItemId(position)).itemView.setBackgroundColor(Color.TRANSPARENT);
                //recyclerView.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
            }
        }, 200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    recyclerView.findViewHolderForItemId(setMessageAdapter.getItemId(position)).itemView.setBackgroundColor(Color.TRANSPARENT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //recyclerView.findViewHolderForItemId(setMessageAdapter.getItemId(position)).itemView.setBackgroundColor(Color.parseColor("#4B6FC69C"));
                //recyclerView.findViewHolderForItemId(setMessageAdapter.getItemId(position)).itemView.setBackgroundColor(Color.TRANSPARENT);
                //recyclerView.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
            }
        }, 1500);
    }

    public void cancelReply(View view) {
        replyMessage = false;
        editLayout.setBackgroundResource(R.drawable.round_background9);
        replyLay.setVisibility(View.GONE);
    }

    private void showAtBottom() {
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    private int getUsableScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final DisplayMetrics metrics = new DisplayMetrics();

            final WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            return metrics.heightPixels;

        } else {
            return rootView.getRootView().getHeight();
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            isKeyBoardHidden=false;
            if(KEY_BACKPRESSED==0) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void initPopup()
    {
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.reveal_layout_popup, null);
        popupWindow=new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null)); // To avoid borders & overdraw
        //popupWindow.setContentView(layout);
        //popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        //popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        //popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setHeight((int)getResources().getDimension(com.vanniktech.emoji.R.dimen.emoji_keyboard_height));
        //setClickListenersOnPopUP(layout);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                Toast.makeText(ChatWindow.this, "Popup dismiss is called", Toast.LENGTH_SHORT).show();
                //isPopUpShown=false;
                showSoftKeyboard(message_body);


            }
        });


      /*  popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("POP","Touch Intercepter is called"+event);

                if(event.getAction()==MotionEvent.ACTION_OUTSIDE)
                {
                    Log.d("POP","Touch ChatEmoticon");
                    isPopUpShown=true;
                    popupWindow.dismiss();
                    return true;
                }
                else {

                    return true;
                }
            }
        });
*/


         ib_audio_pop = (ImageButton) layout.findViewById(R.id.audio_pop);
         ib_camera_pop = (ImageButton) layout.findViewById(R.id.camera_pop);
         ib_contacts_pop = (ImageButton) layout.findViewById(R.id.contacts_pop);
         ib_gallery_pop = (ImageButton) layout.findViewById(R.id.gallery_pop);
         ib_location_pop= (ImageButton) layout.findViewById(R.id.location_pop);
         ib_doc_pop = (ImageButton) layout.findViewById(R.id.document_pop);
         ib_images_pop = (ImageButton) layout.findViewById(R.id.images_pop);
         ib_call_pop= (ImageButton) layout.findViewById(R.id.call_pop);




        ib_audio_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("audio/*");
                startActivityForResult(i, 4);
            }
        });
        ib_camera_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                Log.i("ChatWindow", "" + fileUri);

                List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    activity.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                //intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, 11);


            }
        });
        ib_contacts_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }
        });
        ib_gallery_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("video/*");
                startActivityForResult(i, 1);
            }
        });
        ib_location_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GPSTracker gps = new GPSTracker(ChatWindow.this);

                if (gps.canGetLocation()) {

                    //double latitude = gps.getLatitude();
                    //double longitude = gps.getLongitude();
                    MyParams3 pass = new MyParams3(gps);

                    new getLocationNow().execute(pass);

                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });
        ib_doc_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                String[] mimetypes = {"text/*", "application/pdf"};
                // DEFINE MORE MIMETYPES IN THE ABOVE STRING ARRAY TO PICK PPT/ XLS FILES ETC.
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

                startActivityForResult(intent, 200);
            }
        });

        ib_images_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //images pick.
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
            }
        });

    }

    public void setClickListenersOnPopUP(View view)
    {


    }

    public void updatecondensedMessageList(){

        try {
            if (messageCondensedList != null && !messageCondensedList.isEmpty()) {
                messageCondensedList.clear();
                messageCondensedList.add(messageList.get(messageList.size() - 2));
                messageCondensedList.add(messageList.get(messageList.size() - 1));
            } else {
                messageCondensedList = new ArrayList<>();
                if(messageList.size()>=2) {
                    messageCondensedList.add(messageList.get(messageList.size() - 2));
                    messageCondensedList.add(messageList.get(messageList.size() - 1));
                }else if(messageList.size()==1)
                {
                    messageCondensedList.add(messageList.get(messageList.size() - 1));

                }

            }
            setCondensedMessageAdapter.notifyDataSetChanged();
        }catch (Exception e){
            Log.e("Exception",e.getMessage());
        }
    }

    public int getScreenHeight() {

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.y;
    }



}