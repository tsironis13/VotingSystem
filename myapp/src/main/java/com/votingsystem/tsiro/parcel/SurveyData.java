package com.votingsystem.tsiro.parcel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by giannis on 20/6/2016.
 */
public class SurveyData implements Parcelable {

    private int survey_id, responses;
    private String title, type, active_since, valid_until;
    private boolean answered;

    public SurveyData() {}

    protected SurveyData(Parcel in) {
        survey_id       =   in.readInt();
        responses       =   in.readInt();
        answered        =   in.readInt() != 0;
        title           =   in.readString();
        active_since    =   in.readString();
        valid_until     =   in.readString();
        type            =   in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(survey_id);
        dest.writeInt(responses);
        dest.writeInt((int) (answered ? 1 : 0));
        dest.writeString(title);
        dest.writeString(active_since);
        dest.writeString(valid_until);
        dest.writeString(type);
    }
}
