package com.votingsystem.tsiro.LoginActivityMVC;

import android.text.method.TransformationMethod;

import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.helperClasses.CustomSpinnerItem;

import java.util.List;

/**
 * Created by user on 5/2/2016.
 */
public interface LAMVCView {
    void handlePasswordTextChanges(TextView showHidePasswordTtv, int action);
    void changeTransformationMethod(TransformationMethod transformationMethod, int action);
    void displayFeedbackMsg(int code);
    void onSuccessfulFirmNamesSpnrLoad(List<CustomSpinnerItem> firmNameWithIDArrayList, boolean firmsLoaded);
    void onFailureFirmNamesSpnrLoad(List<CustomSpinnerItem> firmNameWithIDArrayList, boolean firmsLoaded);
    void onSuccess();
    void onSuccessUserSignIn(int user_id, String username, String email, int firm_id);
    void onFailure(int code, String field, String hint, String retry_in);
}
