package com.rohan7055.earnchat.celgram.celme;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.rohan7055.earnchat.celgram.ChatWindow;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by manu on 7/16/2016.
 */
public class SendMessageService extends IntentService {

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "com.admybrand.chat8.celgram.celme.SEND_FILE";
    public static final String MESSAGE = "message";
    public static final String EXTRAS_ADDRESS = "go_host";
    public static final String EXTRAS_PORT = "go_port";

    public SendMessageService(String name) {
        super(name);
    }

    public SendMessageService   () {
        super("SendIpService");

    }

    public void showToast(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            String host = intent.getExtras().getString(EXTRAS_ADDRESS);
            String message=intent.getExtras().getString(MESSAGE);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_PORT);
            DataOutputStream stream = null;
            if(message.equals("ping")){
               // showToast("Sending ip");
            }
            else{
                //showToast("Sending Message");
            }
            try {

                socket.bind(null);
                Log.d("Connecting", "opening client socket ");
               // showToast("Opening socket for ip/msg");
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                Log.d("Connected", "Client socket - " + socket.isConnected());
               // showToast("Socket for ip/msg "+socket.isConnected());
                stream = new DataOutputStream(socket.getOutputStream());
                stream.writeUTF(message);

                stream.close();
                socket.close();
                if(message.equals("ping")){
                    ChatWindow.ip_sent=true;
                }


            }
            catch (IOException e) {
                Log.e("SEND IP SERVICE", e.getMessage());
               // showToast(e.getMessage());
                ChatWindow.retry=true;


            }

            finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                            //showToast("Closing ip/msg socket");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }
}
