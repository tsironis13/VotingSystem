package com.votingsystem.tsiro.POJO;

import com.votingsystem.tsiro.parcel.SurveyDetailsData;

/**
 * Created by giannis on 16/7/2016.
 */
public class SurveyDetails {

    private int code;
    private SurveyDetailsData data;

    public SurveyDetails(int code, SurveyDetailsData data) {
        this.code   =   code;
        this.data   =   data;
    }

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public SurveyDetailsData getData() { return data; }

    public void setData(SurveyDetailsData data) { this.data = data; }
}
