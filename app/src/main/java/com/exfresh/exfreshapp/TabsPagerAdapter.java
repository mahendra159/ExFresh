package com.exfresh.exfreshapp;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.exfresh.exfreshapp.NewsReportsFragment;
import com.exfresh.exfreshapp.VideoFragment;
import com.exfresh.exfreshapp.ArticlesFragment;

/**
 * Created by Mahendra on 7/14/2015.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // News Reports fragment activity
                return new NewsReportsFragment();
            case 1:
                // Video Reports fragment activity
                return new VideoFragment();
            case 2:
            // Articles fragment activity
                return new ArticlesFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }


}
