package com.votingsystem.tsiro.Register;

import android.view.View;
import android.widget.RelativeLayout;
import com.rey.material.widget.ProgressView;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import java.util.List;

/**
 * Created by user on 5/2/2016.
 */
public interface RegisterInputFieldFinishedListener {
    public void showToastMsg(int code);
    public void startProgressLoader(ProgressView inputFieldProgressView);
    public void hideProgressLoader(ProgressView inputFieldProgressView);
    public void onInputFieldError(int code, View view);
    public void onSuccess(RelativeLayout validInputRlt, String tag);
    public void onSuccessfirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList);
    public void onFailurefirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList);
}
