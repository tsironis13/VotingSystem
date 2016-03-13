package com.votingsystem.tsiro.interfaces;

import com.rey.material.widget.EditText;
import android.widget.TextView;

/**
 * Created by user on 10/12/2015.
 */
public interface LoginActivityCommonElementsAndMuchMore {
    public void setLoginActivitySpan(TextView registerSpanTtv, String stringResource, int start, int end, int code);
    public void forgotPasswordOnClick();
    public void signInHereOnClick();
    public void registerOnClick();
    public boolean validateEditText(EditText[] fields);
    public String encodeUtf8(String text);
    public String decodeUtf8(String text);
}
