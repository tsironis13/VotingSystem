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
    private NetworkInfo activeNetworkInfo;
    private List<NetworkStateListeners> networkStateListenerList;

    public NetworkState() {
        networkStateListenerList = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(debugTag, "onReceive");
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        notifyStateToAll();
    }

    private void notifyStateToAll() {
        for (NetworkStateListeners networkStateListeners : networkStateListenerList) {
            notifyState(networkStateListeners);
        }
    }

    public void notifyState(NetworkStateListeners networkStateListeners) {
        Log.e(debugTag, "notifyState Called");
        if (activeNetworkInfo == null) {
            networkStateListeners.networkStatus(AppConfig.NO_CONNECTION);
        } else {
            networkStateListeners.networkStatus(activeNetworkInfo.getType());
        }
    }

    public void addListener(NetworkStateListeners networkStateListeners) {
        networkStateListenerList.add(networkStateListeners);
    }

    public void removeListener(NetworkStateListeners networkStateListeners) {
        networkStateListenerList.remove(networkStateListeners);
    }
}
