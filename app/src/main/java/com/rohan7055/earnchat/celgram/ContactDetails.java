package com.rohan7055.earnchat.celgram;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.AppConstants;
import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.CelgramApi.RestClientUser;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;
import com.rohan7055.earnchat.celgram.ResponseModels.GetStatus;
import com.rohan7055.earnchat.celgram.ResponseModels.Statusdata;
import com.rohan7055.earnchat.celgram.events.DpChangedEvent;
import com.rohan7055.earnchat.celgram.events.MessageEvent;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactDetails extends AppCompatActivity {


    TextView full_name,uname,id;
    CircleImageView profileImage;
    String value1;
    String userid;
    Context context;
    ImageView imageview7;
    StringBuilder dualsim;
    LinearLayout callsim;
    String perm;
    ImageView back_img;
    ContactSQLiteHelper db_contact;
    TextView tv_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        context=this;
        dualsim=new StringBuilder();
        tv_status =(TextView)findViewById(R.id.tv_status);
        full_name = (TextView) findViewById(R.id.tvFullname);
        id=(TextView)findViewById(R.id.tvMobile);
        uname=(TextView)findViewById(R.id.textView14);
        back_img=(ImageView)findViewById(R.id.back_imgx);
        imageview7=(ImageView)findViewById(R.id.imageView7);
        db_contact=new ContactSQLiteHelper(this);
        profileImage=(CircleImageView)findViewById(R.id.profilePage_profilePic);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imageview7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        // uname=(TextView)findViewById(R.id.UserName_pp);

        //mail=(TextView)findViewById(R.id.textView19);
        /*
        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        String fname = preferences.getString("firstname", "");
        String userid = preferences.getString("id", "");
        String lname = preferences.getString("lastname", "");
        String uname = preferences.getString("username", "");*/
        Bundle extras = getIntent().getExtras();


        String value = extras.getString("CONTACT_NAME");
        userid = extras.getString("userid");
        Log.i("Anuraj_Conatact userid",userid);
        value1=extras.getString("ID");
        String value2=extras.getString("UNAME");
        String value3=extras.getString("MAIL");
        String image = extras.getString("image");
        full_name.setText(value);
        id.setText("+91 "+value1);
        uname.setText("~@"+value2);
        //mail.setText(value3);

/*
        Picasso.with(this).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/EarnChat/" +userid+ "/" + image)).error(R.drawable.default_user).fit().centerCrop().into(profileImage);
*/
        //getStatus();
        Picasso.with(this).load(AppConstants.AWS_PROFILEIMAGE+image).error(R.drawable.default_user).fit().into(profileImage, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Log.i("ImageLoadedCON","OnSucess");
            }

            @Override
            public void onError() {
                Log.i("ImageLoadedCON","OnFailure");
            }
        });
    }





    @Subscribe
    public void OnDpChangeEvent(DpChangedEvent event)
    {
        boolean isDpchanged = event.isdpchange();
        if(isDpchanged)
        {
            Log.i("Contact Details","DPCHANGED EVENt");
            Picasso.with(this).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/EarnChat/" +userid+ "/" + db_contact.getLocalImage(userid))).error(R.drawable.default_user).fit().centerCrop().into(profileImage);

        }
    }




    public void Call(View view){

        String number=value1;
        perm="android.permission.CALL_PHONE";
        int permission = PermissionChecker.checkSelfPermission(context, perm);

        if (permission == PermissionChecker.PERMISSION_GRANTED) {

            //Use Intent.ACTION_CALL to directly call without giving the user the choice to select a different app
            //for calling like TrueCaller etc.
            Intent i=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
            startActivity(i);
        } else {
            // permission not granted, you decide what to do
        }

        /*final Dialog dialog = new Dialog(ContactDetails.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.call_popup);
        callsim=(LinearLayout)dialog.findViewById(R.id.callsim1);

        dialog.show();


        callsim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number=value1;
                perm="android.permission.CALL_PHONE";
                int permission = PermissionChecker.checkSelfPermission(context, perm);

                if (permission == PermissionChecker.PERMISSION_GRANTED) {

                    //Use Intent.ACTION_CALL to directly call without giving the user the choice to select a different app
                    //for calling like TrueCaller etc.
                    Intent i=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
                    startActivity(i);
                } else {
                    // permission not granted, you decide what to do
                }

            }
        });*/

    }



    public void callChikoop(View view){}

    @TargetApi(22)
    public void checkAPI() {
        if (android.os.Build.VERSION.SDK_INT >= 22) {
            SubscriptionManager manager = SubscriptionManager.from(this);
            List<SubscriptionInfo> sil = manager.getActiveSubscriptionInfoList();
            if (sil != null) {
                for (SubscriptionInfo subInfo : sil) {
                    Log.v("TestMain", "SubInfo:" + subInfo);
                    dualsim.append(subInfo);
                    dualsim.append("\n");
                }
            } else {
                Log.v("TestMain", "SubInfo: list is null");
            }
            Toast.makeText(ContactDetails.this,dualsim,Toast.LENGTH_LONG).show();



        }
        else{

            TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            String carrierName = manager.getNetworkOperatorName();
            Toast.makeText(ContactDetails.this,carrierName,Toast.LENGTH_LONG).show();

        }
    }

    public void getStatus()
    {
        RestClientUser.ApiInterfaceUser service = RestClientUser.getClient();
        SharedPreferences preferences= getSharedPreferences("USER", MODE_PRIVATE);
        String sessionId =  preferences.getString("session","");
        Call<GetStatus> call = service.getStatus(sessionId,userid);
        call.enqueue(new Callback<GetStatus>() {
            @Override
            public void onResponse(Call<GetStatus> call, Response<GetStatus> response) {
                GetStatus getStatus = response.body();
                Statusdata statusdata;
                if (getStatus.isStatus()) {
                    try {
                        statusdata = getStatus.getData();
                        final String status = statusdata.getCelgram_status();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_status.setText(status);
                            }
                        });
                    } catch (Exception e) {
                        Log.i("Contacts detail", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<GetStatus> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //finish();
        super.onBackPressed();

        // System.exit(0);


    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Runtime.getRuntime().gc();
        System.gc();
    }
}




