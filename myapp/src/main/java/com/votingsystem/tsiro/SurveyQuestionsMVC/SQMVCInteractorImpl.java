package com.votingsystem.tsiro.SurveyQuestionsMVC;

import android.util.Log;

import com.votingsystem.tsiro.POJO.SurveyQuestionBody;
import com.votingsystem.tsiro.POJO.SurveyQuestions;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.parcel.QuestionData;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.rest.ApiService;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by giannis on 25/6/2016.
 */
public class SQMVCInteractorImpl implements SQMVCInteractor {

    private static final String debugTag = SQMVCInteractorImpl.class.getSimpleName();
    private ApiService apiService = RetrofitSingleton.getInstance().getApiService();

    @Override
    public void getSurveyQuestions(SurveyQuestionBody surveyQuestionBody, final SQMVCFinishedListener SQMVCfinishedListener) {
        Call<SurveyQuestions> call = apiService.getSurveyQuestions(surveyQuestionBody);
        call.enqueue(new Callback<SurveyQuestions>() {
            @Override
            public void onResponse(Response<SurveyQuestions> response, Retrofit retrofit) {
                Log.e(debugTag, response.body().getCode()+"");
                List<QuestionData> firmElementList = response.body().getData();
                if (response.body().getCode() != AppConfig.STATUS_OK) {
                    SQMVCfinishedListener.onFailure(response.body().getCode());
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
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(debugTag, t.toString());
            }
        });
    }
}