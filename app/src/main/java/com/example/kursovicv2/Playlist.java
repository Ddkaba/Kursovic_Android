package com.example.kursovicv2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Playlist extends AppCompatActivity {
    ImageButton File;
    CardView PlaySecond;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.playlist);
        PlaySecond = findViewById(R.id.ToPlayCardView);
        File = findViewById(R.id.ToFile);
        File.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()){
                    Intent intent = new Intent(Playlist.this, FileSystem.class);
                    String path = Environment.getExternalStorageDirectory().getPath();
                    intent.putExtra("path",path);
                    startActivity(intent);
                }else{
                    requestPermission();
                }
            }
        });
        PlaySecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Playlist.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(Playlist.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }

    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(Playlist.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(Playlist.this, "Storage permission", Toast.LENGTH_SHORT);

        } else ActivityCompat.requestPermissions(Playlist.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
    }
}
