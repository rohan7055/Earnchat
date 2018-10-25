package com.rohan7055.earnchat.celgram.events;

public class AlertUser {
    private String message;

    public AlertUser(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
