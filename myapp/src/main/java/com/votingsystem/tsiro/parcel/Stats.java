package com.votingsystem.tsiro.parcel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by giannis on 16/7/2016.
 */
public class Stats implements Parcelable{

    private String title;
    private float percentage;
    private int count, index;

    public Stats(String title, float percentage, int count, int index) {
        this.title      =   title;
        this.percentage =   percentage;
        this.count      =   count;
        this.index      =   index;
    }

    protected Stats(Parcel in) {
        title       = in.readString();
        percentage  = in.readFloat();
        count       = in.readInt();
        index   = in.readInt();
    }

    public static final Creator<Stats> CREATOR = new Creator<Stats>() {
        @Override
        public Stats createFromParcel(Parcel in) {
            return new Stats(in);
        }

        @Override
        public Stats[] newArray(int size) {
            return new Stats[size];
        }
    };

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public float getPercentage() { return percentage; }

    public void setPercentage(float percentage) { this.percentage = percentage; }

    public int getCount() { return count; }

    public void setCount(int count) { this.count = count; }

    public int getIndex() { return index; }

    public void setIndex(int index) { this.index = index; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeFloat(percentage);
        parcel.writeInt(count);
        parcel.writeInt(index);
    }
}
