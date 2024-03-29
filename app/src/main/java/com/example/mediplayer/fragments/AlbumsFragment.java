package com.example.mediplayer.fragments;

import static com.example.mediplayer.MainActivity.albums;
import static com.example.mediplayer.MainActivity.musicFiles;
import static com.example.mediplayer.adapter.AlbumDetailsAdapter.albumFiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mediplayer.R;
import com.example.mediplayer.adapter.AlbumAdapter;
import com.example.mediplayer.adapter.MusicAdapter;


public class AlbumsFragment extends Fragment {

    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;
    public AlbumsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_albums, container, false);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        if(!(albums.size()<1)){
            albumAdapter=new AlbumAdapter(getContext(), albums);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        return view;
    }

}