package com.example.kursovicv21;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.slider.Slider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Player_Fragment extends Fragment {
    static MediaPlayer player;
    ArrayList<Audio> AudioList;
    ArrayList<Audio> Favorite = new ArrayList<>();
    Integer position;
    ImageButton favourite, PlayStop, Next, Back;
    TextView Max, Min, NameSong, Author;
    Slider Slider;
    String name, author;
    Thread updateSlider;
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
        Author = view.findViewById(R.id.AuthorName);
        Back = view.findViewById(R.id.BackButton);
        Next = view.findViewById(R.id.NextButton);

        try{
            AudioList = getArguments().getParcelableArrayList("Audio");
            name = getArguments().getString("title");
            author = getArguments().getString("author");
            position = getArguments().getInt("pos");
            if(player != null)
            {
                player.stop();
                player.release();
            }
            Switching(position);
        }catch (Exception e){
            e.printStackTrace();
        }

        updateSlider = new Thread(){
            @Override
            public void run(){
                int currentposition = 0;
                while(currentposition<TotalTime){
                    try{
                        sleep(1000);
                        currentposition = player.getCurrentPosition();
                        Slider.setValue(currentposition);
                    }
                    catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        Slider.setValueTo(TotalTime);
        updateSlider.start();

        Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull com.google.android.material.slider.Slider slider) { }

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

        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTimeLabel(player.getCurrentPosition());
                Min.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        PlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying()) {
                    player.pause();
                    PlayStop.setImageResource(R.drawable.play);
                }
                else {
                    player.start();
                    PlayStop.setImageResource(R.drawable.pause);
                }
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                player.release();
                position = ((position-1)<0)?(AudioList.size()-1):(position-1);
                Switching(position);
            }
        });

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                player.release();
                position = ((position+1)%AudioList.size());
                Switching(position);
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = favourite.getDrawable();
                if(drawable.getConstantState().equals(getResources().getDrawable(R.drawable.not_favorite_white).getConstantState())) {
                    favourite.setImageResource(R.drawable.favorite_white);
                    AddMusicFavorite();
                    try {
                        FileOutputStream fos = new FileOutputStream(requireContext().getFilesDir().getPath() + "/Favorite.text");
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(Favorite);
                        oos.close();
                        fos.close();
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else{
                    favourite.setImageResource(R.drawable.not_favorite_white);
                    int Fposition = 0;
                    for(Audio audio : Favorite){
                        if (audio.getPath().equals(AudioList.get(position).getPath())) break;
                        Fposition = Fposition + 1;
                    }
                    Favorite.remove(Fposition);
                    try {
                        FileOutputStream fos = new FileOutputStream(requireContext().getFilesDir().getPath() + "/Favorite.text");
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(Favorite);
                        oos.close();
                        fos.close();
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer player) {
                Next.performClick();
            }
        });

        return view;
    }

    public void Switching(int pos){
        Uri uri = Uri.parse(AudioList.get(pos).getPath());
        player = MediaPlayer.create(getContext(),uri);
        NameSong.setText(AudioList.get(pos).getTitle());
        Author.setText(AudioList.get(pos).getArtist());
        TotalTime = player.getDuration();
        player.setLooping(true);
        player.seekTo(0);
        Slider.setValueTo(0);
        Slider.setValueTo(TotalTime);
        float speed = 1.0f;
        player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed));
        Max.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes((long) TotalTime), TimeUnit.MILLISECONDS.toSeconds((long) TotalTime) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) TotalTime))));
        PlayStop.setImageResource(R.drawable.pause);
        try {
            FileInputStream fis = new FileInputStream(requireContext().getFilesDir().getPath() + "/Favorite.text");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Favorite = (ArrayList<Audio>) ois.readObject();
            for(Audio audio : Favorite){
                if(audio.getPath().equals(AudioList.get(position).getPath())) {favourite.setImageResource(R.drawable.favorite_white); break; }
                else  favourite.setImageResource(R.drawable.not_favorite_white);
            }
            ois.close();
            fis.close();
        } catch(Exception ex) { ex.printStackTrace();
            Log.e("Error"," ");
        }
        player.start();
    }

    public String createTimeLabel(int time){
        String timeLabel;
        int min = time/1000/60;
        int sec = time/1000 % 60;
        timeLabel = min + ":";
        if(sec < 10) timeLabel +="0";
        timeLabel += sec;
        return  timeLabel;
    }

    private ArrayList<Audio> AddMusicFavorite(){
        String path = AudioList.get(position).getPath();
        String title = AudioList.get(position).getTitle();
        String artist = AudioList.get(position).getArtist();
        Audio audio = new Audio(path, title, artist);
        Favorite.add(audio);
        return Favorite;
    }

}
