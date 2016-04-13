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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;

import com.androidadvance.topsnackbar.TSnackbar;
import com.rey.material.widget.EditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.TextView;
import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.deserializer.FirmsDeserializer;
import com.votingsystem.tsiro.POJO.Firm;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.mainClasses.AdminBaseActivity;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.rest.ApiService;
import com.votingsystem.tsiro.votingsystem.R;
import java.util.List;

/**
 * Created by user on 10/10/2015.
 */
public class SignInFragment extends Fragment {

    private static final String debugTag = SignInFragment.class.getSimpleName();
    private static String error_no_connection;
    private TextView forgotPasswordTtV, registerTtv;
    private EditText signInUsernameEdt, signInPasswordEdt;
    private Button signInBtn;
    private SnackBar snackBar;
    private LayoutInflater inflater;
    private SharedPreferences sessionPrefs;
    private View view;
    private int connectionStatus, initialConnectionStatus;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver connectionStatusReceiver;
    private String registrationToken;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_signin, container, false);
        signInUsernameEdt       =   (EditText) view.findViewById(R.id.usernameEdt);
        signInPasswordEdt       =   (EditText) view.findViewById(R.id.passwordEdt);
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

        connectionStatus = initialConnectionStatus;
        initializeBroadcastReceivers();

        sessionPrefs = LoginActivity.getSessionPrefs(getActivity());
        boolean key = sessionPrefs.contains("17");
        int value = sessionPrefs.getInt("17", 0);
        //Toast.makeText(getContext(), "key, value" + key + " " + value, Toast.LENGTH_SHORT).show();
        if ( sessionPrefs.contains("user_id") ) startBaseActivity();
        signInBtn.setTransformationMethod(null);
        setRegisterSpan();
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e(debugTag, "CONNECTION: " + LoginActivity.connectionStatusUpdated);
                //Log.d(debugTag, "CONNECTIVITY STATUS: " + connectivityObserver.getConnectivityStatus(getActivity()));
                if (signInUsernameEdt.getText().toString().isEmpty() || signInPasswordEdt.getText().toString().isEmpty()) {
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), AdminBaseActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

    private void checkLogin(String username, String password){

        ApiService apiService = RetrofitSingleton.getInstance().getApiService();
        Log.d(debugTag, "ApiService Singleton: " + apiService.toString());
        /*Call<User> call = apiService.validateUser(username, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(retrofit.Response<User> response, Retrofit retrofit) {
                if (response.body().getError()) {
                    errorresponseTtv.setText(response.body().getError_msg());
                } else {
                    SharedPreferences.Editor sessionPrefsEditor = sessionPrefs.edit();
                    sessionPrefsEditor.putInt("user_id", response.body().getId());
                    sessionPrefsEditor.putString("user_email", response.body().getEmail());
                    sessionPrefsEditor.putString("username", response.body().getUsername());
                    sessionPrefsEditor.apply();

                    Log.d(debugTag, "user id: " + response.body().getId() + "email: " + response.body().getEmail() + " username: " + response.body().getUsername());
                    inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View popup = inflater.inflate(R.layout.popup_login, null);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });*/
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
}
