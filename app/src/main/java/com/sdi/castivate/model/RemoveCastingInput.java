package com.sdi.castivate.model;

/**
 * Created by nijamudhin on 3/10/2017.
 */

public class RemoveCastingInput {
    public String userId;
    public String castingId;

    public RemoveCastingInput(String userId, String castingId) {
        this.userId = userId;
        this.castingId = castingId;
    }
}
