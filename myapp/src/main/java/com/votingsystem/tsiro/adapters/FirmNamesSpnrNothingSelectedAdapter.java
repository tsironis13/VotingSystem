package com.votingsystem.tsiro.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import com.votingsystem.tsiro.votingsystem.R;
import java.util.ArrayList;


/**
 * Created by giannis on 15/3/2016.
 */
public class FirmNamesSpnrNothingSelectedAdapter extends ArrayAdapter<FirmNameWithID> implements SpinnerAdapter {

    private static final String debugTag = FirmNamesSpnrNothingSelectedAdapter.class.getSimpleName();
    protected static final int EXTRA = 1;
    protected SpinnerAdapter adapter;
    protected Context context;
    protected int nothingSelectedLayout;
    protected int nothingSelectedDropdownLayout;
    protected LayoutInflater layoutInflater;
    protected ArrayList<FirmNameWithID> firmNameWithIDs;

    public FirmNamesSpnrNothingSelectedAdapter(Context context, int resource, ArrayList<FirmNameWithID> objects) {
        super(context, resource, objects);
        this.firmNameWithIDs                = objects;
        //this.adapter                        = spinnerAdapter;
        this.nothingSelectedLayout          = resource;
        this.context                        = context;
        this.layoutInflater                 = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(debugTag, "Position: " + position + " ConvertView: " + convertView);
        //return getNothingSelectedView(parent);
        if (position == 0) {
            //return super.getView(0, convertView, parent);
            return getNothingSelectedView(parent);
        } else {
            return super.getView(position, convertView, parent);
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Log.e(debugTag, "getDropDownView: " + position + " " + convertView);
        View view = convertView;
        ViewHolder viewHolder;
        if (view == null) {
            view                 = layoutInflater.inflate(R.layout.spinner_dropdown_item, parent, false);
            viewHolder           = new ViewHolder(view);
            view.setTag(R.layout.spinner_dropdown_item, viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag(R.layout.spinner_dropdown_item);
        }
        FirmNameWithID firmNameWithID = firmNameWithIDs.get(position);
        viewHolder.dropdownItemTtv.setText(firmNameWithID.getFirm_name());
        //if (position == 0) return new View(context);
        //if (position == 0) return nothingSelectedDropdownLayout == -1 ? new View(context) : getNothingSelectedDropdownView(parent);
        //Could re-use the convertView if possible, use setTag...
        return view;
    }

    @Override
    public int getCount() {
        int count = (firmNameWithIDs != null) ? firmNameWithIDs.size() : 0;
        return count == 0 ? 0 : count;
    }

    /*@Override
    public FirmNameWithID getItem(int position) {
        return position == 0 ? null : firmNameWithIDs.get(position - EXTRA);
    }*/

    protected View getNothingSelectedView(ViewGroup parent) {
        return layoutInflater.inflate(nothingSelectedLayout, parent, false);
    }

    static class ViewHolder {
        TextView dropdownItemTtv;

        public ViewHolder(View view) {
            dropdownItemTtv = (TextView) view.findViewById(R.id.spnrDropdownItem);
        }
    }
    /*public FirmNamesSpnrNothingSelectedAdapter(ArrayList<FirmNameWithID> firmNameWithIDs, int nothingSelectedLayout, Context context){
        this(firmNameWithIDs, nothingSelectedLayout, -1, context);
    };

    public FirmNamesSpnrNothingSelectedAdapter(ArrayList<FirmNameWithID> firmNameWithIDs, int nothingSelectedLayout, int nothingSelectedDropdownLayout, Context context){
        this.firmNameWithIDs                = firmNameWithIDs;
        //this.adapter                        = spinnerAdapter;
        this.nothingSelectedLayout          = nothingSelectedLayout;
        this.nothingSelectedDropdownLayout  = nothingSelectedDropdownLayout;
        this.context                        = context;
        this.layoutInflater                 = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(debugTag, "ConvertView: "+convertView+" Parent: "+parent+" Position: "+position);
        //if (position == 0) return getNothingSelectedView(parent);
        //return adapter.getView(position - EXTRA, convertView, parent);
        return getNothingSelectedView(parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Log.e(debugTag,"getDropDownView: "+position+" "+convertView);
        View view = convertView;
        ViewHolder viewHolder;
        if (convertView == null) {
            view                 = layoutInflater.inflate(R.layout.spinner_dropdown_item, parent, false);
            viewHolder           = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        FirmNameWithID firmNameWithID = firmNameWithIDs.get(position);
        viewHolder.dropdownItemTtv.setText(firmNameWithID.getFirm_name());
        viewHolder.dropdownItemTtv.setTag(firmNameWithID.getId());
        //if (position == 0) return nothingSelectedDropdownLayout == -1 ? new View(context) : getNothingSelectedDropdownView(parent);
        //Could re-use the convertView if possible, use setTag...
        return view;
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
        return 1;
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

    static class ViewHolder {
        TextView dropdownItemTtv;

        public ViewHolder(View view) {
            dropdownItemTtv = (TextView) view.findViewById(R.id.spnrDropdownItem);
        }
    }
    */
}
