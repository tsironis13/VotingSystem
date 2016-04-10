package com.votingsystem.tsiro.LoginActivityMVC;

import android.text.method.TransformationMethod;

import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import java.util.List;

/**
 * Created by user on 5/2/2016.
 */
public interface LAMVCView {
    void handlePasswordTextChanges(TextView showHidePasswordTtv, int action);
    void changeTransformationMethod(TransformationMethod transformationMethod, int action);
    void displayFeedbackMsg(int code);
    void onSuccessfulFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList, boolean firmsLoaded);
    void onFailureFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList, boolean firmsLoaded);
    void onSuccess();
    void onFailure(int code, String field, String hint);
}
