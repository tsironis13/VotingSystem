package com.votingsystem.tsiro.ObserverPattern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.Parcelable;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import java.util.Observable;

/**
 * Created by user on 19/12/2015.
 */
public class ConnectivityObserver extends Observable implements Parcelable{

    private ConnectivityManager connectivityManager;
    private NetworkInfo activeNetworkInfo;
    private int connectivityStatus;
    private int connectionType;

    public ConnectivityObserver(){}

    public ConnectivityObserver(Parcel source){
        connectivityStatus = source.readInt();
    }

    public int getConnectivityStatus(Context context){
        if ( !LoginActivity.connectionStatusUpdated ) {
            this.connectivityStatus = isConnected(context);
        }
        return this.connectivityStatus;
    }

    public void setConnectivityStatus(int connectivityStatus) {
        this.connectivityStatus = connectivityStatus;
        setChanged();
        notifyObservers();
    }

    private int isConnected(Context context){
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        connectionType = activeNetworkInfo != null ? activeNetworkInfo.getType() : AppConfig.NO_CONNECTION;
        return connectionType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(connectivityStatus);
    }

    public static final Parcelable.Creator<ConnectivityObserver> CREATOR = new Parcelable.Creator<ConnectivityObserver>() {

        @Override
        public ConnectivityObserver createFromParcel(Parcel source) {
            return new ConnectivityObserver(source);
        }

        @Override
        public ConnectivityObserver[] newArray(int size) {
            return new ConnectivityObserver[size];
        }
    };
}
