package com.votingsystem.tsiro.mainClasses;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.rey.material.widget.Button;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.POJO.ObjectAnimatorProperties;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.POJO.SurveyAnswersList;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by giannis on 25/6/2016.
 */
public class SurveyQuestionsActivity extends AppCompatActivity implements SQMVCView, SaveQuestionListener, View.OnClickListener {

    private static final String debugTag = SurveyQuestionsActivity.class.getSimpleName();
    private ViewPager mPager;
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
    private int viewId, selectedIndex, surveyId;
    private SQMVCPresenterImpl SQMVCpresenterImpl;
    private Menu menu;
    private DialogPlus dialog;
    private List<Integer> mandatoryQuestionsCompleted;
    private ProgressDialog progressDialog;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    Intent intent = new Intent(SurveyQuestionsActivity.this, SurveysActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) toolbar.setElevation((float) MyApplication.convertPixelToDpAndViceVersa(this, 0, 3));

        if (savedInstanceState == null) {
            initializeProgressDialog();
            mandatoryQuestionsCompleted = new ArrayList<>();
            surveyId                    = getIntent().getIntExtra(getResources().getString(R.string.survey_id), 0);
            SQMVCpresenterImpl          = new SQMVCPresenterImpl(this);
            SQMVCpresenterImpl.getSurveyQuestions(new SurveyQuestionBody(getResources().getString(R.string.get_survey_questions), surveyId));
            objectAnimatorList              =   new ArrayList<>();
            objectAnimatorPropertiesList    =   new ArrayList<>();
        }
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                if (data.get(mPager.getCurrentItem()).isMandatory() == 1) {
                    if (position == 1 && data.get(0).isMandatory() == 1) mandatoryQuestionsCompleted.add(0);
                    mandatoryQuestionsCompleted.add(mPager.getCurrentItem());
                }

                if (position + 1 == data.size()) menu.findItem(R.id.postSurveyAnswersItem).setVisible(true);
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
                    if (data.get(mPager.getCurrentItem()).isMandatory() == 1) {
                        if (checkMandatoryQuestionNotFilledOut(mPager.getCurrentItem(), data.get(mPager.getCurrentItem()).getTypeId())) {
                            initializeMandatoryDialogPlus(getResources().getString(R.string.mandatory_dialog_title));
                        } else {
                            setNextQuestionAndBottomSheetStuff();
                        }
                    } else {
                        setNextQuestionAndBottomSheetStuff();
                    }
                } else if (mPager.getCurrentItem() + 1 < surveyQuestionsPagerAdapter.getCount()){
                    if (data.get(mPager.getCurrentItem()).isMandatory() == 1) {
                        if (checkMandatoryQuestionNotFilledOut(mPager.getCurrentItem(), data.get(mPager.getCurrentItem()).getTypeId())) {
                            initializeMandatoryDialogPlus(getResources().getString(R.string.mandatory_dialog_title));
                        } else {
                            mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                        }
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                    }
                } else {
                    boolean pendingQuestionsToFillOut = false;
                    if (data.get(mPager.getCurrentItem()).isMandatory() == 1) {
                        if (checkMandatoryQuestionNotFilledOut(mPager.getCurrentItem(), data.get(mPager.getCurrentItem()).getTypeId())) {
                            initializeMandatoryDialogPlus(getResources().getString(R.string.mandatory_dialog_title));
                        } else {
                            for (int i = 0; i < mandatoryQuestionsCompleted.size(); i++) {
                                if (checkMandatoryQuestionNotFilledOut(mandatoryQuestionsCompleted.get(i), data.get(mandatoryQuestionsCompleted.get(i)).getTypeId())) {
                                    initializeMandatoryDialogPlus(getResources().getString(R.string.complete_all_mandatory_questions));
                                    pendingQuestionsToFillOut = true;
                                    break;
                                }
                            }
                            if (!pendingQuestionsToFillOut) storeQuestionsAnswers();
                        }
                    } else {
                        storeQuestionsAnswers();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (data != null) if (mPager.getCurrentItem() + 1 == data.size())floatingActionButton.setIcon(ContextCompat.getDrawable(SurveyQuestionsActivity.this, R.drawable.done), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.survey_questions_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean pendingQuestionsToFillOut = false;
        switch (item.getItemId()) {
            case R.id.postSurveyAnswersItem:
                if (data.get(mPager.getCurrentItem()).isMandatory() == 1) {
                    if (checkMandatoryQuestionNotFilledOut(mPager.getCurrentItem(), data.get(mPager.getCurrentItem()).getTypeId())) {
                         initializeMandatoryDialogPlus(getResources().getString(R.string.mandatory_dialog_title));
                    } else {
                        for (int i = 0; i < mandatoryQuestionsCompleted.size(); i++) {
                            if (checkMandatoryQuestionNotFilledOut(mandatoryQuestionsCompleted.get(i), data.get(mandatoryQuestionsCompleted.get(i)).getTypeId())) {
                                initializeMandatoryDialogPlus(getResources().getString(R.string.complete_all_mandatory_questions));
                                pendingQuestionsToFillOut = true;
                                break;
                            }
                        }
                        if (!pendingQuestionsToFillOut) storeQuestionsAnswers();
                    }
                } else {
                    storeQuestionsAnswers();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            Intent intent = new Intent(SurveyQuestionsActivity.this, SurveysActivity.class);
            startActivity(intent);
        }
        if (data != null) if (keyCode == KeyEvent.KEYCODE_BACK && mPager.getCurrentItem() + 1 != data.size()) floatingActionButton.setIcon(ContextCompat.getDrawable(SurveyQuestionsActivity.this, R.drawable.done), true);
        return super.onKeyDown(keyCode, event);
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
    public void onSuccessSurveyQuestionsFetched(final String surveyTitle, final List<QuestionData> data) {
        if (progressDialog.isShowing()) {
            this.data = data;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    if (data != null && data.size() > 0) {
                        surveyQuestionsPagerAdapter = new SurveyQuestionsPagerAdapter(getSupportFragmentManager(), surveyTitle, data.get(0), data.size());
                        mPager.setAdapter(surveyQuestionsPagerAdapter);
                        //this.data = data;
                        fragment = surveyQuestionsPagerAdapter.getCurrentFragment();
                        if (fragment != null && fragment instanceof SurveyQuestionsFragment) {
                            ((SurveyQuestionsFragment) fragment).setCurrentPageIndex(1, data.size());
                            Button button = new Button(SurveyQuestionsActivity.this);
                            button.setOnClickListener(SurveyQuestionsActivity.this);
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
            }, 1500);
        }
    }

    @Override
    public void onSuccessSurveyDetailsFetched(final SurveyDetailsData surveyDetailsData) {
        if (progressDialog.isShowing()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    finish();
                    Bundle bundle = new Bundle();
                    bundle.putString(getResources().getString(R.string.details_activ_action_key), getResources().getString(R.string.details_activ_action));
                    bundle.putParcelable(getResources().getString(R.string.data_parcelable_key), surveyDetailsData);
                    Intent intent = new Intent(SurveyQuestionsActivity.this, SurveyDetailsActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }, 1500);
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

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.message));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void setNextQuestionAndBottomSheetStuff() {
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
    }

    private void initializeMandatoryDialogPlus(String text) {
        if (dialog != null) {
            View view = dialog.getHolderView();
            if (view != null) {
                TextView textView = (TextView) view.findViewById(R.id.mandatoryDialogTtv);
                textView.setText(text);
            }
        } else {
            dialog = DialogPlus.newDialog(this)
                    .setContentHolder(new ViewHolder(R.layout.mandatory_dialog))
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(DialogPlus dialog, View view) {
                            if (view.getId() == R.id.confirmMandatoryDialogBtn) dialog.dismiss();
                        }
                    })
                    .create();
        }
        dialog.show();
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

    private void storeQuestionsAnswers() {
        int answer = 0;
        String textAnswer = "";
        List<String> answersList = null;
        List<Integer> integerList;
        HashMap<Integer, Integer> integerHashMap = null;
        List<SurveyAnswersList> surveyAnswersList;

        surveyAnswersList = new ArrayList<>();
        for (int i = 0; i < surveyQuestionsPagerAdapter.getCount(); i++) {
            SurveyQuestionsFragment fragment = (SurveyQuestionsFragment) surveyQuestionsPagerAdapter.getFragmentAtPosition(i);
            if (data.get(i).getTypeId() == 100 || data.get(i).getTypeId() == 400 || data.get(i).getTypeId() == 500 || data.get(i).getTypeId() == 600) {
                answer = fragment.getSingleChoiceSelectedId();
            } else if (data.get(i).getTypeId() == 200) {
                integerList = fragment.getMultipleChoiceSelectedIds();
                answersList  = new ArrayList<>();
                for (Integer item: integerList) {
                    answersList.add(String.valueOf(item));
                }
            } else if (data.get(i).getTypeId() == 300) {
                answersList = fragment.getRankingViewIndexs();
            } else if (data.get(i).getTypeId() == 700) {
                integerHashMap = fragment.getMatrixViewIndexs();
            } else if (data.get(i).getTypeId() == 800) {
                textAnswer = fragment.getFreeAnswerView();
            }
            surveyAnswersList.add(new SurveyAnswersList(i, answer, data.get(i).getTypeId(), textAnswer, answersList, integerHashMap));
        }
        SurveyAnswersBody surveyAnswersBody = new SurveyAnswersBody(getResources().getString(R.string.get_survey_stats), LoginActivity.getSessionPrefs(getApplicationContext()).getInt(getResources().getString(R.string.user_id), 0), surveyId, surveyAnswersList);
        SQMVCpresenterImpl.uploadSurveyAnswers(surveyAnswersBody);
        initializeProgressDialog();
    }

    private boolean checkMandatoryQuestionNotFilledOut(int index, int questionId) {
        SurveyQuestionsFragment fragment = (SurveyQuestionsFragment) surveyQuestionsPagerAdapter.getFragmentAtPosition(index);
        if (questionId == 100 || questionId == 400 || questionId == 600) {
            return fragment.getSingleChoiceSelectedId() == -1;
        } else if (questionId == 200) {
            return fragment.getMultipleChoiceSelectedIds().isEmpty();
        } else if (questionId == 700) {
            return fragment.getMatrixViewIndexs().containsValue(-1);
        } else if (questionId == 800){
            return fragment.getFreeAnswerView() == null;
        } else {
            return false;
        }
    }
}
