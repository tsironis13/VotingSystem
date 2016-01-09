package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.rey.material.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;
import com.votingsystem.tsiro.ObserverPattern.ConnectivityObserver;
import com.votingsystem.tsiro.POJO.Email;
import com.votingsystem.tsiro.POJO.Firm;
import com.votingsystem.tsiro.POJO.RegisterUser;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.rest.ApiService;
import com.votingsystem.tsiro.votingsystem.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by user on 11/12/2015.
 */
public class RegisterFragment extends Fragment {

    private static final String debugTag = RegisterFragment.class.getSimpleName();
    private static String error_no_connection, empty_fields;
    private LinearLayout registerBaseLlt;
    private TextView signInHereTtv, errorresponseTtv, showHidePasswordTtv;
    private EditText usernameEdt, passwordEdt, confirmPasswordEdt, emailEdt, firmCodeEdt;
    private Button submitBtn;
    private ProgressView progressView;
    private Spinner registerSpnr;
    private View view;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private ConnectivityObserver connectivityObserver;
    private ApiService apiService;
    private SparseIntArray emailCodes;
    private List<String> firmNamesList;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_register, container, false);
        registerBaseLlt         =   (LinearLayout) view.findViewById(R.id.registerBaseLlt);
        usernameEdt             =   (EditText) view.findViewById(R.id.usernameEdt);
        passwordEdt             =   (EditText) view.findViewById(R.id.passwordEdt);
        confirmPasswordEdt      =   (EditText) view.findViewById(R.id.confirmPasswordEdt);
        emailEdt                =   (EditText) view.findViewById(R.id.emailEdt);
        //firmCodeEdt             =   (EditText) view.findViewById(R.id.firmCodeEdt);
        signInHereTtv           =   (TextView) view.findViewById(R.id.signInHereTtv);
        errorresponseTtv        =   (TextView) view.findViewById(R.id.errorresponseTtv);
        showHidePasswordTtv     =   (TextView) view.findViewById(R.id.showHidePasswordTtv);
        submitBtn               =   (Button) view.findViewById(R.id.submitBtn);
        progressView            =   (ProgressView) view.findViewById(R.id.progressView);
        registerSpnr            =   (Spinner) view.findViewById(R.id.registerSpnr);
        connectivityObserver    =   getArguments().getParcelable("connectivityObserver");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firmNamesList = new ArrayList<String>();
        apiService = RetrofitSingleton.getInstance().getApiService();
        Call<Firm> call = apiService.getFirmNames(getResources().getString(R.string.get_firm_names));
        call.enqueue(new Callback<Firm>() {
            @Override
            public void onResponse(Response<Firm> response, Retrofit retrofit) {
                List<Firm.FirmElement> firmElementList = response.body().getFirm_element();
                for ( int i = 0; i < firmElementList.size(); i++ ) { firmNamesList.add(firmElementList.get(i).getFirm_name()); }
                spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, firmNamesList);
                registerSpnr.setAdapter(spinnerAdapter);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
        submitBtn.setTransformationMethod(null);
        setSignInHereSpan();
        usernameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        passwordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(errorresponseTtv.getText())) errorresponseTtv.setText("");
                if (start >= 0) {
                    if (passwordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                        showHidePasswordTtv.setText(getResources().getString(R.string.hidePassword));
                    } else if (passwordEdt.getTransformationMethod() instanceof PasswordTransformationMethod) {
                        showHidePasswordTtv.setText(getResources().getString(R.string.showPassword));
                    }
                }
                if (start == 0 && before == 1) {
                    confirmPasswordEdt.setHelper("");
                    showHidePasswordTtv.setText(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        showHidePasswordTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordEdt.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    //use HideReturnsTransformationMethod to make password visible
                    passwordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showHidePasswordTtv.setText(getResources().getString(R.string.hidePassword));
                } else if (passwordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                    passwordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showHidePasswordTtv.setText(getResources().getString(R.string.showPassword));
                }
                passwordEdt.setSelection(passwordEdt.getText().length());
            }
        });
        confirmPasswordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(debugTag, "start: "+start+" before: "+before);
                if ( start >= 0 ) setConfirmPasswordEdtErrorMsg();
                if ( start == 0 && before == 1 ) confirmPasswordEdt.setHelper("");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        confirmPasswordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && passwordEdt.getText().length() != 0) {
                    setConfirmPasswordEdtErrorMsg();
                } else if (hasFocus && passwordEdt.getText().length() != 0 && confirmPasswordEdt.getText().length() != 0) {
                    setConfirmPasswordEdtErrorMsg();
                }
            }
        });
        emailEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ( !hasFocus ) {
                    if ( connectivityObserver.getConnectivityStatus(getActivity()) != AppConfig.NO_CONNECTION ) {
                        progressView.start();
                        Call<Email> call = apiService.isEmailValid(emailEdt.getText().toString());
                        call.enqueue(new Callback<Email>() {
                            @Override
                            public void onResponse(Response<Email> response, Retrofit retrofit) {
                                emailCodes = AppConfig.getEmailCodes();
                                int resource_message = emailCodes.get(response.body().getError_code());
                                if ( isAdded() ) {
                                    if (response.body().getError_code() != AppConfig.EMAIL_OK) {
                                        setHelperText(emailEdt, getResources().getString(resource_message), "red");
                                    } else {
                                        setHelperText(emailEdt, getResources().getString(resource_message), "green");
                                    }
                                    progressView.stop();
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                if ( isAdded() ) {
                                    if ( t instanceof IOException ) {
                                        setToastHelperMsg(getResources().getString(R.string.no_connection));
                                    } else {
                                        setToastHelperMsg(getResources().getString(R.string.error_occured));
                                    }
                                    emailEdt.setText(null);
                                    progressView.stop();
                                }
                            }
                        });
                    } else {
                        if ( isAdded() ) setToastHelperMsg(getResources().getString(R.string.no_connection));
                    }
                }
            }
        });
        signInHereTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof TextView) commonElements.signInHereOnClick();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( connectivityObserver.getConnectivityStatus(getActivity()) != AppConfig.NO_CONNECTION ) {
                    empty_fields = commonElements.encodeUtf8(getResources().getString(R.string.empty_fields));
                    if ( !commonElements.validateEditText(new EditText[]{usernameEdt, passwordEdt, confirmPasswordEdt, emailEdt, firmCodeEdt}) ) {
                        errorresponseTtv.setText(commonElements.decodeUtf8(empty_fields));
                    } else {
                        checkRegister(usernameEdt.getText().toString(), passwordEdt.getText().toString(), confirmPasswordEdt.getText().toString(), emailEdt.getText().toString(), firmCodeEdt.getText().toString());
                    }
                } else {
                    error_no_connection = commonElements.encodeUtf8(getResources().getString(R.string.no_connection));
                    Toast.makeText(getActivity(), commonElements.decodeUtf8(error_no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkRegister(String username, String password, String confirm_password, String email, String firm_code) {
        apiService = RetrofitSingleton.getInstance().getApiService();
        Call<RegisterUser> call = apiService.registerUser(username, password, confirm_password, email, "Arx.net", firm_code);
        call.enqueue(new Callback<RegisterUser>() {
            @Override
            public void onResponse(Response<RegisterUser> response, Retrofit retrofit) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void setSignInHereSpan(){ commonElements.setLoginActivitySpan(signInHereTtv, getResources().getString(R.string.signInHere), 22, 34, 1); }

    private void setConfirmPasswordEdtErrorMsg() {
        if ( !confirmPasswordEdt.getText().toString().equals(passwordEdt.getText().toString()) ) setHelperText(confirmPasswordEdt, "does not match", "red");
        if ( confirmPasswordEdt.getText().toString().equals(passwordEdt.getText().toString()) ) setHelperText(confirmPasswordEdt, "matched", "green");
    }

    private void setHelperText(EditText editText, String message, String color) { editText.setHelper(Html.fromHtml("<font color=" + color + ">" + message + "</font>")); }
    private void setToastHelperMsg(String message) { Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show(); }
}
