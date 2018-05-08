package com.virtualapk.host;

import java.io.File;

import com.didi.virtualapk.PluginManager;
import com.squareup.picasso.Picasso;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    public static final String PLUGIN_PKG_NAME = "com.virtualapk.imageplugin";
    public static final String IMG1_URL = "http://pic.wenwen.soso.com/p/20130809/20130809170922-693425734.jpg";
    public static final String IMG2_URL = "http://img2.imgtn.bdimg.com/it/u=2881489320,987765159&fm=214&gp=0.jpg";
    public static final String IMG3_URL = "http://img.17k.com/images/bookcover/2012/608/3/121692.jpg";
    private String mUrl;
    private static final int PERMISSION_REQUEST_CODE_STORAGE = 20180507;
    private SeekBar seekBar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_main);

        ImageView homeIconIv1 = (ImageView) findViewById(R.id.iv_home_icon1);
        ImageView homeIconIv2 = (ImageView) findViewById(R.id.iv_home_icon2);
        Button imageBrowserBtn = (Button) findViewById(R.id.btn_image_browser);
        Button imageStartServiceBtn = (Button) findViewById(R.id.btn_start_service);
        seekBar1 = (SeekBar) this.findViewById(R.id.music_seekBar);
        Picasso.with(this).load(IMG1_URL).into(homeIconIv1);
        Picasso.with(this).load(IMG2_URL).into(homeIconIv2);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar1.setProgress(seekBar.getProgress());
                Intent intent = new Intent("cn.com.seekBar");
                intent.putExtra("seekBarPosition", seekBar.getProgress());
                //System.out.println("==========="+seekBar.getProgress());
                sendBroadcast(intent);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
            }
        });
//        loadPlugin(this);
        imageBrowserBtn.setOnClickListener(this);
        imageStartServiceBtn.setOnClickListener(this);
        if (hasPermission()) {
            loadPlugin();
        } else {
            requestPermission();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PERMISSION_REQUEST_CODE_STORAGE == requestCode) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            } else {
                if (loadPlugin()) return;
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean loadPlugin() {
        ContentResolver cr = getContentResolver();
        if (cr != null) {
            // 获取所有歌曲
            Cursor cursor = cr.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (null == cursor) {
                return true;
            }
            if (cursor.moveToFirst()) {
                do {
                    String url = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
                    String name = cursor
                            .getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String sbr = name.substring(name.length() - 3,
                            name.length());
                    //Log.e("--------------", sbr);
                    if (sbr.equals("mp3")) {
                        mUrl = url;
                        break;
                    }
                } while (cursor.moveToNext());
            }
        }
       loadPlugin(this);
        return false;
    }

    private boolean hasPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_STORAGE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_image_browser) {
            if (PluginManager.getInstance(this).getLoadedPlugin(PLUGIN_PKG_NAME) == null) {
                Toast.makeText(getApplicationContext(),
                        "插件未加载,请尝试重启APP", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            intent.setClassName(PLUGIN_PKG_NAME, "com.virtualapk.imageplugin.ImageBrowserActivity");
            intent.putExtra("IMG_URL", IMG3_URL);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_start_service) {
            startService();
        }

    }

    private void startService() {
        Intent intent = new Intent("com.yan.test");
        intent.setPackage(PLUGIN_PKG_NAME);
        intent.putExtra("id", mUrl);
        startService(intent);
    }

    private void loadPlugin(Context base) {
        PluginManager pluginManager = PluginManager.getInstance(base);
        AssetsManager.copyAllAssetsApk(this);
        File apk = new File(getDir(AssetsManager.APK_DIR, Context.MODE_PRIVATE), "Demo.apk");
//        File apk = new File(getExternalStorageDirectory(), "plugin.apk");
        if (apk.exists()) {
            try {
                pluginManager.loadPlugin(apk);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "SDcard根目录未检测到plugin.apk插件", Toast.LENGTH_SHORT).show();
        }
    }
}
