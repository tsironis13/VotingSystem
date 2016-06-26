package com.votingsystem.tsiro.POJO;

import com.votingsystem.tsiro.parcel.SurveyData;
import java.util.List;

/**
 * Created by giannis on 20/6/2016.
 */
public class AllSurveys {
    private int code;
    private List<SurveyData> data;

    public AllSurveys() {}

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public List<SurveyData> getData() { return data; }

    public void setData(List<SurveyData> data) { this.data = data; }

}
