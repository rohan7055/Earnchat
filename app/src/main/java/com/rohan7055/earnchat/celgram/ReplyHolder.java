package com.rohan7055.earnchat.celgram;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rohan7055.earnchat.R;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lenovo on 9/6/2017.
 */

public class ReplyHolder extends RecyclerView.ViewHolder {

    private TextView sender,timestamp,day;
    private EmojiTextView message,celgram_name;
    private ImageView msg_status;
    private CircleImageView sender_image;
    private LinearLayout linearLayout;

    private ImageView replyImage;
    private TextView replyText;
    private TextView replyingTo;

    public ReplyHolder(View v) {
        super(v);

        message = (EmojiTextView) v.findViewById(R.id.message);
        celgram_name=(EmojiTextView)v.findViewById(R.id.celgram_name);
        sender = (TextView) v.findViewById(R.id.sender_name);
        timestamp= (TextView) v.findViewById(R.id.timestamp);
        day=(TextView)v.findViewById(R.id.day);
        msg_status=(ImageView)v.findViewById(R.id.msg_status);
        sender_image=(CircleImageView) v.findViewById(R.id.sender_image);

        linearLayout =(LinearLayout)v.findViewById(R.id.replyLay);
        replyImage=(ImageView)v.findViewById(R.id.reply_image);
        replyText=(TextView)v.findViewById(R.id.replyToText);
        replyingTo=(TextView)v.findViewById(R.id.reply_sender);
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

    public ImageView getMsg_status() {
        return msg_status;
    }

    public CircleImageView getSender_image() {
        return sender_image;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public ImageView getReplyImage() {
        return replyImage;
    }

    public TextView getReplyText() {
        return replyText;
    }

    public TextView getReplyingTo() {
        return replyingTo;
    }
}
