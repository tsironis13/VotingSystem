package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Html;
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
    private LinearLayout registerBaseLlt, errorcontainerLlt;
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
    private boolean firmsLoaded;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_register, container, false);
        registerBaseLlt             =   (LinearLayout) view.findViewById(R.id.registerBaseLlt);
        errorcontainerLlt           =   (LinearLayout) view.findViewById(R.id.errorcontainerLlt);
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
        snackBar                    =   ((LoginActivity)getActivity()).getSnackBar();
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
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputValidityMap.put(getResources().getString(R.string.username_tag), false);
                    registerPresenterImpl.handleInputFieldTextChanges(start, before, usernameEdt, acceptUsernameRlt, null, "username");
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            usernameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        if (!inputValidityMap.get(getResources().getString(R.string.username_tag)))
                            registerPresenterImpl.validateInputFieldOnFocusChange(setPresenterObjParams(connectionStatus, isAdded(), usernameEdt, usernameProgressView, getResources().getString(R.string.usernameValidation), acceptUsernameRlt, getResources().getString(R.string.username_tag), usernameEdt));
                }
            });
            passwordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        if (!inputValidityMap.get(getResources().getString(R.string.password_tag)))
                            registerPresenterImpl.validateInputFieldOnFocusChange(setPresenterObjParams(connectionStatus, isAdded(), passwordEdt, null, getResources().getString(R.string.passwordValidation), acceptPasswordRlt, getResources().getString(R.string.password_tag), passwordErrorTtv));
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
                    registerPresenterImpl.handleInputFieldTextChanges(start, before, confirmPasswordEdt, null, passwordEdt, getResources().getString(R.string.confirm_password));
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            confirmPasswordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) if (confirmPasswordEdt.getText().toString().isEmpty())
                        setText(getResources().getString(R.string.error), confirmPasswordEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))), "#DD2C00");
                }
            });
            emailEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                        if (!inputValidityMap.get(getResources().getString(R.string.email_tag)))
                            registerPresenterImpl.validateInputFieldOnFocusChange(setPresenterObjParams(connectionStatus, isAdded(), emailEdt, emailProgressView, getResources().getString(R.string.emailValidation), acceptEmailRlt, getResources().getString(R.string.email_tag), emailEdt));
                }
            });
            pickFirmSpnr.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner parent, View view, int position, long id) {
                    Log.e(debugTag, "onItemSelected, position: "+ position);
                    if (view instanceof TextView)
                        ((TextView) view).setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                    if (position != 0) {
                        //FirmNamesSpnrNothingSelectedAdapter firmNamesSpnrNothingSelectedAdapter = (FirmNamesSpnrNothingSelectedAdapter) parent.getAdapter();
                        //FirmNameWithID firmNameWithID = (FirmNameWithID) firmNamesSpnrNothingSelectedAdapter.getUnderlinedSpinnerAdapter().getItem(position - 1);
                        //if (!firmCodeEdt.getText().toString().isEmpty()) registerPresenterImpl.validateFirmCode(firmNameWithID.getId(), firmCodeEdt.getText().toString());
                    }
                }
            });
            pickFirmSpnr.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.e(debugTag, "on touch called");
                    if (!firmsLoaded) showSnackBar(AppConfig.NO_CONNECTION);
                    return false;
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (connectionStatus == AppConfig.NO_CONNECTION) {
                        showSnackBar(AppConfig.NO_CONNECTION);
                    } else {
                        registerUserProgressView.start();
                        registerPresenterImpl.validateForm(registerBaseLlt);
                    }
                }
            });

            signInHereTtv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof TextView) commonElements.signInHereOnClick();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(connectionStatusReceiver, new IntentFilter(getResources().getString(R.string.network_state_update)));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(connectionStatusReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
            if ( !inputEdt.getHelper().toString().isEmpty() ) inputEdt.setHelper(null);
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
    public void onFailure(List<FirmNameWithID> firmNameWithIDList, boolean firmsLoaded) {
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
    public void setInputFieldError(int code, View view) {
        if ( view instanceof TextView ) {
            inputValidityMap.put(getResources().getString(R.string.password_tag), false);
            if ( !inputValidityMap.get(getResources().getString(R.string.password_tag)) ) passwordEdt.getChildAt(1).setVisibility(View.GONE);
        }
        setText(getResources().getString(R.string.error), view, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(inputValidationCodes.get(code)))), "#DD2C00");
    }

    @Override
    public void onSuccess(RelativeLayout inputValidRlt, String tag) {
        animationStaff(inputValidRlt, 0.0f, 1.0f, getResources().getString(R.string.visible));
        if ( tag.equals(getResources().getString(R.string.password_tag)) ) {
            animationStaff(showHidePasswordTtv, 1.0f, 0.0f, getResources().getString(R.string.gone));
            showhideAcceptPasswordAnimationRunnable = new Runnable() {
                @Override
                public void run() {
                    animationStaff(showHidePasswordTtv, 0.0f, 1.0f, getResources().getString(R.string.visible));
                    animationStaff(acceptPasswordRlt, 1.0f, 0.0f, getResources().getString(R.string.gone));
                }
            };
            handlerStaff().postDelayed(showhideAcceptPasswordAnimationRunnable, AppConfig.showhideAcceptDelay);
            showhideAcceptPasswordAnimationTargetTimeinMillis = System.currentTimeMillis() + AppConfig.showhideAcceptDelay;
        }
        inputValidityMap.put(tag, true);
    }

    @Override
    public void onFormValidationFailure(String field, String errorType) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) errorcontainerLlt.getLayoutParams();
        params.setMargins(0, 0, 0, 87);
        errorcontainerLlt.setLayoutParams(params);
        TextView textView = new TextView(getActivity());
        textView.setPadding(50, 80, 35, 80);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setTextColor(Color.parseColor("#DD2C00"));
        textView.setText(getResources().getString(R.string.empty_fields, field));
        com.rey.material.widget.RelativeLayout.LayoutParams lp  = new  com.rey.material.widget.RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(lp);
        errorcontainerLlt.addView(textView);
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
        for ( String fieldsArrayItem : fieldsArray ) {
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
                inputValidityMap.put(getResources().getString(R.string.password_tag), false);
                if ( System.currentTimeMillis() < showhideAcceptPasswordAnimationTargetTimeinMillis ) handlerStaff().removeCallbacks(showhideAcceptPasswordAnimationRunnable);
                registerPresenterImpl.handleRegisterPasswordEdtTextChanges(start, before, passwordEdt, acceptPasswordRlt, showHidePasswordTtv);
                if ( !passwordErrorTtv.getText().toString().isEmpty() ) {
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
