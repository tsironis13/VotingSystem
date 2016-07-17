package com.votingsystem.tsiro.POJO;

import java.util.HashMap;
import java.util.List;

/**
 * Created by giannis on 12/7/2016.
 */
public class SurveyAnswersBody {
    int user_id, survey_id;
    String action;
    List<SurveyAnswersList> survey_answers_list;

    public SurveyAnswersBody(String action, int user_id, int survey_id, List<SurveyAnswersList> survey_answers_list) {
        this.action             =   action;
        this.user_id            =   user_id;
        this.survey_id          =   survey_id;
        this.survey_answers_list  =   survey_answers_list;
    }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public int getUserId() { return user_id; }

    public void setUserId(int user_id) { this.user_id = user_id; }

    public int getSurveyId() { return survey_id; }

    public void setSurveyId(int survey_id) { this.survey_id = survey_id; }

    public List<SurveyAnswersList> getSurveyAnswersList() { return survey_answers_list; }

    public void setSurveyAnswersList(List<SurveyAnswersList> survey_answers_list) { this.survey_answers_list = survey_answers_list; }
}
