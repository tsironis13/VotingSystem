package com.votingsystem.tsiro.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.votingsystem.tsiro.ObserverPattern.ConnectivityObserver;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.ConnectivitySingleton;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by user on 19/12/2015.
 */
public class NetworkState extends BroadcastReceiver implements Observer{

    private static final String debugTag = NetworkState.class.getName();
    private ConnectivityManager connectivityManager;
    private NetworkInfo activeNetworkInfo;
    private boolean isConnected;
    private ConnectivityObserver connectivityObserver;
    private static boolean firstConnect = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        connectivityObserver = ConnectivitySingleton.getInstance();
        connectivityObserver.addObserver(this);
        if ( activeNetworkInfo != null ) {
            Log.e(debugTag, "firstconnect: "+firstConnect);
            if ( firstConnect ) {
                Log.e(debugTag, "firstconnect: "+firstConnect);
                connectivityObserver.setConnectivityStatus(activeNetworkInfo.getType());
                firstConnect = false;
            }
        } else {
            firstConnect = true;
            connectivityObserver.setConnectivityStatus(AppConfig.NO_CONNECTION);
        }
    }

    @Override
    public void update(Observable observable, Object data) {}
}
