package com.example.kursovicv21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SharedMemory;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.nio.file.FileSystem;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Languages_Setting, Color_Setting{
    String language;
    String Basic_color;
    String Additionally_color;
    BottomNavigationView navigation;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language();
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(Navigation);
        sharedPreferences = getSharedPreferences("Color_setting", MODE_PRIVATE);
        Colors(sharedPreferences);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, new Playlist_Fragment()).commit();
    }

    @Override
    protected void onResume() {
        String languages = Locale.getDefault().getLanguage();
        Colors(sharedPreferences);
        if (!languages.equals(language)) recreate();
        super.onResume();
    }


    private NavigationBarView.OnItemSelectedListener Navigation = new NavigationBarView.OnItemSelectedListener() { //Метод управления меню
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_filesystem:
                    loadFragment(FileSystem_Fragment.newInstance());
                    return true;
                case R.id.navigation_playlist:
                    loadFragment(Playlist_Fragment.newInstance());
                    return true;
                case R.id.navigation_chosen:
                    loadFragment(Choose_Fragment.newInstance());
                    return true;
                case R.id.navigation_radio:
                    loadFragment(Radio_Fragment.newInstance());
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) { //Метод загрузки фрагментов
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_content, fragment);
        ft.commit();
    }

    public void language() { //Локализация приложения
        SharedPreferences sharedPreferences = getSharedPreferences("Languages_setting", MODE_PRIVATE);
        language = sharedPreferences.getString("language", "ru");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
    }

    public void Colors(SharedPreferences sharedPreferences) { //Метод смены цвета приложения
        Basic_color = sharedPreferences.getString("Basic Color", "Black");
        Additionally_color = sharedPreferences.getString("Additionally Color", "Green");
        if (Additionally_color.equals("Orange")) navigation.setBackgroundColor((ContextCompat.getColor(this, R.color.orange)));
        if (Additionally_color.equals("Red")) navigation.setBackgroundColor((ContextCompat.getColor(this, R.color.red)));
        if (Additionally_color.equals("Green")) navigation.setBackgroundColor((ContextCompat.getColor(this, R.color.green)));
    }
}