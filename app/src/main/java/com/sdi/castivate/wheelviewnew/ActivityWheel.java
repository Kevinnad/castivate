package com.sdi.castivate.wheelviewnew;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.sdi.castivate.R;
import com.sdi.castivate.utils.Library;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by androidusr1 on 13/10/16.
 */
public class ActivityWheel extends Activity {

    public Handler handler1 = new Handler();
    List<String> years = new ArrayList<String>();
    Runnable r = new Runnable() {
        public void run() {

            System.out.println("Scrooled Position" + Library.birthSelectedPos);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wheel);

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1900; i--) {
            years.add(Integer.toString(i));
        }

        WheelViewNew wheelTime = (WheelViewNew) findViewById(R.id.wheelTime);

        wheelTime.setOffset(2);
        wheelTime.setItems(years);
        wheelTime.setOnWheelViewListener(new WheelViewNew.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {

//                Toast.makeText(PickTime.this, item.toString(), Toast.LENGTH_LONG).show();

                handler1.removeCallbacks(r);
                Library.birthSelectedPos = item;
                handler1.postDelayed(r, 1000);
            }
        });
    }

}
