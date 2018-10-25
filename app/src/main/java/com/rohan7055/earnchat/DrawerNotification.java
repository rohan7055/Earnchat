package com.rohan7055.earnchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/*created by Rohan 13 feb 17*/
public class DrawerNotification extends AppCompatActivity {
    TextView tvChangeNotification,tvToneName;
    Ringtone ringtoneNotification;
    final int RQS_RINGTONEPICKER = 1;
    Uri ringtonUri;

    public Uri getUriRingtone()
    {
        return ringtonUri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_notification);
       // applyFont(this, findViewById(R.id.base_layout));
        tvChangeNotification = (TextView) findViewById(R.id.message_notificationTone);
        tvToneName = (TextView) findViewById(R.id.message_toneName);
        tvChangeNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRingTonePicker();
            }
        });
    }

    public void startRingTonePicker() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        startActivityForResult(intent, RQS_RINGTONEPICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RQS_RINGTONEPICKER && resultCode == RESULT_OK) {
            ringtonUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            SharedPreferences PSharedPreferences=getSharedPreferences("MYDATA",MODE_PRIVATE);
            SharedPreferences.Editor editor=PSharedPreferences.edit();
            editor.putString("uri", String.valueOf(ringtonUri));
            editor.apply();
            ringtoneNotification = RingtoneManager.getRingtone(getApplicationContext(), ringtonUri);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(),
                        RingtoneManager.TYPE_NOTIFICATION, ringtonUri);
            }
            catch (Throwable t) {
                //error
            }
            tvToneName.setText(ringtoneNotification.getTitle(getApplicationContext()));
            Toast.makeText(getApplicationContext(),
                    ringtoneNotification.getTitle(getApplicationContext()),
                    Toast.LENGTH_LONG).show();
        }
    }

}
