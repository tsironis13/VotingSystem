package com.votingsystem.tsiro.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.LinearLayout;
import com.votingsystem.tsiro.POJO.ObjectAnimatorProperties;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.interfaces.UpdateNewSurveyObj;
import com.votingsystem.tsiro.votingsystem.R;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 19/9/2016.
 */
public class NewSurveyQuestionFragment extends Fragment implements View.OnClickListener {

    private static final String debugTag = NewSurveyQuestionFragment.class.getSimpleName();
    private View view;
    private LinearLayout answersContainerLlt;
    private EditText questionTitleEdt;
    private Button addAnswerViewBtn;
    private UpdateNewSurveyObj updateNewSurveyObj;
    private int categoryId;
    private String questionType;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity) this.updateNewSurveyObj = (UpdateNewSurveyObj) context;
    }

    public static NewSurveyQuestionFragment newInstance(int categoryId, String questionType) {
        Bundle bundle = new Bundle();
        bundle.putInt("category_id", categoryId);
        bundle.putString("title", questionType);
        NewSurveyQuestionFragment newSurveyDetails = new NewSurveyQuestionFragment();
        newSurveyDetails.setArguments(bundle);
        return newSurveyDetails;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view = inflater.inflate(R.layout.fragment_new_survey_question, container, false);
        questionTitleEdt        = (EditText) view.findViewById(R.id.questionTitleEdt);
        answersContainerLlt     = (LinearLayout) view.findViewById(R.id.answersContainerLlt);
        addAnswerViewBtn        = (Button) view.findViewById(R.id.addAnswerViewBtn);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            categoryId      = getArguments().getInt(getResources().getString(R.string.category_id));
            questionType    = getArguments().getString(getResources().getString(R.string.title));
        }

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.clear_white);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(questionType);
        }
        updateNewSurveyObj.initializeSpannableText(questionTitleEdt, getResources().getString(R.string.question_text), true);
        addAnswerViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAnswerView(answersContainerLlt.getChildCount());
            }
        });
        setHasOptionsMenu(true);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            if (view instanceof FloatingActionButton) {
                Log.e(debugTag, view.getTag()+"");
                View parent = answersContainerLlt.findViewWithTag(view.getTag());
                if (parent != null) onViewRemovalAnimation(parent);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_question_menu, menu);
    }

    private void onViewRemovalAnimation(final View view) {
        ObjectAnimator translationAnimator  = ObjectAnimator.ofFloat(view, getResources().getString(R.string.translation_y), 0 , 50);
        ObjectAnimator alphaAnimator        = ObjectAnimator.ofFloat(view, getResources().getString(R.string.alpha), 1 , 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationAnimator, alphaAnimator);
        animatorSet.setDuration(300);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                answersContainerLlt.removeView(view);
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }

    private void addAnswerView(int childs) {
        LinearLayout answerContainer = new LinearLayout(getActivity());
        answerContainer.setTag(childs);
        answerContainer.setWeightSum(100f);
        LinearLayout.LayoutParams answerContainerlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 55));
        answerContainerlp.bottomMargin = (int) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 15);
        answerContainer.setLayoutParams(answerContainerlp);

        EditText answerEdt = new EditText(getActivity());
        LinearLayout.LayoutParams answerEdtlp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        answerEdtlp.weight = 100f;

        answerEdt.setPadding(0, 0, (int) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 10), (int) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 5));
        answerEdt.applyStyle(R.style.newSurveyTitle);
        answerEdt.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.new_survey_hint_color));
        updateNewSurveyObj.initializeSpannableText(answerEdt, getResources().getString(R.string.answer), false);
        answerEdt.setLayoutParams(answerEdtlp);

        FloatingActionButton floatingActionButton = new FloatingActionButton(getActivity());
        LinearLayout.LayoutParams floatingActionButtonlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        floatingActionButtonlp.setMargins(0, (int) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, -10), 0, 0);
        floatingActionButton.setTag(childs);
        floatingActionButton.setSize(FloatingActionButton.SIZE_MINI);
        floatingActionButton.setIcon(R.drawable.remove);
        floatingActionButton.setColorNormal(ContextCompat.getColor(getActivity(), R.color.password_error_txv_text_color));
        floatingActionButton.setColorPressed(ContextCompat.getColor(getActivity(), R.color.remove_answer_color_pressed));
        floatingActionButton.setOnClickListener(this);

        answerContainer.addView(answerEdt);
        answerContainer.addView(floatingActionButton);
        answersContainerLlt.addView(answerContainer);

    }


}
