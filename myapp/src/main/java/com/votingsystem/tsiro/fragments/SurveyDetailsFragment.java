package com.votingsystem.tsiro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.POJO.SurveyDetails;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 16/7/2016.
 */
public class SurveyDetailsFragment extends Fragment {

    private static final String debugTag = SurveyDetailsFragment.class.getSimpleName();
    private SurveyDetailsData surveyDetailsData;
    private View view, tView;
    private TextView surveyTitleTtv, totalResponsesTtv, answeredTtv, completionRateTtv, createdDateTtv, lastModifiedDateTtv, categoryTtv, questionsTtv;
    private LinearLayout showStatsLlt;
    private boolean onSavedInstanceStateCalled;

    public static SurveyDetailsFragment newInstance(SurveyDetailsData surveyDetailsData, String type) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", surveyDetailsData);
        bundle.putString("type", type);
        SurveyDetailsFragment surveyDetailsFragment = new SurveyDetailsFragment();
        surveyDetailsFragment.setArguments(bundle);
        return surveyDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view = inflater.inflate(R.layout.fragment_survey_details, container, false);
        showStatsLlt        = (LinearLayout) view.findViewById(R.id.showStatsLlt);
        tView               = view.findViewById(R.id.tView);
        surveyTitleTtv      = (TextView) view.findViewById(R.id.surveyTitleTtv);
        totalResponsesTtv   = (TextView) view.findViewById(R.id.totalResponsesTtv);
        answeredTtv         = (TextView) view.findViewById(R.id.answeredTtv);
        completionRateTtv   = (TextView) view.findViewById(R.id.completionRateTtv);
        createdDateTtv      = (TextView) view.findViewById(R.id.createdDateTtv);
        lastModifiedDateTtv = (TextView) view.findViewById(R.id.lastModifiedDateTtv);
        categoryTtv         = (TextView) view.findViewById(R.id.categoryTtv);
        questionsTtv        = (TextView) view.findViewById(R.id.questionsTtv);
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            getActivity().setTitle(getActivity().getResources().getString(R.string.details_title));

            if (getArguments() != null) {
                surveyDetailsData   = getArguments().getParcelable(getActivity().getResources().getString(R.string.data_parcelable_key));
                String type = getArguments().getString(getResources().getString(R.string.type));
                if (surveyDetailsData != null && type != null) {
                    if (type.equals(getResources().getString(R.string.pending)) || surveyDetailsData.getResponses() == 0) {
                        showStatsLlt.setVisibility(View.GONE);
                        tView.setVisibility(View.GONE);
                    }
                    surveyTitleTtv.setText(surveyDetailsData.getTitle());
                    totalResponsesTtv.setText(getResources().getString(R.string.total_responses, surveyDetailsData.getResponses()));
                    answeredTtv.setText(getResources().getString(R.string.answered, surveyDetailsData.getResponses()));
                    completionRateTtv.setText(getResources().getString(R.string.persentage_text, surveyDetailsData.getCompletionRate()));
                    createdDateTtv.setText(surveyDetailsData.getCreatedDate());
                    lastModifiedDateTtv.setText(surveyDetailsData.getModifiedDate());
                    categoryTtv.setText(surveyDetailsData.getCategory());
                    questionsTtv.setText(getResources().getString(R.string.questions, surveyDetailsData.getQuestion().size()));
                }
                showStatsLlt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!onSavedInstanceStateCalled) getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.surveyDetailsFgmtContainer, SurveyStatsFragment.newInstance(surveyDetailsData), getResources().getString(R.string.survey_stats_fgmt)).commit();
                    }
                });
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onSavedInstanceStateCalled = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        onSavedInstanceStateCalled = false;
    }
}
