package com.example.kursovicv21;

import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
//Интерфейс для обработки нажатия на элемент списка настроек
public interface OnSettingSelectedListener {
    void OnSettingListener(int position, View v, ArrayList<ImageView> arrayList);
}
