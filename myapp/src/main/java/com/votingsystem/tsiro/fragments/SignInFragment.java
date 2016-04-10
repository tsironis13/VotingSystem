package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class SignInFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String debugTag = SignInFragment.class.getSimpleName();
    private static String error_no_connection, empty_fields;
    private TextView forgotPasswordTtV, registerTtv, errorresponseTtv, showHidePasswordTtv;
    private EditText signInUsernameEdt, signInPasswordEdt;
    private Button signInBtn;
    private SnackBar snackBar;
    private LayoutInflater inflater;
    private SharedPreferences sessionPrefs;
    private View view;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private BroadcastReceiver broadcastReceiver;
    TSnackbar tSnackbar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_signin, container, false);
        signInUsernameEdt       =   (EditText) view.findViewById(R.id. signInUsernameEdt);
        signInPasswordEdt       =   (EditText) view.findViewById(R.id.signInPasswordEdt);
        signInBtn               =   (Button) view.findViewById(R.id.signInBtn);
        forgotPasswordTtV       =   (TextView) view.findViewById(R.id.forgotPasswordTtv);
        registerTtv             =   (TextView) view.findViewById(R.id.registerTtv);
        errorresponseTtv        =   (TextView) view.findViewById(R.id.errorresponseTtv);
        showHidePasswordTtv     =   (TextView) view.findViewById(R.id.showHidePasswordTtv);
        snackBar                =   ((LoginActivity) getActivity()).getSnackBar();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.d(debugTag, "here");
            }
        };
        if (snackBar.isShown()) snackBar.dismiss();

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
                    empty_fields = commonElements.encodeUtf8(getResources().getString(R.string.empty_field));
                    errorresponseTtv.setText(commonElements.decodeUtf8(empty_fields));
                } else {
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    checkLogin(signInUsernameEdt.getText().toString(), signInPasswordEdt.getText().toString());
                }
            }
        });

        signInUsernameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorresponseTtv.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        signInPasswordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(errorresponseTtv.getText())) errorresponseTtv.setText("");
                if (start >= 0) {
                    if (signInPasswordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                        showHidePasswordTtv.setText(getResources().getString(R.string.hidePassword));
                    } else if (signInPasswordEdt.getTransformationMethod() instanceof PasswordTransformationMethod) {
                        showHidePasswordTtv.setText(getResources().getString(R.string.showPassword));
                    }
                }
                if (start == 0 && before == 1) showHidePasswordTtv.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        showHidePasswordTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( signInPasswordEdt.getTransformationMethod() instanceof PasswordTransformationMethod ) {
                    //use HideReturnsTransformationMethod to make password visible
                    signInPasswordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showHidePasswordTtv.setText(getResources().getString(R.string.hidePassword));
                } else if ( signInPasswordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod ){
                    signInPasswordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showHidePasswordTtv.setText(getResources().getString(R.string.showPassword));
                }
                signInPasswordEdt.setSelection(signInPasswordEdt.getText().length());
            }
        });
        forgotPasswordTtV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( v instanceof TextView ) commonElements.forgotPasswordOnClick();
            }
        });
        registerTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( v instanceof TextView ) commonElements.registerOnClick();
            }
        });
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private static Gson getGsonBuilder(){
        Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Firm.class, new FirmsDeserializer())
                        .create();
        return gson;
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

    private interface FirmNamesRequestCallback{
        void getFirmNames(List<Firm.FirmElement> firmNames);
    }

    @Override
    public void onResume() {
        super.onResume();
        //TSnackbar tSnackbar = ((LoginActivity) getActivity()).getKalase();
        //if (tSnackbar != null && tSnackbar.isShown()) tSnackbar.dismiss();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("networkStateUpdated"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.commonElements = null;
        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
