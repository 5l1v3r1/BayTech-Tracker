package com.visneweb.techbay.tracker;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * Created by riskactive on 16.03.2018.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new ScannerFragment();
            case 1: return new CardReaderFragment();
            case 2: return new SettingFragment();
            default: return new ScannerFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
