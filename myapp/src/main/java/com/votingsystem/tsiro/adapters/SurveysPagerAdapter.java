package com.votingsystem.tsiro.adapters;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.votingsystem.tsiro.POJO.AllSurveys;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.CompletedSurveysFragment;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.OngoingSurveysFragment;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.PendingSurveysFragment;
import com.votingsystem.tsiro.parcel.SurveyData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 19/6/2016.
 */
public class SurveysPagerAdapter extends FragmentStatePagerAdapter {

    private static final String debugTag = SurveysPagerAdapter.class.getSimpleName();
    private Fragment[] pages = new Fragment[getCount()];
    private List<SurveyData> data;
    String[] tabText;

    public SurveysPagerAdapter(FragmentManager fragmentManager, String[] tabText) {
        super(fragmentManager);
        this.tabText = tabText;
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("SurveysPagerAdapter", position+"");
        switch (position) {
            case 0:
                if (pages[position] == null) pages[position] = CompletedSurveysFragment.newInstance();
                return pages[position];
            case 1:
                if (pages[position] == null) pages[position] = OngoingSurveysFragment.newInstance();
                return pages[position];
            case 2:
                if (pages[position] == null) pages[position] = PendingSurveysFragment.newInstance();
                return pages[position];
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabText[position];
    }

    @Override
    public int getCount() {
        return 3;
    }

//    private List<SurveyData> filterSurveyDataBasedOnType(List<SurveyData> data, String type) {
//        List<SurveyData> filteredList = new ArrayList<SurveyData>();
//        for (SurveyData item : data) {
//            if (item.getType().equals(type)) filteredList.add(item);
//        }
//        return filteredList;
//    }
}
