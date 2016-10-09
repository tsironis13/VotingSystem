package com.votingsystem.tsiro.services.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.votingsystem.tsiro.mainClasses.SurveysActivity;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 4/4/2016.
 */
public class MyFcmListenerService extends FirebaseMessagingService {

    private static final String debugTag = MyFcmListenerService.class.getSimpleName();
    private PendingIntent resultPendingIntent;
    private String action, title, body;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String from = remoteMessage.getFrom();
        Log.e(debugTag, from);
        if (remoteMessage.getData().size() > 0) {
            Log.e(debugTag, "Message data payload: " + remoteMessage.getData());
            action   = remoteMessage.getData().get(getResources().getString(R.string.action));
            title    = remoteMessage.getData().get(getResources().getString(R.string.notification_title));
            body     = remoteMessage.getData().get(getResources().getString(R.string.notification_body));
        }
        if (remoteMessage.getNotification() != null) {
            Log.e(debugTag, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        generateNotification(action, title, body);
    }

    private void generateNotification(String action, String title, String body) {
        if (getResources().getString(R.string.email_notification).equals(action)) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(getResources().getString(R.string.mailto)));
            resultPendingIntent = PendingIntent.getActivity(this, 0, Intent.createChooser(intent, getResources().getString(R.string.open_with)), PendingIntent.FLAG_UPDATE_CURRENT);
        } else if (getResources().getString(R.string.new_user_survey_notification).equals(action)) {
            Intent intent = new Intent(this, SurveysActivity.class);
            resultPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        }
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(resultPendingIntent);

        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, nBuilder.build());
    }
}
