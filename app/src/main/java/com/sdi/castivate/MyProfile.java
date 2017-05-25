package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.bumptech.glide.Glide;
import com.sdi.castivate.adapter.HorizontalListViewAdapter;
import com.sdi.castivate.adapter.LocationAdapter;
import com.sdi.castivate.adapter.LocationSearchAdapter;
import com.sdi.castivate.croppings.CropUtils;
import com.sdi.castivate.croppings.CropperImageActivity;
import com.sdi.castivate.model.DeleteFileInput;
import com.sdi.castivate.model.DeleteFileOutput;
import com.sdi.castivate.model.FileModel;
import com.sdi.castivate.model.LoginResponse;
import com.sdi.castivate.model.ProfileinfoInput;
import com.sdi.castivate.model.ProfileinfoOutput;
import com.sdi.castivate.model.RecyclerModel;
import com.sdi.castivate.model.UpdatePersonInput;
import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.HorizontalListView;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.ImagePreview;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.ListExpandable;
import com.sdi.castivate.utils.MarshmallowPermission;
import com.sdi.castivate.utils.MultipartUtility;
import com.sdi.castivate.utils.Network;
import com.sdi.castivate.utils.RegisterRemoteApi;
import com.sdi.castivate.wheelviewnew.WheelViewNew;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by androidusr1 on 12/10/16.
 */
public class MyProfile extends Activity implements View.OnClickListener {

    public static RelativeLayout tap_current_location;
    LinearLayout layBack, genderUnionType, layBirthYear, layEthinicity, photo_videos_files;
    TextView txtEmail, txtChangePass, txtGender, txtUnion, txtBirthYear, txtEthinicity, txtLocation, txtSubcribe, txtResume, txtSave, location, txtSeeAll;
    int ResultCode = 12, paidPos;
    ImageView txtEdit, iv_delete, iv1;
    EditText txtUserName, etLocation;
    Button btnVideoGallery, btnImageGallery, btnCamera, btnVideo, btnCancel, btn_Camera, btn_Gallery, btn_Cancel;
    Dialog dialogCamera;
    // By nijam
    ImageView view, img_profile;
    Context context;
    ArrayList<String> years = new ArrayList<String>();
    private final static int PERMISSION_RQ = 84;
    private final int PICK_IMAGE_MULTIPLE = 111;
    private final int PICK_VIDEO_MULTIPLE = 2;
    private final int TAKE_PICTURE_REQUEST = 3;
    private final int TAKE_VIDEO_REQUEST = 4;
    ArrayList<String> image_path;
    public static ListView listLocations;
    Geocoder geocoder;
    DownloadTask placesDownloadTask;
    String genderValue, unionValue, strSelectedLocation = "", strCurrentLocation = "";
    WheelViewNew wheelBirthYear, wheelEthnicity;
    String[] ethnicity = {"Caucasian", "African American", "Hispanic",
            "Asian", "Native American", "Middle Eastern", "Other"};
    String[] from = null;
    String[] stockArr;
    int[] to = null;
    JSONObject jObject;
    ArrayList<String> ethnicityList;
    double dlatitude, dlongitude;
    CheckBox chk_nationwide;
    // RecyclerViewAdapter adapter;
    public static HorizontalListViewAdapter adapter;
    LocationSearchAdapter searchAdapter = null;
    HorizontalListView horizontalListView;
    ListView locationListView;
    ArrayList<RecyclerModel> list;
    ArrayList<FileModel> lists = new ArrayList<FileModel>();
    List<String> listLocationItem = new ArrayList<String>();
    ArrayList<String> stringList;
    LocationAdapter locationAdapter;
    MarshmallowPermission marshMallowPermission;
    ProgressDialog progress;
    public static Boolean selectVideo = false;
    TextView txtLogout;
    RadioGroup unionType, genderType;
    RadioButton male, female, union, nonUnion;
    public static RelativeLayout rl1, birth_year_layout, ethnicity_layout;
    String username, email, birthYear, ethnicityVAlue, profileImages = "";
    String isMale = "", isUnion = "", isNationwide = "", strLocation = "";
    public static Boolean SAVE = false;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        context = this;
        hideSoftKeyboard();
        // By nijam
        horizontalListView = (HorizontalListView) findViewById(R.id.hlvSimpleList);
        view = (ImageView) findViewById(R.id.textview);
        img_profile = (ImageView) findViewById(R.id.img_my_profile);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        iv1 = (ImageView) findViewById(R.id.iv1);
        txtLogout = (TextView) findViewById(R.id.txtLogout);
        txtSave = (TextView) findViewById(R.id.txtSave);
        txtSeeAll = (TextView) findViewById(R.id.txtSeeAll);
        unionType = (RadioGroup) findViewById(R.id.unionType);
        genderType = (RadioGroup) findViewById(R.id.genderType);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        union = (RadioButton) findViewById(R.id.union);
        nonUnion = (RadioButton) findViewById(R.id.non_union);
        locationListView = (ListView) findViewById(R.id.lView);
        etLocation = (EditText) findViewById(R.id.etLocation);

        rl1 = (RelativeLayout) findViewById(R.id.rl1);
        birth_year_layout = (RelativeLayout) findViewById(R.id.birth_year_layout);
        ethnicity_layout = (RelativeLayout) findViewById(R.id.ethnicity_layout);
        listLocations = (ListView) findViewById(R.id.listLocations);
        iv1.setOnClickListener(this);
        chk_nationwide = (CheckBox) findViewById(R.id.chk_nationwide);

        chk_nationwide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    listLocationItem = new ArrayList<String>();

                    locationAdapter = new LocationAdapter(context, listLocationItem, "Edit");
                    listLocations.setAdapter(locationAdapter);
                    ListExpandable.getListViewSize(listLocations);
                    enableNationWide(false);

                    if (txtEdit.getVisibility() == View.VISIBLE) {
                        locationAdapter.calledBy = "Fix";
                        locationAdapter.notifyDataSetInvalidated();
                    } else {
                        locationAdapter.calledBy = "Edit";
                        locationAdapter.notifyDataSetInvalidated();
                    }
                } else {
                    enableNationWide(true);

                }
            }
        });


        tap_current_location = (RelativeLayout) findViewById(R.id.tap_current_location);
        tap_current_location.setOnClickListener(this);
        if (tap_current_location.getVisibility() == View.VISIBLE)
            tap_current_location.setVisibility(View.GONE);


        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1900; i--) {
            years.add(Integer.toString(i));
        }


        wheelBirthYear = (WheelViewNew) findViewById(R.id.wheelBirthYear);

        wheelBirthYear.setOffset(2);
        wheelBirthYear.setItems(years);
        wheelBirthYear.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View p_v, MotionEvent p_event) {
                // this will disallow the touch request for parent scroll on
                // touch of child view
                p_v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        wheelEthnicity = (WheelViewNew) findViewById(R.id.wheelEthnicity);
        ethnicityList = new ArrayList<String>(Arrays.asList(ethnicity));
        wheelEthnicity.setOffset(2);
        wheelEthnicity.setItems(ethnicityList);

        wheelEthnicity.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View p_v, MotionEvent p_event) {
                // this will disallow the touch request for parent scroll on
                // touch of child view
                p_v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        list = new ArrayList<RecyclerModel>();
        /*if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission to save videos in external storage
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RQ);
        }*/

        initUIValues();
        setValues();
        //  getImageVideoList();
        birthYear = "1990";
        ethnicityVAlue = "Caucasian";
        iv1.setEnabled(false);

        locationAdapter = new LocationAdapter(context, listLocationItem, "Fix");
        listLocations.setAdapter(locationAdapter);
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog();
            }
        });

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Library.doc_id != null)
                    deletePhoto(Integer.parseInt(Library.doc_id));
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SAVE = true;
                Library.hideSoftKeyBoard(v, context);

                locationAdapter.calledBy = "Fix";
                locationAdapter.notifyDataSetChanged();

                validate();


            }
        });

        txtGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderType.setVisibility(View.VISIBLE);
                genderUnionType.setVisibility(View.GONE);
                unionType.setVisibility(View.GONE);

            }
        });


        txtUnion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderType.setVisibility(View.GONE);
                genderUnionType.setVisibility(View.GONE);
                unionType.setVisibility(View.VISIBLE);
            }
        });


        union.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderType.setVisibility(View.GONE);
                genderUnionType.setVisibility(View.VISIBLE);
                unionType.setVisibility(View.GONE);
                union.setChecked(true);
                nonUnion.setChecked(false);
                txtUnion.setText("Union");
            }
        });


        nonUnion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderType.setVisibility(View.GONE);
                genderUnionType.setVisibility(View.VISIBLE);
                unionType.setVisibility(View.GONE);
                nonUnion.setChecked(true);
                union.setChecked(false);
                txtUnion.setText("Non-Union");
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderType.setVisibility(View.GONE);
                genderUnionType.setVisibility(View.VISIBLE);
                unionType.setVisibility(View.GONE);
                male.setChecked(true);
                female.setChecked(false);
                txtGender.setText("Male");
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderType.setVisibility(View.GONE);
                genderUnionType.setVisibility(View.VISIBLE);
                unionType.setVisibility(View.GONE);
                female.setChecked(true);
                male.setChecked(false);
                txtGender.setText("Female");
            }
        });


//        etLocation.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View arg0, MotionEvent arg1) {
//                // TODO Auto-generated method stub
//
//                // etLocation.setFocusable(true);
//                //etLocation.requestFocus();
//
//                return false;
//
//            }
//        });
        // Adding textchange listener
        etLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Creating a DownloadTask to download Google Places matching
                // "s"

                if (s.equals("") && s.equals("Enter city")) {
                    if (tap_current_location.getVisibility() == View.VISIBLE)
                        tap_current_location.setVisibility(View.GONE);
                    etLocation.requestFocus();
                } else {
                    if (Network.isNetworkConnected(context)) {


                        if (s != null && s.length() > 0) {
                            if (s.toString().trim().contains(strCurrentLocation)) {
                                tap_current_location.setVisibility(View.GONE);
                                //hideSoftKeyboard();
                                // KeyboardUtility.hideSoftKeyboard(activity);
                            } else if (s.toString().trim().contains(strSelectedLocation)) {
                                tap_current_location.setVisibility(View.VISIBLE);
                                // hideSoftKeyboard();
                                // KeyboardUtility.hideSoftKeyboard(activity);
                            } else {
                                tap_current_location.setVisibility(View.VISIBLE);
                            }
                            if (s.length() == 0) {
                                tap_current_location.setVisibility(View.GONE);
                            }
                        }


                        tap_current_location.setVisibility(View.VISIBLE);
                        etLocation.requestFocus();
                        findViewById(R.id.frlay).setVisibility(View.VISIBLE);


                        placesDownloadTask = new DownloadTask();

                        // Getting url to the Google Places Autocomplete api
                        String url = getAutoCompleteUrl(s.toString().trim());

                        // Start downloading Google Places
                        // This causes to execute doInBackground() of DownloadTask
                        // class
                        placesDownloadTask.execute(url);
                    } else {
                        Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // Setting an item click listener for the AutoCompleteTextView dropdown
        // list
        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id) {
                // TODO Auto-generated method stub
                // hideSoftKeyboard();
                // tap_current_location.setText(stringList.get(position).toString());

                boolean isHaving = false;
                for (int i = 0; i < listLocationItem.size(); i++) {
                    if (listLocationItem.get(i).equals(stringList.get(index).toString())) {
                        isHaving = true;
                    }
                }
                if (isHaving) {

                    hideSoftKeyboard();

                    etLocation.setText("");
                    Library.alert(context, "This city is selected already");

                } else {
                    etLocation.setText(stringList.get(index).toString());
                    strSelectedLocation = stringList.get(index).toString();

                    hideSoftKeyboard();

                    // Creating a DownloadTask to download Places details of
                    // the selected place
                    try {
                        // Geocoder geocoders = new Geocoder(CastingScreen.this);
                        List<Address> addr;
                        try {
                            addr = geocoder.getFromLocationName(stringList.get(index).toString(), 1);
                            if (addr.size() > 0) {
                                dlatitude = addr.get(0).getLatitude();
                                dlongitude = addr.get(0).getLongitude();
                            }

                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } catch (NullPointerException e) {
                        // TODO: handle exception
                        // Toast.makeText(context, "Please enable the GPS to see filter casting on location based.", Toast.LENGTH_LONG).show();
                    }

                    tap_current_location.setVisibility(View.GONE);
                }


            }
        });

        locationListView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View p_v, MotionEvent p_event) {
                // this will disallow the touch request for parent scroll on
                // touch of child view
                p_v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        stockArr = new String[years.size()];
        stockArr = years.toArray(stockArr);


        if (txtEdit.getVisibility() == View.VISIBLE) {
            locationAdapter.calledBy = "Fix";
            locationAdapter.notifyDataSetChanged();
        }

        etLocation.setEnabled(false);

    }

    private void validate() {

        username = txtUserName.getText().toString().trim();
        email = txtEmail.getText().toString().trim();

        Pattern p = Pattern.compile("^[ A-z]+$");
        Matcher m = p.matcher(username);
        boolean b = m.matches();
        Pattern p1 = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher m1 = p1.matcher(email);
        boolean b1 = m1.matches();

        if (username == null || username.length() == 0) {
            Library.alert(context, "Please enter Name");
            return;
        } else if (email == null || email.length() == 0) {
            Library.alert(context, "Please enter your Email");
            return;

        } else if (b1 == false) {
            Library.alert(context, "Please enter valid Email Id");
            return;

        } else {
            if (male.isChecked()) {
                isMale = "1";

            } else {
                isMale = "2"; //female
            }
            if (union.isChecked()) {
                isUnion = "1";

            } else {
                isUnion = "2"; //non-union
            }

            if (chk_nationwide.isChecked()) {
                isNationwide = "1";

            } else {
                isNationwide = "2";
                locationString();
            }

            int pos = wheelBirthYear.getSeletedIndex();
            birthYear = years.get(pos);

            int pos1 = wheelEthnicity.getSeletedIndex();
            ethnicityVAlue = ethnicityList.get(pos1);

            if (isNationwide.equals("2") && strLocation.equals("")) {
                Library.alert(context, "Please enter the preferred location");

            } else {
                if (Network.isNetworkConnected(context)) {
                    updateRetrofit();
                } else {
                    Library.alert(context, getResources().getString(R.string.internet_not_available));

                }
            }


        }

    }

    private void updateRetrofit() {
        // TODO Auto-generated method stub

        // Progress Dialog
        pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        // Send the userName,password
        if (encodedImage == null) {
            encodedImage = "";
        }
        UpdatePersonInput input = new UpdatePersonInput(Library.getUserId(context), email, username, isMale, isUnion, birthYear, ethnicityVAlue, isNationwide, strLocation, encodedImage);

        RegisterRemoteApi.getInstance().setUpdatePersonInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().getUpdatePersonData(context, new Callback<LoginResponse>() {
            @Override
            public void success(LoginResponse changePassResponse, Response response) {
                closeProgress();
                System.out.println(changePassResponse);
                System.out.println(response);

                if (changePassResponse.status == 200) {

                    etLocation.setEnabled(false);

                    Library.putUserDetails(context, changePassResponse);


                    alert("Your profile Updated Successfully");

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
                SAVE = false;

                txtEdit.setVisibility(View.VISIBLE);
                txtSave.setVisibility(View.GONE);
                txtUserName.setEnabled(false);
                //  img_profile.setEnabled(false);
                iv1.setEnabled(false);
                txtGender.setEnabled(false);
                txtUnion.setEnabled(false);
                chk_nationwide.setEnabled(false);
                etLocation.setEnabled(false);


                layBirthYear.setVisibility(View.VISIBLE);
                layEthinicity.setVisibility(View.VISIBLE);
                birth_year_layout.setVisibility(View.GONE);
                ethnicity_layout.setVisibility(View.GONE);

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
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


    private void locationString() {
        for (int i = 0; i < listLocationItem.size(); i++) {
            if (strLocation.equals("")) {
                strLocation = listLocationItem.get(i);
            } else {
                strLocation = strLocation + "#" + listLocationItem.get(i);
            }
        }
    }


    public void enableNationWide(boolean status) {

        etLocation.setEnabled(status);
        iv1.setEnabled(status);
        listLocations.setEnabled(status);


    }

    public void logout() {
        SharedPreferences settings = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = settings.edit();

        editor1.putBoolean("loginstatus", false);

        editor1.remove("id");
        editor1.remove("userid");
        editor1.remove("email");
        editor1.remove("plantype");
        editor1.remove("apply_Date");
        editor1.remove("Expiry_date");
        editor1.remove("isPurchased");
        editor1.remove("username");
        editor1.remove("gender");
        editor1.remove("uniontype");
        editor1.remove("birthyear");
        editor1.remove("ethincity");
        editor1.remove("preferCities");
        editor1.remove("isNationwide");
        editor1.remove("PurchasedDate");
        editor1.remove("profileImage");
        editor1.remove("imageCount");
        editor1.remove("videoCount");
        editor1.remove("resumeCount");
        editor1.remove("audioCount");
        editor1.remove("totalCount");
        editor1.remove("new_notification");
        editor1.remove("autosubmit");
        editor1.remove("new");
        //   editor1.remove("profileImage");

        editor1.commit();


        Intent in = new Intent(context, CastingScreen.class);
        startActivity(in);
        finishAffinity();
    }


    private void deletePhoto(final int pos) {
        // TODO Auto-generated method stub

        // Progress Dialog
        pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        try {


            DeleteFileInput input = new DeleteFileInput(Library.getUserId(context), Library.doc_id);

            RegisterRemoteApi.getInstance().setDeleteFileInput(input);

            // Call Login JSON
            RegisterRemoteApi.getInstance().postDeleteFile(context, new Callback<DeleteFileOutput>() {
                @Override
                public void success(DeleteFileOutput profileinfoOutput, Response response) {

                    if (profileinfoOutput.status == 200) {

                        Library.reduceUserProfileDetails(context, Library.doc_id);
                        txtResume.setText("Update Resume");
                        iv_delete.setVisibility(View.GONE);

                        SharedPreferences settings = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = settings.edit();

                        editor1.remove("resumeCount");
                        editor1.commit();

                        // MyProfile.files.remove(pos);
                        // MyProfile.adapter.notifyDataSetChanged();
                        closeProgress();

                    } else {
                        closeProgress();
                        Library.alert(context, profileinfoOutput.message);

                    }


                }

                @Override
                public void failure(RetrofitError error) {
                    closeProgress();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            closeProgress();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    ProgressDialog pd;
    public static ArrayList<FileModel> files;

    private void getImageVideoList() {
        // TODO Auto-generated method stub

        // Progress Dialog
        pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        // Send the userName,password

        ProfileinfoInput input = new ProfileinfoInput(Library.getUserId(context));

        RegisterRemoteApi.getInstance().setProfileinfoInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().postPersonProfile(context, new Callback<ProfileinfoOutput>() {
            @Override
            public void success(ProfileinfoOutput profileinfoOutput, Response response) {
                closeProgress();

                if (profileinfoOutput.status == 200) {
                    files = new ArrayList<FileModel>();
                    for (int i = 0; i < profileinfoOutput.files.size(); i++) {
                        if (profileinfoOutput.files.get(i).type.equals("resume")) {

                            String resumeName = profileinfoOutput.files.get(i).url;
                            Library.doc_id = profileinfoOutput.files.get(i).doc_id;
                            System.out.println("profileinfoOutput.files.get(i).doc_id = " + profileinfoOutput.files.get(i).doc_id);
                            int pos = resumeName.lastIndexOf("/");
                            pos = pos + 1;
                            resumeName = resumeName.substring(pos, resumeName.length());
                            txtResume.setText(resumeName);
                            iv_delete.setVisibility(View.VISIBLE);
                        } else {
                            files.add(profileinfoOutput.files.get(i));
                        }
                    }

                    Library.putUserProfileDetails(context, profileinfoOutput.filescnt);

                    adapter = new HorizontalListViewAdapter(context, files);
                    horizontalListView.setAdapter(adapter);

                } else {
                    Library.alert(context, profileinfoOutput.message);

                }


            }

            @Override
            public void failure(RetrofitError error) {
                //        closeProgress();
//                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void closeProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (Library.doc_id != null) {
            iv_delete.setVisibility(View.VISIBLE);
        }*/
    }

    private void initUIValues() {

        layBack = (LinearLayout) findViewById(R.id.layBack);
        genderUnionType = (LinearLayout) findViewById(R.id.gender_union_type);
        layBirthYear = (LinearLayout) findViewById(R.id.layBirthYear);
        layEthinicity = (LinearLayout) findViewById(R.id.layEthinicity);
        photo_videos_files = (LinearLayout) findViewById(R.id.photo_videos_files);
        txtUserName = (EditText) findViewById(R.id.txtUserName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtChangePass = (TextView) findViewById(R.id.txtChangePass);
        //txtGender = (TextView) findViewById(R.id.txtGender);
        txtGender = (TextView) findViewById(R.id.txtMale);
        txtUnion = (TextView) findViewById(R.id.txtUnion);
        txtBirthYear = (TextView) findViewById(R.id.txtBirthYear);
        txtEthinicity = (TextView) findViewById(R.id.txtEthinicity);
        txtResume = (TextView) findViewById(R.id.txtResume);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtSubcribe = (TextView) findViewById(R.id.txtSubcribe);
        location = (TextView) findViewById(R.id.location);
        txtEdit = (ImageView) findViewById(R.id.txtEdit);
        marshMallowPermission = new MarshmallowPermission(this);
        // by nijam

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImageAlert();

                /*final CharSequence[] items = {"Photo", "Video", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Add");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int item) {
                        //           boolean result = Utility.checkPermission(MainActivity.this);

                        if (items[item].equals("Photo")) {
                            videoChoose = "Photo";
                            *//*if (result)*//*
                            //       recordVideo();
                            final CharSequence[] sequences = {"Take Photo", "Photo Library", "Cancel"};


                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Add Photo");
                            builder.setItems(sequences, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (sequences[which].equals("Take Photo")) {
                                        dialog.dismiss();

                                        cameraIntent();


                                    } else if (sequences[which].equals("Photo Library")) {
                                        dialog.dismiss();

                                        int imgMAXCount = getImageCount("Photo");

                                        if (imgMAXCount != 0) {
                                            Intent intent = new Intent(context, CastingCustomPhotoGallery.class);
                                            intent.setType("image*//**//*");
                                            intent.putExtra("update_count", imgMAXCount);
                                            startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
                                        } else {
                                            Library.alert(context, "Your limit is exceeded.");
                                        }

                                    } else if (sequences[which].equals("Cancel")) {
                                        dialog.dismiss();
                                    }

                                }
                            });
                            builder.show();

                        } else if (items[item].equals("Video")) {
                            videoChoose = "Video";
                            final CharSequence[] sequences = {"Take Video", "Video Library", "Cancel"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Add Photo");
                            builder.setItems(sequences, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (sequences[which].equals("Take Video")) {
                                        recordVideo();
                                    } else if (sequences[which].equals("Video Library")) {
                                        int imgMAXCount = getImageCount("Video");

                                        if (imgMAXCount != 0) {
                                            Intent intent = new Intent(context, CastingCustomVideoGallery.class);
                                            intent.setType("video*//*");
                                            intent.putExtra("update_count", imgMAXCount);
                                            startActivityForResult(intent, PICK_VIDEO_MULTIPLE);
                                        } else {
                                            Library.alert(context, "Your limit is exceeded.");
                                        }

                                    } else if (sequences[which].equals("Cancel")) {
                                        dialog.dismiss();
                                    }

                                }
                            });
                            builder.show();

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }*/


            }
        });

        txtSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyProfile.this, PhotosVideoFileActivity.class);
                i.putExtra("profile", "profile");
                startActivity(i);
            }
        });


        layBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

                //startActivity(new Intent(MyProfile.this, CastingScreen.class));
            }
        });


        txtChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyProfile.this, ChangePassword.class));

            }
        });

        txtSubcribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MyProfile.this, CastingPlan.class);
                in.putExtra("CalledBy", "MyProfile");
                startActivityForResult(in, ResultCode);

            }
        });


        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(MyProfile.this, EditProfile.class);
                startActivityForResult(intent, 1);*/
                txtEdit.setVisibility(View.GONE);
                locationAdapter.calledBy = "Edit";
                locationAdapter.notifyDataSetChanged();
                txtSave.setVisibility(View.VISIBLE);
                txtUserName.setEnabled(true);
                //  img_profile.setEnabled(true);
                //   iv1.setEnabled(true);
                txtGender.setEnabled(true);
                txtUnion.setEnabled(true);
                chk_nationwide.setEnabled(true);
                etLocation.setEnabled(false);


                layBirthYear.setVisibility(View.GONE);
                layEthinicity.setVisibility(View.GONE);
                birth_year_layout.setVisibility(View.VISIBLE);
                ethnicity_layout.setVisibility(View.VISIBLE);


                //edit wheel

                String birthyear = prefs.getString("birthyear", "");
                int pos = years.indexOf(birthyear);
                wheelBirthYear.setSeletion(pos);

                String ethincity = prefs.getString("ethincity", "");
                int pos1 = ethnicityList.indexOf(ethincity);
                wheelEthnicity.setSeletion(pos1);


            }
        });

        txtResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtResume.getText().toString().equals("Update Resume")) {

                } else {
                    Intent intent = new Intent(MyProfile.this, ResumeUpload.class);
                    intent.putExtra("CalledBy", "MyProfile");
                    startActivityForResult(intent, ResumeUpoad);
                }
            }
        });

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtSave.getVisibility() == View.VISIBLE) {
                    uploadImageToBackEnd();
                } else {
                    if (!profileImages.equals("")) {
                        Intent in = new Intent(context, ImagePreview.class);
                        in.putExtra("image", profileImages);
                        startActivity(in);
                    }
                }
            }
        });

        location.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideList();
                hideSoftKeyboard();
                return false;
            }
        });
    }

    public void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private void hideList() {
        if (tap_current_location.getVisibility() == View.VISIBLE)
            tap_current_location.setVisibility(View.GONE);

    }

    public void uploadImageToBackEnd() {
        dialogCamera = new Dialog(context);
        dialogCamera.setContentView(R.layout.dialog);
        dialogCamera.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogCamera.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogCamera.show();

        btn_Camera = (Button) dialogCamera.findViewById(R.id.btnCamera);
        btn_Gallery = (Button) dialogCamera.findViewById(R.id.btnGallery);
        btn_Cancel = (Button) dialogCamera.findViewById(R.id.btnCancel);

		/* Camera button click Method */
        btn_Camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogCamera.dismiss();

                if (marshMallowPermission.checkPermissionForCamera()) {
                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
                        initCamera(context);
                    } else {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }
                } else {
                    marshMallowPermission.requestPermissionForCamera();
                }

                // grid_view_photos.setVisibility(View.GONE);

//                captureImage();
                // mCamera = Camera.open(cameraId);

                // do we have a camera?
                // if (!getPackageManager()
                // .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                // Library.showToast(context, "No camera on this device");
                //
                // }

            }
        });

		/* Gallery button click Method */
        btn_Gallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dialogCamera.dismiss();

//                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, RESULT_LOAD_IMAGE);
                if (marshMallowPermission.checkPermissionForExternalStorage()) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(i, FROM_LIBRARY);
                } else {
                    marshMallowPermission.requestPermissionForExternalStorage();
                }
            }
        });
        /* Cancel button click Method */
        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dialogCamera.dismiss();

            }
        });

    }

    //////////////////////////////////////////////Test///////////////
    public static final int FROM_LIBRARY = 1010;
    public static final int FROM_CAMERA = 1115;
    public static final int CROP_PIC = 2;
    String profileImagePath;
    int ACTION_REQUEST_CROP = 100;

    private void initCamera(Context mContext) {

        CropUtils.STORAGE availableStorage = CropUtils.getStorageWithFreeSpace(mContext);
        String rootPath = CropUtils.getRootPath(mContext, availableStorage);

        File folder = new File(rootPath);

        if (!folder.isDirectory()) {
            folder.mkdir();
        }

        File fileCamera = new File(CropUtils.getImagePath(mContext,
                availableStorage, true));
        profileImagePath = fileCamera.getPath();

        if (!fileCamera.exists())
            try {
                fileCamera.createNewFile();
            } catch (IOException e) {
            }

        Uri mImageUri = Uri.fromFile(fileCamera);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, FROM_CAMERA);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FROM_CAMERA && resultCode == Activity.RESULT_OK) {

            if (profileImagePath != null && !profileImagePath.equals("")) {
                startActivityForResult(new Intent(context,
                        CropperImageActivity.class).putExtra("Path",
                        profileImagePath), ACTION_REQUEST_CROP);

            }

        }

        if (requestCode == CROP_PIC && resultCode != 0) {
            // Create an instance of bundle and get the returned data
            Bundle extras = data.getExtras();
            // get the cropped bitmap from extras
            Bitmap thePic = extras.getParcelable("data");
            // set image bitmap to image view
//            imageProfile.setImageBitmap(thePic);

//            imageProfile.setImageBitmap(RoundedShapeBitmap.getRoundedShape(thePic, 120));

        }

        if (requestCode == FROM_LIBRARY && resultCode == RESULT_OK && null != data) {

            profileImagePath = getRealPathFromURI(data.getData());

            if (profileImagePath != null && !profileImagePath.equals("")) {
                startActivityForResult(new Intent(context,
                        CropperImageActivity.class).putExtra("Path",
                        profileImagePath), ACTION_REQUEST_CROP);

            }

        } else if (requestCode == ACTION_REQUEST_CROP
                && resultCode == RESULT_OK) {

            String filePath = data.getStringExtra("picture_path");
            System.out.println(" filePath>>>" + filePath);
            if (filePath != null && !filePath.equals("")) {
                profileImage = new File(filePath);
                System.out.println("profileImage: " + profileImage);
                imageDisplay();
            } else {
                return;
            }

        }

    }
*/
    public void imageDisplay() {

        Bitmap bm = decodeFile(profileImage);

        DebugReportOnLocat.ln("Profile image : " + bm);
        if (bm != null) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            DebugReportOnLocat.ln("encodedImage >> " + encodedImage);
            //    img_profile.setImageBitmap(null);
            //   img_profile.setImageBitmap(decodeFile(profileImage));
            //   img_profile.setImageBitmap(Bitmap.createScaledBitmap(bm, 120, 120, false));

        }
        Picasso.with(context).load(profileImage).into(img_profile);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null,
                null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private Bitmap decodeFile(File f) {

        Bitmap b = null;

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);

            BitmapFactory.decodeStream(fis, null, o);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {

            try {

                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ee) {
                ee.printStackTrace();
            }
        }

        int scale = 1;

        if (f.length() > 8000) {

            scale = 5;

            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(
                        2,
                        (int) Math.ceil(Math.log(IMAGE_MAX_SIZE
                                / (double) Math.max(o.outHeight, o.outWidth))
                                / Math.log(0.5)));
            }
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        b = BitmapFactory.decodeStream(fis, null, o2);
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return b;
    }

    public static byte[] b = new byte[1];
    public static String encodedImage = null;
    final int IMAGE_MAX_SIZE = 600;
    File profileImage;
    // Button btnGallery, btnCamera, btnCancel, btn_apply;
    //  MarshmallowPermission marshMallowPermission;


    private int getImageCount(String type) {

        int imgMAXCount = 19;
        SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        int imageCount = sharedpreferences.getInt("imageCount", 0);
        int videoCount = sharedpreferences.getInt("videoCount", 0);

        if (type.equals("Photo") && videoCount != 0) {
            imgMAXCount = 20;
        } else if (type.equals("Video") && imageCount != 0) {
            imgMAXCount = 20;
        }
        imgMAXCount = imgMAXCount - imageCount;
        imgMAXCount = imgMAXCount - videoCount;

        return imgMAXCount;
    }

    int ResumeUpoad = 900;

    private void setValues() {

        prefs = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);

        txtUserName.setText(prefs.getString("username", ""));
        txtEmail.setText(prefs.getString("email", ""));
        txtGender.setText(prefs.getString("username", ""));
        //   txtUserName.setText(prefs.getString("username", ""));
        String gender = prefs.getString("gender", "");
        if (gender.equals("1")) {
            txtGender.setText("Male");
        } else {
            txtGender.setText("Female");
        }
        String uniontype = prefs.getString("uniontype", "");
        if (uniontype.equals("1")) {
            txtUnion.setText("Union");
        } else {
            txtUnion.setText("Non-Union");
        }
        txtBirthYear.setText(prefs.getString("birthyear", ""));


        txtEthinicity.setText(prefs.getString("ethincity", ""));

        String birthyear = prefs.getString("birthyear", "");
        int pos = years.indexOf(birthyear);
        wheelBirthYear.setSeletion(pos);

        String ethincity = prefs.getString("ethincity", "");
        int pos1 = ethnicityList.indexOf(ethincity);
        wheelEthnicity.setSeletion(pos1);


        String city = prefs.getString("preferCities", "");
        city = city.replaceAll("#", "");
        txtLocation.setText(city);

        String isPurchased;
        isPurchased = prefs.getString("isPurchased", "");

        if (isPurchased.equals("1")) {
            txtSubcribe.setVisibility(View.GONE);
        } else {
            // changed by nijam
            txtSubcribe.setVisibility(View.VISIBLE);
            // txtSubcribe.setVisibility(View.GONE);
        }

        String isNationwide = prefs.getString("isNationwide", "");
        if (isNationwide.equals("1")) {
            chk_nationwide.setChecked(true);
            enableNationWide(false);
        } else {
            chk_nationwide.setChecked(false);
            enableNationWide(true);
        }

        String preferCities = prefs.getString("preferCities", "");
        if (preferCities.length() != 0) {
            if (preferCities.contains("#")) {
                String[] cityList = preferCities.split("#");
                String a = preferCities.replace(", United States", "");
                String[] cityList1 = a.split("#");
                listLocationItem = new ArrayList<String>(Arrays.asList(cityList1)); //new ArrayList is only needed if you absolutely need an ArrayList

                locationAdapter = new LocationAdapter(context, listLocationItem, "Edit");
                listLocations.setAdapter(locationAdapter);
                ListExpandable.getListViewSize(listLocations);

            } else {


                String a = preferCities.replace(", United States", "");
                listLocationItem.add(a);
                locationAdapter = new LocationAdapter(context, listLocationItem, "Edit");
                listLocations.setAdapter(locationAdapter);
                ListExpandable.getListViewSize(listLocations);
            }
        }


        profileImages = prefs.getString("profileImage", "");
        //   img_profile.setImageBitmap(null);
        if (!profileImages.equals("")) {
            Glide.with(MyProfile.this).load(profileImages).placeholder(R.drawable.avathar_profile)
                    .error(R.drawable.avathar_profile).into(img_profile);
        } else {
            Glide.with(MyProfile.this).load("").placeholder(R.drawable.avathar_profile)
                    .error(R.drawable.avathar_profile).into(img_profile);
        }


    }

    ArrayList<File> uploadFileList;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed here it is 2
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        } else if (requestCode == ResultCode) {
            if (resultCode == Activity.RESULT_OK) {
                txtSubcribe.setVisibility(View.GONE);
            }
        } else if (requestCode == ResumeUpoad && resultCode == Activity.RESULT_OK) {
            {
                String message = data.getStringExtra("resumeName");
                txtResume.setText(message);
                if (!message.equals("")) {
                    iv_delete.setVisibility(View.VISIBLE);
                }

            }
        } else if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            uploadFileList = new ArrayList<File>();
            final File file = new File(data.getData().getPath());
            uploadFileList.add(file);
            new DownloadFromURL().execute();

        } else if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK) {

            getImageVideoList();

        } else if (requestCode == PICK_VIDEO_MULTIPLE && resultCode == RESULT_OK) {

            getImageVideoList();

        } else if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_OK) {

            uploadFileList = new ArrayList<File>();

            File file = new File(data.getData().getPath());
            uploadFileList.add(file);

            File filesDir = context.getFilesDir();
            File imageFile = new File(filesDir, "Thumb" + "_" + System.currentTimeMillis() + ".png");

            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
            File s = saveBitmap(bitmap, imageFile.getAbsolutePath());

            uploadFileList.add(s);

            new DownloadFromURL().execute();

        }
        if (requestCode == FROM_CAMERA && resultCode == Activity.RESULT_OK) {

            if (profileImagePath != null && !profileImagePath.equals("")) {
                startActivityForResult(new Intent(context,
                        CropperImageActivity.class).putExtra("Path",
                        profileImagePath), ACTION_REQUEST_CROP);

            }

        }
        if (requestCode == CROP_PIC && resultCode != 0) {
            // Create an instance of bundle and get the returned data
            Bundle extras = data.getExtras();
            // get the cropped bitmap from extras
            Bitmap thePic = extras.getParcelable("data");
            // set image bitmap to image view
//            imageProfile.setImageBitmap(thePic);

//            imageProfile.setImageBitmap(RoundedShapeBitmap.getRoundedShape(thePic, 120));

        }
        if (requestCode == FROM_LIBRARY && resultCode == RESULT_OK && null != data) {

            profileImagePath = getRealPathFromURI(data.getData());

            if (profileImagePath != null && !profileImagePath.equals("")) {
                startActivityForResult(new Intent(context,
                        CropperImageActivity.class).putExtra("Path",
                        profileImagePath), ACTION_REQUEST_CROP);

            }

        } else if (requestCode == ACTION_REQUEST_CROP
                && resultCode == RESULT_OK) {

            String filePath = data.getStringExtra("picture_path");
            System.out.println(" filePath>>>" + filePath);
            if (filePath != null && !filePath.equals("")) {
                profileImage = new File(filePath);
                System.out.println("profileImage: " + profileImage);

                //  img_profile.setImageURI(Uri.fromFile(profileImage));
                imageDisplay();
            } else {
                return;
            }

        }


    }


    private void cameraIntent() {

        File saveDir = null;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            saveDir = new File(Environment.getExternalStorageDirectory(), "CastivateFiles");
            saveDir.mkdirs();
        }

        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .stillShot()
                .labelConfirm(R.string.mcam_use_stillshot);

        materialCamera.start(TAKE_PICTURE_REQUEST);
    }


    private void recordVideo() {

        File saveDir = null;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            saveDir = new File(Environment.getExternalStorageDirectory(), "CastivateFiles");
            saveDir.mkdirs();
        }

        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .showPortraitWarning(false)
                .allowRetry(true)
                .defaultToFrontFacing(true)
                .labelConfirm(R.string.mcam_use_video);
        materialCamera.start(TAKE_VIDEO_REQUEST);
    }

    Bitmap imageBitmap(String path) {
        final int THUMBSIZE = 64;
        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path),
                THUMBSIZE, THUMBSIZE);
        return ThumbImage;
    }

    public static String getRealPathFromURI(Context context, Uri contentURI, String type) {

        String result = null;
        try {
            Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
                Log.d("TAG", "result******************" + result);
            } else {
                cursor.moveToFirst();
                int idx = 0;
                if (type.equalsIgnoreCase("IMAGE")) {
                    idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                } else if (type.equalsIgnoreCase("VIDEO")) {
                    idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                } else if (type.equalsIgnoreCase("AUDIO")) {
                    idx = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
                }
                result = cursor.getString(idx);
                Log.d("TAG", "result*************else*****" + result);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception ", e);
        }
        return result;
    }


    public void uploadfile() {
        try {

            System.gc();

            MultipartUtility multipart = new MultipartUtility(HttpUri.CASTING_FILE_UPLOAD, "UTF-8");
//            multipart.addFormField("description", "Cool Pictures");

            multipart.addFormField("userid", Library.getUserId(context));
            for (int i = 0; i < uploadFileList.size(); i++) {
                multipart.addFilePart("uploads[]", uploadFileList.get(i));
            }

            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");

            for (String line : response) {
                System.out.println("result : " + line);
                Status = line;
            }

            System.out.println("Server Response : " + Status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String Status = "";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tap_current_location:
                if (tap_current_location.getVisibility() == View.VISIBLE)
                    tap_current_location.setVisibility(View.GONE);
                // KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
                hideSoftKeyboard();
               /* dlatitude = dclatitude;
                dlongitude = dclongitude;
                etLocation.setText(strCurrentLocation);*/
                // etLocation.setFocusable(false);
                hideSoftKeyboard();
                break;
            case R.id.iv1:
                if (etLocation.getText().toString().length() == 0) {
                    Library.alert(context, "Please enter the preferred location");

                } else if (listLocationItem.size() > 4) {
                    Library.alert(context, "Maximum 5 cities can be selected");

                } else {
//                    Comment Now
//                    setValues();
                    if (listLocationItem.size() == 0) {

                        listLocationItem.add(etLocation.getText().toString().trim());
                        locationAdapter = new LocationAdapter(context, listLocationItem, "Edit");
                        listLocations.setAdapter(locationAdapter);

                    } else {

                        if (listLocationItem.size() == 4) {
                            rl1.setVisibility(View.GONE);
                        }
                        listLocationItem.add(etLocation.getText().toString().trim());
                        locationAdapter.notifyDataSetChanged();

                    }


                    etLocation.setText("");
                    ListExpandable.getListViewSize(listLocations);

                    hideList();
                }
                break;
        }
    }

    public class DownloadFromURL extends AsyncTask<String, String, String> {


        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(context);
            progress.setMessage("Loading ...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        public String doInBackground(String... params) {
            uploadfile();
            return null;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            if ((progress != null) && progress.isShowing()) {
                progress.dismiss();
            }

            getImageVideoList();
        }
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                DebugReportOnLocat.ln("data " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // tap_current_location.setVisibility(View.GONE);

            try {

                jObject = new JSONObject(result);
                JSONArray jsonArray = jObject.getJSONArray("predictions");
                // PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                // list = placeJsonParser.parse(jObject);
                from = new String[jsonArray.length()];
                to = new int[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    from[i] = object.get("description").toString();
                    to[i] = android.R.id.text1;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (from.length > 0) {

                    // StringBuilder sb = new StringBuilder();
                    // for (int i = 0; i < from.length; i++) {
                    // sb.append(from[i] + "\n");
                    // HashMap<String, String> h = new HashMap<String,
                    // String>();
                    // h.put(from[i], from[i]);
                    // listLocation.add(h);
                    // }
                    try {
                        // String ar[] = new String[3];
                        // ar[0] = from[0];
                        // ar[1] = from[1];
                        // ar[2] = from[2];

                        stringList = new ArrayList<String>(Arrays.asList(from));

                        if (!stringList.isEmpty()) {
                            try {
                                // tap_current_location
                                // .setVisibility(View.VISIBLE);
                                // frlay.setVisibility(View.VISIBLE);
                                searchAdapter = new LocationSearchAdapter(MyProfile.this, stringList);

                                locationListView.setAdapter(searchAdapter);

                                searchAdapter.notifyDataSetChanged();

                                ListExpandable.getListViewSize(locationListView);
                                FrameLayout frlay = (FrameLayout) findViewById(R.id.frlay);
                                frlay.bringToFront();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (IndexOutOfBoundsException e) {
                        // TODO: handle exception
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }


    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            DebugReportOnLocat.ln("data " + data);
            br.close();

        } catch (Exception e) {
            //Log.d("Exception while downloading url", e.toString());
            System.out.println("Exception while downloading url" + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getAutoCompleteUrl(String place) {

        // Obtain browser key from https://code.google.com/apis/console
        // String key = "key=AIzaSyAq-4noVLUBMP-kbkeJoQeAjkYb8sPuseI";
        String key = "key=AIzaSyDG78fc_ZTNzO7OqoVaL-o_Q_6aJ9mLwas";

        // place to be be searched
        place = place.replaceAll(" ", "%20");
        String input = "input=" + place.trim();
        // reference of countries
        String country = "components=country:us";
        // place type to be searched
        // String types = "types=(cities)";
        String types = "types=(regions)";
        // String radius = "radius=" + PROXIMITY_RADIUS;
        // Sensor enabledo
        String sensor = "sensor=false";
        // (cities)&language=pt_BR
        // Building the parameters to the web service
        String parameters = input + "&" + country + "&" + types + "&"
        /* + language+ "&" */ + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

        return url;
    }


    private File savebitmap(String filename) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(filename + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, filename + ".png");
            Log.e("file exist", "" + file + ",Bitmap= " + filename);
        }
        try {
            // make a new bitmap from your file
            Bitmap bitmap = BitmapFactory.decodeFile(file.getName());

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }

    private File saveBitmap(Bitmap bitmap, String path) {
        File file = null;
        if (bitmap != null) {
            file = new File(path);
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(path); //here is set your file path where you want to save or also here you can set file object directly

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    private void showImageAlert() {
        dialogCamera = new Dialog(context);
        dialogCamera.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogCamera.setContentView(R.layout.profile_video_alert);
        dialogCamera.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogCamera.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogCamera.show();

        btnCamera = (Button) dialogCamera.findViewById(R.id.btnCamera);
        btnImageGallery = (Button) dialogCamera.findViewById(R.id.btnImageGallery);
        btnVideo = (Button) dialogCamera.findViewById(R.id.btnVideo);
        btnVideoGallery = (Button) dialogCamera.findViewById(R.id.btnVideoGallery);
        btnCancel = (Button) dialogCamera.findViewById(R.id.btnCancel);

        /* Camera button click Method */
        btnCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogCamera.dismiss();
                int imgMAXCount = getImageCount("Photo");
                if (imgMAXCount != 0) {
                    if (marshMallowPermission.checkPermissionForCamera()) {
                        cameraIntent();
                    } else {
                        marshMallowPermission.requestPermissionForCamera();
                    }

                } else {
                    Library.alert(context, "Your limit is exceeded.");
                }


            }
        });

        /* Gallery button click Method */
        btnImageGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogCamera.dismiss();

                int imgMAXCount = getImageCount("Photo");

                if (imgMAXCount != 0) {

                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
                        Intent intent = new Intent(context, CastingCustomPhotoGallery.class);
                        intent.setType("image*//*");
                        intent.putExtra("update_count", imgMAXCount);
                        startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
                    } else {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }

                } else {
                    Library.alert(context, "Your limit is exceeded.");
                }

            }

        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCamera.dismiss();

                int imgMAXCount = getImageCount("Video");

                if (imgMAXCount != 0) {
                    if (marshMallowPermission.checkPermissionForCamera()) {
                        recordVideo();
                    } else {
                        marshMallowPermission.requestPermissionForCamera();
                    }

                } else {
                    Library.alert(context, "Your limit is exceeded.");
                }
            }
        });

        btnVideoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCamera.dismiss();
                int imgMAXCount = getImageCount("Video");

                if (imgMAXCount != 0) {
                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
                        Intent intent = new Intent(context, CastingCustomVideoGallery.class);
                        intent.setType("video/*");
                        intent.putExtra("update_count", imgMAXCount);
                        startActivityForResult(intent, PICK_VIDEO_MULTIPLE);
                    } else {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }
                } else {
                    Library.alert(context, "Your limit is exceeded.");
                }

            }
        });

        /* Cancel button click Method */
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogCamera.dismiss();
            }
        });

    }

    public void alertdialog() {
      /*  dialogCamera = new Dialog(context);
        dialogCamera.setCancelable(false);
        dialogCamera.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCamera.setContentView(R.layout.custom_alertdialog);
        // set the custom dialog components - title and content
        TextView alertHead = (TextView) dialogCamera.findViewById(R.id.custom_alertdialog_tv_alerthead);
        TextView alertContent = (TextView) dialogCamera.findViewById(R.id.custom_alertdialog_tv_alertcontent);
        // To hide cancel and line separator
        View line = (View) dialogCamera.findViewById(R.id.centerLineDialog);

        Button btnDialogOk = (Button) dialogCamera.findViewById(R.id.custom_alertdialog_btn_ok);

        Button btnDialogCancel = (Button) dialogCamera.findViewById(R.id.custom_alertdialog_btn_cancel);


        // if button is clicked, close the custom dialog
        btnDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCamera.dismiss();
                logout();
            }
        });
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCamera.dismiss();
            }
        });
        dialogCamera.show();*/


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.alert_common, null);
        alertDialog.setContentView(convertView);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView txtContent = (TextView) convertView.findViewById(R.id.txtContent);
        Button btnOk = (Button) convertView.findViewById(R.id.btnOk);
        Button btnCancel = (Button) convertView.findViewById(R.id.btncancel);
        View viewSep = (View) convertView.findViewById(R.id.viewSep);
        txtContent.setText("Are you sure you want to logout?");
        btnOk.setText("Cancel");
        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setText("OK");
        viewSep.setVisibility(View.VISIBLE);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //   finishAffinity();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                logout();
              /*  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);*/
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
