package com.sdi.castivate.model;

/**
 * Created by Gnanaoly on 20-Jan-17.
 */

public class DeleteFileInput {

    public String userid;
    public String doc_id;

    public DeleteFileInput(String userid, String doc_id) {
        this.userid = userid;
        this.doc_id = doc_id;
    }
}
