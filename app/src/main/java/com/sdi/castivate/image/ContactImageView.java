package com.sdi.castivate.image;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by androidusr1 on 3/1/17.
 */
public class ContactImageView extends ImageView {

    public ContactImageView(Context context) {
        super(context);
    }

    public ContactImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public int position;
}
