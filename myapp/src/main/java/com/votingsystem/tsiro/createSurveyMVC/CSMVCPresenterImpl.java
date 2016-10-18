package com.votingsystem.tsiro.createSurveyMVC;

import com.votingsystem.tsiro.POJO.NewSurvey;

/**
 * Created by giannis on 3/10/2016.
 */

public class CSMVCPresenterImpl implements CSMVCPresenter, CSMVCFinishedListener {

    private CSMVCView CSMVCview;
    private CSMVCInteractorImpl CSMVCinteractorImpl;

    public CSMVCPresenterImpl(CSMVCView CSMVCview) {
        this.CSMVCview      = CSMVCview;
        CSMVCinteractorImpl = new CSMVCInteractorImpl();
    }

    public void uploadNewUserSurvey(NewSurvey newSurvey) {
        this.CSMVCinteractorImpl.uploadNewUserSurvey(newSurvey, this);
    }

    @Override
    public void onSuccess() {
        if (CSMVCview != null) CSMVCview.onSuccess();
    }

    @Override
    public void onFailure(int code) {
        CSMVCview.onFailure(code);
    }

    @Override
    public void onDestroy() {
        this.CSMVCview = null;
    }
}
