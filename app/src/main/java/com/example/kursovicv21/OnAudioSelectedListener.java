package com.example.kursovicv21;

import java.io.File;
import java.util.ArrayList;
//Интерфейс для обработки нажатия на элемент плейлиста
public interface OnAudioSelectedListener {
    void onAudioClicked(int position);
    void onAudioLongClicked(int position);
}
