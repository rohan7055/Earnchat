package com.rohan7055.earnchat.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.AddMember;
import com.rohan7055.earnchat.celgram.AddSingleMember;
import com.rohan7055.earnchat.celgram.ContactsModel;

import java.util.List;

/**
 * Created by manu on 10/25/2016.
 */
public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.MyViewHolder> {

    private List<ContactsModel> listData;
    private Activity activity;
    private int flag;

    public AddMemberAdapter(List<ContactsModel> list, Activity activity, int flag) {
        this.listData = list;
        this.activity = activity;
        this.flag = flag;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (flag == 0) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.added_member, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_contacts_cell, parent, false);
        }

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(AddMemberAdapter.MyViewHolder holder, int position) {
        if (flag == 0) {
            holder.contact_name.setText(listData.get(position).getDisplay_name());
            holder.main_layout.setTag(listData.get(position).getId());
            holder.main_layout.setOnClickListener(deleteMember);
        } else if (flag == 1 || flag == 3) {
            /*holder.contact_name.setText(listData.get(position).getDisplay_name());
            if (listData.get(position).getGroup_member()) {
                holder.main_layout.setBackgroundColor(activity.getResources().getColor(R.color.selectedContact));
                holder.username.setTypeface(null, Typeface.ITALIC);
                holder.username.setText("Already a group member");
            } else {
                holder.username.setText(listData.get(position).getUname());
                holder.main_layout.setBackgroundColor(activity.getResources().getColor(R.color.background_theme_color));
                holder.main_layout.setTag(listData.get(position).getId());
                holder.main_layout.setOnClickListener(addSingleMember);
                *//*if (listData.get(position).getChecked()) {
                    holder.main_layout.setBackgroundColor(activity.getResources().getColor(R.color.selectedContact));
                    holder.main_layout.setTag(listData.get(position).getId());
                    holder.main_layout.setOnClickListener(deleteMember);
                } else {
                    holder.main_layout.setBackgroundColor(activity.getResources().getColor(R.color.background_theme_color));
                    holder.main_layout.setTag(listData.get(position).getId());
                    if (flag == 1) {
                        holder.main_layout.setOnClickListener(addMember);
                    } else {
                        holder.main_layout.setOnClickListener(addSingleMember);
                    }
                }*//*
            }*/
            holder.contact_name.setText(listData.get(position).getDisplay_name());

            if (listData.get(position).getGroup_member()) {
                holder.main_layout.setBackgroundColor(activity.getResources().getColor(R.color.selectedContact));
                holder.username.setTypeface(null, Typeface.ITALIC);
                holder.username.setText("Already a group member");
            }
            else{
                holder.username.setText(listData.get(position).getUname());
                if (listData.get(position).getChecked()) {
                    holder.main_layout.setBackgroundColor(activity.getResources().getColor(R.color.selectedContact));
                    holder.main_layout.setTag(listData.get(position).getId());
                    holder.main_layout.setOnClickListener(deleteMember);
                } else {
                    holder.main_layout.setBackgroundColor(activity.getResources().getColor(R.color.background_theme_color));
                    holder.main_layout.setTag(listData.get(position).getId());
                    if(flag==1){
                        holder.main_layout.setOnClickListener(addMember);
                    }
                    else{
                        holder.main_layout.setOnClickListener(addSingleMember);
                    }
                }
            }
        }

        else if (flag == 2 || flag == 4) {
            int end_index;
            if (flag == 2) {
                end_index = new AddMember().getNameSize();
            } else {
                end_index = new AddSingleMember().getName_size();
            }
            Spannable word = new SpannableString(listData.get(position).getDisplay_name().substring(0, end_index));
            word.setSpan(new ForegroundColorSpan(Color.parseColor("#1ad1ff")), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.contact_name.setText(word);

            Spannable wordTwo = new SpannableString(listData.get(position).getDisplay_name().substring(end_index));
            holder.contact_name.append(wordTwo);

            if (listData.get(position).getGroup_member()) {
                holder.main_layout.setBackgroundColor(activity.getResources().getColor(R.color.selectedContact));
                holder.username.setTypeface(null, Typeface.ITALIC);
                holder.username.setText("Already a group member");
            } else {
                holder.username.setText(listData.get(position).getUname());

                if (listData.get(position).getChecked()) {
                    holder.main_layout.setBackgroundColor(activity.getResources().getColor(R.color.selectedContact));
                    holder.main_layout.setTag(listData.get(position).getId());
                    holder.main_layout.setOnClickListener(deleteMember);
                } else {
                    holder.main_layout.setBackgroundColor(activity.getResources().getColor(R.color.background_theme_color));
                    holder.main_layout.setTag(listData.get(position).getId());
                    if (flag == 2) {
                        holder.main_layout.setOnClickListener(addMember);
                    } else {
                        holder.main_layout.setOnClickListener(addSingleMember);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView contact_name, username;
        private RelativeLayout main_layout;
        private ImageView contact_image;


        public MyViewHolder(View view) {
            super(view);
            contact_name = (TextView) view.findViewById(R.id.contact_name3);
            username = (TextView) view.findViewById(R.id.contact_username);
            main_layout = (RelativeLayout) view.findViewById(R.id.main_layout);
            contact_image = (ImageView) view.findViewById(R.id.contact_image);
        }
    }

    View.OnClickListener addMember = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (new AddMember().getParticipantsCount() < 100) {
                new AddMember().addMember((String) v.getTag());
            } else {
                Toast toast = Toast.makeText(activity, "No more participants allowed!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    };

    View.OnClickListener deleteMember = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AddMember().deleteMember((String) v.getTag());
        }
    };

    View.OnClickListener addSingleMember = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            ContactSQLiteHelper contactSQLiteHelper = new ContactSQLiteHelper(activity);
            MaterialDialog dialog = new MaterialDialog.Builder(activity)
                    .content("Do you want to add " + contactSQLiteHelper.getContact((String) v.getTag()).getDisplay_name() + "?")
                    .positiveText("Yes")
                    .negativeText("No")
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            switch (which) {
                                case POSITIVE:
                                    new AddSingleMember().addMember(activity, (String) v.getTag());
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
    };
}

