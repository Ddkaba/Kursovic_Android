package com.example.kursovicv21;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class Choose_Fragment extends Fragment implements OnAudioSelectedListener{
    ArrayList<Audio> Favorite = new ArrayList<>();

    public Choose_Fragment(){}
    public static Choose_Fragment newInstance(){ return new Choose_Fragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favourite_fragment, container, false);
        TextView Score = view.findViewById(R.id.Favorite_Score);
        ImageView setting = view.findViewById(R.id.favourite_setting);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_favorite);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Метод для перехода в меню настроек
                Intent intent = new Intent(getActivity().getBaseContext(), SettingActivity.class);
                startActivity(intent);

            }
        });
        try {
            FileInputStream fis = new FileInputStream(requireContext().getFilesDir().getPath() + "/Favorite.text");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Favorite = (ArrayList<Audio>) ois.readObject();
            ois.close();
            if(fis!=null)
                fis.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        if(!Favorite.isEmpty())
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new ChooseAdapter(getContext(),Favorite, this));
            Score.setText(getResources().getString(R.string.Score_audio) +" "+ String.valueOf(Favorite.size()));
        }
        return view;
    }

    @Override
    public void onAudioClicked(int position) { //Воспроизведение избранной аудиозаписи
        Intent intent = new Intent(getContext(), Player_Activity.class);
        intent.putExtra("pos",position);
        intent.putExtra("title",Favorite.get(position).getTitle());
        intent.putExtra("author",Favorite.get(position).getArtist());
        intent.putExtra("Audio", Favorite);
        intent.putExtra("fav",true);
        startActivity(intent);
    }

    @Override
    public void onAudioLongClicked(int position) { //Метод для подтверждения удаления избранной аудиозаписи
        AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.Confirmation));
        builder.setMessage(getResources().getString(R.string.Delete_Audio));
        builder.setPositiveButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { } //Обработка нажатия на кнопку Нет
        });
        builder.setNegativeButton(getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //Обработка нажатия на кнопку Да
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
