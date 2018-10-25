package com.rohan7055.earnchat.celgram.collapsingToolbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rohan7055.earnchat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeaderView extends LinearLayout {
    @Nullable
    @BindView(R.id.contact_group_name)
    TextView contact_group_name;

    @Nullable
    @BindView(R.id.creation_details)
    TextView creation_details;

    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void bindTo(String contact_group_name, String creation_details) {
        this.contact_group_name.setText(contact_group_name);
        this.creation_details.setText(creation_details);

    }

    public void setTextSize(float size) {
        contact_group_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }
}
