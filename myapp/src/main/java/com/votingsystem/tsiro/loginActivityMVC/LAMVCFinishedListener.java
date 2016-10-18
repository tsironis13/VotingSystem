package com.votingsystem.tsiro.loginActivityMVC;

import com.votingsystem.tsiro.helperClasses.CustomSpinnerItem;

import java.util.List;

/**
 * Created by user on 5/2/2016.
 */
public interface LAMVCFinishedListener {
    void displayFeedbackMsg(int code);
    void onSuccessfirmNamesSpnrLoad(List<CustomSpinnerItem> firmNameWithIDArrayList);
    void onFailurefirmNamesSpnrLoad(List<CustomSpinnerItem> firmNameWithIDArrayList);
    void onFailure(int code, String field, String hint, String retry_in);
    void onSuccess();
    void onSuccessUserSignIn(int user_id, String username, String email, int firm_id);
}
