package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.rey.material.widget.EditText;
import com.rey.material.widget.LinearLayout;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.TextView;
//import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.POJO.RegisterFormBody;
import com.votingsystem.tsiro.POJO.RegisterFormField;
import com.votingsystem.tsiro.LoginActivityMVC.LAMVCPresenterImpl;
import com.votingsystem.tsiro.LoginActivityMVC.LAMVCView;
import com.votingsystem.tsiro.adapters.FirmNamesSpnrNothingSelectedAdapter;
import com.votingsystem.tsiro.animation.AnimationListener;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.helperClasses.CustomSpinnerItem;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.votingsystem.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 11/12/2015.
 */
public class RegisterFragment extends Fragment implements LAMVCView, View.OnFocusChangeListener, TextWatcher{
    private static final String debugTag = RegisterFragment.class.getSimpleName();
    private LinearLayout baseLlt;
    private TextView signInHereTtv, showHidePasswordTtv, passwordErrorTtv;
    private EditText usernameEdt, passwordEdt, confirmPasswordEdt, emailEdt, firmCodeEdt, onFocusChangeEditText, onTextWatcherEditText;
    private Button submitBtn;
    private ProgressView progressView;
    private Spinner pickFirmSpnr;
    private View view;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private BroadcastReceiver connectionStatusReceiver;
    private int connectionStatus, initialConnectionStatus;
    private LAMVCPresenterImpl LAMVCpresenterImpl;
    private Snackbar snackBar;
    private boolean firmsLoaded;
    private String registrationToken;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) this.commonElements    =   (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_register, container, false);
        baseLlt                     =   (LinearLayout) view.findViewById(R.id.baseLlt);
        usernameEdt                 =   (EditText) view.findViewById(R.id.usernameEdt);
        passwordEdt                 =   (EditText) view.findViewById(R.id.passwordEdt);
        confirmPasswordEdt          =   (EditText) view.findViewById(R.id.confirmPasswordEdt);
        emailEdt                    =   (EditText) view.findViewById(R.id.emailEdt);
        firmCodeEdt                 =   (EditText) view.findViewById(R.id.firmCodeEdt);
        signInHereTtv               =   (TextView) view.findViewById(R.id.signInHereTtv);
        showHidePasswordTtv         =   (TextView) view.findViewById(R.id.showHidePasswordTtv);
        passwordErrorTtv            =   (TextView) view.findViewById(R.id.passwordErrorTtv);
        submitBtn                   =   (Button) view.findViewById(R.id.submitBtn);
        progressView                =   (ProgressView) view.findViewById(R.id.progressView);
        pickFirmSpnr                =   (Spinner) view.findViewById(R.id.pickFirmSpnr);
        snackBar                    =   ((LoginActivity) getActivity()).getSnackBar();

        if (getArguments() != null) {
            initialConnectionStatus     =   getArguments().getInt(getResources().getString(R.string.connectivity_status));
            Log.e(debugTag, "initialConnectionStatus: "+initialConnectionStatus);
            registrationToken           =   getArguments().getString(getResources().getString(R.string.registration_token));
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ( savedInstanceState == null ) {
            firmsLoaded = false;
            if (snackBar != null && snackBar.isShown()) snackBar.dismiss();
            LAMVCpresenterImpl      =   new LAMVCPresenterImpl(this);

            LAMVCpresenterImpl.firmNamesSpnrActions(initialConnectionStatus);

            connectionStatus = initialConnectionStatus;
            if (isAdded() && isVisible()) initializeBroadcastReceivers();

            submitBtn.setTransformationMethod(null);
            setSignInHereSpan();

            usernameEdt.setOnFocusChangeListener(this);
            passwordEdt.setOnFocusChangeListener(this);
            confirmPasswordEdt.setOnFocusChangeListener(this);
            emailEdt.setOnFocusChangeListener(this);
            firmCodeEdt.setOnFocusChangeListener(this);

            usernameEdt.addTextChangedListener(this);
            passwordEdt.addTextChangedListener(this);
            confirmPasswordEdt.addTextChangedListener(this);
            emailEdt.addTextChangedListener(this);
            firmCodeEdt.addTextChangedListener(this);

            showHidePasswordTtv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LAMVCpresenterImpl.handleShowHidePasswordTtv(passwordEdt);
                    if (passwordErrorTtv !=null && !TextUtils.isEmpty(passwordErrorTtv.getText().toString())) passwordErrorTtv.setText(null);
                }
            });
            pickFirmSpnr.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner parent, View view, int position, long id) {
                    if (view instanceof TextView) ((TextView) view).setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
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
                    if (connectionStatus == AppConfig.NO_CONNECTION) {
                        showSnackBar(AppConfig.NO_CONNECTION);
                    } else {
                        progressView.start();
                        LAMVCpresenterImpl.validateForm(isAdded(), fillRegisterFormFields());
                    }
                }
            });
            signInHereTtv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof TextView && commonElements != null) commonElements.signInHereOnClick();
                }
            });
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(connectionStatusReceiver, new IntentFilter(getResources().getString(R.string.network_state_update)));
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
        if (this.LAMVCpresenterImpl != null) LAMVCpresenterImpl.onDestroy();
        this.commonElements = null;
        //RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        //refWatcher.watch(this);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim != 0) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            if (enter) return super.onCreateAnimation(transit, true, nextAnim);
            if (commonElements != null) animation.setAnimationListener(new AnimationListener(commonElements, null, getResources().getString(R.string.register_fgmt), ""));

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(animation);

            return animationSet;
        } else {
            return null;
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        View error_view_holder;
        if (!hasFocus) {
            switch (view.getId()) {
                case R.id.wrapped_usernameEdt           :   onFocusChangeEditText   =   usernameEdt;
                                                            break;
                case R.id.wrapped_passwordEdt           :   onFocusChangeEditText   =   passwordEdt;
                                                            break;
                case R.id.wrapped_confirmPasswordEdt    :   onFocusChangeEditText   =   confirmPasswordEdt;
                                                            break;
                case R.id.wrapped_emailEdt              :   onFocusChangeEditText   =   emailEdt;
                                                            break;
                case R.id.wrapped_firCodeEdt            :   onFocusChangeEditText   =   firmCodeEdt;
                                                            break;
            }
            if (TextUtils.isEmpty(onFocusChangeEditText.getText().toString())) {
                error_view_holder =  onFocusChangeEditText.equals(passwordEdt) ? passwordErrorTtv : onFocusChangeEditText;
                commonElements.setText(getResources().getString(R.string.error), error_view_holder, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.error_empty_requried_field))), getResources().getString(R.string.error_color_string));
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        View current_focus = getActivity().getWindow().getCurrentFocus();
        if (current_focus != null) {
            switch (current_focus.getId()) {
                case R.id.wrapped_usernameEdt           :   onTextWatcherEditText   =   usernameEdt;
                                                            break;
                case R.id.wrapped_passwordEdt           :   onTextWatcherEditText   =   passwordEdt;
                                                            break;
                case R.id.wrapped_confirmPasswordEdt    :   onTextWatcherEditText   =   confirmPasswordEdt;
                                                            break;
                case R.id.wrapped_emailEdt              :   onTextWatcherEditText   =   emailEdt;
                                                            break;
                case R.id.wrapped_firCodeEdt            :   onTextWatcherEditText   =   firmCodeEdt;
                                                            break;
            }
        }
        if (onTextWatcherEditText != null && onTextWatcherEditText.equals(passwordEdt)) {
            LAMVCpresenterImpl.handleInputFieldTextChanges(before, onTextWatcherEditText, passwordErrorTtv);
            LAMVCpresenterImpl.handleRegisterPasswordEdtTextChanges(start, before, onTextWatcherEditText, showHidePasswordTtv);
        } else {
            LAMVCpresenterImpl.handleInputFieldTextChanges(before, onTextWatcherEditText, null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {}
    /*
     *  LAMVCVIEW CALLBACKS
     *
     */
    @Override
    public void changeTransformationMethod(TransformationMethod transformationMethod, int action) {
        passwordEdt.setTransformationMethod(transformationMethod);
        animationStaff(showHidePasswordTtv, 0.0f, 1.0f, getResources().getString(R.string.visible));
        showHidePasswordTtv.setText(action);
        passwordEdt.setSelection(passwordEdt.getText().length());
    }

    @Override
    public void handlePasswordTextChanges(TextView showHidePasswordTtv, int action) {
        showHidePasswordTtv.setVisibility(View.VISIBLE);
        showHidePasswordTtv.setText(action);
    }

    @Override
    public void onSuccessfulFirmNamesSpnrLoad(List<CustomSpinnerItem> firmNameWithIDList, boolean firmsLoaded) {
        this.firmsLoaded = firmsLoaded;
        if (pickFirmSpnr.getAdapter() == null) {
            pickFirmSpnr.setAdapter(new FirmNamesSpnrNothingSelectedAdapter(getActivity(), R.layout.spinner_selection_item, firmNameWithIDList));
            setpickFirmSpnrDropDownViewRes(pickFirmSpnr);
        }
        pickFirmSpnr.setClickable(true);
    }

    @Override
    public void onFailureFirmNamesSpnrLoad(List<CustomSpinnerItem> firmNameWithIDList, boolean firmsLoaded) {
        this.firmsLoaded = firmsLoaded;
        pickFirmSpnr.setAdapter(new FirmNamesSpnrNothingSelectedAdapter(getActivity(), R.layout.spinner_selection_item, firmNameWithIDList));
        setpickFirmSpnrDropDownViewRes(pickFirmSpnr);
        pickFirmSpnr.setClickable(false);
    }

    @Override
    public void displayFeedbackMsg(int code) {
        if (progressView != null && progressView.isShown()) progressView.stop();
        showSnackBar(code);
    }

    @Override
    public void onFailure(int code, String tag, String hint, String retry_in) {
        EditText errorView = (EditText) baseLlt.findViewWithTag(tag);
        if (code == AppConfig.ERROR_EMPTY_REQUIRED_FIELD) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.empty_field, hint), null, code);
        } else if (code == AppConfig.ERROR_NO_FIRM_PICKED) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.empty_field, getResources().getString(R.string.pick_firm)), null, code);
        } else if (code == AppConfig.ERROR_INPUT_EXISTS) {
            if (tag.equals(getResources().getString(R.string.password_tag))) {
                passwordEdt.getChildAt(1).setVisibility(View.GONE);
                commonElements.showErrorContainerSnackbar(getResources().getString(R.string.correct_field, hint), passwordErrorTtv, code);
            } else {
                commonElements.showErrorContainerSnackbar(getResources().getString(R.string.correct_field, hint), errorView, code);
            }
        } else if (code == AppConfig.ERROR_INVALID_INPUT) {
            if (tag.equals(getResources().getString(R.string.password_tag))) {
                passwordEdt.getChildAt(1).setVisibility(View.GONE);
                commonElements.showErrorContainerSnackbar(getResources().getString(R.string.correct_field, hint), passwordErrorTtv, code);
            } else {
                commonElements.showErrorContainerSnackbar(getResources().getString(R.string.correct_field, hint), errorView, code);
            }
        } else if (code == AppConfig.ERROR_INVALID_PASSWORD_LENGTH) {
            passwordEdt.getChildAt(1).setVisibility(View.GONE);
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.correct_field, hint), passwordErrorTtv, code);
        } else if (code == AppConfig.ERROR_PASSWORD_MISMATCH) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.correct_error, getResources().getString(R.string.error_passwords_mismatch)), null, code);
        } else if (code == AppConfig.ERROR_INVALID_EMAIL) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.correct_field, hint), errorView, code);
        } else if (code == AppConfig.ERROR_FIRM_CODE_MISMATCH) {
            commonElements.showErrorContainerSnackbar(getResources().getString(R.string.correct_error, getResources().getString(R.string.error_firm_code_mismatch)), null, code);
        } else if (code == AppConfig.STATUS_ERROR) {
            showSnackBar(AppConfig.STATUS_ERROR);
        }
        if (progressView != null && progressView.isShown()) progressView.stop();
    }

    @Override
    public void onSuccess() {
        if (progressView != null && progressView.isShown()) progressView.stop();
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) commonElements.signInHereOnClick();
    }

    @Override
    public void onSuccessUserSignIn(int user_id, String username, String email, int firm_id) {}

    private void initializeBroadcastReceivers() {
        connectionStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isAdded()) {
                    connectionStatus = intent.getExtras().getInt(getResources().getString(R.string.connectivity_status));
                    LAMVCpresenterImpl.firmNamesSpnrActions(connectionStatus);
                }
            }
        };
    }

    private RegisterFormBody fillRegisterFormFields() {
        int firm_id;
        List<RegisterFormField> fields = new ArrayList<>();
        fields.add(new RegisterFormField(getResources().getString(R.string.username_tag), usernameEdt.getText().toString(), usernameEdt.getHint().toString()));
        fields.add(new RegisterFormField(getResources().getString(R.string.password_tag), passwordEdt.getText().toString(), passwordEdt.getHint().toString()));
        fields.add(new RegisterFormField(getResources().getString(R.string.confirm_password_tag), confirmPasswordEdt.getText().toString(), confirmPasswordEdt.getHint().toString()));
        fields.add(new RegisterFormField(getResources().getString(R.string.email_tag), emailEdt.getText().toString(), emailEdt.getHint().toString()));
        fields.add(new RegisterFormField(getResources().getString(R.string.firm_code_tag), firmCodeEdt.getText().toString(), firmCodeEdt.getHint().toString()));
        if (pickFirmSpnr.getSelectedItemPosition() != 0 && pickFirmSpnr != null && pickFirmSpnr.getAdapter() != null) {
            CustomSpinnerItem obj  =   (CustomSpinnerItem) pickFirmSpnr.getAdapter().getItem(pickFirmSpnr.getSelectedItemPosition() - 1);
            firm_id             =   obj.getId();
        } else {
            firm_id             =   0;
        }
        if (registrationToken == null || registrationToken.equals(getResources().getString(R.string.empty_string))) registrationToken = LoginActivity.getSessionPrefs(getActivity()).getString(getResources().getString(R.string.registration_token), getResources().getString(R.string.empty_string));
        return new RegisterFormBody(getResources().getString(R.string.register_user), fields, firm_id, registrationToken);
    }

    private void setSignInHereSpan(){ commonElements.setLoginActivitySpan(signInHereTtv, getResources().getString(R.string.signin_here), 22, 34, 1); }

    private static void animationStaff(View view, float fromAlpha, float toAlpha, String visibility) {
        if (visibility.equals("visible")) {
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
        if (progressView != null && progressView.isShown()) progressView.stop();
        commonElements.showSnackBar(code);
    }
}
