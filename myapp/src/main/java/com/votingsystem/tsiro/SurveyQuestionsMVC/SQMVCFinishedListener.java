package com.votingsystem.tsiro.SurveyQuestionsMVC;

import com.votingsystem.tsiro.parcel.SurveyDetailsData;
import com.votingsystem.tsiro.parcel.QuestionData;

import java.util.List;

/**
 * Created by giannis on 25/6/2016.
 */
public interface SQMVCFinishedListener {
    void onSuccessSurveyQuestionsFetched(String surveyTitle, List<QuestionData> data);
    void onSuccessSurveyDetailsFetched(SurveyDetailsData surveyDetailsData);
    void onFailure(int code);
}
