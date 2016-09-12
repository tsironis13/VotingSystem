package com.votingsystem.tsiro.LoginActivityMVC;

import com.votingsystem.tsiro.POJO.LoginFormBody;
import com.votingsystem.tsiro.POJO.RegisterFormBody;
import com.votingsystem.tsiro.POJO.ResetPassowrdBody;
import com.votingsystem.tsiro.helperClasses.CustomSpinnerItem;
import java.util.ArrayList;

/**
 * Created by user on 5/2/2016.
 */
public interface LAMVCInteractor {
    void loginUser(LoginFormBody loginFormBody, final boolean isAdded, final LAMVCFinishedListener LAMVCfinishedListener);
    void validateForm(RegisterFormBody registerFormBody, boolean isAdded, LAMVCFinishedListener LAMVCfinishedListener);
    void populateFirmNamesSpnr(ArrayList<CustomSpinnerItem> firmNameWithIDArrayList, LAMVCFinishedListener LAMVCfinishedListener);
    void resetPassword(ResetPassowrdBody resetPassowrdBody, boolean isAdded, LAMVCFinishedListener LAMVCfinishedListener);
}
