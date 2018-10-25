package com.rohan7055.earnchat.celgram;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by manu on 10/17/2016.
 */
public class DateUtils {
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public String getCurrentTime(){
        return df.format(c.getTime());
    }
}
