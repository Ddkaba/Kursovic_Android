package com.example.kursovicv21;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;

import java.util.concurrent.TimeUnit;

public class Player_Fragment extends Fragment {
    MediaPlayer player;
    ImageButton favourite, PlayStop;
    TextView Max, Min, NameSong;
    Slider Slider;
    float TotalTime;

    public Player_Fragment(){}
    public static Player_Fragment newInstance(){
        return new Player_Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_fragment, container, false);
        favourite = view.findViewById(R.id.Favorite);
        PlayStop = view.findViewById(R.id.PlayStop);
        Slider = view.findViewById(R.id.Slider);
        Max = view.findViewById(R.id.MaxValue);
        Min = view.findViewById(R.id.text);
        NameSong = view.findViewById(R.id.NameSong);
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = favourite.getDrawable();
                if(drawable.getConstantState().equals(getResources().getDrawable(R.drawable.not_favorite).getConstantState())) {
                    favourite.setImageResource(R.drawable.favorite);
                }
                else{
                    favourite.setImageResource(R.drawable.not_favorite);
                }
            }
        });
        player = MediaPlayer.create(getActivity(), R.raw.odium);
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
        return view;
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
