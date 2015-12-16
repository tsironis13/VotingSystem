package com.votingsystem.tsiro.interfaces;

import android.widget.TextView;

/**
 * Created by user on 10/12/2015.
 */
public interface LoginActivityCommonElements {
    public void setLoginActivitySpan(TextView registerSpanTtv, String stringResource, int start, int end, int code);
    public void forgotPasswordOnClick();
    public void signInHereOnClick();
    public void registerOnClick();
}
