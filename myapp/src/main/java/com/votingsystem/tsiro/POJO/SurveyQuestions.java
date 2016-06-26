package com.votingsystem.tsiro.POJO;

import com.votingsystem.tsiro.parcel.QuestionData;

import java.util.HashMap;
import java.util.List;

/**
 * Created by giannis on 25/6/2016.
 */
public class SurveyQuestions {

    private int code;

    private String title;

    private List<QuestionData> data;

    public SurveyQuestions() {}

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public List<QuestionData> getData() { return data; }

    public void setData(List<QuestionData> data) { this.data = data; }

}
