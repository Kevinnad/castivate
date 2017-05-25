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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.util.StringUtils;
import com.sdi.castivate.model.CastingDetailsModel;
import com.sdi.castivate.model.ChangePassResponse;
import com.sdi.castivate.model.LoginInput;
import com.sdi.castivate.model.LoginResponse;
import com.sdi.castivate.model.PurchaseInput;
import com.sdi.castivate.utils.KeyboardUtility;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.Network;
import com.sdi.castivate.utils.RegisterRemoteApi;
import com.sdi.castivate.utils.billing.IabHelper;
import com.sdi.castivate.utils.billing.IabResult;
import com.sdi.castivate.utils.billing.Inventory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by androidusr1 on 11/8/16.
 */
@SuppressWarnings({"deprecation", "unchecked"})
@SuppressLint({"ResourceAsColor", "InlinedApi", "ShowToast", "UseSparseArrays"})
public class CastingLogin extends Activity implements View.OnClickListener {
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    LinearLayout casting_login_back_icon;
    RelativeLayout rel_casting_login;
    TextView txt_forgot, textView3;
    EditText et_email, et_password;
    Button btn_signin;
    String email, password;
    Context context;
    Activity activity;
    ProgressDialog pDialog;
    InputStream is = null;
    String json = "";
    StringBuilder sb;
    String Status;
    IabHelper mHelper;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    private ArrayList<CastingDetailsModel> selectedCastingDetailsModels = new ArrayList<CastingDetailsModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_login);

        context = this;
        activity = this;
        mHelper = new IabHelper(this, getResources().getString(R.string.base64encodedpublickey));

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d("Billing", "Problem setting up In-app Billing: " + result);
                } else {

                    Log.d("Billing", "setting up In-app Billing: " + result);
                }
            }
        });


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
                        purchaseRetrofit(purchased, "1");

                    } else {
                        purchaseRetrofit("", "0");
                    }
                }
            }
        };


        findElements();

     //   checkString();
    }

   /* private void checkString() {

        String testValue = "Test1,Test2,Test3";
        if (testValue.contains(",")) {
            int count = testValue.length() - testValue.replace(",", "").length();

            if (count == 2) {
                int index = testValue.indexOf(",", testValue.indexOf(",") + 1);
                String test = testValue.substring(0, index);
                System.out.println("Value == " + test);

            } else if (count == 1) {
                System.out.println("Value == " + testValue);
            }
        } else {
            System.out.println("Value == " + testValue);
        }

    }*/

    private void findElements() {

        try {
            selectedCastingDetailsModels = (ArrayList<CastingDetailsModel>) getIntent().getSerializableExtra("selectedCastingDetailsModels");
        } catch (Exception e) {
            e.printStackTrace();
        }

        txt_forgot = (TextView) findViewById(R.id.txt_forgot);
        textView3 = (TextView) findViewById(R.id.textView3);
        casting_login_back_icon = (LinearLayout) findViewById(R.id.casting_login_back_icon);
        rel_casting_login = (RelativeLayout) findViewById(R.id.rel_casting_login);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_signin = (Button) findViewById(R.id.btn_signin);
        rel_casting_login.setOnClickListener(this);
        btn_signin.setOnClickListener(this);
        casting_login_back_icon.setOnClickListener(this);
        txt_forgot.setOnClickListener(this);
        textView3.setOnClickListener(this);

//        et_email.setText("english@yopmail.com");
//        et_password.setText("qwertyqwerty");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.casting_login_back_icon:
                finish();
                break;
            case R.id.rel_casting_login:
                KeyboardUtility.hideSoftKeyboard(activity);
                break;
            case R.id.txt_forgot:
                forgotPassword();
                break;
            case R.id.btn_signin:
                Library.hideSoftKeyBoard(v, context);
                getValues();
                validation();
                break;
            case R.id.textView3:
                Intent intent = new Intent(CastingLogin.this, Signup.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void forgotPassword() {

        Intent intent = new Intent(CastingLogin.this, CastingForgotPassword.class);
        startActivity(intent);
        finish();
    }

    private void getValues() {

        email = et_email.getText().toString().trim();
        password = et_password.getText().toString().trim();

    }

    private void validation() {

        if (email.length() == 0) {
            Library.alert(context, "Please enter your Email");

        } else if (!Library.isValidEmail(email)) {
            Library.alert(context, "Please enter valid Email Id");

        } else if (password.length() == 0) {
            Library.alert(context, "Please enter Password");

        } else if (password.length() < 8 || password.length() > 21) {
            Library.alert(context, "Password must be atleast 8 characters");

        } else {

            if (Network.isNetworkConnected(context)) {
                LoginRetrofit();
            } else {
                Library.alert(context, getResources().getString(R.string.internet_not_available));
            }
        }
    }


    private void LoginRetrofit() {
        // TODO Auto-generated method  stub

        // Progress Dialog
        pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        // Send the userName,password

        final LoginInput input = new LoginInput(email, password, Library.getUserId(context), Library.getPushId(context));

        //RegisterInput input = new RegisterInput("2072", "ramsdevelop@yahoo.com", "ram@123", "Ganesh", "1","1","1983","Caucasian","2","Pondy, Chennai, Bangalore");
        RegisterRemoteApi.getInstance().setLoginInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().getLoginData(context, new Callback<LoginResponse>() {
            @Override
            public void success(LoginResponse loginResponse, Response response) {
                closeProgress();

                if (loginResponse.status == 200) {


                    Library.putUserDetails(context, loginResponse);

                    // SharedPreferences.Editor editor = getSharedPreferences(Library.UserName, MODE_PRIVATE).edit();
                    SharedPreferences.Editor editor = getSharedPreferences(Library.UserPREFERENCES, MODE_PRIVATE).edit();
                    editor.putString("username", loginResponse.username);
                    editor.putBoolean("loginstatus", true);
                    //    editor.putString("profileImage", loginResponse.profileImage);
                    editor.commit();

                    if (loginResponse.isPurchased.equals("1")) {
                        checkPurchase();
                    } else {
                        Intent intent = new Intent(CastingLogin.this, CastingScreen.class);
                        intent.putExtra("name", loginResponse.username);
                        startActivity(intent);
                        finish();
                    }


                } else {
                    Library.alert(context, loginResponse.message);

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

        }

    }

    private void purchaseRetrofit(String planType, final String isPurchased) {
        // TODO Auto-generated method stub

        // Progress Dialog
        pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        // Send the userName,password


        PurchaseInput input = new PurchaseInput(Library.getUserId(context), "", planType, isPurchased);

        //RegisterInput input = new RegisterInput("2072", "ramsdevelop@yahoo.com", "ram@123", "Ganesh", "1","1","1983","Caucasian","2","Pondy, Chennai, Bangalore");
        RegisterRemoteApi.getInstance().setPurchaseInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().posAppPurchase(context, new Callback<ChangePassResponse>() {
            @Override
            public void success(ChangePassResponse purchaseResponse, Response response) {
                closeProgress();

                if (purchaseResponse.status == 200) {

                    System.out.println(response.toString());
                    SharedPreferences prefs = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("isPurchased", isPurchased);

                    editor.commit();

                    Intent intent = new Intent(CastingLogin.this, CastingScreen.class);
                    startActivity(intent);
                    finish();
                } else {
                    Library.alert(context, purchaseResponse.message);

                }


            }

            @Override
            public void failure(RetrofitError error) {
                closeProgress();
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
