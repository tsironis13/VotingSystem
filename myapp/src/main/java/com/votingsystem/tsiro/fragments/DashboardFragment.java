package com.votingsystem.tsiro.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.votingsystem.tsiro.ObserverPattern.NetworkStateListeners;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.broadcastReceivers.NetworkStateReceiver;
import com.votingsystem.tsiro.mainClasses.CreateSurveyActivity;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.mainClasses.SurveysActivity;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by user on 16/11/2015.
 */
public class DashboardFragment extends Fragment implements DAMVCView, NetworkStateListeners {

    private static final String debugTag = DashboardFragment.class.getSimpleName();
    private View view;
    private TextView firmTtv, totalSurveysTtv, responsedSurveysTtv, lastCreatedDateTtv;
    private Button viewAllBtn, createSurveyBtn;
    private DAMVCPresenterImpl DAMVCpresenterImpl;
    private ProgressDialog progressDialog;
    private NetworkStateReceiver networkStateReceiver;
    private boolean activityCreated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        firmTtv             =   (TextView) view.findViewById(R.id.firmTtv);
        totalSurveysTtv     =   (TextView) view.findViewById(R.id.totalSurveysTtv);
        responsedSurveysTtv =   (TextView) view.findViewById(R.id.responsedSurveysTtv);
        lastCreatedDateTtv  =   (TextView) view.findViewById(R.id.lastCreatedDateTtv);
        viewAllBtn          =   (Button) view.findViewById(R.id.viewAll);
        createSurveyBtn     =   (Button) view.findViewById(R.id.createSurveyBtn);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.home));
        DAMVCpresenterImpl      = new DAMVCPresenterImpl(this);
        networkStateReceiver    = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        getActivity().registerReceiver(networkStateReceiver, new IntentFilter(getResources().getString(R.string.connectivity_change)));

        activityCreated = true;

        viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SurveysActivity.class));
            }
        });
        createSurveyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CreateSurveyActivity.class));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        getActivity().unregisterReceiver(networkStateReceiver);
        this.DAMVCpresenterImpl.onDestroy();
    }

    @Override
    public void onSuccessDashboardDetails(final String firm_name, final int total_surveys, final int responses, final String last_created_date) {
        if (progressDialog.isShowing()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    firmTtv.setText(firm_name);
                    totalSurveysTtv.setText(String.valueOf(total_surveys));
                    responsedSurveysTtv.setText(String.valueOf(responses));
                    lastCreatedDateTtv.setText(last_created_date);
                }
            }, 1500);
        }
    }

    @Override
    public void onFailure(int code) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.baseFrlt, ErrorFragment.newInstance(getResources().getString(R.string.dashboard_fgmt), code), getResources().getString(R.string.error_fgmt)).commit();
        }
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getActivity().getResources().getString(R.string.message));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void networkStatus(int connectionType) {
        if (activityCreated)
            if (connectionType != AppConfig.NO_CONNECTION) {
                initializeProgressDialog();
                DAMVCpresenterImpl.initializeDashboardDetails(isAdded(), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0));
            } else {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.baseFrlt, ErrorFragment.newInstance(getResources().getString(R.string.dashboard_fgmt), AppConfig.NO_CONNECTION), getResources().getString(R.string.error_fgmt)).commit();
            }
            activityCreated = false;
    }
}
