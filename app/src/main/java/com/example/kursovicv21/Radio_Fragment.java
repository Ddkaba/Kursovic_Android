package com.example.kursovicv21;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class Radio_Fragment extends Fragment implements OnRadioSelectedListener {
    public static Fragment newInstance() { return new Radio_Fragment();}
    ArrayList<Radio> radio = new ArrayList<>(); //ArrayList для данных радио, которые необходимы для заполнения RecyclerView
    MediaPlayer mediaPlayer = new MediaPlayer(); //MediaPlayer для проигрыша радио
    RecyclerView recyclerView;
    ProgressBar SupportProgressBar;
    ImageView SupportImageView;
    int backposition = -1;
    int nowposition = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.radio_fragment, container, false);
        recyclerView = view.findViewById(R.id.Recycler_Radio);
        if(hasConnection(getContext())){
            Adding();
            RadioAdapter adapter = new RadioAdapter(radio, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        else{
            Toast toast = Toast.makeText(getContext(),R.string.Internet,Toast.LENGTH_LONG);
            toast.show();
        }
        return view;
    }

    @Override
    public void clickOnImage(int position, ProgressBar myProgressBar, ImageView myImage) {//Обработка нажатия на элемент списка радио
        if(nowposition != position){ //Нажатие было произведено на тот же элемент или нет
            if(backposition != -1) { //Скрытие всех элементов
                SupportProgressBar.setVisibility(View.INVISIBLE);
                SupportImageView.setVisibility(View.INVISIBLE);
            }
            MediaPlayer SupportMediaPlayer = new MediaPlayer(); //Создание дополнительного MediaPlayer
            try { mediaPlayer.stop(); mediaPlayer.release();} //Остановка и очистка основного MediaPlayer
            catch (Exception e) { e.printStackTrace(); }
            SupportMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); //Смена типа музыки
            SupportProgressBar = myProgressBar;
            SupportImageView = myImage;
            backposition = position;
            myProgressBar.setVisibility(View.VISIBLE);
            myImage.setVisibility(View.INVISIBLE);
            try { SupportMediaPlayer.setDataSource(radio.get(position).getURL()); } //Смена источника музыки
            catch (Exception e) { e.printStackTrace(); }
            mediaPlayer = SupportMediaPlayer; //Запись всех данных в основной MediaPlayer
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) { //Метод загрузки песни, создающий для этого отдельный поток
                    mp.start();
                    nowposition = position; //Запись позиции радио в списке источников, необходимо для дальнейшей работы
                    myImage.setImageResource(R.drawable.audiotrack_white);
                    myImage.setVisibility(View.VISIBLE);
                    myProgressBar.setVisibility(View.INVISIBLE);
                }
            });
            mediaPlayer.prepareAsync();
        }
        else { //Если нажатие было на тот же элемент
            if(!mediaPlayer.isPlaying()){ //Если музыка играет
                mediaPlayer.start();
                myImage.setVisibility(View.VISIBLE);
            }
            else{
                mediaPlayer.pause();
                myImage.setVisibility(View.INVISIBLE);
            }
            myProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void Adding(){ //Добавление новых элементов в список радиостанций
        radio.add(new Radio("https://icecast-studio21.cdnvideo.ru/S21cl_1s", "Studio21", R.drawable.studio21_radio));
        radio.add(new Radio("https://emgspb.hostingradio.ru/europaplusspb128.mp3", "EuropaPlus", R.drawable.europa_plus_radio));
        radio.add(new Radio("https://pub0301.101.ru:8443/stream/air/mp3/256/99","Energy", R.drawable.energy_radio));
        radio.add(new Radio("https://radiorecord.hostingradio.ru/rr_main96.aacp","Record", R.drawable.radio_record));
        radio.add(new Radio("https://radiorecord.hostingradio.ru/deep96.aacp","Record Deep", R.drawable.record_deep_radio));
        radio.add(new Radio("https://icecast227.ptspb.ru:8443/monte", "Monte Carlo", R.drawable.monte_carlo_radio));
        radio.add(new Radio("https://r163-172-185-220.relay.radiotoolkit.com:30003/spdeep","SoundPark Deep", R.drawable.soundpark_deep_radio));
        radio.add(new Radio("https://maximum.hostingradio.ru/maximum96.aacp", "MAXIMUM", R.drawable.maximum_radio));
        radio.add(new Radio("https://radiokrug.ru/radio/piterfm/icecast.audio", "PiterFM", R.drawable.piter_fm_radio));
        radio.add(new Radio("https://nashe1.hostingradio.ru/rock-256","RockFM", R.drawable.rock_fm_radio));
        radio.add(new Radio("https://hitfm.hostingradio.ru/hitfm96.aacp","Hit FM", R.drawable.hit_fm_radio));
        radio.add(new Radio("https://r163-172-168-211.relay.radiotoolkit.com:30003/svoefm", "Свое FM", R.drawable.svoe_fm_radio));
        radio.add(new Radio("https://dfm-dfmrusdance.hostingradio.ru/dfmrusdance96.aacp", "Russian Dance", R.drawable.dfm_rus_dance_radio));
        radio.add(new Radio("https://dfm-dfmdeep.hostingradio.ru/dfmdeep96.aacp", "DEEP", R.drawable.dfm_deep_radio));
    }

    public static boolean hasConnection(final Context context) //Метод проверки наличия интернета
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) return true;
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) return true;
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) return true;
        return false;
    }

}
