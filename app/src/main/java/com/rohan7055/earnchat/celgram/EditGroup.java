package com.rohan7055.earnchat.celgram;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rohan7055.earnchat.UserHelper;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class EditGroup extends AppCompatActivity {

    TextView activity_name,subject_length;
    EditText group_subject;
    ImageView group_icon;
    Button done;
    String new_filepath,new_subject;
    GroupsModel groupsModel;
    EventBus evetBus;
    UserHelper userHelper;

    ContactSQLiteHelper contacts_db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        groupsModel = (GroupsModel)getIntent().getSerializableExtra("getgroup");
        activity_name=(TextView)findViewById(R.id.heading_txt);
        subject_length=(TextView)findViewById(R.id.new_subject_length);
        group_subject=(EditText)findViewById(R.id.new_group_subject);
        group_icon=(ImageView)findViewById(R.id.new_group_icon);
        done=(Button)findViewById(R.id.new_button_done);
        done.setOnClickListener(onDone);
        activity_name.setText("Change Group Subject");
        group_subject.setText("");


        //Picasso.with(getApplicationContext()).load(new File(getIntent().getStringExtra("dp_filepath"))).resize(100,100).into(group_icon);
        //Glide.with(getApplicationContext()).load(new File(groupsModel.getDp_url())).error(R.drawable.group_icon).override(100,100).into(group_icon);

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

    View.OnClickListener onDone=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent returnIntent = new Intent();
            returnIntent.putExtra("new_subject",group_subject.getText().toString());
            setResult(Activity.RESULT_OK,returnIntent);
            group_subject.setText("");

            /*ChangeGroupDpEvent event = new ChangeGroupDpEvent(groupsModel);
            evetBus.post(event);*/
            finish();
        }
    };

    public void changeGroupIcon(View v)
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
                    new_filepath = cursor.getString(columnIndex);
                    cursor.close();
                    Picasso.with(getApplicationContext()).load(new File(new_filepath)).resize(100,100).into(group_icon);
                }
        }

    }

}
