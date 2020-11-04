package com.tang.xu.formwork.utils;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;

import java.io.IOException;

public class MediaPlayerUtils {
    public static MediaPlayer mediaPlayer;
    public static int play = 0;
    public static int pause = 1;
    public static int stop = 2;
    public static int buff = stop;
    private OnMusicProgressListener  onMusicProgress;

    private Handler handler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    if (onMusicProgress!=null){
                        int progress = nowProgress();
                        int p = (int) (((float)progress)/((float)mediaPlayer.getDuration())*100);
                        onMusicProgress.OnProgress(progress,p);
                        handler.sendEmptyMessageDelayed(1000,1000);
                    }
                    break;
            }

        }
    };


    public MediaPlayerUtils() {
        mediaPlayer = new MediaPlayer();
    }

    /**
     * 开始操作
     */
    public void startPlay(AssetFileDescriptor assetFileDescriptor){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(assetFileDescriptor);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    buff=play;
                    handler.sendEmptyMessage(1000);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }


    /**
     * 开始操作
     */
    public void startPlay(String path){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    buff=play;
                    handler.sendEmptyMessage(1000);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停操作
     */
    public void pausePlay(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            buff=pause;
            handler.removeMessages(1000);
        }
    }

    /**
     * 停止操作
     */
    public void stopPlay(){
        mediaPlayer.stop();
        buff=stop;
        handler.removeMessages(1000);
    }

    /**
     * 停止操作
     */
    public void stoCallpPlay(){
        mediaPlayer.stop();
    }

    /**
     * 重新播放操作
     */
    public void continuPlay(){
        mediaPlayer.start();
        buff=play;
        handler.sendEmptyMessage(1000);
    }

    /**
     * 跳转到当前进度操作
     */
    public void setProgress(int progress){
        mediaPlayer.seekTo(progress);
    }

    /**
     * 返回当前进度条
     * @return
     */
    public int nowProgress(){
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 返回总时长
     * @return
     */
    public int numProgress(){
        return mediaPlayer.getDuration();
    }

    /**
     * 设置是否循环
     * @return
     */
    public void isLopper(boolean islopper){
        mediaPlayer.setLooping(islopper);
    }

    /**
     * 播放结束
     * @param listene
     */
    public void setOnComplListene(MediaPlayer.OnCompletionListener listene){
        mediaPlayer.setOnCompletionListener(listene);
    }

    /**
     * 播放错误
     * @param listene
     */
    public void setOnErrorListener(MediaPlayer.OnErrorListener listene){
        mediaPlayer.setOnErrorListener(listene);
    }

    public void setProgressListene(OnMusicProgressListener  onmusicProgress){
        onMusicProgress = onmusicProgress;
    }

    public interface OnMusicProgressListener {
        void OnProgress(int progress,int p);
    }
}
