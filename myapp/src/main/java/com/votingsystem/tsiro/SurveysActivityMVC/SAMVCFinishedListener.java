package com.votingsystem.tsiro.SurveysActivityMVC;

import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;

import java.util.List;

/**
 * Created by giannis on 20/6/2016.
 */
public interface SAMVCFinishedListener {
    void onSuccessSurveysFetched(List<SurveyData> data, int offset);
    void onSuccessSurveyDetailsFetched(SurveyDetailsData surveyDetailsData);
    void onFailure(int code);
}
