package com.votingsystem.tsiro.POJO;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by user on 28/11/2015.
 */
public class NavDrawerData implements Parcelable{

    public String category;
    public int iconId;

    public NavDrawerData(){}

    public NavDrawerData(Parcel source) {
        category    = source.readString();
        iconId      = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeInt(iconId);
    }

    public static final Parcelable.Creator<NavDrawerData> CREATOR =
            new Parcelable.Creator<NavDrawerData>(){
                @Override
                public NavDrawerData createFromParcel(Parcel in) {
                    return new NavDrawerData(in);
                }
                @Override
                public NavDrawerData[] newArray(int size) {
                    return new NavDrawerData[size];
                }
            };
}
