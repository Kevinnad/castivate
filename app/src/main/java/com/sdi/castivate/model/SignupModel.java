package com.sdi.castivate.model;

/**
 * Created by twilightuser on 5/10/16.
 */
public class SignupModel {

    private String userid;
    private String email;
    private String password;
    private String username;
    private String gender;
    private String uniontype;
    private String birthyear;
    private String ethincity;
    private String isNAtionwide;
    private String prefercities;



    public SignupModel(String userid, String email, String password, String username, String gender, String uniontype, String birthyear, String ethincity, String isNAtionwide, String prefercities ) {
        this.userid = userid;
        this.email = email;
        this.password = password;
        this.username = username;
        this.gender = gender;
        this.uniontype = uniontype;
        this.birthyear = birthyear;
        this.ethincity = ethincity;
        this.isNAtionwide = isNAtionwide;
        this.prefercities = prefercities;
    }




    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUniontype() {
        return uniontype;
    }

    public void setUniontype(String uniontype) {
        this.uniontype = uniontype;
    }

    public String getBirthyear() {
        return birthyear;
    }

    public void setBirthyear(String birthyear) {
        this.birthyear = birthyear;
    }

    public String getEthincity() {
        return ethincity;
    }

    public void setEthincity(String ethincity) {
        this.ethincity = ethincity;
    }

    public String getIsNAtionwide() {
        return isNAtionwide;
    }

    public void setIsNAtionwide(String isNAtionwide) {
        this.isNAtionwide = isNAtionwide;
    }

    public String getPrefercities() {
        return prefercities;
    }

    public void setPrefercities(String prefercities) {
        this.prefercities = prefercities;
    }









}
