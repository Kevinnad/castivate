package com.sdi.castivate.fonts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by nijamudhin on 5/4/2017.
 */

public class LatoRegular extends TextView {

    Context context;

    public LatoRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inite();
    }

    public LatoRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inite();
    }

    public LatoRegular(Context context) {
        super(context);
        this.context = context;
        inite();
    }

    private void inite() {
        if (!isInEditMode()) {
            // Typeface face = Typeface.createFromAsset(context.getAssets(),
            // "Bariol_Regular.otf");
            // this.setTypeface(face);

            this.setTypeface(TypeFaceProvider
                    .get(context, "fonts/Lato-Regular_0.ttf"));

        }
    }
}


