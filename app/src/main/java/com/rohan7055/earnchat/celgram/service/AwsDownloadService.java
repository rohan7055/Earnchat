package com.rohan7055.earnchat.celgram.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.rohan7055.earnchat.celgram.AwsUtils;
import com.rohan7055.earnchat.celgram.DataBase_Helper.MessageSQLiteHelper;
import com.rohan7055.earnchat.celgram.Message;
import com.rohan7055.earnchat.celgram.events.DownloadEvent;
import com.rohan7055.earnchat.celgram.events.ProgressEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by manu on 12/28/2016.
 */

public class AwsDownloadService extends IntentService{
    String url,folder,bucket,path,initials,extension;
    EventBus eventBus;
    boolean notified=false;
    MessageSQLiteHelper msg_helper;
    Message message;
    Context context;
    ArrayList<TransferState> paused=new ArrayList<>();

    public AwsDownloadService(String name){
        super(name);
        setIntentRedelivery(true);
    }
    public AwsDownloadService(){
        super("download");
        setIntentRedelivery(true);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        message=(Message)intent.getExtras().getSerializable("message");
        eventBus=EventBus.getDefault();
        context=getApplicationContext();
        msg_helper=new MessageSQLiteHelper(context);
        notified=false;
        boolean resume=false;
        paused.add(TransferState.CANCELED);
        paused.add(TransferState.FAILED);
        paused.add(TransferState.PAUSED);



        if(message.getMsg_status()==-1){
            bucket="earnchatthumb";
            path=context.getFilesDir()+"/Thumbnails";
            initials="thumb";
            extension="png";
        }
        else {
            extension=message.getMime_type();
            switch (message.getMsg_type()) {
                case 1:
                    bucket = "earnchatimages";
                    folder = "images";
                    initials="image";
                    break;

                case 2:
                    bucket = "earnchatvideos";
                    folder = "videos";
                    initials="video";
                    break;

                case 3:
                    bucket = "earnchataudios";
                    folder = "audios";
                    initials="audio";
                    break;

                case 4:
                    bucket = "earnchatdocuments";
                    folder = "documents";
                    initials="document";
                    break;

                default:
                    break;
            }

            path = Environment.getExternalStorageDirectory().getPath().toString()+"/EarnChat/Media/"+folder;
        }


        if(message.getAws_id()==-1){
            resume=false;
        }
        else{
            TransferState state=AwsUtils.getObserver(message.getAws_id(),context).getState();
            if(paused.contains(state)){
                resume=true;
            }
            else{
                resume=false;
            }

        }

        if(!resume) {
            File FPath = new File(path);
            if (!FPath.exists())
                FPath.mkdirs();

            final String fname = initials + System.currentTimeMillis() + "." + extension;
            final File f = new File(FPath, fname);
            try {
                f.createNewFile();
            } catch (IOException e) {
                Log.e("FileErrror", e.getMessage());

            }

            TransferUtility transferUtility = AwsUtils.getTransferUtility(context);
            final TransferObserver observer = transferUtility.download(
                    bucket,     /* The bucket to download from */
                    message.getMsg_url(),    /* The key for the object to download */
                    f        /* The file to download the object to */
            );


            if (message.getMsg_status() == -1) {
                msg_helper.updateAwsId(message.getId(), observer.getId());
                message.setAws_id(observer.getId());
                message.setThumb_path(f.getPath());
                msg_helper.updateThumbPath(message.getId(), f.getPath());
            } else {
                message.setFile_path(f.getPath());
                msg_helper.updateFilePath(message.getId(), f.getPath());
                message.setAws_id(observer.getId());
                msg_helper.updateAwsId(message.getId(), observer.getId());
                DownloadEvent event = new DownloadEvent(message, false);
                eventBus.post(event);
                message.setMsg_status(-2);
                msg_helper.updateSingleMessageStatus(message.getId(), -2);
            }

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
                        final int percentage = (int) (bytesCurrent * 100 / bytesTotal);
                        Log.i("success", "downloaded data is " + bytesTotal);
                        if(percentage>0){
                            ProgressEvent event=new ProgressEvent(message.getId(),percentage);
                            eventBus.post(event);
                        }
                        if (percentage == 100) {
                            if (!notified) {
                                notified = true;
                                if (message.getMsg_status() == -1) {
                                    message.setMsg_status(0);
                                    msg_helper.updateSingleMessageStatus(message.getId(), 0);
                                    eventBus.post(message);
                                } else {
                                    msg_helper.updateSingleMessageStatus(message.getId(), 1);
                                    DownloadEvent event = new DownloadEvent(message, false);
                                    eventBus.post(event);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onError(int id, Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Unable to download", Toast.LENGTH_SHORT).show();
                    boolean pause = AwsUtils.getTransferUtility(context).pause(id);
                    if (!pause) {
                        AwsUtils.getTransferUtility(context).cancel(id);
                    }
                    DownloadEvent event = new DownloadEvent(message, true);
                    eventBus.post(event);
                }
            });
        }
        else{
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
                        final int percentage = (int) (bytesCurrent * 100 / bytesTotal);
                        Log.i("success", "downloaded data is " + bytesTotal);
                        if(percentage>0){
                            ProgressEvent event=new ProgressEvent(message.getId(),percentage);
                            eventBus.post(event);
                        }

                        if (percentage == 100) {
                            if (!notified) {
                                notified = true;
                                msg_helper.updateSingleMessageStatus(message.getId(), 1);
                                    DownloadEvent event = new DownloadEvent(message, false);
                                    eventBus.post(event);
                            }
                        }
                    }
                }

                @Override
                public void onError(int id, Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Unable to download", Toast.LENGTH_SHORT).show();
                    boolean pause = AwsUtils.getTransferUtility(context).pause(id);
                    if (!pause) {
                        AwsUtils.getTransferUtility(context).cancel(id);
                    }
                    DownloadEvent event = new DownloadEvent(message, true);
                    eventBus.post(event);
                }
            });
        }

    }
}
