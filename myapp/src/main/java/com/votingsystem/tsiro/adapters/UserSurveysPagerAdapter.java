package com.votingsystem.tsiro.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.votingsystem.tsiro.fragments.survey_tabs_fragments.CompletedSurveysFragment;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.OngoingSurveysFragment;
import com.votingsystem.tsiro.fragments.survey_tabs_fragments.PendingSurveysFragment;
import com.votingsystem.tsiro.fragments.user_survey_tabs_fragments.ApprovedUserSurveysFragment;
import com.votingsystem.tsiro.fragments.user_survey_tabs_fragments.RejectedUserSurveysFragment;
import com.votingsystem.tsiro.fragments.user_survey_tabs_fragments.UnderProcessUserSurveysFragment;

/**
 * Created by giannis on 18/10/2016.
 */

public class UserSurveysPagerAdapter extends FragmentStatePagerAdapter {

    private static final String debugTag = UserSurveysPagerAdapter.class.getSimpleName();
    private Fragment[] pages = new Fragment[getCount()];
    private String[] tabText;
    private int connectionType;

    public UserSurveysPagerAdapter(FragmentManager fragmentManager, String[] tabText, int connectionType) {
        super(fragmentManager);
        this.tabText        = tabText;
        this.connectionType = connectionType;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (pages[position] == null) pages[position] = UnderProcessUserSurveysFragment.newInstance();
                return pages[position];
            case 1:
                if (pages[position] == null) pages[position] = ApprovedUserSurveysFragment.newInstance();
                return pages[position];
            case 2:
                if (pages[position] == null) pages[position] = RejectedUserSurveysFragment.newInstance();
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
}
