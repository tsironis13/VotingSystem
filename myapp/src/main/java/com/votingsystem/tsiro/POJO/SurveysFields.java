package com.votingsystem.tsiro.POJO;

/**
 * Created by giannis on 27/8/2016.
 */
public class SurveysFields {

    private int survey_id, user_id, responses, last_modified_date;
    private String title;

    public SurveysFields(int survey_id, int user_id, int responses, int last_modified_date, String title) {
        this.survey_id          = survey_id;
        this.user_id            = user_id;
        this.responses          = responses;
        this.last_modified_date = last_modified_date;
        this.title              = title;
    }

    public int getSurveyId() { return survey_id; }

    public int getUserId() { return user_id; }

    public void setUserId(int user_id) { this.user_id = user_id; }

    public int getResponses() { return responses; }

    public void setResponses(int responses) { this.responses = responses; }

    public int getLastModified() { return last_modified_date; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }
}
