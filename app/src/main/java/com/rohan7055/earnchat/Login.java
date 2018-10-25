package com.rohan7055.earnchat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.Model.StatusModel;
import com.rohan7055.earnchat.Model.UserCreationStatusModel;
import com.rohan7055.earnchat.Model.UserModel8Chat;
import com.rohan7055.earnchat.celgram.DataBase_Helper.MessageSQLiteHelper;
import com.rohan7055.earnchat.celgram.Message;
import com.rohan7055.earnchat.celgram.ResponseModels.AdveryiserLoginModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button login;
    EditText mobile;
    EditText password;
    ProgressDialog dialog;
    TextView forgot_password;
    EditText edit, new_password, code, email, name, email_support, mobile_support, message;
    String mail;
    String mailer = "support@chikoop.com";
    Spinner chooser;
    private int flag = 0;
    //private TextView basic_problem, login_problem,payement_problem, other_problem, problem_not_resolved;
    private int temp = 0;
    private boolean advertiser = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        login = (Button)findViewById(R.id.login_loginbtn);
        mobile = (EditText) findViewById(R.id.login_EditTextMobileNO);
        password = (EditText) findViewById(R.id.login_EditTextPassword);
        password.setTypeface(Typeface.DEFAULT);
        forgot_password = (TextView)findViewById(R.id.forgot);


        //support = (TextView)findViewById(R.id.support);
        Button signup = (Button) findViewById(R.id.signupButton);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                alertDialog.setTitle("Register as a");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Advertiser",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String url = "https://www.admybrand.com/register";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Media Owner",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = "https://www.admybrand.com/media_register";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                                dialog.dismiss();
                            }
                });
                alertDialog.show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobile.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Mobile Field cannot be Empty", Toast.LENGTH_LONG).show();
                }
                else{ login(); }
                }

        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.admybrand.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }

    public void checkemail() {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.activity_forgot_password, null);
            dialogBuilder.setView(dialogView);

            edit = (EditText) dialogView.findViewById(R.id.email_forgot);
            final String temp = edit.getText().toString();
            dialogBuilder.setTitle("Verify your Email ID");
            dialogBuilder.setMessage("Fill the details below : ");
            dialogBuilder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if(!temp.isEmpty()) {
                        verifyemail();
                    }
                    else {
                        Toast.makeText(Login.this, "Email field can not be empty", Toast.LENGTH_SHORT).show();
                        checkemail();
                    }
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
    }

    private void verifyemail() {
        mail = edit.getText().toString();
        LoginApi service  = ServiceGenerator.createService(LoginApi.class);
        Call<UserCreationStatusModel> call = service.forgot_password(mail);
        dialog = ProgressDialog.show(this, "",
                "Verifying Email ID. Please wait...", true);
        call.enqueue(new Callback<UserCreationStatusModel>() {

            @Override
            public void onResponse(Call<UserCreationStatusModel> call, Response<UserCreationStatusModel> response) {
                UserCreationStatusModel UserCreationStatusModel = response.body();
                if(UserCreationStatusModel.getStatus()) {
                    dialog.dismiss();
                    forgot();

                }else{
                    dialog.dismiss();
                    Toast.makeText(Login.this,UserCreationStatusModel.getMessage(), Toast.LENGTH_LONG).show();
                    checkemail();
                }
            }
            @Override
            public void onFailure(Call<UserCreationStatusModel> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(Login.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void forgot() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.forgot_password, null);
        dialogBuilder.setView(dialogView);

        email = (EditText) dialogView.findViewById(R.id.email);
        email.setText(mail);
        code = (EditText) dialogView.findViewById(R.id.code);
        new_password = (EditText) dialogView.findViewById(R.id.password);

        dialogBuilder.setTitle("Mail has been sent. Please Check your Registered Mail. ");
        dialogBuilder.setMessage("Fill the details below : ");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                resetpassword();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void resetpassword() {
        mail = edit.getText().toString();
        String e_code = code.getText().toString();
        String e_new_password = new_password.getText().toString();
        LoginApi service  = ServiceGenerator.createService(LoginApi.class);
        Call<UserCreationStatusModel> call = service.reset_password(mail, e_code, e_new_password);
        dialog = ProgressDialog.show(this, "",
                "Verifying Email ID. Please wait...", true);
        call.enqueue(new Callback<UserCreationStatusModel>() {

            @Override
            public void onResponse(Call<UserCreationStatusModel> call, Response<UserCreationStatusModel> response) {
                UserCreationStatusModel UserCreationStatusModel = response.body();
                if(UserCreationStatusModel.getStatus()) {
                    dialog.dismiss();
                    Toast.makeText(Login.this,UserCreationStatusModel.getMessage(), Toast.LENGTH_LONG).show();

                }else{
                    dialog.dismiss();
                    Toast.makeText(Login.this,UserCreationStatusModel.getMessage(), Toast.LENGTH_LONG).show();
                    forgot();
                }
            }
            @Override
            public void onFailure(Call<UserCreationStatusModel> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(Login.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*public void support_message() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.support_layout, null);
        dialogBuilder.setView(dialogView);

        basic_problem = (TextView) dialogView.findViewById(R.id.textView10);
        login_problem = (TextView) dialogView.findViewById(R.id.textView26);
        payement_problem = (TextView) dialogView.findViewById(R.id.textView47);
        other_problem = (TextView) dialogView.findViewById(R.id.textView48);
        problem_not_resolved = (TextView) dialogView.findViewById(R.id.textView49);

        basic_problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = 1;
                adv_support_message();
            }

        });
        login_problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = 2;
                adv_support_message();
            }

        });
        payement_problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = 3;
                adv_support_message();
            }

        });
        other_problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = 4;
                adv_support_message();
            }

        });
        problem_not_resolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = 5;
                adv_support_message();
            }

        });

        dialogBuilder.setTitle("Choose One : ");
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }*/

    public void adv_support_message() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.adv_support_layout, null);
        dialogBuilder.setView(dialogView);

        name = (EditText) dialogView.findViewById(R.id.name);
        email_support = (EditText) dialogView.findViewById(R.id.emailid);
        mobile_support = (EditText) dialogView.findViewById(R.id.contact);
        message = (EditText) dialogView.findViewById(R.id.message);
        if(temp == 1)
            message.setText("General Problem: ");
        else if(temp == 2)
            message.setText("Login/Signup Problem: ");
        else if(temp == 3)
            message.setText("Payment Problem: ");
        else if(temp == 4)
            message.setText("Other Problem: ");
        else if(temp == 5)
            message.setText("Problem not Resolved: ");
        chooser = (Spinner) dialogView.findViewById(R.id.spinner3);
        if(temp == 5)
            chooser.setVisibility(View.VISIBLE);
        else
            chooser.setVisibility(View.GONE);
        List<String> list = new ArrayList<>();
        list.add("Chikoop Support : support@chikoop.com");
        list.add("Customer Care : care@chikoop.com");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooser.setAdapter(dataAdapter);
        chooser.setOnItemSelectedListener(this);
        dialogBuilder.setTitle("Fill the details below : ");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                    if(flag!=0)
                    {
                        send_support();
                    }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        flag++;
        if(pos==0)
            mailer = "support@chikoop.com";
        else
            mailer = "care@chikoop.com";
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void send_support() {
        String name1 = name.getText().toString();
        String email1 = email_support.getText().toString();
        String mobile1 = mobile_support.getText().toString();
        String message1 = message.getText().toString();
        LoginApi service  = ServiceGenerator.createService(LoginApi.class);
        Call<UserCreationStatusModel> call = service.support(name1, email1, mobile1, message1, mailer);
        call.enqueue(new Callback<UserCreationStatusModel>() {

            @Override
            public void onResponse(Call<UserCreationStatusModel> call, Response<UserCreationStatusModel> response) {
                UserCreationStatusModel UserCreationStatusModel = response.body();
                if(UserCreationStatusModel.getStatus()) {
                    Toast.makeText(Login.this,"We'll help with your queries/problems. Wait till we contact you.", Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(Login.this,UserCreationStatusModel.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<UserCreationStatusModel> call, Throwable t) {
                Toast.makeText(Login.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*public void login() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.EIGHTCHAT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final LoginApi service = retrofit.create(LoginApi.class);
        AdveryiserLoginModel model = new AdveryiserLoginModel(mobile.getText().toString(), password.getText().toString());

        Call<UserModel8Chat> call;

        if(advertiser) {
            call = service.loginAdvertiser(model);
        }else{
            call = service.loginMediaOwners(model);
        }

        dialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);
        call.enqueue(new Callback<UserModel8Chat>() {
            @Override
            public void onResponse(Call<UserModel8Chat> call, Response<UserModel8Chat> response) {
                UserModel8Chat model = response.body();

                //UserModel8Chat user = model.getResult();

                *//*Log.d("id", user.getMobile());
                Log.d("mobile", user.getMobile());
                Log.d("firstname", user.getFirstname());
                Log.d("lastname", user.getLastname());
                Log.d("username", user.getUsername());
                Log.d("imagename", user.getImgname());*//*

                if(model.getStatus()) {
                    dialog.dismiss();
                   // Log.i("Tag",user.getMobile());
                    //UserHelper.saveUser8chat(user, Login.this);
                    SharedPreferences prefs1 = getSharedPreferences("my_prefs", MODE_PRIVATE);
                    SharedPreferences userpref = getSharedPreferences("USER", MODE_PRIVATE);
                    SharedPreferences.Editor editor =userpref.edit();
                    editor.putString("mobile",mobile.getText().toString().trim());

                    SharedPreferences.Editor edit = prefs1.edit();
                    edit.putString("password", password.getText().toString());
                    edit.putString("mobile", mobile.getText().toString().trim());
                    editor.apply();
                    edit.apply();

                        //Sending Customer Care welcome message...
                        Message message=new Message(AppConstants.CustomerCare,false,0,"",0,"Hii I am your Customer Care Bot...","","");
                        MessageSQLiteHelper db_helper = new MessageSQLiteHelper(Login.this);
                        message.setId(String.valueOf(new Random().nextInt(9999)-1)+System.currentTimeMillis());
                        message.setMsg_status(0);
                        db_helper.addMessage(message);
                        db_helper.addToChatList(AppConstants.CustomerCare,message.getId());

                        Intent intent = new Intent(getApplicationContext(), CelgramMain.class);
                        startActivity(intent);
                        finish();
                }
                else{
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel8Chat> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/



    public void login() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.EIGHTCHAT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final LoginApi service = retrofit.create(LoginApi.class);
        AdveryiserLoginModel model = new AdveryiserLoginModel(mobile.getText().toString(), password.getText().toString());
        Call<StatusModel> call;

        if(advertiser) {
            call = service.loginAdvertiser(model);
        }else{
            call = service.loginMediaOwners(model);
        }

        dialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);
        call.enqueue(new Callback<StatusModel>() {

            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                StatusModel model = response.body();
                if(model.getStatus()) {
                    dialog.dismiss();

                    UserModel8Chat user = model.getData().get(0);

                    // Log.i("Tag",user.getMobile());
                    if(advertiser){
                        UserHelper.saveUser8chat(user, Login.this, "Advertiser");
                    }else{
                        UserHelper.saveUser8chat(user, Login.this, "Media Owner");
                    }

                    SharedPreferences prefs1 = getSharedPreferences("my_prefs", MODE_PRIVATE);
                    SharedPreferences userpref = getSharedPreferences("USER", MODE_PRIVATE);
                    SharedPreferences.Editor editor =userpref.edit();
                    editor.putString("mobile",mobile.getText().toString().trim());
                    SharedPreferences.Editor edit = prefs1.edit();
                    edit.putString("password", password.getText().toString());
                    edit.putString("mobile", mobile.getText().toString().trim());
                    editor.apply();
                    edit.apply();

                    //Sending Customer Care welcome message...
                    Message message=new Message(AppConstants.CustomerCare,false,0,"",0,
                            "Dear "+user.getFirstname()+" "+user.getLastname()+",\n" +
                                    "\n" +
                                    "I am Jaarvis, Chief Chatbot at ADmyBRAND, I along with our Customer Care team is here to help you for any of your queries and assure you quick response in all cases.\n" +
                                    "\n" +
                                    "Please Confirm following details that we have about you, you can let us know if there is change in any of these:\n" +
                                    "\n" +
                                    "You are "+user.getSalutation()+" "+user.getFirstname()+" "+user.getLastname()+"\n" +
                                    "You work at "+user.getBrand()+" and your company is "+user.getCompany()+"\n" +
                                    "Your phone number is "+user.getMobile()+" and\n" +
                                    "Your registered email id is "+user.getEmail()+"\n" +
                                    "\n" +
                                    "Once again we welcome you on ADmyBRAND - the end to end marketing platform and we gives our best to be your virtual assistant for all your marketing needs. ","","");
                    MessageSQLiteHelper db_helper = new MessageSQLiteHelper(Login.this);
                    message.setId(String.valueOf(new Random().nextInt(9999)-1)+System.currentTimeMillis());
                    message.setMsg_status(0);
                    db_helper.addMessage(message);
                    db_helper.addToChatList(AppConstants.CustomerCare,message.getId());

                    Message message2=new Message(AppConstants.CustomerCare,false,0,"",0,
                            "This app - 8Chat can also be used to connect with other Media Owners and Advertisers and will help you in networking. Please follow certain guidelines when you connect with any Advertiser or Media Owner on 8Chat App. We call these 8 rules as the 8 basic rules of 8Chat App.\n" +
                                    "\n" +
                                    "1. While posting any messages to fellow network please restrict the message you want to convey to a single post and please keep the conversation professional.\n" +
                                    "\n" +
                                    "2. Always mention your Admybrand ID and LinkedIn URL when you want to request for a contact and try to be specific in your conversation.\n" +
                                    "\n" +
                                    "3. Please avoid random chatting here, this is a professional network for advertisers and Media Owners and we want more professional discussions to happen here otherwise people will start loosing interest in this and this will end up becoming another whatsapp.\n" +
                                    "\n" +
                                    "4. Check the pinned messages and domain specific groups if you want to share a new information about your company.  A streamlined approach is always better than a generalized approach.\n" +
                                    "\n" +
                                    "5. In case you encounter a typo or make a post by mistake feel free to use the EDIT or Delete option or let it be, people in this network are smart enough to identify typos and repeated correction messages will add in spam only.\n" +
                                    "\n" +
                                    "6. Please make sure that you introduce yourself in any new chat with individual or in a group. It will help you to gain credibility and discussions can be specific. \n" +
                                    "\n" +
                                    "7. In case you find any offensive content or if you find someone behaving inappropriately in the group, please notify customer care group. Its located on top of all other chat options. SPAM messages will be blocked by our customer care agents on any chat - one to one chats, group chats or chat with our customer service agent.\n" +
                                    "\n" +
                                    "8. Please be polite while you speak to customer care agent, though at times you will be responded by the AI driven chat bot but most of the times its an agent which will be helping you out in your queries. We may take some time reverting you for your queries. Please be patient. We always try our best to service your requests on immediate basis.\n" +
                                    "\n" +
                                    "Once again welcome onboard and we wish a delightful experience for you here and an opportunity of networking with fellow advertisers and media owners.","","");
                    message2.setId(String.valueOf(new Random().nextInt(9999)-1)+System.currentTimeMillis());
                    message2.setMsg_status(0);
                    db_helper.addMessage(message2);
                    db_helper.addToChatList(AppConstants.CustomerCare,message2.getId());

                    Intent intent = new Intent(getApplicationContext(), CelgramMain.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Network issue", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            finishAffinity();
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
}