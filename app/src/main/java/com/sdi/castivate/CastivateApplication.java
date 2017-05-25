package com.sdi.castivate;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crittercism.app.Crittercism;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.sdi.castivate.model.CastingDetailsModel;
import com.sdi.castivate.model.DriveModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by Balachandar on 21-Apr-15.
 */

public class CastivateApplication extends MultiDexApplication {

    private static CastivateApplication instance;
    static Context context;

    private ArrayList<DriveModel> commonDriveModels;

    public ArrayList<DriveModel> getCommonDriveModels() {
        return commonDriveModels;
    }

    public void setCommonDriveModels(ArrayList<DriveModel> commonDriveModels) {
        this.commonDriveModels = commonDriveModels;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();


        instance = this;
        context = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

//        Calendar c = Calendar.getInstance();
//        int today = Integer.valueOf(String.valueOf(c.get(Calendar.DATE)));
//            SharedPreferences new_maintain_pref = getSharedPreferences("new_maintain_pref", MODE_PRIVATE);
//            String new_str = new_maintain_pref.getString("new", "");
//            int date1= new_maintain_pref.getInt("date", 0);
//            if(new_str.equals("")){
//                int date = Integer.valueOf(String.valueOf(c.get(Calendar.DATE)));
//                SharedPreferences.Editor editor = new_maintain_pref.edit();
//                editor.putInt("date",date);
//                editor.commit();
//            }else if(today>date1){
//
//                SharedPreferences.Editor editor = new_maintain_pref.edit();
//                editor.putInt("date",today);
//                editor.putString("new","");
//                editor.commit();
//            }

        Crittercism.initialize(getApplicationContext(), "e8eec61791c540bba37ba7cfb33c253e00555300");
    }

    public static Context getAppContext() {
        return CastivateApplication.context;
    }


    /* Static 'instance' method */
    public static CastivateApplication getInstance() {
        return instance;
    }

    /* Other methods protected by singleton-ness */
    protected static void demoMethod() {
        System.out.println("demoMethod for singleton");

    }

    HashMap<Integer, ArrayList<CastingDetailsModel>> hashMap = new HashMap<Integer, ArrayList<CastingDetailsModel>>();


    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}