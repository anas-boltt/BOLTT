package com.android.boltt.energy_ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.boltt.R;

/**
 * Created by boltt on 26/2/16.
 */
public class EnergyScreenThree extends Fragment {

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.energy_screen_three, null);
        return v;
    }
}
