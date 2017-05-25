package com.sdi.castivate;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by nijamudhin on 5/9/2017.
 */

public class WebPreview extends Activity {
    WebView mywebview;
    String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_preview);
        mywebview = (WebView) findViewById(R.id.webView1);
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.setWebViewClient(new myWebClient());

        try {
            path = getIntent().getStringExtra("image");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mywebview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + path);
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return false;

        }
    }
}
