package com.rohan7055.earnchat.celgram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.otpmodule.OTPLoginActivity;

public class AlertScreenActivity extends AppCompatActivity {

    private Button btnCancel,btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_screen);
        btnCancel=(Button)findViewById(R.id.btn_cancel);
        btnVerify=(Button)findViewById(R.id.btn_verify);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AlertScreenActivity.this, OTPLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
