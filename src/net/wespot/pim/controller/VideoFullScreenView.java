/*******************************************************************************
 * Copyright (C) 2013 Open Universiteit Nederland
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors: Angel Suarez
 ******************************************************************************/
package net.wespot.pim.controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import net.wespot.pim.R;

public class VideoFullScreenView extends Activity {

    private VideoView vidDisplay;
    private MediaPlayer mediaPlayer;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_fullscreen_view);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            getActionBar().hide();
        }

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent i = getIntent();
        String filePath = i.getStringExtra("filePath");

//        Uri myUri = Uri.parse(filePath); // initialize Uri here
////        vidDisplay.setVideoURI(myUri);
//
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            mediaPlayer.setDataSource(getApplicationContext(), myUri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.start();

        vidDisplay = (VideoView) findViewById(R.id.videoView);
        vidDisplay.setVideoURI(Uri.parse(filePath));
        MediaController mediaController = new MediaController(this);
        vidDisplay.setMediaController(mediaController);
        vidDisplay.requestFocus();
        vidDisplay.seekTo(1);
        vidDisplay.start();

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onDestroy() {
        vidDisplay.getHolder().getSurface().release();
//        mediaPlayer.release();
//        mediaPlayer = null;
        super.onDestroy();
    }


}