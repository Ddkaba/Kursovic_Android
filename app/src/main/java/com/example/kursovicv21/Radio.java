package com.example.kursovicv21;
//Класс настроек, создан для заполнение элемента RecyclerView на активность Radio_Fragment
public class Radio {
    private String URL;
    private String NameRadio;
    private int Picture;

    public Radio(String URL, String NameRadio, int picture) { //Конструктор класс
        this.URL = URL;
        this.NameRadio = NameRadio;
        Picture = picture;
    }
    //Геттеры и Сеттеры
    public String getURL() { return URL; }
    public int getPicture() { return Picture; }
    public String getNameRadio() { return NameRadio; }
    public void setURL(String URL) { this.URL = URL; }
    public void setPicture(int picture) { Picture = picture; }
    public void setNameRadio(String nameRadio) { NameRadio = nameRadio; }
}
