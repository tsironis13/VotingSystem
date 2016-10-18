package com.votingsystem.tsiro.surveyQuestionsMVC;

import android.util.Log;

import com.votingsystem.tsiro.parcel.QuestionStatsDetails;
import com.votingsystem.tsiro.parcel.Stats;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.POJO.SurveyDetails;
import com.votingsystem.tsiro.POJO.SurveyQuestionBody;
import com.votingsystem.tsiro.POJO.SurveyQuestions;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.parcel.QuestionData;
import com.votingsystem.tsiro.rest.ApiService;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by giannis on 25/6/2016.
 */
class SQMVCInteractorImpl implements SQMVCInteractor {

    private static final String debugTag = SQMVCInteractorImpl.class.getSimpleName();
    private ApiService apiService = RetrofitSingleton.getInstance().getApiService();

    @Override
    public void getSurveyQuestions(SurveyQuestionBody surveyQuestionBody, final SQMVCFinishedListener SQMVCfinishedListener) {
        Call<SurveyQuestions> call = apiService.getSurveyQuestions(surveyQuestionBody);
        call.enqueue(new Callback<SurveyQuestions>() {
            @Override
            public void onResponse(Response<SurveyQuestions> response, Retrofit retrofit) {
                if (response.body() != null) {
                    List<QuestionData> firmElementList = response.body().getData();
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        SQMVCfinishedListener.onFailure(response.body().getCode(), 1);
                    } else {
                        SQMVCfinishedListener.onSuccessSurveyQuestionsFetched(response.body().getTitle(), firmElementList);
                    }
//                if (firmElementList != null) {
//                    for (int i = 0; i< firmElementList.size(); i++) {
//                        if (firmElementList.get(i).getAnswers() != null) {
//                            Log.e(debugTag,  firmElementList.get(i).getAnswers().size()+"");
//                            for (int y = 0; y < firmElementList.get(i).getAnswers().size(); y++) {
//                                Log.e(debugTag, firmElementList.get(i).getAnswers().get(y)+"");
//                            }
//                        }
//                    }
//                }
                } else {
                    SQMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 1);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(debugTag, t.toString());
                if (t instanceof IOException) {
                    SQMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 1);
                } else {
                    SQMVCfinishedListener.onFailure(AppConfig.INTERNAL_ERROR, 1);
                }
            }
        });
    }

    @Override
    public void uploadSurveyAnswers(SurveyAnswersBody surveyAnswersBody, final SQMVCFinishedListener SQMVCfinishedListener) {
        Call<SurveyDetails> call = apiService.uploadSurveyAnswersOrGetSurveyStats(surveyAnswersBody);
        call.enqueue(new Callback<SurveyDetails>() {
            @Override
            public void onResponse(Response<SurveyDetails> response, Retrofit retrofit) {
                if (response.body() != null) {
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        SQMVCfinishedListener.onFailure(response.body().getCode(), 2);
                    } else {
                        SQMVCfinishedListener.onSuccessSurveyDetailsFetched(response.body().getData());
                    }
                    Log.e(debugTag, response.body().getData().getQuestion().size()+"");
                    for (QuestionStatsDetails details : response.body().getData().getQuestion()) {
                        for (Stats stats: details.getStats()) {
//                        Log.e(debugTag, "Answer title: "+stats.getTitle()+" Answer pers: "+stats.getPercentage()+"Count: "+stats.getCount());
                        }
                    }
                } else {
                    SQMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 2);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(debugTag, t.toString());
                if (t instanceof IOException) {
                    SQMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE, 2);
                } else {
                    SQMVCfinishedListener.onFailure(AppConfig.INTERNAL_ERROR, 2);
                }
            }
        });
    }
}
