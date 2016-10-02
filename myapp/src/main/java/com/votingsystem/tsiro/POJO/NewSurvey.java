package com.votingsystem.tsiro.POJO;

import android.util.SparseArray;

/**
 * Created by giannis on 19/9/2016.
 */
public class NewSurvey {

    private String title;
    private long active_since, valid_until;
    private int category;
    private SparseArray<NewSurveyQuestion> newSurveyQuestionSparseArray;

    public NewSurvey(){}

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public long getValidUntil() { return valid_until; }

    public void setValidUntil(long valid_until) { this.valid_until = valid_until; }

    public int getCategory() { return category; }

    public void setCategory(int category) { this.category = category; }

    public long getActiveSince() { return active_since; }

    public void setActiveSince(long active_since) { this.active_since = active_since; }

    public SparseArray<NewSurveyQuestion> getNewSurveyQuestionSparseArray() { return newSurveyQuestionSparseArray; }

    public void setNewSurveyQuestionSparseArray(SparseArray<NewSurveyQuestion> newSurveyQuestionSparseArray) { this.newSurveyQuestionSparseArray = newSurveyQuestionSparseArray; }
}
