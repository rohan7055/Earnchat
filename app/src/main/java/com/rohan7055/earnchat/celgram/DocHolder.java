package com.rohan7055.earnchat.celgram;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.rohan7055.earnchat.R;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;


public class DocHolder extends RecyclerView.ViewHolder {

        private TextView sender,timestamp,day,doc_name;
        private EmojiTextView celgram_name;
        private CircleImageView sender_image;
        private ImageView msg_status,cancel,doc_image;
        private CircularProgressButton cpb;
        private RelativeLayout relativeLayout;

        public DocHolder(View view)
        {
            super(view);
             sender=(TextView)view.findViewById(R.id.sender_name);
             timestamp=(TextView)view.findViewById(R.id.timestamp);
             day=(TextView)view.findViewById(R.id.day);
             celgram_name=(EmojiTextView)view.findViewById(R.id.celgram_name);
             sender_image=(CircleImageView) view.findViewById(R.id.sender_image);
            msg_status=(ImageView)view.findViewById(R.id.msg_status);
            doc_name=(TextView)view.findViewById(R.id.doc_name);
            cancel=(ImageView)view.findViewById(R.id.cancel);
            cpb=(CircularProgressButton)view.findViewById(R.id.btnWithText);
            doc_image=(ImageView)view.findViewById(R.id.doc_img);
            relativeLayout = (RelativeLayout)view.findViewById(R.id.layout11);
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

    public TextView getDoc_name() {
        return doc_name;
    }

    public EmojiTextView getCelgram_name() {
        return celgram_name;
    }

    public CircleImageView getSender_image() {
        return sender_image;
    }

    public ImageView getMsg_status() {
        return msg_status;
    }

    public ImageView getCancel() {
        return cancel;
    }

    public CircularProgressButton getCpb() {
        return cpb;
    }

    public ImageView getDoc_image() {
        return doc_image;
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }
}


