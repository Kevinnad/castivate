package com.sdi.castivate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdi.castivate.EditProfile;
import com.sdi.castivate.MyProfile;
import com.sdi.castivate.R;
import com.sdi.castivate.Signup;

import java.util.List;

/**
 * Created by androidusr1 on 12/10/16.
 */
public class LocationAdapter extends BaseAdapter {

    List<String> locationList;
    private Context mContext;
    private LayoutInflater mInflater;
    public String calledBy;


    public LocationAdapter(Context context,
                           List<String> locationList, String calledBy) {
        super();
        this.mContext = context;
        this.locationList = locationList;
        mInflater = LayoutInflater.from(mContext);
        this.calledBy = calledBy;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return locationList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return locationList.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View rootView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;

        if (rootView == null) {
            rootView = mInflater.inflate(R.layout.item_location, null);
            holder = new ViewHolder();

            holder.txtLocation = (TextView) rootView
                    .findViewById(R.id.txtLocation);
            holder.imgRemove = (ImageView) rootView
                    .findViewById(R.id.imgRemove);


            rootView.setTag(holder);

            holder.imgRemove.setTag(position);

        } else {
            holder = (ViewHolder) rootView.getTag();
        }

        holder.txtLocation.setText(locationList.get(position));

        holder.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();

                if (calledBy.equals("Edit")) {
                    //    EditProfile.rl1.setVisibility(View.VISIBLE);
                    // changed by nijam on workflow IV
                    MyProfile.rl1.setVisibility(View.VISIBLE);
                    try {
                        locationList.remove(pos);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (calledBy.equals("Sign")) {
                    Signup.rl1.setVisibility(View.VISIBLE);
                    try {
                        locationList.remove(pos);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });


        return rootView;
    }

    public static class ViewHolder {
        public TextView txtLocation;
        public static ImageView imgRemove;

    }
}
