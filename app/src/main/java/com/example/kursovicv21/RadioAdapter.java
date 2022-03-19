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
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView RadioName;
        public ImageView RadioImage;
        public ProgressBar progressBar;
        public ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            RadioName = itemView.findViewById(R.id.RadioName);
            RadioImage = itemView.findViewById(R.id.RadioImageView);
            progressBar = itemView.findViewById(R.id.progressBar);
            imageView = itemView.findViewById(R.id.imageViewSound);
        }
    }

    private List<Radio> mRadio;
    public RadioAdapter(List<Radio> mRadio, OnRadioSelectedListener listener) { this.mRadio = mRadio; this.listener = listener; }

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.clickOnImage(pos, progressBar, imageView);
            }
        });
        TextView RadioN = holder.RadioName;
        RadioN.setText(radio.getNameRadio());
        ImageView RadioI = holder.RadioImage;
        RadioI.setImageResource(radio.getPicture());
    }

    @Override
    public int getItemCount() { return  mRadio.size(); }
}
