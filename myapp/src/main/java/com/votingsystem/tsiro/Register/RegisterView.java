package com.votingsystem.tsiro.Register;

import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.RelativeLayout;
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
    public void setInputFieldError(int code, View view);
    public void displayFeedbackMsg(int code);
    public void onSuccess(RelativeLayout inputValidRlt, String tag);
    public void onSuccessfulFirmNamesSpnrLoad(List<FirmNameWithID> firmNameWithIDArrayList);
    public void onFailure(List<FirmNameWithID> firmNameWithIDArrayList);
}
