package com.rohan7055.earnchat.celgram.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.rohan7055.earnchat.MyApplication;
import com.rohan7055.earnchat.celgram.Message;

public class RestartReceiver extends BroadcastReceiver {
    boolean isServiceRunning = false;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(RestartReceiver.class.getSimpleName(), "Restart Receiver is called");
        this.context=context;
        boolean isRestart=intent.getBooleanExtra("RESTART",false);
        if(intent.getAction()!=null) {
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                if (isMyServiceRunning(MessageReceive.class)) {
                    Log.i(RestartReceiver.class.getSimpleName(), "Service is Running ACTION_TIME_TICK");
                    isServiceRunning = true;
                } else {
                    Log.i(RestartReceiver.class.getSimpleName(), "Service is not Running ACTION_TIME_TICK");
                    Intent i = new Intent(context, MessageReceive.class);
                    context.startService(i);
                    isServiceRunning = true;
                    Log.i(RestartReceiver.class.getSimpleName(), "Service is Restarted and Running ACTION_TIME_TICK");

                }

            }

            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
                //start your sercice
                if (!isMyServiceRunning(MessageReceive.class)) {
                    Intent i = new Intent(context, MessageReceive.class);
                    context.startService(i);
                    isServiceRunning = true;
                    Log.i(RestartReceiver.class.getSimpleName(), "Service is Restarted and Running on BOOTUP");

                } else {
                    Log.i(RestartReceiver.class.getSimpleName(), "Service is Running on BOOTUP");

                }
            }
        }

        if(isRestart)
        {
            Log.i(RestartReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!! on Destroy");

            context.startService(new Intent(context, MessageReceive.class));;
            Log.i(RestartReceiver.class.getSimpleName(), "Service is Restarted and Running on Destroy");


        }else{
            if (!isMyServiceRunning(MessageReceive.class)) {
                Intent i = new Intent(context, MessageReceive.class);
                context.startService(i);
                isServiceRunning = true;
                Log.i(RestartReceiver.class.getSimpleName(), "Service is Restarted and Running on ALARM");

            } else {
                Log.i(RestartReceiver.class.getSimpleName(), "Service is Running on ALARM");

            }
        }
        try{
            AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent1=new Intent(context,RestartReceiver.class);
            PendingIntent alarmIntent=PendingIntent.getBroadcast(context,1545,intent1,0);
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + 5000, alarmIntent);
            }else{
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, alarmIntent);

            }
        }catch (Exception e)
        {
            Log.i(RestartReceiver.class.getSimpleName(),e.getMessage());
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
