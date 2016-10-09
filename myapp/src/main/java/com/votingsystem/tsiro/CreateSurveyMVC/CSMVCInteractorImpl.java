package com.votingsystem.tsiro.CreateSurveyMVC;

import android.util.Log;

import com.votingsystem.tsiro.POJO.AllSurveys;
import com.votingsystem.tsiro.POJO.NewSurvey;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.rest.ApiService;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by giannis on 3/10/2016.
 */

class CSMVCInteractorImpl implements CSMVCInteractor {

    private static final String debugTag = CSMVCInteractorImpl.class.getSimpleName();
    private ApiService apiService;

    CSMVCInteractorImpl() { this.apiService = RetrofitSingleton.getInstance().getApiService(); }

    @Override
    public void uploadNewUserSurvey(NewSurvey newSurvey, final CSMVCFinishedListener CSMVCfinishedListener) {
        //AllSurveys POJO is used for convenience,
        // no need to create a POJO class for specific response body mapping
        Call<AllSurveys> call = apiService.uploadNewUserSurvey(newSurvey);
        call.enqueue(new Callback<AllSurveys>() {
            @Override
            public void onResponse(Response<AllSurveys> response, Retrofit retrofit) {
                if (response.body() != null) {
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        CSMVCfinishedListener.onFailure(response.body().getCode());
                    } else {
                        CSMVCfinishedListener.onSuccess();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(debugTag, t.toString());
                if (t instanceof IOException) {
                    CSMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE);
                } else {
                    CSMVCfinishedListener.onFailure(AppConfig.INTERNAL_ERROR);
                }
            }
        });
    }
}
