package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 10/16/2016.
 */
public class ConnectivityEvent {

    private boolean connected;

    public ConnectivityEvent(boolean connected){
        this.connected=connected;
    }

    public boolean isConnected()
    {
      return connected;
    }

}
