package com.sdi.castivate.model;

/**
 * Created by nijamudhin on 4/19/2017.
 */

public class ThankCommentInput {
    public String talent_image_id;

    public String imageFav;

    public String imageId;

    public String email;

    public String name;

    public String userId;

    public String comment;

    public ThankCommentInput(String talent_image_id, String imageFav, String imageId, String email, String name, String userId, String comment, String deviceId) {
        this.talent_image_id = talent_image_id;
        this.imageFav = imageFav;
        this.imageId = imageId;
        this.email = email;
        this.name = name;
        this.userId = userId;
        this.comment = comment;
        this.deviceId = deviceId;
    }

    public String deviceId;
}
