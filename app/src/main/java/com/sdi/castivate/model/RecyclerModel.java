package com.sdi.castivate.model;

import android.graphics.Bitmap;

/**
 * Created by nijamudhin on 1/12/2017.
 */

public class RecyclerModel {
   public int type;
    public Bitmap thumbnail;
    public String path;

    public RecyclerModel(int type, Bitmap thumbnail, String path) {
        this.type = type;
        this.thumbnail = thumbnail;
        this.path = path;
    }

}
