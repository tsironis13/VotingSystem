package com.votingsystem.tsiro.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

/**
 * Created by giannis on 15/3/2016.
 */
public class FirmNamesSpnrNothingSelectedAdapter implements SpinnerAdapter {

    private static final String debugTag = FirmNamesSpnrNothingSelectedAdapter.class.getSimpleName();
    protected static final int EXTRA = 1;
    protected SpinnerAdapter adapter;
    protected Context context;
    protected int nothingSelectedLayout;
    protected int nothingSelectedDropdownLayout;
    protected LayoutInflater layoutInflater;

    public FirmNamesSpnrNothingSelectedAdapter(SpinnerAdapter spinnerAdapter, int nothingSelectedLayout, Context context){
        this(spinnerAdapter, nothingSelectedLayout, -1, context);
    };

    public FirmNamesSpnrNothingSelectedAdapter(SpinnerAdapter spinnerAdapter, int nothingSelectedLayout, int nothingSelectedDropdownLayout, Context context){
        this.adapter                        = spinnerAdapter;
        this.nothingSelectedLayout          = nothingSelectedLayout;
        this.nothingSelectedDropdownLayout  = nothingSelectedDropdownLayout;
        this.context                        = context;
        this.layoutInflater                 = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(debugTag, "ConvertView: "+convertView+" Parent: "+parent+" Position: "+position);
        if (position == 0) return getNothingSelectedView(parent);
        return adapter.getView(position - EXTRA, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Log.e(debugTag,"getDropDownView: "+position+" "+convertView);
        if (position == 0) return nothingSelectedDropdownLayout == -1 ? new View(context) : getNothingSelectedDropdownView(parent);
        //Could re-use the convertView if possible, use setTag...
        return adapter.getDropDownView(position - EXTRA, null, parent);
    }

    @Override
    public int getCount() {
        int count = (adapter != null) ? adapter.getCount() : 0;
        return count == 0 ? 0 : count + EXTRA;
    }

    @Override
    public Object getItem(int position) {
        return position == 0 ? null : adapter.getItem(position - EXTRA);
    }

    @Override
    public long getItemId(int position) {
        return position >= EXTRA ? adapter.getItemId(position - EXTRA) : position - EXTRA;
    }

    @Override
    public boolean hasStableIds() {
        return adapter != null && adapter.hasStableIds();
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
        return adapter.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {}

    protected View getNothingSelectedView(ViewGroup parent) {
        return layoutInflater.inflate(nothingSelectedLayout, parent, false);
    }

    protected View getNothingSelectedDropdownView(ViewGroup parent) {
        return layoutInflater.inflate(nothingSelectedDropdownLayout, parent, false);
    }

    public SpinnerAdapter getUnderlinedSpinnerAdapter() {
        return this.adapter;
    }
}
