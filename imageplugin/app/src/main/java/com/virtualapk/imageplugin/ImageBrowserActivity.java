package com.virtualapk.imageplugin;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class ImageBrowserActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browser);

        ImageView homeIcon1 = (ImageView) findViewById(R.id.iv_home_icon1);
        ImageView homeIcon2 = (ImageView) findViewById(R.id.iv_home_icon2);
        ImageView homeIcon3 = (ImageView) findViewById(R.id.iv_home_icon3);
        String IMG_URL = getIntent().getStringExtra("IMG_URL");
        Picasso.with(this).load(IMG_URL).into(homeIcon1);
    }
}
