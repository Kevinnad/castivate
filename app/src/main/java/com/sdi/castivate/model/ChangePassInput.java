package com.sdi.castivate.model;

/**
 * Created by androidusr1 on 12/10/16.
 */
public class ChangePassInput {
   /* "{
            ""userid"":""2072"",
            ""newPassword"": ""123@ram"",
            ""oldPassword"": ""ram@123""
}"*/

    public ChangePassInput(String userid, String newPassword, String oldPassword) {
        this.userid = userid;
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
    }

    public String userid;
    public String newPassword;
    public String oldPassword;


}
