package com.sdi.castivate.utils;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.sdi.castivate.R;

/**
 * Created by nijamudhin on 1/13/2017.
 */

public class VideoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);

        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        Button btnclose = (Button) findViewById(R.id.btnclose);
        String path = getIntent().getStringExtra("video");
        //    File f = new File(path);

        //Creating MediaController
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        //specify the location of media file
        Uri uri = Uri.parse(path);

        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();


        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
