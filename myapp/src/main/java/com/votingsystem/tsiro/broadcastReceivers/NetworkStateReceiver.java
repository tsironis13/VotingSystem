package com.votingsystem.tsiro.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.votingsystem.tsiro.ObserverPattern.NetworkStateListeners;
import com.votingsystem.tsiro.app.AppConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 19/12/2015.
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    private static final String debugTag = NetworkStateReceiver.class.getName();
    private List<NetworkStateListeners> networkStateListenerList;

    public NetworkStateReceiver() {
        networkStateListenerList = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e(debugTag, "onReceive called");
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        int status = (connectivityManager.getActiveNetworkInfo() != null) ? connectivityManager.getActiveNetworkInfo().getType() : AppConfig.NO_CONNECTION;
        notifyStateToAll(status);
    }

    private void notifyStateToAll(int status) {
        for (NetworkStateListeners networkStateListeners : networkStateListenerList) {
            notifyState(networkStateListeners, status);
        }
    }

    private static void notifyState(NetworkStateListeners networkStateListeners, int status) {
//        Log.e(debugTag, "notifyState Called");
        networkStateListeners.networkStatus(status);
    }

    public void addListener(NetworkStateListeners networkStateListeners) {
        networkStateListenerList.add(networkStateListeners);
    }

    public void removeListener(NetworkStateListeners networkStateListeners) {
        networkStateListenerList.remove(networkStateListeners);
    }
}
