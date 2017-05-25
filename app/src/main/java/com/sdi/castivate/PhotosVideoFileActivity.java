package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdi.castivate.R;
import com.sdi.castivate.adapter.PhotoAdapter;
import com.sdi.castivate.adapter.VideoAdapter;
import com.sdi.castivate.adapter.VoiceAdapter;
import com.sdi.castivate.model.NewProfileinfoOutput;
import com.sdi.castivate.model.ProfileinfoInput;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.RegisterRemoteApi;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nijamudhin on 3/13/2017.
 */

public class PhotosVideoFileActivity extends Activity {
    LinearLayout photo_activity, video_activity, file_activity, audio_activity, back_icon;
    TextView photoCount, videosCount, fileCount, audioCount;
    String typeValue = "";
    private final int COUNT = 1001;
    ProgressDialog pd;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_photo);
        context = this;
        photoCount = (TextView) findViewById(R.id.photoCount);
        videosCount = (TextView) findViewById(R.id.videoCount);
        fileCount = (TextView) findViewById(R.id.fileCount);
        audioCount = (TextView) findViewById(R.id.audioCount);
        photo_activity = (LinearLayout) findViewById(R.id.photo_activity);
        video_activity = (LinearLayout) findViewById(R.id.video_activity);
        file_activity = (LinearLayout) findViewById(R.id.file_activity);
        audio_activity = (LinearLayout) findViewById(R.id.audio_activity);
        back_icon = (LinearLayout) findViewById(R.id.back_icon);

        SharedPreferences sharedpreferences = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        int imageCount = sharedpreferences.getInt("imageCount", 0);
        int videoCount = sharedpreferences.getInt("videoCount", 0);
        int resumeCount = sharedpreferences.getInt("resumeCount", 0);
        int voiceCount = sharedpreferences.getInt("audioCount", 0);

        photoCount.setText(String.valueOf(imageCount));
        videosCount.setText(String.valueOf(videoCount));
        fileCount.setText(String.valueOf(resumeCount));
        audioCount.setText(String.valueOf(voiceCount));

        //    typeValue = getIntent().getStringExtra("profile");

        photo_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                Intent i = new Intent(PhotosVideoFileActivity.this, NewPhotoUpload.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Library.castivatePhoto = "";
                i.putExtra("type", "image");
                startActivityForResult(i, COUNT);
            }
        });

        video_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                Intent i = new Intent(PhotosVideoFileActivity.this, NewPhotoUpload.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Library.castivateVideo = "";
                i.putExtra("type", "video");
                startActivityForResult(i, COUNT);
            }
        });

        file_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                //    Intent i = new Intent(PhotosVideoFileActivity.this, ResumeUpload.class);
                Intent i = new Intent(PhotosVideoFileActivity.this, NewPhotoUpload.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                i.putExtra("type", "files");
                startActivityForResult(i, COUNT);
            }
        });

        audio_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                Intent i = new Intent(PhotosVideoFileActivity.this, NewPhotoUpload.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Library.castivateAudio = "";
                i.putExtra("type", "audio");
                startActivityForResult(i, COUNT);
            }
        });

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    startActivity(new Intent(PhotosVideoFileActivity.this, MyProfile.class));
                finish();
               /* if (typeValue != null) {
                    if (typeValue.equals("profile")) {
                        startActivity(new Intent(PhotosVideoFileActivity.this, MyProfile.class));
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }*/


            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COUNT && resultCode == RESULT_OK) {
            getImageVideoList();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void getImageVideoList() {
        // TODO Auto-generated method stub

        // Progress Dialog
        pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        // Send the userName,password

        ProfileinfoInput input = new ProfileinfoInput(Library.getUserId(context));

        RegisterRemoteApi.getInstance().setProfileinfoInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().postnewPersonProfile(context, new Callback<NewProfileinfoOutput>() {
            @Override
            public void success(NewProfileinfoOutput newProfileinfoOutput, Response response) {
                closeProgress();
                if (newProfileinfoOutput.status == 200) {

                    int videoCount = newProfileinfoOutput.videos.size();
                    int imageCount = newProfileinfoOutput.images.size();
                    int resumeCount = newProfileinfoOutput.documents.size();
                    int audioCount1 = newProfileinfoOutput.voice.size();
                    int totalCount = videoCount + imageCount;

                    SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("imageCount", imageCount);
                    editor.putInt("videoCount", videoCount);
                    editor.putInt("resumeCount", resumeCount);
                    editor.putInt("audioCount", audioCount1);
                    editor.putInt("totalCount", totalCount);
                    editor.commit();


                    photoCount.setText(String.valueOf(imageCount));
                    videosCount.setText(String.valueOf(videoCount));
                    fileCount.setText(String.valueOf(resumeCount));
                    audioCount.setText(String.valueOf(audioCount1));


                }
            }

            @Override
            public void failure(RetrofitError error) {
                closeProgress();
            }
        });
    }


    private void closeProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }


}
