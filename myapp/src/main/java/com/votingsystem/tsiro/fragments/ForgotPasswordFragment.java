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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.TextView;
import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.LoginActivityMVC.LAMVCPresenterImpl;
import com.votingsystem.tsiro.LoginActivityMVC.LAMVCView;
import com.votingsystem.tsiro.POJO.ResetPassowrdBody;
import com.votingsystem.tsiro.animation.AnimationListener;
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

        if (getArguments() != null) {
            initialConnectionStatus     =   getArguments().getInt(getResources().getString(R.string.connectivity_status));
            Log.e(debugTag, "initialConnectionStatus: "+initialConnectionStatus);
            registrationToken           =   getArguments().getString(getResources().getString(R.string.registration_token));
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (snackBar != null && snackBar.isShown()) snackBar.dismiss();
        LAMVCpresenterImpl      =   new LAMVCPresenterImpl(this);

        connectionStatus = initialConnectionStatus;
        if (isAdded() && isVisible()) initializeBroadcastReceivers();

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
        emailEdt.setOnEditorActionListener(new android.widget.TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(android.widget.TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) submitForm();
                return false;
            }
        });
        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
        signInHereTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof TextView && commonElements != null) commonElements.signInHereOnClick();
            }
        });
        registerTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( v instanceof TextView && commonElements != null) commonElements.registerOnClick();
            }
        });
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(connectionStatusReceiver, new IntentFilter(getResources().getString(R.string.network_state_update)));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!LoginActivity.settingsDialogWasOpened) emailEdt.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(connectionStatusReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LAMVCpresenterImpl.onDestroy();
        this.commonElements = null;
        //RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        //refWatcher.watch(this);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim != 0) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            if (enter) return super.onCreateAnimation(transit, true, nextAnim);
            if (commonElements != null) animation.setAnimationListener(new AnimationListener(commonElements, null, getResources().getString(R.string.forgotPasswordFgmt), ""));

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(animation);

            return animationSet;
        } else {
            return null;
        }
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
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) commonElements.signInHereOnClick();
    }

    @Override
    public void onFailure(int code, String field, String hint, String retry_in) {
        if (code == AppConfig.ERROR_EMPTY_REQUIRED_FIELD) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.empty_field, hint), null, code);
        } else if (code == AppConfig.ERROR_INVALID_EMAIL) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.correct_field, hint), emailEdt, code);
        } else if (code == AppConfig.ERROR_NOT_VERIFIED_YET) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.error_not_verified_yet), null, code);
        } else if (code == AppConfig.ERROR_EMAIL_DOESNT_EXIST) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.correct_field, hint), emailEdt, code);
        } else if (code == AppConfig.ERROR_RESET_PASWD_ALREADY_REQUESTED) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.error_reset_password_already_requested, retry_in), null, code);
        } else {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.error_occured), null, code);
        }
        if (progressView != null && progressView.isShown()) progressView.stop();
    }
    @Override
    public void displayFeedbackMsg(int code) {
        commonElements.dismissErrorContainerSnackBar();
        if (progressView != null && progressView.isShown()) progressView.stop();
        showSnackBar(code);
    }

    private void submitForm() {
        if (connectionStatus == AppConfig.NO_CONNECTION) {
            showSnackBar(AppConfig.NO_CONNECTION);
        } else {
            progressView.start();
            LAMVCpresenterImpl.resetPassword(isAdded(), fillResetPasswordFields());
        }
    }

    private void showSnackBar(int code) {
        if (progressView != null && progressView.isShown()) progressView.stop();
        commonElements.dismissErrorContainerSnackBar();
        commonElements.showSnackBar(code);
    }

    private void initializeBroadcastReceivers() {
        connectionStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isAdded()) {
                    connectionStatus = intent.getExtras().getInt(getResources().getString(R.string.connectivity_status));
                    if (connectionStatus != AppConfig.NO_CONNECTION) if (snackBar.isShown()) snackBar.dismiss();
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
