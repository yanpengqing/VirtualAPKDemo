package com.virtualapk.imageplugin;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Joseph on 2018/5/4.
 *         <p/>
 */

public class PluginService extends Service {


    public static MediaPlayer player = null;


    @Override
    public void onCreate() {

        SeekBarBroadcastReceiver receiver = new SeekBarBroadcastReceiver();
        IntentFilter filter = new IntentFilter("cn.com.seekBar");
        this.registerReceiver(receiver, filter);
        super.onCreate();
    }
    private class SeekBarBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int seekBarPosition = intent.getIntExtra("seekBarPosition", 0);
            // System.out.println("--------"+seekBarPosition);
            player.seekTo(seekBarPosition * player.getDuration() / 100);
            player.start();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d("PluginService","服务启动了");
        String id = intent.getStringExtra("id");
        Log.d("PluginService",id);
        Uri myUri = null;
        try {
            myUri = Uri.parse(id);
        } catch (Exception e) {
            Toast.makeText(this,"没有找到music",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(getApplicationContext(), myUri);
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.start();
    }
}
