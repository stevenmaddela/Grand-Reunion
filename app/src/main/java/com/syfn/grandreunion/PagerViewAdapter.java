package com.syfn.grandreunion;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class PagerViewAdapter extends FragmentPagerAdapter {
    public PagerViewAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                HubFragment hubFragment = new HubFragment();
                return hubFragment;
            case 1:
                FundFragment fundFragment = new FundFragment();
                return fundFragment;
            case 2:
                EventFragment eventFragment = new EventFragment();
                return eventFragment;
            case 3:
                MembersFragment membersFragment = new MembersFragment();
                return membersFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
