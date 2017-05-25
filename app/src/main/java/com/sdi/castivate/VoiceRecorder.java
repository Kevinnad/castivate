package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.MultipartUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nijamudhin on 3/27/2017.
 */

public class VoiceRecorder extends Activity {
    //  ImageView play, stop, record;
    private MediaRecorder myAudioRecorder = new MediaRecorder();
    private String outputFile = null, typeValue = "";
    private TextView timerValue, recText, useAudio, play, stop, record;
    private long startTime = 0L;
    private Context context;
    private Handler customHandler = new Handler();
    private ProgressDialog pd;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    ArrayList<File> uploadFileList;
    LinearLayout layBack;
    MediaPlayer m = new MediaPlayer();
    // black  #17202A   grey  #DAD8D8
    // mTextView.setTextColor(Color.parseColor("#bdbdbd"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_recorder);
        context = this;

        try {
            typeValue = getIntent().getStringExtra("specialreq");
        } catch (Exception e) {
            e.printStackTrace();
        }

        timerValue = (TextView) findViewById(R.id.timerText);
        play = (TextView) findViewById(R.id.play);
        record = (TextView) findViewById(R.id.record);
        stop = (TextView) findViewById(R.id.stop);
        useAudio = (TextView) findViewById(R.id.textView3);
        layBack = (LinearLayout) findViewById(R.id.layBack);



       /* timerValue = (TextView) findViewById(R.id.timerText);
        recText = (TextView) findViewById(R.id.recText);
        useAudio = (TextView) findViewById(R.id.useAudio);*/


      /*  play = (ImageView) findViewById(R.id.button3);
        stop = (ImageView) findViewById(R.id.button2);
        record = (ImageView) findViewById(R.id.button);*/

        stop.setEnabled(false);
        stop.setTextColor(Color.parseColor("#DAD8D8"));
        play.setEnabled(false);
        play.setTextColor(Color.parseColor("#DAD8D8"));

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/castivate-recording" + ts + ".3gp";
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);


        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.setEnabled(false);
                record.setTextColor(Color.parseColor("#DAD8D8"));

                stop.setEnabled(true);
                stop.setTextColor(Color.parseColor("#17202A"));

                play.setEnabled(false);
                play.setTextColor(Color.parseColor("#DAD8D8"));

                try {
                    if (myAudioRecorder == null) {
                        myAudioRecorder = new MediaRecorder();
                        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                        myAudioRecorder.setOutputFile(outputFile);
                    }
                    myAudioRecorder.prepare();
                    //     record.setBackground(getDrawable(R.drawable.ic_fiber_smart_record_black_24dp));
                    //      record.setImageDrawable(getResources().getDrawable(R.drawable.ic_fiber_smart_record_black_24dp));
                    //         recText.setVisibility(View.VISIBLE);
                    myAudioRecorder.start();
                    startTime = SystemClock.uptimeMillis();
                    timerun();
                    customHandler.postDelayed(updateTimerThread, 0);
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                record.setEnabled(false);
                record.setTextColor(Color.parseColor("#DAD8D8"));

                stop.setEnabled(true);
                stop.setTextColor(Color.parseColor("#17202A"));

                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop.setEnabled(false);
                stop.setTextColor(Color.parseColor("#DAD8D8"));

                record.setEnabled(true);
                record.setTextColor(Color.parseColor("#17202A"));

                play.setEnabled(true);
                play.setTextColor(Color.parseColor("#17202A"));


                stop();
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {

                if (m != null) {
                    try {
                        m.reset();
                        m.setDataSource(outputFile);
                        m.prepare();
                        m.start();
                        final Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (m != null && m.isPlaying()) {
                                            timerValue.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    int value = m.getCurrentPosition()/1000;
                                                    if (value < 10) {
                                                        timerValue.setText("" + 0 + ":" + 0 + (String.valueOf(value)));
                                                    } else {
                                                        timerValue.setText("" + 0 + ":" + (String.valueOf(value)));
                                                    }

                                                }
                                            });
                                        } else {
                                            timer.cancel();
                                            timer.purge();
                                        }
                                    }
                                });
                            }
                        }, 0, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        timerValue.setText("" + mins + ":"
                                + String.format("%02d", secs)/* + ":"
                    + String.format("%03d", milliseconds)*/);                        //      recText.setVisibility(View.GONE);
                    }
                });

                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });

        useAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!outputFile.equals("")) {
                    if (typeValue != null) {
                        if (typeValue.equals("req")) {
                            Intent intent = new Intent();
                            if (outputFile != null) {
                                intent.putExtra("message", outputFile);
                            }
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    } else {
                        uploadFileList = new ArrayList<File>();
                        File f = new File(outputFile);
                        uploadFileList.add(f);
                        new DownloadFromURL().execute();

                    }
                }
            }
        });

        layBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void timerun() {
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                if (myAudioRecorder != null) {
                    stop();

                }
            }

        };
        handler.postDelayed(r, 30000);

    }

    public void stop() {

        if (myAudioRecorder != null) {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            //            recText.setVisibility(View.GONE);
            //  record.setBackground(getDrawable(R.drawable.ic_fiber_manual_record_black_24dp));

            //   record.setImageDrawable(getResources().getDrawable(R.drawable.ic_fiber_manual_record_black_24dp));

            myAudioRecorder = null;
            timeSwapBuff += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimerThread);

            Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();

        }

    }

    int secs, mins;

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs) /*+ ":"
                    + String.format("%03d", milliseconds)*/);
            customHandler.postDelayed(this, 0);
        }

    };


    public class DownloadFromURL extends AsyncTask<String, String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        public String doInBackground(String... params) {
            uploadfile();
            return null;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            closeProgress();

            //   getImageVideoList();

            if (!Status.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(Status);
                    int code = jsonObject.getInt("status");
                    if (code == 200) {
                        String message = jsonObject.getString("message");
                        alert(code, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Library.alert(context, "Something went wrong. Try again.");
            }

        }

    }


    private void alert(final int code, String message) {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.alert_common, null);
        alertDialog.setContentView(convertView);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView txtContent = (TextView) convertView.findViewById(R.id.txtContent);
        Button btnOk = (Button) convertView.findViewById(R.id.btnOk);
        txtContent.setText(message);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                if (code == 200) {
                    Intent intent = new Intent();
                    if (outputFile != null) {
                        intent.putExtra("message", outputFile);
                    }
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }

            }
        });


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int value = (int) ((width) * 3 / 4);

        alertDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = value;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }


    private void closeProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    public String Status = "";

    public void uploadfile() {
        try {

            System.gc();

            MultipartUtility multipart = new MultipartUtility(HttpUri.CASTING_FILE_UPLOAD, "UTF-8");
//            multipart.addFormField("description", "Cool Pictures");

            multipart.addFormField("userid", Library.getUserId(context));
            //multipart new android param
            multipart.addFormField("user", "android");
            for (int i = 0; i < uploadFileList.size(); i++) {
                multipart.addFilePart("uploads[]", uploadFileList.get(i));
            }

            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");

            for (String line : response) {
                System.out.println("result : " + line);
                Status = line;
            }

            System.out.println("Server Response : " + Status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

