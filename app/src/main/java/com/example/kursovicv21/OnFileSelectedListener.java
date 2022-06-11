package com.example.kursovicv21;

import java.io.File;
//Интерфейс для обработки нажатия на элемент списка файловой системы
public interface OnFileSelectedListener {
    void onFileClicked(File file);
}
