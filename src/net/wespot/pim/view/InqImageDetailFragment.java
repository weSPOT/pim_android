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

package net.wespot.pim.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.wespot.pim.R;
import net.wespot.pim.controller.AudioManagerView;
import net.wespot.pim.controller.ImageDetailActivity;
import net.wespot.pim.controller.VideoFullScreenView;
import net.wespot.pim.utils.images.ImageFetcher;
import net.wespot.pim.utils.images.ImageWorker;
import net.wespot.pim.utils.images.Utils;
import org.celstec.dao.gen.ResponseLocalObject;

/**
 * This fragment will populate the children of the ViewPager from {@link net.wespot.pim.controller.ImageDetailActivity}.
 */
public class InqImageDetailFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private static final String VIDEO_DATA_EXTRA = "extra_video_data";
    private static final String TEXT_NUMBER_DATA_EXTRA = "extra_text_data";
    private static final String AUDIO_DATA_EXTRA = "extra_audio_data";
    private static ResponseLocalObject response;
    private String mImageUrl;
    private String mVideoUrl;
    private String mTextValue;
    private String mAudioUrl;
    private ImageView mImageView;
    private ImageFetcher mImageFetcher;
    private ProgressBar progressBar;
    private ImageView mPlayButtonView;

    private TextView valueResponse;

    private String TAG = "InqImageDetailFragment";

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param responseLocalObject The image url to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static InqImageDetailFragment newInstance(ResponseLocalObject responseLocalObject) {
        final InqImageDetailFragment f = new InqImageDetailFragment();

        final Bundle args = new Bundle();
        if (responseLocalObject.isVideo()){
            args.putString(VIDEO_DATA_EXTRA, responseLocalObject.getUriAsString());
        }else if(responseLocalObject.isPicture()){
            args.putString(IMAGE_DATA_EXTRA, responseLocalObject.getUriAsString());
        }else if(responseLocalObject.getValue() != null){
            args.putString(TEXT_NUMBER_DATA_EXTRA, responseLocalObject.getValue());
        }else if(responseLocalObject.isAudio()){
            args.putString(AUDIO_DATA_EXTRA, responseLocalObject.getUriAsString());
        }


        f.setArguments(args);

        return f;
    }

    /**
     * Empty constructor as per the Fragment documentation
     */
    private InqImageDetailFragment() {}

    /**
     * Populate image using a url from extras, use the convenience factory method
     * {@link InqImageDetailFragment#newInstance(org.celstec.dao.gen.ResponseLocalObject)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : "";
        mVideoUrl = getArguments() != null ? getArguments().getString(VIDEO_DATA_EXTRA) : "";
        mTextValue = getArguments() != null ? getArguments().getString(TEXT_NUMBER_DATA_EXTRA) : "";
        mAudioUrl = getArguments() != null ? getArguments().getString(AUDIO_DATA_EXTRA) : "";


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.fragment_detail_image, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mPlayButtonView = (ImageView) v.findViewById(R.id.VideoPreviewPlayButton);
        progressBar = (ProgressBar) v.findViewById(R.id.detail_fragment_progress_bar);
        valueResponse = (TextView) v.findViewById(R.id.text_value_response);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mImageUrl != null){
            if (ImageDetailActivity.class.isInstance(getActivity())) {
                mImageFetcher = ((ImageDetailActivity) getActivity()).getImageFetcher();
                mImageFetcher.loadImage(mImageUrl, mImageView);
            }

            // Pass clicks on the ImageView to the parent activity to handle
            if (View.OnClickListener.class.isInstance(getActivity()) && Utils.hasHoneycomb()) {
                mImageView.setOnClickListener((View.OnClickListener) getActivity());
            }
            Log.i(TAG, "Current element image: " + mImageUrl);
        }else if(mVideoUrl != null){
            mPlayButtonView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            mImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Video in fullscreen (URI): " +mVideoUrl);

                    Intent intent = new Intent(getActivity(), VideoFullScreenView.class);
                    intent.putExtra("filePath", mVideoUrl);
                    getActivity().startActivity(intent);
                }
            });
            Log.i(TAG, "Current element video: " + mVideoUrl);
        }else if(mTextValue != null){
            progressBar.setVisibility(View.INVISIBLE);
            valueResponse.setVisibility(View.VISIBLE);
            valueResponse.setText(mTextValue);
            Log.i(TAG, "Current element text: " + mTextValue);
        }else if(mAudioUrl != null){
            mPlayButtonView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            mImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Audio in fullscreen (URI): " +mAudioUrl);

                    Intent intent = new Intent(getActivity(), AudioManagerView.class);
                    intent.putExtra("filePath", mAudioUrl);
                    getActivity().startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
            mPlayButtonView.setImageDrawable(null);
        }
    }
}
