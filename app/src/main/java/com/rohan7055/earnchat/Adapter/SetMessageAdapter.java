package com.rohan7055.earnchat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rohan7055.earnchat.AppConstants;
import com.rohan7055.earnchat.celgram.ContactsModel;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.rohan7055.earnchat.celgram.ReplyHolder;
import com.rohan7055.earnchat.celgram.RoundedCornersTransform;
import com.rohan7055.earnchat.utils.ImageHelper;
import com.rohan7055.earnchat.utils.RoundedCornersTransformation;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.dd.CircularProgressButton;
import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.AudioHolder;
import com.rohan7055.earnchat.celgram.AwsUtils;
import com.rohan7055.earnchat.celgram.CelgramUtils;
import com.rohan7055.earnchat.celgram.ChatWindow;
import com.rohan7055.earnchat.celgram.ContactHolder;
import com.rohan7055.earnchat.celgram.DocHolder;
import com.rohan7055.earnchat.celgram.ImagePreviewActivity1;
import com.rohan7055.earnchat.celgram.MediaHolder;

import com.rohan7055.earnchat.celgram.Message;
import com.rohan7055.earnchat.celgram.MessageHolder;
import com.rohan7055.earnchat.celgram.NotificationHolder;
import com.rohan7055.earnchat.celgram.StickerHolder;
import com.rohan7055.earnchat.celgram.Utils;
import com.rohan7055.earnchat.celgram.events.MessageAckEvent;
import com.rohan7055.earnchat.celgram.service.AwsDownloadService;
import com.rohan7055.earnchat.celgram.service.AwsUploadService;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class SetMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<Object> listData;
    private Activity activity;
    private ArrayList<TransferState> paused=new ArrayList<TransferState>();
    private ArrayList<TransferState> pending=new ArrayList<TransferState>();
    final MediaPlayer mediaPlayer;
    String current_media="";
    private SparseBooleanArray mSelectedItemsIds;
    private Handler myHandler=new Handler();
    private final int YOU_MSG=0,ME_MSG=1,YOU_MEDIA=2,ME_MEDIA=3,NOTIFICATION=4,YOU_LOC=5,ME_LOC=6, ME_CONTACT=7,YOU_CONTACT=8,
            YOU_VIDEO=9,ME_VIDEO=10,YOU_AUDIO=11,ME_AUDIO=12,YOU_DOC=13,ME_DOC=14,ME_STICKER=15,YOU_STICKER=16,ME_REPLY=17,YOU_REPLY=18;
    Context mcontext;
    boolean isContact;

    private HashMap<String,Integer> id_position=new HashMap<>();
    private EventBus eventBus;
    ContactSQLiteHelper contactSQLiteHelper;
    public static boolean notselected = true;

    public SetMessageAdapter(List<Object> list, Activity activity, boolean IsContact) {
        Log.i("CheckListSize",""+String.valueOf(list.size()));
        setHasStableIds(true);
        this.listData = list;
        this.activity = activity;
        eventBus=EventBus.getDefault();
        for(int i=0;i<list.size();i++){
            id_position.put(((Message)list.get(i)).getId(),i);

        }
        paused.add(TransferState.CANCELED);
        paused.add(TransferState.FAILED);
        paused.add(TransferState.PAUSED);

        pending.add(TransferState.WAITING);
        pending.add(TransferState.WAITING_FOR_NETWORK);
        pending.add(TransferState.RESUMED_WAITING);

        isContact = IsContact;
        notselected = true;

        mediaPlayer = new MediaPlayer();
        mSelectedItemsIds = new SparseBooleanArray();
        contactSQLiteHelper = new ContactSQLiteHelper(activity);
    }



    @Override
    public int getItemViewType(int position) {
        if(listData.get(position) instanceof Message) {
            Message message=((Message) listData.get(position));
            id_position.put(message.getId(),position);

            int msg_type =message.getMsg_type();
            if (msg_type == 0) {
                if (message.isSelf()) {
                    return ME_MSG;
                } else {
                    return YOU_MSG;
                }
            } else if (msg_type == 1) {
                if (message.isSelf()) {
                    return ME_MEDIA;
                } else {
                    return YOU_MEDIA;
                }
            }
            else if(msg_type==2){
                if (message.isSelf()) {
                    return ME_VIDEO;
                } else {
                    return YOU_VIDEO;
                }
            }
            else if(msg_type==13){
                if(message.isSelf()){
                    return ME_LOC;
                }else{
                    return YOU_LOC;
                }


            } else if(msg_type==5){
                if(message.isSelf()){
                    return ME_CONTACT;
                }else{
                    return YOU_CONTACT;
                }
            }
            else if(msg_type==3){
                if(message.isSelf()){
                    return ME_AUDIO;

                }else{
                    return YOU_AUDIO;
                }
            }
            else if(msg_type==4){
                if(message.isSelf()){
                    return ME_DOC;

                }else{
                    return YOU_DOC;
                }
            }
            else if(msg_type==6){
                if(message.isSelf()){
                    return ME_STICKER;

                }else{
                    return YOU_STICKER;
                }
            }
            else if(msg_type==14){
                if(message.isSelf()){
                    return ME_REPLY;

                }else{
                    return YOU_REPLY;
                }
            }
            else {
                return NOTIFICATION;
            }
        }
        else{
            return NOTIFICATION;
        }
    }

    public int getScreenWidth() {

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }

    public int getScreenHeight() {

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.y;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder=null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mcontext= parent.getContext();
        View v;
        switch (viewType) {

            case ME_MSG:
                v= inflater.inflate(R.layout.message_me, parent, false);
                viewHolder = new MessageHolder(v);
                break;
            case ME_MEDIA:
                v= inflater.inflate(R.layout.media_me, parent, false);
                viewHolder = new MediaHolder(v);
                break;
            case YOU_MSG:
                v= inflater.inflate(R.layout.message_you, parent, false);
                viewHolder = new MessageHolder(v);
                break;
            case YOU_MEDIA:
                v = inflater.inflate(R.layout.media_you, parent, false);
                viewHolder = new MediaHolder(v);
                break;
            case ME_LOC:
                v= inflater.inflate(R.layout.loc_me,parent,false);
                viewHolder=new MessageHolder(v);
                break;
            case YOU_LOC:
                v=inflater.inflate(R.layout.loc_you,parent,false);
                viewHolder=new MessageHolder(v);
                break;
            case ME_CONTACT:
                v=inflater.inflate(R.layout.contact_me,parent,false);
                viewHolder=new ContactHolder(v);
                break;
            case YOU_CONTACT:
                v=inflater.inflate(R.layout.contact_you,parent,false);
                viewHolder=new ContactHolder(v);
                break;
            case ME_VIDEO:
                v=inflater.inflate(R.layout.media_me,parent,false);
                viewHolder=new MediaHolder(v);
                break;
            case YOU_VIDEO:
                v = inflater.inflate(R.layout.media_you, parent, false);
                viewHolder = new MediaHolder(v);
                break;
            case ME_AUDIO:
                v=inflater.inflate(R.layout.audio_me,parent,false);
                viewHolder=new AudioHolder(v);
                break;
            case YOU_AUDIO:
                v = inflater.inflate(R.layout.audio_you, parent, false);
                viewHolder = new AudioHolder(v);
                break;
            case ME_DOC:
                v=inflater.inflate(R.layout.doc_me,parent,false);
                viewHolder=new DocHolder(v);
                break;
            case YOU_DOC:
                v = inflater.inflate(R.layout.doc_you, parent, false);
                viewHolder = new DocHolder(v);
                break;
            case ME_STICKER:
                v = inflater.inflate(R.layout.sticker_me, parent, false);
                viewHolder = new StickerHolder(v);
                break;
            case YOU_STICKER:
                v = inflater.inflate(R.layout.sticker_you, parent, false);
                viewHolder = new StickerHolder(v);
                break;
            case ME_REPLY:
                v = inflater.inflate(R.layout.reply_me, parent, false);
                viewHolder = new ReplyHolder(v);
                break;
            case YOU_REPLY:
                v = inflater.inflate(R.layout.reply_you, parent, false);
                viewHolder = new ReplyHolder(v);
                break;
            case NOTIFICATION:
                v=inflater.inflate(R.layout.chat_notification_cell, parent, false);
                viewHolder = new NotificationHolder(v);
                break;

            default:break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        /** Change background color of the selected items in list view  **/
        holder.itemView
                .setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4
                        : Color.TRANSPARENT);
        switch (holder.getItemViewType()) {
            case ME_MSG:
                MessageHolder vh1 = (MessageHolder) holder;
                configureMessageHolder(vh1, position);
                break;
            case ME_MEDIA:
                MediaHolder vh2 = (MediaHolder) holder;
                configureMediaHolder(vh2,position);
                break;
            case YOU_MSG:
                MessageHolder vh3 = (MessageHolder) holder;
                configureMessageHolder(vh3, position);
                break;
            case YOU_MEDIA:
                MediaHolder vh4 = (MediaHolder) holder;
                configureMediaHolder(vh4,position);
                break;
            case ME_LOC:
                MessageHolder vh5=(MessageHolder)holder;
                configureLocationHolder(vh5,position);
                break;
            case YOU_LOC:
                MessageHolder vh6=(MessageHolder)holder;
                configureLocationHolder(vh6,position);
                break;
            case NOTIFICATION:
                NotificationHolder vh7=(NotificationHolder) holder;
                configureNotification(vh7,position);
                break;
            case ME_CONTACT:
                ContactHolder vh8=(ContactHolder)holder;
                configureContactHolder(vh8,position);
                break;
            case YOU_CONTACT:
                ContactHolder vh9=(ContactHolder)holder;
                configureContactHolder(vh9,position);
                break;
            case ME_VIDEO:
                MediaHolder vh10=(MediaHolder)holder;
                configureVideoHolder(vh10,position);
                break;
            case YOU_VIDEO:
                MediaHolder vh11=(MediaHolder)holder;
                configureVideoHolder(vh11,position);
                break;
            case ME_AUDIO:
                AudioHolder vh12=(AudioHolder)holder;
                configureAudioHolder(vh12,position);
                break;
            case YOU_AUDIO:
                AudioHolder vh13=(AudioHolder)holder;
                configureAudioHolder(vh13,position);
                break;
            case ME_DOC:
                DocHolder vh14=(DocHolder)holder;
                configureDocHolder(vh14,position);
                break;
            case YOU_DOC:
                DocHolder vh15=(DocHolder)holder;
                configureDocHolder(vh15,position);
                break;
            case ME_STICKER:
                StickerHolder vh16=(StickerHolder) holder;
                configureStickerHolder(vh16,position);
                break;
            case YOU_STICKER:
                StickerHolder vh17=(StickerHolder) holder;
                configureStickerHolder(vh17,position);
                break;
            case ME_REPLY:
                ReplyHolder vh18=(ReplyHolder) holder;
                configureReplyHolder(vh18,position);
                break;
            case YOU_REPLY:
                ReplyHolder vh19=(ReplyHolder) holder;
                configureReplyHolder(vh19,position);
                break;

            default:break;

        }
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /***
     * Methods required for do selections, remove selections, etc.
     */

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    private void configureMessageHolder(MessageHolder vh, int position) {


        Message message = (Message) listData.get(position);

        vh.getMessage().setText(message.getData());
        vh.getTimestamp().setText(message.getTimestamp().substring(11, 16));

        setTimeTextVisibility(CelgramUtils.getCurrentTs(message), CelgramUtils.getPreviousTs(message,position,listData), vh.getDay());

        if(message.isSelf()){
            switch(message.getMsg_status()){
                case 0:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                    break;
                case 1:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));
                    break;
                case 2:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delivered));
                    break;
                case 3:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_read));
                    break;
                default:vh.getMsg_status().setVisibility(View.GONE);
            }

        }
        else{
            String userid = null;
            userid = contactSQLiteHelper.getUserid(message.getConvo_partner());
            Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/8Chat/ProfileImage/" + userid + "/" + contactSQLiteHelper.getContact(message.getConvo_partner()).getImgname())).error(R.drawable.temp).transform(new RoundedCornersTransform()).into(vh.getSender_image());

            if (!isContact){
                vh.getSender().setVisibility(View.VISIBLE);
            }

            if(message.getMsg_status()==0||message.getMsg_status()==2){
                MessageAckEvent event=new MessageAckEvent(message.getId(),message.getConvo_partner());
                eventBus.post(event);
            }

            if (CelgramMain.number_name.get(message.getConvo_partner()) != null) {
                vh.getSender().setText(CelgramMain.number_name.get(message.getConvo_partner()));
            } else {
                if(message.getConvo_type()==0) {
                    vh.getSender().setText(message.getConvo_partner());
                }
                else{
                    if(CelgramMain.number_name.get(message.getSender_id()) != null) {
                        vh.getSender().setText(CelgramMain.number_name.get(message.getSender_id()));
                        vh.getCelgram_name().setVisibility(View.GONE);
                    }
                    else{
                        vh.getSender().setText(message.getSender_id());
                        vh.getCelgram_name().setVisibility(View.VISIBLE);
                        vh.getCelgram_name().setText(CelgramMain.number_cname.get(message.getSender_id()));
                    }
                }

            }

        }


    }

    private void configureReplyHolder(ReplyHolder vh, final int position) {

        final Message message = (Message) listData.get(position);

        vh.getMessage().setText(message.getData());
        vh.getTimestamp().setText(message.getTimestamp().substring(11, 16));

        setTimeTextVisibility(CelgramUtils.getCurrentTs(message), CelgramUtils.getPreviousTs(message,position,listData), vh.getDay());

        SharedPreferences preferences = mcontext.getSharedPreferences("USER", MODE_PRIVATE);
        String mobile = preferences.getString("mobile", "");
        if (message.getMsg_url().equals(mobile)) {
            vh.getReplyingTo().setText("You");
        }
        else{
            List<ContactsModel> cList = new ArrayList<>();
            cList = contactSQLiteHelper.getAllContacts(1);
            ContactsModel model = ChatWindow.getActiveContact(cList, message.getMsg_url());

            if (model != null) {
                vh.getReplyingTo().setText(model.getDisplay_name());
            } else {
                vh.getReplyingTo().setText(message.getMsg_url());
            }
        }

        int msg_type = message.getMedia_size();
        if (msg_type == 6){
            vh.getReplyText().setText("\uD83D\uDCF7"+" STICKER");
        }
        else{
            vh.getReplyText().setText(message.getDuration());
        }

        vh.getLinearLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notselected) {
                    for (Object obj : listData) {
                        Message m = (Message) obj;
                        if (m.getId().equals(message.getMime_type())) {
                            ChatWindow.smoothScroll(listData.indexOf(obj));
                            break;
                        }
                    }
                }
            }
        });

        //according to message type do things...
        if (msg_type == 1) {

            Message replied = null;

            for (Object obj:listData){
                Message m = (Message)obj;
                if (m.getId().equals(message.getMime_type())){
                    replied = m;
                    break;
                }
            }

            try {
                File imgFile = null;
                if (replied != null) {
                    if (!replied.isSelf()) {
                        imgFile = new File(replied.getFile_path());
                        Log.i("SetMessageAdapter", imgFile.toString());
                        if (!imgFile.exists()) {
                            imgFile = new File(replied.getThumb_path());
                            Log.i("SetMessagegetThumb", imgFile.toString());
                        }
                    } else {
                        if (replied.getFile_path().equals("")) {
                            if (replied.getThumb_path() != null) {
                                imgFile = new File(replied.getThumb_path());
                            }
                        } else {
                            imgFile = new File(replied.getFile_path());
                            if (!imgFile.exists()) {
                                imgFile = new File(replied.getThumb_path());
                            }
                        }
                    }
                }
                else{
                    vh.getReplyImage().setImageResource(R.drawable.images);
                }
                if (imgFile != null) {
                    Picasso.with(mcontext).load(imgFile).transform(new RoundedCornersTransformation(10, 0)).fit().centerCrop().into(vh.getReplyImage());
                }
                else{
                    vh.getReplyImage().setImageResource(R.drawable.images);
                }
            }
            catch (Exception e){
                vh.getReplyImage().setImageResource(R.drawable.images);
            }
        }
        else if (msg_type == 2) {

            Message replied = null;

            for (Object obj:listData){
                Message m = (Message)obj;
                if (m.getId().equals(message.getMime_type())){
                    replied = m;
                    break;
                }
            }

            try{
                Bitmap thumb = null;
                if (replied != null) {
                    if (message.isSelf()) {
                        thumb = ThumbnailUtils.createVideoThumbnail(replied.getFile_path(), MediaStore.Video.Thumbnails.MICRO_KIND);
                    } else {
                        if (message.getFile_path().equals("")) {
                            thumb = BitmapFactory.decodeFile(replied.getThumb_path());
                        } else {
                            thumb = ThumbnailUtils.createVideoThumbnail(replied.getFile_path(), MediaStore.Video.Thumbnails.MICRO_KIND);
                        }
                    }
                }
                else{
                    vh.getReplyImage().setImageResource(R.drawable.images);
                }

                if (thumb != null) {
                    vh.getReplyImage().setImageBitmap(ImageHelper.getRoundedCornerBitmap(thumb, 10));
                }
                else{
                    vh.getReplyImage().setImageResource(R.drawable.images);
                }
            }
            catch (Exception e){
                vh.getReplyImage().setImageResource(R.drawable.images);
            }
        }
        else if (msg_type == 13) {
            vh.getReplyImage().setImageResource(R.drawable.loc);
        }
        else if (msg_type == 5) {
            vh.getReplyImage().setImageResource(R.drawable.contact_as_att);
        }
        else if (msg_type == 3) {
            vh.getReplyImage().setImageResource(R.drawable.audio);
        }
        else if (msg_type == 4) {
            vh.getReplyImage().setImageResource(R.drawable.doc);
        }
        else if (msg_type == 6) {
            String sticker_name=message.getDuration();
            if(sticker_name!=null){
                Picasso.with(activity)
                        .load(activity.getResources().getIdentifier(sticker_name,"drawable",activity.getPackageName()))
                        .fit()
                        .centerCrop()
                        .into(vh.getReplyImage());
            }
        }
        else{
            vh.getReplyImage().setVisibility(View.GONE);
        }

        if(message.isSelf()){
            switch(message.getMsg_status()){
                case 0:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                    break;
                case 1:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));
                    break;
                case 2:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delivered));
                    break;
                case 3:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_read));
                    break;
                default:vh.getMsg_status().setVisibility(View.GONE);
            }
        }
        else{
            /*String userid = null;
            userid = contactSQLiteHelper.getUserid(message.getConvo_partner());
            if (message.getConvo_partner().equals(AppConstants.CustomerCare)) {
                Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/8Chat/ProfileImage/" + userid + "/" + contactSQLiteHelper.getContact(message.getConvo_partner()).getImgname())).error(R.shadow.customer_care).transform(new RoundedCornersTransform()).into(vh.getSender_image());
            }
            else{
                Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/8Chat/ProfileImage/" + userid + "/" + contactSQLiteHelper.getContact(message.getConvo_partner()).getImgname())).error(R.shadow.temp).transform(new RoundedCornersTransform()).into(vh.getSender_image());
            }*/

            if(message.getMsg_status()==0||message.getMsg_status()==2){
                MessageAckEvent event=new MessageAckEvent(message.getId(),message.getConvo_partner());
                eventBus.post(event);
            }

            if (!isContact){
                vh.getSender().setVisibility(View.VISIBLE);
            }

            if (CelgramMain.number_name.get(message.getConvo_partner()) != null) {
                vh.getSender().setText(CelgramMain.number_name.get(message.getConvo_partner()));
            } else {
                if(message.getConvo_type()==0) {
                    vh.getSender().setText(message.getConvo_partner());
                    //vh.getSender().setText(title);
                }
                else{
                    if(CelgramMain.number_name.get(message.getSender_id()) != null) {
                        vh.getSender().setText(CelgramMain.number_name.get(message.getSender_id()));
                        //vh.getSender().setText(title);
                        vh.getCelgram_name().setVisibility(View.GONE);
                    }
                    else{
                        vh.getSender().setText(message.getSender_id());
                        //vh.getSender().setText(title);
                        vh.getCelgram_name().setVisibility(View.VISIBLE);
                        vh.getCelgram_name().setText(CelgramMain.number_cname.get(message.getSender_id()));
                    }
                }
            }
            //vh.getSender().setVisibility(View.GONE);
        }
    }

    private void configureVideoHolder(final MediaHolder vh,int position){

        final Message media = ChatWindow.getMessage(position);
        vh.getMedia().setTag(position);
        vh.getMedia().setOnClickListener(showMedia);
        vh.getCancel().setTag(position);
        vh.getCircularProgressButton().setTag(position);
        vh.getCancel().setOnClickListener(cancelTransfer);
        vh.getCircularProgressButton().setOnClickListener(resumeTransfer);
        vh.getCancel().setVisibility(View.GONE);
        vh.getCircularProgressButton().setVisibility(View.GONE);
        vh.getCircularProgressButton().setIndeterminateProgressMode(false);
        vh.getCircularProgressButton().setProgress(0);
        vh.getCircularProgressButton().setAlpha(0.5f);

        if(!media.isSelf()){
            vh.getCircularProgressButton().setText("\u25BD  "+Utils.getMediaSize(media.getMedia_size()));
            vh.getPlay_video().setVisibility(View.GONE);
        }

        vh.getTimestamp().setText(media.getTimestamp().substring(11, 16));

        setTimeTextVisibility(CelgramUtils.getCurrentTs(media), CelgramUtils.getPreviousTs(media,position,listData), vh.getDay());

        if (media != null) {

            Bitmap thumb;

            if(media.isSelf()){
                thumb=ThumbnailUtils.createVideoThumbnail(media.getFile_path(), MediaStore.Video.Thumbnails.MICRO_KIND);

                switch(media.getMsg_status()){
                    case 0:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                        break;
                    case 1:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));
                        break;
                    case 2:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delivered));
                        break;
                    case 3:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_read));
                        break;
                    default:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                        break;
                }

            }
            else{
                String userid = null;
                userid = contactSQLiteHelper.getUserid(media.getConvo_partner());
                Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/8Chat/ProfileImage/" + userid + "/" + contactSQLiteHelper.getContact(media.getConvo_partner()).getImgname())).error(R.drawable.temp).transform(new RoundedCornersTransform()).into(vh.getSender_image());

                if(media.getFile_path().equals("")){
                    thumb=BitmapFactory.decodeFile(media.getThumb_path());
                }
                else{
                    thumb=ThumbnailUtils.createVideoThumbnail(media.getFile_path(), MediaStore.Video.Thumbnails.MICRO_KIND);
                }

                if(media.getMsg_status()==0||media.getMsg_status()==2){
                    MessageAckEvent event=new MessageAckEvent(media.getId(),media.getConvo_partner());
                    eventBus.post(event);
                }

                if (!isContact){
                    vh.getSender().setVisibility(View.VISIBLE);
                }

                if (CelgramMain.number_name.get(media.getConvo_partner()) != null) {
                    vh.getSender().setText(CelgramMain.number_name.get(media.getConvo_partner()));
                } else {
                    if(media.getConvo_type()==0) {
                        vh.getSender().setText(media.getConvo_partner());
                    }
                    else{
                        if(CelgramMain.number_name.get(media.getSender_id()) != null) {
                            vh.getSender().setText(CelgramMain.number_name.get(media.getSender_id()));
                            vh.getCelgram_name().setVisibility(View.GONE);
                        }
                        else{
                            vh.getSender().setText(media.getSender_id());
                            vh.getCelgram_name().setVisibility(View.VISIBLE);
                            vh.getCelgram_name().setText(CelgramMain.number_cname.get(media.getSender_id()));
                        }
                    }

                }
            }

            thumb = ImageHelper.getRoundedCornerBitmap(thumb, 10);
            vh.getMedia().setImageBitmap(thumb);

            if (media.isSelf()) {

                if (media.getMsg_status() == -2) {
                    //thumbnail uploading
                    boolean show_progress=false;

                    if(media.getAws_id()!=-1) {
                        TransferState state=AwsUtils.getObserver(media.getAws_id(),mcontext).getState();
                        if (paused.contains(state)) {show_progress=false;
                        }
                        else {show_progress=true;}
                    }
                    else{show_progress=true;}

                    if(show_progress){
                        vh.getCircularProgressButton().setAlpha(0.7f);
                        vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                        vh.getCircularProgressButton().setClickable(false);
                        vh.getCircularProgressButton().setIndeterminateProgressMode(true);
                        vh.getCircularProgressButton().setProgress(50);
                        vh.getCancel().setVisibility(View.VISIBLE);
                        vh.getCancel().setClickable(true);
                    }
                    else{
                        vh.getCancel().setVisibility(View.GONE);
                        vh.getCircularProgressButton().setIndeterminateProgressMode(false);
                        vh.getCircularProgressButton().setAlpha(0.5f);
                        vh.getCircularProgressButton().setProgress(0);
                        vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                        vh.getCircularProgressButton().setClickable(true);
                    }

                }
                else if (media.getMsg_status() ==-1) {
                    //image uploading
                    if (media.getAws_id() !=-1){
                        TransferState state=AwsUtils.getObserver(media.getAws_id(),mcontext).getState();
                        if(pending.contains(state)) {
                            vh.getCircularProgressButton().setIndeterminateProgressMode(true);
                            vh.getCircularProgressButton().setProgress(50);
                            vh.getCircularProgressButton().setAlpha(0.7f);
                            vh.getCircularProgressButton().setClickable(false);
                            vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                            vh.getCancel().setVisibility(View.GONE);
                        }
                        else if(state.equals(TransferState.IN_PROGRESS)){
                            vh.getCircularProgressButton().setIndeterminateProgressMode(false);
                            vh.getCircularProgressButton().setProgress(media.getProgress());
                            vh.getCircularProgressButton().setAlpha(0.7f);
                            vh.getCircularProgressButton().setClickable(false);
                            vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                            vh.getCancel().setVisibility(View.VISIBLE);
                            vh.getCancel().setClickable(true);
                        }
                        else{
                            vh.getCancel().setVisibility(View.GONE);
                            vh.getCircularProgressButton().setIndeterminateProgressMode(false);
                            vh.getCircularProgressButton().setProgress(0);
                            vh.getCircularProgressButton().setAlpha(0.5f);
                            vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                            vh.getCircularProgressButton().setClickable(true);

                        }

                    }

                }
                else {
                    vh.getCircularProgressButton().setVisibility(View.GONE);
                    vh.getCancel().setVisibility(View.GONE);
                }

            }

            else{
                if(media.getMsg_status()==-2){
                    //image downloading
                    if(media.getAws_id()!=-1) {
                        TransferState state = AwsUtils.getObserver(media.getAws_id(), mcontext).getState();

                        if(pending.contains(state)) {
                            vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                            vh.getCancel().setVisibility(View.VISIBLE);
                            vh.getCircularProgressButton().setIndeterminateProgressMode(true);
                            vh.getCircularProgressButton().setProgress(50);
                            vh.getCircularProgressButton().setAlpha(0.7f);
                            vh.getCircularProgressButton().setClickable(false);
                            vh.getCancel().setClickable(true);

                        }
                        else if(state.equals(TransferState.IN_PROGRESS)){
                            vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                            vh.getCancel().setVisibility(View.VISIBLE);
                            vh.getCircularProgressButton().setClickable(false);
                            vh.getCancel().setClickable(true);
                            vh.getCircularProgressButton().setIndeterminateProgressMode(false);
                            vh.getCircularProgressButton().setProgress(media.getProgress());
                            vh.getCircularProgressButton().setAlpha(0.7f);
                        }
                        else{
                            vh.getCancel().setVisibility(View.GONE);
                            vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                            vh.getCircularProgressButton().setClickable(true);
                            vh.getCircularProgressButton().setProgress(0);
                            vh.getCircularProgressButton().setAlpha(0.5f);
                            vh.getCircularProgressButton().setText("\u25BD  "+Utils.getMediaSize(media.getMedia_size()));
                        }
                    }

                }
                else {
                    if (media.getFile_path().equals("")) {
                        vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                        vh.getCancel().setVisibility(View.GONE);
                        vh.getCircularProgressButton().setClickable(true);
                        vh.getCircularProgressButton().setIndeterminateProgressMode(false);
                        vh.getCircularProgressButton().setProgress(0);
                        vh.getCircularProgressButton().setAlpha(0.5f);
                        vh.getCircularProgressButton().setText("\u25BD  "+Utils.getMediaSize(media.getMedia_size()));
                    }
                    else {
                        vh.getCircularProgressButton().setVisibility(View.GONE);
                        vh.getCancel().setVisibility(View.GONE);
                        vh.getPlay_video().setVisibility(View.VISIBLE);
                    }
                }

            }

        }
    }

    private void configureContactHolder(ContactHolder vh, int position)
    {
        Message message=(Message)listData.get(position);

        String ms=message.getData();
        final String x[]=ms.split("/");
        vh.getContact_name().setText(x[0]);
        vh.getContact_number().setText(x[1]);
        vh.getTimestamp().setText(message.getTimestamp().substring(11, 16));

        setTimeTextVisibility(CelgramUtils.getCurrentTs(message), CelgramUtils.getPreviousTs(message,position,listData), vh.getDay());

        if(message.isSelf()){
            switch(message.getMsg_status()){
                case 0:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                    break;
                case 1:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));
                    break;
                case 2:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delivered));
                    break;
                case 3:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_read));
                    break;
                default:vh.getMsg_status().setVisibility(View.GONE);
            }

        }
        else{
            String userid = null;
            userid = contactSQLiteHelper.getUserid(message.getConvo_partner());
            Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/8Chat/ProfileImage/" + userid + "/" + contactSQLiteHelper.getContact(message.getConvo_partner()).getImgname())).error(R.drawable.temp).transform(new RoundedCornersTransform()).into(vh.getSender_image());

            if(message.getMsg_status()==0||message.getMsg_status()==2){
                MessageAckEvent event=new MessageAckEvent(message.getId(),message.getConvo_partner());
                eventBus.post(event);
            }

            if (!isContact){
                vh.getSender().setVisibility(View.VISIBLE);
            }

            if (CelgramMain.number_name.get(message.getConvo_partner()) != null) {
                vh.getSender().setText(CelgramMain.number_name.get(message.getConvo_partner()));
            } else {
                if(message.getConvo_type()==0) {
                    vh.getSender().setText(message.getConvo_partner());
                }
                else{
                    if(CelgramMain.number_name.get(message.getSender_id()) != null) {
                        vh.getSender().setText(CelgramMain.number_name.get(message.getSender_id()));
                        vh.getCelgram_name().setVisibility(View.GONE);
                    }
                    else{
                        vh.getSender().setText(message.getSender_id());
                        vh.getCelgram_name().setVisibility(View.VISIBLE);
                        vh.getCelgram_name().setText(CelgramMain.number_cname.get(message.getSender_id()));
                    }
                }

            }


            vh.getSave_contact().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    if (notselected) {
                        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                        contactIntent
                                .putExtra(ContactsContract.Intents.Insert.PHONE, x[1]);

                        activity.startActivity(contactIntent);
                    }
                }
            });

            vh.getMsg_contact().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (notselected) {
                        //Toast.makeText(mcontext,"Message contact",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(activity, ChatWindow.class);
                        i.putExtra("isContact", true);
                        i.putExtra("contact", x[1]);
                        CelgramMain.current_chat = x[1];
                        activity.startActivity(i);
                        activity.finish();
                    }
                }
            });
        }
    }

    private void configureLocationHolder(MessageHolder vh,int position) {
        Message message = (Message) listData.get(position);
        vh.getTimestamp().setText(message.getTimestamp().substring(11, 16));

        setTimeTextVisibility(CelgramUtils.getCurrentTs(message), CelgramUtils.getPreviousTs(message, position, listData), vh.getDay());

        if (message.isSelf()) {
            switch (message.getMsg_status()) {
                case 0:
                    vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                    break;
                case 1:
                    vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));
                    break;
                case 2:
                    vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delivered));
                    break;
                case 3:
                    vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_read));
                    break;
                default:
                    vh.getMsg_status().setVisibility(View.GONE);
            }

        } else {
            String userid = null;
            userid = contactSQLiteHelper.getUserid(message.getConvo_partner());
            Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/8Chat/ProfileImage/" + userid + "/" + contactSQLiteHelper.getContact(message.getConvo_partner()).getImgname())).error(R.drawable.temp).transform(new RoundedCornersTransform()).into(vh.getSender_image());

            if (message.getMsg_status() == 0 || message.getMsg_status() == 2) {
                MessageAckEvent event = new MessageAckEvent(message.getId(), message.getConvo_partner());
                eventBus.post(event);
            }

            if (!isContact){
                vh.getSender().setVisibility(View.VISIBLE);
            }

            if (CelgramMain.number_name.get(message.getConvo_partner()) != null) {
                vh.getSender().setText(CelgramMain.number_name.get(message.getConvo_partner()));
            } else {
                if (message.getConvo_type() == 0) {
                    vh.getSender().setText(message.getConvo_partner());
                } else {
                    if (CelgramMain.number_name.get(message.getSender_id()) != null) {
                        vh.getSender().setText(CelgramMain.number_name.get(message.getSender_id()));
                        vh.getCelgram_name().setVisibility(View.GONE);
                    } else {
                        vh.getSender().setText(message.getSender_id());
                        vh.getCelgram_name().setVisibility(View.VISIBLE);
                        vh.getCelgram_name().setText(CelgramMain.number_cname.get(message.getSender_id()));
                    }
                }

            }

        }

        final String ms = message.getData();
        Log.i("Location_check","data"+ms);
        String loc[] = ms.split("/");

        vh.getMessage().setText(loc[0]);

        String longi = ms.substring(ms.lastIndexOf("|") + 1);

        final String lat=ms.substring(ms.lastIndexOf("/") + 1,ms.lastIndexOf("|"));

        String coordinates[] = loc[1].split("|");

        final double lati=Double.parseDouble(lat);
        final double longitude=Double.parseDouble(longi);

        Log.i("Location_check",""+coordinates[0]);

        Log.i("Location_check",""+coordinates[1]);

        vh.getMessage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notselected) {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lati, longitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    activity.startActivity(intent);
                }
            }
        });
    }

    private void configureMediaHolder(final MediaHolder vh, int position) {

        final Message media = ChatWindow.getMessage(position);
        vh.getMedia().setTag(position);
        vh.getMedia().setOnClickListener(showMedia);
        vh.getCancel().setTag(position);
        vh.getCircularProgressButton().setTag(position);
        vh.getCancel().setOnClickListener(cancelTransfer);
        vh.getCircularProgressButton().setOnClickListener(resumeTransfer);
        vh.getCancel().setVisibility(View.GONE);
        vh.getCircularProgressButton().setVisibility(View.GONE);
        vh.getCircularProgressButton().setIndeterminateProgressMode(false);
        vh.getCircularProgressButton().setProgress(0);
        vh.getCircularProgressButton().setAlpha(0.5f);

        if (media.getData().isEmpty()){
            vh.getMessage().setVisibility(View.GONE);
        }
        else{
            vh.getMessage().setText(media.getData());
        }

        if(!media.isSelf()){
            vh.getCircularProgressButton().setText("\u25BD  "+Utils.getMediaSize(media.getMedia_size()));
            vh.getPlay_video().setVisibility(View.GONE);
        }

        vh.getTimestamp().setText(media.getTimestamp().substring(11, 16));

        setTimeTextVisibility(CelgramUtils.getCurrentTs(media), CelgramUtils.getPreviousTs(media, position, listData), vh.getDay());


        File imgFile = null;
        //File imgFile;

        if (media.isSelf()) {
            imgFile = new File(media.getFile_path());
            Log.i("SetMessageAdapter",imgFile.toString());
            if(!imgFile.exists()){
                imgFile=new File(media.getThumb_path());
                Log.i("SetMessagegetThumb",imgFile.toString());
            }

            switch(media.getMsg_status()){
                case 0:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                    break;
                case 1:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));
                    break;
                case 2:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delivered));
                    break;
                case 3:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_read));
                    break;
                default:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                    break;
            }

        } else {
            String userid = null;
            userid = contactSQLiteHelper.getUserid(media.getConvo_partner());
            Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/8Chat/ProfileImage/" + userid + "/" + contactSQLiteHelper.getContact(media.getConvo_partner()).getImgname())).error(R.drawable.temp).transform(new RoundedCornersTransform()).into(vh.getSender_image());

            if (media.getMsg_status() == 0 || media.getMsg_status() == 2) {
                MessageAckEvent event = new MessageAckEvent(media.getId(), media.getConvo_partner());
                eventBus.post(event);
            }

            if (!isContact){
                vh.getSender().setVisibility(View.VISIBLE);
            }

            if (CelgramMain.number_name.get(media.getConvo_partner()) != null) {
                vh.getSender().setText(CelgramMain.number_name.get(media.getConvo_partner()));
            } else {
                if (media.getConvo_type() == 0) {
                    vh.getSender().setText(media.getConvo_partner());
                } else {
                    if (CelgramMain.number_name.get(media.getSender_id()) != null) {
                        vh.getSender().setText(CelgramMain.number_name.get(media.getSender_id()));
                        vh.getCelgram_name().setVisibility(View.GONE);
                    } else {
                        vh.getSender().setText(media.getSender_id());
                        vh.getCelgram_name().setVisibility(View.VISIBLE);
                        vh.getCelgram_name().setText(CelgramMain.number_cname.get(media.getSender_id()));
                    }
                }

            }

            if (media.getFile_path().equals("")) {
                if (media.getThumb_path() != null) {
                    imgFile = new File(media.getThumb_path());
                }
            } else {
                imgFile = new File(media.getFile_path());
                if(!imgFile.exists()){
                    imgFile=new File(media.getThumb_path());
                }
            }
        }

        if (imgFile != null) {

            //Picasso.with(mcontext).load(imgFile).fit().centerCrop().into(vh.getMedia());
            Picasso.with(mcontext).load(imgFile).transform(new RoundedCornersTransformation(20, 0)).fit().centerCrop().into(vh.getMedia());

            if (media.isSelf()) {

                if (media.getMsg_status() == -2) {
                    //thumbnail uploading
                    boolean show_progress=false;

                    if(media.getAws_id()!=-1) {
                        TransferState state=AwsUtils.getObserver(media.getAws_id(),mcontext).getState();
                        if (paused.contains(state)) {show_progress=false;
                        }
                        else {show_progress=true;}
                    }
                    else{show_progress=true;}

                    if(show_progress){
                        vh.getCircularProgressButton().setAlpha(0.7f);
                        vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                        vh.getCircularProgressButton().setClickable(false);
                        vh.getCircularProgressButton().setIndeterminateProgressMode(true);
                        vh.getCircularProgressButton().setProgress(50);
                        vh.getCancel().setVisibility(View.VISIBLE);
                        vh.getCancel().setClickable(true);
                    }
                    else{
                        vh.getCancel().setVisibility(View.GONE);
                        vh.getCircularProgressButton().setIndeterminateProgressMode(false);
                        vh.getCircularProgressButton().setAlpha(0.5f);
                        vh.getCircularProgressButton().setProgress(0);
                        vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                        vh.getCircularProgressButton().setClickable(true);
                    }

                }
                else if (media.getMsg_status() ==-1) {
                    //image uploading
                    if (media.getAws_id() !=-1){
                        TransferState state=AwsUtils.getObserver(media.getAws_id(),mcontext).getState();
                        if(pending.contains(state)) {
                            vh.getCircularProgressButton().setIndeterminateProgressMode(true);
                            vh.getCircularProgressButton().setProgress(50);
                            vh.getCircularProgressButton().setAlpha(0.7f);
                            vh.getCircularProgressButton().setClickable(false);
                            vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                            vh.getCancel().setVisibility(View.GONE);
                        }
                        else if(state.equals(TransferState.IN_PROGRESS)){
                            vh.getCircularProgressButton().setIndeterminateProgressMode(false);
                            vh.getCircularProgressButton().setProgress(media.getProgress());
                            vh.getCircularProgressButton().setAlpha(0.7f);
                            vh.getCircularProgressButton().setClickable(false);
                            vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                            vh.getCancel().setVisibility(View.VISIBLE);
                            vh.getCancel().setClickable(true);
                        }
                        else{
                            vh.getCancel().setVisibility(View.GONE);
                            vh.getCircularProgressButton().setIndeterminateProgressMode(false);
                            vh.getCircularProgressButton().setProgress(0);
                            vh.getCircularProgressButton().setAlpha(0.5f);
                            vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                            vh.getCircularProgressButton().setClickable(true);

                        }

                    }

                }
                else {
                    vh.getCircularProgressButton().setVisibility(View.GONE);
                    vh.getCancel().setVisibility(View.GONE);
                }

            }

            else{
                if(media.getMsg_status()==-2){
                    //image downloading
                    if(media.getAws_id()!=-1) {
                        TransferState state = AwsUtils.getObserver(media.getAws_id(), mcontext).getState();

                        if(pending.contains(state)) {
                            vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                            vh.getCancel().setVisibility(View.VISIBLE);
                            vh.getCircularProgressButton().setIndeterminateProgressMode(true);
                            vh.getCircularProgressButton().setProgress(50);
                            vh.getCircularProgressButton().setAlpha(0.7f);
                            vh.getCircularProgressButton().setClickable(false);
                            vh.getCancel().setClickable(true);

                        }
                        else if(state.equals(TransferState.IN_PROGRESS)){
                            vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                            vh.getCancel().setVisibility(View.VISIBLE);
                            vh.getCircularProgressButton().setClickable(false);
                            vh.getCancel().setClickable(true);
                            vh.getCircularProgressButton().setIndeterminateProgressMode(false);
                            vh.getCircularProgressButton().setProgress(media.getProgress());
                            vh.getCircularProgressButton().setAlpha(0.7f);
                        }
                        else{
                            vh.getCancel().setVisibility(View.GONE);
                            vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                            vh.getCircularProgressButton().setClickable(true);
                            vh.getCircularProgressButton().setProgress(0);
                            vh.getCircularProgressButton().setAlpha(0.5f);
                            vh.getCircularProgressButton().setText("\u25BD  "+Utils.getMediaSize(media.getMedia_size()));
                        }
                    }

                }
                else {
                    if (media.getFile_path().equals("")) {
                        vh.getCircularProgressButton().setVisibility(View.VISIBLE);
                        vh.getCancel().setVisibility(View.GONE);
                        vh.getCircularProgressButton().setClickable(true);
                        vh.getCircularProgressButton().setIndeterminateProgressMode(false);
                        vh.getCircularProgressButton().setProgress(0);
                        vh.getCircularProgressButton().setAlpha(0.5f);
                        vh.getCircularProgressButton().setText("\u25BD  "+Utils.getMediaSize(media.getMedia_size()));
                    }
                    else {
                        vh.getCircularProgressButton().setVisibility(View.GONE);
                        vh.getCancel().setVisibility(View.GONE);
                    }
                }

            }
        }

    }

    private void configureDocHolder(DocHolder vh,int position){

        Message media = (Message)listData.get(position);

        vh.getTimestamp().setText(media.getTimestamp().substring(11, 16));
        vh.getDoc_name().setText(media.getData());

        setTimeTextVisibility(CelgramUtils.getCurrentTs(media), CelgramUtils.getPreviousTs(media, position, listData), vh.getDay());

        if (media.isSelf()) {

            switch(media.getMsg_status()){
                case 0:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                    break;
                case 1:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));
                    break;
                case 2:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delivered));
                    break;
                case 3:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_read));
                    break;
                default:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                    break;
            }

        } else {
            String userid = null;
            userid = contactSQLiteHelper.getUserid(media.getConvo_partner());
            Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/8Chat/ProfileImage/" + userid + "/" + contactSQLiteHelper.getContact(media.getConvo_partner()).getImgname())).error(R.drawable.temp).transform(new RoundedCornersTransform()).into(vh.getSender_image());

            if (media.getMsg_status() == 0 || media.getMsg_status() == 2) {
                MessageAckEvent event = new MessageAckEvent(media.getId(), media.getConvo_partner());
                eventBus.post(event);
            }

            if (!isContact){
                vh.getSender().setVisibility(View.VISIBLE);
            }

            if (CelgramMain.number_name.get(media.getConvo_partner()) != null) {
                vh.getSender().setText(CelgramMain.number_name.get(media.getConvo_partner()));
            } else {
                if (media.getConvo_type() == 0) {
                    vh.getSender().setText(media.getConvo_partner());
                } else {
                    if (CelgramMain.number_name.get(media.getSender_id()) != null) {
                        vh.getSender().setText(CelgramMain.number_name.get(media.getSender_id()));
                        vh.getCelgram_name().setVisibility(View.GONE);
                    } else {
                        vh.getSender().setText(media.getSender_id());
                        vh.getCelgram_name().setVisibility(View.VISIBLE);
                        vh.getCelgram_name().setText(CelgramMain.number_cname.get(media.getSender_id()));
                    }
                }

            }
        }
        vh.getCpb().setTag(position);
        vh.getCancel().setTag(position);
        vh.getCpb().setVisibility(View.GONE);
        vh.getCancel().setVisibility(View.GONE);
        vh.getCpb().setOnClickListener(resumeTransfer);
        vh.getCancel().setOnClickListener(cancelTransfer);
        vh.getCpb().setIndeterminateProgressMode(false);
        vh.getCpb().setProgress(0);
        vh.getCpb().setAlpha(0.5f);
        //vh.getDoc_name().setTag(position);
        //vh.getDoc_name().setOnClickListener(showDoc);
        vh.getRelativeLayout().setTag(position);
        vh.getRelativeLayout().setOnClickListener(showDoc);

        if(media.isSelf()){
            if(media.getAws_id()!=-1){
                TransferState state=AwsUtils.getObserver(media.getAws_id(),mcontext).getState();
                if(pending.contains(state)){
                    vh.getCpb().setVisibility(View.VISIBLE);
                    vh.getCpb().setIndeterminateProgressMode(true);
                    vh.getCpb().setProgress(50);
                    vh.getCpb().setAlpha(0.7f);
                    vh.getCpb().setClickable(false);
                    vh.getCancel().setVisibility(View.GONE);
                    vh.getDoc_image().setVisibility(View.GONE);
                }
                else if(state.equals(TransferState.IN_PROGRESS)){
                    vh.getCpb().setIndeterminateProgressMode(false);
                    vh.getCpb().setAlpha(0.7f);
                    vh.getCpb().setVisibility(View.VISIBLE);
                    vh.getCpb().setClickable(false);
                    vh.getCpb().setProgress(media.getProgress());
                    vh.getCancel().setVisibility(View.VISIBLE);
                    vh.getCancel().setClickable(true);
                    vh.getDoc_image().setVisibility(View.GONE);
                }
                else if(state.equals(TransferState.COMPLETED)){
                    vh.getCancel().setVisibility(View.GONE);
                    vh.getCpb().setVisibility(View.GONE);
                    vh.getDoc_image().setVisibility(View.VISIBLE);
                }
                else{
                    vh.getCpb().setVisibility(View.VISIBLE);
                    vh.getCpb().setIndeterminateProgressMode(false);
                    vh.getCpb().setProgress(0);
                    vh.getCpb().setClickable(true);
                    vh.getCpb().setAlpha(0.5f);
                    vh.getCancel().setVisibility(View.GONE);
                    vh.getDoc_image().setVisibility(View.GONE);
                }

            }
            else{
                vh.getCpb().setVisibility(View.VISIBLE);
                vh.getCpb().setIndeterminateProgressMode(true);
                vh.getCpb().setProgress(50);
                vh.getCpb().setAlpha(0.7f);
                vh.getCpb().setClickable(false);
                vh.getCancel().setVisibility(View.GONE);
                vh.getDoc_image().setVisibility(View.GONE);
            }

        }
        else{
            if(media.getFile_path().equals("")){
                if(media.getAws_id()==-1){
                    Log.i("check_download","true");
                    vh.getCpb().setVisibility(View.VISIBLE);
                    vh.getCpb().setClickable(true);
                    vh.getCpb().setIndeterminateProgressMode(false);
                    vh.getCpb().setProgress(0);
                    vh.getCpb().setAlpha(0.5f);
                    vh.getCpb().setText("\u25BD  "+Utils.getMediaSize(media.getMedia_size()));
                    vh.getCancel().setVisibility(View.GONE);
                    vh.getDoc_name().setVisibility(View.GONE);}

                else{
                    TransferState state=AwsUtils.getObserver(media.getAws_id(),mcontext).getState();
                    if(pending.contains(state)){
                        vh.getCpb().setVisibility(View.VISIBLE);
                        vh.getCpb().setIndeterminateProgressMode(true);
                        vh.getCpb().setProgress(50);
                        vh.getCpb().setAlpha(0.7f);
                        vh.getCpb().setClickable(false);
                        vh.getCancel().setVisibility(View.GONE);
                        vh.getDoc_name().setVisibility(View.GONE);
                    }
                    else if(state.equals(TransferState.IN_PROGRESS)){
                        vh.getCpb().setIndeterminateProgressMode(false);
                        vh.getCpb().setAlpha(0.7f);
                        vh.getCpb().setVisibility(View.VISIBLE);
                        vh.getCpb().setClickable(false);
                        vh.getCpb().setProgress(media.getProgress());
                        vh.getCancel().setVisibility(View.VISIBLE);
                        vh.getCancel().setClickable(true);
                        vh.getDoc_name().setVisibility(View.GONE);
                    }
                    else{
                        vh.getCpb().setVisibility(View.VISIBLE);
                        vh.getCpb().setClickable(true);
                        vh.getCpb().setIndeterminateProgressMode(false);
                        vh.getCpb().setProgress(0);
                        vh.getCpb().setAlpha(0.5f);
                        vh.getCpb().setIdleText("\u25BD  "+Utils.getMediaSize(media.getMedia_size()));
                        vh.getCancel().setVisibility(View.GONE);
                        vh.getDoc_name().setVisibility(View.GONE);
                    }
                }
            }
            else{
                vh.getCpb().setVisibility(View.GONE);
                vh.getCancel().setVisibility(View.GONE);
                vh.getDoc_name().setVisibility(View.VISIBLE);
            }

        }


    }

    private void configureNotification(NotificationHolder vh,int position){
        String notification=((Message)listData.get(position)).getNotificationBody();
        if(notification!=null){
            vh.getNotification_text().setText(notification);
        }

    }

    private void configureStickerHolder(StickerHolder vh,int position){
        Message message=((Message)listData.get(position));

        vh.getTimestamp().setText(message.getTimestamp().substring(11, 16));

        setTimeTextVisibility(CelgramUtils.getCurrentTs(message), CelgramUtils.getPreviousTs(message,position,listData), vh.getDay());

        if(message.isSelf()){
            switch(message.getMsg_status()){
                case 0:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                    break;
                case 1:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));
                    break;
                case 2:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delivered));
                    break;
                case 3:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_read));
                    break;
                default:vh.getMsg_status().setVisibility(View.GONE);
            }

        }
        else{
            String userid = null;
            userid = contactSQLiteHelper.getUserid(message.getConvo_partner());
            Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/8Chat/ProfileImage/" + userid + "/" + contactSQLiteHelper.getContact(message.getConvo_partner()).getImgname())).error(R.drawable.temp).transform(new RoundedCornersTransform()).into(vh.getSender_image());

            if(message.getMsg_status()==0||message.getMsg_status()==2){
                MessageAckEvent event=new MessageAckEvent(message.getId(),message.getConvo_partner());
                eventBus.post(event);
            }

            if (!isContact){
                vh.getSender().setVisibility(View.VISIBLE);
            }

            if (CelgramMain.number_name.get(message.getConvo_partner()) != null) {
                vh.getSender().setText(CelgramMain.number_name.get(message.getConvo_partner()));
            } else {
                if(message.getConvo_type()==0) {
                    vh.getSender().setText(message.getConvo_partner());
                }
                else{
                    if(CelgramMain.number_name.get(message.getSender_id()) != null) {
                        vh.getSender().setText(CelgramMain.number_name.get(message.getSender_id()));
                        vh.getCelgram_name().setVisibility(View.GONE);
                    }
                    else{
                        vh.getSender().setText(message.getSender_id());
                        vh.getCelgram_name().setVisibility(View.VISIBLE);
                        vh.getCelgram_name().setText(CelgramMain.number_cname.get(message.getSender_id()));
                    }
                }

            }

        }

        String sticker_name=message.getData();
        if(sticker_name!=null){
            Picasso.with(activity)
                    .load(activity.getResources().getIdentifier(sticker_name,"drawable",activity.getPackageName()))
                    .resize(200,200)
                    .into(vh.getSticker());
        }

    }

    private void configureAudioHolder(final AudioHolder vh,int position){
        Message media = (Message)listData.get(position);

        vh.getTimestamp().setText(media.getTimestamp().substring(11, 16));

        setTimeTextVisibility(CelgramUtils.getCurrentTs(media), CelgramUtils.getPreviousTs(media,position,listData), vh.getDay());

        if(media.isSelf()){
            switch(media.getMsg_status()){
                case 0:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                    break;
                case 1:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sent));
                    break;
                case 2:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delivered));
                    break;
                case 3:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_read));
                    break;
                default:vh.getMsg_status().setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending));
                    break;
            }

        }
        else{
            String userid = null;
            userid = contactSQLiteHelper.getUserid(media.getConvo_partner());
            Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/8Chat/ProfileImage/" + userid + "/" + contactSQLiteHelper.getContact(media.getConvo_partner()).getImgname())).error(R.drawable.temp).transform(new RoundedCornersTransform()).into(vh.getSender_image());

            if(media.getMsg_status()==0||media.getMsg_status()==2){
                MessageAckEvent event=new MessageAckEvent(media.getId(),media.getConvo_partner());
                eventBus.post(event);
            }

            if (!isContact){
                vh.getSender().setVisibility(View.VISIBLE);
            }

            if (CelgramMain.number_name.get(media.getConvo_partner()) != null) {
                vh.getSender().setText(CelgramMain.number_name.get(media.getConvo_partner()));
            } else {
                if(media.getConvo_type()==0) {
                    vh.getSender().setText(media.getConvo_partner());
                }
                else{
                    if(CelgramMain.number_name.get(media.getSender_id()) != null) {
                        vh.getSender().setText(CelgramMain.number_name.get(media.getSender_id()));
                        vh.getCelgram_name().setVisibility(View.GONE);
                    }
                    else{
                        vh.getSender().setText(media.getSender_id());
                        vh.getCelgram_name().setVisibility(View.VISIBLE);
                        vh.getCelgram_name().setText(CelgramMain.number_cname.get(media.getSender_id()));
                    }
                }

            }

        }
        vh.getCpb().setTag(position);
        vh.getCancel().setTag(position);
        vh.getCpb().setVisibility(View.GONE);
        vh.getCancel().setVisibility(View.GONE);
        vh.getCpb().setOnClickListener(resumeTransfer);
        vh.getCancel().setOnClickListener(cancelTransfer);
        vh.getCpb().setIndeterminateProgressMode(false);
        vh.getCpb().setProgress(0);
        vh.getCpb().setAlpha(0.5f);
        vh.getPlay_pause().setTag(position);
        vh.getPlay_pause().setOnClickListener(playPause);
        vh.getDuration().setText(media.getDuration());
        if(media.isSelf()){
            vh.getHead_layout().setVisibility(View.GONE);
        }
        else{
            vh.getPp_relative_layout().setVisibility(View.GONE);
        }


        if(media.isSelf()){
            if(media.getAws_id()!=-1){
                TransferState state=AwsUtils.getObserver(media.getAws_id(),mcontext).getState();
                if(pending.contains(state)){
                    vh.getCpb().setVisibility(View.VISIBLE);
                    vh.getCpb().setIndeterminateProgressMode(true);
                    vh.getCpb().setProgress(50);
                    vh.getCpb().setAlpha(0.7f);
                    vh.getCpb().setClickable(false);
                    vh.getCancel().setVisibility(View.GONE);
                    vh.getHead_layout().setVisibility(View.GONE);
                }
                else if(state.equals(TransferState.IN_PROGRESS)){
                    vh.getCpb().setIndeterminateProgressMode(false);
                    vh.getCpb().setAlpha(0.7f);
                    vh.getCpb().setVisibility(View.VISIBLE);
                    vh.getCpb().setClickable(false);
                    vh.getCpb().setProgress(media.getProgress());
                    vh.getCancel().setVisibility(View.VISIBLE);
                    vh.getCancel().setClickable(true);
                    vh.getHead_layout().setVisibility(View.GONE);
                }
                else if(state.equals(TransferState.COMPLETED)){
                    vh.getCancel().setVisibility(View.GONE);
                    vh.getCpb().setVisibility(View.GONE);
                    vh.getHead_layout().setVisibility(View.VISIBLE);
                }
                else{
                    vh.getCpb().setVisibility(View.VISIBLE);
                    vh.getCpb().setIndeterminateProgressMode(false);
                    vh.getCpb().setProgress(0);
                    vh.getCpb().setClickable(true);
                    vh.getCpb().setAlpha(0.5f);
                    vh.getCancel().setVisibility(View.GONE);
                    vh.getHead_layout().setVisibility(View.GONE);
                }

            }
            else{
                vh.getCpb().setVisibility(View.VISIBLE);
                vh.getCpb().setIndeterminateProgressMode(true);
                vh.getCpb().setProgress(50);
                vh.getCpb().setAlpha(0.7f);
                vh.getCpb().setClickable(false);
                vh.getCancel().setVisibility(View.GONE);
                vh.getHead_layout().setVisibility(View.GONE);
            }

        }
        else{
            if(media.getFile_path().equals("")){
                if(media.getAws_id()==-1){
                    Log.i("check_download","true");
                    vh.getCpb().setVisibility(View.VISIBLE);
                    vh.getCpb().setClickable(true);
                    vh.getCpb().setIndeterminateProgressMode(false);
                    vh.getCpb().setProgress(0);
                    vh.getCpb().setAlpha(0.5f);
                    vh.getCpb().setText("\u25BD  "+Utils.getMediaSize(media.getMedia_size()));
                    vh.getCancel().setVisibility(View.GONE);
                    vh.getPp_relative_layout().setVisibility(View.GONE);}

                else{
                    TransferState state=AwsUtils.getObserver(media.getAws_id(),mcontext).getState();
                    if(pending.contains(state)){
                        vh.getCpb().setVisibility(View.VISIBLE);
                        vh.getCpb().setIndeterminateProgressMode(true);
                        vh.getCpb().setProgress(50);
                        vh.getCpb().setAlpha(0.7f);
                        vh.getCpb().setClickable(false);
                        vh.getCancel().setVisibility(View.GONE);
                        vh.getPp_relative_layout().setVisibility(View.GONE);
                    }
                    else if(state.equals(TransferState.IN_PROGRESS)){
                        vh.getCpb().setIndeterminateProgressMode(false);
                        vh.getCpb().setAlpha(0.7f);
                        vh.getCpb().setVisibility(View.VISIBLE);
                        vh.getCpb().setClickable(false);
                        vh.getCpb().setProgress(media.getProgress());
                        vh.getCancel().setVisibility(View.VISIBLE);
                        vh.getCancel().setClickable(true);
                        vh.getPp_relative_layout().setVisibility(View.GONE);
                    }
                    else{
                        vh.getCpb().setVisibility(View.VISIBLE);
                        vh.getCpb().setClickable(true);
                        vh.getCpb().setIndeterminateProgressMode(false);
                        vh.getCpb().setProgress(0);
                        vh.getCpb().setAlpha(0.5f);
                        vh.getCpb().setIdleText("\u25BD  "+Utils.getMediaSize(media.getMedia_size()));
                        vh.getCancel().setVisibility(View.GONE);
                        vh.getPp_relative_layout().setVisibility(View.GONE);
                    }
                }
            }
            else{
                vh.getCpb().setVisibility(View.GONE);
                vh.getCancel().setVisibility(View.GONE);
                vh.getPp_relative_layout().setVisibility(View.VISIBLE);
            }

        }


    }


    View.OnClickListener showMedia = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (notselected) {
                int position = (Integer) v.getTag();
                Message media = ChatWindow.getMessage(position);

                if (!(media.getMsg_status() == -1 || media.getMsg_status() == -2)) {

                    if (media.getAws_id() != -1) {

                        if (!media.getFile_path().equals("")) {

                            File media_file = new File(media.getFile_path());

                            if (media_file.exists()) {

                                if (media.isSelf()) {

                                    switch (media.getMsg_type()) {
                                        case 1:
                                            Intent i = new Intent(mcontext, ImagePreviewActivity1.class);
                                            File imgFile = new File(media.getFile_path());
                                            i.putExtra("Imageurl", imgFile.getAbsolutePath());
                                            i.putExtra("ImageTimeStamp", media.getTimestamp());
                                            mcontext.startActivity(i);
                                            break;

                                        case 2:
                                            Intent intent = new Intent();
                                            intent.setAction(android.content.Intent.ACTION_VIEW);
                                            File f = new File(media.getFile_path());
                                            Uri video = FileProvider.getUriForFile(activity, "com.release.chikoopapp.celgram.provider", f);

                                            List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                            for (ResolveInfo resolveInfo : resInfoList) {
                                                String packageName = resolveInfo.activityInfo.packageName;
                                                activity.grantUriPermission(packageName, video, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            }

                                            intent.setDataAndType(video, "video/*");
                                            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                            activity.startActivity(intent);
                                            break;

                                        default:
                                            break;
                                    }

                                } else {

                                    switch (media.getMsg_type()) {
                                        case 1:
                                            Intent i = new Intent(mcontext, ImagePreviewActivity1.class);
                                            File imgFile = new File(media.getFile_path());
                                            i.putExtra("Imageurl", imgFile.getAbsolutePath());
                                            i.putExtra("ImageTimeStamp", media.getTimestamp());
                                            mcontext.startActivity(i);
                                            break;

                                        case 2:
                                            Intent intent = new Intent();
                                            intent.setAction(android.content.Intent.ACTION_VIEW);
                                            File f = new File(media.getFile_path());
                                            Uri video = FileProvider.getUriForFile(activity, "com.release.chikoopapp.celgram.provider", f);

                                            List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                            for (ResolveInfo resolveInfo : resInfoList) {
                                                String packageName = resolveInfo.activityInfo.packageName;
                                                activity.grantUriPermission(packageName, video, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            }

                                            intent.setDataAndType(video, "video/*");
                                            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                            activity.startActivity(intent);
                                            break;

                                        default:
                                            break;
                                    }

                                }
                            } else {
                                MaterialDialog dialog = new MaterialDialog.Builder(activity)
                                        .content("Sorry! the media does not exist on storage")
                                        .positiveText("OK")
                                        .onAny(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                switch (which) {
                                                    case POSITIVE:
                                                        break;
                                                    case NEGATIVE:
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        })
                                        .show();

                            }
                        }

                    }

                }
            }
        }
    };


    public View.OnClickListener cancelTransfer=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            view.setVisibility(View.GONE);
            int pos=(Integer)view.getTag();
            Message media=ChatWindow.getMessage(pos);
            boolean isPause=AwsUtils.getTransferUtility(mcontext).pause(media.getAws_id());
            if(!isPause){
                Log.i("CHECK_PAUSE","false");
            }
            else{
                Log.i("CHECK_PAUSE","true");

            }
            View v=(View) view.getParent();
            CircularProgressButton cpb=(CircularProgressButton)v.findViewById(R.id.btnWithText);
            cpb.setVisibility(View.VISIBLE);
            cpb.setAlpha(0.5f);
            cpb.setProgress(0);
            cpb.setClickable(true);

            if(!media.isSelf()){cpb.setIdleText("\u25BD  "+Utils.getMediaSize(media.getMedia_size()));}

        }
    };


    public int getPosition(String id){
        if(id_position.get(id)!=null){
            return id_position.get(id);
        }
        else{
            return -1;
        }
    }

    public int getSetPosition(String id,int progress){
        if(id_position.get(id)!=null){
            ((Message)listData.get(id_position.get(id))).setProgress(progress);
            return id_position.get(id);
        }
        else{
            return -1;
        }
    }

//    public void startDownload(Message message){
//        Intent downloadService=new Intent(activity.getApplicationContext(), AwsDownloadService.class);
//        downloadService.putExtra("message",message);
//        activity.getApplicationContext().startService(downloadService);
//    }

    public View.OnClickListener resumeTransfer=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View v=(View)((View)view.getParent()).getParent();
            final CircularProgressButton cpb=(CircularProgressButton)v.findViewById(R.id.btnWithText);
            final ImageView cancel=(ImageView)v.findViewById(R.id.cancel);
            cancel.setVisibility(View.VISIBLE);
            cancel.setClickable(true);
            cpb.setClickable(false);
            cpb.setIndeterminateProgressMode(true);
            cpb.setProgress(50);
            cpb.setAlpha(0.7f);

            int pos=(Integer)view.getTag();
            final Message message=ChatWindow.getMessage(pos);
            if(message.isSelf()){
                Intent uploadService=new Intent(mcontext, AwsUploadService.class);
                uploadService.putExtra("message",message);
                mcontext.startService(uploadService);

            }
            else{
                Intent downloadService=new Intent(mcontext, AwsDownloadService.class);
                downloadService.putExtra("message",message);
                mcontext.startService(downloadService);

            }
        }
    };

    View.OnClickListener playPause=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (notselected) {
                final Handler mHandler = new Handler();
                int pos = (Integer) view.getTag();
                Message media = (Message) listData.get(pos);

                if (!media.getFile_path().equals("")) {

                    if (new File(media.getFile_path()).exists()) {
                        View v = (View) view.getParent();

                        final SeekBar seekBar = (SeekBar) v.findViewById(R.id.seek);

                        if (!current_media.equals(media.getFile_path())) {
                            current_media = media.getFile_path();
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            try {
                                mediaPlayer.setDataSource(media.getFile_path());
                                mediaPlayer.prepare();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            seekBar.setMax(mediaPlayer.getDuration());
                            //seekBar.setClickable(false);
                        }

                        final Runnable UpdateSongTime = new Runnable() {
                            public void run() {
                                int curr;
                                curr = mediaPlayer.getCurrentPosition();
                                seekBar.setProgress(curr);
                                mHandler.postDelayed(this, 100);
                            }
                        };

                        if (mediaPlayer.isPlaying()) {
                            Log.i("ClickCheck", "Paused");
                            ((ImageView) view).setImageResource(R.drawable.ic_play_arrow_black_24dp);

                            mediaPlayer.pause();
                        } else {
                            Log.i("ClickCheck", "Playing");
                            ((ImageView) view).setImageResource(R.drawable.ic_pause_black_24dp);
                            int starttime = mediaPlayer.getCurrentPosition();
                            mediaPlayer.start();
                            seekBar.setProgress(starttime);
                            mHandler.postDelayed(UpdateSongTime, 100);

                        }
                    } else {
                        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                                .content("Sorry! the media does not exist on storage")
                                .positiveText("OK")
                                .onAny(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        switch (which) {
                                            case POSITIVE:
                                                break;
                                            case NEGATIVE:
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                })
                                .show();
                    }

                } else {
                    MaterialDialog dialog = new MaterialDialog.Builder(activity)
                            .content("Sorry! the media does not exist on storage")
                            .positiveText("OK")
                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    switch (which) {
                                        case POSITIVE:
                                            break;
                                        case NEGATIVE:
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .show();
                }
            }
        }
    };


    private void setTimeTextVisibility(long ts1, long ts2, TextView timeText) {
        String monthName="";
        if (ts2 == 0) {

            timeText.setVisibility(View.VISIBLE);
            //TIME IN MILLIS ONLY, NOT CONVERTED TO DATE.

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(ts1);


            try{

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
                //simpleDateFormat.setCalendar(calendar);
                monthName = simpleDateFormat.format(cal.getTime());

            } catch (Exception e) {
                if (e != null)
                    e.printStackTrace();
            }
            timeText.setText(monthName+"  "+cal.get(Calendar.DAY_OF_MONTH)+" , "+cal.get(Calendar.YEAR));

        } else {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTimeInMillis(ts1);
            cal2.setTimeInMillis(ts2);

            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

            if (sameDay) {
                timeText.setVisibility(View.GONE);
                timeText.setText("");
            } else {



                timeText.setVisibility(View.VISIBLE);
                //TIME IN MILLIS ONLY, NOT CONVERTED TO DATE.
                try{

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
                    //simpleDateFormat.setCalendar(calendar);
                    monthName = simpleDateFormat.format(cal1.getTime());

                } catch (Exception e) {
                    if (e != null)
                        e.printStackTrace();
                }

                timeText.setText(monthName+"  "+cal1.get(Calendar.DAY_OF_MONTH)+" , "+cal1.get(Calendar.YEAR));
            }
        }
    }

    View.OnClickListener showDoc= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (notselected) {
                int pos = (Integer) view.getTag();
                Message media = (Message) listData.get(pos);
                File doc = new File(media.getFile_path());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri photoURI = FileProvider.getUriForFile(activity, "com.rohan7055.earnchat.provider", doc);

                List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    activity.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                intent.setDataAndType(photoURI, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                activity.startActivity(intent);
            }
        }
    };

}