package com.rohan7055.earnchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.rohan7055.earnchat.intro.fragment0;
import com.rohan7055.earnchat.intro.intro;


public class SplashActivity extends AppCompatActivity {
    Handler handler=new Handler();
    Activity activity;
    final int SPLASH_DISPLAY_LENGTH = 2000;
    private static final int APP_REQUEST_CODE=123;
    private String userid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        activity=this;

        UserHelper.loadUser(getApplicationContext());
        SharedPreferences user = getSharedPreferences("USER", MODE_PRIVATE);
        userid=UserHelper.getId();
        Log.d("SESSIONID", Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID).toString());
        if(Build.VERSION.SDK_INT>=23){
            //requestpermission if granted then proceed
            String[] PERMISSIONS = {android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.GET_ACCOUNTS, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.RECORD_AUDIO};
            if(!hasPermissions(SplashActivity.this, PERMISSIONS)){
                Toast.makeText(SplashActivity.this, "Please grant all the permissions to use the application", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, APP_REQUEST_CODE);

            }else{
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(userid==null||userid.equals(""))
                        {
                            activity.startActivity(new Intent(activity,intro.class));
                            activity.finish();

                        }else{
                            Intent intent = new Intent(getApplicationContext(), CelgramMain.class);
                            activity.startActivity(intent);
                            activity.finish();

                        }

                    }
                },SPLASH_DISPLAY_LENGTH);
            }

        }else{
            //normal check and proceed
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(userid==null||userid.equals(""))
                    {
                        activity.startActivity(new Intent(activity,intro.class));
                        activity.finish();

                    }else{
                        Intent intent = new Intent(getApplicationContext(), CelgramMain.class);
                        activity.startActivity(intent);
                        activity.finish();

                    }
                }
            },SPLASH_DISPLAY_LENGTH);
        }




    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case intro.APP_REQUEST_CODE: {
                if (grantResults.length == 10
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED && grantResults[5] == PackageManager.PERMISSION_GRANTED
                        && grantResults[6] == PackageManager.PERMISSION_GRANTED && grantResults[7] == PackageManager.PERMISSION_GRANTED
                        && grantResults[8] == PackageManager.PERMISSION_GRANTED && grantResults[9] == PackageManager.PERMISSION_GRANTED) {
                    if(userid==null||userid.equals(""))
                    {
                        startActivity(new Intent(activity,intro.class));
                        finish();

                    }else{
                        Intent intent = new Intent(getApplicationContext(), CelgramMain.class);
                        startActivity(intent);
                        finish();

                    }
                  /*  Intent i = new Intent(SplashActivity.this, SplashActivity.class);
                    finish();
                    startActivity(i);*/
                } else {
                    SplashActivity.this.finishAffinity();
                    Toast.makeText(this, "permissions denied. Please give all the permissions to use the app.", Toast.LENGTH_LONG).show();
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                return;
            }
        }
    }
}
