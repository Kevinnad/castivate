package com.sdi.castivate.model;

/**
 * Created by androidusr1 on 24/10/16.
 */
public class PurchaseInput {

    public String userid;
    public String PurchasedDate;
    public String plantype;
    public String isPurchased;
    public PurchaseInput(String userid, String purchasedDate, String plantype, String isPurchased) {
        this.userid = userid;
        PurchasedDate = purchasedDate;
        this.plantype = plantype;
        this.isPurchased = isPurchased;
    }


}
