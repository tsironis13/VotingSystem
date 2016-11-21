package com.votingsystem.tsiro.userSurveysActivityMVC;

import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.POJO.NewSurvey;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;

import java.util.List;

/**
 * Created by giannis on 12/11/2016.
 */

public class USAMVCPresenterImpl implements USAMVCPresenter, USAMVCFinishedListener {

    private USAMVCView USAMVCview;
    private USAMVCInteractorImpl USAMVCinteractorImpl;

    public USAMVCPresenterImpl(USAMVCView USAMVCview) {
        this.USAMVCview              = USAMVCview;
        this.USAMVCinteractorImpl    = new USAMVCInteractorImpl();
    }

    public void getSurveysBasedOnSpecificUserId(AllSurveysBody allSurveysBody) {
        this.USAMVCinteractorImpl.getAllSurveys(allSurveysBody, this);
    }

    public void getSurveyDetails(SurveyAnswersBody surveyAnswersBody) {
        this.USAMVCinteractorImpl.getSurveyStats(surveyAnswersBody, this);
    }

    @Override
    public void onSuccessSurveysFetched(List<SurveyData> data, int page, int total) {
        USAMVCview.onSuccessSurveysFetched(data, page, total);
    }

    @Override
    public void onSuccessSurveyDetailsFetched(SurveyDetailsData data) {
        USAMVCview.onSuccessSurveyDetailsFetched(data);
    }

    public void deleteUserSurvey(NewSurvey newSurvey, int adapter_position) {
        this.USAMVCinteractorImpl.deleteUserSurvey(newSurvey, adapter_position, this);
    }

    @Override
    public void onSuccessUserSurveyDeletion(int adapter_position) {
        USAMVCview.onSuccessUserSurveyDeletion(adapter_position);
    }

    @Override
    public void onFailure(int code, int request) {
        USAMVCview.onFailure(code, request);
    }

    @Override
    public void onDestroy() { this.USAMVCview = null; }
}
