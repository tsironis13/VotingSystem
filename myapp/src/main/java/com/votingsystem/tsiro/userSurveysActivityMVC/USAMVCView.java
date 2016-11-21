package com.votingsystem.tsiro.userSurveysActivityMVC;

import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;

import java.util.List;

/**
 * Created by giannis on 12/11/2016.
 */

public interface USAMVCView {
    void onSuccessSurveysFetched(List<SurveyData> data, int offset, int total);
    void onSuccessUserSurveyDeletion(int survey_id);
    void onSuccessSurveyDetailsFetched(SurveyDetailsData data);
    void onFailure(int code, int request);
}
