package com.votingsystem.tsiro.POJO;

/**
 * Created by giannis on 19/6/2016.
 */
public class FirmSurveyDetails {

    private int code, total_surveys, responses;
    private String firm_name, last_created_date;

    public FirmSurveyDetails() {}

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public int getResponses() { return responses; }

    public void setResponses(int responses) { this.responses = responses; }

    public int getTotalSurveys() { return total_surveys; }

    public void setTotalSurveys(int total_surveys) { this.total_surveys = total_surveys; }

    public String getFirmName() { return firm_name; }

    public void setFirmName(String firm_name) { this.firm_name = firm_name; }

    public String getLastCreatedDate() { return last_created_date; }

    public void setLastCreatedDate(String last_created_date) { this.last_created_date = last_created_date; }
}
