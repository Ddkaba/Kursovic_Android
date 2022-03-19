package com.example.kursovicv21;


public class Radio {
    private String URL;
    private String NameRadio;
    private int Picture;

    public Radio(String URL, String NameRadio, int picture) {
        this.URL = URL;
        this.NameRadio = NameRadio;
        Picture = picture;
    }

    public String getURL() { return URL; }
    public int getPicture() { return Picture; }
    public String getNameRadio() { return NameRadio; }
    public void setURL(String URL) { this.URL = URL; }
    public void setPicture(int picture) { Picture = picture; }
    public void setNameRadio(String nameRadio) { NameRadio = nameRadio; }
}
