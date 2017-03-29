package com.votingsystem.tsiro.fragments.user_survey_tabs_fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.adapters.SurveysRcvAdapter;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.interfaces.OnLoadMoreListener;
import com.votingsystem.tsiro.interfaces.RecyclerViewClickListener;
import com.votingsystem.tsiro.interfaces.SurveysActivityCommonElements;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.observerPattern.RecyclerViewTouchListener;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;
import com.votingsystem.tsiro.recyclerViewStuff.DividerItemDecoration;
import com.votingsystem.tsiro.spinnerLoading.SpinnerLoading;
import com.votingsystem.tsiro.userSurveysActivityMVC.USAMVCPresenterImpl;
import com.votingsystem.tsiro.userSurveysActivityMVC.USAMVCView;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.List;

/**
 * Created by giannis on 19/10/2016.
 */

public class RejectedUserSurveysFragment extends Fragment implements USAMVCView, SwipeRefreshLayout.OnRefreshListener{

    private static final String debugTag = RejectedUserSurveysFragment.class.getSimpleName();
    private View view;
    private RelativeLayout listSurveysRlt;
    private SpinnerLoading spinnerLoading;
    private Button retryBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rejectedSurveysRcV;
    private ImageView noSurveysImv;
    private TextView noSurveysTxv, codeDescTtv;
    private boolean surveysLoaded, noConnectionViewAdded, onSwiped;
    private SurveysRcvAdapter surveysRcvAdapter;
    private SparseIntArray inputValidationCodes;
    private USAMVCPresenterImpl USAMVCpresenterImpl;
    private List<SurveyData> data;
    private SurveysActivityCommonElements commonElements;
    private static int connectionStatus;
    private int total, cPage;

    public RejectedUserSurveysFragment() {}

    public static RejectedUserSurveysFragment newInstance() {
        return new RejectedUserSurveysFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_rejected_user_surveys, container, false);
        listSurveysRlt          =   (RelativeLayout) view.findViewById(R.id.listSurveysRlt);
        spinnerLoading          =   (SpinnerLoading) view.findViewById(R.id.spinnerLoading);
        codeDescTtv             =   (TextView) view.findViewById(R.id.codeDescTtv);
        retryBtn                =   (Button) view.findViewById(R.id.sRetryBtn);
        swipeRefreshLayout      =   (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLlt);
        rejectedSurveysRcV      =   (RecyclerView) view.findViewById(R.id.rejectedSurveysRcV);
        noSurveysImv            =   (ImageView) view.findViewById(R.id.noSurveysImv);
        noSurveysTxv            =   (TextView) view.findViewById(R.id.noSurveysTxv);
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
            rejectedSurveysRcV.setHasFixedSize(true);
            rejectedSurveysRcV.setLayoutManager(new LinearLayoutManager(getActivity()));
            if (!surveysLoaded) {
                surveysRcvAdapter = new SurveysRcvAdapter(null, rejectedSurveysRcV, getResources().getString(R.string.rejected));
                rejectedSurveysRcV.setAdapter(surveysRcvAdapter);
            }
            handleActionsBasedOnNetworkStatus();
            rejectedSurveysRcV.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
            rejectedSurveysRcV.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), rejectedSurveysRcV, new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {


                }
            }));
        }
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionStatus != AppConfig.NO_CONNECTION) {
                    listSurveysRlt.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
                    rejectedSurveysRcV.setVisibility(View.VISIBLE);
                    codeDescTtv.setVisibility(View.GONE);
                    retryBtn.setVisibility(View.GONE);
                    initializeSurveysList();
                }
            }
        });
    }

    @Override
    public void onSuccessUserSurveyDeletion(int survey_id) {}

    @Override
    public void onSuccessSurveysFetched(final List<SurveyData> newData, final int page, int total) {
        if (swipeRefreshLayout.getVisibility() == View.GONE) swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            onSwiped = true;
        }
        if (total != 0) this.total = total;
        if (!surveysLoaded) spinnerLoading.setVisibility(View.GONE);
        Handler handler = new Handler();
        // create new runnable to make surveys list visible only when spinner loading widget is completed faded out of screen
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (page == 1) {
                    data = newData;
                    surveysRcvAdapter.refreshData(data);
                    surveysRcvAdapter.notifyDataSetChanged();
                } else {
                    if (noConnectionViewAdded) {
                        data.remove(data.size() - 1);
                        surveysRcvAdapter.notifyItemRemoved(data.size());
                        noConnectionViewAdded = false;
                    }
                    int y       =   0;
                    int start   =   data.size();
                    int end     =   start + newData.size();
                    for (int i = start + 1; i <= end; i++) {
                        data.add(newData.get(y));
                        surveysRcvAdapter.notifyItemInserted(data.size());
                        y++;
                    }
                    surveysRcvAdapter.setLoaded();
                }
                surveysLoaded = true;
            }
        }, 250);
    }

    @Override
    public void onSuccessSurveyDetailsFetched(SurveyDetailsData data) {}

    @Override
    public void onFailure(int code, int request) {
        if (swipeRefreshLayout.getVisibility() == View.GONE) swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (!surveysLoaded) spinnerLoading.setVisibility(View.GONE);
        if (request == 1) {
            if (code == AppConfig.ERROR_EMPTY_LIST) {
                if (rejectedSurveysRcV.getLayoutManager().getItemCount() != 0) {
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
    public void onRefresh() {
        if (connectionStatus != AppConfig.NO_CONNECTION) {
            if (data != null) {
                surveysRcvAdapter.onSwipeToRefresh();
                int count = surveysRcvAdapter.getItemCount();
                data.clear();
                surveysRcvAdapter.notifyItemRangeRemoved(0, count);
            }
            initializeSurveysList();
        } else {
            onErrorBackgroundView(AppConfig.NO_CONNECTION);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
    }

    public static void updatedNetworkStatus(int status) {
        connectionStatus = status;
    }

    private void handleActionsBasedOnNetworkStatus() {
        if (connectionStatus != AppConfig.NO_CONNECTION) {
            initializeSurveysList();
        } else {
            onErrorBackgroundView(AppConfig.NO_CONNECTION);
        }
    }

    private void onErrorBackgroundView(int code) {
        swipeRefreshLayout.setVisibility(View.GONE);
        listSurveysRlt.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.view_color));
        rejectedSurveysRcV.setVisibility(View.GONE);
        codeDescTtv.setVisibility(View.VISIBLE);
        retryBtn.setVisibility(View.VISIBLE);
        codeDescTtv.setText(getResources().getString(inputValidationCodes.get(code)));
    }

    private void initializeSurveysList() {
        USAMVCpresenterImpl = new USAMVCPresenterImpl(this);
        if (!surveysLoaded) spinnerLoading.setVisibility(View.VISIBLE);
        USAMVCpresenterImpl.getSurveysBasedOnSpecificUserId(new AllSurveysBody(getResources().getString(R.string.list_user_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), -1, getResources().getString(R.string.rejected), 1, 0));

        surveysRcvAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page) {
                cPage = page;
                Log.e(debugTag, "onLoadMore "+ page);
                /* Use Handler to avoid illegal state exception described bellow.
                 * Cannot call this method in a scroll callback. Scroll callbacks might be run during a measure & layout pass where you cannot change the RecyclerView data. Any method call that might change the structure of the RecyclerView or the adapter contents should be postponed to the next frame.
                 */
                Handler handler = new Handler();
//                surveysRcvAdapter.updateConnectionStatus(connectionStatus);
                if (connectionStatus != AppConfig.NO_CONNECTION) {
                    if (data != null) {
                        if (rejectedSurveysRcV.getLayoutManager().getItemCount() < total) {
//                            data.add(null);
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    surveysRcvAdapter.notifyItemChanged(data.size() - 1);
//                                }
//                            });
                            USAMVCpresenterImpl.getSurveysBasedOnSpecificUserId(new AllSurveysBody(getResources().getString(R.string.list_user_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), -1, getResources().getString(R.string.rejected), page, 0));
                        }
                    }
                } else {
                    if (rejectedSurveysRcV.getLayoutManager().getItemCount() < total) {
                        data.add(null);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                surveysRcvAdapter.notifyItemInserted(data.size() - 1);
                                noConnectionViewAdded = true;
                            }
                        });
                    }
                }
            }
        });
    }
}
