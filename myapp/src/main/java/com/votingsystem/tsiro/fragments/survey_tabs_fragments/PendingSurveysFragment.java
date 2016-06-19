package com.votingsystem.tsiro.fragments.survey_tabs_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 19/6/2016.
 */
public class PendingSurveysFragment extends Fragment {

    private View view;
    private TextView textView;

    public PendingSurveysFragment() {}

    public static PendingSurveysFragment newInstance(String text) {
        return new PendingSurveysFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_pendingsurveys, container, false);
        textView = (TextView) view.findViewById(R.id.test);
        return view;
    }
}
