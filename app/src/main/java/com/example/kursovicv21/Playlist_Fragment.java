package com.example.kursovicv21;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Playlist_Fragment extends Fragment implements OnAudioSelectedListener{
    ArrayList<Audio> AudioList = new ArrayList<>();

    public Playlist_Fragment(){}
    public static Playlist_Fragment newInstance(){ return new Playlist_Fragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_fragment, container, false);
        TextView Score = view.findViewById(R.id.Score);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        try {
            FileInputStream fis = new FileInputStream(requireContext().getFilesDir().getPath() + "/Audio.text");
            ObjectInputStream ois = new ObjectInputStream(fis);
            AudioList = (ArrayList<Audio>) ois.readObject();
            ois.close();
        } catch(Exception ex) { ex.printStackTrace(); }
        if(!AudioList.isEmpty())
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new PlaylistAdapter(getContext(),AudioList, this));
            Score.setText("Количество песен: " + String.valueOf(AudioList.size()));
        }
        return view;
    }

    @Override
    public void onAudioClicked(int position) {
        Log.e("Name", ":" + AudioList.get(position).getPath());
        Bundle bundle = new Bundle();
        bundle.putInt("pos",position);
        bundle.putParcelableArrayList("Audio", AudioList);
        bundle.putString("title",AudioList.get(position).getTitle());
        bundle.putString("author",AudioList.get(position).getArtist());
        bundle.putBoolean("fav",false);
        Player_Fragment fragment = new Player_Fragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fl_content, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onAudioLongClicked(int position) {
        AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Подтверждение");
        builder.setMessage("Вы хотите удалить эту песню?");
        builder.setPositiveButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        builder.setNegativeButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AudioList.remove(position);
                try {
                    FileOutputStream fos = new FileOutputStream(requireContext().getFilesDir().getPath() + "/Audio.text");
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(AudioList);
                    oos.close();
                    fos.close();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
                Playlist_Fragment fragment = new Playlist_Fragment();
                getFragmentManager().beginTransaction().replace(R.id.fl_content, fragment).addToBackStack(null).commit();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
