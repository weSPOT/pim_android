/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wespot.pim.controller;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import net.wespot.pim.BuildConfig;
import net.wespot.pim.R;
import net.wespot.pim.utils.images.ImageCache;
import net.wespot.pim.utils.images.ImageFetcher;
import net.wespot.pim.utils.images.Utils;
import net.wespot.pim.view.InqImageDetailFragment;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.dao.gen.GeneralItemLocalObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageDetailActivity extends FragmentActivity implements OnClickListener {
    private static final String IMAGE_CACHE_DIR = "images";
    public static final String RESPONSE_POSITION = "extra_image";
    public static final String GENERAL_ITEM_ID = "DataCollectionTaskGeneralItemId";
    private static final String TAG = "ImageDetailActivity";
    private long generalItemId;

    private ImagePagerAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private ViewPager mPager;

    private TextView current_info;
    private TextView info_image;
    private ImageView prev_item;
    private ImageView next_item;

    private GeneralItemLocalObject giLocalObject;
    private int responsePosition;

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putLong("currentInquiry", INQ.inquiry.getCurrentInquiry().getId());
//        if(INQ.inquiry.getCurrentInquiry().getRunLocalObject()!=null){
//            outState.putLong("currentInquiryRunLocalObject", INQ.inquiry.getCurrentInquiry().getRunLocalObject().getId());
//            Log.e(TAG, "Recover in InqDataCollectionFragment > onSaveInstanceState & current inq = null");
//        }
//    }

    @TargetApi(VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }

        if (savedInstanceState != null) {
            INQ.init(this);
            INQ.accounts.syncMyAccountDetails();
            INQ.inquiry.setCurrentInquiry(
                    DaoConfiguration.getInstance().
                            getInquiryLocalObjectDao().
                            load(savedInstanceState.getLong("currentInquiry")));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager_detail_image);

        prev_item = (ImageView) findViewById(R.id.prev_button);
        next_item = (ImageView) findViewById(R.id.next_button);
        current_info = (TextView) findViewById(R.id.info_current_element);
        info_image = (TextView) findViewById(R.id.info_image);

        Bundle extras = getIntent().getExtras();

//        if (extras != null){
        giLocalObject = DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().load(extras.getLong(GENERAL_ITEM_ID));
        responsePosition = extras.getInt(RESPONSE_POSITION);
//        }

        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        // For this sample we'll use half of the longest width to resize our images. As the
        // image scaling ensures the image is larger than this, we should be left with a
        // resolution that is appropriate for both portrait and landscape. For best image quality
        // we shouldn't divide by 2, but this will use more memory and require a larger memory
        // cache.
        final int longest = (height > width ? height : width) / 2;

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);

        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), giLocalObject.getResponses().size());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setPageMargin((int) getResources().getDimension(R.dimen.data_collect_pager_image_detail_margin));
        mPager.setOffscreenPageLimit(2);
        mPager.setOnPageChangeListener(new MyListener());

        // Set up activity to go full screen
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);

        // Enable some additional newer visibility and ActionBar features to create a more
        // immersive photo viewing experience
        if (Utils.hasHoneycomb()) {

            // Start low profile mode and hide ActionBar
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
//            actionBar.hide();
        }

        // Set the current item based on the extra passed in to this activity
        if (responsePosition != -1) {
            Date date = new Date(giLocalObject.getResponses().get(responsePosition).getTimeStamp());
            Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            info_image.setText(format.format(date));
            mPager.setCurrentItem(responsePosition);
        }

        next_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To change body of implemented methods use File | Settings | File Templates.
                mPager.setCurrentItem(mPager.getCurrentItem()+1);
            }
        });

        prev_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To change body of implemented methods use File | Settings | File Templates.
                mPager.setCurrentItem(mPager.getCurrentItem()-1);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inquiry, menu);
        return true;
    }

    /**
     * Called by the ViewPager child fragments to load images via the one ImageFetcher
     */
    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    /**
     * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
     * could be a large number of items in the ViewPager and we don't want to retain them all in
     * memory at once but create/destroy them on the fly.
     */
    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final int mSize;

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public Fragment getItem(int position) {
//            ResponseLocalObject res = giLocalObject.getResponses().get(position);

//            info_image.setText(res.getTimeStamp().toString());
            return InqImageDetailFragment.newInstance(giLocalObject.getResponses().get(position).getUriAsString());
        }
    }

    /**
     * Set on the ImageView in the ViewPager children fragments, to enable/disable low profile mode
     * when the ImageView is touched.
     */
    @TargetApi(VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    private class MyListener implements ViewPager.OnPageChangeListener {

        private MyListener() {
            int real_number_elements = mAdapter.getCount();


            Log.d(TAG, "Current element: "+1);
            Log.d(TAG, "Number elements: "+real_number_elements);

            current_info.setText(1+" of "+real_number_elements);

        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            int real_position = i+1;
            int real_number_elements = mAdapter.getCount();

            Log.d(TAG, "Current element: "+real_position);
            Log.d(TAG, "Number elements: " + real_number_elements);

            Date date = new Date(giLocalObject.getResponses().get(i).getTimeStamp());
            Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            info_image.setText(format.format(date));
            current_info.setText(real_position+" of "+real_number_elements);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }
}
