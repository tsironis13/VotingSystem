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
import com.votingsystem.tsiro.observerPattern.NetworkStateListeners;
import com.votingsystem.tsiro.observerPattern.RecyclerViewTouchListener;
import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.recyclerViewStuff.DividerItemDecoration;
import com.votingsystem.tsiro.spinnerLoading.SpinnerLoading;
import com.votingsystem.tsiro.surveysActivityMVC.SAMVCPresenterImpl;
import com.votingsystem.tsiro.surveysActivityMVC.SAMVCView;
import com.votingsystem.tsiro.adapters.SurveysRcvAdapter;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.broadcastReceivers.NetworkStateReceiver;
import com.votingsystem.tsiro.interfaces.OnLoadMoreListener;
import com.votingsystem.tsiro.interfaces.RecyclerViewClickListener;
import com.votingsystem.tsiro.interfaces.SurveysActivityCommonElements;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.mainClasses.SurveyDetailsActivity;
import com.votingsystem.tsiro.mainClasses.SurveyQuestionsActivity;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.List;


/**
 * Created by giannis on 19/6/2016.
 */
public class OngoingSurveysFragment extends Fragment implements SAMVCView, SwipeRefreshLayout.OnRefreshListener {

    private static final String debugTag = OngoingSurveysFragment.class.getSimpleName();
    private View view;
    private RelativeLayout listSurveysRlt;
    private SpinnerLoading spinnerLoading;
    private Button retryBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView ongoingSurveysRcV;
    private ImageView noSurveysImv;
    private TextView noSurveysTxv, codeDescTtv;
    private SAMVCPresenterImpl SAMVCpresenterImpl;
    private SurveysRcvAdapter surveysRcvAdapter;
    private List<SurveyData> data;
    private int total, cPage;
    private static int connectionStatus;
    private boolean surveysLoaded, noConnectionViewAdded, onSwiped;
    private SurveysActivityCommonElements commonElements;
    private SparseIntArray inputValidationCodes;

    public OngoingSurveysFragment() {}

    public static OngoingSurveysFragment newInstance() {
        return new OngoingSurveysFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) this.commonElements = (SurveysActivityCommonElements) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_ongoingsurveys, container, false);
        listSurveysRlt      =   (RelativeLayout) view.findViewById(R.id.listSurveysRlt);
        spinnerLoading      =   (SpinnerLoading) view.findViewById(R.id.spinnerLoading);
        codeDescTtv         =   (TextView) view.findViewById(R.id.codeDescTtv);
        retryBtn            =   (Button) view.findViewById(R.id.retryBtn);
        swipeRefreshLayout  =   (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLlt);
        ongoingSurveysRcV   =   (RecyclerView) view.findViewById(R.id.ongoingSurveysRcV);
        noSurveysImv        =   (ImageView) view.findViewById(R.id.noSurveysImv);
        noSurveysTxv        =   (TextView) view.findViewById(R.id.noSurveysTxv);
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
            ongoingSurveysRcV.setHasFixedSize(true);
            ongoingSurveysRcV.setLayoutManager(new LinearLayoutManager(getActivity()));
            if (!surveysLoaded) {
                surveysRcvAdapter = new SurveysRcvAdapter(null, ongoingSurveysRcV, getResources().getString(R.string.ongoing));
                ongoingSurveysRcV.setAdapter(surveysRcvAdapter);
            }
            handleActionsBasedOnNetworkStatus();
            ongoingSurveysRcV.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
            ongoingSurveysRcV.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), ongoingSurveysRcV, new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Bundle bundle = new Bundle();
                    if (view != null) {
                        if (view instanceof LinearLayout) {
                            if (connectionStatus != AppConfig.NO_CONNECTION) {
//                            surveysRcvAdapter.notifyItemRemoved(data.size() - 1);
                                SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), getResources().getString(R.string.ongoing), cPage, -1));
                            }
                        } else {
                            if ((Integer) view.getTag() == 0) {
                                getActivity().finish();
                                Intent intent = new Intent(getActivity(), SurveyQuestionsActivity.class);
                                bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.firm_surveys));
                                bundle.putInt(getResources().getString(R.string.survey_id), data.get(position).getSurveyId());
                                bundle.putString(getResources().getString(R.string.survey_title), data.get(position).getTitle());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else {
                                Log.e(debugTag, "VOTED");
                                if (connectionStatus != AppConfig.NO_CONNECTION) {
                                    getSurveyDetails(data.get(position).getSurveyId());
                                } else {
                                    commonElements.showErrorContainerSnackbar(getResources().getString(inputValidationCodes.get(AppConfig.NO_CONNECTION)));
                                }
                            }
                        }
                    }
                }
            }));
        }
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionStatus != AppConfig.NO_CONNECTION) {
                    listSurveysRlt.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
                    ongoingSurveysRcV.setVisibility(View.VISIBLE);
                    codeDescTtv.setVisibility(View.GONE);
                    retryBtn.setVisibility(View.GONE);
                    initializeSurveysList();
                }
            }
        });
    }

    @Override
    public void onSuccessSurveysFetched(final List<SurveyData> newData, final int page, int total) {
        Log.e(debugTag, "TOTAL: "+newData.size()+"");
        if (swipeRefreshLayout.getVisibility() == View.GONE) swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            onSwiped = true;
        }
        if (total != 0) this.total = total;
        spinnerLoading.setVisibility(View.GONE);
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
                    int y       = 0;
                    int start   = data.size();
                    int end     = start + newData.size();
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
    public void onSuccessSurveyDetailsFetched(final SurveyDetailsData surveyDetailsData) {
        if (getActivity() != null) {
            getActivity().finish();
            Bundle bundle = new Bundle();
            Intent intent = new Intent(getActivity(), SurveyDetailsActivity.class);
            bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.firm_surveys));
            bundle.putString(getResources().getString(R.string.details_activ_action_key), getResources().getString(R.string.show_details));
            //pass type into bundle to show or hide show stats Llt (showStatsLlt) inside SurveyDetailsFragment
            // depending the type of survey
            bundle.putString(getResources().getString(R.string.type), getResources().getString(R.string.ongoing));
            bundle.putParcelable(getResources().getString(R.string.data_parcelable_key), surveyDetailsData);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onFailure(int code, int request) {
        Log.e(debugTag, "onFailure");
        if (swipeRefreshLayout.getVisibility() == View.GONE) swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (!surveysLoaded) spinnerLoading.setVisibility(View.GONE);
        if (request == 1) {
            if (code == AppConfig.ERROR_EMPTY_LIST) {
                if (ongoingSurveysRcV.getLayoutManager().getItemCount() != 0) {
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
        Log.e(debugTag, "onRefresh");
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

    private void onErrorBackgroundView(int code) {
        swipeRefreshLayout.setVisibility(View.GONE);
        listSurveysRlt.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.view_color));
        ongoingSurveysRcV.setVisibility(View.GONE);
        codeDescTtv.setVisibility(View.VISIBLE);
        retryBtn.setVisibility(View.VISIBLE);
        codeDescTtv.setText(getResources().getString(inputValidationCodes.get(code)));
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

    private void initializeSurveysList() {
        SAMVCpresenterImpl = new SAMVCPresenterImpl(this);
        if (!surveysLoaded) spinnerLoading.setVisibility(View.VISIBLE);
        SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), getResources().getString(R.string.ongoing), 1, -1));

        surveysRcvAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page) {
                cPage = page;
                /* Use Handler to avoid illegal state exception described bellow.
                 * Cannot call this method in a scroll callback. Scroll callbacks might be run during a measure & layout pass where you cannot change the RecyclerView data. Any method call that might change the structure of the RecyclerView or the adapter contents should be postponed to the next frame.
                 */
                Handler handler = new Handler();
                if (connectionStatus != AppConfig.NO_CONNECTION) {
                    if (data != null) {
                        if (ongoingSurveysRcV.getLayoutManager().getItemCount() < total) {
//                            data.add(null);
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    surveysRcvAdapter.notifyItemChanged(data.size() - 1);
//                                }
//                            });
                            SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), getResources().getString(R.string.ongoing ), page, -1));
                        }
                    }
                } else {
                    if (ongoingSurveysRcV.getLayoutManager().getItemCount() < total) {
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

    private void getSurveyDetails(int surveyId) {
        SurveyAnswersBody surveyAnswersBody = new SurveyAnswersBody(getResources().getString(R.string.get_survey_stats), false, LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.firm_id), 0), LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0), surveyId, null);
        SAMVCpresenterImpl.getSurveyDetails(surveyAnswersBody);
    }

}
