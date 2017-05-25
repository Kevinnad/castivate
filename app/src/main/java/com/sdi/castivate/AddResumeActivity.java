package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialcamera.MaterialCamera;
import com.sdi.castivate.adapter.HorizontalListViewAdapter;
import com.sdi.castivate.model.DeleteFileInput;
import com.sdi.castivate.model.DeleteFileOutput;
import com.sdi.castivate.model.FileModel;
import com.sdi.castivate.model.NewDeleteFileInput;
import com.sdi.castivate.model.NewProfileinfoOutput;
import com.sdi.castivate.model.ProfileinfoInput;
import com.sdi.castivate.model.ProfileinfoOutput;
import com.sdi.castivate.utils.HorizontalListView;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.MarshmallowPermission;
import com.sdi.castivate.utils.MultipartUtility;
import com.sdi.castivate.utils.RegisterRemoteApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nijamudhin on 3/1/2017.
 */

public class AddResumeActivity extends Activity {
    TextView txtResume, casting_login_back;
    ImageView add_icon, iv_delete;
    Button btnCamera, btnImageGallery, btnVideo, btnVideoGallery, btnCancel;
    HorizontalListView horizontalListView;
    int ResumeUpoad = 900;
    Dialog dialogCamera;
    Context context;
    public String Status = "", resumeName;
    ProgressDialog pd, progress;
    MarshmallowPermission marshMallowPermission;
    private final int PICK_IMAGE_MULTIPLE = 111;
    private final int PICK_VIDEO_MULTIPLE = 2;
    private final int TAKE_PICTURE_REQUEST = 3;
    private final int TAKE_VIDEO_REQUEST = 4;
    public static ArrayList<FileModel> files;
    ArrayList<File> uploadFileList;
    public static HorizontalListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;
        txtResume = (TextView) findViewById(R.id.txtResume);
        casting_login_back = (TextView) findViewById(R.id.casting_login_back);
        add_icon = (ImageView) findViewById(R.id.add_icon);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        horizontalListView = (HorizontalListView) findViewById(R.id.hlvSimpleList);
        marshMallowPermission = new MarshmallowPermission(this);
        getImageVideoList();

        txtResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtResume.getText().toString().equals("Update Resume")) {

                } else {
                    Intent intent = new Intent(AddResumeActivity.this, ResumeUpload.class);
                    intent.putExtra("CalledBy", "MyProfile");
                    startActivityForResult(intent, ResumeUpoad);
                }
            }
        });

        add_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageAlert();
            }
        });

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resumeName != null)
                    deletePhoto(resumeName);
            }
        });

        casting_login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    startActivity(new Intent(AddResumeActivity.this,PhotosVideoFileActivity.class));
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ResumeUpoad && resultCode == Activity.RESULT_OK) {
            {
                String message = data.getStringExtra("resumeName");
                txtResume.setText(message);
                if (!message.equals("")) {
                    iv_delete.setVisibility(View.VISIBLE);
                }

            }
        } else if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            uploadFileList = new ArrayList<File>();
            final File file = new File(data.getData().getPath());
            uploadFileList.add(file);
            new AddResumeActivity.DownloadFromURL().execute();

        } else if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK) {

            getImageVideoList();

        } else if (requestCode == PICK_VIDEO_MULTIPLE && resultCode == RESULT_OK) {

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

            new AddResumeActivity.DownloadFromURL().execute();

        }
    }


    private void showImageAlert() {
        dialogCamera = new Dialog(context);
        dialogCamera.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogCamera.setContentView(R.layout.profile_video_alert);
        dialogCamera.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogCamera.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogCamera.show();

        btnCamera = (Button) dialogCamera.findViewById(R.id.btnCamera);
        btnImageGallery = (Button) dialogCamera.findViewById(R.id.btnImageGallery);
        btnVideo = (Button) dialogCamera.findViewById(R.id.btnVideo);
        btnVideoGallery = (Button) dialogCamera.findViewById(R.id.btnVideoGallery);
        btnCancel = (Button) dialogCamera.findViewById(R.id.btnCancel);

        /* Camera button click Method */
        btnCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogCamera.dismiss();
                int imgMAXCount = getImageCount("Photo");
                if (imgMAXCount != 0) {
                    if (marshMallowPermission.checkPermissionForCamera()) {
                        cameraIntent();
                    } else {
                        marshMallowPermission.requestPermissionForCamera();
                    }

                } else {
                    Library.alert(context, "Your limit is exceeded.");
                }


            }
        });

        /* Gallery button click Method */
        btnImageGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogCamera.dismiss();

                int imgMAXCount = getImageCount("Photo");

                if (imgMAXCount != 0) {

                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
                        Intent intent = new Intent(context, CastingCustomPhotoGallery.class);
                        intent.setType("image*//*");
                        intent.putExtra("update_count", imgMAXCount);
                        startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
                    } else {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }

                } else {
                    Library.alert(context, "Your limit is exceeded.");
                }

            }

        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCamera.dismiss();

                int imgMAXCount = getImageCount("Video");

                if (imgMAXCount != 0) {
                    if (marshMallowPermission.checkPermissionForCamera()) {
                        recordVideo();
                    } else {
                        marshMallowPermission.requestPermissionForCamera();
                    }

                } else {
                    Library.alert(context, "Your limit is exceeded.");
                }
            }
        });

        btnVideoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCamera.dismiss();
                int imgMAXCount = getImageCount("Video");

                if (imgMAXCount != 0) {
                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
                        Intent intent = new Intent(context, CastingCustomVideoGallery.class);
                        intent.setType("video/*");
                        intent.putExtra("update_count", imgMAXCount);
                        startActivityForResult(intent, PICK_VIDEO_MULTIPLE);
                    } else {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }
                } else {
                    Library.alert(context, "Your limit is exceeded.");
                }

            }
        });

        /* Cancel button click Method */
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogCamera.dismiss();
            }
        });

    }

    private int getImageCount(String type) {

        int imgMAXCount = 19;
        SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        int imageCount = sharedpreferences.getInt("imageCount", 0);
        int videoCount = sharedpreferences.getInt("videoCount", 0);

        if (type.equals("Photo") && videoCount != 0) {
            imgMAXCount = 20;
        } else if (type.equals("Video") && imageCount != 0) {
            imgMAXCount = 20;
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

    private void recordVideo() {

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
                .defaultToFrontFacing(true)
                .labelConfirm(R.string.mcam_use_video);
        materialCamera.start(TAKE_VIDEO_REQUEST);
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

                if (profileinfoOutput.status == 200) {
                    files = new ArrayList<FileModel>();
                    int videoCount = profileinfoOutput.videos.size();
                    int imageCount = profileinfoOutput.images.size();
                    int resumeCount = profileinfoOutput.documents.size();
                    int voiceCount = profileinfoOutput.voice.size();
                    int totalCount = videoCount + imageCount;

                    SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("imageCount", imageCount);
                    editor.putInt("videoCount", videoCount);
                    editor.putInt("resumeCount", resumeCount);
                    editor.putInt("audioCount", voiceCount);
                    editor.putInt("totalCount", totalCount);

                    editor.commit();

                    for (int i = 0; i < profileinfoOutput.documents.size(); i++) {
                        if (profileinfoOutput.documents.get(i) != null) {

                            resumeName = profileinfoOutput.documents.get(i).toString();
                            int pos = resumeName.lastIndexOf("/");
                            pos = pos + 1;
                            resumeName = resumeName.substring(pos, resumeName.length());
                            txtResume.setText(resumeName);
                            iv_delete.setVisibility(View.VISIBLE);
                        } /*else {
                                files.add(profileinfoOutput.files.get(i));
                            }*/
                    }


                      /*  Library.putUserProfileDetails(context, profileinfoOutput.filescnt);

                        adapter = new HorizontalListViewAdapter(context, files);
                        horizontalListView.setAdapter(adapter);*/

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

    private void deletePhoto(final String pos) {
        // TODO Auto-generated method stub

        // Progress Dialog
        pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        try {


            NewDeleteFileInput input = new NewDeleteFileInput(Library.getUserId(context), resumeName);

            RegisterRemoteApi.getInstance().setNewDeleteInput(input);

            // Call Login JSON
            RegisterRemoteApi.getInstance().getNewDeleteInput(context, new Callback<DeleteFileOutput>() {
                @Override
                public void success(DeleteFileOutput profileinfoOutput, Response response) {

                    if (profileinfoOutput.status == 200) {
                        closeProgress();
                        //        Library.reduceUserProfileDetails(context, Library.doc_id);
                        txtResume.setText("Update Resume");
                        iv_delete.setVisibility(View.GONE);

                        SharedPreferences settings = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = settings.edit();

                        editor1.remove("resumeCount");
                        editor1.commit();

                        // MyProfile.files.remove(pos);
                        // MyProfile.adapter.notifyDataSetChanged();


                    } else {
                        closeProgress();
                        Library.alert(context, profileinfoOutput.message);

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


    public class DownloadFromURL extends AsyncTask<String, String, String> {


        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(context);
            progress.setMessage("Loading ...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        public String doInBackground(String... params) {
            uploadfile();
            return null;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            if ((progress != null) && progress.isShowing()) {
                progress.dismiss();
            }

            getImageVideoList();
        }
    }

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

}
