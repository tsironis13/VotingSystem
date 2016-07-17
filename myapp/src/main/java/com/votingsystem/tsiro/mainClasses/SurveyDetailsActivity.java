package com.votingsystem.tsiro.mainClasses;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.votingsystem.tsiro.parcel.SurveyDetailsData;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.fragments.SurveyStatsFragment;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 16/7/2016.
 */
public class SurveyDetailsActivity extends AppCompatActivity {

    private static final String debugTag = SurveyDetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_details_activity);

        Toolbar toolbar =   (Toolbar) findViewById(R.id.appBar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    Intent intent = new Intent(SurveyDetailsActivity.this, SurveysActivity.class);
                    startActivity(intent);
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) toolbar.setElevation((float) MyApplication.convertPixelToDpAndViceVersa(this, 0, 3));

        if (savedInstanceState == null) {
            if (getIntent().getExtras() != null) {
                String action = getIntent().getExtras().getString(getResources().getString(R.string.details_activ_action_key));
                if (action != null && action.equals(getResources().getString(R.string.details_activ_action))) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.surveyDetailsFgmtContainer, SurveyStatsFragment.newInstance((SurveyDetailsData) getIntent().getExtras().getParcelable(getResources().getString(R.string.data_parcelable_key))), getResources().getString(R.string.survey_stats_fgmt)).commit();
                } else {
                }
            }
//            Log.e(debugTag, ""+getIntent().getExtras().getParcelable(getResources().getString(R.string.data_parcelable_key)));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            Intent intent = new Intent(SurveyDetailsActivity.this, SurveysActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
