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
    void addNewSurveyFields(String title, long active_since, long valid_until, int category);
    NewSurvey getNewSurveyObj();
    void initializeDialogs(int action, String message);
//    method just to check if everything works as expected
    void logObj();
    void showSnackBar(String text, View view);
}
