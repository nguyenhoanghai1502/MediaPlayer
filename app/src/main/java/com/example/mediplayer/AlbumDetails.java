package com.example.mediplayer;

import static com.example.mediplayer.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mediplayer.adapter.AlbumDetailsAdapter;

import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    String albumName;
    ArrayList<MusicFiles> albumSongs=new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_album_details);
        recyclerView=findViewById(R.id.recyclerView);
        albumPhoto=findViewById(R.id.album_photo);
        albumName=getIntent().getStringExtra("albumName");
        int j=0;
        for(int i=0; i< musicFiles.size();i++){
            if(albumName.equals(musicFiles.get(i).getAlbum())){
                albumSongs.add(j, musicFiles.get(i));
                j++;
            }
        }
        byte[] image=getAlbumArt(albumSongs.get(0).getPath());
        if(image!=null){
            Glide.with(this).load(image).into(albumPhoto);
        }
        else{
            Glide.with(this).load(R.drawable.love_song).into(albumPhoto);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(albumSongs.size()<1)){
            albumDetailsAdapter=new AlbumDetailsAdapter(this, albumSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
    }


    private  byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}