package com.android.boltt.tabItem;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.boltt.R;
import com.android.boltt.adapter.EnergyVerticalPagerAdapter;

/**
 * Created by boltt on 25/2/16.
 */
public class TabTwo extends Fragment {

    private View v;
    private EnergyVerticalPagerAdapter mEnergyAdapter;
    private ViewPager mPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_nutrition, null);

        mEnergyAdapter = new EnergyVerticalPagerAdapter(getActivity().getSupportFragmentManager(),getActivity());
        mPager = (ViewPager)v.findViewById(R.id.pager);
        mPager.setAdapter(mEnergyAdapter);


        return v;
    }

}
