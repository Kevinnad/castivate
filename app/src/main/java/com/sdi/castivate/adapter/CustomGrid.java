package com.sdi.castivate.adapter;

/**
 * Created by androidusr1 on 24/4/17.
 */

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdi.castivate.NewPhotoUpload;
import com.sdi.castivate.R;
import com.sdi.castivate.model.DeleteModal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomGrid extends BaseAdapter {
    private Context mContext;
    private ArrayList<DeleteModal> Imageid;
    ViewHolder holder = null;


    public CustomGrid(Context c, ArrayList<DeleteModal> Imageid) {
        mContext = c;
        this.Imageid = Imageid;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Imageid.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return Imageid.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid = convertView;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = inflater.inflate(R.layout.new_deleteitem, null);
            holder = new ViewHolder();

           // holder.overView = (ImageView) grid.findViewById(R.id.imgExpand);
            holder.imageView = (ImageView) grid.findViewById(R.id.imgProfileImage);
            grid.setTag(holder);


        } else {
            holder = (ViewHolder) grid.getTag();
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (NewPhotoUpload.isEdit) {
                    int a = (int) view.getTag();
                    if (Imageid.get(a).isDel) {
                        Imageid.get(a).isDel = false;

                    } else {
                        Imageid.get(a).isDel = true;
                    }
                    notifyDataSetChanged();


                }
            }
        });


        DeleteModal delModal = Imageid.get(position);

        if (delModal.img_thumb.equals("audiourl")) {
            holder.imageView.setImageResource(R.drawable.mic_icon2);
        } else {
            Picasso.with(mContext).load(delModal.img_thumb).into(holder.imageView);
        }

        holder.imageView.setTag(position);

        if (delModal.isDel) {
         //   holder.overView.setVisibility(View.VISIBLE);
            holder.imageView.setBackgroundResource(R.drawable.imgbg2);
        } else {
          //  holder.overView.setVisibility(View.GONE);
            holder.imageView.setBackgroundResource(R.drawable.imgbg);
        }

        return grid;
    }


    public class ViewHolder {

      //  public ImageView overView;
        public ImageView imageView;

    }
}