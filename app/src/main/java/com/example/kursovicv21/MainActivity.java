package com.example.kursovicv21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.nio.file.FileSystem;

public class MainActivity extends AppCompatActivity {

    private NavigationBarView.OnItemSelectedListener Navigation
            = new NavigationBarView.OnItemSelectedListener() {
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
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_content, fragment);
        ft.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(Navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, new Playlist_Fragment()).commit();
    }
}