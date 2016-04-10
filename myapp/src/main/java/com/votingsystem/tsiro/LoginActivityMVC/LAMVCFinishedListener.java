package com.votingsystem.tsiro.LoginActivityMVC;

import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import java.util.List;

/**
 * Created by user on 5/2/2016.
 */
public interface LAMVCFinishedListener {
    public void displayFeedbackMsg(int code);
    public void onSuccessfirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList);
    public void onFailurefirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList);
    public void onFailure(int code, String field, String hint);
    public void onSuccess();
}
