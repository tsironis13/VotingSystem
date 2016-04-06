package com.votingsystem.tsiro.Register;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

import com.rey.material.widget.LinearLayout;
import com.rey.material.widget.RelativeLayout;
import com.rey.material.widget.EditText;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.POJO.RegisterFormBody;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 5/2/2016.
 */
public class RegisterPresenterImpl implements RegisterPresenter, RegisterFormFinishedListener {

    private static final String debugTag = RegisterPresenterImpl.class.getSimpleName();
    private RegisterView registerView;
    private RegisterInteractorImpl registerInteractorImpl;
    private boolean firmsLoaded;
    private ArrayList<FirmNameWithID> firmNameWithIDArrayList = new ArrayList<>();

    public RegisterPresenterImpl(RegisterView registerView) {
        this.registerView = registerView;
        this.registerInteractorImpl = new RegisterInteractorImpl();
    }

    public void handleInputFieldTextChanges(int before, EditText inputEdt, View errorView) {
        if ( registerView != null ) {
            if (before == 1 || before == 0) {
                if (errorView != null && errorView instanceof TextView && !TextUtils.isEmpty(((TextView) errorView).getText().toString())) {
                    ((TextView) errorView).setText(null);
                } else if (errorView == null && inputEdt.getHelper() != null && !TextUtils.isEmpty(inputEdt.getHelper().toString())) {
                    inputEdt.setHelper(null);
                }
            }
        }
    }

    public void handleShowHidePasswordTtv(EditText registerPasswordEdt) {
        if ( !registerPasswordEdt.getText().toString().isEmpty() && registerView != null ) {
            if (registerPasswordEdt.getTransformationMethod() instanceof PasswordTransformationMethod) {
                //use HideReturnsTransformationMethod to make password visible
                registerView.changeTransformationMethod(HideReturnsTransformationMethod.getInstance(), R.string.hide);
            } else if (registerPasswordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                registerView.changeTransformationMethod(PasswordTransformationMethod.getInstance(), R.string.show);
            }
        }
    }

    public void handleRegisterPasswordEdtTextChanges(int start, int before, EditText passwordEdt, TextView showHidePasswordTtv) {
        if (registerView != null) {
            if (start >= 0 && passwordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                registerView.handlePasswordTextChanges(showHidePasswordTtv, R.string.hide);
            } else if (start >= 0 && passwordEdt.getTransformationMethod() instanceof PasswordTransformationMethod) {
                registerView.handlePasswordTextChanges(showHidePasswordTtv, R.string.show);
            }
            if (passwordEdt.getChildAt(1).getVisibility() == View.GONE)  passwordEdt.getChildAt(1).setVisibility(View.VISIBLE);
            if (start == 0 && before == 1) registerView.handlePasswordTextChanges(showHidePasswordTtv, R.string.empty_string);
        }
    }

    public void firmNamesSpnrActions(int connectionStatus) {
        if (connectionStatus == AppConfig.NO_CONNECTION) {
            if (!firmsLoaded && registerView != null) registerView.onFailureFirmNamesSpnrLoad(firmNameWithIDArrayList, false);
        } else {
            if (!firmsLoaded) registerInteractorImpl.populateFirmNamesSpnr(firmNameWithIDArrayList, this);
        }
    }

    public void validateForm(int connectionStatus, boolean isAdded, RegisterFormBody registerFormBody, String token){
        if (connectionStatus != AppConfig.NO_CONNECTION) registerInteractorImpl.validateForm(registerFormBody, isAdded, this, token);
    }
    
    @Override
    public void displayFeedbackMsg(int code) { if ( registerView != null ) registerView.displayFeedbackMsg(code); }

    @Override
    public void onSuccessfirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList) {
        if (registerView != null) {
            registerView.onSuccessfulFirmNamesSpnrLoad(firmNameWithIDArrayList, true);
            firmsLoaded = true;
        }
    }

    @Override
    public void onFailurefirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList) { if ( registerView != null ) registerView.onFailureFirmNamesSpnrLoad(firmNameWithIDArrayList, false); }

    @Override
    public void onFormValidationFailure(int code, String field, String hint) {
        if (registerView != null) registerView.onFormValidationFailure(code, field, hint);
    }

    @Override
    public void onFormValidationSuccess() {
        if (registerView != null) registerView.onFormValidationSuccess();
    }

    @Override
    public void onDestroy() {
        registerView = null;
    }
}
