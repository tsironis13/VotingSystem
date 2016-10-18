package com.votingsystem.tsiro.fragments;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rey.material.widget.Button;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.observerPattern.NetworkStateListeners;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.broadcastReceivers.NetworkStateReceiver;
import com.votingsystem.tsiro.interfaces.SurveysQuestionsActivityCommonElements;
import com.votingsystem.tsiro.mainClasses.SurveyQuestionsActivity;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 25/7/2016.
 */
public class ErrorFragment extends Fragment implements NetworkStateListeners {

    private static final String debugTag = ErrorFragment.class.getSimpleName();
    private View view;
    private TextView codeDescTtv;
    private Button retryBtn;
    private DashboardFragment dashboardFragment;
    private NetworkStateReceiver networkStateReceiver;
    private int connectionStatus, code;
    private String tag;
    private SurveysQuestionsActivityCommonElements commonElements;

    public static ErrorFragment newInstance(String tag, int code) {
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putInt("code", code);
        ErrorFragment errorFragment = new ErrorFragment();
        errorFragment.setArguments(bundle);
        return errorFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SurveyQuestionsActivity) this.commonElements = (SurveysQuestionsActivityCommonElements) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view = inflater.inflate(R.layout.fragment_error, container, false);
        codeDescTtv = (TextView) view.findViewById(R.id.codeDescTtv);
        retryBtn    = (Button) view.findViewById(R.id.retryBtn);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SparseIntArray inputValidationCodes = AppConfig.getCodes();
        if (getArguments() != null) {
            tag     = getArguments().getString(getResources().getString(R.string.tag));
            code    = getArguments().getInt(getResources().getString(R.string.code));
        }
        dashboardFragment       = new DashboardFragment();
        networkStateReceiver    = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        getActivity().registerReceiver(networkStateReceiver, new IntentFilter(getResources().getString(R.string.connectivity_change)));
        codeDescTtv.setText(getResources().getString(inputValidationCodes.get(code)));
        retryBtn.setTransformationMethod(null);
        retryBtn.setClickable(true);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionStatus != AppConfig.NO_CONNECTION)
                    if (tag.equals(getResources().getString(R.string.dashboard_fgmt))) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.baseFrlt, dashboardFragment, getResources().getString(R.string.dashboard_fgmt)).commit();
                    } else if (tag.equals(getResources().getString(R.string.survey_questions_fgmt))) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(ErrorFragment.this).commit();
                        commonElements.onErrorFragmentRemove();
                    }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        getActivity().unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void networkStatus(int connectionType) {
        connectionStatus = connectionType;
    }
}
