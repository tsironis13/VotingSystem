package com.votingsystem.tsiro.DashboardActivityMVC;

/**
 * Created by giannis on 18/6/2016.
 */
public interface DAMVCInteractor {
    void getDashboardFirmDetails(boolean isAdded, int user_id, int firm_id, DAMVCFinishedListener DAMVCfinishedListener);
}
