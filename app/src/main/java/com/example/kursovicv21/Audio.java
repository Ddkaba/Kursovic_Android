package com.example.kursovicv21;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
//Класс настроек, создан для заполнение элемента RecyclerView на активность Playlist_Fragment, Choose_Fragment
public class Audio implements Parcelable, Serializable {
    private String path;
    private String title;
    private String artist;

    public Audio(String path, String title, String artist) { //Конструктор класс
        this.path = path;
        this.title = title;
        this.artist = artist;
    }

    //Получение и изменение информации в объектах
    public void setPath(String path) { this.path = path; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getPath() { return path; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }

    @Override
    public int describeContents() { //Описывает различного рода специальные объекты, описывающие интерфейс.
        return 0;
    } //Описывает специальные объекты интерфейса

    private Audio (Parcel in){
        setPath(in.readString());
        setTitle(in.readString());
        setArtist(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { //Упаковывает объект для передачи
        dest.writeString(getPath());
        dest.writeString(getTitle());
        dest.writeString(getArtist());
    }

    public static final Parcelable.Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel source) {
            return new Audio(source);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
        } //Возвращает Arraylist<Audio>
    };
}

