package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.vision.text.Text;
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
 * Created by nijamudhin on 4/19/2017.
 */

public class CastivateThankScreenSigned extends Activity {
    EditText et_comments;
    String nameValue, emailValue, commentsValue, photo = "";
    Button submit;
    LinearLayout layBack;
    ProgressDialog pDialog;
    Context context;
    SharedPreferences preferences;
    int talentId;
    TextView et_name;
    ImageView fbShare, twitterShare;
    Bitmap b;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.castivate_thank_like);
        context = this;
        FacebookSdk.sdkInitialize(context);
        shareDialog = new ShareDialog(this);

        preferences = getSharedPreferences(Library.UserPREFERENCES, MODE_PRIVATE);
        nameValue = preferences.getString("username", "");
        emailValue = preferences.getString("email", "");

        et_name = (TextView) findViewById(R.id.thank_userName);
        layBack = (LinearLayout) findViewById(R.id.casting_login_back_icon);
        et_comments = (EditText) findViewById(R.id.thank_Comment);
        submit = (Button) findViewById(R.id.btnChangePass);
        fbShare = (ImageView) findViewById(R.id.fb_share1);
        twitterShare = (ImageView) findViewById(R.id.twitter_share1);

        et_name.setText("Hi" + " " + nameValue + ",");
        talentId = getIntent().getIntExtra("talentid", 0);
        photo = getIntent().getStringExtra("photo");
        b = decodeFile(photo);
        layBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CastingScreen.isMoveLike = false;
                finish();
            }
        });

        fbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog(b);
            }
        });

        twitterShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KnownIssueFBandTwiter();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameValue = et_name.getText().toString();
                commentsValue = et_comments.getText().toString();
                if (nameValue.equals("")) {
//                    Library.alert(context, "Please enter the name");
                } else {
                    submitComment(talentId, nameValue, emailValue, commentsValue);
                }


            }
        });


    }

    private void submitComment(int talentId, String name, String email, String comment) {
        // Progress Dialog
        pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        ThankCommentInput input = new ThankCommentInput(talentId + "", 1 + "", talentId + "", email, name, Library.getUserId(context), comment, Library.getDeviceID(context));
        RegisterRemoteApi.getInstance().setThankCommentInput(input);
        RegisterRemoteApi.getInstance().submitComments(context, new Callback<ThankCommentOutput>() {
            @Override
            public void success(ThankCommentOutput thankCommentOutput, Response response) {
                closeProgress();
                if (thankCommentOutput.addLikes != null) {
                    if (thankCommentOutput.addLikes.status.equals("201") || thankCommentOutput.addLikes.status.equals("200")) {
                        CastingScreen.isMoveLike = true;
                        Intent in = new Intent(CastivateThankScreenSigned.this, CastivateThoughtScreen.class);
                        in.putExtra("photo", photo);
                        startActivity(in);
                        finish();
                    } else {
                        CastingScreen.isMoveLike = false;

                    }
                } else {
                    CastingScreen.isMoveLike = false;
                }

            }

            @Override
            public void failure(RetrofitError error) {
                closeProgress();
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
        shareDialog.show(content);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CastingScreen.isMoveLike = false;
    }
}
