package com.votingsystem.tsiro.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 16/7/2016.
 */
public class SurveyDetailsData implements Parcelable{

    private int responses, total_questions;
    private String title, created_date, modified_date, category;
    private float completion_rate;
    private List<QuestionStatsDetails> question;

    public SurveyDetailsData(int responses, int total_questions, String title, String created_date, String modified_date, String category, float completion_rate, List<QuestionStatsDetails> question) {
        this.responses          =   responses;
        this.total_questions    =   total_questions;
        this.title              =   title;
        this.created_date       =   created_date;
        this.modified_date      =   modified_date;
        this.category           =   category;
        this.completion_rate    =   completion_rate;
        this.question           =   question;
    }

    protected SurveyDetailsData(Parcel in) {
        responses       = in.readInt();
        total_questions = in.readInt();
        title           = in.readString();
        created_date    = in.readString();
        modified_date   = in.readString();
        category        = in.readString();
        completion_rate = in.readFloat();
        question        = new ArrayList<>();
        in.readTypedList(question, QuestionStatsDetails.CREATOR);
    }

    public static final Creator<SurveyDetailsData> CREATOR = new Creator<SurveyDetailsData>() {
        @Override
        public SurveyDetailsData createFromParcel(Parcel in) {
            return new SurveyDetailsData(in);
        }

        @Override
        public SurveyDetailsData[] newArray(int size) {
            return new SurveyDetailsData[size];
        }
    };

    public int getResponses() { return responses; }

    public void setResponses(int responses) { this.responses = responses; }

    public int getTotalQuestions() { return total_questions; }

    public void setTotalQuestions(int total_questions) { this.total_questions = total_questions; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getCreatedDate() { return created_date; }

    public void setCreatedDate(String created_date) { this.created_date = created_date; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getModifiedDate() { return modified_date; }

    public void setModifiedDate(String modified_date) { this.modified_date = modified_date; }

    public float getCompletionRate() { return completion_rate; }

    public void setCompletionRate(float completion_rate) { this.completion_rate = completion_rate; }

    public List<QuestionStatsDetails> getQuestion() { return question; }

    public void setQuestion(List<QuestionStatsDetails> question) { this.question = question; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(responses);
        parcel.writeInt(total_questions);
        parcel.writeString(title);
        parcel.writeString(created_date);
        parcel.writeString(modified_date);
        parcel.writeString(category);
        parcel.writeFloat(completion_rate);
        parcel.writeTypedList(question);
    }
}
