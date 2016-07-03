package com.votingsystem.tsiro.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.nakama.arraypageradapter.ArrayFragmentStatePagerAdapter;
import com.votingsystem.tsiro.POJO.SurveyQuestions;
import com.votingsystem.tsiro.fragments.SurveyQuestionsFragment;
import com.votingsystem.tsiro.parcel.QuestionData;

import java.util.List;

/**
 * Created by giannis on 26/6/2016.
 */
public class SurveyQuestionsPagerAdapter extends ArrayFragmentStatePagerAdapter<QuestionData> {

    private static final String debugTag = SurveyQuestionsPagerAdapter.class.getSimpleName();
    private Fragment[] pages;
    private String surveyTitle;
    private int position;

    public SurveyQuestionsPagerAdapter(FragmentManager fm, String surveyTitle, QuestionData question, int size) {
        super(fm, question);
        pages               =   new Fragment[size];
        this.surveyTitle    =   surveyTitle;
    }

    @Override
    public Fragment getFragment(QuestionData question, int position) {
        this.position = position;
        if (pages[position] == null) pages[position] = SurveyQuestionsFragment.newInstance(this.surveyTitle, question, position+1);
        return pages[position];
    }

    public Fragment getCurrentFragment() {
        return this.pages[position];
    }
}
