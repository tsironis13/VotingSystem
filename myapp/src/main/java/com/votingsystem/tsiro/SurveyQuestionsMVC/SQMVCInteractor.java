package com.votingsystem.tsiro.SurveyQuestionsMVC;

import com.votingsystem.tsiro.POJO.SurveyQuestionBody;

/**
 * Created by giannis on 25/6/2016.
 */
public interface SQMVCInteractor {
    void getSurveyQuestions(SurveyQuestionBody surveyQuestionBody, SQMVCFinishedListener SQMVCfinishedListener);
}
