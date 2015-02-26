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
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import daoBase.DaoConfiguration;
import net.wespot.pim.BuildConfig;
import net.wespot.pim.R;
import net.wespot.pim.utils.images.ImageCache;
import net.wespot.pim.utils.images.ImageFetcher;
import net.wespot.pim.utils.images.Utils;
import net.wespot.pim.utils.layout.RecyclingImageView;
import net.wespot.pim.view.InqDataCollectionTaskFragment;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.ResponseDelegator;
import org.celstec.arlearn2.android.events.ResponseEvent;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.InquiryLocalObject;
import org.celstec.dao.gen.ResponseLocalObject;
import org.celstec.dao.gen.RunLocalObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight forward GridView
 * implementation with the key addition being the ImageWorker class w/ImageCache to load children
 * asynchronously, keeping the UI nice and smooth and caching thumbnails for quick retrieval. The
 * cache is retained over configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
public class ImageGridFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, ListItemClickInterface<ResponseLocalObject> {
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private static final String GENERAL_ITEM = "generalItemId";
    private static final String RUN_ID = "runId";
    private static final String INQUIRY_ID = "inquiryId";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private RunLocalObject runLocalObject;

    private GridView mGridView;

    Comparator<ResponseLocalObject> responseLocalObjectComparator = new Comparator<ResponseLocalObject>() {
        public int compare(ResponseLocalObject responseLocalObject, ResponseLocalObject responseLocalObject2) {
            return (int) (responseLocalObject.getTimeStamp() - responseLocalObject2.getTimeStamp());
        }
    };

    private ImageFetcher mImageFetcher;

    private GeneralItemLocalObject giLocalObject;
    private List<ResponseLocalObject> responseLocalObjectList;

    public ImageGridFragment() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(GENERAL_ITEM, giLocalObject.getId());
        if (INQ.inquiry.getCurrentInquiry() != null){
            outState.putLong(RUN_ID, INQ.inquiry.getCurrentInquiry().getRunId());
            outState.putLong(INQUIRY_ID, INQ.inquiry.getCurrentInquiry().getId());
        }
    }

    public GridView getmGridView() {
        return mGridView;
    }

    public void setmGridView(GridView mGridView) {
        this.mGridView = mGridView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ARL.eventBus.register(this);

        Bundle extras = getArguments();


        if (savedInstanceState != null){
            INQ.init(this.getActivity());
            INQ.accounts.syncMyAccountDetails();
            giLocalObject = DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().load(savedInstanceState.getLong(GENERAL_ITEM));
            runLocalObject = DaoConfiguration.getInstance().getRunLocalObjectDao().load(savedInstanceState.getLong(RUN_ID));

            InquiryLocalObject inquiryLocalObject = DaoConfiguration.getInstance().getInquiryLocalObjectDao().load(savedInstanceState.getLong(INQUIRY_ID));
            inquiryLocalObject.setRunLocalObject(runLocalObject);

            INQ.inquiry.setCurrentInquiry(inquiryLocalObject);
            Log.e(TAG, savedInstanceState.getLong(GENERAL_ITEM) + " - General Item ");
            Log.e(TAG, savedInstanceState.getLong(RUN_ID) + " - Run ");
        }else{
            if (extras != null) {
                giLocalObject = DaoConfiguration.getInstance(getActivity()).getGeneralItemLocalObjectDao().load(extras.getLong(InqDataCollectionTaskFragment.GENERAL_ITEM));
            }
        }


        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        responseLocalObjectList = giLocalObject.getResponses();

        Log.e(TAG, "GI-ID:"+giLocalObject.getId()+
                " GI-Title:"+giLocalObject.getTitle()+
                " Number responses:"+responseLocalObjectList.size());
        for(ResponseLocalObject r : responseLocalObjectList){
            Log.e(TAG,"RESPONSE ID:"+r.getId());
        }

        Collections.sort(responseLocalObjectList, responseLocalObjectComparator);



        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        mAdapter = new ImageAdapter(getActivity());
//        mAdapter = new ImageAdapter(getActivity(), mImageFetcher, giLocalObject);
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.ic_taks_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_image_grid, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView1);

        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);

        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }
                                if (Utils.hasJellyBean()) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    public void onEventMainThread(ResponseEvent responseEvent){
//        Log.e(TAG, "Numero de elementos antes de notifyDataSetChanged: "+mAdapter.getCount()+" "+responseLocalObjectList.size());
//        mAdapter.notifyDataSetChanged();
//        Log.e(TAG, "Numero de elementos antes de notifiy invalidated "+mAdapter.getCount()+" "+responseLocalObjectList.size());
//        mAdapter.notifyDataSetInvalidated();
//        Log.e(TAG, "Numero de elementos despues de notifiy invalidated "+mAdapter.getCount()+" "+responseLocalObjectList.size());
//        mGridView.invalidate();
//        Log.e(TAG, "Numero de elementos de invalidar "+mAdapter.getCount()+" "+responseLocalObjectList.size());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ARL.eventBus.unregister(this);
        mImageFetcher.closeCache();

    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        final Intent intent = new Intent(getActivity(), ImageDetailActivity.class);

        intent.putExtra(ImageDetailActivity.GENERAL_ITEM_ID, giLocalObject.getId());
        intent.putExtra(ImageDetailActivity.RESPONSE_POSITION, (int) position);

        if (Utils.hasJellyBean()) {
            // makeThumbnailScaleUpAnimation() looks kind of ugly here as the loading spinner may
            // show plus the thumbnail image in GridView is cropped. so using
            // makeScaleUpAnimation() instead.
            ActivityOptions options =
                    ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
            getActivity().startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    @Override
    public void onListItemClick(View v, int position, ResponseLocalObject item) {
        final Intent intent = new Intent(getActivity(), ImageDetailActivity.class);

        intent.putExtra(ImageDetailActivity.GENERAL_ITEM_ID, item.getGeneralItemLocalObject().getId());
        intent.putExtra(ImageDetailActivity.RESPONSE_POSITION, position);

        if (Utils.hasJellyBean()) {
            // makeThumbnailScaleUpAnimation() looks kind of ugly here as the loading spinner may
            // show plus the thumbnail image in GridView is cropped. so using
            // makeScaleUpAnimation() instead.
            ActivityOptions options =
                    ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
            getActivity().startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onListItemClick(View v, int position, ResponseLocalObject object) {
//        final Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
//
//        intent.putExtra(ImageDetailActivity.GENERAL_ITEM_ID, giLocalObject.getId());
//        intent.putExtra(ImageDetailActivity.RESPONSE_POSITION, (int) position);
//
//        if (Utils.hasJellyBean()) {
//            // makeThumbnailScaleUpAnimation() looks kind of ugly here as the loading spinner may
//            // show plus the thumbnail image in GridView is cropped. so using
//            // makeScaleUpAnimation() instead.
//            ActivityOptions options =
//                    ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
//            getActivity().startActivity(intent, options.toBundle());
//        } else {
//            startActivity(intent);
//        }
//    }

    @Override
    public boolean setOnLongClickListener(View v, int position, final ResponseLocalObject object) {
        // Not used
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if (object.getIsSynchronized() == false){
                            DaoConfiguration.getInstance().getResponseLocalObjectDao().delete(object);
                        }else{
                            object.setRevoked(true);
                            object.setNextSynchronisationTime(0l);
                            object.setIsSynchronized(false);
                            DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(object);
//                            object.delete();
                            ResponseDelegator.getInstance().syncResponses(object.getRunId());
                        }

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //Do your No progress
                        break;
                }
            }
        };
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setMessage("Are you sure to delete?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
        return false;
    }



    @TargetApi(VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ResponseLocalObject responseLocalObject = giLocalObject.getResponses().get(position);

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if (responseLocalObject.getIsSynchronized() == false){
                            DaoConfiguration.getInstance().getResponseLocalObjectDao().delete(responseLocalObject);
                        }else{
                            responseLocalObject.setRevoked(true);
                            responseLocalObject.setNextSynchronisationTime(0l);
                            responseLocalObject.setIsSynchronized(false);
                            DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(responseLocalObject);
                            ResponseDelegator.getInstance().syncResponses(responseLocalObject.getRunId());

                        }

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //Do your No progress
                        break;
                }
            }
        };
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setMessage("Are you sure to delete?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
        return false;
    }

    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    public class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {

            if (getNumColumns() == 0) {
                return 0;
            }
            return responseLocalObjectList.size();
        }

        @Override
        public Object getItem(int position) {
            return responseLocalObjectList.get(position).getThumbnailUriAsString();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {

            ResponseLocalObject responseLocalObject = responseLocalObjectList.get(position);

            ImageView imageView;


//            if (convertView == null) {
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.entry_data_collection_response, null);
//
//                final ImageView[] imageView = new ImageView[]{
//                        (ImageView) convertView.findViewById(R.id.filtered_image)};
//
//                final VideoView[] videos = new VideoView[]{
//                        (VideoView) convertView.findViewById(R.id.video)};


    //            ImageView imageView;
    //            if (convertView == null) { // if it's not recycled, instantiate and initialize
    //                imageView = new RecyclingImageView(mContext);
    //                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    //                imageView.setLayoutParams(mImageViewLayoutParams);
    //            } else { // Otherwise re-use the converted view
    //                imageView = (ImageView) convertView;
    //            }
    //
    //            // Check the height matches our calculated column width
    //            if (imageView.getLayoutParams().height != mItemHeight) {
    //                imageView.setLayoutParams(mImageViewLayoutParams);
    //            }

            if (responseLocalObject.isAudio()) {
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);

                if (imageView.getLayoutParams().height != mItemHeight) {
                    imageView.setLayoutParams(mImageViewLayoutParams);
                }

                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_task_record));
                return imageView;
            } else if (responseLocalObject.isPicture()) {
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);

                if (imageView.getLayoutParams().height != mItemHeight) {
                    imageView.setLayoutParams(mImageViewLayoutParams);
                }

                mImageFetcher.loadImage(responseLocalObject.getThumbnailUriAsString(), imageView);
                return imageView;
            } else if (responseLocalObject.isVideo()) {
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);

                if (imageView.getLayoutParams().height != mItemHeight) {
                    imageView.setLayoutParams(mImageViewLayoutParams);
                }

                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_task_video));
                return imageView;
            } else if (responseLocalObject.getValue() != null ) {
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.entry_data_collection_response, null);
//
//                final TextView[] views = new TextView[]{
//                        (TextView) convertView.findViewById(R.id.caption)};
//                views[0].setText(responseLocalObject.getValue().toString());
//                views[0].setVisibility(View.VISIBLE);
//                return convertView;

                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);

                if (imageView.getLayoutParams().height != mItemHeight) {
                    imageView.setLayoutParams(mImageViewLayoutParams);
                }

                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_description));

                return imageView;
            } else {
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);

                if (imageView.getLayoutParams().height != mItemHeight) {
                    imageView.setLayoutParams(mImageViewLayoutParams);
                }

                imageView.setImageDrawable(getResources().getDrawable(R.drawable.empty_photo));
                return imageView;
            }
        }


        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }


//    public class ImageAdapter extends AbstractResponsesLazyListAdapter {
//
//        private GridView.LayoutParams mImageViewLayoutParams;
//        private int mItemHeight = 0;
//        private int mNumColumns = 0;
//        private GeneralItemLocalObject gi;
////        private ImageFetcher mImageFetcher;
//
//        public ImageAdapter(Context context) {
//            super(context);
//        }
//
//        public ImageAdapter(Context context, ImageFetcher imageFetcher, GeneralItemLocalObject giLocalObject) {
////            super(context, giLocalObject.getId());
//            super(context);
////            mImageFetcher = imageFetcher;
//            gi = giLocalObject;
//            mImageViewLayoutParams = new GridView.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        }
//
//        @Override
//        public View newView(Context context, ResponseLocalObject item, ViewGroup parent) {
//
//            if (item == null) return null;
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//            mImageViewLayoutParams = new GridView.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            return inflater.inflate(R.layout.entry_data_collection_response, parent, false);
//        }
//
//        @Override
//        public void bindView(View convertView, Context mContext, ResponseLocalObject responseLocalObject) {
////            if (position < mNumColumns) {
////                if (convertView == null) {
////                    convertView = new View(mContext);
////                }
////                // Set empty view with height of ActionBar
////                convertView.setLayoutParams(new AbsListView.LayoutParams(
////                        LayoutParams.MATCH_PARENT, mActionBarHeight));
////                return convertView;
////            }
//
//            // Now handle the main ImageView thumbnails
//            ImageView imageView;
//            if (convertView == null) { // if it's not recycled, instantiate and initialize
//                imageView = new RecyclingImageView(mContext);
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setLayoutParams(mImageViewLayoutParams);
//            } else { // Otherwise re-use the converted view
//                imageView = (ImageView) convertView;
//            }
//
//            // Check the height matches our calculated column width
//            if (imageView.getLayoutParams().height != mItemHeight) {
//                imageView.setLayoutParams(mImageViewLayoutParams);
//            }
//
//            Log.e(TAG, "GI-ID:"+responseLocalObject.getGeneralItemLocalObject().getId()+
//                    " RES-ID:"+responseLocalObject.getId()+"" +
//                    " GI-Title:"+responseLocalObject.getGeneralItemLocalObject().getTitle());
//
//            if (responseLocalObject.isAudio()) {
////                Log.e(TAG, "is audio");
//                imageView = new RecyclingImageView(mContext);
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setLayoutParams(mImageViewLayoutParams);
//
//                if (imageView.getLayoutParams().height != mItemHeight) {
//                    imageView.setLayoutParams(mImageViewLayoutParams);
//                }
//
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_task_record));
//            } else if (responseLocalObject.isPicture()) {
////                Log.e(TAG, "is image");
//
//                imageView = new RecyclingImageView(mContext);
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setLayoutParams(mImageViewLayoutParams);
//
//                if (imageView.getLayoutParams().height != mItemHeight) {
//                    imageView.setLayoutParams(mImageViewLayoutParams);
//                }
//
//            } else if (responseLocalObject.isVideo()) {
////                Log.e(TAG, "is video");
//
//                imageView = new RecyclingImageView(mContext);
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setLayoutParams(mImageViewLayoutParams);
//
//                if (imageView.getLayoutParams().height != mItemHeight) {
//                    imageView.setLayoutParams(mImageViewLayoutParams);
//                }
//
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_task_video));
//            } else if (responseLocalObject.getValue() != null ) {
////                Log.e(TAG, "is value");
//
////                convertView = LayoutInflater.from(mContext).inflate(R.layout.entry_data_collection_response, null);
////
////                final TextView[] views = new TextView[]{
////                        (TextView) convertView.findViewById(R.id.caption)};
////                views[0].setText(responseLocalObject.getValue().toString());
////                views[0].setVisibility(View.VISIBLE);
////                return convertView;
//
//                imageView = new RecyclingImageView(mContext);
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setLayoutParams(mImageViewLayoutParams);
//
//                if (imageView.getLayoutParams().height != mItemHeight) {
//                    imageView.setLayoutParams(mImageViewLayoutParams);
//                }
//
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_description));
//
//            } else {
////                Log.e(TAG, "is text");
//
//                imageView = new RecyclingImageView(mContext);
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setLayoutParams(mImageViewLayoutParams);
//
//                if (imageView.getLayoutParams().height != mItemHeight) {
//                    imageView.setLayoutParams(mImageViewLayoutParams);
//                }
//
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.empty_photo));
//            }
//
//
////            Log.e(TAG, "Load async: "+runLocalObject.getResponses().get(position - mNumColumns));
//
//            // Finally load the image asynchronously into the ImageView, this also takes care of
//            // setting a placeholder image while the background thread runs
////            mImageFetcher.loadImage(runLocalObject.getResponses().get(position - mNumColumns), imageView);
//            mImageFetcher.loadImage(responseLocalObject, imageView);
//            return;
//        }
//
//
//
//        public void setItemHeight(int height) {
//            if (height == mItemHeight) {
//                return;
//            }
//            mItemHeight = height;
//            mImageViewLayoutParams =
//                    new GridView.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, mItemHeight);
//            mImageFetcher.setImageSize(height);
//            notifyDataSetChanged();
//        }
//
//        public void setNumColumns(int numColumns) {
//            mNumColumns = numColumns;
//        }
//
//        public int getNumColumns() {
//            return mNumColumns;
//        }
//
//        @Override
//        public int getCount() {
//            // If columns have yet to be determined, return no items
//            if (getNumColumns() == 0) {
//                return 0;
//            }
//
//            // Size + number of columns for top empty row
//            return gi.getResponses().size()+ mNumColumns;
//        }
//
//        @Override
//        public ResponseLocalObject getItem(int position) {
//            return position < mNumColumns ?
//                    null : gi.getResponses().get(position - mNumColumns);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position < mNumColumns ? 0 : position - mNumColumns;
//        }
//
//        @Override
//        public int getViewTypeCount() {
//            // Two types of views, the normal ImageView and the top row of empty views
//            return 2;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return (position < mNumColumns) ? 1 : 0;
//        }
//
//        @Override
//        public boolean hasStableIds() {
//            return true;
//        }
//    }

}
