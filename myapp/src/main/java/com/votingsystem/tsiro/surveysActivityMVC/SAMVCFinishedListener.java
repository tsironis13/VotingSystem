package com.votingsystem.tsiro.surveysActivityMVC;

import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;

import java.util.List;

/**
 * Created by giannis on 20/6/2016.
 */
interface SAMVCFinishedListener {
    void onSuccessSurveysFetched(List<SurveyData> data, int page, int total);
    void onSuccessSurveyDetailsFetched(SurveyDetailsData surveyDetailsData);
    void onFailure(int code, int request);
}
