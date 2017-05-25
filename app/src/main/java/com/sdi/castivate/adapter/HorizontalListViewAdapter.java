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

import com.sdi.castivate.MyProfile;
import com.sdi.castivate.R;
import com.sdi.castivate.image.ContactImageView;
import com.sdi.castivate.image.ImageDownload;
import com.sdi.castivate.model.DeleteFileInput;
import com.sdi.castivate.model.DeleteFileOutput;
import com.sdi.castivate.model.FileModel;
import com.sdi.castivate.utils.ImagePreview;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.RegisterRemoteApi;
import com.sdi.castivate.utils.VideoActivity;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class HorizontalListViewAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater = null;

    ArrayList<FileModel> list = new ArrayList<FileModel>();
    Drawable placeholder;

    public HorizontalListViewAdapter(Context activity, ArrayList<FileModel> list) {
        this.context = activity;
        this.list = list;
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
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        FileModel model = list.get(position);

        View rowView = convertView;
        if (convertView == null) {
            rowView = inflater.inflate(R.layout.cardview_item, null);
            holder = new ViewHolder();
            holder.imgProfileImage = (ContactImageView) rowView.findViewById(R.id.imgProfileImage);
            holder.imgDelete = (ImageView) rowView.findViewById(R.id.imgDelete);
            holder.imgPlay = (ImageView) rowView.findViewById(R.id.imgPlay);
            holder.imgExpand = (ImageView) rowView.findViewById(R.id.imgExpand);

            rowView.setTag(holder);

            holder.imgDelete.setTag(position);
            holder.imgProfileImage.setTag(position);
            holder.imgExpand.setTag(position);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.imgProfileImage.position = position;

        if (list.get(position).type.equals("image")) {

            new ImageDownload(context, holder.imgProfileImage, placeholder, position).execute(list.get(position).url);
            holder.imgPlay.setVisibility(View.GONE);
//            Picasso.with(context).load(list.get(position).url).into(holder.imgProfileImage);

        } else if (list.get(position).type.equals("video")) {

            new ImageDownload(context, holder.imgProfileImage, placeholder, position).execute(list.get(position).thumbUrl);
            holder.imgPlay.setVisibility(View.VISIBLE);

//            Picasso.with(context).load(list.get(position).thumbUrl).into(holder.imgProfileImage);

        }

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();

                deletePhoto(pos);

            }
        });

        holder.imgExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();

                if (list.get(position).type.equals("image")) {
                    Intent in = new Intent(context, ImagePreview.class);
                    in.putExtra("image", list.get(position).url);
                    ((Activity) context).startActivity(in);
                } else {
                    if (list.get(position).type.equals("video")) {
                        Intent in = new Intent(context, VideoActivity.class);
                        in.putExtra("video", list.get(position).url);
                        ((Activity) context).startActivity(in);
                    }
                }
            }
        });

        return rowView;
    }

    public class ViewHolder {
        //LinearLayout rootLay;
        ImageView imgDelete, imgPlay, imgExpand;
        ContactImageView imgProfileImage;
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


            DeleteFileInput input = new DeleteFileInput(Library.getUserId(context), list.get(pos).doc_id);

               RegisterRemoteApi.getInstance().setDeleteFileInput(input);

            // Call Login JSON
            RegisterRemoteApi.getInstance().postDeleteFile(context, new Callback<DeleteFileOutput>() {
                @Override
                public void success(DeleteFileOutput profileinfoOutput, Response response) {

                    if (profileinfoOutput.status == 200) {
                        closeProgress();

                        Library.reduceUserProfileDetails(context, MyProfile.files.get(pos).type);

                        MyProfile.files.remove(pos);
                        MyProfile.adapter.notifyDataSetChanged();


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
