package com.votingsystem.tsiro.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import java.util.List;

/**
 * Created by giannis on 15/3/2016.
 */
public class FirmNamesSpnrNothingSelectedAdapter extends ArrayAdapter<FirmNameWithID> {

    private static final String debugTag = FirmNamesSpnrNothingSelectedAdapter.class.getSimpleName();
    private static final int EXTRA = 1;
    private Context context;
    private int nothingSelectedLayout;
    private List<FirmNameWithID> firmNameWithIDArrayList;
    private LayoutInflater layoutInflater;

    public FirmNamesSpnrNothingSelectedAdapter(Context context, int nothingSelectedLayout, List<FirmNameWithID> firmNameWithIDArrayList) {
        super(context, nothingSelectedLayout, firmNameWithIDArrayList);
        this.context                    =   context;
        this.nothingSelectedLayout      =   nothingSelectedLayout;
        this.firmNameWithIDArrayList    =   firmNameWithIDArrayList;
        this.layoutInflater             =   LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            return getNothingSelectedView(parent);
        } else {
            return super.getView(position - EXTRA, convertView, parent);
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        int positionToReturn;
        if (position == 0) {
            return new View(context);
        } else {
            positionToReturn = position - EXTRA;
        }
        return super.getDropDownView(positionToReturn, null, parent);
    }

    @Override
    public int getCount() {
        int count = (firmNameWithIDArrayList == null) ? 0 : firmNameWithIDArrayList.size();
        return count == 0 ? 0 : count + EXTRA;
    }

    private View getNothingSelectedView(ViewGroup parent) {
        return layoutInflater.inflate(nothingSelectedLayout, parent, false);
    }
}
