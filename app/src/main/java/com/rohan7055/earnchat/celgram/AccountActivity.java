package com.rohan7055.earnchat.celgram;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.Model.StatusModelSession;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.UserHelper;
import com.rohan7055.earnchat.celgram.CelgramApi.RestClientS3Group;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {
    private LinearLayout llUsername,llStatus;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        llUsername=(LinearLayout)findViewById(R.id.llchangeusername);
        llStatus=(LinearLayout)findViewById(R.id.llchangestatus);

        llUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AccountActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.change_username);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.CENTER_HORIZONTAL;
                TextView tvTitle=(TextView)dialog.findViewById(R.id.tvTitle);
                final EditText etUsername=(EditText)dialog.findViewById(R.id.et_value);
                Button btnYes=(Button) dialog.findViewById(R.id.btnYes);
                Button btnNo=(Button) dialog.findViewById(R.id.btnNo);
                tvTitle.setText("Change Username");
                etUsername.setHint("Enter Username");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username=etUsername.getText().toString();
                        if(!username.equals(""))
                        {
                            dialog.dismiss();
                          updateUsername(UserHelper.getId(),username);
                        }else{
                            Toast.makeText(AccountActivity.this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();


            }
        });

        llStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AccountActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.change_username);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.CENTER_HORIZONTAL;
                TextView tvTitle=(TextView)dialog.findViewById(R.id.tvTitle);
                final EditText etUsername=(EditText)dialog.findViewById(R.id.et_value);
                Button btnYes=(Button) dialog.findViewById(R.id.btnYes);
                Button btnNo=(Button) dialog.findViewById(R.id.btnNo);
                tvTitle.setText("Change Status");
                etUsername.setHint("Enter Status");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username=etUsername.getText().toString();
                        if(!username.equals(""))
                        {
                            updateStatus(UserHelper.getId(),username);
                            dialog.dismiss();

                        }else{
                            Toast.makeText(AccountActivity.this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
    }

    private void updateUsername(String id, final String username) {
        RestClientS3Group.ApiGroupInterface service=RestClientS3Group.getClient();
        Call<StatusModelSession> call = service.updateusername(id, username);
        dialog = ProgressDialog.show(this, "",
                "Updating!. Please wait...", true);
        call.enqueue(new Callback<StatusModelSession>() {
            @Override
            public void onResponse(Call<StatusModelSession> call, Response<StatusModelSession> response) {
                dialog.dismiss();
                StatusModelSession statusModelSession = response.body();
                if(statusModelSession.getStatus()){
                    UserHelper.setUsername(username);
                    getSharedPreferences("USER", MODE_PRIVATE).edit()
                            .putString("username",username).apply();
                    Toast.makeText(AccountActivity.this, statusModelSession.getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AccountActivity.this, statusModelSession.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<StatusModelSession> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(AccountActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus(String id, final String status){
        RestClientS3Group.ApiGroupInterface service=RestClientS3Group.getClient();
        Call<StatusModelSession> call = service.updatestatus(id, status);
        dialog = ProgressDialog.show(this, "",
                "Updating!. Please wait...", true);
        call.enqueue(new Callback<StatusModelSession>() {
            @Override
            public void onResponse(Call<StatusModelSession> call, Response<StatusModelSession> response) {
                dialog.dismiss();
                StatusModelSession statusModelSession = response.body();
                if(statusModelSession.getStatus()){
                    UserHelper.setStatus(status);
                    getSharedPreferences("USER", MODE_PRIVATE).edit()
                            .putString("status",status).apply();
                    Toast.makeText(AccountActivity.this, statusModelSession.getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AccountActivity.this, statusModelSession.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<StatusModelSession> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(AccountActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
