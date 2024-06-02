package com.mazksr.youtunify;

import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.Toast;

import java.util.concurrent.TimeoutException;

public class Player {
    private static MediaPlayer player;
    private static boolean isPreparing;
    public static String songId = "";
    private static final Handler handler = new Handler();
    public static int libraryPos = -1;


    public static MediaPlayer getPlayer() {
        if (player == null) {
            player = new MediaPlayer();
        }
        return player;
    }

    public static void stop() {
        player.stop();
        player.reset();
        songId = "";
    }

    public static void play(String source, MediaPlayer.OnPreparedListener onPreparedListener, MediaPlayer.OnCompletionListener onCompletionListener){
        if (player.isPlaying() || isPreparing) {
            stop();
        }
        try {
            player.setDataSource(source);
            player.setOnPreparedListener(onPreparedListener);
            System.out.println("ff" + source);
            isPreparing = true;
            player.prepareAsync();
            player.setOnCompletionListener(onCompletionListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
