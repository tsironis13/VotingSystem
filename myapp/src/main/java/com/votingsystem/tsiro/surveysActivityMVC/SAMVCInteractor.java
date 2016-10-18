package com.votingsystem.tsiro.surveysActivityMVC;

import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;

/**
 * Created by giannis on 20/6/2016.
 */
interface SAMVCInteractor {
    void getAllSurveys(AllSurveysBody allSurveysBody, SAMVCFinishedListener SAMVCfinishedListener);
    void getSurveyStats(SurveyAnswersBody surveyAnswersBody, SAMVCFinishedListener SAMVCfinishedListener);
}
