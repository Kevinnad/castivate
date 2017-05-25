package com.sdi.castivate;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

import java.util.EnumSet;

/**
 * Created by nijamudhin on 4/20/2017.
 */

public class CommentsSubmitted extends Activity {
    TextView thank_Message;
    String message = "", photo = "";
    LinearLayout casting_login_back_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.castivate_thank_submit);
        thank_Message = (TextView) findViewById(R.id.thank_msg);
        casting_login_back_icon = (LinearLayout) findViewById(R.id.casting_login_back_icon);

        try {
            message = getIntent().getStringExtra("msg");

            String input = message;
            LinkExtractor linkExtractor = LinkExtractor.builder()
                    .linkTypes(EnumSet.of(LinkType.EMAIL))
                    .build();
            Iterable<LinkSpan> links = linkExtractor.extractLinks(input);
            LinkSpan link = links.iterator().next();
            link.getType();
            link.getBeginIndex();
            link.getEndIndex();

            String matt = input.substring(link.getBeginIndex(), link.getEndIndex());
            String mat = " <font color=#c70209>" + input.substring(link.getBeginIndex(), link.getEndIndex()) + "</font>";

            String input1 = input.replace(matt, mat);
            thank_Message.setText(Html.fromHtml(input1));

           /* String space = (String) thank_Message.getTag();
            String text = thank_Message.getText().toString();
            thank_Message.setText(text.replace(space, (space += " ")));
            thank_Message.setTag(space);*/


        } catch (Exception e) {
            e.printStackTrace();
        }


        casting_login_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
