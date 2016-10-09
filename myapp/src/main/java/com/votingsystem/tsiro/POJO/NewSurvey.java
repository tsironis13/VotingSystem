package com.votingsystem.tsiro.POJO;

import android.util.SparseArray;
import java.util.List;

/**
 * Created by giannis on 19/9/2016.
 */
public class NewSurvey {

    private int user_id, firm_id;
    private String action, title, category, token;
    private long active_since, valid_until;
    private SparseArray<NewSurveyQuestion> surveyQuestionSparseArray;
    private List<NewSurveyQuestion> questions_list;

    public NewSurvey(){}

    public int getUserId() { return user_id; }

    public void setUserId(int user_id) { this.user_id = user_id; }

    public int getFirmId() { return firm_id; }

    public void setFirmId(int firm_id) { this.firm_id = firm_id; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public long getValidUntil() { return valid_until; }

    public void setValidUntil(long valid_until) { this.valid_until = valid_until; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public long getActiveSince() { return active_since; }

    public void setActiveSince(long active_since) { this.active_since = active_since; }

    public SparseArray<NewSurveyQuestion> getNewSurveyQuestionSparseArray() { return surveyQuestionSparseArray; }

    public void setNewSurveyQuestionSparseArray(SparseArray<NewSurveyQuestion> surveyQuestionSparseArray) { this.surveyQuestionSparseArray = surveyQuestionSparseArray; }

    public List<NewSurveyQuestion> getQuestionsList() { return questions_list; }

    public void setQuestionslist(List<NewSurveyQuestion> questions_list) { this.questions_list = questions_list; }
}
