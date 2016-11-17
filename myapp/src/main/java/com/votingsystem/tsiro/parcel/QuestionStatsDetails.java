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
    private int type_id, answered, skipped;
    private boolean no_answers;
    private List<String> free_answers;
    private List<Stats> stats;

    public QuestionStatsDetails(String title, int type_id, int answered, int skipped, boolean no_answers, List<String> free_answers, List<Stats> stats) {
        this.title          =   title;
        this.type_id        =   type_id;
        this.answered       =   answered;
        this.skipped        =   skipped;
        this.no_answers     =   no_answers;
        this.free_answers   =   free_answers;
        this.stats          =   stats;
    }

    private QuestionStatsDetails(Parcel in) {
        title           = in.readString();
        type_id         = in.readInt();
        answered        = in.readInt();
        skipped         = in.readInt();
        no_answers      = in.readInt() != 1;
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

    public int getAnswered() { return answered; }

    public void setAnswered(int answered) { this.answered = answered; }

    public int getSkipped() { return skipped; }

    public void setSkipped(int skipped) { this.skipped = skipped; }

    public boolean getNoAnswers() { return no_answers; }

    public void setNoAnswers(boolean no_answers) { this.no_answers = no_answers; }

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
        parcel.writeInt(answered);
        parcel.writeInt(skipped);
        parcel.writeInt(no_answers ? 0 : 1);
        parcel.writeStringList(free_answers);
        parcel.writeTypedList(stats);
    }
}
