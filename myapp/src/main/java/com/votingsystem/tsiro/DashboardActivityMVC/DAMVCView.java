package com.votingsystem.tsiro.DashboardActivityMVC;

/**
 * Created by giannis on 18/6/2016.
 */
public interface DAMVCView {
    void onSuccessDashboardDetails(String firm_name, int total_surveys, int responses, String last_created_date);
    void onFailure();
}
