package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdi.castivate.model.ChangePassResponse;
import com.sdi.castivate.model.ForgotPassInput;
import com.sdi.castivate.utils.KeyboardUtility;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.Network;
import com.sdi.castivate.utils.RegisterRemoteApi;

import java.io.InputStream;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by androidusr1 on 11/8/16.
 */
@SuppressWarnings("deprecation")
public class CastingForgotPassword extends Activity implements View.OnClickListener {

    LinearLayout casting_forgot_back_icon;
    RelativeLayout rel_casting_forgot;
    Context context;
    Activity activity;
    Button btn_forgot_submit;
    EditText et_forgot_email;
    String forgot_email;
    ProgressDialog pDialog;
    InputStream is = null;
    String json = "";
    StringBuilder sb;
    String Status;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_forgot_password);

        context = this;
        activity = this;
        findElements();
    }

    private void findElements() {

        casting_forgot_back_icon = (LinearLayout) findViewById(R.id.casting_forgot_back_icon);
        btn_forgot_submit = (Button) findViewById(R.id.btn_forgot_submit);
        et_forgot_email = (EditText) findViewById(R.id.et_forgot_email);
        rel_casting_forgot = (RelativeLayout) findViewById(R.id.rel_casting_forgot);
        rel_casting_forgot.setOnClickListener(this);
        btn_forgot_submit.setOnClickListener(this);
        casting_forgot_back_icon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.casting_forgot_back_icon:
                backForgotPassword();
                break;
            case R.id.rel_casting_forgot:
                KeyboardUtility.hideSoftKeyboard(activity);
                break;
            case R.id.btn_forgot_submit:
                Library.hideSoftKeyBoard(v, context);
                getValues();
                validation();
                break;
        }
    }

    private void backForgotPassword() {

        Intent intent = new Intent(CastingForgotPassword.this, CastingLogin.class);
        startActivity(intent);
        finish();
    }

    private void getValues() {

        forgot_email = et_forgot_email.getText().toString().trim();

    }

    private void validation() {

        if (forgot_email.length() == 0) {
            Library.alert(context, "Please enter your Email");
            //Library.showToast(context, "Enter your Email ID");
        } else if (!Library.isValidEmail(forgot_email)) {
            Library.alert(context, "Please enter valid Email Id");

            //Library.showToast(context, "Enter your valid Email ID");
        } else {

            if (Network.isNetworkConnected(context)) {
                forgotPassRetrofit();
            } else {
                Library.alert(context, getResources().getString(R.string.internet_not_available));
            }
        }

    }

    private void forgotPassRetrofit() {
        // TODO Auto-generated method stub

        // Progress Dialog
        pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        // Send the userName,password

        ForgotPassInput input = new ForgotPassInput(forgot_email, Library.getUserId(context));

        RegisterRemoteApi.getInstance().setForgotPassInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().getForgotPassData(context, new Callback<ChangePassResponse>() {
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

    private void alert(String string) {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.alert_common, null);
        alertDialog.setContentView(convertView);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView txtContent = (TextView) convertView.findViewById(R.id.txtContent);
        Button btnOk = (Button) convertView.findViewById(R.id.btnOk);
        txtContent.setText(string);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                finish();
                Intent intent = new Intent(CastingForgotPassword.this, CastingLogin.class);
                startActivity(intent);
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
