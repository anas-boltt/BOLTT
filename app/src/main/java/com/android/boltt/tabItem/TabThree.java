package com.android.boltt.tabItem;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.boltt.R;

/**
 * Created by boltt on 25/2/16.
 */
public class TabThree extends Fragment {

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.tabone, null);
        return v;
    }
}
