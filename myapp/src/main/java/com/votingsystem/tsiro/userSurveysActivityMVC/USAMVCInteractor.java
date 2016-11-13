package com.votingsystem.tsiro.userSurveysActivityMVC;

import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;

/**
 * Created by giannis on 12/11/2016.
 */

interface USAMVCInteractor {
    void getAllSurveys(AllSurveysBody allSurveysBody, USAMVCFinishedListener USAMVCfinishedListener);
    void getSurveyStats(SurveyAnswersBody surveyAnswersBody, USAMVCFinishedListener USAMVCfinishedListener);
}
