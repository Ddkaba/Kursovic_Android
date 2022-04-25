package com.example.kursovicv21;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Player_Fragment extends AppCompatActivity implements Color_Setting {
    ImageButton favourite, PlayStop, Next, Back, Player_Setting, Playlist;
    ArrayList<Audio> Favorite = new ArrayList<>();
    TextView Max, Min, NameSong, Author, Speed;
    CardView BottomCardView, TopCardView;
    SharedPreferences sharedPreferences;
    ArrayList<Audio> AudioList;
    float TotalTime, speed;
    Thread updateSlider;
    String name, author;
    MediaPlayer player;
    Integer position;
    Slider Slider;

    @Override
    public void onStop() {
        if(player != null && player.isPlaying()) player.pause();
        PlayStop.setImageResource(R.drawable.play);
        super.onStop();
    }

    @Override
    public void onResume() {
        if(player != null) {
            player.start();
            PlayStop.setImageResource(R.drawable.pause);
        }
        Colors(sharedPreferences);
        super.onResume();
    }

    @Nullable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.player_fragment);
        initialization();
        sharedPreferences =  this.getSharedPreferences("Color_setting", MODE_PRIVATE);
        Colors(sharedPreferences);
        try{
            AudioList = getIntent().getParcelableArrayListExtra("Audio");
            name = getIntent().getStringExtra("title");
            author = getIntent().getStringExtra("author");
            position = getIntent().getIntExtra("pos", 0);
            if(player != null) {
                player.stop();
                player.release();
            }
            Switching(position);
        }catch (Exception e){ e.printStackTrace(); }

        updateSlider = new Thread(){
            @Override
            public void run(){
                int currentposition = 0;
                while(currentposition<TotalTime){
                    try{
                        sleep(1000);
                        currentposition = player.getCurrentPosition();
                        Handler handler = new Handler(getApplicationContext().getMainLooper());
                        int Currentposition = currentposition;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Slider.setValue(Currentposition);
                            }
                        } );
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
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(@NonNull com.google.android.material.slider.Slider slider) { }
            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(@NonNull com.google.android.material.slider.Slider slider) { }
        });

        Slider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull com.google.android.material.slider.Slider slider, float value, boolean fromUser) {
                Slider.setLabelFormatter(new LabelFormatter() {
                    @NonNull
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf(createTimeLabel(Math.round(value)));
                    }
                });
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
                position = ((position+1)>AudioList.size()-1)?(0):(position+1);
                Switching(position);
            }
        });

        Player_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = favourite.getDrawable();
                if(drawable.getConstantState().equals(getResources().getDrawable(R.drawable.not_favorite_white).getConstantState())) {
                    favourite.setImageResource(R.drawable.favourite_white);
                    AddMusicFavorite();
                    FileOutput();
                }
                else{
                    favourite.setImageResource(R.drawable.not_favorite_white);
                    int Fposition = 0;
                    for(Audio audio : Favorite){
                        if (audio.getPath().equals(AudioList.get(position).getPath())) break;
                        Fposition = Fposition + 1;
                    }
                    Favorite.remove(Fposition);
                    FileOutput();
                }
            }
        });

        Speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float This_speed = player.getPlaybackParams().getSpeed();
                if(This_speed == 1.0) speed = 1.25f;
                if(This_speed == 1.25) speed = 1.5f;
                if(This_speed == 1.5) speed = 1.75f;
                if(This_speed == 1.75) speed = 2.0f;
                if(This_speed == 2.0) speed = 1.0f;
                Speed.setText(String.valueOf(speed));
                player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed));
            }
        });

        Playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onBackPressed(); }
        });

    }


    public void initialization(){ //Метод инициализации элементов
        BottomCardView = findViewById(R.id.Player_CardView_Bottom);
        TopCardView = findViewById(R.id.Player_CardView_Top);
        Player_Setting = findViewById(R.id.Player_setting);
        Playlist = findViewById(R.id.ToPlaylist);
        Speed = findViewById(R.id.SpeedOfTrack);
        favourite = findViewById(R.id.Favorite);
        PlayStop = findViewById(R.id.PlayStop);
        NameSong = findViewById(R.id.NameSong);
        Author = findViewById(R.id.AuthorName);
        Back = findViewById(R.id.BackButton);
        Next = findViewById(R.id.NextButton);
        Slider = findViewById(R.id.Slider);
        Max = findViewById(R.id.MaxValue);
        Min = findViewById(R.id.text);
    }


    public void Switching(int pos){ //Метод переключения между песнями
        if(player != null)   player.release();
        Slider.setValueTo(0);
        Uri uri = Uri.parse(AudioList.get(pos).getPath());
        player = MediaPlayer.create(getApplicationContext(),uri);
        TotalTime = player.getDuration();
        Slider.setValueTo(TotalTime);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                NameSong.setText(AudioList.get(pos).getTitle());
                Author.setText(AudioList.get(pos).getArtist());
                player.setLooping(false);
                player.seekTo(0);
                speed = 1.0f;
                Speed.setText("1.0");
                player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed));
                Max.setText(createTimeLabel((int)TotalTime));
                PlayStop.setImageResource(R.drawable.pause);
                IsFavourite();
                player.start();
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                position = ((position+1)>AudioList.size()-1)?(0):(position+1);
                Switching(position);
            }
        });

    }

    public String createTimeLabel(int time){ //Метод создания нового TextView для пользователя при прослушивании и переключении аудиозаписи
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

    public void IsFavourite(){
        try {
            FileInputStream fis = new FileInputStream(this.getFilesDir().getPath() + "/Favorite.text");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Favorite = (ArrayList<Audio>) ois.readObject();
            for(Audio audio : Favorite){
                if(audio.getPath().equals(AudioList.get(position).getPath())) {favourite.setImageResource(R.drawable.favourite_white); break; }
                else  favourite.setImageResource(R.drawable.not_favorite_white);
            }
            ois.close();
            fis.close();
        } catch(Exception ex) { ex.printStackTrace();
            Log.e("Error"," ");
        }
    }

    public void FileOutput(){
        try {
            FileOutputStream fos = new FileOutputStream(getApplicationContext().getFilesDir().getPath() + "/Favorite.text");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(Favorite);
            oos.close();
            fos.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void Colors(SharedPreferences sharedPreferences) { //Метод смены цвета приложения
        String Additionally_color = sharedPreferences.getString("Additionally Color", "Green");
        if (Additionally_color.equals("Orange")) {
            Slider.setTrackActiveTintList(ColorStateList.valueOf((ContextCompat.getColor(getApplicationContext(), R.color.orange))));
            Slider.setTrackInactiveTintList(ColorStateList.valueOf((ContextCompat.getColor(getApplicationContext(), R.color.red))));
            Slider.setThumbTintList(ColorStateList.valueOf((ContextCompat.getColor(getApplicationContext(), R.color.orange))));
            BottomCardView.setCardBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.orange)));
            TopCardView.setCardBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.orange)));
            Player_Setting.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.orange)));
            PlayStop.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.orange)));
            Next.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.orange)));
            Back.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.orange)));
        }
        if (Additionally_color.equals("Red")){
            Slider.setTrackInactiveTintList(ColorStateList.valueOf((ContextCompat.getColor(getApplicationContext(), R.color.orange))));
            Slider.setTrackActiveTintList(ColorStateList.valueOf((ContextCompat.getColor(getApplicationContext(), R.color.red))));
            Slider.setThumbTintList(ColorStateList.valueOf((ContextCompat.getColor(getApplicationContext(), R.color.red))));
            BottomCardView.setCardBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.red)));
            TopCardView.setCardBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.red)));
            Player_Setting.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.red)));
            PlayStop.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.red)));
            Next.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.red)));
            Back.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.red)));

        }
        if (Additionally_color.equals("Green")){
            Slider.setThumbTintList(ColorStateList.valueOf((ContextCompat.getColor(getApplicationContext(), R.color.black_yellow))));
            Slider.setTrackInactiveTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.green)));
            Slider.setTrackActiveTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.yellow)));
            BottomCardView.setCardBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.green)));
            TopCardView.setCardBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.green)));
            Player_Setting.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.green)));
            PlayStop.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.green)));
            Next.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.green)));
            Back.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.green)));
        }
    }
}
