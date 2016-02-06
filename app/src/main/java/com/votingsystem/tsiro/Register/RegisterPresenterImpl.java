package com.votingsystem.tsiro.Register;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.rey.material.widget.EditText;
import com.rey.material.widget.ProgressView;
import com.votingsystem.tsiro.ObserverPattern.ConnectivityObserver;
import com.votingsystem.tsiro.POJO.UserConnectionStaff;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import com.votingsystem.tsiro.rest.ApiService;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

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

    public void handleInputFieldTextChanges(int start, int before, EditText inputEdt, RelativeLayout acceptRlt, int visibility) {
        if ( visibility == View.VISIBLE ) registerView.clearEditextHelpersAndSuccessIcon("clearSuccessIcon", acceptRlt, null);
        if ( ( before == 1 || before == 0 ) && inputEdt.getHelper() != null ) registerView.clearEditextHelpersAndSuccessIcon("clearHelper", null, inputEdt);
    }

    public void validateInputFieldOnFocusChange(RegisterPresenterParamsObj registerPresenterParamsObj){
        if ( registerPresenterParamsObj.getConnectionStatus() == AppConfig.NO_CONNECTION ) {
            if ( registerPresenterParamsObj.isAdded() && registerView != null ) registerView.setToastMsg(AppConfig.NO_CONNECTION);
        } else {
            registerInteractorImpl.validateInputField(registerPresenterParamsObj, this);
        }
    }

    public void getFirmNamesToPopulateSpnr(int connectionStatus) {
        if ( connectionStatus == AppConfig.NO_CONNECTION ) {
            if ( !firmsLoaded ) registerView.onFailure();
        } else {
            if ( !firmsLoaded ) registerInteractorImpl.populateFirmNamesSpnr(new ArrayList<FirmNameWithID>(), this);
        }
    }

    @Override
    public void startProgressLoader(ProgressView inputFieldPrgv) {
        registerView.showFieldValidationProgress(inputFieldPrgv);
    }

    @Override
    public void hideProgressLoader(ProgressView inputFieldPrgv) {
        registerView.hideFieldValidationProgress(inputFieldPrgv);
    }

    @Override
    public void showToastMsg(int code) {
        registerView.setToastMsg(code);
    }

    @Override
    public void onInputFieldError(int code, View view) {
        registerView.setInputFieldError(code, view);
    }

    @Override
    public void onSuccess(RelativeLayout validInputRlt, String tag) {
        registerView.onSuccess(validInputRlt, tag);
    }

    @Override
    public void onSuccessfirmNamesSpnrLoad(ArrayList<FirmNameWithID> firmNameWithIDArrayList) {
        registerView.onSuccessfulFirmNamesSpnrLoad(firmNameWithIDArrayList);
        firmsLoaded = true;
    }

    @Override
    public void onFailurefirmNamesSpnrLoad() {
        registerView.onFailure();
    }

    @Override
    public void onDestroy() {
        registerView = null;
    }
}
