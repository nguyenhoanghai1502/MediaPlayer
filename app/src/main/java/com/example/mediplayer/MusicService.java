package com.example.mediplayer;

import static com.example.mediplayer.PalyerActivity.listSongs;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener{
    IBinder myBinder=new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles=new ArrayList<>();
    Uri uri;
    int position=-1;
    ActionPlay actionPlay;

    @Override
    public void onCreate() {
        super.onCreate();
        musicFiles=listSongs;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return myBinder;
    }



    public class MyBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition=intent.getIntExtra("servicePosition",-1);
        String actionName=intent.getStringExtra("ActionName");
        if(myPosition!=-1){
            playMedia(myPosition);
        }
        if(actionName!=null){
            switch (actionName){
                case "playPause":
                    Toast.makeText(this, "PlayPause", Toast.LENGTH_SHORT).show();
                    break;
                case "next":
                    Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
                    break;
                case "Previous":
                    Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMedia(int Startposition) {
        musicFiles=listSongs;
        position=Startposition;
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(musicFiles!=null){
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else{
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    void start(){
        mediaPlayer.start();
    }
    void pause(){
        mediaPlayer.pause();
    }
    boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    void stop(){
        mediaPlayer.stop();
    }
    void release(){
        mediaPlayer.release();
    }
    int getDuration(){
        return mediaPlayer.getDuration();
    }
    void seekTo(int position){
        mediaPlayer.seekTo(position);
    }
    void createMediaPlayer(int position){
        uri=Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer=MediaPlayer.create(getBaseContext(), uri);
    }
    int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }
    void OnCompleted(){
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(actionPlay!=null){
            actionPlay.nextBtnClicked();
        }
        createMediaPlayer(position);
        mediaPlayer.start();
        OnCompleted();

    }
}
