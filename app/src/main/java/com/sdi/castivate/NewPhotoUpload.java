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
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialcamera.MaterialCamera;
import com.sdi.castivate.adapter.CustomGrid;
import com.sdi.castivate.adapter.FileAdapter;
import com.sdi.castivate.adapter.PhotoAdapter;
import com.sdi.castivate.adapter.VideoAdapter;
import com.sdi.castivate.adapter.VoiceAdapter;
import com.sdi.castivate.croppings.CropUtils;
import com.sdi.castivate.model.DeleteFileOutput;
import com.sdi.castivate.model.DeleteModal;
import com.sdi.castivate.model.NewDeleteFileInput;
import com.sdi.castivate.model.NewFileModel;
import com.sdi.castivate.model.NewProfileinfoOutput;
import com.sdi.castivate.model.ProfileinfoInput;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.MarshmallowPermission;
import com.sdi.castivate.utils.MultipartUtility;
import com.sdi.castivate.utils.RegisterRemoteApi;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nijamudhin on 3/15/2017.
 */

public class NewPhotoUpload extends Activity {
    LinearLayout layBack;
    Context context;
    Dialog mpopup;
    //  PopupWindow mpopup;
    public static boolean isEdit = false;
    TextView textView, cameraLabel, library, castivateLabel;
    public static TextView txtDelete;
    ImageView cameraId, photoId, googleDriveId, dropboxId, oneDriveId, castivateProfile, ivDelete;
    Button btnCamera, btnImageGallery, btnCancel, btnVideo, btnVideoGallery;
    MarshmallowPermission marshMallowPermission;
    private final int PICK_VIDEO_MULTIPLE = 1;
    private final int PICK_IMAGE_MULTIPLE = 2;
    private final int TAKE_PICTURE_REQUEST = 3;
    private final int TAKE_VIDEO_REQUEST = 4;
    private final int TAKE_VOICE_REQUEST = 5;
    private final int DROPBOX = 6;
    private final int GOOGLEDRIVE = 5;
    private final int ONEDRIVE = 5;
    GridView iv_gridView;
    public String del_str = "";
    ArrayList<NewFileModel> add = new ArrayList<>();
    public static PhotoAdapter adapter;
    public static VideoAdapter adapterVideo;
    public static VoiceAdapter voiceAdapter;
    public static FileAdapter fileAdapter;
    public CustomGrid deleteAdapter;
    //  NewFileModel doc = new NewFileModel();


    public ArrayList<DeleteModal> del_arr;
    Integer[] imageIDs = {
            R.drawable.add_photo_icon, R.drawable.add_photo_icon};
    //   ImageView iv_AddPhoto;
    String typeValue = "", profileVideoPath;
    private final int COUNT = 1001;
    public static ArrayList<String> files = new ArrayList<String>();
    public static ArrayList<String> fileImage = new ArrayList<String>();
    public static ArrayList<String> fileData = new ArrayList<String>();
    public static ArrayList<String> fileAudio = new ArrayList<String>();
    public static ArrayList<String> filesData = new ArrayList<String>();
    public static ArrayList<String> filesDoc = new ArrayList<String>();
    public static ArrayList<String> filesDocThumb = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_photo_upload);
        context = this;
        iv_gridView = (GridView) findViewById(R.id.iv_gridView);
        textView = (TextView) findViewById(R.id.textView2);
        txtDelete = (TextView) findViewById(R.id.txt_Delete);
        ivDelete = (ImageView) findViewById(R.id.txt_Edit);
        del_arr = new ArrayList();
        getImageVideoList();
     /*   ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, numbers);*/
        //    ArrayAdapter<Drawable> adapter = new ArrayAdapter<Drawable>(this, android.R.layout.simple_list_item_1, numbers[0]);


        //   add.add(1,doc);
        //   iv_gridView.setAdapter(new ImageAdapter(this));

        typeValue = getIntent().getStringExtra("type");

        if (typeValue.equals("video")) {
            textView.setText("Videos");
        } else if (typeValue.equals("audio")) {
            textView.setText("Audio");
        } else if (typeValue.equals("files")) {
            textView.setText("Files");
        }

        iv_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 && txtDelete.getVisibility() == View.GONE) {
                    if (typeValue.equals("image")) {
                        showImageAlert();
                    } else if (typeValue.equals("audio")) {

                        showAudioAlert();
                       /* if (marshMallowPermission.checkPermissionForRecord()) {
                            if (marshMallowPermission.checkPermissionForExternalStorage()) {
                                Intent i = new Intent(NewPhotoUpload.this, VoiceRecorder.class);
                                startActivityForResult(i, TAKE_VOICE_REQUEST);

                            } else {
                                marshMallowPermission.requestPermissionForExternalStorage();
                            }
                        } else {
                            marshMallowPermission.requestPermissionForRecord();
                        }*/
                    } else if (typeValue.equals("files")) {
                     /*   Runtime.getRuntime().totalMemory();
                        Runtime.getRuntime().freeMemory();
                        Intent i = new Intent(NewPhotoUpload.this, ResumeUpload.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        i.putExtra("type", "files");
                        startActivityForResult(i, COUNT);*/

                        showFileAlert();

                    } else {
                        showVideoAlert();
                    }

                }
                //  Toast.makeText(context, "item", Toast.LENGTH_LONG).show();
            }
        });


        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ivDelete.setVisibility(View.VISIBLE);
                txtDelete.setVisibility(View.GONE);
                isEdit = false;
                del_str = "";
                for (int i = 0; i < del_arr.size(); i++) {
                    if (del_arr.get(i).isDel) {
                        if (del_str.equals("")) {
                            del_str = del_arr.get(i).img;
                        } else {
                            del_str = del_str + "," + del_arr.get(i).img;
                        }
                    }
                }


                System.out.println(del_str);

                if (!del_str.equals("")) {
                    //delete service
                    deletePhoto(del_str);

                } else {
                    if (typeValue.equals("image")) {
                        iv_gridView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else if (typeValue.equals("video")) {
                        iv_gridView.setAdapter(adapterVideo);
                        adapterVideo.notifyDataSetChanged();

                    } else if (typeValue.equals("files")) {
                        iv_gridView.setAdapter(fileAdapter);
                        fileAdapter.notifyDataSetChanged();
                    } else {
                        iv_gridView.setAdapter(voiceAdapter);
                        voiceAdapter.notifyDataSetChanged();
                    }
                }

            }
        });


        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                del_arr.clear();
                isEdit = true;
                ivDelete.setVisibility(View.GONE);
                txtDelete.setVisibility(View.VISIBLE);
                if (typeValue.equals("image")) {
                    for (int i = 0; i < files.size(); i++) {

                        del_arr.add(new DeleteModal(files.get(i), fileImage.get(i), false));

                    }
                } else if (typeValue.equals("video")) {
                    for (int i = 0; i < filesData.size(); i++) {

                        del_arr.add(new DeleteModal(fileData.get(i), filesData.get(i), false));

                    }
                } else if (typeValue.equals("audio")) {
                    for (int i = 0; i < fileAudio.size(); i++) {

                        del_arr.add(new DeleteModal(fileAudio.get(i), "audiourl", false));


                    }
                } else if (typeValue.equals("files")) {
                    for (int i = 0; i < filesDoc.size(); i++) {
                        del_arr.add(new DeleteModal(filesDoc.get(i), filesDocThumb.get(i), false));
                    }
                }

                del_arr.remove(0);
                deleteAdapter = new CustomGrid(context, del_arr);
                iv_gridView.setAdapter(deleteAdapter);

            }
        });


        layBack = (LinearLayout) findViewById(R.id.layBack);
        marshMallowPermission = new MarshmallowPermission(this);
        //    iv_AddPhoto = (ImageView) findViewById(R.id.iv_AddPhoto);

        layBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Library.castivateAudio.equals("") && Library.castivatePhoto.equals("") && Library.castivateVideo.equals("")) {

                    deleteCache(context);

                   /* Intent intent = new Intent(NewPhotoUpload.this, PhotosVideoFileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();*/

                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();

                } else {
                    deleteCache(context);
                    finish();
                }


            }
        });

       /* iv_AddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    ProgressDialog pd;

    private void deletePhoto(final String pos) {
        // TODO Auto-generated method stub

        // Progress Dialog
        pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        try {

            NewDeleteFileInput input = new NewDeleteFileInput(Library.getUserId(context), pos);

            RegisterRemoteApi.getInstance().setNewDeleteInput(input);

            // Call Login JSON
            RegisterRemoteApi.getInstance().getNewDeleteInput(context, new Callback<DeleteFileOutput>() {
                @Override
                public void success(DeleteFileOutput profileinfoOutput, Response response) {
                    closeProgress();
                    if (profileinfoOutput.status == 200) {

                        Library.reduceUserProfileDetails(context, "image");

                        //      MyProfile.files.remove(pos);
                        getImageVideoList();


                    } else {
                        closeProgress();
                        if (!profileinfoOutput.message.contains("check")) {
                            Library.alert(context, profileinfoOutput.message);
                        } else {
                            getImageVideoList();
                        }

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    closeProgress();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            closeProgress();
        }
    }


    private void showAudioAlert() {
      /*  View mpopup = getLayoutInflater().inflate(R.layout.new_upload_alert, null);
        mpopup = new PopupWindow(mpopup, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
        mpopup.showAtLocation(mpopup, Gravity.BOTTOM, 0, 0);
        //    mpopup.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
        mpopup.setOutsideTouchable(false);
        mpopup.setTouchable(true);*/


        mpopup = new Dialog(context);
        mpopup.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        mpopup.setContentView(R.layout.audio_upload_alert);
        mpopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mpopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mpopup.show();
        mpopup.setCancelable(true);
        mpopup.setCanceledOnTouchOutside(true);
       /* Window window = mpopup.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);*/
        cameraId = (ImageView) mpopup.findViewById(R.id.cameraId);
        //   photoId = (ImageView) mpopup.findViewById(R.id.photoId);
        //  library = (TextView) mpopup.findViewById(R.id.library);
        //    castivateLabel = (TextView) mpopup.findViewById(R.id.castivateLabel);
        cameraLabel = (TextView) mpopup.findViewById(R.id.cameraLabel);
        //  library.setVisibility(View.GONE);
        //    photoId.setVisibility(View.GONE);
        //    castivateLabel.setVisibility(View.GONE);
        cameraId.setImageResource(R.drawable.audio_icon4);
        cameraLabel.setText("Recorder");
        //   castivateProfile = (ImageView) mpopup.findViewById(R.id.castivateProfile);
        //   castivateProfile.setVisibility(View.GONE);
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
                if (marshMallowPermission.checkPermissionForRecord()) {
                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
                        Intent i = new Intent(NewPhotoUpload.this, VoiceRecorder.class);
                        startActivityForResult(i, TAKE_VOICE_REQUEST);
                    } else {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }
                } else {
                    marshMallowPermission.requestPermissionForRecord();
                }
            }
        });


        googleDriveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Intent i = new Intent(NewPhotoUpload.this, AudioFiles.class);
                i.putExtra("audio", "googledrive");
                startActivityForResult(i, GOOGLEDRIVE);
            }
        });


        dropboxId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Intent i = new Intent(NewPhotoUpload.this, AudioFiles.class);
                i.putExtra("audio", "dropbox");
                startActivityForResult(i, DROPBOX);


            }
        });

        oneDriveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Intent i = new Intent(NewPhotoUpload.this, AudioFiles.class);
                i.putExtra("audio", "onedrive");
                startActivityForResult(i, ONEDRIVE);
            }
        });


    }

    private void showVideoAlert() {
        /*mpopup = new Dialog(context);
        mpopup.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        mpopup.setContentView(R.layout.profile_video_alert);
        mpopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mpopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mpopup.show();

        btnCamera = (Button) mpopup.findViewById(R.id.btnCamera);
        btnImageGallery = (Button) mpopup.findViewById(R.id.btnImageGallery);
        btnVideo = (Button) mpopup.findViewById(R.id.btnVideo);
        btnVideoGallery = (Button) mpopup.findViewById(R.id.btnVideoGallery);
        btnCamera.setVisibility(View.GONE);
        btnImageGallery.setVisibility(View.GONE);
        btnCancel = (Button) mpopup.findViewById(R.id.btnCancel);

*/


        //    View mpopup = getLayoutInflater().inflate(R.layout.new_upload_alert, null);
        mpopup = new Dialog(context);
        mpopup.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        mpopup.setContentView(R.layout.photo_alert_upload);
        mpopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mpopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mpopup.show();
        mpopup.setCancelable(true);
        mpopup.setCanceledOnTouchOutside(true);

       /* Window window = mpopup.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);*/
      /*
        mpopup = new PopupWindow(mpopup, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
        mpopup.showAtLocation(mpopup, Gravity.BOTTOM, 0, 0);
        //    mpopup.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
        mpopup.setOutsideTouchable(false);
        mpopup.setTouchable(true);*/
        cameraId = (ImageView) mpopup.findViewById(R.id.cameraId);
        photoId = (ImageView) mpopup.findViewById(R.id.photoId);
        library = (TextView) mpopup.findViewById(R.id.library);
        library.setText("Video Library");
        cameraLabel = (TextView) mpopup.findViewById(R.id.cameraLabel);
       /* castivateLabel = (TextView) mpopup.findViewById(R.id.castivateLabel);
        castivateProfile = (ImageView) mpopup.findViewById(R.id.castivateProfile);
        castivateProfile.setVisibility(View.GONE);
        castivateLabel.setVisibility(View.GONE);*/
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

              /*  int imgMAXCount = getImageCount("Video");*/

                //    if (imgMAXCount != 0) {
                if (marshMallowPermission.checkPermissionForCamera()) {
                    recordVideo();
                } else {
                    marshMallowPermission.requestPermissionForCamera();
                }

             /* } else {
                    Library.alert(context, "Your limit is exceeded.");
                }*/
            }
        });

        photoId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                //    int imgMAXCount = getImageCount("Video");

                //      if (imgMAXCount != 0) {
                if (marshMallowPermission.checkPermissionForExternalStorage()) {
                    Intent intent = new Intent(context, CastingCustomVideoGallery.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setType("video/*");
                    //    intent.putExtra("update_count", imgMAXCount);
                    startActivityForResult(intent, PICK_VIDEO_MULTIPLE);
                } else {
                    marshMallowPermission.requestPermissionForExternalStorage();
                }
               /* } else {
                    Library.alert(context, "Your limit is exceeded.");
                }
*/
            }
        });


        googleDriveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Intent i = new Intent(NewPhotoUpload.this, CastingVideoUploadProfile.class);
                i.putExtra("video", "googledrive");
                startActivityForResult(i, GOOGLEDRIVE);
            }
        });


        dropboxId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Intent i = new Intent(NewPhotoUpload.this, CastingVideoUploadProfile.class);
                i.putExtra("video", "dropbox");
                startActivityForResult(i, DROPBOX);


            }
        });

        oneDriveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Intent i = new Intent(NewPhotoUpload.this, CastingVideoUploadProfile.class);
                i.putExtra("video", "onedrive");
                startActivityForResult(i, ONEDRIVE);
            }
        });


    }

    private void showImageAlert() {
       /* mpopup = new Dialog(context);
        mpopup.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        mpopup.setContentView(R.layout.profile_video_alert);
        mpopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mpopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mpopup.show();

        btnCamera = (Button) mpopup.findViewById(R.id.btnCamera);
        btnImageGallery = (Button) mpopup.findViewById(R.id.btnImageGallery);
        btnVideo = (Button) mpopup.findViewById(R.id.btnVideo);
        btnVideoGallery = (Button) mpopup.findViewById(R.id.btnVideoGallery);
        btnVideo.setVisibility(View.GONE);
        btnVideoGallery.setVisibility(View.GONE);
        btnCancel = (Button) mpopup.findViewById(R.id.btnCancel);*/


       /* View mpopup = getLayoutInflater().inflate(R.layout.new_upload_alert, null);
        mpopup = new PopupWindow(mpopup, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
        mpopup.showAtLocation(mpopup, Gravity.BOTTOM, 0, 0);
        //    mpopup.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
        //  mpopup.setOutsideTouchable(false);
        mpopup.setTouchable(true);*/

        mpopup = new Dialog(context);
        mpopup.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        mpopup.setContentView(R.layout.photo_alert_upload);
        mpopup.setCancelable(true);
        mpopup.setCanceledOnTouchOutside(true);
        mpopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mpopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


       /* Window window = mpopup.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);*/

        cameraId = (ImageView) mpopup.findViewById(R.id.cameraId);
        photoId = (ImageView) mpopup.findViewById(R.id.photoId);
        library = (TextView) mpopup.findViewById(R.id.library);
        cameraLabel = (TextView) mpopup.findViewById(R.id.cameraLabel);
        /*castivateLabel = (TextView) mpopup.findViewById(R.id.castivateLabel);
        castivateProfile = (ImageView) mpopup.findViewById(R.id.castivateProfile);
        castivateProfile.setVisibility(View.GONE);
        castivateLabel.setVisibility(View.GONE);*/
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

        /* Camera button click Method */
        cameraId.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mpopup.dismiss();
               /* int imgMAXCount = getImageCount("Photo");
                if (imgMAXCount != 0) {*/
                if (marshMallowPermission.checkPermissionForCamera()) {
                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
                        cameraIntent();
                    } else {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }
                } else {
                    marshMallowPermission.requestPermissionForCamera();
                }
/*
                } else {
                    Library.alert(context, "Your limit is exceeded.");
                }*/


            }
        });

        /* Gallery button click Method */
        photoId.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mpopup.dismiss();

                int imgMAXCount = getImageCount("Photo");

       //         if (imgMAXCount != 0) {

                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
                        Intent intent = new Intent(context, CastingCustomPhotoGallery.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setType("image*//*");
                        intent.putExtra("update_count", 10);
                        startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
                    } else {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }

                /*} else {
                    Library.alert(context, "Your limit is exceeded.");
                }*/
                }
   //         }

        });


        googleDriveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();

                Intent i = new Intent(NewPhotoUpload.this, CastingImageUploadProfile.class);
                i.putExtra("image", "googledrive");
                startActivityForResult(i, GOOGLEDRIVE);
            }
        });


        dropboxId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Intent i = new Intent(NewPhotoUpload.this, CastingImageUploadProfile.class);
                i.putExtra("image", "dropbox");
                startActivityForResult(i, DROPBOX);


            }
        });

        oneDriveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                Intent i = new Intent(NewPhotoUpload.this, CastingImageUploadProfile.class);
                i.putExtra("image", "onedrive");
                startActivityForResult(i, ONEDRIVE);
            }
        });

        mpopup.show();


      /*  Intent i = new Intent(NewPhotoUpload.this, CastingImageUploadProfile.class);
        i.putExtra("image", "dropbox");
        startActivityForResult(i, DROPBOX);
        Intent i = new Intent(NewPhotoUpload.this, CastingImageUploadProfile.class);
        i.putExtra("image", "googledrive");
        startActivityForResult(i, GOOGLEDRIVE);
        Intent i = new Intent(NewPhotoUpload.this, CastingImageUploadProfile.class);
        i.putExtra("image", "onedrive");
        startActivityForResult(i, ONEDRIVE);*/


    }

    private void showFileAlert() {
        mpopup = new Dialog(context);
        mpopup.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        mpopup.setContentView(R.layout.new_upload_alert);
        mpopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mpopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mpopup.show();
        mpopup.setCancelable(true);
        mpopup.setCanceledOnTouchOutside(true);
       /* Window window = mpopup.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);*/

        LinearLayout image = (LinearLayout) mpopup.findViewById(R.id.image_photo);
        LinearLayout label = (LinearLayout) mpopup.findViewById(R.id.label_photo);
        image.setVisibility(View.GONE);
        label.setVisibility(View.GONE);
        ImageView dropboxId = (ImageView) mpopup.findViewById(R.id.dropboxId);
        ImageView googleDriveId = (ImageView) mpopup.findViewById(R.id.googleDriveId);
        ImageView oneDriveId = (ImageView) mpopup.findViewById(R.id.iCloudId);
        RelativeLayout rlll = (RelativeLayout) mpopup.findViewById(R.id.rlll);


        rlll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
            }
        });

        dropboxId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                //  dropboxApi();
                Intent i = new Intent(NewPhotoUpload.this, NewResumeUpload.class);
                i.putExtra("resume", "dropbox");
                startActivityForResult(i, DROPBOX);

            }
        });

        googleDriveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                //     googleDriveApi();
                Intent i = new Intent(NewPhotoUpload.this, NewResumeUpload.class);
                i.putExtra("resume", "googledrive");
                startActivityForResult(i, GOOGLEDRIVE);
            }
        });

        oneDriveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
                //  oneDriveApi();
                Intent i = new Intent(NewPhotoUpload.this, NewResumeUpload.class);
                i.putExtra("resume", "onedrive");
                startActivityForResult(i, ONEDRIVE);
            }
        });
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
            public void success(NewProfileinfoOutput profileinfoOutput, Response response) {
                closeProgress();

                System.out.println("upload image response========================");
                if (profileinfoOutput.status == 200) {

                    int videoCount = profileinfoOutput.videos.size();
                    int imageCount = profileinfoOutput.images.size();
                    int resumeCount = profileinfoOutput.documents.size();
                    int audioCount = profileinfoOutput.voice.size();
                    int totalCount = videoCount + imageCount;

                    SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("imageCount", imageCount);
                    editor.putInt("videoCount", videoCount);
                    editor.putInt("resumeCount", resumeCount);
                    editor.putInt("audioCount", audioCount);
                    editor.putInt("totalCount", totalCount);
                    editor.commit();

                    if (typeValue.equals("image")) {
                        files.clear();
                        fileImage.clear();

                        for (int i = 0; i < profileinfoOutput.images.size(); i++) {
                       /* if (profileinfoOutput.files.get(i).type.equals("resume")) {

                            String resumeName = profileinfoOutput.files.get(i).url;
                            Library.doc_id = profileinfoOutput.files.get(i).doc_id;
                            System.out.println("profileinfoOutput.files.get(i).doc_id = " + profileinfoOutput.files.get(i).doc_id);
                            int pos = resumeName.lastIndexOf("/");
                            pos = pos + 1;
                            resumeName = resumeName.substring(pos, resumeName.length());
                        } else {
                            files.add(profileinfoOutput.images.get(i));
                        }*/
                            //       files.add()
                            System.out.println("images    " + i + "     " + profileinfoOutput.images.get(i));
                            files.add(profileinfoOutput.images.get(i));
                        }

                        for (int j = 0; j < profileinfoOutput.images_thumb.size(); j++) {
                            System.out.println("images  thumb   " + j + "     " + profileinfoOutput.images.get(j));

                            fileImage.add(profileinfoOutput.images_thumb.get(j));
                        }

                        //  Library.putUserProfileDetails(context, profileinfoOutput.filescnt);
                        files.add(0, "ADD");
                        fileImage.add(0, "ADD");
                        adapter = new PhotoAdapter(context, files, fileImage);
                        iv_gridView.setAdapter(adapter);

                    } else if (typeValue.equals("video")) {
                        filesData.clear();
                        fileData.clear();

                        for (int i = 0; i < profileinfoOutput.videos_thumb.size(); i++) {
                            //    fileData.add(new NewVideoType());
                           /* NewVideoType newVideoType = new NewVideoType();
                            newVideoType.type = profileinfoOutput.videos_thumb.get(i);
                            newVideoType.url = profileinfoOutput.videos.get(i);
                            newVideoType.stringType = "ADD";
                            fileData.add(newVideoType);*/

                            filesData.add(profileinfoOutput.videos_thumb.get(i));
                        }

                        for (int j = 0; j < profileinfoOutput.videos.size(); j++) {
                            fileData.add(profileinfoOutput.videos.get(j));
                        }

                        //  Library.putUserProfileDetails(context, profileinfoOutput.filescnt);
                        filesData.add(0, "ADD");
                        fileData.add(0, "ADD");
                        //   fileData.add(0, new NewVideoType());
                        // fileData.add(0,new NewVideoType());
                        adapterVideo = new VideoAdapter(context, filesData, fileData);
                        iv_gridView.setAdapter(adapterVideo);
                    } else if (typeValue.equals("audio")) {
                        fileAudio.clear();

                        for (int k = 0; k < profileinfoOutput.voice.size(); k++) {
                            fileAudio.add(profileinfoOutput.voice.get(k));
                        }

                        fileAudio.add(0, "ADD");
                        voiceAdapter = new VoiceAdapter(context, fileAudio);
                        iv_gridView.setAdapter(voiceAdapter);

                    } else if (typeValue.equals("files")) {
                        filesDoc.clear();
                        filesDocThumb.clear();

                        for (int i = 0; i < profileinfoOutput.documents.size(); i++) {
                            //    fileData.add(new NewVideoType());
                           /* NewVideoType newVideoType = new NewVideoType();
                            newVideoType.type = profileinfoOutput.videos_thumb.get(i);
                            newVideoType.url = profileinfoOutput.videos.get(i);
                            newVideoType.stringType = "ADD";
                            fileData.add(newVideoType);*/

                            filesDoc.add(profileinfoOutput.documents.get(i));
                        }

                        for (int j = 0; j < profileinfoOutput.documents_thumb.size(); j++) {
                            filesDocThumb.add(profileinfoOutput.documents_thumb.get(j));
                        }

                        //  Library.putUserProfileDetails(context, profileinfoOutput.filescnt);
                        filesDoc.add(0, "ADD");
                        filesDocThumb.add(0, "ADD");
                        //   fileData.add(0, new NewVideoType());
                        // fileData.add(0,new NewVideoType());
                        fileAdapter = new FileAdapter(context, filesDoc, filesDocThumb);
                        iv_gridView.setAdapter(fileAdapter);


                    }
                } else {
                    Library.alert(context, profileinfoOutput.message);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                closeProgress();
//                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void closeProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    private int getImageCount(String type) {

        int imgMAXCount = 1;
        SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        int imageCount = sharedpreferences.getInt("imageCount", 0);
        int videoCount = sharedpreferences.getInt("videoCount", 0);

        if (type.equals("Photo") && videoCount != 0) {
            imgMAXCount = 1000;
        } else if (type.equals("Video") && imageCount != 0) {
            imgMAXCount = 1000;
        }
        imgMAXCount = imgMAXCount - imageCount;
        imgMAXCount = imgMAXCount - videoCount;

        return imgMAXCount;
    }


    private void cameraIntent() {

        File saveDir = null;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            saveDir = new File(Environment.getExternalStorageDirectory(), "CastivateFiles");
            saveDir.mkdirs();
        }

        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .stillShot()
                .labelConfirm(R.string.mcam_use_stillshot);

        materialCamera.start(TAKE_PICTURE_REQUEST);
    }

    ArrayList<File> uploadFileList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            uploadFileList = new ArrayList<File>();
            final File file = new File(data.getData().getPath());
            uploadFileList.add(file);
            new DownloadFromURL().execute();

        } else if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK) {

            getImageVideoList();

        } else if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_OK) {

            uploadFileList = new ArrayList<File>();

            File file = new File(data.getData().getPath());
            uploadFileList.add(file);

            File filesDir = context.getFilesDir();
            File imageFile = new File(filesDir, "Thumb" + "_" + System.currentTimeMillis() + ".png");

            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
            File s = saveBitmap(bitmap, imageFile.getAbsolutePath());

            uploadFileList.add(s);

            new DownloadFromURL().execute();

        } else if (requestCode == PICK_VIDEO_MULTIPLE && resultCode == RESULT_OK) {
            getImageVideoList();
        } else if (requestCode == TAKE_VOICE_REQUEST && resultCode == RESULT_OK) {
            getImageVideoList();
        } else if (requestCode == DROPBOX && resultCode == RESULT_OK) {
            getImageVideoList();
        } else if (requestCode == GOOGLEDRIVE && resultCode == RESULT_OK) {
            getImageVideoList();
        } else if (requestCode == ONEDRIVE && resultCode == RESULT_OK) {
            getImageVideoList();
        } else if (requestCode == COUNT && resultCode == RESULT_OK) {
            getImageVideoList();
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

                    new DownloadFromURL().execute();
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


    /* private void recordVideo() {

         File saveDir = null;

         if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
             // Only use external storage directory if permission is granted, otherwise cache directory is used by default
             saveDir = new File(Environment.getExternalStorageDirectory(), "CastivateFiles");
             saveDir.mkdirs();
         }

         MaterialCamera materialCamera = new MaterialCamera(this)
                 .saveDir(saveDir)
                 .showPortraitWarning(false)
                 .allowRetry(true)
                 .defaultToFrontFacing(true);
              //   .labelConfirm(R.string.mcam_use_video);
         materialCamera.start(TAKE_VIDEO_REQUEST);
     }*/
    private void recordVideo() {

       /* File saveDir = null;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            saveDir = new File(Environment.getExternalStorageDirectory(), "CastivateFiles");
            saveDir.mkdirs();
        } else {
            saveDir = new File(Environment.getExternalStorageDirectory(), "CastivateFiles");
            saveDir.mkdirs();
        }


        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .showPortraitWarning(false)
                .allowRetry(true)
                .defaultToFrontFacing(true)
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
        startActivityForResult(intent, 890);

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

            if (!Status.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(Status);
                    int code = jsonObject.getInt("status");
                    String message = jsonObject.getString("message");
//                Library.alert(context, message);
                    alert(code, message);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Library.alert(context, "Something went wrong. Try again.");
            }

            //     getImageVideoList();
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

    @Override
    public void onBackPressed() {
        //    super.onBackPressed();
        if (Library.castivateAudio.equals("") && Library.castivatePhoto.equals("") && Library.castivateVideo.equals("")) {

            deleteCache(context);

                   /* Intent intent = new Intent(NewPhotoUpload.this, PhotosVideoFileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();*/

            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();

        } else {
            deleteCache(context);
            finish();
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

                    getImageVideoList();
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


}
