package com.rohan7055.earnchat.celgram;

import com.rohan7055.earnchat.MyApplication;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by manu on 8/9/2016.
 */
public class WsConfig extends MyApplication {

    private Socket msocket;

    public Socket getSocket() {
        return msocket;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            msocket = IO.socket("http://192.168.0.14:3000/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

