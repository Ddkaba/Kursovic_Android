package com.example.kursovicv21;

import android.widget.ImageView;
import android.widget.ProgressBar;
//Интерфейс для обработки нажатия на элемент списка радио RecyclerView
public interface OnRadioSelectedListener {
    void clickOnImage(int position, ProgressBar myProgressBar, ImageView myImage);
}
