package com.votingsystem.tsiro.POJO;

import java.util.List;

/**
 * Created by giannis on 1/10/2016.
 */

public class NewSurveyQuestion {
    private transient int key;
    private transient String action;
    private String title, type;
    private List<String> answers;
    private int type_id;
    private transient boolean single_choice;
    private int mandatory;

    public NewSurveyQuestion(int key, String action, String title, String type, List<String> answers, int type_id, boolean single_choice, int mandatory) {
        this.key            = key;
        this.action         = action;
        this.title          = title;
        this.type           = type;
        this.answers        = answers;
        this.type_id        = type_id;
        this.single_choice  = single_choice;
        this.mandatory      = mandatory;
    }

    public int getKey() { return key; }

    public void setKey(int key) { this.key = key; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public List<String> getAnswersList() { return answers; }

    public void setAnswersList(List<String> answers) { this.answers = answers; }

    public int getTypeId() { return type_id; }

    public void setTypeId(int type_id) { this.type_id = type_id; }

    public boolean isSingleChoice() { return single_choice; }

    public void setSingleChoice(boolean single_choice) { this.single_choice = single_choice; }

    public int isMandatory() { return mandatory; }

    public void setMandatory(int mandatory) { this.mandatory = mandatory; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }
}
