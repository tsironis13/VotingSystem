package com.votingsystem.tsiro.mainClasses;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.votingsystem.tsiro.adapters.UserSurveysPagerAdapter;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.CompletedSurveysFragment;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.OngoingSurveysFragment;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.PendingSurveysFragment;
import com.votingsystem.tsiro.observerPattern.NetworkStateListeners;
import com.votingsystem.tsiro.observerPattern.RecyclerViewTouchListener;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.POJO.SurveysFields;
import com.votingsystem.tsiro.recyclerViewStuff.DividerItemDecoration;
import com.votingsystem.tsiro.sqLite.MySQLiteHelper;
import com.votingsystem.tsiro.surveysActivityMVC.SAMVCPresenterImpl;
import com.votingsystem.tsiro.surveysActivityMVC.SAMVCView;
import com.votingsystem.tsiro.adapters.SearchSurveysRcvAdapter;
import com.votingsystem.tsiro.adapters.SurveysPagerAdapter;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.broadcastReceivers.NetworkStateReceiver;
import com.votingsystem.tsiro.interfaces.RecyclerViewClickListener;
import com.votingsystem.tsiro.interfaces.SurveysActivityCommonElements;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.List;

/**
 * Created by giannis on 19/6/2016.
 */
public class SurveysActivity extends AppCompatActivity implements NetworkStateListeners, SurveysActivityCommonElements, SAMVCView{

    private static final String debugTag = SurveysActivity.class.getSimpleName();
    private CoordinatorLayout coordinatorLayout;
    private NetworkStateReceiver networkStateReceiver;
    private Dialog toolbarSearchDialog;
    private SAMVCPresenterImpl SAMVCpresenterImpl;
    private List<SurveysFields> matchesList;
    private SparseIntArray inputValidationCodes;
    private int connectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surveys_activity);

        Toolbar toolbar         =   (Toolbar) findViewById(R.id.appBar);
        coordinatorLayout       =   (CoordinatorLayout) findViewById(R.id.coordinatorLayt);
        TabLayout mTabs         =   (TabLayout) findViewById(R.id.tabs);
        ViewPager mPager        =   (ViewPager) findViewById(R.id.surveysPager);

        inputValidationCodes = AppConfig.getCodes();

        if (getIntent() != null) {
            connectionStatus = getIntent().getIntExtra(getResources().getString(R.string.connection_status), 0);
            if (getIntent().getStringExtra(getResources().getString(R.string.action)).equals(getResources().getString(R.string.firm_surveys))) {
                mPager.setAdapter(new SurveysPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.surveys_tabs), connectionStatus));
            } else {
                mPager.setAdapter(new UserSurveysPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.user_surveys_tabs), connectionStatus));
            }
            mTabs.setupWithViewPager(mPager);
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (getIntent().getStringExtra(getResources().getString(R.string.action)).equals(getResources().getString(R.string.firm_surveys))) {
                getSupportActionBar().setTitle(getResources().getString(R.string.firm_surveys));
            } else {
                getSupportActionBar().setTitle(getResources().getString(R.string.user_surveys));
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SurveysActivity.this, DashboardActivity.class);
                    Bundle sessionBundle = new Bundle();
                    sessionBundle.putInt("user_id", LoginActivity.getSessionPrefs(SurveysActivity.this).getInt(getResources().getString(R.string.user_id), 0));
                    intent.putExtras(sessionBundle);
                    startActivity(intent);
                }
            });
        }
        //SAMVCpresenterImpl.getSurveysBasedOnSpecificFirmId(new AllSurveysBody(getResources().getString(R.string.list_surveys), 2));
//        initializeTabsViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(getResources().getString(R.string.connectivity_change)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_surveys_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search:
                loadToolBarSearchDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void networkStatus(int connectionType) {
        Log.e(debugTag, "Connection status => "+connectionType);
        connectionStatus = connectionType;
        CompletedSurveysFragment.updatedNetworkStatus(connectionStatus);
        OngoingSurveysFragment.updatedNetworkStatus(connectionStatus);
        PendingSurveysFragment.updatedNetworkStatus(connectionStatus);
    }

    @Override
    public void showErrorContainerSnackbar(String desc) {
        Snackbar snkBar = Snackbar.make(coordinatorLayout, desc, Snackbar.LENGTH_LONG);

        View sbView = snkBar.getView();
        sbView.setMinimumHeight((int) MyApplication.convertPixelToDpAndViceVersa(this, 0, 80));
        sbView.setPadding(24,24,24,24);
        android.widget.TextView textView = (android.widget.TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(14);
        textView.setTextColor(ContextCompat.getColor(this, R.color.accentColor));
        snkBar.show();
    }

    @Override
    public void onSuccessSurveysFetched(List<SurveyData> data, int offset, int total) {}

    @Override
    public void onSuccessSurveyDetailsFetched(SurveyDetailsData surveyDetailsData) {
        this.finish();
        Bundle bundle = new Bundle();
        Intent intent = new Intent(SurveysActivity.this, SurveyDetailsActivity.class);
        bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.firm_surveys));
        bundle.putString(getResources().getString(R.string.details_activ_action_key), getResources().getString(R.string.show_details));
        bundle.putString(getResources().getString(R.string.type), getResources().getString(R.string.ongoing));
        bundle.putParcelable(getResources().getString(R.string.data_parcelable_key), surveyDetailsData);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onFailure(int code, int request) {
        showErrorContainerSnackbar(getResources().getString(inputValidationCodes.get(code)));
    }

    private void loadToolBarSearchDialog() {
        SAMVCpresenterImpl = new SAMVCPresenterImpl(this);
        View view                           = SurveysActivity.this.getLayoutInflater().inflate(R.layout.search_view_layout, null);
//        LinearLayout parentToolbarSearch    = (LinearLayout) view.findViewById(R.id.toolbarSearchLlt);
        ImageView arrowBack                 = (ImageView) view.findViewById(R.id.arrowBack);
        final EditText edtToolSearch        = (EditText) view.findViewById(R.id.edt_tool_search);
        final RecyclerView rcvSearch        = (RecyclerView) view.findViewById(R.id.rcvSearch);
        final ImageView clear               = (ImageView) view.findViewById(R.id.clear);
        final TextView txtEmpty = (TextView) view.findViewById(R.id.txt_empty);

//        Utils.setListViewHeightBasedOnChildren(listSearch);

        toolbarSearchDialog = new Dialog(SurveysActivity.this, R.style.MaterialSearch);
        toolbarSearchDialog.setContentView(view);
        toolbarSearchDialog.setCancelable(false);
        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
        toolbarSearchDialog.show();

        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SurveysActivity.this);
        rcvSearch.setHasFixedSize(true);
        rcvSearch.setLayoutManager(linearLayoutManager);
        rcvSearch.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(SurveysActivity.this, R.drawable.divider)));
        final SearchSurveysRcvAdapter searchSurveysRcvAdapter = new SearchSurveysRcvAdapter();
        rcvSearch.setAdapter(searchSurveysRcvAdapter);

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarSearchDialog.dismiss();
            }
        });
        edtToolSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e(debugTag, "char seq => "+charSequence+" i =>" + i+" i1 => "+ i1+" i2 => "+ i2);
                MySQLiteHelper.getInstance(SurveysActivity.this).openDatabase();
                matchesList = MySQLiteHelper.getInstance(SurveysActivity.this).getFirmSurveys(LoginActivity.getSessionPrefs(SurveysActivity.this).getInt(getResources().getString(R.string.firm_id), 0), charSequence.toString());
                searchSurveysRcvAdapter.setData(matchesList);
                if (i >= 0 && i2 == 1) {
                    clear.setVisibility(View.VISIBLE);
                } else if (i == 0 && i2 == 0) {
                    clear.setVisibility(View.GONE);
                    searchSurveysRcvAdapter.setData(null);
                }
                searchSurveysRcvAdapter.notifyDataSetChanged();
                MySQLiteHelper.getInstance(SurveysActivity.this).closeDB();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtToolSearch.getText().length() != 0) {
                    edtToolSearch.getText().clear();
                    searchSurveysRcvAdapter.setData(null);
                    searchSurveysRcvAdapter.notifyDataSetChanged();
                }
            }
        });
        rcvSearch.addOnItemTouchListener(new RecyclerViewTouchListener(SurveysActivity.this, rcvSearch, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.e(debugTag, matchesList.get(position).getSurveyId()+"");
                if (connectionStatus != AppConfig.NO_CONNECTION) {
                    showErrorContainerSnackbar(getResources().getString(inputValidationCodes.get(AppConfig.NO_CONNECTION)));
                    SurveyAnswersBody surveyAnswersBody = new SurveyAnswersBody(getResources().getString(R.string.get_survey_stats), true, LoginActivity.getSessionPrefs(SurveysActivity.this).getInt(getResources().getString(R.string.firm_id), 0), LoginActivity.getSessionPrefs(SurveysActivity.this).getInt(getResources().getString(R.string.user_id), 0), matchesList.get(position).getSurveyId(), null);
                    SAMVCpresenterImpl.getSurveyDetails(surveyAnswersBody);
                }
            }
        }) {
        });
    }

}
