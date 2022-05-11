package com.example.kursovicv21;

import static android.content.Context.MODE_PRIVATE;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Radio_Fragment extends Fragment implements OnRadioSelectedListener, Color_Setting {
    public static Fragment newInstance() { return new Radio_Fragment();}
    ArrayList<Radio> radio = new ArrayList<>(); //ArrayList для данных радио, которые необходимы для заполнения RecyclerView
    ArrayList<Radio> SaveRadio = new ArrayList<>();
    ImageView SupportImageView, AddRadio;
    EditText NewNameRadio, NewURLRadio;
    static boolean preparation = false;
    ProgressBar SupportProgressBar;
    RecyclerView recyclerView;
    SharedPreferences Color;
    CardView RadioView;
    boolean isThis = false;
    int back_position = -1;
    int now_position = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.radio_fragment, container, false);
        initialization(view);
        Color =  this.getContext().getSharedPreferences("Color_setting", MODE_PRIVATE);
        Colors(Color);
        StopRelease();
        if(hasConnection(getContext())){
            Adding();
            ReadingRadio();
            if(SaveRadio.size() > 0){
                for (int i = 0; i < SaveRadio.size(); i++){
                    radio.add(SaveRadio.get(i));
                }
            }
            RadioAdapter adapter = new RadioAdapter(radio, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        else Toast.makeText(getContext(),R.string.Internet,Toast.LENGTH_LONG).show();

        AddRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View customView = inflater.inflate(R.layout.new_radio_dialog, null);
                NewNameRadio = customView.findViewById(R.id.NameNewRadio);
                NewURLRadio = customView.findViewById(R.id.URLNewRadio);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.AddNewRadio))
                        .setIcon(R.drawable.radio_black)
                        .setView(customView)
                        .setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(NewNameRadio.getText().toString().length() != 0 && NewURLRadio.getText().toString().length() != 0){
                                    String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
                                    if(!Pattern.compile(URL_REGEX).matcher(NewURLRadio.getText().toString()).find())
                                        Toast.makeText(getContext(), "Это не ссылка", Toast.LENGTH_LONG).show();
                                    else{
                                        SaveRadio.add(new Radio(NewURLRadio.getText().toString(),NewNameRadio.getText().toString(), R.drawable.radio_new_station));
                                        RecordingRadio();
                                        SaveRadio.clear();
                                        Radio_Fragment fragment = new Radio_Fragment();
                                        getFragmentManager().beginTransaction().replace(R.id.fl_content, fragment).addToBackStack(null).commit();
                                    }
                                }
                                else {
                                    Toast.makeText(getContext(), "Необходимо заполнить поля", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.Cancel), null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        StopRelease();
        super.onStop();
    }

    @Override
    public void clickOnImage(int position, ProgressBar myProgressBar, ImageView myImage) {//Обработка нажатия на элемент списка радио
        if(now_position != position){ //Нажатие было произведено на тот же элемент или нет
            if(back_position != -1) { //Скрытие всех элементов
                SupportProgressBar.setVisibility(View.INVISIBLE);
                SupportImageView.setVisibility(View.INVISIBLE);
            }
            MediaPlayer SupportMediaPlayer = new MediaPlayer(); //Создание дополнительного MediaPlayer
            try { MainActivity.mediaPlayer.stop(); MainActivity.mediaPlayer.release();} //Остановка и очистка основного MediaPlayer
            catch (Exception e) { e.printStackTrace(); }
            SupportMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); //Смена типа музыки
            SupportProgressBar = myProgressBar;
            SupportImageView = myImage;
            back_position = position;
            myProgressBar.setVisibility(View.VISIBLE);
            myImage.setVisibility(View.INVISIBLE);
            preparation = true;
            try { SupportMediaPlayer.setDataSource(radio.get(position).getURL()); } //Смена источника музыки
            catch (Exception e) { e.printStackTrace(); }
            MainActivity.mediaPlayer = SupportMediaPlayer; //Запись всех данных в основной MediaPlayer
            MainActivity.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) { //Метод загрузки песни, создающий для этого отдельный поток
                    mp.start();
                    now_position = position; //Запись позиции радио в списке источников, необходимо для дальнейшей работы
                    myImage.setVisibility(View.VISIBLE);
                    myProgressBar.setVisibility(View.INVISIBLE);
                    isThis = true;
                    preparation = false;
                }
            });
            MainActivity.mediaPlayer.prepareAsync();
        }
        else { //Если нажатие было на тот же элемент
            if(!MainActivity.mediaPlayer.isPlaying()){ //Если музыка не играет
                MainActivity.mediaPlayer.start();
                myImage.setVisibility(View.VISIBLE);
            }
            else{
                MainActivity.mediaPlayer.pause();
                myImage.setVisibility(View.INVISIBLE);
            }
            myProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void initialization(View view){ //Метод инициализации элементов интерфейса
        recyclerView = view.findViewById(R.id.Recycler_Radio);
        RadioView = view.findViewById(R.id.RadioCardView);
        AddRadio = view.findViewById(R.id.checkRadio);
    }

    public boolean hasConnection(final Context context){ //Метод проверки наличия интернета
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) return true;
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) return true;
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) return true;
        return false;
    }

    public void StopRelease(){
        if(MainActivity.mediaPlayer != null && MainActivity.mediaPlayer.isPlaying())
            if(!isThis) MainActivity.mediaPlayer.stop();
    }

    public void Colors(SharedPreferences sharedPreferences){ //Метод смены цвета приложения
        String Additionally_color = sharedPreferences.getString("Additionally Color", "Green");
        if(Additionally_color.equals("Orange")) RadioView.setCardBackgroundColor((ContextCompat.getColor(getContext(), R.color.orange)));
        if(Additionally_color.equals("Red")) RadioView.setCardBackgroundColor((ContextCompat.getColor(getContext(), R.color.red)));
        if(Additionally_color.equals("Green")) RadioView.setCardBackgroundColor((ContextCompat.getColor(getContext(), R.color.green)));
    }

    public void ReadingRadio(){ //Функция чтения из файла списка радио
        try {
            FileInputStream fis = new FileInputStream(requireContext().getFilesDir().getPath() + "/Radio.text");
            ObjectInputStream ois = new ObjectInputStream(fis);
            SaveRadio = (ArrayList<Radio>) ois.readObject();
            ois.close();
        } catch(Exception ex) { ex.printStackTrace(); }
    }

    public void RecordingRadio(){ //Функция записи в файла списка радио
        try {
            FileOutputStream fos = new FileOutputStream(requireContext().getFilesDir().getPath() + "/Radio.text");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(SaveRadio);
            oos.close();
            fos.close();
        } catch(Exception ex) { ex.printStackTrace(); }
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
}
