package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdi.castivate.model.ChangePassInput;
import com.sdi.castivate.model.ChangePassResponse;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.Network;
import com.sdi.castivate.utils.RegisterRemoteApi;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by androidusr1 on 12/10/16.
 */
public class ChangePassword extends Activity {

    LinearLayout layBack;
    EditText etOldPass, etNewPass, etConfirmPass;
    Button btnChangePass;
    Context context;

    String OldPass, NewPass, ConfirmPass;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        context = this;
        initUIValues();

    }

    private void initUIValues() {
        layBack = (LinearLayout) findViewById(R.id.layBack);
        etOldPass = (EditText) findViewById(R.id.etOldPass);
        etNewPass = (EditText) findViewById(R.id.etNewPass);
        etConfirmPass = (EditText) findViewById(R.id.etConfirmPass);
        btnChangePass = (Button) findViewById(R.id.btnChangePass);

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Library.hideSoftKeyBoard(v, context);

                getValues();
            }
        });

        layBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Library.hideSoftKeyBoard(v, context);
                finish();
            }
        });
    }

    private void getValues() {

        OldPass = etOldPass.getText().toString().trim();
        NewPass = etNewPass.getText().toString().trim();
        ConfirmPass = etConfirmPass.getText().toString().trim();

        if (OldPass.length() == 0) {
//            Toast.makeText(context, "Please enter your Old Password", Toast.LENGTH_SHORT).show();
            Library.alert(context, "Enter your Old Password");

        } else if (NewPass.length() == 0) {
//            Toast.makeText(context, "Please enter your New Password", Toast.LENGTH_SHORT).show();
            Library.alert(context, "Enter your New Password");
        } else if (ConfirmPass.length() == 0) {
//            Toast.makeText(context, "Please enter your Confirm Password", Toast.LENGTH_SHORT).show();
            Library.alert(context, "Enter your Confirm Password");

        } else if (!NewPass.equals(ConfirmPass)) {
//            Toast.makeText(context, "New Password and confirm password does not match", Toast.LENGTH_SHORT).show();

            Library.alert(context, "New Password and confirm password does not match");

        } else if (OldPass.length() < 8 || ConfirmPass.length() < 8 || NewPass.length() < 8) {
            //Toast.makeText(context, "Passwords must be atleast 8 characters", Toast.LENGTH_SHORT).show();
            Library.alert(context, "Passwords must be atleast 8 characters");

        } else {
            if (Network.isNetworkConnected(context)) {
                changePassRetrofit();
            } else {
                //  Library.showToast(context, getResources().getString(R.string.internet_not_available));
                Library.alert(context, getResources().getString(R.string.internet_not_available));

            }
        }

    }

    private void changePassRetrofit() {
        // TODO Auto-generated method stub

        // Progress Dialog
        pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        // Send the userName,password

        ChangePassInput input = new ChangePassInput(Library.getUserId(context), NewPass, OldPass);

        RegisterRemoteApi.getInstance().setChangePassInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().getChanPassData(context, new Callback<ChangePassResponse>() {
            @Override
            public void success(ChangePassResponse changePassResponse, Response response) {
                closeProgress();

                if (changePassResponse.status == 200) {
                    alert(changePassResponse.message);
                } else {
//                    Toast.makeText(context, changePassResponse.message, Toast.LENGTH_LONG).show();
                    Library.alert(context, changePassResponse.message);

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
        if (pd.isShowing())
            pd.dismiss();
    }

    private void alert(String msg) {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.alert_common, null);
        alertDialog.setContentView(convertView);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView txtContent = (TextView) convertView.findViewById(R.id.txtContent);
        Button btnOk = (Button) convertView.findViewById(R.id.btnOk);
        txtContent.setText(msg);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                finish();

            }
        });


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int value = (int) ((width) * 3 / 4);

        alertDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = value;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }
}
