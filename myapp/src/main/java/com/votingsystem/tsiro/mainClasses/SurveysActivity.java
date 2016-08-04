package com.votingsystem.tsiro.mainClasses;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;

import com.votingsystem.tsiro.ObserverPattern.NetworkStateListeners;
import com.votingsystem.tsiro.adapters.SurveysPagerAdapter;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.broadcastReceivers.NetworkStateReceiver;
import com.votingsystem.tsiro.interfaces.SurveysActivityCommonElements;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 19/6/2016.
 */
public class SurveysActivity extends AppCompatActivity implements NetworkStateListeners, SurveysActivityCommonElements{

    private static final String debugTag = SurveysActivity.class.getSimpleName();
    private CoordinatorLayout coordinatorLayout;
    private TabLayout mTabs;
    private ViewPager mPager;
    private NetworkStateReceiver networkStateReceiver;
    private boolean activityCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surveys_activity);

        Toolbar toolbar         =   (Toolbar) findViewById(R.id.appBar);
        coordinatorLayout       =   (CoordinatorLayout) findViewById(R.id.coordinatorLayt);
        mTabs                   =   (TabLayout) findViewById(R.id.tabs);
        mPager                  =   (ViewPager) findViewById(R.id.surveysPager);
        networkStateReceiver    = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(getResources().getString(R.string.connectivity_change)));

        activityCreated = true;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.surveys));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        //SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), 2));
//        initializeTabsViewPager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void networkStatus(int connectionType) {
        if (activityCreated)
            mPager.setAdapter(new SurveysPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.surveys_tabs), connectionType));
            mTabs.setupWithViewPager(mPager);
            activityCreated = false;
    }

    @Override
    public void showErrorContainerSnackbar(String desc) {
        Snackbar snkBar = Snackbar.make(coordinatorLayout, desc, Snackbar.LENGTH_LONG);

        View sbView = snkBar.getView();
        sbView.setMinimumHeight((int) MyApplication.convertPixelToDpAndViceVersa(this, 0, 80));
        sbView.setPadding(24,24,24,24);
        android.widget.TextView textView = (android.widget.TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(14);
        textView.setTextColor(ContextCompat.getColor(this, R.color.sb_error_text));
        snkBar.show();
    }

}
