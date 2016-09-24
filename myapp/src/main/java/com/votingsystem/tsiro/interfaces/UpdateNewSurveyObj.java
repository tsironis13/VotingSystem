package com.votingsystem.tsiro.interfaces;

import android.view.View;

import com.votingsystem.tsiro.POJO.NewSurvey;

/**
 * Created by giannis on 19/9/2016.
 */
public interface UpdateNewSurveyObj {
    void initializeSpannableText(View view, String text, boolean required);
    void addNewSurveyFields(String title, long active_since, long valid_until, int category);
    NewSurvey getObj();
//    method just to check if everything works as expected
    void logObj();
}
