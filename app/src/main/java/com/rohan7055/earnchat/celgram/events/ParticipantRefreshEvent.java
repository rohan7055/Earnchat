package com.rohan7055.earnchat.celgram.events;

/**
 * Created by rohan on 1/5/2018.
 */

public class ParticipantRefreshEvent {
    private boolean isrefresh;

    public ParticipantRefreshEvent(boolean isrefresh) {
        this.isrefresh = isrefresh;
    }

    public boolean isrefresh() {
        return isrefresh;
    }
}
