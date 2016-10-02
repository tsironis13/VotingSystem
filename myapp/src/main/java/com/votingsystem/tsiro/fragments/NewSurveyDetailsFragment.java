package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.widget.EditText;
import com.rey.material.widget.ImageButton;
import com.rey.material.widget.LinearLayout;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.POJO.NewSurvey;
import com.votingsystem.tsiro.POJO.NewSurveyQuestion;
import com.votingsystem.tsiro.adapters.FirmNamesSpnrNothingSelectedAdapter;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.helperClasses.CustomSpinnerItem;
import com.votingsystem.tsiro.interfaces.UpdateNewSurveyObj;
import com.votingsystem.tsiro.votingsystem.R;
import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.internal.zznu.it;

/**
 * Created by giannis on 30/7/2016.
 */
public class NewSurveyDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String debugTag = NewSurveyDetailsFragment.class.getSimpleName();
    private View view;
    private EditText surveyTitleEdt;
    private android.widget.EditText activeSinceEdt, validUntilEdt;
    private TextView hiddenActiveSinceLabelTtv, hiddenValidUntilLabelTtv;
    private boolean hiddenActiveSinceLabelActivated, hiddenValidUntilLabelActivated, hasQuestions;
    private Spinner surveyCategorySpnr;
    private TextView questionsLabelTtv;
    private LinearLayout questionsContainerLlt;
    private FloatingActionsMenu fabTypeMenu;
    private UpdateNewSurveyObj updateNewSurveyObj;
    private SparseArray<NewSurveyQuestion> newSurveyQuestionSparseArray;
    private int keyAt; //used to set max sparse array key value + 1 on new question insertion

    public static NewSurveyDetailsFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        NewSurveyDetailsFragment newSurveyDetails = new NewSurveyDetailsFragment();
        newSurveyDetails.setArguments(bundle);
        return newSurveyDetails;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.updateNewSurveyObj = (UpdateNewSurveyObj) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view = inflater.inflate(R.layout.fragment_new_survey_details, container, false);
        surveyTitleEdt              = (EditText) view.findViewById(R.id.surveyTitleEdt);
        activeSinceEdt              = (android.widget.EditText) view.findViewById(R.id.activeSinceEdt);
        validUntilEdt               = (android.widget.EditText) view.findViewById(R.id.validUntilEdt);
        hiddenActiveSinceLabelTtv   = (TextView) view.findViewById(R.id.hiddenActiveSinceLabelTtv);
        hiddenValidUntilLabelTtv    = (TextView) view.findViewById(R.id.hiddenValidUntilLabelTtv);
        surveyCategorySpnr          = (Spinner) view.findViewById(R.id.surveyCategorySpnr);
        questionsLabelTtv           = (TextView) view.findViewById(R.id.questionsLabelTtv);
        questionsContainerLlt       = (LinearLayout) view.findViewById(R.id.questionsContainerLlt);
        fabTypeMenu                 = (FloatingActionsMenu) view.findViewById(R.id.fabTypeMenu);
        FloatingActionButton multipleChoiceFAB = (FloatingActionButton) view.findViewById(R.id.multipleChoiceFAB);
        FloatingActionButton satisfactionRatingFAB = (FloatingActionButton) view.findViewById(R.id.satisfactionRatingFAB);
        FloatingActionButton sliderFAB = (FloatingActionButton) view.findViewById(R.id.sliderFAB);
        FloatingActionButton listSelectionFAB = (FloatingActionButton) view.findViewById(R.id.listSelectionFAB);
        FloatingActionButton rankingFAB = (FloatingActionButton) view.findViewById(R.id.rankingFAB);
        FloatingActionButton freeAnswerFAB = (FloatingActionButton) view.findViewById(R.id.freeAnswerFAB);
        multipleChoiceFAB.setOnClickListener(this);
        satisfactionRatingFAB.setOnClickListener(this);
        sliderFAB.setOnClickListener(this);
        listSelectionFAB.setOnClickListener(this);
        rankingFAB.setOnClickListener(this);
        freeAnswerFAB.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        }
        updateNewSurveyObj.initializeSpannableText(surveyTitleEdt, getResources().getString(R.string.new_survey_title), true);
        if (getArguments() != null) {
            if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getArguments().getString(getResources().getString(R.string.title)));
        }
        activeSinceEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(getResources().getString(R.string.active_since_action));
            }
        });
        activeSinceEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) showDateDialog(getResources().getString(R.string.active_since_action));

            }
        });
        validUntilEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(getResources().getString(R.string.valid_until_action));
            }
        });
        validUntilEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) showDateDialog(getResources().getString(R.string.valid_until_action));
            }
        });
        surveyCategorySpnr.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                if (view instanceof TextView) ((TextView) view).setTextColor(ContextCompat.getColor(getActivity(), R.color.no_surveys_txv_color));
            }
        });
        NewSurvey newSurvey = updateNewSurveyObj.getNewSurveyObj();
        if (newSurvey != null) {
            if (newSurvey.getCategory() == 0) createPickCategoryItems();
            newSurveyQuestionSparseArray = newSurvey.getNewSurveyQuestionSparseArray();
            if (questionsContainerLlt.getChildCount() != 0) questionsContainerLlt.removeAllViews();
            if (newSurveyQuestionSparseArray != null && newSurveyQuestionSparseArray.size() != 0) {
                hasQuestions = true;
                questionsLabelTtv.setVisibility(View.VISIBLE);
                for (int i = 0; i < newSurveyQuestionSparseArray.size(); i++) {
                    keyAt = newSurveyQuestionSparseArray.keyAt(i);
                    NewSurveyQuestion newSurveyQuestion = newSurveyQuestionSparseArray.get(newSurveyQuestionSparseArray.keyAt(i));

                    LinearLayout questionLlt = new LinearLayout(getActivity());
                    questionLlt.setId(newSurveyQuestion.getKey());
                    questionLlt.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                    questionLlt.setOnClickListener(this);
                    LinearLayout.LayoutParams questionLltparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0, 50));
                    questionLlt.setLayoutParams(questionLltparams);
                    questionLlt.setGravity(Gravity.CENTER_VERTICAL);

                    TextView questionTtv = new TextView(getActivity());
                    questionTtv.setText(i+1+". "+newSurveyQuestion.getQuestion());
                    questionTtv.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
                    questionTtv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.9f));

                    ImageButton imgBtn = new ImageButton(getActivity());
                    imgBtn.setId(newSurveyQuestion.getKey());
                    imgBtn.setOnClickListener(this);
                    imgBtn.setImageResource(R.drawable.ic_edit);
                    imgBtn.setColorFilter(ContextCompat.getColor(getActivity(), R.color.primaryColor));
                    imgBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
                    LinearLayout.LayoutParams buttonlp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.1f);
                    buttonlp.gravity = Gravity.END|Gravity.CENTER_VERTICAL;
                    imgBtn.setLayoutParams(buttonlp);

                    questionLlt.addView(questionTtv);
                    questionLlt.addView(imgBtn);
                    questionsContainerLlt.addView(questionLlt);
                }
            }
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onClick(View view) {
        if (view instanceof ImageButton || view instanceof LinearLayout) {
            NewSurveyQuestion newSurveyQuestion = newSurveyQuestionSparseArray.get(view.getId());
            newSurveyQuestion.setKey(view.getId());
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.new_question_anim_enter, R.anim.new_question_anim_exit, R.anim.new_question_anim_enter, R.anim.new_question_anim_exit)
                    .replace(R.id.createSurveyFgmtContainer, NewSurveyQuestionFragment.newInstance(newSurveyQuestion), getResources().getString(R.string.new_survey_question_fgmt))
                    .addToBackStack(getResources().getString(R.string.new_survey_question_fgmt))
                    .commit();
        } else {
            int categoryId = 0;
            String questionType = "";
            String title = surveyTitleEdt.getText().toString();
            Log.e(debugTag, title);
            updateNewSurveyObj.addNewSurveyFields(title, 20, 20, 20);
            updateNewSurveyObj.logObj();
            fabTypeMenu.collapse();
            switch (view.getId()) {
                case R.id.multipleChoiceFAB:
                    categoryId      = 100;
                    questionType    = getResources().getString(R.string.multiple_choice);
                    break;
                case R.id.satisfactionRatingFAB:
                    categoryId      = 400;
                    questionType    = getResources().getString(R.string.satisfaction_rating);
                    break;
                case R.id.sliderFAB:
                    categoryId      = 500;
                    questionType    = getResources().getString(R.string.slider);
                    break;
                case R.id.listSelectionFAB:
                    categoryId      = 600;
                    questionType    = getResources().getString(R.string.list_selection);
                    break;
                case R.id.rankingFAB:
                    categoryId      = 300;
                    questionType    = getResources().getString(R.string.ranking);
                    break;
                case R.id.freeAnswerFAB:
                    categoryId      = 800;
                    questionType    = getResources().getString(R.string.free_answer);
                    break;
            }
            View keyboardView = getActivity().getCurrentFocus();
            if (keyboardView != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(keyboardView.getWindowToken(), 0);
            }
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.new_question_anim_enter, R.anim.new_question_anim_exit, R.anim.new_question_anim_enter, R.anim.new_question_anim_exit)
                    .replace(R.id.createSurveyFgmtContainer, NewSurveyQuestionFragment.newInstance(new NewSurveyQuestion(keyAt+1, getResources().getString(R.string.add), null, questionType, null, categoryId, false, false)), getResources().getString(R.string.new_survey_question_fgmt))
                    .addToBackStack(getResources().getString(R.string.new_survey_question_fgmt))
                    .commit();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (hasQuestions) menu.findItem(R.id.post).setVisible(true);
        hasQuestions = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.survey_questions_new_survey_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String survey   = surveyTitleEdt.getText().toString().trim();
        int category_id = getCategoryId();
        if (survey.matches(getResources().getString(R.string.empty_string)) || category_id == 0) {
            updateNewSurveyObj.showSnackBar(getResources().getString(R.string.fill_out_required_fields), fabTypeMenu);
        }

        return true;
    }

    private void showDateDialog(final String from) {
        Dialog.Builder builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker_Light) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                int year = dialog.getYear();
                int month = dialog.getMonth() + 1;
                int day = dialog.getDay();
                if (from.equals(getResources().getString(R.string.active_since_action))) {
                    if (!hiddenActiveSinceLabelActivated) {
                        hiddenActiveSinceLabelTtv.setVisibility(View.VISIBLE);
                        hiddenActiveSinceLabelActivated = true;
                    }
                    activeSinceEdt.setText(getResources().getString(R.string.edt_date_format, day, month, year));
                    //day - 1, because SimpleDateFormat default time system is UTC (our time zone-> UTC + 3), so for an active since date '22-9'
                    //we have to subtract a day from the selected day (21-9 21:00 UTC after conversion becomes 22-9 00::00 UTC+3)
                    //the actual date we want
                    convertTimestampToEpoch(getResources().getString(R.string.active_since_format, day-1, month, year));
                } else {
                    if (!hiddenValidUntilLabelActivated) {
                        hiddenValidUntilLabelTtv.setVisibility(View.VISIBLE);
                        hiddenValidUntilLabelActivated = true;
                    }
                    validUntilEdt.setText(getResources().getString(R.string.edt_date_format, day, month, year));
                    convertTimestampToEpoch(getResources().getString(R.string.valid_until_format, day, month, year));
                }
                Log.e(debugTag, day + "-" + (month) + "-" + year);
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
                Log.e(debugTag, "negative");
            }
        };
        builder
                .positiveAction(getResources().getString(R.string.pick))
                .negativeAction(getResources().getString(R.string.cancel));

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), getResources().getString(R.string.data_picker));
    }

    private int getCategoryId() {
        int category_id = 0;
        if (surveyCategorySpnr.getSelectedItemPosition() != 0 && surveyCategorySpnr != null && surveyCategorySpnr.getAdapter() != null) {
            CustomSpinnerItem customSpinnerItem = (CustomSpinnerItem) surveyCategorySpnr.getAdapter().getItem(surveyCategorySpnr.getSelectedItemPosition() - 1);
            category_id = customSpinnerItem.getId();
        }
        return category_id;
    }

    private void createPickCategoryItems() {
        String[] categories = getResources().getStringArray(R.array.survey_categories);
        Log.e(debugTag, categories[0]);
        List<CustomSpinnerItem> customSpinnerItems = new ArrayList<>();
        for (int i = 0; i < categories.length; i++) {
            customSpinnerItems.add(new CustomSpinnerItem(categories[i],i+1));
        }
        surveyCategorySpnr.setAdapter(new FirmNamesSpnrNothingSelectedAdapter(getActivity(), R.layout.category_spinner_selection_item, customSpinnerItems));
        FirmNamesSpnrNothingSelectedAdapter firmNamesSpnrNothingSelectedAdapter = (FirmNamesSpnrNothingSelectedAdapter) surveyCategorySpnr.getAdapter();
        firmNamesSpnrNothingSelectedAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        TextView selectionItem = (TextView) getActivity().findViewById(R.id.pickCategory);
        updateNewSurveyObj.initializeSpannableText(selectionItem, "<font color='#B3B3B3'>Επιλογή κατηγορίας</font>", true);
    }

    private long convertTimestampToEpoch(String timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format), Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(timestamp);
            long epoch = date.getTime()/1000;
            Log.e(debugTag, date.getTime()/1000+"");
            return epoch;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
