package com.votingsystem.tsiro.mainClasses;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.rey.material.widget.Button;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.POJO.SurveyQuestionBody;
import com.votingsystem.tsiro.POJO.SurveyQuestions;
import com.votingsystem.tsiro.SurveyQuestionsMVC.SQMVCPresenterImpl;
import com.votingsystem.tsiro.SurveyQuestionsMVC.SQMVCView;
import com.votingsystem.tsiro.adapters.SurveyQuestionsPagerAdapter;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.fragments.SurveyQuestionsFragment;
import com.votingsystem.tsiro.interfaces.SaveQuestionListener;
import com.votingsystem.tsiro.parcel.QuestionData;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.List;

/**
 * Created by giannis on 25/6/2016.
 */
public class SurveyQuestionsActivity extends AppCompatActivity implements SQMVCView, SaveQuestionListener {

    private static final String debugTag = SurveyQuestionsActivity.class.getSimpleName();
    private ViewPager mPager;
    Button button;
    private TextView currentPageIndexTxv;
    private SurveyQuestionsPagerAdapter surveyQuestionsPagerAdapter;
    private List<QuestionData> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_questions_activity);

        Toolbar toolbar     =   (Toolbar) findViewById(R.id.appBar);
        mPager              =   (ViewPager) findViewById(R.id.surveyQuestionsPager);
        //button              =   (Button) findViewById(R.id.add);
        currentPageIndexTxv =   (TextView) findViewById(R.id.currentPageIndexTtv);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) toolbar.setElevation((float) MyApplication.convertPixelToDpAndViceVersa(this, 0, 3));

        if (savedInstanceState == null) {
            SQMVCPresenterImpl SQMVCpresenterImpl = new SQMVCPresenterImpl(this);
            SQMVCpresenterImpl.getSurveyQuestions(new SurveyQuestionBody(getResources().getString(R.string.get_survey_questions), getIntent().getIntExtra(getResources().getString(R.string.survey_id), 0)));
        }
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //Log.e(debugTag, "CURRENT PAGE INDEX "+mPager.getCurrentItem());
//                //Log.e(debugTag, "TOTAL PAGES "+surveyQuestionsPagerAdapter.getCount());
//            }
//        });
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Log.e(debugTag, "OnPageSelected "+position);
                int current = mPager.getCurrentItem() + 1;
                currentPageIndexTxv.setText("Erotisi "+current+" apo "+data.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onSuccessSurveyQuestionsFetched(String surveyTitle, List<QuestionData> data) {
        if (data != null && data.size() > 0) {
            surveyQuestionsPagerAdapter = new SurveyQuestionsPagerAdapter(getSupportFragmentManager(), surveyTitle, data.get(0), data.size());
            mPager.setAdapter(surveyQuestionsPagerAdapter);

            this.data = data;
            int current = mPager.getCurrentItem() + 1;
            currentPageIndexTxv.setText("Erotisi "+current+" apo "+data.size());
        }
        Log.e(debugTag, data+"");
    }

    @Override
    public void onFailure(int code) {

    }

    @Override
    public void onSaveQuestion() {
        if (mPager.getCurrentItem() + 1 < data.size() && surveyQuestionsPagerAdapter.getCount() == mPager.getCurrentItem() + 1) {
            surveyQuestionsPagerAdapter.add(data.get(mPager.getCurrentItem()+1));
            mPager.setCurrentItem(surveyQuestionsPagerAdapter.getCount(), true);
            int current = mPager.getCurrentItem() + 1;
            currentPageIndexTxv.setText("Erotisi "+current+" apo "+data.size());
        }
    }
}
