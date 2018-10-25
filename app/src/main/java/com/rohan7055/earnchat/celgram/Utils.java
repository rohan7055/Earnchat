package com.rohan7055.earnchat.celgram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

/**
 * Created by manu on 8/17/2016.
 */
public class Utils {

    private static final float BITMAP_SCALE = 1.0f;
    private static final float BLUR_RADIUS = 1.5f;

    public static String md5(String md5){
        try{
            java.security.MessageDigest md=java.security.MessageDigest.getInstance("MD5");
            byte [] array=md.digest(md5.getBytes());
            StringBuffer sb=new StringBuffer();
            for (int i=0;i<array.length;++i){
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            Log.i ("Success","generated token"+sb.toString());
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Bitmap blur(Context context, Bitmap image) {
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    public static String getMediaSize(long duration){
        String dur;
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        double kb=duration/1024;
        if(kb>=1){
            if(kb>=1024){
                return numberFormat.format(kb/1024)+" mb ";
            }
            else{
                return String.valueOf((int)kb)+" kb ";
            }
        }
        else{
            return "0"+numberFormat.format(kb)+" kb ";
        }
    }

    public static Bitmap getbitmap(String path){
        Bitmap imgthumBitmap=null;
        try
        {

            final int THUMBNAIL_SIZE = 64;

            FileInputStream fis = new FileInputStream(path);
            imgthumBitmap = BitmapFactory.decodeStream(fis);

            imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap,
                    THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

            ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
            imgthumBitmap.compress(Bitmap.CompressFormat.JPEG, 100,bytearroutstream);


        }
        catch(Exception ex) {

        }
        return imgthumBitmap;
    }
}
