package com.votingsystem.tsiro.fragments;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.widget.Button;
import com.votingsystem.tsiro.ObserverPattern.NetworkStateListeners;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.broadcastReceivers.NetworkStateReceiver;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 25/7/2016.
 */
public class ErrorFragment extends Fragment implements NetworkStateListeners {

    private static final String debugTag = ErrorFragment.class.getSimpleName();
    private View view;
    private Button retryBtn;
    private DashboardFragment dashboardFragment;
    private NetworkStateReceiver networkStateReceiver;
    private int connectionStatus;
    private String tag;

    public static ErrorFragment newInstance(String tag) {
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        ErrorFragment errorFragment = new ErrorFragment();
        errorFragment.setArguments(bundle);
        return errorFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view = inflater.inflate(R.layout.fragment_error, container, false);
        retryBtn = (Button) view.findViewById(R.id.retryBtn);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            tag = getArguments().getString(getResources().getString(R.string.tag));
        }
        dashboardFragment       = new DashboardFragment();
        networkStateReceiver    = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        getActivity().registerReceiver(networkStateReceiver, new IntentFilter(getResources().getString(R.string.connectivity_change)));
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionStatus != AppConfig.NO_CONNECTION)
                    if (tag.equals(getResources().getString(R.string.dashboard_fgmt))) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.baseFrlt, dashboardFragment, getResources().getString(R.string.dashboard_fgmt)).commit();
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
