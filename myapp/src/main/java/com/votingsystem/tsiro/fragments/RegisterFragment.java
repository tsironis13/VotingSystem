package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import com.androidadvance.topsnackbar.TSnackbar;
import com.rey.material.widget.EditText;
import com.rey.material.widget.RelativeLayout;
import com.rey.material.widget.LinearLayout;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.TextView;
import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.Register.RegisterPresenterImpl;
import com.votingsystem.tsiro.Register.RegisterPresenterParamsObj;
import com.votingsystem.tsiro.Register.RegisterView;
import com.votingsystem.tsiro.adapters.FirmNamesSpnrNothingSelectedAdapter;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import com.votingsystem.tsiro.interfaces.DismissErrorContainerSnackBar;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.votingsystem.R;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 11/12/2015.
 */
public class RegisterFragment extends Fragment implements RegisterView{
    private static final String debugTag = RegisterFragment.class.getSimpleName();
    private static String error_no_connection, empty_fields;
    private LinearLayout baseLlt;
    private RelativeLayout acceptUsernameRlt, acceptPasswordRlt, acceptEmailRlt;
    private TextView signInHereTtv, showHidePasswordTtv, passwordErrorTtv;
    private EditText usernameEdt, passwordEdt, confirmPasswordEdt, emailEdt, firmCodeEdt;
    private Button submitBtn;
    private ProgressView usernameProgressView, emailProgressView, registerUserProgressView;
    private Spinner pickFirmSpnr;
    private View view;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private SparseIntArray inputValidationCodes;
    private BroadcastReceiver connectionStatusReceiver;
    private HashMap<String, Boolean> inputValidityMap;
    private static Handler mainThreadHandler;
    private static long showhideAcceptPasswordAnimationTargetTimeinMillis;
    private Runnable showhideAcceptPasswordAnimationRunnable;
    private TextWatcher registerPasswordEdtTextWatcher;
    private int connectionStatus, initialConnectionStatus;
    private RegisterPresenterImpl registerPresenterImpl;
    private SnackBar snackBar;
    private boolean firmsLoaded, validationMapChanged, formSubmitted;
    private TSnackbar errorContainerSnackbar;
    private DismissErrorContainerSnackBar dismissErrorContainerSnackBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) {
            this.commonElements                 =   (LoginActivityCommonElementsAndMuchMore) context;
            this.dismissErrorContainerSnackBar  =   (DismissErrorContainerSnackBar) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_register, container, false);
        baseLlt                     =   (LinearLayout) view.findViewById(R.id.baseLlt);
        acceptUsernameRlt           =   (RelativeLayout) view.findViewById(R.id.acceptUsernameRlt);
        acceptPasswordRlt           =   (RelativeLayout) view.findViewById(R.id.acceptPasswordRlt);
        acceptEmailRlt              =   (RelativeLayout) view.findViewById(R.id.acceptEmailRlt);
        usernameEdt                 =   (EditText) view.findViewById(R.id.usernameEdt);
        passwordEdt                 =   (EditText) view.findViewById(R.id.passwordEdt);
        confirmPasswordEdt          =   (EditText) view.findViewById(R.id.confirmPasswordEdt);
        emailEdt                    =   (EditText) view.findViewById(R.id.emailEdt);
        firmCodeEdt                 =   (EditText) view.findViewById(R.id.firmCodeEdt);
        signInHereTtv               =   (TextView) view.findViewById(R.id.signInHereTtv);
        showHidePasswordTtv         =   (TextView) view.findViewById(R.id.showHidePasswordTtv);
        passwordErrorTtv            =   (TextView) view.findViewById(R.id.passwordErrorTtv);
        submitBtn                   =   (Button) view.findViewById(R.id.submitBtn);
        usernameProgressView        =   (ProgressView) view.findViewById(R.id.usernameProgressView);
        emailProgressView           =   (ProgressView) view.findViewById(R.id.emailProgressView);
        registerUserProgressView    =   (ProgressView) view.findViewById(R.id.registerUserProgressView);
        pickFirmSpnr                =   (Spinner) view.findViewById(R.id.pickFirmSpnr);
        snackBar                    =   ((LoginActivity) getActivity()).getSnackBar();
        initialConnectionStatus     =   getArguments().getInt(getResources().getString(R.string.connectivity_status));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ( savedInstanceState == null ) {
            firmsLoaded = false;
            if (snackBar.isShown()) snackBar.dismiss();
            registerPresenterImpl = new RegisterPresenterImpl(this);
            passwordEdt.addTextChangedListener(handleRegisterPasswordTextChanges());
            fillValidityInputMap(getResources().getStringArray(R.array.input_fields_array));
            inputValidationCodes = AppConfig.getCodes();

            registerPresenterImpl.firmNamesSpnrActions(initialConnectionStatus);
            connectionStatus = initialConnectionStatus;
            initializeBroadcastReceivers();

            submitBtn.setTransformationMethod(null);
            setSignInHereSpan();
            usernameEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (formSubmitted) validationMapChanged = true;
                    inputValidityMap.put(getResources().getString(R.string.username_tag), false);
                    registerPresenterImpl.handleInputFieldTextChanges(start, before, usernameEdt, acceptUsernameRlt, null, "username");
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            usernameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        if (!inputValidityMap.get(getResources().getString(R.string.username_tag)) && !TextUtils.isEmpty(usernameEdt.getText().toString())) {
                            registerPresenterImpl.validateInputFieldOnFocusChange(setPresenterObjParams(connectionStatus, isAdded(), usernameEdt, usernameProgressView, getResources().getString(R.string.usernameValidation), acceptUsernameRlt, getResources().getString(R.string.username_tag), usernameEdt));
                        } else if (!inputValidityMap.get(getResources().getString(R.string.username_tag)) && TextUtils.isEmpty(usernameEdt.getText().toString())) {
                            setText(getResources().getString(R.string.error), usernameEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))), "#DD2C00");
                        }
                        //validationMapChanged = false;
                }
            });
            passwordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        if (!inputValidityMap.get(getResources().getString(R.string.password_tag)) && !TextUtils.isEmpty(passwordEdt.getText().toString())) {
                            registerPresenterImpl.validateInputFieldOnFocusChange(setPresenterObjParams(connectionStatus, isAdded(), passwordEdt, null, getResources().getString(R.string.passwordValidation), acceptPasswordRlt, getResources().getString(R.string.password_tag), passwordErrorTtv));
                            Log.e(debugTag, "here");
                        } else if (!inputValidityMap.get(getResources().getString(R.string.password_tag)) && TextUtils.isEmpty(passwordEdt.getText().toString())){
                            setText(getResources().getString(R.string.error), passwordErrorTtv, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))), "#DD2C00");
                        }
                        //validationMapChanged = false;
                }
            });
            showHidePasswordTtv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    passwordEdt.removeTextChangedListener(registerPasswordEdtTextWatcher);
                    registerPresenterImpl.handleShowHidePasswordTtv(passwordEdt);
                    passwordEdt.addTextChangedListener(registerPasswordEdtTextWatcher);
                }
            });
            confirmPasswordEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (formSubmitted) validationMapChanged = true;
                    //inputValidityMap.put(getResources().getString(R.string.confirm_password_tag), false);
                    registerPresenterImpl.handleInputFieldTextChanges(start, before, confirmPasswordEdt, null, passwordEdt, getResources().getString(R.string.confirm_password));
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            confirmPasswordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        if (TextUtils.isEmpty(confirmPasswordEdt.getText().toString())) {
                            setText(getResources().getString(R.string.error), confirmPasswordEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))), "#DD2C00");
                        } else {
                            //if (confirmPasswordEdt.getText().length() == AppConfig.PASSWORD_LENGTH && confirmPasswordEdt.getText().toString().equals(passwordEdt.getText().toString()))
                                //inputValidityMap.put(getResources().getString(R.string.confirm_password_tag), true);
                        }
                        //validationMapChanged = false;
                }
            });
            emailEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (formSubmitted) validationMapChanged = false;
                    inputValidityMap.put(getResources().getString(R.string.email_tag), false);
                    registerPresenterImpl.handleInputFieldTextChanges(start, before, emailEdt, acceptEmailRlt, null, "email");
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            emailEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        if (!inputValidityMap.get(getResources().getString(R.string.email_tag)) && !TextUtils.isEmpty(emailEdt.getText().toString())) {
                            registerPresenterImpl.validateInputFieldOnFocusChange(setPresenterObjParams(connectionStatus, isAdded(), emailEdt, emailProgressView, getResources().getString(R.string.emailValidation), acceptEmailRlt, getResources().getString(R.string.email_tag), emailEdt));
                        } else if (!inputValidityMap.get(getResources().getString(R.string.email_tag)) && TextUtils.isEmpty(emailEdt.getText().toString())) {
                            setText(getResources().getString(R.string.error), emailEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))), "#DD2C00");
                        }
                        //validationMapChanged = false;
                }
            });
            firmCodeEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (formSubmitted) validationMapChanged = true;
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
            firmCodeEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        if (TextUtils.isEmpty(firmCodeEdt.getText().toString())) setText(getResources().getString(R.string.error), firmCodeEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))), "#DD2C00");
                        validationMapChanged = false;
                }
            });
            pickFirmSpnr.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner parent, View view, int position, long id) {
                    if (view instanceof TextView)
                        ((TextView) view).setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                }
            });
            pickFirmSpnr.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!firmsLoaded) showSnackBar(AppConfig.NO_CONNECTION);
                    return false;
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (errorContainerSnackbar != null && errorContainerSnackbar.isShown())
                        errorContainerSnackbar.dismiss();
                    if (connectionStatus == AppConfig.NO_CONNECTION) {
                        showSnackBar(AppConfig.NO_CONNECTION);
                    } else {
                        formSubmitted = true;
                        registerUserProgressView.start();
                        registerPresenterImpl.emptyFieldsValidation(baseLlt);
                    }
                }
            });
            signInHereTtv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (errorContainerSnackbar != null) errorContainerSnackbar.dismiss();
                    if (v instanceof TextView) commonElements.signInHereOnClick();
                }
            });
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(connectionStatusReceiver, new IntentFilter(getResources().getString(R.string.network_state_update)));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(connectionStatusReceiver);
        registerPresenterImpl.onDestroy();
        this.commonElements = null;
        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
    /*
     *  REGISTER VIEW CALLBACKS
     *
     */
    @Override
    public void clearEditextHelpersAndSuccessIcon(String action, RelativeLayout acceptRlt, EditText inputEdt) {
        if (action.equals(getResources().getString(R.string.clear_success_icon))) {
            animationStaff(acceptRlt, 1.0f, 0.0f, getResources().getString(R.string.gone));
        } else {
            if (!TextUtils.isEmpty(inputEdt.getHelper().toString())) inputEdt.setHelper(null);
        }
    }

    @Override
    public void changeTransformationMethod(TransformationMethod transformationMethod, String text) {
        passwordEdt.setTransformationMethod(transformationMethod);
        animationStaff(showHidePasswordTtv, 0.0f, 1.0f, getResources().getString(R.string.visible));
        showHidePasswordTtv.setText(text);
        passwordEdt.setSelection(passwordEdt.getText().length());
    }

    @Override
    public void handlePasswordTextChanges(String text, TextView showHidePasswordTtv) {
        showHidePasswordTtv.setVisibility(View.VISIBLE);
        showHidePasswordTtv.setText(text);
    }

    @Override
    public void onSuccessfulFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDList, boolean firmsLoaded) {
        this.firmsLoaded = firmsLoaded;
        if (pickFirmSpnr.getAdapter() == null) {
            pickFirmSpnr.setAdapter(new FirmNamesSpnrNothingSelectedAdapter(getActivity(), R.layout.spinner_selection_item, firmNameWithIDList));
            setpickFirmSpnrDropDownViewRes(pickFirmSpnr);
        }
        pickFirmSpnr.setClickable(true);
    }

    @Override
    public void onFailureFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDList, boolean firmsLoaded) {
        this.firmsLoaded = firmsLoaded;
        pickFirmSpnr.setAdapter(new FirmNamesSpnrNothingSelectedAdapter(getActivity(), R.layout.spinner_selection_item, firmNameWithIDList));
        setpickFirmSpnrDropDownViewRes(pickFirmSpnr);
        pickFirmSpnr.setClickable(false);
    }

    @Override
    public void displayFeedbackMsg(int code) {
        showSnackBar(code);
    }

    @Override
    public void showFieldValidationProgress(ProgressView inputFieldPrgv) { inputFieldPrgv.start(); }

    @Override
    public void hideFieldValidationProgress(ProgressView inputFieldPrgv) { inputFieldPrgv.stop(); }

    @Override
    public void onFailure(int code, View view) {
        if (view instanceof TextView) {
            inputValidityMap.put(getResources().getString(R.string.password_tag), false);
            if (!inputValidityMap.get(getResources().getString(R.string.password_tag))) passwordEdt.getChildAt(1).setVisibility(View.GONE);
        }
        setText(getResources().getString(R.string.error), view, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(inputValidationCodes.get(code)))), "#DD2C00");
        if (validationMapChanged && formSubmitted) {
            registerPresenterImpl.validateForm(inputValidityMap);
        }
        formSubmitted = false;
    }

    @Override
    public void onSuccess(RelativeLayout inputValidRlt, String tag) {
        animationStaff(inputValidRlt, 0.0f, 1.0f, getResources().getString(R.string.visible));
        if (tag.equals(getResources().getString(R.string.password_tag))) {
            animationStaff(showHidePasswordTtv, 1.0f, 0.0f, getResources().getString(R.string.gone));
            showhideAcceptPasswordAnimationRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isAdded()) {
                        animationStaff(showHidePasswordTtv, 0.0f, 1.0f, getResources().getString(R.string.visible));
                        animationStaff(acceptPasswordRlt, 1.0f, 0.0f, getResources().getString(R.string.gone));
                    }
                }
            };
            handlerStaff().postDelayed(showhideAcceptPasswordAnimationRunnable, AppConfig.showhideAcceptDelay);
            showhideAcceptPasswordAnimationTargetTimeinMillis = System.currentTimeMillis() + AppConfig.showhideAcceptDelay;
        }
        inputValidityMap.put(tag, true);
        if (validationMapChanged && formSubmitted) {
            registerPresenterImpl.validateForm(inputValidityMap);
        }
        formSubmitted = false;
    }

    @Override
    public void onEmptyFieldsValidationFailure(String field, String errorType) {
        errorContainerSnackbar = TSnackbar.make(((LoginActivity) getActivity()).getErrorContainerRlt(), getResources().getString(R.string.empty_fields, field), TSnackbar.LENGTH_INDEFINITE);
        View snackBarView = errorContainerSnackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor(getResources().getString(R.string.errorContainer_Snackbar_background_color)));
        android.widget.TextView snackBarTtv = (android.widget.TextView) snackBarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        snackBarTtv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        snackBarTtv.setTextColor(Color.parseColor(getResources().getString(R.string.errorContainer_Snackbar_Ttv_color)));
        errorContainerSnackbar.show();
        if (registerUserProgressView != null && registerUserProgressView.isShown()) registerUserProgressView.stop();
        dismissErrorContainerSnackBar.dismissErrorContainerSnackBar(errorContainerSnackbar);
    }

    @Override
    public void onEmptyFieldsValidationSuccess() {
        if (errorContainerSnackbar != null && errorContainerSnackbar.isShown()) errorContainerSnackbar.dismiss();
        if (registerUserProgressView != null && registerUserProgressView.isShown()) registerUserProgressView.stop();


        /*View current = getActivity().getCurrentFocus();
        if (current != null) {
            Log.e(debugTag, "current focus: "+current);
            current.clearFocus();
            current.requestFocus();
        }
        if (!validationMapChanged) {
            registerPresenterImpl.validateForm(inputValidityMap);
        }*/
    }

    @Override
    public void onFormValidationFailure() {

    }

    @Override
    public void onFormValidationSuccess() {

    }

    private void initializeBroadcastReceivers() {
        connectionStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                connectionStatus = intent.getExtras().getInt(getResources().getString(R.string.connectivity_status));
                if (connectionStatus != AppConfig.NO_CONNECTION) {
                    if (snackBar.isShown()) snackBar.dismiss();
                }
                registerPresenterImpl.firmNamesSpnrActions(connectionStatus);
            }
        };
    }

    private RegisterPresenterParamsObj setPresenterObjParams(int connectionStatus, boolean isAdded, EditText inputEditext, ProgressView inputFieldProgressView, String retrofitAction, RelativeLayout validInputRlt, String tag, View errorView) {
        RegisterPresenterParamsObj registerPresenterParamsObj  = new RegisterPresenterParamsObj();
        registerPresenterParamsObj.setConnectionStatus(connectionStatus);
        registerPresenterParamsObj.setIsAdded(isAdded);
        registerPresenterParamsObj.setInputEditText(inputEditext);
        registerPresenterParamsObj.setInputFieldProgressView(inputFieldProgressView);
        registerPresenterParamsObj.setRetrofitAction(retrofitAction);
        registerPresenterParamsObj.setValidInputRlt(validInputRlt);
        registerPresenterParamsObj.setTag(tag);
        registerPresenterParamsObj.setErrorView(errorView);
        return registerPresenterParamsObj;
    }

    private void setText(String action, View view, String decodedMessage, String color) {
        if ( view instanceof EditText ) {
            if (action.equals(getResources().getString(R.string.error))) ((EditText) view).setHelper(Html.fromHtml("<font color=" + color + ">" + decodedMessage + "</font>"));
        } else if ( view instanceof TextView ) {
            ((TextView) view).setText(decodedMessage);
        }
    }

    private static Handler handlerStaff() {
        if ( mainThreadHandler == null ) mainThreadHandler = new Handler(Looper.getMainLooper());
        return mainThreadHandler;
    }

    private void fillValidityInputMap(String[] fieldsArray) {
        inputValidityMap = new HashMap<>();
        for (String fieldsArrayItem : fieldsArray) {
            inputValidityMap.put(fieldsArrayItem, false);
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
                if (formSubmitted) validationMapChanged = true;
                inputValidityMap.put(getResources().getString(R.string.password_tag), false);
                if (System.currentTimeMillis() < showhideAcceptPasswordAnimationTargetTimeinMillis) handlerStaff().removeCallbacks(showhideAcceptPasswordAnimationRunnable);
                registerPresenterImpl.handleRegisterPasswordEdtTextChanges(start, before, passwordEdt, acceptPasswordRlt, showHidePasswordTtv);
                if (!TextUtils.isEmpty(passwordErrorTtv.getText().toString())) {
                    passwordErrorTtv.setText(null);
                    passwordEdt.getChildAt(1).setVisibility(View.VISIBLE);
                }
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

    private static void setpickFirmSpnrDropDownViewRes(Spinner pickFirmSpnr) {
        FirmNamesSpnrNothingSelectedAdapter firmNamesSpnrNothingSelectedAdapter = (FirmNamesSpnrNothingSelectedAdapter) pickFirmSpnr.getAdapter();
        firmNamesSpnrNothingSelectedAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
    }

    private void showSnackBar(int code) {
        snackBar.actionText("");
        if (code == AppConfig.NO_CONNECTION) {
            snackBar
                    .text(getResources().getString(R.string.no_connection))
                    .applyStyle(R.style.SnackBarNoConnection);
        } else if (code == AppConfig.UNAVAILABLE_SERVICE) {
            snackBar
                    .text(getResources().getString(R.string.unavailable_service))
                    .applyStyle(R.style.SnackBarUnavailableService);
        } else if (code == AppConfig.INTERNAL_ERROR) {
            snackBar
                    .text(getResources().getString(R.string.error_occured))
                    .applyStyle(R.style.SnackBarInternalError);
        }
        snackBar.show();
    }
}
