package com.votingsystem.tsiro.Register;

import android.view.View;
import android.widget.RelativeLayout;

import com.rey.material.widget.EditText;
import com.rey.material.widget.ProgressView;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;

import java.util.ArrayList;

/**
 * Created by user on 5/2/2016.
 */
public interface RegisterView {
    public void clearEditextHelpersAndSuccessIcon(String action, RelativeLayout acceptRlt, EditText inputEdt);
    public void showFieldValidationProgress(ProgressView inputFieldPrgv);
    public void hideFieldValidationProgress(ProgressView inputFieldPrgv);
    public void setInputFieldError(int code, View view);
    public void setToastMsg(int code);
    public void onSuccess(RelativeLayout inputValidRlt, String tag);
    public void onSuccessfulFirmNamesSpnrLoad(ArrayList<FirmNameWithID> firmNameWithIDArrayList);
    public void onFailure();
}
