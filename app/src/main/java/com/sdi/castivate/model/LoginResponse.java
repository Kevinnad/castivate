package com.sdi.castivate.model;

import android.util.Base64;

/**
 * Created by androidusr1 on 11/10/16.
 */
public class LoginResponse {

    /*{"id":"120","userid":"2125","username":"test","email":"test101@yopmail.com","password":"12345678",
                        "zipcode":"","gender":"1","uniontype":"1","birthyear":"1900","ethincity":"Caucasian","isNationwide":"1",
                        "PurchasedDate":"","plantype":0,"isPurchased":0,"apply_Date":"2016-10-11 14:55:34",
                        "Expiry_date":"2016-11-09 00:00:00","preferCities":"#gdfgdf##Tampa#FL#Unit#Richmond#VA#U##gdfgdf",
                        "message":"User exist","status":200}*/

    public String id;
    public String userid;
    public String username;
    public String email;
    public String zipcode;
    public String gender;
    public String uniontype;
    public String birthyear;
    public String ethincity;
    public String isNationwide;
    public String PurchasedDate;
    public String plantype;
    public String apply_Date;
    public String Expiry_date;
    public String isPurchased;
    public String preferCities;
    public String message;
    public int status;
    // by nijam
    public String profileImage;

}
