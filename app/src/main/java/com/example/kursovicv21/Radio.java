package com.example.kursovicv21;

public class Radio {
    private String URL;
    private int Picture;
    private int Hz;

    public Radio(String URL, int picture, int hz) {
        this.URL = URL;
        Picture = picture;
        Hz = hz;
    }

    public String getURL() { return URL; }
    public int getPicture() { return Picture; }
    public int getHz() { return Hz; }
    public void setURL(String URL) { this.URL = URL; }
    public void setPicture(int picture) { Picture = picture; }
    public void setHz(int hz) { Hz = hz; }
}
