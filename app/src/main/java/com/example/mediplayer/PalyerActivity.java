package com.example.mediplayer;

import static com.example.mediplayer.ApplicationClass.ACTION_NEXT;
import static com.example.mediplayer.ApplicationClass.ACTION_PLAY;
import static com.example.mediplayer.ApplicationClass.ACTION_PREVIOUS;
import static com.example.mediplayer.ApplicationClass.CHANNEL_ID_2;
import static com.example.mediplayer.MainActivity.musicFiles;
import static com.example.mediplayer.MainActivity.repeatBoolean;
import static com.example.mediplayer.MainActivity.shuffleBoolean;
import static com.example.mediplayer.adapter.AlbumDetailsAdapter.albumFiles;
import static com.example.mediplayer.adapter.MusicAdapter.mFiles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.palette.graphics.Palette;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PalyerActivity extends AppCompatActivity
        implements ActionPlay, ServiceConnection {
    TextView song_name, artist_name, duration_played, duration_total;
    ImageView cover_art, nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    int position=-1;
    public static ArrayList<MusicFiles> listSongs=new ArrayList<>();
    static Uri uri;
    private Handler handler=new Handler();
    private Thread playThread, prevThread, nextThread;
    MusicService musicService;
    MediaSessionCompat mediaSessionCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        mediaSessionCompat=new MediaSessionCompat(getBaseContext(), "My Audio");
        setContentView(R.layout.activity_palyer);
        initView();
        getIntentMethod();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(musicService!=null&& b){
                    musicService.seekTo(i*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PalyerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(musicService!=null){
                    int mCurrentPosition=musicService.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        shuffleBtn.setOnClickListener(view -> {
            if(shuffleBoolean){
                shuffleBoolean=false;
                shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_24);

            }
            else {
                shuffleBoolean=true;
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);

            }
        });
        repeatBtn.setOnClickListener(view -> {
            if(repeatBoolean){
                repeatBoolean=false;
                repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_24);
            }
            else{
                repeatBoolean=true;
                repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_on);
            }
        });
    }
    @Override
    protected void onResume() {
        Intent intent=new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void prevThreadBtn() {
        prevThread=new Thread(){
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked() {
        if(musicService.isPlaying()){
            musicService.stop();
            musicService.release();
            if(shuffleBoolean&&!repeatBoolean){
                position=getRandom(listSongs.size()-1);
            }else if(!shuffleBoolean&&!repeatBoolean){
                position=((position-1)<0?listSongs.size()-1:position-1);
            }
            uri=Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PalyerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            showNotification(R.drawable.ic_baseline_pause_24);

            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            musicService.start();
        }
        else{
            musicService.stop();
            musicService.release();
            if(shuffleBoolean&&!repeatBoolean){
                position=getRandom(listSongs.size()-1);
            }else if(!shuffleBoolean&&!repeatBoolean){
                position=((position-1)<0?listSongs.size()-1:position-1);
            }
            uri=Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PalyerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            showNotification(R.drawable.ic_baseline_play_arrow_24);

            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        }
//        musicService.OnCompleted();

    }

    private void nextThreadBtn() {
        nextThread=new Thread(){
            @Override
            public void run() {
                super.run();
               nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    public void nextBtnClicked() {
        if(musicService.isPlaying()){
            musicService.stop();
            musicService.release();
            if(shuffleBoolean&&!repeatBoolean){
                position=getRandom(listSongs.size()-1);
            }else if(!shuffleBoolean&&!repeatBoolean){
                position=((position+1)%listSongs.size());
            }
            uri=Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PalyerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            showNotification(R.drawable.ic_baseline_pause_24);
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            musicService.start();
        }
        else{
            musicService.stop();
            musicService.release();
            if(shuffleBoolean&&!repeatBoolean){
                position=getRandom(listSongs.size()-1);
            }else if(!shuffleBoolean&&!repeatBoolean){
                position=((position+1)%listSongs.size());
            }
            uri=Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PalyerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            showNotification(R.drawable.ic_baseline_play_arrow_24);

            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        }
//        musicService.OnCompleted();

    }

    private int getRandom(int i) {
        Random random=new Random();
        return random.nextInt(i+1);
    }

    private void playThreadBtn() {
        playThread=new Thread(){
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    public void playPauseBtnClicked() {
        if(musicService.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            showNotification(R.drawable.ic_baseline_play_arrow_24);

            musicService.pause();
            seekBar.setMax(musicService.getDuration()/1000);
            PalyerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });

        }
        else{
            showNotification(R.drawable.ic_baseline_pause_24);
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            musicService.start();
            seekBar.setMax(musicService.getDuration()/1000);
            PalyerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });

        }
    }

    private String formattedTime(int mCurrentPosition) {
        String totalout="";
        String totalNew="";
        String seconds=String.valueOf(mCurrentPosition%60);
        String minute=String.valueOf(mCurrentPosition/60);
        totalout=minute+":"+seconds;
        totalNew=minute+":"+"0"+seconds;
        if(seconds.length()==1){
            return totalNew;
        }else{
            return totalout;
        }
    }

    private void getIntentMethod() {
        position=getIntent().getIntExtra("position", -1);
        String sender=getIntent().getStringExtra("sender");
        if(sender!=null&&sender.equals("albumDetails")){
            listSongs=albumFiles;
        }
        else{
            listSongs=mFiles;
        }
        if(listSongs!=null){
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            uri=Uri.parse(listSongs.get(position).getPath());
        }
        if(musicService!=null){
            musicService.stop();
            musicService.release();
        }
        showNotification(R.drawable.ic_baseline_pause_24);
        Intent intent=new Intent(this, MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);

    }

    private void initView() {
        song_name=findViewById(R.id.song_name);
        artist_name=findViewById(R.id.song_artist);
        duration_played=findViewById(R.id.duration_Played);
        duration_total=findViewById(R.id.duration_Total);
        cover_art=findViewById(R.id.cover_art);
        nextBtn=findViewById(R.id.id_next);
        prevBtn=findViewById(R.id.id_prev);
        shuffleBtn=findViewById(R.id.id_shuffle);
        repeatBtn=findViewById(R.id.id_repeat);
        playPauseBtn=findViewById(R.id.id_play_pause);
        seekBar=findViewById(R.id.seek_bar);
    }

    private void metaData(Uri uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal=Integer.parseInt(listSongs.get(position).getDuration())/1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art=retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if(art!=null){
            bitmap= BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(this, cover_art, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch=palette.getDominantSwatch();
                    if(swatch!=null){
                        ImageView gradient=findViewById(R.id.imageViewTransparent);
                        RelativeLayout mContainer=findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.transparent_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),0x00000000 });
                        gradient.setBackgroundResource(R.drawable.transparent_bg);
                        GradientDrawable gradientDrawableBg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),swatch.getRgb() });
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());
                    }
                    else{
                        ImageView gradient=findViewById(R.id.imageViewTransparent);
                        RelativeLayout mContainer=findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.transparent_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0x00000000 });
                        gradient.setBackgroundResource(R.drawable.transparent_bg);
                        GradientDrawable gradientDrawableBg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }
            });
        }
        else{
            Glide.with(this).asBitmap().load(R.drawable.love_song).into(cover_art);
            ImageView gradient=findViewById(R.id.imageViewTransparent);
            RelativeLayout mContainer=findViewById(R.id.mContainer);
            gradient.setBackgroundResource(R.drawable.transparent_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);
        }
    }

    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap){
        Animation animOut= AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn= AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.MyBinder myBinder=(MusicService.MyBinder) iBinder;
        musicService=myBinder.getService();
        Toast.makeText(this, "Connected!"+musicService, Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration()/1000);
        metaData(uri);
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        musicService.OnCompleted();
    }
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService=null;
    }

    void showNotification(int playPauseBtn){
        Intent intent=new Intent(this, PalyerActivity.class);
        PendingIntent contentIntent= PendingIntent
                .getActivity(this, 0, intent,0);
        Intent previntent=new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending= PendingIntent
                .getBroadcast(this, 0,
                previntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent pauseintent=new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending= PendingIntent
                .getBroadcast(this, 0,
                pauseintent,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextintent=new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending= PendingIntent
                .getBroadcast(this, 0,
                nextintent,PendingIntent.FLAG_UPDATE_CURRENT);
        byte[] picture=null;
        picture=getAlbumArt(listSongs.get(position).getPath());
        Bitmap thumb=null;
        if(picture!=null){
            thumb=BitmapFactory.decodeByteArray(picture,0, picture.length);
        }
        else{
            thumb=BitmapFactory.decodeResource(getResources(), R.drawable.love_song);
        }
        Notification notification=new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(listSongs.get(position).getTitle())
                .setContentText(listSongs.get(position).getArtist())
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", prevPending)
                .addAction(playPauseBtn,"Pause", pausePending)
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManagerCompat notificationManager=NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);
    }
    private  byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}