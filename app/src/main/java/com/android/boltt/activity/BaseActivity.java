package com.android.boltt.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * This is the parent activity of every activity in this app
 * Make sure you extend this BaseActivity to every Activity you
 * make.
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
