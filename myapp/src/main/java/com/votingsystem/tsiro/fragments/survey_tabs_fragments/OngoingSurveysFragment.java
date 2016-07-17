package com.votingsystem.tsiro.fragments.survey_tabs_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.ObserverPattern.RecyclerViewTouchListener;
import com.votingsystem.tsiro.POJO.AllSurveys;
import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.RecyclerViewStuff.DividerItemDecoration;
import com.votingsystem.tsiro.SurveysActivityMVC.SAMVCPresenterImpl;
import com.votingsystem.tsiro.SurveysActivityMVC.SAMVCView;
import com.votingsystem.tsiro.adapters.SurveysRcvAdapter;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.interfaces.OnLoadMoreListener;
import com.votingsystem.tsiro.interfaces.RecyclerViewClickListener;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.mainClasses.SurveyQuestionsActivity;
import com.votingsystem.tsiro.mainClasses.SurveysActivity;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.votingsystem.R;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by giannis on 19/6/2016.
 */
public class OngoingSurveysFragment extends Fragment implements SAMVCView {

    private static final String debugTag = OngoingSurveysFragment.class.getSimpleName();
    private View view;
    private RecyclerView ongoingSurveysRcV;
    private ImageView noSurveysImv;
    private TextView noSurveysTxv;
    private SAMVCPresenterImpl SAMVCpresenterImpl;
    private SurveysRcvAdapter surveysRcvAdapter;
    private List<SurveyData> data;

    public OngoingSurveysFragment() {}

    public static OngoingSurveysFragment newInstance() {
        return new OngoingSurveysFragment();
    }
//    public static OngoingSurveysFragment newInstance(List<SurveyData> data) {
//        OngoingSurveysFragment ongoingSurveysFragment = new OngoingSurveysFragment();
//
//        Bundle args = new Bundle();
//        args.putParcelableArrayList("data", (ArrayList<? extends Parcelable>) data);
//        //args.putSerializable("data", (Serializable) data);
//        ongoingSurveysFragment.setArguments(args);
//        return ongoingSurveysFragment;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_ongoingsurveys, container, false);
        ongoingSurveysRcV   =   (RecyclerView) view.findViewById(R.id.ongoingSurveysRcV);
        noSurveysImv        =   (ImageView) view.findViewById(R.id.noSurveysImv);
        noSurveysTxv        =   (TextView) view.findViewById(R.id.noSurveysTxv);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            SAMVCpresenterImpl = new SAMVCPresenterImpl(this);
            SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), getResources().getString(R.string.ongoing), AppConfig.FETCHED_SURVEYS_LIMIT, 0));

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            ongoingSurveysRcV.setLayoutManager(linearLayoutManager);

            surveysRcvAdapter = new SurveysRcvAdapter(null, ongoingSurveysRcV, getResources().getString(R.string.ongoing));
            ongoingSurveysRcV.setAdapter(surveysRcvAdapter);
            ongoingSurveysRcV.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
            ongoingSurveysRcV.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), ongoingSurveysRcV, new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Bundle bundle = new Bundle();
                    if (view != null && view instanceof RelativeLayout && (Integer) view.getTag() == 0) {
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), SurveyQuestionsActivity.class);
                        bundle.putInt(getResources().getString(R.string.survey_id), data.get(position).getSurveyId());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Log.e(debugTag, "VOTED");
                    }
                }
            }));

            surveysRcvAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(int offset) {
                    if (data != null && data.size() % 10 == 0) {
                        data.add(null);
                        surveysRcvAdapter.notifyItemChanged(data.size() - 1);
                        SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), getResources().getString(R.string.ongoing ), AppConfig.FETCHED_SURVEYS_LIMIT, offset));
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //if (this.SAMVCpresenterImpl != null) SAMVCpresenterImpl.onDestroy();
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
            if (ongoingSurveysRcV.getLayoutManager().getItemCount() != 0) {
                this.data.remove(data.size() - 1);
                surveysRcvAdapter.notifyItemRemoved(data.size());
            } else {
                noSurveysImv.setVisibility(View.VISIBLE);
                noSurveysTxv.setVisibility(View.VISIBLE);
            }
        }
    }
}
