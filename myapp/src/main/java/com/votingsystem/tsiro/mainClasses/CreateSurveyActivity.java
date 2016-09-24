package com.votingsystem.tsiro.mainClasses;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.POJO.NewSurvey;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.fragments.NewSurveyDetailsFragment;
import com.votingsystem.tsiro.interfaces.UpdateNewSurveyObj;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 30/7/2016.
 */
public class CreateSurveyActivity extends AppCompatActivity implements UpdateNewSurveyObj {

    private static final String debugTag = CreateSurveyActivity.class.getSimpleName();
    private NewSurvey newSurvey;
    private boolean confirmFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_survey_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleToolbarIconsAndHardwareBackButtonPress();
//
// finish();
//                    Intent intent = new Intent(SurveyQuestionsActivity.this, SurveysActivity.class);
//                    startActivity(intent);
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation((float) MyApplication.convertPixelToDpAndViceVersa(this, 0, 3));
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.createSurveyFgmtContainer, NewSurveyDetailsFragment.newInstance(getResources().getString(R.string.new_survey)), getResources().getString(R.string.new_survey_details_fgmt))
                .addToBackStack(getResources().getString(R.string.new_survey_details_fgmt))
                .commit();
        newSurvey = new NewSurvey();
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
    public void addNewSurveyFields(String title, long active_since, long valid_until, int category) {
        if (title != null && active_since != 0 && valid_until != 0 && category != 0) {
            newSurvey.setTitle(title);
            newSurvey.setActiveSince(active_since);
            newSurvey.setValidUntil(valid_until);
            newSurvey.setCategory(category);
        }
    }

    @Override
    public NewSurvey getObj() {
        return (newSurvey != null) ? newSurvey : null;
    }

    @Override
    public void logObj() {
        Log.e(debugTag, "Title: "+newSurvey.getTitle());
        Log.e(debugTag, "Category "+newSurvey.getCategory());
        //        Log.e(debugTag, "Active since: "+newSurvey.getActiveSince());
    }

    private void handleToolbarIconsAndHardwareBackButtonPress() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 2 && getTopBackStackFragment() != null && getTopBackStackFragment().equals(getResources().getString(R.string.new_survey_question_fgmt))) {
            initializeDialogs();
        } else {
            this.finish();
        }
    }

    private void initializeDialogs() {
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                confirmFlag = true;
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onDismiss(dialog);
                if (confirmFlag) getSupportFragmentManager().popBackStack();
                confirmFlag = false;
            }
        };
        ((SimpleDialog.Builder)builder)
                .message(getResources().getString(R.string.discard_dialog_msg))
                .title(getResources().getString(R.string.discard_dialog_title))
                .positiveAction(getResources().getString(R.string.confirm_action))
                .negativeAction(getResources().getString(R.string.cancel));

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), getResources().getString(R.string.dialog));
    }

    private String getTopBackStackFragment() {
        return getSupportFragmentManager().getBackStackEntryCount() > 0 ? getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName() : null;
    }
}
