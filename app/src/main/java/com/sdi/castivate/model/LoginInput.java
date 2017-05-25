package com.sdi.castivate.model;

/**
 * Created by androidusr1 on 11/10/16.
 */
public class LoginInput {

    public String email;
    public String password;
    public String userid;
    public String userDeviceId;

    public LoginInput(String email, String password, String userid,String userDeviceId) {
        this.email = email;
        this.password = password;
        this.userid = userid;
        this.userDeviceId = userDeviceId;
    }
}
