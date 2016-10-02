package com.votingsystem.tsiro.POJO;

import java.util.List;

/**
 * Created by giannis on 19/6/2016.
 */
public class FirmSurveyDetails {

    private int code, total_surveys, responses;
    private String firm_name, last_created_date;
    private List<JnctFirmSurveysFields> jnct_data;
    private List<SurveysFields> surveys_data;

    public FirmSurveyDetails() {}

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public int getResponses() { return responses; }

    public int getTotalSurveys() { return total_surveys; }

    public String getFirmName() { return firm_name; }

    public String getLastCreatedDate() { return last_created_date; }

    public List<SurveysFields> getSurveysFieldsList() { return surveys_data; }

    public void setSurveysFieldsList(List<SurveysFields> surveys_data) { this.surveys_data = surveys_data; }

    public List<JnctFirmSurveysFields> getJnctFirmSurveysFieldsList() { return jnct_data; }

    public void setJnctFirmSurveysFieldsList(List<JnctFirmSurveysFields> jnct_data) { this.jnct_data = jnct_data; }
}
