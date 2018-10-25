package com.rohan7055.earnchat.celgram;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.AppConstants;
import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.RegisterActivity;
import com.rohan7055.earnchat.UserHelper;
import com.rohan7055.earnchat.celgram.CelgramApi.RestClientS3Group;
import com.rohan7055.earnchat.celgram.ResponseModels.RegisterResponse;
import com.rohan7055.earnchat.celgram.events.ChangeLastSeenAvail;
import com.rohan7055.earnchat.celgram.events.DisconnectEvent;
import com.rohan7055.earnchat.celgram.events.OnDestroyEvent;
import com.rohan7055.earnchat.celgram.service.MessageReceive;
import com.rohan7055.earnchat.otpmodule.OTPLoginActivity;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    private Button btnLogout;
    private CircleImageView ivProfile;
    private TextView tvFullname,tvMobile;
    private RelativeLayout rlSetProfile,rlAccount,rlNotification;
    private String file_path="";
    private ProgressDialog dialog;
    private ImageView ivBack;
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        btnLogout=(Button)findViewById(R.id.btnLogout);
        ivProfile=(CircleImageView)findViewById(R.id.profilePage_profilePic);
        tvFullname=(TextView)findViewById(R.id.tvFullname);
        tvMobile=(TextView)findViewById(R.id.tvMobile);
        rlSetProfile=(RelativeLayout)findViewById(R.id.rlsetProfile);
        rlAccount=(RelativeLayout)findViewById(R.id.rlAccount);
        rlNotification=(RelativeLayout)findViewById(R.id.rlNotify);
        ivBack=(ImageView)findViewById(R.id.back_imgx);

        tvFullname.setText(UserHelper.getFirstName()+" "+UserHelper.getLastname());
        tvMobile.setText("+91 "+UserHelper.getId());
        Picasso.with(getApplicationContext()).load(AppConstants.AWS_PROFILEIMAGE+UserHelper.getImgname()).resize(100,100).into(ivProfile);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHelper.logout(SettingsActivity.this);
              /*  Intent broadcastIntent = new Intent("RestartReceiver");
                broadcastIntent.putExtra("RESTART",false);
                sendBroadcast(broadcastIntent);*/
              eventBus.post(new ChangeLastSeenAvail(false));
              eventBus.post(new DisconnectEvent());
              Intent intent=new Intent(SettingsActivity.this, OTPLoginActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(intent);
              finish();
                //i
                //ActivityCompat.finishAffinity(SettingsActivity.this);
              overridePendingTransition(0,0);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setType("image/*");
                final int ACTIVITY_SELECT_IMAGE = 1234;
                startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
            }
        });

        rlSetProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setType("image/*");
                final int ACTIVITY_SELECT_IMAGE = 1234;
                startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
            }
        });

        rlAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this,AccountActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    Picasso.with(getApplicationContext()).load(new File(file_path)).resize(100,100).into(ivProfile);
                    //GroupInfo groupInfo = new GroupInfo();
                    UploadDpPost(file_path);
                }
        }
    }



    private void UploadDpPost(String file_path) {
        File f = new File(file_path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file_name", f.getName(), requestFile);
        RestClientS3Group.ApiGroupInterface service=RestClientS3Group.getClient();
        Call<RegisterResponse> call = service.uploadProfile(createRequestBody(UserHelper.getId()), body);
        dialog = ProgressDialog.show(this, "",
                "Uploading!. Please wait...", true);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                dialog.dismiss();
                RegisterResponse registerResponse = response.body();
                if(registerResponse.getStatus())
                {
                    UserHelper.setImgname(registerResponse.getResult().getImgname());
                    getSharedPreferences("USER", MODE_PRIVATE).edit()
                            .putString("imgname",registerResponse.getResult().getImgname()).apply();
                }else
                {
                    Toast.makeText(SettingsActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(SettingsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
    public static RequestBody createRequestBody(String field) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), field);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!eventBus.isRegistered(this)){
            eventBus.register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);

    }

    @Subscribe
    public void BlankEvent(ChangeLastSeenAvail seenAvail){

    }


}
