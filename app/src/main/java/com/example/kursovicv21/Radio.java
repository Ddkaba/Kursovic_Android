package com.example.kursovicv21;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

//Класс настроек, создан для заполнение элемента RecyclerView на активность Radio_Fragment
public class Radio implements Parcelable, Serializable {
    private String URL;
    private String NameRadio;
    private int Picture;

    public Radio(String URL, String NameRadio, int picture) { //Конструктор класс
        this.URL = URL;
        this.NameRadio = NameRadio;
        Picture = picture;
    }

    //Получение и изменение информации в объектах
    public String getURL() { return URL; }
    public int getPicture() { return Picture; }
    public String getNameRadio() { return NameRadio; }
    public void setURL(String URL) { this.URL = URL; }
    public void setPicture(int picture) { Picture = picture; }
    public void setNameRadio(String nameRadio) { NameRadio = nameRadio; }

    @Override
    public int describeContents() { return 0; } //Описывает специальные объекты интерфейса


    private Radio (Parcel in){
        setURL(in.readString());
        setPicture(in.readInt());
        setNameRadio(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { //Упаковывает объект для передачи
        dest.writeString(getURL());
        dest.writeInt(getPicture());
        dest.writeString(getNameRadio());
    }

    public static final Creator<Radio> CREATOR = new Creator<Radio>() {
        @Override
        public Radio createFromParcel(Parcel in) { return new Radio(in); }

        @Override
        public Radio[] newArray(int size) { return new Radio[size]; } //Возвращает Arraylist<Radio>
    };
}
