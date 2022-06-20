package com.example.mediplayer.adapter;
import android.content.ContentUris;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mediplayer.MusicFiles;
import com.example.mediplayer.PalyerActivity;
import com.example.mediplayer.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyVieHolder> {
    private Context mContext;
    public static ArrayList<MusicFiles> mFiles;
    public MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles){
        this.mFiles=mFiles;
        this.mContext=mContext;
    }
    public class MyVieHolder extends RecyclerView.ViewHolder
    {
        TextView file_name;
        ImageView album_art, menuMore;
        public MyVieHolder(@NonNull View itemView){
            super(itemView);
            file_name=itemView.findViewById(R.id.music_file_name);
            album_art=itemView.findViewById(R.id.music_img);
            menuMore=itemView.findViewById(R.id.menuMore);
    }
    }

    @NonNull
    @Override
    public MyVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyVieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVieHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image=getAlbumArt(mFiles.get(position).getPath());
        if(image!=null){
            Glide.with(mContext).asBitmap().load(image).into(holder.album_art);
        }else{
            Glide.with(mContext).load(R.drawable.love_song).into(holder.album_art);
        }
        //song click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, PalyerActivity.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        // menu more in item songs
        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(mContext, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.delete:
//                            Toast.makeText(mContext, "Delete Clicked!", Toast.LENGTH_SHORT).show();
                                deleteSong(position, view);
                                break;
                        }

                        return true;
                    }
                });
            }
        });
    }

    private void deleteSong(int position, View view) {
        Uri contentUri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).getId()));

        File file=new File(mFiles.get(position).getPath());
        boolean deleted=file.delete();

        if(deleted) {
            mContext.getContentResolver().delete(contentUri, null, null );
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mFiles.size());
            Snackbar.make(view, "Song deleted!: ", Snackbar.LENGTH_LONG).show();
        }
        else{
            Snackbar.make(view, "Song can't be deleted!: ", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    private  byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
    public void updateList(ArrayList<MusicFiles> musicFilesArrayList){
        mFiles=new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
