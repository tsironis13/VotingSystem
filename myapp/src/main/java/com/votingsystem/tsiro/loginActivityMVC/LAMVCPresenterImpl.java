package com.votingsystem.tsiro.loginActivityMVC;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.rey.material.widget.EditText;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.POJO.LoginFormBody;
import com.votingsystem.tsiro.POJO.RegisterFormBody;
import com.votingsystem.tsiro.POJO.ResetPassowrdBody;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.helperClasses.CustomSpinnerItem;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 5/2/2016.
 */
public class LAMVCPresenterImpl implements LAMVCPresenter, LAMVCFinishedListener {

    private static final String debugTag = LAMVCPresenterImpl.class.getSimpleName();
    private LAMVCView LAMVCview;
    private LAMVCInteractorImpl LAMVCinteractorImpl;
    private boolean firmsLoaded;
    private ArrayList<CustomSpinnerItem> firmNameWithIDArrayList = new ArrayList<>();

    public LAMVCPresenterImpl(LAMVCView LAMVCview) {
        this.LAMVCview              =   LAMVCview;
        this.LAMVCinteractorImpl    =   new LAMVCInteractorImpl();
    }

    public void handleInputFieldTextChanges(int before, EditText inputEdt, View errorView) {
        if (LAMVCview != null) {
            if (before == 1 || before == 0) {
                if (errorView != null && errorView instanceof TextView && !TextUtils.isEmpty(((TextView) errorView).getText().toString())) {
                    ((TextView) errorView).setText(null);
                } else if (errorView == null && inputEdt != null && inputEdt.getHelper() != null && !TextUtils.isEmpty(inputEdt.getHelper().toString())) {
                    inputEdt.setHelper(null);
                }
            }
        }
    }

    public void handleShowHidePasswordTtv(EditText registerPasswordEdt) {
        if ( !registerPasswordEdt.getText().toString().isEmpty() && LAMVCview != null ) {
            if (registerPasswordEdt.getTransformationMethod() instanceof PasswordTransformationMethod) {
                //use HideReturnsTransformationMethod to make password visible
                LAMVCview.changeTransformationMethod(HideReturnsTransformationMethod.getInstance(), R.string.hide);
            } else if (registerPasswordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                LAMVCview.changeTransformationMethod(PasswordTransformationMethod.getInstance(), R.string.show);
            }
        }
    }

    public void handleRegisterPasswordEdtTextChanges(int start, int before, EditText passwordEdt, TextView showHidePasswordTtv) {
        if (LAMVCview != null) {
            if (start >= 0 && passwordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                LAMVCview.handlePasswordTextChanges(showHidePasswordTtv, R.string.hide);
            } else if (start >= 0 && passwordEdt.getTransformationMethod() instanceof PasswordTransformationMethod) {
                LAMVCview.handlePasswordTextChanges(showHidePasswordTtv, R.string.show);
            }
            if (passwordEdt.getChildAt(1).getVisibility() == View.GONE)  passwordEdt.getChildAt(1).setVisibility(View.VISIBLE);
            if (start == 0 && before == 1) LAMVCview.handlePasswordTextChanges(showHidePasswordTtv, R.string.empty_string);
        }
    }

    public void firmNamesSpnrActions(int connectionStatus) {
        if (connectionStatus == AppConfig.NO_CONNECTION) {
            if (!firmsLoaded && LAMVCview != null) LAMVCview.onFailureFirmNamesSpnrLoad(firmNameWithIDArrayList, false);
        } else {
            if (!firmsLoaded) LAMVCinteractorImpl.populateFirmNamesSpnr(firmNameWithIDArrayList, this);
        }
    }

    public void validateForm(boolean isAdded, RegisterFormBody registerFormBody){
        LAMVCinteractorImpl.validateForm(registerFormBody, isAdded, this);
    }

    public void resetPassword(boolean isAdded, ResetPassowrdBody resetPassowrdBody) {
        LAMVCinteractorImpl.resetPassword(resetPassowrdBody, isAdded, this);
    }

    public void loginUser(boolean isAdded, LoginFormBody loginFormBody) {
        LAMVCinteractorImpl.loginUser(loginFormBody, isAdded, this);
    }

    @Override
    public void displayFeedbackMsg(int code) { if ( LAMVCview != null ) LAMVCview.displayFeedbackMsg(code); }

    @Override
    public void onSuccessfirmNamesSpnrLoad(List<CustomSpinnerItem> firmNameWithIDArrayList) {
        if (LAMVCview != null) {
            LAMVCview.onSuccessfulFirmNamesSpnrLoad(firmNameWithIDArrayList, true);
            firmsLoaded = true;
        }
    }

    @Override
    public void onFailurefirmNamesSpnrLoad(List<CustomSpinnerItem> firmNameWithIDArrayList) { if ( LAMVCview != null ) LAMVCview.onFailureFirmNamesSpnrLoad(firmNameWithIDArrayList, false); }

    @Override
    public void onFailure(int code, String field, String hint, String retry_in) {
        if (LAMVCview != null) LAMVCview.onFailure(code, field, hint, retry_in);
    }

    @Override
    public void onSuccess() {
        if (LAMVCview != null) LAMVCview.onSuccess();
    }

    @Override
    public void onSuccessUserSignIn(int user_id, String username, String email, int firm_id) {
        if (LAMVCview != null) LAMVCview.onSuccessUserSignIn(user_id, username, email, firm_id);
    }

    @Override
    public void onDestroy() {
        LAMVCview = null;
    }
}
