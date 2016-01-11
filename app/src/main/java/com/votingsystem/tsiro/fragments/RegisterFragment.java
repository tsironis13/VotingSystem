package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.votingsystem.tsiro.POJO.Firm;
import com.votingsystem.tsiro.POJO.UserConnectionStaff;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.rest.ApiService;
import com.votingsystem.tsiro.votingsystem.R;
import java.io.IOException;
import java.util.ArrayList;
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
    private EditText registerUsernameEdt, registerPasswordEdt, confirmPasswordEdt, emailEdt, firmCodeEdt;
    private Button submitBtn;
    private ProgressView usernameProgressView, emailProgressView;
    private Spinner registerSpnr;
    private View view;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private ConnectivityObserver connectivityObserver;
    private ApiService apiService;
    private SparseIntArray inputValidationCodes;
    private List<FirmNameWithID> firmNamesList;
    private ArrayAdapter<FirmNameWithID> spinnerAdapter;
    private FirmNameWithID spinnerState;
    private BroadcastReceiver broadcastReceiver;
    private static int connectivityStatus;
    private static boolean firmsLoaded;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_register, container, false);
        registerBaseLlt         =   (LinearLayout) view.findViewById(R.id.registerBaseLlt);
        registerUsernameEdt     =   (EditText) view.findViewById(R.id.registerUsernameEdt);
        registerPasswordEdt     =   (EditText) view.findViewById(R.id.registerPasswordEdt);
        confirmPasswordEdt      =   (EditText) view.findViewById(R.id.confirmPasswordEdt);
        emailEdt                =   (EditText) view.findViewById(R.id.emailEdt);
        firmCodeEdt             =   (EditText) view.findViewById(R.id.firmCodeEdt);
        signInHereTtv           =   (TextView) view.findViewById(R.id.signInHereTtv);
        errorresponseTtv        =   (TextView) view.findViewById(R.id.errorresponseTtv);
        showHidePasswordTtv     =   (TextView) view.findViewById(R.id.showHidePasswordTtv);
        submitBtn               =   (Button) view.findViewById(R.id.submitBtn);
        usernameProgressView    =   (ProgressView) view.findViewById(R.id.usernameProgressView);
        emailProgressView       =   (ProgressView) view.findViewById(R.id.emailProgressView);
        registerSpnr            =   (Spinner) view.findViewById(R.id.registerSpnr);
        connectivityObserver    =   getArguments().getParcelable("connectivityObserver");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputValidationCodes = AppConfig.getEmailCodes();
        apiService = RetrofitSingleton.getInstance().getApiService();
        if ( connectivityObserver.getConnectivityStatus(getActivity()) != AppConfig.NO_CONNECTION ) {
            getFirmNames();
        } else {
            firmsLoaded = false;
        }
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                connectivityStatus = intent.getExtras().getInt("connectivityStatus");
                if ( connectivityStatus != AppConfig.NO_CONNECTION && !firmsLoaded ) {
                    getFirmNames();
                }
            }
        };
        firmNamesList = new ArrayList<FirmNameWithID>();
        submitBtn.setTransformationMethod(null);
        setSignInHereSpan();
        registerUsernameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0 && before == 1) registerUsernameEdt.setHelper("");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        registerUsernameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (connectivityObserver.getConnectivityStatus(getActivity()) != AppConfig.NO_CONNECTION) {
                        usernameProgressView.start();
                        Call<UserConnectionStaff> call = apiService.isUsernameValid(getResources().getString(R.string.usernameValidation), registerUsernameEdt.getText().toString());
                        call.enqueue(new Callback<UserConnectionStaff>() {
                            @Override
                            public void onResponse(Response<UserConnectionStaff> response, Retrofit retrofit) {
                                int resource_message = inputValidationCodes.get(response.body().getError_code());
                                if (isAdded()) {
                                    if (response.body().getError_code() != AppConfig.INPUT_OK) {
                                        if (response.body().getError_code() == AppConfig.ERROR_INVALID_INPUT) {
                                            setHelperText(registerUsernameEdt, commonElements.encodeUtf8(getResources().getString(R.string.invalid_username)), "red");
                                        } else {
                                            setHelperText(registerUsernameEdt, commonElements.encodeUtf8(getResources().getString(resource_message)), "red");
                                        }
                                    } else {
                                        setHelperText(registerUsernameEdt, commonElements.encodeUtf8(getResources().getString(resource_message)), "green");
                                    }
                                    usernameProgressView.stop();
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                if (isAdded()) {
                                    if (t instanceof IOException) {
                                        setToastHelperMsg(getResources().getString(R.string.no_connection));
                                    } else {
                                        setToastHelperMsg(getResources().getString(R.string.error_occured));
                                    }
                                    registerUsernameEdt.setText(null);
                                    usernameProgressView.stop();
                                }
                            }
                        });
                    } else {
                        if (isAdded())
                            setToastHelperMsg(getResources().getString(R.string.no_connection));
                    }
                }
            }
        });
        registerPasswordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showHidePasswordTtv.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(errorresponseTtv.getText())) errorresponseTtv.setText("");
                if ( start >= 0 && !registerPasswordEdt.getText().toString().isEmpty() ) {
                    if (registerPasswordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                        showHidePasswordTtv.setText(getResources().getString(R.string.hidePassword));
                    } else if (registerPasswordEdt.getTransformationMethod() instanceof PasswordTransformationMethod) {
                        showHidePasswordTtv.setText(getResources().getString(R.string.showPassword));
                    }
                }
                if ( start == 0 && before == 1 ) {
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
                if ( registerPasswordEdt.getText().toString().isEmpty() ) {
                    showHidePasswordTtv.setVisibility(View.INVISIBLE);
                } else {
                    if ( registerPasswordEdt.getTransformationMethod() instanceof PasswordTransformationMethod ) {
                        //use HideReturnsTransformationMethod to make password visible
                        registerPasswordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        showHidePasswordTtv.setText(getResources().getString(R.string.hidePassword));
                    } else if ( registerPasswordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod ) {
                        registerPasswordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        showHidePasswordTtv.setText(getResources().getString(R.string.showPassword));
                    }
                    registerPasswordEdt.setSelection(registerPasswordEdt.getText().length());
                }
            }
        });
        confirmPasswordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start >= 0 && !registerPasswordEdt.getText().toString().isEmpty()) setConfirmPasswordEdtErrorMsg();
                if (start == 0 && before == 1) confirmPasswordEdt.setHelper("");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        confirmPasswordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ( !hasFocus && registerPasswordEdt.getText().length() < 9 ) {
                    setConfirmPasswordEdtErrorMsg();
                } else if ( hasFocus && !registerPasswordEdt.getText().toString().isEmpty() && !confirmPasswordEdt.getText().toString().isEmpty() ) {
                    setConfirmPasswordEdtErrorMsg();
                } else if ( hasFocus && registerPasswordEdt.getText().length() > 8 ) {
                    setConfirmPasswordEdtErrorMsg();
                }
            }
        });
        emailEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ( !hasFocus ) {
                    if ( connectivityObserver.getConnectivityStatus(getActivity()) != AppConfig.NO_CONNECTION ) {
                        emailProgressView.start();
                        Call<UserConnectionStaff> call = apiService.isEmailValid(getResources().getString(R.string.emailValidation), emailEdt.getText().toString());
                        call.enqueue(new Callback<UserConnectionStaff>() {
                            @Override
                            public void onResponse(Response<UserConnectionStaff> response, Retrofit retrofit) {
                                int resource_message = inputValidationCodes.get(response.body().getError_code());
                                if ( isAdded() ) {
                                    if (response.body().getError_code() != AppConfig.INPUT_OK) {
                                        setHelperText(emailEdt, commonElements.encodeUtf8(getResources().getString(resource_message)), "red");
                                    } else {
                                        setHelperText(emailEdt, commonElements.encodeUtf8(getResources().getString(resource_message)), "green");
                                    }
                                    emailProgressView.stop();
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
                                    emailProgressView.stop();
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
                    spinnerState = (FirmNameWithID) registerSpnr.getSelectedItem();
                    Log.d(debugTag, spinnerState.getId() + "");
                    empty_fields = commonElements.encodeUtf8(getResources().getString(R.string.empty_fields));
                    if ( !commonElements.validateEditText(new EditText[]{registerUsernameEdt, registerPasswordEdt, confirmPasswordEdt, emailEdt, firmCodeEdt}) ) {
                        errorresponseTtv.setText(commonElements.decodeUtf8(empty_fields));
                    } else {
                        checkRegister(registerUsernameEdt.getText().toString(), registerPasswordEdt.getText().toString(), confirmPasswordEdt.getText().toString(), emailEdt.getText().toString(), firmCodeEdt.getText().toString());
                    }
                } else {
                    error_no_connection = commonElements.encodeUtf8(getResources().getString(R.string.no_connection));
                    Toast.makeText(getActivity(), commonElements.decodeUtf8(error_no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("networkStateUpdated"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private void getFirmNames() {
        Call<Firm> call = apiService.getFirmNames(getResources().getString(R.string.get_firm_names));
        call.enqueue(new Callback<Firm>() {
            @Override
            public void onResponse(Response<Firm> response, Retrofit retrofit) {
                List<Firm.FirmElement> firmElementList = response.body().getFirm_element();
                for ( int i = 0; i < firmElementList.size(); i++ ) {
                    Log.d(debugTag, "firm_id: "+firmElementList.get(i).getFirm_id()+" firm_name: "+firmElementList.get(i).getFirm_name());
                    firmNamesList.add(new FirmNameWithID(firmElementList.get(i).getFirm_name(), firmElementList.get(i).getFirm_id()));
                }
                spinnerAdapter = new ArrayAdapter<FirmNameWithID>(getActivity(), android.R.layout.simple_spinner_item, firmNamesList);
                registerSpnr.setAdapter(spinnerAdapter);
                firmsLoaded = true;
            }
            @Override
            public void onFailure(Throwable t) {}
        });
    }

    private void checkRegister(String username, String password, String confirm_password, String email, String firm_code) {
        Call<UserConnectionStaff> call = apiService.registerUser(username, password, confirm_password, email, "Arx.net", firm_code);
        call.enqueue(new Callback<UserConnectionStaff>() {
            @Override
            public void onResponse(Response<UserConnectionStaff> response, Retrofit retrofit) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void setSignInHereSpan(){ commonElements.setLoginActivitySpan(signInHereTtv, getResources().getString(R.string.signInHere), 22, 34, 1); }

    private void setConfirmPasswordEdtErrorMsg() {
        if ( registerPasswordEdt.getText().length() < 9 && !confirmPasswordEdt.getText().toString().equals(registerPasswordEdt.getText().toString()) ) {
            setHelperText(confirmPasswordEdt, commonElements.encodeUtf8(getResources().getString(R.string.passwords_mismatch)), "red");
        } else if ( confirmPasswordEdt.getText().toString().equals(registerPasswordEdt.getText().toString()) ) {
            setHelperText(confirmPasswordEdt, "", "");
        } else if ( registerPasswordEdt.getText().length() > 8 ) {
            setHelperText(confirmPasswordEdt, commonElements.encodeUtf8(getResources().getString(R.string.invalid_password_length)), "red");
        }
    }

    private void setHelperText(EditText editText, String message, String color) { editText.setHelper(Html.fromHtml("<font color=" + color + ">" + commonElements.decodeUtf8(message) + "</font>")); }
    private void setToastHelperMsg(String message) { Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show(); }
}
