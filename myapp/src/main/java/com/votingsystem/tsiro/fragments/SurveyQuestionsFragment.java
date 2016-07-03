package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.EditText;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Slider;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.RecyclerViewStuff.SImpleItemTouchHelperCallback;
import com.votingsystem.tsiro.adapters.SurveyListPickerQuestionRcvAdapter;
import com.votingsystem.tsiro.adapters.SurveyRankingQuestionRcvAdapter;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.interfaces.SaveQuestionListener;
import com.votingsystem.tsiro.parcel.QuestionData;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 25/6/2016.
 */
public class SurveyQuestionsFragment extends Fragment implements View.OnClickListener {

    private static final String debugTag = SurveyQuestionsFragment.class.getSimpleName();
    private View view;
    private LinearLayout questionContainer, pageIndexLlt;
    private TextView surveyTitleTtv, questionTitleTtv, pageIndexTtv;
    private QuestionData question;
    private SaveQuestionListener saveQuestionListener;
    private int position;
    private ImageView angryImv, sadImv, neutralImv, happyImv, vhappyImv, arrowImv;
    private ImageView[] icons;
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) saveQuestionListener = (SaveQuestionListener) context;
    }

    public static SurveyQuestionsFragment newInstance(String surveyTitle, QuestionData question, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("survey_title", surveyTitle);
        bundle.putParcelable("question", question);
        bundle.putInt("position", position);
        SurveyQuestionsFragment surveyQuestionsFragment = new SurveyQuestionsFragment();
        surveyQuestionsFragment.setArguments(bundle);
        return surveyQuestionsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view  =   inflater.inflate(R.layout.fragment_survey_questions, container, false);
        questionContainer       =   (LinearLayout) view.findViewById(R.id.questionContainer);
        surveyTitleTtv          =   (TextView) view.findViewById(R.id.surveyTitleTtv);
        questionTitleTtv        =   (TextView) view.findViewById(R.id.questionTitleTtv);
        pageIndexLlt            =   (LinearLayout) view.findViewById(R.id.pageIndexLlt);
        arrowImv                =   (ImageView) view.findViewById(R.id.arrowImv);
        pageIndexTtv            =   (TextView) view.findViewById(R.id.pageIndexTtv);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pageIndexLlt.setElevation((float) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 4));
            pageIndexLlt.setTranslationZ((float) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 4));
        }
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.survey));

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                question = getArguments().getParcelable(getResources().getString(R.string.question));
                surveyTitleTtv.setText(getArguments().getString(getResources().getString(R.string.survey_title)));

                if (question != null && question.getTypeId() != 0) {
                    switch (question.getTypeId()) {
                        case 100:
                            initializeSingleChoiceView(question);
                            break;
                        case 200:
                            initializeManyChoicesView(question);
                            break;
                        case 300:
                            initializeRankingView(question);
                            break;
                        case 400:
                            initializeSatisfactionRatingView();
                            break;
                        case 500:
                            initializeSliderView(question);
                            break;
                        case 600:
                            initializeListPickerView(question);
                            break;
                        case 700:
                            initializeMatrixView(question);
                            break;
                        case 800:
                            initializeFreeAnswerView();
                            break;
                    }
                    questionTitleTtv.setText(getResources().getString(R.string.question_title, getArguments().getInt(getResources().getString(R.string.position)), question.getTitle()));
                }
            }
            pageIndexLlt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveQuestionListener.onBottomSheetClicked(view);
                }
            });
        }
    }

    private void initializeSingleChoiceView(QuestionData question) {
        if (question.getAnswers() != null && question.getAnswers().size() > 0) {
            final RadioButton[] rb = new RadioButton[question.getAnswers().size()];

            CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        for (RadioButton aRb : rb) {
                            aRb.setChecked(aRb == compoundButton);
                        }
                    }
                }
            };
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, (int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 10));
            for (int i =0; i < question.getAnswers().size(); i++) {
                rb[i] = new RadioButton(getActivity());
                rb[i].setId(i);
                rb[i].setOnCheckedChangeListener(onCheckedChangeListener);
                rb[i].setText(getResources().getString(R.string.radio_button_label, question.getAnswers().get(i)));
                rb[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                rb[i].setGravity(Gravity.CENTER_VERTICAL);
                questionContainer.addView(rb[i]);
                rb[i].setLayoutParams(lp);
            }
        }
    }

    private void initializeManyChoicesView(QuestionData question) {
        if (question.getAnswers() != null && question.getAnswers().size() > 0) {
            CheckBox[] checkBox = new CheckBox[question.getAnswers().size()];

            for (int i = 0; i < question.getAnswers().size(); i++) {
                checkBox[i] = new CheckBox(getActivity());
                checkBox[i].setText(question.getAnswers().get(i));
                questionContainer.addView(checkBox[i]);
            }
        }
    }

    private void initializeRankingView(QuestionData question) {
        if (question.getAnswers() != null && question.getAnswers().size() > 0) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

            RecyclerView rankingView;
            LinearLayout rankingLlt = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.question_ranking_template, (ViewGroup) getActivity().findViewById(R.id.questionRankingLlt));
            if (rankingLlt != null && rankingLlt.getChildAt(1) instanceof RecyclerView) {
                questionContainer.addView(rankingLlt);
                rankingView = (RecyclerView) rankingLlt.findViewById(R.id.questionRankingRcv);
                rankingView.setHasFixedSize(true);
                rankingView.setLayoutManager(linearLayoutManager);

                SurveyRankingQuestionRcvAdapter surveyQuestionRcvAdapter = new SurveyRankingQuestionRcvAdapter(question.getAnswers());
                rankingView.setAdapter(surveyQuestionRcvAdapter);
                ItemTouchHelper.Callback callback = new SImpleItemTouchHelperCallback(surveyQuestionRcvAdapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(rankingView);
            }
        }
    }

    private void initializeSatisfactionRatingView() {
        LinearLayout satisfRatingView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.question_satisfaction_rating_template, (ViewGroup) getActivity().findViewById(R.id.satisfactionLlt));
        questionContainer.addView(satisfRatingView);
        if (satisfRatingView != null) {
            angryImv      =   (ImageView) getActivity().findViewById(R.id.angryImv);
            sadImv        =   (ImageView) getActivity().findViewById(R.id.sadImv);
            neutralImv    =   (ImageView) getActivity().findViewById(R.id.neutralImv);
            happyImv      =   (ImageView) getActivity().findViewById(R.id.happyImv);
            vhappyImv     =   (ImageView) getActivity().findViewById(R.id.vhappyImv);
            angryImv.setOnClickListener(this);
            sadImv.setOnClickListener(this);
            neutralImv.setOnClickListener(this);
            happyImv.setOnClickListener(this);
            vhappyImv.setOnClickListener(this);
            icons = new ImageView[]{angryImv, sadImv, neutralImv, happyImv, vhappyImv};
        }
    }

    private void initializeSliderView(final QuestionData question) {
        if (question.getAnswers() != null && question.getAnswers().size() > 0) {
            LinearLayout view   =   (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.question_slider_template, (ViewGroup) getActivity().findViewById(R.id.questionTemplateLlt));
            questionContainer.addView(view);
            if (view != null) {
                Slider slider       =   (Slider) getActivity().findViewById(R.id.questionSldr);
                final TextView textView   =   (TextView) getActivity().findViewById(R.id.questionSliderTtv);
                slider.setValueRange(1, question.getAnswers().size(), true);

                textView.setText(question.getAnswers().get(0));
                slider.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
                    @Override
                    public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                        textView.setText(question.getAnswers().get(newValue-1));

                    }
                });
            }
        }
    }

    private void initializeListPickerView(QuestionData question) {
        if (question.getAnswers() != null && question.getAnswers().size() > 0) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

            RecyclerView listPickerView = (RecyclerView) LayoutInflater.from(getActivity()).inflate(R.layout.question_ranking_template, (ViewGroup) getActivity().findViewById(R.id.questionRankingRcv), false);

            questionContainer.addView(listPickerView);
            listPickerView.setHasFixedSize(true);
            listPickerView.setLayoutManager(linearLayoutManager);

            SurveyListPickerQuestionRcvAdapter surveyListPickerQuestionRcvAdapter = new SurveyListPickerQuestionRcvAdapter(question.getAnswers());
            listPickerView.setAdapter(surveyListPickerQuestionRcvAdapter);
        }
    }

    private void initializeMatrixView(QuestionData question) {
        if (question.getAnswers() != null && question.getAnswers().size() > 0) {
            TextView[] textViews  = new TextView[question.getAnswers().size()];
            LinearLayout matrixView;
            onCheckedChangeListener1 = new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    Log.e(debugTag, radioGroup.getTag()+"");

                    LinearLayout parent = (LinearLayout) radioGroup.getParent();
                    LinearLayout matrix = (LinearLayout) parent.getChildAt(1);
                    if (radioGroup.getTag().equals("rGroup_1")) {
                        if (matrix.findViewWithTag("matrixScalesLlt_"+1).getVisibility() == View.GONE) matrix.findViewWithTag("matrixScalesLlt_"+1).setVisibility(View.VISIBLE);
                    } else if (radioGroup.getTag().equals("rGroup_2")) {
                        if (matrix.findViewWithTag("matrixScalesLlt_"+2).getVisibility() == View.GONE) matrix.findViewWithTag("matrixScalesLlt_"+2).setVisibility(View.VISIBLE);
                    } else {
                        if (matrix.findViewWithTag("matrixScalesLlt_"+3).getVisibility() == View.GONE) matrix.findViewWithTag("matrixScalesLlt_"+3).setVisibility(View.VISIBLE);
                    }
                }
            };
            for(int i = 0; i < question.getAnswers().size(); i++) {
                textViews[i] = new TextView(getActivity());
                textViews[i].setText(question.getAnswers().get(i));
                questionContainer.addView(textViews[i]);
                matrixView   =   (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.question_matrix_scale_template, (ViewGroup) getActivity().findViewById(R.id.matrixContainerLlt), false);
                if (matrixView.getChildAt(0) != null && matrixView.getChildAt(1) != null && matrixView.getChildAt(0) instanceof RadioGroup) {
                    ((RadioGroup) matrixView.getChildAt(0)).setOnCheckedChangeListener(onCheckedChangeListener1);
                    matrixView.getChildAt(0).setTag("rGroup_"+(i+1));
                    matrixView.getChildAt(1).setTag("matrixScalesLlt_"+(i+1));

                }
                questionContainer.addView(matrixView);
            }
        }
    }

    private void initializeFreeAnswerView() {
        EditText freeAnswerView = (EditText) LayoutInflater.from(getActivity()).inflate(R.layout.question_free_answer_template, (ViewGroup) getActivity().findViewById(R.id.freeAnswerEdt));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) freeAnswerView.setElevation((float) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 4));
        questionContainer.addView(freeAnswerView);
    }

    @Override
    public void onClick(View view) {
        if (view instanceof ImageView) {
            for (ImageView imv : icons) {
                if (imv.getTag().equals(getResources().getString(R.string.satsf_icon_selected))) {
                    imv.clearColorFilter();
                    imv.setTag("");
                }
            }
        }
        switch (view.getId()) {
            case R.id.angryImv:
                angryImv.setColorFilter(ContextCompat.getColor(getActivity(),R.color.angry_fill_color));
                angryImv.setTag(getResources().getString(R.string.satsf_icon_selected));
                break;
            case R.id.sadImv:
                sadImv.setColorFilter(ContextCompat.getColor(getActivity(),R.color.sad_fill_color));
                sadImv.setTag(getResources().getString(R.string.satsf_icon_selected));
                break;
            case R.id.neutralImv:
                neutralImv.setColorFilter(ContextCompat.getColor(getActivity(),R.color.neutral_fill_color));
                neutralImv.setTag(getResources().getString(R.string.satsf_icon_selected));
                break;
            case R.id.happyImv:
                happyImv.setColorFilter(ContextCompat.getColor(getActivity(),R.color.happy_fill_color));
                happyImv.setTag(getResources().getString(R.string.satsf_icon_selected));
                break;
            case R.id.vhappyImv:
                vhappyImv.setColorFilter(ContextCompat.getColor(getActivity(),R.color.vhappy_fill_color));
                vhappyImv.setTag(getResources().getString(R.string.satsf_icon_selected));
                break;
        }
    }

    public void setCurrentPageIndex(int index, int size) {
        pageIndexTtv.setText(getResources().getString(R.string.page_index_text, index, size));
    }

//    public void onBottomSheetAction(String action) {
//        if (isAdded()) {
//            Log.e(debugTag, arrowImv+"");
//            if (action.equals(getResources().getString(R.string.expanded))) {
//                Log.e(debugTag, action+" ACTION");
//                arrowImv.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.arrow_down));
//            } else {
//                Log.e(debugTag, action+" ACTION");
//                arrowImv.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.arrow_up));
//            }
//        }
//
//    }
}
