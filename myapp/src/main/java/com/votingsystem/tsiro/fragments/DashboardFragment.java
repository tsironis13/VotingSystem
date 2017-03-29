package com.votingsystem.tsiro.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rey.material.widget.Button;
import com.rey.material.widget.LinearLayout;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.dashboardActivityMVC.DAMVCPresenterImpl;
import com.votingsystem.tsiro.dashboardActivityMVC.DAMVCView;
import com.votingsystem.tsiro.observerPattern.NetworkStateListeners;
import com.votingsystem.tsiro.POJO.JnctFirmSurveysFields;
import com.votingsystem.tsiro.POJO.SurveysFields;
import com.votingsystem.tsiro.spinnerLoading.SpinnerLoading;
import com.votingsystem.tsiro.sqLite.MySQLiteHelper;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.broadcastReceivers.NetworkStateReceiver;
import com.votingsystem.tsiro.mainClasses.CreateSurveyActivity;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.mainClasses.SurveysActivity;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.List;

/**
 * Created by user on 16/11/2015.
 */
public class DashboardFragment extends Fragment implements DAMVCView, NetworkStateListeners {

    private static final String debugTag = DashboardFragment.class.getSimpleName();
    private SpinnerLoading spinnerLoading;
    private LinearLayout dashboardCardLlt;
    private View view;
    private TextView firmTtv, totalSurveysTtv, responsedSurveysTtv, lastCreatedDateTtv;
    private Button viewAllBtn, createSurveyBtn;
    private DAMVCPresenterImpl DAMVCpresenterImpl;
    private ProgressDialog progressDialog;
    private NetworkStateReceiver networkStateReceiver;
    private boolean activityCreated, dataFetched;
    private int connectionType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        spinnerLoading      =   (SpinnerLoading) view.findViewById(R.id.spinnerLoading);
        dashboardCardLlt    =   (LinearLayout) view.findViewById(R.id.dashboardCardLlt);
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
        getActivity().getWindow().setBackgroundDrawable(null);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.home));
        networkStateReceiver = new NetworkStateReceiver();
        activityCreated = true;

        viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SurveysActivity.class);
                intent.putExtra(getResources().getString(R.string.connection_status), connectionType);
                intent.putExtra(getResources().getString(R.string.action), getResources().getString(R.string.firm_surveys));
                startActivity(intent);
            }
        });
        createSurveyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateSurveyActivity.class);
                intent.putExtra(getResources().getString(R.string.action), getResources().getString(R.string.tag_new));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        DAMVCpresenterImpl = new DAMVCPresenterImpl(this);
        networkStateReceiver.addListener(this);
        getActivity().registerReceiver(networkStateReceiver, new IntentFilter(getResources().getString(R.string.connectivity_change)));
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            if (!dataFetched && connectionType != AppConfig.NO_CONNECTION) {
                DAMVCpresenterImpl.initializeDashboardDetails(isAdded(), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        networkStateReceiver.removeListener(this);
        getActivity().unregisterReceiver(networkStateReceiver);
        this.DAMVCpresenterImpl.onDestroy();
//        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }

    @Override
    public void onSuccessDashboardDetails(final String firm_name, final int total_surveys, final int responses, final String last_created_date, final List<JnctFirmSurveysFields> jnctFirmSurveysFieldsList, final List<SurveysFields> surveysFieldsList) {
//        Log.e(debugTag, "SIZE: "+surveysFieldsList.size());
        dataFetched = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeSpinnerLoadingAnimation(spinnerLoading, dashboardCardLlt);
                firmTtv.setText(firm_name);
                totalSurveysTtv.setText(String.valueOf(total_surveys));
                responsedSurveysTtv.setText(String.valueOf(responses));
                lastCreatedDateTtv.setText(last_created_date);
                if (!MySQLiteHelper.getInstance(getActivity()).checkDatabase(getActivity())) {
                    MySQLiteHelper.getInstance(getActivity()).createDatabase(getActivity());
                    MySQLiteHelper.getInstance(getActivity()).openDatabase();
                    if (!MySQLiteHelper.getInstance(getActivity()).isDatabaseEmpty()) {
                        MySQLiteHelper.getInstance(getActivity()).insertToDatabase(getResources().getString(R.string.jnct_table), jnctFirmSurveysFieldsList, null);
                        MySQLiteHelper.getInstance(getActivity()).insertToDatabase(getResources().getString(R.string.surveys_table), surveysFieldsList, null);
                    }
                    MySQLiteHelper.getInstance(getActivity()).closeDB();
                } else {
                    MySQLiteHelper.getInstance(getActivity()).openDatabase();
                    MySQLiteHelper.getInstance(getActivity()).isDatabaseEmpty();
                    MySQLiteHelper.getInstance(getActivity()).closeDB();
                }
            }
        }, 1500);
    }

    @Override
    public void onFailure(int code) {
        spinnerLoading.setVisibility(View.GONE);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.baseFrlt, ErrorFragment.newInstance(getResources().getString(R.string.dashboard_fgmt), code), getResources().getString(R.string.error_fgmt)).commit();
    }

    @Override
    public void networkStatus(int connectionType) {
        this.connectionType = connectionType;
        if (activityCreated)
            if (connectionType != AppConfig.NO_CONNECTION) {
                DAMVCpresenterImpl.initializeDashboardDetails(isAdded(), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0));
            } else {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.baseFrlt, ErrorFragment.newInstance(getResources().getString(R.string.dashboard_fgmt), AppConfig.NO_CONNECTION), getResources().getString(R.string.error_fgmt)).commit();
            }
            activityCreated = false;
    }

    private void initializeSpinnerLoadingAnimation(final SpinnerLoading spinnerLoading, final LinearLayout dashboardCardLlt) {
        spinnerLoading.setVisibility(View.GONE);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(spinnerLoading, getResources().getString(R.string.alpha), 1 , 0);
        alphaAnimator.setDuration(230);
        alphaAnimator.start();
        alphaAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                dashboardCardLlt.setVisibility(View.VISIBLE);
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(dashboardCardLlt, getResources().getString(R.string.alpha), 0 , 1);
                alphaAnimator.setDuration(150);
                alphaAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }
}
