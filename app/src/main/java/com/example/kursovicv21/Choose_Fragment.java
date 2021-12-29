package com.example.kursovicv21;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Choose_Fragment extends Fragment implements OnAudioSelectedListener{
    ArrayList<Audio> Favorite = new ArrayList<>();

    public Choose_Fragment(){}
    public static Choose_Fragment newInstance(){ return new Choose_Fragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favourite_fragment, container, false);
        TextView Score = view.findViewById(R.id.Favorite_Score);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_favorite);
        try {
            FileInputStream fis = new FileInputStream(requireContext().getFilesDir().getPath() + "/Favorite.text");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Favorite = (ArrayList<Audio>) ois.readObject();
            ois.close();
            if(fis!=null)
                fis.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            Log.e("Error"," ");
        }
        if(!Favorite.isEmpty())
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new ChooseAdapter(getContext(),Favorite, this));
            Score.setText("Количество песен: " + String.valueOf(Favorite.size()));
        }
        return view;
    }

    @Override
    public void onAudioClicked(int position) {
        Log.e("Name", ":" + Favorite.get(position).getPath());
        Bundle bundle = new Bundle();
        bundle.putInt("pos",position);
        bundle.putParcelableArrayList("Audio", Favorite);
        bundle.putString("title",Favorite.get(position).getTitle());
        bundle.putString("author",Favorite.get(position).getArtist());
        bundle.putBoolean("fav",true);
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
                Favorite.remove(position);
                try {
                    FileOutputStream fos = new FileOutputStream(requireContext().getFilesDir().getPath() + "/Favorite.text");
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(Favorite);
                    oos.close();
                    fos.close();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
                Choose_Fragment fragment = new Choose_Fragment();
                getFragmentManager().beginTransaction().replace(R.id.fl_content, fragment).addToBackStack(null).commit();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
