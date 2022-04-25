package com.example.kursovicv21;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
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
        Adding();
        initialization();
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
        Description = findViewById(R.id.Description);
        Back = findViewById(R.id.Back_ImageView);
        QR = findViewById(R.id.QR);
    }


    public void Adding() { //Добавление новых элементов в список радиостанций
        settings.add(new Setting(getResources().getString(R.string.Control_Color), R.drawable.palette_white));
        settings.add(new Setting(getResources().getString(R.string.Language), R.drawable.language_white));
        //settings.add(new Setting(getResources().getString(R.string.Scale), R.drawable.zoom_in_white));
        settings.add(new Setting(getResources().getString(R.string.Info), R.drawable.info_white));
    }

    @Override
    public void OnSettingListener(int position, View v, ArrayList<ImageView> arrayList) { //Обработка нажатий на элемент меню настроек
        PopupMenu popupMenu = new PopupMenu(this, v);
        switch (position) {
            case 0:
                popupMenu.inflate(R.menu.color_menu);
                showColorMenu(popupMenu); //Вывод меню настроек цвета
                break;
            case 1:
                popupMenu.inflate(R.menu.languages_menu);
                showLanguagesMenu(popupMenu); //Вывод меню настроек языка
                break;
            case 2:
            case 3:
                Information();
                break;
        }
    }


    private void showLanguagesMenu(PopupMenu popupMenu) { //Метод вызова всплывающего меню управления языками
        setting = getSharedPreferences("Languages_setting", MODE_PRIVATE); //Получение настроек языка
        SharedPreferences.Editor editor = setting.edit(); //Разрешение на изменение настроек языка
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Russian:
                        editor.putString("language","ru"); editor.apply(); language();
                        return true;
                    case R.id.English:
                        editor.putString("language","en"); editor.apply(); language();
                        return true;
                    case R.id.German:
                        editor.putString("language","de"); editor.apply(); language();
                        return true;
                    case R.id.Finnish:
                        editor.putString("language","fi"); editor.apply(); language();
                        return true;
                    case R.id.Swedish:
                        editor.putString("language","sv"); editor.apply(); language();
                        return true;
                    default:
                        language();
                        return false;
                }
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) { //Метод убирающий меню при нажатии на любую часть меню, кроме самого меню
            }
        });
        popupMenu.show();
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

    private void showColorMenu(PopupMenu popupMenu) { //Метод вызова всплывающего меню управления цветами
        SharedPreferences.Editor editor = Color.edit(); //Разрешение на изменение настроек цвета
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.White_Color:
                        Toast toast = Toast.makeText(getBaseContext(), "Функция находится в разработке", Toast.LENGTH_LONG);
                        toast.show();
                        editor.putString("Basic Color","White"); editor.apply(); Colors(Color);
                        return true;
                    case R.id.Black_Color:
                        Toast toast1 = Toast.makeText(getBaseContext(), "Функция находится в разработке", Toast.LENGTH_LONG);
                        toast1.show();
                        editor.putString("Basic Color","Black"); editor.apply(); Colors(Color);
                        return true;
                    case R.id.Green_Color:
                        editor.putString("Additionally Color","Green"); editor.apply(); Colors(Color);
                        return true;
                    case R.id.Orange_Color:
                        editor.putString("Additionally Color","Orange"); editor.apply(); Colors(Color);
                        return true;
                    case R.id.Red_Color:
                        editor.putString("Additionally Color","Red"); editor.apply(); Colors(Color);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) { //Метод убирающий меню при нажатии на любую часть меню, кроме самого меню
            }
        });
        popupMenu.show();
    }

    public void Colors(SharedPreferences sharedPreferences){ //Метод смены цвета приложения
        String Basic_color = sharedPreferences.getString("Basic Color", "Black");
        String Additionally_color = sharedPreferences.getString("Additionally Color", "Green");
        if(Additionally_color.equals("Orange")) Setting_CardView.setCardBackgroundColor((ContextCompat.getColor(this, R.color.orange)));
        if(Additionally_color.equals("Red")) Setting_CardView.setCardBackgroundColor((ContextCompat.getColor(this, R.color.red)));
        if(Additionally_color.equals("Green"))Setting_CardView.setCardBackgroundColor((ContextCompat.getColor(this, R.color.green)));
    }

    public void Information(){ //Метод для вывода информации о проекте
        QR.setImageResource(R.drawable.qr);
        Description.setText(getResources().getString(R.string.Music_Player) + "\n" + getResources().getString(R.string.Customer)
                + "\n" + getResources().getString(R.string.Executor) + "\n" + "<-" + getResources().getString(R.string.QR));
        Description.setVisibility(View.VISIBLE);
        QR.setVisibility(View.VISIBLE);
        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QR.setImageResource(R.drawable.qr1);
            }
        });
        new CountDownTimer(30000, 1000){
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                Description.setVisibility(View.INVISIBLE);
                QR.setVisibility(View.INVISIBLE);
            }
        }.start();
    }
}