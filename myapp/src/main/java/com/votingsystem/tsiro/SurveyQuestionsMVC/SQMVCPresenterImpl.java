package com.votingsystem.tsiro.SurveyQuestionsMVC;

import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;
import com.votingsystem.tsiro.POJO.SurveyQuestionBody;
import com.votingsystem.tsiro.parcel.QuestionData;

import java.util.List;

/**
 * Created by giannis on 25/6/2016.
 */
public class SQMVCPresenterImpl implements SQMVCPresenter, SQMVCFinishedListener{

    private SQMVCView SQMVCview;
    private SQMVCInteractorImpl SQMVCinteractorImpl;

    public SQMVCPresenterImpl(SQMVCView SQMVCview) {
        this.SQMVCview              =   SQMVCview;
        this.SQMVCinteractorImpl    =   new SQMVCInteractorImpl();
    }

    public void getSurveyQuestions(SurveyQuestionBody surveyQuestionBody) {
        SQMVCinteractorImpl.getSurveyQuestions(surveyQuestionBody, this);
    }

    public void uploadSurveyAnswers(SurveyAnswersBody surveyAnswersBody) {
        SQMVCinteractorImpl.uploadSurveyAnswers(surveyAnswersBody, this);
    }

    @Override
    public void onSuccessSurveyQuestionsFetched(String surveyTitle, List<QuestionData> data) {
        SQMVCview.onSuccessSurveyQuestionsFetched(surveyTitle, data);
    }

    @Override
    public void onSuccessSurveyDetailsFetched(SurveyDetailsData surveyDetailsData) {
        SQMVCview.onSuccessSurveyDetailsFetched(surveyDetailsData);
    }

    @Override
    public void onFailure(int code, int request) {
        SQMVCview.onFailure(code, request);
    }

    @Override
    public void onDestroy() {
        this.SQMVCview = null;
    }
}
