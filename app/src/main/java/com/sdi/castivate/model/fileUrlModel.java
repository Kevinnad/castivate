package com.sdi.castivate.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by twilightuser on 21/9/16.
 */
public class fileUrlModel implements Serializable {

    private String fileUrl;

    private Bitmap thumbnail;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}
