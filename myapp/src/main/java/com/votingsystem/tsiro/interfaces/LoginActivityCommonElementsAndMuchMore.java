package com.votingsystem.tsiro.interfaces;

import com.androidadvance.topsnackbar.TSnackbar;
import com.rey.material.widget.TextView;
import android.view.View;


/**
 * Created by user on 10/12/2015.
 */
public interface LoginActivityCommonElementsAndMuchMore {
    void setLoginActivitySpan(TextView registerSpanTtv, String stringResource, int start, int end, int code);
    void forgotPasswordOnClick();
    void signInHereOnClick();
    void registerOnClick();
    void showSnackBar(int code);
    void showErrorContainerSnackbar(String error_type, View errorView, int code);
    void dismissErrorContainerSnackBar();
    void setText(String action, View view, String decodedMessage, String color);
    void animationOccured(boolean ishappening);
    void onSplashScreenAnimationFinish();
    String encodeUtf8(String text);
    String decodeUtf8(String text);
}
