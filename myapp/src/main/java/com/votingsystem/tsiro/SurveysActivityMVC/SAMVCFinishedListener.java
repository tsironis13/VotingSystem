package com.votingsystem.tsiro.SurveysActivityMVC;

import com.votingsystem.tsiro.parcel.SurveyData;
import java.util.List;

/**
 * Created by giannis on 20/6/2016.
 */
public interface SAMVCFinishedListener {
    void onSuccessSurveysFetched(List<SurveyData> data, int offset);
    void onFailure(int code);
}
