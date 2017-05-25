package com.sdi.castivate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sdi.castivate.model.CastingDetailsModel;
import com.sdi.castivate.model.ChangePassResponse;
import com.sdi.castivate.model.PurchaseInput;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.RegisterRemoteApi;
import com.sdi.castivate.utils.billing.IabHelper;
import com.sdi.castivate.utils.billing.IabResult;
import com.sdi.castivate.utils.billing.Inventory;
import com.sdi.castivate.utils.billing.Purchase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by androidusr1 on 10/8/16.
 */
@SuppressWarnings({"deprecation", "unchecked"})
@SuppressLint({"ResourceAsColor", "InlinedApi", "ShowToast", "UseSparseArrays"})
public class CastingPlan extends Activity implements View.OnClickListener {

    String payment = "$9.99 for 12 Months";
    int payment_type;
    IabHelper mHelper;
    String base64EncodedPublicKey;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    Context context;
    ProgressDialog pDialog;
    String CalledBy = "";
    RadioButton radio_year, radio_month;
    String planType;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    private RadioGroup radioPaymentGroup;
    private LinearLayout casting_plan_back_icon;
    private Button btn_subscribe_now;
    private RadioButton rb;
    private ArrayList<CastingDetailsModel> selectedCastingDetailsModels = new ArrayList<CastingDetailsModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_plan);
        context = this;

        if (getIntent().hasExtra("CalledBy")) {
            CalledBy = getIntent().getStringExtra("CalledBy");
        }

        findElements();

        callHelper("create");


        mGotInventoryListener
                = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result,
                                                 Inventory inventory) {

                if (result.isFailure()) {

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

                    if (!purchased.equals("0")) {
                        /*Intent intent = new Intent(CastingLogin.this, CastingScreen.class);
                        startActivity(intent);
                        finish();*/
                        planType = purchased;
                        purchaseRetrofit();

                    } else {
                        clickSub();
                    }
                }
            }
        };

    }

    private void callHelper(final String calledBy) {

        base64EncodedPublicKey = getResources().getString(R.string.base64encodedpublickey);

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Toast.makeText(context, "Problem setting up In-app Billing:", Toast.LENGTH_LONG).show();

                } else {

                    if (calledBy.equals("")) {
                        clickSub();
                    }
                }
            }
        });
    }

    private void findElements() {

        casting_plan_back_icon = (LinearLayout) findViewById(R.id.casting_plan_back_icon);
        radioPaymentGroup = (RadioGroup) findViewById(R.id.radioPayment);
        btn_subscribe_now = (Button) findViewById(R.id.btn_subscribe_now);
        radio_month = (RadioButton) findViewById(R.id.radio_month);
        radio_year = (RadioButton) findViewById(R.id.radio_year);

        btn_subscribe_now.setOnClickListener(this);
        casting_plan_back_icon.setOnClickListener(this);

        try {
            selectedCastingDetailsModels = (ArrayList<CastingDetailsModel>) getIntent().getSerializableExtra("selectedCastingDetailsModels");
        } catch (Exception e) {
            e.printStackTrace();
        }


        radioPaymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                rb = (RadioButton) findViewById(checkedId);

                payment = String.valueOf(rb.getText());

            }
        });

        casting_plan_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CalledBy.equals("Register")) {
                    Intent intent = new Intent(CastingPlan.this, CastingScreen.class);
                    startActivity(intent);
                    finish();
                } else if (CalledBy.equals("MyProfile")) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                } else {
                    finish();
                }


            }
        });

        mPurchaseFinishedListener
                = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                if (result.isFailure()) {
                    Log.d("Billing Failure", "Error purchasing: " + result);
                    String url = android.os.Environment.getExternalStorageDirectory().toString();

                    File myFile = new File(url + "/myCastivateTest.txt");
                    try {
                        if (!myFile.exists())
                            myFile.createNewFile();

                        FileOutputStream fOut = new FileOutputStream(myFile);
                        OutputStreamWriter myOutWriter =
                                new OutputStreamWriter(fOut);
                        myOutWriter.append("Purchase Failure");
                        myOutWriter.append(result.toString());
                        myOutWriter.append(purchase.toString());
                        myOutWriter.close();
                        fOut.close();


                        Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                } else if (purchase.getSku().equals(Library.monthly) || purchase.getSku().equals(Library.yearly)) {
                    // give user access to premium content and update the UI

                    String url = android.os.Environment.getExternalStorageDirectory().toString();

                    File myFile = new File(url + "/myCastivateTest.txt");
                    try {
                        if (!myFile.exists())
                            myFile.createNewFile();

                        FileOutputStream fOut = new FileOutputStream(myFile);
                        OutputStreamWriter myOutWriter =
                                new OutputStreamWriter(fOut);
                        myOutWriter.append("Purchase Success");
                        myOutWriter.append(result.toString());
                        myOutWriter.append(purchase.toString());
                        myOutWriter.close();
                        fOut.close();

                        purchaseRetrofit();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.casting_plan_back_icon:
                finish();
                break;
            case R.id.btn_subscribe_now:

                checkPurchase();

                break;

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

    public void clickSub() {
        try {

            String plan = "";

            if (radio_month.isChecked()) {
                plan = Library.monthly;
                planType = "2";

            } else {
                plan = Library.yearly;
                planType = "1";

            }

            mHelper.launchSubscriptionPurchaseFlow(this, plan, 10001,
                    mPurchaseFinishedListener, "");

        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();

            if (mHelper != null) {
                try {
                    mHelper.dispose();
                } catch (IabHelper.IabAsyncInProgressException e1) {
                    e1.printStackTrace();
                }
            }
            mHelper = null;

            callHelper("");

        }
    }

    private void purchaseRetrofit() {
        // TODO Auto-generated method stub

        // Progress Dialog
        pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        // Send the userName,password

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());

        PurchaseInput input = new PurchaseInput(Library.getUserId(context), formattedDate, planType, "1");

        //RegisterInput input = new RegisterInput("2072", "ramsdevelop@yahoo.com", "ram@123", "Ganesh", "1","1","1983","Caucasian","2","Pondy, Chennai, Bangalore");
        RegisterRemoteApi.getInstance().setPurchaseInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().posAppPurchase(context, new Callback<ChangePassResponse>() {
            @Override
            public void success(ChangePassResponse purchaseResponse, Response response) {
                closeProgress();

                if (purchaseResponse.status == 200) {

                    SharedPreferences prefs = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("isPurchased", "1");
//                    editor.putString("PurchasedDate", loginResponse.PurchasedDate);

                    editor.commit();

                    if (CalledBy.equals("Register")) {
                        Intent intent = new Intent(CastingPlan.this, CastingScreen.class);
                        startActivity(intent);
                        finish();
                    } else if (CalledBy.equals("MyProfile")) {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        finish();
                    }
                } else {
                    Library.alert(context, purchaseResponse.message);

                }


            }

            @Override
            public void failure(RetrofitError error) {
                closeProgress();
//                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void closeProgress() {
        // TODO Auto-generated method stub
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void checkPurchase() {

        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (Exception e) {
            e.printStackTrace();
            if (mHelper != null) {
                try {
                    mHelper.dispose();
                } catch (IabHelper.IabAsyncInProgressException e1) {
                    e1.printStackTrace();
                }
            }
            mHelper = null;

            callHelper("");
        }

    }

}
