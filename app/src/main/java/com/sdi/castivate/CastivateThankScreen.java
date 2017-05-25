package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.sdi.castivate.model.MatchedCastingInput;
import com.sdi.castivate.model.ThankCommentInput;
import com.sdi.castivate.model.ThankCommentOutput;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.RegisterRemoteApi;

import java.io.File;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nijamudhin on 4/17/2017.
 */

public class CastivateThankScreen extends Activity {
    EditText et_name, et_email, et_comments;
    String nameValue, emailValue, commentsValue, photo = "";
    Button submit;
    LinearLayout layBack;
    ProgressDialog pDialog;
    Context context;
    int talentId;
    ImageView fb_share, twitter_share;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    Bitmap b;

   /* private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            //   displayMessage(profile);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.castivate_thank);
        context = this;
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(context);
        shareDialog = new ShareDialog(this);
//        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
//            @Override
//            public void onSuccess(Sharer.Result result) {
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });


        layBack = (LinearLayout) findViewById(R.id.casting_login_back_icon);
        et_name = (EditText) findViewById(R.id.thanking_name);
        et_email = (EditText) findViewById(R.id.thanking_email);
        et_comments = (EditText) findViewById(R.id.thanking_comments);
        submit = (Button) findViewById(R.id.btnChangePass);
        fb_share = (ImageView) findViewById(R.id.fb_share);
        twitter_share = (ImageView) findViewById(R.id.twitter_share);



     /*   if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .build();
        Bitmap image = ((BitmapDrawable) getResources().getDrawable(R.drawable.taxi_icon)).getBitmap();
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent contents = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareLinkContent contentData = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com")).setContentDescription(IConstant.PLAYSTORE_LINK)
                .build();*/


        try {
            talentId = getIntent().getIntExtra("talentid", 0);
            photo = getIntent().getStringExtra("photo");

            b = decodeFile(photo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        layBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        fb_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  File f = new File(photo);
               /* if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentTitle("A casting from Castivate!")
                            .setContentDescription(
                                    "Hey, Check out this awesome casting on Castivate! Click the URL below:  http://www.castivate.com/get")
                            .setContentUrl(Uri.parse(photo))

                            //  .setImageUrl(uri)
                            .build();
                    shareDialog.show(content);
                }*/
                ShareDialog(b);


/*
                Intent chooser = new Intent(Intent.ACTION_SEND);

                chooser.setType("image*//*");

                chooser.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                chooser.putExtra(Intent.EXTRA_TEXT, "Hey, Check out this awesome casting on Castivate! Click the URL below:  http://www.castivate.com/get");
                chooser.putExtra(Intent.EXTRA_SUBJECT, "A casting from Castivate!");

                chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(chooser);*/
            }
        });

        twitter_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent chooser = new Intent(Intent.ACTION_SEND);

                chooser.setType("image*//*");
                File f = new File(photo);
                chooser.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                chooser.putExtra(Intent.EXTRA_TEXT, "Hey, Check out this awesome casting on Castivate! Click the URL below:  http://www.castivate.com/get");
                chooser.putExtra(Intent.EXTRA_SUBJECT, "A casting from Castivate!");

                chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(chooser);*/
                KnownIssueFBandTwiter();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameValue = et_name.getText().toString();
                emailValue = et_email.getText().toString();
                commentsValue = et_comments.getText().toString();
                if (nameValue.equals("")) {
                    Library.alert(context, "Please enter the name");
                } else if (!Library.emailValidation(emailValue)) {
                    Library.alert(context, "Please enter valid email");
                } else {
                    submitComment(nameValue, emailValue, commentsValue);
                }


            }
        });


    }

    public void ShareDialog(Bitmap imagePath) {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(imagePath)
                .setCaption("Castivate link")
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        shareDialog.show(this, content);
    }

    public static Bitmap decodeFile(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inPreferQualityOverSpeed = true;

        return BitmapFactory.decodeFile(photoPath, options);
    }

    private void submitComment(String name, String email, String comment) {
        // Progress Dialog
        pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        ThankCommentInput input = new ThankCommentInput(talentId + "", 1 + "", talentId + "", email, name, 0 + "", comment, Library.getDeviceID(context));
        RegisterRemoteApi.getInstance().setThankCommentInput(input);
        RegisterRemoteApi.getInstance().submitComments(context, new Callback<ThankCommentOutput>() {
            @Override
            public void success(ThankCommentOutput thankCommentOutput, Response response) {
                closeProgress();
                if (thankCommentOutput.addLikes != null) {
                    if (thankCommentOutput.addLikes.status.equals("100")) {

                        CastingScreen.isMoveLike = true;
                        String msg = thankCommentOutput.addLikes.message;
                        Intent in = new Intent(CastivateThankScreen.this, CommentsSubmitted.class);
                        in.putExtra("msg", msg);
                        startActivity(in);
                        finish();
                    }else if(thankCommentOutput.addLikes.status.equals("201")){
                        CastingScreen.isMoveLike = true;
                        Intent in = new Intent(CastivateThankScreen.this, CastivateThoughtScreen.class);
                        in.putExtra("photo", photo);
                        startActivity(in);
                        finish();
                    }else{
                        CastingScreen.isMoveLike = false;

                    }
                } else {
                    CastingScreen.isMoveLike = false;

                }
            }

            @Override
            public void failure(RetrofitError error) {

                CastingScreen.isMoveLike = false;

                closeProgress();
            }
        });

    }

    private void KnownIssueFBandTwiter() {
    /*    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image*//*");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        Uri uri = Uri.parse("android.resource://com.taxi_cab.app/drawable/taxi_icon");
        String extraString = "TaxiCab App: Start getting 20% OFF on every taxi cab ride in bay area just by downloading taxi app." + IConstant.PLAYSTORE_LINK;
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.taxicab.app&hl=en");
        //    shareIntent.putExtra(Intent.EXTRA_TEXT, Uri.parse("market://details?id=example.package"));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        //  shareIntent.putExtra(Intent.EXTRA_TITLE, extraString);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "app name");
        startActivity(Intent.createChooser(shareIntent, "Share via"));*/


        try {
            File file = new File(photo);
            Intent tweetIntent = new Intent(Intent.ACTION_SEND);
            tweetIntent.putExtra(Intent.EXTRA_TEXT, "Hey, Check out this awesome casting on Castivate! Click the URL below:  http://www.castivate.com/get");
            tweetIntent.putExtra(Intent.EXTRA_SUBJECT, "A casting from Castivate!");
            tweetIntent.setType("image/*");
            tweetIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

            PackageManager packManager = context.getPackageManager();
            List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

            boolean resolved = false;
            for (ResolveInfo resolveInfo : resolvedInfoList) {
                if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                    tweetIntent.setClassName(
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name);
                    resolved = true;
                    break;
                }
            }
            if (resolved) {
                startActivity(tweetIntent);
            } else {
                File f = new File(photo);
                Intent i = new Intent();
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_TEXT, "Hey, Check out this awesome casting on Castivate! Click the URL below:  http://www.castivate.com/get");
                i.putExtra(Intent.EXTRA_SUBJECT, "A casting from Castivate!");
                i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                i.setAction(Intent.ACTION_VIEW);
                // i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + IConstant.PLAYSTORE_LINK));
                startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void closeProgress() {
        // TODO Auto-generated method stub
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
