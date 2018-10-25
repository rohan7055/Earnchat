package com.rohan7055.earnchat.otpmodule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.Model.StatusModel;
import com.rohan7055.earnchat.Model.StatusModelSession;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.RegisterActivity;
import com.rohan7055.earnchat.celgram.CelgramApi.RestClientS3Group;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPLoginActivity extends AppCompatActivity {
   // private final static String API_KEY = "49bab3c1-6836-11e8-a895-0200cd936042";
   // private final static String API_KEY = "fb07ade8-b4c4-11e8-a895-0200cd936042";
    private final static String API_KEY = "228a809f-b9c5-11e8-a895-0200cd936042";
    private Button btnSubmit;
    private EditText name, email, mobileNumber,etOTP;
    private String sessionId,otp,otpAuto;
    public static final String OTP_REGEX = "[0-9]{1,6}";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private ProgressDialog dialog;
    private String mobile="";
    // private ChangeListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSubmit = (Button) findViewById(R.id.login_loginbtn);
        mobileNumber = (EditText) findViewById(R.id.login_EditTextMobileNO);
        etOTP=(EditText)findViewById(R.id.etOTP);

        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.

            if (API_KEY.isEmpty()) {

                Toast.makeText(getApplicationContext(), "Please obtain your API KEY first from 2Factor.in", Toast.LENGTH_LONG).show();
                return;
            }

        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(mobileNumber.getText().toString())) {

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(OTPLoginActivity.this);
                    builder.setMessage("Enter all the information requested Correctly").setPositiveButton("Dismiss", dialogClickListener)
                            .show();

                } else {
                    // Check if no view has focus:
                    View view1 = getCurrentFocus();
                    if (view1 != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    };

                    mobile=mobileNumber.getText().toString();
                    if(mobile.length()!=10)
                    {
                        Toast.makeText(OTPLoginActivity.this, "Please Enter a valid 10 digit Mobile Number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ApiInterface apiService =
                            ApiClient.getClient().create(ApiInterface.class);

                    Call<MessageResponse> call = apiService.sentOTP(API_KEY, mobileNumber.getText().toString());
                    dialog = ProgressDialog.show(OTPLoginActivity.this, "",
                            "Requesting OTP!. Please wait...", true);
                    call.enqueue(new Callback<MessageResponse>() {
                        @Override
                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                            dialog.dismiss();
                            sessionId = response.body().getDetails();
                            Log.d("SenderID", sessionId);
                             getOTP();
                            etOTP.setVisibility(View.VISIBLE);


                        }

                        @Override
                        public void onFailure(Call<MessageResponse> call, Throwable t) {
                            dialog.dismiss();
                            Log.e("ERROR", t.toString());
                        }

                    });
                }

            }
        });



        etOTP.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() == 6)
                {
                    if(etOTP.getText()!=null){
                        otpAuto = etOTP.getText().toString();
                        checkOtp();
                    }
                }
            }
        });
    }

    private void getOTP() {

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {

                //From the received text string you may do string operations to get the required OTP
                //It depends on your SMS format
                // If your OTP is six digits number, you may use the below code

             /*   Pattern pattern = Pattern.compile(OTP_REGEX);
                Matcher matcher = pattern.matcher(messageText);

                while (matcher.find())
                {
                    otp1 = matcher.group();
                }
                */
                otpAuto=messageText.substring(0,6);
                etOTP.setText(otpAuto);
            }
        });
    }

    private void checkOtp() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MessageResponse> call = apiService.verifyOTP(API_KEY,sessionId, otpAuto);
        dialog = ProgressDialog.show(this, "",
                "Validating OTP!. Please wait...", true);

        call.enqueue(new Callback<MessageResponse>() {

            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {

                dialog.dismiss();

                try {
                    if(response.body().getStatus().equals("Success")){

                     checkUserExist(mobile);
                    }else{
                        Log.d("Failure", response.body().getDetails()+"|||"+response.body().getStatus());
                        Toast.makeText(OTPLoginActivity.this, "You have entered a wrong OTP,Enter a valid OTP", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e("ERROR", t.toString());
                Toast.makeText(OTPLoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);

        int readSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
  /*  public interface ChangeListener {
       public void onChange(String otp);

    }
    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }
*/

  private void checkUserExist(final String mobile)
  {
      RestClientS3Group.ApiGroupInterface service=RestClientS3Group.getClient();
      Call<StatusModelSession> call = service.checkUser(mobile);
      dialog = ProgressDialog.show(this, "",
              "Validating User!. Please wait...", true);
      call.enqueue(new Callback<StatusModelSession>() {
          @Override
          public void onResponse(Call<StatusModelSession> call, Response<StatusModelSession> response) {
              dialog.dismiss();
              final StatusModelSession statusModel = response.body();
              String session= Settings.Secure.getString(getContentResolver(),
                      Settings.Secure.ANDROID_ID);
              if(statusModel.getStatus()||statusModel.getData().equals(session))
              {
                  //new user proceed to registration or same mobile is used to register
                  Intent i=new Intent(OTPLoginActivity.this,RegisterActivity.class);
                  i.putExtra("user_id",mobile);
                  if(statusModel.getStatus())
                  {
                      i.putExtra("FLAG",1);
                  }else
                  {
                      i.putExtra("FLAG",2);
                  }
                  startActivity(i);
                  finish();

              }else
              {
                  //other mobile is used to register and send alert to other mobile
                  AlertDialog.Builder builder = new AlertDialog.Builder(OTPLoginActivity.this);

                  builder.setTitle("Confirm");
                  builder.setMessage("It seems like you are using a different android device to login.Are you sure to log out from other devices?");

                  builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                      public void onClick(DialogInterface dialog, int which) {

                          dialog.dismiss();
                          Intent i=new Intent(OTPLoginActivity.this,RegisterActivity.class);
                          i.putExtra("user_id",mobile);
                          i.putExtra("FLAG",3);
                          i.putExtra("old_session",statusModel.getData());
                          startActivity(i);
                          finish();
                      }
                  });

                  builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                      }
                  });

                  AlertDialog alert = builder.create();
                  alert.show();


              }

          }

          @Override
          public void onFailure(Call<StatusModelSession> call, Throwable t) {
              dialog.dismiss();

          }
      });

  }


}
