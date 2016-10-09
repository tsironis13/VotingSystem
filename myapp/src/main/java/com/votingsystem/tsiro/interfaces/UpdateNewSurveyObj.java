package com.votingsystem.tsiro.interfaces;

import android.view.View;

import com.votingsystem.tsiro.POJO.NewSurvey;
import com.votingsystem.tsiro.POJO.NewSurveyQuestion;

import java.util.List;

/**
 * Created by giannis on 19/9/2016.
 */
public interface UpdateNewSurveyObj {
    void initializeSpannableText(View view, String text, boolean required);
    NewSurvey getNewSurveyObj();
    void initializeDialogs(int action, String message);
    void showSnackBar(int code, View view);
}
