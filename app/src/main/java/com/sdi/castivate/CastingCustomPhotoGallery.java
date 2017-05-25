package com.sdi.castivate;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
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
import android.widget.TextView;

import com.sdi.castivate.model.DataPathModel;
import com.sdi.castivate.model.ImageUrl;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.MarshmallowPermission;
import com.sdi.castivate.utils.MultipartUtility;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")

public class CastingCustomPhotoGallery extends Activity {

    public int update_count = 0;
    ImageUrl recentImageUrl;
    ArrayList<DataPathModel> mDataPathModelList = new ArrayList<>();

    //    private String[] arrPath;
//    private boolean[] thumbnailsselection;
//    private int ids[];
    Cursor imagecursor;
    Context context;
    int j = 0;
    ArrayList<Integer> Scount = new ArrayList<Integer>();
    private GridView grdImages;
    private TextView btnSelect;
    private ImageAdapter imageAdapter;
    private int count;
    private int max_count;
    private LinearLayout custom_photo_gallery_back_icon;
    /*Configuration configuration;
    int screenWidthDp;
    int smallestScreenWidthDp;
    */
    private ImageView photoPreview;
    private ImageView imageOverlay;
    MarshmallowPermission marshMallowPermission;

    /**
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       Runtime.getRuntime().totalMemory();
       Runtime.getRuntime().freeMemory();
        setContentView(R.layout.custom_photo_gallery);
        marshMallowPermission = new MarshmallowPermission(this);
        this.overridePendingTransition(0, 0);
        context = this;
       /* configuration = this.getResources().getConfiguration();
        screenWidthDp = configuration.screenWidthDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.
        smallestScreenWidthDp = configuration.smallestScreenWidthDp; //The smallest screen size an application will see in normal operation, corresponding to smallest screen width resource qualifier
*/


        grdImages = (GridView) findViewById(R.id.grdImages);
        btnSelect = (TextView) findViewById(R.id.btnSelect);
        photoPreview = (ImageView) findViewById(R.id.photoPreview);
        imageOverlay = (ImageView) findViewById(R.id.imageOverlay);

        grdImages.setSelector(new ColorDrawable(Color.TRANSPARENT));


        custom_photo_gallery_back_icon = (LinearLayout) findViewById(R.id.custom_photo_gallery_back_icon);
        custom_photo_gallery_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadGridView();
    }


    @Override
    protected void onResume() {
        super.onResume();

      /*  Intent intent = getIntent();

        if (intent.hasExtra("update_count")) {
            update_count = intent.getIntExtra("update_count", 0);
            if(!imagecursor.isClosed()){
                max_count = imagecursor.getCount();
                System.out.println(max_count);
                update_count = max_count;
            }
            max_count = update_count;
            update_count = 0;
        }*/

        if (update_count > 0) {
            max_count = 10;
            //    update_count = max_count - update_count;
        } else {
            max_count = 10;
            //     update_count = 0;
        }

    }

    /**
     * Class method
     */

    ArrayList<File> selectedimage;

    public void loadGridView() {
        final String[] columns = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
        };
        final String orderBy = MediaStore.Images.Media._ID;

        //imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy); //old code
        imagecursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy); //raghul

        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();

        // this.arrPath = new String[this.count];
        // ids = new int[count];
        // this.thumbnailsselection = new boolean[this.count];

        for (int i = 0; i < this.count; i++) {

            DataPathModel mDataPathModel = new DataPathModel();
            //Image position
            imagecursor.moveToPosition(i);
            mDataPathModel.ids = imagecursor.getInt(image_column_index);
            //Image path
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            mDataPathModel.arrPath = imagecursor.getString(dataColumnIndex);


            mDataPathModelList.add(mDataPathModel);
        }

        //imageAdapter = null;

        imageAdapter = new ImageAdapter();
        grdImages.setAdapter(imageAdapter);
        grdImages.setFastScrollEnabled(false);
        grdImages.setSmoothScrollbarEnabled(true); //raghul
        //imageAdapter.notifyDataSetChanged();
        imagecursor.close();

        btnSelect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
//                final int len = thumbnailsselection.length;

                btnSelect.setEnabled(false);
                selectedimage = new ArrayList<File>();
                for (int i = 0; i < mDataPathModelList.size(); i++) {

                    if (mDataPathModelList.get(i).isSelected()) {
                        selectedimage.add(new File(mDataPathModelList.get(i).arrPath));
                    }
                }
                if (selectedimage.size() != 0)
                    new UploadImages().execute();
                else {
                    Library.alert(context, "Please select atleast one image");
                }


            }
        });

    }

    ProgressDialog pd;

    public class UploadImages extends AsyncTask<String, String, String> {


        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.dismiss();
                pd = null;
            }

            if (pd == null) {
                pd = new ProgressDialog(context);
                pd.setMessage("Loading ...");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setCancelable(false);
                pd.show();


            }

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
                    int code = jsonObject.getInt("status");
                    String message = jsonObject.getString("message");
//                Library.alert(context, message);
                    alert(code, message);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Library.alert(context, "Something went wrong. Try again.");
                btnSelect.setEnabled(true);
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
                    btnSelect.setEnabled(true);
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


    public void uploadfile() {
        try {

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String Status = "";


    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        super.onPause();
        System.gc();
    }

    /**
     * This method used to set bitmap.
     *
     * @param iv represented ImageView
     * @param id represented id
     */


    private void setBitmap(final ImageView iv, final int id, final int pos) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {


                return MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);

            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
                if (result != null) mDataPathModelList.get(pos).setImagebitmap(result);
            }
        }.execute();
    }

    private void closeProgress() {
        if (pd.isShowing())
            pd.dismiss();
    }

    private void previewImage() {

        if (recentImageUrl != null) {
            photoPreview.setImageResource(R.drawable.contact_picture_placeholder);
            imageOverlay.setVisibility(View.GONE);
        } else {

            try {
                if(recentImageUrl.getUploadImageUrl()!=null)
                photoPreview.setImageBitmap(BitmapFactory.decodeFile(recentImageUrl.getUploadImageUrl()));
                imageOverlay.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * List adapter
     *
     * @author tasol
     */

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;


        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return mDataPathModelList.size();
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
            if (view == convertView) {

                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.custom_image_gallery_item, null);
                holder.photoImageView = (ImageView) view.findViewById(R.id.photoImageView);

                holder.photoImageViewOverlay = (ImageView) view.findViewById(R.id.photoImageViewOverlay);
                holder.photoCount = (TextView) view.findViewById(R.id.photoCount);
                holder.photoImageView.setImageDrawable(null);

                view.setTag(holder);

            } else {

                holder = (ViewHolder) view.getTag();
            }

            DataPathModel mDataPathModel = mDataPathModelList.get(position);


            try {

                if (mDataPathModel.getImagebitmap() == null) {

                    setBitmap(holder.photoImageView, mDataPathModel.ids, position);

                } else {
                    holder.photoImageView.setImageBitmap(mDataPathModel.getImagebitmap());
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (mDataPathModel.isSelected()) {

                holder.photoCount.setVisibility(View.VISIBLE);
                holder.photoCount.setText("" + mDataPathModelList.get(position).getIndexPosition());
                holder.photoImageViewOverlay.setVisibility(View.VISIBLE);
                previewImage();

            } else {
                holder.photoImageViewOverlay.setVisibility(View.GONE);
                holder.photoCount.setText("");
                previewImage();
            }

            holder.photoImageView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if (mDataPathModelList.get(position).isSelected()) {

                        mDataPathModelList.get(position).setSelected(false);

                        holder.photoCount.setVisibility(View.GONE);
//                        holder.photoCount.setText(String.valueOf(update_count));
                        mDataPathModelList.get(position).setIndexPosition(0);
                        holder.photoImageViewOverlay.setVisibility(View.GONE);
                        previewImage();

                        try {
                            String stcount = holder.photoCount.getText().toString().trim();
                            int pos = Scount.indexOf(position);
//                            Scount.set(pos,-1);
                            Scount.remove(pos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        update_count--;
                        int k = 0;
                        for (int i = 0; i < Scount.size(); i++) {
                            if (Scount.get(i) != null) {
                                k++;
                                mDataPathModelList.get(Scount.get(i)).setIndexPosition(k);

                            }
                        }
                        imageAdapter.notifyDataSetChanged();

                    } else {

                        if (update_count < max_count) {

                            File file = new File(mDataPathModelList.get(position).arrPath);

                            long length = file.length();

                            length = length / (1024 * 1024);

                            System.out.println("File size : " + length);

                            if (length < 100) {
                                System.out.println("File Size :" + length);
                                System.out.println("File Size Allowed");
                                System.out.println("add image");

                                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), mDataPathModelList.get(position).getIds(), MediaStore.Images.Thumbnails.MINI_KIND, null);
//                                bitmap = mDataPathModelList.get(position).getImagebitmap();
                                photoPreview.setImageBitmap(bitmap);

                                update_count++;
                                Scount.add(position);
                                holder.photoCount.setVisibility(View.VISIBLE);
                                holder.photoCount.setText(String.valueOf(update_count));
                                mDataPathModelList.get(position).setSelected(true);
                                mDataPathModelList.get(position).setIndexPosition(update_count);
                                holder.photoImageViewOverlay.setVisibility(View.VISIBLE);


                            } else {
                                System.out.println("File Size :" + length);
                                System.out.println("File Size Not Allowed");
                                Library.alert(context, "File size exceeded.");
                            }
                        }
                    }
                }
            });

            return view;
        }

        /**
         * Inner class
         *
         * @author tasol
         */
        private class ViewHolder {
            public ImageView photoImageView;
            public ImageView photoImageViewOverlay;
            public TextView photoCount;
            public int value;
            public int id;

        }

    }
}
