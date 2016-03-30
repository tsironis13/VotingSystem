package com.votingsystem.tsiro.Register;

import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.rey.material.widget.ProgressView;
import com.votingsystem.tsiro.POJO.Firm;
import com.votingsystem.tsiro.POJO.RegisterFormBody;
import com.votingsystem.tsiro.POJO.UserConnectionStaff;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import com.votingsystem.tsiro.rest.ApiService;
import com.votingsystem.tsiro.votingsystem.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by user on 5/2/2016.
 */
public class RegisterInteractorImpl implements RegisterInteractor {

    private static final String debugTag = RegisterInteractorImpl.class.getSimpleName();
    private ApiService apiService;
    private Call<UserConnectionStaff> call;

    public RegisterInteractorImpl() {
        apiService = RetrofitSingleton.getInstance().getApiService();
    }

    @Override
    public void validateForm(RegisterFormBody registerFormFieldsBody, final RegisterPresenterParamsObj registerPresenterParamsObj, final RegisterFormFinishedListener registerFormFinishedListener) {
        call = apiService.registerUser(registerFormFieldsBody);
        call.enqueue(new Callback<UserConnectionStaff>() {
            @Override
            public void onResponse(Response<UserConnectionStaff> response, Retrofit retrofit) {
                if (registerPresenterParamsObj.isAdded()) {
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        registerFormFinishedListener.onFormValidationFailure(response.body().getCode(), response.body().getTag(), response.body().getHint());
                    } else {
                        registerFormFinishedListener.onFormValidationSuccess();
                    }
                    Log.e(debugTag, "Code: "+response.body().getCode());
                    //Log.e(debugTag, "Data: "+response.body().getData());
                    //Log.e(debugTag, "Error code: "+response.body().getError_code());
                    //Log.e(debugTag, response.body().getData().size()+"");
                    //Log.e(debugTag, response.body().getKa());
                    //for (int i = 0; i < response.body().getData().size(); i++) {
                    //    Log.e(debugTag, "Tag: "+response.body().getData().get(i).getTag()+" Code: "+response.body().getData().get(i).getCode());
                    //}
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (registerPresenterParamsObj.isAdded()) {
                    if (t instanceof IOException) {
                        registerFormFinishedListener.displayFeedbackMsg(AppConfig.UNAVAILABLE_SERVICE);
                    } else {
                        registerFormFinishedListener.displayFeedbackMsg(AppConfig.INTERNAL_ERROR);
                    }
                }
                Log.e(debugTag, t.toString());
            }
        });
    }

    @Override
    public void validateInputField(final RegisterPresenterParamsObj registerPresenterParamsObj, final RegisterFormFinishedListener registerFormFinishedListener) {
            /*if ( registerPresenterParamsObj.getTag().equals("password") && registerPresenterParamsObj.getInputEditText() != null && registerPresenterParamsObj.getInputEditText().getText().length() != 8 ) {
                registerInputFieldFinishedListener.onInputFieldError(AppConfig.ERROR_INVALID_PASSWORD_LENGTH, registerPresenterParamsObj.getErrorView());
            } else {
                if (!registerPresenterParamsObj.getTag().equals("password")) registerInputFieldFinishedListener.startProgressLoader(registerPresenterParamsObj.getInputFieldProgressView());
                if (registerPresenterParamsObj.getInputEditText() != null) call = apiService.isInputFieldValid(registerPresenterParamsObj.getRetrofitAction(), registerPresenterParamsObj.getInputEditText().getText().toString());
                call.enqueue(new Callback<UserConnectionStaff>() {
                    @Override
                    public void onResponse(Response<UserConnectionStaff> response, Retrofit retrofit) {
                        if ( registerPresenterParamsObj.isAdded() ) {
                            if (response.body().getError_code() != AppConfig.INPUT_OK) {
                                Log.e(debugTag, response.body().getError_code()+"");
                                registerInputFieldFinishedListener.onInputFieldError(response.body().getError_code(), registerPresenterParamsObj.getErrorView());
                            } else {
                                registerInputFieldFinishedListener.onSuccess(registerPresenterParamsObj.getValidInputRlt(), registerPresenterParamsObj.getTag());
                            }
                            if (!registerPresenterParamsObj.getTag().equals("password")) registerInputFieldFinishedListener.hideProgressLoader(registerPresenterParamsObj.getInputFieldProgressView());
                        }
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        if (registerPresenterParamsObj.isAdded()) {
                            if (t instanceof IOException) {
                                registerInputFieldFinishedListener.displayFeedbackMsg(AppConfig.UNAVAILABLE_SERVICE);
                            } else {
                                registerInputFieldFinishedListener.displayFeedbackMsg(AppConfig.INTERNAL_ERROR);
                            }
                        }
                        if (!registerPresenterParamsObj.getTag().equals("password")) registerInputFieldFinishedListener.hideProgressLoader(registerPresenterParamsObj.getInputFieldProgressView());
                    }
                });
            }*/
        }

    @Override
    public void populateFirmNamesSpnr(final ArrayList<FirmNameWithID> firmNameWithIDArrayList, final RegisterFormFinishedListener registerFormFinishedListener) {
        Call<Firm> call = apiService.getFirmNames("getFirmNames");
        call.enqueue(new Callback<Firm>() {
            @Override
            public void onResponse(Response<Firm> response, Retrofit retrofit) {
                if (response.body().getError() == AppConfig.STATUS_OK) {
                    List<Firm.FirmElement> firmElementList = response.body().getFirm_element();
                    for (int i = 0; i < firmElementList.size(); i++) {
                        Log.d(debugTag, "firm_id: " + firmElementList.get(i).getFirm_id() + " firm_name: " + firmElementList.get(i).getFirm_name());
                        firmNameWithIDArrayList.add(new FirmNameWithID(firmElementList.get(i).getFirm_name(), firmElementList.get(i).getFirm_id()));
                    }
                    registerFormFinishedListener.onSuccessfirmNamesSpnrLoad(firmNameWithIDArrayList);
                }
            }
            @Override
            public void onFailure(Throwable t) {
                registerFormFinishedListener.onFailurefirmNamesSpnrLoad(firmNameWithIDArrayList);
            }
        });
    }
}
