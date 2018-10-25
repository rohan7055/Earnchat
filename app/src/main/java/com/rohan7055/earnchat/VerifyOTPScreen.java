package com.rohan7055.earnchat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.Model.UserCreationStatusModel;
import com.rohan7055.earnchat.Model.UserModel;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VerifyOTPScreen extends AppCompatActivity {

    static EditText OTPValue;
    TextView clickHere, countdown;
    Button VarifyButton;
    RelativeLayout relativeLayout;
    private String msg = null;
    private BroadcastReceiver mIntentReceiver;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varify_optscreen);
        OTPValue = (EditText)findViewById(R.id.verify_OTPValue);
        VarifyButton = (Button)findViewById(R.id.verify_ButtonSignUp);
        clickHere = (TextView)findViewById(R.id.verify_clickHere);
        relativeLayout = (RelativeLayout)findViewById(R.id.verify_loadingScreen);
        countdown = (TextView)findViewById(R.id.textView13);

        countDownTimer = new CountDownTimer(3*60*1000, 1000) {

            public void onTick(long millisUntilFinished) {

                countdown.setText(""+String.format("%d min %d sec",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VerifyOTPScreen.this);

                alertDialogBuilder.setTitle("Didn't receive any message from 8Chat?");
                alertDialogBuilder
                        .setIcon(R.drawable.icon)
                        .setMessage("Maybe a server problem. Please try again after sometime! You don't have to register again. It's on us. Use the previously entered crendentials to login in the Login Page!")
                        .setCancelable(false)
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close current activity
                                VerifyOTPScreen.this.finish();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        }.start();

        VarifyButton.setEnabled(false);
        OTPValue.setEnabled(false);

        VarifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(OTPValue.getText().toString().isEmpty())
                {
                    OTPValue.requestFocus();
                }
                else
                {
                    countDownTimer.cancel();
                    newuser(OTPValue.getText().toString());
                }
            }
        });PackageManager pm = getPackageManager();
        int hasPerm = 0;
        if(Build.VERSION.SDK_INT>=23) {
            hasPerm = pm.checkPermission(
                    Manifest.permission.RECEIVE_SMS,
                    getPackageName());
        }
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(VerifyOTPScreen.this, "You Disabled the RECEIVE SMS permission. You have to enter the OTP code manually!", Toast.LENGTH_LONG)
                    .show();

        }
    }
    public void clickHereCall(View view)
    {
        relativeLayout.setAlpha(0);
        VarifyButton.setEnabled(true);
        OTPValue.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
        mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                msg = intent.getStringExtra("verify");

                //if(!msg.equals(null))
                if(!msg.isEmpty())
                {
                    relativeLayout.setAlpha(0);
                    VarifyButton.setEnabled(true);
                    OTPValue.setEnabled(true);
                    OTPValue.setText(msg);
                    countDownTimer.cancel();
                    newuser(msg);
                }
           }
        };this.registerReceiver(mIntentReceiver, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mIntentReceiver);
    }

    void newuser(final String OTP)
    {
        final ProgressDialog dialog;
        Bundle getBundle;
        getBundle = this.getIntent().getExtras();
        final String mobile = getBundle.getString("mobile");
        final String password = getBundle.getString("password");
        final LoginApi service  = ServiceGenerator.createService(LoginApi.class);
        Call<UserCreationStatusModel> call = service.OTP(mobile, OTP);
        dialog = ProgressDialog.show(this, "",
                "Verifying OTP. Please wait...", true);
        call.enqueue(new Callback<UserCreationStatusModel>() {

            @Override
            public void onResponse(Call<UserCreationStatusModel> call, Response<UserCreationStatusModel> response) {
                UserCreationStatusModel UserCreationStatusModel = response.body();
                if(UserCreationStatusModel.getStatus()) {
                    dialog.dismiss();

                    Call<UserModel> call2 = service.login(mobile, password);

                    call2.enqueue(new Callback<UserModel>() {

                        @Override
                        public void onResponse(Call<UserModel> call2, Response<UserModel> response) {
                            UserModel user = response.body();
                            if(true) {
                                dialog.dismiss();
                                UserHelper.saveUser(user, VerifyOTPScreen.this);
                                Call<UserCreationStatusModel> call1 = service.verification("{\"verified\":\"Y\"}", user.getSession());
                                call1.enqueue(new Callback<UserCreationStatusModel>() {
                                    @Override
                                    public void onResponse(Call<UserCreationStatusModel> call, Response<UserCreationStatusModel> response) {}

                                    @Override
                                    public void onFailure(Call<UserCreationStatusModel> call, Throwable t) {}
                                });

                                //TODO: something here commented out
                                /*Intent intent = new Intent(VerifyOTPScreen.this,ProfilePage.class);
                                intent.putExtra("CHECK", false);
                                startActivity(intent);
                                finish();*/

                            }else{
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Network issue", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    dialog.dismiss();
                    Toast.makeText(VerifyOTPScreen.this,UserCreationStatusModel.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<UserCreationStatusModel> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(VerifyOTPScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}