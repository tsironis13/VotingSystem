package com.votingsystem.tsiro.POJO;

import java.util.List;

/**
 * Created by giannis on 1/10/2016.
 */

public class NewSurveyQuestion {
    private int key;
    private String action, question, question_type;
    private List<String> answers_list;
    private int category_id;
    private boolean single_choice;
    private boolean mandatory;

    public NewSurveyQuestion(int key, String action, String question, String question_type, List<String> answers_list, int category_id, boolean single_choice, boolean mandatory) {
        this.key            = key;
        this.action         = action;
        this.question       = question;
        this.question_type  = question_type;
        this.answers_list   = answers_list;
        this.category_id    = category_id;
        this.single_choice  = single_choice;
        this.mandatory      = mandatory;
    }

    public int getKey() { return key; }

    public void setKey(int key) { this.key = key; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getQuestionType() { return question_type; }

    public void setQuestionType(String question_type) { this.question_type = question_type; }

    public List<String> getAnswersList() { return answers_list; }

    public void setAnswersList(List<String> answers_list) { this.answers_list = answers_list; }

    public int getCategoryId() { return category_id; }

    public void setCategoryId(int category_id) { this.category_id = category_id; }

    public boolean isSingleChoice() { return single_choice; }

    public void setSingleChoice(boolean single_choice) { this.single_choice = single_choice; }

    public boolean isMandatory() { return mandatory; }

    public void setMandatory(boolean mandatory) { this.mandatory = mandatory; }

    public String getQuestion() { return question; }

    public void setQuestion(String question) { this.question = question; }
}
