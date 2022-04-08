package com.example.kursovicv21;
//Класс настроек, создан для заполнение элемента RecyclerView на активность SettingActivity
public class Setting {
    private String NameSetting;
    private int ImageSetting;

    public Setting(String nameSetting, int imageSetting) { //Конструктор класс
        NameSetting = nameSetting;
        ImageSetting = imageSetting;
    }
    //Геттеры и Сеттеры
    public String getNameSetting() { return NameSetting; }
    public int getImageSetting() { return ImageSetting; }
    public void setNameSetting(String nameSetting) { NameSetting = nameSetting; }
    public void setImageSetting(int imageSetting) { ImageSetting = imageSetting; }
}
