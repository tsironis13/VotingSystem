package com.votingsystem.tsiro.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;

/**
 * Created by giannis on 15/3/2016.
 */
public class FirmNamesSpnrNothingSelectedAdapter implements SpinnerAdapter {

    private static final String debugTag = FirmNamesSpnrNothingSelectedAdapter.class.getSimpleName();
    private static final int EXTRA = 1;
    private SpinnerAdapter spinnerAdapter;
    private Context context;
    private int nothingSelectedLayout;
    private LayoutInflater layoutInflater;

    public FirmNamesSpnrNothingSelectedAdapter(SpinnerAdapter spinnerAdapter, int nothingSelectedLayout, Context context) {
        this.spinnerAdapter                 = spinnerAdapter;
        this.nothingSelectedLayout          = nothingSelectedLayout;
        this.context                        = context;
        this.layoutInflater                 = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(debugTag, "Position: " + position + " ConvertView: " + convertView);
        if (position == 0) {
            return getNothingSelectedView(parent);
        } else {
            return spinnerAdapter.getView(position - EXTRA, convertView, parent);
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        //Log.e(debugTag, "getDropDownView: " + position + " " + convertView);
        if (position == 0) {
            return new View(context);
        } else {
            return spinnerAdapter.getDropDownView(position - EXTRA, null, parent);
        }
    }

    @Override
    public int getCount() {
        int count = (spinnerAdapter == null) ? 0 : spinnerAdapter.getCount();
        return count == 0 ? 0 : count + EXTRA;
    }

    @Override
    public FirmNameWithID getItem(int position) {
        //return position == 0 ? null : spinnerAdapter.getItem(position + EXTRA);
        return null;
    }

    @Override
    public long getItemId(int position) {
        //return position >= EXTRA ? super.getItemId(position + EXTRA) : position + EXTRA;
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return spinnerAdapter.isEmpty();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {}

    protected View getNothingSelectedView(ViewGroup parent) {
        return layoutInflater.inflate(nothingSelectedLayout, parent, false);
    }

}
