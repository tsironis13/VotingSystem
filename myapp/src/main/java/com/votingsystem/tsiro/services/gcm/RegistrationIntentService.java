package com.votingsystem.tsiro.services.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.votingsystem.R;
import java.io.IOException;

/**
 * Created by giannis on 5/4/2016.
 */
public class RegistrationIntentService extends IntentService {

    private static final String debugTag = RegistrationIntentService.class.getSimpleName();
    private Intent intent;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RegistrationIntentService(String name) {
        super(name);
    }

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID   =   InstanceID.getInstance(this);
        intent                  =   new Intent(getResources().getString(R.string.registration_token));
        try {
            String token   =   instanceID.getToken(getResources().getString(R.string.gcm_senderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            intent.putExtra(getResources().getString(R.string.registration_token), token);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
