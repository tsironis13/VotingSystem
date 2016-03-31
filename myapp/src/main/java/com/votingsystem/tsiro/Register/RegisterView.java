package com.votingsystem.tsiro.Register;

import android.text.method.TransformationMethod;
import android.view.View;
import com.rey.material.widget.RelativeLayout;
import com.rey.material.widget.EditText;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import java.util.List;

/**
 * Created by user on 5/2/2016.
 */
public interface RegisterView {
    public void handlePasswordTextChanges(TextView showHidePasswordTtv, int action);
    public void changeTransformationMethod(TransformationMethod transformationMethod, int action);
    public void displayFeedbackMsg(int code);
    public void onSuccessfulFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList, boolean firmsLoaded);
    public void onFailureFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList, boolean firmsLoaded);
    public void onFormValidationSuccess();
    public void onFormValidationFailure(int code, String field, String hint);
}
