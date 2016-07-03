package com.votingsystem.tsiro.fragments.survey_tabs_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.ObserverPattern.RecyclerViewTouchListener;
import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.RecyclerViewStuff.DividerItemDecoration;
import com.votingsystem.tsiro.SurveysActivityMVC.SAMVCPresenterImpl;
import com.votingsystem.tsiro.SurveysActivityMVC.SAMVCView;
import com.votingsystem.tsiro.adapters.SurveysRcvAdapter;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.interfaces.OnLoadMoreListener;
import com.votingsystem.tsiro.interfaces.RecyclerViewClickListener;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.List;

/**
 * Created by giannis on 19/6/2016.
 */
public class CompletedSurveysFragment extends Fragment implements SAMVCView {

    private static final String debugTag = CompletedSurveysFragment.class.getSimpleName();
    private View view;
    private RecyclerView completedSurveysRcV;
    private ImageView noSurveysImv;
    private TextView noSurveysTxv;
    private SurveysRcvAdapter surveysRcvAdapter;
    private List<SurveyData> data;

    public CompletedSurveysFragment() {}

    public static CompletedSurveysFragment newInstance() {
        return new CompletedSurveysFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(debugTag, "onCreateView");
        if ( view == null ) view = inflater.inflate(R.layout.fragment_completedsurveys, container, false);
        completedSurveysRcV = (RecyclerView) view.findViewById(R.id.completedSurveysRcV);
        noSurveysImv        =   (ImageView) view.findViewById(R.id.noSurveysImv);
        noSurveysTxv        =   (TextView) view.findViewById(R.id.noSurveysTxv);
        return view;
    }

    // TODO: 21/6/2016 configure Limit and offset values
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(debugTag, "onActivityCreated " + savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        completedSurveysRcV.setHasFixedSize(true);
        completedSurveysRcV.setLayoutManager(linearLayoutManager);

        if (savedInstanceState == null) {
            final SAMVCPresenterImpl SAMVCpresenterImpl = new SAMVCPresenterImpl(this);
            SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), getResources().getString(R.string.completed), AppConfig.FETCHED_SURVEYS_LIMIT, 0));

            surveysRcvAdapter = new SurveysRcvAdapter(null, completedSurveysRcV, getResources().getString(R.string.completed));
            completedSurveysRcV.setAdapter(surveysRcvAdapter);
            completedSurveysRcV.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
            completedSurveysRcV.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), completedSurveysRcV, new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Log.e(debugTag, "POSITION CLICKED: "+position);
                }
            }));

            surveysRcvAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(int offset) {
                    if (data != null && data.size() % 10 == 0) {
                        data.add(null);
                        surveysRcvAdapter.notifyItemChanged(data.size() - 1);
                        SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), getResources().getString(R.string.completed), AppConfig.FETCHED_SURVEYS_LIMIT, offset));
                    }
                }
            });
        }
    }

    @Override
    public void onSuccessSurveysFetched(List<SurveyData> newData, int offset) {
        if (offset == 0) {
            this.data = newData;
            surveysRcvAdapter.refreshData(newData);
            surveysRcvAdapter.notifyDataSetChanged();
        } else {
            this.data.remove(data.size() - 1);
            surveysRcvAdapter.notifyItemRemoved(data.size());
            int y       =   0;
            int start   =   data.size();
            int end     =   start + newData.size();
            for (int i = start + 1; i <= end; i++) {
                data.add(newData.get(y));
                surveysRcvAdapter.notifyItemInserted(this.data.size());
                y++;
            }
            surveysRcvAdapter.setLoaded();
        }
    }

    @Override
    public void onFailure(int code) {
        if (code == AppConfig.ERROR_EMPTY_LIST) {
            if (completedSurveysRcV.getLayoutManager().getItemCount() != 0) {
                this.data.remove(data.size() - 1);
                surveysRcvAdapter.notifyItemRemoved(data.size());
            } else {
                noSurveysImv.setVisibility(View.VISIBLE);
                noSurveysTxv.setVisibility(View.VISIBLE);
            }
        }
    }
}
