package com.sdi.castivate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.billing.IabHelper;
import com.sdi.castivate.utils.billing.IabResult;
import com.sdi.castivate.utils.billing.Inventory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Balachandar on 17-Apr-15.
 */

public class SplashScreen extends Activity {
    Context context;
    SharedPreferences sharedpreferences;
    boolean initalScreen;
    IabHelper mHelper;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    int Ratecount;
    int nwdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        context = this;


        // Add code to print out the key hash facebook
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.sdi.castivate",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }



        mHelper = new IabHelper(this, getResources().getString(R.string.base64encodedpublickey));

        checkRate();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                sharedpreferences = getSharedPreferences(Library.MyPREFERENCES,
                        Context.MODE_PRIVATE);
                initalScreen = sharedpreferences.getBoolean(
                        Library.INITIAL_SCREEN, false);
                if (initalScreen == false) {
                    Library.helpOverlayView = true;
                    startActivity(new Intent(SplashScreen.this,
                            InitialFilterScreen.class));
                } else {
                    CastingScreen.isFavScreen = false;

                    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                        public void onIabSetupFinished(IabResult result) {
                            if (!result.isSuccess()) {
                                // Oh no, there was a problem.
                                Log.d("Billing", "Problem setting up In-app Billing: " + result);
                            } else {

                                Log.d("Billing", "setting up In-app Billing: " + result);

                                if (!Library.getEmailId(context).equals("") && Library.getIsPurchased(context).equals("1")) {
//                                if (!Library.getEmailId(context).equals("")) {
                                    checkPurchase();
                                } else {
                                    Intent in = new Intent(SplashScreen.this,
                                            CastingScreen.class);
                                    in.putExtra("CalledBy", "Spalah");
                                    startActivity(in);
                                }


                            }
                        }
                    });

                }
            }
        }, 2000);

        mGotInventoryListener
                = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result,
                                                 Inventory inventory) {

//                Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();

                if (result.isFailure()) {

                    String url = android.os.Environment.getExternalStorageDirectory().toString();

                    File myFile = new File(url + "/myCastivateTest.txt");
                    try {
                        if (!myFile.exists())
                            myFile.createNewFile();

                        FileOutputStream fOut = new FileOutputStream(myFile);
                        OutputStreamWriter myOutWriter =
                                new OutputStreamWriter(fOut);
                        myOutWriter.append("Check Purchase");
                        myOutWriter.append("Failure");
                        myOutWriter.close();
                        fOut.close();

                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                } else {
                    // does the user have the premium upgrade?
                    Boolean mIsPremiumMonth = inventory.hasPurchase(Library.monthly);
                    Boolean mIsPremiumYear = inventory.hasPurchase(Library.yearly);

                    String url = android.os.Environment.getExternalStorageDirectory().toString();

                    File myFile = new File(url + "/myCastivateTest.txt");
                    try {
                        if (!myFile.exists())
                            myFile.createNewFile();

                        FileOutputStream fOut = new FileOutputStream(myFile);
                        OutputStreamWriter myOutWriter =
                                new OutputStreamWriter(fOut);
                        myOutWriter.append("Check Purchase");
                        myOutWriter.append(Library.monthly + mIsPremiumMonth.toString());
                        myOutWriter.append(Library.yearly + mIsPremiumYear.toString());
                        myOutWriter.close();
                        fOut.close();

                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                    String purchased;
                    if (mIsPremiumYear) {
                        purchased = "1";
                    } else if (mIsPremiumMonth) {
                        purchased = "2";
                    } else {
                        purchased = "0";
                    }

                    Intent in = new Intent(SplashScreen.this,
                            CastingScreen.class);
                    in.putExtra("CalledBy", "Spalah");
                    in.putExtra("Purchase", purchased);
                    startActivity(in);
                }
            }
        };

    }

    private void checkPurchase() {

        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (Exception e) {
            e.printStackTrace();

            String url = android.os.Environment.getExternalStorageDirectory().toString();

            File myFile = new File(url + "/myCastivateTest.txt");
            try {
                if (!myFile.exists())
                    myFile.createNewFile();

                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);
                myOutWriter.append("Check Purchase Failure Exception");
                myOutWriter.append(e.toString());
                myOutWriter.close();
                fOut.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    public void checkRate() {
        SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);

        Ratecount = prefs.getInt("Ratecount", 0);
        nwdate = prefs.getInt("ndate", 0);
        boolean getRateFlag = prefs.getBoolean(Library.RATEIT_FLAG, false);
        int dateCount = prefs.getInt("dateCount", 0);

        Ratecount++;
        SharedPreferences.Editor editor = getSharedPreferences("my_pref",
                MODE_PRIVATE).edit();
        editor.putInt("Ratecount", Ratecount);
        editor.commit();

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                .replace("-", "");
        int datei = Integer.parseInt(date);

        if (datei != nwdate) {
            dateCount++;
            editor.putInt("dateCount", dateCount);
            editor.putInt("ndate", datei);
            editor.commit();

        } else {
            dateCount = 3;
            editor.putInt("dateCount", dateCount);
            editor.commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
        mHelper = null;
    }
}
