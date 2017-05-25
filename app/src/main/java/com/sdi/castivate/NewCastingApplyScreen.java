package com.sdi.castivate;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sdi.castivate.croppings.CropUtils;
import com.sdi.castivate.model.ApplyCastingInput;
import com.sdi.castivate.model.ApplyCastingOutput;
import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.MarshmallowPermission;
import com.sdi.castivate.utils.MultipartUtility;
import com.sdi.castivate.utils.RegisterRemoteApi;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nijamudhin on 3/13/2017.
 */

public class NewCastingApplyScreen extends Activity implements View.OnClickListener {

    Context context;
    TextView txtSave, library, cameraLabel;
    ProgressDialog pd;
    //  PopupWindow mpopup;
    Dialog mpopup;
    LinearLayout back_icon, ll_back, ll_front, ll_side, ll_voice, ll_video;
    ImageView iv_back1, iv_back2, iv_back3, iv_back4, iv_front1, iv_front2, iv_front3, iv_front4, iv_side1, iv_side2, iv_side3, iv_side4, iv_audio1, iv_audio2, iv_audio3, iv_audio4, iv_video1, iv_video2, iv_video3, iv_video4, cameraId, photoId, castivateProfile, dropboxId, googleDriveId, oneDriveId;
    public String type = "", Status = "", profileVideoPath = "";
    private final int TAKE_VIDEO_REQUEST = 4;
    MarshmallowPermission marshMallowPermission;
    String profileImagePath, castingId = "";
    File profileImage;
    //   ListView listView;
    public static final int FROM_CAMERA = 1115;
    private final int PICK_VIDEO_MULTIPLE = 1;
    int ACTION_REQUEST_CROP = 100;
    public static byte[] b = new byte[1];
    public static String encodedImage = null;
    final int IMAGE_MAX_SIZE = 600;
    ArrayList<File> fileListBack = new ArrayList<File>();
    ArrayList<File> fileListFront = new ArrayList<File>();
    ArrayList<File> fileListSide = new ArrayList<File>();
    ArrayList<File> fileListAudio = new ArrayList<File>();
    ArrayList<File> fileListVideo = new ArrayList<File>();
    public static final int FROM_LIBRARY = 1010;
    public static final int DROPBOX = 901;
    public static final int GOOGLEDRIVE = 902;
    public static final int ONEDRIVE = 903;
    public static final int VOICE = 904;
    public static final int VIDEO = 905;
    public static final int PHOTO = 906;
    public static final int AUDIO = 907;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.castivate_require_photo);
        context = this;
        marshMallowPermission = new MarshmallowPermission(this);

        try {
            castingId = getIntent().getStringExtra("castingid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        txtSave = (TextView) findViewById(R.id.txtSave);
        //  listView = (ListView) findViewById(R.id.listView_special);
        iv_back1 = (ImageView) findViewById(R.id.iv_back1);
        iv_back2 = (ImageView) findViewById(R.id.iv_back2);
        iv_back3 = (ImageView) findViewById(R.id.iv_back3);
        iv_back4 = (ImageView) findViewById(R.id.iv_back4);
        iv_front1 = (ImageView) findViewById(R.id.iv_front1);
        iv_front2 = (ImageView) findViewById(R.id.iv_front2);
        iv_front3 = (ImageView) findViewById(R.id.iv_front3);
        iv_front4 = (ImageView) findViewById(R.id.iv_front4);
        iv_side1 = (ImageView) findViewById(R.id.iv_side1);
        iv_side2 = (ImageView) findViewById(R.id.iv_side2);
        iv_side3 = (ImageView) findViewById(R.id.iv_side3);
        iv_side4 = (ImageView) findViewById(R.id.iv_side4);
        iv_audio1 = (ImageView) findViewById(R.id.iv_audio1);
        iv_audio2 = (ImageView) findViewById(R.id.iv_audio2);
        iv_audio3 = (ImageView) findViewById(R.id.iv_audio3);
        iv_audio4 = (ImageView) findViewById(R.id.iv_audio4);
        iv_video1 = (ImageView) findViewById(R.id.iv_video1);
        iv_video2 = (ImageView) findViewById(R.id.iv_video2);
        iv_video3 = (ImageView) findViewById(R.id.iv_video3);
        iv_video4 = (ImageView) findViewById(R.id.iv_video4);

        back_icon = (LinearLayout) findViewById(R.id.casting_back_icon);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_front = (LinearLayout) findViewById(R.id.ll_front);
        ll_side = (LinearLayout) findViewById(R.id.ll_side);
        ll_voice = (LinearLayout) findViewById(R.id.ll_voice);
        ll_video = (LinearLayout) findViewById(R.id.ll_video);

        iv_back1.setOnClickListener(this);
        iv_back2.setOnClickListener(this);
        iv_back3.setOnClickListener(this);
        iv_back4.setOnClickListener(this);
        iv_front1.setOnClickListener(this);
        iv_front2.setOnClickListener(this);
        iv_front3.setOnClickListener(this);
        iv_front4.setOnClickListener(this);
        iv_side1.setOnClickListener(this);
        iv_side2.setOnClickListener(this);
        iv_side3.setOnClickListener(this);
        iv_side4.setOnClickListener(this);
        iv_audio1.setOnClickListener(this);
        iv_audio2.setOnClickListener(this);
        iv_audio3.setOnClickListener(this);
        iv_audio4.setOnClickListener(this);
        iv_video1.setOnClickListener(this);
        iv_video2.setOnClickListener(this);
        iv_video3.setOnClickListener(this);
        iv_video4.setOnClickListener(this);

        back_icon.setOnClickListener(this);
        txtSave.setOnClickListener(this);


        if (Library.photoBack.equals("0")) {
            ll_back.setVisibility(View.GONE);
        } else {
            ll_back.setVisibility(View.VISIBLE);
            if (Library.photoBack.equals("1")) {
                iv_back1.setVisibility(View.VISIBLE);
                iv_back1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
            } else if (Library.photoBack.equals("2")) {
                iv_back1.setVisibility(View.VISIBLE);
                iv_back1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_back2.setVisibility(View.VISIBLE);
                iv_back2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
            } else if (Library.photoBack.equals("3")) {
                iv_back1.setVisibility(View.VISIBLE);
                iv_back1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_back2.setVisibility(View.VISIBLE);
                iv_back2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_back3.setVisibility(View.VISIBLE);
                iv_back3.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
            } else if (Library.photoBack.equals("4")) {
                iv_back1.setVisibility(View.VISIBLE);
                iv_back1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_back2.setVisibility(View.VISIBLE);
                iv_back2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_back3.setVisibility(View.VISIBLE);
                iv_back3.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_back4.setVisibility(View.VISIBLE);
                iv_back4.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));

            }

        }

        if (Library.photoFront.equals("0")) {
            ll_front.setVisibility(View.GONE);
        } else {
            ll_front.setVisibility(View.VISIBLE);
            if (Library.photoFront.equals("1")) {
                iv_front1.setVisibility(View.VISIBLE);
                iv_front1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
            } else if (Library.photoFront.equals("2")) {
                iv_front1.setVisibility(View.VISIBLE);
                iv_front1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_front2.setVisibility(View.VISIBLE);
                iv_front2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
            } else if (Library.photoFront.equals("3")) {
                iv_front1.setVisibility(View.VISIBLE);
                iv_front1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_front2.setVisibility(View.VISIBLE);
                iv_front2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_front3.setVisibility(View.VISIBLE);
                iv_front3.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
            } else if (Library.photoFront.equals("4")) {
                iv_front1.setVisibility(View.VISIBLE);
                iv_front1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_front2.setVisibility(View.VISIBLE);
                iv_front2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_front3.setVisibility(View.VISIBLE);
                iv_front3.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_front4.setVisibility(View.VISIBLE);
                iv_front4.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));

            }
        }

        if (Library.photoSide.equals("0")) {
            ll_side.setVisibility(View.GONE);
        } else {
            ll_side.setVisibility(View.VISIBLE);
            if (Library.photoSide.equals("1")) {
                iv_side1.setVisibility(View.VISIBLE);
                iv_side1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
            } else if (Library.photoSide.equals("2")) {
                iv_side1.setVisibility(View.VISIBLE);
                iv_side1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_side2.setVisibility(View.VISIBLE);
                iv_side2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
            } else if (Library.photoSide.equals("3")) {
                iv_side1.setVisibility(View.VISIBLE);
                iv_side1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_side2.setVisibility(View.VISIBLE);
                iv_side2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_side3.setVisibility(View.VISIBLE);
                iv_side3.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));

            } else if (Library.photoSide.equals("4")) {
                iv_side1.setVisibility(View.VISIBLE);
                iv_side1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_side2.setVisibility(View.VISIBLE);
                iv_side2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_side3.setVisibility(View.VISIBLE);
                iv_side3.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_side4.setVisibility(View.VISIBLE);
                iv_side4.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));

            }
        }

        if (Library.voiceCount.equals("0")) {
            ll_voice.setVisibility(View.GONE);
        } else {
            ll_voice.setVisibility(View.VISIBLE);
            if (Library.voiceCount.equals("1")) {
                iv_audio1.setVisibility(View.VISIBLE);
                iv_audio1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
            } else if (Library.voiceCount.equals("2")) {
                iv_audio1.setVisibility(View.VISIBLE);
                iv_audio1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_audio2.setVisibility(View.VISIBLE);
                iv_audio2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
            } else if (Library.voiceCount.equals("3")) {
                iv_audio1.setVisibility(View.VISIBLE);
                iv_audio1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_audio2.setVisibility(View.VISIBLE);
                iv_audio2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_audio3.setVisibility(View.VISIBLE);
                iv_audio3.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));

            } else if (Library.voiceCount.equals("4")) {
                iv_audio1.setVisibility(View.VISIBLE);
                iv_audio1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_audio2.setVisibility(View.VISIBLE);
                iv_audio2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_audio3.setVisibility(View.VISIBLE);
                iv_audio3.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_audio4.setVisibility(View.VISIBLE);
                iv_audio4.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));


            }
        }

        if (Library.videoCount.equals("0")) {
            ll_video.setVisibility(View.GONE);
        } else {
            ll_video.setVisibility(View.VISIBLE);
            if (Library.videoCount.equals("1")) {
                iv_video1.setVisibility(View.VISIBLE);
                iv_video1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
            } else if (Library.videoCount.equals("2")) {
                iv_video1.setVisibility(View.VISIBLE);
                iv_video1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_video2.setVisibility(View.VISIBLE);
                iv_video2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
            } else if (Library.videoCount.equals("3")) {
                iv_video1.setVisibility(View.VISIBLE);
                iv_video1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_video2.setVisibility(View.VISIBLE);
                iv_video2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_video3.setVisibility(View.VISIBLE);
                iv_video3.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));

            } else if (Library.videoCount.equals("4")) {
                iv_video1.setVisibility(View.VISIBLE);
                iv_video1.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_video2.setVisibility(View.VISIBLE);
                iv_video2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_video3.setVisibility(View.VISIBLE);
                iv_video3.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));
                iv_video4.setVisibility(View.VISIBLE);
                iv_video4.setImageDrawable(getResources().getDrawable(R.drawable.add_photo_icon));

            }
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back1:
                type = "b1";
                uploadAlert();
                break;


            case R.id.iv_back2:
                type = "b2";
                if (fileListBack.size() >= 1) {
                    uploadAlert();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_back3:
                type = "b3";
                if (fileListBack.size() >= 2) {
                    uploadAlert();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_back4:
                type = "b4";
                if (fileListBack.size() >= 1) {
                    uploadAlert();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_front1:
                type = "f1";
                uploadAlert();
                break;

            case R.id.iv_front2:
                type = "f2";
                if (fileListFront.size() >= 1) {
                    uploadAlert();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_front3:
                type = "f3";
                if (fileListFront.size() >= 2) {
                    uploadAlert();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_front4:
                type = "f4";
                if (fileListFront.size() >= 3) {
                    uploadAlert();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_side1:
                type = "s1";
                uploadAlert();
                break;

            case R.id.iv_side2:
                type = "s2";
                if (fileListSide.size() >= 1) {
                    uploadAlert();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_side3:
                type = "s3";
                if (fileListSide.size() >= 2) {
                    uploadAlert();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_side4:
                type = "s4";
                if (fileListSide.size() >= 3) {
                    uploadAlert();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_video1:
                type = "v1";
                uploadAlert();
                break;

            case R.id.iv_video2:
                type = "v2";
                if (fileListVideo.size() >= 1) {
                    uploadAlert();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_video3:
                type = "v3";
                if (fileListVideo.size() >= 2) {
                    uploadAlert();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_video4:
                type = "v4";
                if (fileListVideo.size() >= 3) {
                    uploadAlert();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_audio1:
                type = "a1";
                audioUpload();
                break;

            case R.id.iv_audio2:
                type = "a2";
                if (fileListAudio.size() >= 1) {
                    audioUpload();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_audio3:
                type = "a3";
                if (fileListAudio.size() >= 2) {
                    audioUpload();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.iv_audio4:
                type = "a4";
                if (fileListAudio.size() >= 3) {
                    audioUpload();

                } else
                    Library.alert(context, "Please add in previous items");
                break;

            case R.id.txtSave:
                validate();
                break;

            case R.id.casting_back_icon:
                finish();
                break;

        }
    }

    private void validate() {
        if ((String.valueOf(fileListBack.size()).equals(Library.photoBack)) && (String.valueOf(fileListFront.size()).equals(Library.photoFront)) && (String.valueOf(fileListSide.size()).equals(Library.photoSide)) && (String.valueOf(fileListAudio.size()).equals(Library.voiceCount)) && (String.valueOf(fileListVideo.size()).equals(Library.videoCount))) {
            new fileUpload().execute();
        } else {
            Library.alert(context, "You must add all the required files");
        }
    }

    public class fileUpload extends AsyncTask<String, String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            // Progress Dialog
            pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.show();
        }


        @Override
        protected String doInBackground(String... params) {
            uploadFile();
            return null;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            closeProgress();
            if (!Status.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(Status);
                    int code = jsonObject.getInt("status");
                    if (code == 200) {

                        if ((pd != null) && pd.isShowing()) {
                            pd.dismiss();
                        }
                       /* String message = jsonObject.getString("message");
                        alert(code, message);*/
                        ApplyCastingRetrofit();
                    } else {
                        Library.alert(context, jsonObject.getString("message"));
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
                    ApplyCastingRetrofit();
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

    public void uploadFile() {
        try {

            System.gc();

            MultipartUtility multipart = new MultipartUtility(HttpUri.CASTING_FILE_UPLOAD, "UTF-8");
//            multipart.addFormField("description", "Cool Pictures");

            multipart.addFormField("userid", Library.getUserId(context));
            //multipart new android param
            multipart.addFormField("user", "android");
            multipart.addFormField("special_recruitment", String.valueOf(1));
            multipart.addFormField("castingid", castingId);


            for (int i = 0; i < fileListFront.size(); i++) {
                multipart.addFilePart("photo_frontview[]", fileListFront.get(i));

            }
            for (int j = 0; j < fileListBack.size(); j++) {
                multipart.addFilePart("photo_backview[]", fileListBack.get(j));
            }

            for (int k = 0; k < fileListSide.size(); k++) {
                multipart.addFilePart("photo_sideview[]", fileListSide.get(k));
            }
            for (int l = 0; l < fileListAudio.size(); l++) {
                multipart.addFilePart("voice[]", fileListAudio.get(l));
            }

            for (int m = 0; m < fileListVideo.size(); m++) {
                multipart.addFilePart("video[]", fileListVideo.get(m));
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

    void uploadAlert() {
       /* View popUpView = getLayoutInflater().inflate(R.layout.new_upload_alert, null);
        mpopup = new PopupWindow(popUpView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
        mpopup.showAtLocation(popUpView, Gravity.BOTTOM, 0, 0);
        //    mpopup.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
        mpopup.setOutsideTouchable(true);
        mpopup.setTouchable(true);*/
        mpopup = new Dialog(context);
        mpopup.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        mpopup.setContentView(R.layout.new_upload_alert);
        mpopup.setCancelable(true);
        mpopup.setCanceledOnTouchOutside(true);
        mpopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mpopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        cameraId = (ImageView) mpopup.findViewById(R.id.cameraId);
        photoId = (ImageView) mpopup.findViewById(R.id.photoId);
        library = (TextView) mpopup.findViewById(R.id.library);
        cameraLabel = (TextView) mpopup.findViewById(R.id.cameraLabel);
        if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
            library.setText("Video Gallery");
        } else if (type.equals("a1") || (type.equals("a2")) || (type.equals("a3")) || (type.equals("a4"))) {
            library.setVisibility(View.GONE);
            photoId.setVisibility(View.GONE);
            cameraLabel.setText("Recorder");
            cameraId.setImageResource(R.drawable.audio_icon4);
        }
        castivateProfile = (ImageView) mpopup.findViewById(R.id.castivateProfile);
        dropboxId = (ImageView) mpopup.findViewById(R.id.dropboxId);
        googleDriveId = (ImageView) mpopup.findViewById(R.id.googleDriveId);
        oneDriveId = (ImageView) mpopup.findViewById(R.id.iCloudId);
        RelativeLayout rlll = (RelativeLayout) mpopup.findViewById(R.id.rlll);


        rlll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
            }
        });

        cameraId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();

                if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
                    recordVideo();
                } else if (type.equals("a1") || (type.equals("a2")) || (type.equals("a3")) || (type.equals("a4"))) {
                    if (marshMallowPermission.checkPermissionForRecord()) {
                        Intent i = new Intent(NewCastingApplyScreen.this, VoiceRecorder.class);
                        i.putExtra("specialreq", "req");
                        startActivityForResult(i, VOICE);
                    } else {
                        marshMallowPermission.requestPermissionForRecord();
                    }

                } else {
                    if (marshMallowPermission.checkPermissionForCamera()) {
                        initCamera(context);
                    } else {
                        marshMallowPermission.requestPermissionForCamera();
                    }
                }


             /*   if (marshMallowPermission.checkPermissionForRecord()) {
                    recordAudio();
                } else {
                    marshMallowPermission.requestPermissionForRecord();
                }*/


            }
        });

        photoId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
                    //    recordVideo();
                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
                        Intent intent = new Intent(context, VideoGridView.class);
                        intent.setType("video/*");
                        //    intent.putExtra("update_count", imgMAXCount);
                        startActivityForResult(intent, PICK_VIDEO_MULTIPLE);
                    } else {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }


                } else {
                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
                        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        startActivityForResult(i, FROM_LIBRARY);
                    } else {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }
                }


            }


        });

        castivateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                Intent i = new Intent(NewCastingApplyScreen.this, NewPhotoUpload.class);
                if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
                    i.putExtra("type", "video");
                    Library.castivateVideo = "castivateVideo";
                    startActivityForResult(i, VIDEO);
                    //  i.putExtra("special","v");
                } else if (type.equals("a1") || (type.equals("a2")) || (type.equals("a3")) || (type.equals("a4"))) {
                    i.putExtra("type", "audio");
                    Library.castivateAudio = "castivateAudio";
                    startActivityForResult(i, AUDIO);
                    //  i.putExtra("special","a");
                } else {
                    i.putExtra("type", "image");
                    Library.castivatePhoto = "castivatePhoto";
                    startActivityForResult(i, PHOTO);
                    //    i.putExtra("special","i");
                }

                //   startActivity(i);

            }
        });

        dropboxId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                Intent i = new Intent(NewCastingApplyScreen.this, CastingImageUpload.class);

                if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
                    i.putExtra("video", "dropbox");
                } else if (type.equals("a1") || (type.equals("a2")) || (type.equals("a3")) || (type.equals("a4"))) {
                    i.putExtra("audio", "dropbox");
                } else {
                    i.putExtra("image", "dropbox");
                }
                startActivityForResult(i, DROPBOX);

            }
        });

        googleDriveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                Intent i = new Intent(NewCastingApplyScreen.this, CastingImageUpload.class);

                if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
                    i.putExtra("video", "googledrive");
                } else if (type.equals("a1") || (type.equals("a2")) || (type.equals("a3")) || (type.equals("a4"))) {
                    i.putExtra("audio", "googledrive");
                } else {
                    i.putExtra("image", "googledrive");
                }
                startActivityForResult(i, GOOGLEDRIVE);

            }
        });

        oneDriveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                Intent i = new Intent(NewCastingApplyScreen.this, CastingImageUpload.class);

                if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
                    i.putExtra("video", "onedrive");
                } else if (type.equals("a1") || (type.equals("a2")) || (type.equals("a3")) || (type.equals("a4"))) {
                    i.putExtra("audio", "onedrive");
                } else {
                    i.putExtra("image", "onedrive");
                }
                startActivityForResult(i, ONEDRIVE);
            }
        });
        mpopup.show();
    }

    void audioUpload(){
        mpopup = new Dialog(context);
        mpopup.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        mpopup.setContentView(R.layout.audio_upload);
        mpopup.setCancelable(true);
        mpopup.setCanceledOnTouchOutside(true);
        mpopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mpopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        cameraId = (ImageView) mpopup.findViewById(R.id.cameraId);
        cameraLabel = (TextView) mpopup.findViewById(R.id.cameraLabel);
        if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
            library.setText("Video Gallery");
        } else if (type.equals("a1") || (type.equals("a2")) || (type.equals("a3")) || (type.equals("a4"))) {
           /* library.setVisibility(View.GONE);
            photoId.setVisibility(View.GONE);
            cameraLabel.setText("Recorder");
            cameraId.setImageResource(R.drawable.mic_icon1);*/
        }
        castivateProfile = (ImageView) mpopup.findViewById(R.id.castivateProfile);
        dropboxId = (ImageView) mpopup.findViewById(R.id.dropboxId);
        googleDriveId = (ImageView) mpopup.findViewById(R.id.googleDriveId);
        oneDriveId = (ImageView) mpopup.findViewById(R.id.iCloudId);
        RelativeLayout rlll = (RelativeLayout) mpopup.findViewById(R.id.rlll);


        rlll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
            }
        });

        cameraId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();

                if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
                    recordVideo();
                } else if (type.equals("a1") || (type.equals("a2")) || (type.equals("a3")) || (type.equals("a4"))) {
                    if (marshMallowPermission.checkPermissionForRecord()) {
                        Intent i = new Intent(NewCastingApplyScreen.this, VoiceRecorder.class);
                        i.putExtra("specialreq", "req");
                        startActivityForResult(i, VOICE);
                    } else {
                        marshMallowPermission.requestPermissionForRecord();
                    }

                } else {
                    if (marshMallowPermission.checkPermissionForCamera()) {
                        initCamera(context);
                    } else {
                        marshMallowPermission.requestPermissionForCamera();
                    }
                }


             /*   if (marshMallowPermission.checkPermissionForRecord()) {
                    recordAudio();
                } else {
                    marshMallowPermission.requestPermissionForRecord();
                }*/


            }
        });

       /* photoId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();

                if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
                    //    recordVideo();
                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
                        Intent intent = new Intent(context, VideoGridView.class);
                        intent.setType("video*//*");
                        //    intent.putExtra("update_count", imgMAXCount);
                        startActivityForResult(intent, PICK_VIDEO_MULTIPLE);
                    } else {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }


                } else {
                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
                        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        startActivityForResult(i, FROM_LIBRARY);
                    } else {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }
                }


            }


        });*/

        castivateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                Intent i = new Intent(NewCastingApplyScreen.this, NewPhotoUpload.class);
                if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
                    i.putExtra("type", "video");
                    Library.castivateVideo = "castivateVideo";
                    startActivityForResult(i, VIDEO);
                    //  i.putExtra("special","v");
                } else if (type.equals("a1") || (type.equals("a2")) || (type.equals("a3")) || (type.equals("a4"))) {
                    i.putExtra("type", "audio");
                    Library.castivateAudio = "castivateAudio";
                    startActivityForResult(i, AUDIO);
                    //  i.putExtra("special","a");
                } else {
                    i.putExtra("type", "image");
                    Library.castivatePhoto = "castivatePhoto";
                    startActivityForResult(i, PHOTO);
                    //    i.putExtra("special","i");
                }

                //   startActivity(i);

            }
        });

        dropboxId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                Intent i = new Intent(NewCastingApplyScreen.this, CastingImageUpload.class);

                if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
                    i.putExtra("video", "dropbox");
                } else if (type.equals("a1") || (type.equals("a2")) || (type.equals("a3")) || (type.equals("a4"))) {
                    i.putExtra("audio", "dropbox");
                } else {
                    i.putExtra("image", "dropbox");
                }
                startActivityForResult(i, DROPBOX);

            }
        });

        googleDriveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                Intent i = new Intent(NewCastingApplyScreen.this, CastingImageUpload.class);

                if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
                    i.putExtra("video", "googledrive");
                } else if (type.equals("a1") || (type.equals("a2")) || (type.equals("a3")) || (type.equals("a4"))) {
                    i.putExtra("audio", "googledrive");
                } else {
                    i.putExtra("image", "googledrive");
                }
                startActivityForResult(i, GOOGLEDRIVE);

            }
        });

        oneDriveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                Intent i = new Intent(NewCastingApplyScreen.this, CastingImageUpload.class);

                if (type.equals("v1") || (type.equals("v2")) || (type.equals("v3")) || (type.equals("v4"))) {
                    i.putExtra("video", "onedrive");
                } else if (type.equals("a1") || (type.equals("a2")) || (type.equals("a3")) || (type.equals("a4"))) {
                    i.putExtra("audio", "onedrive");
                } else {
                    i.putExtra("image", "onedrive");
                }
                startActivityForResult(i, ONEDRIVE);
            }
        });
        mpopup.show();
    }







    private void recordVideo() {

       /* File saveDir = null;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            saveDir = new File(Environment.getExternalStorageDirectory(), "CastivateFiles");
            saveDir.mkdirs();
        }

        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .showPortraitWarning(false)
                .allowRetry(true)
                .defaultToFrontFacing(true)
                //  .countdownSeconds(30.0f)
                .labelConfirm(R.string.mcam_use_video);

        materialCamera.start(TAKE_VIDEO_REQUEST);*/

        CropUtils.STORAGE availableStorage = CropUtils.getStorageWithFreeSpace(context);
        String rootPath = CropUtils.getRootPath(context, availableStorage);

        File folder = new File(rootPath);

        if (!folder.isDirectory()) {
            folder.mkdir();
        }

        File fileCamera = new File(getVideoPath(context,
                availableStorage, true));
        profileVideoPath = fileCamera.getAbsolutePath();

        if (!fileCamera.exists())
            try {
                fileCamera.createNewFile();
            } catch (IOException e) {
            }

        Uri mVideoUri = null;


        mVideoUri = Uri.fromFile(fileCamera);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri);
        startActivityForResult(intent, TAKE_VIDEO_REQUEST);

    }

    public static String getVideoPath(Context context, CropUtils.STORAGE availableStorage, boolean isTemp) {

        String path = null;
        String rootPath = getRootPath(context, availableStorage);

        String imageName = "wp_" + System.currentTimeMillis();
        if (isTemp) {
            imageName = "temp_" + System.currentTimeMillis();
        }

        switch (availableStorage) {
            case NONE:
                break;
            case PHONE:
            case SDCARD:
                if (rootPath != null)
                    path = rootPath + File.separator + imageName + ".mp4";
                break;
        }
        Log.d("log_tag", "Image Path: " + path);
        return path;
    }

    public static String getRootPath(Context context, CropUtils.STORAGE availableStorage) {
        String path = null;
        String appName = context.getString(R.string.app_name);
        switch (availableStorage) {
            case NONE:
                break;
            case PHONE:
                // path = Environment.getDataDirectory().getPath() + File.separator
                // + appName;

                path = Environment.getDataDirectory().getAbsolutePath() + File.separator + appName;

                break;
            case SDCARD:
                path = Environment.getExternalStorageDirectory().getPath() + File.separator + appName;
                break;
        }

        Log.d("log_tag", "Root Path: " + path);
        return path;
    }


    private void ApplyCastingRetrofit() {
        // TODO Auto-generated method stub

        // Progress Dialog
        pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();


        SharedPreferences prefs = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);

        ApplyCastingInput input = new ApplyCastingInput(Library.getUserId(context),
                Library.role_id, Library.enthicity, Library.age_range, Library.gender);

        RegisterRemoteApi.getInstance().setApplyCastingInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().getApplyCasting(context, new Callback<ApplyCastingOutput>() {
            @Override
            public void success(ApplyCastingOutput changePassResponse, Response response) {
                closeProgress();

                if (changePassResponse.status == 200) {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Library.alert(context, changePassResponse.message);

                }
            }

            @Override
            public void failure(RetrofitError error) {
                closeProgress();
//                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }


    private void initCamera(Context mContext) {

        CropUtils.STORAGE availableStorage = CropUtils.getStorageWithFreeSpace(mContext);
        String rootPath = CropUtils.getRootPath(mContext, availableStorage);

        File folder = new File(rootPath);

        if (!folder.isDirectory()) {
            folder.mkdir();
        }

        File fileCamera = new File(CropUtils.getImagePath(mContext,
                availableStorage, true));
        profileImagePath = fileCamera.getPath();

        if (!fileCamera.exists())
            try {
                fileCamera.createNewFile();
            } catch (IOException e) {
            }

        Uri mImageUri = Uri.fromFile(fileCamera);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, FROM_CAMERA);
    }

    ArrayList<File> uploadFileList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FROM_CAMERA && resultCode == Activity.RESULT_OK) {
            if (profileImagePath != null && !profileImagePath.equals("")) {
              /*  startActivityForResult(new Intent(context,
                        CropperImageActivity.class).putExtra("Path",
                        profileImagePath), ACTION_REQUEST_CROP);
*/
                File f = new File(profileImagePath);
                if (type.equals("b1")) {
                    if (fileListBack.size() == 0) {
                        fileListBack.add(0, f);
                    } else {
                        fileListBack.set(0, f);
                    }

                } else if (type.equals("b2")) {
                    if (fileListBack.size() <= 1) {
                        fileListBack.add(1, f);
                    } else {

                        fileListBack.set(1, f);

                    }
                } else if (type.equals("b3")) {
                    if (fileListBack.size() <= 2) {
                        fileListBack.add(2, f);
                    } else {

                        fileListBack.set(2, f);

                    }
                } else if (type.equals("b4")) {
                    if (fileListBack.size() <= 3) {
                        fileListBack.add(3, f);
                    } else {

                        fileListBack.set(3, f);

                    }
                } else if (type.equals("f1")) {
                    if (fileListFront.size() == 0) {
                        fileListFront.add(0, f);
                    } else {
                        fileListFront.set(0, f);
                    }
                } else if (type.equals("f2")) {
                    if (fileListFront.size() <= 1) {
                        fileListFront.add(1, f);
                    } else {
                        fileListFront.set(1, f);
                    }
                } else if (type.equals("f3")) {
                    if (fileListFront.size() <= 2) {
                        fileListFront.add(2, f);
                    } else {
                        fileListFront.set(2, f);
                    }
                } else if (type.equals("f4")) {
                    if (fileListFront.size() <= 3) {
                        fileListFront.add(3, f);
                    } else {
                        fileListFront.set(3, f);
                    }
                } else if (type.equals("s1")) {
                    if (fileListSide.size() == 0) {
                        fileListSide.add(0, f);
                    } else {
                        fileListSide.set(0, f);
                    }
                } else if (type.equals("s2")) {
                    if (fileListSide.size() <= 1) {
                        fileListSide.add(1, f);
                    } else {
                        fileListSide.set(1, f);
                    }
                } else if (type.equals("s3")) {
                    if (fileListSide.size() <= 2) {
                        fileListSide.add(2, f);
                    } else {
                        fileListSide.set(2, f);
                    }
                } else if (type.equals("s4")) {
                    if (fileListSide.size() <= 3) {
                        fileListSide.add(3, f);
                    } else {
                        fileListSide.set(3, f);
                    }
                }
                profileImage = new File(profileImagePath);

                imageDisplay();

            }
        } else if (requestCode == FROM_LIBRARY && resultCode == RESULT_OK && null != data) {

            profileImagePath = getRealPathFromURI(data.getData());

            File f = new File(profileImagePath);
            if (type.equals("b1")) {
                if (fileListBack.size() == 0) {
                    fileListBack.add(0, f);
                } else {
                    fileListBack.set(0, f);
                }
            } else if (type.equals("b2")) {
                if (fileListBack.size() <= 1) {
                    fileListBack.add(1, f);
                } else {

                    fileListBack.set(1, f);

                }

            } else if (type.equals("b3")) {
                if (fileListBack.size() <= 2) {
                    fileListBack.add(2, f);
                } else {

                    fileListBack.set(2, f);

                }
            } else if (type.equals("b4")) {
                if (fileListBack.size() <= 3) {
                    fileListBack.add(3, f);
                } else {

                    fileListBack.set(3, f);

                }
            } else if (type.equals("f1")) {
                if (fileListFront.size() == 0) {
                    fileListFront.add(0, f);
                } else {
                    fileListFront.set(0, f);
                }
            } else if (type.equals("f2")) {
                if (fileListFront.size() <= 1) {
                    fileListFront.add(1, f);
                } else {
                    fileListFront.set(1, f);
                }
            } else if (type.equals("f3")) {
                if (fileListFront.size() <= 2) {
                    fileListFront.add(2, f);
                } else {
                    fileListFront.set(2, f);
                }
            } else if (type.equals("f4")) {
                if (fileListFront.size() <= 3) {
                    fileListFront.add(3, f);
                } else {
                    fileListFront.set(3, f);
                }
            } else if (type.equals("s1")) {
                if (fileListSide.size() == 0) {
                    fileListSide.add(0, f);
                } else {
                    fileListSide.set(0, f);
                }
            } else if (type.equals("s2")) {
                if (fileListSide.size() <= 1) {
                    fileListSide.add(1, f);
                } else {
                    fileListSide.set(1, f);
                }
            } else if (type.equals("s3")) {
                if (fileListSide.size() <= 2) {
                    fileListSide.add(2, f);
                } else {
                    fileListSide.set(2, f);
                }
            } else if (type.equals("s4")) {
                if (fileListSide.size() <= 3) {
                    fileListSide.add(3, f);
                } else {
                    fileListSide.set(3, f);
                }
            }

            if (profileImagePath != null && !profileImagePath.equals("")) {
                profileImage = new File(profileImagePath);
                imageDisplay();
            }
        } else if (requestCode == DROPBOX && resultCode == RESULT_OK && data != null) {

           /* String message = data.getStringExtra("filepath");
            File f = new File(message);

            Bitmap bmp = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

            if (bmp != null) {
                if (type.equals("v1")) {
                    iv_video1.setImageBitmap(bmp);
                } else if (type.equals("v2")) {
                    iv_video2.setImageBitmap(bmp);
                } else if (type.equals("v3")) {
                    iv_video3.setImageBitmap(bmp);
                } else if (type.equals("v4")) {
                    iv_video4.setImageBitmap(bmp);
                }

            }

            if(f.exists()){
                if (type.equals("v1")) {
                    fileListVideo.add(f);
                } else if (type.equals("v2")) {
                    fileListVideo.add(f);
                } else if (type.equals("v3")) {
                    fileListVideo.add(f);
                } else if (type.equals("v4")) {
                    fileListVideo.add(f);
                }
            }*/

            String message = data.getStringExtra("filepath");
    //        String url = data.getStringExtra("url");
            File f = new File(message);
            Bitmap bitmapVideo = null;
            if (type.equals("v1") || type.equals("v2") || type.equals("v3") || type.equals("v4")) {
                bitmapVideo = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            } else {
                profileImagePath = f.getAbsolutePath();
                if (profileImagePath != null && !profileImagePath.equals("")) {
                    profileImage = new File(profileImagePath);
                    imageDisplay();
                }
                //   bitmapVideo = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Images.Thumbnails.MICRO_KIND);
            }

            if (bitmapVideo == null) {
                //   kjgh
                if (type.equals("v1") || type.equals("v2") || type.equals("v3") || type.equals("v4")) {
                    bitmapVideo = BitmapFactory.decodeResource(getResources(), R.drawable.play_icon);
                } else {
                    bitmapVideo = BitmapFactory.decodeResource(getResources(), R.drawable.photo_icon);
                }

            }

            if (type.equals("v1")) {
                iv_video1.setImageBitmap(bitmapVideo);
            } else if (type.equals("v2")) {
                iv_video2.setImageBitmap(bitmapVideo);
            } else if (type.equals("v3")) {
                iv_video3.setImageBitmap(bitmapVideo);

            } else if (type.equals("v4")) {
                iv_video4.setImageBitmap(bitmapVideo);
            } else if (type.equals("b1")) {
                iv_back1.setImageBitmap(bitmapVideo);
            } else if (type.equals("b2")) {
                iv_back2.setImageBitmap(bitmapVideo);
            } else if (type.equals("b3")) {
                iv_back3.setImageBitmap(bitmapVideo);
            } else if (type.equals("b4")) {
                iv_back4.setImageBitmap(bitmapVideo);
            } else if (type.equals("f1")) {
                iv_front1.setImageBitmap(bitmapVideo);
            } else if (type.equals("f2")) {
                iv_front2.setImageBitmap(bitmapVideo);
            } else if (type.equals("f3")) {
                iv_front3.setImageBitmap(bitmapVideo);
            } else if (type.equals("f4")) {
                iv_front4.setImageBitmap(bitmapVideo);
            } else if (type.equals("s1")) {
                iv_side1.setImageBitmap(bitmapVideo);
            } else if (type.equals("s2")) {
                iv_side2.setImageBitmap(bitmapVideo);
            } else if (type.equals("s3")) {
                iv_side3.setImageBitmap(bitmapVideo);
            } else if (type.equals("s4")) {
                iv_side4.setImageBitmap(bitmapVideo);
            } else if (type.equals("a1")) {
                iv_audio1.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() == 0) {
                    fileListAudio.add(0, f);
                } else {
                    fileListAudio.set(0, f);
                }
            } else if (type.equals("a2")) {
                iv_audio2.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 1) {
                    fileListAudio.add(1, f);
                } else {
                    fileListAudio.set(1, f);
                }
            } else if (type.equals("a3")) {
                iv_audio3.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 2) {
                    fileListAudio.add(2, f);
                } else {
                    fileListAudio.set(2, f);
                }
            } else if (type.equals("a4")) {
                iv_audio4.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 3) {
                    fileListAudio.add(3, f);
                } else {
                    fileListAudio.set(3, f);
                }
            }

            if (f.exists()) {
                if (type.equals("b1")) {
                    if (fileListBack.size() == 0) {
                        fileListBack.add(0, f);
                    } else {
                        fileListBack.set(0, f);
                    }
                } else if (type.equals("b2")) {
                    if (fileListBack.size() <= 1) {
                        fileListBack.add(1, f);
                    } else {

                        fileListBack.set(1, f);

                    }
                } else if (type.equals("b3")) {
                    if (fileListBack.size() <= 2) {
                        fileListBack.add(2, f);
                    } else {

                        fileListBack.set(2, f);

                    }
                } else if (type.equals("b4")) {
                    if (fileListBack.size() <= 3) {
                        fileListBack.add(3, f);
                    } else {

                        fileListBack.set(3, f);

                    }
                } else if (type.equals("f1")) {
                    if (fileListFront.size() == 0) {
                        fileListFront.add(0, f);
                    } else {
                        fileListFront.set(0, f);
                    }
                } else if (type.equals("f2")) {
                    if (fileListFront.size() <= 1) {
                        fileListFront.add(1, f);
                    } else {
                        fileListFront.set(1, f);
                    }
                } else if (type.equals("f3")) {
                    if (fileListFront.size() <= 2) {
                        fileListFront.add(2, f);
                    } else {
                        fileListFront.set(2, f);
                    }
                } else if (type.equals("f4")) {
                    if (fileListFront.size() <= 3) {
                        fileListFront.add(3, f);
                    } else {
                        fileListFront.set(3, f);
                    }
                } else if (type.equals("s1")) {
                    if (fileListSide.size() == 0) {
                        fileListSide.add(0, f);
                    } else {
                        fileListSide.set(0, f);
                    }
                } else if (type.equals("s2")) {
                    if (fileListSide.size() <= 1) {
                        fileListSide.add(1, f);
                    } else {
                        fileListSide.set(1, f);
                    }
                } else if (type.equals("s3")) {
                    if (fileListSide.size() <= 2) {
                        fileListSide.add(2, f);
                    } else {
                        fileListSide.set(2, f);
                    }
                } else if (type.equals("s4")) {
                    if (fileListSide.size() <= 3) {
                        fileListSide.add(3, f);
                    } else {
                        fileListSide.set(3, f);
                    }
                } else if (type.equals("v1")) {
                    if (fileListVideo.size() == 0) {
                        fileListVideo.add(0, f);
                    } else {
                        fileListVideo.set(0, f);
                    }
                } else if (type.equals("v2")) {
                    if (fileListVideo.size() <= 1) {
                        fileListVideo.add(1, f);
                    } else {
                        fileListVideo.set(1, f);
                    }
                } else if (type.equals("v3")) {
                    if (fileListVideo.size() <= 2) {
                        fileListVideo.add(2, f);
                    } else {
                        fileListVideo.set(2, f);
                    }
                } else if (type.equals("v4")) {
                    if (fileListVideo.size() <= 3) {
                        fileListVideo.add(3, f);
                    } else {
                        fileListVideo.set(3, f);
                    }
                }
            }
/*
            if (type.equals("b1")) {
                Glide.with(context).load(url.trim())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_back1);
                //    Picasso.with(context).load(message.trim()).into(iv_back1);
                //   iv_back1.setImageBitmap(bm);
            } else if (type.equals("b2")) {
                Glide.with(context).load(url.trim())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_back2);
                //    Picasso.with(context).load(message.trim()).into(iv_back2);
                //  iv_back2.setImageBitmap(bm);
            } else if (type.equals("b3")) {
                Glide.with(context).load(url.trim())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_back3);
                //    Picasso.with(context).load(message.trim()).into(iv_back3);
                //    iv_back3.setImageBitmap(bm);
            } else if (type.equals("b4")) {
                Glide.with(context).load(url.trim())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_back4);
                //     Picasso.with(context).load(message.trim()).into(iv_back4);
                //     iv_back4.setImageBitmap(bm);
            } else if (type.equals("f1")) {
                Glide.with(context).load(url.trim())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_front1);
                //      Picasso.with(context).load(message.trim()).into(iv_front1);
                //     iv_front1.setImageBitmap(bm);
            } else if (type.equals("f2")) {
                Glide.with(context).load(url.trim())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_front2);
                //       Picasso.with(context).load(message.trim()).into(iv_front2);
                //     iv_front2.setImageBitmap(bm);
            } else if (type.equals("f3")) {
                Glide.with(context).load(url.trim())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_front3);
                //         Picasso.with(context).load(message.trim()).into(iv_front3);
                //     iv_front3.setImageBitmap(bm);
            } else if (type.equals("f4")) {
                Glide.with(context).load(url.trim())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_front4);
                //       Picasso.with(context).load(message.trim()).into(iv_front4);
                //     iv_front4.setImageBitmap(bm);
            } else if (type.equals("s1")) {
                Glide.with(context).load(url.trim())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_side1);
                //        Picasso.with(context).load(message.trim()).into(iv_side1);
                //     iv_side1.setImageBitmap(bm);
            } else if (type.equals("s2")) {
                Glide.with(context).load(url.trim())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_side2);
                //       Picasso.with(context).load(message.trim()).into(iv_side2);
                //    iv_side2.setImageBitmap(bm);
            } else if (type.equals("s3")) {
                Glide.with(context).load(url.trim())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_side3);
                //        Picasso.with(context).load(message.trim()).into(iv_side3);
                //    iv_side3.setImageBitmap(bm);
            } else if (type.equals("s4")) {
                Glide.with(context).load(message.trim())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_side4);
                //       Picasso.with(context).load(message.trim()).into(iv_side4);
                //     iv_side4.setImageBitmap(bm);
            }*/


            //    imageDisplay();

          /*  String message = data.getStringExtra("filepath");
            File f = new File(message);
            Bitmap bitmapVideo = decodeFile(f.getAbsoluteFile());

            if (bitmapVideo != null) {
                if (type.equals("v1")) {
                    iv_video1.setImageBitmap(bitmapVideo);
                } else if (type.equals("v2")) {
                    iv_video2.setImageBitmap(bitmapVideo);
                } else if (type.equals("v3")) {
                    iv_video3.setImageBitmap(bitmapVideo);
                } else if (type.equals("v4")) {
                    iv_video4.setImageBitmap(bitmapVideo);
                }

            }*/


            //     Bitmap bitmapVideo = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);


            //         profileImage = new File(message);

            //        imageDisplay();


        } else if (requestCode == GOOGLEDRIVE && resultCode == RESULT_OK && null != data) {

            //      profileImagePath = getRealPathFromURI(data.getData());

          /*  String message = data.getStringExtra("filepath");
            File f = new File(message);

            Bitmap bitmapVideo = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

            if (type.equals("v1")) {
                iv_video1.setImageBitmap(bitmapVideo);
            } else if (type.equals("v2")) {
                iv_video2.setImageBitmap(bitmapVideo);
            } else if (type.equals("v3")) {
                iv_video3.setImageBitmap(bitmapVideo);
            } else if (type.equals("v4")) {
                iv_video4.setImageBitmap(bitmapVideo);
            }

            if(f.exists()){
                if (type.equals("v1")) {
                    fileListVideo.add(f);
                } else if (type.equals("v2")) {
                    fileListVideo.add(f);
                } else if (type.equals("v3")) {
                    fileListVideo.add(f);
                } else if (type.equals("v4")) {
                    fileListVideo.add(f);
                }
            }*/

            String message = data.getStringExtra("filepath");
            File f = new File(message);
            Bitmap bitmapVideo = null;
            if (type.equals("v1") || type.equals("v2") || type.equals("v3") || type.equals("v4")) {
                bitmapVideo = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
            } else {
                //   bitmapVideo = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Images.Thumbnails.MICRO_KIND);
                profileImagePath = f.getAbsolutePath();
                if (profileImagePath != null && !profileImagePath.equals("")) {
                    profileImage = new File(profileImagePath);
                    imageDisplay();
                }
            }


            if (bitmapVideo == null) {
                //   kjgh
                if (type.equals("v1") || type.equals("v2") || type.equals("v3") || type.equals("v4")) {
                    bitmapVideo = BitmapFactory.decodeResource(getResources(), R.drawable.play_icon);
                } else {
                    bitmapVideo = BitmapFactory.decodeResource(getResources(), R.drawable.photo_icon);
                }

            }

            if (type.equals("v1")) {
                iv_video1.setImageBitmap(bitmapVideo);
            } else if (type.equals("v2")) {
                iv_video2.setImageBitmap(bitmapVideo);
            } else if (type.equals("v3")) {
                iv_video3.setImageBitmap(bitmapVideo);
            } else if (type.equals("v4")) {
                iv_video4.setImageBitmap(bitmapVideo);
            } else if (type.equals("b1")) {
                iv_back1.setImageBitmap(bitmapVideo);
            } else if (type.equals("b2")) {
                iv_back2.setImageBitmap(bitmapVideo);
            } else if (type.equals("b3")) {
                iv_back3.setImageBitmap(bitmapVideo);
            } else if (type.equals("b4")) {
                iv_back4.setImageBitmap(bitmapVideo);
            } else if (type.equals("f1")) {
                iv_front1.setImageBitmap(bitmapVideo);
            } else if (type.equals("f2")) {
                iv_front2.setImageBitmap(bitmapVideo);
            } else if (type.equals("f3")) {
                iv_front3.setImageBitmap(bitmapVideo);
            } else if (type.equals("f4")) {
                iv_front4.setImageBitmap(bitmapVideo);
            } else if (type.equals("s1")) {
                iv_side1.setImageBitmap(bitmapVideo);
            } else if (type.equals("s2")) {
                iv_side2.setImageBitmap(bitmapVideo);
            } else if (type.equals("s3")) {
                iv_side3.setImageBitmap(bitmapVideo);

            } else if (type.equals("s4")) {
                iv_side4.setImageBitmap(bitmapVideo);
            } else if (type.equals("a1")) {
                iv_audio1.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() == 0) {
                    fileListAudio.add(0, f);
                } else {
                    fileListAudio.set(0, f);
                }
            } else if (type.equals("a2")) {
                iv_audio2.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 1) {
                    fileListAudio.add(1, f);
                } else {
                    fileListAudio.set(1, f);
                }
            } else if (type.equals("a3")) {
                iv_audio3.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 2) {
                    fileListAudio.add(2, f);
                } else {
                    fileListAudio.set(2, f);
                }
            } else if (type.equals("a4")) {
                iv_audio4.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 3) {
                    fileListAudio.add(3, f);
                } else {
                    fileListAudio.set(3, f);
                }
            }


            if (f.exists()) {
                if (type.equals("b1")) {
                    if (fileListBack.size() == 0) {
                        fileListBack.add(0, f);
                    } else {
                        fileListBack.set(0, f);
                    }

                } else if (type.equals("b2")) {
                    if (fileListBack.size() <= 1) {
                        fileListBack.add(1, f);
                    } else {

                        fileListBack.set(1, f);

                    }
                } else if (type.equals("b3")) {
                    if (fileListBack.size() <= 2) {
                        fileListBack.add(2, f);
                    } else {

                        fileListBack.set(2, f);

                    }
                } else if (type.equals("b4")) {
                    if (fileListBack.size() <= 3) {
                        fileListBack.add(3, f);
                    } else {

                        fileListBack.set(3, f);

                    }
                } else if (type.equals("f1")) {
                    if (fileListFront.size() == 0) {
                        fileListFront.add(0, f);
                    } else {
                        fileListFront.set(0, f);
                    }
                } else if (type.equals("f2")) {
                    if (fileListFront.size() <= 1) {
                        fileListFront.add(1, f);
                    } else {
                        fileListFront.set(1, f);
                    }
                } else if (type.equals("f3")) {
                    if (fileListFront.size() <= 2) {
                        fileListFront.add(2, f);
                    } else {
                        fileListFront.set(2, f);
                    }
                } else if (type.equals("f4")) {
                    if (fileListFront.size() <= 3) {
                        fileListFront.add(3, f);
                    } else {
                        fileListFront.set(3, f);
                    }
                } else if (type.equals("s1")) {
                    if (fileListSide.size() == 0) {
                        fileListSide.add(0, f);
                    } else {
                        fileListSide.set(0, f);
                    }
                } else if (type.equals("s2")) {
                    if (fileListSide.size() <= 1) {
                        fileListSide.add(1, f);
                    } else {
                        fileListSide.set(1, f);
                    }
                } else if (type.equals("s3")) {
                    if (fileListSide.size() <= 2) {
                        fileListSide.add(2, f);
                    } else {
                        fileListSide.set(2, f);
                    }
                } else if (type.equals("s4")) {
                    if (fileListSide.size() <= 3) {
                        fileListSide.add(3, f);
                    } else {
                        fileListSide.set(3, f);
                    }
                } else if (type.equals("v1")) {
                    if (fileListVideo.size() == 0) {
                        fileListVideo.add(0, f);
                    } else {
                        fileListVideo.set(0, f);
                    }
                } else if (type.equals("v2")) {
                    if (fileListVideo.size() <= 1) {
                        fileListVideo.add(1, f);
                    } else {
                        fileListVideo.set(1, f);
                    }
                } else if (type.equals("v3")) {
                    if (fileListVideo.size() <= 2) {
                        fileListVideo.add(2, f);
                    } else {
                        fileListVideo.set(2, f);
                    }
                } else if (type.equals("v4")) {
                    if (fileListVideo.size() <= 3) {
                        fileListVideo.add(3, f);
                    } else {
                        fileListVideo.set(3, f);
                    }
                }
            }


        } else if (requestCode == ONEDRIVE && resultCode == RESULT_OK && null != data) {

            //     profileImagePath = getRealPathFromURI(data.getData());

            String message = data.getStringExtra("filepath");
            File f = new File(message);
            Bitmap bitmapVideo = null;
            if (type.equals("v1") || type.equals("v2") || type.equals("v3") || type.equals("v4")) {
                bitmapVideo = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            } else {
                //    bitmapVideo = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Images.Thumbnails.MICRO_KIND);
                profileImagePath = f.getAbsolutePath();
                if (profileImagePath != null && !profileImagePath.equals("")) {
                    profileImage = new File(profileImagePath);
                    imageDisplay();
                }
            }


            if (bitmapVideo == null) {
                //   kjgh
                if (type.equals("v1") || type.equals("v2") || type.equals("v3") || type.equals("v4")) {
                    bitmapVideo = BitmapFactory.decodeResource(getResources(), R.drawable.play_icon);
                } else {
                    bitmapVideo = BitmapFactory.decodeResource(getResources(), R.drawable.photo_icon);
                }

            }

            if (type.equals("v1")) {
                iv_video1.setImageBitmap(bitmapVideo);
            } else if (type.equals("v2")) {
                iv_video2.setImageBitmap(bitmapVideo);
            } else if (type.equals("v3")) {
                iv_video3.setImageBitmap(bitmapVideo);
            } else if (type.equals("v4")) {
                iv_video4.setImageBitmap(bitmapVideo);
            } else if (type.equals("b1")) {
                iv_back1.setImageBitmap(bitmapVideo);
            } else if (type.equals("b2")) {
                iv_back2.setImageBitmap(bitmapVideo);
            } else if (type.equals("b3")) {
                iv_back3.setImageBitmap(bitmapVideo);
            } else if (type.equals("b4")) {
                iv_back4.setImageBitmap(bitmapVideo);
            } else if (type.equals("f1")) {
                iv_front1.setImageBitmap(bitmapVideo);
            } else if (type.equals("f2")) {
                iv_front2.setImageBitmap(bitmapVideo);
            } else if (type.equals("f3")) {
                iv_front3.setImageBitmap(bitmapVideo);
            } else if (type.equals("f4")) {
                iv_front4.setImageBitmap(bitmapVideo);
            } else if (type.equals("s1")) {
                iv_side1.setImageBitmap(bitmapVideo);
            } else if (type.equals("s2")) {
                iv_side2.setImageBitmap(bitmapVideo);
            } else if (type.equals("s3")) {
                iv_side3.setImageBitmap(bitmapVideo);
            } else if (type.equals("s4")) {
                iv_side4.setImageBitmap(bitmapVideo);
            } else if (type.equals("a1")) {
                iv_audio1.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() == 0) {
                    fileListAudio.add(0, f);
                } else {
                    fileListAudio.set(0, f);
                }
            } else if (type.equals("a2")) {
                iv_audio2.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 1) {
                    fileListAudio.add(1, f);
                } else {
                    fileListAudio.set(1, f);
                }
            } else if (type.equals("a3")) {
                iv_audio3.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 2) {
                    fileListAudio.add(2, f);
                } else {
                    fileListAudio.set(2, f);
                }
            } else if (type.equals("a4")) {
                iv_audio4.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 3) {
                    fileListAudio.add(3, f);
                } else {
                    fileListAudio.set(3, f);
                }
            }


            if (f.exists()) {
                if (type.equals("b1")) {
                    if (fileListBack.size() == 0) {
                        fileListBack.add(0, f);
                    } else {
                        fileListBack.set(0, f);
                    }
                } else if (type.equals("b2")) {
                    if (fileListBack.size() <= 1) {
                        fileListBack.add(1, f);
                    } else {

                        fileListBack.set(1, f);

                    }
                } else if (type.equals("b3")) {
                    if (fileListBack.size() <= 2) {
                        fileListBack.add(2, f);
                    } else {

                        fileListBack.set(2, f);

                    }
                } else if (type.equals("b4")) {
                    if (fileListBack.size() <= 3) {
                        fileListBack.add(3, f);
                    } else {

                        fileListBack.set(3, f);

                    }
                } else if (type.equals("f1")) {
                    if (fileListFront.size() == 0) {
                        fileListFront.add(0, f);
                    } else {
                        fileListFront.set(0, f);
                    }
                } else if (type.equals("f2")) {
                    if (fileListFront.size() <= 1) {
                        fileListFront.add(1, f);
                    } else {
                        fileListFront.set(1, f);
                    }
                } else if (type.equals("f3")) {
                    if (fileListFront.size() <= 2) {
                        fileListFront.add(2, f);
                    } else {
                        fileListFront.set(2, f);
                    }
                } else if (type.equals("f4")) {
                    if (fileListFront.size() <= 3) {
                        fileListFront.add(3, f);
                    } else {
                        fileListFront.set(3, f);
                    }
                } else if (type.equals("s1")) {
                    if (fileListSide.size() == 0) {
                        fileListSide.add(0, f);
                    } else {
                        fileListSide.set(0, f);
                    }
                } else if (type.equals("s2")) {
                    if (fileListSide.size() <= 1) {
                        fileListSide.add(1, f);
                    } else {
                        fileListSide.set(1, f);
                    }
                } else if (type.equals("s3")) {
                    if (fileListSide.size() <= 2) {
                        fileListSide.add(2, f);
                    } else {
                        fileListSide.set(2, f);
                    }
                } else if (type.equals("s4")) {
                    if (fileListSide.size() <= 3) {
                        fileListSide.add(3, f);
                    } else {
                        fileListSide.set(3, f);
                    }
                } else if (type.equals("v1")) {
                    if (fileListVideo.size() == 0) {
                        fileListVideo.add(0, f);
                    } else {
                        fileListVideo.set(0, f);
                    }
                } else if (type.equals("v2")) {
                    if (fileListVideo.size() <= 1) {
                        fileListVideo.add(1, f);
                    } else {
                        fileListVideo.set(1, f);
                    }
                } else if (type.equals("v3")) {
                    if (fileListVideo.size() <= 2) {
                        fileListVideo.add(2, f);
                    } else {
                        fileListVideo.set(2, f);
                    }
                } else if (type.equals("v4")) {
                    if (fileListVideo.size() <= 3) {
                        fileListVideo.add(3, f);
                    } else {
                        fileListVideo.set(3, f);
                    }
                }
            }


        } else if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_OK) {
            uploadFileList = new ArrayList<File>();
            if (profileVideoPath != null && !profileVideoPath.equals("")) {
                File file = new File(profileVideoPath);
                uploadFileList.add(file);


                //   File file = new File(data.getData().getPath());


                File filesDir = context.getFilesDir();
                File imageFile = new File(filesDir, "Thumb" + "_" + System.currentTimeMillis() + ".png");

                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                if (type.equals("v1")) {
                    iv_video1.setImageBitmap(bitmap);
                } else if (type.equals("v2")) {
                    iv_video2.setImageBitmap(bitmap);
                } else if (type.equals("v3")) {
                    iv_video3.setImageBitmap(bitmap);
                } else if (type.equals("v4")) {
                    iv_video4.setImageBitmap(bitmap);
                }


                File s = saveBitmap(bitmap, imageFile.getAbsolutePath());

                uploadFileList.add(s);

                if (type.equals("v1")) {
                    if (fileListVideo.size() == 0) {
                        fileListVideo.add(0, file);
                    } else {
                        fileListVideo.set(0, file);
                    }
                } else if (type.equals("v2")) {
                    if (fileListVideo.size() <= 1) {
                        fileListVideo.add(1, file);
                    } else {
                        fileListVideo.set(1, file);
                    }
                } else if (type.equals("v3")) {
                    if (fileListVideo.size() <= 2) {
                        fileListVideo.add(2, file);
                    } else {
                        fileListVideo.set(2, file);
                    }
                } else if (type.equals("v4")) {
                    if (fileListVideo.size() <= 3) {
                        fileListVideo.add(3, file);
                    } else {
                        fileListVideo.set(3, file);
                    }
                }
            }

            //        new DownloadFromURL().execute();

        } else if (requestCode == PICK_VIDEO_MULTIPLE && resultCode == RESULT_OK) {
            uploadFileList = new ArrayList<File>();

            String videourl = data.getStringExtra("url");


            File file = new File(videourl);
            uploadFileList.add(file);

            File filesDir = context.getFilesDir();
            File imageFile = new File(filesDir, "Thumb" + "_" + System.currentTimeMillis() + ".png");

            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
            if (type.equals("v1")) {
                iv_video1.setImageBitmap(bitmap);
            } else if (type.equals("v2")) {
                iv_video2.setImageBitmap(bitmap);
            } else if (type.equals("v3")) {
                iv_video3.setImageBitmap(bitmap);
            } else if (type.equals("v4")) {
                iv_video4.setImageBitmap(bitmap);
            }


            File s = saveBitmap(bitmap, imageFile.getAbsolutePath());

            uploadFileList.add(s);

            if (type.equals("v1")) {
                if (fileListVideo.size() == 0) {
                    fileListVideo.add(0, file);
                } else {
                    fileListVideo.set(0, file);
                }
            } else if (type.equals("v2")) {
                if (fileListVideo.size() <= 1) {
                    fileListVideo.add(1, file);
                } else {
                    fileListVideo.set(1, file);
                }
            } else if (type.equals("v3")) {
                if (fileListVideo.size() <= 2) {
                    fileListVideo.add(2, file);
                } else {
                    fileListVideo.set(2, file);
                }
            } else if (type.equals("v4")) {
                if (fileListVideo.size() <= 3) {
                    fileListVideo.add(3, file);
                } else {
                    fileListVideo.set(3, file);
                }
            }


        } else if (requestCode == VOICE && resultCode == RESULT_OK) {

            String message = data.getStringExtra("message");
            File f = new File(message);

            if (type.equals("a1")) {
                iv_audio1.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() == 0) {
                    fileListAudio.add(0, f);
                } else {
                    fileListAudio.set(0, f);
                }
            } else if (type.equals("a2")) {
                iv_audio2.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 1) {
                    fileListAudio.add(1, f);
                } else {
                    fileListAudio.set(1, f);
                }
            } else if (type.equals("a3")) {
                iv_audio3.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 2) {
                    fileListAudio.add(2, f);
                } else {
                    fileListAudio.set(2, f);
                }
            } else if (type.equals("a4")) {
                iv_audio4.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 3) {
                    fileListAudio.add(3, f);
                } else {
                    fileListAudio.set(3, f);
                }
            }
        } else if (requestCode == VIDEO && resultCode == RESULT_OK) {

            String message = data.getStringExtra("url");
            String thumb = data.getStringExtra("thumb");
            File f = new File(message);

            //    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
            if (type.equals("v1")) {
                Glide.with(context).load(thumb).into(iv_video1);

                //  iv_video1.setImageBitmap(bitmap);
            } else if (type.equals("v2")) {
                Glide.with(context).load(thumb).into(iv_video2);
                //   iv_video2.setImageBitmap(bitmap);
            } else if (type.equals("v3")) {
                Glide.with(context).load(thumb).into(iv_video3);
                //    iv_video3.setImageBitmap(bitmap);
            } else if (type.equals("v4")) {
                Glide.with(context).load(thumb).into(iv_video4);
                //    iv_video4.setImageBitmap(bitmap);
            }


            if (type.equals("v1")) {
                if (fileListVideo.size() == 0) {
                    fileListVideo.add(0, f);
                } else {
                    fileListVideo.set(0, f);
                }
            } else if (type.equals("v2")) {
                if (fileListVideo.size() <= 1) {
                    fileListVideo.add(1, f);
                } else {
                    fileListVideo.set(1, f);
                }
            } else if (type.equals("v3")) {
                if (fileListVideo.size() <= 2) {
                    fileListVideo.add(2, f);
                } else {
                    fileListVideo.set(2, f);
                }
            } else if (type.equals("v4")) {
                if (fileListVideo.size() <= 3) {
                    fileListVideo.add(3, f);
                } else {
                    fileListVideo.set(3, f);
                }
            }
        } else if (requestCode == AUDIO && resultCode == RESULT_OK) {

            String message = data.getStringExtra("message");
            File f = new File(message);

            if (type.equals("a1")) {
                iv_audio1.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() == 0) {
                    fileListAudio.add(0, f);
                } else {
                    fileListAudio.set(0, f);
                }
            } else if (type.equals("a2")) {
                iv_audio2.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 1) {
                    fileListAudio.add(1, f);
                } else {
                    fileListAudio.set(1, f);
                }
            } else if (type.equals("a3")) {
                iv_audio3.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 2) {
                    fileListAudio.add(2, f);
                } else {
                    fileListAudio.set(2, f);
                }
            } else if (type.equals("a4")) {
                iv_audio4.setImageResource(R.drawable.mic_icon);
                if (fileListAudio.size() <= 3) {
                    fileListAudio.add(3, f);
                } else {
                    fileListAudio.set(3, f);
                }
            }
        } else if (requestCode == PHOTO && resultCode == RESULT_OK && data != null) {

            //   ArrayList<String> msg = data.getStringArrayListExtra("message");
            //  ArrayList<String> myList = (ArrayList<String>) getIntent().getSerializableExtra("message");
            if (data.hasExtra("message")) {


                String msg = data.getStringExtra("message");
                String message = data.getStringExtra("url");
                File f = new File(msg);

                if (type.equals("b1")) {
                    if (fileListBack.size() == 0) {
                        fileListBack.add(0, f);
                    } else {
                        fileListBack.set(0, f);
                    }
                } else if (type.equals("b2")) {
                    if (fileListBack.size() <= 1) {
                        fileListBack.add(1, f);
                    } else {

                        fileListBack.set(1, f);

                    }
                } else if (type.equals("b3")) {
                    if (fileListBack.size() <= 2) {
                        fileListBack.add(2, f);
                    } else {

                        fileListBack.set(2, f);

                    }
                } else if (type.equals("b4")) {
                    if (fileListBack.size() <= 3) {
                        fileListBack.add(3, f);
                    } else {

                        fileListBack.set(3, f);

                    }
                } else if (type.equals("f1")) {
                    if (fileListFront.size() == 0) {
                        fileListFront.add(0, f);
                    } else {
                        fileListFront.set(0, f);
                    }
                } else if (type.equals("f2")) {
                    if (fileListFront.size() <= 1) {
                        fileListFront.add(1, f);
                    } else {
                        fileListFront.set(1, f);
                    }
                } else if (type.equals("f3")) {
                    if (fileListFront.size() <= 2) {
                        fileListFront.add(2, f);
                    } else {
                        fileListFront.set(2, f);
                    }
                } else if (type.equals("f4")) {
                    if (fileListFront.size() <= 3) {
                        fileListFront.add(3, f);
                    } else {
                        fileListFront.set(3, f);
                    }
                } else if (type.equals("s1")) {
                    if (fileListSide.size() == 0) {
                        fileListSide.add(0, f);
                    } else {
                        fileListSide.set(0, f);
                    }
                } else if (type.equals("s2")) {
                    if (fileListSide.size() <= 1) {
                        fileListSide.add(1, f);
                    } else {
                        fileListSide.set(1, f);
                    }
                } else if (type.equals("s3")) {
                    if (fileListSide.size() <= 2) {
                        fileListSide.add(2, f);
                    } else {
                        fileListSide.set(2, f);
                    }
                } else if (type.equals("s4")) {
                    if (fileListSide.size() <= 3) {
                        fileListSide.add(3, f);
                    } else {
                        fileListSide.set(3, f);
                    }
                }
                //     if (profileImagePath != null && !profileImagePath.equals("")) {
                /*profileImage = new File(message);
                imageDisplay();*/

                //          File file = new File(profileImagePath);

                //       Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (type.equals("b1")) {
                    Glide.with(context).load(message.trim())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_back1);
                    //    Picasso.with(context).load(message.trim()).into(iv_back1);
                    //   iv_back1.setImageBitmap(bm);
                } else if (type.equals("b2")) {
                    Glide.with(context).load(message.trim())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_back2);
                    //    Picasso.with(context).load(message.trim()).into(iv_back2);
                    //  iv_back2.setImageBitmap(bm);
                } else if (type.equals("b3")) {
                    Glide.with(context).load(message.trim())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_back3);
                    //    Picasso.with(context).load(message.trim()).into(iv_back3);
                    //    iv_back3.setImageBitmap(bm);
                } else if (type.equals("b4")) {
                    Glide.with(context).load(message.trim())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_back4);
                    //     Picasso.with(context).load(message.trim()).into(iv_back4);
                    //     iv_back4.setImageBitmap(bm);
                } else if (type.equals("f1")) {
                    Glide.with(context).load(message.trim())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_front1);
                    //      Picasso.with(context).load(message.trim()).into(iv_front1);
                    //     iv_front1.setImageBitmap(bm);
                } else if (type.equals("f2")) {
                    Glide.with(context).load(message.trim())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_front2);
                    //       Picasso.with(context).load(message.trim()).into(iv_front2);
                    //     iv_front2.setImageBitmap(bm);
                } else if (type.equals("f3")) {
                    Glide.with(context).load(message.trim())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_front3);
                    //         Picasso.with(context).load(message.trim()).into(iv_front3);
                    //     iv_front3.setImageBitmap(bm);
                } else if (type.equals("f4")) {
                    Glide.with(context).load(message.trim())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_front4);
                    //       Picasso.with(context).load(message.trim()).into(iv_front4);
                    //     iv_front4.setImageBitmap(bm);
                } else if (type.equals("s1")) {
                    Glide.with(context).load(message.trim())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_side1);
                    //        Picasso.with(context).load(message.trim()).into(iv_side1);
                    //     iv_side1.setImageBitmap(bm);
                } else if (type.equals("s2")) {
                    Glide.with(context).load(message.trim())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_side2);
                    //       Picasso.with(context).load(message.trim()).into(iv_side2);
                    //    iv_side2.setImageBitmap(bm);
                } else if (type.equals("s3")) {
                    Glide.with(context).load(message.trim())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_side3);
                    //        Picasso.with(context).load(message.trim()).into(iv_side3);
                    //    iv_side3.setImageBitmap(bm);
                } else if (type.equals("s4")) {
                    Glide.with(context).load(message.trim())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_side4);
                    //       Picasso.with(context).load(message.trim()).into(iv_side4);
                    //     iv_side4.setImageBitmap(bm);
                }
            }
            //   }
        } else if (requestCode == 890 && resultCode == RESULT_OK) {
            if (profileVideoPath != null && !profileVideoPath.equals("")) {
                System.out.println("Camera video path == " + profileVideoPath);

                File fileCamera = new File(profileVideoPath);

                if (fileCamera.exists()) {
                    uploadFileList = new ArrayList<File>();

                    //   File file = new File(data.getData().getPath());
                    uploadFileList.add(fileCamera);

                    File filesDir = context.getFilesDir();
                    File imageFile = new File(filesDir, "Thumb" + "_" + System.currentTimeMillis() + ".png");

                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(fileCamera.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    File s = saveBitmap(bitmap, imageFile.getAbsolutePath());

                    uploadFileList.add(s);


                }
            }
        }
    }

    private File saveBitmap(Bitmap bitmap, String path) {
        File file = null;
        if (bitmap != null) {
            file = new File(path);
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(path); //here is set your file path where you want to save or also here you can set file object directly

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public void imageDisplay() {

        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeFile(profileImagePath);
            DebugReportOnLocat.ln("Profile image : " + bm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bm != null) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            DebugReportOnLocat.ln("encodedImage >> " + encodedImage);
            if (type.equals("b1")) {
                iv_back1.setImageBitmap(bm);
            } else if (type.equals("b2")) {
                iv_back2.setImageBitmap(bm);
            } else if (type.equals("b3")) {
                iv_back3.setImageBitmap(bm);
            } else if (type.equals("b4")) {
                iv_back4.setImageBitmap(bm);
            } else if (type.equals("f1")) {
                iv_front1.setImageBitmap(bm);
            } else if (type.equals("f2")) {
                iv_front2.setImageBitmap(bm);
            } else if (type.equals("f3")) {
                iv_front3.setImageBitmap(bm);
            } else if (type.equals("f4")) {
                iv_front4.setImageBitmap(bm);
            } else if (type.equals("s1")) {
                iv_side1.setImageBitmap(bm);
            } else if (type.equals("s2")) {
                iv_side2.setImageBitmap(bm);
            } else if (type.equals("s3")) {
                iv_side3.setImageBitmap(bm);
            } else if (type.equals("s4")) {
                iv_side4.setImageBitmap(bm);
            } else if (type.equals("v1")) {
                iv_video1.setImageBitmap(bm);
            } else if (type.equals("v2")) {
                iv_video2.setImageBitmap(bm);
            } else if (type.equals("v3")) {
                iv_video3.setImageBitmap(bm);
            } else if (type.equals("v4")) {
                iv_video4.setImageBitmap(bm);
            }
        } else {
            // Toast.makeText(context, "Pick Images Only", Toast.LENGTH_LONG)
            // .show();
        }

    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null,
                null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private Bitmap decodeFile(File f) {

        Bitmap b = null;

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);

            BitmapFactory.decodeStream(fis, null, o);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {

            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ee) {
                ee.printStackTrace();
            }
        }

        int scale = 1;

        if (f.length() > 8000) {

            scale = 5;

            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(
                        2,
                        (int) Math.ceil(Math.log(IMAGE_MAX_SIZE
                                / (double) Math.max(o.outHeight, o.outWidth))
                                / Math.log(0.5)));
            }
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        b = BitmapFactory.decodeStream(fis, null, o2);
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return b;
    }

    private void closeProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    /*public class DownloadFromURL extends AsyncTask<String, String, String> {


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

            //       getImageVideoList();
        }

    }*/


    //   public String Status = "";

   /* public void uploadfile() {
        try {

            System.gc();

            MultipartUtility multipart = new MultipartUtility(HttpUri.CASTING_FILE_UPLOAD, "UTF-8");
//            multipart.addFormField("description", "Cool Pictures");

            multipart.addFormField("userid", Library.getUserId(context));
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
    }*/


}
