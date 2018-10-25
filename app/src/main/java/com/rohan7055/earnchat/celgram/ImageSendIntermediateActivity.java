package com.rohan7055.earnchat.celgram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.rohan7055.earnchat.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageSendIntermediateActivity extends AppCompatActivity {

    ImageView imageView;
    String imgname;
    String urirecv;
    int ori=90;
    Uri uri;

    Bitmap bitmap = null;
    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_send_intermediate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imgname = getIntent().getStringExtra("path");
        urirecv=getIntent().getStringExtra("uri");

        uri = Uri.parse(urirecv);

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView = (ImageView) findViewById(R.id.imageView69);
        imageView.setImageURI(uri);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                File file = new File(imgname);
                Uri finalUri = getImageUri(bitmap);
                String filepath=createDirectoryAndSaveFile(file.getName(),finalUri);

                EmojiEditText editText = (EmojiEditText)findViewById(R.id.caption);
                String msg = editText.getText().toString().trim();

                if(TextUtils.isEmpty(msg)){
                    msg = "";
                }

                data=new Intent();
                data.putExtra("path",filepath);
                data.putExtra("uri",finalUri.toString());
                data.putExtra("caption", msg);
                setResult(RESULT_OK,data);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.preview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_delete){
            finish();
        }
        else if(id==R.id.action_crop){
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(1, 1)
                    .setFixAspectRatio(false)
                    .setBorderLineColor(R.color.ThemeColor)
                    .start(this);
        }
        else if(id== R.id.action_rotate){

            if (bitmap != null) {
                Matrix matrix = new Matrix();
                matrix.postRotate(ori);
                Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                imageView.setImageBitmap(bitmap2);
            }

            //rot=ori;
            Toast.makeText(ImageSendIntermediateActivity.this,ori+"", Toast.LENGTH_SHORT).show();

            ori=(ori+90)%360;
        }
        return super.onOptionsItemSelected(item);
    }

    private String createDirectoryAndSaveFile(String fileName,Uri resultUri) {


        File direct = new File(Environment.getExternalStorageDirectory() + "/8Chat/sent/");

        Bitmap bitmap = null;

        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + "/8Chat/sent/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File(Environment.getExternalStorageDirectory() + "/8Chat/sent/"), fileName);
        if (file.exists()) {
            file.delete();
        }

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
