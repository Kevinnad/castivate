package com.sdi.castivate.model;

/**
 * Created by Gnanaoly on 20-Jan-17.
 */

public class ApplyCastingInput {

    public String userid;
    public String role_id;
    public String enthicity;
    public String age_range;
    public String gender;

    public ApplyCastingInput(String userid, String role_id, String enthicity, String age_range, String gender) {
        this.userid = userid;
        this.role_id = role_id;
        this.enthicity = enthicity;
        this.age_range = age_range;
        this.gender = gender;
    }

}
