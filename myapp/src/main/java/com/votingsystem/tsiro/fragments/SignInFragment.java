package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.LinearLayout;

import com.rey.material.widget.EditText;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.TextView;
//import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.LoginActivityMVC.LAMVCPresenterImpl;
import com.votingsystem.tsiro.LoginActivityMVC.LAMVCView;
import com.votingsystem.tsiro.POJO.LoginFormBody;
import com.votingsystem.tsiro.animation.AnimationListener;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.mainClasses.DashboardActivity;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.votingsystem.R;
import java.util.List;

/**
 * Created by user on 10/10/2015.
 */
public class SignInFragment extends Fragment implements LAMVCView{

    private static final String debugTag = SignInFragment.class.getSimpleName();
    private LinearLayout toSharedLogo, bottomView, middleView;
    private TextView forgotPasswordTtv, registerTtv;
    private EditText usernameEdt, passwordEdt;
    private Button signInBtn;
    private Snackbar snackBar;
    private ProgressView progressView;
    private SharedPreferences sessionPrefs;
    private View view, divider;
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
        toSharedLogo            =   (LinearLayout) view.findViewById(R.id.toSharedLogo);
        bottomView              =   (LinearLayout) view.findViewById(R.id.bottomView);
        middleView              =   (LinearLayout) view.findViewById(R.id.middleView);
        usernameEdt             =   (EditText) view.findViewById(R.id.usernameEdt);
        passwordEdt             =   (EditText) view.findViewById(R.id.passwordEdt);
        progressView            =   (ProgressView) view.findViewById(R.id.progressView);
        signInBtn               =   (Button) view.findViewById(R.id.signInBtn);
        forgotPasswordTtv       =   (TextView) view.findViewById(R.id.forgotPasswordTtv);
        registerTtv             =   (TextView) view.findViewById(R.id.registerTtv);
        snackBar                =   ((LoginActivity)getActivity()).getSnackBar();

        if (toSharedLogo.getVisibility() == View.GONE || bottomView.getVisibility() == View.GONE) {
            toSharedLogo.setVisibility(View.VISIBLE);
            bottomView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) middleView.getLayoutParams();
            lParams.weight = 62f;
            middleView.setLayoutParams(lParams);
        }
        if (getArguments() != null) {
            initialConnectionStatus = getArguments().getInt(getResources().getString(R.string.connectivity_status));
            Log.e(debugTag, "initialConnectionStatus: "+initialConnectionStatus);
            registrationToken       = getArguments().getString(getResources().getString(R.string.registration_token));
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) toSharedLogo.setTransitionName(getResources().getString(R.string.toshared_logo_trns));

        if (snackBar != null && snackBar.isShown()) snackBar.dismiss();
        LAMVCpresenterImpl  =   new LAMVCPresenterImpl(this);

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        connectionStatus = initialConnectionStatus;
        if (isAdded()) initializeBroadcastReceivers();

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
        forgotPasswordTtv.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onResume() {
        super.onResume();
        if (!LoginActivity.settingsDialogWasOpened) {
            usernameEdt.setText("");
            passwordEdt.setText("");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(connectionStatusReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.LAMVCpresenterImpl !=null) LAMVCpresenterImpl.onDestroy();
        this.commonElements = null;
        //RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        //refWatcher.watch(this);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim != 0) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            if (enter) return super.onCreateAnimation(transit, true, nextAnim);
            if (commonElements != null) animation.setAnimationListener(new AnimationListener(commonElements, this, getResources().getString(R.string.signin_fgmt), ""));

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(animation);

            return animationSet;
        } else {
            return null;
        }
    }

    private void setRegisterSpan() {
        commonElements.setLoginActivitySpan(registerTtv, getResources().getString(R.string.register), 16, 23, 0);
    }

    private void startBaseActivity() {
        Intent intent = new Intent(getActivity(), DashboardActivity.class);
        String user_id, user_email, username;
        if ( sessionPrefs.getInt("user_id", -1) != -1 ) {
            Bundle sessionBundle = new Bundle();
            sessionBundle.putInt("user_id", sessionPrefs.getInt("user_id", -1));
            intent.putExtras(sessionBundle);
            startActivity(intent);
        }
    }

    private void initializeBroadcastReceivers() {
        connectionStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isAdded()) connectionStatus = intent.getExtras().getInt(getResources().getString(R.string.connectivity_status));
            }
        };
    }

    @Override
    public void handlePasswordTextChanges(TextView showHidePasswordTtv, int action) {}

    @Override
    public void changeTransformationMethod(TransformationMethod transformationMethod, int action) {}

    @Override
    public void displayFeedbackMsg(int code) {
        if (progressView != null && progressView.isShown()) progressView.stop();
        showSnackBar(code);
    }

    @Override
    public void onSuccessfulFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList, boolean firmsLoaded) {}

    @Override
    public void onFailureFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList, boolean firmsLoaded) {}

    @Override
    public void onSuccess() {}

    @Override
    public void onSuccessUserSignIn(int user_id, String username, String email, int firm_id) {
        getActivity().finish();
        initializeSessionId(user_id, username, email, firm_id);
        Intent intent = new Intent(getActivity(), DashboardActivity.class);
        intent.putExtra(getResources().getString(R.string.user_id), user_id);
        intent.putExtra(getResources().getString(R.string.username_tag), username);
        intent.putExtra(getResources().getString(R.string.email_tag), email);
        intent.putExtra(getResources().getString(R.string.firm_id), firm_id);
        startActivity(intent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onFailure(int code, String field, String hint, String retry_in) {
        if (code == AppConfig.ERROR_INVALID_USERNAME_PASSWORD) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.error_invalid_username_password), null, code);
        } else if (code == AppConfig.ERROR_ACCOUNT_NOT_VERIFIED_YET) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.error_account_not_verified_yet), null, code);
        } else {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.error_occured), null, code);
        }
        if (progressView != null && progressView.isShown()) progressView.stop();
    }

    private void submitForm() {
        Log.e(debugTag, connectionStatus+"");
        if (connectionStatus == AppConfig.NO_CONNECTION) {
            showSnackBar(AppConfig.NO_CONNECTION);
        } else {
            progressView.start();
            LAMVCpresenterImpl.loginUser(isAdded(), fillLoginUserFields());
        }
    }

    private void showSnackBar(int code) {
        if (progressView != null && progressView.isShown()) progressView.stop();
        commonElements.showSnackBar(code);
    }

    private LoginFormBody fillLoginUserFields() {
        return new LoginFormBody(getResources().getString(R.string.login_user), usernameEdt.getText().toString(), passwordEdt.getText().toString());
    }

    private void initializeSessionId(int user_id, String username, String email, int firm_id) {
        SharedPreferences.Editor editor = LoginActivity.getSessionPrefs(getActivity()).edit();
        int id = LoginActivity.getSessionPrefs(getActivity()).getInt(getResources().getString(R.string.user_id), 0);
        Log.e(debugTag, id+"");
        if (id == 0) {
            editor.putInt(getResources().getString(R.string.user_id), user_id);
            editor.putString(getResources().getString(R.string.username_tag), username);
            editor.putString(getResources().getString(R.string.email_tag), email);
            editor.putInt(getResources().getString(R.string.firm_id), firm_id);
            editor.apply();
        }
    }
}
