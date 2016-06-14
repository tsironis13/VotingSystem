package com.votingsystem.tsiro.mainClasses;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
//import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.fragments.*;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by user on 16/11/2015.
 */
public class DashboardActivity extends AppCompatActivity {

    private static final String debugTag = "DashboardActivity";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            DashboardFragment dashboardFragmentFragment = new DashboardFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.baseFrlt, dashboardFragmentFragment, getResources().getString(R.string.dashboardFgmt)).commit();
            Log.d(debugTag, "created for the first time");
        } else {
            Log.d(debugTag, "was not created due to orientation change");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        //refWatcher.watch(this);
    }
}
