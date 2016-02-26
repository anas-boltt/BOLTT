package com.android.boltt.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.android.boltt.R;
import com.android.boltt.tabItem.TabFive;
import com.android.boltt.tabItem.TabFour;
import com.android.boltt.tabItem.TabOne;
import com.android.boltt.tabItem.TabThree;
import com.android.boltt.tabItem.TabTwo;


public class MainTabPage extends FragmentActivity {

	private static final String TAB_1_TAG = "tab_1";
	private static final String TAB_2_TAG = "tab_2";
	private static final String TAB_3_TAG = "tab_3";
	private static final String TAB_4_TAG = "tab_4";
	private static final String TAB_5_TAG = "tab_5";

	private FragmentTabHost mTabHost;

	private TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_page);
		InitView();


		tv = (TextView) mTabHost.getCurrentTabView().findViewById(
				R.id.txt_tabtxt); // selected Tabs
		tv.setTextColor(Color.parseColor("#337ba6"));

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub

				Log.d("","Which Tab: " +tabId);
				for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
					tv = (TextView) mTabHost.getTabWidget().getChildAt(i)
							.findViewById(R.id.txt_tabtxt); // Unselected Tabs
					tv.setTextColor(Color.parseColor("#919191"));
					//mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#8A4117"));
				}

				tv = (TextView) mTabHost.getCurrentTabView().findViewById(
						R.id.txt_tabtxt); // selected Tabs
				tv.setTextColor(Color.parseColor("#337ba6"));

				//mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#C35817"));
/*
				if(tabId.equals("tab_2"))
				{

					mPager.setAdapter(mEnergyAdapter);

				}*/
			}
		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	private void InitView() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

		mTabHost.setup(this, getSupportFragmentManager(), R.id.maintabcontent);

		mTabHost.addTab(
				setIndicator(MainTabPage.this, mTabHost.newTabSpec(TAB_1_TAG),
						R.drawable.ic_place, R.string.tab_contact),
				TabOne.class, null);

		mTabHost.addTab(
				setIndicator(MainTabPage.this, mTabHost.newTabSpec(TAB_2_TAG),
						R.drawable.ic_place, R.string.tab_dialpad),
				TabTwo.class, null);
		mTabHost.addTab(
				setIndicator(MainTabPage.this, mTabHost.newTabSpec(TAB_3_TAG),
						R.drawable.ic_place, R.string.tab_home),
				TabThree.class, null);
		mTabHost.addTab(
				setIndicator(MainTabPage.this, mTabHost.newTabSpec(TAB_4_TAG),
						R.drawable.ic_place, R.string.tab_credit),
				TabFour.class, null);
		mTabHost.addTab(
				setIndicator(MainTabPage.this, mTabHost.newTabSpec(TAB_5_TAG),
						R.drawable.ic_place, R.string.tab_setting),
				TabFive.class, null);

		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {


		}
	}

	/*
	 * @Override public void onBackPressed() { boolean isPopFragment = false;
	 * String currentTabTag = mTabHost.getCurrentTabTag();
	 * 
	 * if (currentTabTag.equals(TAB_1_TAG)) { isPopFragment =
	 * ((BaseContainerFragment) getSupportFragmentManager()
	 * .findFragmentByTag(TAB_1_TAG)).popFragment(); } else if
	 * (currentTabTag.equals(TAB_2_TAG)) { isPopFragment =
	 * ((BaseContainerFragment) getSupportFragmentManager()
	 * .findFragmentByTag(TAB_2_TAG)).popFragment(); } else if
	 * (currentTabTag.equals(TAB_3_TAG)) { isPopFragment =
	 * ((BaseContainerFragment) getSupportFragmentManager()
	 * .findFragmentByTag(TAB_3_TAG)).popFragment(); }
	 * 
	 * if (!isPopFragment) { finish(); } }
	 */

	private TabSpec setIndicator(Context ctx, TabSpec spec, int resid,
			int string) {

		View v = LayoutInflater.from(ctx).inflate(R.layout.tab_item, null);
		tv = (TextView) v.findViewById(R.id.txt_tabtxt);
		ImageView img = (ImageView) v.findViewById(R.id.img_tabtxt);

		tv.setText(string);

		img.setBackgroundResource(resid);
		return spec.setIndicator(v);
	}

	public void refresh_frag() {

		Intent intent = getIntent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
		finish();
		overridePendingTransition(0, 0);
		
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		
	}

}