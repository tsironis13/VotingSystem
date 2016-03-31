package com.votingsystem.tsiro.Register;

import com.votingsystem.tsiro.POJO.RegisterFormBody;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import java.util.ArrayList;

/**
 * Created by user on 5/2/2016.
 */
public interface RegisterInteractor {
    public void validateForm(RegisterFormBody registerFormBody, boolean isAdded, RegisterFormFinishedListener registerFormFinishedListener);
    public void populateFirmNamesSpnr(ArrayList<FirmNameWithID> firmNameWithIDArrayList,  RegisterFormFinishedListener registerFormFinishedListener);
}
