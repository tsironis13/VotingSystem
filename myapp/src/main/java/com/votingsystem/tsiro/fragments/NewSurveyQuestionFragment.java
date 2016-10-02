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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.rey.material.widget.Button;
import com.rey.material.widget.CompoundButton;
import com.rey.material.widget.EditText;
import com.rey.material.widget.LinearLayout;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Switch;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.POJO.NewSurveyQuestion;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.interfaces.UpdateNewSurveyObj;
import com.votingsystem.tsiro.votingsystem.R;
import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 19/9/2016.
 */
public class NewSurveyQuestionFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String debugTag = NewSurveyQuestionFragment.class.getSimpleName();
    private View view;
    private EditText questionTitleEdt;
    private TextView answersTtv;
    private LinearLayout answersContainerLlt;
    private Button addAnswerViewBtn, cancelBtn, saveBtn;
    private RadioButton singleChoiceRbtn, multipleChoiceRbtn;
    private Switch mandatoryQuestionSwitch;
    private UpdateNewSurveyObj updateNewSurveyObj;
    private String action;
    private String questionType;
    private int key, categoryId;
    private int selectedRadioBtnId = R.id.singleChoiceRbtn;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity) this.updateNewSurveyObj = (UpdateNewSurveyObj) context;
    }

    public static NewSurveyQuestionFragment newInstance(NewSurveyQuestion newSurveyQuestion) {
        Bundle bundle = new Bundle();
        bundle.putInt("key", newSurveyQuestion.getKey());
        bundle.putString("action", newSurveyQuestion.getAction());
        bundle.putInt("category_id", newSurveyQuestion.getCategoryId());
        bundle.putString("title", newSurveyQuestion.getQuestionType());
        if (newSurveyQuestion.getAction().equals("edit")) {
            if (newSurveyQuestion.getQuestion() != null) bundle.putString("question", newSurveyQuestion.getQuestion());
            if (newSurveyQuestion.getAnswersList() != null && newSurveyQuestion.getAnswersList().size() > 0) bundle.putStringArrayList("answers", (ArrayList<String>) newSurveyQuestion.getAnswersList());
            bundle.putBoolean("mandatory", newSurveyQuestion.isMandatory());
            bundle.putBoolean("is_single_choice", newSurveyQuestion.isSingleChoice());
        }
        NewSurveyQuestionFragment newSurveyDetails = new NewSurveyQuestionFragment();
        newSurveyDetails.setArguments(bundle);
        return newSurveyDetails;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view = inflater.inflate(R.layout.fragment_new_survey_question, container, false);
        questionTitleEdt        = (EditText) view.findViewById(R.id.questionTitleEdt);
        answersTtv              = (TextView) view.findViewById(R.id.answersTtv);
        answersContainerLlt     = (LinearLayout) view.findViewById(R.id.answersContainerLlt);
        addAnswerViewBtn        = (Button) view.findViewById(R.id.addAnswerViewBtn);
        singleChoiceRbtn        = (RadioButton) view.findViewById(R.id.singleChoiceRbtn);
        multipleChoiceRbtn      = (RadioButton) view.findViewById(R.id.multipleChoiceRbtn);
        mandatoryQuestionSwitch = (Switch) view.findViewById(R.id.mandatoryQuestionSwitch);
        cancelBtn               = (Button) view.findViewById(R.id.cancelBtn);
        saveBtn                 = (Button) view.findViewById(R.id.saveBtn);
        singleChoiceRbtn.setOnCheckedChangeListener(this);
        multipleChoiceRbtn.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            key             = getArguments().getInt(getResources().getString(R.string.key));
            action          = getArguments().getString(getResources().getString(R.string.action));
            categoryId      = getArguments().getInt(getResources().getString(R.string.category_id));
            questionType    = getArguments().getString(getResources().getString(R.string.title));
            if (categoryId == 800 || categoryId == 400) {
                answersTtv.setVisibility(View.GONE);
                answersContainerLlt.setVisibility(View.GONE);
                addAnswerViewBtn.setVisibility(View.GONE);
            }
            if (categoryId != 100) {
                singleChoiceRbtn.setVisibility(View.GONE);
                multipleChoiceRbtn.setVisibility(View.GONE);
            }
            if (action.equals(getResources().getString(R.string.edit))) {
                cancelBtn.setTag(getResources().getString(R.string.edit));
                cancelBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.note_text_color));
                cancelBtn.setText(getResources().getString(R.string.delete));

                String question         = getArguments().getString(getResources().getString(R.string.question));
                List<String> answers    = getArguments().getStringArrayList(getResources().getString(R.string.tag_answers));
                boolean mandatory       = getArguments().getBoolean(getResources().getString(R.string.mandatory));
                boolean isSingleChoice  = getArguments().getBoolean(getResources().getString(R.string.is_single_choice));
                if (answers != null) {
                    for (int i = 0; i < answers.size(); i++) {
                        addAnswerView(i, answers.get(i));
                    }
                }
                questionTitleEdt.setText(question);
                if (mandatory) mandatoryQuestionSwitch.setChecked(true);
                if (!isSingleChoice) multipleChoiceRbtn.setChecked(true);
            }
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
                addAnswerView(answersContainerLlt.getChildCount(), getResources().getString(R.string.empty_string));
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag().equals(getResources().getString(R.string.cancel))) {
                    updateNewSurveyObj.initializeDialogs(1, getResources().getString(R.string.discard_question_dialog_msg));
                } else {
                    updateNewSurveyObj.getNewSurveyObj().getNewSurveyQuestionSparseArray().delete(key);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manipulateQuestion();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        manipulateQuestion();
        return true;
    }

    private void manipulateQuestion() {
        boolean answersFilledOut = true;
        List<String> answersList = new ArrayList<>();
        String question = questionTitleEdt.getText().toString().trim();
        if (question.matches(getResources().getString(R.string.empty_string))) {
            updateNewSurveyObj.showSnackBar(getResources().getString(R.string.fill_out_required_fields), null);
        } else {
            if (categoryId != 800 && categoryId != 400) {
                if (answersContainerLlt.getChildCount() < 2) {
                    updateNewSurveyObj.showSnackBar(getResources().getString(R.string.add_answers_note), null);
                } else {
                    for (int i = 0; i < answersContainerLlt.getChildCount(); i++) {
                        if (answersContainerLlt.getChildAt(i) != null && answersContainerLlt.getChildAt(i) instanceof LinearLayout) {
                            LinearLayout linearLayout = (LinearLayout) answersContainerLlt.getChildAt(i);
                            if (linearLayout.getChildAt(0) != null && linearLayout.getChildAt(0) instanceof EditText) {
                                EditText answerEdt = (EditText) linearLayout.getChildAt(0);
                                String answer = answerEdt.getText().toString().trim();
                                if (answer.matches(getResources().getString(R.string.empty_string))) {
                                    updateNewSurveyObj.showSnackBar(getResources().getString(R.string.fill_out_answers), null);
                                    answersFilledOut = false;
                                    break;
                                } else {
                                    answersList.add(answer);
                                }
                            }
                        }
                    }
                    if (answersFilledOut) {
                        boolean single_choice = false;
                        if (categoryId == 100) if (selectedRadioBtnId == R.id.singleChoiceRbtn) single_choice = true;
                        NewSurveyQuestion newSurveyQuestion = new NewSurveyQuestion(key, getResources().getString(R.string.edit), question, questionType, answersList, categoryId, single_choice, mandatoryQuestionSwitch.isChecked());
                        updateNewSurveyObj(newSurveyQuestion, action);
                    }
                }
            } else {
                NewSurveyQuestion newSurveyQuestion = new NewSurveyQuestion(key, getResources().getString(R.string.edit), question, questionType, null, categoryId, false, mandatoryQuestionSwitch.isChecked());
                updateNewSurveyObj(newSurveyQuestion, action);
            }
        }
    }

    private void updateNewSurveyObj(NewSurveyQuestion newSurveyQuestion, String action) {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (updateNewSurveyObj.getNewSurveyObj() != null) {
            if (action.equals(getResources().getString(R.string.add))) {
                updateNewSurveyObj.getNewSurveyObj().getNewSurveyQuestionSparseArray().put(key, newSurveyQuestion);
            } else {
                updateNewSurveyObj.getNewSurveyObj().getNewSurveyQuestionSparseArray().append(newSurveyQuestion.getKey(), newSurveyQuestion);
            }
        }
        getActivity().getSupportFragmentManager().popBackStack();
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

    private void addAnswerView(int childs, String text) {
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
        if (!text.equals(getResources().getString(R.string.empty_string))) answerEdt.setText(text);
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

    @Override
    public void onCheckedChanged(android.widget.CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            singleChoiceRbtn.setChecked(singleChoiceRbtn == compoundButton);
            multipleChoiceRbtn.setChecked(multipleChoiceRbtn == compoundButton);
            selectedRadioBtnId = compoundButton.getId();
        }
    }
}
