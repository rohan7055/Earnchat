package com.rohan7055.earnchat.celgram;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.webkit.MimeTypeMap;

import com.rohan7055.earnchat.CelgramMain;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by manu on 10/17/2016.
 */
public class CelgramUtils {
    private static Calendar c = Calendar.getInstance();
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static String getCurrentTime(){
        return df.format(c.getTime());
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }
        return extension;
    }

    public static long getCurrentTs(Message message){
        long currentTs=0;
        String dateCurr=message.getTimestamp();
        if (dateCurr != null) {
            SimpleDateFormat sdc = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
            Date currDate = null;
            try {
                currDate = sdc.parse(dateCurr);
            } catch (ParseException e) {

                e.printStackTrace();
            }

            currentTs = currDate.getTime();

        }

        return currentTs;
    }

    public static long getPreviousTs(Message message,int position,List<Object> listData){
        long previousTs = 0;
        if(position>0) {
            Message pm = (Message) listData.get(position - 1);

            String dateString = pm.getTimestamp();
            if (dateString != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                Date testDate = null;
                try {
                    testDate = sdf.parse(dateString);
                } catch (ParseException e) {

                    e.printStackTrace();
                }

                previousTs = testDate.getTime();

            }
        }
        return previousTs;
    }

    public static String getMessageBrief(Message message) {
        String text;
        if(message.getConvo_type()==0) {
            switch (message.getMsg_type()) {
                case 0:
                    text = (message.isSelf() ? "Me: " : "") + message.getData();
                    break;
                case 1:
                    text = (message.isSelf() ? "Me: " : "") + "image " + "\uD83D\uDCF7";
                    break;
                case 2:
                    text = (message.isSelf() ? "Me: " : "") + "video " + "\uD83C\uDFA5";
                    break;
                case 3:
                    text = (message.isSelf() ? "Me: " : "") + "audio " + "\uD83D\uDD08";
                    break;
                case 4:
                    text = (message.isSelf() ? "Me: " : "") + "document " + Html.fromHtml("&#128195;");
                    break;
                case 5:
                    text = (message.isSelf() ? "Me: " : "") + "contact  " + Html.fromHtml("&#9742;");
                    break;
                case 6:
                    text = (message.isSelf() ? "Me: " : "") + "STICKER";
                    break;
                case 13:
                    //text = (message.isSelf() ? "Me: " : "") + "location " + "\uD83D\uDCCD";
                    text = (message.isSelf() ? "Me: " : "") + "location " + "\uD83D\uDCCC";
                    break;
                case 14:
                    text = (message.isSelf() ? "Me: " : "") + message.getData();
                    break;
                default:
                    text = "";
                    break;

            }
        }
        else{
            String sender;

            if(CelgramMain.number_name.get(message.getSender_id())==null){
                sender=message.getSender_id();
            }
            else{
                sender= CelgramMain.number_name.get(message.getSender_id());
            }
            switch (message.getMsg_type()) {
                case 0:
                    text = (message.isSelf() ? "Me: " :sender+ ":") + message.getData();
                    break;
                case 1:
                    text = (message.isSelf() ? "Me: " : sender+ ":") + "image " + "\uD83D\uDCF7";
                    break;
                case 2:
                    text = (message.isSelf() ? "Me: " : sender+ ":") + "video " + "\uD83C\uDFA5";
                    break;
                case 3:
                    text = (message.isSelf() ? "Me: " : sender+ ":") + "audio " + "\uD83D\uDD08";
                    break;
                case 4:
                    text = (message.isSelf() ? "Me: " : sender+ ":") + "document " + Html.fromHtml("&#128195;");
                    break;
                case 5:
                    text = (message.isSelf() ? "Me: " : sender+ ":") + "contact  " + Html.fromHtml("&#9742;");
                    break;
                case 6:
                    text = (message.isSelf() ? "Me: " :sender+ ":") + "STICKER";
                    break;
                case 13:
                    text = (message.isSelf() ? "Me: " : sender+ ":") + "location " + "\uD83D\uDCCC";
                    break;
                case 14:
                    text = (message.isSelf() ? "Me: " : sender+ ":") + message.getData();
                    break;
                default:text=message.getNotificationBody();
                    break;
            }
        }

        return text;
    }

}