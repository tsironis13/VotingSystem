package com.votingsystem.tsiro.services.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.votingsystem.tsiro.votingsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by giannis on 4/4/2016.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String debugTag = MyGcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        generateNotification(data);

    }

    private void generateNotification(Bundle data) {
        Bundle t = data.getBundle("notification");

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, Intent.createChooser(intent, "Open with"), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                                                    .setSmallIcon(R.drawable.app_logo)
                                                    .setContentTitle(t.getString("title"))
                                                    .setContentText(t.getString("body"))
                                                    .setDefaults(Notification.DEFAULT_VIBRATE)
                                                    .setContentIntent(resultPendingIntent);

        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, nBuilder.build());
    }
}
