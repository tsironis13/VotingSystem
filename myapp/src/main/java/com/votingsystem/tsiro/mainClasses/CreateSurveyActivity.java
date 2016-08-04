package com.votingsystem.tsiro.mainClasses;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.fragments.NewSurveyDetailsFragment;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 30/7/2016.
 */
public class CreateSurveyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_survey_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);

        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation((float) MyApplication.convertPixelToDpAndViceVersa(this, 0, 3));
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.createSurveyFgmtContainer, NewSurveyDetailsFragment.newInstance(getResources().getString(R.string.new_survey)), getResources().getString(R.string.new_survey_details_fgmt)).commit();
    }
}
