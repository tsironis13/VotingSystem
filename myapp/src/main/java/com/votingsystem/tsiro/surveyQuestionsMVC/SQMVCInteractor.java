package com.votingsystem.tsiro.surveyQuestionsMVC;

import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.POJO.SurveyQuestionBody;

/**
 * Created by giannis on 25/6/2016.
 */
public interface SQMVCInteractor {
    void getSurveyQuestions(SurveyQuestionBody surveyQuestionBody, SQMVCFinishedListener SQMVCfinishedListener);
    void uploadSurveyAnswers(SurveyAnswersBody surveyAnswersBody, SQMVCFinishedListener SQMVCfinishedListener);
}
