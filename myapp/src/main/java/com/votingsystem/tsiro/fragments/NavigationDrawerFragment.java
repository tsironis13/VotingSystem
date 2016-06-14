package com.votingsystem.tsiro.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.votingsystem.tsiro.POJO.NavDrawerData;
import com.votingsystem.tsiro.RecyclerViewStuff.NavDrawerRcVDivider;
import com.votingsystem.tsiro.RecyclerViewStuff.RecyclerViewTouchListener;
import com.votingsystem.tsiro.adapters.NavDrawerRcViewAdapter;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 24/11/2015.
 */
public class NavigationDrawerFragment extends Fragment {

    private RecyclerView navigationDrawerRcV;
    private NavDrawerRcViewAdapter navDrawerRcViewAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RecyclerView.ItemDecoration navDrawerItemDecoration;
    private int mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View navDrawerViewContainer;
    private static SharedPreferences sessionPrefs;
    private int userId;
    private static final String debugTag = "NavigationDrawerFragment";
    private HashMap<String, Integer> navDrawerMetricsHash;
    private ArrayList<NavDrawerData> listDrawerData;
    private LinearLayoutManager linearLayoutManager;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getUserId(getActivity());
        mUserLearnedDrawer = readFromPreferences(getActivity(), userId, 0);
        //Log.d(debugTag, "mUserLearnedDrawer " + mUserLearnedDrawer);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) {
            view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
           // Log.d(debugTag, "view is null "+view.toString());
        } else {
            //Log.d(debugTag, "view is not null");
                    ((ViewGroup) view.getParent()).removeView(view);
        }
        navigationDrawerRcV = (RecyclerView) view.findViewById(R.id.navigationDrawerRcV);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        navigationDrawerRcV.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ( savedInstanceState != null ) {
            navDrawerRcViewAdapter = new NavDrawerRcViewAdapter(getActivity(), getNavDrawerData());
        } else {
            navDrawerRcViewAdapter = new NavDrawerRcViewAdapter(getActivity(), getNavDrawerData());
        }
        navigationDrawerRcV.setAdapter(navDrawerRcViewAdapter);
        if ( getResources().getConfiguration().orientation == 1 ) {
            navDrawerItemDecoration = new NavDrawerRcVDivider(getActivity(), R.drawable.nav_drawer_divider, 1, getNavDrawersMetricsInPx());
        } else {
            navDrawerItemDecoration = new NavDrawerRcVDivider(getActivity(), R.drawable.nav_drawer_divider, 0, getNavDrawersMetricsInPx());
        }
        navigationDrawerRcV.addItemDecoration(navDrawerItemDecoration);
        navigationDrawerRcV.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), navigationDrawerRcV, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //changeClickedItemColor(view, position);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if ( view != null) {
            ViewGroup parentViewGroup = (ViewGroup) view.getParent();
            if ( parentViewGroup != null ) parentViewGroup.removeAllViews();
        }
    }

    private HashMap<String, Integer> getNavDrawersMetricsInPx() {
        int vertical_spacing = (int) getResources().getDimension(R.dimen.in_common1);
        int row_height = (int) getResources().getDimension(R.dimen.nav_drawer_row_height);
        navDrawerMetricsHash = new HashMap<>();
        navDrawerMetricsHash.put("vertical spacing", vertical_spacing);
        navDrawerMetricsHash.put("row height", row_height);
        return navDrawerMetricsHash;
    }

    public void setUp(int navDrawerFragmentId, DrawerLayout drawerLayout, Toolbar toolbar){
        navDrawerViewContainer = getActivity().findViewById(navDrawerFragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if ( mUserLearnedDrawer == 0 ) {
                    mUserLearnedDrawer = 1;
                    saveToPreferences(getActivity(), userId, mUserLearnedDrawer);
                }
                /*  call supportInvalidateOptionsMenu so that when Drawer layout
                 *  is opened or closed the activity's action bar be refreshed
                 */
                getActivity().supportInvalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                /*  call supportInvalidateOptionsMenu so that when Drawer layout
                 *  is opened or closed the activity's action bar be refreshed
                 */
                getActivity().supportInvalidateOptionsMenu();
            }
        };
        if ( mUserLearnedDrawer == 0 ) {
            mDrawerLayout.openDrawer(navDrawerViewContainer);
        }
        //if ( !mUserLearnedDrawer && !mFromSavedInstanceState ) {
        //    mDrawerLayout.openDrawer(navDrawerViewContainer);
       // }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    private ArrayList<NavDrawerData> getNavDrawerData() {
        String[] category = {getString(R.string.firm), getString(R.string.mysurveys), getString(R.string.newsurvey), getString(R.string.editsurvey), getString(R.string.viewresults), getString(R.string.settings), getString(R.string.logout)};
        int[] icons = {R.drawable.company, R.drawable.mysurveys, R.drawable.newsurvey, R.drawable.editsurvey, R.drawable.viewresults, R.drawable.settings, R.drawable.logout};
        listDrawerData = new ArrayList<NavDrawerData>();
        for ( int i = 0; i < category.length && i < icons.length; i++ ) {
            NavDrawerData navDrawerData = new NavDrawerData();
            navDrawerData.category  = category[i];
            navDrawerData.iconId    = icons[i];
            listDrawerData.add(navDrawerData);
        }
        return listDrawerData;
    }

    private int getUserId(Context context) {
        sessionPrefs = LoginActivity.getSessionPrefs(context);
        userId = sessionPrefs.getInt("user_id", 0);
        //Log.d(debugTag, "user id: " + userId);
        return userId;
    }

    private static void saveToPreferences(Context context, int preferenceName, int preferenceValue) {
        sessionPrefs = LoginActivity.getSessionPrefs(context);
        //Log.d(debugTag, "pref name, value"+ preferenceName + preferenceValue);
        Toast.makeText(context, ""+preferenceName+" "+preferenceValue, Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = sessionPrefs.edit();
        editor.putInt(""+preferenceName, preferenceValue);
        editor.apply();
    }

    private static int readFromPreferences(Context context, int preferenceName, int defaultValue) {
        sessionPrefs = LoginActivity.getSessionPrefs(context);
        return sessionPrefs.getInt(""+preferenceName, defaultValue);
    }
}
