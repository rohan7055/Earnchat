package com.rohan7055.earnchat.celgram;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.CelgramApi.RestClientGroup;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroup extends AppCompatActivity {

    TextView activity_title,subject_length;
    CircleImageView group_icon;
    EmojiEditText group_subject;
    private String file_path = "";
    public static Activity activity;
    EmojiPopup emojiPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        activity=this;

        activity_title=(TextView)findViewById(R.id.heading_txt);
        activity_title.setText("Create New Group");

        group_icon=(CircleImageView)findViewById(R.id.new_group_icon);
        group_subject=(EmojiEditText)findViewById(R.id.new_group_subject);
        subject_length=(TextView)findViewById(R.id.new_subject_length);
        subject_length.setText("25");

        ImageView back = (ImageView) findViewById(R.id.back_img_group);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rootView);

        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                if (emojiPopup.isShowing()) {
                    emojiPopup.dismiss();
                }
            }
        }).build(group_subject);

        ImageView emo_icon = (ImageView) findViewById(R.id.emo);
        emo_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
            }
        });

        group_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emojiPopup.isShowing()) {
                    emojiPopup.dismiss();
                }
            }
        });

        group_subject.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!group_subject.getText().toString().equalsIgnoreCase("")){

                    Integer length=25-group_subject.length();
                    subject_length.setText(length.toString());

                }
                else{
                    subject_length.setText("25");
                }
            }
        });
    }

    public void next(View v)
    {
        if(group_subject.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"Group name can't be blank",Toast.LENGTH_LONG).show();
            group_subject.requestFocus();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), AddMember.class);
            intent.putExtra("file_path", file_path);
            intent.putExtra("group_subject", group_subject.getText().toString());

            startActivity(intent);
        }

    }

    public void addGroupIcon(View v)
    {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        final int ACTIVITY_SELECT_IMAGE = 1234;
        startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1234:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    file_path = cursor.getString(columnIndex);
                    cursor.close();
                    TextView txt = (TextView)findViewById(R.id.uploadText);
                    txt.setVisibility(View.INVISIBLE);
                    Picasso.with(getApplicationContext()).load(new File(file_path)).resize(100,100).into(group_icon);
                    //GroupInfo groupInfo = new GroupInfo();
                    UploadDpPost(file_path);
                }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CelgramMain.class));
        finish();
    }

    public void UploadDpPost(final String filePath) {
        File f = new File(filePath);
        Bitmap bm = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        String title = f.getName();

        RestClientGroup.ApiGroupInterface service = RestClientGroup.getClient();
        Call<group_dp_model> call = service.uploadDP(title, encodedImage);
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Uploading... Please wait...", true);
        call.enqueue(new Callback<group_dp_model>() {
            @Override
            public void onResponse(Call<group_dp_model> call, Response<group_dp_model> response) {
                group_dp_model groupDpUpload = response.body();
                try {
                    if (groupDpUpload.isStatus()) {
                        file_path = groupDpUpload.getImageName();
                        Log.d("Anuraj_upload", file_path);
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "uploaded successfully", Toast.LENGTH_LONG).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dialog.dismiss();
                    file_path = "";
                    Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<group_dp_model> call, Throwable t) {
                dialog.dismiss();
                file_path = "";
            }
        });
    }
}