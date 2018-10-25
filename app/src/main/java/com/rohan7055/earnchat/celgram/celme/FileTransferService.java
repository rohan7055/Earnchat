// Copyright 2011 Google Inc. All Rights Reserved.

package com.rohan7055.earnchat.celgram.celme;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.rohan7055.earnchat.celgram.ChatWindow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService{

	private static final int SOCKET_TIMEOUT = 5000;
	public static final String ACTION_SEND_FILE = "com.admybrand.chat8.celgram.celme.SEND_FILE";
	public static final String EXTRAS_FILE_PATH = "file_url";
	public static final String EXTRAS_ADDRESS = "go_host";
	public static final String EXTRAS_PORT = "go_port";
	byte buf[]  = new byte[1024];
	int len;

	public FileTransferService(String name) {
		super(name);
	}

	public FileTransferService() {
		super("FileTransferService");

	}

	public void showToast(String message) {
		final String msg = message;
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}
	/*
	 * (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		//showToast("MyService is handling intent.");
		Context context = getApplicationContext();
		if (intent.getAction().equals(ACTION_SEND_FILE)) {
			String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
			String host = intent.getExtras().getString(EXTRAS_ADDRESS);
			Socket socket = new Socket();
			int port = intent.getExtras().getInt(EXTRAS_PORT);

			try {
				Log.d(ChatWindow.TAG, "Opening client socket - ");
			//	showToast("Opening socket");

				socket.bind(null);
				socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

				Log.d(ChatWindow.TAG, "Client socket - " + socket.isConnected());
			//	showToast("socket ="+socket.isConnected());
				OutputStream stream = socket.getOutputStream();
				ContentResolver cr = context.getContentResolver();
				InputStream is = null;
				try {
					is = cr.openInputStream(Uri.parse(fileUri));
				} catch (FileNotFoundException e) {
					Log.d(ChatWindow.TAG, e.toString());
				}
				if(is!=null){
				Log.d("Checkpoint1","is not null");}
				Log.d("Checkpoint2","Proceed to copy");
				try {

					while ((len = is.read(buf)) != -1) {

						stream.write(buf, 0, len);

					}
					Log.d("WHILE EXIT","WRITTEN");
				} catch (Exception e) {
					Log.d(ChatWindow.TAG, e.toString());

				}
				Log.d("Writing","Finished");
				socket.close();
				stream.close();
				is.close();
				Log.d(ChatWindow.TAG, "Client: Data written");
			} catch (IOException e) {
				//showToast(e.getMessage());
			} finally {
				if (socket != null) {
					if (socket.isConnected()) {
						try {

							socket.close();
							//showToast("closing socket");
						} catch (IOException e) {
						//	showToast("closing socket:"+e.getMessage());
						}
					}
				}
			}

		}
	}
}
