package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.chooser.android.DbxChooser;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.microsoft.onedrivesdk.picker.IPicker;
import com.microsoft.onedrivesdk.picker.IPickerResult;
import com.microsoft.onedrivesdk.picker.LinkType;
import com.microsoft.onedrivesdk.picker.Picker;
import com.microsoft.onedrivesdk.saver.ISaver;
import com.sdi.castivate.googledriveutils.ApiClientAsyncTask;
import com.sdi.castivate.googledriveutils.DownloadFileFromURL;
import com.sdi.castivate.googledriveutils.FileCacheForService;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.MultipartUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by nijamudhin on 4/21/2017.
 */

public class CastingImageUploadProfile extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final String TAG = "Google Drive Activity";
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_CODE_OPENER = 900;
    private static final int DBX_CHOOSER_REQUEST = 901;
    /**
     * Handle result of Created file
     */
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (result.getStatus().isSuccess()) {
                        Toast.makeText(getApplicationContext(), "file created: " + "" +
                                result.getDriveFile().getDriveId(), Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            };
    public DriveFile file;
    /*Google Drive*/
    RelativeLayout resume_upload;
    Context context;
    String fileName;
    String flag = "";


    String fileType;
    final private ResultCallback<DriveResource.MetadataResult> metadataCallback = new ResultCallback<DriveResource.MetadataResult>() {
        @Override
        public void onResult(DriveResource.MetadataResult result) {
            if (!result.getStatus().isSuccess()) {
                System.out.println("Problem while trying to fetch metadata");
                return;
            }
            Metadata metadata = result.getMetadata();
            fileName = metadata.getTitle();
            fileType = metadata.getFileExtension();
            System.out.println("Metadata successfully fetched. Title: "
                    + metadata.getTitle());
            System.out
                    .println("Metadata successfully fetched. getFileExtension: "
                            + metadata.getFileExtension());
        }
    };
    boolean isDrive;
    DbxChooser mChooser;
    RelativeLayout layShowFileName;
    TextView txtFileName;
    TextView casting_resume_upload_done;
    ImageView imgRemove;
    File dataPath;
    File zipPath;
    ProgressDialog pDialog;
    RelativeLayout audio_rl;
    LinearLayout casting_resume_upload_back_icon;
    public String selectedFilepath = "", type = "", typeAudio = "", typeVideo = "";
    private File f;
    private boolean mLoggedIn, onResume;
    private DropboxAPI<AndroidAuthSession> mApi;
    /*OneDrive*/
    private IPicker mPicker;
    private ISaver mSaver;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onResume() {
        super.onResume();
        if (isDrive) {
            if (mGoogleApiClient.isConnected()) {
                OpenFileFromGoogleDrive();
            } else {
                if (mGoogleApiClient == null) {
                    /**
                     * Create the API client and bind it to an instance variable.
                     * We use this instance as the callback for connection and connection failures.
                     * Since no account name is passed, the user is prompted to choose.
                     */
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addApi(Drive.API)
                            .addScope(Drive.SCOPE_FILE)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
                }
                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                    isDrive = true;
                }
            }
        }


        if (!flag.equals("back")) {
            finish();
        }

//
//        if ((type.equals("dropbox") || (type.equals("googledrive")) || (type.equals("onedrive"))) || (typeVideo.equals("dropbox") || (typeVideo.equals("googledrive")) || (typeVideo.equals("onedrive"))) || (typeAudio.equals("dropbox") || (typeAudio.equals("googledrive")) || (typeAudio.equals("onedrive")))) {
//
//        } else {
//            finish();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            // disconnect Google API client connection
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_image_upload);
        context = this;
        audio_rl = (RelativeLayout) findViewById(R.id.audio_rl);
        flag = "";
        resume_upload = (RelativeLayout) findViewById(R.id.resume_upload);
        layShowFileName = (RelativeLayout) findViewById(R.id.layShowFileName);
        txtFileName = (TextView) findViewById(R.id.txtFileName);
        casting_resume_upload_done = (TextView) findViewById(R.id.casting_resume_upload_done);
        imgRemove = (ImageView) findViewById(R.id.imgRemove);
        casting_resume_upload_back_icon = (LinearLayout) findViewById(R.id.casting_resume_upload_back_icon);
        resume_upload.setOnClickListener(this);
        imgRemove.setOnClickListener(this);
        mChooser = new DbxChooser(getResources().getString(R.string.dropbox_app_key));
        String churl = android.os.Environment.getExternalStorageDirectory().toString();
        dataPath = new File(churl, "Casting_Folder");
        zipPath = new File(churl, "Casting_Folder");


        try {
            type = getIntent().getStringExtra("image");

            if (type.equals("dropbox")) {
                dropboxApi();
            } else if (type.equals("googledrive")) {
                googleDriveApi();
            } else if (type.equals("onedrive")) {
                oneDriveApi();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            typeVideo = getIntent().getStringExtra("video");

            if (typeVideo.equals("dropbox")) {
                dropboxApi();
            } else if (typeVideo.equals("googledrive")) {
                googleDriveApi();
            } else if (typeVideo.equals("onedrive")) {
                oneDriveApi();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            typeAudio = getIntent().getStringExtra("audio");

            if (typeAudio.equals("dropbox")) {
                dropboxApi();
            } else if (typeAudio.equals("googledrive")) {
                googleDriveApi();
            } else if (typeAudio.equals("onedrive")) {
                oneDriveApi();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        casting_resume_upload_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (layShowFileName.getVisibility() == View.VISIBLE) {

                    /*casting_resume_upload_done.setEnabled(false);

                    pDialog = new ProgressDialog(ResumeUpload.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    pDialog.setMessage("Please wait..");
                    pDialog.show();
                    pDialog.setCancelable(false);
                    *//*Zip the file*//*
                    if (ZipFile.zip(dataPath.getAbsolutePath(), zipPath.getAbsolutePath(), "Casting.zip", false)) {
                        if (dataPath.exists()) {
                            deleteRecursive(dataPath);
                        }
                        new callservice().execute();
                    }*/

                    new DownloadFromURL().execute();

                } else {

                    Library.alert(context, "Please upload");


                }

            }
        });

        casting_resume_upload_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);*/
                finish();
            }
        });

    }


    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resume_upload:

                if (type != null) {
                    if (type.equals("dropbox")) {
                        dropboxApi();
                    } else if (type.equals("googledrive")) {
                        googleDriveApi();
                    } else if (type.equals("onedrive")) {
                        oneDriveApi();
                    }
                }

                if (typeAudio != null) {
                    if (typeAudio.equals("dropbox")) {
                        dropboxApi();
                    } else if (typeAudio.equals("googledrive")) {
                        googleDriveApi();
                    } else if (typeAudio.equals("onedrive")) {
                        oneDriveApi();
                    }
                }

                if (typeVideo != null) {
                    if (typeVideo.equals("dropbox")) {
                        dropboxApi();
                    } else if (typeVideo.equals("googledrive")) {
                        googleDriveApi();
                    } else if (typeVideo.equals("onedrive")) {
                        oneDriveApi();
                    }
                }



               /* final CharSequence[] items = {"Dropbox", "Google Drive", "OneDrive", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(CastingImageUpload.this);
                builder.setTitle("Image upload");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Dropbox")) {
                            dropboxApi();
                        } else if (items[item].equals("Google Drive")) {
                            dialog.dismiss();
                            googleDriveApi();
                        } else if (items[item].equals("OneDrive")) {
                            oneDriveApi();
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();*/
                break;
            case R.id.imgRemove:
                layShowFileName.setVisibility(View.GONE);
                resume_upload.setVisibility(View.VISIBLE);
                txtFileName.setText("");

                if (!selectedFilepath.equals("")) {
                    File file = new File(selectedFilepath);
                    if (file.exists()) file.delete();
                    selectedFilepath = "";
                }
                break;
        }
    }

    private void dropboxApi() {

        flag = "back";
        mChooser.forResultType(DbxChooser.ResultType.PREVIEW_LINK)
                .launch(CastingImageUploadProfile.this, DBX_CHOOSER_REQUEST);
    }

    private void oneDriveApi() {

        flag = "back";
        mPicker = Picker.createPicker(getResources().getString(R.string.onedrive_client_id));
        mPicker.startPicking(this, LinkType.DownloadLink);
    }

    private void googleDriveApi() {

        flag = "back";
        if (mGoogleApiClient == null) {
            /**
             * Create the API client and bind it to an instance variable.
             * We use this instance as the callback for connection and connection failures.
             * Since no account name is passed, the user is prompted to choose.
             */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            isDrive = true;
        } else {
            OpenFileFromGoogleDrive();
            isDrive = false;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
//        Toast.makeText(getApplicationContext(), "GoogleApiClient connection failed", Toast.LENGTH_LONG).show();
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }

    }

    /**
     * It invoked when Google API client connected
     *
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
//        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
        OpenFileFromGoogleDrive();
    }

    /**
     * It invoked when connection suspend
     *
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Open list of folder and file of the Google Drive
     */
    public void OpenFileFromGoogleDrive() {

        if (typeVideo != null) {
            if (typeVideo.equals("googledrive")) {
                IntentSender intentSender = Drive.DriveApi
                        .newOpenFileActivityBuilder()
                /*.setMimeType(new String[]{"application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/msword", "application/vnd.google-apps.document"})*/
                        .setMimeType(new String[]{"video/mp4"})
                        .build(mGoogleApiClient);
                try {
                    startIntentSenderForResult(
                            intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.w(TAG, "Unable to send intent", e);
                }
            }

        } else if (typeAudio != null) {

            if (typeAudio.equals("googledrive")) {
                IntentSender intentSender = Drive.DriveApi
                        .newOpenFileActivityBuilder()
                /*.setMimeType(new String[]{"application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/msword", "application/vnd.google-apps.document"})*/
                        .setMimeType(new String[]{"audio/mpeg3", "audio/x-mpeg-3", "video/mpeg", "video/x-mpeg", "audio/mp3"})
                        .build(mGoogleApiClient);
                try {
                    startIntentSenderForResult(
                            intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.w(TAG, "Unable to send intent", e);
                }
            }
        } else {

            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                /*.setMimeType(new String[]{"application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/msword", "application/vnd.google-apps.document"})*/
                    .setMimeType(new String[]{"image/jpeg", "image/pjpeg", "image/png"})
                    .build(mGoogleApiClient);
            try {
                startIntentSenderForResult(
                        intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.w(TAG, "Unable to send intent", e);
            }
        }


    }

    /**
     * Handle Response of selected file
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        if (requestCode == REQUEST_CODE_OPENER) {
            if (resultCode == RESULT_OK) {
                DriveId driveId = (DriveId) data
                        .getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                System.out.println("Selected file's ID: " + driveId + " >>> "
                        + driveId.getResourceId());
                isDrive = false;
                new DownloadDriveFileAsyncTask(context).execute(driveId);
            } else if (resultCode == 0) {
                finish();
            } else {
                audio_rl.setVisibility(View.GONE);
                if (mGoogleApiClient != null) {

                    if (mGoogleApiClient.isConnected()) {
                        // Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);

                        mGoogleApiClient.clearDefaultAccountAndReconnect();
                        mGoogleApiClient.disconnect();
                        isDrive = false;

                    }
                }
            }
        } else if (requestCode == 61680) {
            if (resultCode == Activity.RESULT_OK) {
                final IPickerResult result = mPicker.getPickerResult(requestCode, resultCode, data);
                // Handle the case if nothing was picked

                if (result != null) {
                    // Do something with the picked file

                    final String name = result.getName();

                    String filenameArray[] = name.split("\\.");
                    String extension = filenameArray[filenameArray.length - 1];

                    if (typeAudio != null) {

                        if (extension.equals("mp3")) {
                            DownloadFileFromURL dw = new DownloadFileFromURL(context) {
                                @Override
                                protected void onPostExecute(String filePath) {
                                    // TODO Auto-generated method stub
                                    super.onPostExecute(filePath);
//                            Toast.makeText(context, result.getName(), Toast.LENGTH_SHORT).show();

                                    showFilename(result.getName(), filePath);
                                }
                            };


                            dw.execute(result.getLink().toString(), result.getName()
                                    .toString());
                        } else {
                            audio_rl.setVisibility(View.GONE);
                            showAlert("onedrive");
                        }

                    } else if (typeVideo != null) {

                        if (extension.equals("mp4")) {
                            DownloadFileFromURL dw = new DownloadFileFromURL(context) {
                                @Override
                                protected void onPostExecute(String filePath) {
                                    // TODO Auto-generated method stub
                                    super.onPostExecute(filePath);
//                            Toast.makeText(context, result.getName(), Toast.LENGTH_SHORT).show();

                                    showFilename(result.getName(), filePath);
                                }
                            };
                            dw.execute(result.getLink().toString(), result.getName()
                                    .toString());
                        } else {
                            audio_rl.setVisibility(View.GONE);
                            showAlert("onedrive");
                        }


                    } else {
                        if (extension.equals("jpeg") || extension.equals("jpg") || extension.equals("png")) {
                            DownloadFileFromURL dw = new DownloadFileFromURL(context) {
                                @Override
                                protected void onPostExecute(String filePath) {
                                    // TODO Auto-generated method stub
                                    super.onPostExecute(filePath);
//                            Toast.makeText(context, result.getName(), Toast.LENGTH_SHORT).show();

                                    showFilename(result.getName(), filePath);
                                }
                            };
                            dw.execute(result.getLink().toString(), result.getName()
                                    .toString());
                        } else {
                            audio_rl.setVisibility(View.GONE);
                            showAlert("onedrive");
                        }
                    }

                    return;
                }
            } else {
                finish();
            }
        } else if (requestCode == DBX_CHOOSER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                DbxChooser.Result result = new DbxChooser.Result(data);
                final String name = result.getName();

                String filenameArray[] = name.split("\\.");
                String extension = filenameArray[filenameArray.length - 1];
                /*if(extension.equals(".mp4")){

                }*/

                if (typeAudio != null) {
                    if (extension.equals("mp3")) {
                        DownloadFileFromURL dw = new DownloadFileFromURL(context) {
                            @Override
                            protected void onPostExecute(String filePath) {
                                // TODO Auto-generated method stub
                                super.onPostExecute(filePath);
                                showFilename(name, filePath);
                            }
                        };
                        dw.execute(result.getLink().toString(), result.getName()
                                .toString());

                    } else {
                        audio_rl.setVisibility(View.GONE);
                        showAlert("dropbox");

                    }
                } else if (typeVideo != null) {
                    if (extension.equals("mp4")) {
                        DownloadFileFromURL dw = new DownloadFileFromURL(context) {
                            @Override
                            protected void onPostExecute(String filePath) {
                                // TODO Auto-generated method stub
                                super.onPostExecute(filePath);
                                showFilename(name, filePath);
                            }
                        };
                        dw.execute(result.getLink().toString(), result.getName()
                                .toString());

                    } else {
                        audio_rl.setVisibility(View.GONE);
                        showAlert("dropbox");

                    }
                } else {
                    if (extension.equals("jpeg") || extension.equals("jpg") || extension.equals("png")) {
                        DownloadFileFromURL dw = new DownloadFileFromURL(context) {
                            @Override
                            protected void onPostExecute(String filePath) {
                                // TODO Auto-generated method stub
                                super.onPostExecute(filePath);
                                showFilename(name, filePath);
                            }
                        };
                        dw.execute(result.getLink().toString(), result.getName()
                                .toString());

                    } else {
                        audio_rl.setVisibility(View.GONE);
                        showAlert("dropbox");

                    }
                }


               /* if (extension.equals("jpeg") || extension.equals("jpg") || extension.equals("png")) {
                    DownloadFileFromURL dw = new DownloadFileFromURL(context) {
                        @Override
                        protected void onPostExecute(String filePath) {
                            // TODO Auto-generated method stub
                            super.onPostExecute(filePath);
                            showFilename(name, filePath);
                        }
                    };
                    dw.execute(result.getLink().toString(), result.getName()
                            .toString());

                } else {

                    showAlert("dropbox");

                }*/


            } else {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showAlert(final String msg) {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        LayoutInflater inflater = getLayoutInflater(getArguments());
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.alert_common, null);
        alertDialog.setContentView(convertView);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView txtContent = (TextView) convertView.findViewById(R.id.txtContent);
        Button btnOk = (Button) convertView.findViewById(R.id.btnOk);
        txtContent.setText("Please select image in .jpg or .jpeg or png format");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                if (msg.equals("onedrive")) {
                    oneDriveApi();
                } else {
                    dropboxApi();
                }
            }
        });


        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int value = (int) ((width) * 3 / 4);

        alertDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = value;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    File resumeFile;

    public void showFilename(String filename, String filepath) {

        /*if (filepath != null) {
            Intent intent = new Intent();
            intent.putExtra("filepath", filepath);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
*/

        resume_upload.setVisibility(View.GONE);
        layShowFileName.setVisibility(View.VISIBLE);
        txtFileName.setText(filename);
        selectedFilepath = filepath;
        /*Check file type*/
        String filenameArray[] = filename.split("\\.");
        String extension = filenameArray[filenameArray.length - 1];

       /* Bitmap bitmapImage;
        if (extension.equals("pdf")) {
            txtFileName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pdf_icon, 0, 0, 0);
            bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_pdf);


        } else {
            txtFileName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.word_icon, 0, 0, 0);
            bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_doc);

        }
*/
       /* String destDir = android.os.Environment.getExternalStorageDirectory().toString() + "/Casting_Folder";

        File file = new File(destDir, "ThumbFileType.png");
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        resumeFile = new File(filepath);
        new DownloadFromURL().execute();

    }

    String uploadfiles3() {
        File fs = new File(zipPath.getAbsolutePath());
        File files[] = fs.listFiles();
        MultipartUtility multipart = null;
        try {
            multipart = new MultipartUtility(HttpUri.CASTING_FILE_UPLOAD, "UTF-8");

            multipart.addFormField("userid", Library.getUserId(context));
            multipart.addFormField("role_id", Library.role_id);
            multipart.addFormField("enthicity", Library.enthicity);
            multipart.addFormField("age_range", Library.age_range);
            multipart.addFormField("gender", Library.gender);
            //multipart new android param
            multipart.addFormField("user", "android");

            for (int j = 0; j < files.length; j++) {
                multipart.addFilePart("uploads[]", files[j]);
            }
            List<String> response = multipart.finish();
            String Status = response.get(0);
            return Status;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Library.isApplied = false;
        finish();
    }

    private void alert(final int code, String msg) {


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.alert_common, null);
        alertDialog.setContentView(convertView);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView txtContent = (TextView) convertView.findViewById(R.id.txtContent);
        Button btnOk = (Button) convertView.findViewById(R.id.btnOk);
        txtContent.setText(msg);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                deleteRecursive(zipPath);

                String calledBy = "";
                if (getIntent().hasExtra("CalledBy")) {
                    calledBy = getIntent().getStringExtra("CalledBy");
                }

               /* if (calledBy.equals("Casting")) {
                    Library.isApplied = true;

                    Intent intent = new Intent(ResumeUpload.this, CastingScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                } else if (calledBy.equals("Favorite")) {
                    Intent intent = new Intent(ResumeUpload.this, FavoriteScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                }*/

                if (code == 200) {

                    Intent intent = new Intent();
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

    public class callservice extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String response = uploadfiles3();
            return response;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            if (pDialog != null) pDialog.dismiss();

            casting_resume_upload_done.setEnabled(true);


            if (aVoid != null) {

                try {
                    JSONObject jsonObject = new JSONObject(aVoid);
                    if (jsonObject.has("status")) {

                        if (jsonObject.getString("status").equals("200")) {

//                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            alert(1, jsonObject.getString("message"));

                        } else {
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }
    }

    class DownloadDriveFileAsyncTask extends
            ApiClientAsyncTask<DriveId, Boolean, String[]> {
        public DownloadDriveFileAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // if (pDialog != null && pDialog.isShowing()) {
            // pDialog.dismiss();
            pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Please wait..");
            pDialog.show();
            pDialog.setCancelable(false);
            // }
        }

        @Override
        protected String[] doInBackgroundConnected(DriveId... driveId) {
            // String contents = null;
            int count;
            DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(),
                    driveId[0]);
            file.getMetadata(getGoogleApiClient()).setResultCallback(
                    metadataCallback);
            DriveApi.DriveContentsResult driveContentsResult = file.open(
                    getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null)
                    .await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }
            System.out.println(" fileName >> " + fileName);
            System.out.println(" fileName >> " + fileType);
            DriveContents driveContents = driveContentsResult
                    .getDriveContents();
            InputStream input = driveContents.getInputStream();
            String dfileName = fileName;
            System.out.println(" File>>> " + dfileName);
            FileCacheForService fileCacheForService = new FileCacheForService(
                    context);
            File myFile = fileCacheForService.getFile(dfileName);
            // Output stream to write file
            OutputStream output = null;
            try {
                output = new FileOutputStream(myFile);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // BufferedReader reader = new BufferedReader();
            // StringBuilder builder = new StringBuilder();
            // String line;
            byte data[] = new byte[1024];
            long total = 0;
            try {
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    // publishProgress(""+(int)((total*100)/lenghtOfFile));
                    // writing data to file
                    output.write(data, 0, count);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // flushing output
            try {
                output.flush();
                // closing streams
                output.close();
                input.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            driveContents.discard(getGoogleApiClient());
            return new String[]{dfileName, myFile.getAbsolutePath()};
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (result == null) {
                System.out.println("Error while reading from the file");
                return;
            }
            System.out.println("File contents: " + result);
            showFilename(result[0], result[1]);


            if (mGoogleApiClient != null) {

                if (mGoogleApiClient.isConnected()) {
                    // Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);

                    mGoogleApiClient.clearDefaultAccountAndReconnect();
                    mGoogleApiClient.disconnect();
                    isDrive = false;

                }
            }

        }
    }

    ProgressDialog pd;

    private class DownloadFromURL extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Loading ...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
            pd.setProgress(0);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            uploadfile();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if ((pd != null) && pd.isShowing()) {
                pd.dismiss();
            }

            try {
                if (Status != null) {
                    JSONObject jsonObject = new JSONObject(Status);
                    int code = jsonObject.getInt("status");
                    String message = jsonObject.getString("message");


                    //    Library.doc_id = jsonObject.getString("documnent_id");

                    //                Library.alert(context, message);
                    alert(code, message);
                } else {
                    alert(200, "Something went wrong");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

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

            multipart.addFilePart("uploads[]", resumeFile);



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

    String Status;

}

