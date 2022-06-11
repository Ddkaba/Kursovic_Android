package com.example.kursovicv21;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder>{
    Context context;
    List<File> filesAndFolders;
    OnFileSelectedListener listener;
    public FileAdapter(Context context, List<File> filesAndFolders, OnFileSelectedListener listener){
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
        File selectedFile = filesAndFolders.get(position);
        holder.textView.setSelected(true);
        holder.textView.setText(selectedFile.getName());
        if(selectedFile.isDirectory()) holder.imageView.setImageResource(R.drawable.folder);
        else holder.imageView.setImageResource(R.drawable.file);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFileClicked(selectedFile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesAndFolders.size();
    }
}
