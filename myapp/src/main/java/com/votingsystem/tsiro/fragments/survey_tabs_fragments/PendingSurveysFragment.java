package com.votingsystem.tsiro.fragments.survey_tabs_fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.ObserverPattern.RecyclerViewTouchListener;
import com.votingsystem.tsiro.POJO.AllSurveys;
import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.RecyclerViewStuff.DividerItemDecoration;
import com.votingsystem.tsiro.SurveysActivityMVC.SAMVCPresenterImpl;
import com.votingsystem.tsiro.SurveysActivityMVC.SAMVCView;
import com.votingsystem.tsiro.adapters.SurveysRcvAdapter;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.interfaces.OnLoadMoreListener;
import com.votingsystem.tsiro.interfaces.RecyclerViewClickListener;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.mainClasses.SurveyDetailsActivity;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;
import com.votingsystem.tsiro.votingsystem.R;
import java.util.List;


/**
 * Created by giannis on 19/6/2016.
 */
public class PendingSurveysFragment extends Fragment implements SAMVCView{

    private static final String debugTag = PendingSurveysFragment.class.getSimpleName();
    private View view;
    private RecyclerView pendingSurveysRcV;
    private ImageView noSurveysImv;
    private TextView noSurveysTxv;
    private SAMVCPresenterImpl SAMVCpresenterImpl;
    private SurveysRcvAdapter surveysRcvAdapter;
    private List<SurveyData> data;
    private ProgressDialog progressDialog;

    public PendingSurveysFragment() {}

    public static PendingSurveysFragment newInstance() {
        return new PendingSurveysFragment();
    }
//    public static PendingSurveysFragment newInstance(List<SurveyData> data) {
//        PendingSurveysFragment pendingSurveysFragment = new PendingSurveysFragment();
//
//        Bundle args = new Bundle();
//        args.putParcelableArrayList("data", (ArrayList<? extends Parcelable>) data);
//        //args.putSerializable("data", (Serializable) data);
//        pendingSurveysFragment.setArguments(args);
//        return pendingSurveysFragment;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_pendingsurveys, container, false);
        pendingSurveysRcV = (RecyclerView) view.findViewById(R.id.pendingSurveysRcV);
        noSurveysImv        =   (ImageView) view.findViewById(R.id.noSurveysImv);
        noSurveysTxv        =   (TextView) view.findViewById(R.id.noSurveysTxv);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            SAMVCpresenterImpl = new SAMVCPresenterImpl(this);
            SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), getResources().getString(R.string.pending), AppConfig.FETCHED_SURVEYS_LIMIT, 0));

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            pendingSurveysRcV.setLayoutManager(linearLayoutManager);

            surveysRcvAdapter = new SurveysRcvAdapter(null, pendingSurveysRcV, getResources().getString(R.string.pending));
            pendingSurveysRcV.setAdapter(surveysRcvAdapter);
            pendingSurveysRcV.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
            pendingSurveysRcV.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), pendingSurveysRcV, new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    //Log.e(debugTag, "POSITION CLICKED: "+position);
                    getSurveyDetails(data.get(position).getSurveyId());
                }
            }));

            surveysRcvAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(int offset) {
                    if (data != null && data.size() % 10 == 0) {
                        data.add(null);
                        surveysRcvAdapter.notifyItemChanged(data.size() - 1);
                        SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), getResources().getString(R.string.pending), AppConfig.FETCHED_SURVEYS_LIMIT, offset));
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelableArrayList("data", (ArrayList<? extends Parcelable>) this.data);
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
    public void onSuccessSurveyDetailsFetched(final SurveyDetailsData surveyDetailsData) {
        if (progressDialog.isShowing()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    getActivity().finish();
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(getActivity(), SurveyDetailsActivity.class);
                    bundle.putString(getResources().getString(R.string.details_activ_action_key), getResources().getString(R.string.show_details));
                    bundle.putString(getResources().getString(R.string.type), getResources().getString(R.string.pending));
                    bundle.putParcelable(getResources().getString(R.string.data_parcelable_key), surveyDetailsData);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }, 1500);
        }
    }

    @Override
    public void onFailure(int code) {
        if (code == AppConfig.ERROR_EMPTY_LIST) {
            if (pendingSurveysRcV.getLayoutManager().getItemCount() != 0) {
                this.data.remove(data.size() - 1);
                surveysRcvAdapter.notifyItemRemoved(data.size());
            } else {
                noSurveysImv.setVisibility(View.VISIBLE);
                noSurveysTxv.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getActivity().getResources().getString(R.string.message));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void getSurveyDetails(int surveyId) {
        initializeProgressDialog();
        SurveyAnswersBody surveyAnswersBody = new SurveyAnswersBody(getResources().getString(R.string.get_survey_stats), true, LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), surveyId, null);
        SAMVCpresenterImpl.getSurveyDetails(surveyAnswersBody);
    }
}
