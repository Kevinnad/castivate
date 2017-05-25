package com.sdi.castivate.model;

/**
 * Created by nijamudhin on 3/6/2017.
 */

public class UpdateAutoSubmitInput {
    private String status;
    private String userId;

    public UpdateAutoSubmitInput(String status, String userId) {
        this.status = status;
        this.userId = userId;
    }
}
