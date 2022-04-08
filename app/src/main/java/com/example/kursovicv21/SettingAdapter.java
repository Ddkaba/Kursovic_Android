package com.example.kursovicv21;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {
    OnSettingSelectedListener listener;
    ArrayList<ImageView> arrayList = new ArrayList<>();
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Setting_textView;
        public ImageView Setting_imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            Setting_textView = itemView.findViewById(R.id.Name_setting); //Инициализация различных элементов
            Setting_imageView = itemView.findViewById(R.id.ImageViewSetting);
        }
    }

    private List<Setting> mSetting;
    public SettingAdapter(List<Setting> mSetting, OnSettingSelectedListener listener) { this.mSetting = mSetting; this.listener = listener;} //Конструктор

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View SettingView = inflater.inflate(R.layout.recycler_item_setting, parent, false);
        SettingAdapter.ViewHolder viewHolder = new SettingAdapter.ViewHolder(SettingView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() { //Обработка нажатия на элемент списка RecyclerView
            @Override
            public void onClick(View v) {
                listener.OnSettingListener(pos, v, arrayList);
            }
        });
        Setting setting = mSetting.get(position);
        TextView SettingText = holder.Setting_textView;
        SettingText.setText(setting.getNameSetting()); //Заполнение элемента нужными данными
        ImageView SettingImage = holder.Setting_imageView;
        arrayList.add(SettingImage);
        SettingImage.setImageResource(setting.getImageSetting()); //Заполнение элемента нужными данными
    }

    @Override
    public int getItemCount() { return mSetting.size(); } //Получение количества элементов списка
}
