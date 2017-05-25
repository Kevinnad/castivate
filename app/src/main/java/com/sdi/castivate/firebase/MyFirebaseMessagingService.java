package com.sdi.castivate.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sdi.castivate.CastingScreen;
import com.sdi.castivate.R;
import com.sdi.castivate.utils.DebugReportOnLocat;

import java.util.Map;

/**
 * Created by Gnanaoly on 27-Sep-16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    String rollId = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        Map<String, String> response = remoteMessage.getData();

        String message = response.get("message");

        if (message != null) {
            if (message.contains("#")) {


                String[] mmessage = message.split("#");
                DebugReportOnLocat.ln("spilt message:" + mmessage);
                rollId = mmessage[1];
                sendNotification(mmessage[0]);
            } else {
                rollId = "";
                sendNotification(message);
            }

        }else {
            sendNotification("Content By Online");
        }

    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, CastingScreen.class);

        if (!rollId.equals("")) {
            intent.putExtra("rollID", rollId);
        }else{
            intent.putExtra("fromImage", "Image");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Castivate")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.nanoTime(), notificationBuilder.build());
    }
}
