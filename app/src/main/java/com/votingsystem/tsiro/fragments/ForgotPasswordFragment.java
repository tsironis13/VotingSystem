package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.votingsystem.tsiro.ObserverPattern.ConnectivityObserver;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by user on 6/12/2015.
 */
public class ForgotPasswordFragment extends Fragment {

    private static final String debugTag = ForgotPasswordFragment.class.getSimpleName();
    private Button sendEmailBtn;
    private TextView signInHereTtv, registerTtv;
    private View view;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private ConnectivityObserver connectivityObserver;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_forgotpassword, container, false);
        sendEmailBtn            =   (Button) view.findViewById(R.id.sendEmailBtn);
        signInHereTtv           =   (TextView) view.findViewById(R.id.signInHereTtv);
        registerTtv             =   (TextView) view.findViewById(R.id.registerTtv);
        connectivityObserver    =   getArguments().getParcelable("connectivityObserver");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sendEmailBtn.setTransformationMethod(null);
        setSignInHereSpan();
        setRegisterSpan();
        signInHereTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof TextView) commonElements.signInHereOnClick();
            }
        });
        registerTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( v instanceof TextView ) commonElements.registerOnClick();
            }
        });
        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(debugTag, "CONNECTIVITY STATUS: " + connectivityObserver.getConnectivityStatus(getActivity()));
            }
        });
    }

    private void setSignInHereSpan(){ commonElements.setLoginActivitySpan(signInHereTtv, getResources().getString(R.string.signInHere), 22, 34, 1); }
    private void setRegisterSpan() { commonElements.setLoginActivitySpan(registerTtv, getResources().getString(R.string.register), 16, 23, 0); }
}
