package com.sdi.castivate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sdi.castivate.R;
import com.sdi.castivate.model.CommentModal;
import com.sdi.castivate.model.Comments;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nijamudhin on 4/19/2017.
 */



public class CommentAdapter extends BaseAdapter {
    private ArrayList<Comments> mData;
    Context context;
    public CommentAdapter(Context context, ArrayList<Comments> data){
        this.mData = data;
        this.context=context;
    }
    @Override
    public int getCount() {
        return mData.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        VIewHolder holder = null;


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = convertView;
        if (convertView == null) {
            holder = new VIewHolder();
            rowView = new View(context);
            rowView = inflater.inflate(R.layout.comment_list_items, null);
            holder.comment = (TextView) rowView.findViewById(R.id.comment);
            holder.name = (TextView) rowView.findViewById(R.id.name);
            rowView.setTag(holder);
        } else {
            holder = (CommentAdapter.VIewHolder) rowView.getTag();
        }

        try {
            holder.comment.setText(mData.get(position).comment);
            holder.name.setText(mData.get(position).name);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return rowView;
    }



    public class VIewHolder {
        //LinearLayout rootLay;
        TextView name,comment;
    }

}