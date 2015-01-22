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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import net.wespot.pim.R;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class AudioManagerView extends Activity {

    private MediaPlayer mediaPlayer;

    public TextView songName;
    private double startTime = 0;
    private double finalTime = 0;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private ImageButton playButton,pauseButton;
    public static int oneTimeOnly = 0;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            getActionBar().hide();
        }


        songName = (TextView)findViewById(R.id.textView4);

        playButton = (ImageButton)findViewById(R.id.imageButton1);
        pauseButton = (ImageButton)findViewById(R.id.imageButton2);
        songName.setText("");
        pauseButton.setEnabled(false);

        Intent i = getIntent();
        String filePath = i.getStringExtra("filePath");

        Uri myUri1 = null;
        String myUri = null;

        if (filePath.contains(".m4a")){
            myUri = filePath; // initialize Uri here

        }else{
            myUri1 = Uri.parse(filePath); // initialize Uri here
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
        try {
            if (filePath.contains(".m4a")){
//                mediaPlayer.setDataSource(myUri);
                playAudio(filePath);
                return;
            }else{
                mediaPlayer.setDataSource(getApplicationContext(), myUri1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        play(playButton);

    }
    private File mediaFile;

    private void playAudio(String mediaUrl) {
        try {
            URLConnection cn = new URL(mediaUrl).openConnection();
            InputStream is = cn.getInputStream();

            // create file to store audio
            mediaFile = new File(this.getCacheDir(),"mediafile");
            FileOutputStream fos = new FileOutputStream(mediaFile);
            byte buf[] = new byte[16 * 1024];
            Log.i("FileOutputStream", "Download");

            // write to file until complete
            do {
                int numread = is.read(buf);
                if (numread <= 0)
                    break;
                fos.write(buf, 0, numread);
            } while (true);
            fos.flush();
            fos.close();
            Log.i("FileOutputStream", "Saved");
            MediaPlayer mp = new MediaPlayer();

            // create listener to tidy up after playback complete
            MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    // free up media player
                    mp.release();
                    Log.i("MediaPlayer.OnCompletionListener", "MediaPlayer Released");
                }
            };
            mp.setOnCompletionListener(listener);

            FileInputStream fis = new FileInputStream(mediaFile);
            // set mediaplayer data source to file descriptor of input stream
            mp.setDataSource(fis.getFD());
            mp.prepare();
            Log.i("MediaPlayer", "Start Player");
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void play(View view){
        mediaPlayer.start();
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        if(oneTimeOnly == 0){
            oneTimeOnly = 1;
        }

        pauseButton.setEnabled(true);
        playButton.setEnabled(false);
    }

    public void pause(View view){
        mediaPlayer.pause();
        pauseButton.setEnabled(false);
        playButton.setEnabled(true);
    }
    public void forward(View view){
        int temp = (int)startTime;
        if((temp+forwardTime)<=finalTime){
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo((int) startTime);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Cannot jump forward 5 seconds",
                    Toast.LENGTH_SHORT).show();
        }

    }
    public void rewind(View view){
        int temp = (int)startTime;
        if((temp-backwardTime)>0){
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo((int) startTime);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Cannot jump backward 5 seconds",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onDestroy() {
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }


}