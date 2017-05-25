package com.sdi.castivate;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

/**
 * Created by nijamudhin on 3/28/2017.
 */

public class AudioPreview extends Activity {
    Button closeButton, buttonPlayStop;
    SeekBar seekBar;
    String path;
    private MediaPlayer m;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_preview);
        closeButton = (Button) findViewById(R.id.btnclose);
        buttonPlayStop = (Button) findViewById(R.id.ButtonPlayStop);
        seekBar = (SeekBar) findViewById(R.id.SeekBar01);

        m = new MediaPlayer();
        path = getIntent().getStringExtra("audio");
        if (m != null) {
            try {
                m.reset();
                m.setDataSource(path);
                m.prepare();
                m.start();
                startPlayProgressUpdater();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        seekBar.setMax(m.getDuration());
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);
                return false;
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick();
            }
        });


    }

    public void startPlayProgressUpdater() {
        seekBar.setProgress(m.getCurrentPosition());

        if (m.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        } else {
            m.pause();
            //    seekBar.setProgress(0);
            seekBar.setProgress(m.getCurrentPosition());
        }
    }

    // This is event handler thumb moving event
    private void seekChange(View v) {
        if (m.isPlaying()) {
            SeekBar sb = (SeekBar) v;
            m.seekTo(sb.getProgress());
        }
    }


    // This is event handler for buttonClick event
    private void buttonClick() {
        if (buttonPlayStop.getText() == "PLAY") {
            buttonPlayStop.setText("PAUSE");

            if (m != null) {
                try {
                    //    m.reset();
                    //    m.setDataSource(path);
                    //     m.prepare();
                    m.start();
                    startPlayProgressUpdater();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



          /*  try {
                m.start();
                startPlayProgressUpdater();
            } catch (IllegalStateException e) {
                m.pause();
            }*/
        } else {
            buttonPlayStop.setText("PLAY");
            m.pause();
        }
    }

}
