package com.votingsystem.tsiro.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.votingsystem.tsiro.fragments.survey_tabs_fragments.CompletedSurveysFragment;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.OngoingSurveysFragment;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.PendingSurveysFragment;

/**
 * Created by giannis on 19/6/2016.
 */
public class SurveysPagerAdapter extends FragmentStatePagerAdapter {

    String[] tabText;

    public SurveysPagerAdapter(FragmentManager fragmentManager, String[] tabText) {
        super(fragmentManager);
        this.tabText = tabText;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = CompletedSurveysFragment.newInstance("");
                break;
            case 1:
                fragment = OngoingSurveysFragment.newInstance("");
                break;
            case 2:
                fragment = PendingSurveysFragment.newInstance("");
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabText[position];
    }

    @Override
    public int getCount() {
        return 3;
    }
}
