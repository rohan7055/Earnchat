package com.rohan7055.earnchat.celgram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.rohan7055.earnchat.R;
import com.vanniktech.emoji.EmojiTextView;

public class Message_info extends AppCompatActivity {

    EmojiTextView msg_data;
    TextView tv_timestamp;
    ImageView iv_msg_status;
    Message message_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);
        msg_data = (EmojiTextView)findViewById(R.id.message);
        tv_timestamp=(TextView)findViewById(R.id.timestamp);
        iv_msg_status = (ImageView)findViewById(R.id.msg_status);
        message_model =(Message)getIntent().getSerializableExtra("msg_info");
        String msg_data= message_model.getData();
        String time = message_model.getTimestamp();
        message_model.getMsg_status();

    }
}
