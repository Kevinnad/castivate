package com.sdi.castivate.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sdi.castivate.AudioPreview;
import com.sdi.castivate.NewPhotoUpload;
import com.sdi.castivate.R;
import com.sdi.castivate.googledriveutils.DownloadFileFromURL;
import com.sdi.castivate.image.ContactImageView;
import com.sdi.castivate.model.DeleteFileOutput;
import com.sdi.castivate.model.NewDeleteFileInput;
import com.sdi.castivate.utils.ImagePreview;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.RegisterRemoteApi;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nijamudhin on 3/28/2017.
 */

public class VoiceAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater = null;
    ProgressDialog pd;
    ArrayList<String> data = new ArrayList<>();
    Drawable placeholder;

    public VoiceAdapter(Context activity, ArrayList<String> data) {
        this.context = activity;
        this.data = data;
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
        String model = data.get(position);

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

      //      holder.imgProfileImage.setBackgroundColor(Color.TRANSPARENT);

            rowView.setTag(holder);

            holder.imgDelete.setTag(position);
            holder.imgProfileImage.setTag(position);
            holder.imgExpand.setTag(position);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        //  holder.imgProfileImage.position = position;
        try {
            if (Library.castivateAudio == "castivateAudio") {
                holder.imgDelete.setVisibility(View.GONE);

                if (data.get(position).equals("ADD")) {
                    data.remove(0);
                    //        holder.layImage.setVisibility(View.GONE);
                    //        holder.iv_AddPhoto.setVisibility(View.VISIBLE);

                } else if (data.get(position) != null && !data.get(position).equals("")) {

                    holder.layImage.setVisibility(View.VISIBLE);
                    holder.iv_AddPhoto.setVisibility(View.GONE);
                    //                for (int i = 0; i < data.size(); i++) {
    //                    new ImageDownload(context, holder.imgProfileImage, placeholder, position).execute(data.get(position));
                    Picasso.with(context).load(data.get(position)).placeholder(R.drawable.mic_icon2).into(holder.imgProfileImage);
    //            }
                }
            } else {
                if (data.get(position).equals("ADD")) {
                    holder.layImage.setVisibility(View.GONE);
                    holder.iv_AddPhoto.setVisibility(View.VISIBLE);

                } else if (data.get(position) != null && !data.get(position).equals("")) {

                    holder.layImage.setVisibility(View.VISIBLE);
                    holder.iv_AddPhoto.setVisibility(View.GONE);

                    //                for (int i = 0; i < data.size(); i++) {
    //                    new ImageDownload(context, holder.imgProfileImage, placeholder, position).execute(data.get(position));
                    Picasso.with(context).load(data.get(position)).placeholder(R.drawable.mic_icon2).into(holder.imgProfileImage);
    //            }
                }
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

                    /*if (data.get(position) != null && !data.get(position).equals("ADD")) {
                        Intent in = new Intent(context, AudioPreview.class);
                        in.putExtra("audio", data.get(position));
                        ((Activity) context).startActivity(in);
                    }*/


                    if (Library.castivateAudio == "castivateAudio") {
                        if (data.get(position) != null && !data.get(position).equals("ADD")) {
                            String value = data.get(position).toString();
                            int position = value.lastIndexOf("/");
                            position = position + 1;
                            String name = value.substring(position, value.length());

                            System.out.println("name = " + name);

                            String path = Environment.getExternalStorageDirectory().toString() + "/Casting_Folder/" + name;
                            File f = new File(path);

                            if (f.exists()) {
                                Intent intent = new Intent();
                                intent.putExtra("message", path);
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
                                        ((Activity) context).setResult(Activity.RESULT_OK, intent);
                                        ((Activity) context).finish();

                                    }
                                };
                                dw.execute(value, name);
                            }

                        }

                    } else {
                        if (data.get(position) != null && !data.get(position).equals("ADD")) {
                            Intent in = new Intent(context, AudioPreview.class);
                            String value = data.get(position).toString();
                            in.putExtra("audio", value);
                            ((Activity) context).startActivity(in);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowView;
    }

    public class ViewHolder {
        //LinearLayout rootLay;
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
        // Send the userName,password
        try {


            NewDeleteFileInput input = new NewDeleteFileInput(Library.getUserId(context), data.get(pos));

            RegisterRemoteApi.getInstance().setNewDeleteInput(input);

            // Call Login JSON
            RegisterRemoteApi.getInstance().getNewDeleteInput(context, new Callback<DeleteFileOutput>() {
                @Override
                public void success(DeleteFileOutput profileinfoOutput, Response response) {

                    if (profileinfoOutput.status == 200) {

                        Library.reduceUserProfileDetails(context, "audio");

                        //      MyProfile.files.remove(pos);
                        NewPhotoUpload.fileAudio.remove(pos);
                        NewPhotoUpload.voiceAdapter.notifyDataSetChanged();
                        closeProgress();

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

    private void closeProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
            closeProgress();
        }
    }
}
