package com.rohan7055.earnchat;

/**
 * Created by Karthik on 02-07-2016.
 */
import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

import com.rohan7055.earnchat.celgram.service.RestartReceiver;


public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Foreground.init(this);
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        RestartReceiver receiver = new RestartReceiver();
        registerReceiver(receiver, filter);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

   }
