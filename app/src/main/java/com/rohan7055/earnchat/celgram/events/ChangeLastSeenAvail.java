package com.rohan7055.earnchat.celgram.events;

public class ChangeLastSeenAvail {
    private Boolean available;
    private String lastseen;

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getLastseen() {
        return lastseen;
    }

    public void setLastseen(String lastseen) {
        this.lastseen = lastseen;
    }

    public ChangeLastSeenAvail(Boolean available) {
        this.available = available;
    }
}
