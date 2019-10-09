package au.edu.sydney.comp5216.running_diary.ui.music;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

import au.edu.sydney.comp5216.running_diary.R;

/**
 * MusicPlayerActivity will start once song is selected
 */
public class MusicPlayerActivity extends AppCompatActivity {
    // Set variables
    private ImageView pausePlayBtn, nextBtn, previousBtn;
    private TextView songNameTxt, curTime, totTime;
    private SeekBar seekBar;

    private MediaPlayer mPlayer;
    private int position;
    private ArrayList<File> songList;
    private String songName;

    /**
     * When activity start, music player view is created and song starts playing
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);

        pausePlayBtn = findViewById(R.id.pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);

        songNameTxt = findViewById(R.id.songName);
        curTime = findViewById(R.id.duration_start);
        totTime = findViewById(R.id.duration_end);

        seekBar = findViewById(R.id.seekBar);

        // Get intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        // Set song list from intent and get position of selected song
        songList = (ArrayList) bundle.getParcelableArrayList("song");
        position = bundle.getInt("position", 0);

        // Call start playing method
        startPlaying(position);

        // Set onclickListener for play/pause button
        pausePlayBtn.setOnClickListener(new View.OnClickListener(){
            /**
             * When play/pause button is clicked
             * @param view
             */
            @Override
            public void onClick(View view){
                // If music player is currently playing, change icon to play icon and pause the player
                if(mPlayer.isPlaying()){
                    pausePlayBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    mPlayer.pause();
                } else{ // Change icon to pause icon and start playing music
                    pausePlayBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                    mPlayer.start();
                }
            }
        });

        // Set onClickListener for next button
        nextBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * When next button is clicked, increase position by one and start playing
             * @param view
             */
            @Override
            public void onClick(View view) {
                if(position < songList.size() - 1){
                    position++;
                } else{ // If current song position is the last of list, set next song to first position
                    position = 0;
                }
                startPlaying(position);
            }
        });

        // Set onClickListener for previous button
        previousBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * When previous button is clicked, decrease position by one and start playing
             * @param view
             */
            @Override
            public void onClick(View view) {
                if(position == 0){ // If current song is the first of list, set previous song to first position
                    position = 0;
                } else{
                    position--;
                }
                startPlaying(position);
            }
        });

    }

    /**
     * Start playing song method
     * @param position of the song on the list
     */
    private void startPlaying(int position){
        // If player is playing, reset the player
        if(mPlayer != null && mPlayer.isPlaying()){
            mPlayer.reset();
        }

        // Set song name
        songName = songList.get(position).getName();
        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        // Get uri of the song from the list by position and create media player for the uri
        Uri uri = Uri.parse(songList.get(position).toString());
        mPlayer = MediaPlayer.create(MusicPlayerActivity.this, uri);

        // Set onPreparedListener for the music player
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            /**
             * When music player is on prepared, setup duration bar and text and start playing
              * @param mediaPlayer
             */
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                String totalTime = createTimeLabel(mPlayer.getDuration());
                totTime.setText(totalTime);
                seekBar.setMax(mPlayer.getDuration());
                mPlayer.start();
            }
        });

        // Set on change listener for duration seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * When there is progress changed, set progress
             * @param seekBar
             * @param i
             * @param b
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    mPlayer.seekTo(i);
                    seekBar.setProgress(i);
                }
            }

            /**
             * onStartTrackingTouch method from onSeekBarChangeListener
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            /**
             * onStopTrackingTouch method from onSeekBarChangeListener
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        /**
         * Create new thread for music player
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mPlayer != null) {
                    try {
                        // When music player is on play, get message and send to handler
                        if (mPlayer.isPlaying()) {
                            Message msg = new Message();
                            msg.what = mPlayer.getCurrentPosition();
                            handler.sendMessage(msg);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // Create new handler for the thread
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        /**
         * Set duration seekbar progress and current time
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            int current_position = msg.what;
            seekBar.setProgress(current_position);
            String cTime = createTimeLabel(current_position);
            curTime.setText(cTime);
        }
    };

    /**
     * Create time label in mm:ss format from song duration
     * @param duration
     * @return string of time duration
     */
    public String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    /**
     * When "back to playlist" button is clicked, stop player and close activity
     * @param view
     */
    public void backToPlaylist(View view){
        mPlayer.stop();
        finish();
    }
}
