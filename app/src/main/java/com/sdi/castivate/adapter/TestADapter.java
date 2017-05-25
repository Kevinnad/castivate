package com.sdi.castivate.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Murali on 14-Dec-16.
 */
public class TestADapter extends BaseAdapter {

    Context mContext;
    List<String> list;

    public TestADapter(Context mContext, List<String> list) {
        this.mContext = mContext;
        this.list = list;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder = null;

        if(convertView == null){
            myHolder = new MyHolder();
            convertView.setTag(myHolder);
        }else{

            myHolder = (MyHolder)convertView.getTag();
        }


        return convertView;
    }

    public class MyHolder{
        public TextView textView;
        public ImageView imageView;

    }
}
