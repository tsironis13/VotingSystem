package com.votingsystem.tsiro.DashboardActivityMVC;

import com.votingsystem.tsiro.POJO.JnctFirmSurveysFields;
import com.votingsystem.tsiro.POJO.SurveysFields;

import java.util.List;

/**
 * Created by giannis on 18/6/2016.
 */
public interface DAMVCFinishedListener {

    void onSuccessDashboardDetails(String firm_name, int total_surveys, int responses, String last_created_date);
    void onSuccessFetchTableData(List<JnctFirmSurveysFields> jnctFirmSurveysFieldsList, List<SurveysFields> surveysFieldsList);
    void onFailure(int code);

}
