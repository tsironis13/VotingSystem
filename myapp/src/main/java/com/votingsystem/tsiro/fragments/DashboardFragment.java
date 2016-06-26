package com.votingsystem.tsiro.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.widget.Button;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.DashboardActivityMVC.DAMVCPresenterImpl;
import com.votingsystem.tsiro.DashboardActivityMVC.DAMVCView;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.mainClasses.SurveysActivity;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by user on 16/11/2015.
 */
public class DashboardFragment extends Fragment implements DAMVCView{

    private static final String debugTag = DashboardFragment.class.getSimpleName();
    private View view;
    private TextView firmTtv, totalSurveysTtv, responsedSurveysTtv, lastCreatedDateTtv;
    private Button viewAllBtn;
    private DAMVCPresenterImpl DAMVCpresenterImpl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        firmTtv             =   (TextView) view.findViewById(R.id.firmTtv);
        totalSurveysTtv     =   (TextView) view.findViewById(R.id.totalSurveysTtv);
        responsedSurveysTtv =   (TextView) view.findViewById(R.id.responsedSurveysTtv);
        lastCreatedDateTtv  =   (TextView) view.findViewById(R.id.lastCreatedDateTtv);
        viewAllBtn          =   (Button) view.findViewById(R.id.viewAll);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.home));
        DAMVCpresenterImpl = new DAMVCPresenterImpl(this);
        DAMVCpresenterImpl.initializeDashboardDetails(isAdded(), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0));
        viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SurveysActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.DAMVCpresenterImpl.onDestroy();
    }

    @Override
    public void onSuccessDashboardDetails(String firm_name, int total_surveys, int responses, String last_created_date) {
        firmTtv.setText(firm_name);
        totalSurveysTtv.setText(String.valueOf(total_surveys));
        responsedSurveysTtv.setText(String.valueOf(responses));
        lastCreatedDateTtv.setText(last_created_date);
    }

    @Override
    public void onFailure() {

    }
}
