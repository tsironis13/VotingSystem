package com.votingsystem.tsiro.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.votingsystem.tsiro.ObserverPattern.NetworkStateListeners;
import com.votingsystem.tsiro.ObserverPattern.RegistrationTokenListeners;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 5/4/2016.
 */
public class RegistrationTokenReceiver extends BroadcastReceiver {

    private static final String debugTag = RegistrationTokenReceiver.class.getSimpleName();
    private List<RegistrationTokenListeners> registrationTokenListenersList;

    public RegistrationTokenReceiver() {
        this.registrationTokenListenersList = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String token = intent != null ? intent.getStringExtra("registrationToken") : null;
        initializeRegistrationTokenToAll(token);
    }

    private void initializeRegistrationTokenToAll(String token) {
        for (RegistrationTokenListeners registrationTokenListeners : registrationTokenListenersList) {
            initializeRegistrationToken(registrationTokenListeners, token);
        }
    }

    private static void initializeRegistrationToken(RegistrationTokenListeners registrationTokenListeners, String token) {
        registrationTokenListeners.getRegistrationToken(token);
    }

    public void addListener(RegistrationTokenListeners registrationTokenListeners) {
        registrationTokenListenersList.add(registrationTokenListeners);
    }

    public void removeListener(RegistrationTokenListeners registrationTokenListeners) {
        registrationTokenListenersList.remove(registrationTokenListeners);
    }
}
