package com.rohan7055.earnchat.celgram;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.CelgramMain;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.DataBase_Helper.ContactSQLiteHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MagicChat extends AppCompatActivity {
    TextView activity_title;
    Button start_chat;
    EditText add_contact, search_contact;
    TextView explore_contact;
    ImageView icon,back_img;
    EditText chat_name;
    final int CAMERA_CAPTURE = 1;
    private Uri picUri;
    final int PIC_CROP = 2;
    String id;
    private String filepath = null;
    private List<MagicContactsHolder> contactList = new ArrayList<>();
    private String encodedImage;
    ContactSQLiteHelper contacts_db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_chat);
        add_contact = (EditText) findViewById(R.id.et_phonebook);
        back_img=(ImageView)findViewById(R.id.back_img_group);
        search_contact = (EditText) findViewById(R.id.et_search_contact);
        explore_contact = (TextView) findViewById(R.id.tv_explore);
        chat_name = (EditText) findViewById(R.id.et_name_chat);
        icon = (ImageView) findViewById(R.id.iv_magic_dp);
        start_chat = (Button) findViewById(R.id.button_start_chatting);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        contacts_db = new ContactSQLiteHelper(this);


//        start_chat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), ChatWindow.class);
//                i.putExtra("flag", 2);
//                startActivity(i);
//                finish();
//            }
////        });


        search_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MagicChat.this, CreateGroup.class);
                intent.putExtra("flag", 0);
                startActivityForResult(intent, 2);


            }
        });

        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MagicChat.this, NewChat.class);

                startActivityForResult(intent, 1);

            }
        });
        explore_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MagicChat.this, MGCChat.class);
               // intent.putExtra("flag", 1);
               // startActivityForResult(intent, 3);
                startActivity(intent);


            }
        });


        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(MagicChat.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    action();
                else
                    ActivityCompat.requestPermissions(MagicChat.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }

        });
        activity_title = (TextView) findViewById(R.id.heading_txt);
        activity_title.setText("Magic Chat");

    }

    private void action() {
        try {
            Intent pickIntent = new Intent();
            pickIntent.setType("image/*");
            pickIntent.setAction(Intent.ACTION_GET_CONTENT);
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Intent takeGallery = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            String pickTitle = "Take or select a photo";
            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent, takeGallery});
            startActivityForResult(chooserIntent, CAMERA_CAPTURE);
            //use standard intent to capture an image
//            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //we will handle the returned data in onActivityResult
//            startActivity(captureIntent);

        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support capturing images!";
            Toast toast = Toast.makeText(MagicChat.this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) {
                picUri = data.getData();
//                carry out the crop operation
                performCrop();
            }
            else if (requestCode == PIC_CROP) {
                //get the returned data
                Bundle extras = data.getExtras();
//get the cropped bitmap
//                Bitmap thePic = extras.getParcelable("data");
                ImageView picView = (ImageView) findViewById(R.id.iv_magic_dp);
//display the returned cropped image
//                picView.setImageBitmap(thePic);
                Bitmap bitmap = extras.getParcelable("data");

                Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                final RectF rectF = new RectF(rect);
                final float roundPx = 1000;

                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Paint paint = new Paint();
                paint.setShader(shader);
                paint.setAntiAlias(true);
                Canvas c = new Canvas(circleBitmap);
                c.drawRoundRect(rectF, roundPx, roundPx, paint);


                picView.setImageBitmap(circleBitmap);
                saveImage(circleBitmap);
                String image;
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MagicChat.this);

                id = String.valueOf(sharedPref.getInt("id", -1));

                if (filepath == null || filepath == "") {
                    image = "no image";

                } else {
                    image = filepath;

                }


            }


        }
    }




    private void saveImage(Bitmap finalBitmap) {
        if(finalBitmap==null)
            return;
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            filepath=getStringImage(finalBitmap);
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    private void performCrop() {
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);

        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CelgramMain.class));
        finish();
    }
}