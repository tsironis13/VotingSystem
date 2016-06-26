package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rey.material.widget.Button;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.POJO.SurveyQuestionBody;
import com.votingsystem.tsiro.SurveyQuestionsMVC.SQMVCPresenterImpl;
import com.votingsystem.tsiro.SurveyQuestionsMVC.SQMVCView;
import com.votingsystem.tsiro.adapters.SurveyQuestionsPagerAdapter;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.interfaces.SaveQuestionListener;
import com.votingsystem.tsiro.mainClasses.SurveyQuestionsActivity;
import com.votingsystem.tsiro.parcel.QuestionData;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.List;

/**
 * Created by giannis on 25/6/2016.
 */
public class SurveyQuestionsFragment extends Fragment {

    private static final String debugTag = SurveyQuestionsFragment.class.getSimpleName();
    private View view;
    private LinearLayout questionContainer;
    private TextView surveyTitleTtv;
    private TextView textView;
    private Button saveQuestionBtn;
    private QuestionData question;
    private SaveQuestionListener saveQuestionListener;
    private int position;

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
        textView                =   (TextView) view.findViewById(R.id.test);
        saveQuestionBtn         =   (Button) view.findViewById(R.id.saveQuestionBtn);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.survey));

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                question = getArguments().getParcelable(getResources().getString(R.string.question));
                surveyTitleTtv.setText(getArguments().getString(getResources().getString(R.string.survey_title)));

                if (question != null && question.getTypeId() != 0) {
                    Log.e(debugTag, question.getTypeId()+"");
                    switch (question.getTypeId()) {
                        case 100:
                            initializeSingleChoiceView(question);
                            break;
                        case 200:
                            initializeManyChoicesView();
                            break;
                        case 300:
                            initializeRankingView();
                            break;
                        case 400:
                            initializeSatisfactionRatingView();
                            break;
                        case 500:
                            initializeSliderView();
                            break;
                        case 600:
                            initializeListPickerView();
                            break;
                        case 700:
                            initializeMatrixView();
                            break;
                        case 800:
                            initializeFreeAnswerView();
                            break;
                    }
                    textView.setText(getResources().getString(R.string.question_title, getArguments().getInt(getResources().getString(R.string.position)), question.getTitle()));
                }
            }
            saveQuestionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveQuestionListener.onSaveQuestion();
                }
            });
        }
    }

    private void initializeSingleChoiceView(QuestionData question) {
        if (question.getAnswers() != null && question.getAnswers().size() > 0) {
            final RadioButton[] rb    =   new RadioButton[question.getAnswers().size()];

            com.rey.material.widget.CompoundButton.OnCheckedChangeListener c = new CompoundButton.OnCheckedChangeListener() {
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
            lp.setMargins((int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 10), 0, (int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 10), (int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 10));
            for (int i =0; i < question.getAnswers().size(); i++) {
                rb[i] = new RadioButton(getActivity());
                rb[i].setId(i);
                rb[i].setOnCheckedChangeListener(c);
                rb[i].setText(getResources().getString(R.string.radio_button_label, question.getAnswers().get(i)));
                rb[i].setTextSize(15);
                questionContainer.addView(rb[i]);
                rb[i].setLayoutParams(lp);
            }
        }
    }

    private void initializeManyChoicesView() {

    }

    private void initializeRankingView() {
        RecyclerView r = new RecyclerView(getActivity());
    }

    private void initializeSatisfactionRatingView() {

    }

    private void initializeSliderView() {
    }

    private void initializeListPickerView() {

    }

    private void initializeMatrixView() {
    }

    private void initializeFreeAnswerView() {
    }
}
