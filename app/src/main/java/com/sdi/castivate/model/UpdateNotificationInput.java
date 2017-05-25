package com.sdi.castivate.model;

/**
 * Created by Gnanaoly on 01-Feb-17.
 */

public class UpdateNotificationInput {

    public String userid;
    public String notification_type;
    public String type_value;
    public String notify_status;

    public UpdateNotificationInput(String userid, String notification_type, String type_value, String notify_status) {
        this.userid = userid;
        this.notification_type = notification_type;
        this.type_value = type_value;
        this.notify_status = notify_status;
    }

}
