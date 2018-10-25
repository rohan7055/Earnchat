package com.rohan7055.earnchat.celgram;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.rohan7055.earnchat.R;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by manu on 7/24/2016.
 */
public class MediaHolder extends RecyclerView.ViewHolder {

    private TextView sender, timestamp, day;
    private EmojiTextView message,celgram_name;
    private ImageView media,cancel,msg_status,play_video;
    private CircularProgressButton circularProgressButton;
    private CircleImageView sender_image;

    public MediaHolder(View v) {
        super(v);
        sender = (TextView) v.findViewById(R.id.sender_name);
        timestamp = (TextView) v.findViewById(R.id.timestamp);
        day= (TextView)v.findViewById(R.id.day);
        message=(EmojiTextView) v.findViewById(R.id.message);
        media= (ImageView)v.findViewById(R.id.sticker);
        circularProgressButton=(CircularProgressButton)v.findViewById(R.id.btnWithText);
        cancel=(ImageView)v.findViewById(R.id.cancel);
        celgram_name=(EmojiTextView) v.findViewById(R.id.celgram_name);
        msg_status=(ImageView)v.findViewById(R.id.msg_status);
        sender_image=(CircleImageView) v.findViewById(R.id.sender_image);
        play_video=(ImageView)v.findViewById(R.id.play_video);
    }

    public TextView getSender() {
        return sender;
    }

    public TextView getTimestamp() {
        return timestamp;
    }

    public TextView getDay() {
        return day;
    }

    public EmojiTextView getMessage() {
        return message;
    }

    public EmojiTextView getCelgram_name() {
        return celgram_name;
    }

    public ImageView getMedia() {
        return media;
    }

    public ImageView getCancel() {
        return cancel;
    }

    public ImageView getMsg_status() {
        return msg_status;
    }

    public CircularProgressButton getCircularProgressButton() {
        return circularProgressButton;
    }

    public CircleImageView getSender_image() {
        return sender_image;
    }

    public ImageView getPlay_video() {
        return play_video;
    }
}
