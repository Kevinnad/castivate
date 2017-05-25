package com.sdi.castivate.googledriveutils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import retrofit2.http.Url;

/**
 * Background Async Task to download file
 */
public class DownloadFileFromURL extends AsyncTask<String, String, String> {


    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    Context mContext;
    private ProgressDialog pDialog;

    public DownloadFileFromURL(Context mContext) {
        super();
        this.mContext = mContext;
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (pDialog != null) {
            pDialog.dismiss();
        }
        pDialog = new ProgressDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Please wait..");
        pDialog.setCancelable(false);
        pDialog.show();

        // showDialog(progress_bar_type);
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {

            String value = f_url[0];
            URL url = new URL(f_url[0]);

            if (value.contains("dl=0")) {
                value = value.replace("dl=0", "dl=1");
                url = new URL(value);
            }

            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            System.out.println(" File>>> " + f_url[1]);

            FileCacheForService fileCacheForService = new FileCacheForService(mContext);

            File myFile = fileCacheForService.getFile(f_url[1]);

            // Output stream to write file
            OutputStream output = new FileOutputStream(myFile);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();


            return myFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String file_url) {

        System.out.println("file_url = " + file_url);


        try {

            if (pDialog != null) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }


        } catch (Exception e) {
            // TODO: handle exception
        }

    }

}

/* ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // your stuff to update the UI
                pDialog = new ProgressDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                pDialog.setMessage("Please wait..");
                pDialog.setCancelable(false);
                pDialog.show();
            }
        });*/