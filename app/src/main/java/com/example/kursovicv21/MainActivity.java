package com.example.kursovicv21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Languages_Setting, Color_Setting{
    SharedPreferences sharedPreferences;
    BottomNavigationView navigation;
    static MediaPlayer mediaPlayer;
    String Additionally_color;
    String Basic_color;
    String language;

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
        checkPermissionStorage();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, new Playlist_Fragment()).commit();
    }

    @Override
    protected void onResume() {
        String languages = Locale.getDefault().getLanguage();
        Colors(sharedPreferences);
        if (!languages.equals(language)) recreate();
        super.onResume();
    }

    private NavigationBarView.OnItemSelectedListener Navigation = new NavigationBarView.OnItemSelectedListener() { //Переключение между фрагментами приложения
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) { //Прослушиватель для обработки событий в элементах навигации
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

    private void checkPermissionStorage(){ //Проверка разрешения на работу в файловой системе
        int result = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
    }

    private void loadFragment(Fragment fragment) { //Выполняет загрузку фрагментов
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, fragment).commit();
    }

    public void language() { //Метод обновления локализации приложения
        SharedPreferences sharedPreferences = getSharedPreferences("Languages_setting", MODE_PRIVATE);
        language = sharedPreferences.getString("language", "ru");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
    }

    public void Colors(SharedPreferences sharedPreferences) { //Метод смены цвета интерфейса
        Basic_color = sharedPreferences.getString("Basic Color", "Black");
        Additionally_color = sharedPreferences.getString("Additionally Color", "Green");
        if (Additionally_color.equals("Orange")) navigation.setBackgroundColor((ContextCompat.getColor(this, R.color.orange)));
        if (Additionally_color.equals("Red")) navigation.setBackgroundColor((ContextCompat.getColor(this, R.color.red)));
        if (Additionally_color.equals("Green")) navigation.setBackgroundColor((ContextCompat.getColor(this, R.color.green)));
    }
}