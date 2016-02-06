package com.votingsystem.tsiro.Register;

import android.view.View;
import android.widget.RelativeLayout;

import com.rey.material.widget.EditText;
import com.rey.material.widget.ProgressView;

/**
 * Created by user on 6/2/2016.
 */
public class RegisterPresenterParamsObj {

    private int connectionStatus;
    private boolean isAdded;
    private String inputField, action, retrofitAction, tag;
    private ProgressView inputFieldProgressView;
    private RelativeLayout validInputRlt;
    private EditText inputEditText;
    private View errorView;

    public RegisterPresenterParamsObj(){}

    public int getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(int connectionStatus) { this.connectionStatus = connectionStatus; }

    public boolean isAdded() {
        return isAdded;
    }

    public void setIsAdded(boolean isAdded) {
        this.isAdded = isAdded;
    }

    public String getInputField() {
        return inputField;
    }

    public void setInputField(String inputField) {
        this.inputField = inputField;
    }

    public ProgressView getInputFieldProgressView() {
        return inputFieldProgressView;
    }

    public void setInputFieldProgressView(ProgressView inputFieldProgressView) { this.inputFieldProgressView = inputFieldProgressView; }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRetrofitAction() {
        return retrofitAction;
    }

    public void setRetrofitAction(String retrofitAction) {
        this.retrofitAction = retrofitAction;
    }

    public RelativeLayout getValidInputRlt() { return validInputRlt; }

    public void setValidInputRlt(RelativeLayout validInputRlt) { this.validInputRlt = validInputRlt; }

    public String getTag() { return tag; }

    public void setTag(String tag) { this.tag = tag; }

    public EditText getInputEditText() { return inputEditText; }

    public void setInputEditText(EditText inputEditText) { this.inputEditText = inputEditText; }

    public View getErrorView() { return errorView; }

    public void setErrorView(View errorView) { this.errorView = errorView; }
}
