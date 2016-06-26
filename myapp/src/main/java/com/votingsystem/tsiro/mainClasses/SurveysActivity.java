package com.votingsystem.tsiro.mainClasses;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.votingsystem.tsiro.POJO.AllSurveys;
import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.SurveysActivityMVC.SAMVCPresenterImpl;
import com.votingsystem.tsiro.SurveysActivityMVC.SAMVCView;
import com.votingsystem.tsiro.adapters.SurveysPagerAdapter;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.List;

/**
 * Created by giannis on 19/6/2016.
 */
public class SurveysActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout mTabs;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surveys_activity);

        toolbar     =   (Toolbar) findViewById(R.id.appBar);
        mTabs       =   (TabLayout) findViewById(R.id.tabs);
        mPager      =   (ViewPager) findViewById(R.id.surveysPager);

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
        initializeTabsViewPager();
    }

    private void initializeTabsViewPager() {
        mPager.setAdapter(new SurveysPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.surveys_tabs)));
        mTabs.setupWithViewPager(mPager);
    }
}
