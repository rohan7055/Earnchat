package com.rohan7055.earnchat.intro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rohan7055.earnchat.R;

/**
 * Created by Anuraj on 7/24/2017.
 */

public class fragment2 extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.intro_fragment3, container, false);
        return v;
    }

}
