package com.rohan7055.earnchat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.rohan7055.earnchat.intro.intro;

public class MainActivity extends AppCompatActivity {

    final private static int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT>=23) {
            String[] PERMISSIONS = {android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.GET_ACCOUNTS, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.RECORD_AUDIO};
            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
            }else{
                UserHelper.loadUser(getApplicationContext());
                if(UserHelper.getId().equals("")){
                    if (!UserHelper.isOnce()) {
                        startActivity(new Intent(MainActivity.this, intro.class));
                        finish();
                    }
                    else{
                        startActivity(new Intent(MainActivity.this, Login.class));
                    }
                }else{
                    startActivity(new Intent(MainActivity.this, CelgramMain.class));
                    finish();
                }
            }
        }
        else{
            UserHelper.loadUser(getApplicationContext());
            if(UserHelper.getId().equals("")){
                if (!UserHelper.isOnce()) {
                    startActivity(new Intent(MainActivity.this, intro.class));
                    finish();
                }
                else{
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
            }else{
                startActivity(new Intent(MainActivity.this, CelgramMain.class));
                finish();
            }
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
            case MainActivity.REQUEST_CODE_ASK_PERMISSIONS: {
                if (grantResults.length == 10
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED && grantResults[5] == PackageManager.PERMISSION_GRANTED
                        && grantResults[6] == PackageManager.PERMISSION_GRANTED && grantResults[7] == PackageManager.PERMISSION_GRANTED
                        && grantResults[8] == PackageManager.PERMISSION_GRANTED && grantResults[9] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                    finish();
                    startActivity(i);
                } else {
                    MainActivity.this.finishAffinity();
                    Toast.makeText(this, "permissions denied. Please give all the permissions to use the app.", Toast.LENGTH_LONG).show();
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                return;
            }
        }
    }
}