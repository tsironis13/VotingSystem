package com.votingsystem.tsiro.DashboardActivityMVC;

import android.util.Log;
import com.votingsystem.tsiro.POJO.FirmSurveyDetails;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.rest.ApiService;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by giannis on 18/6/2016.
 */
public class DAMVCInteractorImpl implements DAMVCInteractor {

    private static final String debugTag = DAMVCInteractorImpl.class.getSimpleName();
    private ApiService apiService;

    public DAMVCInteractorImpl() { apiService = RetrofitSingleton.getInstance().getApiService(); }

    @Override
    public void getDashboardFirmDetails(final boolean isAdded, int user_id, int firm_id, final DAMVCFinishedListener DAMVCfinishedListener) {
        Log.e(debugTag, user_id+"  "+firm_id);
        Call<FirmSurveyDetails> call = apiService.getFirmSurveyDetails("firm_survey_details", user_id, firm_id);
        call.enqueue(new Callback<FirmSurveyDetails>() {
            @Override
            public void onResponse(Response<FirmSurveyDetails> response, Retrofit retrofit) {
                if (isAdded) {
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        DAMVCfinishedListener.onFailure(AppConfig.STATUS_ERROR);
                    } else {
                        Log.e(debugTag, "SUCCESS");
                        DAMVCfinishedListener.onSuccessDashboardDetails(response.body().getFirmName(), response.body().getTotalSurveys(), response.body().getResponses(), response.body().getLastCreatedDate());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(debugTag, t.toString());
                if (t instanceof IOException) {
                    DAMVCfinishedListener.onFailure(AppConfig.UNAVAILABLE_SERVICE);
                } else {
                    DAMVCfinishedListener.onFailure(AppConfig.INTERNAL_ERROR);
                }
            }
        });
    }


}
