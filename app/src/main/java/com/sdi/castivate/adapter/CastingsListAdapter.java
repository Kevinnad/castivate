package com.sdi.castivate.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdi.castivate.R;
import com.sdi.castivate.model.CastingDetailsModel;
import com.sdi.castivate.utils.Library;

import static android.content.Context.MODE_PRIVATE;

public class CastingsListAdapter extends BaseAdapter {

    Context activity;
    private LayoutInflater inflater = null;
    public static boolean REMOVECASTING = false;

    //ListInterface listInterface;

    ArrayList<CastingDetailsModel> list = new ArrayList<CastingDetailsModel>();

    CastingsListAdapter adapetr;

    public CastingsListAdapter(Context activity, ArrayList<CastingDetailsModel> list/*, ListInterface listInterface*/) {
        this.activity = activity;
        this.list = list;
//		this.listInterface = listInterface;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        VIewHolder holder = null;
        CastingDetailsModel model = list.get(position);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = convertView;
        if (convertView == null) {
            holder = new VIewHolder();
            rowView = new View(activity);
            rowView = inflater.inflate(R.layout.casting_list_item, null);

            holder.castingTitle = (TextView) rowView.findViewById(R.id.cast_title_no_image);
            holder.castingPaidStatus = (TextView) rowView.findViewById(R.id.paid_status_no_image);
            holder.castingType = (TextView) rowView.findViewById(R.id.cast_type_no_image);
            //by nijam
            holder.castingCity = (TextView) rowView.findViewById(R.id.cast_title_city);
            holder.castingState = (TextView) rowView.findViewById(R.id.cast_title_state);
            holder.newIcon = (ImageView) rowView.findViewById(R.id.new_icon);

            holder.layApplied = (LinearLayout) rowView.findViewById(R.id.layApplied);


            //holder.rootLay = (LinearLayout) rowView.findViewById(R.id.rootLay);

            rowView.setTag(holder);

//			holder.rootLay.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//
//					CastingListDetailsModel model = list.get(position);
////					listInterface.getList(model);
//				}
//			});

        } else {
            holder = (VIewHolder) rowView.getTag();
            //rowView = (View) convertView;
        }

        holder.castingTitle.setText(model.castingTitle/*.trim()*/);
        holder.castingPaidStatus.setText(model.castingPaidStatus);
        holder.castingType.setText(model.castingType);

        // by nijam
        holder.castingCity.setText(model.country);
        holder.castingState.setText(", " + model.state);
        holder.castingCity.setTextColor(Color.parseColor("#04839E"));
        holder.castingState.setTextColor(Color.parseColor("#04839E"));

        try {
            if (model.isNew.equals("1")) {

                SharedPreferences new_maintain_pref = activity.getSharedPreferences("new_maintain_pref", MODE_PRIVATE);
                String new_str = new_maintain_pref.getString("new", "");
                if (!new_str.contains(model.casting_id + "")) {
                    holder.newIcon.setVisibility(View.VISIBLE);
                } else {
                    holder.newIcon.setVisibility(View.GONE);
                }


            } else {
                holder.newIcon.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (model.applyFlag.equals("1")) {
            // edited by nijam on workflow4
            holder.layApplied.setVisibility(View.GONE);
            //  holder.layApplied.setVisibility(View.VISIBLE);
            holder.castingTitle.setTextColor(Color.parseColor("#31BC41"));
            REMOVECASTING = true;


        } else {
            holder.castingTitle.setTextColor(Color.parseColor("#2F2F2F"));

            holder.layApplied.setVisibility(View.GONE);
        }

        // rowView.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // CastingScreen.castingsList.setVisibility(View.GONE);
        // CastingScreen.castingViewNoImage.setVisibility(View.VISIBLE);
        // Toast.makeText(activity, "vvv", Toast.LENGTH_LONG).show();
        //
        // }
        // });

        return rowView;
    }

    public class VIewHolder {
        //LinearLayout rootLay;
        TextView castingTitle, castingPaidStatus, castingType, castingCity, castingState;
        LinearLayout layApplied;
        ImageView newIcon;
    }

    public synchronized void refreshAdapter(ArrayList<CastingDetailsModel> items) {
        // itemDetailsrrayList.clear();
        list = items;
        notifyDataSetChanged();
    }

}
