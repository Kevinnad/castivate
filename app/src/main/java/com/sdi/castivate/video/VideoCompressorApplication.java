package com.sdi.castivate.video;/*
* By Jorge E. Hernandez (@lalongooo) 2015
* */

import android.app.Application;

import com.sdi.castivate.file.FileUtilsCompressor;


public class VideoCompressorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtilsCompressor.createApplicationFolder();
    }

}