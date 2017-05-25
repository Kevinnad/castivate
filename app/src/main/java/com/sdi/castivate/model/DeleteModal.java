package com.sdi.castivate.model;

/**
 * Created by androidusr1 on 24/4/17.
 */
public class DeleteModal {

    public String img;
    public String img_thumb;
    public boolean isDel;

    public DeleteModal(String img, String img_thumb, boolean isDel) {
        this.img = img;
        this.img_thumb = img_thumb;
        this.isDel = isDel;
    }
}
