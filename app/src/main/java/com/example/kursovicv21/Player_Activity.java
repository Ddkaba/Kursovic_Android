package com.example.kursovicv21;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Player_Activity extends AppCompatActivity implements Color_Setting, TimePickerDialog.OnTimeSetListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private static final String TAG = "MyActivity";
    public static final int SWIPE = 100;
    private GestureDetectorCompat gestureDetectorCompat;
    ImageButton favourite, PlayStop, Next, Back, Player_Setting, Playlist, Timer;
    ArrayList<Audio> Favorite = new ArrayList<>();
    TextView Max, Min, NameSong, Author, Speed;
    CardView BottomCardView, TopCardView;
    SharedPreferences sharedPreferences;
    ArrayList<Audio> AudioList;
    float TotalTime, speed;
    Thread updateSlider;
    String name, author;
    Integer position;
    Slider Slider;

    @Override
    public void onResume() {
        Colors(sharedPreferences);
        super.onResume();
    }

    @Nullable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.player);
        initialization();
        Slider.setValue(0);
        NameSong.setSelected(true);
        Author.setSelected(true);
        gestureDetectorCompat = new GestureDetectorCompat(this, this);
        sharedPreferences =  this.getSharedPreferences("Color_setting", MODE_PRIVATE);
        Colors(sharedPreferences);
        try{
            AudioList = getIntent().getParcelableArrayListExtra("Audio");
            name = getIntent().getStringExtra("title");
            author = getIntent().getStringExtra("author");
            position = getIntent().getIntExtra("pos", 0);
            Switching(position);
        }catch (Exception e){ e.printStackTrace(); }


        updateSlider = new Thread(){ //Создание нового потока для обновления слидера
            @Override
            public void run(){ //Создание нового потока для обновления слайдера
                float current_position = 0;
                while(current_position<TotalTime){
                    try{
                        sleep(1000);
                        current_position = MainActivity.mediaPlayer.getCurrentPosition();
                        Handler handler = new Handler(getApplicationContext().getMainLooper());
                        float Current_position = current_position;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Slider.setValue(Current_position);
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

        Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {  //Метод определяющий прикосновение к слайдеру
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(@NonNull com.google.android.material.slider.Slider slider) { }
            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(@NonNull com.google.android.material.slider.Slider slider) { }
        });

        Slider.addOnChangeListener(new Slider.OnChangeListener() { //Метод перемотки аудиозаписи и смены значения слайдера
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
                    MainActivity.mediaPlayer.seekTo(Math.round(value));
                    Slider.setValue(value);
                }
            }
        });

        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() { //Handler для обновления TextView при прослушивании
            @Override
            public void run() {
                String currentTime = createTimeLabel(MainActivity.mediaPlayer.getCurrentPosition());
                Min.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        PlayStop.setOnClickListener(new View.OnClickListener() { //Метод остановки или воспроизведение прослушивания при нажатии на кнопку
            @Override
            public void onClick(View v) {
                SwitchingInterface();
            }
        });

        Back.setOnClickListener(new View.OnClickListener() { //Метод включения прошлой аудиозаписи
            @Override
            public void onClick(View v) {
                MainActivity.mediaPlayer.stop();
                BackSong();
            }
        });

        Next.setOnClickListener(new View.OnClickListener() { //Метод включения следующей аудиозаписи
            @Override
            public void onClick(View v) {
                MainActivity.mediaPlayer.stop();
                NextSong();
            }
        });

        Player_Setting.setOnClickListener(new View.OnClickListener() { //Метод для перехода в меню настроек
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() { //Метод для добавления или удаления аудиозаписи в плейлист избранных записей
            @Override
            public void onClick(View v) {
                Drawable drawable = favourite.getDrawable();
                if(drawable.getConstantState().equals(getResources().getDrawable(R.drawable.not_favorite_white).getConstantState())) {
                    favourite.setImageResource(R.drawable.favourite_white);
                    AddMusicFavorite();
                }
                else{
                    favourite.setImageResource(R.drawable.not_favorite_white);
                    int Fposition = 0;
                    for(Audio audio : Favorite){
                        if (audio.getPath().equals(AudioList.get(position).getPath())) break;
                        Fposition = Fposition + 1;
                    }
                    Favorite.remove(Fposition);
                }
                FileOutput();
            }
        });

        Speed.setOnClickListener(new View.OnClickListener() { //Метод смены скорости прослушивания
            @Override
            public void onClick(View v) {
                float This_speed = MainActivity.mediaPlayer.getPlaybackParams().getSpeed();
                if(This_speed == 1.0) speed = 1.25f;
                if(This_speed == 1.25) speed = 1.5f;
                if(This_speed == 1.5) speed = 1.75f;
                if(This_speed == 1.75) speed = 2.0f;
                if(This_speed == 2.0) speed = 1.0f;
                Speed.setText(String.valueOf(speed));
                MainActivity.mediaPlayer.setPlaybackParams(MainActivity.mediaPlayer.getPlaybackParams().setSpeed(speed));
            }
        });

        Timer.setOnClickListener(new View.OnClickListener() { //Метод запуска таймера сна
            @Override
            public void onClick(View v) {
               new TimePickerDialog(Player_Activity.this, (TimePickerDialog.OnTimeSetListener) Player_Activity.this, 0 , 0, true).show();
            }
        });

        Playlist.setOnClickListener(new View.OnClickListener() { //Метод для перехода в плейлист
            @Override
            public void onClick(View v) { onBackPressed(); }
        });

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) { //Метод для работы таймера сна
        int TimeHour = hourOfDay*60*60*1000;
        int TimeMinutes = minute*60*1000;
        Timer.setImageResource(R.drawable.timer_grey);
        Playlist.setEnabled(false);
        new CountDownTimer(TimeHour+TimeMinutes, 1000){
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                MainActivity.mediaPlayer.pause();
                Playlist.setEnabled(true);
                PlayStop.setImageResource(R.drawable.play);
                Timer.setImageResource(R.drawable.timer);
            }
        }.start();
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
        Timer = findViewById(R.id.Timer);
        Min = findViewById(R.id.text);
    }

    public void SwitchingInterface(){ //Метод для остановки и воспроизведения аудиозаписи
        if(MainActivity.mediaPlayer.isPlaying()) {
            MainActivity.mediaPlayer.pause();
            PlayStop.setImageResource(R.drawable.play);
        }
        else {
            MainActivity.mediaPlayer.start();
            PlayStop.setImageResource(R.drawable.pause);
        }
    }

    public void NextSong(){  //Метод для переключения на следующую аудиозапись
        position = ((position+1)>AudioList.size()-1)?(0):(position+1);
        Switching(position);
    }

    public void BackSong(){ //Метод для переключения на прошлую аудиозапись
        position = ((position-1)<0)?(AudioList.size()-1):(position-1);
        Switching(position);
    }

    public void Switching(int pos){ //Метод переключения между песнями
        if(MainActivity.mediaPlayer != null)   MainActivity.mediaPlayer.release();
        Slider.setValue(0);
        Uri uri = Uri.parse(AudioList.get(pos).getPath());
        MainActivity.mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        MainActivity.mediaPlayer.setLooping(false);
        TotalTime = MainActivity.mediaPlayer.getDuration();
        Slider.setValueTo(TotalTime);
        MainActivity.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                speed = 1.0f;
                Speed.setText("1.0");
                NameSong.setText(AudioList.get(pos).getTitle());
                Author.setText(AudioList.get(pos).getArtist());
                MainActivity.mediaPlayer.seekTo(0);
                MainActivity.mediaPlayer.setPlaybackParams(MainActivity.mediaPlayer.getPlaybackParams().setSpeed(speed));
                Max.setText(createTimeLabel((int)TotalTime));
                PlayStop.setImageResource(R.drawable.pause);
                IsFavourite();
                MainActivity.mediaPlayer.start();
            }
        });

        MainActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) { //Метод, которые вызывается при окончании прослушивания аудиозаписи
                Log.e(TAG, "onComplete hit");
                mp.reset();
                Slider.setValue(0);
                Slider.setValueTo(1);
                NextSong();
            }
        });

    }


    public String createTimeLabel(int time){ //Метод создания нового TextView для пользователя при прослушивании и перемотки аудиозаписи
        String timeLabel;
        int min = time/1000/60;
        int sec = time/1000 % 60;
        timeLabel = min + ":";
        if(sec < 10) timeLabel +="0";
        timeLabel += sec;
        return  timeLabel;
    }

    private ArrayList<Audio> AddMusicFavorite(){ //Метод добавления аудиозаписи в плейлист избранных аудиозаписей
        String path = AudioList.get(position).getPath();
        String title = AudioList.get(position).getTitle();
        String artist = AudioList.get(position).getArtist();
        Audio audio = new Audio(path, title, artist);
        Favorite.add(audio);
        return Favorite;
    }

    public void IsFavourite(){ // Метод проверяющий добавлена ли песня в плейлист избранных
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

    public void FileOutput(){ //Метод для сохранения плейлиста избранных аудиозаписей в файл
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

    @Override
    public boolean onDown(MotionEvent e) { return false; }

    @Override
    public void onShowPress(MotionEvent e) { }

    @Override
    public boolean onSingleTapUp(MotionEvent e) { return false; }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }

    @Override
    public void onLongPress(MotionEvent e) { }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { //Метод, обрабатывающий скроллинг по экрану
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if(Math.abs(diffX) > Math.abs(diffY)){
            if(Math.abs(diffX) > SWIPE && Math.abs(velocityX) > SWIPE){
                MainActivity.mediaPlayer.stop();
                if(diffX > 0){
                    BackSong();
                }
                else{
                    NextSong();
                }
            }
        }
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) { //Метод, обрабатывающий двойное нажатие на экран
        SwitchingInterface();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { //Метод определяющий событие нажатия на экран
        gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void Colors(SharedPreferences sharedPreferences) { //Метод смены цвета
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
