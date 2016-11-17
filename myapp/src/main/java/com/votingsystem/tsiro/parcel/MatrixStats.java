package com.votingsystem.tsiro.parcel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by giannis on 20/7/2016.
 */
public class MatrixStats implements Parcelable{

    private float percentage;
    private int count;

    public MatrixStats(float percentage, int count) {
        this.percentage = percentage;
        this.count      = count;
    }

    private MatrixStats(Parcel in) {
        percentage  = in.readFloat();
        count       = in.readInt();
    }

    public static final Creator<MatrixStats> CREATOR = new Creator<MatrixStats>() {
        @Override
        public MatrixStats createFromParcel(Parcel in) {
            return new MatrixStats(in);
        }

        @Override
        public MatrixStats[] newArray(int size) {
            return new MatrixStats[size];
        }
    };

    public float getPercentage() { return percentage; }

    public void setPercentage(float percentage) { this.percentage = percentage; }

    public int getCount() { return count; }

    public void setCount(int count) { this.count = count; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(percentage);
        parcel.writeInt(count);
    }
}
