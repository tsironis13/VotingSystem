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
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
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
import com.votingsystem.tsiro.POJO.UserConnectionStaff;
import com.votingsystem.tsiro.Register.RegisterPresenterImpl;
import com.votingsystem.tsiro.Register.RegisterPresenterParamsObj;
import com.votingsystem.tsiro.Register.RegisterView;
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

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by user on 11/12/2015.
 */
public class RegisterFragment extends Fragment implements RegisterView{
    private static final String debugTag = RegisterFragment.class.getSimpleName();
    private static String error_no_connection, empty_fields;
    private LinearLayout registerBaseLlt;
    private RelativeLayout acceptUsernameRlt, acceptPasswordRlt, acceptEmailRlt;
    private TextView signInHereTtv, errorresponseTtv, showHidePasswordTtv, passwordErrorTtv;
    private EditText registerUsernameEdt, registerPasswordEdt, confirmPasswordEdt, registerEmailEdt, firmCodeEdt;
    private Button submitBtn;
    private ProgressView usernameProgressView, emailProgressView;
    private Spinner pickFirmSpnr;
    private View view;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private ConnectivityObserver connectivityObserver;
    private ApiService apiService;
    private SparseIntArray inputValidationCodes;
    private ArrayAdapter<FirmNameWithID> spinnerAdapter;
    private FirmNameWithID spinnerState;
    private BroadcastReceiver broadcastReceiver;
    private HashMap<String, Boolean> inputValidityMap;
    private static Handler mainThreadHandler;
    private UpdateInputMapValue updateInputMapInterface;
    private static long showhideAcceptPasswordAnimationTargetTimeinMillis;
    private Runnable showhideAcceptPasswordAnimationRunnable;
    private TextWatcher registerPasswordEdtTextWatcher;
    private int connectionStatus, initialConnectionStatus;
    private RegisterPresenterImpl registerPresenter;
    private RegisterPresenterParamsObj registerPresenterParamsObj;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_register, container, false);
        registerBaseLlt             =   (LinearLayout) view.findViewById(R.id.registerBaseLlt);
        acceptUsernameRlt           =   (RelativeLayout) view.findViewById(R.id.acceptUsernameRlt);
        acceptPasswordRlt           =   (RelativeLayout) view.findViewById(R.id.acceptPasswordRlt);
        registerUsernameEdt         =   (EditText) view.findViewById(R.id.registerUsernameEdt);
        registerPasswordEdt         =   (EditText) view.findViewById(R.id.registerPasswordEdt);
        confirmPasswordEdt          =   (EditText) view.findViewById(R.id.confirmPasswordEdt);
        registerEmailEdt            =   (EditText) view.findViewById(R.id.registerEmailEdt);
        firmCodeEdt                 =   (EditText) view.findViewById(R.id.firmCodeEdt);
        signInHereTtv               =   (TextView) view.findViewById(R.id.signInHereTtv);
        errorresponseTtv            =   (TextView) view.findViewById(R.id.errorresponseTtv);
        showHidePasswordTtv         =   (TextView) view.findViewById(R.id.showHidePasswordTtv);
        passwordErrorTtv            =   (TextView) view.findViewById(R.id.passwordErrorTtv);
        submitBtn                   =   (Button) view.findViewById(R.id.submitBtn);
        usernameProgressView        =   (ProgressView) view.findViewById(R.id.usernameProgressView);
        emailProgressView           =   (ProgressView) view.findViewById(R.id.emailProgressView);
        pickFirmSpnr                =   (Spinner) view.findViewById(R.id.pickFirmSpnr);
        connectivityObserver        =   getArguments().getParcelable("connectivityObserver");
        initialConnectionStatus     =   getArguments().getInt("initialConnectivityStatus");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ( savedInstanceState == null ) {
            registerPresenter = new RegisterPresenterImpl(this);
            registerPasswordEdt.addTextChangedListener(handleRegisterPasswordTextChanges());
            fillValidityInputMap(getResources().getStringArray(R.array.input_fields_array));
            inputValidationCodes = AppConfig.getCodes();

            registerPresenter.getFirmNamesToPopulateSpnr(initialConnectionStatus);

            apiService = RetrofitSingleton.getInstance().getApiService();

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    connectionStatus = intent.getExtras().getInt("connectivityStatus");
                    registerPresenter.getFirmNamesToPopulateSpnr(connectionStatus);
                }
            };

            submitBtn.setTransformationMethod(null);
            setSignInHereSpan();
            registerUsernameEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputValidityMap.put(getResources().getString(R.string.username_tag), false);
                    registerPresenter.handleInputFieldTextChanges(start, before, registerUsernameEdt, acceptUsernameRlt, acceptUsernameRlt.getVisibility());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            registerUsernameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) if ( !inputValidityMap.get(getResources().getString(R.string.username_tag)) ) registerPresenter.validateInputFieldOnFocusChange(setPresenterObjParams(connectionStatus, isAdded(), registerUsernameEdt.getText().toString(), usernameProgressView, getResources().getString(R.string.usernameValidation), acceptUsernameRlt, getResources().getString(R.string.username_tag), null, registerUsernameEdt));
                }
            });
            registerPasswordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if ( !hasFocus ) if ( !inputValidityMap.get(getResources().getString(R.string.password_tag)) ) registerPresenter.validateInputFieldOnFocusChange(setPresenterObjParams(connectionStatus, isAdded(), registerPasswordEdt.getText().toString(), null, getResources().getString(R.string.passwordValidation), acceptPasswordRlt, getResources().getString(R.string.password_tag), registerPasswordEdt, passwordErrorTtv));
                }

            });
            showHidePasswordTtv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerPasswordEdt.removeTextChangedListener(registerPasswordEdtTextWatcher);
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
                    registerPasswordEdt.addTextChangedListener(registerPasswordEdtTextWatcher);
                }
            });
            confirmPasswordEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (start >= 2 && inputValidityMap.get(getResources().getString(R.string.password_tag))) {
                        if (!confirmPasswordEdt.getText().toString().equals(registerPasswordEdt.getText().toString())) {
                            setText("error", confirmPasswordEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.passwords_mismatch))), "#DD2C00");
                        } else {
                            if (confirmPasswordEdt.getHelper() != null)
                                clearEditextHelper(confirmPasswordEdt);
                        }
                    }
                    if (start == 0 && before == 1) if (confirmPasswordEdt.getHelper() != null)
                        clearEditextHelper(confirmPasswordEdt);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
            confirmPasswordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) if (confirmPasswordEdt.getText().toString().isEmpty()) setText("error", confirmPasswordEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))), "#DD2C00");
                }
            });
            registerEmailEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if ( !hasFocus ) {
                        if ( !inputValidityMap.get(getResources().getString(R.string.email_tag)) ) {
                            if (registerEmailEdt.getText().toString().isEmpty()) {
                                setText("error", registerEmailEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))), "#DD2C00");
                            } else {
                                //if (connectivityObserver.getConnectivityStatus(getActivity(), true, true) != AppConfig.NO_CONNECTION) {
                                    //progressViewActions("start", emailProgressView);
                                    Call<UserConnectionStaff> call = apiService.isEmailValid(getResources().getString(R.string.emailValidation), registerEmailEdt.getText().toString());
                                    call.enqueue(new Callback<UserConnectionStaff>() {
                                        @Override
                                        public void onResponse(Response<UserConnectionStaff> response, Retrofit retrofit) {
                                            int resource_message = inputValidationCodes.get(response.body().getError_code());
                                            if (isAdded()) {
                                                if (response.body().getError_code() != AppConfig.INPUT_OK) {
                                                    setText("sddss", registerEmailEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(resource_message))), "#DD2C00");
                                                } else {
                                                    setText("sddss", registerEmailEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(resource_message))), "#DD2C00");
                                                }
                                               // progressViewActions("stop", emailProgressView);
                                            }
                                        }
                                        @Override
                                        public void onFailure(Throwable t) {
                                            if (isAdded()) {
                                                if (t instanceof IOException) {
                                                   // setToastHelperMsg(getResources().getString(R.string.no_connection));
                                                } else {
                                                 //   setToastHelperMsg(getResources().getString(R.string.error_occured));
                                                }
                                                registerEmailEdt.setText(null);
                                               // progressViewActions("stop", emailProgressView);
                                            }
                                        }
                                    });
                                //} else {
                                    //if (isAdded())
                                        //setToastHelperMsg(getResources().getString(R.string.no_connection));
                                //}
                            }
                        }
                    }
                }
            });
            registerEmailEdt.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        pickFirmSpnr.performClick();
                        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(firmCodeEdt.getWindowToken(), 0);
                    }
                    return false;
                }
            });
            pickFirmSpnr.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner parent, View view, int position, long id) {
                    //firmCodeEdt.setFocusableInTouchMode(true);
                    //firmCodeEdt.requestFocus();
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
                    //if (connectivityObserver.getConnectivityStatus(getActivity(), true, true) != AppConfig.NO_CONNECTION) {
                        spinnerState = (FirmNameWithID) pickFirmSpnr.getSelectedItem();
                        Log.d(debugTag, spinnerState.getId() + "");
                        empty_fields = commonElements.encodeUtf8(getResources().getString(R.string.empty_fields));
                        if (!commonElements.validateEditText(new EditText[]{registerUsernameEdt, registerPasswordEdt, confirmPasswordEdt, registerEmailEdt, firmCodeEdt})) {
                            errorresponseTtv.setText(commonElements.decodeUtf8(empty_fields));
                        } else {
                            checkRegister(registerUsernameEdt.getText().toString(), registerPasswordEdt.getText().toString(), confirmPasswordEdt.getText().toString(), registerEmailEdt.getText().toString(), firmCodeEdt.getText().toString());
                        }
                  //  } else {
                        error_no_connection = commonElements.encodeUtf8(getResources().getString(R.string.no_connection));
                        Toast.makeText(getActivity(), commonElements.decodeUtf8(error_no_connection), Toast.LENGTH_SHORT).show();
                  //  }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(debugTag, "onResume");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("networkStateUpdated"));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(debugTag, "onPause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerPresenter.onDestroy();
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

    private TextWatcher handleRegisterPasswordTextChanges() {
        registerPasswordEdtTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

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
        };
        return registerPasswordEdtTextWatcher;
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

    private RegisterPresenterParamsObj setPresenterObjParams(int connectionStatus, boolean isAdded, String inputField, ProgressView inputFieldProgressView, String retrofitAction, RelativeLayout validInputRlt, String tag, EditText inputEditext, View errorView) {
        registerPresenterParamsObj  = new RegisterPresenterParamsObj();
        registerPresenterParamsObj.setConnectionStatus(connectionStatus);
        registerPresenterParamsObj.setIsAdded(isAdded);
        registerPresenterParamsObj.setInputField(inputField);
        registerPresenterParamsObj.setInputFieldProgressView(inputFieldProgressView);
        registerPresenterParamsObj.setRetrofitAction(retrofitAction);
        registerPresenterParamsObj.setValidInputRlt(validInputRlt);
        registerPresenterParamsObj.setTag(tag);
        registerPresenterParamsObj.setInputEditText(inputEditext);
        registerPresenterParamsObj.setErrorView(errorView);
        return registerPresenterParamsObj;
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

    @Override
    public void clearEditextHelpersAndSuccessIcon(String action, RelativeLayout acceptRlt, EditText inputEdt) {
        if ( action.equals("clearSuccessIcon") ) {
            animationStaff(acceptRlt, 1.0f, 0.0f, "gone");
        } else {
            if ( !inputEdt.getHelper().toString().isEmpty() ) inputEdt.setHelper(null);
        }
    }

    @Override
    public void onSuccessfulFirmNamesSpnrLoad(ArrayList<FirmNameWithID> firmNameWithIDArrayList) {
        spinnerAdapter = new ArrayAdapter<FirmNameWithID>(getActivity(), android.R.layout.simple_spinner_item, firmNameWithIDArrayList);
        pickFirmSpnr.setAdapter(spinnerAdapter);
    }

    @Override
    public void onFailure() { Log.e(debugTag, "onFailure"); }

    @Override
    public void setToastMsg(int code) { Toast.makeText(getActivity(), commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(inputValidationCodes.get(code)))), Toast.LENGTH_SHORT).show(); }

    @Override
    public void showFieldValidationProgress(ProgressView inputFieldPrgv) { inputFieldPrgv.start(); }

    @Override
    public void hideFieldValidationProgress(ProgressView inputFieldPrgv) { inputFieldPrgv.stop(); }

    @Override
    public void setInputFieldError(int code, View view) {
        if ( view instanceof TextView ) {
            inputValidityMap.put(getResources().getString(R.string.password_tag), false);
            if ( !inputValidityMap.get(getResources().getString(R.string.password_tag)) ) registerPasswordEdt.getChildAt(1).setVisibility(View.GONE);
        }
        setText("error", view, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(inputValidationCodes.get(code)))), "#DD2C00");
    }

    @Override
    public void onSuccess(RelativeLayout inputValidRlt, String tag) {
        animationStaff(inputValidRlt, 0.0f, 1.0f, "visible");
        if ( tag.equals(getResources().getString(R.string.password_tag)) ) {
            animationStaff(showHidePasswordTtv, 1.0f, 0.0f, "gone");
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
        inputValidityMap.put(tag, true);
    }

    private void setText(String action, View view, String decodedMessage, String color) {
        if ( view instanceof EditText ) {
            if (action.equals("error")) {
                ((EditText) view).setHelper(Html.fromHtml("<font color=" + color + ">" + decodedMessage + "</font>"));
            } else {
                ((EditText) view).setHelper(Html.fromHtml("<font color=" + color + ">" + decodedMessage + "</font>"));
            }
        } else if ( view instanceof TextView ) {
            ((TextView) view).setText(decodedMessage);
        }
    }
}
