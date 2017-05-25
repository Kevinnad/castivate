package com.sdi.castivate.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sdi.castivate.NewPhotoUpload;
import com.sdi.castivate.R;
import com.sdi.castivate.WebPreview;
import com.sdi.castivate.image.ContactImageView;
import com.sdi.castivate.model.DeleteFileOutput;
import com.sdi.castivate.model.NewDeleteFileInput;
import com.sdi.castivate.utils.ImagePreview;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.RegisterRemoteApi;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nijamudhin on 5/3/2017.
 */

public class FileAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater = null;
    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> listValue = new ArrayList<>();
    Drawable placeholder;

    public FileAdapter(Context activity, ArrayList<String> data, ArrayList<String> listValue) {
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
        return position;
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

        holder.imgProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.get(position) != null && !data.get(position).equals("ADD")) {
                    Intent in = new Intent(context, WebPreview.class);
                    in.putExtra("image", data.get(position));
                    ((Activity) context).startActivity(in);
                }
            }
        });


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
