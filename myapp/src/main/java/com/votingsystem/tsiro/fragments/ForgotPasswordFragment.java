package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.TextView;
import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.LoginActivityMVC.LAMVCPresenterImpl;
import com.votingsystem.tsiro.LoginActivityMVC.LAMVCView;
import com.votingsystem.tsiro.POJO.ResetPassowrdBody;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.List;

/**
 * Created by user on 6/12/2015.
 */
public class ForgotPasswordFragment extends Fragment implements LAMVCView{

    private static final String debugTag = ForgotPasswordFragment.class.getSimpleName();
    private Button sendEmailBtn;
    private TextView signInHereTtv, registerTtv;
    private EditText emailEdt;
    private SnackBar snackBar;
    private ProgressView progressView;
    private View view;
    private int connectionStatus, initialConnectionStatus;
    private LAMVCPresenterImpl LAMVCpresenterImpl;
    private BroadcastReceiver connectionStatusReceiver;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private String registrationToken;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) this.commonElements    =   (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_forgotpassword, container, false);
        emailEdt                    =   (EditText) view.findViewById(R.id.emailEdt);
        sendEmailBtn                =   (Button) view.findViewById(R.id.sendEmailBtn);
        signInHereTtv               =   (TextView) view.findViewById(R.id.signInHereTtv);
        registerTtv                 =   (TextView) view.findViewById(R.id.registerTtv);
        progressView                =   (ProgressView) view.findViewById(R.id.progressView);
        snackBar                    =   ((LoginActivity)getActivity()).getSnackBar();
        initialConnectionStatus     =   getArguments().getInt(getResources().getString(R.string.connectivity_status));
        registrationToken           =   getArguments().getString(getResources().getString(R.string.registration_token));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (snackBar.isShown()) snackBar.dismiss();
        LAMVCpresenterImpl      =   new LAMVCPresenterImpl(this);

        connectionStatus = initialConnectionStatus;
        initializeBroadcastReceivers();

        sendEmailBtn.setTransformationMethod(null);
        setSignInHereSpan();
        setRegisterSpan();

        emailEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LAMVCpresenterImpl.handleInputFieldTextChanges(before, emailEdt, null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionStatus == AppConfig.NO_CONNECTION) {
                    showSnackBar(AppConfig.NO_CONNECTION);
                } else {
                    progressView.start();
                    LAMVCpresenterImpl.resetPassword(isAdded(), fillResetPasswordFields());
                }
            }
        });
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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(connectionStatusReceiver, new IntentFilter(getResources().getString(R.string.network_state_update)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(connectionStatusReceiver);
        LAMVCpresenterImpl.onDestroy();
        this.commonElements = null;
        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
    /*
     *  LAMVCVIEW CALLBACKS
     *
     */
    @Override
    public void handlePasswordTextChanges(com.rey.material.widget.TextView showHidePasswordTtv, int action) {}

    @Override
    public void changeTransformationMethod(TransformationMethod transformationMethod, int action) {}

    @Override
    public void onSuccessfulFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList, boolean firmsLoaded) {}

    @Override
    public void onFailureFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList, boolean firmsLoaded) {}

    @Override
    public void onSuccess() {
        commonElements.dismissErrorContainerSnackBar();
        if (progressView != null && progressView.isShown()) progressView.stop();
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) getActivity().getSupportFragmentManager().popBackStack();
        SignInFragment signInFragment = new SignInFragment();
        getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                        .replace(R.id.loginActivityFgmtContainer, signInFragment, getResources().getString(R.string.signInFgmt))
                                        .commit();
    }

    @Override
    public void onFailure(int code, String field, String hint) {
        if (code == AppConfig.ERROR_EMPTY_REQUIRED_FIELD) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.empty_field, hint), null, code);
        } else if (code == AppConfig.ERROR_INVALID_EMAIL) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.correct_field, hint), emailEdt, code);
        } else if (code == AppConfig.ERROR_NOT_VERIFIED_YET) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.error_not_verified_yet), null, code);
        } else if (code == AppConfig.ERROR_EMAIL_DOESNT_EXIST) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.correct_field, hint), emailEdt, code);
        }
        if (progressView != null && progressView.isShown()) progressView.stop();
    }
    @Override
    public void displayFeedbackMsg(int code) {

    }

    private void showSnackBar(int code) {
        commonElements.showSnackBar(code);
    }

    private void initializeBroadcastReceivers() {
        connectionStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                connectionStatus = intent.getExtras().getInt(getResources().getString(R.string.connectivity_status));
                if (connectionStatus != AppConfig.NO_CONNECTION) {
                    if (snackBar.isShown()) snackBar.dismiss();
                }
            }
        };
    }

    private void setSignInHereSpan(){ commonElements.setLoginActivitySpan(signInHereTtv, getResources().getString(R.string.signInHere), 22, 34, 1); }

    private void setRegisterSpan() { commonElements.setLoginActivitySpan(registerTtv, getResources().getString(R.string.register), 16, 23, 0); }

    private ResetPassowrdBody fillResetPasswordFields() {
        return new ResetPassowrdBody(getResources().getString(R.string.reset_password), emailEdt.getText().toString(), registrationToken);
    }
}
