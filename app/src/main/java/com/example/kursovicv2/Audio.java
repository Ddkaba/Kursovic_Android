package com.example.kursovicv2;

public class Audio {
    private String path;
    private String title;
    private String artist;

    public Audio(String path, String title, String artist) {
        this.path = path;
        this.title = title;
        this.artist = artist;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
}

