package com.votingsystem.tsiro.SurveyQuestionsMVC;

import com.votingsystem.tsiro.POJO.SurveyQuestionBody;
import com.votingsystem.tsiro.POJO.SurveyQuestions;
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

    @Override
    public void onSuccessSurveyQuestionsFetched(String surveyTitle, List<QuestionData> data) {
        SQMVCview.onSuccessSurveyQuestionsFetched(surveyTitle, data);
    }

    @Override
    public void onFailure(int code) {
        SQMVCview.onFailure(code);
    }

    @Override
    public void onDestroy() {
        this.SQMVCview = null;
    }
}
