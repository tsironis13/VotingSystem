package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.rey.material.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.votingsystem.tsiro.interfaces.UpdateInputMapValue;
import com.votingsystem.tsiro.rest.ApiService;
import com.votingsystem.tsiro.votingsystem.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private RelativeLayout acceptUsernameRlt, acceptPasswordRlt, acceptEmailRlt;
    private TextView signInHereTtv, errorresponseTtv, showHidePasswordTtv, passwordErrorTtv;
    private EditText registerUsernameEdt, registerPasswordEdt, confirmPasswordEdt, registerEmailEdt, firmCodeEdt;
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
    private HashMap<String, Boolean> inputValidityMap;
    private static Handler mainThreadHandler;
    private UpdateInputMapValue updateInputMapInterface;
    private static long showhideAcceptPasswordAnimationTargetTimeinMillis;
    private Runnable showhideAcceptPasswordAnimationRunnable;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_register, container, false);
        registerBaseLlt         =   (LinearLayout) view.findViewById(R.id.registerBaseLlt);
        acceptUsernameRlt       =   (RelativeLayout) view.findViewById(R.id.acceptUsernameRlt);
        acceptPasswordRlt       =   (RelativeLayout) view.findViewById(R.id.acceptPasswordRlt);
        registerUsernameEdt     =   (EditText) view.findViewById(R.id.registerUsernameEdt);
        registerPasswordEdt     =   (EditText) view.findViewById(R.id.registerPasswordEdt);
        confirmPasswordEdt      =   (EditText) view.findViewById(R.id.confirmPasswordEdt);
        registerEmailEdt        =   (EditText) view.findViewById(R.id.registerEmailEdt);
        firmCodeEdt             =   (EditText) view.findViewById(R.id.firmCodeEdt);
        signInHereTtv           =   (TextView) view.findViewById(R.id.signInHereTtv);
        errorresponseTtv        =   (TextView) view.findViewById(R.id.errorresponseTtv);
        showHidePasswordTtv     =   (TextView) view.findViewById(R.id.showHidePasswordTtv);
        passwordErrorTtv        =   (TextView) view.findViewById(R.id.passwordErrorTtv);
        submitBtn               =   (Button) view.findViewById(R.id.submitBtn);
        usernameProgressView    =   (ProgressView) view.findViewById(R.id.usernameProgressView);
        emailProgressView       =   (ProgressView) view.findViewById(R.id.emailProgressView);
        registerSpnr            =   (Spinner) view.findViewById(R.id.registerSpnr);
        connectivityObserver    =   getArguments().getParcelable("connectivityObserver");
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ( savedInstanceState == null ) {
            fillValidityInputMap(getResources().getStringArray(R.array.input_fields_array));
            inputValidationCodes = AppConfig.getCodes();
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
                    if ( connectivityStatus != AppConfig.NO_CONNECTION && !firmsLoaded ) getFirmNames();
                }
            };
            firmNamesList = new ArrayList<FirmNameWithID>();
            submitBtn.setTransformationMethod(null);
            setSignInHereSpan();
            registerUsernameEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputValidityMap.put(getResources().getString(R.string.username_tag), false);
                    if ( acceptUsernameRlt.getVisibility() == View.VISIBLE ) animationStaff(acceptUsernameRlt, 1.0f, 0.0f, "gone");
                    if ( registerUsernameEdt.getHelper() != null ) clearEditextHelper(registerUsernameEdt);
                    if ( start == 0 && before == 1 ) registerUsernameEdt.setHelper(null);
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
            registerUsernameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if ( !hasFocus ) {
                        if ( !inputValidityMap.get(getResources().getString(R.string.username_tag)) ) {
                            if (registerUsernameEdt.getText().toString().isEmpty()) {
                                setText("error", registerUsernameEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))), "#DD2C00");
                            } else {
                                if (connectivityObserver.getConnectivityStatus(getActivity()) != AppConfig.NO_CONNECTION) {
                                    progressViewActions("start", usernameProgressView);
                                    Call<UserConnectionStaff> call = apiService.isUsernameValid(getResources().getString(R.string.usernameValidation), registerUsernameEdt.getText().toString());
                                    call.enqueue(new Callback<UserConnectionStaff>() {
                                        @Override
                                        public void onResponse(Response<UserConnectionStaff> response, Retrofit retrofit) {
                                            int resource_message = inputValidationCodes.get(response.body().getError_code());
                                            if (isAdded()) {
                                                if (response.body().getError_code() != AppConfig.INPUT_OK) {
                                                    if (response.body().getError_code() == AppConfig.ERROR_INVALID_INPUT) {
                                                        setText("error", registerUsernameEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(resource_message))), "#DD2C00");
                                                    } else {
                                                        setText("error", registerUsernameEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(resource_message))), "#DD2C00");
                                                    }
                                                } else {
                                                    animationStaff(acceptUsernameRlt, 0.0f, 1.0f, "visible");
                                                    inputValidityMap.put(getResources().getString(R.string.username_tag), true);
                                                }
                                                progressViewActions("stop", usernameProgressView);
                                            }
                                        }
                                        @Override
                                        public void onFailure(Throwable t) {
                                            if (isAdded()) {
                                                if (t instanceof IOException) {
                                                    setToastHelperMsg(getResources().getString(R.string.unavailable_service));
                                                } else {
                                                    setToastHelperMsg(getResources().getString(R.string.error_occured));
                                                }
                                                registerUsernameEdt.setText(null);
                                                progressViewActions("stop", usernameProgressView);
                                            }
                                        }
                                    });
                                } else {
                                    if (isAdded())
                                        setToastHelperMsg(getResources().getString(R.string.no_connection));
                                }
                            }
                        }
                    }
                }
            });
            registerPasswordEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if ( System.currentTimeMillis() < showhideAcceptPasswordAnimationTargetTimeinMillis ) handlerStaff().removeCallbacks(showhideAcceptPasswordAnimationRunnable);
                    inputValidityMap.put(getResources().getString(R.string.password_tag), false);
                    if ( !passwordErrorTtv.getText().toString().isEmpty() ) {
                        passwordErrorTtv.setText(null);
                        registerPasswordEdt.getChildAt(1).setVisibility(View.VISIBLE);
                    }
                    if ( acceptPasswordRlt.getVisibility() == View.VISIBLE ) animationStaff(acceptPasswordRlt, 1.0f, 0.0f, "gone");
                    if ( registerPasswordEdt.getHelper() != null ) clearEditextHelper(registerPasswordEdt);
                    showHidePasswordTtv.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(errorresponseTtv.getText()))
                        errorresponseTtv.setText("");
                    if (start >= 0 && !registerPasswordEdt.getText().toString().isEmpty()) {
                        if ( registerPasswordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod ) {
                            showHidePasswordTtv.setText(getResources().getString(R.string.hidePassword));
                        } else if ( registerPasswordEdt.getTransformationMethod() instanceof PasswordTransformationMethod ) {
                            showHidePasswordTtv.setText(getResources().getString(R.string.showPassword));
                        }
                    }
                    if (start == 0 && before == 1) showHidePasswordTtv.setText(null);
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
            registerPasswordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if ( !hasFocus ) {
                        if ( !inputValidityMap.get(getResources().getString(R.string.password_tag)) ) {
                            if (registerPasswordEdt.getText().toString().isEmpty()) {
                                passwordErrorTtv.setText(commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))));
                            } else if (registerPasswordEdt.getText().length() != 8) {
                                passwordErrorTtv.setText(commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.invalid_password_length))));
                                Runnable updateinputvalueRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        updateInputMapInterface.updatevalue(false);
                                    }
                                };
                                handlerStaff().post(updateinputvalueRunnable);
                            } else {
                                if (connectivityObserver.getConnectivityStatus(getActivity()) != AppConfig.NO_CONNECTION) {
                                    Call<UserConnectionStaff> call = apiService.isPasswordValid(getResources().getString(R.string.passwordValidation), registerPasswordEdt.getText().toString());
                                    call.enqueue(new Callback<UserConnectionStaff>() {
                                        @Override
                                        public void onResponse(Response<UserConnectionStaff> response, Retrofit retrofit) {
                                            int resource_message = inputValidationCodes.get(response.body().getError_code());
                                            if (isAdded()) {
                                                if (response.body().getError_code() != AppConfig.INPUT_OK) {
                                                    if (response.body().getError_code() == AppConfig.ERROR_INVALID_INPUT) {
                                                        passwordErrorTtv.setText(commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(resource_message))));
                                                    } else {
                                                        passwordErrorTtv.setText(commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(resource_message))));
                                                    }
                                                    updateInputMapInterface.updatevalue(false);
                                                } else {
                                                    animationStaff(acceptPasswordRlt, 0.0f, 1.0f, "visible");
                                                    animationStaff(showHidePasswordTtv, 1.0f, 0.0f, "gone");
                                                    updateInputMapInterface.updatevalue(true);
                                                    showhideAcceptPasswordAnimationRunnable = new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            animationStaff(showHidePasswordTtv, 0.0f, 1.0f, "visible");
                                                            animationStaff(acceptPasswordRlt, 1.0f, 0.0f, "gone");
                                                        }
                                                    };
                                                    handlerStaff().postDelayed(showhideAcceptPasswordAnimationRunnable, AppConfig.showhideAcceptDelay);
                                                    showhideAcceptPasswordAnimationTargetTimeinMillis = System.currentTimeMillis() + AppConfig.showhideAcceptDelay;
                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(Throwable t) {
                                            if (isAdded()) {
                                                if (t instanceof IOException) {
                                                    setToastHelperMsg(getResources().getString(R.string.unavailable_service));
                                                } else {
                                                    setToastHelperMsg(getResources().getString(R.string.error_occured));
                                                }
                                                registerUsernameEdt.setText(null);
                                                progressViewActions("stop", usernameProgressView);
                                            }
                                        }
                                    });
                                } else {
                                    if ( isAdded() ) setToastHelperMsg(getResources().getString(R.string.no_connection));
                                }
                            }
                        }
                        updateInputMapInterface = new UpdateInputMapValue() {
                            @Override
                            public void updatevalue(boolean value) {
                                inputValidityMap.put(getResources().getString(R.string.password_tag), value);
                                if ( !inputValidityMap.get(getResources().getString(R.string.password_tag)) ) registerPasswordEdt.getChildAt(1).setVisibility(View.GONE);
                            }
                        };
                    }
                }
            });
            showHidePasswordTtv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (registerPasswordEdt.getText().toString().isEmpty()) {
                        showHidePasswordTtv.setVisibility(View.INVISIBLE);
                    } else {
                        if (registerPasswordEdt.getTransformationMethod() instanceof PasswordTransformationMethod) {
                            //use HideReturnsTransformationMethod to make password visible
                            registerPasswordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            animationStaff(showHidePasswordTtv, 0.0f, 1.0f, "visible");
                            showHidePasswordTtv.setText(getResources().getString(R.string.hidePassword));
                        } else if (registerPasswordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                            registerPasswordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            animationStaff(showHidePasswordTtv, 0.0f, 1.0f, "visible");
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
                    Log.d(debugTag, inputValidityMap.get(getResources().getString(R.string.password_tag))+"");
                    if ( start >= 2 && inputValidityMap.get(getResources().getString(R.string.password_tag)) ) {
                      if ( !confirmPasswordEdt.getText().toString().equals(registerPasswordEdt.getText().toString()) ) {
                          setText("error", confirmPasswordEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.passwords_mismatch))), "#DD2C00");
                      } else {
                          if ( confirmPasswordEdt.getHelper() != null ) clearEditextHelper(confirmPasswordEdt);
                      }
                    }
                    if (start == 0 && before == 1) if ( confirmPasswordEdt.getHelper() != null ) clearEditextHelper(confirmPasswordEdt);
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
            confirmPasswordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if ( !hasFocus ) if ( confirmPasswordEdt.getText().toString().isEmpty() ) setText("error", confirmPasswordEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))), "#DD2C00");
                }
            });
            registerEmailEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if ( !hasFocus ) {
                        if ( !inputValidityMap.get(getResources().getString(R.string.email_tag)) ) {
                            if (registerPasswordEdt.getText().toString().isEmpty()) {
                                setText("error", registerEmailEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))), "#DD2C00");
                            } else {
                                if (connectivityObserver.getConnectivityStatus(getActivity()) != AppConfig.NO_CONNECTION) {
                                    progressViewActions("start", emailProgressView);
                                    Call<UserConnectionStaff> call = apiService.isEmailValid(getResources().getString(R.string.emailValidation), registerEmailEdt.getText().toString());
                                    call.enqueue(new Callback<UserConnectionStaff>() {
                                        @Override
                                        public void onResponse(Response<UserConnectionStaff> response, Retrofit retrofit) {
                                            int resource_message = inputValidationCodes.get(response.body().getError_code());
                                            if (isAdded()) {
                                                if (response.body().getError_code() != AppConfig.INPUT_OK) {
                                                    setText("sddss", registerEmailEdt, commonElements.encodeUtf8(getResources().getString(resource_message)), "red");
                                                } else {
                                                    setText("sddss", registerEmailEdt, commonElements.encodeUtf8(getResources().getString(resource_message)), "green");
                                                }
                                                progressViewActions("stop", emailProgressView);
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
                                                registerEmailEdt.setText(null);
                                                progressViewActions("stop", emailProgressView);
                                            }
                                        }
                                    });
                                } else {
                                    if (isAdded())
                                        setToastHelperMsg(getResources().getString(R.string.no_connection));
                                }
                            }
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
                    if (connectivityObserver.getConnectivityStatus(getActivity()) != AppConfig.NO_CONNECTION) {
                        spinnerState = (FirmNameWithID) registerSpnr.getSelectedItem();
                        Log.d(debugTag, spinnerState.getId() + "");
                        empty_fields = commonElements.encodeUtf8(getResources().getString(R.string.empty_fields));
                        if (!commonElements.validateEditText(new EditText[]{registerUsernameEdt, registerPasswordEdt, confirmPasswordEdt, registerEmailEdt, firmCodeEdt})) {
                            errorresponseTtv.setText(commonElements.decodeUtf8(empty_fields));
                        } else {
                            checkRegister(registerUsernameEdt.getText().toString(), registerPasswordEdt.getText().toString(), confirmPasswordEdt.getText().toString(), registerEmailEdt.getText().toString(), firmCodeEdt.getText().toString());
                        }
                    } else {
                        error_no_connection = commonElements.encodeUtf8(getResources().getString(R.string.no_connection));
                        Toast.makeText(getActivity(), commonElements.decodeUtf8(error_no_connection), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
                for (int i = 0; i < firmElementList.size(); i++) {
                    Log.d(debugTag, "firm_id: " + firmElementList.get(i).getFirm_id() + " firm_name: " + firmElementList.get(i).getFirm_name());
                    firmNamesList.add(new FirmNameWithID(firmElementList.get(i).getFirm_name(), firmElementList.get(i).getFirm_id()));
                }
                spinnerAdapter = new ArrayAdapter<FirmNameWithID>(getActivity(), android.R.layout.simple_spinner_item, firmNamesList);
                registerSpnr.setAdapter(spinnerAdapter);
                firmsLoaded = true;
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    private static Handler handlerStaff() {
        if ( mainThreadHandler == null ) mainThreadHandler = new Handler(Looper.getMainLooper());
        return mainThreadHandler;
    }

    private void checkRegister(String username, String password, String confirm_password, String email, String firm_code) {
        Call<UserConnectionStaff> call = apiService.registerUser(username, password, confirm_password, email, "Arx.net", firm_code);
        call.enqueue(new Callback<UserConnectionStaff>() {
            @Override
            public void onResponse(Response<UserConnectionStaff> response, Retrofit retrofit) {}

            @Override
            public void onFailure(Throwable t) {}
        });
    }

    private void fillValidityInputMap(String[] fieldsArray) {
        inputValidityMap = new HashMap<>();
        for ( int i = 0; i < fieldsArray.length; i++ ) {
            inputValidityMap.put(fieldsArray[i], false);
        }
    }

    private void setSignInHereSpan(){ commonElements.setLoginActivitySpan(signInHereTtv, getResources().getString(R.string.signInHere), 22, 34, 1); }

    private static void animationStaff(View view, float fromAlpha, float toAlpha, String visibility) {
        if ( visibility.equals("visible")) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        Animation fadeAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        fadeAnimation.setDuration(400);
        view.startAnimation(fadeAnimation);
    }

    private static void clearEditextHelper(EditText editText) {
        if ( !editText.getHelper().toString().isEmpty() ) editText.setHelper(null);
    }

    private static void progressViewActions(String action, ProgressView progressView) {
        if ( action.equals("start") ) {
            progressView.start();
        } else {
            progressView.stop();
        }
    }

    private void setConfirmPasswordEdtErrorMsg() {
        if ( registerPasswordEdt.getText().length() < 9 && !confirmPasswordEdt.getText().toString().equals(registerPasswordEdt.getText().toString()) ) {
            setText("sdds", confirmPasswordEdt, commonElements.encodeUtf8(getResources().getString(R.string.passwords_mismatch)), "red");
        } else if ( confirmPasswordEdt.getText().toString().equals(registerPasswordEdt.getText().toString()) ) {
            setText("sds", confirmPasswordEdt, "", "");
        } else if ( registerPasswordEdt.getText().length() > 8 ) {
            setText("sdss", confirmPasswordEdt, commonElements.encodeUtf8(getResources().getString(R.string.invalid_password_length)), "red");
        }
    }

    private void setText(String action, EditText editText, String decodedMessage, String color) {
        if ( action.equals("error") ) {
            editText.setHelper(Html.fromHtml("<font color=" + color + ">" + decodedMessage + "</font>"));
        } else {
            editText.setHelper(Html.fromHtml("<font color=" + color + ">" + decodedMessage + "</font>"));
        }
    }

    private void setToastHelperMsg(String message) { Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show(); }
}
