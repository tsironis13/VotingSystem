package com.votingsystem.tsiro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.adapters.FirmNamesSpnrNothingSelectedAdapter;
import com.votingsystem.tsiro.helperClasses.CustomSpinnerItem;
import com.votingsystem.tsiro.votingsystem.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by giannis on 30/7/2016.
 */
public class NewSurveyDetailsFragment extends Fragment {

    private static final String debugTag = NewSurveyDetailsFragment.class.getSimpleName();
    private View view;
    private EditText surveyTitleEdt;
    private android.widget.EditText activeSinceEdt, validUntilEdt;
    private TextView hiddenActiveSinceLabelTtv, hiddenValidUntilLabelTtv;
    private boolean hiddenActiveSinceLabelActivated, hiddenValidUntilLabelActivated;
    private Spinner surveyCategorySpnr;

    public static NewSurveyDetailsFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        NewSurveyDetailsFragment newSurveyDetails = new NewSurveyDetailsFragment();
        newSurveyDetails.setArguments(bundle);
        return newSurveyDetails;
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
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeSpannableText(0, null);
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
        createPickCategoryItems();
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
        initializeSpannableText(1, selectionItem);
    }

    private void initializeSpannableText(int tag, TextView textView) {
        String required_sign = "<font color='#E53935'>* </font>";
        String text = "";
        if (tag == 1) { text = "<font color='#B3B3B3'>Επιλογή κατηγορίας</font>"; }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            if (tag == 0) {
                surveyTitleEdt.setHint(Html.fromHtml(required_sign + getResources().getString(R.string.new_survey_title), Html.FROM_HTML_MODE_LEGACY));
            } else {
                textView.setHint(Html.fromHtml(required_sign + text, Html.FROM_HTML_MODE_LEGACY));
            }
        } else {
            if (tag == 0) {
                surveyTitleEdt.setHint(Html.fromHtml(required_sign + getResources().getString(R.string.new_survey_title)));
            } else {
                textView.setHint(Html.fromHtml(required_sign + text));
            }
        }
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
