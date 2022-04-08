package com.example.kursovicv21;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileSystem_Fragment extends Fragment implements OnFileSelectedListener, Color_Setting{
    ArrayList<String> Path = new ArrayList<>();
    ArrayList<Audio> AudioList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    CardView FileSystem_CardView;
    ImageView Add;
    String data;
    String path;
    File root;

    public FileSystem_Fragment() {}
    public static FileSystem_Fragment newInstance(){ return new FileSystem_Fragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filesystem_fragment, container, false);
        FileSystem_CardView = view.findViewById(R.id.FileSystem_CardView);
        sharedPreferences =  this.getActivity().getSharedPreferences("Color_setting", MODE_PRIVATE);
        Colors(sharedPreferences);
        Add = view.findViewById(R.id.check);
        if(checkPermission()) path = Environment.getExternalStorageDirectory().getPath();
        else requestPermission();
        root = new File(path);
        try {
            data = getArguments().getString("path");
            File file = new File(data);
            root = file;
        } catch (Exception e){
            e.printStackTrace();
        }
        File[] filesAndFolders = root.listFiles();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFile);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new FileAdapter(getContext(),filesAndFolders,this));

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Path.size() > 0) {
                    try {
                        FileInputStream fis = new FileInputStream(requireContext().getFilesDir().getPath() + "/Audio.text");
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        AudioList = (ArrayList<Audio>) ois.readObject();
                        ois.close();
                        fis.close();
                    } catch(Exception ex) { ex.printStackTrace(); }
                    for(int i = 0;i<Path.size();i++){ AddMusic(Path.get(i)); }
                    try {
                        FileOutputStream fos = new FileOutputStream(requireContext().getFilesDir().getPath() + "/Audio.text");
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(AudioList);
                        oos.close();
                        fos.close();
                        Toast toast = Toast.makeText(getContext(), getResources().getString(R.string.Add_Audio), Toast.LENGTH_SHORT);
                        toast.show();
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    Toast toast = Toast.makeText(getContext(), getResources().getString(R.string.Necessary_add_audio), Toast.LENGTH_SHORT);
                    toast.show(); }
            }
        });
        return view;
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED) return true;
        else return false;
    }

    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(getActivity(), "Storage permission", Toast.LENGTH_SHORT);
        } else ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
    }

    @Override
    public void onFileClicked(File file) {
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
                builder.setMessage(getResources().getString(R.string.Add_Audio_Question));
                builder.setPositiveButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
                builder.setNegativeButton(getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { Path.add(file.getAbsolutePath()); }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
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

    public void Colors(SharedPreferences sharedPreferences) { //Метод смены цвета приложения
        String Basic_color = sharedPreferences.getString("Basic Color", "Black");
        String Additionally_color = sharedPreferences.getString("Additionally Color", "Green");
        if (Additionally_color.equals("Orange")) FileSystem_CardView.setCardBackgroundColor((ContextCompat.getColor(getContext(), R.color.orange)));
        if (Additionally_color.equals("Red")) FileSystem_CardView.setCardBackgroundColor((ContextCompat.getColor(getContext(), R.color.red)));
        if (Additionally_color.equals("Green")) FileSystem_CardView.setCardBackgroundColor((ContextCompat.getColor(getContext(), R.color.green)));
    }
}
