package com.votingsystem.tsiro.POJO;

import java.util.List;

/**
 * Created by giannis on 27/8/2016.
 */
public class JnctFirmSurveys {

    private int code;
    private List<JnctFirmSurveysFields> jnct_data;
    private List<SurveysFields> surveys_data;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public List<JnctFirmSurveysFields> getJnctFIrmSurveysFieldsList() { return jnct_data; }

    public void setJnctFIrmSurveysFieldsList(List<JnctFirmSurveysFields> jnct_data) { this.jnct_data = jnct_data; }

    public List<SurveysFields> getSurveysDataList() { return surveys_data; }

    public void setSurveysDataList(List<SurveysFields> surveys_data) { this.surveys_data = surveys_data; }
}
