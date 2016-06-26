package com.votingsystem.tsiro.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by giannis on 26/6/2016.
 */
public class QuestionData implements Parcelable {

    private int type_id, mandatory;
    private String type, title;
    private List<String> answers;

    public QuestionData() {}

    protected QuestionData(Parcel in) {
        type_id = in.readInt();
        mandatory = in.readInt();
        type = in.readString();
        title = in.readString();
        answers = in.createStringArrayList();
    }

    public static final Creator<QuestionData> CREATOR = new Creator<QuestionData>() {
        @Override
        public QuestionData createFromParcel(Parcel in) { return new QuestionData(in); }

        @Override
        public QuestionData[] newArray(int size) { return new QuestionData[size]; }
    };

    public int getTypeId() { return type_id; }

    public void setTypeId(int type_id) { this.type_id = type_id; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public int isMandatory() { return mandatory; }

    public void setMandatory(int mandatory) { this.mandatory = mandatory; }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type_id);
        parcel.writeInt(mandatory);
        parcel.writeString(type);
        parcel.writeString(title);
        parcel.writeStringList(answers);
    }
}
