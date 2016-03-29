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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 5/2/2016.
 */
public class RegisterPresenterImpl implements RegisterPresenter, RegisterInputFieldFinishedListener {

    private static final String debugTag = RegisterPresenterImpl.class.getSimpleName();
    private RegisterView registerView;
    private RegisterInteractorImpl registerInteractorImpl;
    private boolean firmsLoaded;
    private ArrayList<FirmNameWithID> firmNameWithIDArrayList = new ArrayList<>();

    public RegisterPresenterImpl(RegisterView registerView) {
        this.registerView = registerView;
        this.registerInteractorImpl = new RegisterInteractorImpl();
    }

    public void handleInputFieldTextChanges(int start, int before, EditText inputEdt, RelativeLayout acceptRlt, EditText auxEdt, String tag) {
        if ( registerView != null ) {
            if (tag.equals("username") || tag.equals("email")) {
                if (acceptRlt.getVisibility() == View.VISIBLE)
                    registerView.clearEditextHelpersAndSuccessIcon("clearSuccessIcon", acceptRlt, null);
            } /*else if (tag.equals("confirmpassword")) {
                if (start >= 2 && !inputEdt.getText().toString().equals(auxEdt.getText().toString())) {
                    registerView.onFailure(AppConfig.ERROR_PASSWORD_MISMATCH, inputEdt);
                    return;
                } else if (start >= 2 && inputEdt.getText().toString().equals(auxEdt.getText().toString()) && inputEdt.getHelper() != null) {
                    registerView.clearEditextHelpersAndSuccessIcon("clearHelper", null, inputEdt);
                }
            }*/
            if ((before == 1 || before == 0) && inputEdt.getHelper() != null)
                registerView.clearEditextHelpersAndSuccessIcon("clearHelper", null, inputEdt);
        }
    }

    public void validateInputFieldOnFocusChange(RegisterPresenterParamsObj registerPresenterParamsObj){
        if (registerPresenterParamsObj.getConnectionStatus() != AppConfig.NO_CONNECTION) registerInteractorImpl.validateInputField(registerPresenterParamsObj, this);
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

    public void firmNamesSpnrActions(int connectionStatus) {
        if (connectionStatus == AppConfig.NO_CONNECTION) {
            if (!firmsLoaded && registerView != null) registerView.onFailureFirmNamesSpnrLoad(firmNameWithIDArrayList, false);
        } else {
            if (!firmsLoaded) registerInteractorImpl.populateFirmNamesSpnr(firmNameWithIDArrayList, this);
        }
    }

    public void emptyFieldsValidation(LinearLayout baseLlt) {
        boolean empty_field = false;
        for (int i = 0; i < baseLlt.getChildCount(); i++) {
            View formChild = baseLlt.getChildAt(i);
            if (formChild instanceof LinearLayout) {
                for (int j = 0; j < ((LinearLayout) formChild).getChildCount(); j++) {
                    View containerChild = ((LinearLayout) formChild).getChildAt(j);
                    if (containerChild instanceof RelativeLayout) {
                        for (int k = 0; k < ((RelativeLayout) containerChild).getChildCount(); k++) {
                            View innerContainerChild = ((RelativeLayout) containerChild).getChildAt(k);
                            if (innerContainerChild instanceof EditText && innerContainerChild.getTag().equals("required")) {
                                if (TextUtils.isEmpty(((EditText) innerContainerChild).getText())) {
                                    registerView.onEmptyFieldsValidationFailure(((EditText) innerContainerChild).getHint().toString(), "empty_fields");
                                    empty_field = true;
                                    return;
                                }
                            }
                        }
                    } else if (containerChild instanceof EditText) {
                        if (TextUtils.isEmpty(((EditText) containerChild).getText())) {
                            registerView.onEmptyFieldsValidationFailure(((EditText) containerChild).getHint().toString(), "empty_fields");
                            empty_field = true;
                            return;
                        }
                    } else if (containerChild instanceof Spinner) {
                        if (((Spinner) containerChild).getSelectedItemPosition() == 0) {
                            empty_field = true;
                            registerView.onEmptyFieldsValidationFailure("Επιλογή Εταιρείας", "empty_fields");
                        }
                    }
                }
            }
        }
        if (!empty_field) registerView.onEmptyFieldsValidationSuccess();
    }

    public void validateForm(RegisterFormBody registerFormBody, RegisterPresenterParamsObj registerPresenterParamsObj){
        if (registerPresenterParamsObj.getConnectionStatus() != AppConfig.NO_CONNECTION) registerInteractorImpl.validateForm(registerFormBody, registerPresenterParamsObj);

        //for (Map.Entry<String, Boolean> cursor : inputValidityMap.entrySet()) {
        //    Log.e(debugTag, "Key: "+cursor.getKey()+" Value: "+ cursor.getValue());
        //}
    }

    @Override
    public void startProgressLoader(ProgressView inputFieldPrgv) { if ( registerView != null ) registerView.showFieldValidationProgress(inputFieldPrgv); }

    @Override
    public void hideProgressLoader(ProgressView inputFieldPrgv) { if ( registerView != null ) registerView.hideFieldValidationProgress(inputFieldPrgv); }

    @Override
    public void displayFeedbackMsg(int code) { if ( registerView != null ) registerView.displayFeedbackMsg(code); }

    @Override
    public void onInputFieldError(int code, View view) { if ( registerView != null ) registerView.onFailure(code, view); }

    @Override
    public void onSuccess(RelativeLayout validInputRlt, String tag) { if ( registerView != null ) registerView.onSuccess(validInputRlt, tag); }

    @Override
    public void onSuccessfirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList) {
        if ( registerView != null ) {
            registerView.onSuccessfulFirmNamesSpnrLoad(firmNameWithIDArrayList, true);
            firmsLoaded = true;
        }
    }

    @Override
    public void onFailurefirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList) { if ( registerView != null ) registerView.onFailureFirmNamesSpnrLoad(firmNameWithIDArrayList, false); }

    @Override
    public void onDestroy() {
        registerView = null;
    }
}
