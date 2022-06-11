package com.example.kursovicv21;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RadioAdapter extends RecyclerView.Adapter<RadioAdapter.ViewHolder> {
    OnRadioSelectedListener listener;
    ProgressBar Playing_progressBar;
    ImageView Playing_imageView;
    int PlayingPosition = -1;
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView RadioName;
        public ImageView RadioImage;
        public ProgressBar progressBar;
        public ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            RadioName = itemView.findViewById(R.id.RadioName); //Инициализация различных элементов
            RadioImage = itemView.findViewById(R.id.RadioImageView);
            progressBar = itemView.findViewById(R.id.progressBar);
            imageView = itemView.findViewById(R.id.imageViewSound);
        }
    }

    private List<Radio> mRadio;
    public RadioAdapter(List<Radio> mRadio, OnRadioSelectedListener listener) { this.mRadio = mRadio; this.listener = listener; } //Конструктор

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View RadioView = inflater.inflate(R.layout.recycler_item_radio, parent, false);
        ViewHolder viewHolder = new ViewHolder(RadioView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        Radio radio = mRadio.get(position);
        ProgressBar progressBar = holder.progressBar;
        ImageView imageView = holder.imageView;
        TextView RadioN = holder.RadioName;
        RadioN.setText(radio.getNameRadio()); //Заполнение элемента нужными данными
        ImageView RadioI = holder.RadioImage;
        RadioI.setImageResource(radio.getPicture()); //Заполнение элемента нужными данными
        if(PlayingPosition != -1) {
            if (PlayingPosition == position && Playing_imageView.equals(imageView) && Playing_progressBar.equals(progressBar)) {
                if (MainActivity.mediaPlayer.isPlaying()) imageView.setVisibility(View.VISIBLE);
                else {
                    if(!MainActivity.mediaPlayer.isPlaying() && MainActivity.mediaPlayer != null &&
                            Radio_Fragment.preparation) progressBar.setVisibility(View.VISIBLE);
                    else {
                        progressBar.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                    }
                }
            }
            else {
                progressBar.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() { //Обработка нажатия на элемент списка RecyclerView
            @Override
            public void onClick(View v) {
                listener.clickOnImage(pos, progressBar, imageView);
                PlayingPosition = pos;
                Playing_imageView = imageView;
                Playing_progressBar = progressBar;
            }
        });
    }

    @Override
    public int getItemCount() { return  mRadio.size(); } //Получение количества элементов списка
}
