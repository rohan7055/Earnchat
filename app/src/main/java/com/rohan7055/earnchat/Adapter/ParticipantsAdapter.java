package com.rohan7055.earnchat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rohan7055.earnchat.AppConstants;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.ContactsModel;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by manu on 10/19/2016.
 */
public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.MyViewHolder>{

    LayoutInflater inflater;
    List<ContactsModel> list=new ArrayList<>();
    Context context;
    boolean selfAdmin;
    Activity activity;
    ContactSQLiteHelper contacts_db;
    OnMemberItemClickListener onMemberItemClickListener;
    String admin;
    String userid;
    String groupId;

    public ParticipantsAdapter(Context context, Activity activity, List<ContactsModel> list, Boolean selfAdmin,String admin,String groupId){

        this.list=list;
        inflater=LayoutInflater.from(context);
        this.context=context;
        this.selfAdmin=selfAdmin;
        this.activity=activity;
        this.admin=admin;
        contacts_db=new ContactSQLiteHelper(context);
        SharedPreferences preferences = activity.getSharedPreferences("USER", MODE_PRIVATE);
        this.userid = preferences.getString("id", "");
        this.groupId=groupId;

    }

    public void setOnMemberItemClickListener(OnMemberItemClickListener onMemberItemClickListener) {
        this.onMemberItemClickListener = onMemberItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.my_contacts_cell,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ContactsModel participant=list.get(position);

        /*if(CelgramMain.number_name.get(participant.getId())!=null){
            holder.contact_name.setText(participant.getDisplay_name());
        }
        else{
            holder.contact_name.setText(participant.getId());
//            holder.chikoop_name.setVisibility(View.VISIBLE);
//            holder.chikoop_name.setText(participant.getChikoop_name());
        }*/

        if (participant.getUname() == null || participant.getUname().isEmpty()){
            holder.contact_name.setText(participant.getId());
        }
        else{
            holder.contact_name.setText(participant.getDisplay_name());
            holder.contact_username.setText(participant.getUname());
            String URL;
            if ((list.get(position)).getUname().startsWith("Advertiser")) {
                URL = AppConstants.UPLOADS_URL_ADVERTISERS;
            } else {
                URL = AppConstants.UPLOADS_URL_MEDIA;
            }
            Picasso.with(context).load(URL + list.get(position).getImgname()).error(R.drawable.temp).centerCrop().fit().into(holder.contact_image);
        }

        //Log.i("Participant Adapter",AppConstants.UPLOADS_URL_ADVERTISERS+contacts_db.getUserid(participant.getId())+"/"+participant.getImgname());
        holder.contact_layout.setTag(position);
        //holder.contact_layout.setOnLongClickListener(onLongClick);
    }

    View.OnLongClickListener onLongClick=new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Integer position=(Integer) v.getTag();
            String name=list.get(position).getDisplay_name();
            final String member=list.get(position).getId();
            ArrayList<String> menu_items=new ArrayList<>();

            menu_items.add("Message "+name);
            menu_items.add("Call "+name);

            if(list.get(position).getContact_type()==2&&selfAdmin){
                menu_items.add("Add to contacts");
                menu_items.add("Remove "+name);

                new MaterialDialog.Builder(activity)
                        .items(menu_items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which){
                                    case 0:
                                        Toast.makeText(context,"Message",Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        Toast.makeText(context,"Call",Toast.LENGTH_SHORT).show();
                                        break;
                                    case 2:
                                        Toast.makeText(context,"Add to contacts",Toast.LENGTH_SHORT).show();
                                        break;
                                    case 3:
                                        Toast.makeText(context,"Remove",Toast.LENGTH_SHORT).show();
                                        onMemberItemClickListener.onRemoveItem(member,groupId);
                                        break;
                                    default:break;

                                }
                            }
                        })
                        .show();
            }

            else if(list.get(position).getContact_type()==2){
                menu_items.add("Add to contacts");
                new MaterialDialog.Builder(activity)
                        .items(menu_items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which){
                                    case 0:
                                        Toast.makeText(context,"Message",Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        Toast.makeText(context,"Call",Toast.LENGTH_SHORT).show();
                                        break;
                                    case 2:
                                        Toast.makeText(context,"Add to contacts",Toast.LENGTH_SHORT).show();
                                        break;
                                    default:break;

                                }
                            }
                        })
                        .show();
            }
            else if(selfAdmin){
                menu_items.add("Remove "+name);
                new MaterialDialog.Builder(activity)
                        .items(menu_items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which){
                                    case 0:
                                        Toast.makeText(context,"Message",Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        Toast.makeText(context,"Call",Toast.LENGTH_SHORT).show();
                                        break;
                                    case 2:
                                        Toast.makeText(context,"Remove",Toast.LENGTH_SHORT).show();
                                        onMemberItemClickListener.onRemoveItem(member,groupId);
                                        break;
                                    default:break;

                                }
                            }
                        })
                        .show();
            }
            else{
                new MaterialDialog.Builder(activity)
                        .items(menu_items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which){
                                    case 0:
                                        Toast.makeText(context,"Message",Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        Toast.makeText(context,"Call",Toast.LENGTH_SHORT).show();
                                        break;
                                    default:break;

                                }
                            }
                        })
                        .show();
            }


            return true;
        }
    };


    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView contact_name,contact_username,chikoop_name;
        public CircleImageView contact_image;
        LinearLayout contact_layout;



        public MyViewHolder(View view) {
            super(view);
            contact_image=(CircleImageView)view.findViewById(R.id.contact_image);
            contact_name=(TextView)view.findViewById(R.id.contact_name3);
            contact_username=(TextView)view.findViewById(R.id.contact_username);
            contact_layout=(LinearLayout)view.findViewById(R.id.contact_layout);
//           chikoop_name=(TextView)view.findViewById(R.id.chikoop_name);
        }
    }

    public interface OnMemberItemClickListener {


        void onRemoveItem(String member,String groupId);
    }
}

