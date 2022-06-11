package com.example.kursovicv21;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class FileSystem_Fragment extends Fragment implements OnFileSelectedListener{
    ArrayList<Audio> AudioList = new ArrayList<>();
    ArrayList<File> Files = new ArrayList<>();
    String data;
    String path;
    File root;

    public FileSystem_Fragment() {}
    public static FileSystem_Fragment newInstance(){ return new FileSystem_Fragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filesystem_fragment, container, false);
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            path = Environment.getExternalStorageDirectory().getPath();
            root = new File(path);
            try {
                data = getArguments().getString("path");
                File file = new File(data);
                root = file;
            } catch (Exception e) { e.printStackTrace(); }
            File[] filesAndFolders = root.listFiles();
            Arrays.sort(filesAndFolders);
            Files.clear();
            for(int i =0; i < filesAndFolders.length; i++){
                File file = filesAndFolders[i];
                if(!file.isHidden()) Files.add(file);
            }
            RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFile);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new FileAdapter(getContext(), Files, this));
        }
        else Toast.makeText(getContext(), R.string.Permission, Toast.LENGTH_LONG).show();
        return view;
    }

    @Override
    public void onFileClicked(File file) { //Переход в выбранную папку при нажатии на папку или подтверждение добавления записи при нажатии на файл MP3
        if(file.isDirectory()){
            Bundle bundle = new Bundle();
            bundle.putString("path",file.getAbsolutePath());
            FileSystem_Fragment fragment = new FileSystem_Fragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fl_content, fragment).addToBackStack(null).commit();
        }
        else{
            if(file.getName().toLowerCase().endsWith(".mp3")) {
                AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(R.string.Confirmation));
                builder.setMessage(getResources().getString(R.string.Add_Audio_Question1) + " " + file.getName() + " " + getResources().getString(R.string.Add_Audio_Question2));
                builder.setPositiveButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
                builder.setNegativeButton(getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { AddNewAudio(file.getAbsolutePath(), file.getName()); }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    public void AddNewAudio(String path, String Name){  //Добавление новых аудиофайлов в плейлист
        try {
            FileInputStream fis = new FileInputStream(requireContext().getFilesDir().getPath() + "/Audio.text");
            ObjectInputStream ois = new ObjectInputStream(fis);
            AudioList = (ArrayList<Audio>) ois.readObject();
            ois.close();
            fis.close();
        } catch(Exception ex) { ex.printStackTrace(); }
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(path);
        String artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        Audio audio;
        if(title == null && artist == null ) audio = new Audio(path,  Name.replace(".mp3", ""), "");
        else audio = new Audio(path, title, artist);
        AudioList.add(audio);
        try {
            FileOutputStream fos = new FileOutputStream(requireContext().getFilesDir().getPath() + "/Audio.text");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(AudioList);
            oos.close();
            fos.close();
            Toast.makeText(getContext(), getResources().getString(R.string.Add_Audio), Toast.LENGTH_SHORT).show();
        } catch(Exception ex) { ex.printStackTrace(); }
    }
}
