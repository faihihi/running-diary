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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.File;
import java.util.ArrayList;

import au.edu.sydney.comp5216.running_diary.R;

public class MusicPlayerActivity extends AppCompatActivity {
    private ImageView pausePlayBtn, nextBtn, previousBtn, logo;
    private TextView songNameTxt, curTime, totTime;
    private SeekBar seekBar;

    private MediaPlayer mPlayer;
    private int position;
    private ArrayList<File> songList;
    private String songName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);

        pausePlayBtn = findViewById(R.id.pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);
        logo = findViewById(R.id.logo);

        songNameTxt = findViewById(R.id.songName);
        curTime = findViewById(R.id.duration_start);
        totTime = findViewById(R.id.duration_end);

        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        songList = (ArrayList) bundle.getParcelableArrayList("song");
        position = bundle.getInt("position", 0);

        startPlaying(position);


        pausePlayBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(mPlayer.isPlaying()){
                    pausePlayBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    mPlayer.pause();
                } else{
                    pausePlayBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                    mPlayer.start();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position < songList.size() - 1){
                    position++;
                } else{
                    position = 0;
                }

                startPlaying(position);
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == 0){
                    position = 0;
                } else{
                    position--;
                }

                startPlaying(position);
            }
        });

    }

    private void startPlaying(int position){
        if(mPlayer != null && mPlayer.isPlaying()){
            mPlayer.reset();
        }

        songName = songList.get(position).getName();
        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        Uri uri = Uri.parse(songList.get(position).toString());
        mPlayer = MediaPlayer.create(MusicPlayerActivity.this, uri);


        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                String totalTime = createTimeLabel(mPlayer.getDuration());
                totTime.setText(totalTime);
                seekBar.setMax(mPlayer.getDuration());
                mPlayer.start();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    mPlayer.seekTo(i);
                    seekBar.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mPlayer != null) {
                    try {
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

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int current_position = msg.what;
            seekBar.setProgress(current_position);
            String cTime = createTimeLabel(current_position);
            curTime.setText(cTime);
        }
    };

    public String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    public void backToPlaylist(View view){
        finish();
    }
}
