package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sdi.castivate.model.PersonNotificationInput;
import com.sdi.castivate.model.PersonNotificationOutput;
import com.sdi.castivate.model.UpdateNotificationInput;
import com.sdi.castivate.model.UpdateNotificationOutput;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.RegisterRemoteApi;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.sdi.castivate.R.id.notification_toggle;
import static com.sdi.castivate.R.id.notification_txtSave;

/**
 * Created by Gnanaoly on 01-Feb-17.
 */

public class Notification_Settings extends Activity implements
        View.OnClickListener {
    LinearLayout ll_back, linear_layout_options;
    ToggleButton toggleButton;
    RadioButton daily, weekly, monthly;
    TextView btnSave, weekTxt, monthTxt;
    String userid = "", type, status, type_value, not_status;
    Context c;
    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings);
        c = this;
        userid = Library.getUserId(c);
        ll_back = (LinearLayout) findViewById(R.id.notification_settings_back_icon);
        linear_layout_options = (LinearLayout) findViewById(R.id.linear_layout_options);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        daily = (RadioButton) findViewById(R.id.Daily);
        weekly = (RadioButton) findViewById(R.id.Weekly);
        monthly = (RadioButton) findViewById(R.id.Monthly);

        btnSave = (TextView) findViewById(notification_txtSave);
        weekTxt = (TextView) findViewById(R.id.textView17);
        monthTxt = (TextView) findViewById(R.id.textView13);
        ll_back.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        daily.setOnClickListener(this);
        weekly.setOnClickListener(this);
        monthly.setOnClickListener(this);
        getValue();


        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (toggleButton.isChecked()) {
                    linear_layout_options.setVisibility(View.VISIBLE);
                   /* daily.setChecked(true);
                    weekly.setChecked(false);
                    monthly.setChecked(false);*/
                } else {
                    linear_layout_options.setVisibility(View.GONE);
                }
            }
        });

    }


    private void closeProgress() {
        // TODO Auto-generated method stub
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void getValue() {
        if (userid != null) {

            PersonNotificationInput per = new PersonNotificationInput(userid);
            pDialog = new ProgressDialog(c, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
            RegisterRemoteApi.getInstance().setPersonNotificationInput(per);
            RegisterRemoteApi.getInstance().personNotification(c, new Callback<PersonNotificationOutput>() {
                @Override
                public void success(PersonNotificationOutput personNotificationOutput, Response response) {
                    closeProgress();
                    type = personNotificationOutput.notification_type;
                    status = personNotificationOutput.notify_status;
                    type_value = personNotificationOutput.type_value;
                    afterGettingStatus();
                    SharedPreferences sharedpreferences = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("notification", status);
                    editor.commit();


                }

                @Override
                public void failure(RetrofitError error) {
                    closeProgress();
                   /* Toast.makeText(c, "The requested service is not available. Please try again after sometime",
                            Toast.LENGTH_LONG).show();*/
                    //   finish();
                }
            });
        }
    }

    private void afterGettingStatus() {
        /*if (status.equals("0")) {
            toggleButton.setChecked(false);
            linear_layout_options.setVisibility(View.GONE);
        } else if (status.equals("1")) {
            toggleButton.setChecked(true);
            linear_layout_options.setVisibility(View.VISIBLE);
        }

        if (status.equals("0")) {
            toggleButton.setChecked(false);
        }
        if (status.equals("1")) {
            toggleButton.setChecked(true);
        }

        if (type.equals("0")) {
            linear_layout_options.setVisibility(View.GONE);
        } else if (type.equals("3")) {
            daily.setChecked(true);
            weekly.setChecked(false);
            monthly.setChecked(false);
            txt = "";
            weekTxt.setText("");
            monthTxt.setText("");
        } else if (type.equals("2")) {
            daily.setChecked(false);
            weekly.setChecked(true);
            monthly.setChecked(false);
            txt = type_value;
            if (type_value.contains("Sun")) txt = "Sunday";
            if (type_value.contains("Mon")) txt = "Monday";
            if (type_value.contains("Tue")) txt = "Tuesday";
            if (type_value.contains("Wed")) txt = "Wednesday";
            if (type_value.contains("Thu")) txt = "Thursday";
            if (type_value.contains("Fri")) txt = "Friday";
            if (type_value.contains("Sat")) txt = "Saturday";
            weekTxt.setText(txt);
            monthTxt.setText("");
        } else if (type.equals("1")) {
            daily.setChecked(false);
            weekly.setChecked(false);
            monthly.setChecked(true);
            txt = type_value;
            weekTxt.setText("");
            monthTxt.setText(type_value);
        }*/
        toggleButton.setChecked(true);
        linear_layout_options.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notification_settings_back_icon:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                //  finish();
                break;
            case R.id.notification_txtSave:
                getSave();
                break;
            case R.id.Daily:
                daily.setChecked(true);
                weekly.setChecked(false);
                monthly.setChecked(false);
                txt = "";
                break;
            case R.id.Monthly:
                daily.setChecked(false);
                monthly.setChecked(true);
                weekly.setChecked(false);
                monthList();
                weekTxt.setText("");
                txt = monthTxt.getText().toString();
                break;
            case R.id.Weekly:
                daily.setChecked(false);
                monthly.setChecked(false);
                weekly.setChecked(true);
                weekList();
                txt = weekTxt.getText().toString();
                weekTxt.setText("");
                break;
        }
    }

    private void getSave() {


        if (toggleButton.isChecked()) {

            not_status = "1";
        } else {
            not_status = "0";
        }
        String not_type = "0";

        if (weekly.isChecked()) {
            not_type = "2";
        }
        if (monthly.isChecked()) {
            not_type = "1";
        }
        if (daily.isChecked()) {
            not_type = "3";
            txt = "";
        }
        if (!weekTxt.getText().toString().equals("")) {
            txt = weekTxt.getText().toString();
        }
        if (!monthTxt.getText().toString().equals("")) {
            txt = monthTxt.getText().toString();
        }

        UpdateNotificationInput per = new UpdateNotificationInput(userid, not_type, txt, not_status);
        if (not_status.equals("0")) {
            per = new UpdateNotificationInput(userid, "", "", not_status);
        }
        pDialog = new ProgressDialog(c, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();


        RegisterRemoteApi.getInstance().setUpdateNotificationInput(per);
        RegisterRemoteApi.getInstance().updateNotification(c, new Callback<UpdateNotificationOutput>() {
            @Override
            public void success(UpdateNotificationOutput personNotificationOutput, Response response) {
                closeProgress();
                String message = personNotificationOutput.message;
                String status = personNotificationOutput.status;
                int statusValue = Integer.parseInt(status);
                if (statusValue == 200) {
                    alert("Updated successfully");
                } else {
                    Library.alert(c, message);
                }


            }

            @Override
            public void failure(RetrofitError error) {
                closeProgress();
                Library.alert(c, "The requested service is not available. Please try again after sometime");
                //   Toast.makeText(c, "The requested service is not available. Please try again after sometime", Toast.LENGTH_LONG).show();
                //   finish();
            }
        });


    }

    String txt;

    void weekList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Notification_Settings.this);
        builderSingle.setTitle("Select A Day");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Notification_Settings.this, R.layout.select_dialog);
        arrayAdapter.add("Sunday");
        arrayAdapter.add("Monday");
        arrayAdapter.add("Tuesday");
        arrayAdapter.add("Wednesday");
        arrayAdapter.add("Thursday");
        arrayAdapter.add("Friday");
        arrayAdapter.add("Saturday");
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                afterGettingStatus();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                //    txt = strName.substring(0, Math.min(strName.length(), 3));
                txt = strName;
                AlertDialog.Builder builderInner = new AlertDialog.Builder(Notification_Settings.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        monthTxt.setText("");
                        weekTxt.setText(txt);
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }

    void monthList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Notification_Settings.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select A Day");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Notification_Settings.this, R.layout.select_dialog);
        arrayAdapter.add("Start");
        arrayAdapter.add("End");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                afterGettingStatus();

            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                txt = strName;
                AlertDialog.Builder builderInner = new AlertDialog.Builder(Notification_Settings.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        weekTxt.setText("");
                        monthTxt.setText(txt);
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }


    private void alert(String msg) {


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog alertDialog = new Dialog(c);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

                SharedPreferences sharedPreference = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreference.edit();
                editor.putString("new_notification", not_status);
                editor.commit();


                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                //  finish();

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
