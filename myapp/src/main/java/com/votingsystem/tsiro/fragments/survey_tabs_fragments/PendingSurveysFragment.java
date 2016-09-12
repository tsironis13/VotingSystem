package com.votingsystem.tsiro.fragments.survey_tabs_fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rey.material.widget.Button;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.ObserverPattern.NetworkStateListeners;
import com.votingsystem.tsiro.ObserverPattern.RecyclerViewTouchListener;
import com.votingsystem.tsiro.POJO.AllSurveys;
import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.RecyclerViewStuff.DividerItemDecoration;
import com.votingsystem.tsiro.SurveysActivityMVC.SAMVCPresenterImpl;
import com.votingsystem.tsiro.SurveysActivityMVC.SAMVCView;
import com.votingsystem.tsiro.adapters.SurveysRcvAdapter;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.broadcastReceivers.NetworkStateReceiver;
import com.votingsystem.tsiro.interfaces.OnLoadMoreListener;
import com.votingsystem.tsiro.interfaces.RecyclerViewClickListener;
import com.votingsystem.tsiro.interfaces.SurveysActivityCommonElements;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.mainClasses.SurveyDetailsActivity;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;
import com.votingsystem.tsiro.votingsystem.R;
import java.util.List;


/**
 * Created by giannis on 19/6/2016.
 */
public class PendingSurveysFragment extends Fragment implements SAMVCView, NetworkStateListeners, SwipeRefreshLayout.OnRefreshListener {

    private static final String debugTag = PendingSurveysFragment.class.getSimpleName();
    private View view;
    private RelativeLayout listSurveysRlt;
    private Button retryBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pendingSurveysRcV;
    private ImageView noSurveysImv;
    private TextView noSurveysTxv, codeDescTtv;
    private SAMVCPresenterImpl SAMVCpresenterImpl;
    private SurveysRcvAdapter surveysRcvAdapter;
    private List<SurveyData> data;
    private ProgressDialog progressDialog;
    private NetworkStateReceiver networkStateReceiver;
    private Bundle savedInstanceState;
    private int connectionStatus, total;
    private boolean surveysLoaded, fragmentCreated;
    private SurveysActivityCommonElements commonElements;
    private SparseIntArray inputValidationCodes;

    public PendingSurveysFragment() {
    }

    public static PendingSurveysFragment newInstance(int connectionStatus) {
        Bundle bundle = new Bundle();
        bundle.putInt("connection_status", connectionStatus);
        PendingSurveysFragment pendingSurveysFragment = new PendingSurveysFragment();
        pendingSurveysFragment.setArguments(bundle);
        return pendingSurveysFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            this.commonElements = (SurveysActivityCommonElements) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_pendingsurveys, container, false);
        listSurveysRlt = (RelativeLayout) view.findViewById(R.id.listSurveysRlt);
        codeDescTtv = (TextView) view.findViewById(R.id.codeDescTtv);
        retryBtn = (Button) view.findViewById(R.id.retryBtn);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLlt);
        pendingSurveysRcV = (RecyclerView) view.findViewById(R.id.pendingSurveysRcV);
        noSurveysImv = (ImageView) view.findViewById(R.id.noSurveysImv);
        noSurveysTxv = (TextView) view.findViewById(R.id.noSurveysTxv);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.accentColor));
        inputValidationCodes = AppConfig.getCodes();
        retryBtn.setTransformationMethod(null);
        if (savedInstanceState == null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            pendingSurveysRcV.setHasFixedSize(true);
            pendingSurveysRcV.setLayoutManager(linearLayoutManager);

            if (!surveysLoaded) {
                surveysRcvAdapter = new SurveysRcvAdapter(null, pendingSurveysRcV, getResources().getString(R.string.pending));
                pendingSurveysRcV.setAdapter(surveysRcvAdapter);
            }
        }
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        getActivity().registerReceiver(networkStateReceiver, new IntentFilter(getResources().getString(R.string.connectivity_change)));

        this.savedInstanceState = savedInstanceState;
        fragmentCreated = true;
        if (getArguments() != null)
            connectionStatus = getArguments().getInt(getResources().getString(R.string.connection_status));

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionStatus != AppConfig.NO_CONNECTION) {
                    listSurveysRlt.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
                    pendingSurveysRcV.setVisibility(View.VISIBLE);
                    codeDescTtv.setVisibility(View.GONE);
                    retryBtn.setVisibility(View.GONE);
                    initializeSurveysList();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        getActivity().unregisterReceiver(networkStateReceiver);
        //if (this.SAMVCpresenterImpl != null) SAMVCpresenterImpl.onDestroy();
    }

    @Override
    public void onSuccessSurveysFetched(List<SurveyData> newData, int offset, int total) {
        if (swipeRefreshLayout.getVisibility() == View.GONE) swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (total != 0) this.total = total;
        if (offset == 0) {
            this.data = newData;
            surveysRcvAdapter.refreshData(newData);
            surveysRcvAdapter.notifyDataSetChanged();
        } else {
            this.data.remove(data.size() - 1);
            surveysRcvAdapter.notifyItemRemoved(data.size());
            int y = 0;
            int start = data.size();
            int end = start + newData.size();
            for (int i = start + 1; i <= end; i++) {
                data.add(newData.get(y));
                surveysRcvAdapter.notifyItemInserted(this.data.size());
                y++;
            }
            surveysRcvAdapter.setLoaded();
        }
        surveysLoaded = true;
    }

    @Override
    public void onSuccessSurveyDetailsFetched(final SurveyDetailsData surveyDetailsData) {
        if (progressDialog.isShowing()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    if (getActivity() != null) {
                        getActivity().finish();
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(getActivity(), SurveyDetailsActivity.class);
                        bundle.putString(getResources().getString(R.string.details_activ_action_key), getResources().getString(R.string.show_details));
                        bundle.putString(getResources().getString(R.string.type), getResources().getString(R.string.pending));
                        bundle.putParcelable(getResources().getString(R.string.data_parcelable_key), surveyDetailsData);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }, 1500);
        }
    }

    @Override
    public void onFailure(int code, int request) {
        if (swipeRefreshLayout.getVisibility() == View.GONE) swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (request == 1) {
            if (code == AppConfig.ERROR_EMPTY_LIST) {
                if (pendingSurveysRcV.getLayoutManager().getItemCount() != 0) {
                    this.data.remove(data.size() - 1);
                    surveysRcvAdapter.notifyItemRemoved(data.size());
                } else {
                    noSurveysImv.setVisibility(View.VISIBLE);
                    noSurveysTxv.setVisibility(View.VISIBLE);
                }
            } else {
                onErrorBackgroundView(code);
            }
        } else {
            if (isAdded()) commonElements.showErrorContainerSnackbar(getResources().getString(inputValidationCodes.get(code)));
        }
    }

    @Override
    public void networkStatus(int connectionType) {
        connectionStatus = connectionType;
        if (savedInstanceState == null)
            if (!surveysLoaded && connectionStatus != AppConfig.NO_CONNECTION) {
                if (fragmentCreated) initializeSurveysList();
            } else if (!surveysLoaded) {
                onErrorBackgroundView(AppConfig.NO_CONNECTION);
            }
        fragmentCreated = false;
    }

    @Override
    public void onRefresh() {
        if (connectionStatus != AppConfig.NO_CONNECTION) {
            initializeSurveysList();
        } else {
            onErrorBackgroundView(AppConfig.NO_CONNECTION);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void onErrorBackgroundView(int code) {
        swipeRefreshLayout.setVisibility(View.GONE);
        listSurveysRlt.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.view_color));
        pendingSurveysRcV.setVisibility(View.GONE);
        codeDescTtv.setVisibility(View.VISIBLE);
        retryBtn.setVisibility(View.VISIBLE);
        codeDescTtv.setText(getResources().getString(inputValidationCodes.get(code)));
    }

    private void initializeSurveysList() {
        SAMVCpresenterImpl = new SAMVCPresenterImpl(this);
        SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), getResources().getString(R.string.pending), AppConfig.FETCHED_SURVEYS_LIMIT, 0));

        surveysRcvAdapter = new SurveysRcvAdapter(null, pendingSurveysRcV, getResources().getString(R.string.pending));
        pendingSurveysRcV.setAdapter(surveysRcvAdapter);
        pendingSurveysRcV.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
        pendingSurveysRcV.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), pendingSurveysRcV, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (view != null)
                    if (view instanceof LinearLayout) {
                        if (connectionStatus != AppConfig.NO_CONNECTION) {
                            surveysRcvAdapter.notifyItemRemoved(data.size() - 1);
                            SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), getResources().getString(R.string.pending), AppConfig.FETCHED_SURVEYS_LIMIT, position));
                        }
                    } else {
                        if (connectionStatus != AppConfig.NO_CONNECTION) {
                            getSurveyDetails(data.get(position).getSurveyId());
                        } else {
                            commonElements.showErrorContainerSnackbar(getResources().getString(inputValidationCodes.get(AppConfig.NO_CONNECTION)));
                        }
                    }
            }
        }));

        surveysRcvAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(int offset) {
                surveysRcvAdapter.updateConnectionStatus(connectionStatus);
                if (connectionStatus != AppConfig.NO_CONNECTION) {
                    if (data != null) {
                        if (pendingSurveysRcV.getLayoutManager().getItemCount() < total) {
                            Handler handler = new Handler();
                            data.add(null);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    surveysRcvAdapter.notifyItemChanged(data.size() - 1);
                                }
                            });
                            SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), getResources().getString(R.string.pending), AppConfig.FETCHED_SURVEYS_LIMIT, offset));
                        }
                    }
                } else {
                    if (pendingSurveysRcV.getLayoutManager().getItemCount() < total) {
                        data.add(null);
                        surveysRcvAdapter.notifyItemInserted(data.size() - 1);
                    }
                }
            }
        });
    }

    private void initializeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
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
