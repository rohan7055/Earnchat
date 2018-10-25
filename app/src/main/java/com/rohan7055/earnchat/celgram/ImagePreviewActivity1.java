package com.rohan7055.earnchat.celgram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan7055.earnchat.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Taranjyot on 11/29/2016.
 */
public class ImagePreviewActivity1 extends AppCompatActivity {

    ImageView img;
    TextView date;
    PhotoViewAttacher mAttacher;
    ImageView back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview1);
        img = (ImageView) findViewById(R.id.preview_image);
        date = (TextView) findViewById(R.id.date);
        back = (ImageView) findViewById(R.id.back);
        Bundle extras = getIntent().getExtras();
        String img_path = extras.getString("Imageurl");
        String date1 = extras.getString("ImageTimeStamp");
     try {
         SimpleDateFormat sdfmt1 = new SimpleDateFormat("dd/MM/yy");
         SimpleDateFormat sdfmt2 = new SimpleDateFormat("dd-MMM-yyyy");
         Date dDate = sdfmt1.parse(date1);
         String strOutput = sdfmt2.format(dDate);
         date.setText(strOutput);
     }catch (Exception e){
         e.printStackTrace();
     }
        date.setText(date1);
        Bitmap myBitmap = BitmapFactory.decodeFile(img_path);
        img.setImageBitmap(myBitmap);
        mAttacher = new PhotoViewAttacher(img);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
     //   Glide.with(this).load(img_path).into(img);
        Toast.makeText(this,date1,Toast.LENGTH_SHORT).show();
/*        Picasso.with(this)
                .load(img_path)
                .error(R.drawable.background)
                .into(img);
  */
    }
}
