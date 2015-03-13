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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import net.wespot.pim.R;
import net.wespot.pim.controller.ImageDetailActivity;
import net.wespot.pim.controller.VideoFullScreenView;
import net.wespot.pim.utils.images.ImageFetcher;
import net.wespot.pim.utils.images.Utils;
import org.celstec.dao.gen.ResponseLocalObject;

import java.io.IOException;

/**
 * This fragment will populate the children of the ViewPager from {@link net.wespot.pim.controller.ImageDetailActivity}.
 */
public class InqImageDetailFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private static final String VIDEO_DATA_EXTRA = "extra_video_data";
    private static final String TEXT_NUMBER_DATA_EXTRA = "extra_text_data";
    private static final String AUDIO_DATA_EXTRA = "extra_audio_data";
    private String mImageUrl;
    private String mVideoUrl;
    private String mTextValue;
    private String mAudioUrl;
    private ImageView mImageView;
    private ImageFetcher mImageFetcher;
    private ProgressBar progressBar;
    private ImageView mPlayButtonView;
    private RelativeLayout mediaBar;
    private RelativeLayout mediaBarVideo;

    private TextView valueResponse;


    ////////////////////////////////////
    // Private object for audio manager.
    ////////////////////////////////////

    private MediaPlayer mediaPlayer;
    public static int oneTimeOnly = 0;
    private double startTime = 0;
    private double finalTime = 0;
    private SeekBar seekbar;
    private Handler playHandler = new Handler();
    private ImageView playPauseButton;
    private int status = PAUSE;
    private final static int PAUSE = 0;
    private final static int PLAYING = 1;

//    private File recording = null;
//    private MediaPlayer mediaPlayer;
//    private SeekBar seekbar;
//    private int status = PAUSE;

//
//    private double startTime = 0;
//    private double finalTime = 0;
//    public static int oneTimeOnly = 0;
//    private View v;
//    private Handler playHandler = new Handler();


    ////////////////////////////////////
    // Private object for audio manager.
    ////////////////////////////////////
    private SeekBar seekbarVideo;
    private ImageView playPauseButtonVideo;


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
    public InqImageDetailFragment() {}

    /**
     * Populate image using a url from extras, use the convenience factory method
     * {@link InqImageDetailFragment#newInstance(org.celstec.dao.gen.ResponseLocalObject)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : "";
        mVideoUrl = getArguments() != null ? getArguments().getString(VIDEO_DATA_EXTRA) : "";
        mTextValue = getArguments() != null ? getArguments().getString(TEXT_NUMBER_DATA_EXTRA) : "";
        mAudioUrl = getArguments() != null ? getArguments().getString(AUDIO_DATA_EXTRA) : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_detail_image, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mediaBar = (RelativeLayout) v.findViewById(R.id.mediaBar);
        mediaBarVideo = (RelativeLayout) v.findViewById(R.id.mediaBarVideo);
        mPlayButtonView = (ImageView) v.findViewById(R.id.VideoPreviewPlayButton);
        progressBar = (ProgressBar) v.findViewById(R.id.detail_fragment_progress_bar);
        valueResponse = (TextView) v.findViewById(R.id.text_value_response);

        playPauseButton = (ImageView) v.findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseButton();
                    }
                }
        );

        playPauseButtonVideo = (ImageView) v.findViewById(R.id.playPauseButtonVideo);
        playPauseButtonVideo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        playPauseButton();
                    }
                }
        );

        seekbar = (SeekBar) v.findViewById(R.id.seekbar);
        seekbarVideo = (SeekBar) v.findViewById(R.id.seekbarVideo);
        seekbar.setOnSeekBarChangeListener(this);
//        seekbarVideo.setOnSeekBarChangeListener(this);

        return v;
    }

    public static void enableStrictMode(InqImageDetailFragment context) {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()
                        .penaltyLog()
                        .build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .penaltyLog()
                        .build());
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

//            mediaBarVideo.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.INVISIBLE);
//            prepareVideoPlayer();
//            playPauseButtonVideo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    playPauseVideoButton();
//                }
//            });

            Log.i(TAG, "Current element video: " + mVideoUrl);
        }else if(mTextValue != null){
            progressBar.setVisibility(View.INVISIBLE);
            valueResponse.setVisibility(View.VISIBLE);
            valueResponse.setText(mTextValue);
            Log.i(TAG, "Current element text: " + mTextValue);
        }else if(mAudioUrl != null){
//            mPlayButtonView.setVisibility(View.VISIBLE);
            mediaBar.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            preparePlayer();
            playPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playPauseButton();
                }
            });
//            playPauseButton.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View view) {
//                    Log.i(TAG, "Audio in fullscreen (URI): " +mAudioUrl);
//
//                    Intent intent = new Intent(getActivity(), AudioManagerView.class);
//                    intent.putExtra("filePath", mAudioUrl);
//                    getActivity().startActivity(intent);
//                }
//            });

//            AudioFragment nextFrag= new AudioFragment();
//            getActivity().getFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_detail_image, nextFrag, TAG_FRAGMENT)
//                    .addToBackStack(null)
//                    .commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            playHandler.removeCallbacks(UpdateSongTime);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

//    URL url = new URL(mAudioUrl);
//    InputStream inputStream = url.openStream();
//
//    File outputSource = null;
//
//    FileOutputStream fileOutputStream;
//    fileOutputStream = new FileOutputStream(outputSource);
//
//    int c;
//
//    int bytesRead = 0;
//    while ((c = inputStream.read()) != -1) {
//    fileOutputStream.write(c);
//
//    bytesRead++;
//    }
//
//    inputStream.close();
//    fileOutputStream.close();
//
//    mediaPlayer.setDataSource(String.valueOf(outputSource));

    public void preparePlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        oneTimeOnly = 0;
        status = PAUSE;
        try {
            Uri uri = Uri.parse(mAudioUrl);
            mediaPlayer.setDataSource(getActivity(), uri);
            mediaPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playPauseButton() {
        if (status == PAUSE) {
            playPauseButton.setImageDrawable(getActivity().getResources().getDrawable(android.R.drawable.ic_media_pause));

            status = PLAYING;
            mediaPlayer.start();

            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            if(oneTimeOnly == 0){
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }
            seekbar.setProgress((int)startTime);
            playHandler.postDelayed(UpdateSongTime,100);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    status = PAUSE;
                    playPauseButton.setImageDrawable(getActivity().getResources().getDrawable(android.R.drawable.ic_media_play));
                }
            });
        } else {
            playPauseButton.setImageDrawable(getActivity().getResources().getDrawable(android.R.drawable.ic_media_play));
            status = PAUSE;
            mediaPlayer.pause();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            if (status == PAUSE) {
                startTime = 0;
            }
            seekbar.setProgress((int)startTime);
            playHandler.postDelayed(this, 100);

        }
    };

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getProgress() == finalTime){
            status = PAUSE;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        } else
        if (status == PAUSE) {
            mediaPlayer.seekTo(seekBar.getProgress());
        } else {
            mediaPlayer.pause();
            mediaPlayer.seekTo(seekBar.getProgress());
            mediaPlayer.start();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

//    public void prepareVideoPlayer() {
//        if (mediaPlayer == null) {
//            mediaPlayer = new MediaPlayer();
//        }
//        mediaPlayer.reset();
//        oneTimeOnly = 0;
//        status = PAUSE;
//        try {
////            if (recording != null) {
//            if (mVideoUrl != null) {
////                Uri uri = Uri.fromFile(recording);
//                Uri uri = Uri.parse(mVideoUrl);
//                mediaPlayer.setDataSource(getActivity(),Uri.parse(new File(mVideoUrl).toString()));
//                mediaPlayer.prepare();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void playPauseVideoButton() {
//
//        if (status == PAUSE) {
////            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
//            playPauseButtonVideo.setImageDrawable(getActivity().getResources().getDrawable(android.R.drawable.ic_media_play));
//
//            status = PLAYING;
//            mediaPlayer.start();
////            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
//            playPauseButtonVideo.setImageDrawable(getActivity().getResources().getDrawable(android.R.drawable.ic_media_pause));
//            finalTime = mediaPlayer.getDuration();
//            startTime = mediaPlayer.getCurrentPosition();
//            if(oneTimeOnly == 0){
//                seekbar.setMax((int) finalTime);
//                oneTimeOnly = 1;
//            }
//            seekbar.setProgress((int)startTime);
////            playHandler.postDelayed(UpdateSongTime,100);
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    status = PAUSE;
//                    playPauseButtonVideo.setImageDrawable(getActivity().getResources().getDrawable(android.R.drawable.ic_media_play));
//
//                }
//            });
//        } else {
//            playPauseButtonVideo.setImageDrawable(getActivity().getResources().getDrawable(android.R.drawable.ic_media_play));
//            status = PAUSE;
//            mediaPlayer.pause();
//        }
//    }
}
