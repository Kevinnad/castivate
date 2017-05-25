package com.sdi.castivate.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sdi.castivate.CastivateApplication;
import com.sdi.castivate.R;
import com.sdi.castivate.model.FileCountModel;
import com.sdi.castivate.model.LoginResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Dinash on 21-Apr-15.
 */
@SuppressWarnings("deprecation")
public class Library {


    public static final String MIXPANEL_TOKEN = "dad6ddc4632acf0de9934e5482267a93"; //for live
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String UserPREFERENCES = "UserDetails";
    public static final String UserName = "UserName";
    public static final String CUSTOMER_ID = "cusidKey";
    public static final String PERFORMANCE_TYPE = "performance";
    public static final String UNION = "union";
    public static final String NON_UNION = "non_union";
    public static final String GENDER = "gender";
    public static final String USER_ID = "user_id";
    public static final String INITIAL_SCREEN = "initial_screen";
    public static final String BIRTH = "birth";
    public static final String CURRENT_LOCATION = "current_location";
    public static final String SELECTED_LOCATION = "selected_location";
    public static final String ETHNICITY = "ethnicity";
    public static final String RATEIT = "rate";
    public static final String RATEIT_FLAG = "rate_flag";
    public static final String HELP_OVERLAY = "help_overlay";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    //17/08/2016
    public static final String REMAINING_DAYS = "remainingdays";
    public static final String LOGIN_STATUS = "loginstatus";
    public static final String NOTIFICATION = "notification";
    // Google project id
    //  public static final String SENDER_ID = "619832204246";
    // Google live account project id
    public static final String SENDER_ID = "142057670383";
    public static final String MESSAGE_KEY = "message";
    /**
     * Tag used on log messages.
     */
    public static final String TAG = "Castivate GCM";
    public static final String DISPLAY_MESSAGE_ACTION =
            "com.sdi.castivate.DISPLAY_MESSAGE";
    public static final String EXTRA_MESSAGE = "message";
    //8552fc16f1fbec2869abdcfe3359b126
    //public static final String MIXPANEL_TOKEN = "8552fc16f1fbec2869abdcfe3359b126"; //for testing
//    public static String weekly = "weekly_purchase";
    public static String monthly = "monthly_purchase";
    public static String yearly = "yearly_purchase";

    public static int DeviceIDCount = 1;
    public static String ethnicity;
    public static String remainingDays;
    public static boolean helpOverlayView = false;
    public static String pushRoleId = "";
    public static String pushGender = "";
    public static String pushAgeRange = "";
    // AIzaSyBZ-4n5N9sZFm4t6sgDwKoszEKrnVCR_nI
    public static String pushEthnicity = "";
    public static String pushPerformanceType = "";
    public static String imageUpload = "";
    public static String SAVE_FOLDER_NAME = CastivateApplication.getAppContext().getFilesDir().getAbsolutePath() + "/Castivate";
    public static String SAVE_FOLDER_TEMP_NAME = CastivateApplication.getAppContext().getFilesDir().getAbsolutePath() + "/CastivateTemp";


    public static String birthSelectedPos = "";
    public static String role_id;
    public static String enthicity;
    public static String age_range;
    public static String gender;
    public static String doc_id;
    public static int currentPos;
    public static boolean isApplied;

    //by nijam
    public static String specialRecruitment = "";
    public static String videoCount = "";
    public static String photoBack = "";
    public static String photoSide = "";
    public static String photoFront = "";
    public static String voiceCount = "";
    public static String castivatePhoto = "";
    public static String castivateVideo = "";
    public static String castivateAudio = "";
    public static String emailValue = "";
    public static String nameValue = "";
    public static String Comments = "";


    // =======================================================================================================
    // push notifications
    // =======================================================================================================
    public static void InsertDeviceID(String DeviceID, String User_ID) {
        DebugReportOnLocat.ln("Device ID>>> " + DeviceID);
        // =====================================================================

    }

    // Toast reusable component
    public static void showToast(Context context, String toastMessage) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    public static void hideSoftKeyBoard(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /* Get Device Id Value*/
    public static String getDeviceID(Context ctx) {
        String AndroidUDID = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("Android UDID: ", AndroidUDID);
        return AndroidUDID;
    }

    public static String senData(JSONStringer envelope) {

        HttpPost httppost = null;

        httppost = new HttpPost(HttpUri.SUBMIT_IMAGE);

        String responseString = "";
        System.err.println("Envelope : " + envelope);
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        // request parameters
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 50000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        // set parameter
        HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);

        try {
            // the entity holds the request
            HttpEntity entity = new StringEntity(envelope.toString());
            httppost.setEntity(entity);
            // Response handler
            ResponseHandler<String> rh = new ResponseHandler<String>() {
                // invoked when client receives response
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    // get response entity
                    HttpEntity entity = response.getEntity();
                    // read the response as byte array
                    StringBuffer out = new StringBuffer();
                    byte[] b = EntityUtils.toByteArray(entity);
                    out.append(new String(b, 0, b.length));
                    return out.toString();
                }
            };
            responseString = httpClient.execute(httppost, rh);
            imageUpload = responseString;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("me", "Exc : " + e.toString());
        }
        // close the connection
        httpClient.getConnectionManager().shutdown();
        return responseString;
    }

    public static boolean emailValidation(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static String getUserId(Context context) {
        String userId;
        SharedPreferences prefs = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        userId = prefs.getString("userid", "");

        return userId;
    }

    public static boolean getLoginStatus(Context context) {
        boolean userId;
        SharedPreferences prefs = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        userId = prefs.getBoolean("loginstatus", false);

        return userId;
    }


    public static String getEmailId(Context context) {
        String email;
        SharedPreferences prefs = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        email = prefs.getString("email", "");

        return email;
    }

    public static String getId(Context context) {
        String userId;
        SharedPreferences prefs = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        userId = prefs.getString("id", "");

        return userId;
    }

    public static String getIsPurchased(Context context) {
        String isPurchased;
        SharedPreferences prefs = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        isPurchased = prefs.getString("isPurchased", "");

        return isPurchased;
    }

    public static String getPushId(Context context) {
        String pushId;
        SharedPreferences prefs = context.getSharedPreferences("PushNotification", Context.MODE_PRIVATE);
        pushId = prefs.getString("regId", "");

        if (pushId.equals("")) {
            pushId = FirebaseInstanceId.getInstance().getToken();
        }

        return pushId;
    }


    public static void putUserDetails(Context context, LoginResponse loginResponse) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("id", loginResponse.id);
        editor.putString("userid", loginResponse.userid);
        editor.putString("email", loginResponse.email);
        editor.putString("plantype", loginResponse.plantype);
        editor.putString("apply_Date", loginResponse.apply_Date);
        editor.putString("Expiry_date", loginResponse.Expiry_date);
        editor.putString("isPurchased", loginResponse.isPurchased);
        editor.putString("username", loginResponse.username);
        editor.putString("gender", loginResponse.gender);
        editor.putString("uniontype", loginResponse.uniontype);
        editor.putString("birthyear", loginResponse.birthyear);
        editor.putString("ethincity", loginResponse.ethincity);
        editor.putString("preferCities", loginResponse.preferCities);
        editor.putString("isNationwide", loginResponse.isNationwide);
        editor.putString("PurchasedDate", loginResponse.PurchasedDate);
        //By nijam
        editor.putString("profileImage", loginResponse.profileImage);
        editor.commit();

        String value = sharedpreferences.getString("profileImage", "");

        SharedPreferences sharedpreferences1 = context.getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedpreferences1.edit();

        String gender;
        boolean UnionType;
        if (loginResponse.gender.equals("1")) {
            gender = "male";
        } else {
            gender = "female";

        }
        if (loginResponse.uniontype.equals("1")) {
            UnionType = true;
        } else {
            UnionType = false;

        }

        editor1.putString(Library.ETHNICITY, loginResponse.ethincity);
        editor1.putString(Library.GENDER, gender);
        editor1.putString(Library.BIRTH, loginResponse.birthyear);
        editor1.putBoolean(Library.NON_UNION, UnionType);
        editor1.commit();

    }


    public static void alert(Context context, final String msg) {

        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        LayoutInflater inflater = getLayoutInflater(getArguments());
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

            }
        });


        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int value = (int) ((width) * 3 / 4);

        alertDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = value;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static void showNetworkAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Network Alert");
        builder.setMessage("Please check your network connection and try again");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void putUserProfileDetails(Context context, ArrayList<FileCountModel> fileCountModels) {
        int imageCount = 0, videoCount = 0, resumeCount = 0, totalCount = 0;

        for (int i = 0; i < fileCountModels.size(); i++) {
            if (fileCountModels.get(i).type.equals("image")) {
                imageCount = Integer.parseInt(fileCountModels.get(i).count);
            } else if (fileCountModels.get(i).type.equals("resume")) {
                resumeCount = Integer.parseInt(fileCountModels.get(i).count);
            } else if (fileCountModels.get(i).type.equals("video")) {
                videoCount = Integer.parseInt(fileCountModels.get(i).count);
            }
        }
        totalCount = imageCount + videoCount;

        SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("imageCount", imageCount);
        editor.putInt("videoCount", videoCount);
        editor.putInt("resumeCount", resumeCount);
        editor.putInt("totalCount", totalCount);

        editor.commit();

    }

    public static void reduceUserProfileDetails(Context context, String type) {
        int imageCount = 0, videoCount = 0, resumeCount = 0, totalCount = 0, audioCount = 0;
        SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        imageCount = sharedpreferences.getInt("imageCount", 0);
        videoCount = sharedpreferences.getInt("videoCount", 0);
        resumeCount = sharedpreferences.getInt("resumeCount", 0);
        audioCount = sharedpreferences.getInt("audioCount", 0);

        if (type.equals("image")) {
            imageCount = imageCount - 1;
        } else if (type.equals("video")) {
            videoCount = videoCount - 1;
        } else if (type.equals("audio")) {
            audioCount = audioCount - 1;
        }

        totalCount = imageCount + videoCount;

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("imageCount", imageCount);
        editor.putInt("videoCount", videoCount);
        editor.putInt("resumeCount", resumeCount);
        editor.putInt("audioCount", audioCount);
        editor.putInt("totalCount", totalCount);

        editor.commit();

    }


}
