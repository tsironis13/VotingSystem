package com.votingsystem.tsiro.app;

import com.votingsystem.tsiro.ObserverPattern.ConnectivityObserver;

/**
 * Created by user on 19/12/2015.
 */
public class ConnectivitySingleton {

    private static ConnectivityObserver connectivityObserver;

    public static ConnectivityObserver getInstance() {
        if ( connectivityObserver == null ) connectivityObserver = new ConnectivityObserver();
        return connectivityObserver;
    }
}
