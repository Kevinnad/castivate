package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sdi.castivate.model.ApplyCastingInput;
import com.sdi.castivate.model.ApplyCastingOutput;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.RegisterRemoteApi;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nijamudhin on 3/14/2017.
 */

public class ProfileNotMatching extends Activity {
    Button btnApply, btnCancel;
    Context context;
    Dialog dialogCamera;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.castivate_apply);
        context = this;
        btnApply = (Button) findViewById(R.id.appliedId);
        btnCancel = (Button) findViewById(R.id.cancelCasting);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                int imageCount = sharedpreferences.getInt("imageCount", 0);
                int videoCount = sharedpreferences.getInt("videoCount", 0);
                int resumeCount = sharedpreferences.getInt("resumeCount", 0);
                int audioCount = sharedpreferences.getInt("audioCount", 0);

                if (Library.specialRecruitment.equals("special")) {
                    if (imageCount != 0 && videoCount != 0 && resumeCount != 0 && audioCount != 0) {
                        startActivity(new Intent(ProfileNotMatching.this, NewCastingApplyScreen.class));
                    } else {
                        //  alertdialog();
                        Intent i = new Intent(ProfileNotMatching.this, PhotosVideoFileActivity.class);
                        startActivityForResult(i, 99);
                    }
                } else {
                    if (imageCount != 0 && videoCount != 0 && resumeCount != 0 && audioCount != 0) {
                        ApplyCastingRetrofit();
                    } else {
                        //   alertdialog();
                        Intent i = new Intent(ProfileNotMatching.this, PhotosVideoFileActivity.class);
                        startActivityForResult(i, 99);
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void alertdialog() {
        dialogCamera = new Dialog(context);
        dialogCamera.setCancelable(false);
        dialogCamera.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCamera.setContentView(R.layout.custom_alertdialog);
        // set the custom dialog components - title and content
        TextView alertHead = (TextView) dialogCamera.findViewById(R.id.custom_alertdialog_tv_alerthead);
        TextView alertContent = (TextView) dialogCamera.findViewById(R.id.custom_alertdialog_tv_alertcontent);
        alertContent.setText("Please give all the basic details");
        // To hide cancel and line separator
        View line = (View) dialogCamera.findViewById(R.id.centerLineDialog);

        Button btnDialogOk = (Button) dialogCamera.findViewById(R.id.custom_alertdialog_btn_ok);

        Button btnDialogCancel = (Button) dialogCamera.findViewById(R.id.custom_alertdialog_btn_cancel);

        btnDialogCancel.setVisibility(View.GONE);
        line.setVisibility(View.GONE);

        // if button is clicked, close the custom dialog
        btnDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCamera.dismiss();
                Intent i = new Intent(ProfileNotMatching.this, PhotosVideoFileActivity.class);
                startActivityForResult(i, 99);
            }
        });
        dialogCamera.show();
    }


    private void ApplyCastingRetrofit() {
        // TODO Auto-generated method stub

        // Progress Dialog
        pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();


        ApplyCastingInput input = new ApplyCastingInput(Library.getUserId(context),
                Library.role_id, Library.enthicity, Library.age_range, Library.gender);

        RegisterRemoteApi.getInstance().setApplyCastingInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().getApplyCasting(context, new Callback<ApplyCastingOutput>() {
            @Override
            public void success(ApplyCastingOutput changePassResponse, Response response) {
                closeProgress();

                if (changePassResponse.status == 200) {

                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();

                } else {
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
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
