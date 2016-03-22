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

import com.rey.material.widget.SnackBar;
import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by user on 6/12/2015.
 */
public class ForgotPasswordFragment extends Fragment {

    private static final String debugTag = ForgotPasswordFragment.class.getSimpleName();
    private Button sendEmailBtn;
    private TextView signInHereTtv, registerTtv;
    private SnackBar snackBar;
    private View view;
    private LoginActivityCommonElementsAndMuchMore commonElements;

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
        snackBar                =   ((LoginActivity)getActivity()).getSnackBar();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (snackBar.isShown()) snackBar.dismiss();
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
                
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.commonElements = null;
        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    private void setSignInHereSpan(){ commonElements.setLoginActivitySpan(signInHereTtv, getResources().getString(R.string.signInHere), 22, 34, 1); }
    private void setRegisterSpan() { commonElements.setLoginActivitySpan(registerTtv, getResources().getString(R.string.register), 16, 23, 0); }
}
