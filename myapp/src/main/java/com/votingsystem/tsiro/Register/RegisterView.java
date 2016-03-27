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
    public void clearEditextHelpersAndSuccessIcon(String action, RelativeLayout acceptRlt, EditText inputEdt);
    public void handlePasswordTextChanges(String text, TextView showHidePasswordTtv);
    public void changeTransformationMethod(TransformationMethod transformationMethod, String text);
    public void showFieldValidationProgress(ProgressView inputFieldPrgv);
    public void hideFieldValidationProgress(ProgressView inputFieldPrgv);
    public void displayFeedbackMsg(int code);
    public void onSuccess(RelativeLayout inputValidRlt, String tag);
    public void onFailure(int code, View view);
    public void onSuccessfulFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList, boolean firmsLoaded);
    public void onFailureFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList, boolean firmsLoaded);
    public void onEmptyFieldsValidationSuccess();
    public void onEmptyFieldsValidationFailure(String field, String errorType);
    public void onFormValidationSuccess();
    public void onFormValidationFailure();
}
