package com.android.boltt.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class VerticalPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "VerticalPagerAdapter";
    private final Activity activity;

    public VerticalPagerAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        /*Log.d(TAG, "Visbility: " + ((AnimationActivity2)activity).getBottomBarVisibility());
        switch (position) {
            case 0:
                return new NutritionMainFragment();
            case 1:
                return new NutritionalInsightsFragment();
            case 2:
                return new VitaminsAndMineralsFragment();
            case 3:
                return new ExampleFragment();
            default:
                return null;
        }*/
        // Todo replace this return statement with a legit one
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}