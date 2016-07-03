package com.votingsystem.tsiro.mainClasses;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.rey.material.widget.Button;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.FrameLayout;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.POJO.ObjectAnimatorProperties;
import com.votingsystem.tsiro.POJO.SurveyQuestionBody;
import com.votingsystem.tsiro.SurveyQuestionsMVC.SQMVCPresenterImpl;
import com.votingsystem.tsiro.SurveyQuestionsMVC.SQMVCView;
import com.votingsystem.tsiro.adapters.SurveyQuestionsPagerAdapter;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.fragments.SurveyQuestionsFragment;
import com.votingsystem.tsiro.interfaces.SaveQuestionListener;
import com.votingsystem.tsiro.parcel.QuestionData;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 25/6/2016.
 */
public class SurveyQuestionsActivity extends AppCompatActivity implements SQMVCView, SaveQuestionListener, View.OnClickListener {

    private static final String debugTag = SurveyQuestionsActivity.class.getSimpleName();
    private ViewPager mPager;
    private TextView currentPageIndexTxv;
    private SurveyQuestionsPagerAdapter surveyQuestionsPagerAdapter;
    private FloatingActionButton floatingActionButton;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout questionBtnLlt;
    private List<QuestionData> data;
    private List<ObjectAnimator> objectAnimatorList;
    private ObjectAnimatorProperties viewAnimation, scrollViewAnimation, fabAnimation;
    private ObjectAnimator[] objectAnimators;
    private List<ObjectAnimatorProperties> objectAnimatorPropertiesList;
    private Fragment fragment;
    private LinearLayout.LayoutParams questionButtonParams;
    private int viewId, selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_questions_activity);

        Toolbar toolbar         =   (Toolbar) findViewById(R.id.appBar);
        mPager                  =   (ViewPager) findViewById(R.id.surveyQuestionsPager);
        floatingActionButton    =   (FloatingActionButton) findViewById(R.id.saveQuestionFab);
        horizontalScrollView    =   (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        questionBtnLlt          =   (LinearLayout) findViewById(R.id.questionBtnLlt);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) toolbar.setElevation((float) MyApplication.convertPixelToDpAndViceVersa(this, 0, 3));

        if (savedInstanceState == null) {
            SQMVCPresenterImpl SQMVCpresenterImpl = new SQMVCPresenterImpl(this);
            SQMVCpresenterImpl.getSurveyQuestions(new SurveyQuestionBody(getResources().getString(R.string.get_survey_questions), getIntent().getIntExtra(getResources().getString(R.string.survey_id), 0)));
            objectAnimatorList              =   new ArrayList<>();
            objectAnimatorPropertiesList    =   new ArrayList<>();
        }
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position + 1 < surveyQuestionsPagerAdapter.getCount()) {
                    if (floatingActionButton.getTag().equals(getResources().getString(R.string.done))) floatingActionButton.setIcon(ContextCompat.getDrawable(SurveyQuestionsActivity.this, R.drawable.forward), true);
                    floatingActionButton.setTag(getResources().getString(R.string.forward));
                } else {
                    if (floatingActionButton.getTag().equals(getResources().getString(R.string.forward))) floatingActionButton.setIcon(ContextCompat.getDrawable(SurveyQuestionsActivity.this, R.drawable.done), true);
                    floatingActionButton.setTag(getResources().getString(R.string.done));
                }
                for (int i = 0; i < questionBtnLlt.getChildCount(); i++) {
                    if (questionBtnLlt.getChildAt(i).getId() == selectedIndex) {
                        questionBtnLlt.getChildAt(i).setBackgroundColor(ContextCompat.getColor(SurveyQuestionsActivity.this, R.color.primaryColor));
                    }
                }
                questionBtnLlt.getChildAt(position).setBackgroundColor(ContextCompat.getColor(SurveyQuestionsActivity.this, R.color.accentColor));
                selectedIndex = position+1;
                if (horizontalScrollView.getTag().equals(getResources().getString(R.string.expanded))) {
                    helperCollpseAnimation(getResources().getString(R.string.empty_string));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() + 1 < data.size() && surveyQuestionsPagerAdapter.getCount() == mPager.getCurrentItem() + 1) {
                    surveyQuestionsPagerAdapter.add(data.get(mPager.getCurrentItem()+1));
                    fragment = surveyQuestionsPagerAdapter.getCurrentFragment();
                    if (fragment != null && fragment instanceof SurveyQuestionsFragment) {
                        ((SurveyQuestionsFragment) fragment).setCurrentPageIndex(mPager.getCurrentItem() + 2, data.size());
                        Button button = new Button(SurveyQuestionsActivity.this);
                        button.setOnClickListener(SurveyQuestionsActivity.this);
                        if (questionButtonParams != null) {
                            button.setLayoutParams(questionButtonParams);
                        } else {
                            button.setLayoutParams(initializeButtonParams(button));
                        }
                        button.setId(mPager.getCurrentItem() + 2);
                        button.setBackgroundColor(ContextCompat.getColor(SurveyQuestionsActivity.this, R.color.primaryColor));
                        button.setTextColor(ContextCompat.getColor(SurveyQuestionsActivity.this, android.R.color.white));
                        button.setText(String.valueOf(mPager.getCurrentItem() + 2));
                        questionBtnLlt.addView(button);
                    }
                    if (horizontalScrollView.getTag().equals(getResources().getString(R.string.collapsed))) {
                        mPager.setCurrentItem(surveyQuestionsPagerAdapter.getCount(), true);
                    } else {
                        helperCollpseAnimation(getResources().getString(R.string.on_fab_clicked));
                    }
                } else {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        viewId = view.getId();
        if (horizontalScrollView.getTag().equals(getResources().getString(R.string.collapsed))) {
            mPager.setCurrentItem(viewId-1, true);
        } else {
            helperCollpseAnimation(getResources().getString(R.string.on_question_btn_clicked));
        }
    }

    @Override
    public void onSuccessSurveyQuestionsFetched(String surveyTitle, List<QuestionData> data) {
        if (data != null && data.size() > 0) {
            surveyQuestionsPagerAdapter = new SurveyQuestionsPagerAdapter(getSupportFragmentManager(), surveyTitle, data.get(0), data.size());
            mPager.setAdapter(surveyQuestionsPagerAdapter);
            this.data = data;

            fragment = surveyQuestionsPagerAdapter.getCurrentFragment();
            if (fragment != null && fragment instanceof SurveyQuestionsFragment) {
                ((SurveyQuestionsFragment) fragment).setCurrentPageIndex(1, data.size());
                Button button = new Button(SurveyQuestionsActivity.this);
                button.setOnClickListener(this);
                button.setId(mPager.getCurrentItem() + 1);
                button.setBackgroundColor(ContextCompat.getColor(SurveyQuestionsActivity.this, R.color.accentColor));
                button.setTextColor(ContextCompat.getColor(SurveyQuestionsActivity.this, android.R.color.white));
                button.setText("1");
                questionBtnLlt.addView(button);
                button.setLayoutParams(initializeButtonParams(button));
                selectedIndex = 1;
            }
        }
    }

    @Override
    public void onFailure(int code) {

    }

    @Override
    public void onBottomSheetClicked(final View view) {
        if (view != null) {
            if (horizontalScrollView.getTag().equals(getResources().getString(R.string.collapsed))) {
                horizontalScrollView.setTag(getResources().getString(R.string.expanded));
                horizontalScrollView.setVisibility(View.VISIBLE);

                viewAnimation       = new ObjectAnimatorProperties(view, getResources().getString(R.string.float_type), getResources().getString(R.string.translation_y), 0, -(int)MyApplication.convertPixelToDpAndViceVersa(getBaseContext(), 0, 100));
                scrollViewAnimation = new ObjectAnimatorProperties(horizontalScrollView, getResources().getString(R.string.float_type), getResources().getString(R.string.translation_y), 0, -(int)MyApplication.convertPixelToDpAndViceVersa(getBaseContext(), 0, 100));
                fabAnimation        = new ObjectAnimatorProperties(floatingActionButton, getResources().getString(R.string.float_type), getResources().getString(R.string.translation_y), 0, -(int)MyApplication.convertPixelToDpAndViceVersa(getBaseContext(), 0, 100));

                objectAnimatorPropertiesList.add(viewAnimation);
                objectAnimatorPropertiesList.add(scrollViewAnimation);
                objectAnimatorPropertiesList.add(fabAnimation);
                initializeAnimatorSet(objectAnimatorPropertiesList, 300, "");
            } else {
                helperCollpseAnimation(getResources().getString(R.string.empty_string));
                objectAnimatorPropertiesList.clear();
            }
        }
    }

    @Override
    public void onSaveQuestion() {
        if (mPager.getCurrentItem() + 1 < data.size() && surveyQuestionsPagerAdapter.getCount() == mPager.getCurrentItem() + 1) {
            surveyQuestionsPagerAdapter.add(data.get(mPager.getCurrentItem()+1));
            mPager.setCurrentItem(surveyQuestionsPagerAdapter.getCount(), true);
        }
    }

    private void initializeAnimatorSet(List<ObjectAnimatorProperties> objectAnimatorPropertiesList, int duration, final String action) {
        ObjectAnimator objectAnimator = null;
        if (objectAnimatorList != null && objectAnimatorList.size() > 0) objectAnimatorList.clear();
        for (ObjectAnimatorProperties object: objectAnimatorPropertiesList) {
            if (object.getValueType().equals(getResources().getString(R.string.float_type))) {
                objectAnimator = ObjectAnimator.ofFloat(object.getView(), object.getPropertyName(), object.getValueFrom(), object.getValueTo());
            }
            objectAnimatorList.add(objectAnimator);
        }
        if (objectAnimatorList != null) objectAnimators = objectAnimatorList.toArray(new ObjectAnimator[objectAnimatorList.size()]);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimators);
        animatorSet.setDuration(duration);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                if (action.equals(getResources().getString(R.string.on_fab_clicked))) {
                    mPager.setCurrentItem(surveyQuestionsPagerAdapter.getCount(), true);
                } else if (action.equals(getResources().getString(R.string.on_question_btn_clicked))) {
                    if (viewId != 0) mPager.setCurrentItem(viewId - 1, true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }

    private LinearLayout.LayoutParams initializeButtonParams(Button button) {
        questionButtonParams = (LinearLayout.LayoutParams) button.getLayoutParams();
        questionButtonParams.width    =   (int)MyApplication.convertPixelToDpAndViceVersa(getBaseContext(), 0, 60);
        questionButtonParams.height   =   (int)MyApplication.convertPixelToDpAndViceVersa(getBaseContext(), 0, 70);
        questionButtonParams.setMargins(0, 0, (int)MyApplication.convertPixelToDpAndViceVersa(getBaseContext(), 0, 10), 0);
        questionButtonParams.gravity = Gravity.CENTER_VERTICAL;
        return questionButtonParams;
    }

    private void helperCollpseAnimation(String action) {
        horizontalScrollView.setTag(getResources().getString(R.string.collapsed));
        viewAnimation.setValueFrom(-(int) MyApplication.convertPixelToDpAndViceVersa(getBaseContext(), 0, 100));
        scrollViewAnimation.setValueFrom(-(int) MyApplication.convertPixelToDpAndViceVersa(getBaseContext(), 0, 100));
        fabAnimation.setValueFrom(-(int) MyApplication.convertPixelToDpAndViceVersa(getBaseContext(), 0, 100));
        viewAnimation.setValueTo(0);
        scrollViewAnimation.setValueTo(0);
        fabAnimation.setValueTo(0);
        initializeAnimatorSet(objectAnimatorPropertiesList, 300, action);
    }
}
