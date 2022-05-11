package com.example.kursovicv21;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;


public class SettingActivity extends AppCompatActivity implements OnSettingSelectedListener, Color_Setting, Languages_Setting {
    ArrayList<Setting> settings = new ArrayList<>();
    ConstraintLayout mConstraintLayout, mSetting_item;
    TextView Name_Setting, Description;
    SharedPreferences setting;
    CardView Setting_CardView;
    RecyclerView recyclerView;
    SharedPreferences Color;
    ImageView Back, QR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        getSupportActionBar().hide();  //Метод скрывающий верную планку приложения
        Adding(); //Метод добавления данных в массив
        initialization(); //Метод инициализации элементов интерфейса
        Color = getSharedPreferences("Color_setting", MODE_PRIVATE); //Получение настроек цвета
        Colors(Color);
        SettingAdapter settingAdapter = new SettingAdapter(settings, this);
        recyclerView.setAdapter(settingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Метод обработки нажатия на кнопку назад, возвращает обратно к плейлистам
                onBackPressed();
            }
        });
    }

    public void initialization(){ //Метод инициализации элементов
        recyclerView = findViewById(R.id.Setting_recyclerView);
        mConstraintLayout = findViewById(R.id.Setting_ConstrainLayout);
        mSetting_item = findViewById(R.id.Setting_ConstrainLayout_item);
        Setting_CardView = findViewById(R.id.Setting_cardView);
        Name_Setting = findViewById(R.id.Name_setting);
        Back = findViewById(R.id.Back_ImageView);
    }

    public void Adding() { //Добавление новых элементов в список радиостанций
        settings.add(new Setting(getResources().getString(R.string.Control_Color), R.drawable.palette_white));
        settings.add(new Setting(getResources().getString(R.string.Language), R.drawable.language_white));
        settings.add(new Setting(getResources().getString(R.string.Info), R.drawable.info_white));
    }

    @Override
    public void OnSettingListener(int position, View v, ArrayList<ImageView> arrayList) { //Обработка нажатий на элемент меню настроек
        switch (position) {
            case 0:
                showColorMenu(); //Вывод меню настроек цвета
                break;
            case 1:
                showLanguagesMenu(); //Вывод меню настроек языка
                break;
            case 2:
                Information(); //Вывод информации про проект
                break;
            default:
                break;
        }
    }

    private void showLanguagesMenu() { //Метод вызова всплывающего меню управления языками
        CharSequence[] Languages_items = {"Русский", "English", "Deutsch", "Suomalainen", "Svenska"};
        setting = getSharedPreferences("Languages_setting", MODE_PRIVATE); //Получение настроек языка
        SharedPreferences.Editor editor = setting.edit(); //Разрешение на изменение настроек языка
        AlertDialog.Builder  builder = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.Language))
                .setItems(Languages_items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                editor.putString("language","ru"); editor.apply(); language();
                                break;
                            case 1:
                                editor.putString("language","en"); editor.apply(); language();
                                break;
                            case 2:
                                editor.putString("language","de"); editor.apply(); language();
                                break;
                            case 3:
                                editor.putString("language","fi"); editor.apply(); language();
                                break;
                            case 4:
                                editor.putString("language","sv"); editor.apply(); language();
                                break;
                            default:
                                break;
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void language(){ //Метод обновления локализации приложения
        String language = setting.getString("language", "ru" );
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
        recreate(); //Пересоздание активности
    }

    private void showColorMenu() { //Метод вызова всплывающего меню управления цветами
        CharSequence[] Color_items = {getResources().getString(R.string.Green), getResources().getString(R.string.Orange),
                getResources().getString(R.string.Red)};
        SharedPreferences.Editor editor = Color.edit(); //Разрешение на изменение настроек цвета
        AlertDialog.Builder  builder = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.Control_Color))
                .setItems(Color_items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                editor.putString("Additionally Color","Green"); editor.apply(); Colors(Color);
                                break;
                            case 1:
                                editor.putString("Additionally Color","Orange"); editor.apply(); Colors(Color);
                                break;
                            case 2:
                                editor.putString("Additionally Color","Red"); editor.apply(); Colors(Color);
                                break;
                            default:
                                break;
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void Colors(SharedPreferences sharedPreferences){ //Метод смены цвета приложения
        String Additionally_color = sharedPreferences.getString("Additionally Color", "Green");
        if(Additionally_color.equals("Orange")) Setting_CardView.setCardBackgroundColor((ContextCompat.getColor(this, R.color.orange)));
        if(Additionally_color.equals("Red")) Setting_CardView.setCardBackgroundColor((ContextCompat.getColor(this, R.color.red)));
        if(Additionally_color.equals("Green"))Setting_CardView.setCardBackgroundColor((ContextCompat.getColor(this, R.color.green)));
    }

    public void Information(){ //Метод для вывода информации о проекте
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.information_dialog, null);
        Description = customView.findViewById(R.id.Description);
        QR = customView.findViewById(R.id.QR);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.Info))
                .setIcon(R.drawable.info)
                .setView(customView)
                .setPositiveButton(getResources().getString(R.string.OK), null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Description.setText(getResources().getString(R.string.Music_Player) + "\n" + getResources().getString(R.string.Customer)
                + "\n" + getResources().getString(R.string.Executor) + "\n" + "<-" + getResources().getString(R.string.QR));
        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QR.setImageResource(R.drawable.qr1);
            }
        });
    }
}