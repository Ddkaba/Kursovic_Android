package com.example.kursovicv21;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    Context context;
    ArrayList<Audio> mAudio;
    OnAudioSelectedListener listener;
    public PlaylistAdapter(Context context, ArrayList<Audio> mAudio, OnAudioSelectedListener listener){
        this.context = context;
        this.mAudio = mAudio;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView PositionAuthorName;

        public ViewHolder(View itemView){
            super(itemView);
            PositionAuthorName = itemView.findViewById(R.id.AuthorName);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_song, parent, false);
        return new PlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistAdapter.ViewHolder holder, int position) {
        int pos = position;
        Audio audio = mAudio.get(position);
        holder.PositionAuthorName.setText(String.valueOf(position+1)+ "." + audio.getArtist() + " - " + audio.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAudioClicked(pos);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { listener.onAudioLongClicked(pos);return false; }
        });
    }

    @Override
    public int getItemCount() {
        return mAudio.size();
    }
}
