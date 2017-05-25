package com.sdi.castivate.model;

/**
 * Created by Gnanaoly on 06-Oct-16.
 */
public class RegisterInput {


    public RegisterInput(String userid, String email, String password, String username, String gender,
                         String uniontype, String birthyear, String ethincity, String isNationwide,
                         String prefercities,String userDeviceId, String profileImage) {
        this.userid = userid;
        this.email = email;
        this.password = password;
        this.username = username;
        this.gender = gender;
        this.uniontype = uniontype;
        this.birthyear = birthyear;
        this.ethincity = ethincity;
        this.isNationwide = isNationwide;
        this.prefercities = prefercities;
        this.userDeviceId = userDeviceId;
        this.profileImage = profileImage;
    }

    public String userid;
    public String email;
    public String password;
    public String username;
    public String gender;
    public String uniontype;
    public String birthyear;
    public String ethincity;
    public String isNationwide;
    public String prefercities;
    public String userDeviceId;
    public String profileImage;
}
