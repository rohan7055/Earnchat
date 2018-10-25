package com.rohan7055.earnchat.celgram;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.rohan7055.earnchat.R;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Snehil Verma on 11/8/2016.
 */
public class AudioHolder extends RecyclerView.ViewHolder {

        private TextView duration,timestamp,sender,day;
        private EmojiTextView celgram_name;
        private ImageView play_pause,cancel,msg_status;
        private CircleImageView sender_image;
        private SeekBar seekBar;
        private RelativeLayout pp_relative_layout,head_layout;
        private CircularProgressButton cpb;

        public AudioHolder(View view) {
            super(view);
            sender=(TextView)view.findViewById(R.id.sender_name);
            celgram_name=(EmojiTextView)view.findViewById(R.id.celgram_name);
            day=(TextView)view.findViewById(R.id.day);
            duration = (TextView) view.findViewById(R.id.duration);
            play_pause = (ImageView) view.findViewById(R.id.playpause);
            seekBar = (SeekBar) view.findViewById(R.id.seek);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            pp_relative_layout = (RelativeLayout) view.findViewById(R.id.pp_relative_layout);
            head_layout = (RelativeLayout) view.findViewById(R.id.head_layout);
            cancel = (ImageView) view.findViewById(R.id.cancel);
            cpb = (CircularProgressButton) view.findViewById(R.id.btnWithText);
            sender_image=(CircleImageView) view.findViewById(R.id.sender_image);
            msg_status=(ImageView)view.findViewById(R.id.msg_status);
        }

    public TextView getDuration() {
        return duration;
    }

    public TextView getTimestamp() {
        return timestamp;
    }

    public TextView getSender() {
        return sender;
    }

    public TextView getDay() {
        return day;
    }

    public EmojiTextView getCelgram_name() {
        return celgram_name;
    }

    public ImageView getPlay_pause() {
        return play_pause;
    }

    public ImageView getCancel() {
        return cancel;
    }

    public CircleImageView getSender_image() {
        return sender_image;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public RelativeLayout getPp_relative_layout() {
        return pp_relative_layout;
    }

    public RelativeLayout getHead_layout() {
        return head_layout;
    }

    public CircularProgressButton getCpb() {
        return cpb;
    }

    public ImageView getMsg_status() {
        return msg_status;
    }
}



