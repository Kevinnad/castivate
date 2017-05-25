package com.sdi.castivate.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sdi.castivate.R;
import com.sdi.castivate.image.ContactImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by nijamudhin on 1/13/2017.
 */

public class ImagePreview extends Activity {
    ContactImageView imageView;
    Button btnclose;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_preview);
        imageView = (ContactImageView) findViewById(R.id.imagePreview);
        btnclose = (Button) findViewById(R.id.btnclose);
        context = this;

        String path = getIntent().getStringExtra("image");
        Picasso.with(context).load(path).error(R.drawable.avathar_profile).into(imageView);
     //   new com.sdi.castivate.image.ImageDownload(context, imageView, null, 0).execute(path);

        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
