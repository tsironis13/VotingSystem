package com.votingsystem.tsiro.userSurveysActivityMVC;

import com.votingsystem.tsiro.parcel.SurveyData;
import java.util.List;

/**
 * Created by giannis on 12/11/2016.
 */

interface USAMVCFinishedListener {
    void onSuccessSurveysFetched(List<SurveyData> data, int page, int total);
    void onFailure(int code, int request);
}
