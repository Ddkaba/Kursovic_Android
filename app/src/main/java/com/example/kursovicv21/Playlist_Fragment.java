package com.example.kursovicv21;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Playlist_Fragment extends Fragment implements OnAudioSelectedListener{
    ArrayList<Audio> AudioList = new ArrayList<>();
    ArrayList<String> Path = new ArrayList<>();
    private final static String FILE_NAME = "content.txt";

    public Playlist_Fragment(){}
    public static Playlist_Fragment newInstance(){ return new Playlist_Fragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_fragment, container, false);
        TextView Score = view.findViewById(R.id.Score);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        try{
            Path = getArguments().getStringArrayList("AudioPath");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(!Path.isEmpty()){
            for(int i = 0;i<Path.size();i++){
                AddMusic(Path.get(i));
            }
            try {
                FileOutputStream fos = new FileOutputStream(requireContext().getFilesDir().getPath() + "/file.text");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(AudioList);
                oos.close();
                if(fos!=null)
                    fos.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            try {
                FileInputStream fis = new FileInputStream(requireContext().getFilesDir().getPath() + "/file.text");
                ObjectInputStream ois = new ObjectInputStream(fis);
                ArrayList<Audio> clubs = (ArrayList<Audio>) ois.readObject();
                ois.close();
                if(fis!=null)
                    fis.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new PlaylistAdapter(getContext(),AudioList, this));
            Score.setText("Количество песен: " + String.valueOf(AudioList.size()));
        }
        return view;
    }

    private ArrayList<Audio> AddMusic(String path){
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(path);
        String artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        Audio audio = new Audio(path, title, artist);
        AudioList.add(audio);
        return AudioList;
    }

    @Override
    public void onAudioClicked(int position) {
        Log.e("Name", ":" + AudioList.get(position).getPath());
        Bundle bundle = new Bundle();
        bundle.putInt("pos",position);
        bundle.putParcelableArrayList("Audio", AudioList);
        bundle.putString("path",AudioList.get(position).getPath());
        bundle.putString("title",AudioList.get(position).getTitle());
        bundle.putString("author",AudioList.get(position).getArtist());
        Player_Fragment fragment = new Player_Fragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fl_content, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onAudioLongClicked(int position) {
        Log.e("Name", ":" + AudioList.get(position).getTitle());
    }
}
