package com.sdi.castivate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.iid.FirebaseInstanceId;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.sdi.castivate.adapter.CastingsListAdapter;
import com.sdi.castivate.adapter.EthnicityFilterAdapter;
import com.sdi.castivate.adapter.ImageListAdapter;
import com.sdi.castivate.adapter.LocationSearchAdapter;
import com.sdi.castivate.adapter.SwipeViewListAdapter;
import com.sdi.castivate.croppings.CropUtils;
import com.sdi.castivate.croppings.CropperImageActivity;
import com.sdi.castivate.help.Help;
import com.sdi.castivate.location.GPSTracker;
import com.sdi.castivate.model.ApplyCastingInput;
import com.sdi.castivate.model.ApplyCastingOutput;
import com.sdi.castivate.model.AutoSubmit;
import com.sdi.castivate.model.AutoSubmitOutput;
import com.sdi.castivate.model.CastingDetailsModel;
import com.sdi.castivate.model.CastingImagesModel;
import com.sdi.castivate.model.CastingList;
import com.sdi.castivate.model.ChangePassResponse;
import com.sdi.castivate.model.EthnicityModel;
import com.sdi.castivate.model.MatchedCastingInput;
import com.sdi.castivate.model.MatchedCastingOutput;
import com.sdi.castivate.model.NewProfileinfoOutput;
import com.sdi.castivate.model.PersonNotificationInput;
import com.sdi.castivate.model.PersonNotificationOutput;
import com.sdi.castivate.model.ProfileinfoInput;
import com.sdi.castivate.model.PurchaseInput;
import com.sdi.castivate.model.RemoveCastingInput;
import com.sdi.castivate.model.RemoveCastingOutput;
import com.sdi.castivate.model.ThankCommentInput;
import com.sdi.castivate.model.ThankCommentOutput;
import com.sdi.castivate.model.UpdateAutoSubmitInput;
import com.sdi.castivate.model.UpdateAutoSubmitOutput;
import com.sdi.castivate.model.UpdateNotificationInput;
import com.sdi.castivate.model.UpdateNotificationOutput;
import com.sdi.castivate.model.Users;
import com.sdi.castivate.model.UsersRes;
import com.sdi.castivate.swipemenulistview.SwipeMenu;
import com.sdi.castivate.swipemenulistview.SwipeMenuCreator;
import com.sdi.castivate.swipemenulistview.SwipeMenuItem;
import com.sdi.castivate.swipemenulistview.SwipeMenuListView;
import com.sdi.castivate.utils.CastingsLinkMovementMethod;
import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.GCMInterface;
import com.sdi.castivate.utils.GridInterface;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.ListExpandable;
import com.sdi.castivate.utils.MarshmallowPermission;
import com.sdi.castivate.utils.Network;
import com.sdi.castivate.utils.RegisterRemoteApi;
import com.sdi.castivate.utils.ServiceCall;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Balachandar on 16-Apr-15.
 */

@SuppressWarnings("deprecation")
@SuppressLint({"ResourceAsColor", "InlinedApi", "ShowToast", "UseSparseArrays"})
public class CastingScreen extends Activity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, View.OnTouchListener, GCMInterface, GridInterface/*
                                                                                                                                                             */ {

    public static final String REG_ID = "regId";
    static final String EXTRA_MESSAGE = "message";
    static final String TAG = "Register Activity";
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static final String APP_VERSION = "appVersion";
    public static double dclatitude, dclongitude;
    public static String favCount = "", commentCount = "", roleFav;
    public static String favImgCount = "", imageFav;
    public static RelativeLayout tap_current_location;
    public static int sequenceOrder = -1;
    // image upload and camera
    public static byte[] b = new byte[1];
    public static String encodedImage = null;
    public static String strPerformanceType, strGender, strUnionType, strBithYear, strEthnicity, strSelectedLocation = "", strCurrentLocation, userId;
    public static boolean notification, UnionType = false, ratePopFlag;
    // public static String defaultCurrentLocation;
    public static String userTokenId = "", favriteFlag, message = "";
    public String notify_status = "", status = "";
    public static int rateCount = 0;
    public String tid, cityName;
    public static boolean isFavScreen, imageCount;
    public static int position = 0;/*
                                     * ,
									 * saveImagePostionleft=4,saveImagePostionRight
									 * =5
									 */
    public static int scrollCount = 0;
    static String androidUserID;
    static String remainingDays;
    private static int RESULT_LOAD_IMAGE = 2000;
    final int CAMERA_CAPTURE = 1000;
    final int IMAGE_MAX_SIZE = 600;
    // ProgressDialog pDialog;
    // casting filter screen
    public CheckBox chkActor, chkModel, chkSinger, chkDancer, chkMale, chkFemale, chkUnion, chkNonUnion, fav_iconNoImage;
    // casting main screen
    public TextView textCastingTitleNoImage, textCastingTypeNoImage, textPaidStatusNoImage, textUnionStatusNoImage, textUnionTypeNoImage, textSynopsisNoImage, textAgeRangeNoImage,
            textRoleForGenderNoImage, textRoleForEthnicityNoImage, txtRoleDescriptionNoImage, textSubmissionDetailNoImage, textCastivateNoImage, textDistanceNoImage,
            textCastivate, txtUploadYourImage, txtSwipeCastings, txtNotificationSettings, txtNocast;
    public ListView ethnicityListView, castingsList;
    private SwipeMenuListView menuListView;
    public boolean isPush;
    public boolean forChk;
    public String PushRollID = "";
    SharedPreferences sharedpreferences, sharedPreference;
    Editor editor;
    GridView grid_view_photos;
    LinearLayout lay_submission_detail_no_image;
    double dlatitude, dlongitude;
    GPSTracker gps;
    File filePath;
    String provider = "", android_unique_user_id = "";
    String strState = "", strCity = "";
    LinearLayout castivityScreen;
    // by nijam
    LinearLayout tabLayout;
    ImageView gear_icon, gear_icon_mirror, outbox_icon, casting_image, help_overlay;
    ToggleButton toggleButton, notification_toggle, auto_submit_toggle;
    TextView birthYear, currentLocation, txtEthnicity, txtSubmitCasting, txtReset, text_help_over_lay, favCountText, select_icon, imgGrid, txtName, local_talent, textlogout, txt_signin;
    RelativeLayout castingViewNoImage, rel_cast_view, rel_cast, rel_upload_screen, rel_cast_outer, rel_info_ListView, home_screen, rel_home_screen, rel_image_screen;
    CastingsListAdapter castListAdapter;
    LocationSearchAdapter searchAdapter = null;
    ArrayList<String> stringList;
    ListView locationListView;
    ProgressBar progressView;
    ArrayList<String> years = new ArrayList<String>();
    ArrayList<EthnicityModel> ethnicityList;
    ArrayList<String> selchkboxlist;
    FrameLayout frlay;
    boolean castImageViewStatus, listViewStatus, gridViewStatus;
    Context context;
    View view;
    File picFile;
    int rateThisAppFlag = 1;
    Button btnGallery, btnCamera, btnCancel;
    // Swipe left and right
    Dialog dialogCamera;
    File profileImage;
    SwipeViewListAdapter swipeViewListAdapter;
    String imagePath = "";
    Bitmap imgBitMap;
    String picPath;
    // Sugumaran Changes (25th May 16)
    // private GestureDetector imgGestureDetector;
    // View.OnTouchListener imgGestureListener;
    boolean helpOverlayFlag/* ,noImageCasting */, notifFlag = false;
    CheckBox imgFav;
    // to retrieve the Google JSON Map services for US states
    EditText editTextLocation;
    String getFrom = "cast";
    int initialPositionValue = 0;
    Activity activity;
    ImageView clear, birthClear, castingsListIcon /* ,castingsListIconChange */;
    EthnicityFilterAdapter adapterEthnicityList;
    String[] ethnicity = {"Caucasian", "African American", "Hispanic", "Asian", "Native American", "Middle Eastern", "Other"};
    ArrayList<CastingDetailsModel> swipeList;
    MarshmallowPermission marshmallowPermission;
    /*vasanth you add this*/
    TextView txtSignup, txtLogin, profileName;
    ImageView userProfileImage;

    // /////////////
    View.OnTouchListener gestureListener;
    int count = 0;
    DownloadTask placesDownloadTask;
    String totalCasting;
    MixpanelAPI mixpanel;
    Animation animBlink;
    String build = android.os.Build.MODEL;
    String os = android.os.Build.VERSION.RELEASE;
    // Sugumaran Changes (25th May 16)
    ImageView casting_image_only;
    //10-08-2016
    TextView btn_apply;
    LinearLayout layBottom;
    ImageView imgTick;
    TextView curLoc;
    LinearLayout auto_complete_view;
    View autoCompleteView;
    Geocoder geocoder;
    List<Address> addresses;
    Address returnAddress;
    String[] from = null;
    int[] to = null;
    JSONObject jObject;
    InputStream is = null;
    String json = "";
    JSONObject jObj = null;
    JSONArray jArr = null;
    StringBuilder sb;
    String Status, listType = "1", photofilePath = "";
    View view_allcasting, view_local, view_applied;
    int head, tail;
    int page = 0;
    // int oldPage = 0;
    int getTotalCastings;
    int lastPage;
    LinearLayout lay_login;
    int totalPages;
    boolean bs = false;
    ArrayList<CastingDetailsModel> myList;

    // List<HashMap<String, String>> listLocation = new
    // ArrayList<HashMap<String, String>>();
    ArrayList<CastingDetailsModel> castingsListArr = new ArrayList<CastingDetailsModel>();
    ArrayList<CastingImagesModel> myListImage;
    boolean testB = false;
    boolean swpe = false;
    String cityAndStateName = "";
    JSONObject json_data = null;
    boolean castViewStatus = false, lastImageStatus;
    boolean castViewStatusreak = false;
    String swipe = "";
    String bSwipe = "";
    String imgCasting = "", userName;
    GoogleCloudMessaging gcm;
    String regId;
    String userNotificationFlag = "Yes";
    Bitmap bm;
    int checkPages = -1;
    // Sugumaran Changes (25th May 16)
    int imagePos = -1;
    int Ratecount;
    int nwdate;
    ProgressDialog pDialog;
    //by nijam
    TextView txtAllCasting, txtLocalCasting, txtAppliedCasting;
//    public static ArrayList<Integer> countValue;

    // Sugumaran
    private GestureDetector gestureDetector;
    // 17-Dec-2015 : New Concept
    private SwipeRefreshLayout swipeRefreshLayout;
    // Sugumaran
    private ArrayList<CastingDetailsModel> selectedCastingDetailsModels = new ArrayList<CastingDetailsModel>();
    private Handler handlerCastingSync = new Handler() {
        @Override
        public void handleMessage(Message msg) {
          /*  if (Network.isNetworkConnected(CastingScreen.this)) {
                rel_upload_screen.setVisibility(View.GONE);
                castingsList.setEnabled(false);
                new GetDatas("1").execute();

            } else {
                Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
                finishAffinity();
            }*/
            if (Network.isNetworkConnected(CastingScreen.this)) {
                txtNocast.setVisibility(View.GONE);
                rel_upload_screen.setVisibility(View.GONE);
                castingsList.setEnabled(false);
                castingsList.setVisibility(View.VISIBLE);
                menuListView.setVisibility(View.GONE);
                //      myList.clear();
                //     swipeList.clear();
                //     castingsListArr.clear();
                listType = String.valueOf(1);
                view_allcasting.setBackgroundColor(Color.parseColor("#CB212E"));
                view_local.setBackgroundColor(Color.parseColor("#2F2F2F"));
                view_applied.setBackgroundColor(Color.parseColor("#2F2F2F"));
                new GetDatas("1").execute();
                //     myList.clear();

            } else {
                Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
                finishAffinity();
            }
        }
    };

    private Handler handlerCastingImagesSync = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Network.isNetworkConnected(CastingScreen.this)) {
                rel_upload_screen.setVisibility(View.GONE);
                new GetImages().execute();
            } else {
                Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
                finishAffinity();
            }
        }
    };
    ;
    private PostUserTokenIdSync postUserTokenIdSync;
    private PostSetNotifSync postSetNotifSync;
    private PostFavoriteCastingSync postFavoriteCastingSync;
    private Handler handlerFavoriteSync = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Network.isNetworkConnected(CastingScreen.this)) {
                postFavoriteCastingSync = new PostFavoriteCastingSync();
                postFavoriteCastingSync.execute();
            } else {
                Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_SHORT).show();


            }
        }
    };
    // Sugumaran
    private PostFavoriteImageSync postFavoriteImageSync;
    // Sugumaran Code
    private Handler handlerImgFavoriteSync = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Network.isNetworkConnected(CastingScreen.this)) {
                // postFavoriteCastingSync = new PostFavoriteCastingSync();
                // postFavoriteCastingSync.execute();

                postFavoriteImageSync = new PostFavoriteImageSync();
                postFavoriteImageSync.execute();

            } else {
                Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_SHORT).show();

            }
        }
    };
    private ProgressDialog progressBar;

    public static Bitmap getBitmapFromResources(Resources resources, int resImage) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = 1;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        return BitmapFactory.decodeResource(resources, resImage, options);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            Log.d("RegisterActivity", "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }


    ImageView upload_your_picture1;
    ImageView upload_your_picture2;

    TextView txtCommentCount;
    ImageView imgReportAbuse;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        context = this;
        activity = this;


        //   SharedPreferences prefs = getSharedPreferences(Library.UserName, MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences(Library.UserPREFERENCES, MODE_PRIVATE);
        String restoredText = preferences.getString("username", null);

        /*vasanth you add this*/
        txtSignup = (TextView) findViewById(R.id.txt_signup);
        txtLogin = (TextView) findViewById(R.id.txt_login);
        txtNotificationSettings = (TextView) findViewById(R.id.textView5);
        txtNocast = (TextView) findViewById(R.id.nocast);
        profileName = (TextView) findViewById(R.id.profile_uesrname);
        txtCommentCount = (TextView) findViewById(R.id.txtCommentCount);
        userProfileImage = (ImageView) findViewById(R.id.profile_image);
        lay_login = (LinearLayout) findViewById(R.id.lay_login);
        getAutoSubmitStatus();

        //by nijam
        txtAllCasting = (TextView) findViewById(R.id.txtAllCasting);
        txtLocalCasting = (TextView) findViewById(R.id.txtLocalCasting);
        txtAppliedCasting = (TextView) findViewById(R.id.txtAppliedCasting);
        view_allcasting = (View) findViewById(R.id.view_allcasting);
        view_local = (View) findViewById(R.id.view_local);
        view_applied = (View) findViewById(R.id.view_applied);

        imgReportAbuse = (ImageView) findViewById(R.id.imgReportAbuse);
        SharedPreferences sharedpreferencesLogin = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        imageCount = sharedpreferencesLogin.getBoolean("loginstatus", false);

        imgReportAbuse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                reportMethod();
            }
        });

        txtAllCasting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Network.isNetworkConnected(CastingScreen.this)) {
                    txtNocast.setVisibility(View.GONE);
                    rel_upload_screen.setVisibility(View.GONE);
                    castingsList.setEnabled(false);
                    castingsList.setVisibility(View.VISIBLE);
                    menuListView.setVisibility(View.GONE);
                    myList.clear();
                    swipeList.clear();
                    castingsListArr.clear();
                    page = 0;
                    listType = String.valueOf(1);
                    view_allcasting.setBackgroundColor(Color.parseColor("#CB212E"));
                    view_local.setBackgroundColor(Color.parseColor("#2F2F2F"));
                    view_applied.setBackgroundColor(Color.parseColor("#2F2F2F"));
                    new GetDatas("1").execute();
                    myList.clear();

                } else {
                    Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
                    finishAffinity();
                }
            }
        });

        txtAppliedCasting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences s = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                String ss = s.getString("id", "");


                if (!ss.equals("")) {
                    //          if (Library.getIsPurchased(context).equals("1")) {

                    if (Network.isNetworkConnected(CastingScreen.this)) {
                        rel_upload_screen.setVisibility(View.GONE);
                        txtNocast.setVisibility(View.GONE);
                        myList.clear();
                        swipeList.clear();
                        castingsListArr.clear();
                        listType = String.valueOf(3);
                        castingsList.setEnabled(false);
                        castingsList.setVisibility(View.GONE);
                        menuListView.setVisibility(View.VISIBLE);
                        view_allcasting.setBackgroundColor(Color.parseColor("#2F2F2F"));
                        view_local.setBackgroundColor(Color.parseColor("#2F2F2F"));
                        view_applied.setBackgroundColor(Color.parseColor("#CB212E"));
                        new GetDatas("3").execute();
                        menuListView.setSelection(0);
                    } else {
                        Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
                        finishAffinity();
                    }
                   /* } else {
                        alertdialog();
                    }*/

                } else {
                    Library.alert(context, /*"Please subscribe to apply for castings from within the app"*/"Please login to view all the applied castings");

                }
            }
        });

        txtLocalCasting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Network.isNetworkConnected(CastingScreen.this)) {
                    txtNocast.setVisibility(View.GONE);
                    rel_upload_screen.setVisibility(View.GONE);
                    castingsList.setEnabled(false);
                    castingsList.setVisibility(View.VISIBLE);
                    menuListView.setVisibility(View.GONE);
                    myList.clear();
                    swipeList.clear();
                    castingsListArr.clear();
                    page = 0;
                    listType = String.valueOf(2);
                    view_allcasting.setBackgroundColor(Color.parseColor("#2F2F2F"));
                    view_local.setBackgroundColor(Color.parseColor("#CB212E"));
                    view_applied.setBackgroundColor(Color.parseColor("#2F2F2F"));
                    new GetDatas("2").execute();
                    castingsList.setSelection(0);
                    myList.clear();

                } else {
                    Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
                    finishAffinity();
                }
            }
        });

        /*txtNotificationSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Library.getId(context).equals("")) {
                    //  startActivity(new Intent(CastingScreen.this, Notification_Settings.class));
                    Intent i = new Intent(CastingScreen.this, Notification_Settings.class);
                    startActivityForResult(i, 90);
                } else {
                    Library.alert(context, "To access notification please login");
                }
            }
        });
*/


        txtCommentCount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("Talentid == " + myListImage.get(sequenceOrder).imgId);

                Intent in = new Intent(context, CommentListActivity.class);
                in.putExtra("talentid", myListImage.get(sequenceOrder).imgId);
                startActivity(in);


            }
        });

        if (!Library.getId(context).equals("")) {
            imgReportAbuse.setVisibility(View.VISIBLE);
        } else {
            imgReportAbuse.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        if (intent.hasExtra("CalledBy")) {
            checkRate();
        } else if (intent.hasExtra("from")) {
            position = 0;
        }

        // New Concept
        upload_your_picture1 = (ImageView) findViewById(R.id.upload_your_picture1);
        upload_your_picture2 = (ImageView) findViewById(R.id.upload_your_picture2);
        upload_your_picture1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImageToBackEnd();

//                if (sequenceOrder < myListImage.size()) {
//                    CastingImagesModel get = myListImage.get(sequenceOrder);
//                    Intent intent = new Intent(context, ContactsForm.class);
//                    intent.putExtra("imageId", get.imgId);
//                    startActivity(intent);
//                }else{
//                    Intent intent = new Intent(context, ContactsForm.class);
//                    startActivity(intent);
//                }

            }
        });
        upload_your_picture2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (sequenceOrder < myListImage.size()) {
                    CastingImagesModel get = myListImage.get(sequenceOrder);
                    Intent intent = new Intent(context, ContactsForm.class);
                    intent.putExtra("imageId", get.imgId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ContactsForm.class);
                    startActivity(intent);
                }

            }
        });

        lay_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Library.getId(context).length() > 0) {
                    Intent intent = new Intent(CastingScreen.this, MyProfile.class);
                    startActivityForResult(intent, 99);

                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setVisibility(View.GONE);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                imageGrid();
            }
        });

        // final Animation animScale = AnimationUtils.loadAnimation(this,
        // R.anim.scale_up);

        // bottmLay =(RelativeLayout)findViewById(R.id.bottmLay);


        imgFav = (CheckBox) findViewById(R.id.imgFav);


        animBlink = AnimationUtils.loadAnimation(this, R.anim.blink);
        txtSwipeCastings = (TextView) findViewById(R.id.swipe_text);
        txtSwipeCastings.setVisibility(View.GONE);
        txtSwipeCastings.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                txtSwipeCastings.setVisibility(View.GONE);
                return false;
            }
        });

        // Initialize the library with your
        // Mixpanel project token, MIXPANEL_TOKEN, and a reference
        // to your application context.

        if (TextUtils.isEmpty(regId)) {
            regId = FirebaseInstanceId.getInstance().getToken();
            // Toast.makeText(getApplicationContext(),token,Toast.LENGTH_LONG).show();
            Log.d("Token", "Refreshed token: " + regId);

            storeRegistrationId(context, regId);
        }


        swpe = true;

        myList = new ArrayList<CastingDetailsModel>();
        castingsListArr = new ArrayList<CastingDetailsModel>();
        // bList = new ArrayList<CastingDetailsModel>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1900; i--) {
            years.add(Integer.toString(i));
        }

        userTokenId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        DebugReportOnLocat.ln("Android ID:" + userTokenId);
        sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
        androidUserID = sharedpreferences.getString(Library.CUSTOMER_ID, "");
        remainingDays = sharedpreferences.getString(Library.REMAINING_DAYS, "");
        strPerformanceType = sharedpreferences.getString(Library.PERFORMANCE_TYPE, "");
        strUnionType = sharedpreferences.getString(Library.UNION, "");
        strCurrentLocation = sharedpreferences.getString(Library.CURRENT_LOCATION, "");
        strSelectedLocation = sharedpreferences.getString(Library.SELECTED_LOCATION, "");
        strGender = sharedpreferences.getString(Library.GENDER, "");
        strEthnicity = sharedpreferences.getString(Library.ETHNICITY, "");
        dlatitude = sharedpreferences.getFloat(Library.LAT, 0);
        dlongitude = sharedpreferences.getFloat(Library.LNG, 0);
        strBithYear = sharedpreferences.getString(Library.BIRTH, "");
        rateThisAppFlag = sharedpreferences.getInt(Library.RATEIT, 0);
        UnionType = sharedpreferences.getBoolean(Library.NON_UNION, false);
        notification = sharedpreferences.getBoolean(Library.NOTIFICATION, false);
        helpOverlayFlag = sharedpreferences.getBoolean(Library.HELP_OVERLAY, false);


        findElements();
        clickMenuEvents();
        getAutoSubmitStatus();
        getValue();

        SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
        notify_status = sharedpreferences.getString("new_notification", "");
        message = sharedpreferences.getString("autosubmit", "");
        String city = sharedpreferences.getString("preferCities", "");
        if (city.equals("")) {
            clear.setVisibility(View.GONE);
        } else {
            clear.setVisibility(View.VISIBLE);
            editTextLocation.setText(city);
        }

        if (notify_status.equals("1")) {
            notification_toggle.setChecked(true);
        } else {
            notification_toggle.setChecked(false);
        }

        if (message.equals("1")) {
            auto_submit_toggle.setChecked(true);
        } else {
            auto_submit_toggle.setChecked(false);
        }

        mixpanel = MixpanelAPI.getInstance(context, Library.MIXPANEL_TOKEN);

        //10-08-2016 add applay button

        btn_apply = (TextView) findViewById(R.id.btn_apply);
        layBottom = (LinearLayout) findViewById(R.id.layBottom);
        imgTick = (ImageView) findViewById(R.id.imgTick);

        castingsList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                // TODO Auto-generated method stub

                listViewStatus = false;
                String getTitle = "";
                if (castingsListArr != null && castingsListArr.size() != 0) {
                    getTitle = castingsListArr.get(pos).castingTitle.toString();
                    System.out.println(getTitle);
                }
                //
                // int myPoss = pos % 20;
                // int myPag = pos % 20;
                // int myPo = castingsListArr.size() / 20;
                // int totalList = (myPo * 20) + 20;
                //
                // if (castingsListArr.size() < totalList) {
                // ArrayList<CastingDetailsModel> list = null;
                // for (int m = 0; m < totalList; m++) {
                // list = new ArrayList<CastingDetailsModel>();
                // list.add(castingsListArr.get(m));
                // }
                //
                // page = myPag;
                // myList = new ArrayList<CastingDetailsModel>(list);
                // setAllData(myList, myPoss);
                // } else {
                // //int myPo = castingsListArr.size() / 20;
                // ArrayList<CastingDetailsModel> list = null;
                // for (int m = (myPo*20); m < totalList; m++) {
                // list = new ArrayList<CastingDetailsModel>();
                // list.add(castingsListArr.get(m));
                // }
                //
                // page = myPo;
                // myList = new ArrayList<CastingDetailsModel>(list);
                // setAllData(myList, myPoss);
                //
                // }

                // page = myPage;
                //
                // myList = new ArrayList<CastingDetailsModel>(tempList);
                // setAllData(myList, myPoss);

                int padge = page;
                int scrollC = scrollCount;

                int size = CastivateApplication.getInstance().hashMap.size();

                for (int i = 0; i < (size + 1); i++) {

                    if (CastivateApplication.getInstance().hashMap.containsKey(i)) {
                        ArrayList<CastingDetailsModel> tempList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(i));

                        for (int j = 0; j < tempList.size(); j++) {
                            String newTitle = tempList.get(j).castingTitle.toString();
                            if (newTitle.equals(getTitle)) {
                                int getPoss = j;

                                head = 0;
                                tail = tempList.size();
                                page = i;
                                myList = new ArrayList<CastingDetailsModel>(tempList);
                                setAllData(myList, getPoss);

                                break;
                            }
                        }
                    }
                }

            }
        });


        menuListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewStatus = false;
                String getTitle = "";
                if (castingsListArr != null && castingsListArr.size() != 0) {
                    getTitle = castingsListArr.get(position).castingTitle.toString();
                    System.out.println(getTitle);
                }
                //
                // int myPoss = pos % 20;
                // int myPag = pos % 20;
                // int myPo = castingsListArr.size() / 20;
                // int totalList = (myPo * 20) + 20;
                //
                // if (castingsListArr.size() < totalList) {
                // ArrayList<CastingDetailsModel> list = null;
                // for (int m = 0; m < totalList; m++) {
                // list = new ArrayList<CastingDetailsModel>();
                // list.add(castingsListArr.get(m));
                // }
                //
                // page = myPag;
                // myList = new ArrayList<CastingDetailsModel>(list);
                // setAllData(myList, myPoss);
                // } else {
                // //int myPo = castingsListArr.size() / 20;
                // ArrayList<CastingDetailsModel> list = null;
                // for (int m = (myPo*20); m < totalList; m++) {
                // list = new ArrayList<CastingDetailsModel>();
                // list.add(castingsListArr.get(m));
                // }
                //
                // page = myPo;
                // myList = new ArrayList<CastingDetailsModel>(list);
                // setAllData(myList, myPoss);
                //
                // }

                // page = myPage;
                //
                // myList = new ArrayList<CastingDetailsModel>(tempList);
                // setAllData(myList, myPoss);

                int padge = page;
                int scrollC = scrollCount;

                int size = CastivateApplication.getInstance().hashMap.size();

                for (int i = 0; i < (size + 1); i++) {

                    if (CastivateApplication.getInstance().hashMap.containsKey(i)) {
                        ArrayList<CastingDetailsModel> tempList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(i));

                        for (int j = 0; j < castingsListArr.size(); j++) {
                            String newTitle = castingsListArr.get(j).castingTitle.toString();
                            if (newTitle.equals(getTitle)) {
                                int getPoss = j;

                                head = 0;
                                tail = tempList.size();
                                page = i;
                                myList = new ArrayList<CastingDetailsModel>(castingsListArr);
                                setAllData(myList, getPoss);

                                break;
                            }
                        }
                    }
                }


            }
        });


        castingsList.setOnScrollListener(new OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            private LinearLayout lBelow;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;

            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount && this.currentScrollState == SCROLL_STATE_IDLE) {
                    /** To do code here */

                    int totalRecord = 0;
                    try {
                        totalRecord = Integer.parseInt(myList.get(0).totalCasting);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // if (totalRecord > totalItem) {
                    if (totalRecord > totalItem) {

                        scrollCount++;
                        int pages = page;
                        int totalpage = totalPages;

                        if (page < totalPages) {
                            page = page + 1;
                            int newPage = page;
                            if (listType.equals("2")) {
                                if (Network.isNetworkConnected(CastingScreen.this)) {
                                    txtNocast.setVisibility(View.GONE);
                                    rel_upload_screen.setVisibility(View.GONE);
                                    castingsList.setEnabled(false);
                                    castingsList.setVisibility(View.VISIBLE);
                                    menuListView.setVisibility(View.GONE);
                                    //       myList.clear();
                                    //      swipeList.clear();
                                    //      castingsListArr.clear();
                                    listType = String.valueOf(2);
                                    view_allcasting.setBackgroundColor(Color.parseColor("#2F2F2F"));
                                    view_local.setBackgroundColor(Color.parseColor("#CB212E"));
                                    view_applied.setBackgroundColor(Color.parseColor("#2F2F2F"));
                                    new GetDatas("2").execute();
                                    //      myList.clear();

                                } else {
                                    Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
                                    finishAffinity();
                                }
                            } else {

                                handlerCastingSync.sendEmptyMessage(0);
                            }


                            // for (int i = 0; i < totalPages; i++) {
                            // if (!hashMap.containsKey(i)) {
                            // page = i;
                            // handlerCastingSync.sendEmptyMessage(0);
                            // break;
                            // }
                            // }

                        }
                        // else {
                        // Toast.makeText(context, "Castings List ended",
                        // Toast.LENGTH_SHORT).show();
                        // }

                        castingsList.setAdapter(null);

                        ArrayList<CastingDetailsModel> castingsListArrList = new ArrayList<CastingDetailsModel>(castingsListArr);

                        refreshYourAdapter(castingsListArrList);
                        castingsList.setAdapter(castListAdapter);

                        castListAdapter.notifyDataSetChanged();
                        castingsList.setSelection(0);
                        int getpos = castingsListArr.size();
                        System.out.println("getPos " + getpos);
                        castingsList.setSelection(getpos - 19);

                    }

                }
            }
        });


        menuListView.setOnScrollListener(new OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            private LinearLayout lBelow;


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount && this.currentScrollState == SCROLL_STATE_IDLE) {
                    /** To do code here */

                    int totalRecord = Integer.parseInt(castingsListArr.get(0).totalCasting);
                    // if (totalRecord > totalItem) {
                    if (totalRecord > totalItem) {

                        scrollCount++;
                        int pages = page;
                        int totalpage = totalPages;

                        if (page < totalPages) {
                            page = page + 1;
                            int newPage = page;

                            handlerCastingSync.sendEmptyMessage(0);

                            // for (int i = 0; i < totalPages; i++) {
                            // if (!hashMap.containsKey(i)) {
                            // page = i;
                            // handlerCastingSync.sendEmptyMessage(0);
                            // break;
                            // }
                            // }

                        }
                        // else {
                        // Toast.makeText(context, "Castings List ended",
                        // Toast.LENGTH_SHORT).show();
                        // }

                        menuListView.setAdapter(null);

                        ArrayList<CastingDetailsModel> castingsListArrList = new ArrayList<CastingDetailsModel>(castingsListArr);

                        refreshYour(castingsListArrList);
                        castingsList.setAdapter(swipeViewListAdapter);
                        swipeViewListAdapter.notifyDataSetChanged();
                        castingsList.setSelection(0);
                        int getpos = castingsListArr.size();
                        System.out.println("getPos " + getpos);
                        menuListView.setSelection(getpos - 19);

                    }

                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;
            }
        });


        if (helpOverlayFlag == false) {
            // castivityScreen.setVisibility(View.GONE);
            help_overlay.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left));
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

            help_overlay.setVisibility(View.VISIBLE);
            sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            editor.putBoolean(Library.HELP_OVERLAY, true);
            editor.commit();
            // Library.helpOverlayView=false;
        }

        if (Network.isNetworkConnected(CastingScreen.this)) {
            if (!androidUserID.equals("") && !Library.getEmailId(context).equals("")) {
                String get;
                if (savedInstanceState == null && isFavScreen == true) {
                    Bundle extras = getIntent().getExtras();
                    if (extras == null) {
                        get = null;
                    } else {

                        if (getIntent().hasExtra("fromImage")) {
                            getFrom = extras.getString("fromImage");
                        }

                        if (getFrom.equalsIgnoreCase("Image")) {
                            handlerCastingSync.sendEmptyMessage(0);
                        } else {
                            if (getIntent().hasExtra("rollID")) {
                                get = extras.getString("rollID");
                                PushRollID = get;
                                getPushNotificationCasting();
                                isFavScreen = false;

                                isPush = true;
                                new GetDatasNew().execute();
                            }
                        }
                    }
                } else {

                    if (getIntent().hasExtra("fromImage")) {
                        handlerCastingSync.sendEmptyMessage(0);
                    } else if (getIntent().hasExtra("rollID")) {
                        get = getIntent().getStringExtra("rollID");
                        if (myList != null && myList.size() != 0) {
                            PushRollID = get;
                            getPushNotificationCasting();
                            isFavScreen = false;
                        } else {
                            PushRollID = get;
                            isPush = true;
                            handlerCastingSync.sendEmptyMessage(0);
                        }
                    } else if (getIntent().hasExtra("Purchase")) {

                        String purchaseType = getIntent().getStringExtra("Purchase");
                        if (Library.getIsPurchased(context).equals("1") && purchaseType.equals("0")) {
                            purchaseRetrofit("", "0");
                        } else if (Library.getIsPurchased(context).equals("0") && !purchaseType.equals("0")) {
                            purchaseRetrofit(purchaseType, "1");
                        } else {
                            handlerCastingSync.sendEmptyMessage(0);
                        }

                    } else {
                        handlerCastingSync.sendEmptyMessage(0);
                    }

                }

            } else {

                postUserTokenIdSync = new PostUserTokenIdSync();
                postUserTokenIdSync.execute();


            }
        } else {
            Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_SHORT).show();
            finishAffinity();
        }
        editTextLocation.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                tap_current_location.setVisibility(View.VISIBLE);
                return false;

            }
        });
        // Adding textchange listener
        editTextLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Creating a DownloadTask to download Google Places matching
                // "s"
                if (Network.isNetworkConnected(context)) {
                    if (s.toString().trim().contains(strSelectedLocation)) {
                        tap_current_location.setVisibility(View.VISIBLE);
                        // hideSoftKeyboard();
                        // KeyboardUtility.hideSoftKeyboard(activity);
                        clear.setVisibility(View.VISIBLE);
                    } else if (s.toString().trim().contains(strCurrentLocation)) {
                        tap_current_location.setVisibility(View.GONE);
                        hideSoftKeyboard();
                        clear.setVisibility(View.VISIBLE);
                        // KeyboardUtility.hideSoftKeyboard(activity);
                    } else {
                        tap_current_location.setVisibility(View.VISIBLE);
                    }
                    if (s.length() == 0) {
                        tap_current_location.setVisibility(View.GONE);
                    }

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

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Setting an item click listener for the AutoCompleteTextView dropdown
        // list
        locationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id) {
                // TODO Auto-generated method stub
                // hideSoftKeyboard();
                // tap_current_location.setText(stringList.get(position).toString());

                editTextLocation.setText(stringList.get(index).toString());

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

                            // Toast.makeText(CastingScreen.this, "Lat "+
                            // dlatitude+"\n Long "+ dlongitude,
                            // Toast.LENGTH_SHORT).show();

                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } catch (NullPointerException e) {
                    // TODO: handle exception
                    Toast.makeText(context, "Please enable the GPS to see filter casting on location based.", Toast.LENGTH_LONG).show();
                }

                tap_current_location.setVisibility(View.GONE);

            }
        });
        // if (ratePopFlag == false) {
        // if (rateThisAppFlag >= 3) {
        // rateThisAppAlert();
        // }
        // }

        if (strPerformanceType.contains("actor")) {
            chkActor.setChecked(true);
            chkActor.setTextColor(context.getResources().getColor(R.color.white));
        }

        if (strPerformanceType.contains("model")) {
            chkModel.setChecked(true);
            chkModel.setTextColor(context.getResources().getColor(R.color.white));
        }
        if (strPerformanceType.contains("singer")) {
            chkSinger.setChecked(true);
            chkSinger.setTextColor(context.getResources().getColor(R.color.white));
        }
        if (strPerformanceType.contains("dancer")) {
            chkDancer.setChecked(true);
            chkDancer.setTextColor(context.getResources().getColor(R.color.white));
        }

        if (strGender.contains("female")) {
            chkFemale.setChecked(true);
            chkFemale.setTextColor(context.getResources().getColor(R.color.white));
        } else if (strGender.equals("male")) {
            chkMale.setChecked(true);
            chkMale.setTextColor(context.getResources().getColor(R.color.white));
        }
        if (UnionType) {

            chkUnion.setChecked(true);
            chkUnion.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            //   chkNonUnion.setChecked(true);
            chkNonUnion.setTextColor(context.getResources().getColor(R.color.dark_black));
        }
        if (strBithYear != null) {
            birthYear.setText(strBithYear);

        } else {
            birthClear.setVisibility(View.INVISIBLE);
        }

        if (strEthnicity != null) {
            txtEthnicity.setText(strEthnicity);
        }

        favCountText.setOnClickListener(this);
        txtReset.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
                hideSoftKeyboard();

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
                txtContent.setText("            Reset Filter            ");
                btnOk.setText("No");
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText("Yes");
                viewSep.setVisibility(View.VISIBLE);

                btnCancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();


                        if (rel_image_screen.getVisibility() == View.VISIBLE) {
                            rel_image_screen.setVisibility(View.GONE);
                            rel_upload_screen.setVisibility(View.GONE);
                        }
                        castImageViewStatus = false;
                        gear_icon.setVisibility(View.VISIBLE);
                        outbox_icon.setVisibility(View.VISIBLE);
                        // if (listViewStatus)
                        // castingsListIconChange.setVisibility(View.VISIBLE);
                        // else
                        // castingsListIcon.setVisibility(View.VISIBLE);

                        gear_icon_mirror.setVisibility(View.INVISIBLE);
                        chkActor.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkActor.setChecked(false);
                        chkModel.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkModel.setChecked(false);
                        chkSinger.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkSinger.setChecked(false);
                        chkDancer.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkDancer.setChecked(false);
                        chkMale.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkMale.setChecked(false);
                        chkFemale.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkFemale.setChecked(false);
                        chkUnion.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkUnion.setChecked(false);
                        chkNonUnion.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkNonUnion.setChecked(false);
                        birthYear.setText("");
                        birthClear.setVisibility(View.INVISIBLE);
                        dlatitude = dclatitude;
                        dlongitude = dclongitude;
                        editTextLocation.setText(strCurrentLocation);
                        txtEthnicity.setText("");
                        if (selchkboxlist != null) {
                            selchkboxlist.clear();
                        }
                        CastivateApplication.getInstance().hashMap.clear();
                        bs = false;

                        SharedPreferences settings = context.getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                        Editor editor1 = settings.edit();
                        editor1.remove(Library.BIRTH);
                        editor1.remove(Library.ETHNICITY);
                        editor1.remove(Library.GENDER);
                        editor1.remove(Library.PERFORMANCE_TYPE);
                        editor1.remove(Library.SELECTED_LOCATION);
                        editor1.remove(Library.UNION);
                        editor1.commit();

                        strPerformanceType = "";
                        strGender = "";
                        strBithYear = "";
                        strEthnicity = "";
                        strUnionType = "";
                        strSelectedLocation = "";
                        clear.setVisibility(View.INVISIBLE);
                        page = 0;
                        totalPages = 0;
                        castViewStatus = false;
                        castViewStatusreak = false;
                        swipe = "";
                        bSwipe = "";
                        swpe = true;
                        position = 0;
                        bs = false;
                        if (castivityScreen.getVisibility() == View.VISIBLE) {
                            castivityScreen.setVisibility(View.GONE);
                        }
                        castingViewNoImage.setVisibility(View.VISIBLE);
                        castingsList.setVisibility(View.GONE);
                        dlongitude = dclongitude;
                        dlatitude = dclatitude;
                        grid_view_photos.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                        myList = new ArrayList<CastingDetailsModel>();
                        castingsListArr = new ArrayList<CastingDetailsModel>();
                        //        handlerCastingSync.sendEmptyMessage(0);


                        if (Network.isNetworkConnected(CastingScreen.this)) {
                            txtNocast.setVisibility(View.GONE);
                            rel_upload_screen.setVisibility(View.GONE);
                            castingsList.setEnabled(false);
                            castingsList.setVisibility(View.VISIBLE);
                            menuListView.setVisibility(View.GONE);
                            myList.clear();
                            swipeList.clear();
                            castingsListArr.clear();
                            listType = String.valueOf(1);
                            view_allcasting.setBackgroundColor(Color.parseColor("#CB212E"));
                            view_local.setBackgroundColor(Color.parseColor("#2F2F2F"));
                            view_applied.setBackgroundColor(Color.parseColor("#2F2F2F"));
                            new GetDatas("1").execute();
                            myList.clear();

                        } else {
                            Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
                            finishAffinity();
                        }


                    }
                });
                btnOk.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
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


                /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);

                alertDialogBuilder

                        .setMessage("            Reset Filter            ").setCancelable(false).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    public void onClick(DialogInterface dialog, int id) {
                        if (rel_image_screen.getVisibility() == View.VISIBLE) {
                            rel_image_screen.setVisibility(View.GONE);
                            rel_upload_screen.setVisibility(View.GONE);
                        }
                        castImageViewStatus = false;
                        gear_icon.setVisibility(View.VISIBLE);
                        outbox_icon.setVisibility(View.VISIBLE);
                        // if (listViewStatus)
                        // castingsListIconChange.setVisibility(View.VISIBLE);
                        // else
                        // castingsListIcon.setVisibility(View.VISIBLE);

                        gear_icon_mirror.setVisibility(View.INVISIBLE);
                        chkActor.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkActor.setChecked(false);
                        chkModel.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkModel.setChecked(false);
                        chkSinger.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkSinger.setChecked(false);
                        chkDancer.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkDancer.setChecked(false);
                        chkMale.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkMale.setChecked(false);
                        chkFemale.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkFemale.setChecked(false);
                        chkUnion.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkUnion.setChecked(false);
                        chkNonUnion.setTextColor(context.getResources().getColor(R.color.dark_black));
                        chkNonUnion.setChecked(false);
                        birthYear.setText("");
                        dlatitude = dclatitude;
                        dlongitude = dclongitude;
                        editTextLocation.setText(strCurrentLocation);
                        txtEthnicity.setText("");
                        if (selchkboxlist != null) {
                            selchkboxlist.clear();
                        }
                        CastivateApplication.getInstance().hashMap.clear();
                        bs = false;

                        SharedPreferences settings = context.getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                        Editor editor1 = settings.edit();
                        editor1.remove(Library.BIRTH);
                        editor1.remove(Library.ETHNICITY);
                        editor1.remove(Library.GENDER);
                        editor1.remove(Library.PERFORMANCE_TYPE);
                        editor1.remove(Library.UNION);
                        editor1.commit();

                        strPerformanceType = "";
                        strGender = "";
                        strBithYear = "";
                        strEthnicity = "";
                        strUnionType = "";

                        page = 0;
                        totalPages = 0;
                        castViewStatus = false;
                        castViewStatusreak = false;
                        swipe = "";
                        bSwipe = "";
                        swpe = true;
                        position = 0;
                        bs = false;
                        if (castivityScreen.getVisibility() == View.VISIBLE) {
                            castivityScreen.setVisibility(View.GONE);
                        }
                        castingViewNoImage.setVisibility(View.VISIBLE);
                        castingsList.setVisibility(View.GONE);
                        dlongitude = dclongitude;
                        dlatitude = dclatitude;
                        grid_view_photos.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                        myList = new ArrayList<CastingDetailsModel>();
                        castingsListArr = new ArrayList<CastingDetailsModel>();
                        handlerCastingSync.sendEmptyMessage(0);
                    }
                })

                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
*/
            }
        });

        rel_cast.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                rel_upload_screen.setEnabled(true);

                // KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
                hideSoftKeyboard();
                castingViewNoImage.setVisibility(View.VISIBLE);
                castingsList.setVisibility(View.GONE);
                if (rel_image_screen.getVisibility() == View.VISIBLE) {
                    rel_image_screen.setVisibility(View.GONE);
                    rel_upload_screen.setVisibility(View.GONE);
                    castImageViewStatus = false;
                }
                // if (listViewStatus)
                // castingsListIconChange.setVisibility(View.VISIBLE);
                // else
                // castingsListIcon.setVisibility(View.VISIBLE);
                gear_icon.setVisibility(View.VISIBLE);
                grid_view_photos.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                tap_current_location.setVisibility(View.GONE);
                outbox_icon.setVisibility(View.VISIBLE);
                gear_icon_mirror.setVisibility(View.INVISIBLE);
                castivityScreen.setVisibility(View.VISIBLE);
                if (castivityScreen.getVisibility() == View.VISIBLE) {
                    castivityScreen.setVisibility(View.GONE);
                }
                castingViewNoImage.setVisibility(View.VISIBLE);
                castingsList.setVisibility(View.GONE);
                // filterValue=1;

                myList = new ArrayList<CastingDetailsModel>();
                castingsListArr = new ArrayList<CastingDetailsModel>();
                page = 0;
                CastivateApplication.getInstance().hashMap.clear();
                position = 0;

                castViewStatus = false;
                castViewStatusreak = false;
                swipe = "";
                bSwipe = "";
                swpe = true;
                bs = false;
                totalPages = 0;

                handlerCastingSync.sendEmptyMessage(0);
            }
        });

        select_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
                hideSoftKeyboard();

                rel_upload_screen.setEnabled(true);
                try {
                    currentLocation();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                // v.startAnimation(animScale);

                // Handler handler = null;
                // handler = new Handler();
                // handler.postDelayed(new Runnable() {
                // public void run() {
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
                gear_icon.setVisibility(View.VISIBLE);
                // if (listViewStatus)
                // castingsListIconChange.setVisibility(View.VISIBLE);
                // else
                // castingsListIcon.setVisibility(View.VISIBLE);
                outbox_icon.setVisibility(View.VISIBLE);
                gear_icon_mirror.setVisibility(View.INVISIBLE);
                grid_view_photos.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                if (castivityScreen.getVisibility() == View.VISIBLE) {
                    castivityScreen.setVisibility(View.GONE);
                }
                // filterValue=1;
                // list = new ArrayList<CastingDetailsModel>();
                myList = new ArrayList<CastingDetailsModel>();
                castingsListArr = new ArrayList<CastingDetailsModel>();
                myList.clear();
                page = 0;
                bs = false;
                position = 0;
                // by nijam
                strSelectedLocation = editTextLocation.getText().toString();
                if (strSelectedLocation == "") {

                    myList.clear();
                    swipeList.clear();
                    castingsListArr.clear();
                }
                castViewStatus = false;
                castViewStatusreak = false;
                swipe = "";
                bSwipe = "";
                swpe = true;

                totalPages = 0;

                CastivateApplication.getInstance().hashMap.clear();
                tap_current_location.setVisibility(View.GONE);
                castingViewNoImage.setVisibility(View.VISIBLE);
                castingsList.setVisibility(View.GONE);

                if (rel_image_screen.getVisibility() == View.VISIBLE) {
                    rel_image_screen.setVisibility(View.GONE);
                    rel_upload_screen.setVisibility(View.GONE);
                    castImageViewStatus = false;
                }
                myList = new ArrayList<CastingDetailsModel>();
                JSONObject props = new JSONObject();
                try {
                    props.put("User ID", androidUserID);
                    props.put("Location", strSelectedLocation);
                    props.put("Gender", strGender);
                    props.put("Birth Year", strBithYear);
                    props.put("Ethnicity", strEthnicity);
                    props.put("Performance Type", strPerformanceType);
                    props.put("Union Type", strUnionType);
                    props.put("Android Version", os);
                    // props.put("Castivate Version", )

                    props.put("UserID", androidUserID);
                    mixpanel.track("Filter Criteria", props);
                    props.put("Plan", "Premium");
                    mixpanel.getPeople().identify(androidUserID);

                } catch (JSONException e) {
                    // TODO: handle exception
                }

//by nijam
                textCastivate.setVisibility(View.GONE);
                castingViewNoImage.setVisibility(View.GONE);
                rel_image_screen.setVisibility(View.GONE);
                rel_upload_screen.setVisibility(View.GONE);
                castingsList.setVisibility(View.VISIBLE);

                castingsListIcon.setVisibility(View.GONE);
                outbox_icon.setVisibility(View.INVISIBLE);
                // by nijam
                tabLayout.setVisibility(View.VISIBLE);
                // castingsListIconChange.setVisibility(View.GONE);// My
                rel_info_ListView.setVisibility(View.VISIBLE);
                listViewStatus = true;

                grid_view_photos.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);

                castListAdapter = new CastingsListAdapter(CastingScreen.this, castingsListArr);
                refreshYourAdapter(castingsListArr);
                castingsList.setAdapter(castListAdapter);
                castingsList.setSelection(0);
                castListAdapter.notifyDataSetChanged();


                //     handlerCastingSync.sendEmptyMessage(0);
                if (Network.isNetworkConnected(CastingScreen.this)) {
                    txtNocast.setVisibility(View.GONE);
                    rel_upload_screen.setVisibility(View.GONE);
                    castingsList.setEnabled(false);
                    castingsList.setVisibility(View.VISIBLE);
                    menuListView.setVisibility(View.GONE);
                    myList.clear();
                    swipeList.clear();
                    castingsListArr.clear();
                    listType = String.valueOf(1);
                    view_allcasting.setBackgroundColor(Color.parseColor("#CB212E"));
                    view_local.setBackgroundColor(Color.parseColor("#2F2F2F"));
                    view_applied.setBackgroundColor(Color.parseColor("#2F2F2F"));
                    new GetDatas("1").execute();
                    myList.clear();

                } else {
                    Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
                    finishAffinity();
                }


            }
            // }, 1500);
            //
            // }
        });

        clickEvents();

        // DebugReportOnLocat.ln("Register Id:"+registerID);
        // Gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        rel_cast_view.setOnTouchListener(gestureListener);
        lay_submission_detail_no_image.setOnTouchListener(gestureListener);
        casting_image.setOnTouchListener(gestureListener);
        rel_cast_outer.setOnTouchListener(gestureListener);

        // Sugumaran Changes (25th May 16)
        // imgGestureDetector = new GestureDetector(this, new
        // ImageGestureDetector());
        // imgGestureListener = new View.OnTouchListener() {
        // public boolean onTouch(View v, MotionEvent event) {
        // return imgGestureDetector.onTouchEvent(event);
        // }
        // };
        // // Sugumaran Changes (25th May 16)
        // casting_image_only.setOnTouchListener(imgGestureListener);

        casting_image_only.setOnTouchListener(gestureListener);

        // grid_view_photos.setOnTouchListener(gestureListener);
        try {
            currentLocation();

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        // home_screen
        // rel_upload_screen

        // System.out.println("TopBar width * Height: "+home_screen.getWidth()+" x "+home_screen.getHeight());
        // System.out.println("BottomBar width * Height: "+rel_upload_screen.getWidth()+" x "+rel_upload_screen.getHeight());

        mixpanel.getPeople().identify(androidUserID);
        mixpanel.getPeople().set("Plan", "Premium");


        if (Library.getId(context).length() > 0) {
            //txtLogin.setText("");//dsgrhseryhreyhswreyhsrewhj
            txtLogin.setText("My Profile");//dsgrhseryhreyhswreyhsrewhj
//                        textlogout.setVisibility(View.VISIBLE);

                        /*vasanth you add this*/
            txtLogin.setVisibility(View.GONE);
            txtSignup.setVisibility(View.GONE);
            userProfileImage.setVisibility(View.VISIBLE);
            profileName.setVisibility(View.VISIBLE);


            sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
            String username = sharedpreferences.getString("username", "");
            String profileImage = sharedpreferences.getString("profileImage", "");
            profileName.setText(username);

            profileName.setText(Html.fromHtml("<u>" + username + "</u>"));
            if (!profileImage.equals(""))
                Picasso.with(CastingScreen.this).load(profileImage).placeholder(R.drawable.avathar_profile)
                        .error(R.drawable.avathar_profile).into(userProfileImage);

        } else {
            txtLogin.setText("Login");
            textlogout.setVisibility(View.GONE);
            userProfileImage.setVisibility(View.GONE);
            profileName.setVisibility(View.GONE);

                        /*vasanth you add this*/
            txtLogin.setVisibility(View.VISIBLE);
            txtSignup.setVisibility(View.VISIBLE);
        }

        if (Library.getUserId(context) != null) {
            getImageVideoList();
        }

    }

    private void getAutoSubmitStatus() {
        userId = Library.getUserId(context);
        AutoSubmit per = new AutoSubmit(userId);
        RegisterRemoteApi.getInstance().setAutoSubmit(per);
        RegisterRemoteApi.getInstance().autoSubmit(context, new Callback<AutoSubmitOutput>() {
            @Override
            public void success(AutoSubmitOutput autoSubmitOutput, Response response) {
                try {
                    System.out.println("response---->" + autoSubmitOutput.autoSubmitStatus);
                    message = autoSubmitOutput.autoSubmitStatus;
                    sharedPreference = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    editor = sharedPreference.edit();
                    editor.putString("autosubmit", message);
                    editor.commit();

                    if (message.equals("1")) {
                        auto_submit_toggle.setChecked(true);
                    } else {
                        auto_submit_toggle.setChecked(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("failed");
            }
        });

    }

    private void getValue() {
        if (userId != null) {

            PersonNotificationInput per = new PersonNotificationInput(userId);
            pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
            RegisterRemoteApi.getInstance().setPersonNotificationInput(per);
            RegisterRemoteApi.getInstance().personNotification(context, new Callback<PersonNotificationOutput>() {
                @Override
                public void success(PersonNotificationOutput personNotificationOutput, Response response) {
                    closeProgress();
                    notify_status = personNotificationOutput.notify_status;
                    //   notificationStatus();
                    if (notify_status.equals("1")) {
                        notification_toggle.setChecked(true);
                    } else {
                        notification_toggle.setChecked(false);
                    }
                    sharedPreference = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    editor = sharedPreference.edit();
                    editor.putString("new_notification", notify_status);
                    editor.commit();

                }

                @Override
                public void failure(RetrofitError error) {
                    closeProgress();
                  /*  Toast.makeText(context, "The requested service is not available. Please try again after sometime",
                            Toast.LENGTH_LONG).show();*/
                    //   finish();
                }
            });
        }
    }

   /* private void notificationStatus() {
        if (notify_status.equals("1")) {
            notification_toggle.setChecked(true);
        } else {
            notification_toggle.setChecked(false);
        }
    }*/


    private void updateAutoSubmit() {
        userId = Library.getUserId(context);
        UpdateAutoSubmitInput submitInput = new UpdateAutoSubmitInput(status, userId);
        System.out.println("input---->" + status + userId);
        RegisterRemoteApi.getInstance().setUpdateAutoSubmitInput(submitInput);
        RegisterRemoteApi.getInstance().updateAutoSubmit(context, new Callback<UpdateAutoSubmitOutput>() {
            @Override
            public void success(UpdateAutoSubmitOutput updateAutoSubmitOutput, Response response) {
                System.out.println("response--update-->" + updateAutoSubmitOutput.getMessage());
                System.out.println("input---->" + status + userId);
                String msg = updateAutoSubmitOutput.getMessage();

            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("update failed");
            }
        });
    }


    // HashMap<Integer, ArrayList<CastingDetailsModel>> hashMap = new
    // HashMap<Integer, ArrayList<CastingDetailsModel>>();

    @SuppressLint("InlinedApi")
    private void findElements() {

        castingsList = (ListView) findViewById(R.id.rel_info_list);

        imgGrid = (TextView) findViewById(R.id.imgGrid);
        local_talent = (TextView) findViewById(R.id.local_talent);
        txtName = (TextView) findViewById(R.id.txtName);

        grid_view_photos = (GridView) findViewById(R.id.grid_view_photos);

        rel_info_ListView = (RelativeLayout) findViewById(R.id.rel_info_ListView);

        // grid_view_photos.setOnItemClickListener(new OnItemClickListener() {
        //
        // @Override
        // public void onItemClick(AdapterView<?> parent, View view, int
        // position, long id) {
        // // TODO Auto-generated method stub
        //
        // String getName =
        // myListImage.get(position).userName.toString().trim();
        // txtName.setVisibility(View.VISIBLE);
        // if (getName.equals("")) {
        // txtName.setVisibility(View.GONE);
        // } else {
        // txtName.setText(getName);
        // }
        //
        // imgGrid.setVisibility(View.VISIBLE);
        // local_talent.setVisibility(View.VISIBLE);
        // rel_cast_view.setVisibility(View.VISIBLE);
        // grid_view_photos.setVisibility(View.GONE);
        // gridViewStatus = false;
        // casting_image.setVisibility(View.VISIBLE);
        // rel_upload_screen.setVisibility(View.VISIBLE);
        // Picasso.with(context).load(myListImage.get(position).imageRole).into(casting_image);
        //
        // }
        // });
        menuListView = (SwipeMenuListView) findViewById(R.id.swipeListView);

        final SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(context);
                item1.setBackground(new ColorDrawable(Color.parseColor("#FF9933")));
                item1.setWidth(150);
                item1.setTitle("Delete");
                item1.setTitleSize(18);
                item1.setTitleColor(Color.WHITE);
                menu.addMenuItem(item1);
            }
        };


        menuListView.setMenuCreator(creator);
        menuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //    ArrayList<CastingDetailsModel> castingsListArr = swipeViewListAdapter.getItem(position);

                switch (index) {
                    case 0:
                        try {
                            if (Library.getId(context) != null && !Library.getId(context).equals("")) {

                                final CastingDetailsModel model = castingsListArr.get(position);
                                if (model != null) {

                                    removeCastings(model.casting_id + "", position);

                                } else {
                                    Library.alert(context, "Please login.");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }

                return false;
            }
        });

        // Right
        menuListView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);

        // Left
        menuListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);


        menuListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                // swipe start
                System.out.println("setOnSwipeListener onSwipeStart");
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
                System.out.println("setOnSwipeListener onSwipeEnd");

            }
        });

        swipeList = new ArrayList<CastingDetailsModel>();
        swipeList.addAll(castingsListArr);
        swipeViewListAdapter = new SwipeViewListAdapter(context, swipeList);
        menuListView.setAdapter(swipeViewListAdapter);


        imgGrid.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                // castingsListIcon.setVisibility(View.GONE);

                // grid_view_photos.removeAllViews();

                swipeRefreshLayout.setVisibility(View.VISIBLE);
                imgFav.setVisibility(View.GONE);

                rel_image_screen.setVisibility(View.VISIBLE);
                imgGrid.setVisibility(View.GONE);
                local_talent.setVisibility(View.GONE);
                // rel_cast_view.setVisibility(View.GONE);
                casting_image.setVisibility(View.GONE);
                txtName.setVisibility(View.GONE);

                grid_view_photos.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                gridViewStatus = true;
                imageGrid();

                int oldPage = page;
                page = 0;

                // Sugumaran Changes (25th May 16)
                casting_image_only.setVisibility(View.GONE);

            }
        });
        progressView = (ProgressBar) findViewById(R.id.progress_view);
        castingsListIcon = (ImageView) findViewById(R.id.castings_list_icon);
        castingsListIcon.setOnClickListener(this);
        // castingsListIconChange = (ImageView)
        // findViewById(R.id.castings_list_icon_change);
        // castingsListIconChange.setOnClickListener(this);

        lay_submission_detail_no_image = (LinearLayout) findViewById(R.id.lay_submission_detail_no_image);

        rel_home_screen = (RelativeLayout) findViewById(R.id.rel_home_screen);
        rel_image_screen = (RelativeLayout) findViewById(R.id.rel_image_screen);

        help_overlay = (ImageView) findViewById(R.id.help_overlay);
        help_overlay.setOnTouchListener(this);

        gear_icon = (ImageView) findViewById(R.id.gear_icon);
        editTextLocation = (EditText) findViewById(R.id.current_location);
        editTextLocation.setOnClickListener(this);
        locationListView = (ListView) findViewById(R.id.lView);

        outbox_icon = (ImageView) findViewById(R.id.outbox_icon);
        outbox_icon.setVisibility(View.VISIBLE);
        gear_icon_mirror = (ImageView) findViewById(R.id.gear_icon_mirror);
        home_screen = (RelativeLayout) findViewById(R.id.top_bar);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        notification_toggle = (ToggleButton) findViewById(R.id.notification_toggle);
        auto_submit_toggle = (ToggleButton) findViewById(R.id.auto_submit_toggle);

        if (notification == true) {
            toggleButton.setChecked(true);
        } else {
            toggleButton.setChecked(false);
        }
        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                JSONObject props = new JSONObject();
                if (arg1 == true) {
                    userNotificationFlag = "Yes";
                    notification = true;
                    sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                    editor = sharedpreferences.edit();
                    editor.putBoolean(Library.NOTIFICATION, notification);
                    editor.commit();
                    toggleButton.setChecked(true);

                    try {
                        props.put("User ID", androidUserID);
                        props.put("Notification status", "On");
                        mixpanel.track("Notification", props);
                    } catch (JSONException e) {

                    }
                    postSetNotifSync = new PostSetNotifSync();
                    postSetNotifSync.execute();
                } else {
                    userNotificationFlag = "No";
                    toggleButton.setChecked(false);
                    notification = false;
                    sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                    editor = sharedpreferences.edit();
                    editor.putBoolean(Library.NOTIFICATION, notification);
                    editor.commit();

                    try {
                        props.put("User ID", androidUserID);
                        props.put("Notification status", "Off");
                        mixpanel.track("Notification", props);
                    } catch (JSONException e) {
                        // TODO: handle exception
                    }

                    postSetNotifSync = new PostSetNotifSync();
                    postSetNotifSync.execute();
                }

            }
        });

       /* notification_toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               *//* if (userId != null) {
                    startActivity(new Intent(CastingScreen.this, Notification_Settings.class));
                }*//*
                if (!Library.getId(context).equals("")) {
                      startActivity(new Intent(CastingScreen.this, Notification_Settings.class));
                  *//*  Intent i = new Intent(CastingScreen.this, Notification_Settings.class);
                    startActivityForResult(i, 90);*//*
                } else {
                    Library.alert(context, "To access notification please login");
                }
            }
        });
        */

        notification_toggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!notification_toggle.isChecked()) {
                    UpdateNotificationInput per = new UpdateNotificationInput(Library.getUserId(context), "", "", 0 + "");
                    //     per = new UpdateNotificationInput(Library.getUserId(context) + "", "", "", 0);
                    pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    pDialog.setMessage("Loading...");
                    pDialog.setCancelable(false);
                    pDialog.show();


                    RegisterRemoteApi.getInstance().setUpdateNotificationInput(per);
                    RegisterRemoteApi.getInstance().updateNotification(context, new Callback<UpdateNotificationOutput>() {
                        @Override
                        public void success(UpdateNotificationOutput personNotificationOutput, Response response) {
                            closeProgress();
                            String message = personNotificationOutput.message;
                            String status = personNotificationOutput.status;
                            int statusValue = Integer.parseInt(status);
                            if (statusValue == 200) {
                                alert("Updated successfully");
                            } else {
                                Library.alert(context, message);
                            }


                        }

                        @Override
                        public void failure(RetrofitError error) {
                            closeProgress();
                            Library.alert(context, "The requested service is not available. Please try again after sometime");
                            //   Toast.makeText(c, "The requested service is not available. Please try again after sometime", Toast.LENGTH_LONG).show();
                            //   finish();
                        }
                    });
                } else {
                    if (!Library.getId(context).equals("")) {
                        //       startActivity(new Intent(CastingScreen.this, Notification_Settings.class));
                        Intent i = new Intent(CastingScreen.this, Notification_Settings.class);
                        startActivityForResult(i, 90);
                    } else {
                        notification_toggle.setChecked(false);
                        Library.alert(context, "To access notification please login");
                    }
                }


            }
        });

       /* auto_submit_toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


            }
        });*/


        auto_submit_toggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Library.getId(context).equals("")) {
                    if (auto_submit_toggle.isChecked()) {
                        status = String.valueOf(1);
                        updateAutoSubmit();
                    } else {
                        status = String.valueOf(0);
                        updateAutoSubmit();
                    }
                } else {
                    auto_submit_toggle.setChecked(false);
                    Library.alert(context, "To access auto submit please login");
                }
            }
        });

        rel_cast_view = (RelativeLayout) findViewById(R.id.rel_cast_view);

        tap_current_location = (RelativeLayout) findViewById(R.id.tap_current_location);
        tap_current_location.setOnClickListener(this);
        if (tap_current_location.getVisibility() == View.VISIBLE)
            tap_current_location.setVisibility(View.GONE);
        frlay = (FrameLayout) findViewById(R.id.frlay);

        rel_cast = (RelativeLayout) findViewById(R.id.rel_cast);

        castivityScreen = (LinearLayout) findViewById(R.id.castivity_screen);
        // by nijam
        tabLayout = (LinearLayout) findViewById(R.id.linearID);

        casting_image = (ImageView) findViewById(R.id.casting_image);

        // Sugumaran Changes (25th May 16)
        casting_image_only = (ImageView) findViewById(R.id.casting_image_only);

        select_icon = (TextView) findViewById(R.id.select_icon);
        clear = (ImageView) findViewById(R.id.clear);
        clear.setOnClickListener(this);

        birthClear = (ImageView) findViewById(R.id.birth_clear);
        birthClear.setOnClickListener(this);
        castingViewNoImage = (RelativeLayout) findViewById(R.id.casting_view_no_image);

        home_screen.setVisibility(View.VISIBLE);
        rel_upload_screen = (RelativeLayout) findViewById(R.id.rel_upload_screen);

        rel_upload_screen.setOnClickListener(this);

        rel_cast_outer = (RelativeLayout) findViewById(R.id.rel_cast_outer);
        chkActor = (CheckBox) findViewById(R.id.checkbox_actor);
        chkModel = (CheckBox) findViewById(R.id.txt_model);
        chkSinger = (CheckBox) findViewById(R.id.txt_singer);
        chkDancer = (CheckBox) findViewById(R.id.txt_dancer);

        chkMale = (CheckBox) findViewById(R.id.txt_male);
        chkFemale = (CheckBox) findViewById(R.id.txt_female);

        chkUnion = (CheckBox) findViewById(R.id.chk_union);
        chkNonUnion = (CheckBox) findViewById(R.id.chk_non_union);

        birthYear = (TextView) findViewById(R.id.birth_year);


        txtEthnicity = (TextView) findViewById(R.id.txt_ethnicity);

        txtEthnicity.setOnClickListener(this);
        txtReset = (TextView) findViewById(R.id.text_reset);
        text_help_over_lay = (TextView) findViewById(R.id.text_help_over_lay);
        text_help_over_lay.setOnClickListener(this);
        txtSubmitCasting = (TextView) findViewById(R.id.txt_submit_casting);
        txtSubmitCasting.setOnClickListener(this);

//		By Nivetha
       /* txt_signin = (TextView) findViewById(R.id.txt_signin);
        txt_signin.setOnClickListener(this);*/


        /*vasanth you add this*/
        //  txtLogin = (TextView) findViewById(R.id.txt_signin);
        txtLogin.setOnClickListener(this);
        txtSignup.setOnClickListener(this);


        textCastivate = (TextView) findViewById(R.id.text_castivate);
        textlogout = (TextView) findViewById(R.id.text_logout);
        textlogout.setOnClickListener(this);


        // new design to view casting no image
        textCastingTitleNoImage = (TextView) findViewById(R.id.cast_title_no_image);
        textCastingTypeNoImage = (TextView) findViewById(R.id.cast_type_no_image);
        txtRoleDescriptionNoImage = (TextView) findViewById(R.id.role_desc_no_image);
        textPaidStatusNoImage = (TextView) findViewById(R.id.paid_status_no_image);
        textSubmissionDetailNoImage = (TextView) findViewById(R.id.submission_detail_no_image);
        textAgeRangeNoImage = (TextView) findViewById(R.id.age_range_no_image);
        textRoleForEthnicityNoImage = (TextView) findViewById(R.id.ethnicity_no_image);
        textRoleForGenderNoImage = (TextView) findViewById(R.id.gender_no_image);
        textUnionStatusNoImage = (TextView) findViewById(R.id.union_status_no_image);
        textSynopsisNoImage = (TextView) findViewById(R.id.submission_info_no_image);
        fav_iconNoImage = (CheckBox) findViewById(R.id.fav_icon_select_no_image);
        favCountText = (TextView) findViewById(R.id.fav_count);
        favCountText.setVisibility(View.INVISIBLE);
        textDistanceNoImage = (TextView) findViewById(R.id.distance_no_image);

    }

    private void ethnicityDialog() {

        final Dialog dialog = new Dialog(CastingScreen.this, android.R.style.Theme_DeviceDefault_Dialog);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ethnicity);
        dialog.setCancelable(false);

        ethnicityListView = (ListView) dialog.findViewById(R.id.ethnicity_list);

        ethnicityList = new ArrayList<EthnicityModel>();

        ImageView backBtn = (ImageView) dialog.findViewById(R.id.back_arrow);

        ImageView selectBtn = (ImageView) dialog.findViewById(R.id.select_icon);

        for (int i = 0; i < ethnicity.length; i++) {

            EthnicityModel ethnicityModel = new EthnicityModel(ethnicity[i], false);
            ethnicityList.add(ethnicityModel);
        }
        if (strEthnicity != null) {
            // split the string using separator, in this case it is ","
            String[] items = strEthnicity.split(", ");
            selchkboxlist = new ArrayList<String>();
            for (String item : items) {
                selchkboxlist.add(item);
            }

            DebugReportOnLocat.ln("Java String converted to ArrayList: " + selchkboxlist);
        }

        for (int i = 0; i < ethnicityList.size(); i++) {
            if (selchkboxlist != null)
                for (int j = 0; j < selchkboxlist.size(); j++) {

                    if (ethnicityList.get(i).name.equals(selchkboxlist.get(j))) {

                        ethnicityList.get(i).checked = true;
                    }
                }
        }
        ethnicityListView.setAdapter(new EthnicityFilterAdapter(CastingScreen.this, ethnicityList));

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ethnicityFilter();

                dialog.dismiss();
            }

        });
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ethnicityFilter();
                dialog.dismiss();
            }
        });

		/*textlogout.setOnClickListener(new OnClickListener() {
            @Override
			public void onClick(View v) {
				ethnicityFilter();
				dialog.dismiss();
			}
		});*/
        // Create and populate ethnicity.
        // private String[] ethnicity = {"Caucasian",
        // "African American", "Hispanic", "Asian", "Mixed",
        // "Native American","Middle Eastern", "Other"};

        dialog.show();
        // =====================

    }

    private void ethnicityFilter() {
        // TODO Auto-generated method stub
        int count = ethnicityListView.getAdapter().getCount();
        selchkboxlist = new ArrayList<String>();
        strEthnicity = "";
        for (int i = 0; i < count; i++) {
            RelativeLayout itemLayout = (RelativeLayout) ethnicityListView.getChildAt(i); // Find
            // by
            // under
            // LinearLayout
            CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.ethnicity_checkbox);

            if (checkbox.isChecked()) {
                Log.d("Item " + String.valueOf(i), checkbox.getTag().toString());
                selchkboxlist.add(checkbox.getTag().toString());
                // String str2 =
                // Arrays.asList(selchkboxlist).toString();

                strEthnicity = selchkboxlist.toString();

                DebugReportOnLocat.ln("items selected:::" + selchkboxlist);
                // Toast.makeText(Activities.this,checkbox.getTag().toString()
                // ,Toast.LENGTH_LONG).show();

            }

        }

        strEthnicity = strEthnicity.replace("[", "").replace("]", "");
        if (strEthnicity.equals("")) {
            txtEthnicity.setText("Ethnicity");
        } else {
            txtEthnicity.setText(strEthnicity);
        }

    }

    private void rateThisAppAlert() {
        // TODO Auto-generated method stub

        final Dialog dialog = new Dialog(context);
        // Set Dialog Title
        dialog.setTitle("Rate Castivate");
        dialog.setCancelable(false);
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(context);
        tv.setText("If you enjoy using Castivate App, would you mind taking a moment to rate it? It won't take more than a minute. Thanks for your support!");
        tv.setTextSize(18);
        tv.setGravity(Gravity.CENTER);
        // tv.setLayoutParams(new LayoutParams(500, 500));
        tv.setPadding(10, 10, 10, 10);

        ll.addView(tv);

        // First Button
        Button b1 = new Button(context);
        b1.setText("Rate this App");
        b1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    context.startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "Couldn't launch the Play Store.", Toast.LENGTH_LONG);
                }

                SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
                Editor editor = prefs.edit();
                editor.putBoolean(Library.RATEIT_FLAG, true);
                editor.commit();

                // sharedpreferences =
                // getSharedPreferences(Library.MyPREFERENCES,
                // Context.MODE_PRIVATE);
                // ratePopFlag = true;
                // editor.putBoolean(Library.RATEIT_FLAG, ratePopFlag);
                // editor.commit();
                dialog.dismiss();

            }
        });
        ll.addView(b1);

        // Second Button
        Button b2 = new Button(context);
        b2.setText("Remind me later");
        b2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();

                //
                // sharedpreferences =
                // getSharedPreferences(Library.MyPREFERENCES,
                // Context.MODE_PRIVATE);
                // if (sharedpreferences.contains(Library.RATEIT)) {
                // SharedPreferences.Editor editor = sharedpreferences.edit();
                // rateCount = -1;
                // editor.putInt(Library.RATEIT, rateCount);
                // editor.commit();
                // }

            }
        });
        ll.addView(b2);

        // Third Button
        Button b3 = new Button(context);
        b3.setText("No, Thanks");
        b3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
                Editor editor = prefs.edit();
                editor.putBoolean(Library.RATEIT_FLAG, true);
                editor.commit();

                // sharedpreferences =
                // getSharedPreferences(Library.MyPREFERENCES,
                // Context.MODE_PRIVATE);
                //
                // ratePopFlag = true;
                // editor.putBoolean(Library.RATEIT_FLAG, ratePopFlag);
                // editor.commit();
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        dialog.setContentView(ll);

        // Show Dialog
        dialog.show();

    }

    @SuppressWarnings("static-access")
    public void currentLocation() {

        // create class object
        gps = new GPSTracker(context);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            // dlatitude = gps.getLatitude();
            // dlongitude = gps.getLongitude();

            dclatitude = gps.getLatitude();
            dclongitude = gps.getLongitude();

            // \n is for new line
            // Toast.makeText(getApplicationContext(),
            // "Your Location is - \nLat: " + latitude + "\nLong: " +
            // longitude, Toast.LENGTH_LONG).show();
            try {
                geocoder = new Geocoder(context, Locale.ENGLISH);
                addresses = geocoder.getFromLocation(dclatitude, dclongitude, 1);
                StringBuilder str = new StringBuilder();
                if (geocoder.isPresent()) {
                    // Toast.makeText(getApplicationContext(),
                    // "geocoder present", Toast.LENGTH_SHORT).show();

                    if (strSelectedLocation.equals("")) {
                        dlatitude = dclatitude;
                        dlongitude = dclongitude;

                        // curlatitude=dlatitude;
                        // curlongitude=dlongitude;
                        try {
                            if (addresses != null)
                                returnAddress = addresses.get(0);
                            String localityString = returnAddress.getLocality();
                            String country = returnAddress.getCountryName();

                            String region_code = returnAddress.getCountryCode();
                            // String zipcode =
                            // returnAddress.getPostalCode();

                            str.append(localityString + " ");
                            str.append(country + ", " + region_code + "");
                            // str.append(zipcode + "");

                            // if (strSelectedLocation.equals("")) {
                            if (str != null) {
                                editTextLocation.setText(str);
                                strSelectedLocation = str.toString();
                                strCurrentLocation = str.toString();

                            }

                            sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                            editor = sharedpreferences.edit();

                            editor.putString(Library.CURRENT_LOCATION, strCurrentLocation);
                            editor.commit();
                        } catch (IndexOutOfBoundsException e) {

                            e.printStackTrace();
                        }

                    } else {

                        editTextLocation.setText(strSelectedLocation);

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

    private void clickMenuEvents() {
        gear_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rel_upload_screen.setEnabled(false);
                if (tap_current_location.getVisibility() == View.VISIBLE) {
                    tap_current_location.setVisibility(View.GONE);
                }
                if (birthYear.getText().toString().equals("")) {
                    birthClear.setVisibility(View.GONE);
                }

                if (castivityScreen.getVisibility() == View.VISIBLE) {
                    castivityScreen.setVisibility(View.GONE);
                    outbox_icon.setVisibility(View.VISIBLE);
                    gear_icon_mirror.setVisibility(View.GONE);
                    gear_icon.setVisibility(View.VISIBLE);
                    castingsListIcon.setVisibility(View.VISIBLE);
                    // by nijam
                    outbox_icon.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.GONE);

                    // ///

                    // if (listViewStatus)
                    // castingsListIconChange.setVisibility(View.VISIBLE);
                    // else

                } else {
                    // castingsListIconChange.setVisibility(View.INVISIBLE);
                    castingsListIcon.setVisibility(View.INVISIBLE);
                    // by nijam
                    outbox_icon.setVisibility(View.INVISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    gear_icon.setVisibility(View.INVISIBLE);
                    castivityScreen.setVisibility(View.VISIBLE);
                    outbox_icon.setVisibility(View.INVISIBLE);
                    gear_icon_mirror.setVisibility(View.VISIBLE);

                    if (Library.getId(context).length() > 0) {
                        //txtLogin.setText("");//dsgrhseryhreyhswreyhsrewhj
                        txtLogin.setText("My Profile");//dsgrhseryhreyhswreyhsrewhj
//                        textlogout.setVisibility(View.VISIBLE);

                        /*vasanth you add this*/
                        txtLogin.setVisibility(View.GONE);
                        txtSignup.setVisibility(View.GONE);
                        userProfileImage.setVisibility(View.VISIBLE);
                        profileName.setVisibility(View.VISIBLE);

                        SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                        String username = sharedpreferences.getString("username", "");
                        String profileImage = sharedpreferences.getString("profileImage", "");
                        profileName.setText(username);

                        profileName.setText(Html.fromHtml("<u>" + username + "</u>"));
                        if (!profileImage.equals(""))
                            Picasso.with(CastingScreen.this).load(profileImage).placeholder(R.drawable.avathar_profile)
                                    .error(R.drawable.avathar_profile).into(userProfileImage);

                    } else {
                        txtLogin.setText("Login");
                        textlogout.setVisibility(View.GONE);
                        userProfileImage.setVisibility(View.GONE);
                        profileName.setVisibility(View.GONE);

                        /*vasanth you add this*/
                        txtLogin.setVisibility(View.VISIBLE);
                        txtSignup.setVisibility(View.VISIBLE);
                    }
                    castivityScreen.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left));
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

                }
            }

        });
        gear_icon_mirror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();

                if (castivityScreen.getVisibility() == View.VISIBLE) {
                    // if (listViewStatus)
                    // castingsListIconChange.setVisibility(View.VISIBLE);
                    // else
                    // castingsListIcon.setVisibility(View.VISIBLE);

                    rel_upload_screen.setEnabled(true);

                    castingsListIcon.setVisibility(View.VISIBLE);

                    // by nijam
                    tabLayout.setVisibility(View.GONE);

                    castivityScreen.setVisibility(View.GONE);
                    outbox_icon.setVisibility(View.VISIBLE);
                    gear_icon_mirror.setVisibility(View.GONE);
                    tap_current_location.setVisibility(View.GONE);
                    gear_icon.setVisibility(View.VISIBLE);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                }
            }

        });

        outbox_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view = findViewById(R.id.rel_home_screen);// your layout id
                view.getRootView();
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    File picDir = new File(Environment.getExternalStorageDirectory() + "/Castivate");
                    if (!picDir.exists()) {
                        picDir.mkdir();
                    }
                    view.setDrawingCacheEnabled(true);
                    view.buildDrawingCache(true);
                    Bitmap bitmap = view.getDrawingCache();
                    // Date date = new Date();
                    String fileName = "castivate" + ".jpeg";
                    picFile = new File(picDir + "/" + fileName);
                    try {
                        picFile.createNewFile();
                        FileOutputStream picOut = new FileOutputStream(picFile);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (int) (bitmap.getHeight()));
                        boolean saved = bitmap.compress(CompressFormat.JPEG, 100, picOut);
                        if (saved) {
                            // Toast.makeText(getApplicationContext(),
                            // "Image saved to your device Pictures "+
                            // "directory!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Error
                        }
                        picOut.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    view.destroyDrawingCache();
                } else {
                    // Error

                }

                Intent chooser = new Intent(Intent.ACTION_SEND);

                chooser.setType("image/*");
                chooser.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(picFile));
                if (rel_image_screen.getVisibility() == View.VISIBLE) {
                    chooser.putExtra(Intent.EXTRA_TEXT, "Hey, Check out this awesome talent on Castivate! Click the URL below:  http://www.castivate.com/get");
                    chooser.putExtra(Intent.EXTRA_SUBJECT, "A local talent from Castivate!");

                } else {
                    chooser.putExtra(Intent.EXTRA_TEXT, "Hey, Check out this awesome casting on Castivate! Click the URL below:  http://www.castivate.com/get");
                    chooser.putExtra(Intent.EXTRA_SUBJECT, "A casting from Castivate!");

                }

                chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(chooser);
                JSONObject props = new JSONObject();

                try {
                    props.put("User ID", androidUserID);
                    props.put("Casting Title", myList.get(position).castingTitle.toString().trim());
                    props.put("Casting ID", myList.get(position).roleId.toString().trim());
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                mixpanel.track("Share Social Network", props);

            }
        });

    }

    @SuppressLint("InflateParams")
    public void alert(ArrayList<String> year_values, final TextView text) {
        // TODO Auto-generated method stub
        final Dialog alertDialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
        // alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setTitle("Birth Year");

        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.alert_spinner, null);
        alertDialog.setContentView(convertView);
        final ListView lv = (ListView) convertView.findViewById(R.id.list_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, year_values);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedFromList = (String) (lv.getItemAtPosition(position));
                text.setText(selectedFromList);
                text.setTextColor(Color.BLACK);
                strBithYear = selectedFromList;
                if ((birthClear.getVisibility() == View.GONE) || (birthClear.getVisibility() == View.INVISIBLE)) {
                    birthClear.setVisibility(View.VISIBLE);
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onPause() {

        super.onPause();

        DebugReportOnLocat.ln("onpause:");

    }

    @Override
    protected void onStart() {

        super.onStart();
        // KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
        hideSoftKeyboard();
        // FlurryAgent.onStartSession(this, "7XR67SX3652ZF6VCSDPS");
        DebugReportOnLocat.ln("onstart:");
    }

    @Override
    protected void onStop() {

        super.onStop();
        // FlurryAgent.onEndSession(this);
        DebugReportOnLocat.ln("onstop:");
    }

    @Override
    protected void onResume() {

        try {
            if (isMoveLike) {
                if (imgFav.getVisibility() == View.VISIBLE) {
                    imgFav.setChecked(true);

                    if (imageCount) {
                        int value = Integer.parseInt(myListImage.get(sequenceOrder).commentCount) + 1;
                        if (value == 1) {
                            txtCommentCount.setText("" + value);
                        } else if (value == 0) {
                            txtCommentCount.setText("");
                        } else {
                            txtCommentCount.setText("" + value);
                        }


                    } else {
                        int value = Integer.parseInt(myListImage.get(sequenceOrder).commentCount);
                        if (value == 0) {
                            txtCommentCount.setText("");
                        } else {
                            txtCommentCount.setText("" + value);
                        }

                    }


                    //  myListImage.get(sequenceOrder).commentCount = String.valueOf(value);


                    isMoveLike = false;
                }
            } else {
                if (imgFav.getVisibility() == View.VISIBLE) {
                    imgFav.setChecked(myListImage.get(sequenceOrder).favImg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (progressBar != null && progressBar.isShowing()) {
            progressBar.dismiss();
        }
       /* finish();
        startActivity(getIntent());*/
        DebugReportOnLocat.ln("onresume:");
        super.onResume();
        // KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
        hideSoftKeyboard();
        ((CastingsLinkMovementMethod) CastingsLinkMovementMethod.getInstance()).setContext(context, false);

        try {
            if (!Library.getId(context).equals("")) {
                txtLogin.setText("My Profile");
//                textlogout.setVisibility(View.VISIBLE);

            } else {
                txtLogin.setText("Login");
                //txtLogin.setText("Sign In");
                textlogout.setVisibility(View.GONE);

            }

            if (Library.isApplied) {
                layBottom.setVisibility(View.VISIBLE);
                btn_apply.setText("Submitted");
                imgTick.setVisibility(View.VISIBLE);
                layBottom.setBackgroundColor(getResources().getColor(R.color.green));

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        //by nijam
        textCastivate.setVisibility(View.GONE);
        castingViewNoImage.setVisibility(View.GONE);
        rel_image_screen.setVisibility(View.GONE);
        rel_upload_screen.setVisibility(View.GONE);
        castingsList.setVisibility(View.VISIBLE);


        castingsListIcon.setVisibility(View.GONE);
        outbox_icon.setVisibility(View.INVISIBLE);
        // by nijam
        tabLayout.setVisibility(View.VISIBLE);
        rel_info_ListView.setVisibility(View.VISIBLE);
        listViewStatus = true;

        grid_view_photos.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.GONE);

        castListAdapter = new CastingsListAdapter(CastingScreen.this, castingsListArr);
        refreshYourAdapter(castingsListArr);
        castingsList.setAdapter(castListAdapter);
        castingsList.setSelection(0);
        castListAdapter.notifyDataSetChanged();


        // results = new ArrayList<CastingListDetailsModel>();
        // _itemListAdapter = new ItemListBaseAdapter(this, image_details);
        // refreshYourAdapter(GetSearchResults());
        // lv1.setAdapter(_itemListAdapter);

        // refreshYourAdapter(castingsListArr);
        // castingsList.setAdapter(castListAdapter);
        // castListAdapter.notifyDataSetChanged();
        //     handlerCastingSync.sendEmptyMessage(0);

    }

    private void refreshYourAdapter(final ArrayList<CastingDetailsModel> items) {
        // this is what I meant. The error clearly states you are not updating
        // the adapter on the UI thread
        runOnUiThread(new Runnable() {
            public void run() {
                if (items != null && items.size() != 0) {
                    castListAdapter.refreshAdapter(items);
                }
            }
        });
    }

    private void refreshYour(final ArrayList<CastingDetailsModel> items) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (items != null && items.size() != 0) {
                    //     swipeViewListAdapter = new SwipeViewListAdapter(context, swipeList);
                    swipeViewListAdapter.refreshAdapter(items);
                }
            }
        });
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

    public void postJSONData() {

        System.out.println("set token service --------------------->");
        try {
            HttpPost request = new HttpPost(HttpUri.SETTOKEN);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/x-www-form-urlencoded");

            JSONStringer item = null;

            try {
                // {"userTokenId":"3b10484355df66599afc32d6652383ed4f4b8ba6429f7821480b505ebe1299djjjjj4111","userDeviceId":"aaabbb"}
                item = new JSONStringer().object().key("userTokenId").value(userTokenId).key("userDeviceId").value(regId).endObject();
                Log.d("Data", item.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StringEntity entity = null;

            try {
                entity = new StringEntity(item.toString());
                request.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Send request to WCF service
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = null;

            try {
                response = httpClient.execute(request);
                if (response.getStatusLine().getStatusCode() == 200) {

                    HttpEntity responseEntity = response.getEntity();

                    try {
                        is = responseEntity.getContent();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        BufferedReader reader2 = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                        sb = new StringBuilder();
                        String line = null;
                        while ((line = reader2.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        is.close();
                        json = sb.toString();

                        System.out.println("JSON Result ----------------> " + json);

                        try {

                            JSONObject oneObject = new JSONObject(json);

                            System.out.println("User ID ------------------------------> " + oneObject.getString("userId"));

                            System.out.println("Remaining Days ------------------------------> " + oneObject.getString("remainingdays"));

                            Library.remainingDays = oneObject.getString("remainingdays");
                            remainingDays = Library.remainingDays;

                            sharedpreferences = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                            editor = sharedpreferences.edit();
                            editor.putString("userid", oneObject.getString("userId"));

                            editor.commit();
                        } catch (JSONException e) {
                            System.out.println("Exception : " + e.getMessage());
                            // TODO: handle exception
                        }
                        JSONObject props = new JSONObject();

                        try {
                            props.put("User ID", androidUserID);
                            props.put("Gender", strGender);
                            props.put("Birth Year", strBithYear);
                            props.put("Ethnicity", strEthnicity);
                        } catch (JSONException e) {
                            // TODO: handle exception
                        }

                        mixpanel.track("New Downloads", props);

                        // mixpanel.registerSuperProperties(oneObject);
                        DebugReportOnLocat.ln("json response for submit::" + Library.getUserId(context));
                        sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                        editor = sharedpreferences.edit();
                        editor.putString(Library.CUSTOMER_ID, Library.getUserId(context));
                        editor.putString(Library.REMAINING_DAYS, Library.remainingDays);
                        editor.commit();
                        // Toast.makeText(context,
                        // "Your device registered to the server. user id: " +
                        // json,
                        // Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("Buffer Error", "Error converting result " + e.toString());
                    }

                } else {
                    Status = null;
                }

            } catch (ClientProtocolException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public static boolean isMoveLike = false;

    public void postJSONNotifData() {
        try {
            HttpPost request = new HttpPost(HttpUri.SETNOTIFICATION);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/x-www-form-urlencoded");

            JSONStringer item = null;

            try {
                // {"userTokenId":"3b10484355df66599afc32d6652383ed4f4b8ba6429f7821480b505ebe1299djjjjj4111","userDeviceId":"aaabbb"}
                item = new JSONStringer().object().key("userId").value(androidUserID).key("userNotificationFlag").value(userNotificationFlag).endObject();
                Log.d("Data", item.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StringEntity entity = null;

            try {
                entity = new StringEntity(item.toString());
                request.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Send request to WCF service
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = null;

            try {
                response = httpClient.execute(request);
                if (response.getStatusLine().getStatusCode() == 200) {

                    HttpEntity responseEntity = response.getEntity();

                    try {
                        is = responseEntity.getContent();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        BufferedReader reader2 = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                        sb = new StringBuilder();
                        String line = null;
                        while ((line = reader2.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        is.close();
                        json = sb.toString();
                        if (json.contains("1")) {
                            // Library.showToast(context, "success");
                            DebugReportOnLocat.ln("json response for notif::" + json);
                        }
                        // jArr = new JSONArray(json);

                        // JSONObject oneObject = jArr.getJSONObject(0); //
                        // Pulling
                        // items
                        // from
                        // the
                        // array

                        // Toast.makeText(context,
                        // "Your device registered to the server. user id: " +
                        // json,
                        // Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("Buffer Error", "Error converting result " + e.toString());
                    }

                } else {
                    Status = null;
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void clickEvents() {

        fav_iconNoImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (((CheckBox) v).isChecked()) {
                    fav_iconNoImage.setEnabled(false);
                    favriteFlag = "1";

                    // if (casting_image.getVisibility() == View.VISIBLE) {
                    // handlerImgFavoriteSync.sendEmptyMessage(0);
                    // } else {
                    handlerFavoriteSync.sendEmptyMessage(0);
                    // }

                } else {
                    fav_iconNoImage.setEnabled(false);
                    favriteFlag = "0";

                    // if (casting_image.getVisibility() == View.VISIBLE) {
                    // handlerImgFavoriteSync.sendEmptyMessage(0);
                    // } else {
                    handlerFavoriteSync.sendEmptyMessage(0);
                    // }
                    // handlerFavoriteSync.sendEmptyMessage(0);
                }
            }
        });

        imgFav.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View i) {
                // TODO Auto-generated method stub

                if (((CheckBox) i).isChecked()) {
                    favriteFlag = "1";
//                    handlerImgFavoriteSync.sendEmptyMessage(0);
                    System.out.println("Library.getUserId(context) = " + Library.getUserId(context));
                    marshmallowPermission = new MarshmallowPermission(CastingScreen.this);

                    if (marshmallowPermission.checkPermissionForExternalStorage()) {
                        view = findViewById(R.id.rel_home_screen);// your layout id
                        view.getRootView();
                        String state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            File picDir = new File(Environment.getExternalStorageDirectory() + "/Castivate");
                            if (!picDir.exists()) {
                                picDir.mkdir();
                            }
                            view.setDrawingCacheEnabled(true);
                            view.buildDrawingCache(true);
                            Bitmap bitmap = view.getDrawingCache();
                            // Date date = new Date();
                            String fileName = "castivate" + ".jpeg";
                            picFile = new File(picDir + "/" + fileName);
                            photofilePath = picFile.getAbsolutePath();
                            try {
                                picFile.createNewFile();
                                FileOutputStream picOut = new FileOutputStream(picFile);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (int) (bitmap.getHeight()));
                                boolean saved = bitmap.compress(CompressFormat.JPEG, 100, picOut);
                                if (saved) {
                                    // Toast.makeText(getApplicationContext(),
                                    // "Image saved to your device Pictures "+
                                    // "directory!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Error
                                }
                                picOut.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            view.destroyDrawingCache();
                        }
                        Intent in;


                        if (imageCount) {
                            in = new Intent(CastingScreen.this, CastivateThankScreenSigned.class);
                            in.putExtra("talentid", myListImage.get(sequenceOrder).imgId);
                            in.putExtra("photo", photofilePath);
                        } else {
                            in = new Intent(CastingScreen.this, CastivateThankScreen.class);
                            in.putExtra("talentid", myListImage.get(sequenceOrder).imgId);
                            in.putExtra("photo", photofilePath);
                        }
                        startActivity(in);
                    } else {
                        marshmallowPermission.requestPermissionForExternalStorage();
                    }

                  /*  in = new Intent(CastingScreen.this, CastivateThankScreen.class);
                    in.putExtra("talentid", myListImage.get(sequenceOrder).imgId);
                    in.putExtra("photo", photofilePath);
                    startActivity(in);*/


                } else {
                    if (imageCount)
                        submitComment(myListImage.get(sequenceOrder).imgId, "", "", "");

                }
            }
        });
                /*else {
                    imgFav.setEnabled(false);
                    favriteFlag = "0";
                    handlerImgFavoriteSync.sendEmptyMessage(0);
                    System.out.println("Library.getUserId(context) = " + Library.getUserId(context));

                    view = findViewById(R.id.rel_home_screen);// your layout id
                    view.getRootView();
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        File picDir = new File(Environment.getExternalStorageDirectory() + "/Castivate");
                        if (!picDir.exists()) {
                            picDir.mkdir();
                        }
                        view.setDrawingCacheEnabled(true);
                        view.buildDrawingCache(true);
                        Bitmap bitmap = view.getDrawingCache();
                        // Date date = new Date();
                        String fileName = "castivate" + ".jpeg";
                        picFile = new File(picDir + "/" + fileName);
                        photofilePath = picFile.getAbsolutePath();
                        try {
                            picFile.createNewFile();
                            FileOutputStream picOut = new FileOutputStream(picFile);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (int) (bitmap.getHeight()));
                            boolean saved = bitmap.compress(CompressFormat.JPEG, 100, picOut);
                            if (saved) {
                                // Toast.makeText(getApplicationContext(),
                                // "Image saved to your device Pictures "+
                                // "directory!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Error
                            }
                            picOut.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        view.destroyDrawingCache();
                    } else {
                        // Error

                    }

                    Intent in;
                 */
                /*   if (Library.getUserId(context).equals("")) {
                        in = new Intent(CastingScreen.this, CastivateThankScreen.class);
                        in.putExtra("talentid",myListImage.get(sequenceOrder).imgId);
                    } else {*//*
                    in = new Intent(CastingScreen.this, CastivateThankScreen.class);
                    in.putExtra("talentid", myListImage.get(sequenceOrder).imgId);
                    in.putExtra("photo", photofilePath);
                    startActivity(in);
                }*/


       /* imgFav.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imgFav.setEnabled(false);
                    favriteFlag = "1";
//                    handlerImgFavoriteSync.sendEmptyMessage(0);
                    System.out.println("Library.getUserId(context) = " + Library.getUserId(context));

                    Intent in;
                    in = new Intent(CastingScreen.this, CastivateThankScreen.class);
                    in.putExtra("talentid", myListImage.get(sequenceOrder).imgId);
                    startActivity(in);

                    *//*view = findViewById(R.id.rel_home_screen);// your layout id
                    view.getRootView();
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        File picDir = new File(Environment.getExternalStorageDirectory() + "/Castivate");
                        if (!picDir.exists()) {
                            picDir.mkdir();
                        }
                        view.setDrawingCacheEnabled(true);
                        view.buildDrawingCache(true);
                        Bitmap bitmap = view.getDrawingCache();
                        // Date date = new Date();
                        String fileName = "castivate" + ".jpeg";
                        picFile = new File(picDir + "/" + fileName);
                        try {
                            picFile.createNewFile();
                            FileOutputStream picOut = new FileOutputStream(picFile);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (int) (bitmap.getHeight()));
                            boolean saved = bitmap.compress(CompressFormat.JPEG, 100, picOut);
                            if (saved) {
                                // Toast.makeText(getApplicationContext(),
                                // "Image saved to your device Pictures "+
                                // "directory!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Error
                            }
                            picOut.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        view.destroyDrawingCache();
                    } else {
                        // Error

                    }*//*


                } else {
                    imgFav.setEnabled(false);
                    favriteFlag = "0";
                    handlerImgFavoriteSync.sendEmptyMessage(0);
                    System.out.println("Library.getUserId(context) = " + Library.getUserId(context));

                    view = findViewById(R.id.rel_home_screen);// your layout id
                    view.getRootView();
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        File picDir = new File(Environment.getExternalStorageDirectory() + "/Castivate");
                        if (!picDir.exists()) {
                            picDir.mkdir();
                        }
                        view.setDrawingCacheEnabled(true);
                        view.buildDrawingCache(true);
                        Bitmap bitmap = view.getDrawingCache();
                        // Date date = new Date();
                        String fileName = "castivate" + ".jpeg";
                        picFile = new File(picDir + "/" + fileName);
                        try {
                            picFile.createNewFile();
                            FileOutputStream picOut = new FileOutputStream(picFile);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (int) (bitmap.getHeight()));
                            boolean saved = bitmap.compress(CompressFormat.JPEG, 100, picOut);
                            if (saved) {
                                // Toast.makeText(getApplicationContext(),
                                // "Image saved to your device Pictures "+
                                // "directory!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Error
                            }
                            picOut.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        view.destroyDrawingCache();
                    } else {
                        // Error

                    }

                    Intent in;
                 *//*   if (Library.getUserId(context).equals("")) {
                        in = new Intent(CastingScreen.this, CastivateThankScreen.class);
                        in.putExtra("talentid",myListImage.get(sequenceOrder).imgId);
                    } else {*//*
                    in = new Intent(CastingScreen.this, CastivateThankScreen.class);
                    in.putExtra("talentid", myListImage.get(sequenceOrder).imgId);
                    //     in.putExtra("photo",picDir + \"/\" + fileName);
                    //    }
                    startActivity(in);
                }
            }

        });
*/


        chkActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    chkActor.setTextColor(context.getResources().getColor(R.color.white));

                    if (strPerformanceType != null)
                        strPerformanceType = strPerformanceType + "," + "actor";
                    else
                        strPerformanceType = "actor";
                } else {
                    if (strPerformanceType.contains("actor")) {
                        strPerformanceType = strPerformanceType.replace("actor", "");
                    }

                    chkActor.setTextColor(context.getResources().getColor(R.color.dark_black));
                }
            }
        });
        chkModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    chkModel.setTextColor(context.getResources().getColor(R.color.white));

                    if (strPerformanceType != null)
                        strPerformanceType = strPerformanceType + "," + "model";
                    else
                        strPerformanceType = "model";
                } else {
                    if (strPerformanceType.contains("model")) {
                        strPerformanceType = strPerformanceType.replace("model", "");
                    }
                    chkModel.setTextColor(context.getResources().getColor(R.color.dark_black));
                }
            }
        });
        chkSinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    chkSinger.setTextColor(context.getResources().getColor(R.color.white));

                    if (strPerformanceType != null)
                        strPerformanceType = strPerformanceType + "," + "singer";
                    else
                        strPerformanceType = "singer";

                } else {
                    if (strPerformanceType.contains("singer")) {
                        strPerformanceType = strPerformanceType.replace("singer", "");
                    }
                    chkSinger.setTextColor(context.getResources().getColor(R.color.dark_black));
                }
            }
        });
        chkDancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    chkDancer.setTextColor(context.getResources().getColor(R.color.white));

                    if (strPerformanceType != null)
                        strPerformanceType = strPerformanceType + "," + "dancer";
                    else
                        strPerformanceType = "dancer";
                } else {
                    if (strPerformanceType.contains("dancer")) {
                        strPerformanceType = strPerformanceType.replace("dancer", "");
                    }
                    chkDancer.setTextColor(context.getResources().getColor(R.color.dark_black));
                }
            }
        });
        chkMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    chkMale.setTextColor(context.getResources().getColor(R.color.white));
                    chkFemale.setChecked(false);
                    chkFemale.setTextColor(context.getResources().getColor(R.color.dark_black));
                    strGender = "male";
                } else {
                    strGender = "";
                    chkMale.setTextColor(context.getResources().getColor(R.color.dark_black));

                }
            }
        });
        chkFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    chkFemale.setTextColor(context.getResources().getColor(R.color.white));
                    chkMale.setChecked(false);
                    chkMale.setTextColor(context.getResources().getColor(R.color.dark_black));
                    strGender = "female";
                } else {
                    strGender = "";
                    chkFemale.setTextColor(context.getResources().getColor(R.color.dark_black));

                }
            }
        });
        chkUnion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // is chkIos checked?

                if (((CheckBox) v).isChecked()) {
                    chkUnion.setTextColor(context.getResources().getColor(R.color.white));
                    //  chkNonUnion.setChecked(false);
                    // chkNonUnion.setTextColor(context.getResources().getColor(R.color.dark_black));
                    strUnionType = "union";
                    UnionType = true;
                    //  by nijam
                    /*if (chkNonUnion.isChecked()) {
                        strUnionType = strUnionType + ",non-union";
                    }*/
                    chkNonUnion.setChecked(false);
                    chkNonUnion.setTextColor(context.getResources().getColor(R.color.dark_black));

                } else {
                    strUnionType = "";
                   /* if (chkNonUnion.isChecked()) {
                        strUnionType = strUnionType + "non-union";
                    }*/

                    UnionType = false;
                    chkUnion.setTextColor(context.getResources().getColor(R.color.dark_black));

                }

                // if (((CheckBox) v).isChecked()) {
                // chkUnion.setTextColor(context.getResources().getColor(
                // R.color.white));
                // if (!strUnionType.equals("")) {
                // strUnionType = strUnionType + "," + "Union";
                // }
                //
                // else {
                // strUnionType = "union";
                // }
                // UnionType = true;
                //
                // } else {
                // strUnionType = "";
                // if (chkNonUnion.isChecked() == true) {
                // strUnionType = "non-union";
                // }
                // UnionType = false;
                // chkUnion.setTextColor(context.getResources().getColor(
                // R.color.dark_black));
                //
                // }
            }
        });

        chkNonUnion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // is chkIos checked?

                if (((CheckBox) v).isChecked()) {
                    chkNonUnion.setTextColor(context.getResources().getColor(R.color.white));
                    // chkUnion.setChecked(false);
                    //  chkUnion.setTextColor(context.getResources().getColor(R.color.dark_black));
                    strUnionType = "non-union";
                   /* if (chkUnion.isChecked()) {
                        strUnionType = "union," + strUnionType;
                    }*/
                    chkUnion.setChecked(false);
                    chkUnion.setTextColor(context.getResources().getColor(R.color.dark_black));
                    UnionType = false;
                } else {
                    strUnionType = "";
                    UnionType = true;
                   /* if (chkUnion.isChecked()) {
                        strUnionType = "union" + strUnionType;
                    }*/
                    chkNonUnion.setTextColor(context.getResources().getColor(R.color.dark_black));

                }

                // if (((CheckBox) v).isChecked()) {
                // chkNonUnion.setTextColor(context.getResources().getColor(
                // R.color.white));
                //
                // if (!strUnionType.equals(""))
                // strUnionType = strUnionType + "," + "non-union";
                // else
                // strUnionType = "non-union";
                // } else {
                // strUnionType = "";
                // if (chkUnion.isChecked() == true) {
                // strUnionType = "union";
                // }
                //
                // chkNonUnion.setTextColor(context.getResources().getColor(
                // R.color.dark_black));
                //
                // }
            }
        });
        birthYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert(years, birthYear);

            }
        });
    }

    // Sugumaran Changes (25th May 16)


    public void postFavorite(String favoriteValue) {
        try {
            HttpPost request = new HttpPost(HttpUri.SET_FAV);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/x-www-form-urlencoded");

            JSONStringer item = null;

            try {

                // if(rel_image_screen.getVisibility() == View.VISIBLE){
                //
                // item = new
                // JSONStringer().object().key("userId").value(Library.androidUserID).key("roleId").value(myList.get(position).roleId).key("castingFav")
                // .value(favoriteValue).key("roleEnthicity").value(myList.get(position).roleForEthnicity).key("roleAgeRange").value(myList.get(position).ageRange)
                // .key("roleFor").value(myList.get(position).roleForGender).endObject();
                // Log.d("Data", item.toString());
                //
                // }else{

                // {"userId":"68","roleId":"77","castingFav":"1","roleEnthicity":"Caucasian","roleAgeRange":"40-50","roleFor":"Female"}
                if (myList.size() == 10) {
                    if (position == 10)
                        position = 9;
                }

                item = new JSONStringer().object().key("userId").value(Library.getUserId(context)).key("roleId").value(myList.get(position).roleId).key("castingFav")
                        .value(favoriteValue).key("roleEnthicity").value(myList.get(position).roleForEthnicity).key("roleAgeRange").value(myList.get(position).ageRange)
                        .key("roleFor").value(myList.get(position).roleForGender).endObject();
                Log.d("Data", item.toString());

                // }

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StringEntity entity = null;

            try {
                entity = new StringEntity(item.toString());
                request.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Send request to WCF service
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = null;

            try {
                response = httpClient.execute(request);
                if (response.getStatusLine().getStatusCode() == 200) {

                    HttpEntity responseEntity = response.getEntity();

                    try {
                        is = responseEntity.getContent();
                    } catch (IllegalStateException e) {

                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        BufferedReader reader2 = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                        sb = new StringBuilder();
                        String line = null;
                        while ((line = reader2.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        is.close();
                        json = sb.toString();

                        // response: [{"fav_count":"13"}]

                        json = sb.toString();
                        // [{"fav_count":"9","roleFav":"1"}]
                        jArr = new JSONArray(json);
                        DebugReportOnLocat.ln("json response for fav::" + jArr);

                        JSONObject oneObject = jArr.getJSONObject(0); // Pulling
                        // items
                        // from
                        // the
                        // array

                        favCount = oneObject.getString("fav_count");
                        roleFav = oneObject.getString("roleFav");

                        DebugReportOnLocat.ln("json response for submit::" + json);

                    } catch (Exception e) {
                        Log.e("Buffer Error", "Error converting result " + e.toString());
                    }

                } else {
                    Status = null;
                }

            } catch (ClientProtocolException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void postImageFavorite(String favoriteValue) {
        try {
            HttpPost request = new HttpPost(HttpUri.SET_IMG_FAV);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/x-www-form-urlencoded");

            JSONStringer item = null;

            try {

                // if(rel_image_screen.getVisibility() == View.VISIBLE){
                //
                // item = new
                // JSONStringer().object().key("userId").value(Library.androidUserID).key("roleId").value(myList.get(position).roleId).key("castingFav")
                // .value(favoriteValue).key("roleEnthicity").value(myList.get(position).roleForEthnicity).key("roleAgeRange").value(myList.get(position).ageRange)
                // .key("roleFor").value(myList.get(position).roleForGender).endObject();
                // Log.d("Data", item.toString());
                //
                // }else{

                // {"userId":"68","roleId":"77","castingFav":"1","roleEnthicity":"Caucasian","roleAgeRange":"40-50","roleFor":"Female"}

                // userId,imageId,imageFav
                if (position == 10)
                    position = 9;

                // int get = sequenceOrder - 1;

                // Sugumaran Changes (25th May 16)
                // this below if condition made for the new changes(Image only
                // shows). Otherwise else content is remain
                if (casting_image_only.getVisibility() == View.VISIBLE) {

                    if (imagePos >= 0 && imagePos < myListImage.size()) {
                        item = new JSONStringer().object().key("userId").value(Library.getUserId(context)).key("imageId").value(myListImage.get((imagePos)).imgId).key("imageFav")
                                .value(favoriteValue).endObject();


                    }
                } else {
                    if (sequenceOrder == -1) {
                        // get = 0;
                        sequenceOrder = 0;
                    }
                    item = new JSONStringer().object().key("userId").value(Library.getUserId(context)).key("imageId").value(myListImage.get((imagePos)).imgId).key("imageFav")
                            .value(favoriteValue).endObject();
                }

                System.out.println("item.toString() :: " + item.toString());
                Log.d("Data", item.toString());

                // }

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StringEntity entity = null;

            try {
                entity = new StringEntity(item.toString());
                System.out.println("entity :: " + entity.toString());
                request.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Send request to WCF service
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = null;

            try {
                response = httpClient.execute(request);
                if (response.getStatusLine().getStatusCode() == 200) {

                    HttpEntity responseEntity = response.getEntity();

                    try {
                        is = responseEntity.getContent();
                    } catch (IllegalStateException e) {

                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        BufferedReader reader2 = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                        sb = new StringBuilder();
                        String line = null;
                        while ((line = reader2.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        is.close();
                        json = sb.toString();

                        // response: [{"fav_count":"13"}]

                        json = sb.toString();
                        // [{"fav_count":"9","roleFav":"1"}]
                        jArr = new JSONArray(json);
                        DebugReportOnLocat.ln("json response for fav::" + jArr);
                        JSONObject oneObject = jArr.optJSONObject(0);
                        favCount = oneObject.optString("fav_count");
                        imageFav = oneObject.optString("imageFav");
                        DebugReportOnLocat.ln("json response for submit::" + json);
                    } catch (Exception e) {
                        Log.e("Buffer Error", "Error converting result " + e.toString());
                    }

                } else {
                    Status = null;
                }

            } catch (ClientProtocolException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // to get the general casting from web service
    public String getJSONData() {
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            if (strPerformanceType != null) {
                strPerformanceType = strPerformanceType.startsWith(",") ? strPerformanceType.substring(1) : strPerformanceType;
            }

            dlatitude = Math.round(dlatitude * 100.0) / 100.0;
            dlongitude = Math.round(dlongitude * 100.0) / 100.0;
            if (dlatitude == 0.0 && dlongitude == 0.0) {
                // http://casting.sdiphp.com/castingNew/public/casting?lat=37.28&lang=-122.00&userId=152&birthyear=&ethnicity=&gender=&performancetype=&page=0&uniontype=
                dlatitude = 37.28; // latitude::37.3382082
                dlongitude = -122.00; // longitude:: -121.8863286
            }
            nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(dlatitude)));
            nameValuePairs.add(new BasicNameValuePair("lang", String.valueOf(dlongitude)));
            nameValuePairs.add(new BasicNameValuePair("userId", Library.getUserId(context)));
            if (!Library.getEmailId(context).equals("")) {
                nameValuePairs.add(new BasicNameValuePair("email", Library.getEmailId(context)));
            }

            mixpanel.identify(Library.getUserId(context));

            if (!strBithYear.equals(""))
                nameValuePairs.add(new BasicNameValuePair("birthyear", strBithYear));
            if (!strEthnicity.equals(""))
                nameValuePairs.add(new BasicNameValuePair("ethnicity", strEthnicity));
            if (!strGender.equals(""))
                nameValuePairs.add(new BasicNameValuePair("gender", strGender));
            if (strPerformanceType.equals(",,,,,,,,")) {
                strPerformanceType = strPerformanceType.replace(",,,,,,,,", "");
            } else if (strPerformanceType.equals(",,,,,,,")) {
                strPerformanceType = strPerformanceType.replace(",,,,,,,", "");
            } else if (strPerformanceType.equals(",,,,,,")) {
                strPerformanceType = strPerformanceType.replace(",,,,,,", "");
            } else if (strPerformanceType.equals(",,,,,")) {
                strPerformanceType = strPerformanceType.replace(",,,,,", "");
            } else if (strPerformanceType.equals(",,,,")) {
                strPerformanceType = strPerformanceType.replace(",,,,", "");
            } else if (strPerformanceType.equals(",,,")) {
                strPerformanceType = strPerformanceType.replace(",,,", "");
            } else if (strPerformanceType.equals(",,")) {
                strPerformanceType = strPerformanceType.replace(",,", "");
            } else if (strPerformanceType.equals(",")) {
                strPerformanceType = strPerformanceType.replace(",", "");
            }

            if (!strPerformanceType.equals(""))
                nameValuePairs.add(new BasicNameValuePair("performancetype", strPerformanceType));
            if (!strUnionType.equals(""))
                nameValuePairs.add(new BasicNameValuePair("uniontype", strUnionType));
            // added by nijam workflow4
            nameValuePairs.add(new BasicNameValuePair("listType", listType));

            if (strSelectedLocation.contains(",")) {
                String[] cityList = strSelectedLocation.split(",");

                String arrayFirst = cityList[0];

                if (arrayFirst.contains(" ")) {
                    cityName = arrayFirst.replace(" ", "_");
                } else {
                    cityName = arrayFirst;
                }

                //   strSelectedLocation = cityName;

            }


            //    nameValuePairs.add(new BasicNameValuePair("city", strSelectedLocation));
            nameValuePairs.add(new BasicNameValuePair("city", cityName));

            nameValuePairs.add(new BasicNameValuePair("page", String.valueOf(page)));
            String Urls = HttpUri.CASTING + URLEncodedUtils.format(nameValuePairs, "utf-8");
            System.out.println("Casting URL Link :: " + Urls);
            HttpGet httpget = new HttpGet(Urls);
            DebugReportOnLocat.ln("filter casting values:" + URLEncodedUtils.format(nameValuePairs, "utf-8"));
            HttpResponse response = httpclient.execute(httpget);
            // Execute HTTP Get Request
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity());
                DebugReportOnLocat.ln("result:::" + result);

                sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                editor = sharedpreferences.edit();
                editor.putFloat(Library.LAT, (float) dlatitude);
                editor.putFloat(Library.LNG, (float) dlongitude);
                editor.putString(Library.ETHNICITY, strEthnicity);
                editor.putString(Library.PERFORMANCE_TYPE, strPerformanceType);
                editor.putString(Library.UNION, strUnionType);
                editor.putString(Library.GENDER, strGender);
                editor.putString(Library.BIRTH, strBithYear);
                editor.putString(Library.SELECTED_LOCATION, strSelectedLocation);
                editor.putBoolean(Library.NON_UNION, UnionType);

                editor.commit();

            } else {

                return "";

            }
        } catch (Exception e) {
            e.printStackTrace();
            DebugReportOnLocat.ln("Error : " + e.getMessage());
        }

        return result;
    }

    // to get the general casting from web service
    public String getJSONImages() {
        String result = "";
        // try {
        //
        // HttpClient httpclient = new DefaultHttpClient();
        //
        // HttpGet httpget = new HttpGet(HttpUri.IMAGE_URL);
        //
        // HttpResponse response = httpclient.execute(httpget);.
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();

        String lat = Double.toString(latitude);
        String lang = Double.toString(longitude);

        try {

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            //    nameValuePairs.add(new BasicNameValuePair("lat", lat));
            //     nameValuePairs.add(new BasicNameValuePair("lang", lang));

            // Sugumaran
            //   nameValuePairs.add(new BasicNameValuePair("ds", getDS()));
            nameValuePairs.add(new BasicNameValuePair("userId", Library.getUserId(context)));
            nameValuePairs.add(new BasicNameValuePair("deviceId", Library.getDeviceID(context)));

            HttpClient httpclient = new DefaultHttpClient();
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");

            String Url = HttpUri.IMAGE_URL + "?" + paramsString;

            System.out.println("Url " + Url);

            HttpGet httpget = new HttpGet(HttpUri.IMAGE_URL + "?" + paramsString);
            HttpResponse response = httpclient.execute(httpget);
            // Execute HTTP Get Request
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity());
                DebugReportOnLocat.ln("result:::" + result);

            } else {

                return "";

            }
        } catch (Exception e) {
            e.printStackTrace();
            DebugReportOnLocat.ln("Error : " + e.getMessage());
        }

        return result;
    }

    void CastingLeftSwipeView() {

        position--;
        DebugReportOnLocat.ln("left pos" + position);
        int pos = position;

        if (position >= head && position <= tail) {

            setAllData(myList, position);

        } else {

            page = page - 1;

            if (page >= 0 && page <= totalPages) {

                if (CastivateApplication.getInstance().hashMap.containsKey(page)) {
                    myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
                    castViewStatus = false;
                    position = 19;
                    head = 0;
                    tail = myList.size();

                    setAllData(myList, position);

                } else {
                    position = 19;

                    handlerCastingSync.sendEmptyMessage(0);
                }
            } else {

                page = lastPage;

                if (getTotalCastings <= 20) {
                    page = 0;

                    myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
                    castViewStatus = false;

                    position = myList.size() - 1;

                    head = 0;
                    tail = myList.size();

                    if ((page == 0 && position == 0) || (page == totalPages && position == myList.size() - 1)) {

                        textCastivate.setVisibility(View.VISIBLE);
                        castingViewNoImage.setVisibility(View.GONE);

                        castingsList.setVisibility(View.GONE);
                        bSwipe = "left";
                        DebugReportOnLocat.ln("" + pos);

                    } else {

                        setAllData(myList, position);

                    }

                } else {
                    if (CastivateApplication.getInstance().hashMap.containsKey(page)) {

                        myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
                        castViewStatus = false;

                        position = myList.size() - 1;

                        head = 0;
                        tail = myList.size();

                        if ((page == 0 && position == 0) || (page == totalPages && position == myList.size() - 1)) {

                            textCastivate.setVisibility(View.VISIBLE);
                            castingViewNoImage.setVisibility(View.GONE);

                            castingsList.setVisibility(View.GONE);
                            bSwipe = "left";

                            DebugReportOnLocat.ln("" + pos);
                        } else {

                            setAllData(myList, position);

                        }

                    } else {
                        swpe = false;
                        handlerCastingSync.sendEmptyMessage(0);

                    }
                }

            }

        }

    }

    void CastingRightSwipeView() {

        position++;

        if (position >= head && position < tail) {

            setAllData(myList, position);
        } else {

            page = page + 1;

            if (CastivateApplication.getInstance().hashMap.containsKey(page)) {

                myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
                castViewStatus = false;

                head = 0;
                position = 0;
                tail = myList.size();

                setAllData(myList, position);

            } else {

                position = -1;

                if (page > totalPages) {

                    page = 0;

                    if (CastivateApplication.getInstance().hashMap.containsKey(page)) {

                        myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
                        castViewStatus = false;
                        position = 0;
                        head = 0;
                        tail = myList.size();

                        if ((page == 0 && position == 0) || (page == totalPages && position == myList.size() - 1)) {

                            testB = true;
                            bSwipe = "right";
                            setAllData(myList, position);
                        } else {
                            setAllData(myList, position);
                        }

                    } else {
                        position = 0;

                        handlerCastingSync.sendEmptyMessage(0);
                    }
                } else {

                    handlerCastingSync.sendEmptyMessage(0);

                }

            }
        }

    }

    public void setAllData(final ArrayList<CastingDetailsModel> myList, final int currentPos) {

      /*  SharedPreferences new_maintain_pref1 = getSharedPreferences("new_maintain_pref", MODE_PRIVATE);
        String new_str = new_maintain_pref1.getString("new", "");
        String cache = "";
        if (new_str.equals("")) {
            cache = myList.get(currentPos).casting_id + "";
        } else if(!new_str.contains(myList.get(currentPos).casting_id+"")) {
            cache = new_str + "," + myList.get(currentPos).casting_id;
        }
        Editor editor = new_maintain_pref1.edit();
        editor.putString("new", cache);
        editor.commit();*/


        try {
            Calendar c = Calendar.getInstance();
            int date = Integer.valueOf(String.valueOf(c.get(Calendar.DATE)));
            SharedPreferences new_maintain_pref = getSharedPreferences("new_maintain_pref", MODE_PRIVATE);
            int date_pref = new_maintain_pref.getInt("date", 0000);

            if (date > date_pref) {
                Editor editor1 = new_maintain_pref.edit();
                editor1.putString("new", "");
                editor1.putInt("date", date);
                editor1.commit();
            } else if (date == date_pref) {
                String new_str = new_maintain_pref.getString("new", "");
                if (new_str.equals("")) {
                    new_str = myList.get(currentPos).casting_id + "";
                } else if (!new_str.contains(myList.get(currentPos).casting_id + "")) {
                    new_str = new_str + "," + myList.get(currentPos).casting_id;
                }
                Editor editor2 = new_maintain_pref.edit();
                editor2.putString("new", new_str);
                editor2.commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            if (!Library.getId(context).equals("")) {
                imgReportAbuse.setVisibility(View.VISIBLE);
            } else {
                imgReportAbuse.setVisibility(View.GONE);
            }

            System.out.println("Swipe------------------------------->");

            if (myList.get(currentPos).favCasting.equals("0")) {
                fav_iconNoImage.setChecked(false);
            } else {
                fav_iconNoImage.setChecked(true);
            }
            if (castingViewNoImage.getVisibility() == View.GONE) {
                castingViewNoImage.setVisibility(View.VISIBLE);
            }
            if (listViewStatus == true) {
                // scrollCount = 0;
                // castingsListIconChange.setVisibility(View.VISIBLE);
                castingsListIcon.setVisibility(View.GONE);
                // by nijam
                outbox_icon.setVisibility(View.INVISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                castingsList.setVisibility(View.VISIBLE);
                rel_cast_view.setVisibility(View.GONE);
                castingViewNoImage.setVisibility(View.GONE);
                menuListView.setVisibility(View.GONE);
            } else {
                castingsListIcon.setVisibility(View.VISIBLE);
                // by nijam
                outbox_icon.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.GONE);
                // castingsListIconChange.setVisibility(View.GONE);
                rel_cast_view.setVisibility(View.VISIBLE);
                castingViewNoImage.setVisibility(View.VISIBLE);
                castingsList.setVisibility(View.GONE);
                menuListView.setVisibility(View.GONE);
            }

            textCastingTitleNoImage.setText(Html.fromHtml(myList.get(currentPos).castingTitle.toString().trim()));
            textCastingTypeNoImage.setText(Html.fromHtml(myList.get(currentPos).castingType.toString().trim()));
            textPaidStatusNoImage.setText(myList.get(currentPos).castingPaidStatus.toString().trim());
            strState = myList.get(currentPos).state.toString().trim();
            strCity = myList.get(currentPos).country.toString().trim();

            if (strState.equals("") && strCity.equals("")) {
                cityAndStateName = "Nationwide";
            } else {
                cityAndStateName = strCity + ", " + strState;
            }
            if (cityAndStateName.equals(", ")) {
                cityAndStateName = "Nationwide";
            }

            if (strCity.equals("") && !strState.equals("")) {
                cityAndStateName = strState;
            } else if (!strCity.equals("") && strState.equals("")) {
                cityAndStateName = strCity;
            }

            String html = cityAndStateName + " - " + myList.get(currentPos).castingSubmissionDetail.toString().trim();
            Spannable WordtoSpan = new SpannableString(html);
            WordtoSpan.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, cityAndStateName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            WordtoSpan.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.NORMAL), cityAndStateName.length(), html.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            textSubmissionDetailNoImage.setText(WordtoSpan);

            //line for link in string
            Linkify.addLinks(textSubmissionDetailNoImage, Linkify.ALL);

            ((CastingsLinkMovementMethod) CastingsLinkMovementMethod.getInstance()).setContext(context, false);
            textSubmissionDetailNoImage.setMovementMethod(CastingsLinkMovementMethod.getInstance());

            textAgeRangeNoImage.setText("Age " + myList.get(currentPos).ageRange.toString().trim());
            textSynopsisNoImage.setText(Html.fromHtml(myList.get(currentPos).castingSynopsis.toString().trim()));
            textRoleForEthnicityNoImage.setText(Html.fromHtml(myList.get(currentPos).roleForEthnicity.toString().trim()));
            textUnionStatusNoImage.setText(myList.get(currentPos).castingUnionStatus.toString().trim());
            textRoleForGenderNoImage.setText(myList.get(currentPos).roleForGender.toString().trim());
            txtRoleDescriptionNoImage.setText(myList.get(currentPos).roleDescription.toString().trim());

            if (myList.get(currentPos).abuse_flag == 0) {
                imgReportAbuse.setImageBitmap(null);
                imgReportAbuse.setImageResource(R.drawable.abuse_button1);
            } else {
                imgReportAbuse.setImageBitmap(null);
                imgReportAbuse.setImageResource(R.drawable.abuse_reported_button1);
            }

            if (Library.isApplied) {
                myList.get(Library.currentPos).applyFlag = "1";
                Library.isApplied = false;
                Library.currentPos = 0;
            }

            if (myList.get(currentPos).castingEmail.isEmpty()) {
                layBottom.setVisibility(View.GONE);
                System.out.println("castingEmail Empty");
            } else {

                System.out.println("applyFlag : " + myList.get(currentPos).applyFlag);

                if (myList.get(currentPos).applyFlag.equals("1")) {
                    //submitted
                    layBottom.setVisibility(View.VISIBLE);
                    btn_apply.setText("Applied");
                    imgTick.setVisibility(View.VISIBLE);
                    layBottom.setBackgroundColor(getResources().getColor(R.color.green));
                } else {
                    // by nijam
                    if (!imageCount) {
                        layBottom.setVisibility(View.GONE);
                    } else {


                        System.out.println("castingEmail------------>" + myList.get(currentPos).castingEmail);
                        layBottom.setVisibility(View.VISIBLE);
                        btn_apply.setText("Apply Now ");
                        imgTick.setVisibility(View.GONE);

                        layBottom.setBackgroundColor(getResources().getColor(R.color.red));
                        layBottom.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                try {
                                    Library.role_id = myList.get(currentPos).roleId.toString().trim();
                                    Library.enthicity = myList.get(currentPos).roleForEthnicity.toString().trim();
                                    Library.age_range = myList.get(currentPos).ageRange.toString().trim();
                                    Library.gender = myList.get(currentPos).roleForGender.toString().trim();
                                    Library.currentPos = currentPos;


//                            Commented by Nivetha

                                    selectedCastingDetailsModels.clear();
                                    selectedCastingDetailsModels.add(myList.get(currentPos));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (Library.getEmailId(context).equals("")) {

                                    Intent intent = new Intent(CastingScreen.this, CastingLogin.class);
                                    intent.putExtra("FromCasting", true);
                                    startActivity(intent);

                                }
                                if (Library.getId(context) != null && !Library.getId(context).equals("")) {

                                    try {
                                        final CastingDetailsModel model = myList.get(position);
                                        if (model != null) {

                                            //              if (Library.getIsPurchased(context).equals("1")) {
                                            MatchedCastingRetrofit(model.casting_id);
                                           /* } else {
                                                Intent intent = new Intent(CastingScreen.this, CastingPlan.class);
                                                intent.putExtra("CalledBy", "Casting");
                                                intent.putExtra("FromCasting", true);
                                                startActivity(intent);
                                            }*/


                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
//                            else if (Library.getIsPurchased(context).equals("1")) {

                           /* SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                            int imageCount = sharedpreferences.getInt("imageCount", 0);
                            int videoCount = sharedpreferences.getInt("videoCount", 0);
                            int resumeCount = sharedpreferences.getInt("resumeCount", 0);

                            if (imageCount != 0 && videoCount != 0 && resumeCount != 0) {
                                //   ApplyCastingRetrofit();

                                //  MatchedCastingRetrofit();
                            } else {
                                alertdialog();
                                Intent i = new Intent(CastingScreen.this, MyProfile.class);
                                startActivityForResult(i, 1);
                            }
*///                            } else {
//                                Intent intent = new Intent(CastingScreen.this, CastingPlan.class);
//                                intent.putExtra("CalledBy", "Casting");
//                                intent.putExtra("FromCasting", true);
//                                startActivity(intent);
//                            }

                            }
                        });
                    }
                }
            }


            if (testB == true) {

                textCastivate.setVisibility(View.VISIBLE);
                castingViewNoImage.setVisibility(View.GONE);
                castingsList.setVisibility(View.GONE);
                testB = false;
                int po = currentPos;
                DebugReportOnLocat.ln("new position" + po);
            }
            position = currentPos;
            if (position == -1) {
                position = 19;
            }
        } catch (IndexOutOfBoundsException e) {
            // TODO: handle exception
            System.out.println("ee " + e);
        }
        if (txtNocast.getVisibility() == View.VISIBLE) {
            rel_cast_view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {

        if (castivityScreen.getVisibility() == View.GONE /*
                                                         * ||thankYouLayout.
														 * getVisibility() ==
														 * View
														 * .GONE||grid_view_photos
														 * .getVisibility() ==
														 * View.GONE
														 */) {


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
            txtContent.setText("Do you want to Exit from this Application?");
            btnOk.setText("No");
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setText("Yes");
            viewSep.setVisibility(View.VISIBLE);

            btnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    CastivateApplication.getInstance().hashMap.clear();
                    bs = false;

                    page = 0;
                    totalPages = 0;
                    castViewStatus = false;
                    castViewStatusreak = false;
                    swipe = "";
                    bSwipe = "";
                    swpe = true;
                    position = 0;
                    bs = false;

                    myList = new ArrayList<CastingDetailsModel>();
                    sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                    editor = sharedpreferences.edit();
                    rateCount++;
                    editor.putInt(Library.RATEIT, rateCount);
                    editor.commit();
                    finishAffinity();
                }
            });

            btnOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
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



           /* AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);

            alertDialogBuilder.setMessage("Do you want to Exit from this Application?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                public void onClick(DialogInterface dialog, int id) {
                    CastivateApplication.getInstance().hashMap.clear();
                    bs = false;

                    page = 0;
                    totalPages = 0;
                    castViewStatus = false;
                    castViewStatusreak = false;
                    swipe = "";
                    bSwipe = "";
                    swpe = true;
                    position = 0;
                    bs = false;

                    myList = new ArrayList<CastingDetailsModel>();
                    sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                    editor = sharedpreferences.edit();
                    rateCount++;
                    editor.putInt(Library.RATEIT, rateCount);
                    editor.commit();
                    finishAffinity();

                }
            })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();*/
        } else {
            if (castivityScreen.getVisibility() == View.VISIBLE) {
                rel_image_screen.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));
                gear_icon.setVisibility(View.VISIBLE);
                outbox_icon.setVisibility(View.VISIBLE);
                gear_icon_mirror.setVisibility(View.INVISIBLE);

                if (castivityScreen.getVisibility() == View.VISIBLE) {
                    castivityScreen.setVisibility(View.GONE);
                }
                // filterValue=1;
                // list = new ArrayList<CastingDetailsModel>();
                myList = new ArrayList<CastingDetailsModel>();

                myList.clear();
                page = 0;
                bs = false;
                position = 0;
                castViewStatus = false;
                castViewStatusreak = false;
                swipe = "";
                bSwipe = "";
                swpe = true;

                totalPages = 0;

                CastivateApplication.getInstance().hashMap.clear();
                tap_current_location.setVisibility(View.GONE);
                castingViewNoImage.setVisibility(View.VISIBLE);
                castingsList.setVisibility(View.GONE);
                if (rel_image_screen.getVisibility() == View.VISIBLE) {
                    rel_image_screen.setVisibility(View.GONE);
                    rel_upload_screen.setVisibility(View.GONE);
                    castImageViewStatus = false;
                }
                myList = new ArrayList<CastingDetailsModel>();
                handlerCastingSync.sendEmptyMessage(0);
            }
        }
        hideSoftKeyboard();
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */


    @Override
    protected void onDestroy() {

        mixpanel.flush();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            // ImageView Click

            case R.id.castings_list_icon:
                textCastivate.setVisibility(View.GONE);
                castingViewNoImage.setVisibility(View.GONE);
                rel_image_screen.setVisibility(View.GONE);
                rel_upload_screen.setVisibility(View.GONE);
                castingsList.setVisibility(View.VISIBLE);

                castingsListIcon.setVisibility(View.GONE);
                outbox_icon.setVisibility(View.INVISIBLE);
                // by nijam
                tabLayout.setVisibility(View.VISIBLE);
                // castingsListIconChange.setVisibility(View.GONE);// My
                rel_info_ListView.setVisibility(View.VISIBLE);
                listViewStatus = true;

                grid_view_photos.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                if (listType.equals("3")) {
                    //by nijam
                    menuListView.setVisibility(View.VISIBLE);
                    swipeList = new ArrayList<CastingDetailsModel>();
                    swipeList.addAll(castingsListArr);
                    swipeViewListAdapter = new SwipeViewListAdapter(context, swipeList);
                    menuListView.setAdapter(swipeViewListAdapter);
                    castingsList.setVisibility(View.GONE);

                } else {

                    castListAdapter = new CastingsListAdapter(CastingScreen.this, castingsListArr);
                    refreshYourAdapter(castingsListArr);
                    castingsList.setAdapter(castListAdapter);
                    castingsList.setSelection(0);
                    castListAdapter.notifyDataSetChanged();
                }

                page = 0;

                // Sugumaran Changes (25th May 16)
                casting_image_only.setVisibility(View.GONE);


                if (Network.isNetworkConnected(CastingScreen.this)) {
                    txtNocast.setVisibility(View.GONE);
                    rel_upload_screen.setVisibility(View.GONE);
                    castingsList.setEnabled(false);
                    castingsList.setVisibility(View.VISIBLE);
                    menuListView.setVisibility(View.GONE);
                    myList.clear();
                    swipeList.clear();
                    castingsListArr.clear();
                    listType = String.valueOf(1);
                    view_allcasting.setBackgroundColor(Color.parseColor("#CB212E"));
                    view_local.setBackgroundColor(Color.parseColor("#2F2F2F"));
                    view_applied.setBackgroundColor(Color.parseColor("#2F2F2F"));
                    new GetDatas("1").execute();
                    myList.clear();

                } else {
                    Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
                    finishAffinity();
                }


                break;
            // case R.id.castings_list_icon_change:
            // // castingsListIconChange.setVisibility(View.GONE);
            // // castingsListIcon.setVisibility(View.GONE);
            //
            // castingsList.setVisibility(View.GONE);
            // listViewStatus = false;
            //
            // rel_info_ListView.setVisibility(View.GONE);
            // if (gridViewStatus) {
            // progressView.setVisibility(View.GONE);
            // grid_view_photos.setVisibility(View.VISIBLE);
            // // rel_cast_view.setVisibility(View.GONE);
            // castingViewNoImage.setVisibility(View.GONE);
            // } else {
            // rel_cast_view.setVisibility(View.VISIBLE);
            // castingViewNoImage.setVisibility(View.VISIBLE);
            // grid_view_photos.setVisibility(View.GONE);
            // }
            // if (castImageViewStatus) {
            // rel_image_screen.setVisibility(View.VISIBLE);
            // rel_upload_screen.setVisibility(View.VISIBLE);
            // // rel_cast_view.setVisibility(View.GONE);
            // castingViewNoImage.setVisibility(View.GONE);
            // } else {
            // rel_image_screen.setVisibility(View.GONE);
            // rel_upload_screen.setVisibility(View.GONE);
            // rel_cast_view.setVisibility(View.VISIBLE);
            // castingViewNoImage.setVisibility(View.VISIBLE);
            //
            // }
            // break;

            case R.id.current_location:
                tap_current_location.setVisibility(View.VISIBLE);
                editTextLocation.requestFocus();
                findViewById(R.id.frlay).setVisibility(View.VISIBLE);
                break;
            case R.id.birth_year:

                alert(years, birthYear);
                break;

            case R.id.txt_ethnicity:
                ethnicityDialog();
                break;
            case R.id.txt_submit_casting:

                startActivity(new Intent(CastingScreen.this, PostCastivity.class));

                break;
            /*case R.id.txt_login:

                if (txtLogin.getText().equals("Sign In")) {
                    startActivity(new Intent(CastingScreen.this, CastingLogin.class));
                } else {
                    startActivity(new Intent(CastingScreen.this, MyProfile.class));

                }
                break;*/
    /*vasanth you add this for profile user name*/
            case R.id.profile_uesrname:
                // startActivity(new Intent(CastingScreen.this, MyProfile.class));
                Intent i = new Intent(CastingScreen.this, MyProfile.class);
                startActivityForResult(i, 99);
                break;

            case R.id.txt_signup:
                startActivity(new Intent(CastingScreen.this, Signup.class));
                break;

            case R.id.txt_login:
                startActivity(new Intent(CastingScreen.this, CastingLogin.class));
                break;
            case R.id.text_help_over_lay:

                startActivity(new Intent(context, Help.class));

                break;
            case R.id.text_logout:

                alert();

                break;
            case R.id.clear:

                editTextLocation.setText("");
                clear.setVisibility(View.GONE);
                tap_current_location.setVisibility(View.GONE);
                editor.remove(Library.SELECTED_LOCATION);
                editor.commit();
                strSelectedLocation = "";
                hideSoftKeyboard();
                break;
            case R.id.birth_clear:

                birthYear.setText("");
                strBithYear = "";
                birthClear.setVisibility(View.INVISIBLE);
                break;

            case R.id.rel_upload_screen:
                // rel_info_GirdView.setVisibility(View.GONE);
                // grid_view_photos.setVisibility(View.GONE);

//                uploadImageToBackEnd();
                break;
            case R.id.tap_current_location:
                if (tap_current_location.getVisibility() == View.VISIBLE)
                    tap_current_location.setVisibility(View.GONE);
                // KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
                hideSoftKeyboard();
                dlatitude = dclatitude;
                dlongitude = dclongitude;
                editTextLocation.setText(strCurrentLocation);
                break;

           /* case R.id.txtAllCasting:
                System.out.println("Clicked");
                if (Network.isNetworkConnected(CastingScreen.this)) {
                    rel_upload_screen.setVisibility(View.GONE);
                    castingsList.setEnabled(false);
                    new GetDatas().execute();

                } else {
                    Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
                    finishAffinity();
                }
                break;
            case R.id.txtAppliedCasting:
                break;
            case R.id.txtLocalCasting:
                break;*/


            case R.id.fav_count:

                Intent createBinderIntent = new Intent(context, FavoriteScreen.class);
                startActivity(createBinderIntent);
                isFavScreen = false;
                position = 0;
                sequenceOrder = -1;
                Library.currentPos = 0;
                Library.isApplied = false;

                sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                if (sharedpreferences.contains(Library.RATEIT)) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.remove(Library.RATEIT);
                    editor.commit();
                }

                break;

        }

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
        Button btnCancel = (Button) convertView.findViewById(R.id.btncancel);
        View viewSep = (View) convertView.findViewById(R.id.viewSep);
        txtContent.setText("Are you sure you want to logout");
        btnOk.setText("Ok");
        btnCancel.setVisibility(View.VISIBLE);
        viewSep.setVisibility(View.VISIBLE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                SharedPreferences settings = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = settings.edit();

                editor1.remove("id");
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

                editor1.commit();

                finish();
                startActivity(getIntent());

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
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

        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finishAffinity();
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
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







       /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog);

        // Setting Dialog Title
        // alertDialog.setTitle("Cast");

        // Setting Dialog Message
        alertDialog.setMessage("Please enable GPS to continue inside the app.");

        // On pressing Settings button
        alertDialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Close App", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });

        // Showing Alert Message
        alertDialog.show();*/
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.help_overlay:
                help_overlay.setVisibility(View.GONE);
                txtSwipeCastings.setVisibility(View.VISIBLE);
                txtSwipeCastings.startAnimation(animBlink);

                Handler handler = null;
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        txtSwipeCastings.setVisibility(View.GONE);

                    }
                }, 5000);
                break;
        }

        return false;
    }


    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences("PushNotification", Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }

//    public void captureImage() {
//        try {
//
//            File f = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/tempCastivate.jpg");
//            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            // camera.putExtra("android.intent.extras.CAMERA_FACING", -1);
//            // camera.putExtra("android.intent.extras.CAMERA_FACING",
//            // android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
//            camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//            startActivityForResult(camera, CAMERA_CAPTURE);
//        } catch (ActivityNotFoundException anfe) {
//            // display an error message
//            String errorMessage = "Oops! - your device doesn't support capturing images!";
//            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
//            toast.show();
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        if (resultCode == RESULT_OK) {
//            if (requestCode == CAMERA_CAPTURE) {
//                imagePath = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/tempCastivate.jpg";
//                DebugReportOnLocat.ln("imagePath  " + imagePath);
//                picPath = imagePath;
//                profileImage = new File(imagePath);
//                imageDisplay();
//            }
//
//            if (requestCode == RESULT_LOAD_IMAGE && null != data) {
//                encodedImage = "";
//                Uri selectedImage = data.getData();
//                DebugReportOnLocat.ln("URI : " + selectedImage);
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                String picturePath = cursor.getString(columnIndex);
//                cursor.close();
//
//                picPath = picturePath;
//                // bm = BitmapFactory.decodeFile(picturePath);
//                Bitmap bm = decodeFile(new File(picturePath));
//                if (bm != null) {
//
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                    bm.recycle();
//                    bm = null;
//                    b = baos.toByteArray();
//                    encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//                    DebugReportOnLocat.ln("encodedImage >> " + encodedImage);
//
//                    double latitude = gps.getLatitude();
//                    double longitude = gps.getLongitude();
//
//                    System.out.println("latitude " + latitude);
//                    System.out.println("longitude " + longitude);
//
//
//                    String lat = Double.toString(latitude);
//                    String lang = Double.toString(longitude);
//
//                    Intent intent = new Intent(context, ThankYouActivity.class);
//                    intent.putExtra("Latitude", lat);
//                    intent.putExtra("Longitude", lang);
//                    intent.putExtra("Image", "CastingScreen");
//                    startActivity(intent);
//
//                }
//            }
//
//
//            if (requestCode == FROM_CAMERA
//                    && resultCode == Activity.RESULT_OK) {
//
//                if (profileImagePath != null && !profileImagePath.equals("")) {
//                    startActivityForResult(new Intent(context,
//                            CropperImageActivity.class).putExtra("Path",
//                            profileImagePath), ACTION_REQUEST_CROP);
//
//                }
//
//            }
//
//            if (requestCode == CROP_PIC && resultCode != 0) {
//                // Create an instance of bundle and get the returned data
//                Bundle extras = data.getExtras();
//                // get the cropped bitmap from extras
//                Bitmap thePic = extras.getParcelable("data");
//                // set image bitmap to image view
//                imageProfile.setImageBitmap(thePic);
//
//                imageProfile.setImageBitmap(RoundedShapeBitmap.getRoundedShape(
//                        thePic, 120));
//
//            }
//
//            if (requestCode == FROM_LIBRARY && resultCode == RESULT_OK
//                    && null != data) {
//
//
//                profileImagePath = getRealPathFromURI(data.getData());
//
//                if (profileImagePath != null && !profileImagePath.equals("")) {
//                    startActivityForResult(new Intent(context,
//                            CropperImageActivity.class).putExtra("Path",
//                            profileImagePath), ACTION_REQUEST_CROP);
//
//                }
//
//            } else if (requestCode == ACTION_REQUEST_CROP
//                    && resultCode == RESULT_OK) {
//
//                String filePath = data.getStringExtra("picture_path");
//                System.out.println(" filePath>>>" + filePath);
//                if (filePath != null && !filePath.equals("")) {
//
//                } else {
//                    return;
//                }
//
//                Bitmap imageBitmap = decodeFile(new File(filePath));
//                // Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
//                b = baos.toByteArray();
//                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//
//                try {
//
//                    int rotation = BitmapUtil.getExifOrientation(filePath);
//
//                    if (imageBitmap != null) {
//
//                        Matrix matrix = new Matrix();
//                        matrix.setRotate(rotation);
//                        imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0,
//                                imageBitmap.getWidth(), imageBitmap.getHeight(),
//                                matrix, true);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if (imageBitmap != null) {
//
//                    imageProfile.setImageBitmap(imageBitmap);
//                    updateImage(encodedImage);
//
//                }
//            }

//        }
//    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    // private Camera mCamera;
    // int cameraId;
    // private boolean cameraFront = false;
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

                initCamera(context);

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


                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(i, FROM_LIBRARY);

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

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            String lat = Double.toString(latitude);
            String lang = Double.toString(longitude);

            Intent intent = new Intent(context, ThankYouActivity.class);
            intent.putExtra("Latitude", lat);
            intent.putExtra("Longitude", lang);
            intent.putExtra("Image", "CastingScreen");
            startActivity(intent);

        } else {
            // Toast.makeText(context, "Pick Images Only", Toast.LENGTH_LONG)
            // .show();
        }
    }

//    private Bitmap decodeFile(File f) {
//
//        Bitmap b = null;
//
//        // Decode image size
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(f);
//
//            BitmapFactory.decodeStream(fis, null, o);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//
//            try {
//                fis.close();
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        int scale = 1;
//
//        if (f.length() > 8000) {
//
//            scale = 5;
//
//            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
//                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
//            }
//        }
//        // Decode with inSampleSize
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = scale;
//        try {
//            fis = new FileInputStream(f);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        b = BitmapFactory.decodeStream(fis, null, o2);
//        try {
//            fis.close();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return b;
//    }

    @Override
    public void getPushNotificationCasting() {

        // rollId614#Female#18-30#Caucasian#Actor,Model
        boolean chkPsh = false;
        int pos = 0;
        // String pushData = "605Male18-30Caucasian";
        // String pushData = "605";//Male18-30Caucasian";

        if (forChk == false) {
            for (int pages = 0; pages <= totalPages; pages++) {

                if (CastivateApplication.getInstance().hashMap.containsKey(pages)) {
                    myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(pages));

                    for (int i = 0; i < myList.size(); i++) {
                        String getPushCheck = myList.get(i).roleId;

                        if (!PushRollID.equals("")) {
                            if (PushRollID.equalsIgnoreCase(getPushCheck)) {
                                pos = i;
                                chkPsh = true;
                                break;
                            }
                        }
                    }
                } else {
                    checkPages = -1;
                    forChk = true;
                    break;
                }

            }

        }

        if (chkPsh == false) {
            if (checkPages != totalPages) {
                checkPages++;
            } else {
                checkPages = 0;
            }

            for (int i = 0; i < myList.size(); i++) {
                String getPushCheck = myList.get(i).roleId;

                if (!PushRollID.equals("")) {
                    if (PushRollID.equalsIgnoreCase(getPushCheck)) {
                        pos = i;
                        chkPsh = true;
                        break;
                    }
                }
            }
        }

        if (chkPsh == true) {
            chkPsh = false;
            isPush = false;
            setAllData(myList, pos);
        } else {
            page = checkPages;

            if (page < totalPages) {
                isPush = true;
                handlerCastingSync.sendEmptyMessage(0);
            } else {
                setAllData(myList, 0);
            }

        }

    }

    public void imageGrid() {

        // rel_cast_view.setVisibility(View.GONE);

        // if (myListImage != null && myListImage.size() == 0) {
        progressView.setVisibility(View.GONE);

        progressBar = new ProgressDialog(context);
        progressBar.setMessage("Loading image...");
        // progressBar.show();
        progressBar.setCancelable(false);

        grid_view_photos.setAdapter(null);
        if (Network.isNetworkConnected(context)) {
            new GetImages().execute();
        } else {
            Library.showToast(context, "Please check your network connection.");
        }

        if (myListImage != null && myListImage.size() > 0) {


//			CastivateGridAdapter adapter = new CastivateGridAdapter(context, myListImage, this);
//			grid_view_photos.setAdapter(adapter);
//


            ImageListAdapter imageListAdapter = new ImageListAdapter(context, myListImage, CastingScreen.this);
            grid_view_photos.setAdapter(imageListAdapter);


        }
    }

    @Override
    public void get(int pos) {
        // Sugumaran Changes (25th May 16)
        imagePos = pos;

        imgFav.setVisibility(View.VISIBLE);
        CastingImagesModel model = myListImage.get(pos);
        String getName = model.userName.toString().trim();
        imgFav.setChecked(model.favImg);
        imgFav.setEnabled(true);
        txtName.setVisibility(View.VISIBLE);
        if (getName.equals("")) {
            txtName.setVisibility(View.GONE);
        } else {
            txtName.setText(getName);
        }
        sequenceOrder = pos;
        imgGrid.setVisibility(View.VISIBLE);
        local_talent.setVisibility(View.VISIBLE);
        rel_cast_view.setVisibility(View.VISIBLE);
        grid_view_photos.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.GONE);
        gridViewStatus = false;
        casting_image.setVisibility(View.VISIBLE);
        rel_upload_screen.setVisibility(View.VISIBLE);

        if (Network.isNetworkConnected(context)) {
            progressView.setVisibility(View.VISIBLE);
//			Picasso.with(context).load(model.imageRole).noFade().into(casting_image, new Callback() {
//				@Override
//				public void onSuccess() {
//					progressView.setVisibility(View.GONE);
//				}
//
//				@Override
//				public void onError() {
//				}
//			});

            UrlImageViewHelper.setUrlDrawable(casting_image, model.imageRole, new UrlImageViewCallback() {

                @Override
                public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
                    // TODO Auto-generated method stub
                    progressView.setVisibility(View.GONE);
                }
            });

        } else {
            Library.showToast(context, "Please check your network connection.");
        }

        // Sugumaran Changes (25th May 16)
        casting_image.setVisibility(View.GONE);
        casting_image_only.setVisibility(View.VISIBLE);
        if (Network.isNetworkConnected(context)) {
//			progressView.setVisibility(View.VISIBLE);
//			Picasso.with(context).load(model.imageRole).noFade().into(casting_image_only, new Callback() {
//				@Override
//				public void onSuccess() {
//					progressView.setVisibility(View.GONE);
//				}
//
//				@Override
//				public void onError() {
//					Library.showToast(context, "Image load error.");
//				}
//			});

            progressView.setVisibility(View.VISIBLE);
            UrlImageViewHelper.setUrlDrawable(casting_image_only, model.imageRole, new UrlImageViewCallback() {

                @Override
                public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
                    // TODO Auto-generated method stub
                    progressView.setVisibility(View.GONE);
                }
            });

        } else {
            Library.showToast(context, "Please check your network connection.");
        }

    }

    public void checkRate() {

        SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);

        Ratecount = prefs.getInt("Ratecount", 0);
        nwdate = prefs.getInt("ndate", 0);
        boolean getRateFlag = prefs.getBoolean(Library.RATEIT_FLAG, false);
        int dateCount = prefs.getInt("dateCount", 0);
        //
        // Ratecount++;
        // SharedPreferences.Editor editor = getSharedPreferences("my_pref",
        // MODE_PRIVATE).edit();
        // editor.putInt("Ratecount", Ratecount);
        // editor.commit();
        //
        // String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
        // .replace("-", "");
        // int datei = Integer.parseInt(date);
        //
        // if (datei != nwdate) {
        //
        // dateCount++;
        // editor.putInt("dateCount ", dateCount);
        // editor.putInt("ndate", datei);
        // editor.commit();
        //
        // }

        if ((Ratecount == 5 || dateCount == 3) && getRateFlag == false) {
            rateThisAppAlert();
            // int myDays = 3;
            //
            // final Calendar c = Calendar.getInstance();
            // c.add(Calendar.DATE, myDays); // number of days to add
            // int newDate = (c.get(Calendar.YEAR) * 10000) +
            // ((c.get(Calendar.MONTH) + 1) * 100) +
            // (c.get(Calendar.DAY_OF_MONTH));
            SharedPreferences.Editor editor = getSharedPreferences("my_pref", MODE_PRIVATE).edit();
            editor.putInt("Ratecount", 0);
            editor.putInt("dateCount ", 0);
            editor.putInt("ndate", 0);
            editor.commit();
        }

    }

    public String getDS() {
        String DS = "";
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        System.out.println("Screen Width :" + width);
        System.out.println("Screen Height :" + height);

        String getWH = width + "x" + height;

        if (getWH.equalsIgnoreCase("480x800")) {
            DS = "ap1";
        } else if (getWH.equalsIgnoreCase("720x1280")) {
            DS = "ap2";
        } else if (getWH.equalsIgnoreCase("768x1280")) {
            DS = "ap3";
        } else if ((getWH.equalsIgnoreCase("1080x1920"))/*
                                                         * ||(getWH.equalsIgnoreCase
														 * ("1920x1080"))
														 */) {
            DS = "ap4";
        } else if (getWH.equalsIgnoreCase("1280x800")) {
            DS = "ap4";
        }

        System.out.println("DS " + DS);
        return DS;
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        imageGrid();
    }

	/*
     * private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	 *
	 * @Override public void onReceive(Context context, Intent intent) {
	 *
	 * String getRollIDs = intent.getStringExtra("rollID");
	 *
	 * System.out.println("CastingScreen GetRollID >> " + getRollIDs);
	 *
	 * PushRollID = getRollIDs; getPushNotificationCasting();
	 *
	 * } };
	 */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);

        if (grid_view_photos.getVisibility() == View.VISIBLE) {
            return false;
        } else {
            return gestureDetector.onTouchEvent(ev);
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

                    SharedPreferences prefs = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("isPurchased", isPurchased);

                    editor.commit();

                    handlerCastingSync.sendEmptyMessage(0);
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

    private void closeProgress() {
        // TODO Auto-generated method stub
        if (pDialog.isShowing())
            pDialog.dismiss();
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
                                searchAdapter = new LocationSearchAdapter(CastingScreen.this, stringList);

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

    ;

    public class PostUserTokenIdSync extends AsyncTask<String, Void, Void> {
        ProgressDialog pDialog = null;

        @Override
        public void onPreExecute() {
            pDialog = new ProgressDialog(CastingScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            postJSONData();
            return null;
        }

        @Override
        public void onPostExecute(Void params) {
            if (pDialog != null) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

            notifFlag = true;
            handlerCastingSync.sendEmptyMessage(0);

        }

    }

    public class PostSetNotifSync extends AsyncTask<String, Void, Void> {

        // @Override
        // public void onPreExecute() {
        // pDialog = new ProgressDialog(CastingScreen.this,
        // AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        // pDialog.setMessage("Please wait...");
        // pDialog.setCancelable(false);
        // pDialog.show();
        // }

        @Override
        protected Void doInBackground(String... params) {

            postJSONNotifData();
            return null;
        }

        @Override
        public void onPostExecute(Void params) {
            // if (pDialog != null) {
            // if (pDialog.isShowing())
            // pDialog.dismiss();
            notifFlag = false;
            // }

            // handlerCastingSync.sendEmptyMessage(0);

        }

    }

    @SuppressLint("InlinedApi")
    public class PostFavoriteCastingSync extends AsyncTask<String, Void, Void> {
        ProgressDialog pDialog = null;

        @Override
        public void onPreExecute() {
            pDialog = new ProgressDialog(CastingScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            postFavorite(favriteFlag);
            return null;
        }

        @Override
        public void onPostExecute(Void params) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            fav_iconNoImage.setEnabled(true);
            if (!favCount.equals("0")) {
                // fav_icon.setVisibility(View.INVISIBLE);

                favCountText.setVisibility(View.VISIBLE);
                favCountText.setText(favCount);
            } else {
                // fav_icon.setVisibility(View.VISIBLE);
                favCountText.setVisibility(View.INVISIBLE);
            }
            try {
                JSONObject props = new JSONObject();
                if (roleFav != null && roleFav.equals("1")) {
                    myList.get(position).favCasting = "1";
                    fav_iconNoImage.setSelected(true);

                    try {
                        props.put("User ID", androidUserID);
                        props.put("Casting ID", myList.get(position).roleId.toString().trim());
                        props.put("Casting Title", myList.get(position).castingTitle.toString().trim());
                        props.put("Action", "Added");
                        mixpanel.track("Favorites", props);
                    } catch (JSONException e) {

                    }

                } else {
                    myList.get(position).favCasting = "0";
                    fav_iconNoImage.setSelected(false);
                    try {
                        props.put("User ID", androidUserID);
                        props.put("Casting ID", myList.get(position).roleId.toString().trim());
                        props.put("Casting Title", myList.get(position).castingTitle.toString().trim());
                        props.put("Action", "Removed");
                        mixpanel.track("Favorites", props);
                    } catch (JSONException e) {

                    }
                }

            } catch (IndexOutOfBoundsException e) {

            }

        }

    }

    @SuppressLint("InlinedApi")
    private class PostFavoriteImageSync extends AsyncTask<String, Void, Void> {
        ProgressDialog pDialog = null;

        @Override
        public void onPreExecute() {
            pDialog = new ProgressDialog(CastingScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            postImageFavorite(favriteFlag);
            return null;
        }

        @Override
        public void onPostExecute(Void params) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            imgFav.setEnabled(true);


            // int getTotalfav = Integer.parseInt(favCount) +
            // Integer.parseInt(favImgCount);
            // favCount = "" + getTotalfav;

            if (!favCount.equals("0")) {
                // fav_icon.setVisibility(View.INVISIBLE);

                favCountText.setVisibility(View.VISIBLE);
                favCountText.setText(favCount);
            } else {
                // fav_icon.setVisibility(View.VISIBLE);
                favCountText.setVisibility(View.INVISIBLE);
            }

            // Sugumaran Changes (25th May 16)
            if (casting_image_only.getVisibility() == View.VISIBLE) {

                if (myListImage.get((imagePos)).favImg) {
                    myListImage.get((imagePos)).favImg = false;
                } else {
                    myListImage.get((imagePos)).favImg = true;
                }

            } else {
                if (myListImage.get((sequenceOrder)).favImg) {
                    myListImage.get((sequenceOrder)).favImg = false;
                } else {
                    myListImage.get((sequenceOrder)).favImg = true;
                }
                // .get((sequenceOrder)).favImg = favriteFlag;
            }

            // try {
            // JSONObject props = new JSONObject();
            // if (imageFav != null && imageFav.equals("1")) {
            // myList.get(position).favCasting = "1";
            // myListImage.get(3);
            //
            // fav_iconNoImage.setSelected(true);
            //
            // try {
            // props.put("User ID", androidUserID);
            // props.put("Casting ID",
            // myList.get(position).roleId.toString().trim());
            // props.put("Casting Title",
            // myList.get(position).castingTitle.toString().trim());
            // props.put("Action", "Added");
            // mixpanel.track("Favorites", props);
            // } catch (JSONException e) {
            //
            // }
            //
            // } else {
            // myList.get(position).favCasting = "0";
            // fav_iconNoImage.setSelected(false);
            // try {
            // props.put("User ID", androidUserID);
            // props.put("Casting ID",
            // myList.get(position).roleId.toString().trim());
            // props.put("Casting Title",
            // myList.get(position).castingTitle.toString().trim());
            // props.put("Action", "Removed");
            // mixpanel.track("Favorites", props);
            // } catch (JSONException e) {
            //
            // }
            // }
            //
            // } catch (IndexOutOfBoundsException e) {
            //
            // }

        }

    }

    public class GetImages extends AsyncTask<String, Void, Void> {

        String getImageLinks = "";

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub

            getImageLinks = getJSONImages();
            System.out.println("getImageLinks:: " + getImageLinks);

            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            // TODO Auto-generated method stub
            if (progressBar != null && progressBar.isShowing())
                progressBar.dismiss();

            swipeRefreshLayout.setRefreshing(false);

            myListImage = new ArrayList<CastingImagesModel>();
            JSONArray jArray;
            try {
                jArray = new JSONArray(getImageLinks);
                if (jArray != null) {

                    CastingImagesModel detailsModel;

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);

                        boolean flag = false;
                        if (json_data.getInt("fav_flag") == 0) {
                            flag = false;
                        } else if (json_data.getInt("fav_flag") == 1) {
                            flag = true;
                        }

                        detailsModel = new CastingImagesModel(json_data.getString("casting_image"), json_data.getString("user_name"), json_data.getString("casting_image_thumb"),
                                json_data.getInt("image_id"), flag, "", json_data.optString("commentCount"));
                        myListImage.add(detailsModel);


                        //count


                    }

                    //    tid = myListImage.get(0).commentCount;
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            if (notifFlag == true) {
                userNotificationFlag = "Yes";
                notification = true;
                sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                editor = sharedpreferences.edit();
                editor.putBoolean(Library.NOTIFICATION, notification);
                editor.commit();
                toggleButton.setChecked(true);
                postSetNotifSync = new PostSetNotifSync();
                postSetNotifSync.execute();
            }

        }

    }

    public class GetDatas extends AsyncTask<String, Void, Void> {
        public String type;

        public GetDatas(String type) {
            this.type = type;
        }

        String getAll = "";
        ProgressDialog pDialog = null;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // if (pDialog != null && pDialog.isShowing()) {
            // pDialog.dismiss();
            pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Loading Castings..");
            pDialog.show();
            pDialog.setCancelable(false);
            // }
        }

        @Override
        protected Void doInBackground(String... params) {

            getAll = getJSONData();

            return null;
        }

        @Override
        protected void onPostExecute(Void params) {

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                if (type.equals("3")) {
                    menuListView.setEnabled(true);
                } else {
                    castingsList.setEnabled(true);
                }


                if (getAll != null && !getAll.equals("[]") && !getAll.equals("")) {
                    if (listViewStatus == true) {
                        // castingsListIconChange.setVisibility(View.VISIBLE);

                        rel_cast_view.setVisibility(View.GONE);
                        castingViewNoImage.setVisibility(View.GONE);
                        if (type.equals("3")) {
                            menuListView.setVisibility(View.VISIBLE);
                            castingsList.setVisibility(View.GONE);
                        } else {

                            menuListView.setVisibility(View.GONE);
                            castingsList.setVisibility(View.VISIBLE);

                        }

                    } else {
                        castingsListIcon.setVisibility(View.VISIBLE);
                        // by nijam
                        outbox_icon.setVisibility(View.VISIBLE);
                        tabLayout.setVisibility(View.GONE);
                        rel_cast_view.setVisibility(View.VISIBLE);
                        castingViewNoImage.setVisibility(View.VISIBLE);
                        castingsList.setVisibility(View.GONE);
                        menuListView.setVisibility(View.GONE);

                    }
                    textCastivate.setVisibility(View.GONE);
                    myList = new ArrayList<CastingDetailsModel>();

                    castViewStatus = false;
                    try {
                        JSONArray jArray = new JSONArray(getAll);

                        CastingDetailsModel detailsModel;
                        // CastingDetailsModel listDetailsModel;
                        for (int i = 0; i < jArray.length(); i++) {
                            json_data = jArray.getJSONObject(i);

                         /*   System.out.println("isNewValue---->" + json_data.getString("isNew"));
                            String s = json_data.optString("isNew");
*/
                            detailsModel = new CastingDetailsModel(json_data.getString("IdRole"), json_data.getString("casting_title"), json_data.getString("casting_type"),
                                    json_data.getString("casting_paid_status"), json_data.getString("casting_union_status"), json_data.getString("casting_union_type"),
                                    json_data.getString("casting_submission_detail"), json_data.getString("casting_synopsis"), json_data.getString("casting_image"),
                                    json_data.getString("role_desc"), json_data.optString("ageRange"), json_data.getString("role_for"), json_data.getString("role_ethnicity"),
                                    json_data.getString("fav_flag"), json_data.getString("fav_count"), "", json_data.getString("castingsTotal"),
                                    json_data.getString("casting_state"), json_data.getString("casting_city"), json_data.getString("casting_email"), json_data.getString("apply_flag"), json_data.optInt("abuse_flag"), json_data.optInt("casting_id"), json_data.optString("isNew"));
                            // listDetailsModel = new
                            // CastingListDetailsModel(json_data.getString("IdRole"),
                            // json_data.getString("casting_title"),
                            // json_data.getString("casting_type"),
                            // json_data.getString("casting_paid_status"));

                            getTotalCastings = Integer.parseInt(json_data.getString("castingsTotal"));
                            favCount = json_data.getString("fav_count");
                            DebugReportOnLocat.ln("total::" + getTotalCastings);

                            if (!favCount.equals("0")) {

                                favCountText.setVisibility(View.VISIBLE);
                                favCountText.setText(favCount);
                            } else {

                                favCountText.setVisibility(View.INVISIBLE);
                            }

                            myList.add(detailsModel);
                            castingsListArr.add(detailsModel);


                        }

                        if (castingsList.getVisibility() == View.VISIBLE) {
                            //      myList.clear();
                            //      swipeList.clear();
                            menuListView.setVisibility(View.GONE);
                            castingsList.setVisibility(View.VISIBLE);

                            castingsList.setAdapter(null);


                            ArrayList<CastingDetailsModel> castingsListArrList = new ArrayList<CastingDetailsModel>(castingsListArr);
                            refreshYourAdapter(castingsListArrList);
                            castingsList.setAdapter(castListAdapter);
                            castingsList.setSelection(0);
                            castListAdapter.notifyDataSetChanged();

                            // Sugumaran Changes(31 May 2016)
                            try {
                                int getpos = castingsListArr.size() - 1;
                                System.out.println("getPos " + getpos);
                                castingsList.setSelection(getpos - 19);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //    castingsList.setSelection(0);
                        } else if (menuListView.getVisibility() == View.VISIBLE) {
                            myList.clear();
                            castingsList.setVisibility(View.GONE);
                            //  menuListView.setAdapter(null);
                            ArrayList<CastingDetailsModel> castingsListArrList = new ArrayList<CastingDetailsModel>(castingsListArr);
                            swipeViewListAdapter = new SwipeViewListAdapter(context, castingsListArrList);
                            refreshYour(castingsListArrList);

                            menuListView.setAdapter(swipeViewListAdapter);
                            swipeViewListAdapter.notifyDataSetChanged();

                            // Sugumaran Changes(31 May 2016)
                            int getpos = castingsListArr.size();
                            System.out.println("getPos " + getpos);
                            menuListView.setSelection(getpos - 19);


                        }

                        // My New
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                        CastivateApplication.getInstance().hashMap.put(page, myList);
                        if (getTotalCastings > 20) {
                            totalPages = getTotalCastings / 20;

                            DebugReportOnLocat.ln("totalPages::" + totalPages);
                        }
                        if (isPush) {
                            getPushNotificationCasting();
                        } else {

                            DebugReportOnLocat.ln("total::" + getTotalCastings);

                            if (bs == false) {
                                lastPage = totalPages;
                                bs = true;
                            }

                            DebugReportOnLocat.ln("page " + page);

                            DebugReportOnLocat.ln("list is " + myList);
                            if (getTotalCastings > 20) {
                                if (page == lastPage) {
                                    position = myList.size() - 1;
                                }
                            }

                            head = 0;
                            tail = myList.size();

                            if (position == -1) {
                                position = 0;
                            }

                            // if ((page == 0 && position == 0) || (page ==
                            // totalPages && position == myList.size() - 1)) {
                            if ((page == totalPages && position == myList.size() - 1)) {

                                if (swpe == true) {
                                    head = 0;
                                    tail = myList.size();

                                    position = 0;
                                    setAllData(myList, position);
                                    swpe = false;
                                } else {

                                    textCastivate.setVisibility(View.VISIBLE);
                                    castingViewNoImage.setVisibility(View.GONE);
                                    //   castingsList.setVisibility(View.GONE);
                                    bSwipe = "left";

                                    int pos = position;
                                    DebugReportOnLocat.ln("" + pos);
                                }
                            } else {

                                setAllData(myList, position);

                            }

                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {

                    rel_cast_view.setVisibility(View.GONE);
                    castivityScreen.setVisibility(View.INVISIBLE);
                    outbox_icon.setVisibility(View.VISIBLE);
                    gear_icon_mirror.setVisibility(View.INVISIBLE);
                    gear_icon.setVisibility(View.VISIBLE);
                    if (listType.equals("3")) {
                        menuListView.setVisibility(View.GONE);
                        txtNocast.setVisibility(View.VISIBLE);
                        txtNocast.setText("No castings applied");
                        if (txtNocast.getVisibility() == View.VISIBLE) {
                            rel_cast_view.setVisibility(View.GONE);
                        }
                        outbox_icon.setVisibility(View.INVISIBLE);
                       /* // TODO Auto-generated method stub
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);
                        alertDialogBuilder.setMessage("There are no castings that match your filter selection").setCancelable(false)
                                .setNeutralButton("Try undoing some filters", new DialogInterface.OnClickListener() {
                                    @SuppressLint("NewApi")
                                    public void onClick(DialogInterface dialog, int id) {
                                        *//*if (Network.isNetworkConnected(CastingScreen.this)) {
                                            rel_upload_screen.setVisibility(View.GONE);
                                            castingsList.setEnabled(false);
                                            castingsList.setVisibility(View.VISIBLE);
                                            menuListView.setVisibility(View.GONE);
                                            myList.clear();
                                            swipeList.clear();
                                            castingsListArr.clear();
                                            listType = String.valueOf(1);
                                            view_allcasting.setBackgroundColor(Color.parseColor("#CB212E"));
                                            view_local.setBackgroundColor(Color.parseColor("#2F2F2F"));
                                            view_applied.setBackgroundColor(Color.parseColor("#2F2F2F"));
                                            //     new GetDatas("1").execute();

                                            myList.clear();

                                        } else {
                                            Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
                                            finishAffinity();
                                        }*//*

                                        if (castivityScreen.getVisibility() == View.VISIBLE) {
                                            castivityScreen.setVisibility(View.GONE);
                                            outbox_icon.setVisibility(View.VISIBLE);
                                            gear_icon_mirror.setVisibility(View.GONE);
                                            // if (listViewStatus)
                                            // castingsListIconChange.setVisibility(View.VISIBLE);
                                            // else
                                            // castingsListIcon.setVisibility(View.VISIBLE);

                                        } else {
                                            castivityScreen.setVisibility(View.VISIBLE);
                                            outbox_icon.setVisibility(View.GONE);
                                            gear_icon_mirror.setVisibility(View.VISIBLE);

                                            // castingsListIconChange.setVisibility(View.GONE);

                                            castingsListIcon.setVisibility(View.GONE);
                                            // by nijam
                                            tabLayout.setVisibility(View.VISIBLE);
                                            castivityScreen.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left));
                                            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                                            dlongitude = dclongitude;
                                            dlatitude = dclatitude;
                                        }

                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();*/


                    } else {
                        txtNocast.setVisibility(View.VISIBLE);
                        if (listType.equals("3")) {

                        } else {
                            txtNocast.setText("No castings matched");
                        }
                        if (txtNocast.getVisibility() == View.VISIBLE) {
                            rel_cast_view.setVisibility(View.GONE);
                        }

                       /* getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
                        txtContent.setText("There are no castings that match your filter selection");
                        btnOk.setText("Try undoing some filters");
                        btnCancel.setVisibility(View.GONE);
                        viewSep.setVisibility(View.GONE);

                        btnOk.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                if (castivityScreen.getVisibility() == View.VISIBLE) {
                                    castivityScreen.setVisibility(View.GONE);
                                    outbox_icon.setVisibility(View.VISIBLE);
                                    gear_icon_mirror.setVisibility(View.GONE);
                                    // if (listViewStatus)
                                    // castingsListIconChange.setVisibility(View.VISIBLE);
                                    // else
                                    // castingsListIcon.setVisibility(View.VISIBLE);

                                } else {
                                    castivityScreen.setVisibility(View.VISIBLE);
                                    outbox_icon.setVisibility(View.INVISIBLE);
                                    gear_icon_mirror.setVisibility(View.VISIBLE);

                                    // castingsListIconChange.setVisibility(View.GONE);

                                    castingsListIcon.setVisibility(View.GONE);
                                    // by nijam
                                    tabLayout.setVisibility(View.VISIBLE);
                                    castivityScreen.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left));
                                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                                    dlongitude = dclongitude;
                                    dlatitude = dclatitude;
                                }
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
*/




                       /* // TODO Auto-generated method stub
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);
                        alertDialogBuilder.setMessage("There are no castings that match your filter selection").setCancelable(false)
                                .setNeutralButton("Try undoing some filters", new DialogInterface.OnClickListener() {
                                    @SuppressLint("NewApi")
                                    public void onClick(DialogInterface dialog, int id) {

                                        if (castivityScreen.getVisibility() == View.VISIBLE) {
                                            castivityScreen.setVisibility(View.GONE);
                                            outbox_icon.setVisibility(View.VISIBLE);
                                            gear_icon_mirror.setVisibility(View.GONE);
                                            // if (listViewStatus)
                                            // castingsListIconChange.setVisibility(View.VISIBLE);
                                            // else
                                            // castingsListIcon.setVisibility(View.VISIBLE);

                                        } else {
                                            castivityScreen.setVisibility(View.VISIBLE);
                                            outbox_icon.setVisibility(View.INVISIBLE);
                                            gear_icon_mirror.setVisibility(View.VISIBLE);

                                            // castingsListIconChange.setVisibility(View.GONE);

                                            castingsListIcon.setVisibility(View.GONE);
                                            // by nijam
                                            tabLayout.setVisibility(View.VISIBLE);
                                            castivityScreen.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left));
                                            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                                            dlongitude = dclongitude;
                                            dlatitude = dclatitude;
                                        }

                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
*/

                        // Toast.makeText(CastingScreen.this, "Empty Casting ",
                        // 0).show();

                        // Toast.makeText(CastingScreen.this, "Empty Casting ",
                        // 0).show();

                        if (rel_info_ListView.getVisibility() == View.VISIBLE && (type.equals("1") || type.equals("2"))) {
                            rel_info_ListView.setFocusable(false);

                            textCastivate.setVisibility(View.GONE);
                            castingViewNoImage.setVisibility(View.GONE);
                            rel_image_screen.setVisibility(View.GONE);
                            rel_upload_screen.setVisibility(View.GONE);
                            castingsList.setVisibility(View.VISIBLE);

                            castingsListIcon.setVisibility(View.GONE);
                            // by nijam
                            outbox_icon.setVisibility(View.INVISIBLE);
                            tabLayout.setVisibility(View.VISIBLE);
                            // castingsListIconChange.setVisibility(View.GONE);// My
                            rel_info_ListView.setVisibility(View.VISIBLE);
                            // listViewStatus = true;

                            menuListView.setVisibility(View.GONE);

                            grid_view_photos.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.GONE);

                            if (txtNocast.getVisibility() == View.VISIBLE) {
                                myList.clear();
                                swipeList.clear();
                                castingsListArr.clear();
                            }

                        } else if (rel_info_ListView.getVisibility() == View.VISIBLE && (type.equals("3"))) {
                            rel_info_ListView.setFocusable(false);

                            textCastivate.setVisibility(View.GONE);
                            castingViewNoImage.setVisibility(View.GONE);
                            rel_image_screen.setVisibility(View.GONE);
                            rel_upload_screen.setVisibility(View.GONE);
                            castingsList.setVisibility(View.GONE);
                            menuListView.setVisibility(View.VISIBLE);
                            castingsListIcon.setVisibility(View.GONE);
                            // by nijam
                            outbox_icon.setVisibility(View.INVISIBLE);
                            tabLayout.setVisibility(View.VISIBLE);
                            // castingsListIconChange.setVisibility(View.GONE);// My
                            rel_info_ListView.setVisibility(View.VISIBLE);
                            // listViewStatus = true;

                            grid_view_photos.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                        } else {
                            page = 0;
                        }
                        if (CastivateApplication.getInstance().hashMap.containsKey(page)) {

                            myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
                            castViewStatus = false;
                            position = 0;
                            head = 0;
                            tail = myList.size();
                            int TAILs = myList.size();

                            if ((page == 0 && position == 0) || (page == totalPages && position == myList.size() - 1)) {

                                textCastivate.setVisibility(View.VISIBLE);
                                castingViewNoImage.setVisibility(View.GONE);
                                castingsList.setVisibility(View.GONE);
                            } else {
                                setAllData(myList, position);
                            }

                        } /*
                     * else { position = 0;
					 *
					 * handlerCastingSync.sendEmptyMessage(0); }
					 */
// by nijam


                    }


                }
            }

            if (listViewStatus == false) {
                handlerCastingImagesSync.sendEmptyMessage(0);
            }

        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            ProgressDialog myDialog = new ProgressDialog(context);
            if (myDialog.isShowing()) {
                myDialog.dismiss();
            }
        }

    }


    // @Override
    // public void onWindowFocusChanged(boolean hasFocus) {
    // // TODO Auto-generated method stub
    // super.onWindowFocusChanged(hasFocus);
    //
    // DisplayMetrics displaymetrics = new DisplayMetrics();
    // getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
    // int height = displaymetrics.heightPixels;
    // int width = displaymetrics.widthPixels;
    // System.out.println("Screen Width :"+width);
    // System.out.println("Screen Height :"+height);
    //
    // System.out.println("TopBar width * Height: "+home_screen.getWidth()+" x "+home_screen.getHeight());
    // System.out.println("BottomBar width * Height: "+rel_upload_screen.getWidth()+" x "+rel_upload_screen.getHeight());
    // //System.out.println("casting_image width * Height: "+casting_image.getWidth()+" x "+casting_image.getHeight());
    //
    //
    // }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if ((txtNocast.getVisibility() == View.GONE) && castingsList.getVisibility() == View.GONE && menuListView.getVisibility() == View.GONE) {
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;
                    // right to left swipe
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                        if (casting_image_only.getVisibility() == View.VISIBLE) {

                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_right);
                            casting_image_only.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));

                            imagePos++;
                            if (imagePos >= 0 && myListImage.size() > imagePos) {

                            } else {
                                imagePos = 0;
                            }

                            imgFav.setVisibility(View.VISIBLE);
                            CastingImagesModel model = myListImage.get(imagePos);
                            String getName = model.userName.toString().trim();
                            imgFav.setChecked(model.favImg);
                            imgFav.setEnabled(true);
                            txtName.setVisibility(View.VISIBLE);
                            if (getName.equals("")) {
                                txtName.setVisibility(View.GONE);
                            } else {
                                txtName.setText(getName);
                            }
                            if (Network.isNetworkConnected(context)) {

                                progressView.setVisibility(View.VISIBLE);
//							Picasso.with(context).load(myListImage.get(imagePos).imageRole).noFade().into(casting_image_only, new Callback() {
//								@Override
//								public void onSuccess() {
//									progressView.setVisibility(View.GONE);
//								}
//
//								@Override
//								public void onError() {
//									Library.showToast(context, "Image load error.");
//								}
//							});


                                //change here

                                tid = myListImage.get(imagePos).commentCount;
                                if (!tid.equals("0")) {
                                    txtCommentCount.setText(tid);
                                } else {
                                    txtCommentCount.setText("");
                                }

                                UrlImageViewHelper.setUrlDrawable(casting_image_only, myListImage.get(imagePos).imageRole, new UrlImageViewCallback() {

                                    @Override
                                    public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
                                        // TODO Auto-generated method stub
                                        progressView.setVisibility(View.GONE);
                                    }
                                });

                            } else {
                                Library.showToast(context, "Please check your network connection.");
                            }

                        } else {

                            progressView.setVisibility(View.GONE);
                            rel_upload_screen.setVisibility(View.GONE);
                            grid_view_photos.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.GONE);

                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_right);
                            rel_cast_view.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));
                            if (grid_view_photos.getVisibility() == View.VISIBLE) {
                                rel_upload_screen.setVisibility(View.GONE);
                            }

                            if (textCastivate.getVisibility() == View.VISIBLE) {
                                castingViewNoImage.setVisibility(View.VISIBLE);
                                textCastivate.setVisibility(View.GONE);
                                castingsList.setVisibility(View.GONE);
                                rel_upload_screen.setVisibility(View.GONE);
                                if (bSwipe.equals("left")) {
                                    position++;
                                }
                                if (bSwipe.equals("right")) {

                                }
                                bSwipe = "";

                                if (position < myList.size()) {

                                } else {

                                    page = 0;

                                    myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
                                    castViewStatus = false;
                                    position = 0;
                                    head = 0;
                                    tail = myList.size();

                                }
                                if (txtNocast.getVisibility() == View.VISIBLE) {

                                } else {
                                    setAllData(myList, position);
                                }
                            } else {

                                castViewStatusreak = false;
                                if (rel_image_screen.getVisibility() == View.VISIBLE) {
                                    rel_image_screen.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));
                                    castingViewNoImage.setVisibility(View.VISIBLE);
                                    rel_image_screen.setVisibility(View.GONE);
                                    rel_upload_screen.setVisibility(View.GONE);
                                    castingsList.setVisibility(View.GONE);
                                    castImageViewStatus = false;
                                    gridViewStatus = false;
                                    if (position == 5 || position == 10 || position == 15) {
                                        position--;
                                    }

                                    if (bSwipe.equals("left")) {
                                        position++;
                                    }

                                    if (position <= myList.size() - 1) {

                                        if (position > 19 && lastImageStatus == false) {
                                            castingViewNoImage.setVisibility(View.GONE);
                                            castingsList.setVisibility(View.GONE);
                                            CastingRightSwipeView();
                                            lastImageStatus = true;
                                        } else {

                                            if (position <= myList.size() - 1) {
                                                setAllData(myList, position);
                                            } else {

                                                CastingRightSwipeView();
                                            }
                                        }

                                    } else {

                                        CastingRightSwipeView();
                                    }

                                    swipe = "";
                                    castViewStatus = false;
                                } else if (position == 4 || position == 9 || position == 14 || position == 18 && castViewStatus == false) {
                                    progressView.setVisibility(View.GONE);
                                    castingViewNoImage.setVisibility(View.GONE);
                                    castingsList.setVisibility(View.GONE);
                                    rel_image_screen.setVisibility(View.VISIBLE);
                                    rel_upload_screen.setVisibility(View.VISIBLE);
                                    castImageViewStatus = true;
                                    gridViewStatus = false;
                                    bSwipe = "left";
                                    // if(position==saveImagePostionleft){
                                    // saveImagePostionleft= saveImagePostionleft+5;
                                    if (sequenceOrder == myListImage.size() - 1) {
                                        sequenceOrder = 0;
                                    } else {
                                        sequenceOrder++;

                                    }
                                    // }else{
                                    // sequenceOrder=saveImagePostionRight;
                                    // }

                                    if (myListImage.get(sequenceOrder).imageRole.toString().trim() != null) {

                                        String imgCasting = myListImage.get(sequenceOrder).imageRole.toString().trim();
                                        String cnt = myListImage.get(sequenceOrder).commentCount.toString().trim();
                                        if (!cnt.equals("0")) {
                                            txtCommentCount.setText(cnt);
                                        } else {
                                            txtCommentCount.setText("");
                                        }


                                        userName = myListImage.get(sequenceOrder).userName.toString().trim();
                                        imgFav.setChecked(myListImage.get(sequenceOrder).favImg);
                                        if (!userName.equals("")) {

                                            txtName.setVisibility(View.VISIBLE);
                                            txtName.setText(userName);

                                        } else {
                                            txtName.setVisibility(View.GONE);
                                        }
                                        imgCasting = imgCasting.replace("\"", "");
                                        DebugReportOnLocat.ln("image url::" + imgCasting);

                                        if (myListImage.get(sequenceOrder).favImg == true)
                                            fav_iconNoImage.setChecked(true);
                                        else
                                            fav_iconNoImage.setChecked(false);

                                        rel_upload_screen.setVisibility(View.VISIBLE);
                                        imgGrid.setVisibility(View.VISIBLE);
                                        local_talent.setVisibility(View.VISIBLE);
                                        casting_image.setVisibility(View.VISIBLE);
                                        grid_view_photos.setVisibility(View.GONE);
                                        swipeRefreshLayout.setVisibility(View.GONE);
                                        if (Network.isNetworkConnected(context)) {

                                            progressView.setVisibility(View.VISIBLE);
//										// fav_iconNoImage.setEnabled(false);
//										// imgFav.setEnabled(false);
//										Picasso.with(context).load(imgCasting.replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
//											@Override
//											public void onSuccess() {
//												progressView.setVisibility(View.GONE);
//												fav_iconNoImage.setEnabled(true);
//												imgFav.setEnabled(true);
//											}
//
//											@Override
//											public void onError() {
//												// TODO
//												// Auto-generated
//												// method stub
//
//											}
//										});


                                            UrlImageViewHelper.setUrlDrawable(casting_image, imgCasting, new UrlImageViewCallback() {

                                                @Override
                                                public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
                                                    // TODO Auto-generated method stub
                                                    progressView.setVisibility(View.GONE);
                                                }
                                            });

                                        } else {
                                            Library.showToast(context, "Please check your network connection.");

                                        }

                                    }

                                    castViewStatus = true;

                                    swipe = "right";

                                    int pos = position;
                                    DebugReportOnLocat.ln("pos " + pos);

                                } else {
                                    castViewStatus = false;

                                    swpe = true;
                                    if (txtNocast.getVisibility() == View.VISIBLE) {

                                    } else {
                                        CastingRightSwipeView();
                                    }

                                }
                            }
                        }

                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                        if (casting_image_only.getVisibility() == View.VISIBLE) {
                            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
                            casting_image_only.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));

                            imagePos--;
                            if (imagePos >= 0 && myListImage.size() > imagePos) {

                            } else {
                                imagePos = myListImage.size() - 1;
                            }
                            imgFav.setVisibility(View.VISIBLE);
                            CastingImagesModel model = myListImage.get(imagePos);
                            String getName = model.userName.toString().trim();
                            imgFav.setChecked(model.favImg);
                            imgFav.setEnabled(true);
                            txtName.setVisibility(View.VISIBLE);
                            if (getName.equals("")) {
                                txtName.setVisibility(View.GONE);
                            } else {
                                txtName.setText(getName);
                            }
                            if (Network.isNetworkConnected(context)) {
                                progressView.setVisibility(View.VISIBLE);
//							Picasso.with(context).load(myListImage.get(imagePos).imageRole).noFade().into(casting_image_only, new Callback() {
//								@Override
//								public void onSuccess() {
//									progressView.setVisibility(View.GONE);
//								}
//
//								@Override
//								public void onError() {
//									Library.showToast(context, "Image load error.");
//								}
//							});

                                UrlImageViewHelper.setUrlDrawable(casting_image_only, myListImage.get(imagePos).imageRole, new UrlImageViewCallback() {

                                    @Override
                                    public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
                                        // TODO Auto-generated method stub
                                        progressView.setVisibility(View.GONE);
                                    }
                                });

                            } else {
                                Library.showToast(context, "Please check your network connection.");
                            }

                        } else {

                            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
                            rel_cast_view.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));

                            rel_upload_screen.setVisibility(View.GONE);
                            grid_view_photos.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.GONE);

                            if (grid_view_photos.getVisibility() == View.VISIBLE) {
                                rel_upload_screen.setVisibility(View.GONE);
                            }

                            if (castivityScreen.getVisibility() == View.VISIBLE) {
                                rel_image_screen.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));
                                gear_icon.setVisibility(View.VISIBLE);
                                outbox_icon.setVisibility(View.VISIBLE);
                                gear_icon_mirror.setVisibility(View.INVISIBLE);
                                // if (listViewStatus)
                                // castingsListIconChange.setVisibility(View.VISIBLE);
                                // else
                                // castingsListIcon.setVisibility(View.VISIBLE);
                                if (castivityScreen.getVisibility() == View.VISIBLE) {
                                    castivityScreen.setVisibility(View.GONE);
                                }
                                // filterValue=1;
                                // list = new ArrayList<CastingDetailsModel>();
                                myList = new ArrayList<CastingDetailsModel>();

                                myList.clear();
                                page = 0;
                                bs = false;
                                position = 0;
                                castViewStatus = false;
                                castViewStatusreak = false;
                                swipe = "";
                                bSwipe = "";
                                swpe = true;

                                totalPages = 0;

                                CastivateApplication.getInstance().hashMap.clear();
                                tap_current_location.setVisibility(View.GONE);
                                castingViewNoImage.setVisibility(View.VISIBLE);
                                castingsList.setVisibility(View.GONE);
                                if (rel_image_screen.getVisibility() == View.VISIBLE) {
                                    rel_image_screen.setVisibility(View.GONE);
                                    rel_upload_screen.setVisibility(View.GONE);
                                    castImageViewStatus = false;
                                    gridViewStatus = false;
                                }
                                myList = new ArrayList<CastingDetailsModel>();
                                handlerCastingSync.sendEmptyMessage(0);
                            } else {
                                if (textCastivate.getVisibility() == View.VISIBLE) {
                                    castingViewNoImage.setVisibility(View.VISIBLE);
                                    textCastivate.setVisibility(View.GONE);
                                    castingsList.setVisibility(View.GONE);
                                    if (txtNocast.getVisibility() == View.VISIBLE) {
                                        rel_cast_view.setVisibility(View.GONE);
                                    }
                                    rel_upload_screen.setVisibility(View.GONE);

                                    if (bSwipe.equals("right")) {
                                        position--;
                                    }
                                    bSwipe = "";
                                    int pos = position;

                                    if (pos == -1) {
                                        if (page == 0) {
                                            page = totalPages;
                                            myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
                                            castViewStatus = false;
                                            position = myList.size() - 1;
                                            head = 0;
                                            tail = myList.size();

                                        } else {

                                            page = 0;

                                            myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
                                            castViewStatus = false;
                                            position = 0;
                                            head = 0;
                                            tail = myList.size();

                                        }
                                    }
                                    if (txtNocast.getVisibility() == View.VISIBLE) {

                                    } else {
                                        setAllData(myList, position);
                                    }


                                } else {
                                    if (rel_image_screen.getVisibility() == View.VISIBLE) {
                                        castingViewNoImage.setVisibility(View.VISIBLE);
                                        castingsList.setVisibility(View.GONE);
                                        rel_image_screen.setVisibility(View.GONE);
                                        rel_upload_screen.setVisibility(View.GONE);

                                        if (swipe.equals("left")) {
                                            position--;
                                        }
                                        if (swipe.equals("right")) {

                                        }
                                        castImageViewStatus = false;

                                        castViewStatus = false;
                                        setAllData(myList, position);

                                    } else if (position == 5 || position == 10 || position == 15 || position == 19 && castViewStatus == false) {

                                        // if(position==saveImagePostionRight){
                                        // saveImagePostionRight=
                                        // saveImagePostionRight+5;
                                        if (sequenceOrder == myListImage.size() - 1) {
                                            sequenceOrder = 0;
                                        } else {

                                            sequenceOrder++;

                                        }
                                        // }else{
                                        // sequenceOrder=saveImagePostionleft;
                                        //
                                        // }

                                        castingViewNoImage.setVisibility(View.GONE);
                                        castingsList.setVisibility(View.GONE);
                                        rel_image_screen.setVisibility(View.VISIBLE);
                                        rel_upload_screen.setVisibility(View.VISIBLE);
                                        castImageViewStatus = true;
                                        gridViewStatus = false;
                                        if (myListImage.get(sequenceOrder).imageRole.toString().trim() != null) {

                                            imgCasting = myListImage.get(sequenceOrder).imageRole.toString().trim();
                                            String cnt = myListImage.get(sequenceOrder).commentCount.toString().trim();
                                            if (!cnt.equals("0")) {
                                                txtCommentCount.setText(cnt);
                                            } else {
                                                txtCommentCount.setText("");
                                            }

                                            imgCasting = imgCasting.replace("\"", "");
                                            userName = myListImage.get(sequenceOrder).userName.toString().trim();
                                            imgFav.setChecked(myListImage.get(sequenceOrder).favImg);
                                            if (!userName.equals("")) {

                                                txtName.setVisibility(View.VISIBLE);
                                                txtName.setText(userName);

                                            } else {
                                                txtName.setVisibility(View.GONE);
                                            }
                                            if (myListImage.get(sequenceOrder).favImg == true)
                                                fav_iconNoImage.setChecked(true);
                                            else
                                                fav_iconNoImage.setChecked(false);

                                            DebugReportOnLocat.ln("image url::" + imgCasting);
                                            rel_upload_screen.setVisibility(View.VISIBLE);
                                            imgGrid.setVisibility(View.VISIBLE);
                                            local_talent.setVisibility(View.VISIBLE);
                                            casting_image.setVisibility(View.VISIBLE);
                                            grid_view_photos.setVisibility(View.GONE);
                                            swipeRefreshLayout.setVisibility(View.GONE);

                                            // download and display image from url
                                            if (Network.isNetworkConnected(context)) {

                                                progressView.setVisibility(View.VISIBLE);
//											// fav_iconNoImage.setEnabled(false);
//											// imgFav.setEnabled(false);
//											Picasso.with(context).load(imgCasting.replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
//												@Override
//												public void onSuccess() {
//													progressView.setVisibility(View.GONE);
//													fav_iconNoImage.setEnabled(true);
//													imgFav.setEnabled(true);
//												}
//
//												@Override
//												public void onError() {
//													// Library.showToast(context,
//													// "Something went wrong!");
//
//												}
//											});


                                                UrlImageViewHelper.setUrlDrawable(casting_image, imgCasting, new UrlImageViewCallback() {

                                                    @Override
                                                    public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
                                                        // TODO Auto-generated method stub
                                                        progressView.setVisibility(View.GONE);
                                                    }
                                                });

                                            } else {
                                                Library.showToast(context, "Please check your internet connection");
                                            }

                                        }

                                        swipe = "left";

                                        castViewStatus = true;
                                        int pos = position;
                                        DebugReportOnLocat.ln("pos " + pos);

                                    } else {

                                        castViewStatus = false;
                                        if (txtNocast.getVisibility() == View.VISIBLE) {
                                            rel_cast_view.setVisibility(View.GONE);
                                        } else {
                                            CastingLeftSwipeView();

                                            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // nothing
                    System.out.println("ee " + e);
                }
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    // Sugumaran Changes (25th May 16)
    class ImageGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_right);
                    casting_image_only.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));

                    imagePos++;
                    if (imagePos >= 0 && myListImage.size() > imagePos) {

                    } else {
                        imagePos = 0;
                    }
                    if (Network.isNetworkConnected(context)) {
                        progressView.setVisibility(View.VISIBLE);
//						Picasso.with(context).load(myListImage.get(imagePos).imageRole).noFade().into(casting_image_only, new Callback() {
//							@Override
//							public void onSuccess() {
//								progressView.setVisibility(View.GONE);
//							}
//
//							@Override
//							public void onError() {
//								Library.showToast(context, "Image load error.");
//							}
//						});

                        UrlImageViewHelper.setUrlDrawable(casting_image_only, myListImage.get(imagePos).imageRole, new UrlImageViewCallback() {

                            @Override
                            public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
                                // TODO Auto-generated method stub
                                progressView.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        Library.showToast(context, "Please check your network connection.");
                    }

                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
                    casting_image_only.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));

                    imagePos--;
                    if (imagePos >= 0 && myListImage.size() > imagePos) {

                    } else {
                        imagePos = myListImage.size() - 1;
                    }

                    // casting_image.setVisibility(View.GONE);
                    // casting_image_only.setVisibility(View.VISIBLE);
                    if (Network.isNetworkConnected(context)) {
                        progressView.setVisibility(View.VISIBLE);
//						Picasso.with(context).load(myListImage.get(imagePos).imageRole).noFade().into(casting_image_only, new Callback() {
//							@Override
//							public void onSuccess() {
//								progressView.setVisibility(View.GONE);
//							}
//
//							@Override
//							public void onError() {
//								Library.showToast(context, "Image load error.");
//							}
//						});

                        UrlImageViewHelper.setUrlDrawable(casting_image_only, myListImage.get(imagePos).imageRole, new UrlImageViewCallback() {

                            @Override
                            public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
                                // TODO Auto-generated method stub
                                progressView.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        Library.showToast(context, "Please check your network connection.");
                    }

                }
            } catch (Exception e) {
                System.out.println("ee " + e);
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    public class GetDatasNew extends AsyncTask<String, Void, Void> {

        String getAll = "";

        // ProgressDialog dd = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressBar = new ProgressDialog(context);
            progressBar.setMessage("Loading Castings...");
            progressBar.show();
            progressBar.setCancelable(false);
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub

            getAll = getJSONData();

            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            // TODO Auto-generated method stub
            // super.onPostExecute(dd);

            // if (dd != null)
            if (progressBar != null && progressBar.isShowing()) {
                progressBar.dismiss();

                if (getAll != null && !getAll.equals("[]") && !getAll.equals("")) {
                    rel_cast_view.setVisibility(View.VISIBLE);
                    castingViewNoImage.setVisibility(View.VISIBLE);
                    castingsList.setVisibility(View.GONE);
                    textCastivate.setVisibility(View.GONE);
                    myList = new ArrayList<CastingDetailsModel>();
                    castViewStatus = false;
                    try {
                        JSONArray jArray = new JSONArray(getAll);

                        CastingDetailsModel detailsModel;

                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
                            System.out.println("isNew----->" + json_data.getString("isNew"));
                            System.out.println("isNewString---->" + json_data.optInt("isNew"));

                            detailsModel = new CastingDetailsModel(json_data.getString("IdRole"), json_data.getString("casting_title"), json_data.getString("casting_type"),
                                    json_data.getString("casting_paid_status"), json_data.getString("casting_union_status"), json_data.getString("casting_union_type"),
                                    json_data.getString("casting_submission_detail"), json_data.getString("casting_synopsis"), json_data.getString("casting_image"),
                                    json_data.getString("role_desc"), json_data.getString("ageRange"), json_data.getString("role_for"), json_data.getString("role_ethnicity"),
                                    json_data.getString("fav_flag"), json_data.getString("fav_count"), "", json_data.getString("castingsTotal"),
                                    json_data.getString("casting_state"), json_data.getString("casting_city"), json_data.getString("casting_email"), json_data.getString("apply_flag"), json_data.optInt("abuse_flag"), json_data.optInt("casting_id"), json_data.getString("isNew"));

                            getTotalCastings = Integer.parseInt(json_data.getString("castingsTotal"));
                            favCount = json_data.getString("fav_count");
                            DebugReportOnLocat.ln("total::" + getTotalCastings);
                            if (getTotalCastings > 20) {
                                totalPages = getTotalCastings / 20;
                                // totalPages--;

                                DebugReportOnLocat.ln("totalPages::" + totalPages);
                            }
                            if (!favCount.equals("0")) {
                                // fav_icon.setVisibility(View.INVISIBLE);

                                favCountText.setVisibility(View.VISIBLE);
                                favCountText.setText(favCount);
                            } else {
                                // fav_icon.setVisibility(View.VISIBLE);
                                favCountText.setVisibility(View.INVISIBLE);
                            }

                            myList.add(detailsModel);

                        }

                        if (isPush == true) {
                            getPushNotificationCasting();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }

                } else {
                    DebugReportOnLocat.ln("[]");
                }
            }
        }
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        // ProgressDialog pd;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
            this.imageView.setImageBitmap(null);
        }

        @Override
        public void onPreExecute() {
            // pd = new ProgressDialog(context);
            // pd.setMessage("Loading...");
            // pd.setCancelable(false);
            // pd.show();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            // if (pd.isShowing()) {
            // pd.dismiss();
            // }
            imageView.setImageBitmap(result);
        }

    }

    public void reportMethod() {
        //   getRetro(myList.get(position).casting_id);
        if (Library.getId(context) != null && !Library.getId(context).equals("")) {

            final CastingDetailsModel model = myList.get(position);
            if (model != null && model.abuse_flag == 0) {

                getRetro(model.casting_id, position);

            } else {
                Library.alert(context, "This casting is already reported for abuse.");
            }
        } else {
            Toast.makeText(context, "Please Sign in and continue.", Toast.LENGTH_SHORT).show();
        }
    }


//    public String requestReportAbuse(String castingId) {
//        String result = "";
//        try {
//
//            HttpClient httpclient = new DefaultHttpClient();
//
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//
//            nameValuePairs.add(new BasicNameValuePair("userId", Library.getUserId(context)));
//            nameValuePairs.add(new BasicNameValuePair("casting_id", castingId));
//
//            String Urls = HttpUri.REPORT_ABUSE + URLEncodedUtils.format(nameValuePairs, "utf-8");
//            System.out.println("Casting URL Link :: " + Urls);
//            HttpGet httpget = new HttpGet(Urls);
//            DebugReportOnLocat.ln("filter casting values:" + URLEncodedUtils.format(nameValuePairs, "utf-8"));
//            HttpResponse response = httpclient.execute(httpget);
//            // Execute HTTP Get Request
//            if (response.getStatusLine().getStatusCode() == 200) {
//                result = EntityUtils.toString(response.getEntity());
//                DebugReportOnLocat.ln("result:::" + result);
//
//            } else {
//
//                return "";
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            DebugReportOnLocat.ln("Error : " + e.getMessage());
//        }
//
//        return result;
//    }

    // HTTP POST request
//    private void sendPost() throws Exception {
//
////		String url = "https://selfsolve.apple.com/wcResults.do";
//        String url = "http://php.twilightsoftwares.com:9998/castivateDev/castingNewVer1/public/reportAbuse";
//        URL obj = new URL(url);
//        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
//
//
////		http://php.twilightsoftwares.com:9998/castivateDev/castingNewVer1/public/reportAbuse?userId=28&casting_id=5141
//
//        //add reuqest header
//        con.setRequestMethod("POST");
////		con.setRequestProperty("User-Agent", USER_AGENT);
////		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//
//        String urlParameters = "userId=28&casting_id=5141";
//
//        // Send post request
//        con.setDoOutput(true);
//        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(urlParameters);
//        wr.flush();
//        wr.close();
//
//        int responseCode = con.getResponseCode();
//        System.out.println("\nSending 'POST' request to URL : " + url);
//        System.out.println("Post parameters : " + urlParameters);
//        System.out.println("Response Code : " + responseCode);
//
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//
//        //print result
//        System.out.println(response.toString());
//
//    }
    public void getRetro(final int castingId, final int pos) {

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading");
        pDialog.show();
        ServiceCall.UpdateReportAubuseBody updateReportAubuseBody = ServiceCall.initRestADapters().create(ServiceCall.UpdateReportAubuseBody.class);
        updateReportAubuseBody.createUser(new Users(Library.getUserId(context), String.valueOf(castingId)), new Callback<UsersRes>() {
            @Override
            public void success(UsersRes s, Response response) {
                System.out.println("success");
                pDialog.dismiss();
                Library.alert(context, "" + s.message);
                imgReportAbuse.setImageBitmap(null);
                imgReportAbuse.setImageResource(R.drawable.abuse_reported_button1);

                try {
                    myList.get(pos).abuse_flag = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error");
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        });

// by nijam


        castingsList.setLongClickable(true);
        castingsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, "Applied", Toast.LENGTH_LONG).show();

                return true;
            }
        });

//        ServiceCall.UpdateReportAubuse updateUser = ServiceCall.initRestADapters().create(ServiceCall.UpdateReportAubuse.class);
//        updateUser.update("28", "5141", new Callback<String>() {
//            @Override
//            public void success(String s, Response response) {
//                System.out.println("success");
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                System.out.println("error");
//            }
//        });
    }

    private void removeCastings(String castingId, final int position) {
        userId = Library.getUserId(context);
        RemoveCastingInput input = new RemoveCastingInput(userId, castingId);
        System.out.println("input1---->" + castingId);
        System.out.println("input1---->" + userId);

        RegisterRemoteApi.getInstance().setRemoveCastingInput(input);
        RegisterRemoteApi.getInstance().removeCasting(context, new Callback<RemoveCastingOutput>() {
            @Override
            public void success(RemoveCastingOutput removeCastingOutput, Response response) {
                System.out.println("removeCastingOutput.getMessage() = " + removeCastingOutput.getMessage());
                String message = removeCastingOutput.getMessage();
                System.out.println("List Size -- " + swipeList.size());
                alert(message, position);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(context, "failures", Toast.LENGTH_LONG).show();

            }
        });


    }

    private void alert(final String message, final int pos) {


        //  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.alert_common, null);
        alertDialog.setContentView(convertView);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView txtContent = (TextView) convertView.findViewById(R.id.txtContent);
        Button btnOk = (Button) convertView.findViewById(R.id.btnOk);
        txtContent.setText(message);

        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                /*String roleId = swipeList.get(pos).roleId;
                int castingsListArrPos = 0;

                for (int f = 0; f <= swipeList.size(); f++) {
                    if (castingsListArr.get(f).roleId.equals(roleId)) {
                        castingsListArrPos = f;
                    }
                }*/
                castingsListArr.remove(pos);
////                swipeList.remove(pos);
//                swipeViewListAdapter.notifyDataSetChanged();
                myList.clear();
                swipeList.clear();
                castingsListArr.clear();
                new GetDatas("3").execute();

            }
        });

        alertDialog.show();

    }

    ///////////////////////////////////
//    public static final int FROM_LIBRARY = 1010;
//    public static final int FROM_CAMERA = 1115;
//    public static final int CROP_PIC = 2;
//    MixpanelAPI mixpanel = null;
//    String profileImagePath;
//    int ACTION_REQUEST_CROP = 100;
//    private void initCamera(Context mContext) {
//
//        CropUtils.STORAGE availableStorage = CropUtils.getStorageWithFreeSpace(mContext);
//        String rootPath = CropUtils.getRootPath(mContext, availableStorage);
//
//        File folder = new File(rootPath);
//
//        if (!folder.isDirectory()) {
//            folder.mkdir();
//        }
//
//        File fileCamera = new File(CropUtils.getImagePath(mContext,
//                availableStorage, true));
//        profileImagePath = fileCamera.getPath();
//
//        if (!fileCamera.exists())
//            try {
//                fileCamera.createNewFile();
//            } catch (IOException e) {
//            }
//
//        Uri mImageUri = Uri.fromFile(fileCamera);
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
//        startActivityForResult(intent, FROM_CAMERA);
//    }
//    private String getRealPathFromURI(Uri contentURI) {
//        String result;
//        Cursor cursor = getContentResolver().query(contentURI, null, null,
//                null, null);
//        if (cursor == null) { // Source is Dropbox or other similar local file
//            // path
//            result = contentURI.getPath();
//        } else {
//            cursor.moveToFirst();
//            int idx = cursor
//                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            result = cursor.getString(idx);
//            cursor.close();
//        }
//        return result;
//    }
//
//    private Bitmap decodeFile(File f) {
//
//        Bitmap b = null;
//
//        // Decode image size
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(f);
//
//            BitmapFactory.decodeStream(fis, null, o);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//
//            try {
//
//                fis.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (NullPointerException ee) {
//                ee.printStackTrace();
//            }
//        }
//
//        int scale = 1;
//
//        if (f.length() > 8000) {
//
//            scale = 5;
//
//            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
//                scale = (int) Math.pow(
//                        2,
//                        (int) Math.ceil(Math.log(IMAGE_MAX_SIZE
//                                / (double) Math.max(o.outHeight, o.outWidth))
//                                / Math.log(0.5)));
//            }
//        }
//        // Decode with inSampleSize
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = scale;
//        try {
//            fis = new FileInputStream(f);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        b = BitmapFactory.decodeStream(fis, null, o2);
//        try {
//            fis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//        return b;
//    }


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


        if (requestCode == 99) {

            if (resultCode == Activity.RESULT_OK) {

                if (Library.getId(context).length() > 0) {
                    //txtLogin.setText("");//dsgrhseryhreyhswreyhsrewhj
                    //      txtLogin.setText("My Profile");//dsgrhseryhreyhswreyhsrewhj
//                        textlogout.setVisibility(View.VISIBLE);

                        /*vasanth you add this*/
                    txtLogin.setVisibility(View.GONE);
                    txtSignup.setVisibility(View.GONE);
                    userProfileImage.setVisibility(View.VISIBLE);
                    profileName.setVisibility(View.VISIBLE);

                    SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    String username = sharedpreferences.getString("username", "");
                    String profileImage = sharedpreferences.getString("profileImage", "");
                    profileName.setText(username);

                    profileName.setText(Html.fromHtml("<u>" + username + "</u>"));
                    if (!profileImage.equals(""))
                        Picasso.with(CastingScreen.this).load(profileImage).placeholder(R.drawable.avathar_profile)
                                .error(R.drawable.avathar_profile).into(userProfileImage);

                    String gender = sharedpreferences.getString("gender", "");
                    String uniontype = sharedpreferences.getString("uniontype", "");

                    if (gender.equals("1")) {
                        chkMale.setChecked(true);
                        chkMale.setTextColor(context.getResources().getColor(R.color.white));
                        chkFemale.setChecked(false);
                        chkFemale.setTextColor(Color.parseColor("#000000"));

                    } else {
                        chkFemale.setChecked(true);
                        chkFemale.setTextColor(context.getResources().getColor(R.color.white));
                        chkMale.setChecked(false);
                        chkMale.setTextColor(Color.parseColor("#000000"));
                    }

                    if (uniontype.equals("1")) {

                        chkUnion.setChecked(true);
                        chkUnion.setTextColor(context.getResources().getColor(R.color.white));
                        chkNonUnion.setChecked(false);
                        chkNonUnion.setTextColor(Color.parseColor("#000000"));


                    } else {
                        chkNonUnion.setChecked(true);
                        chkNonUnion.setTextColor(context.getResources().getColor(R.color.white));
                        chkUnion.setChecked(false);
                        chkUnion.setTextColor(Color.parseColor("#000000"));

                    }

                    birthYear.setText(sharedpreferences.getString("birthyear", ""));
                    txtEthnicity.setText(sharedpreferences.getString("ethincity", ""));
                    String city = sharedpreferences.getString("preferCities", "");
                    city = city.replaceAll("#", "");
                    editTextLocation.setText(city);
                    if (city.equals("")) {
                        clear.setVisibility(View.GONE);
                    } else {
                        clear.setVisibility(View.VISIBLE);
                    }
                    if (birthYear.equals("")) {
                        birthClear.setVisibility(View.GONE);
                    } else {
                        birthClear.setVisibility(View.VISIBLE);
                    }

                }
            } else {
                if (Library.getId(context).length() > 0) {
                    //txtLogin.setText("");//dsgrhseryhreyhswreyhsrewhj
                    //      txtLogin.setText("My Profile");//dsgrhseryhreyhswreyhsrewhj
//                        textlogout.setVisibility(View.VISIBLE);

                        /*vasanth you add this*/
                    txtLogin.setVisibility(View.GONE);
                    txtSignup.setVisibility(View.GONE);
                    userProfileImage.setVisibility(View.VISIBLE);
                    profileName.setVisibility(View.VISIBLE);

                    SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    String username = sharedpreferences.getString("username", "");
                    String profileImage = sharedpreferences.getString("profileImage", "");
                    profileName.setText(username);

                    profileName.setText(Html.fromHtml("<u>" + username + "</u>"));
                    if (!profileImage.equals(""))
                        Picasso.with(CastingScreen.this).load(profileImage).placeholder(R.drawable.avathar_profile)
                                .error(R.drawable.avathar_profile).into(userProfileImage);

                    String gender = sharedpreferences.getString("gender", "");
                    String uniontype = sharedpreferences.getString("uniontype", "");

                    if (gender.equals("1")) {
                        chkMale.setChecked(true);
                        chkMale.setTextColor(context.getResources().getColor(R.color.white));
                        chkFemale.setChecked(false);
                        chkFemale.setTextColor(Color.parseColor("#000000"));

                    } else {
                        chkFemale.setChecked(true);
                        chkFemale.setTextColor(context.getResources().getColor(R.color.white));
                        chkMale.setChecked(false);
                        chkMale.setTextColor(Color.parseColor("#000000"));
                    }

                    if (uniontype.equals("1")) {
                        chkUnion.setChecked(true);
                        chkUnion.setTextColor(context.getResources().getColor(R.color.white));
                        chkNonUnion.setChecked(false);
                        chkNonUnion.setTextColor(Color.parseColor("#000000"));
                    } else {
                        chkNonUnion.setChecked(true);
                        chkNonUnion.setTextColor(context.getResources().getColor(R.color.white));
                        chkUnion.setChecked(false);
                        chkUnion.setTextColor(Color.parseColor("#000000"));

                    }

                    birthYear.setText(sharedpreferences.getString("birthyear", ""));
                    txtEthnicity.setText(sharedpreferences.getString("ethincity", ""));
                    String city = sharedpreferences.getString("preferCities", "");
                    city = city.replaceAll("#", "");
                    editTextLocation.setText(city);
                    if (city.equals("")) {
                        clear.setVisibility(View.GONE);
                    } else {
                        clear.setVisibility(View.VISIBLE);
                    }
                    if (birthYear.equals("")) {
                        birthClear.setVisibility(View.GONE);
                    } else {
                        birthClear.setVisibility(View.VISIBLE);
                    }
                }
            }
            tap_current_location.setVisibility(View.GONE);

        }
        if (requestCode == 90) {
            if (resultCode == Activity.RESULT_OK) {
                if (Library.getId(context).length() > 0) {
                    notify_status = sharedPreference.getString("new_notification", "");

                    if (notify_status.equals("1")) {
                        notification_toggle.setChecked(true);
                    } else {
                        notification_toggle.setChecked(false);
                    }

                }
            }
        }

        if (requestCode == 888) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    myList.get(Library.currentPos).applyFlag = "1";
                    castingsListArr.get(Library.currentPos).applyFlag = "1";

                    layBottom.setVisibility(View.VISIBLE);
                    btn_apply.setText("Applied");
                    imgTick.setVisibility(View.VISIBLE);
                    layBottom.setBackgroundColor(getResources().getColor(R.color.green));
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
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

        if (requestCode == FROM_CAMERA && resultCode == Activity.RESULT_OK) {

            if (profileImagePath != null && !profileImagePath.equals("")) {
                startActivityForResult(new Intent(context,
                        CropperImageActivity.class).putExtra("Path",
                        profileImagePath), ACTION_REQUEST_CROP);

            }

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

        } else if (requestCode == 1200 && resultCode == RESULT_OK) {
            myList.get(Library.currentPos).applyFlag = "1";
            castingsListArr.get(Library.currentPos).applyFlag = "1";

            layBottom.setVisibility(View.VISIBLE);
            btn_apply.setText("Applied");
            imgTick.setVisibility(View.VISIBLE);
            layBottom.setBackgroundColor(getResources().getColor(R.color.green));

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

    private void ApplyCastingRetrofit() {
        // TODO Auto-generated method stub

        // Progress Dialog
        pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        // Send the userName,password
        if (encodedImage == null) {
            encodedImage = "";
        }

        SharedPreferences prefs = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);

        ApplyCastingInput input = new ApplyCastingInput(Library.getUserId(context),
                Library.role_id, Library.enthicity, Library.age_range, Library.gender);

        RegisterRemoteApi.getInstance().setApplyCastingInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().getApplyCasting(context, new Callback<ApplyCastingOutput>() {
            @Override
            public void success(ApplyCastingOutput changePassResponse, Response response) {
                closeProgress();

                if (changePassResponse.status == 200) {

                    myList.get(Library.currentPos).applyFlag = "1";
                    castingsListArr.get(Library.currentPos).applyFlag = "1";

                    layBottom.setVisibility(View.VISIBLE);
                    btn_apply.setText("Applied");
                    imgTick.setVisibility(View.VISIBLE);
                    layBottom.setBackgroundColor(getResources().getColor(R.color.green));

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


    private void getImageVideoList() {
        // TODO Auto-generated method stub


        ProfileinfoInput input = new ProfileinfoInput(Library.getUserId(context));

        RegisterRemoteApi.getInstance().setProfileinfoInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().postnewPersonProfile(context, new Callback<NewProfileinfoOutput>() {
            @Override
            public void success(NewProfileinfoOutput profileinfoOutput, Response response) {
                //     closeProgress();

                if (profileinfoOutput.status == 200) {
                    int videoCount = profileinfoOutput.videos.size();
                    int imageCount = profileinfoOutput.images.size();
                    int resumeCount = profileinfoOutput.documents.size();
                    int voiceCount = profileinfoOutput.voice.size();
                    int totalCount = videoCount + imageCount;

                    SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("imageCount", imageCount);
                    editor.putInt("videoCount", videoCount);
                    editor.putInt("resumeCount", resumeCount);
                    editor.putInt("audioCount", voiceCount);
                    editor.putInt("totalCount", totalCount);

                    editor.commit();


                } /*else {
                    Library.alert(context, profileinfoOutput.message);

                }*/

            }

            @Override
            public void failure(RetrofitError error) {
                //       closeProgress();

            }
        });
    }


    private void MatchedCastingRetrofit(final int castingId) {
        // TODO Auto-generated method stub

        // Progress Dialog
        pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        MatchedCastingInput input = new MatchedCastingInput(Library.getUserId(context), castingId + "");

        RegisterRemoteApi.getInstance().setMatchedCastingInput(input);

        // Call Login JSON
        RegisterRemoteApi.getInstance().matchedCasting(context, new Callback<MatchedCastingOutput>() {
            @Override
            public void success(MatchedCastingOutput changePassResponse, Response response) {
                closeProgress();
                System.out.println("response--->" + response.toString());

                SharedPreferences sharedpreferences = context.getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                int imageCount = sharedpreferences.getInt("imageCount", 0);
                int videoCount = sharedpreferences.getInt("videoCount", 0);
                int resumeCount = sharedpreferences.getInt("resumeCount", 0);
                int audioCount = sharedpreferences.getInt("audioCount", 0);
//
                if (changePassResponse.getCode().equals("200") || changePassResponse.getCode().equals("100")) {
                    if (changePassResponse.getMessage().contains("not")) {


                        if (changePassResponse.getCastingList().getSpecial_recruitment().equals("1")) {
                            Library.videoCount = changePassResponse.getCastingList().getVideo();
                            Library.voiceCount = changePassResponse.getCastingList().getVoice();
                            Library.photoBack = changePassResponse.getCastingList().getPhoto_backview();
                            Library.photoFront = changePassResponse.getCastingList().getPhoto_frontview();
                            Library.photoSide = changePassResponse.getCastingList().getPhoto_sideview();
                            Library.specialRecruitment = "special";

                       /* countValue = new ArrayList<Integer>();
                        countValue.add(0, Library.photoBack);
                        countValue.add(1, Library.photoFront);
                        countValue.add(2, Library.photoSide);
                        countValue.add(3, Library.voiceCount);
                        countValue.add(4, Library.videoCount);*/

                            if (imageCount != 0 && videoCount != 0 && resumeCount != 0 && audioCount != 0) {
                                Intent i = new Intent(CastingScreen.this, NewCastingApplyScreen.class);
                                i.putExtra("castingid", castingId + "");
                                startActivityForResult(i, 1200);

                            } else {
                                //   alertdialog();
                                Intent i = new Intent(CastingScreen.this, PhotosVideoFileActivity.class);
                                startActivityForResult(i, 99);
                            }
                        } else {

                            Intent i = new Intent(CastingScreen.this, ProfileNotMatching.class);
                            startActivityForResult(i, 888);

                        }
                    } else if (changePassResponse.getCastingList().getSpecial_recruitment().equals("1")) {
                        Library.videoCount = changePassResponse.getCastingList().getVideo();
                        Library.voiceCount = changePassResponse.getCastingList().getVoice();
                        Library.photoBack = changePassResponse.getCastingList().getPhoto_backview();
                        Library.photoFront = changePassResponse.getCastingList().getPhoto_frontview();
                        Library.photoSide = changePassResponse.getCastingList().getPhoto_sideview();
                        Library.specialRecruitment = "special";


                        if (imageCount != 0 && videoCount != 0 && resumeCount != 0 && audioCount != 0) {
                            Intent i = new Intent(CastingScreen.this, NewCastingApplyScreen.class);
                            i.putExtra("castingid", castingId + "");
                            startActivityForResult(i, 1200);

                        } else {
                            Intent i = new Intent(CastingScreen.this, PhotosVideoFileActivity.class);
                            startActivityForResult(i, 99);
                            // alertdialog();
                        }
                    } else if (changePassResponse.getCastingList().getSpecial_recruitment().equals("0")) {
                        Library.videoCount = changePassResponse.getCastingList().getVideo();
                        Library.voiceCount = changePassResponse.getCastingList().getVoice();
                        Library.photoBack = changePassResponse.getCastingList().getPhoto_backview();
                        Library.photoFront = changePassResponse.getCastingList().getPhoto_frontview();
                        Library.photoSide = changePassResponse.getCastingList().getPhoto_sideview();


                        if (imageCount != 0 && videoCount != 0 && resumeCount != 0 && audioCount != 0) {
                           /* Intent i = new Intent(CastingScreen.this, NewCastingApplyScreen.class);
                            startActivityForResult(i, 1200);*/
                            ApplyCastingRetrofit();
                        } else {
                            //  alertdialog();
                            Intent i = new Intent(CastingScreen.this, PhotosVideoFileActivity.class);
                            startActivityForResult(i, 99);
                        }
                    } else {

                        Library.alert(context, changePassResponse.getMessage());


                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                closeProgress();
//                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void alertdialog() {

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
        txtContent.setText("Please subscribe to apply for castings from within the app");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(CastingScreen.this, CastingPlan.class);
                intent.putExtra("CalledBy", "Casting");
                intent.putExtra("FromCasting", true);
                startActivity(intent);
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
       /* dialogCamera = new Dialog(context);
        dialogCamera.setCancelable(false);
        dialogCamera.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCamera.setContentView(R.layout.custom_alertdialog);
        // set the custom dialog components - title and content
        TextView alertHead = (TextView) dialogCamera.findViewById(R.id.custom_alertdialog_tv_alerthead);
        TextView alertContent = (TextView) dialogCamera.findViewById(R.id.custom_alertdialog_tv_alertcontent);
        alertContent.setText("Please subscribe to apply for castings from within the app");
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

                *//*Intent i = new Intent(CastingScreen.this, PhotosVideoFileActivity.class);
                startActivityForResult(i, 99);*//*
                Intent intent = new Intent(CastingScreen.this, CastingPlan.class);
                intent.putExtra("CalledBy", "Casting");
                intent.putExtra("FromCasting", true);
                startActivity(intent);
            }
        });
        dialogCamera.show();*/
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

                SharedPreferences sharedPreference = getSharedPreferences(Library.UserPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreference.edit();
                editor.putString("new_notification", "0");
                editor.commit();

                notification_toggle.setChecked(false);
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

    private void submitComment(int talentId, String name, String email, String comment) {
        // Progress Dialog
        pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        ThankCommentInput input = new ThankCommentInput(talentId + "", 0 + "", talentId + "", email, name, Library.getUserId(context), comment, Library.getDeviceID(context));
        RegisterRemoteApi.getInstance().setThankCommentInput(input);
        RegisterRemoteApi.getInstance().submitComments(context, new Callback<ThankCommentOutput>() {
            @Override
            public void success(ThankCommentOutput thankCommentOutput, Response response) {
                closeProgress();
            }

            @Override
            public void failure(RetrofitError error) {
                closeProgress();
            }
        });

    }

}