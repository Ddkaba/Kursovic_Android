package com.example.kursovicv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    MediaPlayer player;
    ImageButton playlist, farovite, PlayStop;
    TextView Max, Min, NameSong;
    Slider Slider;
    float TotalTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.playmusic);
        playlist = findViewById(R.id.ToPlaylist);
        farovite = findViewById(R.id.Favorite);
        PlayStop = findViewById(R.id.PlayStop);
        Slider = findViewById(R.id.Slider);
        Max = findViewById(R.id.MaxValue);
        Min = findViewById(R.id.text);
        NameSong = findViewById(R.id.NameSong);
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Playlist.class);
                startActivity(intent);
            }
        });
        farovite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = farovite.getDrawable();
                if(drawable.getConstantState().equals(getResources().getDrawable(R.drawable.not_favorite).getConstantState())) {
                    farovite.setImageResource(R.drawable.favorite);
                }
                else{
                    farovite.setImageResource(R.drawable.not_favorite);
                }
            }
        });
        player = MediaPlayer.create(MainActivity.this, R.raw.odium);
        TotalTime = player.getDuration();
        player.setLooping(true);
        player.seekTo(0);
        Slider.setValueTo(TotalTime);
        float speed = 1.0f;
        player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed));
        player.pause();
        Max.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes((long) TotalTime), TimeUnit.MILLISECONDS.toSeconds((long) TotalTime) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) TotalTime))));
        Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull com.google.android.material.slider.Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull com.google.android.material.slider.Slider slider) {

            }
        });

        Slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull com.google.android.material.slider.Slider slider, float value, boolean fromUser) {
                if(fromUser){
                    player.seekTo(Math.round(value));
                    Slider.setValue(value);
                }
            }
        });
        PlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!player.isPlaying()) {
                    player.start();
                    PlayStop.setImageResource(R.drawable.pause);
                }
                else {
                    player.pause();
                    PlayStop.setImageResource(R.drawable.play);
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (player!=null){
                    try {
                        Message msg = new Message();
                        msg.what = player.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }
            }
        }).start();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            int current = msg.what;
            Slider.setValue(current);
            String elapsedTime = createTimeLabel(current);
            Min.setText(elapsedTime);
        }
    };

    public String createTimeLabel(int time){
        String timeLabel = "";
        int min = time/1000/60;
        int sec = time/1000 % 60;
        timeLabel = min + ":";
        if(sec < 10) timeLabel +="0";
        timeLabel += sec;
        return  timeLabel;
    }
}