package com.sdi.castivate.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Gnanaoly on 18-May-16.
 */
public class ImageDownload extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    String SaveFolderName;
    int row;
    Drawable placeholder;
    Context mContext;

    public ImageDownload(Context context, ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.mContext = context;
    }

    public ImageDownload(Context mContext, ImageView imageView, Drawable placeholder, int row) {
        this.mContext = mContext;
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.placeholder = placeholder;
        this.row = row;

    }


    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Create a progressdialog

    }

    @Override
    protected Bitmap doInBackground(String... URL) {

        String imageURL = URL[0];

        Bitmap image = getFileName(imageURL, mContext, "Castivate");

        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bmRotated) {
        // Set the bitmap into ImageView


        if (imageViewReference != null) {
            ContactImageView imageview1 = (ContactImageView) imageViewReference.get();
            if (imageview1 != null) {
                if (bmRotated != null) {
                    if (imageview1.position == row) {
                        imageview1.setImageBitmap(bmRotated);
                    }
                } else {
                    if (placeholder!=null)
                    imageview1.setImageDrawable(placeholder);
                }
            }
        }
    }

    private Bitmap getFileName(String url, Context context, String folderName) {

        Bitmap bmRotated = null;

        SaveFolderName = context.getFilesDir().getAbsolutePath() + "/" + folderName;

        String filepath = url.substring(0, url.lastIndexOf("/"));
        String str_randomnumber = url.substring(url.lastIndexOf("/") + 1);
        File wallpaperDirectory = new File(SaveFolderName);
        if (!wallpaperDirectory.exists())
            wallpaperDirectory.mkdirs();
        String Photo_ImagePath = SaveFolderName + "/" + str_randomnumber;

        File f = new File(Photo_ImagePath);

        if (!f.exists()) {
            // DebugReportOnLocat.ln(" EEEEEEEEEEXXXXXXXXIIIIISSSSSSSTTTTTTT " +
            // Photo_ImagePath);
            try {
                f.createNewFile();
                str_randomnumber = str_randomnumber.replace(" ", "%20");
                url = filepath + "/" + str_randomnumber;
                // DebugReportOnLocat.ln("filename url " + url);
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(f);
                ImageDownloadUtils.CopyStream(is, os);
                os.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return bmRotated;
            }

        }

        try {
            Bitmap bitmap = decodeFile(new File(Photo_ImagePath));

            ExifInterface exif = new ExifInterface(Photo_ImagePath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            bmRotated = rotateBitmap(bitmap, rotation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bmRotated;

    }

    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
}