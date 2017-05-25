package com.sdi.castivate.model;

import android.graphics.Bitmap;

/**
 * Created by androidusr1 on 19/10/16.
 */
public class DataPathModel {

    public int ids;
    public boolean selected;
    public String arrPath;
    public int indexPosition;
    public ImageUrl imageUpdates;
    public Bitmap imagebitmap;

    public Bitmap getImagebitmap() {
        return imagebitmap;
    }

    public void setImagebitmap(Bitmap imagebitmap) {
        this.imagebitmap = imagebitmap;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public int getIndexPosition() {
        return indexPosition;
    }

    public void setIndexPosition(int indexPosition) {
        this.indexPosition = indexPosition;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getArrPath() {
        return arrPath;
    }

    public void setArrPath(String arrPath) {
        this.arrPath = arrPath;
    }

    public ImageUrl getImageUpdates() {
        return imageUpdates;
    }

    public void setImageUpdates(ImageUrl imageUpdates) {
        this.imageUpdates = imageUpdates;
    }
}
