package com.example.kursovicv21;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder>{
    Context context;
    File[] filesAndFolders;
    OnFileSelectedListener listener;
    public FileAdapter(Context context, File[] filesAndFolders, OnFileSelectedListener listener){
        this.context = context;
        this.filesAndFolders = filesAndFolders;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.Name_file);
            imageView = itemView.findViewById(R.id.imageButton_folder);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File selectedFile = filesAndFolders[position];
        holder.textView.setText(selectedFile.getName());
        if(selectedFile.isDirectory()){
            holder.imageView.setImageResource(R.drawable.folder);
        } else {
            holder.imageView.setImageResource(R.drawable.file);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                holder.itemView.setBackgroundColor(Color.parseColor("#b0b0b0"));
                holder.imageView.setBackgroundColor(Color.parseColor("#b0b0b0"));
                holder.textView.setTextColor(R.color.black);
                listener.onFileClicked(selectedFile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesAndFolders.length;
    }
}
