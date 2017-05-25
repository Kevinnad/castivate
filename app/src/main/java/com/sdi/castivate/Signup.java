package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdi.castivate.adapter.LocationAdapter;
import com.sdi.castivate.adapter.LocationSearchAdapter;
import com.sdi.castivate.adapter.PhotoAdapter;
import com.sdi.castivate.croppings.CropUtils;
import com.sdi.castivate.croppings.CropperImageActivity;
import com.sdi.castivate.location.GPSTracker;
import com.sdi.castivate.model.LoginResponse;
import com.sdi.castivate.model.RegisterInput;
import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.ListExpandable;
import com.sdi.castivate.utils.MarshmallowPermission;
import com.sdi.castivate.utils.Network;
import com.sdi.castivate.utils.RegisterRemoteApi;
import com.sdi.castivate.wheelviewnew.WheelViewNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Signup extends Activity implements View.OnClickListener {


    public static RelativeLayout tap_current_location;
    public static double dclatitude, dclongitude;
    public static ListView listLocations;
    EditText etName, etEmail, etPassword, etLocation;
    CheckBox chk_nationwide;
    String username, email, password, birthYear = "", gender, ethnicityVAlue = "", union_type, strSelectedLocation = "", strCurrentLocation = "", strLocation = "", userid = "", profileImageSignup;
    StringBuilder result;
    String isMale = "", isUnion = "", isNationwide = "";
    Boolean gender1, union_type1;
    RelativeLayout bottom_bar;
    WheelViewNew bithYear, ethnicityView;
    String[] ethnicity = {"Caucasian", "African American", "Hispanic",
            "Asian", "Native American", "Middle Eastern", "Other"};
    String[] stockArr;
    ArrayList<String> years = new ArrayList<String>();
    Context context;
    RadioButton male, female, union, non_union;
    RadioGroup genderType, unionType;
    SharedPreferences.Editor editor;
    ProgressDialog pd;
    TextView next;
    ImageView iv1, img_id;
    LinearLayout casting_login_back_icon;
    LocationSearchAdapter searchAdapter = null;
    ArrayList<String> stringList;
    //Sharedpreferences sharedpreferences;
    // to retrieve the Google JSON Map services for US states
    ListView locationListView;
    DownloadTask placesDownloadTask;
    GPSTracker gps;
    double dlatitude, dlongitude;
    SharedPreferences sharedpreferences;
    SharedPreferences sharedPref;
    int paidPos;
    SharedPreferences sharedpreferencesUser;
    Geocoder geocoder;
    List<Address> addresses;
    Address returnAddress;
    String[] from = null;
    int[] to = null;
    JSONObject jObject;
    List<String> listLocationItem = new ArrayList<String>();
    LocationAdapter locationAdapter;
    ArrayList<String> ethnicityList;
    ScrollView scrollView;
    TextView location;
    public static RelativeLayout rl1;

    //set image
    View popUpView;
    private PopupWindow mpopup;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 3;
    Button btnGallery, btnCamera, btnCancel, btn_apply;
    private static int RESULT_LOAD_IMAGE = 2000;
    final int CAMERA_CAPTURE = 1000;
    Dialog dialogCamera;
    File profileImage;
    String imagePath = "";
    Bitmap imgBitMap;
    String picPath;
    public static byte[] b = new byte[1];
    public static String encodedImage = null;
    final int IMAGE_MAX_SIZE = 600;
    MarshmallowPermission marshMallowPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        context = this;
        hideSoftKeyboard();
        //currentLocation();


        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1900; i--) {
            years.add(Integer.toString(i));
        }
        etEmail = (EditText) findViewById(R.id.etEmail);
        etName = (EditText) findViewById(R.id.etName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        genderType = (RadioGroup) findViewById(R.id.genderType);
        unionType = (RadioGroup) findViewById(R.id.unionType);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        union = (RadioButton) findViewById(R.id.union);
        non_union = (RadioButton) findViewById(R.id.non_union);
        bottom_bar = (RelativeLayout) findViewById(R.id.bottom_bar);
        casting_login_back_icon = (LinearLayout) findViewById(R.id.casting_login_back_icon);
        etLocation = (EditText) findViewById(R.id.etLocation);
        locationListView = (ListView) findViewById(R.id.lView);
        listLocations = (ListView) findViewById(R.id.listLocations);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        location = (TextView) findViewById(R.id.location);
        rl1 = (RelativeLayout) findViewById(R.id.rl1);

        iv1 = (ImageView) findViewById(R.id.iv1);
        iv1.setOnClickListener(this);

        next = (TextView) findViewById(R.id.next);
        next.setOnClickListener(this);
        casting_login_back_icon.setOnClickListener(this);

        locationAdapter = new LocationAdapter(context, listLocationItem, "Sign");
        listLocations.setAdapter(locationAdapter);

        //set Signup image
        img_id = (ImageView) findViewById(R.id.image_profile);

        chk_nationwide = (CheckBox) findViewById(R.id.chk_nationwide);
        chk_nationwide.setChecked(true);
        enableNationWide(false);

        marshMallowPermission = new MarshmallowPermission(this);
        chk_nationwide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    listLocationItem = new ArrayList<String>();
                    locationAdapter = new LocationAdapter(context, listLocationItem, "Sign");
                    listLocations.setAdapter(locationAdapter);
                    ListExpandable.getListViewSize(listLocations);
                    enableNationWide(false);
                } else {
                    enableNationWide(true);
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

        etLocation.setOnClickListener(this);
        tap_current_location = (RelativeLayout) findViewById(R.id.tap_current_location);
        tap_current_location.setOnClickListener(this);
        if (tap_current_location.getVisibility() == View.VISIBLE)
            tap_current_location.setVisibility(View.GONE);
        male.setChecked(true);
        gender = "male";


        //strCurrentLocation = sharedpreferences.getString(Library.CURRENT_LOCATION, "");
        //strSelectedLocation = sharedpreferences.getString(Library.SELECTED_LOCATION, "");


        etLocation.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

                // etLocation.setFocusable(true);
                //etLocation.requestFocus();

                return false;

            }
        });
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

        birthYear = "1990";

        ethnicityVAlue = "Caucasian";
        genderType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                // Method 1 For Getting Index of RadioButton
                paidPos = genderType.indexOfChild(findViewById(checkedId));

                gender = String.valueOf(paidPos);
                DebugReportOnLocat.ln("gender" + gender);
                switch (paidPos) {
                    case 0:
                        gender = "male";
                        DebugReportOnLocat.ln("gender" + gender);
                        break;

                    case 2:
                        gender = "female";
                        DebugReportOnLocat.ln("gender" + gender);
                        break;

                }
            }
        });

        unionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                // Method 1 For Getting Index of RadioButton
                paidPos = unionType.indexOfChild(findViewById(checkedId));

                union_type = String.valueOf(paidPos);
                DebugReportOnLocat.ln("union_type" + union_type);
                switch (paidPos) {
                    case 0:
                        union_type = "union";
                        DebugReportOnLocat.ln("union_type" + union_type);
                        break;

                    case 2:
                        union_type = "non-union";
                        DebugReportOnLocat.ln("union_type" + union_type);
                        break;

                }
            }
        });
        stockArr = new String[years.size()];
        stockArr = years.toArray(stockArr);


        /*ethnicityView = (WheelView) findViewById(R.id.ethnicity_list);
        ethnicityView.setVisibleItems(3);

        ethnicityView.setCyclic(false);
        ethnicityView.setViewAdapter(new DateArrayAdapter(this, ethnicity, 2));
        ethnicityView.setCurrentItem(0);

        OnWheelChangedListener listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays(ethnicityView);
            }
        };


        ethnicityView.addChangingListener(listener);
*/
        /*bithYear = (WheelView) findViewById(R.id.birth_year_list);
        bithYear.setVisibleItems(3);
        bithYear.setViewAdapter(new BirthYearArrayAdapter(this, stockArr, 2));
        bithYear.setCurrentItem(25);
        bithYear.setCyclic(false);
        OnWheelChangedListener listener2 = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateBirth(bithYear);
            }
        };

        bithYear.addChangingListener(listener2);*/

        int pos = years.indexOf("1990");

        bithYear = (WheelViewNew) findViewById(R.id.wheelBirthYear);

        bithYear.setOffset(2);
        bithYear.setItems(years);
        bithYear.setSeletion(pos);
        bithYear.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View p_v, MotionEvent p_event) {
                // this will disallow the touch request for parent scroll on
                // touch of child view
                p_v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        ethnicityList = new ArrayList<String>(Arrays.asList(ethnicity));
        ethnicityView = (WheelViewNew) findViewById(R.id.wheelEthnicity);
        ethnicityView.setOffset(2);
        ethnicityView.setItems(ethnicityList);
        ethnicityView.setSeletion(0);
        ethnicityView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View p_v, MotionEvent p_event) {
                // this will disallow the touch request for parent scroll on
                // touch of child view
                p_v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        //Sign Up image click listener
        img_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* int newHeight = 50; // New height in pixels
                int newWidth = 50;
                img_id.requestLayout();
                img_id.getLayoutParams().height = newHeight;
                img_id.getLayoutParams().width = newWidth;
                img_id.setScaleType(ImageView.ScaleType.FIT_XY);
*/
                uploadImageToBackEnd();
            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    void updateDays(WheelView month) {
        try {
            int pos = month.getCurrentItem();
            ethnicityVAlue = ethnicity[pos].toString();
            DebugReportOnLocat.ln("ethnicityValue:::" + ethnicityVAlue);

        } catch (IndexOutOfBoundsException e) {
            // TODO: handle exception
        }

    }

    void updateBirth(WheelView birth) {
        try {
            int pos = birth.getCurrentItem();
            birthYear = stockArr[pos].toString();
            DebugReportOnLocat.ln("birthYear:::" + birthYear);
        } catch (IndexOutOfBoundsException e) {
            // TODO: handle exception
        }

    }

    public void enableNationWide(boolean status) {

        etLocation.setEnabled(status);
        iv1.setEnabled(status);
        listLocations.setEnabled(status);


    }

    public void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @SuppressWarnings("static-access")
    public void currentLocation() {

        // create class object
        gps = new GPSTracker(context);

        // check if GPS enabled
        if (gps.canGetLocation()) {


            dclatitude = gps.getLatitude();
            dclongitude = gps.getLongitude();


            try {
                geocoder = new Geocoder(context, Locale.ENGLISH);
                addresses = geocoder.getFromLocation(dclatitude, dclongitude, 1);
                StringBuilder str = new StringBuilder();
                if (geocoder.isPresent()) {


                    if (strSelectedLocation.equals("")) {
                        dlatitude = dclatitude;
                        dlongitude = dclongitude;


                        try {
                            if (addresses != null)
                                returnAddress = addresses.get(0);
                            String localityString = returnAddress.getLocality();
                            String country = returnAddress.getCountryName();

                            String region_code = returnAddress.getCountryCode();

                            str.append(localityString + " ");
                            str.append(country + ", " + region_code + "");

                            if (str != null && str.length() > 0) {
                                etLocation.setText(str);
                                strSelectedLocation = str.toString();
                                strCurrentLocation = str.toString();
                                // etLocation.setFocusable(false);

                            }

                            sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                            editor = sharedpreferences.edit();

                            editor.putString(Library.CURRENT_LOCATION, strCurrentLocation);
                            editor.commit();
                        } catch (IndexOutOfBoundsException e) {

                            e.printStackTrace();
                        }

                    } else {

                        etLocation.setText(strSelectedLocation);
                        //etLocation.setFocusable(false);

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "geocoder not present", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {

                Log.e("tag", e.getMessage());
            }

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            showSettingsAlert();
        }
    }

    public void showSettingsAlert() {
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
        txtContent.setText("Please enable GPS to continue inside the app.");
        btnOk.setText("Close App");
        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setText("Enable");
        viewSep.setVisibility(View.VISIBLE);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finishAffinity();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
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

    private void validate() {

        username = etName.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString();

        //profileImageSignup = encodedImage.getBytes();

        Pattern p = Pattern.compile("^[ A-z]+$");
        Matcher m = p.matcher(username);
        boolean b = m.matches();
        Pattern p1 = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher m1 = p1.matcher(email);
        boolean b1 = m1.matches();

        if (encodedImage == null) {
            encodedImage = "";
        }

        if (username == null || username.length() == 0) {
            Library.alert(context, "Please enter Name");
            return;
        } else if (email == null || email.length() == 0) {
            Library.alert(context, "Please enter your Email");
            return;

        } else if (b1 == false) {
            Library.alert(context, "Please enter valid Email Id");
            return;

        } else if (password == null || password.length() == 0) {

            Library.alert(context, "Please enter Password");

            return;
        } else if (password.length() < 8) {
            Library.alert(context, "Please enter atleast 8 characters in Password");

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

            int pos = bithYear.getSeletedIndex();
            birthYear = years.get(pos);

            int pos1 = ethnicityView.getSeletedIndex();
            ethnicityVAlue = ethnicityList.get(pos1);

            if (isNationwide.equals("2") && strLocation.equals("")) {
                Library.alert(context, "Please enter the preferred location");

            } else {
                if (Network.isNetworkConnected(context)) {
                    signupRetrofit();
                } else {
                    Library.alert(context, getResources().getString(R.string.internet_not_available));
                }
            }


        }

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


    private void regBack() {
        Intent intent = new Intent(Signup.this, CastingLogin.class);
        startActivity(intent);
        finish();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                Library.hideSoftKeyBoard(v, context);
                validate();
                break;
            case R.id.casting_login_back_icon:
                regBack();
                break;
           /* case R.id.etLocation:
                tap_current_location.setVisibility(View.VISIBLE);
                etLocation.requestFocus();
                findViewById(R.id.frlay).setVisibility(View.VISIBLE);
                break;*/
            case R.id.tap_current_location:
                if (tap_current_location.getVisibility() == View.VISIBLE)
                    tap_current_location.setVisibility(View.GONE);
                // KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
                hideSoftKeyboard();
                dlatitude = dclatitude;
                dlongitude = dclongitude;
                etLocation.setText(strCurrentLocation);
                // etLocation.setFocusable(false);
                hideSoftKeyboard();
                break;
            case R.id.iv1:
                if (etLocation.getText().toString().length() == 0) {
                    Library.alert(context, "Please enter city");

                } else if (listLocationItem.size() > 4) {
                    Library.alert(context, "Maximum 5 cities can be selected");

                } else {
//                    Comment Now
//                    setValues();
                    if (listLocationItem.size() == 0) {

                        String a = etLocation.getText().toString().trim();
                        String value = a.replace(", United States", "");

                        listLocationItem.add(value);
                        locationAdapter = new LocationAdapter(context, listLocationItem, "Sign");
                        listLocations.setAdapter(locationAdapter);

                    } else {
                        String a = etLocation.getText().toString().trim();
                        String value = a.replace(", United States", "");

                        listLocationItem.add(value);
                        locationAdapter.notifyDataSetChanged();

                        if (listLocationItem.size() == 4) {
                            rl1.setVisibility(View.GONE);
                        }
                    }


                    etLocation.setText("");
                    ListExpandable.getListViewSize(listLocations);

                    hideList();
                }
                break;


        }
    }

    private void hideList() {
        if (tap_current_location.getVisibility() == View.VISIBLE)
            tap_current_location.setVisibility(View.GONE);

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

    private void signupRetrofit() {
        // TODO Auto-generated method stub

        // Progress Dialog
        pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        // Send the userName,password

        RegisterInput input = new RegisterInput(Library.getUserId(context), email, password, username, isMale, isUnion, birthYear,
                ethnicityVAlue, isNationwide, strLocation, Library.getPushId(context), encodedImage);

        //RegisterInput input = new RegisterInput("2072", "ramsdevelop@yahoo.com", "ram@123", "Ganesh", "1","1","1983","Caucasian","2","Pondy, Chennai, Bangalore");
        RegisterRemoteApi.getInstance().setRegisterInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().getSignUpData(context, new Callback<LoginResponse>() {
            @Override
            public void success(LoginResponse loginResponse, Response response) {
                closeProgress();

                if (loginResponse.status == 200) {
                    SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("loginstatus", true);
                    editor.commit();

                    Library.putUserDetails(context, loginResponse);

                    alert();

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

    private void alert() {


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.alert_common, null);
        alertDialog.setContentView(convertView);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView txtContent = (TextView) convertView.findViewById(R.id.txtContent);
        Button btnOk = (Button) convertView.findViewById(R.id.btnOk);
        txtContent.setText("Registration Successful");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                finish();
                Intent intent = new Intent(Signup.this, CastingLogin.class);
                //   intent.putExtra("CalledBy", "Register");
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

    private void closeProgress() {
        // TODO Auto-generated method stub
        if (pd.isShowing())
            pd.dismiss();
    }

    // List<HashMap<String, String>> listLocation = new
    // ArrayList<HashMap<String, String>>();

    /**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(18);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                //    view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

    /**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class BirthYearArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public BirthYearArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(18);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                //  view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
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
                                searchAdapter = new LocationSearchAdapter(Signup.this, stringList);

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

    public void uploadImageToBackEnd() {
        dialogCamera = new Dialog(context);
        dialogCamera.setContentView(R.layout.dialog);
        dialogCamera.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogCamera.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogCamera.show();

        btnCamera = (Button) dialogCamera.findViewById(R.id.btnCamera);
        btnGallery = (Button) dialogCamera.findViewById(R.id.btnGallery);
        btnCancel = (Button) dialogCamera.findViewById(R.id.btnCancel);

		/* Camera button click Method */
        btnCamera.setOnClickListener(new View.OnClickListener() {
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
//raul
//                if (marshMallowPermission.checkPermissionForCamera()) {
//
//                    marshMallowPermission.requestPermissionForCamera();
//                }
//                if (marshMallowPermission.checkPermissionForExternalStorage()) {
//
//                    marshMallowPermission.requestPermissionForExternalStorage();
//                }
//                if (marshMallowPermission.checkPermissionForCamera()) {
//                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
//                        initCamera(context);
//                    } else {
//                    }
//                } else {
//                }

//nijam cam

//                if (marshMallowPermission.checkPermissionForCamera()) {
//                    if (marshMallowPermission.checkPermissionForExternalStorage()) {
//                        initCamera(context);
//                    } else {
//                        marshMallowPermission.requestPermissionForExternalStorage();
//                    }
//                } else {
//                    marshMallowPermission.requestPermissionForCamera();
//                }


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
        btnGallery.setOnClickListener(new View.OnClickListener() {
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
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dialogCamera.dismiss();

            }
        });

    }

    public void imageDisplay() {

        Bitmap bm = decodeFile(profileImage);
        DebugReportOnLocat.ln("Profile image : " + bm);
        if (bm != null) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            DebugReportOnLocat.ln("encodedImage >> " + encodedImage);
            img_id.setImageBitmap(bm);

        } else {
            // Toast.makeText(context, "Pick Images Only", Toast.LENGTH_LONG)
            // .show();
        }
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

    @Override
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
}