package com.sdi.castivate;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.StaleDataException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdi.castivate.model.DataPathModel;
import com.sdi.castivate.model.VideoUrl;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.MultipartUtility;
import com.sdi.castivate.video.MediaControllerCompressor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")

public class CastingCustomVideoGallery extends Activity {

    private GridView grdVideos;
    private TextView btnSelectV;
    private VideoAdapter videoAdapter;
    private String[] arrPath;
    private String[] duration;
    private ProgressBar progressBar;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count;
    File files;
    private int max_count;
    private int update_count = 0;
    ProgressDialog pd;

    private ArrayList<VideoUrl> videoUrls = new ArrayList<VideoUrl>();

    Cursor imagecursor;
    private LinearLayout custom_video_gallery_back_icon;
    Context context;

    /**
     * Overrides methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_video_gallery);

        context = this;

        grdVideos = (GridView) findViewById(R.id.grdVideos);
        btnSelectV = (TextView) findViewById(R.id.btnSelectV);
        custom_video_gallery_back_icon = (LinearLayout) findViewById(R.id.custom_video_gallery_back_icon);
        custom_video_gallery_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent Intent = getIntent();
        update_count = Intent.getIntExtra("update_count", 0);

        max_count = 1;
        update_count = 0;

        final String[] columns = {

                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DURATION
        };

        final String orderBy = MediaStore.Video.Media._ID;
        imagecursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Video.Media._ID);
        this.count = imagecursor.getCount();
        this.arrPath = new String[this.count];
        this.duration = new String[this.count];
        ids = new int[count];
        this.thumbnailsselection = new boolean[this.count];

        for (int i = 0; i < this.count; i++) {
            //Video position
            imagecursor.moveToPosition(i);
            ids[i] = imagecursor.getInt(image_column_index);
            //Video path
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Video.Media.DATA);
            arrPath[i] = imagecursor.getString(dataColumnIndex);
            //Video duration
            int durationColumnIndex = imagecursor.getColumnIndex(MediaStore.Video.Media.DURATION);
            duration[i] = imagecursor.getString(durationColumnIndex);
        }

        videoAdapter = new VideoAdapter();
        grdVideos.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();
        imagecursor.close();
        btnSelectV.setVisibility(View.GONE);
        btnSelectV.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                btnSelectV.setEnabled(false);

                selectedVideos = new ArrayList<String>();
                selectedVideosId = new ArrayList<Integer>();
                final int len = thumbnailsselection.length;

                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        selectedVideos.add(arrPath[i]);
                        selectedVideosId.add(ids[i]);
                    }
                }
                if (selectedVideos.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one video", Toast.LENGTH_LONG).show();
                } else {
                    uploadPos = 0;
                    new UploadImages().execute();
                }
            }
        });
    }

    ArrayList<String> selectedVideos;
    ArrayList<Integer> selectedVideosId;
    int uploadPos;

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    ArrayList<File> selectedimage;

    public void uploadfile() {
        try {

            selectedimage = new ArrayList<>();
            selectedimage.add(new File(selectedVideos.get(uploadPos)));

            File filesDir = context.getFilesDir();
            File imageFile = new File(filesDir, "Thumb" + "_" + System.currentTimeMillis() + ".png");

            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), selectedVideosId.get(uploadPos), MediaStore.Video.Thumbnails.MICRO_KIND, null);
            File s = saveBitmap(bitmap, imageFile.getAbsolutePath());
            selectedimage.add(s);

            System.gc();

            MultipartUtility multipart = new MultipartUtility(HttpUri.CASTING_FILE_UPLOAD, "UTF-8");
//            multipart.addFormField("description", "Cool Pictures");

            multipart.addFormField("userid", Library.getUserId(context));
            //multipart new android param
            multipart.addFormField("user", "android");
            for (int i = 0; i < selectedimage.size(); i++) {
                multipart.addFilePart("uploads[]", selectedimage.get(i));
            }

            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");

            for (String line : response) {
                System.out.println("result : " + line);
                Status = line;
            }

            System.out.println("Server Response : " + Status);

            uploadPos = uploadPos + 1;

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

    String Status = "";

    public class UploadImages extends AsyncTask<String, String, String> {


        @Override
        public void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Loading ...");
            pd.setCancelable(false);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
            pd.setProgress(0);
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
            if ((pd != null) && pd.isShowing()) {
                pd.dismiss();
            }
            if (!Status.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(Status);
                    int code = jsonObject.optInt("status");
                    if (code == 200) {
                        if (uploadPos < selectedVideos.size()) {
                            new UploadImages().execute();
                        } else {
                            if ((pd != null) && pd.isShowing()) {
                                pd.dismiss();
                            }
                            String message = jsonObject.getString("message");
                            alert(code, message);

                        }
                    } else {
                        btnSelectV.setEnabled(true);
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

                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    btnSelectV.setEnabled(true);
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


    private void setBitmap(final ImageView iv, final int id, final int pos) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Video.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);

            }
        }.execute();
    }

    /**
     * List adapter
     *
     * @author tasol
     */

    public class VideoAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public VideoAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;

            if (view == null) {
                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.custom_video_gallery_item, null);
                holder.videoImageView = (ImageView) view.findViewById(R.id.videoImageView);
                holder.videoImageViewOverlay = (ImageView) view.findViewById(R.id.videoImageViewOverlay);
                holder.videoCount = (TextView) view.findViewById(R.id.videoCount);
                holder.videoDuration = (TextView) view.findViewById(R.id.videoDuration);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.videoImageView.setId(position);
            holder.videoCount.setId(position);
            holder.videoDuration.setId(position);

            holder.videoImageView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    int id = v.getId();
                    if (thumbnailsselection[position]) {
                        for (int i = 0; i < videoUrls.size(); i++) {
                            VideoUrl videoUrl = videoUrls.get(i);
                            if (Objects.equals(videoUrl.getUploadVideoUrl(), String.valueOf(position))) {
                                System.out.println("remove video");
                                videoUrls.remove(i);
                                update_count--;

                                holder.isSelect = false;
                                holder.videoCount.setVisibility(View.GONE);
                                holder.videoCount.setText(String.valueOf(update_count));
                                thumbnailsselection[position] = false;
                                holder.videoImageViewOverlay.setVisibility(View.GONE);
                                notifyDataSetChanged();
                            }
                        }
                    } else {

                        if (update_count < max_count) {

                            File file = new File(arrPath[position]);

                            long length = file.length();

                            length = length / (1024 * 1024);

                            Log.i("video length", String.valueOf(length));
                            System.out.println("File size : " + length);

                            if (length < 100) {
                               /* System.out.println("File Size :" + length);
                                System.out.println("File Size Allowed");

                                VideoUrl videoUrl = new VideoUrl();
                                System.out.println("add video");

                                videoUrl.setUploadVideoUrl(String.valueOf(position));
                                videoUrls.add(videoUrl);
                                update_count++;
                                *//*holder.videoCount.setVisibility(View.VISIBLE);
                                holder.videoCount.setText(String.valueOf(videoUrls.size()));*//*
                                thumbnailsselection[position] = true;
                                holder.videoImageViewOverlay.setVisibility(View.VISIBLE);
                                notifyDataSetChanged();*/

                                selectedVideos = new ArrayList<String>();
                                selectedVideosId = new ArrayList<Integer>();

                                selectedVideos.add(arrPath[position]);
                                selectedVideosId.add(ids[position]);
                                uploadPos = 0;
                                new UploadImages().execute();

                            } else {
                               /* files = new File(arrPath[position]);

                                selectedVideos = new ArrayList<String>();
                                selectedVideosId = new ArrayList<Integer>();

                                selectedVideos.add(arrPath[position]);
                                selectedVideosId.add(ids[position]);
                                uploadPos = 0;

                                new VideoCompressor().execute();*/

                                System.out.println("File Size :" + length);
                                System.out.println("File Size Not Allowed");
                                Library.alert(context, "File size exceeded.");
                            }
                        }
                    }

                }
            });

            try {
                long timeInmillisec = Long.parseLong(duration[position]);
                long duration = timeInmillisec / 1000;
                long hours = duration / 3600;
                long minutes = (duration - hours * 3600) / 60;
                long seconds = duration - (hours * 3600 + minutes * 60);

                holder.videoDuration.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                setBitmap(holder.videoImageView, ids[position], position);

            } catch (Throwable e) {
                e.printStackTrace();
            }

            holder.isSelect = false;
            for (int i = 0; i < videoUrls.size(); i++) {

                VideoUrl videoUrl = videoUrls.get(i);
                if (Objects.equals(videoUrl.getUploadVideoUrl(), String.valueOf(position))) {
                    holder.isSelect = true;
                    holder.value = i + 1;
                }
            }

            if (holder.isSelect) {
                holder.videoImageViewOverlay.setVisibility(View.VISIBLE);
//                holder.videoCount.setVisibility(View.VISIBLE);
                holder.videoCount.setText(String.valueOf(holder.value));
            } else {
                holder.videoImageViewOverlay.setVisibility(View.GONE);
                holder.videoCount.setVisibility(View.GONE);
                holder.videoCount.setText("");
            }

            holder.id = position;
            return view;
        }
    }

    /**
     * Inner class
     *
     * @author tasol
     */
    class ViewHolder {
        ImageView videoImageView;
        ImageView videoImageViewOverlay;
        TextView videoCount;
        TextView videoDuration;
        boolean isSelect;
        int value;
        int id;
    }

    class VideoCompressor extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Loading ...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
            pd.setProgress(0);
            pd.show();

            Log.d("TAG", "Start video compression");
            files.getAbsolutePath();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return MediaControllerCompressor.getInstance().convertVideo(files.getAbsolutePath());
            //   return MediaControllerCompressor.getInstance().convertVideo(tempFile.getPath());
        }

        @Override
        protected void onPostExecute(String compressed) {
            super.onPostExecute(compressed);
            if ((pd != null) && pd.isShowing()) {
                pd.dismiss();
            }
            if (compressed != null) {
                System.out.println("compressed URL path = " + compressed);
                Log.d("TAG1", "Compression successfully!");

                new UploadVideos().execute(compressed);


            }
        }
    }

    String StatusValue = "";

    public class UploadVideos extends AsyncTask<String, String, String> {


        @Override
        public void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Loading ...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
            pd.setProgress(0);
            pd.show();
        }

        @Override
        public String doInBackground(String... params) {
            String s = params[0];
            uploadVideofile(s);
            return null;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println(result);
            if ((pd != null) && pd.isShowing()) {
                pd.dismiss();
            }
            if (!StatusValue.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(StatusValue);
                    int code = jsonObject.getInt("status");
                    if (code == 200) {
                        if (uploadPos < selectedVideos.size()) {
                            new UploadImages().execute();
                        } else {
                            if ((pd != null) && pd.isShowing()) {
                                pd.dismiss();
                            }
                            String message = jsonObject.getString("message");
                            alert(code, message);

                        }
                    } else {
                        btnSelectV.setEnabled(true);
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

    public void uploadVideofile(String s) {
        try {

            selectedimage = new ArrayList<>();
            File f = new File(s);
            System.out.println("file path" + f.getAbsolutePath());
            //   if (f.exists())
            selectedimage.add(f);

            File filesDir = context.getFilesDir();
            File imageFile = new File(filesDir, "Thumb" + "_" + System.currentTimeMillis() + ".png");
            //  selectedVideosId = new ArrayList<Integer>();
            //  Uri uri = Uri.fromFile(new File(s));


            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), selectedVideosId.get(uploadPos), MediaStore.Video.Thumbnails.MICRO_KIND, null);
            File file = saveBitmap(bitmap, imageFile.getAbsolutePath());
            selectedimage.add(file);

            System.gc();

            MultipartUtility multipart = new MultipartUtility(HttpUri.CASTING_FILE_UPLOAD, "UTF-8");
//            multipart.addFormField("description", "Cool Pictures");

            multipart.addFormField("userid", Library.getUserId(context));
            //multipart new android param
            multipart.addFormField("user", "android");
            for (int i = 0; i < selectedimage.size(); i++) {
                multipart.addFilePart("uploads[]", selectedimage.get(i));
            }

            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");

            for (String line : response) {
                System.out.println("result : " + line);
                Status = line;
            }

            System.out.println("Server Response : " + Status);

            uploadPos = uploadPos + 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
