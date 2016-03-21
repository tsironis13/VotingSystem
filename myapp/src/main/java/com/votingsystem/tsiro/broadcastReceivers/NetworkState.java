package com.votingsystem.tsiro.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.votingsystem.tsiro.ObserverPattern.NetworkStateListeners;
import com.votingsystem.tsiro.app.AppConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 19/12/2015.
 */
public class NetworkState extends BroadcastReceiver {

    private static final String debugTag = NetworkState.class.getName();
    private ConnectivityManager connectivityManager;
    private List<NetworkStateListeners> networkStateListenerList;
    private int status;

    public NetworkState() {
        networkStateListenerList = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(debugTag, "onReceive");
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        status = (connectivityManager.getActiveNetworkInfo() != null) ? connectivityManager.getActiveNetworkInfo().getType() : AppConfig.NO_CONNECTION;
        notifyStateToAll(status);
    }

    private void notifyStateToAll(int status) {
        for (NetworkStateListeners networkStateListeners : networkStateListenerList) {
            notifyState(networkStateListeners, status);
        }
    }

    public void notifyState(NetworkStateListeners networkStateListeners, int status) {
        Log.e(debugTag, "notifyState Called");
        networkStateListeners.networkStatus(status);
    }

    public void addListener(NetworkStateListeners networkStateListeners) {
        networkStateListenerList.add(networkStateListeners);
    }

    public void removeListener(NetworkStateListeners networkStateListeners) {
        networkStateListenerList.remove(networkStateListeners);
    }
}
