package com.votingsystem.tsiro.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 16/7/2016.
 */
public class QuestionStatsDetails implements Parcelable {

    private String title;
    private int type_id;
    private List<String> free_answers;
    private List<Stats> stats;

    public QuestionStatsDetails(String title, int type_id, List<String> free_answers, List<Stats> stats) {
        this.title          =   title;
        this.type_id        =   type_id;
        this.free_answers   =   free_answers;
        this.stats          =   stats;
    }

    protected QuestionStatsDetails(Parcel in) {
        title           = in.readString();
        type_id         = in.readInt();
        free_answers    = in.createStringArrayList();
        stats           = new ArrayList<>();
        in.readTypedList(stats, Stats.CREATOR);
    }

    public static final Creator<QuestionStatsDetails> CREATOR = new Creator<QuestionStatsDetails>() {
        @Override
        public QuestionStatsDetails createFromParcel(Parcel in) {
            return new QuestionStatsDetails(in);
        }

        @Override
        public QuestionStatsDetails[] newArray(int size) {
            return new QuestionStatsDetails[size];
        }
    };

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public int getTypeId() { return type_id; }

    public void setTypeId(int type_id) { this.type_id = type_id; }

    public List<String> getFreeAnswers() { return free_answers; }

    public void setFreeAnswers(List<String> free_answers) { this.free_answers = free_answers; }

    public List<Stats> getStats() { return stats; }

    public void setStats(List<Stats> stats) { this.stats = stats; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeInt(type_id);
        parcel.writeStringList(free_answers);
        parcel.writeTypedList(stats);
    }
}
