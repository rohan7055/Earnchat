package com.rohan7055.earnchat.celgram;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rohan7055.earnchat.R;

/**
 * Created by manu on 10/9/2016.
 */
public class NotificationHolder extends RecyclerView.ViewHolder {

    private TextView notification_text;

    public NotificationHolder(View v) {
        super(v);
        notification_text = (TextView) v.findViewById(R.id.notification_text);

    }

    public TextView getNotification_text() {
        return notification_text;
    }

    public void setNotification_text(TextView notification_text) {
        this.notification_text = notification_text;
    }



}
