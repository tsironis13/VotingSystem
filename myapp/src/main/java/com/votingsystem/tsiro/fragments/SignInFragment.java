package com.votingsystem.tsiro.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;

import com.androidadvance.topsnackbar.TSnackbar;
import com.rey.material.widget.EditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.TextView;
import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.LoginActivityMVC.LAMVCPresenterImpl;
import com.votingsystem.tsiro.LoginActivityMVC.LAMVCView;
import com.votingsystem.tsiro.POJO.LoginFormBody;
import com.votingsystem.tsiro.POJO.ResetPassowrdBody;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.deserializer.FirmsDeserializer;
import com.votingsystem.tsiro.POJO.Firm;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.mainClasses.AdminBaseActivity;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.rest.ApiService;
import com.votingsystem.tsiro.votingsystem.R;
import java.util.List;

/**
 * Created by user on 10/10/2015.
 */
public class SignInFragment extends Fragment implements LAMVCView{

    private static final String debugTag = SignInFragment.class.getSimpleName();
    private TextView forgotPasswordTtV, registerTtv;
    private EditText usernameEdt, passwordEdt;
    private Button signInBtn;
    private SnackBar snackBar;
    private ProgressView progressView;
    private SharedPreferences sessionPrefs;
    private View view;
    private int connectionStatus, initialConnectionStatus;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private BroadcastReceiver connectionStatusReceiver;
    private LAMVCPresenterImpl LAMVCpresenterImpl;
    private String registrationToken;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_signin, container, false);
        usernameEdt             =   (EditText) view.findViewById(R.id.usernameEdt);
        passwordEdt             =   (EditText) view.findViewById(R.id.passwordEdt);
        progressView            =   (ProgressView) view.findViewById(R.id.progressView);
        signInBtn               =   (Button) view.findViewById(R.id.signInBtn);
        forgotPasswordTtV       =   (TextView) view.findViewById(R.id.forgotPasswordTtv);
        registerTtv             =   (TextView) view.findViewById(R.id.registerTtv);
        snackBar                =   ((LoginActivity)getActivity()).getSnackBar();
        initialConnectionStatus =   getArguments().getInt(getResources().getString(R.string.connectivity_status));
        registrationToken       =   getArguments().getString(getResources().getString(R.string.registration_token));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (snackBar.isShown()) snackBar.dismiss();
        LAMVCpresenterImpl      =   new LAMVCPresenterImpl(this);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        connectionStatus = initialConnectionStatus;
        initializeBroadcastReceivers();

        sessionPrefs = LoginActivity.getSessionPrefs(getActivity());
        boolean key = sessionPrefs.contains("17");
        int value = sessionPrefs.getInt("17", 0);
        //Toast.makeText(getContext(), "key, value" + key + " " + value, Toast.LENGTH_SHORT).show();
        if ( sessionPrefs.contains("user_id") ) startBaseActivity();
        signInBtn.setTransformationMethod(null);
        setRegisterSpan();
        passwordEdt.setOnEditorActionListener(new android.widget.TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(android.widget.TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) submitForm();
                return false;
            }
        });
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
        forgotPasswordTtV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof TextView && commonElements != null) commonElements.forgotPasswordOnClick();
            }
        });
        registerTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof TextView && commonElements != null) commonElements.registerOnClick();
            }
        });
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(connectionStatusReceiver, new IntentFilter(getResources().getString(R.string.network_state_update)));
    }

    private void setRegisterSpan() {
        commonElements.setLoginActivitySpan(registerTtv, getResources().getString(R.string.register), 16, 23, 0);
    }

    private void startBaseActivity() {
        Intent intent = new Intent(getActivity(), AdminBaseActivity.class);
        String user_id, user_email, username;
        if ( sessionPrefs.getInt("user_id", -1) != -1 ) {
            Bundle sessionBundle = new Bundle();
            sessionBundle.putInt("user_id", sessionPrefs.getInt("user_id", -1));
            intent.putExtras(sessionBundle);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(debugTag, "view destroyed");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(connectionStatusReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.commonElements = null;
        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        if (enter) return super.onCreateAnimation(transit, enter, nextAnim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                commonElements.animationOccured(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                commonElements.animationOccured(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);

        return animationSet;
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

    @Override
    public void handlePasswordTextChanges(TextView showHidePasswordTtv, int action) {}

    @Override
    public void changeTransformationMethod(TransformationMethod transformationMethod, int action) {}

    @Override
    public void displayFeedbackMsg(int code) {
        commonElements.dismissErrorContainerSnackBar();
        if (progressView != null && progressView.isShown()) progressView.stop();
        showSnackBar(code);
    }

    @Override
    public void onSuccessfulFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList, boolean firmsLoaded) {}

    @Override
    public void onFailureFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList, boolean firmsLoaded) {}

    @Override
    public void onSuccess() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), AdminBaseActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onFailure(int code, String field, String hint, String retry_in) {
        if (code == AppConfig.ERROR_INVALID_USERNAME_PASSWORD) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.error_invalid_username_password), null, code);
        } else if (code == AppConfig.ERROR_ACCOUNT_NOT_VERIFIED_YET) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.error_account_not_verified_yet), null, code);
        }
        if (progressView != null && progressView.isShown()) progressView.stop();
    }

    private void submitForm() {
        if (connectionStatus == AppConfig.NO_CONNECTION) {
            showSnackBar(AppConfig.NO_CONNECTION);
        } else {
            progressView.start();
            LAMVCpresenterImpl.loginUser(isAdded(), fillLoginUserFields());
        }
    }

    private void showSnackBar(int code) {
        if (progressView != null && progressView.isShown()) progressView.stop();
        commonElements.dismissErrorContainerSnackBar();
        commonElements.showSnackBar(code);
    }

    private LoginFormBody fillLoginUserFields() {
        return new LoginFormBody(getResources().getString(R.string.login_user), usernameEdt.getText().toString(), passwordEdt.getText().toString());
    }
}
