package com.rohan7055.earnchat.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.PermissionChecker;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.AppConstants;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.CelgramUtils;
import com.rohan7055.earnchat.celgram.ChatWindow;
import com.rohan7055.earnchat.celgram.ContactDetails;
import com.rohan7055.earnchat.celgram.ContactsModel;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.rohan7055.earnchat.celgram.DataBase_Helper.MessageSQLiteHelper;
import com.rohan7055.earnchat.celgram.GroupInfo;
import com.rohan7055.earnchat.celgram.GroupsModel;
import com.rohan7055.earnchat.celgram.Message;
import com.rohan7055.earnchat.celgram.RoundedCornersTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.refactor.library.SmoothCheckBox;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.rohan7055.earnchat.celgram.ChatWindow.cmw;

public class ActiveChatAdapter extends RecyclerView.Adapter<ActiveChatAdapter.MyViewHolder> {

    private List<Object> listData;
    private Activity activity;
    private Boolean isContact;
    LinearLayout callsim;
    String value1;
    String perm;
    String id;

    int num;
    ArrayList<String> mute_id;
    public GroupsModel groupsModel;
    //  ChatWindow ch=new ChatWindow();
    ContactSQLiteHelper db_helper_contact;
    MessageSQLiteHelper db_helper;
    private static boolean showcheckbox;
    private  Map<Integer,Boolean> isBoxDeleteCheck;
    private int checkedItemCount=0;
    public InteractionListener mListener;

    public ActiveChatAdapter(List<Object> list, Activity activity,Boolean isContact,Context context) {
        this.listData = list;
        this.activity = activity;
        this.isContact=isContact;
        db_helper_contact = new ContactSQLiteHelper(context);
        db_helper=new MessageSQLiteHelper(context);
        isBoxDeleteCheck=new HashMap<>();
    }



    public void setmListener(InteractionListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_chat_cell, parent, false);

        return new MyViewHolder(itemView);
    }


    public int count()
    {
        return num;
    }
    public void showcheckbox(boolean isShow)
    {
      showcheckbox=isShow;
      notifyDataSetChanged();
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Message message=new Message();
        mute_id=new ArrayList<String>();

        if(showcheckbox)
        {
            if(holder.rl_check.getVisibility()==View.GONE)
            {
                holder.rl_check.setVisibility(View.VISIBLE);
            }
        }else{
            holder.rl_check.setVisibility(View.GONE);
        }

        holder.smoothCheckBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
               isBoxDeleteCheck.put(position,isChecked);
               if(isChecked)
               {
                   checkedItemCount++;
               }else
               {
                   checkedItemCount--;
               }
            }
        });

        SharedPreferences mSharedPreference1 =   PreferenceManager.getDefaultSharedPreferences(activity);
        mute_id.clear();
        int size = mSharedPreference1.getInt("Status_size",0);

        for(int i=0;i<size;i++) {
            mute_id.add(mSharedPreference1.getString("Status_" + i, null));
        }
        Integer count;
//        imagePopup = new ImagePopup(activity);
//        imagePopup.setBackgroundColor(Color.TRANSPARENT);
//        imagePopup.setHideCloseIcon(true);
//        imagePopup.setImageOnClickClose(false);

        holder.contact_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.image_dialog);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.CENTER_HORIZONTAL;
                TextView tv_dialog=(TextView)dialog.findViewById(R.id.tv_dialog);
                ImageView iv_dialog=(ImageView)dialog.findViewById(R.id.iv_dialog);
                ImageView iv_chat=(ImageView)dialog.findViewById(R.id.iv_chat);
                ImageView iv_call=(ImageView)dialog.findViewById(R.id.iv_call);
                ImageView iv_info=(ImageView)dialog.findViewById(R.id.iv_info);
                if(listData.get(position) instanceof ContactsModel) {
                    ContactsModel contactsModel = ((ContactsModel) listData.get(position));


                    if (db_helper_contact.checkLocalprofImageEmpty()) {
                        Log.i("ActiveChatAdap","Table is empty");
                        Picasso.with(activity).load(AppConstants.AWS_PROFILEIMAGE + contactsModel.getImgname()).error(R.drawable.default_user).fit().centerCrop().into(iv_dialog);
                        db_helper_contact.addLocalImage(db_helper_contact.getUserid(contactsModel.getId()), contactsModel.getImgname());
                        saveImage(activity, contactsModel.getImgname(), contactsModel);

                    } else {
                        if (db_helper_contact.checkLocalUseridExist(db_helper_contact.getUserid(contactsModel.getId()))) {
                            String n = "";
                            String x="";
                            String userid = null;
                            try
                            {
                                userid = contactsModel.getId();
                                n = db_helper_contact.getLocalImage(userid);
                                x = contactsModel.getImgname();
                            }catch (Exception e)
                            {
                                Log.i("Userid get",e.getMessage().toString());
                            }


                            if (!n.equals(x)) {
                                /*Log.i("ActiveChatAdap",db_helper_contact.getLocalImage(userid));
                                Log.i("ActiveChatAdap",contactsModel.getImgname());*/
                                Log.i("ActiveChatAdap","image loaded local image not equal to contactimage");
                                Picasso.with(activity).load(AppConstants.BASE_URL + db_helper_contact.getUserid(contactsModel.getId()) + "/" + contactsModel.getImgname()).error(R.drawable.default_user).fit().centerCrop().into(iv_dialog);
                                db_helper_contact.setLocalImage(userid, contactsModel.getImgname());
                                saveImage(activity, contactsModel.getImgname(), contactsModel);
                            } else

                            {
                                Log.i("ActiveChatAdap","Loaded from local filesystem");
                                Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/EarnChat/" + userid + "/" + db_helper_contact.getLocalImage(userid))).error(R.drawable.default_user).fit().centerCrop().into(iv_dialog);

                            }



                        } else {
                            Log.i("ActiveChatAdap","Local userid doesnt exist");
                            Picasso.with(activity).load(AppConstants.AWS_PROFILEIMAGE + contactsModel.getImgname()).error(R.drawable.default_user).fit().centerCrop().into(iv_dialog);
                            db_helper_contact.addLocalImage(db_helper_contact.getUserid(contactsModel.getId()), contactsModel.getImgname());
                            saveImage(activity, contactsModel.getImgname(), contactsModel);
                        }
                    }
                }
                else
                {
                    int pos = position;
                    iv_call.setVisibility(View.GONE);
                    GroupsModel groupsModel = (GroupsModel)listData.get(pos);
                    Picasso.with(activity).load(AppConstants.AWS_IMAGEBUCKET_GROUP+groupsModel.getDp_url()).error(R.drawable.default_user).fit().centerCrop().into(iv_dialog);

                }

                iv_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listData.get(position) instanceof ContactsModel){
                            int pos=position;
                            Intent i=new Intent(activity, ChatWindow.class);
                            i.putExtra("isContact",true);
                            i.putExtra("contact",(ContactsModel)listData.get(position));
                            CelgramMain.current_chat=((ContactsModel)listData.get(position)).getId();

                            activity.startActivityForResult(i,1);
                            dialog.dismiss();
                        }



                        else{
                            int pos = position;
                            // GroupsModel groupsModel = ((GroupsModel) listData.get(position));
                            GroupsModel groupsModel = ((GroupsModel) listData.get(position));
                            Intent i=new Intent(activity, ChatWindow.class);
                            i.putExtra("isContact",false);
                            i.putExtra("group",(GroupsModel)listData.get(position));

                            //i.putExtra("groupmodel",groupsModel);
                            CelgramMain.current_chat=((GroupsModel)listData.get(position)).getId();

                            activity.startActivityForResult(i,2);
                            dialog.dismiss();
                        }

                    }
                });
                iv_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*final Dialog dialog1 = new Dialog(activity);
                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                        dialog1.setContentView(R.layout.call_popup);
                        callsim=(LinearLayout)dialog1.findViewById(R.id.callsim1);

                        dialog1.show();

                        callsim.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                String contact_id= cmw.getId();
                                String number=contact_id;
                                perm="android.permission.CALL_PHONE";
                                int permission = PermissionChecker.checkSelfPermission(activity, perm);

                                if (permission == PermissionChecker.PERMISSION_GRANTED) {

                                    //Use Intent.ACTION_CALL to directly call without giving the user the choice to select a different app
                                    //for calling like TrueCaller etc.
                                    Intent i=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
                                    activity.startActivity(i);
                                } else {
                                    // permission not granted, you decide what to do
                                }

                            }
                        });*/
                        String contact_id= ((ContactsModel)listData.get(position)).getId();
                        String number=contact_id;
                        perm="android.permission.CALL_PHONE";
                        int permission = PermissionChecker.checkSelfPermission(activity, perm);

                        if (permission == PermissionChecker.PERMISSION_GRANTED) {

                            //Use Intent.ACTION_CALL to directly call without giving the user the choice to select a different app
                            //for calling like TrueCaller etc.
                            Intent i=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
                            activity.startActivity(i);
                        } else {
                            // permission not granted, you decide what to do
                            Toast.makeText(activity, "Permission needed to call, please grant the required permission.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                iv_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(listData.get(position) instanceof ContactsModel ) {
                            ContactsModel cmw = (ContactsModel) listData.get(position);
                            String contact_name = cmw.getDisplay_name();
                            String contact_id = cmw.getId();
                            String contact_uname = cmw.getUname();
                            String email = cmw.getChikoop_name();
                            String userid = db_helper_contact.getUserid(cmw.getId());
                            Toast.makeText(activity, contact_name, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(activity, ContactDetails.class);
                            i.putExtra("CONTACT_NAME", contact_name);
                            i.putExtra("ID", contact_id);
                            i.putExtra("UNAME", contact_uname);
                            i.putExtra("MAIL", email);
                            i.putExtra("userid",userid);
                            i.putExtra("image",cmw.getImgname());
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(i);
                            dialog.dismiss();
                        }
                        else
                        {
                            GroupsModel groupsModel = (GroupsModel)listData.get(position);
                            Toast.makeText(activity,"GROUP INFO "+groupsModel.getDisplay_name(),Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(activity, GroupInfo.class);
                            intent.putExtra("group",groupsModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(intent);
                            dialog.dismiss();
                        }
                    }
                });
                tv_dialog.setText(holder.chat_name.getText().toString());
//        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                dialog.show();
            }
        });

        if(listData.get(position) instanceof ContactsModel){
            isContact=true;
            Log.i("TEMPTESTCONTACT","true");
            final ContactsModel contactsModel=((ContactsModel)listData.get(position));
            //boolean isMagic=db_helper_contact.checkmagicContact(contactsModel.getId());
            String lastmsg=db_helper.getLastMsgId(contactsModel.getId());
            Message lastest_msg = db_helper.getMessage(lastmsg);
            contactsModel.setLatest_msg(lastest_msg);
            holder.chat_name.setText(contactsModel.getDisplay_name());
            message=contactsModel.getLatest_msg();
            holder.magic_contactimg.setVisibility(View.GONE);
            holder.magic_icon.setVisibility(View.GONE);
            holder.contact_image.setVisibility(View.VISIBLE);


            if(contactsModel.getIsTyping()){

                holder.chat_message.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                holder.chat_message.setText("typing...");
            }
            else{
                holder.chat_message.setTextColor(activity.getResources().getColor(R.color.textColorSecondary));


                holder.chat_message.setText(CelgramUtils.getMessageBrief(message));

            }

            //Rohan userMute
            if(db_helper_contact.checkmuteStatus(contactsModel))
            {
                holder.iv_mute.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.iv_mute.setVisibility(View.GONE);
            }

            count=contactsModel.getUnseen_msg();
            if(count>0){
                holder.message_count.setVisibility(View.VISIBLE);
                //   holder.chat_time.setTextColor(Color.parseColor("#FFFE5D2A"));
                holder.message_count.setText(count.toString());}
            else{
                holder.message_count.setVisibility(View.GONE);
                holder.chat_time.setTextColor(activity.getResources().getColor(R.color.textColorSecondary));
            }
            holder.chat_time.setText(message.getDate());
            // Picasso.with(activity).load("http://128.199.198.1/old/20.png").into(holder.contact_image);
            // holder.contact_image.setImageDrawable(activity.getResources()
            //        .getDrawable(R.shadow.temp_icon));

            //checkifimageis loaded once
            try {
                if(!contactsModel.getImageLoadedOnce())
                {
                    Picasso.with(activity).load(AppConstants.AWS_PROFILEIMAGE+contactsModel.getImgname()).error(R.drawable.default_user).fit().into(holder.contact_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.i("ImageLoaded","OnSucess");
                            contactsModel.setImageLoadedOnce(true);
                        }

                        @Override
                        public void onError() {
                            Log.i("ImageLoaded","OnFailure");
                            contactsModel.setImageLoadedOnce(false);
                        }
                    });
                }
            }catch (NullPointerException e)
            {
                e.printStackTrace();
                Log.e("ImageLoadedOnce",e.getMessage());
            }



            if(db_helper_contact.checkLocalprofImageEmpty())
            {
                Picasso.with(activity).load(AppConstants.AWS_PROFILEIMAGE+contactsModel.getImgname()).error(R.drawable.default_user).fit().into(holder.contact_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap albumart = ((BitmapDrawable) holder.contact_image.getDrawable()).getBitmap();
                        //Bitmap albumart = BitmapFactory.decodeResource(activity.getResources(),R.shadow.temp_icon);
                        if (albumart != null) {
                            Palette.from(albumart).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(final Palette palette) {

                                    try {
                                        // vibrantDarkSwatch = palette.getDarkVibrantSwatch();
                                        //  vibrantDark = vibrantDarkSwatch.getHsl();
                                        // colorTo = palette.getDarkVibrantSwatch().getRgb();
                                        int colorFrom = activity.getResources().getColor(R.color.cardview_light_background);
                                        int colorTo = palette.getDarkVibrantSwatch().getRgb();
                                        Log.d("COLOR",colorTo+"");
//                                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                        @Override
//                                        public void onAnimationUpdate(ValueAnimator animator) {
//
//                                            holder.palleteLayout.setBackgroundColor((int) animator.getAnimatedValue());
//                                        }
//
//                                    });
//                                    colorAnimation.start();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                            });
                        }



                    }

                    @Override
                    public void onError() {
                        holder.contact_image.setImageResource(R.drawable.default_user);
                    }
                });
                db_helper_contact.addLocalImage(db_helper_contact.getUserid(contactsModel.getId()),contactsModel.getImgname());
                saveImage(activity,contactsModel.getImgname(),contactsModel);

            }
            else
            {
                if(db_helper_contact.checkLocalUseridExist(contactsModel.getId()))

                {
                    String n = "";
                    String x = "";
                    String userid = null;
                    try {
                        userid = contactsModel.getId();
                        n = db_helper_contact.getLocalImage(userid);
                        x = contactsModel.getImgname();
                        Log.d("Anuraj_img", n+", "+x);
                    } catch (Exception e)
                    {
                        Log.i("userid get",e.getMessage().toString());
                    }

                    if(!n.equals(x))
                    {
                        Picasso.with(activity).load(AppConstants.AWS_PROFILEIMAGE+contactsModel.getImgname()).error(R.drawable.default_user).fit().into(holder.contact_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap albumart = ((BitmapDrawable) holder.contact_image.getDrawable()).getBitmap();
                                //Bitmap albumart = BitmapFactory.decodeResource(activity.getResources(),R.shadow.temp_icon);
                                if (albumart != null) {
                                    Palette.from(albumart).generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(final Palette palette) {

                                            try {
                                                // vibrantDarkSwatch = palette.getDarkVibrantSwatch();
                                                //  vibrantDark = vibrantDarkSwatch.getHsl();
                                                // colorTo = palette.getDarkVibrantSwatch().getRgb();
                                                int colorFrom = activity.getResources().getColor(R.color.cardview_light_background);
                                                int colorTo = palette.getDarkVibrantSwatch().getRgb();
                                                Log.d("COLOR",colorTo+"");
//                                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                        @Override
//                                        public void onAnimationUpdate(ValueAnimator animator) {
//
//                                            holder.palleteLayout.setBackgroundColor((int) animator.getAnimatedValue());
//                                        }
//
//                                    });
//                                    colorAnimation.start();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    });
                                }



                            }

                            @Override
                            public void onError() {
                                holder.contact_image.setImageResource(R.drawable.default_user);
                            }
                        });
                        db_helper_contact.setLocalImage(userid,contactsModel.getImgname());
                        saveImage(activity,contactsModel.getImgname(),contactsModel);
                    }
                    else
                    {
                        Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/EarnChat/"+userid+"/"+contactsModel.getImgname())).fit().into(holder.contact_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap albumart = ((BitmapDrawable) holder.contact_image.getDrawable()).getBitmap();
                                //Bitmap albumart = BitmapFactory.decodeResource(activity.getResources(),R.shadow.temp_icon);
                                if (albumart != null) {
                                    Palette.from(albumart).generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(final Palette palette) {

                                            try {
                                                // vibrantDarkSwatch = palette.getDarkVibrantSwatch();
                                                //  vibrantDark = vibrantDarkSwatch.getHsl();
                                                // colorTo = palette.getDarkVibrantSwatch().getRgb();
                                                int colorFrom = activity.getResources().getColor(R.color.cardview_light_background);
                                                int colorTo = palette.getDarkVibrantSwatch().getRgb();
                                                Log.d("COLOR",colorTo+"");
//                                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                        @Override
//                                        public void onAnimationUpdate(ValueAnimator animator) {
//
//                                            holder.palleteLayout.setBackgroundColor((int) animator.getAnimatedValue());
//                                        }
//
//                                    });
//                                    colorAnimation.start();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    });
                                }



                            }

                            @Override
                            public void onError() {
                                //holder.contact_image.setImageResource(R.drawable.default_user);
                                saveImage(activity,contactsModel.getImgname(),contactsModel);
                            }
                        });
                    }

                }
                else {
                    Picasso.with(activity).load(AppConstants.AWS_PROFILEIMAGE+contactsModel.getImgname()).error(R.drawable.default_user).fit().into(holder.contact_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap albumart = ((BitmapDrawable) holder.contact_image.getDrawable()).getBitmap();
                            //Bitmap albumart = BitmapFactory.decodeResource(activity.getResources(),R.shadow.temp_icon);
                            if (albumart != null) {
                                Palette.from(albumart).generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(final Palette palette) {

                                        try {
                                            // vibrantDarkSwatch = palette.getDarkVibrantSwatch();
                                            //  vibrantDark = vibrantDarkSwatch.getHsl();
                                            // colorTo = palette.getDarkVibrantSwatch().getRgb();
                                            int colorFrom = activity.getResources().getColor(R.color.cardview_light_background);
                                            int colorTo = palette.getDarkVibrantSwatch().getRgb();
                                            Log.d("COLOR",colorTo+"");
//                                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                        @Override
//                                        public void onAnimationUpdate(ValueAnimator animator) {
//
//                                            holder.palleteLayout.setBackgroundColor((int) animator.getAnimatedValue());
//                                        }
//
//                                    });
//                                    colorAnimation.start();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }


                                });
                            }
                        }
                        @Override
                        public void onError() {
                            holder.contact_image.setImageResource(R.drawable.default_user);
                        }
                    });
                    db_helper_contact.addLocalImage(contactsModel.getId(),contactsModel.getImgname());
                    saveImage(activity,contactsModel.getImgname(),contactsModel);

                }
            }

            holder.relativeLayout.setTag(position);
            //chat click
            holder.relativeLayout.setOnClickListener(chatClick);
            holder.relativeLayout.setOnLongClickListener(longChatClick);
            holder.linearLayout.setTag(position);
        }

        else {
            isContact = false;
            //  Picasso.with(activity).load("http://128.199.198.1/old/20.png").placeholder(activity.getResources()
            //                      .getDrawable(R.shadow.group_icon)).into(holder.contact_image);


            GroupsModel  groupsModel = ((GroupsModel) listData.get(position));
            //c Log.i("DPPATH",groupsModel.getDp_url());
            Picasso.with(activity).load(AppConstants.AWS_IMAGEBUCKET_GROUP+groupsModel.getDp_url()).error(R.drawable.ic_default_group).fit().centerCrop().into(holder.contact_image, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap albumart = ((BitmapDrawable) holder.contact_image.getDrawable()).getBitmap();
                    //Bitmap albumart = BitmapFactory.decodeResource(activity.getResources(),R.shadow.temp_icon);
                    if (albumart != null) {
                        Palette.from(albumart).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(final Palette palette) {

                                try {
                                    // vibrantDarkSwatch = palette.getDarkVibrantSwatch();
                                    //  vibrantDark = vibrantDarkSwatch.getHsl();
                                    // colorTo = palette.getDarkVibrantSwatch().getRgb();
                                    int colorFrom = activity.getResources().getColor(R.color.cardview_light_background);
                                    int colorTo = palette.getDarkVibrantSwatch().getRgb();
                                    Log.d("COLOR",colorTo+"");
//                                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                        @Override
//                                        public void onAnimationUpdate(ValueAnimator animator) {
//
//                                            holder.palleteLayout.setBackgroundColor((int) animator.getAnimatedValue());
//                                        }
//
//                                    });
//                                    colorAnimation.start();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        });
                    }
                }

                @Override
                public void onError() {
                    holder.contact_image.setImageResource(R.drawable.ic_default_group);
                }
            });

            holder.chat_name.setText(groupsModel.getDisplay_name());

            if (groupsModel.getCreation_status() == 0) {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.message_count.setVisibility(View.GONE);
            }

         /*   else if(groupsModel.getCreation_status() == 2) {

                //group left
            }*/

            else if(groupsModel.getCreation_status() == 3){
                holder.chat_message.setTypeface(null, Typeface.ITALIC);
                //  holder.chat_message.setTextColor(activity.getResources().getColor(R.color.divider_color));
                holder.chat_message.setText("Check internet and tap to retry");
                holder.relativeLayout.setOnClickListener(retry);
            }

            else{
                holder.progressBar.setVisibility(View.GONE);
                message = groupsModel.getLatest_msg();
                String sender;

               /* if(CelgramMain.number_name.get(message.getSender_id())==null){
                    sender=message.getSender_id();
                }
                else{
                   sender= CelgramMain.number_name.get(message.getSender_id());
                }*/



                if (groupsModel.getIsTyping()) {
                    holder.chat_message.setTextColor(activity.getResources().getColor(R.color.accentColor));

                    if(CelgramMain.number_name.get(groupsModel.getTyping())==null){
                        holder.chat_message.setText(groupsModel.getTyping()+" typing...");

                    }
                    else{
                        String name[]= CelgramMain.number_name.get(groupsModel.getTyping()).split(" ");
                        holder.chat_message.setText(name[0]+" typing...");
                    }


                } else {
                    holder.chat_message.setTextColor(activity.getResources().getColor(R.color.textColorSecondary));

                    holder.chat_message.setText(CelgramUtils.getMessageBrief(message));

                }

                if(db_helper_contact.checkGroupmuteStatus(groupsModel))
                {
                    holder.iv_mute.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.iv_mute.setVisibility(View.GONE);
                }


                count = groupsModel.getUnseen_msg();
                if (count > 0) {
                    holder.message_count.setVisibility(View.VISIBLE);
                    holder.chat_time.setTextColor(Color.parseColor("#FFFE5D2A"));
                    holder.message_count.setText(count.toString());
                } else {
                    holder.message_count.setVisibility(View.GONE);
                    holder.chat_time.setTextColor(activity.getResources().getColor(R.color.textColorSecondary));
                }
                holder.chat_time.setText(message.getDate());
                holder.relativeLayout.setTag(position);
                //for shared element transition
                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(activity, ChatWindow.class);
                        i.putExtra("isContact",false);
                        i.putExtra("group",(GroupsModel)listData.get(position));


                        //i.putExtra("groupmodel",groupsModel);
                        CelgramMain.current_chat=((GroupsModel)listData.get(position)).getId();

                        activity.startActivity(i);

                    }
                });
                holder.relativeLayout.setOnLongClickListener(groupLongClick);
                holder.linearLayout.setTag(position);


            }
        }


    }

    public static void saveImage(Context ctx ,final String imagename,ContactsModel contactsModel)
    {    ContactSQLiteHelper db_contact = new ContactSQLiteHelper(ctx);
        Picasso.with(ctx)
                .load(AppConstants.AWS_PROFILEIMAGE+contactsModel.getImgname())
                .into(getTarget(imagename,contactsModel,db_contact));

    }

    //target to save
    private static com.squareup.picasso.Target getTarget(final String url, final ContactsModel contactsModel, final ContactSQLiteHelper db_helper_contact){
        com.squareup.picasso.Target target = new com.squareup.picasso.Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File filePath = Environment.getExternalStorageDirectory();
                        File dir = new File(filePath.getAbsolutePath()+"/EarnChat/"+contactsModel.getId());

                        if (dir.exists()) {
                            for (File file2 : dir.listFiles()) {
                                if (!file2.isDirectory()) {
                                    file2.delete();
                                }
                            }
                        }else{
                            dir.mkdirs();
                        }


                        File file = new File(dir,url);

                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

    public static void imageDownload(Context ctx, String url){
        Picasso.with(ctx)
                .load("http://blog.concretesolutions.com.br/wp-content/uploads/2015/04/Android1.png")
                .into(getTarget(url));
    }

    //target to save
    private static Target getTarget(final String url){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + url);
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

    @Override
    public int getItemCount() {
        num=listData.size();
        return listData.size();
    }



    View.OnClickListener chatClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Intent i=new Intent(activity, ChatWindow.class);
            i.putExtra("isContact",true);
            i.putExtra("contact",(ContactsModel)listData.get(position));

            CelgramMain.current_chat=((ContactsModel)listData.get(position)).getId();


            activity.startActivity(i);
//            activity.finish();

        }
    };

    public void deletecheckedItem()

    {
        if(!isBoxDeleteCheck.isEmpty()&&checkedItemCount>0) {
            AsyncTask<Void, Void, ArrayList<Integer>> asyncTask = new AsyncTask<Void, Void, ArrayList<Integer>>() {
                @Override
                protected ArrayList<Integer> doInBackground(Void... voids) {
                   ArrayList<Integer> newpos=new ArrayList<>();
                    Iterator<Map.Entry<Integer, Boolean>> iterator = isBoxDeleteCheck.entrySet().iterator();
                    while (iterator.hasNext()) {
                        final Map.Entry<Integer, Boolean> pair = iterator.next();
                        if (pair.getValue()) {
                            ContactsModel contactsModel = (ContactsModel) listData.get(pair.getKey());
                            db_helper.clearChat(contactsModel.getId());
                            db_helper.deleteChatUser(contactsModel.getId());
                            ///listData.remove(pair.getKey());
                            newpos.add(pair.getKey());

                        }
                    }
                    isBoxDeleteCheck.clear();
                    return newpos;
                }

                @Override
                protected void onPostExecute(ArrayList<Integer> integers) {
                    for(int i:integers)
                    {
                        listData.remove(i);
                        notifyItemRemoved(i);

                    }
                    if(listData.size()==0)
                    {
                        mListener.onShowNewChats();
                    }
                }
            };
            asyncTask.execute();
        }else
        {
            Toast.makeText(activity, "Atleast Check One Item", Toast.LENGTH_SHORT).show();
        }

    }


    View.OnLongClickListener longChatClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            final int position =(Integer)v.getTag();
            final ContactsModel contactsModel = (ContactsModel)listData.get(position);
            Toast.makeText(activity,"Long Clicked",Toast.LENGTH_SHORT).show();
            Vibrator vb = (Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(100);
            final View view=(View)v.getParent().getParent();
            LinearLayout ll=(LinearLayout)view.findViewById(R.id.active_chat_layout);
            ll.setBackgroundColor(activity.getResources().getColor(R.color.back));

            AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
            builder1.setCancelable(true);
            builder1.setMessage("Are you sure you want to delete this Chat?");
            builder1.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    db_helper.clearChat(contactsModel.getId());
                    db_helper.deleteChatUser(contactsModel.getId());
                    listData.remove(position);
                    notifyItemRemoved(position);

                }
            });
            builder1.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();

                }
            });
            AlertDialog fMapTypeDialog1 = builder1.create();
            fMapTypeDialog1.setCanceledOnTouchOutside(true);
            fMapTypeDialog1.show();
            return true;
        }
    };




    View.OnClickListener groupClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            int position = (Integer) v.getTag();
            // GroupsModel groupsModel = ((GroupsModel) listData.get(position));
            GroupsModel groupsModel = ((GroupsModel) listData.get(position));
            Intent i=new Intent(activity, ChatWindow.class);
            i.putExtra("isContact",false);
            i.putExtra("group",(GroupsModel)listData.get(position));

            //i.putExtra("groupmodel",groupsModel);
            CelgramMain.current_chat=((GroupsModel)listData.get(position)).getId();

            activity.startActivity(i);

        }
    };

    View.OnClickListener retry = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };


    View.OnLongClickListener groupLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            return true;
        }
    };


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView chat_name,chat_time,message_count;
        private TextView chat_message;
        private RelativeLayout relativeLayout,palleteLayout;
        private LinearLayout linearLayout;
        private CircleImageView contact_image;
        private ProgressBar progressBar;
        private ImageView magic_contactimg;
        private ImageView magic_icon;
        private RelativeLayout rl_check;
        private SmoothCheckBox smoothCheckBox;

        ImageView iv_mute;



        public MyViewHolder(View view) {
            super(view);
            chat_name=(TextView)view.findViewById(R.id.contact_name);
            chat_message=(TextView) view.findViewById(R.id.my_chat_message);
            chat_time=(TextView)view.findViewById(R.id.my_chat_time);
            message_count=(TextView)view.findViewById(R.id.message_count);
            contact_image=(CircleImageView)view.findViewById(R.id.contact_image);
            iv_mute=(ImageView)view.findViewById(R.id.iv_mute);
            magic_contactimg=(ImageView)view.findViewById(R.id.contact_image_magic);
            magic_icon=(ImageView)view.findViewById(R.id.magic_icon);
            smoothCheckBox=(SmoothCheckBox)view.findViewById(R.id.scb);
            rl_check=(RelativeLayout)view.findViewById(R.id.rl_checkbox);


            relativeLayout=(RelativeLayout)view.findViewById(R.id.my_chat_view);
//            palleteLayout = (RelativeLayout) view.findViewById(R.id.imagecolor);
            linearLayout=(LinearLayout)view.findViewById(R.id.active_chat_layout);
            progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        }
    }

    public interface InteractionListener
    {
        void onShowNewChats();
    }
}