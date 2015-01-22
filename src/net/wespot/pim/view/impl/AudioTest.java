package net.wespot.pim.view.impl;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import org.celstec.arlearn2.android.util.MediaFolders;

import java.io.File;
import java.io.IOException;

/**
 * ****************************************************************************
 * Copyright (C) 2015 Open Universiteit Nederland
 * <p/>
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors: Angel Suarez
 * Date: 19/01/15
 * ****************************************************************************
 */

public abstract class AudioTest extends Activity implements SeekBar.OnSeekBarChangeListener {
    static final private double EMA_FILTER = 0.6;

    private File recording = null;
    private MediaRecorder mRecorder = null;
    private double mEMA = 0.0;

    private int mInterval = 100; // 5 seconds by default, can be changed later
    private Handler mHandler = new Handler();

    public  abstract int getGameGeneralitemAudioInput();

    public  abstract int getAudioFeedbackView();

    public  abstract int getAudioRecordingLevel0();
    public  abstract int getAudioRecordingLevel1();
    public  abstract int getAudioRecordingLevel2();
    public  abstract int getAudioRecordingLevel3();
    public  abstract int getAudioRecordingLevel4();
    public abstract Drawable getPlayBackground();
    public abstract Drawable getPauseBackground();

    public  abstract int getStartRecordingButton();
    public  abstract int getStopRecordingButton();
    public  abstract int getSubmitButton();
    public  abstract  int getCancelButton();
    public  abstract  int getMediaPlayButton();
    public abstract int getPlayPauseButton();
    public abstract int getDeleteMediaButton();
    public abstract int getSeekBar();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getGameGeneralitemAudioInput());
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView view = (ImageView) findViewById(getAudioFeedbackView());
        findViewById(getStartRecordingButton()).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startRecording();
                    }
                }
        );
        findViewById(getStopRecordingButton()).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopRecording();
                    }
                }
        );
        findViewById(getSubmitButton()).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submitAudio();
                    }
                }
        );
        findViewById(getCancelButton()).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (recording!= null && recording.exists()) recording.deleteOnExit();
                        AudioTest.this.finish();
                    }
                }
        );
        playPauseButton = (ImageView)findViewById(getPlayPauseButton());
        playPauseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseButton();
                    }
                }
        );
        findViewById(getDeleteMediaButton()).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteMedia();
                    }
                }
        );

        seekbar = (SeekBar) findViewById(getSeekBar());
        seekbar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStatusChecker.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRecording();
        mHandler.removeCallbacks(mStatusChecker);
    }

    private void startRecording(){
        if (mRecorder == null) {

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recording = MediaFolders.createOutgoingAmrFile();
            mRecorder.setOutputFile(recording.toString());
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mRecorder.start();
            mEMA = 0.0;
            findViewById(getStopRecordingButton()).setVisibility(View.VISIBLE);
            findViewById(getStartRecordingButton()).setVisibility(View.GONE);
        }
    }

    private void stopRecording(){
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            findViewById(getStopRecordingButton()).setVisibility(View.GONE);
            findViewById(getSubmitButton()).setVisibility(View.VISIBLE);
            findViewById(getMediaPlayButton()).setVisibility(View.VISIBLE);
            ImageView leftView = (ImageView) findViewById(getAudioFeedbackView());
            leftView.setVisibility(View.GONE);
            leftView.setImageResource(getAudioRecordingLevel0());
            preparePlayer();
        }
    }


    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude()/2700.0);
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }




    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            mHandler.postDelayed(mStatusChecker, mInterval);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((50+(int) (100*getAmplitudeEMA())),
//                    100);
//            params.gravity= Gravity.CENTER_HORIZONTAL;
//            findViewById(getAudioFeedbackView()).setLayoutParams(params);
            ImageView leftView = (ImageView) findViewById(getAudioFeedbackView());
            if (mRecorder == null) {
                leftView.setImageResource(getAudioRecordingLevel0());
            } else if (getAmplitudeEMA()<0.2d) {
                leftView.setImageResource(getAudioRecordingLevel1());
            } else if (getAmplitudeEMA()<0.5d) {
                leftView.setImageResource(getAudioRecordingLevel2());
            } else if (getAmplitudeEMA()< 0.8d) {
                leftView.setImageResource(getAudioRecordingLevel3());
            } else {
                leftView.setImageResource(getAudioRecordingLevel4());
            }
//            leftView.setMinimumWidth(100);
//            leftView.setMinimumHeight(100);

        }
    };


    public void submitAudio() {
        // TODO

        if (recording!=null) {
            System.out.println(recording.toString());
            Bundle conData = new Bundle();
            conData.putString("filePath", recording.getAbsolutePath());
            Intent intent = new Intent();
            intent.putExtras(conData);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }


    private MediaPlayer mediaPlayer;
    private ImageView playPauseButton;
    private SeekBar seekbar;
    private int status = PAUSE;
    private final static int PAUSE = 0;
    private final static int PLAYING = 1;

    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;

    private Handler playHandler = new Handler();

    public void preparePlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        oneTimeOnly = 0;
        status = PAUSE;
        try {
            if (recording != null) {
                Uri uri = Uri.fromFile(recording);
                mediaPlayer.setDataSource(this, uri);
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void playPauseButton() {
        if (status == PAUSE) {
//            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            playPauseButton.setImageDrawable(getPauseBackground());

            status = PLAYING;
            mediaPlayer.start();
//            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            playPauseButton.setImageDrawable(getPauseBackground());
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
//                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    playPauseButton.setImageDrawable(getPlayBackground());

                }
            });
        } else {
//            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            playPauseButton.setImageDrawable(getPlayBackground());
            status = PAUSE;
            mediaPlayer.pause();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();

            seekbar.setProgress((int)startTime);
            playHandler.postDelayed(this, 100);
        }
    };

    public void deleteMedia() {
        recording.deleteOnExit();
        findViewById(getStartRecordingButton()).setVisibility(View.VISIBLE);
        ImageView feedbackView = (ImageView) findViewById(getAudioFeedbackView());
        feedbackView.setVisibility(View.VISIBLE);
        feedbackView.setImageResource(getAudioRecordingLevel0());

        findViewById(getStopRecordingButton()).setVisibility(View.GONE);
        findViewById(getSubmitButton()).setVisibility(View.GONE);
        findViewById(getMediaPlayButton()).setVisibility(View.GONE);


    }

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
}
