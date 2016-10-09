package com.votingsystem.tsiro.CreateSurveyMVC;

import com.votingsystem.tsiro.POJO.NewSurvey;

/**
 * Created by giannis on 3/10/2016.
 */

interface CSMVCInteractor {
    void uploadNewUserSurvey(NewSurvey newSurvey, CSMVCFinishedListener CSMVCfinishedListener);
}
