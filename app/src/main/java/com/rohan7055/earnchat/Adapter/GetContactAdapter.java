package com.rohan7055.earnchat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.graphics.Palette;
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

import com.rohan7055.earnchat.AppConstants;
import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.AddParticipants;
import com.rohan7055.earnchat.celgram.ChatWindow;
import com.rohan7055.earnchat.celgram.ContactsModel;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.rohan7055.earnchat.celgram.RoundedCornersTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by manu on 6/26/2016.
 */
public class GetContactAdapter extends RecyclerView.Adapter<GetContactAdapter.MyViewHolder> {

    private List<ContactsModel> listData;
    private Activity activity;
    private Context context;
    private int flag;
    ContactSQLiteHelper db_helper_contact;
    private static String URL;


    public GetContactAdapter(Context context, List<ContactsModel> list, Activity activity, int flag) {
        this.listData = list;
        this.activity = activity;
        this.context = context;
        this.flag = flag;
        db_helper_contact = new ContactSQLiteHelper(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (flag == 1 || flag == 3) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_contact_cell, parent, false);
        } else if (flag == 4) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_contacts_cell, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_contacts_cell, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Log.i("OnBindViewHolder", "=" + String.valueOf(flag));
        Spannable word, wordTwo;
        if (listData.get(position).getIsTyping()) {
            holder.contact_username.setTextColor(activity.getResources().getColor(R.color.accentColor));
            holder.contact_username.setText("typing");
        } else {
            holder.contact_username.setTextColor(activity.getResources().getColor(R.color.textColorSecondary));
            holder.contact_username.setText("".concat(listData.get(position).getUname()));
        }
        //comment temporary
        // Picasso.with(context).load(listData.get(position).getImageUrl()).into(holder.contact_image);
        if (flag == 0) {

            final ContactsModel contactsModel = listData.get(position);

            holder.main_layout.setTag(position);
            holder.main_layout.setOnClickListener(contactClickListener);
            holder.contact_name.setText(contactsModel.getDisplay_name());

            if (contactsModel.getUname().startsWith("Advertiser")){
                URL = AppConstants.UPLOADS_URL_ADVERTISERS;
            }
            else{
                URL = AppConstants.UPLOADS_URL_MEDIA;
            }

            ////
            if (db_helper_contact.checkLocalprofImageEmpty()) {
                Picasso.with(activity).load(URL + contactsModel.getImgname()).error(R.drawable.temp).transform(new RoundedCornersTransform()).into(holder.contact_image, new Callback() {
                    @Override
                    public void onSuccess() {

                        final Bitmap albumart = ((BitmapDrawable) holder.contact_image.getDrawable()).getBitmap();
                        if (albumart != null) {

                            Palette.from(albumart).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(final Palette palette) {

                                    try {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                File filePath = Environment.getExternalStorageDirectory();
                                                File dir = new File(filePath.getAbsolutePath() + "/8Chat/ProfileImage/" + db_helper_contact.getUserid(contactsModel.getId()));
                                                dir.mkdirs();
                                                File file = new File(dir, contactsModel.getImgname());
                                                try {
                                                    file.createNewFile();
                                                    FileOutputStream ostream = new FileOutputStream(file);
                                                    albumart.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                                                    ostream.flush();
                                                    ostream.close();
                                                } catch (IOException e) {
                                                    Log.e("IOException", e.getLocalizedMessage());
                                                }
                                            }
                                        }).start();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onError() {
                        holder.contact_image.setImageResource(R.drawable.temp);
                    }
                });
                db_helper_contact.addLocalImage(db_helper_contact.getUserid(contactsModel.getId()), contactsModel.getImgname());
                //saveImage(activity,contactsModel.getImgname(),contactsModel);
            } else {
                if (db_helper_contact.checkLocalUseridExist(db_helper_contact.getUserid(contactsModel.getId()))) {
                    Log.d("Anuraj_tag", "2");
                    String n = "";
                    String x = "";
                    String userid = null;
                    try {
                        userid = db_helper_contact.getUserid(contactsModel.getId());
                        n = db_helper_contact.getLocalImage(userid);
                        x = contactsModel.getImgname();
                    } catch (Exception e) {
                        Log.i("userid get", e.getMessage().toString());
                    }

                    if (!n.equals(x)) {
                        Log.d("Anuraj_tag", "3");
                        Log.d("Anuraj_tag", URL + contactsModel.getImgname());
                        Picasso.with(activity).load(URL + contactsModel.getImgname()).error(R.drawable.temp).transform(new RoundedCornersTransform()).into(holder.contact_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                final Bitmap albumart = ((BitmapDrawable) holder.contact_image.getDrawable()).getBitmap();
                                //Bitmap albumart = BitmapFactory.decodeResource(activity.getResources(),R.drawable.temp_icon);
                                if (albumart != null) {
                                    Palette.from(albumart).generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(final Palette palette) {
                                            try {
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        File filePath = Environment.getExternalStorageDirectory();
                                                        File dir = new File(filePath.getAbsolutePath() + "/8Chat/ProfileImage/" + db_helper_contact.getUserid(contactsModel.getId()));
                                                        dir.mkdirs();
                                                        File file = new File(dir, contactsModel.getImgname());
                                                        try {
                                                            file.createNewFile();
                                                            FileOutputStream ostream = new FileOutputStream(file);
                                                            albumart.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                                                            ostream.flush();
                                                            ostream.close();
                                                        } catch (IOException e) {
                                                            Log.e("IOException", e.getLocalizedMessage());
                                                        }
                                                    }
                                                }).start();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError() {
                                holder.contact_image.setImageResource(R.drawable.temp);
                            }
                        });
                        db_helper_contact.setLocalImage(userid, contactsModel.getImgname());
                        //saveImage(activity, contactsModel.getImgname(), contactsModel);
                    } else {
                        Log.d("Anuraj_tag", "4");
                        Picasso.with(activity).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/8Chat/ProfileImage/" + userid + "/" + contactsModel.getImgname())).transform(new RoundedCornersTransform()).into(holder.contact_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap albumart = ((BitmapDrawable) holder.contact_image.getDrawable()).getBitmap();
                                //Bitmap albumart = BitmapFactory.decodeResource(activity.getResources(),R.drawable.temp_icon);
                                if (albumart != null) {
                                    Palette.from(albumart).generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(final Palette palette) {

                                            /*try {
                                                int colorFrom = activity.getResources().getColor(R.color.cardview_light_background);
                                                int colorTo = palette.getDarkVibrantSwatch().getRgb();
                                                Log.d("COLOR", colorTo + "");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }*/
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError() {
                                holder.contact_image.setImageResource(R.drawable.temp);
                            }
                        });
                    }
                } else {
                    Log.d("Anuraj_tag", "5");
                    Log.d("Anuraj_tag", URL + contactsModel.getImgname());
                    Picasso.with(activity).load(URL + contactsModel.getImgname()).error(R.drawable.temp).transform(new RoundedCornersTransform()).into(holder.contact_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            final Bitmap albumart = ((BitmapDrawable) holder.contact_image.getDrawable()).getBitmap();
                            //Bitmap albumart = BitmapFactory.decodeResource(activity.getResources(),R.drawable.temp_icon);
                            if (albumart != null) {
                                Palette.from(albumart).generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(final Palette palette) {
                                        try {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    File filePath = Environment.getExternalStorageDirectory();
                                                    File dir = new File(filePath.getAbsolutePath() + "/8Chat/ProfileImage/" + db_helper_contact.getUserid(contactsModel.getId()));
                                                    dir.mkdirs();
                                                    File file = new File(dir, contactsModel.getImgname());
                                                    try {
                                                        file.createNewFile();
                                                        FileOutputStream ostream = new FileOutputStream(file);
                                                        albumart.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                                                        ostream.flush();
                                                        ostream.close();
                                                    } catch (IOException e) {
                                                        Log.e("IOException", e.getLocalizedMessage());
                                                    }
                                                }
                                            }).start();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError() {
                            holder.contact_image.setImageResource(R.drawable.temp);
                        }
                    });
                    db_helper_contact.addLocalImage(db_helper_contact.getUserid(contactsModel.getId()), contactsModel.getImgname());
                    //saveImage(activity, contactsModel.getImgname(), contactsModel);
                }
            }
        }

        ////

        else if (flag == 1) {
            holder.main_layout.setTag(position);
            holder.main_layout.setOnClickListener(searchClickListener);
            word = new SpannableString(listData.get(position).getDisplay_name().substring(0, AddParticipants.getNameSize()));
            word.setSpan(new ForegroundColorSpan(Color.parseColor("#1ad1ff")), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.contact_name.setText(word);

            wordTwo = new SpannableString(listData.get(position).getDisplay_name().substring(AddParticipants.getNameSize()));
            holder.contact_name.append(wordTwo);
            //holder.checkBox.setVisibility(View.GONE);
        }
        else if (flag == 2) {
//            holder.delete.setTag(position);
//            holder.delete.setOnClickListener(deleteClickListener);
//            holder.delete.setVisibility(View.VISIBLE);
            holder.contact_name.setText(listData.get(position).getDisplay_name());
        }
        else {

            holder.checkBox.setTag(position);
            holder.checkBox.setChecked(listData.get(position).getChecked());

            holder.checkBox.setOnClickListener(onCheckClick);


            word = new SpannableString(listData.get(position).getDisplay_name().substring(0, AddParticipants.getNameSize()));
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
            Intent i=new Intent(context, ChatWindow.class);
            i.putExtra("isContact",true);
            i.putExtra("contact",listData.get(position));
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

        public static void saveImage(Context ctx ,final String imagename,ContactsModel contactsModel)
        {
            Log.d("Anuraj_tag", "save image");
            ContactSQLiteHelper db_contact = new ContactSQLiteHelper(ctx);
            Picasso.with(ctx).load(URL+contactsModel.getImgname()).into(getTarget(imagename,contactsModel,db_contact));
        }

        //target to save
        private static com.squareup.picasso.Target getTarget(final String url, final ContactsModel contactsModel, final ContactSQLiteHelper db_helper_contact){
            com.squareup.picasso.Target target = new com.squareup.picasso.Target(){

                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                            Log.d("Anuraj_tag", "getTargetRun");

                            File filePath = Environment.getExternalStorageDirectory();
                            File dir = new File(filePath.getAbsolutePath()+"/8Chat/ProfileImage/"+db_helper_contact.getUserid(contactsModel.getId()));
                            dir.mkdirs();
                            File file = new File(dir,url);

                            try {
                                Log.d("Anuraj_tag", "image saved");
                                file.createNewFile();
                                FileOutputStream ostream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                                ostream.flush();
                                ostream.close();
                            } catch (IOException e) {
                                Log.e("IOException", e.getLocalizedMessage());
                            }
                        }
                    }).start();

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.d("Anuraj_tag", "getTargetFailed");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.d("Anuraj_tag", "getTargetLoad");
                }
            };
            return target;
        }

}

