package com.votingsystem.tsiro.services.fcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 4/4/2016.
 */
public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private Intent intent;
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        intent = new Intent(getResources().getString(R.string.registration_token));
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("", "Refreshed token: " + refreshedToken);

        if (refreshedToken != null) {
            intent.putExtra(getResources().getString(R.string.registration_token), refreshedToken);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
