package com.sdi.castivate.adapter;

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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialcamera.MaterialCamera;
import com.sdi.castivate.CastingCustomPhotoGallery;
import com.sdi.castivate.MyProfile;
import com.sdi.castivate.NewPhotoUpload;
import com.sdi.castivate.R;
import com.sdi.castivate.googledriveutils.DownloadFileFromURL;
import com.sdi.castivate.image.ContactImageView;
import com.sdi.castivate.image.ImageDownload;
import com.sdi.castivate.model.DeleteFileInput;
import com.sdi.castivate.model.DeleteFileOutput;
import com.sdi.castivate.model.FileModel;
import com.sdi.castivate.model.NewDeleteFileInput;
import com.sdi.castivate.model.NewFileModel;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.ImagePreview;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.MarshmallowPermission;
import com.sdi.castivate.utils.MultipartUtility;
import com.sdi.castivate.utils.RegisterRemoteApi;
import com.sdi.castivate.utils.VideoActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nijamudhin on 3/16/2017.
 */

public class PhotoAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater = null;
    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> listValue = new ArrayList<>();

    Drawable placeholder;

    public PhotoAdapter(Context activity, ArrayList<String> data, ArrayList<String> listValue) {
        this.context = activity;
        this.data = data;
        this.listValue = listValue;
        inflater = LayoutInflater.from(context);
        placeholder = context.getResources().getDrawable(R.drawable.avathar_profile);
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return data.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        View rowView = convertView;
        if (convertView == null) {
            rowView = inflater.inflate(R.layout.new_listitem, null);
            holder = new ViewHolder();
            holder.imgProfileImage = (ContactImageView) rowView.findViewById(R.id.imgProfileImage);
            holder.imgDelete = (ImageView) rowView.findViewById(R.id.imgDelete);
            holder.imgPlay = (ImageView) rowView.findViewById(R.id.imgPlay);
            holder.imgExpand = (ImageView) rowView.findViewById(R.id.imgExpand);
            holder.layImage = (RelativeLayout) rowView.findViewById(R.id.layImage);
            holder.iv_AddPhoto = (ImageView) rowView.findViewById(R.id.iv_AddPhoto);


            rowView.setTag(holder);

            holder.imgDelete.setTag(position);
            holder.imgProfileImage.setTag(position);
            holder.imgExpand.setTag(position);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }


        try {
            //  holder.imgProfileImage.position = position;

            if (Library.castivatePhoto.equals("castivatePhoto")) {
                holder.imgDelete.setVisibility(View.GONE);
                if (data.get(position).equals("ADD")) {
                    data.remove(0);
                    //      holder.layImage.setVisibility(View.GONE);
                    //      holder.iv_AddPhoto.setVisibility(View.VISIBLE);

                } else if (data.get(position) != null && !data.get(position).equals("")) {

                    holder.layImage.setVisibility(View.VISIBLE);
                    holder.iv_AddPhoto.setVisibility(View.GONE);

                    //                for (int i = 0; i < data.size(); i++) {
                    //                    new ImageDownload(context, holder.imgProfileImage, placeholder, position).execute(data.get(position));
                    Picasso.with(context).load(data.get(position)).placeholder(R.drawable.avathar_profile).fit().memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imgProfileImage);
                    //            }
                }

            } else {
                if (listValue.get(position).equals("ADD")) {
                    holder.layImage.setVisibility(View.GONE);
                    holder.iv_AddPhoto.setVisibility(View.VISIBLE);

                } else if (listValue.get(position) != null && !listValue.get(position).equals("")) {

                    holder.layImage.setVisibility(View.VISIBLE);
                    holder.iv_AddPhoto.setVisibility(View.GONE);

                    //                for (int i = 0; i < data.size(); i++) {
                    //                    new ImageDownload(context, holder.imgProfileImage, placeholder, position).execute(data.get(position));
                    //                resize(300,300)
                    Picasso.with(context).load(listValue.get(position)).placeholder(R.drawable.avathar_profile).fit().memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imgProfileImage);
                    //            }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



       /* if (list.get(position).type.equals("image")) {

            new ImageDownload(context, holder.imgProfileImage, placeholder, position).execute(list.get(position).url);
            holder.imgPlay.setVisibility(View.GONE);
//            Picasso.with(context).load(list.get(position).url).into(holder.imgProfileImage);



        } else if (list.get(position).type.equals("video")) {

            new ImageDownload(context, holder.imgProfileImage, placeholder, position).execute(list.get(position).thumbUrl);
            holder.imgPlay.setVisibility(View.VISIBLE);

//            Picasso.with(context).load(list.get(position).thumbUrl).into(holder.imgProfileImage);

        }*/
        try {
            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();

                    deletePhoto(pos);

                }
            });

            holder.imgProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    if (Library.castivatePhoto.equals("castivatePhoto")) {

                        if (data.get(position) != null && !data.get(position).equals("ADD")) {
                            final String value = data.get(position).toString();
                      /*  String filenameArray[] = value.split("\\.");
                        String extension = filenameArray[filenameArray.length - 1];
                        System.out.println("name  -------->"+extension);
*/
                            int position = value.lastIndexOf("/");
                            position = position + 1;
                            String name = value.substring(position, value.length());

                            System.out.println("name = " + name);

                            String path = Environment.getExternalStorageDirectory().toString() + "/Casting_Folder/" + name;
                            File f = new File(path);

                            if (f.exists()) {
                                Intent intent = new Intent();
                                intent.putExtra("message", path);
                                intent.putExtra("url", value);
                                ((Activity) context).setResult(Activity.RESULT_OK, intent);
                                ((Activity) context).finish();
                            } else {

                                DownloadFileFromURL dw = new DownloadFileFromURL(context) {
                                    @Override
                                    protected void onPostExecute(String filePath) {
                                        // TODO Auto-generated method stub
                                        super.onPostExecute(filePath);
                                        //         showFilename(name, filePath);
                                        Intent intent = new Intent();
                                        intent.putExtra("message", filePath);
                                        intent.putExtra("url", value);
                                        ((Activity) context).setResult(Activity.RESULT_OK, intent);
                                        ((Activity) context).finish();

                                    }
                                };
                                dw.execute(value, name);
                            }
                        }
                    } else {
                        if (data.get(position) != null && !data.get(position).equals("ADD")) {
                            Intent in = new Intent(context, ImagePreview.class);
                            in.putExtra("image", data.get(position));
                            ((Activity) context).startActivity(in);
                        }
                    }



               /* if (list.get(position).type.equals("image")) {
                    Intent in = new Intent(context, ImagePreview.class);
                    in.putExtra("image", list.get(position).url);
                    ((Activity) context).startActivity(in);
                } else {
                    if (list.get(position).type.equals("video")) {
                        Intent in = new Intent(context, VideoActivity.class);
                        in.putExtra("video", list.get(position).url);
                        ((Activity) context).startActivity(in);
                    }
                }*/
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* holder.imgProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageAlert();
            }
        });

*/
        return rowView;
    }

    public class ViewHolder {
        //LinearLayout rootLay;
        ImageView imgDelete, imgPlay, imgExpand, iv_AddPhoto;
        ContactImageView imgProfileImage;
        RelativeLayout layImage;
    }


    ProgressDialog pd;

    private void deletePhoto(final int pos) {
        // TODO Auto-generated method stub

        // Progress Dialog
        pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        // Send the userName,password
        try {


            NewDeleteFileInput input = new NewDeleteFileInput(Library.getUserId(context), data.get(pos));

            RegisterRemoteApi.getInstance().setNewDeleteInput(input);

            // Call Login JSON
            RegisterRemoteApi.getInstance().getNewDeleteInput(context, new Callback<DeleteFileOutput>() {
                @Override
                public void success(DeleteFileOutput profileinfoOutput, Response response) {

                    if (profileinfoOutput.status == 200) {

                        Library.reduceUserProfileDetails(context, "image");

                        //      MyProfile.files.remove(pos);
                        NewPhotoUpload.files.remove(pos);
                        NewPhotoUpload.adapter.notifyDataSetChanged();
                        closeProgress();

                    } else {
                        closeProgress();
                        if (!profileinfoOutput.message.contains("found")) {
                            Library.alert(context, profileinfoOutput.message);
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

    private void closeProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
            closeProgress();
        }
    }
}
