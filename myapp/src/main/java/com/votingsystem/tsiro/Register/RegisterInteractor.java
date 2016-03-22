package com.votingsystem.tsiro.Register;

import com.rey.material.widget.ProgressView;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;

import java.util.ArrayList;

/**
 * Created by user on 5/2/2016.
 */
public interface RegisterInteractor {
    public void validateInputField(RegisterPresenterParamsObj registerPresenterParamsObj, RegisterInputFieldFinishedListener registerInputFieldFinishedListener);
    public void populateFirmNamesSpnr(ArrayList<FirmNameWithID> firmNameWithIDArrayList,  RegisterInputFieldFinishedListener registerInputFieldFinishedListener);
}
