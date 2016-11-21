package com.votingsystem.tsiro.userSurveysActivityMVC;

import android.util.Log;

import com.votingsystem.tsiro.POJO.AllSurveys;
import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.POJO.NewSurvey;
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
 * Created by giannis on 12/11/2016.
 */

class USAMVCInteractorImpl implements USAMVCInteractor {

    private static String debugTag = USAMVCInteractorImpl.class.getSimpleName();
    private ApiService apiService;

    USAMVCInteractorImpl() { apiService = RetrofitSingleton.getInstance().getApiService(); }

    @Override
    public void getAllSurveys(final AllSurveysBody allSurveysBody, final USAMVCFinishedListener USAMVCfinishedListener) {
        Call<AllSurveys> allSurveys = apiService.getAllSurveys(allSurveysBody);
        allSurveys.enqueue(new Callback<AllSurveys>() {
            @Override
            public void onResponse(Response<AllSurveys> response, Retrofit retrofit) {
                if (response.body() != null) {
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        USAMVCfinishedListener.onFailure(response.body().getCode(), 1);
                    } else {
                        USAMVCfinishedListener.onSuccessSurveysFetched(response.body().getData(), allSurveysBody.getPage(), response.body().getTotal());
                    }
                } else {
                    USAMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 1);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(debugTag, t.toString());
                if (t instanceof IOException) {
                    USAMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 1);
                } else {
                    USAMVCfinishedListener.onFailure(AppConfig.INTERNAL_ERROR, 1);
                }
            }
        });
    }

    @Override
    public void deleteUserSurvey(final NewSurvey newSurvey, final int adapter_position, final USAMVCFinishedListener USAMVCfinishedListener) {
        Call<AllSurveys> deletedSurvey = apiService.deleteUserSurvey(newSurvey);
        deletedSurvey.enqueue(new Callback<AllSurveys>() {
            @Override
            public void onResponse(Response<AllSurveys> response, Retrofit retrofit) {
                if (response.body() != null) {
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        USAMVCfinishedListener.onFailure(response.body().getCode(), 2);
                    } else {
                        USAMVCfinishedListener.onSuccessUserSurveyDeletion(adapter_position);
                    }
                } else {
                    USAMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 2);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(debugTag, t.toString());
                if (t instanceof IOException) {
                    USAMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 2);
                } else {
                    USAMVCfinishedListener.onFailure(AppConfig.INTERNAL_ERROR, 2);
                }
            }
        });
    }

    @Override
    public void getSurveyStats(SurveyAnswersBody surveyAnswersBody, final USAMVCFinishedListener USAMVCfinishedListener) {
        Call<SurveyDetails> surveyDetails = apiService.uploadSurveyAnswersOrGetSurveyStats(surveyAnswersBody);
        surveyDetails.enqueue(new Callback<SurveyDetails>() {
            @Override
            public void onResponse(Response<SurveyDetails> response, Retrofit retrofit) {
                if (response.body() != null) {
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        USAMVCfinishedListener.onFailure(response.body().getCode(), 2);
                    } else {
                        USAMVCfinishedListener.onSuccessSurveyDetailsFetched(response.body().getData());
                    }
                } else {
                    USAMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 2);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(debugTag, t.toString());
                if (t instanceof IOException) {
                    USAMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 2);
                } else {
                    USAMVCfinishedListener.onFailure(AppConfig.INTERNAL_ERROR, 2);
                }
            }
        });
    }
}
