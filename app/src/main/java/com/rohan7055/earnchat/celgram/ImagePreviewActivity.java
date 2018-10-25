package com.rohan7055.earnchat.celgram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.graphics.Matrix;
import com.theartofdev.edmodo.cropper.CropImage;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.rohan7055.earnchat.R;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;

import junit.framework.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImagePreviewActivity extends AppCompatActivity {
    public static final String TAG = "GalleryActivity";
    public static final String EXTRA_NAME = "images";

    private ArrayList<String> _images;
    private GalleryPagerAdapter _adapter;
    public SubsamplingScaleImageView imageView;
    Bitmap myBitmap=null;
    int ori=90;
    public File file;
    Uri resulturi;

    //@InjectView(R.id.pager) ViewPager _pager;
    //@InjectView(R.id.thumbnails) LinearLayout _thumbnails;
    //@InjectView(R.id.btn_close) ImageButton _closeButton;

    private ViewPager _pager;
    private LinearLayout _thumbnails;
    //private ImageButton _closeButton;
    private FloatingActionButton fab;
    Toolbar toolbar;

    String urirecv;
    String fileglobal;

    int rotflag=0;
    int cropflag=0;

    int rot;
    Intent data;
    ImageView thumbView;
    EmojiEditText caption;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        // ButterKnife.inject(this);

        _images = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_NAME);
        Assert.assertNotNull(_images);

        urirecv=getIntent().getStringExtra("uri");

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _pager=(ViewPager)findViewById(R.id.pager);
        _thumbnails=(LinearLayout)findViewById(R.id.thumbnails);

        //_closeButton=(ImageButton)findViewById(R.id.btn_close);

        _adapter = new GalleryPagerAdapter(this);
        _pager.setAdapter(_adapter);
        _pager.setOffscreenPageLimit(6); // how many images to load into memory

        fab=(FloatingActionButton)findViewById(R.id.fab);

        // STORE THE FILES IN THE TEMP FOLDER AND SEND THE NEW URI BACK.

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rotflag==0 && cropflag==0){
                    fileglobal=_images.get(0);
                    // 0 used because only file is there, for multiple images use StringArray.
                    Log.i("UP_FILE_PATH","1="+fileglobal);
                    File media=new File(fileglobal);
                    Log.i("UP_FILE_PATH","2="+media.getPath());
                    // Log.i("UP_FILE_PATH","3="+uri.toString());
                    data=new Intent();
                    data.putExtra("path",fileglobal);
                    data.putExtra("uri",urirecv);
                    data.putExtra("caption",caption.getText().toString());
                    setResult(RESULT_OK,data);
                    finish();}


                else if(rotflag==0 && cropflag==1){
                    //SEND A CROPPPED IMAGE.

                    Log.i("UP_FILE_PATH","1="+fileglobal);
                    data=new Intent();
                    data.putExtra("path",fileglobal);
                    data.putExtra("uri",urirecv);
                    data.putExtra("caption",caption.getText().toString());
                    setResult(RESULT_OK,data);
                    finish();


                }
                else if(rotflag==1 && cropflag==0){
                    //SEND A ROTATED IMAGE.

                    fileglobal=_images.get(0);

                    Toast.makeText(getApplicationContext(),fileglobal+"",Toast.LENGTH_SHORT).show();

                    File rotfile=new File(fileglobal);

                    myBitmap = BitmapFactory.decodeFile(fileglobal);
                    try{
                        Bitmap finbitmap=rotate(myBitmap,rot);

                        FileOutputStream out = new FileOutputStream(rotfile);
                        finbitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();}
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    Uri uri=Uri.fromFile(rotfile);
                    createDirectoryAndSaveFile(rotfile.getName(),uri);

                    String filepath=AwsUtils.getPath(getApplicationContext(),uri);

                    fileglobal=filepath;
                    urirecv=uri.toString();
                    data=new Intent();
                    data.putExtra("path",fileglobal);
                    data.putExtra("uri",urirecv);
                    data.putExtra("caption",caption.getText().toString());
                    setResult(RESULT_OK,data);
                    finish();
                }
                else{
                    Toast.makeText(ImagePreviewActivity.this,"try another option.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*
        _closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Close clicked");
                finish();
            }
        });*/
    }

    //method to rotate the bitmap.
    public static Bitmap rotate(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        // source.recycle();
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),source.getHeight(), matrix, false);
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
            //delete
            //temporarily finishing the activity to flush the single image, if MULTIPLE images then modify the below
            //lines to delete items from the list.

            _images.remove(0);
            finish();
        }
        else if(id==R.id.action_crop){
            File file=new File(_images.get(0));
            Uri uri=Uri.fromFile(file);

            //flag the crop with 1.
            cropflag=1;

            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(1, 1)
                    .setFixAspectRatio(true)
                    .start(this);

            // now save this and send this file.
        }
        else if(id== R.id.action_rotate){

            //rotate image.

            imageView.setOrientation(ori);
            if (myBitmap != null)
            {
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());;
            }
            imageView.setImage(ImageSource.bitmap(myBitmap));
            rot=ori;
            Toast.makeText(ImagePreviewActivity.this,rot+"", Toast.LENGTH_SHORT).show();

            ori=(ori+90)%360;
            rotflag=1;

            //Toast.makeText(ImagePreviewActivity.this,"okay3", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){

                resulturi=result.getUri();
                String filepath=AwsUtils.getPath(getApplicationContext(),resulturi);

                savedata(resulturi,filepath);
            }
        }
    }

    public void savedata(Uri uri,String filepath){

        Toast.makeText(ImagePreviewActivity.this, ""+uri, Toast.LENGTH_SHORT).show();

        File filenew;

        filenew = new File(filepath);
        System.out.println(filepath);

        createDirectoryAndSaveFile(filenew.getName(),uri);

        //update the global file parameters.

        fileglobal=filepath;
        urirecv=uri.toString();

        myBitmap = BitmapFactory.decodeFile(filepath);
        imageView.setImage(ImageSource.bitmap(myBitmap));
        thumbView.setImageBitmap(myBitmap);
    }

    private void createDirectoryAndSaveFile(String fileName,Uri resultUri) {


        File direct = new File(Environment.getExternalStorageDirectory() + "/8Chat");

        Bitmap bitmap = null;

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/8Chat/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/8Chat/"), fileName);
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
    }

    class GalleryPagerAdapter extends PagerAdapter {

        Context _context;
        LayoutInflater _inflater;

        public GalleryPagerAdapter(Context context) {
            _context = context;
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return _images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = _inflater.inflate(R.layout.image_preview_item, container, false);
            container.addView(itemView);

            // Get the border size to show around each image
            int borderSize = _thumbnails.getPaddingTop();

            // Get the size of the actual thumbnail image
            int thumbnailSize = ((FrameLayout.LayoutParams)
                    _pager.getLayoutParams()).bottomMargin - (borderSize*2);

            // Set the thumbnail layout parameters. Adjust as required
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
            params.setMargins(0, 0, borderSize, 0);

            // You could also set like so to remove borders
            //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
            //        ViewGroup.LayoutParams.WRAP_CONTENT,
            //        ViewGroup.LayoutParams.WRAP_CONTENT);

            thumbView = new ImageView(_context);
            thumbView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbView.setLayoutParams(params);
            thumbView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Thumbnail clicked");

                    // Set the pager position when thumbnail clicked
                    _pager.setCurrentItem(position);
                }
            });
            _thumbnails.addView(thumbView);

            imageView =
                    (SubsamplingScaleImageView) itemView.findViewById(R.id.image);

            caption=(EmojiEditText) itemView.findViewById(R.id.caption);


            // Asynchronously load the image and set the thumbnail and pager view
            /*
            Glide.with(_context)
                    .load(_images.get(position))
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            imageView.setImage(ImageSource.bitmap(bitmap));
                            thumbView.setImageBitmap(bitmap);
                        }
                    });
                    */

            file=new File(_images.get(position));
            if(file.exists()){

                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImage(ImageSource.bitmap(myBitmap));
                thumbView.setImageBitmap(myBitmap);

            }
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
