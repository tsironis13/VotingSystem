package com.votingsystem.tsiro.mainClasses;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.fragments.*;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by user on 16/11/2015.
 */
public class AdminBaseActivity extends AppCompatActivity {

    private static final String debugTag = "AdminBaseActivity";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_base_activity);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            AdminBaseFragment adminBaseFragment = new AdminBaseFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.baseFrlt, adminBaseFragment, "adminBaseFgmt").commit();
            Log.d(debugTag, "created for the first time");
        } else {
            Log.d(debugTag, "was not created due to orientation change");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
