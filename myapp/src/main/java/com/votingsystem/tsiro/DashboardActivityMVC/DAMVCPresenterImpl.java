package com.votingsystem.tsiro.DashboardActivityMVC;

/**
 * Created by giannis on 18/6/2016.
 */
public class DAMVCPresenterImpl implements DAMVCPresenter, DAMVCFinishedListener {

    private DAMVCView DAMVCview;
    private DAMVCInteractorImpl DAMVCinteractorimpl;

    public DAMVCPresenterImpl(DAMVCView DAMVCview) {
        this.DAMVCview              =   DAMVCview;
        this.DAMVCinteractorimpl    =   new DAMVCInteractorImpl();
    }

    public void initializeDashboardDetails(boolean isAdded, int user_id, int firm_id) {
        this.DAMVCinteractorimpl.getDashboardFirmDetails(isAdded, user_id, firm_id, this);
    }

    @Override
    public void onSuccessDashboardDetails(String firm_name, int total_surveys, int responses, String last_created_date) {
        DAMVCview.onSuccessDashboardDetails(firm_name, total_surveys, responses, last_created_date);
    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onDestroy() { DAMVCview = null; }
}
