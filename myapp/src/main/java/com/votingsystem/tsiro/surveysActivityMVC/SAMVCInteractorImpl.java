package com.votingsystem.tsiro.surveysActivityMVC;

import android.util.Log;

import com.votingsystem.tsiro.POJO.AllSurveys;
import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.POJO.SurveyDetails;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.rest.ApiService;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by giannis on 20/6/2016.
 */
class SAMVCInteractorImpl implements SAMVCInteractor {

    private static final String debugTag = SAMVCInteractorImpl.class.getSimpleName();
    private ApiService apiService;

    SAMVCInteractorImpl() { apiService = RetrofitSingleton.getInstance().getApiService(); }

    @Override
    public void getAllSurveys(final AllSurveysBody allSurveysBody, final SAMVCFinishedListener SAMVCfinishedListener) {
        Call<AllSurveys> allSurveys = apiService.getAllSurveys(allSurveysBody);
        allSurveys.enqueue(new Callback<AllSurveys>() {
            @Override
            public void onResponse(Response<AllSurveys> response, Retrofit retrofit) {
//                Log.e(debugTag, response.body()+"");
                if (response.body() != null) {
//                    Log.e(debugTag, response.body().getCode()+"");
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        SAMVCfinishedListener.onFailure(response.body().getCode(), 1);
                    } else {
                        SAMVCfinishedListener.onSuccessSurveysFetched(response.body().getData(), allSurveysBody.getPage(), response.body().getTotal());
                    }
                } else {
                    SAMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 1);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(debugTag, t.toString());
                if (t instanceof IOException) {
                    SAMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 1);
                } else {
                    SAMVCfinishedListener.onFailure(AppConfig.INTERNAL_ERROR, 1);
                }
            }
        });
    }

    @Override
    public void getSurveyStats(SurveyAnswersBody surveyAnswersBody, final SAMVCFinishedListener SAMVCfinishedListener) {
        Call<SurveyDetails> surveyDetails = apiService.uploadSurveyAnswersOrGetSurveyStats(surveyAnswersBody);
        surveyDetails.enqueue(new Callback<SurveyDetails>() {
            @Override
            public void onResponse(Response<SurveyDetails> response, Retrofit retrofit) {
                if (response.body() != null) {
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        SAMVCfinishedListener.onFailure(response.body().getCode(), 2);
                    } else {
                        SAMVCfinishedListener.onSuccessSurveyDetailsFetched(response.body().getData());
                    }
                } else {
                    SAMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 2);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(debugTag, t.toString());
                if (t instanceof IOException) {
                    SAMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 2);
                } else {
                    SAMVCfinishedListener.onFailure(AppConfig.INTERNAL_ERROR, 2);
                }
            }
        });
    }


}
