package com.votingsystem.tsiro.LoginActivityMVC;

import com.votingsystem.tsiro.POJO.RegisterFormBody;
import com.votingsystem.tsiro.POJO.ResetPassowrdBody;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import java.util.ArrayList;

/**
 * Created by user on 5/2/2016.
 */
public interface LAMVCInteractor {
    public void validateForm(RegisterFormBody registerFormBody, boolean isAdded, LAMVCFinishedListener LAMVCfinishedListener);
    public void populateFirmNamesSpnr(ArrayList<FirmNameWithID> firmNameWithIDArrayList,  LAMVCFinishedListener LAMVCfinishedListener);
    public void resetPassword(ResetPassowrdBody resetPassowrdBody, boolean isAdded, LAMVCFinishedListener LAMVCfinishedListener);
}
