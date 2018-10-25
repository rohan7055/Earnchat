package com.rohan7055.earnchat.celgram.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.rohan7055.earnchat.celgram.AwsUtils;
import com.rohan7055.earnchat.celgram.DataBase_Helper.MessageSQLiteHelper;
import com.rohan7055.earnchat.celgram.Message;
import com.rohan7055.earnchat.celgram.Utils;
import com.rohan7055.earnchat.celgram.events.AwsEvent;
import com.rohan7055.earnchat.celgram.events.MessageEvent;
import com.rohan7055.earnchat.celgram.events.ProgressEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by manu on 12/28/2016.
 */

public class AwsUploadService extends IntentService {
    Context context;
    Message message;
    String bucket,name;
    EventBus eventBus;
    boolean notified = false,thumb_uploaded=false,upload_thumb=false;
    TransferUtility transferUtility;
    MessageSQLiteHelper msg_helper;
    int percentage;
    ArrayList<TransferState> paused=new ArrayList<>();

    public AwsUploadService(String name){super(name);
    setIntentRedelivery(true);}

    public AwsUploadService(){
        super("upload");
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        message=(Message)intent.getExtras().getSerializable("message");
        eventBus=EventBus.getDefault();
        context=getApplicationContext();
        msg_helper=new MessageSQLiteHelper(context);
        thumb_uploaded=false;
        upload_thumb=false;
        notified=false;
        name=msg_helper.getMsgUrl(message.getId());
        if(name!=null){
        }
        else{
        name=message.getConvo_partner()+ System.currentTimeMillis();
        msg_helper.setMsgUrl(message.getId(),name);}

        paused.add(TransferState.CANCELED);
        paused.add(TransferState.FAILED);
        paused.add(TransferState.PAUSED);


        switch (message.getMsg_type()) {
            case 1:
                bucket = "earnchatimages";
                upload_thumb=true;
                break;

            case 2:
                bucket = "earnchatvideos";
                upload_thumb=true;
                break;

            case 3:
                bucket = "earnchataudios";
                break;

            case 4:
                bucket = "earnchatdocuments";
                break;

            default:
                break;
        }

        transferUtility = AwsUtils.getTransferUtility(context);

        if(upload_thumb){
            if(message.getMsg_status()==-2) {
               uploadThumb(new File(message.getFile_path()), name, message.getMsg_type(), message.getId(), context);
            }
            else if(message.getMsg_status()==-1){
                uploadMedia(new File(message.getFile_path()),name,message.getId(),context);
            }
        }
        else{
            uploadMedia(new File(message.getFile_path()),name,message.getId(),context);
        }
    }

    private void uploadThumb(final File file, final String name, final int msg_type, final String msg_id, final Context context){
        Log.i("Upload_Check","upload_thumb");
        if(message.getAws_id()==-1) {

            Bitmap thumb;
            if (msg_type == 1) {

                thumb = Utils.blur(context, Utils.getbitmap(file.getPath()));

            } else {
                thumb = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            }

            try {
                final File f = new File(context.getFilesDir(), name);
                if(!f.exists()){
                f.createNewFile();
                }


                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                if (msg_type == 1) {
                    thumb.compress(Bitmap.CompressFormat.PNG, 0, bos);
                } else {
                    thumb.compress(Bitmap.CompressFormat.JPEG, 20, bos);
                }
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                message.setThumb_path(f.getPath());
                message.setMsg_url(name);
                msg_helper.updateThumbPath(msg_id, f.getPath());


                final TransferObserver observer = transferUtility.upload(
                        "earnchatthumb",        /* The bucket to upload to */
                        name,       /* The key for the uploaded object */
                        f          /* The file where the data to upload exists */
                      /* The ObjectMetadata associated with the object*/
                );


                msg_helper.updateAwsId(msg_id, observer.getId());
                message.setAws_id(observer.getId());
                AwsEvent awsEvent = new AwsEvent(msg_id, observer.getId(), -2, false);
                eventBus.post(awsEvent);


                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState transferState) {
                        if(transferState.equals(TransferState.COMPLETED)||paused.contains(transferState)){
                            observer.cleanTransferListener();
                        }

                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if (bytesTotal > 0) {

                            percentage = (int) (bytesCurrent * 100 / bytesTotal);
                            if (percentage == 100) {
                                if (!thumb_uploaded) {

                                    thumb_uploaded = true;
                                    message.setMsg_status(-1);
                                    msg_helper.updateSingleMessageStatus(msg_id, -1);
                                    uploadMedia(file, name, msg_id, context);
                                }
                            }

                        }
                    }


                    @Override
                    public void onError(int id, Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Some error occurred while uploading", Toast.LENGTH_SHORT).show();
                        boolean pause=AwsUtils.getTransferUtility(context).pause(id);
                        if(!pause){
                            AwsUtils.getTransferUtility(context).cancel(id);
                        }
                        AwsEvent event = new AwsEvent(message.getId(), id, 0, true);
                        eventBus.post(event);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            final TransferObserver observer=AwsUtils.getObserver(message.getAws_id(),context);
            if(!observer.getState().equals(TransferState.COMPLETED)) {
                AwsUtils.getTransferUtility(context).resume(message.getAws_id());

                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState transferState) {
                        if(transferState.equals(TransferState.COMPLETED)||paused.contains(transferState)){
                            observer.cleanTransferListener();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if (bytesTotal > 0) {

                            percentage = (int) (bytesCurrent * 100 / bytesTotal);

                            if (percentage == 100) {
                                if (!thumb_uploaded) {
                                        thumb_uploaded = true;
                                        message.setMsg_status(-1);
                                        msg_helper.updateSingleMessageStatus(msg_id, -1);
                                        uploadMedia(file, name, msg_id, context);

                                }
                            }

                        }
                    }


                    @Override
                    public void onError(int id, Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Some error occurred while uploading", Toast.LENGTH_SHORT).show();
                        boolean pause = AwsUtils.getTransferUtility(context).pause(id);
                        if (!pause) {
                            AwsUtils.getTransferUtility(context).cancel(id);
                        }
                        AwsEvent event = new AwsEvent(message.getId(), id, 0, true);
                        eventBus.post(event);
                    }
                });
            }
            else{
                thumb_uploaded=true;
                message.setMsg_status(-1);
                msg_helper.updateSingleMessageStatus(msg_id, -1);
                uploadMedia(file, name, msg_id, context);

            }
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void uploadMedia(final File file, final String name, final String msg_id, final Context context) {

        Log.i("Upload_Check","upload_media");
        boolean resume;
        int aws_id=message.getAws_id();

        if(aws_id==-1){
            resume=false;
        }
        else{
            TransferState state = AwsUtils.getObserver(aws_id, context).getState();
            Log.i("CHECK_STATE",""+state);
            if(paused.contains(state)){
                resume=true;
            }
            else{
                resume=false;
            }
        }

        if (!resume) {

            Log.i("CHECK_RESUME","FALSE");
            final TransferObserver observer = transferUtility.upload(
                    bucket,        /* The bucket to upload to */
                    name,       /* The key for the uploaded object */
                    file          /* The file where the data to upload exists */
                      /* The ObjectMetadata associated with the object*/
            );

            msg_helper.updateAwsId(msg_id, observer.getId());
            AwsEvent awsEvent = new AwsEvent(msg_id, observer.getId(), -1, false);
            eventBus.post(awsEvent);


            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState transferState) {

                    if(transferState.equals(TransferState.COMPLETED)||paused.contains(transferState)){
                        observer.cleanTransferListener();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    if (bytesTotal > 0) {
                        percentage = (int) (bytesCurrent * 100 / bytesTotal);
                        if(percentage>0){
                            ProgressEvent event=new ProgressEvent(msg_id,percentage);
                            eventBus.post(event);
                        }
                        if (percentage == 100) {

                            if (!notified) {
                                notified = true;
                                message.setMsg_url(name);
                                Log.i("MSG_URL",name);
                                message.setMsg_status(0);
                                msg_helper.updateSingleMessageStatus(msg_id, 0);
                                MessageEvent event = new MessageEvent(message);
                                eventBus.post(event);
                            }
                        }

                    }
                }

                @Override
                public void onError(int id, Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Some error occurred while uploading", Toast.LENGTH_SHORT).show();
                    boolean pause=AwsUtils.getTransferUtility(context).pause(id);
                    if(!pause){
                        AwsUtils.getTransferUtility(context).cancel(id);
                    }
                    AwsEvent event = new AwsEvent(message.getId(), id, 0, true);
                    eventBus.post(event);

                }
            });


        }
        else{
            Log.i("CHECK_RESUME","TRUE");
            AwsUtils.getTransferUtility(context).resume(message.getAws_id());
            final TransferObserver observer=AwsUtils.getObserver(message.getAws_id(),context);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState transferState) {
                    if(transferState.equals(TransferState.COMPLETED)||paused.contains(transferState)){
                        observer.cleanTransferListener();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    if (bytesTotal > 0) {
                        percentage = (int) (bytesCurrent * 100 / bytesTotal);
                        if(percentage>0){
                            ProgressEvent event=new ProgressEvent(msg_id,percentage);
                            eventBus.post(event);
                        }
                        if (percentage == 100) {

                            if (!notified) {
                                notified = true;
                                message.setMsg_status(0);
                                message.setMsg_url(name);
                                Log.i("MSG_URL",name);
                                msg_helper.updateSingleMessageStatus(msg_id, 0);
                                MessageEvent event = new MessageEvent(message);
                                eventBus.post(event);
                            }
                        }

                    }
                }

                @Override
                public void onError(int id, Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Some error occurred while uploading", Toast.LENGTH_SHORT).show();
                    boolean pause=AwsUtils.getTransferUtility(context).pause(id);
                    if(!pause){
                        AwsUtils.getTransferUtility(context).cancel(id);
                    }
                    AwsEvent event = new AwsEvent(message.getId(), id, 0, true);
                    eventBus.post(event);

                }
            });
        }

    }


}
