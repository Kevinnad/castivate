package com.sdi.castivate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.sdi.castivate.model.DataPathModel;
import com.sdi.castivate.model.fileUrlModel;
import com.sdi.castivate.utils.BitmapUtil;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.Utility;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by twilightuser on 1/9/16.
 */
@SuppressWarnings({"deprecation", "unchecked"})
@SuppressLint({"ResourceAsColor", "InlinedApi", "ShowToast", "UseSparseArrays"})
public class CastingFileUpload extends Activity {

    private final static int PERMISSION_RQ = 84;
    public static ArrayList<DataPathModel> listPictures = new ArrayList<DataPathModel>();
    private final int PICK_IMAGE_MULTIPLE = 1;
    private final int PICK_VIDEO_MULTIPLE = 2;
    private final int TAKE_PICTURE_REQUEST = 3;
    private final int TAKE_VIDEO_REQUEST = 4;
    boolean fromcasting = false;
    ProgressDialog pDialog;
    Context context;
    private ImageView imageViewOne, imageViewTwo, imageViewThree, imageViewFour,
            videoViewOne, videoViewTwo, videoViewThree, videoViewFour;
    private ImageView delImageViewOne, delImageViewTwo, delImageViewThree, delImageViewFour,
            delVideoViewOne, delVideoViewTwo, delVideoViewThree, delVideoViewFour;
    private ImageView VideoViewIconOne, VideoViewIconTwo, VideoViewIconThree, VideoViewIconFour;
    private LinearLayout addPhoto, addVideo;
    private String photoChoose, videoChoose;
    private ArrayList<fileUrlModel> videoUrls = new ArrayList<fileUrlModel>();
    private ArrayList<fileUrlModel> videoThumbnailsUrls = new ArrayList<fileUrlModel>();
    private String video_path, image_path;
    private LinearLayout casting_file_upload_back_icon;
    private TextView casting_file_upload_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_file_upload);

        context = this;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission to save videos in external storage
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RQ);
        }
        //Add photo and video action
        addPhoto = (LinearLayout) findViewById(R.id.add_Photo);
        addVideo = (LinearLayout) findViewById(R.id.add_Video);

        //Photo upload image view
        imageViewOne = (ImageView) findViewById(R.id.imageViewOne);
        imageViewTwo = (ImageView) findViewById(R.id.imageViewTwo);
        imageViewThree = (ImageView) findViewById(R.id.imageViewThree);
        imageViewFour = (ImageView) findViewById(R.id.imageViewFour);

        //Video upload image view
        videoViewOne = (ImageView) findViewById(R.id.videoViewOne);
        videoViewTwo = (ImageView) findViewById(R.id.videoViewTwo);
        videoViewThree = (ImageView) findViewById(R.id.videoViewThree);
        videoViewFour = (ImageView) findViewById(R.id.videoViewFour);

        //Delete Photo upload image view
        delImageViewOne = (ImageView) findViewById(R.id.delImageViewOne);
        delImageViewTwo = (ImageView) findViewById(R.id.delImageViewTwo);
        delImageViewThree = (ImageView) findViewById(R.id.delImageViewThree);
        delImageViewFour = (ImageView) findViewById(R.id.delImageViewFour);

        //Delete Video upload image view
        delVideoViewOne = (ImageView) findViewById(R.id.delVideoViewOne);
        delVideoViewTwo = (ImageView) findViewById(R.id.delVideoViewTwo);
        delVideoViewThree = (ImageView) findViewById(R.id.delVideoViewThree);
        delVideoViewFour = (ImageView) findViewById(R.id.delVideoViewFour);

        // Video icon display for video image
        VideoViewIconOne = (ImageView) findViewById(R.id.VideoViewIconOne);
        VideoViewIconTwo = (ImageView) findViewById(R.id.VideoViewIconTwo);
        VideoViewIconThree = (ImageView) findViewById(R.id.VideoViewIconThree);
        VideoViewIconFour = (ImageView) findViewById(R.id.VideoViewIconFour);

        //Invisible video icon display for video image
        VideoViewIconOne.setVisibility(View.INVISIBLE);
        VideoViewIconTwo.setVisibility(View.INVISIBLE);
        VideoViewIconThree.setVisibility(View.INVISIBLE);
        VideoViewIconFour.setVisibility(View.INVISIBLE);

        //Invisible delete image icon for photo upload
        delImageViewOne.setVisibility(View.INVISIBLE);
        delImageViewTwo.setVisibility(View.INVISIBLE);
        delImageViewThree.setVisibility(View.INVISIBLE);
        delImageViewFour.setVisibility(View.INVISIBLE);

        //Invisible delete image icon for video upload
        delVideoViewOne.setVisibility(View.INVISIBLE);
        delVideoViewTwo.setVisibility(View.INVISIBLE);
        delVideoViewThree.setVisibility(View.INVISIBLE);
        delVideoViewFour.setVisibility(View.INVISIBLE);

        //Casting file upload back
        casting_file_upload_back_icon = (LinearLayout) findViewById(R.id.casting_file_upload_back_icon);
        casting_file_upload_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

               /* Intent intent = new Intent(CastingFileUpload.this, FavoriteScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);*/
            }
        });

        //Casting file upload done.
        casting_file_upload_done = (TextView) findViewById(R.id.casting_file_upload_done);
        casting_file_upload_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listPictures.size() > 0 && videoUrls.size() > 0) {

                    casting_file_upload_done.setEnabled(false);
                    new RetriveDataAsyn().execute();

                } else {
                    casting_file_upload_done.setEnabled(true);

                    Library.alert(context, "Please upload atleast one photo and video");

//                    Toast.makeText(CastingFileUpload.this, "Minimum one photo and video file can be upload. ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Add photo choose Gallery or Capture
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Take Photo", "Photo Library", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(CastingFileUpload.this);
                builder.setTitle("Add Photo");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(CastingFileUpload.this);

                        if (items[item].equals("Take Photo")) {
                            photoChoose = "Take Photo";
                            /*if (result)*/
                            cameraIntent();

                        } else if (items[item].equals("Photo Library")) {
                            photoChoose = "Photo Library";
                            if (result) photoGallery();

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        //Add video choose Gallery or Record
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Take Video", "Photo Library", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(CastingFileUpload.this);
                builder.setTitle("Add Video");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(CastingFileUpload.this);

                        if (items[item].equals("Take Video")) {
                            videoChoose = "Take Video";
                            /*if (result)*/
                            recordVideo();

                        } else if (items[item].equals("Photo Library")) {
                            videoChoose = "Photo Library";
                            if (result) videoGallery();

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        //Remove video position for photo view image icon
        delImageViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage(0);
            }
        });
        delImageViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage(1);
            }
        });
        delImageViewThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage(2);
            }
        });
        delImageViewFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage(3);
            }
        });


        //Remove video position for video view image icon
        delVideoViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVideo(0);
            }
        });
        delVideoViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVideo(1);
            }
        });
        delVideoViewThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVideo(2);
            }
        });
        delVideoViewFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVideo(3);
            }
        });

        Intent in = getIntent();
        if (in.hasExtra("FromCasting")) {

            fromcasting = in.getBooleanExtra("FromCasting", false);
            if (fromcasting) {

                if (listPictures != null) listPictures.clear();
                if (videoUrls != null) videoUrls.clear();

                String churl = android.os.Environment.getExternalStorageDirectory().toString();
                File zipPath = new File(churl, "Casting_Folder");

                if (zipPath.exists()) {
                    deleteRecursive(zipPath);
                }

            }
        }
    }

    private void photoGallery() {


        Intent intent = new Intent(CastingFileUpload.this, CastingCustomPhotoGallery.class);
        intent.setType("image/*");
        intent.putExtra("update_count", listPictures.size());
        startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
    }

    private void videoGallery() {
        Intent intent = new Intent(CastingFileUpload.this, CastingCustomVideoGallery.class);
        intent.setType("video/*");
        intent.putExtra("update_count", videoUrls.size());
        startActivityForResult(intent, PICK_VIDEO_MULTIPLE);
    }

    private void cameraIntent() {

        File saveDir = null;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // Sample was denied WRITE_EXTERNAL_STORAGE permission
            Toast.makeText(this, "Videos will be saved in a cache directory instead of an external storage directory since permission was denied.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode == RESULT_OK && requestCode == TAKE_VIDEO_REQUEST) {
//            final File file = new File(data.getData().getPath());
//            video_path = file.getAbsolutePath();
//            saveVideoImage();
//        }
        if (resultCode == RESULT_OK) {

            if (requestCode == PICK_IMAGE_MULTIPLE) {

                addImages();
            }

            if (requestCode == PICK_VIDEO_MULTIPLE) {
                String[] videosPath = data.getStringExtra("data").split("\\|");
                onSelectFromVideoGalleryResult(videosPath);
            }

            if (requestCode == TAKE_VIDEO_REQUEST) {
                final File file = new File(data.getData().getPath());
                video_path = file.getAbsolutePath();
                saveVideoImage();
            }

            if (requestCode == TAKE_PICTURE_REQUEST) {
                final File file = new File(data.getData().getPath());
                image_path = file.getAbsolutePath();
                saveImagePath();
            }
        }
    }

    private void saveImagePath() {
        fileUrlModel imageUrl = new fileUrlModel();
        imageUrl.setFileUrl(image_path);

        DataPathModel addimage = new DataPathModel();

        addimage.setArrPath(image_path);

        try {
            File imgFile = new File(image_path);
            if (imgFile.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                Matrix matrix = new Matrix();

                matrix.postRotate(0);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth(), myBitmap.getHeight(), true);

                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

//For Rotation new added
                int rotation = BitmapUtil.getExifOrientation(image_path);

                if (rotatedBitmap != null) {

                    Matrix matrix1 = new Matrix();
                    matrix1.setRotate(rotation);
                    rotatedBitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0,
                            rotatedBitmap.getWidth(), rotatedBitmap.getHeight(),
                            matrix1, true);
                }

                addimage.setImagebitmap(rotatedBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        listPictures.add(addimage);
        addImages();
    }

    private void saveVideoImage() {
        fileUrlModel videoUrl = new fileUrlModel();
        videoUrl.setFileUrl(video_path);
        videoUrls.add(videoUrl);

        //videoThumbnails();
        showVideoThumbnail();
    }

    private void addImages() {

        if (listPictures.size() < 4) addPhoto.setEnabled(true);
        else addPhoto.setEnabled(false);

        for (int i = 0; i < listPictures.size(); i++) {
            try {
                switch (i) {
                    case 0:
                        if (listPictures.get(0).getArrPath() != null) {
                            imageViewOne.setImageBitmap(listPictures.get(0).getImagebitmap());
                            delImageViewOne.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 1:
                        if (listPictures.get(1).getArrPath() != null) {
                            imageViewTwo.setImageBitmap(listPictures.get(1).getImagebitmap());
                            delImageViewTwo.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 2:
                        if (listPictures.get(2).getArrPath() != null) {
                            imageViewThree.setImageBitmap(listPictures.get(2).getImagebitmap());
                            delImageViewThree.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 3:
                        if (listPictures.get(3).getArrPath() != null) {
                            imageViewFour.setImageBitmap(listPictures.get(3).getImagebitmap());
                            delImageViewFour.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private void onSelectFromGalleryResult(String[] imagesPath) {
//
//        List<String> imageList = Arrays.asList(imagesPath);
//
//        for (String s : imageList) {
//            fileUrlModel imageUrl = new fileUrlModel();
//            imageUrl.setFileUrl(s);
//            imageUrls.add(imageUrl);
//        }
//        addImages();
//    }

    private void removeImage(int arrayId) {

        if (listPictures.size() >= arrayId) listPictures.remove(arrayId);

        imageViewOne.setImageBitmap(null);
        imageViewTwo.setImageBitmap(null);
        imageViewThree.setImageBitmap(null);
        imageViewFour.setImageBitmap(null);

        delImageViewOne.setVisibility(View.GONE);
        delImageViewTwo.setVisibility(View.GONE);
        delImageViewThree.setVisibility(View.GONE);
        delImageViewFour.setVisibility(View.GONE);

        addImages();
    }

    private void onSelectFromVideoGalleryResult(String[] videosPath) {

        List<String> videoList = Arrays.asList(videosPath);

        for (String s : videoList) {
            fileUrlModel videoUrl = new fileUrlModel();
            videoUrl.setFileUrl(s);
            videoUrls.add(videoUrl);
        }

        showVideoThumbnail();
        //videoThumbnails();

    }

    private void showVideoThumbnail() {

        if (videoUrls.size() < 4) addVideo.setEnabled(true);
        else addVideo.setEnabled(false);

        for (int i = 0; i < videoUrls.size(); i++) {
            try {
                switch (i) {
                    case 0:
                        if (videoUrls.get(0) != null) {
                            Bitmap bitmapt = ThumbnailUtils.createVideoThumbnail(videoUrls.get(0).getFileUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                            videoViewOne.setImageBitmap(bitmapt);
                            videoUrls.get(0).setThumbnail(bitmapt);
                            delVideoViewOne.setVisibility(View.VISIBLE);
                            VideoViewIconOne.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 1:
                        if (videoUrls.get(1) != null) {
                            Bitmap bitmapt = ThumbnailUtils.createVideoThumbnail(videoUrls.get(1).getFileUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                            videoViewTwo.setImageBitmap(bitmapt);
                            videoUrls.get(1).setThumbnail(bitmapt);
                            delVideoViewTwo.setVisibility(View.VISIBLE);
                            VideoViewIconTwo.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 2:
                        if (videoUrls.get(2) != null) {
                            Bitmap bitmapt = ThumbnailUtils.createVideoThumbnail(videoUrls.get(2).getFileUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                            videoViewThree.setImageBitmap(bitmapt);
                            videoUrls.get(2).setThumbnail(bitmapt);
                            delVideoViewThree.setVisibility(View.VISIBLE);
                            VideoViewIconThree.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 3:
                        if (videoUrls.get(3) != null) {
                            Bitmap bitmapt = ThumbnailUtils.createVideoThumbnail(videoUrls.get(3).getFileUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                            videoViewFour.setImageBitmap(bitmapt);
                            videoUrls.get(3).setThumbnail(bitmapt);
                            delVideoViewFour.setVisibility(View.VISIBLE);
                            VideoViewIconFour.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void removeVideo(int arrayId) {

        videoUrls.remove(arrayId);

       /* System.out.println("videoThumbnailsUrls Delete : "+videoThumbnailsUrls.get(arrayId).getFileUrl());

        java.io.File videoThumbnailsUrlsRemove = new java.io.File(videoThumbnailsUrls.get(arrayId).getFileUrl());
        videoThumbnailsUrlsRemove.delete();
        videoThumbnailsUrls.remove(arrayId);*/


        videoViewOne.setImageBitmap(null);
        videoViewTwo.setImageBitmap(null);
        videoViewThree.setImageBitmap(null);
        videoViewFour.setImageBitmap(null);

        delVideoViewOne.setVisibility(View.INVISIBLE);
        delVideoViewTwo.setVisibility(View.INVISIBLE);
        delVideoViewThree.setVisibility(View.INVISIBLE);
        delVideoViewFour.setVisibility(View.INVISIBLE);

        VideoViewIconOne.setVisibility(View.INVISIBLE);
        VideoViewIconTwo.setVisibility(View.INVISIBLE);
        VideoViewIconThree.setVisibility(View.INVISIBLE);
        VideoViewIconFour.setVisibility(View.INVISIBLE);

        showVideoThumbnail();
    }

    public class RetriveDataAsyn extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(CastingFileUpload.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Please wait..");
            pDialog.show();
            pDialog.setCancelable(false);

        }

        @Override
        protected Void doInBackground(Void... params) {

            String churl = android.os.Environment.getExternalStorageDirectory().toString();
            File chfile = new File(churl, "Casting_Folder/Casting_Resume");

            if (!chfile.exists()) {
                chfile.mkdir();
            }


            for (int i = 0; i < listPictures.size(); i++) {

                String extension = FilenameUtils.getExtension(listPictures.get(i).getArrPath());


                File arrfile = new File(listPictures.get(i).getArrPath());
                if (arrfile.exists()) {
                    String desturl = android.os.Environment.getExternalStorageDirectory().toString() + "/Casting_Folder/Casting_Resume/Image0" + i + "." + extension;
                    File destfile = new File(desturl);

                    try {
                        FileUtils.copyFile(arrfile, destfile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                FileOutputStream out = null;
                try {
                    String desturl = android.os.Environment.getExternalStorageDirectory().toString() + "/Casting_Folder/ThumbPic0" + i + "." + "png";
                    File destfile = new File(desturl);
                    Bitmap bitmap = listPictures.get(i).getImagebitmap();
                    out = new FileOutputStream(destfile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            for (int i = 0; i < videoUrls.size(); i++) {

                String extension = FilenameUtils.getExtension(videoUrls.get(i).getFileUrl());

                File arrfile = new File(videoUrls.get(i).getFileUrl());
                if (arrfile.exists()) {
                    String desturl = android.os.Environment.getExternalStorageDirectory().toString() + "/Casting_Folder/Casting_Resume/Video0" + i + "." + extension;
                    File destfile = new File(desturl);

                    try {
                        FileUtils.copyFile(arrfile, destfile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                FileOutputStream out = null;
                try {
                    String desturl = android.os.Environment.getExternalStorageDirectory().toString() + "/Casting_Folder/ThumbVideo0" + i + "." + "png";
                    File destfile = new File(desturl);
                    Bitmap bitmap = videoUrls.get(i).getThumbnail();
                    out = new FileOutputStream(destfile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog != null) pDialog.dismiss();

            String calledBy = "";
            if (getIntent().hasExtra("CalledBy")) {
                calledBy = getIntent().getStringExtra("CalledBy");
            }
            Intent intent = new Intent(CastingFileUpload.this, ResumeUpload.class);
            intent.putExtra("CalledBy", calledBy);
            startActivity(intent);

        }
    }
}
