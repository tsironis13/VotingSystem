package com.votingsystem.tsiro.SurveyQuestionsMVC;

import com.votingsystem.tsiro.POJO.SurveyQuestions;
import com.votingsystem.tsiro.parcel.QuestionData;

import java.util.List;

/**
 * Created by giannis on 25/6/2016.
 */
public interface SQMVCView {
    void onSuccessSurveyQuestionsFetched(String surveyTitle, List<QuestionData> data);
    void onFailure(int code);
}

