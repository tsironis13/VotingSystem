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
    public void validateForm(RegisterFormBody registerFormFieldsBody, final boolean isAdded, final RegisterFormFinishedListener registerFormFinishedListener) {
        call = apiService.registerUser(registerFormFieldsBody);
        call.enqueue(new Callback<UserConnectionStaff>() {
            @Override
            public void onResponse(Response<UserConnectionStaff> response, Retrofit retrofit) {
                if (isAdded) {
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        registerFormFinishedListener.onFormValidationFailure(response.body().getCode(), response.body().getTag(), response.body().getHint());
                    } else {
                        registerFormFinishedListener.onFormValidationSuccess();
                    }
                    Log.e(debugTag, "Code: "+response.body().getCode());
                }
            }
            @Override
            public void onFailure(Throwable t) {
                if (isAdded) {
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
