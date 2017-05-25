package com.sdi.castivate.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sdi.castivate.NewPhotoUpload;
import com.sdi.castivate.R;
import com.sdi.castivate.googledriveutils.DownloadFileFromURL;
import com.sdi.castivate.image.ContactImageView;
import com.sdi.castivate.model.DeleteFileOutput;
import com.sdi.castivate.model.NewDeleteFileInput;
import com.sdi.castivate.model.NewFileModel;
import com.sdi.castivate.model.NewVideoType;
import com.sdi.castivate.utils.ImagePreview;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.RegisterRemoteApi;
import com.sdi.castivate.utils.VideoActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nijamudhin on 3/17/2017.
 */

public class VideoAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater = null;
    ProgressDialog pd;
    //   ArrayList<NewVideoType> list = new ArrayList<NewVideoType>();
    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    Drawable placeholder;

    public VideoAdapter(Context activity, ArrayList<String> data, ArrayList<String> list) {
        this.context = activity;
        this.list = list;
        this.data = data;
        inflater = LayoutInflater.from(context);
        placeholder = context.getResources().getDrawable(R.drawable.avathar_profile);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return list.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //    NewVideoType model = list.get(position);

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
            if (Library.castivateVideo == "castivateVideo") {
                holder.imgDelete.setVisibility(View.GONE);
                if (data.get(position).equals("ADD")) {
                    data.remove(0);
                    list.remove(0);
                    //      holder.layImage.setVisibility(View.GONE);
                    //      holder.iv_AddPhoto.setVisibility(View.VISIBLE);

                } else if (data.get(position) != null && !data.get(position).equals("")) {

                    holder.layImage.setVisibility(View.VISIBLE);
                    holder.imgPlay.setVisibility(View.VISIBLE);
                    holder.iv_AddPhoto.setVisibility(View.GONE);
                    Picasso.with(context).load(data.get(position)).placeholder(R.drawable.avathar_profile).into(holder.imgProfileImage);
                }

            } else {

                if (data.get(position).equals("ADD")) {
                    holder.layImage.setVisibility(View.GONE);
                    holder.iv_AddPhoto.setVisibility(View.VISIBLE);

                } else if (data.get(position) != null && !data.get(position).equals("")) {

                    holder.layImage.setVisibility(View.VISIBLE);
                    holder.imgPlay.setVisibility(View.VISIBLE);
                    holder.iv_AddPhoto.setVisibility(View.GONE);
                    Picasso.with(context).load(data.get(position)).placeholder(R.drawable.avathar_profile).into(holder.imgProfileImage);
                }
            }


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
                                                              String url = list.get(position).toString();
                                                              final String thumb = data.get(position).toString();

                                                              if (Library.castivateVideo == "castivateVideo") {
                                                                  if (data.get(position) != null && !data.get(position).equals("ADD")) {
                                                                      int position = url.lastIndexOf("/");
                                                                      position = position + 1;
                                                                      String name = url.substring(position, url.length());

                                                                      System.out.println("name = " + name);

                                                                      String path = Environment.getExternalStorageDirectory().toString() + "/Casting_Folder/" + name;
                                                                      File f = new File(path);

                                                                      if (f.exists()) {

                                                                          Intent intent = new Intent();
                                                                          intent.putExtra("url", path);
                                                                          intent.putExtra("thumb", thumb);

                                                                          ((Activity) context).setResult(Activity.RESULT_OK, intent);
                                                                          ((Activity) context).finish();
                                                                      } else {

                                                                          DownloadFileFromURL dw = new DownloadFileFromURL(context) {
                                                                              @Override
                                                                              protected void onPostExecute(String filePath) {
                                                                                  // TODO Auto-generated method stub
                                                                                  super.onPostExecute(filePath);


                                                                                  Intent intent = new Intent();


                                                                                  intent.putExtra("url", filePath);
                                                                                  intent.putExtra("thumb", thumb);

                                                                                  ((Activity) context).setResult(Activity.RESULT_OK, intent);
                                                                                  ((Activity) context).finish();
                                                                              }

                                                                          };
                                                                          dw.execute(url, name);
                                                                      }
                                                                  }
                                                              } else {
                                                                  if (list.get(position) != null && !list.get(position).equals("ADD")) {
                                                                      Intent in = new Intent(context, VideoActivity.class);
                                                                      in.putExtra("video", list.get(position));
                                                                      ((Activity) context).startActivity(in);
                                                                  }
                                                              }
                                                          }
                                                      }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowView;
    }

    public class ViewHolder {
        ImageView imgDelete, imgPlay, imgExpand, iv_AddPhoto;
        ContactImageView imgProfileImage;
        RelativeLayout layImage;
    }

    private void deletePhoto(final int pos) {
        // TODO Auto-generated method stub

        // Progress Dialog
        pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        try {

            NewDeleteFileInput input = new NewDeleteFileInput(Library.getUserId(context), list.get(pos));

            RegisterRemoteApi.getInstance().setNewDeleteInput(input);

            // Call Login JSON
            RegisterRemoteApi.getInstance().getNewDeleteInput(context, new Callback<DeleteFileOutput>() {
                @Override
                public void success(DeleteFileOutput profileinfoOutput, Response response) {

                    if (profileinfoOutput.status == 200) {

                        NewPhotoUpload.fileData.remove(pos);
                        NewPhotoUpload.filesData.remove(pos);
                        NewPhotoUpload.adapterVideo.notifyDataSetChanged();
                        Library.reduceUserProfileDetails(context, "video");

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
        }
    }
}

