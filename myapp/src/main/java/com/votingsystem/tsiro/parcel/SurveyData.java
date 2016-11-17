package com.votingsystem.tsiro.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 20/6/2016.
 */
public class SurveyData implements Parcelable {

    private int survey_id, responses;
    private String title, type, active_since, valid_until, created_date, category;
    private long active_since_epoch, valid_until_epoch;
    private boolean answered;
    private List<QuestionData> questions;

    public SurveyData() {}

    protected SurveyData(Parcel in) {
        survey_id           =   in.readInt();
        responses           =   in.readInt();
        answered            =   in.readInt() != 0;
        title               =   in.readString();
        active_since        =   in.readString();
        valid_until         =   in.readString();
        active_since_epoch  =   in.readLong();
        valid_until_epoch   =   in.readLong();
        created_date        =   in.readString();
        type                =   in.readString();
        category            =   in.readString();
        questions           =   new ArrayList<>();
        in.readTypedList(questions, QuestionData.CREATOR);
    }

    public static final Creator<SurveyData> CREATOR = new Creator<SurveyData>() {
        @Override
        public SurveyData createFromParcel(Parcel in) {
            return new SurveyData(in);
        }

        @Override
        public SurveyData[] newArray(int size) {
            return new SurveyData[size];
        }
    };

    public int getResponses() { return responses; }

    public void setResponses(int responses) { this.responses = responses; }

    public boolean getIsAnswered() { return answered; }

    public void setIsAnswered(boolean answered) { this.answered = answered; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public int getSurveyId() { return survey_id; }

    public void setSurveyId(int survey_id) { this.survey_id = survey_id; }

    public String getActiveSince() { return active_since; }

    public void setActiveSince(String active_since) { this.active_since = active_since; }

    public String getValidUntil() { return valid_until; }

    public void setValidUntil(String valid_until) { this.valid_until = valid_until; }

    public long getActiveSinceEpoch() { return active_since_epoch; }

    public void setActiveSinceEpoch(long active_since_epoch) { this.active_since_epoch = active_since_epoch; }

    public long getValidUntilEpoch() { return valid_until_epoch; }

    public void setValidUntilEpoch(long valid_until_epoch) { this.valid_until_epoch = valid_until_epoch; }

    public String getCreatedDate() { return created_date; }

    public void setCreatedDate(String created_date) { this.created_date = created_date; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public List<QuestionData> getQuestions() { return questions; }

    public void setQuestions(List<QuestionData> questions) { this.questions = questions; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(survey_id);
        dest.writeInt(responses);
        dest.writeInt(answered ? 1 : 0);
        dest.writeString(title);
        dest.writeString(active_since);
        dest.writeString(valid_until);
        dest.writeLong(active_since_epoch);
        dest.writeLong(valid_until_epoch);
        dest.writeString(created_date);
        dest.writeString(type);
        dest.writeString(category);
        dest.writeTypedList(questions);
    }
}
