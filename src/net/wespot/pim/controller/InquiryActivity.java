/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wespot.pim.controller;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import daoBase.DaoConfiguration;
import net.wespot.pim.R;
import net.wespot.pim.controller.Adapters.InquiryPagerAdapter;
import net.wespot.pim.utils.layout.BaseFragmentActivity;
import net.wespot.pim.view.InqCreateInquiryFragment;
import org.celstec.arlearn.delegators.INQ;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class InquiryActivity extends BaseFragmentActivity implements ActionBar.TabListener, ViewPager.OnPageChangeListener{

    private static final String TAG = "InquiryActivity";
    public static final String PHASE = "num_phase";

    private static final String CURRENT_INQUIRY = "currentInquiry";
    private static final String CURRENT_INQUIRY_RUN = "currentInquiryRun";


    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    private ViewPager mViewPager;

    public InquiryActivity() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (INQ.inquiry.getCurrentInquiry() == null){
            Log.e(TAG, "Back pressed - New inquiry");
        }else{
            Log.e(TAG, "Back pressed - Show inquiry");
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(CURRENT_INQUIRY, INQ.inquiry.getCurrentInquiry().getId());
        outState.putLong(CURRENT_INQUIRY_RUN, INQ.inquiry.getCurrentInquiry().getRunLocalObject().getId());

    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            INQ.init(this);
            INQ.accounts.syncMyAccountDetails();
            INQ.inquiry.setCurrentInquiry(DaoConfiguration.getInstance().getInquiryLocalObjectDao().load(
                            savedInstanceState.getLong(CURRENT_INQUIRY) ));

            INQ.inquiry.getCurrentInquiry().setRunLocalObject(DaoConfiguration.getInstance().getRunLocalObjectDao().load(
                            savedInstanceState.getLong(CURRENT_INQUIRY_RUN) ));

            Log.e(TAG, "go through savedInstanceState currentInquiryRunLocalObject" + savedInstanceState + " " + DaoConfiguration.getInstance().getRunLocalObjectDao());
            Log.e(TAG, "go through savedInstanceState currentInquiry" + savedInstanceState + " " + INQ.inquiry.getCurrentInquiry());

        }

        if (INQ.inquiry.getCurrentInquiry() == null){
            Log.e(TAG, "New inquiry");

            setContentView(R.layout.wrapper);
            FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction;

            fragmentTransaction = fragmentManager.beginTransaction();
            InqCreateInquiryFragment fragment = new InqCreateInquiryFragment();
            fragmentTransaction.add(R.id.content, fragment);
            fragmentTransaction.commit();

            setTitle(R.string.actionbar_inquiry_list);

        }else{

            Log.e(TAG, "Show inquiry");
            setContentView(R.layout.activity_inquiry);


            // Create an adapter that when requested, will return a fragment representing an object in
            // the collection.
            // ViewPager and its adapters use support library fragments, so we must use
            // getSupportFragmentManager.
            /*
              The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
              each object in a collection. We use a {@link android.support.v4.app.FragmentStatePagerAdapter}
              derivative, which will destroy and re-create fragments as needed, saving and restoring their
              state in the process. This is important to conserve memory and is a best practice when
              allowing navigation between objects in a potentially large collection.
             */
            InquiryPagerAdapter mInquiryPagerAdapter = new InquiryPagerAdapter(getSupportFragmentManager(), mViewPager);
            getmActionBarHelper().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

            // Set up the ViewPager, attaching the adapter.
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mInquiryPagerAdapter);
            mViewPager.setOnPageChangeListener(this);


            // For each of the sections in the app, add a tab to the action bar.
            for (int i = 0; i < mInquiryPagerAdapter.getCount(); i++) {
                // Create a tab with text corresponding to the page title defined by the adapter.
                // Also specify this Activity object, which implements the TabListener interface, as the
                // listener for when this tab is selected.
                getmActionBarHelper().addTab(
                        getmActionBarHelper().newTab()
                                .setText(mInquiryPagerAdapter.getPageTitle(i))
                                .setTabListener(this));
            }

            getActionBar().setTitle(getResources().getString(R.string.actionbar_inquiry)+" - "+INQ.inquiry.getCurrentInquiry().getTitle());

            Bundle extras = getIntent().getExtras();
            if (extras != null){
                mViewPager.setCurrentItem(extras.getInt(PHASE));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {
        Log.e(TAG, "");
    }

    @Override
    public void onPageSelected(int i) {
        Log.e(TAG, "");
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.e(TAG, "");
        if (state == ViewPager.SCROLL_STATE_IDLE)
        {
            if (mViewPager.getCurrentItem() == 2)
            {
                // Hide the keyboard.
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(mViewPager.getWindowToken(), 0);
            }
        }
    }
}
