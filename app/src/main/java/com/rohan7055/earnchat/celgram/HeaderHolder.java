package com.rohan7055.earnchat.celgram;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rohan7055.earnchat.R;

public class HeaderHolder extends RecyclerView.ViewHolder {

    TextView Day;


    public HeaderHolder(View view)
    {
        super(view);
        Day=(TextView)view.findViewById(R.id.day);


    }

    public TextView getDay(){return Day;}

}
