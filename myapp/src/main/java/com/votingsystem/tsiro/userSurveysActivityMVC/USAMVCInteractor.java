package com.votingsystem.tsiro.userSurveysActivityMVC;

import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.POJO.NewSurvey;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.parcel.SurveyData;

/**
 * Created by giannis on 12/11/2016.
 */

interface USAMVCInteractor {
    void getAllSurveys(AllSurveysBody allSurveysBody, USAMVCFinishedListener USAMVCfinishedListener);
    void deleteUserSurvey(NewSurvey newSurvey, int adapter_position, USAMVCFinishedListener USAMVCfinishedListener);
    void getSurveyStats(SurveyAnswersBody surveyAnswersBody, USAMVCFinishedListener USAMVCfinishedListener);
}
