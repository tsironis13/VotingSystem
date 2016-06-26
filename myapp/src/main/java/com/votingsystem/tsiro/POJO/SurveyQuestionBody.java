package com.votingsystem.tsiro.POJO;

/**
 * Created by giannis on 25/6/2016.
 */
public class SurveyQuestionBody {

    private String action;
    private int survey_id;

    public SurveyQuestionBody(String action, int survey_id) {
        this.action     =   action;
        this.survey_id  =   survey_id;
    }
}
