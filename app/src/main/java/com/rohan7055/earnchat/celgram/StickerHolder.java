package com.rohan7055.earnchat.celgram;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rohan7055.earnchat.R;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by manu on 12/3/2016.
 */

public class StickerHolder extends RecyclerView.ViewHolder {
    private ImageView sticker,msg_status;
    private CircleImageView sender_image;
    private EmojiTextView celgram_name;
    private TextView sender,timestamp,day;

    public StickerHolder(View v) {
        super(v);
        sticker = (ImageView) v.findViewById(R.id.sticker);
        sender = (TextView) v.findViewById(R.id.sender_name);
        timestamp = (TextView) v.findViewById(R.id.timestamp);
        day=(TextView)v.findViewById(R.id.day);
        celgram_name=(EmojiTextView)v.findViewById(R.id.celgram_name);
        msg_status=(ImageView)v.findViewById(R.id.msg_status);
        sender_image=(CircleImageView) v.findViewById(R.id.sender_image);
    }

    public ImageView getSticker() {
        return sticker;
    }

    public ImageView getMsg_status() {
        return msg_status;
    }

    public CircleImageView getSender_image() {
        return sender_image;
    }

    public EmojiTextView getCelgram_name() {
        return celgram_name;
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
}
