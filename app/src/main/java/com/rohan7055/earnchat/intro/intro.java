package com.rohan7055.earnchat.intro;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.rohan7055.earnchat.Login;
import com.rohan7055.earnchat.MainActivity;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.otpmodule.OTPLoginActivity;

public class intro extends AppCompatActivity {
    public static final int APP_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_intro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment selectedFragment=new fragment0();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.llay, selectedFragment);
        transaction.commit();

       /* if(Build.VERSION.SDK_INT>=23) {
            String[] PERMISSIONS = {android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.GET_ACCOUNTS, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.RECORD_AUDIO};
            if(!hasPermissions(this, PERMISSIONS)){
                Toast.makeText(this, "Please grant all the permissions to use the application", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(intro.this, PERMISSIONS, APP_REQUEST_CODE);

            }
            else{
                //AppConstants.first = true;
                Fragment selectedFragment=new fragment0();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.llay, selectedFragment);
                transaction.commit();
            }
        }
        else{
            Fragment selectedFragment=new fragment0();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.llay, selectedFragment);
            transaction.commit();
        }*/


    }

    public void next(View v){
        Fragment selectedFragment=new fragment1();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.llay, selectedFragment);
        transaction.setCustomAnimations(0,0);
        transaction.commit();
    }

    public void skip(View v){
        startActivity(new Intent(intro.this, OTPLoginActivity.class));
        finish();
    }

    public void start(View v){
        startActivity(new Intent(intro.this, OTPLoginActivity.class));
        finish();
    }



}
