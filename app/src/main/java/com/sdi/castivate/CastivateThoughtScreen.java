package com.sdi.castivate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.util.List;

/**
 * Created by nijamudhin on 4/19/2017.
 */

public class CastivateThoughtScreen extends Activity {
    LinearLayout casting_login_back_icon;
    String photo = "";
    ImageView fbShare, twitterShare;
    Context context;
    ShareDialog shareDialog;
    Bitmap b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.castivate_thank_thoughts);
        context = this;
        FacebookSdk.sdkInitialize(context);
        shareDialog = new ShareDialog(this);

        fbShare = (ImageView) findViewById(R.id.fb_share);
        twitterShare = (ImageView) findViewById(R.id.tw_share);
        casting_login_back_icon = (LinearLayout) findViewById(R.id.casting_login_back_icon);

        photo = getIntent().getStringExtra("photo");
        b = decodeFile(photo);

        casting_login_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
