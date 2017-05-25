package com.sdi.castivate.model;

/**
 * Created by androidusr1 on 12/10/16.
 */
public class UpdatePersonInput {

    public UpdatePersonInput(String userid, String email, String username, String gender, String uniontype, String birthyear, String ethincity, String isNationwide, String prefercities, String profileImage) {
        this.userid = userid;
        this.email = email;
        this.username = username;
        this.gender = gender;
        this.uniontype = uniontype;
        this.birthyear = birthyear;
        this.ethincity = ethincity;
        this.isNationwide = isNationwide;
        this.prefercities = prefercities;
        this.profileImage = profileImage;
    }

    public String userid;
    public String email;
    public String username;
    public String gender;
    public String uniontype;
    public String birthyear;
    public String ethincity;
    public String isNationwide;
    public String prefercities;
    public String profileImage;

    /*"{
            ""userid"":""2073"",
            ""email"": ""jpp31@yopmail.com"",
            ""username"": ""Ganesh12"",
            ""gender"": ""1"",
            ""uniontype"": ""1"",
            ""birthyear"": ""1983"",
            ""ethincity"": ""Caucasian"",
            ""isNationwide"":""2"",
            ""prefercities"":""Pondy#chennai#Banglore""
}"*/

}
