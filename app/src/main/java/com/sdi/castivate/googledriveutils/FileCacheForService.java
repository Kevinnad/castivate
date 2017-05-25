package com.sdi.castivate.googledriveutils;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FileCacheForService {

    final Long MAX_FILE_AGE = 172800000L;//2 days  in milliseconds
    private File cacheDir;

    public FileCacheForService(Context context) {


        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

            String url = android.os.Environment.getExternalStorageDirectory().toString();

            cacheDir = new File(url, "Casting_Folder");
        } else
            cacheDir = context.getCacheDir();

        if (!cacheDir.exists())
            cacheDir.mkdirs();

    }

    /**
     * @author Gnanaoly
     ***********/

// Using HTTP_NOT_MODIFIED
    public static boolean Changed(String url) {
        try {
     /* HttpURLConnection.setFollowRedirects(false);
      HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
      con.setRequestMethod("GET");*/


            return (true);
        } catch (Exception e) {
            //DebugReportOnLocat.printException(e);;
            return false;
        }
    }

    /**
     * GET THE LAST MODIFIED TIME
     *
     * @param url file location url
     */
    public static long getTheLastModifiedDateTime(String url) {
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
        } catch (MalformedURLException e) {

            //DebugReportOnLocat.printException(e);;
        } catch (IOException e) {

            //DebugReportOnLocat.printException(e);;
        }
        long date = con.getLastModified();

        if (date == 0) {
            //if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln("File last modified information not found.");
        } else {
            //if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln("File last Modified information: " + new Date(date));
        }
        return date;
    }

    public static String getPath() {

        String url = android.os.Environment.getExternalStorageDirectory().toString();

        return url + "/CastivateFile";
    }

    public File getCurrentCatchFolder() {


        return cacheDir;

    }

    public File getDirectory(String folderName) {

        File f = new File(cacheDir, folderName);

        if (!f.exists())
            f.mkdirs();


        return f;

    }

    public File getFile(String fileName) {


        File f2 = new File(cacheDir, fileName);

        return f2;

    }

    public File getFile(String folderName, String fileName) {

        File f = new File(cacheDir, folderName);

        if (!f.exists())
            f.mkdirs();

        File f2 = new File(f, fileName);

        return f2;

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

    /**
     * File clean up for each app start up
     **/
    public void deleteOldFile(Context context) {


        // Get file handle to the directory. In this case the application files dir
        File dir = cacheDir;


        // listFiles() returns a list of File objects to each file found.
        File[] files = dir.listFiles();

        // Loop through all files
        for (File f : files) {

            // Get the last modified date. Miliseconds since 1970
            Long lastmodified = f.lastModified();

            // Do stuff here to deal with the file..
            // For instance delete files older than 1 month
            if (lastmodified + MAX_FILE_AGE < System.currentTimeMillis()) {
                f.delete();
            }
        }
    }

    public void DeleteFile(File fileOrDirectory) {

        try {


            System.err.println(" File delete " + fileOrDirectory.toString());
            fileOrDirectory.delete();


        } catch (Exception e) {
//		DebugReportOnLocat.e(e);
        }
    }

    public void DeleteAllflieRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteAllflieRecursive(child);

        fileOrDirectory.delete();
    }

    /**
     * MD5 String gneneration
     **/
    public String getMD5FileName(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString() + ".txt";

        } catch (NoSuchAlgorithmException e) {
//    	DebugReportOnLocat.e(e);
        }
        return "";
    }


}