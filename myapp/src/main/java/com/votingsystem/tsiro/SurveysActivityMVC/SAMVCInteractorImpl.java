package com.votingsystem.tsiro.SurveysActivityMVC;

import android.util.Log;

import com.votingsystem.tsiro.POJO.AllSurveys;
import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.rest.ApiService;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by giannis on 20/6/2016.
 */
public class SAMVCInteractorImpl implements SAMVCInteractor {

    private static final String debugTag = SAMVCInteractorImpl.class.getSimpleName();
    private ApiService apiService;

    public SAMVCInteractorImpl() { apiService = RetrofitSingleton.getInstance().getApiService(); }

    @Override
    public void getAllSurveys(final AllSurveysBody allSurveysBody, final SAMVCFinishedListener SAMVCfinishedListener) {
        Call<AllSurveys> allSurveys = apiService.getAllSurveys(allSurveysBody);
        allSurveys.enqueue(new Callback<AllSurveys>() {
            @Override
            public void onResponse(Response<AllSurveys> response, Retrofit retrofit) {
                if (response.body().getCode() != AppConfig.STATUS_OK) {
                    SAMVCfinishedListener.onFailure(response.body().getCode());
                } else {
                    SAMVCfinishedListener.onSuccessSurveysFetched(response.body().getData(), allSurveysBody.getOffset());
                }
                //Log.e(debugTag, response.body().getCode()+"");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(debugTag, t.toString());
            }
        });
    }
}
