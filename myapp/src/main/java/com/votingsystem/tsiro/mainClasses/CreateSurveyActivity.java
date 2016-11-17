package com.votingsystem.tsiro.mainClasses;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;
import com.rey.material.widget.LinearLayout;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.POJO.NewSurvey;
import com.votingsystem.tsiro.POJO.NewSurveyQuestion;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.fragments.NewSurveyDetailsFragment;
import com.votingsystem.tsiro.interfaces.UpdateNewSurveyObj;
import com.votingsystem.tsiro.parcel.QuestionData;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.votingsystem.R;

import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

/**
 * Created by giannis on 30/7/2016.
 */
public class CreateSurveyActivity extends AppCompatActivity implements UpdateNewSurveyObj {

    private static final String debugTag = CreateSurveyActivity.class.getSimpleName();
    private NewSurvey newSurvey;
    private boolean confirmFlag;
    private LinearLayout createSurveyLlt;
    private SparseIntArray inputValidationCodes;
    private boolean snackBarIsShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_survey_activity);
        inputValidationCodes    =   AppConfig.getCodes();
        createSurveyLlt         =   (LinearLayout) findViewById(R.id.createSurveyLlt);
        Toolbar toolbar         =   (Toolbar) findViewById(R.id.appBar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleToolbarIconsAndHardwareBackButtonPress();
// finish();
//                    Intent intent = new Intent(SurveyQuestionsActivity.this, SurveysActivity.class);
//                    startActivity(intent);
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation((float) MyApplication.convertPixelToDpAndViceVersa(this, 0, 3));
        }
        String title, action;
        SurveyData data = null;
        SparseArray<NewSurveyQuestion> newSurveyQuestionSparseArray = new SparseArray<>();
        newSurvey = new NewSurvey();
        newSurvey.setNewSurveyQuestionSparseArray(newSurveyQuestionSparseArray);
        if (getIntent().getStringExtra(getResources().getString(R.string.action)).equals(getResources().getString(R.string.tag_new))) {
            title   = getResources().getString(R.string.new_survey);
            action  = getResources().getString(R.string.tag_new);
        } else {
            title   = getResources().getString(R.string.edit_survey);
            action  = getResources().getString(R.string.edit);
            if (getIntent().getExtras().getParcelable(getResources().getString(R.string.data)) != null){
                data = getIntent().getExtras().getParcelable(getResources().getString(R.string.data));
                if (data != null && data.getQuestions() != null && data.getQuestions().size() != 0) {
                    for (int i = 0; i < data.getQuestions().size(); i++) {
                        String qTitle           = data.getQuestions().get(i).getTitle();
                        String qType            = data.getQuestions().get(i).getType();
                        List<String> answers    = data.getQuestions().get(i).getAnswers();
                        int qtypeId             = data.getQuestions().get(i).getTypeId();
                        boolean isSingleChoice = qtypeId == 100;
                        int isMandatory         = data.getQuestions().get(i).isMandatory();
                        NewSurveyQuestion newSurveyQuestion = new NewSurveyQuestion(i, getResources().getString(R.string.edit), qTitle, qType, answers, qtypeId, isSingleChoice, isMandatory);
                        newSurveyQuestionSparseArray.put(i, newSurveyQuestion);
                    }
                }
            }
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.createSurveyFgmtContainer, NewSurveyDetailsFragment.newInstance(title, action, data), getResources().getString(R.string.new_survey_details_fgmt))
                .addToBackStack(getResources().getString(R.string.new_survey_details_fgmt))
                .commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleToolbarIconsAndHardwareBackButtonPress();
            Log.e(debugTag, getTopBackStackFragment());
        }
        return true;
    }

    @Override
    public void initializeSpannableText(View view, String text, boolean required) {
        String required_sign = required ? "<font color='#E53935'>* </font>" : "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            if (view != null) {
                if (view instanceof TextView) {
                    ((TextView) view).setHint(Html.fromHtml(required_sign + text, Html.FROM_HTML_MODE_LEGACY));
                }
                if (view instanceof EditText) {
                    ((EditText) view).setHint(Html.fromHtml(required_sign + text, Html.FROM_HTML_MODE_LEGACY));
                }
            }
        } else {
            if (view instanceof TextView) {
                ((TextView) view).setHint(Html.fromHtml(required_sign + text));
            }
            if (view instanceof EditText) {
                ((EditText) view).setHint(Html.fromHtml(required_sign + text));
            }
        }
    }

    @Override
    public NewSurvey getNewSurveyObj() {
        return (newSurvey != null) ? newSurvey : null;
    }

    @Override
    public void initializeDialogs(final int action, String message) {
        /*
        * action => 0, dialog is initialized from NewSurveyDetailsFragment
        * action => 1, dialog is initialized from NewSurveyQuestionFragment
        * */
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                confirmFlag = true;
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onDismiss(dialog);
                //confirmFlag used to animated pop fragment from back stack on dialog dismiss
                if (confirmFlag) {
                    if (action == 1) {
                        getSupportFragmentManager().popBackStack();
                    } else {
                        CreateSurveyActivity.this.finish();
                    }
                }
                confirmFlag = false;
            }
        };
        ((SimpleDialog.Builder)builder)
                .message(message)
                .title(getResources().getString(R.string.discard_dialog_title))
                .positiveAction(getResources().getString(R.string.confirm_action))
                .negativeAction(getResources().getString(R.string.cancel));

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), getResources().getString(R.string.dialog));
    }

    @Override
    public void showSnackBar(int code, View view) {
        initializeSnackBar(code, view);
    }

    private void initializeSnackBar(int code, final View view) {
        String sbText = getResources().getString(inputValidationCodes.get(code));
        if (!snackBarIsShowing) {
            Snackbar createSurveyActivitySnkBar = Snackbar.make(createSurveyLlt, sbText, Snackbar.LENGTH_SHORT);
            if (view != null && view instanceof FloatingActionsMenu) {
                createSurveyActivitySnkBar.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        snackBarIsShowing = false;
                    }
                });
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initializeFABMenuAnimation(view, -(int)MyApplication.convertPixelToDpAndViceVersa(CreateSurveyActivity.this, 0, 97), 0);
                    }
                }, 1785);
            }
            View sbView = createSurveyActivitySnkBar.getView();
            sbView.setMinimumHeight((int)MyApplication.convertPixelToDpAndViceVersa(this, 0, 80));
            sbView.setPadding(24,24,24,24);
            android.widget.TextView textView = (android.widget.TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextSize(14);
            textView.setTextColor(ContextCompat.getColor(this, R.color.accentColor));
            createSurveyActivitySnkBar.show();
            if (view != null && view instanceof FloatingActionsMenu) {
                snackBarIsShowing = true;
                initializeFABMenuAnimation(view, 0, -(int)MyApplication.convertPixelToDpAndViceVersa(this, 0, 97));
            }
        }
    }

    private void initializeFABMenuAnimation(View view, float start, float end) {
        ObjectAnimator translationAnimator  = ObjectAnimator.ofFloat(view, getResources().getString(R.string.translation_y), start, end);
        translationAnimator.setDuration(230);
        translationAnimator.start();
    }

    private void handleToolbarIconsAndHardwareBackButtonPress() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 2 && getTopBackStackFragment() != null && getTopBackStackFragment().equals(getResources().getString(R.string.new_survey_question_fgmt))) {
            initializeDialogs(1, getResources().getString(R.string.discard_question_dialog_msg));
        } else {
            initializeDialogs(0, getResources().getString(R.string.discard_survey_dialog_msg));
//            this.finish();
        }
    }

    private String getTopBackStackFragment() {
        return getSupportFragmentManager().getBackStackEntryCount() > 0 ? getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName() : null;
    }
}
