package com.votingsystem.tsiro.POJO;

import java.util.HashMap;
import java.util.List;

/**
 * Created by giannis on 12/7/2016.
 */
public class SurveyAnswersList {

    int question_id, answer, type;
    String text_answer;
    List<String> answers_list;
    HashMap<Integer, Integer> matrix_hash;

    public SurveyAnswersList(int question_id, int answer, int type, String text_answer, List<String> answers_list, HashMap<Integer, Integer> matrix_hash) {
        this.question_id    =   question_id;
        this.answer         =   answer;
        this.type           =   type;
        this.text_answer    =   text_answer;
        this.answers_list   =   answers_list;
        this.matrix_hash    =   matrix_hash;
    }

    public int getQuestionId() { return question_id; }

    public void setQuestionId(int question_id) { this.question_id = question_id; }

    public int getAnswer() { return answer; }

    public void setAnswer(int answer) { this.answer = answer; }

    public int getType() { return type; }

    public void setType(int type) { this.type = type; }

    public String getTextAnswer() { return text_answer; }

    public void setTextAnswer(String text_answer) { this.text_answer = text_answer; }

    public HashMap<Integer, Integer> getIntegerHashMap() { return matrix_hash; }

    public void setIntegerHashMap(HashMap<Integer, Integer> matrix_hash) { this.matrix_hash = matrix_hash; }

    public List<String> getAnswersList() { return answers_list; }

    public void setAnswersList(List<String> answers_list) { this.answers_list = answers_list; }
}
