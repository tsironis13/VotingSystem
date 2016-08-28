package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.EditText;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Slider;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.ObserverPattern.RecyclerViewTouchListener;
import com.votingsystem.tsiro.RecyclerViewStuff.SImpleItemTouchHelperCallback;
import com.votingsystem.tsiro.adapters.SurveyListPickerQuestionRcvAdapter;
import com.votingsystem.tsiro.adapters.SurveyRankingQuestionRcvAdapter;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.interfaces.RecyclerViewClickListener;
import com.votingsystem.tsiro.interfaces.SaveQuestionListener;
import com.votingsystem.tsiro.parcel.QuestionData;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by giannis on 25/6/2016.
 */
public class SurveyQuestionsFragment extends Fragment implements View.OnClickListener {

    private static final String debugTag = SurveyQuestionsFragment.class.getSimpleName();
    private View view;
    private LinearLayout questionContainer, pageIndexLlt;
    private TextView surveyTitleTtv, questionTitleTtv, pageIndexTtv, mandatoryNoteTtv;
    private EditText freeAnswerView;
    private QuestionData question;
    private SaveQuestionListener saveQuestionListener;
    private int selectedIndex = -1;
    private List<ImageView> icons;
    private LinearLayoutManager rankingViewLinearLayoutManager;
    private int count_matrix_indexs;
    private HashMap<Integer, Integer> matrix_selected_index;
    private CheckBox[] checkBox;
    private int[] satisfactionImgsDrawables;

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
        mandatoryNoteTtv        =   (TextView) view.findViewById(R.id.mandatoryNoteTtv);
        pageIndexLlt            =   (LinearLayout) view.findViewById(R.id.pageIndexLlt);
        pageIndexTtv            =   (TextView) view.findViewById(R.id.pageIndexTtv);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pageIndexLlt.setElevation((float) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 4));
            pageIndexLlt.setTranslationZ((float) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 4));
        }
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.survey));

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                satisfactionImgsDrawables = new int[]{R.drawable.angry, R.drawable.sad, R.drawable.neutral, R.drawable.happy, R.drawable.vhappy};
                question = getArguments().getParcelable(getResources().getString(R.string.question));
                surveyTitleTtv.setText(getArguments().getString(getResources().getString(R.string.survey_title)));

                if (question != null && question.getTypeId() != 0) {
                    if (question.isMandatory() == 1) mandatoryNoteTtv.setVisibility(View.VISIBLE);
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
                            //Log.e(debugTag, aRb.getId()+"");
                        }
                        selectedIndex = compoundButton.getId();
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

    public int getSingleChoiceSelectedId() { return selectedIndex; }

    private void initializeManyChoicesView(QuestionData question) {
        if (question.getAnswers() != null && question.getAnswers().size() > 0) {
            checkBox = new CheckBox[question.getAnswers().size()];

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, (int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 10));
            for (int i = 0; i < question.getAnswers().size(); i++) {
                checkBox[i] = new CheckBox(getActivity());
                checkBox[i].setText(question.getAnswers().get(i));
                checkBox[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                checkBox[i].setGravity(Gravity.CENTER_VERTICAL);
                questionContainer.addView(checkBox[i]);
                checkBox[i].setLayoutParams(lp);
            }
        }
    }

    public List<Integer> getMultipleChoiceSelectedIds() {
        boolean isChecked = false;
        List<Integer> integerList = new ArrayList<>();
        for (int i = 0; i < checkBox.length; i++) {
            if (checkBox[i].isChecked()) {
                integerList.add(i);
                isChecked = true;
            }
        }
        if (!isChecked) integerList.add(-1);
        return integerList;
    }

    private void initializeRankingView(QuestionData question) {
        if (question.getAnswers() != null && question.getAnswers().size() > 0) {
            rankingViewLinearLayoutManager = new LinearLayoutManager(getActivity());

            RecyclerView rankingView;
            LinearLayout rankingLlt = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.question_ranking_template, (ViewGroup) getActivity().findViewById(R.id.questionRankingLlt), false);
            if (rankingLlt != null && rankingLlt.getChildAt(1) instanceof RecyclerView) {
                questionContainer.addView(rankingLlt);
                rankingView = (RecyclerView) rankingLlt.findViewById(R.id.questionRankingRcv);
                rankingView.setHasFixedSize(true);
                rankingView.setLayoutManager(rankingViewLinearLayoutManager);

                SurveyRankingQuestionRcvAdapter surveyQuestionRcvAdapter = new SurveyRankingQuestionRcvAdapter(question.getAnswers());
                rankingView.setAdapter(surveyQuestionRcvAdapter);
                ItemTouchHelper.Callback callback = new SImpleItemTouchHelperCallback(surveyQuestionRcvAdapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(rankingView);
            }
        }
    }

    public List<String> getRankingViewIndexs() {
        List<String> indexs;
        if (rankingViewLinearLayoutManager != null) {
            indexs = new ArrayList<>();
            for (int i = 0; i < rankingViewLinearLayoutManager.getChildCount(); i++) {
                indexs.add(String.valueOf(rankingViewLinearLayoutManager.getChildAt(i).getTag()));
            }
            return indexs;
        } else {
            return null;
        }
    }

    private void initializeSatisfactionRatingView() {
        icons = new ArrayList<>();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setWeightSum(100);
        linearLayout.setLayoutParams(lp);

        LinearLayout.LayoutParams imvLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 62), 20);

        for (int i = 0; i < 5; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setId(i);
            imageView.setTag(getResources().getString(R.string.empty_string));
            imageView.setImageResource(satisfactionImgsDrawables[i]);
            imageView.setLayoutParams(imvLp);
            linearLayout.addView(imageView);
            imageView.setOnClickListener(this);
            icons.add(imageView);
        }
        questionContainer.addView(linearLayout);
//        LinearLayout satisfRatingView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.question_satisfaction_rating_template, (ViewGroup) getActivity().findViewById(R.id.satisfactionLlt), false);
//        Log.e(debugTag, satisfRatingView+"");
//        questionContainer.addView(satisfRatingView);
//        if (satisfRatingView != null) {
//            angryImv      =   (ImageView) getActivity().findViewById(R.id.angryImv);
//            sadImv        =   (ImageView) getActivity().findViewById(R.id.sadImv);
//            neutralImv    =   (ImageView) getActivity().findViewById(R.id.neutralImv);
//            happyImv      =   (ImageView) getActivity().findViewById(R.id.happyImv);
//            vhappyImv     =   (ImageView) getActivity().findViewById(R.id.vhappyImv);
//            angryImv.setOnClickListener(this);
//            sadImv.setOnClickListener(this);
//            neutralImv.setOnClickListener(this);
//            happyImv.setOnClickListener(this);
//            vhappyImv.setOnClickListener(this);
//            icons = new ImageView[]{angryImv, sadImv, neutralImv, happyImv, vhappyImv};
//        }
    }

    private void initializeSliderView(final QuestionData question) {
        if (question.getAnswers() != null && question.getAnswers().size() > 0) {
            selectedIndex = 0;
            LinearLayout view   =   (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.question_slider_template, (ViewGroup) getActivity().findViewById(R.id.questionTemplateLlt), false);

            LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            Slider slider = new Slider(getActivity());
            slider.setLayoutParams(slp);
            slider.applyStyle(R.style.Material_Widget_Slider_Discrete);
            slider.setPadding((int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 4), 0, (int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 4), 0);
            view.addView(slider);

            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tlp.setMargins(0, (int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 40), 0, 0);
            final TextView textView = new TextView(getActivity());
            textView.setLayoutParams(tlp);
            textView.setPadding((int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 4), 0, (int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 4), 0);
            textView.setTextSize(13);
            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.question_item_text_color));
            view.addView(textView);

            questionContainer.addView(view);

            slider.setValueRange(1, question.getAnswers().size(), true);
            textView.setText(question.getAnswers().get(0));
            slider.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
                @Override
                public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                    selectedIndex = newValue - 1;
                    textView.setText(question.getAnswers().get(newValue-1));
                }
            });
//            if (view != null) {
//                Slider slider           =   (Slider) getActivity().findViewById(R.id.questionSldr);
//                final TextView textView =   (TextView) getActivity().findViewById(R.id.questionSliderTtv);
//                slider.setValueRange(1, question.getAnswers().size(), true);
//
//                textView.setText(question.getAnswers().get(0));
//                slider.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
//                    @Override
//                    public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
//                        selectedIndex = newValue - 1;
//                        textView.setText(question.getAnswers().get(newValue-1));
//                    }
//                });
//            }
        }
    }

    private void initializeListPickerView(QuestionData question) {
        if (question.getAnswers() != null && question.getAnswers().size() > 0) {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

            RecyclerView listPickerView = (RecyclerView) LayoutInflater.from(getActivity()).inflate(R.layout.question_list_picker_template, (ViewGroup) getActivity().findViewById(R.id.questionListPickerRcv), false);
            listPickerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), listPickerView, new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    if (view != null && view instanceof LinearLayout) {
                        if (selectedIndex != -1 && linearLayoutManager.getChildAt(selectedIndex) instanceof LinearLayout) {
                            LinearLayout linearLayout = (LinearLayout) linearLayoutManager.getChildAt(selectedIndex);
                            TextView textView = (TextView) linearLayout.getChildAt(0);
                            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.question_item_text_color));
                        }
                        TextView textView = (TextView) ((LinearLayout) view).getChildAt(0);
                        textView.setTextColor(ContextCompat.getColor(getActivity(),R.color.accentColor));
                        selectedIndex = position;
                    }
                }
            }));

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
            RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    String[] strings = radioGroup.getTag().toString().split("_");
                    switch (checkedId) {
                        case R.id.sDisagreeRbt:
                            matrix_selected_index.put(Integer.valueOf(strings[strings.length - 1]) -1, 0);
                            break;
                        case R.id.disagreeRbt:
                            matrix_selected_index.put(Integer.valueOf(strings[strings.length - 1]) - 1, 1);
                            break;
                        case R.id.neutralRbt:
                            matrix_selected_index.put(Integer.valueOf(strings[strings.length - 1]) - 1, 2);
                            break;
                        case R.id.agreeRbt:
                            matrix_selected_index.put(Integer.valueOf(strings[strings.length - 1]) - 1, 3);
                            break;
                        case R.id.sAgreeRbt:
                            matrix_selected_index.put(Integer.valueOf(strings[strings.length - 1]) - 1, 4);
                            break;
                    }
                    LinearLayout parent = (LinearLayout) radioGroup.getParent();
                    LinearLayout matrix = (LinearLayout) parent.getChildAt(1);

                    if (matrix.findViewWithTag("matrixScalesLlt_" + strings[strings.length - 1]).getVisibility() == View.GONE) matrix.findViewWithTag("matrixScalesLlt_" + strings[strings.length - 1]).setVisibility(View.VISIBLE);
                }
            };
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, (int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 10), 0, (int)MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 10));
            for(int i = 0; i < question.getAnswers().size(); i++) {
                textViews[i] = new TextView(getActivity());
                textViews[i].setText(question.getAnswers().get(i));
                textViews[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textViews[i].setTextColor(ContextCompat.getColor(getActivity(), R.color.question_item_text_color));
                textViews[i].setLayoutParams(lp);
                questionContainer.addView(textViews[i]);
                matrixView   =   (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.question_matrix_scale_template, (ViewGroup) getActivity().findViewById(R.id.matrixContainerLlt), false);
                if (matrixView.getChildAt(0) != null && matrixView.getChildAt(1) != null && matrixView.getChildAt(0) instanceof RadioGroup) {
                    ((RadioGroup) matrixView.getChildAt(0)).setOnCheckedChangeListener(onCheckedChangeListener);
                    matrixView.getChildAt(0).setTag("rGroup_"+(i+1));
                    matrixView.getChildAt(1).setTag("matrixScalesLlt_"+(i+1));
                }
                questionContainer.addView(matrixView);
                count_matrix_indexs++;
            }
            initializeMatrixHashMap();
        }
    }

    private void initializeMatrixHashMap() {
        matrix_selected_index = new HashMap<>(count_matrix_indexs);
        for (int i = 0; i < count_matrix_indexs; i++) {
            matrix_selected_index.put(i, -1);
        }
    }

    public HashMap<Integer, Integer> getMatrixViewIndexs() {
        return matrix_selected_index != null ? matrix_selected_index : null;
    }

    private void initializeFreeAnswerView() {
        freeAnswerView = (EditText) LayoutInflater.from(getActivity()).inflate(R.layout.question_free_answer_template, (ViewGroup) getActivity().findViewById(R.id.freeAnswerEdt), false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) freeAnswerView.setElevation((float) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 4));
        questionContainer.addView(freeAnswerView);
    }

    public String getFreeAnswerView() {
        return (freeAnswerView.getText().toString().isEmpty()) ? null : freeAnswerView.getText().toString();
    }

    @Override
    public void onClick(View view) {
        ImageView imageView;
        if (view != null && view instanceof ImageView) {
            imageView = (ImageView) view;
            for (ImageView imv : icons) {
                if (imv.getTag().equals(getResources().getString(R.string.selected))) {
                    imv.clearColorFilter();
                    imv.setTag("");
                }
            }
            switch (view.getId()) {
                case 0:
                    imageView.setColorFilter(ContextCompat.getColor(getActivity(),R.color.angry_fill_color));
                    imageView.setTag(getResources().getString(R.string.selected));
                    selectedIndex = 0;
                    break;
                case 1:
                    imageView.setColorFilter(ContextCompat.getColor(getActivity(),R.color.sad_fill_color));
                    imageView.setTag(getResources().getString(R.string.selected));
                    selectedIndex = 1;
                    break;
                case 2:
                    imageView.setColorFilter(ContextCompat.getColor(getActivity(),R.color.neutral_fill_color));
                    imageView.setTag(getResources().getString(R.string.selected));
                    selectedIndex = 2;
                    break;
                case 3:
                    imageView.setColorFilter(ContextCompat.getColor(getActivity(),R.color.happy_fill_color));
                    imageView.setTag(getResources().getString(R.string.selected));
                    selectedIndex = 3;
                    break;
                case 4:
                    imageView.setColorFilter(ContextCompat.getColor(getActivity(),R.color.vhappy_fill_color));
                    imageView.setTag(getResources().getString(R.string.selected));
                    selectedIndex = 4;
                    break;
            }
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
