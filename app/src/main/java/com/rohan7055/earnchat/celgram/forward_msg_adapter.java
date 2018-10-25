package com.rohan7055.earnchat.celgram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anuraj on 7/20/2017.
 */

public class forward_msg_adapter extends RecyclerView.Adapter<forward_msg_adapter.MyViewHolder> {

    private List<ContactsModel> listData;
    private Activity activity;
    private Context context;
    private int flag;



    public forward_msg_adapter(Context context,List<ContactsModel> list, Activity activity,int flag) {
        this.listData = list;
        this.activity = activity;
        this.context=context;
        this.flag=flag;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(flag==1||flag==3){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_contact_cell, parent, false);
        }
        else if(flag==4)
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_contacts_cell, parent, false);
        }
        else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_contacts_cell, parent, false);}

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Log.i("OnBindViewHolder","="+String.valueOf(flag));
        Spannable word,wordTwo;
        if(listData.get(position).getIsTyping()){
            holder.contact_username.setTextColor(activity.getResources().getColor(R.color.accentColor));
            holder.contact_username.setText("typing");}
        else{
            holder.contact_username.setTextColor(activity.getResources().getColor(R.color.textColorSecondary));
            holder.contact_username.setText("@".concat(listData.get(position).getUname()));
        }
        //comment temporary
        // Picasso.with(context).load(listData.get(position).getImageUrl()).into(holder.contact_image);
        if(flag==0) {
            holder.main_layout.setTag(position);
            holder.main_layout.setOnClickListener(contactClickListener);
            holder.contact_name.setText(listData.get(position).getDisplay_name());
        }

        else if(flag==1){
            holder.main_layout.setTag(position);
            holder.main_layout.setOnClickListener(searchClickListener);
            word = new SpannableString(listData.get(position).getDisplay_name().substring(0,AddParticipants.getNameSize()));
            word.setSpan(new ForegroundColorSpan(Color.parseColor("#1ad1ff")), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.contact_name.setText(word);

            wordTwo = new SpannableString(listData.get(position).getDisplay_name().substring(AddParticipants.getNameSize()));
            holder.contact_name.append(wordTwo);
            //holder.checkBox.setVisibility(View.GONE);


        }

        else if(flag==2){
//            holder.delete.setTag(position);
//            holder.delete.setOnClickListener(deleteClickListener);
//            holder.delete.setVisibility(View.VISIBLE);
            holder.contact_name.setText(listData.get(position).getDisplay_name());
        }


        else{

            holder.checkBox.setTag(position);
            holder.checkBox.setChecked(listData.get(position).getChecked());

            holder.checkBox.setOnClickListener(onCheckClick);


            word = new SpannableString(listData.get(position).getDisplay_name().substring(0,AddParticipants.getNameSize()));
            word.setSpan(new ForegroundColorSpan(Color.parseColor("#1ad1ff")), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.contact_name.setText(word);

            wordTwo = new SpannableString(listData.get(position).getDisplay_name().substring(AddParticipants.getNameSize()));
            holder.contact_name.append(wordTwo);

        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    private View.OnClickListener contactClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();

            Forward_msg obj = new Forward_msg();
            ArrayList<Message> msgs = obj.addMessages(listData.get(position));

            Log.d("anuraj_contact_id", listData.get(position).getId());

            Intent i=new Intent(context, ChatWindow.class);
            i.putExtra("isContact",true);
            i.putExtra("contact",listData.get(position));
            i.putExtra("frwrd", true);
            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",(Serializable)msgs);
            i.putExtra("BUNDLE",args);
            CelgramMain.current_chat=listData.get(position).getId();
            context.startActivity(i);
            activity.finish();
        }
    };
    private View.OnClickListener contactClickListenerMagic = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Intent i=new Intent(context, ChatWindow.class);
            i.putExtra("isContactMagic",true);
            i.putExtra("contactMagic",listData.get(position));
            CelgramMain.current_chat=listData.get(position).getId();
            context.startActivity(i);
        }
    };

    private View.OnClickListener searchClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            if (AddParticipants.getListSize()<4) {
                int position = (Integer) v.getTag();
                Log.i("TAGG_SEARCHCLICK","="+String.valueOf(position));
                AddParticipants.participantAdded(listData.get(position));
            }
            else{
                Toast.makeText(context,"No More Participants Allowed!!",Toast.LENGTH_LONG).show();

            }
        }
    };

    private View.OnClickListener deleteClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Log.i("TAGG_DELETECLICK","="+String.valueOf(position));
            AddParticipants.participantRemoved(listData.get(position));

        }
    };

    private View.OnClickListener onCheckClick=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Log.i("TAGG_CHECKCLICK","="+String.valueOf(position));

            Log.i("IS_CHECKED","="+String.valueOf(listData.get(position).getChecked()));
            if (!(listData.get(position).getChecked())) {
                if(AddParticipants.getListSize()<5) {
                    AddParticipants.participantAdded(listData.get(position));
                    listData.get(position).setChecked(true);
                }
                else{

                    Toast.makeText(context,"No More Participants Allowed!!",Toast.LENGTH_LONG).show();
                    CheckBox checkBox=(CheckBox) v.findViewById(R.id.checkBox);
                    checkBox.setChecked(listData.get(position).getChecked());

                }
            } else {
                AddParticipants.participantRemoved(listData.get(position));
                listData.get(position).setChecked(false);
            }

        }
    };

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView contact_name,contact_username;
        public ImageView contact_image,delete;
        LinearLayout contact_layout;
        RelativeLayout main_layout;
        CheckBox checkBox;


        MyViewHolder(View view) {
            super(view);
            contact_image=(ImageView)view.findViewById(R.id.contact_image);
            contact_name=(TextView)view.findViewById(R.id.contact_name3);
            contact_username=(TextView)view.findViewById(R.id.contact_username);
            contact_layout=(LinearLayout)view.findViewById(R.id.contact_layout);
//            delete=(ImageView)view.findViewById(R.id.delete_participant );
            main_layout=(RelativeLayout)view.findViewById(R.id.main_layout);
            checkBox=(CheckBox)view.findViewById(R.id.checkBox);
        }
    }
}
