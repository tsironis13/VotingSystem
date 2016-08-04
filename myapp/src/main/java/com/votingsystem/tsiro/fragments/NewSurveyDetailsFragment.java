package com.votingsystem.tsiro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.widget.Button;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.Locale;

/**
 * Created by giannis on 30/7/2016.
 */
public class NewSurveyDetailsFragment extends Fragment {

    private static final String debugTag = SurveyDetailsFragment.class.getSimpleName();
    private View view;
    private Button btn;
    private Dialog.Builder builder;

    public static NewSurveyDetailsFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        NewSurveyDetailsFragment newSurveyDetails = new NewSurveyDetailsFragment();
        newSurveyDetails.setArguments(bundle);
        return newSurveyDetails;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view = inflater.inflate(R.layout.fragment_new_survey_details, container, false);
        btn = (Button) view.findViewById(R.id.date);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getArguments().getString(getResources().getString(R.string.title)));
        }
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker_Light);
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getFragmentManager(), null);
            }
        });
    }
}
