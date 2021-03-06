package com.votingsystem.tsiro.surveysActivityMVC;

import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;

import java.util.List;

/**
 * Created by giannis on 20/6/2016.
 */
public class SAMVCPresenterImpl implements SAMVCPresenter, SAMVCFinishedListener {

    private SAMVCView SAMVCview;
    private SAMVCInteractorImpl SAMVCinteractorImpl;

    public SAMVCPresenterImpl(SAMVCView SAMVCview) {
        this.SAMVCview              = SAMVCview;
        this.SAMVCinteractorImpl    = new SAMVCInteractorImpl();
    }

    public void getSurveysBasedOnSpecificFirmId(AllSurveysBody allSurveysBody) {
        this.SAMVCinteractorImpl.getAllSurveys(allSurveysBody, this);
    }

    public void getSurveyDetails(SurveyAnswersBody surveyAnswersBody) {
        this.SAMVCinteractorImpl.getSurveyStats(surveyAnswersBody, this);
    }

    @Override
    public void onSuccessSurveysFetched(List<SurveyData> data, int page, int total) {
        SAMVCview.onSuccessSurveysFetched(data, page, total);
    }

    @Override
    public void onSuccessSurveyDetailsFetched(SurveyDetailsData surveyDetailsData) {
        SAMVCview.onSuccessSurveyDetailsFetched(surveyDetailsData);
    }

    @Override
    public void onFailure(int code, int request) {
        SAMVCview.onFailure(code, request);
    }

    @Override
    public void onDestroy() {
        this.SAMVCview = null;
    }

}
