package com.rohan7055.earnchat;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rohan7055.earnchat.Model.StatusModelSession;
import com.rohan7055.earnchat.Model.UserModel;
import com.rohan7055.earnchat.celgram.CelgramApi.RestClientS3Group;
import com.rohan7055.earnchat.celgram.ResponseModels.RegisterResponse;
import com.rohan7055.earnchat.celgram.ResponseModels.Result;
import com.rohan7055.earnchat.celgram.events.DisconnectEvent;
import com.rohan7055.earnchat.celgram.events.StringEvent;
import com.rohan7055.earnchat.celgram.service.MessageReceive;
import com.rohan7055.earnchat.otpmodule.OTPLoginActivity;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class RegisterActivity extends AppCompatActivity {

    private CircleImageView ivProfile;
    private MaterialEditText tvFirstname,tvLastname,tvUsername;
    private Button btnNext;
    private String session="";
    private String user_id="";
    private String file_path="";
    private ProgressDialog dialog;
    private int FLAG=0;
    private String old_session;
    private boolean iswithdp=false;
    EventBus eventBus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        user_id=getIntent().getStringExtra("user_id");
        FLAG=getIntent().getIntExtra("FLAG",0);
        eventBus=EventBus.getDefault();
        eventBus.register(this);

        session= Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if(FLAG==3)
        {
            old_session=getIntent().getStringExtra("old_session");
            alertUser(user_id,old_session,session);

        }else{
            addsession(user_id,session);
        }


        ivProfile=(CircleImageView)findViewById(R.id.iv_upload);
        tvFirstname=(MaterialEditText)findViewById(R.id.tv_firstname);
        tvLastname=(MaterialEditText)findViewById(R.id.tv_lastname);
        tvUsername=(MaterialEditText)findViewById(R.id.tv_username);
        btnNext=(Button)findViewById(R.id.btn_next);

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

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(tvFirstname.getText().toString())||TextUtils.isEmpty(tvLastname.getText().toString())||
                    TextUtils.isEmpty(tvUsername.getText().toString()))
                {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Enter all the information requested").setPositiveButton("Dismiss", dialogClickListener)
                            .show();
                }else{
                    String username=tvUsername.getText().toString();
                    String firstname=tvFirstname.getText().toString();
                    String lastname=tvLastname.getText().toString();
                    View view1 = getCurrentFocus();
                    if (view1 != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                    };
                    upload(user_id,username,firstname,lastname);
                }
            }
        });


    }

    private void addsession(String user_id, String session) {
        RestClientS3Group.ApiGroupInterface service=RestClientS3Group.getClient();
        Call<StatusModelSession> call = service.updatesession(user_id, session);
        dialog = ProgressDialog.show(this, "",
                "Processing!. Please wait...", true);
        call.enqueue(new Callback<StatusModelSession>() {
            @Override
            public void onResponse(Call<StatusModelSession> call, Response<StatusModelSession> response) {
                dialog.dismiss();
                StatusModelSession statusModelSession = response.body();
                if(statusModelSession.getStatus())
                {
                    Toast.makeText(RegisterActivity.this, statusModelSession.getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(RegisterActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusModelSession> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public static RequestBody createRequestBody(String field) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), field);
    }

   /* @Multipart
    @POST("upload")
    Call<Result> register(@Part("user_id") RequestBody userid,
                          @Part("firstname") RequestBody firstname,
                          @Part("lastname") RequestBody lastname,
                          @Part("session") RequestBody session,
                          @Part("username") RequestBody username,
                          @Part MultipartBody.Part file_name);*/

    private void upload(String user_id, String username, String firstname, String lastname) {
        RestClientS3Group.ApiGroupInterface service = RestClientS3Group.getClient();
        Call<RegisterResponse> call = null;
        if(!file_path.equals(""))
        {
            //upload with a dp
            File f = new File(file_path);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file_name", f.getName(), requestFile);
            call=service.register(createRequestBody(user_id), createRequestBody(firstname), createRequestBody(lastname),
                    createRequestBody(session), createRequestBody(username),body);
            iswithdp=true;

            dialog = ProgressDialog.show(this, "",
                    "Uploading!. Please wait...", true);




        }else
        {
            //upload without a dp
            call=service.register(createRequestBody(user_id), createRequestBody(firstname), createRequestBody(lastname),
                    createRequestBody(session), createRequestBody(username));
            iswithdp=false;
            dialog = ProgressDialog.show(this, "",
                    "Registering!. Please wait...", true);
        }


        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                dialog.dismiss();


                RegisterResponse registerResponse=response.body();
                if(registerResponse.getStatus())
                {
                    Toast.makeText(RegisterActivity.this, "Successfully Registered ", Toast.LENGTH_SHORT).show();

                    UserHelper.saveUser(registerResponse.getResult(),getApplicationContext());
                    if(isMyServiceRunning(MessageReceive.class)){
                        eventBus.post(new DisconnectEvent());
                    }
                    Intent intent=new Intent(RegisterActivity.this,CelgramMain.class);
                    startActivity(intent);
                    finish();


                }else {
                    Toast.makeText(RegisterActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

        builder.setTitle("Confirm");
        builder.setMessage("Are You Sure? If you press YES you can use any other number to Login and NO to Exit the App");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(RegisterActivity.this,OTPLoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                System.exit(1);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

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
                    TextView txt = (TextView)findViewById(R.id.uploadText);
                    txt.setVisibility(View.INVISIBLE);
                    Picasso.with(getApplicationContext()).load(new File(file_path)).resize(100,100).into(ivProfile);
                    //GroupInfo groupInfo = new GroupInfo();
                    //UploadDpPost(file_path);
                }
        }
    }

    private void alertUser(String user_id,String old_session,String newsession)
    {
        RestClientS3Group.ApiGroupInterface service=RestClientS3Group.getClient();
        Call<StatusModelSession> call = service.alertuser(user_id, old_session,newsession);
        dialog = ProgressDialog.show(this, "",
                "Processing!. Please wait...", true);
        call.enqueue(new Callback<StatusModelSession>() {
            @Override
            public void onResponse(Call<StatusModelSession> call, Response<StatusModelSession> response) {
                dialog.dismiss();
                StatusModelSession statusModelSession = response.body();
                if(statusModelSession.getStatus())
                {
                    Toast.makeText(RegisterActivity.this, statusModelSession.getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(RegisterActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusModelSession> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
        eventBus.unregister(this);
        super.onDestroy();

    }

    @Subscribe
    public void onBlanKEvent(StringEvent event){

    }
}
