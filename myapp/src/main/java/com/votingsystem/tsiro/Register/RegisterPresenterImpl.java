package com.votingsystem.tsiro.Register;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.rey.material.widget.EditText;
import com.rey.material.widget.ProgressView;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import java.util.ArrayList;

/**
 * Created by user on 5/2/2016.
 */
public class RegisterPresenterImpl implements RegisterPresenter, RegisterInputFieldFinishedListener {

    private static final String debugTag = RegisterPresenterImpl.class.getSimpleName();
    private RegisterView registerView;
    private RegisterInteractorImpl registerInteractorImpl;
    private boolean firmsLoaded;

    public RegisterPresenterImpl(RegisterView registerView) {
        this.registerView = registerView;
        this.registerInteractorImpl = new RegisterInteractorImpl();
    }

    public void handleInputFieldTextChanges(int start, int before, EditText inputEdt, RelativeLayout acceptRlt, EditText auxEdt, String tag) {
        if ( registerView != null ) {
            if (tag.equals("username") || tag.equals("email")) {
                if (acceptRlt.getVisibility() == View.VISIBLE)
                    registerView.clearEditextHelpersAndSuccessIcon("clearSuccessIcon", acceptRlt, null);
            } else if (tag.equals("confirmpassword")) {
                if (start >= 2 && !inputEdt.getText().toString().equals(auxEdt.getText().toString())) {
                    registerView.setInputFieldError(AppConfig.ERROR_PASSWORD_MISMATCH, inputEdt);
                    return;
                } else if (start >= 2 && inputEdt.getText().toString().equals(auxEdt.getText().toString()) && inputEdt.getHelper() != null) {
                    registerView.clearEditextHelpersAndSuccessIcon("clearHelper", null, inputEdt);
                }
            }
            if ((before == 1 || before == 0) && inputEdt.getHelper() != null)
                registerView.clearEditextHelpersAndSuccessIcon("clearHelper", null, inputEdt);
        }
    }

    public void validateInputFieldOnFocusChange(RegisterPresenterParamsObj registerPresenterParamsObj){
        if (registerPresenterParamsObj.getConnectionStatus() == AppConfig.NO_CONNECTION) {
            if (registerPresenterParamsObj.isAdded() && registerView != null) registerView.setToastMsg(AppConfig.NO_CONNECTION);
        } else {
            registerInteractorImpl.validateInputField(registerPresenterParamsObj, this);
        }
    }

    public void validateFirmCode(int firmId, String code) {
        Log.e(debugTag,firmId+"");
        registerInteractorImpl.validateFirmCode(this);
    }

    public void handleShowHidePasswordTtv(EditText registerPasswordEdt) {
        if ( !registerPasswordEdt.getText().toString().isEmpty() && registerView != null ) {
            if (registerPasswordEdt.getTransformationMethod() instanceof PasswordTransformationMethod) {
                //use HideReturnsTransformationMethod to make password visible
                registerView.changeTransformationMethod(HideReturnsTransformationMethod.getInstance(),"ΑΠΟΚΡΥΨΗ");
            } else if (registerPasswordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                registerView.changeTransformationMethod(PasswordTransformationMethod.getInstance(), "ΕΜΦΑΝΙΣΗ");
            }
        }
    }

    public void handleRegisterPasswordEdtTextChanges(int start, int before, EditText registerPasswordEdt, RelativeLayout acceptPasswordRlt, TextView showHidePasswordTtv) {
        if ( registerView != null ) {
            if (acceptPasswordRlt.getVisibility() == View.VISIBLE)
                registerView.clearEditextHelpersAndSuccessIcon("clearSuccessIcon", acceptPasswordRlt, null);
            if (start >= 0 && registerPasswordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                registerView.handlePasswordTextChanges("ΑΠΟΚΡΥΨΗ", showHidePasswordTtv);
            } else if (start >= 0 && registerPasswordEdt.getTransformationMethod() instanceof PasswordTransformationMethod) {
                registerView.handlePasswordTextChanges("ΕΜΦΑΝΙΣΗ", showHidePasswordTtv);
            }
            if (start == 0 && before == 1)
                registerView.handlePasswordTextChanges(null, showHidePasswordTtv);
        }
    }

    public void getFirmNamesToPopulateSpnr(int connectionStatus) {
        if (connectionStatus == AppConfig.NO_CONNECTION) {
            if (!firmsLoaded && registerView != null) registerView.onFailure();
        } else {
            if (!firmsLoaded) registerInteractorImpl.populateFirmNamesSpnr(new ArrayList<FirmNameWithID>(), this);
        }
    }

    @Override
    public void startProgressLoader(ProgressView inputFieldPrgv) { if ( registerView != null ) registerView.showFieldValidationProgress(inputFieldPrgv); }

    @Override
    public void hideProgressLoader(ProgressView inputFieldPrgv) { if ( registerView != null ) registerView.hideFieldValidationProgress(inputFieldPrgv); }

    @Override
    public void showToastMsg(int code) { if ( registerView != null ) registerView.setToastMsg(code); }

    @Override
    public void onInputFieldError(int code, View view) { if ( registerView != null ) registerView.setInputFieldError(code, view); }

    @Override
    public void onSuccess(RelativeLayout validInputRlt, String tag) { if ( registerView != null ) registerView.onSuccess(validInputRlt, tag); }

    @Override
    public void onSuccessfirmNamesSpnrLoad(ArrayList<FirmNameWithID> firmNameWithIDArrayList) {
        if ( registerView != null ) {
            registerView.onSuccessfulFirmNamesSpnrLoad(firmNameWithIDArrayList);
            firmsLoaded = true;
        }
    }

    @Override
    public void onFailurefirmNamesSpnrLoad() { if ( registerView != null ) registerView.onFailure(); }

    @Override
    public void onDestroy() {
        registerView = null;
    }
}
