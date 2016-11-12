package com.votingsystem.tsiro.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;

import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.fragments.ErrorFragment;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.CompletedSurveysFragment;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.OngoingSurveysFragment;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.PendingSurveysFragment;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 19/6/2016.
 */
public class SurveysPagerAdapter extends FragmentStatePagerAdapter {

    private static final String debugTag = SurveysPagerAdapter.class.getSimpleName();
    private Fragment[] pages = new Fragment[getCount()];
    private String[] tabText;
    private int connectionType;

    public SurveysPagerAdapter(FragmentManager fragmentManager, String[] tabText, int connectionType) {
        super(fragmentManager);
        this.tabText        = tabText;
        this.connectionType = connectionType;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (pages[position] == null) pages[position] = CompletedSurveysFragment.newInstance(connectionType);
                return pages[position];
            case 1:
                if (pages[position] == null) pages[position] = OngoingSurveysFragment.newInstance(connectionType);
                return pages[position];
            case 2:
                if (pages[position] == null) pages[position] = PendingSurveysFragment.newInstance(connectionType);
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
